package com.automation.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Reads database schema metadata: table existence, columns, row counts.
 * Extracted from DatabaseTestUtils for single-responsibility.
 *
 * @author Victor Grozev
 */
@Slf4j
public class SchemaInspector {

    private final Supplier<Connection> connectionSupplier;
    private final DatabaseTestUtils dbUtils;

    /**
     * @param connectionSupplier supplies the current thread's JDBC connection
     * @param dbUtils            used for query execution (countRows, countRowsWhere)
     */
    public SchemaInspector(Supplier<Connection> connectionSupplier, DatabaseTestUtils dbUtils) {
        this.connectionSupplier = connectionSupplier;
        this.dbUtils = dbUtils;
    }

    /**
     * Check if table exists
     *
     * @param tableName Table name
     * @return true if table exists
     * @throws SQLException if query fails
     */
    public boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = connectionSupplier.get().getMetaData();

        try (ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
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
        DatabaseMetaData metaData = connectionSupplier.get().getMetaData();

        try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        }

        log.info("Table '{}' has columns: {}", tableName, columns);
        return columns;
    }

    /**
     * Count rows in table
     *
     * @param tableName Table name
     * @return Row count
     * @throws SQLException if query fails
     */
    public long countRows(String tableName) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM %s", tableName);
        Object count = dbUtils.executeQuerySingleValue(query);
        return count == null ? 0 : ((Number) count).longValue();
    }

    /**
     * Count rows matching condition
     *
     * @param tableName   Table name
     * @param whereClause WHERE clause (without WHERE keyword)
     * @param params      Parameters for WHERE clause
     * @return Row count
     * @throws SQLException if query fails
     */
    public long countRowsWhere(String tableName, String whereClause, Object... params) throws SQLException {
        String query = String.format("SELECT COUNT(*) FROM %s WHERE %s", tableName, whereClause);
        Object count = dbUtils.executeQuerySingleValue(query, params);
        return count == null ? 0 : ((Number) count).longValue();
    }

    /**
     * Check if record exists
     *
     * @param tableName   Table name
     * @param whereClause WHERE clause
     * @param params      Parameters
     * @return true if record exists
     * @throws SQLException if query fails
     */
    public boolean recordExists(String tableName, String whereClause, Object... params) throws SQLException {
        return countRowsWhere(tableName, whereClause, params) > 0;
    }
}
