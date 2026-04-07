package nhom13.vn.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
//factory để tạo entity manager, quản lý kết nối đến database
public class JPAConfig {

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("XinNghiPhepPU", buildDbProperties());

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    private static Map<String, Object> buildDbProperties() {
        String dbVendor = getEnv("DB_VENDOR", "sqlserver").toLowerCase(Locale.ROOT).trim();
        Map<String, Object> properties = new HashMap<>();

        if ("postgres".equals(dbVendor) || "postgresql".equals(dbVendor)) {
            properties.put("jakarta.persistence.jdbc.url",
                    getEnv("DB_URL", "jdbc:postgresql://localhost:5432/XinNghiPhep"));
            properties.put("jakarta.persistence.jdbc.driver",
                    getEnv("DB_DRIVER", "org.postgresql.Driver"));
            properties.put("jakarta.persistence.jdbc.user", getEnv("DB_USER", "postgres"));
            properties.put("jakarta.persistence.jdbc.password", getEnv("DB_PASSWORD", "postgres"));
            properties.put("hibernate.dialect",
                    getEnv("HIBERNATE_DIALECT", "org.hibernate.dialect.PostgreSQLDialect"));
        } else {
            properties.put("jakarta.persistence.jdbc.url",
                    getEnv("DB_URL",
                            "jdbc:sqlserver://localhost:1433;databaseName=XinNghiPhep;encrypt=true;trustServerCertificate=true"));
            properties.put("jakarta.persistence.jdbc.driver",
                    getEnv("DB_DRIVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver"));
            properties.put("jakarta.persistence.jdbc.user", getEnv("DB_USER", "sa"));
            properties.put("jakarta.persistence.jdbc.password", getEnv("DB_PASSWORD", "123456"));
            properties.put("hibernate.dialect",
                    getEnv("HIBERNATE_DIALECT", "org.hibernate.dialect.SQLServer2012Dialect"));
        }

        properties.put("hibernate.hbm2ddl.auto", getEnv("HIBERNATE_HBM2DDL_AUTO", "update"));
        properties.put("hibernate.show_sql", getEnv("HIBERNATE_SHOW_SQL", "true"));
        properties.put("hibernate.format_sql", getEnv("HIBERNATE_FORMAT_SQL", "true"));
        return properties;
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
