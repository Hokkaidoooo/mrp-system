package util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtils {
    static Configuration cfg = null;
    static SessionFactory sessionFactory = null;

    static {
        cfg = new Configuration();
        cfg.configure();
        applyExternalConfiguration(cfg);
        sessionFactory = cfg.buildSessionFactory();
    }

    public static Session getSessionobject() {
        return sessionFactory.getCurrentSession();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private static void applyExternalConfiguration(Configuration configuration) {
        setOptionalProperty(configuration, "hibernate.connection.driver_class",
                "MRP_DB_DRIVER", "mrp.db.driver", "com.mysql.jdbc.Driver");
        setRequiredProperty(configuration, "hibernate.connection.url",
                "MRP_DB_URL", "mrp.db.url");
        setRequiredProperty(configuration, "hibernate.connection.username",
                "MRP_DB_USERNAME", "mrp.db.username");
        setOptionalProperty(configuration, "hibernate.connection.password",
                "MRP_DB_PASSWORD", "mrp.db.password", "");
        setOptionalProperty(configuration, "hibernate.dialect",
                "MRP_DB_DIALECT", "mrp.db.dialect", "org.hibernate.dialect.MySQL5Dialect");
        setOptionalProperty(configuration, "hibernate.hbm2ddl.auto",
                "MRP_HBM2DDL_AUTO", "mrp.hibernate.hbm2ddl.auto", "validate");
        setOptionalProperty(configuration, "hibernate.show_sql",
                "MRP_HIBERNATE_SHOW_SQL", "mrp.hibernate.show_sql", "false");
        setOptionalProperty(configuration, "hibernate.format_sql",
                "MRP_HIBERNATE_FORMAT_SQL", "mrp.hibernate.format_sql", "false");
    }

    private static void setRequiredProperty(Configuration configuration, String hibernateProperty,
                                            String environmentName, String systemPropertyName) {
        String value = readSetting(environmentName, systemPropertyName);
        if (value == null) {
            throw new IllegalStateException("Missing database configuration: set environment variable "
                    + environmentName + " or JVM property -D" + systemPropertyName);
        }
        configuration.setProperty(hibernateProperty, value);
    }

    private static void setOptionalProperty(Configuration configuration, String hibernateProperty,
                                            String environmentName, String systemPropertyName,
                                            String defaultValue) {
        String value = readSetting(environmentName, systemPropertyName);
        configuration.setProperty(hibernateProperty, value == null ? defaultValue : value);
    }

    private static String readSetting(String environmentName, String systemPropertyName) {
        String value = System.getProperty(systemPropertyName);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(environmentName);
        }
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
