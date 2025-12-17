package com.amalixshop.database;

import com.amalixshop.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Private constructor - prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {

        Connection conn = DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
        );

        System.out.println("Database connection established");
        return conn;
    }
}



