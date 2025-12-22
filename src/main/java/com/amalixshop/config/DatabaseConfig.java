package com.amalixshop.config;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig {
    private static final Map<String, String> config;

    static {
        config = new HashMap<>();
        loadConfig();
    }

    public static void loadConfig() {
        config.put("DB_URL", "jdbc:postgresql://localhost:5432/amalixshop");
        config.put("DB_USER", "postgres");
        config.put("DB_PASSWORD", "Final@2025");
    }

    public static String getUrl() {
        return config.get("DB_URL");
    }

    public static String getUser() {
        return config.get("DB_USER");
    }

    public static String getPassword() {
        return config.get("DB_PASSWORD");
    }
}
