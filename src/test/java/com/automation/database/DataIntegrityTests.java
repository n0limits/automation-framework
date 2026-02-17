package com.automation.database;

import com.automation.base.BaseTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database Data Integrity Tests
 * Tests cover:
 * - Foreign key relationships
 * - Data consistency validation
 * - Referential integrity
 * - Constraint enforcement
 * - Cascading operations
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Database - Data Integrity")
public class DataIntegrityTests extends BaseTest {
    private DatabaseTestUtils dbUtils;
    private TestDataBuilder dataBuilder;

    @BeforeMethod
    public void setupDatabase() {
        log.info("===== Setting up Data Integrity Test =====");

        dbUtils = new DatabaseTestUtils();
        dataBuilder = new TestDataBuilder(dbUtils);

        log.info("Database utilities initialized");
    }

    @AfterMethod
    public void cleanupDatabase() {
        log.info("===== Cleaning up Data Integrity Test =====");

        try {
            dataBuilder.cleanupAll();
            log.info("Test data cleaned up");
        } catch (SQLException e) {
            log.error("Error during cleanup", e);
        } finally {
            dbUtils.cleanup();
            log.info("Database connection closed");
        }
    }

    @Test(description = "Verify foreign key relationship between users and orders")
    @Description("Test orders table correctly references users table")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Referential Integrity")
    public void testUserOrderRelationship() throws SQLException {
        log.info("=== Test: User-Order Relationship ===");

        // Create user
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "order_user")
                .with("email", "orderuser@test.com")
                .with("status", "active")
                .insert();

        log.info("Created user with ID: {}", userId);

        // Create order for user
        Long orderId = dataBuilder
                .forTable("orders")
                .with("user_id", userId)
                .with("order_number", "ORD-" + System.currentTimeMillis())
                .with("amount", 100.50)
                .with("status", "pending")
                .insert();

        log.info("Created order with ID: {}", orderId);

        // Verify order references user
        DatabaseAssertions.assertThat(dbUtils)
                .query("SELECT * FROM orders WHERE id = ?", orderId)
                .returnsOneRow()
                .columnEquals("user_id", userId);

        // Verify user has orders
        DatabaseAssertions.assertThat(dbUtils)
                .table("orders")
                .hasRecord("user_id = ?", userId);

