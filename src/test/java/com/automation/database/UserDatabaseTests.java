package com.automation.database;

import com.automation.base.BaseTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database Tests for User Management
 * Tests cover:
 * - User CRUD operations
 * - Data integrity validation
 * - Query result verification
 * - Transaction handling
 * - Relationship testing
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Database - User Management")
public class UserDatabaseTests extends BaseTest {
    private DatabaseTestUtils dbUtils;
    private TestDataBuilder dataBuilder;

    @BeforeMethod
    public void setupDatabase() {
        log.info("===== Setting up Database Test =====");

        // Initialize database utilities
        dbUtils = new DatabaseTestUtils();
        dataBuilder = new TestDataBuilder(dbUtils);

        log.info("Database utilities initialized");
    }

    @AfterMethod
    public void cleanupDatabase() {
        log.info("===== Cleaning up Database Test =====");

        try {
            // Cleanup all test data
            dataBuilder.cleanupAll();
            log.info("Test data cleaned up");
        } catch (SQLException e) {
            log.error("Error during cleanup", e);
        } finally {
            // Close database connection
            dbUtils.cleanup();
            log.info("Database connection closed");
        }
    }

    @Test(description = "Verify user table exists")
    @Description("Test that user table exists in database")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Database Schema")
    public void testUserTableExists() throws SQLException {
        log.info("=== Test: User Table Exists ===");

        // Assert table exists using fluent API
        DatabaseAssertions.assertThat(dbUtils)
                .table("users")
                .exists();

        log.info("[PASS] User table exists");
    }

    @Test(description = "Verify user table has expected columns")
    @Description("Test user table schema includes required columns")
    @Severity(SeverityLevel.NORMAL)
    @Story("Database Schema")
    public void testUserTableSchema() throws SQLException {
        log.info("=== Test: User Table Schema ===");

        // Get table columns
        List<String> columns = dbUtils.getTableColumns("users");

        log.info("User table columns: {}", columns);

        // Verify required columns exist
        assertThat(columns)
                .as("User table should have required columns")
                .contains("id", "username", "email", "status");

        log.info("[PASS] User table schema is valid");
    }

    @Test(description = "Verify user creation")
    @Description("Test creating new user record in database")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User CRUD")
    public void testCreateUser() throws SQLException {
        log.info("=== Test: Create User ===");

        // Create user using fluent builder
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "testuser_" + System.currentTimeMillis())
                .with("email", "test@example.com")
                .with("password_hash", "hashed_password")
                .with("status", "active")
                .withCurrentTimestamp("created_at")
                .insert();

        log.info("Created user with ID: {}", userId);

        // Verify user was created
        assertThat(userId)
                .as("User ID should be generated")
                .isNotNull()
                .isPositive();

        // Verify user exists in database
        DatabaseAssertions.assertThat(dbUtils)
                .table("users")
                .hasRecord("id = ?", userId);

