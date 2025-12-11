package com.automation.db;

import java.util.Map;

import java.util.Map;

public interface DatabaseConnection extends AutoCloseable {
    void connect(String connectionString, Map<String, String> properties);

    void disconnect();

    Object executeQuery(String query);

    void executeUpdate(String query);

    boolean isConnected();

    /**
     * Implements AutoCloseable for try-with-resources support
     * This allows: try (DatabaseConnection conn = factory.getConnection()) { ... }
     */
    @Override
    default void close() {
        disconnect();
    }
}
