package com.automation.database;

import com.automation.config.ConfigReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Connection Pool Manager using HikariCP
 * Provides high-performance connection pooling for SQL databases.
 *
 * Features:
 * - Thread-safe connection pool management
 * - Configurable pool sizes
 * - Automatic connection validation
 * - Support for multiple database types (MySQL, PostgreSQL)
 * - Graceful shutdown
 *
 * Configuration (via application.properties or environment variables):
 * - db.pool.maximum.size (default: 10)
 * - db.pool.minimum.idle (default: 2)
 * - db.pool.connection.timeout (default: 30000ms)
 * - db.pool.idle.timeout (default: 600000ms)
 * - db.pool.max.lifetime (default: 1800000ms)
 *
 * @author Victor Grozev
 */
@Slf4j
public class ConnectionPoolManager {
    private static final ConnectionPoolManager INSTANCE = new ConnectionPoolManager();
    private final Map<DatabaseType, HikariDataSource> pools = new ConcurrentHashMap<>();

    // Pool configuration defaults
    private static final int DEFAULT_MAX_POOL_SIZE = 10;
    private static final int DEFAULT_MIN_IDLE = 2;
    private static final long DEFAULT_CONNECTION_TIMEOUT = 30000;
    private static final long DEFAULT_IDLE_TIMEOUT = 600000;
    private static final long DEFAULT_MAX_LIFETIME = 1800000;

    private ConnectionPoolManager() {
        log.info("ConnectionPoolManager initialized");
    }

    /**
     * Get singleton instance
     *
     * @return ConnectionPoolManager instance
     */
    public static ConnectionPoolManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get a connection from the pool for the specified database type
     *
     * @param type Database type
     * @return Connection from the pool
     * @throws SQLException if unable to get connection
     */
    public Connection getConnection(DatabaseType type) throws SQLException {
        HikariDataSource dataSource = pools.computeIfAbsent(type, this::createDataSource);
        return dataSource.getConnection();
    }

    /**
     * Create a HikariCP data source for the specified database type
     *
     * @param type Database type
     * @return Configured HikariDataSource
     */
    private HikariDataSource createDataSource(DatabaseType type) {
        log.info("Creating connection pool for database type: {}", type);

        HikariConfig config = new HikariConfig();
        config.setPoolName("HikariPool-" + type.name());

        // Configure based on database type
        switch (type) {
            case MYSQL -> configureMySql(config);
            case POSTGRESQL -> configurePostgres(config);
            default -> throw new IllegalArgumentException("Unsupported database type for pooling: " + type);
        }

        // Common pool settings from configuration
        config.setMaximumPoolSize(ConfigReader.getIntProperty("db.pool.maximum.size", DEFAULT_MAX_POOL_SIZE));
        config.setMinimumIdle(ConfigReader.getIntProperty("db.pool.minimum.idle", DEFAULT_MIN_IDLE));
        config.setConnectionTimeout(ConfigReader.getIntProperty("db.pool.connection.timeout", (int) DEFAULT_CONNECTION_TIMEOUT));
        config.setIdleTimeout(ConfigReader.getIntProperty("db.pool.idle.timeout", (int) DEFAULT_IDLE_TIMEOUT));
        config.setMaxLifetime(ConfigReader.getIntProperty("db.pool.max.lifetime", (int) DEFAULT_MAX_LIFETIME));

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");

        HikariDataSource dataSource = new HikariDataSource(config);
        log.info("Connection pool created for {}: maxPoolSize={}, minIdle={}",
                type, config.getMaximumPoolSize(), config.getMinimumIdle());

        return dataSource;
    }

    /**
     * Configure MySQL connection
     */
    private void configureMySql(HikariConfig config) {
        String jdbcUrl = ConfigReader.getProperty("sql.connection.string");
        String username = ConfigReader.getProperty("sql.username");
        String password = ConfigReader.getProperty("sql.password");

        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            throw new IllegalStateException("MySQL connection string not configured (sql.connection.string)");
        }

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // MySQL-specific settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
    }

    /**
     * Configure PostgreSQL connection
     */
    private void configurePostgres(HikariConfig config) {
        String jdbcUrl = ConfigReader.getProperty("postgres.connection.string");
        String username = ConfigReader.getProperty("postgres.username");
        String password = ConfigReader.getProperty("postgres.password");

        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            throw new IllegalStateException("PostgreSQL connection string not configured (postgres.connection.string)");
        }

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
    }

    /**
     * Get pool statistics for monitoring
     *
     * @param type Database type
     * @return Pool statistics as string, or null if pool doesn't exist
     */
    public String getPoolStats(DatabaseType type) {
        HikariDataSource dataSource = pools.get(type);
        if (dataSource == null) {
            return null;
        }

        return String.format("Pool[%s]: active=%d, idle=%d, waiting=%d, total=%d",
                type.name(),
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
                dataSource.getHikariPoolMXBean().getTotalConnections());
    }

    /**
     * Close a specific connection pool
     *
     * @param type Database type
     */
    public void closePool(DatabaseType type) {
        HikariDataSource dataSource = pools.remove(type);
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("Closing connection pool for: {}", type);
            dataSource.close();
        }
    }

    /**
     * Close all connection pools (call during test suite teardown)
     */
    public void closeAllPools() {
        log.info("Closing all connection pools...");
        pools.forEach((type, dataSource) -> {
            if (!dataSource.isClosed()) {
                log.info("Closing pool: {}", type);
                dataSource.close();
            }
        });
        pools.clear();
        log.info("All connection pools closed");
    }

    /**
     * Check if a pool exists and is active
     *
     * @param type Database type
     * @return true if pool exists and is running
     */
    public boolean isPoolActive(DatabaseType type) {
        HikariDataSource dataSource = pools.get(type);
        return dataSource != null && !dataSource.isClosed() && dataSource.isRunning();
    }
}