        log.info("[PASS] User created successfully");
    }

    @Test(description = "Verify user retrieval")
    @Description("Test querying user record from database")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User CRUD")
    public void testRetrieveUser() throws SQLException {
        log.info("=== Test: Retrieve User ===");

        // Create test user
        String username = "retrieve_test_" + System.currentTimeMillis();
        String email = "retrieve@test.com";

        Long userId = dataBuilder
                .forTable("users")
                .with("username", username)
                .with("email", email)
                .with("status", "active")
                .withCurrentTimestamp("created_at")
                .insert();

        // Retrieve user from database
        Map<String, Object> user = dbUtils.executeQuerySingleRow(
                "SELECT * FROM users WHERE id = ?", userId
        );

        log.info("Retrieved user: {}", user);

        // Verify user data
        assertThat(user)
                .as("User should be found")
                .isNotNull();

        assertThat(user.get("username"))
                .as("Username should match")
                .isEqualTo(username);

        assertThat(user.get("email"))
                .as("Email should match")
                .isEqualTo(email);

        // Using fluent assertions
        DatabaseAssertions.assertThat(dbUtils)
                .query("SELECT * FROM users WHERE id = ?", userId)
                .returnsOneRow()
                .columnEquals("username", username)
                .columnEquals("email", email)
                .columnEquals("status", "active");

        log.info("[PASS] User retrieved successfully");
    }

    @Test(description = "Verify user update")
    @Description("Test updating user record in database")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User CRUD")
    public void testUpdateUser() throws SQLException {
        log.info("=== Test: Update User ===");

        // Create test user
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "update_test")
                .with("email", "before@test.com")
                .with("status", "pending")
                .withCurrentTimestamp("created_at")
                .insert();

        // Update user
        int affectedRows = dataBuilder
                .forTable("users")
                .with("email", "after@test.com")
                .with("status", "active")
                .withCurrentTimestamp("updated_at")
                .update(userId);

        assertThat(affectedRows)
                .as("Update should affect one row")
                .isEqualTo(1);

        // Verify updates
        DatabaseAssertions.assertThat(dbUtils)
                .query("SELECT * FROM users WHERE id = ?", userId)
                .returnsOneRow()
                .columnEquals("email", "after@test.com")
                .columnEquals("status", "active")
                .columnIsNotNull("updated_at");

        log.info("[PASS] User updated successfully");
    }

    @Test(description = "Verify user deletion")
    @Description("Test deleting user record from database")
    @Severity(SeverityLevel.NORMAL)
    @Story("User CRUD")
    public void testDeleteUser() throws SQLException {
        log.info("=== Test: Delete User ===");

        // Create test user (don't track for cleanup)
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "delete_test")
                .with("email", "delete@test.com")
                .with("status", "active")
                .insert(false); // Don't track for cleanup

        // Verify user exists
        DatabaseAssertions.assertThat(dbUtils)
                .table("users")
                .hasRecord("id = ?", userId);

        // Delete user
        int deletedRows = dbUtils.deleteWhere("users", "id = ?", userId);

        assertThat(deletedRows)
                .as("Delete should affect one row")
                .isEqualTo(1);

        // Verify user no longer exists
        DatabaseAssertions.assertThat(dbUtils)
                .table("users")
                .doesNotHaveRecord("id = ?", userId);

        log.info("[PASS] User deleted successfully");
    }

    @Test(description = "Verify multiple users creation")
    @Description("Test creating multiple user records in batch")
    @Severity(SeverityLevel.NORMAL)
    @Story("User CRUD")
    public void testCreateMultipleUsers() throws SQLException {
        log.info("=== Test: Create Multiple Users ===");

        // Prepare multiple user records
        List<Map<String, Object>> users = List.of(
                Map.of("username", "batch_user_1", "email", "batch1@test.com", "status", "active"),
                Map.of("username", "batch_user_2", "email", "batch2@test.com", "status", "active"),
                Map.of("username", "batch_user_3", "email", "batch3@test.com", "status", "pending")
        );

        // Get initial count
        long initialCount = dbUtils.countRows("users");

        // Insert batch
        dataBuilder.forTable("users");
        List<Long> userIds = dataBuilder.insertBatch(users);

        log.info("Created {} users: {}", userIds.size(), userIds);

        // Verify all users created
        assertThat(userIds)
                .as("All users should be created")
                .hasSize(3)
                .allMatch(id -> id != null && id > 0);

        // Verify count increased
        long newCount = dbUtils.countRows("users");
        assertThat(newCount)
                .as("User count should increase by 3")
                .isEqualTo(initialCount + 3);

        log.info("[PASS] Multiple users created successfully");
    }

    @Test(description = "Verify user count by status")
    @Description("Test counting users by different status values")
    @Severity(SeverityLevel.MINOR)
    @Story("User Queries")
    public void testCountUsersByStatus() throws SQLException {
        log.info("=== Test: Count Users By Status ===");

        // Create users with different statuses
        dataBuilder.forTable("users");
        dataBuilder.with("username", "active_user_1").with("email", "a1@test.com").with("status", "active").insert();
        dataBuilder.forTable("users");
        dataBuilder.with("username", "active_user_2").with("email", "a2@test.com").with("status", "active").insert();
        dataBuilder.forTable("users");
        dataBuilder.with("username", "pending_user_1").with("email", "p1@test.com").with("status", "pending").insert();

        // Count active users
        long activeCount = dbUtils.countRowsWhere("users", "status = ?", "active");
        assertThat(activeCount)
                .as("Should have at least 2 active users")
                .isGreaterThanOrEqualTo(2);

        // Count pending users
        long pendingCount = dbUtils.countRowsWhere("users", "status = ?", "pending");
        assertThat(pendingCount)
                .as("Should have at least 1 pending user")
                .isGreaterThanOrEqualTo(1);

        log.info("Active users: {}, Pending users: {}", activeCount, pendingCount);
        log.info("[PASS] User count by status verified");
    }

    @Test(description = "Verify user query with multiple conditions")
    @Description("Test querying users with complex WHERE conditions")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Queries")
    public void testQueryUsersWithConditions() throws SQLException {
        log.info("=== Test: Query Users With Conditions ===");

        // Create test users
        dataBuilder.forTable("users");
        dataBuilder.with("username", "query_test_active").with("email", "qa@test.com").with("status", "active").insert();
        dataBuilder.forTable("users");
        dataBuilder.with("username", "query_test_inactive").with("email", "qi@test.com").with("status", "inactive").insert();

        // Query active users
        List<Map<String, Object>> activeUsers = dbUtils.executeQuery(
                "SELECT * FROM users WHERE status = ? AND username LIKE ?",
                "active", "query_test%"
        );

        assertThat(activeUsers)
                .as("Should find active users")
                .isNotEmpty();

        // Verify all results are active
        for (Map<String, Object> user : activeUsers) {
            assertThat(user.get("status"))
                    .as("All returned users should be active")
                    .isEqualTo("active");
        }

        log.info("Found {} active users matching criteria", activeUsers.size());
        log.info("[PASS] Complex query executed successfully");
    }

    @Test(description = "Verify transaction rollback")
    @Description("Test transaction is rolled back on error")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Transactions")
    public void testTransactionRollback() throws SQLException {
        log.info("=== Test: Transaction Rollback ===");

        long initialCount = dbUtils.countRows("users");

        try {
            dbUtils.executeInTransaction(db -> {
                // Insert user within transaction
                dataBuilder.forTable("users");
                Long userId = dataBuilder
                        .with("username", "rollback_test")
                        .with("email", "rollback@test.com")
                        .with("status", "active")
                        .insert(false); // Don't track

                log.info("Inserted user in transaction: {}", userId);

                // Simulate error to trigger rollback
                throw new RuntimeException("Simulated error for rollback");
            });
        } catch (Exception e) {
            log.info("Transaction failed as expected: {}", e.getMessage());
        }

        // Verify count unchanged (rollback successful)
        long finalCount = dbUtils.countRows("users");
        assertThat(finalCount)
                .as("User count should be unchanged after rollback")
                .isEqualTo(initialCount);

        // Verify user doesn't exist
        DatabaseAssertions.assertThat(dbUtils)
                .table("users")
                .doesNotHaveRecord("username = ?", "rollback_test");

        log.info("[PASS] Transaction rollback verified");
    }

    @Test(description = "Verify transaction commit")
    @Description("Test transaction is committed successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Transactions")
    public void testTransactionCommit() throws Exception {
        log.info("=== Test: Transaction Commit ===");

        long initialCount = dbUtils.countRows("users");

        // Execute successful transaction
        dbUtils.executeInTransaction(db -> {
            // Insert users within transaction
            dataBuilder.forTable("users");
            dataBuilder.with("username", "commit_test_1").with("email", "c1@test.com").with("status", "active").insert();

            dataBuilder.forTable("users");
            dataBuilder.with("username", "commit_test_2").with("email", "c2@test.com").with("status", "active").insert();

            log.info("Inserted 2 users in transaction");
        });

        // Verify count increased
        long finalCount = dbUtils.countRows("users");
        assertThat(finalCount)
                .as("User count should increase by 2")
                .isEqualTo(initialCount + 2);

        // Verify users exist
        DatabaseAssertions.assertThat(dbUtils)
                .table("users")
                .hasRecord("username = ?", "commit_test_1")
                .and()
                .table("users")
                .hasRecord("username = ?", "commit_test_2");

        log.info("[PASS] Transaction commit verified");
    }

    @Test(description = "Verify default values in test data builder")
    @Description("Test TestDataBuilder applies default values correctly")
    @Severity(SeverityLevel.MINOR)
    @Story("Test Data Management")
    public void testTestDataBuilderDefaults() throws SQLException {
        log.info("=== Test: Test Data Builder Defaults ===");

        // Set default values
        dataBuilder.setDefault("status", "active");
        dataBuilder.setDefault("role", "user");

        // Create user without specifying defaults
        Long userId = dataBuilder
                .forTable("users")
                .with("username", "defaults_test")
                .with("email", "defaults@test.com")
                .insert();

        // Verify defaults were applied
        DatabaseAssertions.assertThat(dbUtils)
                .query("SELECT * FROM users WHERE id = ?", userId)
                .returnsOneRow()
                .columnEquals("status", "active");

        log.info("[PASS] Default values applied correctly");
    }

    @Test(description = "Verify test data cleanup tracking")
    @Description("Test TestDataBuilder tracks records for cleanup")
    @Severity(SeverityLevel.MINOR)
    @Story("Test Data Management")
    public void testTestDataCleanupTracking() throws SQLException {
        log.info("=== Test: Test Data Cleanup Tracking ===");

        int initialTrackedCount = dataBuilder.getTrackedRecordCount();

        // Create multiple users
        dataBuilder.forTable("users");
        dataBuilder.with("username", "tracked_1").with("email", "t1@test.com").insert();

        dataBuilder.forTable("users");
        dataBuilder.with("username", "tracked_2").with("email", "t2@test.com").insert();

        int newTrackedCount = dataBuilder.getTrackedRecordCount();

        assertThat(newTrackedCount)
                .as("Should track 2 new records")
                .isEqualTo(initialTrackedCount + 2);

        log.info("Tracked records: {}", dataBuilder.getTrackedRecords());
        log.info("[PASS] Cleanup tracking verified");
    }
}
