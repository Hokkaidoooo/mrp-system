package service;

import org.apache.catalina.startup.Tomcat;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** 独立 JVM 仅加载容器依赖，避免测试类路径替 WAR 补齐缺失依赖。 */
public final class WarSmokeServer {
    public static void main(String[] args) throws Exception {
        Path base = Paths.get(args[0]).toAbsolutePath();
        Path apps = Files.createDirectories(base.resolve("webapps"));
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(base.toString());
        tomcat.getHost().setAppBase(apps.toString());
        tomcat.setPort(0); tomcat.getConnector();
        tomcat.addWebapp("/mrp", Paths.get(args[1]).toAbsolutePath().toString());
        try {
            tomcat.start();
            Files.write(base.resolve("ready.txt"), String.valueOf(tomcat.getConnector().getLocalPort()).getBytes(StandardCharsets.UTF_8));
            while (System.in.read() != -1) { }
        } finally {
            tomcat.stop(); tomcat.destroy();
        }
    }
}
