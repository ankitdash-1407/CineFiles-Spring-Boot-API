package com.cinefiles.backend;
//Every time you call DatabaseEngine.connect(), your server does this:
//
//Opens a network socket.
//
//Sends the username (root) to MySQL.
//
//MySQL checks the password and authenticates.
//
//MySQL allocates memory for the user.
//
//Java finally gets the Connection object.

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseEngine {

    // The Fleet Manager
    private static HikariDataSource dataSource;

    // The static block runs exactly ONCE when the app starts up
    static {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            System.err.println("[CRITICAL] Database Bridge Setup Failed! Environment variables missing.");
            System.exit(1); // Kill the app if secrets are missing
        }

        // Configure the Taxi Lot
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        // --- SYSTEM-LEVEL TUNING ---
        config.setMaximumPoolSize(10); // Max 10 connections at peak traffic
        config.setMinimumIdle(2);      // Keep 2 connections warmed up at all times
        config.setConnectionTimeout(30000); // Wait 30 seconds before giving up on a connection

        // Build the lot!
        dataSource = new HikariDataSource(config);
        System.out.println("[SYSTEM] HikariCP Connection Pool Initialized Successfully.");
    }

    // The Bridge Method (Your managers still call this exact same method!)
    public static Connection connect() throws SQLException {
        // Instead of building a new connection, grab one from the pool
        return dataSource.getConnection();
    }
}
