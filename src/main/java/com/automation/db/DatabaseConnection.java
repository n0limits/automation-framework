package com.automation.db;

import java.util.Map;

/**
 * Database Connection Interface
 * Provides a common abstraction for different database types.
 *
 * Implementation notes:
 * - SQL databases: query parameter is a SQL statement
 * - MongoDB: query parameter is a JSON query string (e.g., {"collection": "users", "filter": {}})
 *
 * @author Victor Grozev
 */
public interface DatabaseConnection extends AutoCloseable {

    /**
     * Connect to the database.
     *
     * @param connectionString Database-specific connection string
     * @param properties Additional connection properties (username, password, database name, etc.)
     */
    void connect(String connectionString, Map<String, String> properties);

    /**
     * Disconnect from the database.
     */
    void disconnect();

    /**
     * Execute a read query.
     *
     * For SQL databases: executes a SELECT statement and returns List<Map<String, Object>>
     * For MongoDB: executes a find query and returns List<Document>
     *
     * @param query The query to execute (SQL statement or JSON query)
     * @return Query results (type depends on implementation)
     */
    Object executeQuery(String query);

    /**
     * Execute an update operation (INSERT, UPDATE, DELETE).
     *
     * @param query The update query/command
     */
    void executeUpdate(String query);

    /**
     * Check if connected to the database.
     *
     * @return true if connected
     */
    boolean isConnected();

    /**
     * Implements AutoCloseable for try-with-resources support.
     * This allows: try (DatabaseConnection conn = factory.getConnection()) { ... }
     */
    @Override
    default void close() {
        disconnect();
    }
}
