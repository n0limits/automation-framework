package com.automation.db;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.Map;

@Slf4j
public class SQLConnection implements DatabaseConnection {
    @Getter
    private Connection connection;
    private final DatabaseType type;
    private boolean connected = false;

    /**
     * Constructor - MUST accept DatabaseType parameter and is called from DatabaseConnectionFactory
     */
    public SQLConnection(DatabaseType type) {
        this.type = type;
    }

    @Override
    public void connect(String connectionString, Map<String, String> properties) {
        try {
            String username = properties.get("username");
            String password = properties.get("password");

            connection = DriverManager.getConnection(connectionString, username, password);
            connected = true;
            log.info("Successfully connected to {} database", type);
        } catch (SQLException e) {
            log.error("Failed to connect to {} database", type, e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    @Override
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                connected = false;
                log.info("{} connection closed", type);
            } catch (SQLException e) {
                log.error("Error closing connection", e);
            }
        }
    }

    @Override
    public Object executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            return rs;
        } catch (SQLException e) {
            log.error("Error executing query: {}", query, e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    @Override
    public void executeUpdate(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
            log.info("Update executed successfully");
        } catch (SQLException e) {
            log.error("Error executing update: {}", query, e);
            throw new RuntimeException("Update execution failed", e);
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

}