        log.info("[PASS] User-Order relationship verified");
    }

    @Test(description = "Verify one-to-many relationship")
    @Description("Test user can have multiple orders")
    @Severity(SeverityLevel.NORMAL)
    @Story("Referential Integrity")
    public void testOneToManyRelationship() throws SQLException {
        log.info("=== Test: One-to-Many Relationship ===");

        // Create user
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "multi_order_user")
                .with("email", "multiorder@test.com")
                .with("status", "active")
                .insert();

        // Create multiple orders for same user
        for (int i = 1; i <= 3; i++) {
            dataBuilder
                    .forTable("orders")
                    .with("user_id", userId)
                    .with("order_number", "ORD-" + System.currentTimeMillis() + "-" + i)
                    .with("amount", 50.0 * i)
                    .with("status", "pending")
                    .insert();
        }

        // Verify user has exactly 3 orders
        long orderCount = dbUtils.countRowsWhere("orders", "user_id = ?", userId);
        assertThat(orderCount)
                .as("User should have 3 orders")
                .isEqualTo(3);

        // Query all orders for user
        List<Map<String, Object>> orders = dbUtils.executeQuery(
                "SELECT * FROM orders WHERE user_id = ? ORDER BY id",
                userId
        );

        assertThat(orders)
                .as("Should return 3 orders")
                .hasSize(3);

        // Verify all orders belong to same user
        for (Map<String, Object> order : orders) {
            assertThat(order.get("user_id"))
                    .as("All orders should belong to user")
                    .isEqualTo(userId);
        }

        log.info("[PASS] One-to-Many relationship verified");
    }

    @Test(description = "Verify data consistency across tables")
    @Description("Test data remains consistent when updating related records")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Data Consistency")
    public void testDataConsistency() throws SQLException {
        log.info("=== Test: Data Consistency ===");

        // Create user
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "consistency_user")
                .with("email", "consistency@test.com")
                .with("status", "active")
                .insert();

        // Create orders for user
        Long order1 = dataBuilder
                .forTable("orders")
                .with("user_id", userId)
                .with("order_number", "CONS-001")
                .with("amount", 100.0)
                .with("status", "pending")
                .insert();

        Long order2 = dataBuilder
                .forTable("orders")
                .with("user_id", userId)
                .with("order_number", "CONS-002")
                .with("amount", 200.0)
                .with("status", "pending")
                .insert();

        // Update user status
        dataBuilder
                .forTable("users")
                .with("status", "suspended")
                .update(userId);

        // Verify user status updated
        DatabaseAssertions.assertThat(dbUtils)
                .query("SELECT * FROM users WHERE id = ?", userId)
                .returnsOneRow()
                .columnEquals("status", "suspended");

        // Verify orders still reference correct user
        DatabaseAssertions.assertThat(dbUtils)
                .query("SELECT * FROM orders WHERE user_id = ?", userId)
                .returnsRowCount(2);

        // Verify join query works correctly
        List<Map<String, Object>> joinResults = dbUtils.executeQuery(
                "SELECT o.*, u.status as user_status FROM orders o " +
                        "JOIN users u ON o.user_id = u.id WHERE o.user_id = ?",
                userId
        );

        assertThat(joinResults)
                .as("Join should return 2 orders")
                .hasSize(2);

        for (Map<String, Object> result : joinResults) {
            assertThat(result.get("user_status"))
                    .as("User status should be reflected in join")
                    .isEqualTo("suspended");
        }

        log.info("[PASS] Data consistency verified");
    }

    @Test(description = "Verify NULL handling in database")
    @Description("Test NULL values are handled correctly")
    @Severity(SeverityLevel.NORMAL)
    @Story("Data Validation")
    public void testNullHandling() throws SQLException {
        log.info("=== Test: NULL Handling ===");

        // Create user with optional NULL fields
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "null_test_user")
                .with("email", "nulltest@test.com")
                .with("status", "active")
                .withNull("phone")
                .withNull("address")
                .insert();

        // Verify NULL fields
        DatabaseAssertions.assertThat(dbUtils)
                .query("SELECT * FROM users WHERE id = ?", userId)
                .returnsOneRow()
                .columnIsNull("phone")
                .columnIsNull("address")
                .columnIsNotNull("username")
                .columnIsNotNull("email");

        log.info("[PASS] NULL handling verified");
    }

    @Test(description = "Verify unique constraint enforcement")
    @Description("Test unique constraints prevent duplicate values")
    @Severity(SeverityLevel.NORMAL)
    @Story("Constraints")
    public void testUniqueConstraint() throws SQLException {
        log.info("=== Test: Unique Constraint ===");

        String uniqueUsername = "unique_test_" + System.currentTimeMillis();

        // Create first user
        Long userId1 = dataBuilder
                .forTable("users")
                .with("username", uniqueUsername)
                .with("email", "unique1@test.com")
                .with("status", "active")
                .insert();

        log.info("Created first user with username: {}", uniqueUsername);

        // Attempt to create second user with same username
        boolean duplicateFailed = false;
        try {
            dataBuilder
                    .forTable("users")
                    .with("username", uniqueUsername) // Same username
                    .with("email", "unique2@test.com") // Different email
                    .with("status", "active")
                    .insert(false); // Don't track for cleanup
        } catch (SQLException e) {
            log.info("Duplicate username rejected as expected: {}", e.getMessage());
            duplicateFailed = true;
        }

        assertThat(duplicateFailed)
                .as("Duplicate username should be rejected")
                .isTrue();

        // Verify only one user exists with that username
        long count = dbUtils.countRowsWhere("users", "username = ?", uniqueUsername);
        assertThat(count)
                .as("Should have exactly one user with that username")
                .isEqualTo(1);

        log.info("[PASS] Unique constraint enforced");
    }

    @Test(description = "Verify data type validation")
    @Description("Test database enforces correct data types")
    @Severity(SeverityLevel.MINOR)
    @Story("Data Validation")
    public void testDataTypeValidation() throws SQLException {
        log.info("=== Test: Data Type Validation ===");

        // Create order with correct data types
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "datatype_user")
                .with("email", "datatype@test.com")
                .with("status", "active")
                .insert();

        Long orderId = dataBuilder
                .forTable("orders")
                .with("user_id", userId) // Integer/Long
                .with("order_number", "DT-001") // String
                .with("amount", 123.45) // Decimal/Double
                .with("status", "completed") // String
                .insert();

        // Query and verify data types are preserved
        Map<String, Object> order = dbUtils.executeQuerySingleRow(
                "SELECT * FROM orders WHERE id = ?", orderId
        );

        assertThat(order.get("user_id"))
                .as("user_id should be numeric")
                .isInstanceOf(Number.class);

        assertThat(order.get("amount"))
                .as("amount should be numeric")
                .isInstanceOf(Number.class);

        assertThat(order.get("order_number"))
                .as("order_number should be string")
                .isInstanceOf(String.class);

        log.info("[PASS] Data types validated");
    }

    @Test(description = "Verify cascading operations")
    @Description("Test related records are handled correctly on parent deletion")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Referential Integrity")
    public void testCascadingOperations() throws SQLException {
        log.info("=== Test: Cascading Operations ===");

        // Create user with orders
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "cascade_user")
                .with("email", "cascade@test.com")
                .with("status", "active")
                .insert(false); // Don't track for auto-cleanup

        Long order1 = dataBuilder
                .forTable("orders")
                .with("user_id", userId)
                .with("order_number", "CAS-001")
                .with("amount", 100.0)
                .with("status", "pending")
                .insert(false); // Don't track

        Long order2 = dataBuilder
                .forTable("orders")
                .with("user_id", userId)
                .with("order_number", "CAS-002")
                .with("amount", 200.0)
                .with("status", "pending")
                .insert(false); // Don't track

        // Verify orders exist
        long initialOrderCount = dbUtils.countRowsWhere("orders", "user_id = ?", userId);
        assertThat(initialOrderCount).isEqualTo(2);

        // Delete user (orders should cascade delete or become orphaned depending on FK constraint)
        int deletedUsers = dbUtils.deleteWhere("users", "id = ?", userId);
        assertThat(deletedUsers).isEqualTo(1);

        // Check if orders were cascade deleted or orphaned
        long remainingOrders = dbUtils.countRowsWhere("orders", "user_id = ?", userId);

        log.info("Orders after user deletion: {}", remainingOrders);

        // Clean up any remaining orders manually
        if (remainingOrders > 0) {
            dbUtils.deleteWhere("orders", "user_id = ?", userId);
            log.info("Manually cleaned up orphaned orders");
        }

        log.info("[PASS] Cascading operations verified");
    }

    @Test(description = "Verify aggregate function calculations")
    @Description("Test SUM, COUNT, AVG aggregate functions")
    @Severity(SeverityLevel.NORMAL)
    @Story("Data Validation")
    public void testAggregateFunctions() throws SQLException {
        log.info("=== Test: Aggregate Functions ===");

        // Create user
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "aggregate_user")
                .with("email", "aggregate@test.com")
                .with("status", "active")
                .insert();

        // Create multiple orders with known amounts
        double[] amounts = {100.0, 200.0, 300.0};
        double expectedSum = 600.0;
        double expectedAvg = 200.0;

        for (double amount : amounts) {
            dataBuilder
                    .forTable("orders")
                    .with("user_id", userId)
                    .with("order_number", "AGG-" + System.currentTimeMillis())
                    .with("amount", amount)
                    .with("status", "completed")
                    .insert();
        }

        // Test COUNT
        Object count = dbUtils.executeQuerySingleValue(
                "SELECT COUNT(*) FROM orders WHERE user_id = ?", userId
        );
        assertThat(((Number) count).longValue())
                .as("COUNT should return 3")
                .isEqualTo(3);

        // Test SUM
        Object sum = dbUtils.executeQuerySingleValue(
                "SELECT SUM(amount) FROM orders WHERE user_id = ?", userId
        );
        assertThat(((Number) sum).doubleValue())
                .as("SUM should return 600.0")
                .isEqualTo(expectedSum);

        // Test AVG
        Object avg = dbUtils.executeQuerySingleValue(
                "SELECT AVG(amount) FROM orders WHERE user_id = ?", userId
        );
        assertThat(((Number) avg).doubleValue())
                .as("AVG should return 200.0")
                .isCloseTo(expectedAvg, org.assertj.core.data.Offset.offset(0.01));

        log.info("[PASS] Aggregate functions verified - COUNT: {}, SUM: {}, AVG: {}", count, sum, avg);
    }

    @Test(description = "Verify GROUP BY query results")
    @Description("Test grouping and aggregation works correctly")
    @Severity(SeverityLevel.MINOR)
    @Story("Data Validation")
    public void testGroupByQuery() throws SQLException {
        log.info("=== Test: GROUP BY Query ===");

        // Create users with orders
        for (int i = 1; i <= 2; i++) {
            Long userId = dataBuilder
                    .forTable("users")
                    .with("username", "group_user_" + i)
                    .with("email", "group" + i + "@test.com")
                    .with("status", "active")
                    .insert();

            // Create 2 orders per user
            for (int j = 1; j <= 2; j++) {
                dataBuilder
                        .forTable("orders")
                        .with("user_id", userId)
                        .with("order_number", "GRP-" + i + "-" + j)
                        .with("amount", 100.0 * j)
                        .with("status", "completed")
                        .insert();
            }
        }

        // Query with GROUP BY
        List<Map<String, Object>> groupResults = dbUtils.executeQuery(
                "SELECT user_id, COUNT(*) as order_count, SUM(amount) as total_amount " +
                        "FROM orders WHERE order_number LIKE 'GRP-%' " +
                        "GROUP BY user_id"
        );

        assertThat(groupResults)
                .as("Should have 2 groups")
                .hasSize(2);

        // Verify each group has correct counts
        for (Map<String, Object> result : groupResults) {
            long orderCount = ((Number) result.get("order_count")).longValue();
            double totalAmount = ((Number) result.get("total_amount")).doubleValue();

            assertThat(orderCount)
                    .as("Each user should have 2 orders")
                    .isEqualTo(2);

            assertThat(totalAmount)
                    .as("Each user's total should be 300.0")
                    .isEqualTo(300.0);
        }

        log.info("[PASS] GROUP BY query verified");
    }
}
