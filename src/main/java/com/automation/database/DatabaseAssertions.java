package com.automation.database;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;

/**
 * Fluent Database Assertions
 * Provides readable and chainable assertions for database testing
 *
 * Usage Example:
 * <pre>
 * DatabaseAssertions.assertThat(dbUtils)
 *     .table("users").exists()
 *     .table("users").hasRowCount(5)
 *     .table("users").hasRecord("id = ?", userId)
 *     .query("SELECT * FROM users WHERE id = ?", userId)
 *         .returnsOneRow()
 *         .columnEquals("username", "testuser")
 *         .columnEquals("status", "active");
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class DatabaseAssertions {
    private final DatabaseTestUtils dbUtils;

    private DatabaseAssertions(DatabaseTestUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    /**
     * Start fluent assertion chain
     *
     * @param dbUtils Database utilities instance
     * @return DatabaseAssertions instance for chaining
     */
    public static DatabaseAssertions assertThat(DatabaseTestUtils dbUtils) {
        return new DatabaseAssertions(dbUtils);
    }

    // ========== Table Assertions ==========

    /**
     * Start assertions for a specific table
     *
     * @param tableName Table name
     * @return TableAssertions instance
     */
    public TableAssertions table(String tableName) {
        return new TableAssertions(dbUtils, tableName);
    }

    /**
     * Start assertions for a query
     *
     * @param query SQL query
     * @param params Query parameters
     * @return QueryAssertions instance
     */
    public QueryAssertions query(String query, Object... params) {
        return new QueryAssertions(dbUtils, query, params);
    }

    // ========== Table Assertions Class ==========

    /**
     * Assertions for a specific table
     */
    @Slf4j
    public static class TableAssertions {
        private final DatabaseTestUtils dbUtils;
        private final String tableName;

        private TableAssertions(DatabaseTestUtils dbUtils, String tableName) {
            this.dbUtils = dbUtils;
            this.tableName = tableName;
        }

        /**
         * Assert table exists
         *
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' exists")
        public TableAssertions exists() {
            try {
                boolean exists = dbUtils.tableExists(tableName);
                log.info("Asserting table '{}' exists: {}", tableName, exists);

                Assertions.assertThat(exists)
                        .as("Table should exist: " + tableName)
                        .isTrue();
            } catch (SQLException e) {
                throw new AssertionError("Error checking if table exists: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table does not exist
         *
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' does not exist")
        public TableAssertions doesNotExist() {
            try {
                boolean exists = dbUtils.tableExists(tableName);
                log.info("Asserting table '{}' does not exist: {}", tableName, !exists);

                Assertions.assertThat(exists)
                        .as("Table should not exist: " + tableName)
                        .isFalse();
            } catch (SQLException e) {
                throw new AssertionError("Error checking if table exists: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table has specific row count
         *
         * @param expectedCount Expected row count
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' has {expectedCount} rows")
        public TableAssertions hasRowCount(long expectedCount) {
            try {
                long actualCount = dbUtils.countRows(tableName);
                log.info("Asserting table '{}' row count {} equals {}", tableName, actualCount, expectedCount);

                Assertions.assertThat(actualCount)
                        .as("Table '%s' should have %d rows", tableName, expectedCount)
                        .isEqualTo(expectedCount);
            } catch (SQLException e) {
                throw new AssertionError("Error counting rows in table: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table is empty
         *
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' is empty")
        public TableAssertions isEmpty() {
            return hasRowCount(0);
        }

        /**
         * Assert table is not empty
         *
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' is not empty")
        public TableAssertions isNotEmpty() {
            try {
                long count = dbUtils.countRows(tableName);
                log.info("Asserting table '{}' is not empty: count={}", tableName, count);

                Assertions.assertThat(count)
                        .as("Table '%s' should not be empty", tableName)
                        .isGreaterThan(0);
            } catch (SQLException e) {
                throw new AssertionError("Error counting rows in table: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table has at least specified number of rows
         *
         * @param minCount Minimum row count
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' has at least {minCount} rows")
        public TableAssertions hasAtLeast(long minCount) {
            try {
                long actualCount = dbUtils.countRows(tableName);
                log.info("Asserting table '{}' has at least {} rows: actual={}", tableName, minCount, actualCount);

                Assertions.assertThat(actualCount)
                        .as("Table '%s' should have at least %d rows", tableName, minCount)
                        .isGreaterThanOrEqualTo(minCount);
            } catch (SQLException e) {
                throw new AssertionError("Error counting rows in table: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table has record matching condition
         *
         * @param whereClause WHERE clause
         * @param params Parameters
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' has record WHERE {whereClause}")
        public TableAssertions hasRecord(String whereClause, Object... params) {
            try {
                boolean exists = dbUtils.recordExists(tableName, whereClause, params);
                log.info("Asserting table '{}' has record WHERE {}: {}", tableName, whereClause, exists);

                Assertions.assertThat(exists)
                        .as("Table '%s' should have record WHERE %s", tableName, whereClause)
                        .isTrue();
            } catch (SQLException e) {
                throw new AssertionError("Error checking record in table: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table does not have record matching condition
         *
         * @param whereClause WHERE clause
         * @param params Parameters
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' does not have record WHERE {whereClause}")
        public TableAssertions doesNotHaveRecord(String whereClause, Object... params) {
            try {
                boolean exists = dbUtils.recordExists(tableName, whereClause, params);
                log.info("Asserting table '{}' does not have record WHERE {}: {}", tableName, whereClause, !exists);

                Assertions.assertThat(exists)
                        .as("Table '%s' should not have record WHERE %s", tableName, whereClause)
                        .isFalse();
            } catch (SQLException e) {
                throw new AssertionError("Error checking record in table: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table has specific number of records matching condition
         *
         * @param whereClause WHERE clause
         * @param expectedCount Expected count
         * @param params Parameters
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' has {expectedCount} records WHERE {whereClause}")
        public TableAssertions hasRecordCount(String whereClause, long expectedCount, Object... params) {
            try {
                long actualCount = dbUtils.countRowsWhere(tableName, whereClause, params);
                log.info("Asserting table '{}' record count WHERE {}: actual={}, expected={}",
                        tableName, whereClause, actualCount, expectedCount);

                Assertions.assertThat(actualCount)
                        .as("Table '%s' should have %d records WHERE %s", tableName, expectedCount, whereClause)
                        .isEqualTo(expectedCount);
            } catch (SQLException e) {
                throw new AssertionError("Error counting records in table: " + tableName, e);
            }

            return this;
        }

        /**
         * Assert table has specific columns
         *
         * @param expectedColumns Expected column names
         * @return this for chaining
         */
        @Step("Assert table '{tableName}' has columns: {expectedColumns}")
        public TableAssertions hasColumns(String... expectedColumns) {
            try {
                List<String> actualColumns = dbUtils.getTableColumns(tableName);
                log.info("Asserting table '{}' has columns: {}", tableName, expectedColumns);

                Assertions.assertThat(actualColumns)
                        .as("Table '%s' should have expected columns", tableName)
                        .containsAll(List.of(expectedColumns));
            } catch (SQLException e) {
                throw new AssertionError("Error getting columns for table: " + tableName, e);
            }

            return this;
        }

        /**
         * Return to parent DatabaseAssertions for chaining
         *
         * @return DatabaseAssertions instance
         */
        public DatabaseAssertions and() {
            return new DatabaseAssertions(dbUtils);
        }
    }

    // ========== Query Assertions Class ==========

    /**
     * Assertions for query results
     */
    @Slf4j
    public static class QueryAssertions {
        private final DatabaseTestUtils dbUtils;
        private final String query;
        private final Object[] params;
        private List<Map<String, Object>> results;
        private Map<String, Object> singleRow;

        private QueryAssertions(DatabaseTestUtils dbUtils, String query, Object... params) {
            this.dbUtils = dbUtils;
            this.query = query;
            this.params = params;
        }

        /**
         * Execute query and get results
         *
         * @return Query results
         */
        private List<Map<String, Object>> getResults() {
            if (results == null) {
                try {
                    results = dbUtils.executeQuery(query, params);
                } catch (SQLException e) {
                    throw new AssertionError("Error executing query: " + query, e);
                }
            }
            return results;
        }

        /**
         * Get single row result
         *
         * @return Single row
         */
        private Map<String, Object> getSingleRow() {
            if (singleRow == null) {
                List<Map<String, Object>> rows = getResults();
                Assertions.assertThat(rows)
                        .as("Query should return at least one row")
                        .isNotEmpty();
                singleRow = rows.get(0);
            }
            return singleRow;
        }

        /**
         * Assert query returns exactly one row
         *
         * @return this for chaining
         */
        @Step("Assert query returns one row")
        public QueryAssertions returnsOneRow() {
            List<Map<String, Object>> rows = getResults();
            log.info("Asserting query returns one row: actual={}", rows.size());

            Assertions.assertThat(rows)
                    .as("Query should return exactly one row")
                    .hasSize(1);

            return this;
        }

        /**
         * Assert query returns no rows
         *
         * @return this for chaining
         */
        @Step("Assert query returns no rows")
        public QueryAssertions returnsNoRows() {
            List<Map<String, Object>> rows = getResults();
            log.info("Asserting query returns no rows: actual={}", rows.size());

            Assertions.assertThat(rows)
                    .as("Query should return no rows")
                    .isEmpty();

            return this;
        }

        /**
         * Assert query returns specific number of rows
         *
         * @param expectedCount Expected row count
         * @return this for chaining
         */
        @Step("Assert query returns {expectedCount} rows")
        public QueryAssertions returnsRowCount(int expectedCount) {
            List<Map<String, Object>> rows = getResults();
            log.info("Asserting query returns {} rows: actual={}", expectedCount, rows.size());

            Assertions.assertThat(rows)
                    .as("Query should return %d rows", expectedCount)
                    .hasSize(expectedCount);

            return this;
        }

        /**
         * Assert query returns at least specified number of rows
         *
         * @param minCount Minimum row count
         * @return this for chaining
         */
        @Step("Assert query returns at least {minCount} rows")
        public QueryAssertions returnsAtLeast(int minCount) {
            List<Map<String, Object>> rows = getResults();
            log.info("Asserting query returns at least {} rows: actual={}", minCount, rows.size());

            Assertions.assertThat(rows)
                    .as("Query should return at least %d rows", minCount)
                    .hasSizeGreaterThanOrEqualTo(minCount);

            return this;
        }

        /**
         * Assert column in first row equals expected value
         *
         * @param columnName Column name
         * @param expectedValue Expected value
         * @return this for chaining
         */
        @Step("Assert column '{columnName}' equals: {expectedValue}")
        public QueryAssertions columnEquals(String columnName, Object expectedValue) {
            Map<String, Object> row = getSingleRow();
            Object actualValue = row.get(columnName);

            log.info("Asserting column '{}' equals: expected={}, actual={}", columnName, expectedValue, actualValue);

            Assertions.assertThat(actualValue)
                    .as("Column '%s' should equal: %s", columnName, expectedValue)
                    .isEqualTo(expectedValue);

            return this;
        }

        /**
         * Assert column in first row is not null
         *
         * @param columnName Column name
         * @return this for chaining
         */
        @Step("Assert column '{columnName}' is not null")
        public QueryAssertions columnIsNotNull(String columnName) {
            Map<String, Object> row = getSingleRow();
            Object actualValue = row.get(columnName);

            log.info("Asserting column '{}' is not null: {}", columnName, actualValue != null);

            Assertions.assertThat(actualValue)
                    .as("Column '%s' should not be null", columnName)
                    .isNotNull();

            return this;
        }

        /**
         * Assert column in first row is null
         *
         * @param columnName Column name
         * @return this for chaining
         */
        @Step("Assert column '{columnName}' is null")
        public QueryAssertions columnIsNull(String columnName) {
            Map<String, Object> row = getSingleRow();
            Object actualValue = row.get(columnName);

            log.info("Asserting column '{}' is null: {}", columnName, actualValue == null);

            Assertions.assertThat(actualValue)
                    .as("Column '%s' should be null", columnName)
                    .isNull();

            return this;
        }

        /**
         * Assert column contains expected value (for strings)
         *
         * @param columnName Column name
         * @param expectedSubstring Expected substring
         * @return this for chaining
         */
        @Step("Assert column '{columnName}' contains: {expectedSubstring}")
        public QueryAssertions columnContains(String columnName, String expectedSubstring) {
            Map<String, Object> row = getSingleRow();
            String actualValue = (String) row.get(columnName);

            log.info("Asserting column '{}' contains '{}': actual='{}'", columnName, expectedSubstring, actualValue);

            Assertions.assertThat(actualValue)
                    .as("Column '%s' should contain: %s", columnName, expectedSubstring)
                    .contains(expectedSubstring);

            return this;
        }

        /**
         * Get query results for custom assertions
         *
         * @return List of result rows
         */
        public List<Map<String, Object>> getResultRows() {
            return getResults();
        }

        /**
         * Return to parent DatabaseAssertions for chaining
         *
         * @return DatabaseAssertions instance
         */
        public DatabaseAssertions and() {
            return new DatabaseAssertions(dbUtils);
        }
    }
}
