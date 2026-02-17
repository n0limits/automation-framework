package com.automation.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Test Data Builder
 * Fluent API for building and inserting test data into database
 *
 * Features:
 * - Fluent builder pattern for readability
 * - Automatic ID tracking
 * - Batch insert support
 * - Relationship management (foreign keys)
 * - Default value handling
 * - Cleanup tracking for teardown
 *
 * Usage Example:
 * <pre>
 * TestDataBuilder builder = new TestDataBuilder(dbUtils);
 *
 * Long userId = builder
 *     .forTable("users")
 *     .with("username", "testuser")
 *     .with("email", "test@example.com")
 *     .with("status", "active")
 *     .insert();
 *
 * Long orderId = builder
 *     .forTable("orders")
 *     .with("user_id", userId)
 *     .with("amount", 100.50)
 *     .with("status", "pending")
 *     .insert();
 *
 * builder.cleanupAll(); // Cleanup in reverse order
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class TestDataBuilder {
    private final DatabaseTestUtils dbUtils;
    private final List<CleanupRecord> cleanupRecords;

    private String currentTable;
    private Map<String, Object> currentValues;
    private Map<String, Object> defaultValues;

    /**
     * Initialize test data builder
     *
     * @param dbUtils Database utilities instance
     */
    public TestDataBuilder(DatabaseTestUtils dbUtils) {
        this.dbUtils = dbUtils;
        this.cleanupRecords = new ArrayList<>();
        this.currentValues = new LinkedHashMap<>();
        this.defaultValues = new LinkedHashMap<>();

        log.info("TestDataBuilder initialized");
    }

    // ========== Fluent Builder Methods ==========

    /**
     * Start building data for specified table
     *
     * @param tableName Table name
     * @return this for chaining
     */
    public TestDataBuilder forTable(String tableName) {
        this.currentTable = tableName;
        this.currentValues = new LinkedHashMap<>();
        log.debug("Building data for table: {}", tableName);
        return this;
    }

    /**
     * Add column value
     *
     * @param column Column name
     * @param value Column value
     * @return this for chaining
     */
    public TestDataBuilder with(String column, Object value) {
        currentValues.put(column, value);
        log.debug("Added value: {} = {}", column, value);
        return this;
    }

    /**
     * Add multiple column values
     *
     * @param values Map of column names to values
     * @return this for chaining
     */
    public TestDataBuilder withAll(Map<String, Object> values) {
        currentValues.putAll(values);
        log.debug("Added {} values", values.size());
        return this;
    }

    /**
     * Add timestamp column with current time
     *
     * @param column Column name
     * @return this for chaining
     */
    public TestDataBuilder withCurrentTimestamp(String column) {
        currentValues.put(column, Timestamp.valueOf(LocalDateTime.now()));
        log.debug("Added current timestamp for: {}", column);
        return this;
    }

    /**
     * Add timestamp column with specific time
     *
     * @param column Column name
     * @param dateTime DateTime value
     * @return this for chaining
     */
    public TestDataBuilder withTimestamp(String column, LocalDateTime dateTime) {
        currentValues.put(column, Timestamp.valueOf(dateTime));
        log.debug("Added timestamp for: {} = {}", column, dateTime);
        return this;
    }

    /**
     * Add null value for column
     *
     * @param column Column name
     * @return this for chaining
     */
    public TestDataBuilder withNull(String column) {
        currentValues.put(column, null);
        log.debug("Added null for: {}", column);
        return this;
    }

    // ========== Default Values ==========

    /**
     * Set default value for a column
     * Default values are used if not explicitly set
     *
     * @param column Column name
     * @param value Default value
     * @return this for chaining
     */
    public TestDataBuilder setDefault(String column, Object value) {
        defaultValues.put(column, value);
        log.debug("Set default: {} = {}", column, value);
        return this;
    }

    /**
     * Clear default values
     *
     * @return this for chaining
     */
    public TestDataBuilder clearDefaults() {
        defaultValues.clear();
        log.debug("Cleared default values");
        return this;
    }

    // ========== Insert Operations ==========

    /**
     * Insert record and return generated ID
     *
     * @return Generated ID
     * @throws SQLException if insert fails
     */
    public Long insert() throws SQLException {
        return insert(true);
    }

    /**
     * Insert record
     *
     * @param trackForCleanup Track record for cleanup
     * @return Generated ID
     * @throws SQLException if insert fails
     */
    public Long insert(boolean trackForCleanup) throws SQLException {
        validateCurrentBuild();

        // Merge default values with current values
        Map<String, Object> finalValues = new LinkedHashMap<>(defaultValues);
        finalValues.putAll(currentValues);

        // Build INSERT query
        String query = buildInsertQuery(currentTable, finalValues);

        // Execute insert
        log.info("Inserting into {}: {}", currentTable, finalValues);
        List<Object> generatedKeys = dbUtils.executeInsert(query, finalValues.values().toArray());

        Long generatedId = null;
        if (!generatedKeys.isEmpty()) {
            Object key = generatedKeys.get(0);
            generatedId = key instanceof Number ? ((Number) key).longValue() : null;
        }

        log.info("[PASS] Insert successful. Generated ID: {}", generatedId);

        // Track for cleanup
        if (trackForCleanup && generatedId != null) {
            CleanupRecord cleanup = new CleanupRecord(currentTable, "id", generatedId);
            cleanupRecords.add(cleanup);
            log.debug("Tracking record for cleanup: {}", cleanup);
        }

        return generatedId;
    }

    /**
     * Insert multiple records in batch
     *
     * @param records List of records (each record is a map of column->value)
     * @return List of generated IDs
     * @throws SQLException if batch insert fails
     */
    public List<Long> insertBatch(List<Map<String, Object>> records) throws SQLException {
        validateCurrentTable();

        if (records.isEmpty()) {
            log.warn("No records to insert in batch");
            return Collections.emptyList();
        }

        log.info("Batch inserting {} records into {}", records.size(), currentTable);

        List<Long> generatedIds = new ArrayList<>();

        for (Map<String, Object> record : records) {
            currentValues = new LinkedHashMap<>(record);
            Long id = insert(true);
            if (id != null) {
                generatedIds.add(id);
            }
        }

        log.info("[PASS] Batch insert completed: {} records", generatedIds.size());
        return generatedIds;
    }

    // ========== Update Operations ==========

    /**
     * Update record by ID
     *
     * @param id Record ID
     * @return Number of affected rows
     * @throws SQLException if update fails
     */
    public int update(Long id) throws SQLException {
        return updateWhere("id = ?", id);
    }

    /**
     * Update records matching condition
     *
     * @param whereClause WHERE clause (without WHERE keyword)
     * @param params Parameters for WHERE clause
     * @return Number of affected rows
     * @throws SQLException if update fails
     */
    public int updateWhere(String whereClause, Object... params) throws SQLException {
        validateCurrentBuild();

        if (currentValues.isEmpty()) {
            log.warn("No values to update");
            return 0;
        }

        // Build UPDATE query
        String query = buildUpdateQuery(currentTable, currentValues, whereClause);
        Object[] allParams = combineParams(currentValues.values().toArray(), params);

        log.info("Updating {}: {} WHERE {}", currentTable, currentValues, whereClause);
        int affected = dbUtils.executeUpdate(query, allParams);
        log.info("[PASS] Update affected {} rows", affected);

        return affected;
    }

    // ========== Cleanup Operations ==========

    /**
     * Cleanup all tracked records (in reverse order)
     *
     * @throws SQLException if cleanup fails
     */
    public void cleanupAll() throws SQLException {
        log.info("Cleaning up {} tracked records", cleanupRecords.size());

        // Cleanup in reverse order (respect foreign key constraints)
        for (int i = cleanupRecords.size() - 1; i >= 0; i--) {
            CleanupRecord record = cleanupRecords.get(i);
            try {
                cleanup(record);
            } catch (SQLException e) {
                log.error("Failed to cleanup record: {}", record, e);
                // Continue with other cleanups
            }
        }

        cleanupRecords.clear();
        log.info("[PASS] Cleanup completed");
    }

    /**
     * Cleanup specific record
     *
     * @param record Cleanup record
     * @throws SQLException if cleanup fails
     */
    private void cleanup(CleanupRecord record) throws SQLException {
        String query = String.format("DELETE FROM %s WHERE %s = ?",
                record.tableName, record.idColumn);

        int affected = dbUtils.executeUpdate(query, record.idValue);
        log.debug("Deleted {} rows from {} where {} = {}",
                affected, record.tableName, record.idColumn, record.idValue);
    }

    /**
     * Cleanup records from specific table
     *
     * @param tableName Table name
     * @throws SQLException if cleanup fails
     */
    public void cleanupTable(String tableName) throws SQLException {
        log.info("Cleaning up records from table: {}", tableName);

        List<CleanupRecord> toRemove = new ArrayList<>();

        for (CleanupRecord record : cleanupRecords) {
            if (record.tableName.equals(tableName)) {
                cleanup(record);
                toRemove.add(record);
            }
        }

        cleanupRecords.removeAll(toRemove);
        log.info("[PASS] Cleaned up {} records from {}", toRemove.size(), tableName);
    }

    // ========== Query Building ==========

    /**
     * Build INSERT query
     *
     * @param tableName Table name
     * @param values Column values
     * @return INSERT query
     */
    private String buildInsertQuery(String tableName, Map<String, Object> values) {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        int i = 0;
        for (String column : values.keySet()) {
            if (i > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(column);
            placeholders.append("?");
            i++;
        }

        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName, columns, placeholders);
    }

    /**
     * Build UPDATE query
     *
     * @param tableName Table name
     * @param values Column values
     * @param whereClause WHERE clause
     * @return UPDATE query
     */
    private String buildUpdateQuery(String tableName, Map<String, Object> values, String whereClause) {
        StringBuilder setClause = new StringBuilder();

        int i = 0;
        for (String column : values.keySet()) {
            if (i > 0) {
                setClause.append(", ");
            }
            setClause.append(column).append(" = ?");
            i++;
        }

        return String.format("UPDATE %s SET %s WHERE %s",
                tableName, setClause, whereClause);
    }

    // ========== Validation ==========

    /**
     * Validate current build state
     *
     * @throws IllegalStateException if state is invalid
     */
    private void validateCurrentBuild() {
        validateCurrentTable();

        if (currentValues.isEmpty() && defaultValues.isEmpty()) {
            throw new IllegalStateException("No values specified for insert/update");
        }
    }

    /**
     * Validate current table is set
     *
     * @throws IllegalStateException if table not set
     */
    private void validateCurrentTable() {
        if (currentTable == null || currentTable.isEmpty()) {
            throw new IllegalStateException("Table not specified. Use forTable() first");
        }
    }

    // ========== Helper Methods ==========

    /**
     * Combine parameter arrays
     *
     * @param params1 First parameter array
     * @param params2 Second parameter array
     * @return Combined array
     */
    private Object[] combineParams(Object[] params1, Object[] params2) {
        Object[] combined = new Object[params1.length + params2.length];
        System.arraycopy(params1, 0, combined, 0, params1.length);
        System.arraycopy(params2, 0, combined, params1.length, params2.length);
        return combined;
    }

    /**
     * Get tracked cleanup records
     *
     * @return List of cleanup records
     */
    public List<CleanupRecord> getTrackedRecords() {
        return new ArrayList<>(cleanupRecords);
    }

    /**
     * Get count of tracked records
     *
     * @return Number of tracked records
     */
    public int getTrackedRecordCount() {
        return cleanupRecords.size();
    }

    // ========== Cleanup Record Class ==========

    /**
     * Record for cleanup tracking
     */
    public static class CleanupRecord {
        public final String tableName;
        public final String idColumn;
        public final Object idValue;

        public CleanupRecord(String tableName, String idColumn, Object idValue) {
            this.tableName = tableName;
            this.idColumn = idColumn;
            this.idValue = idValue;
        }

        @Override
        public String toString() {
            return String.format("%s.%s = %s", tableName, idColumn, idValue);
        }
    }
}
