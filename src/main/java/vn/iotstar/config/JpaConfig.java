package vn.iotstar.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaConfig {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = buildEntityManagerFactory();

    private JpaConfig() {
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        Map<String, Object> overrides = new HashMap<>();
        applyOverride(overrides, "jakarta.persistence.jdbc.url", "app.db.url", "APP_DB_URL");
        applyOverride(overrides, "jakarta.persistence.jdbc.user", "app.db.user", "APP_DB_USER");
        applyOverride(overrides, "jakarta.persistence.jdbc.password", "app.db.password", "APP_DB_PASSWORD");

        if (overrides.isEmpty()) {
            return Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        }
        return Persistence.createEntityManagerFactory("jpa-hibernate-mysql", overrides);
    }

    private static void applyOverride(Map<String, Object> overrides, String propertyName,
                                      String systemPropertyName, String envName) {
        String value = System.getProperty(systemPropertyName);
        if (value == null || value.isBlank()) {
            value = System.getenv(envName);
        }
        if (value != null && !value.isBlank()) {
            overrides.put(propertyName, value.trim());
        }
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }
}
