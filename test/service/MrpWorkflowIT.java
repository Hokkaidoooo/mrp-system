package service;

import entity.BOM;
import entity.Inventory;
import entity.ItemMaster;
import entity.MRP;
import entity.TPOP;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import util.Transactions;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class MrpWorkflowIT {
    private static SessionFactory sessions;
    private static String serverUrl;
    private static String database;
    private static boolean ownsDatabase;
    private static String jdbcUrl;
    private static String user;
    private static String password;

    @BeforeClass
    public static void createIsolatedDatabase() throws Exception {
        serverUrl = required("MRP_TEST_MYSQL_URL");
        if (!serverUrl.matches("jdbc:mysql://[^/]+/")) {
            throw new IllegalArgumentException("MRP_TEST_MYSQL_URL must name a server only, e.g. jdbc:mysql://127.0.0.1:33317/");
        }
        user = required("MRP_TEST_MYSQL_USER");
        password = System.getenv("MRP_TEST_MYSQL_PASSWORD");
        if (password == null) throw new IllegalArgumentException("MRP_TEST_MYSQL_PASSWORD must be set (empty is allowed for an isolated test server)");
        Class.forName("com.mysql.cj.jdbc.Driver");
        database = "mrp_test_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = DriverManager.getConnection(serverUrl, user, password); Statement sql = connection.createStatement()) {
            sql.execute("CREATE DATABASE `" + database + "` CHARACTER SET utf8mb4");
            ownsDatabase = true;
        }
        jdbcUrl = serverUrl + database + "?useSSL=false&serverTimezone=UTC";
        executeScript(Paths.get("db/migration/V1__legacy_schema.sql"));
        executeScript(Paths.get("db/migration/V3__atomic_batches_and_past_due.sql"));
        for (String table : new String[]{"t_mrp", "t_bom", "t_inventory", "t_itemmaster", "t_tpop"}) sql("DELETE FROM " + table);
        Configuration config = new Configuration().configure();
        config.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        config.setProperty("hibernate.connection.url", jdbcUrl);
        config.setProperty("hibernate.connection.username", user);
        config.setProperty("hibernate.connection.password", password);
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        config.setProperty("hibernate.hbm2ddl.auto", "validate");
        config.setProperty("hibernate.connection.isolation", "4");
        sessions = config.buildSessionFactory();
        Transactions.run(sessions, session -> {
            for (int i = 0; i < 3; i++) {
                String part = String.valueOf((char) ('A' + i));
                ItemMaster item = new ItemMaster(); item.setP_No(part); item.setLLC(i); item.setLSR("LFL"); session.save(item);
                Inventory stock = new Inventory(); stock.setP_No(part); stock.setOH("0"); stock.setAL("0"); session.save(stock);
            }
            BOM ab = new BOM(); ab.setParent("A"); ab.setComp("B"); ab.setQ_P(2); session.save(ab);
            BOM bc = new BOM(); bc.setParent("B"); bc.setComp("C"); bc.setQ_P(3); session.save(bc);
            TPOP plan = new TPOP(); plan.setP_No("A"); plan.setPOR1(10); session.save(plan);
            return null;
        });
    }

    @AfterClass
    public static void removeOwnedDatabase() throws Exception {
        if (sessions != null) sessions.close();
        if (ownsDatabase && database != null && database.matches("mrp_test_[0-9a-f]{32}")) {
            try (Connection connection = DriverManager.getConnection(serverUrl, user, password); Statement sql = connection.createStatement()) {
                sql.execute("DROP DATABASE `" + database + "`");
            }
        }
    }

    @Test
    public void rollsBackMidSaveSerializesRecalculationsAndServesWar() throws Exception {
        MrpRecalculationService service = service(new MrpCalculationService());
        service.recalculate();
        String previous = snapshot();
        sql("UPDATE t_tpop SET POR1 = 11 WHERE P_No = 'A'");
        sql("CREATE TRIGGER fail_second_mrp BEFORE INSERT ON t_mrp FOR EACH ROW BEGIN IF NEW.P_No = 'C' THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'injected MRP save failure'; END IF; END");
        try {
            assertThrows(RuntimeException.class, service::recalculate);
            assertEquals("失败必须完整保留旧批次及其主键", previous, snapshot());
        } finally {
            sql("DROP TRIGGER fail_second_mrp");
        }
        assertBatch(service.recalculate(), 22, 66);
        verifyConcurrentBatches();
        sql("UPDATE t_itemmaster SET LT = 3 WHERE P_No = 'B'");
        service.recalculate();
        assertEquals("-2:24", values("SELECT CONCAT(period, ':', quantity) FROM t_mrp_past_due_release"));
        assertEquals("-2:72", values("SELECT CONCAT(period, ':', quantity) FROM t_mrp_past_due_demand"));
        verifyTomcatWar();
    }

    private void verifyConcurrentBatches() throws Exception {
        CountDownLatch firstReading = new CountDownLatch(1), finishFirst = new CountDownLatch(1), secondReading = new CountDownLatch(1);
        MrpCalculationService firstCalculator = new MrpCalculationService() {
            @Override public List<MRP> calculate(List<BOM> bom, List<TPOP> plans, List<Inventory> stocks, List<ItemMaster> items) {
                firstReading.countDown(); await(finishFirst); return super.calculate(bom, plans, stocks, items);
            }
        };
        MrpCalculationService secondCalculator = new MrpCalculationService() {
            @Override public List<MRP> calculate(List<BOM> bom, List<TPOP> plans, List<Inventory> stocks, List<ItemMaster> items) {
                secondReading.countDown(); return super.calculate(bom, plans, stocks, items);
            }
        };
        ExecutorService threads = Executors.newFixedThreadPool(2);
        try {
            Future<List<MRP>> first = threads.submit(() -> service(firstCalculator).recalculate());
            assertTrue(firstReading.await(10, TimeUnit.SECONDS));
            Future<List<MRP>> second = threads.submit(() -> service(secondCalculator).recalculate());
            assertFalse("第二个重算应等待数据库锁", secondReading.await(250, TimeUnit.MILLISECONDS));
            sql("UPDATE t_tpop SET POR1 = 12 WHERE P_No = 'A'");
            finishFirst.countDown();
            assertBatch(first.get(15, TimeUnit.SECONDS), 22, 66);
            assertBatch(second.get(15, TimeUnit.SECONDS), 24, 72);
            assertEquals("B:24,C:72", quantities());
        } finally {
            finishFirst.countDown(); threads.shutdownNow();
        }
    }

    private void verifyTomcatWar() throws Exception {
        Path base = Files.createTempDirectory(Paths.get("target"), "tomcat-it-");
        List<String> containerClasspath = new ArrayList<String>();
        containerClasspath.add(Paths.get("target/test-classes").toString());
        for (String entry : System.getProperty("surefire.test.class.path").split(java.util.regex.Pattern.quote(File.pathSeparator))) {
            String file = new File(entry).getName();
            if (file.startsWith("tomcat-embed-") || file.startsWith("tomcat-annotations-api-") || file.startsWith("ecj-")) containerClasspath.add(entry);
        }
        Path ready = base.resolve("ready.txt");
        ProcessBuilder launch = new ProcessBuilder(Paths.get(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", String.join(File.pathSeparator, containerClasspath), "service.WarSmokeServer",
                base.toString(), Paths.get("target/mrp-1.0.0-SNAPSHOT.war").toString());
        launch.environment().put("MRP_DB_URL", jdbcUrl);
        launch.environment().put("MRP_DB_USERNAME", user);
        launch.environment().put("MRP_DB_PASSWORD", password);
        launch.environment().put("MRP_DB_DIALECT", "org.hibernate.dialect.MySQL5InnoDBDialect");
        launch.redirectErrorStream(true).redirectOutput(base.resolve("server.log").toFile());
        Process tomcat = launch.start();
        boolean verified = false;
        try {
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);
            while (!Files.exists(ready) && tomcat.isAlive() && System.nanoTime() < deadline) Thread.sleep(50);
            assertTrue("Tomcat 未就绪，日志：" + base.resolve("server.log"), Files.exists(ready));
            int port = Integer.parseInt(new String(Files.readAllBytes(ready), StandardCharsets.UTF_8));
            URL url = new URL("http://127.0.0.1:" + port + "/mrp/MRP_domrp.action");
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setConnectTimeout(5000); request.setReadTimeout(45000);
            assertEquals("Tomcat 日志：" + base.resolve("server.log"), 200, request.getResponseCode());
            try (InputStream input = request.getInputStream(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096]; int count;
                while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
                String body = new String(output.toByteArray(), StandardCharsets.UTF_8);
                assertTrue(body.contains("data-mrp-part=\"B\""));
                assertTrue(body.contains("data-mrp-part=\"C\""));
            } finally { request.disconnect(); }
            verified = true;
        } finally {
            tomcat.getOutputStream().close();
            if (!tomcat.waitFor(15, TimeUnit.SECONDS)) { tomcat.destroyForcibly(); throw new IllegalStateException("Tomcat did not stop cleanly"); }
            if (verified) assertEquals("Tomcat 关闭异常", 0, tomcat.exitValue());
        }
    }

    private static void await(CountDownLatch latch) {
        try { if (!latch.await(15, TimeUnit.SECONDS)) throw new IllegalStateException("Test latch timed out"); }
        catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); throw new IllegalStateException(interrupted); }
    }

    private static MrpRecalculationService service(MrpCalculationService calculator) { return new MrpRecalculationService(sessions, calculator); }
    private static void assertBatch(List<MRP> batch, int b, int c) {
        assertEquals(2, batch.size()); assertEquals("B", batch.get(0).getP_No()); assertEquals(b, batch.get(0).getPOR1());
        assertEquals("C", batch.get(1).getP_No()); assertEquals(c, batch.get(1).getPOR1());
    }
    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("Missing " + name);
        return value;
    }
    private static void sql(String statement) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password); Statement sql = connection.createStatement()) { sql.execute(statement); }
    }
    private static void executeScript(Path path) throws Exception {
        String script = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
                .replaceAll("(?s)/\\*.*?\\*/", "").replaceAll("(?m)^\\s*--.*$", "");
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password); Statement sql = connection.createStatement()) {
            for (String statement : script.split(";")) if (!statement.trim().isEmpty()) sql.execute(statement.trim());
        }
    }
    private static String snapshot() throws Exception { return values("SELECT CONCAT(id, ':', P_No, ':', POR1) FROM t_mrp ORDER BY P_No"); }
    private static String quantities() throws Exception { return values("SELECT CONCAT(P_No, ':', POR1) FROM t_mrp ORDER BY P_No"); }
    private static String values(String query) throws Exception {
        StringBuilder result = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, user, password); Statement sql = connection.createStatement(); ResultSet rows = sql.executeQuery(query)) {
            while (rows.next()) { if (result.length() > 0) result.append(','); result.append(rows.getString(1)); }
        }
        return result.toString();
    }
}
