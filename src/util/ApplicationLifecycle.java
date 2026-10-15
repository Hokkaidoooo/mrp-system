package util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public final class ApplicationLifecycle implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) { }
    @Override public void contextDestroyed(ServletContextEvent event) {
        try {
            HibernateUtils.close();
        } finally {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
            java.util.Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                java.sql.Driver driver = drivers.nextElement();
                if (driver.getClass().getClassLoader() != getClass().getClassLoader()) continue;
                try { java.sql.DriverManager.deregisterDriver(driver); }
                catch (java.sql.SQLException failure) { throw new IllegalStateException("Cannot deregister application JDBC driver", failure); }
            }
        }
    }
}
