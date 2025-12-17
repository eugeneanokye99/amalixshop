package com.amalixshop.config;


import java.util.Properties;


public class DatabaseConfig {
    private static Properties properties;

    static {
        properties = new Properties();
      loadConfig();
    }


    public static void loadConfig() {
        // Map environment variables to properties
        properties.setProperty("DB_URL", "jdbc:postgresql://localhost:5432/amalixshop");

        properties.setProperty("DB_USER", "postgres");
        properties.setProperty("DB_PASSWORD", "Final@2025");

    }


    // Public getters
    public static String getUrl() {
        return properties.getProperty("DB_URL");
    }

    public static String getUser() {
        return properties.getProperty("DB_USER");
    }

    public static String getPassword() {
        return properties.getProperty("DB_PASSWORD");
    }



}