package com.automation.db;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL Database Connection implementation using HikariCP connection pooling.
 * Supports MySQL and PostgreSQL databases.
 *
 * The connection pool is managed by ConnectionPoolManager which provides:
 * - High-performance connection pooling
 * - Automatic connection validation
 * - Thread-safe connection management
 *
 * @author Victor Grozev
 */
@Slf4j
public class SQLConnection implements DatabaseConnection {
    @Getter
    private Connection connection;
    private final DatabaseType type;
    private boolean connected = false;
    private final boolean usePooling;

    /**
     * Constructor - MUST accept DatabaseType parameter and is called from DatabaseConnectionFactory
     */
    public SQLConnection(DatabaseType type) {
        this(type, true); // Use pooling by default
    }

    /**
     * Constructor with pooling option
     *
     * @param type Database type
     * @param usePooling Whether to use connection pooling
     */
    public SQLConnection(DatabaseType type, boolean usePooling) {
        this.type = type;
        this.usePooling = usePooling;
    }

    @Override
    public void connect(String connectionString, Map<String, String> properties) {
        try {
            if (usePooling && (type == DatabaseType.MYSQL || type == DatabaseType.POSTGRESQL)) {
                // Use connection pool
                connection = ConnectionPoolManager.getInstance().getConnection(type);
                connected = true;
                log.info("Obtained connection from pool for {} database", type);
            } else {
                // Direct connection (fallback or for unsupported types)
                String username = properties.get("username");
                String password = properties.get("password");
                connection = DriverManager.getConnection(connectionString, username, password);
                connected = true;
                log.info("Successfully connected directly to {} database", type);
            }
        } catch (SQLException e) {
            log.error("Failed to connect to {} database", type, e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    @Override
    public void disconnect() {
        if (connection != null) {
            try {
                // When using pooling, close() returns the connection to the pool
                // rather than actually closing it
                connection.close();
                connected = false;
                if (usePooling) {
                    log.debug("{} connection returned to pool", type);
                } else {
                    log.info("{} connection closed", type);
                }
            } catch (SQLException e) {
                log.error("Error closing/returning connection", e);
            }
        }
    }

    /**
     * Executes a SQL query and returns results as a List of Maps.
     * Each Map represents a row with column names as keys.
     *
     * @param query SQL SELECT query to execute
     * @return List of Maps containing query results (never null)
     * @throws IllegalStateException if not connected
     * @throws RuntimeException if query execution fails
     */
    @Override
    public Object executeQuery(String query) {
        if (!connected) {
            throw new IllegalStateException("Not connected to database");
        }

        List<Map<String, Object>> results = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            log.debug("Query returned {} rows", results.size());
            return results;
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
        if (!connected) {
            return false;
        }
        // Additional validation - check if connection is actually valid
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            log.warn("Error checking connection validity", e);
            return false;
        }
    }

    /**
     * Get current pool statistics (only applicable when using pooling)
     *
     * @return Pool stats string or null if not using pooling
     */
    public String getPoolStats() {
        if (!usePooling) {
            return null;
        }
        return ConnectionPoolManager.getInstance().getPoolStats(type);
    }
}
