package com.automation.database;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DatabaseConnectionFactory {
    private static final Map<DatabaseType, DatabaseConnection> connections = new ConcurrentHashMap<>();

    private DatabaseConnectionFactory() {
        // Private constructor to prevent instantiation
    }

    public static DatabaseConnection getConnection(DatabaseType type) {
        return connections.computeIfAbsent(type, k -> createConnection(type));
    }

    private static DatabaseConnection createConnection(DatabaseType type) {
        log.info("Creating new database connection for type: {}", type);

        return switch (type) {
            case MONGODB -> new MongoDBConnection();
            case MYSQL, POSTGRESQL -> new SQLConnection(type);
        };
    }

    public static void closeAllConnections() {
        connections.values().forEach(connection -> {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        });
        connections.clear();
        log.info("All database connections closed");
    }
}
