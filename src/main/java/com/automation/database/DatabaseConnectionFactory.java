package com.automation.database;

import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe Database Connection Factory using ThreadLocal storage.
 *
 * <p><b>DEPRECATED</b> — This class is no longer used by any active test code.
 * The framework has migrated to {@link ConnectionPoolManager} (HikariCP) for SQL
 * connections, accessed via {@link DatabaseTestUtils}.
 * MongoDB support is handled separately and has no active tests at this time.
 * This class is retained for reference only and should be removed once confirmed
 * that no future code requires direct ThreadLocal connection management.</p>
 *
 * @author Victor Grozev
 * @deprecated Use {@link ConnectionPoolManager} and {@link DatabaseTestUtils} instead.
 */
@Deprecated
@Slf4j
public class DatabaseConnectionFactory {

    private static final ThreadLocal<Map<DatabaseType, DatabaseConnection>> connections =
            ThreadLocal.withInitial(HashMap::new);

    private DatabaseConnectionFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get or create a database connection for the current thread.
     * Returns the same connection instance for repeated calls within
     * the same thread, enabling transaction continuity.
     *
     * @param type Database type
     * @return DatabaseConnection for the current thread
     */
    public static DatabaseConnection getConnection(DatabaseType type) {
        Map<DatabaseType, DatabaseConnection> threadConnections = connections.get();
        return threadConnections.computeIfAbsent(type, k -> createConnection(type));
    }

    private static DatabaseConnection createConnection(DatabaseType type) {
        log.info("Creating new database connection for type: {} (thread: {})",
                type, Thread.currentThread().getName());

        return switch (type) {
            case MONGODB -> new MongoDBConnection();
            case MYSQL, POSTGRESQL -> new SQLConnection(type);
        };
    }

    /**
     * Close all database connections for the current thread.
     * Call this in @AfterMethod / @After hooks to clean up.
     */
    public static void closeAllConnections() {
        Map<DatabaseType, DatabaseConnection> threadConnections = connections.get();
        threadConnections.values().forEach(connection -> {
            try {
                if (connection.isConnected()) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                log.warn("Error closing connection: {}", e.getMessage());
            }
        });
        threadConnections.clear();
        connections.remove();
        log.info("All database connections closed for thread: {}", Thread.currentThread().getName());
    }
}
