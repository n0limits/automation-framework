package com.automation.database;

import com.automation.config.TestConfig;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

/**
 * Database Test Utilities
 * Provides database connectivity and common database operations for testing
 *
 * Features:
 * - Connection management with connection pooling
 * - Query execution (SELECT, INSERT, UPDATE, DELETE)
 * - Result set processing
 * - Transaction management
 * - Database cleanup operations
 * - Schema validation
 *
 * Usage Example:
 * <pre>
 * DatabaseTestUtils db = new DatabaseTestUtils();
 * List<Map<String, Object>> results = db.executeQuery("SELECT * FROM users WHERE id = ?", userId);
 * db.executeUpdate("UPDATE users SET status = ? WHERE id = ?", "active", userId);
 * db.cleanup();
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class DatabaseTestUtils {
    private Connection connection;
    private final TestConfig config;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    /**
     * Initialize database utilities with default configuration
     */
    public DatabaseTestUtils() {
        this.config = TestConfig.getInstance();
        // Get database configuration from TestConfig or environment variables
        this.dbUrl = System.getProperty("db.url", System.getenv("DB_URL"));
        this.dbUser = System.getProperty("db.user", System.getenv("DB_USER"));
        this.dbPassword = System.getProperty("db.password", System.getenv("DB_PASSWORD"));

        log.info("DatabaseTestUtils initialized with URL: {}", maskPassword(dbUrl));
    }

    /**
     * Initialize with custom database configuration
     *
     * @param dbUrl Database JDBC URL
     * @param dbUser Database username
     * @param dbPassword Database password
     */
    public DatabaseTestUtils(String dbUrl, String dbUser, String dbPassword) {
        this.config = TestConfig.getInstance();
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;

        log.info("DatabaseTestUtils initialized with custom URL: {}", maskPassword(dbUrl));
    }

    // ========== Connection Management ==========

    /**
     * Establish database connection
     *
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            log.info("Establishing database connection...");
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            log.info("✅ Database connection established");
        }
        return connection;
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    log.info("Database connection closed");
                }
            } catch (SQLException e) {
                log.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Check if connection is valid
     *
     * @return true if connection is valid
     */
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            log.error("Error checking connection validity", e);
            return false;
        }
    }

    // ========== Query Execution ==========

    /**
     * Execute SELECT query and return results as list of maps
     *
     * @param query SQL query
     * @param params Query parameters (for PreparedStatement)
     * @return List of result rows as maps (column_name -> value)
     * @throws SQLException if query execution fails
     */
    public List<Map<String, Object>> executeQuery(String query, Object... params) throws SQLException {
        log.info("Executing query: {}", query);
        log.debug("Query parameters: {}", Arrays.toString(params));

        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement stmt = prepareStatement(query, params);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        }

        log.info("Query returned {} rows", results.size());
        return results;
    }

    /**
     * Execute query and return single row
     *
     * @param query SQL query
     * @param params Query parameters
     * @return Single row as map, or null if no results
     * @throws SQLException if query execution fails
     */
    public Map<String, Object> executeQuerySingleRow(String query, Object... params) throws SQLException {
        List<Map<String, Object>> results = executeQuery(query, params);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Execute query and return single value
     *
     * @param query SQL query
     * @param params Query parameters
     * @return Single value, or null if no results
     * @throws SQLException if query execution fails
     */
    public Object executeQuerySingleValue(String query, Object... params) throws SQLException {
        Map<String, Object> row = executeQuerySingleRow(query, params);
        return row == null ? null : row.values().iterator().next();
    }

    /**
     * Execute UPDATE, INSERT, or DELETE query
     *
     * @param query SQL query
     * @param params Query parameters
     * @return Number of affected rows
     * @throws SQLException if query execution fails
     */
    public int executeUpdate(String query, Object... params) throws SQLException {
        log.info("Executing update: {}", query);
        log.debug("Update parameters: {}", Arrays.toString(params));

        try (PreparedStatement stmt = prepareStatement(query, params)) {
            int affectedRows = stmt.executeUpdate();
            log.info("Update affected {} rows", affectedRows);
            return affectedRows;
        }
    }

    /**
     * Execute INSERT query and return generated keys
     *
     * @param query SQL INSERT query
     * @param params Query parameters
     * @return Generated keys
     * @throws SQLException if query execution fails
     */
    public List<Object> executeInsert(String query, Object... params) throws SQLException {
        log.info("Executing insert: {}", query);
        log.debug("Insert parameters: {}", Arrays.toString(params));

        List<Object> generatedKeys = new ArrayList<>();

        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(stmt, params);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    while (rs.next()) {
                        generatedKeys.add(rs.getObject(1));
                    }
                }
            }

            log.info("Insert created {} rows with keys: {}", affectedRows, generatedKeys);
            return generatedKeys;
        }
    }

    /**
     * Execute batch updates
     *
     * @param query SQL query
     * @param paramsList List of parameter arrays
     * @return Array of update counts
     * @throws SQLException if batch execution fails
     */
    public int[] executeBatch(String query, List<Object[]> paramsList) throws SQLException {
        log.info("Executing batch update: {}", query);
        log.info("Batch size: {}", paramsList.size());

        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (Object[] params : paramsList) {
                setParameters(stmt, params);
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            log.info("Batch completed: {} operations", results.length);
            return results;
        }
    }

    // ========== Transaction Management ==========

    /**
     * Begin transaction
     *
     * @throws SQLException if transaction start fails
     */
    public void beginTransaction() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        log.info("Transaction started");
    }

    /**
     * Commit transaction
     *
     * @throws SQLException if commit fails
     */
    public void commitTransaction() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
            connection.setAutoCommit(true);
            log.info("Transaction committed");
        }
    }

    /**
     * Rollback transaction
     *
     * @throws SQLException if rollback fails
     */
    public void rollbackTransaction() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
            connection.setAutoCommit(true);
            log.info("Transaction rolled back");
        }
    }

    /**
     * Execute code within a transaction
     *
     * @param transaction Transaction code to execute
     * @throws Exception if transaction fails
     */
    public void executeInTransaction(TransactionCallback transaction) throws Exception {
        try {
            beginTransaction();
            transaction.execute(this);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            log.error("Transaction failed and was rolled back", e);
            throw e;
        }
    }

    // ========== Data Validation ==========

    /**
     * Count rows in table
     *
     * @param tableName Table name
     * @return Row count
     * @throws SQLException if query fails
     */
    public long countRows(String tableName) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM %s", tableName);
        Object count = executeQuerySingleValue(query);
        return count == null ? 0 : ((Number) count).longValue();
    }

    /**
     * Count rows matching condition
     *
     * @param tableName Table name
     * @param whereClause WHERE clause (without WHERE keyword)
     * @param params Parameters for WHERE clause
     * @return Row count
     * @throws SQLException if query fails
     */
    public long countRowsWhere(String tableName, String whereClause, Object... params) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s", tableName, whereClause);
        Object count = executeQuerySingleValue(query, params);
        return count == null ? 0 : ((Number) count).longValue();
    }

    /**
     * Check if record exists
     *
     * @param tableName Table name
     * @param whereClause WHERE clause
     * @param params Parameters
     * @return true if record exists
     * @throws SQLException if query fails
     */
    public boolean recordExists(String tableName, String whereClause, Object... params) throws SQLException {
        return countRowsWhere(tableName, whereClause, params) > 0;
    }

    /**
     * Get table columns
     *
     * @param tableName Table name
     * @return List of column names
     * @throws SQLException if query fails
     */
    public List<String> getTableColumns(String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        DatabaseMetaData metaData = getConnection().getMetaData();

        try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        }

        log.info("Table '{}' has columns: {}", tableName, columns);
        return columns;
    }

    /**
     * Check if table exists
     *
     * @param tableName Table name
     * @return true if table exists
     * @throws SQLException if query fails
     */
    public boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = getConnection().getMetaData();

        try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    // ========== Data Cleanup ==========

    /**
     * Delete all rows from table
     *
     * @param tableName Table name
     * @return Number of deleted rows
     * @throws SQLException if deletion fails
     */
    public int truncateTable(String tableName) throws SQLException {
        log.warn("Truncating table: {}", tableName);
        String query = String.format("DELETE FROM %s", tableName);
        return executeUpdate(query);
    }

    /**
     * Delete rows matching condition
     *
     * @param tableName Table name
     * @param whereClause WHERE clause
     * @param params Parameters
     * @return Number of deleted rows
     * @throws SQLException if deletion fails
     */
    public int deleteWhere(String tableName, String whereClause, Object... params) throws SQLException {
        log.info("Deleting from {}: {}", tableName, whereClause);
        String query = String.format("DELETE FROM %s WHERE %s", tableName, whereClause);
        return executeUpdate(query, params);
    }

    // ========== Helper Methods ==========

    /**
     * Prepare statement with parameters
     *
     * @param query SQL query
     * @param params Parameters
     * @return Prepared statement
     * @throws SQLException if preparation fails
     */
    private PreparedStatement prepareStatement(String query, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        setParameters(stmt, params);
        return stmt;
    }

    /**
     * Set parameters on prepared statement
     *
     * @param stmt PreparedStatement
     * @param params Parameters
     * @throws SQLException if parameter setting fails
     */
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    /**
     * Mask password in connection string for logging
     *
     * @param url Connection URL
     * @return Masked URL
     */
    private String maskPassword(String url) {
        if (url == null) {
            return "null";
        }
        return url.replaceAll("password=[^&;]+", "password=***");
    }

    /**
     * Cleanup resources (close connection)
     */
    public void cleanup() {
        closeConnection();
    }

    /**
     * Functional interface for transaction callbacks
     */
    @FunctionalInterface
    public interface TransactionCallback {
        void execute(DatabaseTestUtils db) throws Exception;
    }
}
