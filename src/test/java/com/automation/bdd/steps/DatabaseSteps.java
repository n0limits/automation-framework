package com.automation.bdd.steps;

import com.automation.bdd.context.ScenarioContext;
import com.automation.database.DatabaseAssertions;
import com.automation.database.DatabaseTestUtils;
import com.automation.database.TestDataBuilder;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DatabaseSteps {

    private static final AtomicLong COUNTER = new AtomicLong(System.currentTimeMillis() % 100_000);

    private final ScenarioContext scenarioContext;

    public DatabaseSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    /**
     * Append a unique suffix to prevent collisions during parallel execution.
     * Uses threadId + counter to guarantee uniqueness across threads and scenarios.
     */
    private String uniquify(String baseName) {
        long suffix = COUNTER.incrementAndGet();
        String unique = baseName + "_" + Thread.currentThread().getId() + "_" + suffix;
        log.debug("Uniquified '{}' → '{}'", baseName, unique);
        return unique;
    }

    private DatabaseTestUtils getDbUtils() {
        return scenarioContext.get("dbUtils", DatabaseTestUtils.class);
    }

    private TestDataBuilder getDataBuilder() {
        return scenarioContext.get("dataBuilder", TestDataBuilder.class);
    }

    // ===== Given =====

    @Given("the database connection is established")
    public void theDatabaseConnectionIsEstablished() throws SQLException {
        getDbUtils().getConnection();
        assertThat(getDbUtils().isConnectionValid())
                .as("Database connection should be valid")
                .isTrue();
        log.info("Database connection established and validated");
    }

    @Given("a user exists with username {string} and email {string}")
    public void aUserExistsWithUsernameAndEmail(String username, String email) throws SQLException {
        String actualUsername = uniquify(username);
        String actualEmail = uniquify(email);
        Long userId = getDataBuilder()
                .forTable("users")
                .with("username", actualUsername)
                .with("email", actualEmail)
                .with("status", "active")
                .insert();
        scenarioContext.set("lastUserId", userId);
        scenarioContext.set("lastUsername", actualUsername);
        scenarioContext.set("lastEmail", actualEmail);
        log.info("Created user '{}' with ID: {}", actualUsername, userId);
    }

    @Given("a user exists with username {string} email {string} and status {string}")
    public void aUserExistsWithUsernameEmailAndStatus(String username, String email, String status) throws SQLException {
        String actualUsername = uniquify(username);
        String actualEmail = uniquify(email);
        Long userId = getDataBuilder()
                .forTable("users")
                .with("username", actualUsername)
                .with("email", actualEmail)
                .with("status", status)
                .insert();
        scenarioContext.set("lastUserId", userId);
        scenarioContext.set("lastUsername", actualUsername);
        scenarioContext.set("lastEmail", actualEmail);
        log.info("Created user '{}' (status: {}) with ID: {}", actualUsername, status, userId);
    }

    // ===== When =====

    private static final List<String> ALLOWED_TABLES = List.of("users", "orders");

    @When("I query the {string} table")
    public void iQueryTheTable(String tableName) throws SQLException {
        if (!ALLOWED_TABLES.contains(tableName.toLowerCase())) {
            throw new IllegalArgumentException("Table name not in allowlist: " + tableName);
        }
        List<Map<String, Object>> results = getDbUtils().executeQuery(
                String.format("SELECT * FROM %s", tableName));
        scenarioContext.set("queryResults", results);
        log.info("Queried table '{}': {} rows", tableName, results.size());
    }

    @When("I query the user by ID")
    public void iQueryTheUserById() throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        Map<String, Object> user = getDbUtils().executeQuerySingleRow(
                "SELECT * FROM users WHERE id = ?", userId);
        scenarioContext.set("queriedUser", user);
        log.info("Queried user by ID: {}", userId);
    }

    @When("I create a user with username {string} and email {string}")
    public void iCreateAUserWithUsernameAndEmail(String username, String email) throws SQLException {
        String actualUsername = uniquify(username);
        String actualEmail = uniquify(email);
        Long userId = getDataBuilder()
                .forTable("users")
                .with("username", actualUsername)
                .with("email", actualEmail)
                .with("status", "active")
                .withCurrentTimestamp("created_at")
                .insert();
        scenarioContext.set("lastUserId", userId);
        scenarioContext.set("lastUsername", actualUsername);
        scenarioContext.set("lastEmail", actualEmail);
        log.info("Created user '{}' with ID: {}", actualUsername, userId);
    }

    @When("I update the user status to {string}")
    public void iUpdateTheUserStatusTo(String status) throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        int affected = getDataBuilder()
                .forTable("users")
                .with("status", status)
                .update(userId);
        scenarioContext.set("affectedRows", affected);
        log.info("Updated user {} status to '{}', affected {} rows", userId, status, affected);
    }

    @When("I delete the user by ID")
    public void iDeleteTheUserById() throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        int deleted = getDbUtils().deleteWhere("users", "id = ?", userId);
        scenarioContext.set("deletedRows", deleted);
        log.info("Deleted user {}: {} rows affected", userId, deleted);
    }

    @When("I create an order for the user with order number {string} and amount {double}")
    public void iCreateAnOrderForTheUser(String orderNumber, double amount) throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        String actualOrderNumber = uniquify(orderNumber);
        Long orderId = getDataBuilder()
                .forTable("orders")
                .with("user_id", userId)
                .with("order_number", actualOrderNumber)
                .with("amount", amount)
                .with("status", "pending")
                .insert();
        scenarioContext.set("lastOrderId", orderId);
        log.info("Created order '{}' (amount: {}) with ID: {}", actualOrderNumber, amount, orderId);
    }

    @When("I create {int} orders for the user with amounts:")
    public void iCreateOrdersForTheUser(int count, List<Map<String, String>> orderData) throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        for (Map<String, String> row : orderData) {
            getDataBuilder()
                    .forTable("orders")
                    .with("user_id", userId)
                    .with("order_number", uniquify(row.get("order_number")))
                    .with("amount", Double.parseDouble(row.get("amount")))
                    .with("status", row.getOrDefault("status", "pending"))
                    .insert();
        }
        log.info("Created {} orders for user {}", count, userId);
    }

    // ===== Then =====

    @Then("the {string} table exists")
    public void theTableExists(String tableName) throws SQLException {
        DatabaseAssertions.assertThat(getDbUtils())
                .table(tableName)
                .exists();
        log.info("Table '{}' exists", tableName);
    }

    @Then("the {string} table has columns:")
    public void theTableHasColumns(String tableName, List<String> expectedColumns) throws SQLException {
        List<String> actualColumns = getDbUtils().getTableColumns(tableName);
        assertThat(actualColumns)
                .as("Table '%s' should have required columns", tableName)
                .containsAll(expectedColumns);
    }

    @Then("the user ID is generated")
    public void theUserIdIsGenerated() {
        Long userId = scenarioContext.get("lastUserId");
        assertThat(userId)
                .as("User ID should be generated")
                .isNotNull()
                .isPositive();
    }

    @Then("the user exists in the database")
    public void theUserExistsInTheDatabase() throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        DatabaseAssertions.assertThat(getDbUtils())
                .table("users")
                .hasRecord("id = ?", userId);
    }

    @Then("the user does not exist in the database")
    public void theUserDoesNotExistInTheDatabase() throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        DatabaseAssertions.assertThat(getDbUtils())
                .table("users")
                .doesNotHaveRecord("id = ?", userId);
    }

    @Then("the queried user has username {string}")
    public void theQueriedUserHasUsername(String expectedUsername) {
        Map<String, Object> user = scenarioContext.get("queriedUser");
        assertThat(user).isNotNull();
        // Compare against the actual stored username (which has a unique suffix)
        String actualUsername = scenarioContext.get("lastUsername");
        assertThat(user.get("username"))
                .as("Username should match (base: '%s')", expectedUsername)
                .isEqualTo(actualUsername);
    }

    @Then("the queried user has email {string}")
    public void theQueriedUserHasEmail(String expectedEmail) {
        Map<String, Object> user = scenarioContext.get("queriedUser");
        assertThat(user).isNotNull();
        // Compare against the actual stored email (which has a unique suffix)
        String actualEmail = scenarioContext.get("lastEmail");
        assertThat(user.get("email"))
                .as("Email should match (base: '%s')", expectedEmail)
                .isEqualTo(actualEmail);
    }

    @Then("the queried user has status {string}")
    public void theQueriedUserHasStatus(String expectedStatus) {
        Map<String, Object> user = scenarioContext.get("queriedUser");
        assertThat(user).isNotNull();
        assertThat(user.get("status"))
                .as("Status should match")
                .isEqualTo(expectedStatus);
    }

    @Then("{int} row(s) was/were affected")
    public void rowsWereAffected(int expectedRows) {
        int affected = scenarioContext.get("affectedRows");
        assertThat(affected)
                .as("Affected rows should match")
                .isEqualTo(expectedRows);
    }

    @Then("{int} row(s) was/were deleted")
    public void rowsWereDeleted(int expectedRows) {
        int deleted = scenarioContext.get("deletedRows");
        assertThat(deleted)
                .as("Deleted rows should match")
                .isEqualTo(expectedRows);
    }

    @Then("the user has {int} order(s)")
    public void theUserHasOrders(int expectedCount) throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        long count = getDbUtils().countRowsWhere("orders", "user_id = ?", userId);
        assertThat(count)
                .as("User should have %d orders", expectedCount)
                .isEqualTo(expectedCount);
    }

    @Then("the order references the user")
    public void theOrderReferencesTheUser() throws SQLException {
        Long orderId = scenarioContext.get("lastOrderId");
        Long userId = scenarioContext.get("lastUserId");
        DatabaseAssertions.assertThat(getDbUtils())
                .query("SELECT * FROM orders WHERE id = ?", orderId)
                .returnsOneRow()
                .columnEquals("user_id", userId);
    }

    @Then("the total order amount for the user is {double}")
    public void theTotalOrderAmountIs(double expectedTotal) throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        Object sum = getDbUtils().executeQuerySingleValue(
                "SELECT SUM(amount) FROM orders WHERE user_id = ?", userId);
        assertThat(((Number) sum).doubleValue())
                .as("Total order amount should match")
                .isEqualTo(expectedTotal);
    }

    @Then("the average order amount for the user is {double}")
    public void theAverageOrderAmountIs(double expectedAvg) throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        Object avg = getDbUtils().executeQuerySingleValue(
                "SELECT AVG(amount) FROM orders WHERE user_id = ?", userId);
        assertThat(((Number) avg).doubleValue())
                .as("Average order amount should match")
                .isCloseTo(expectedAvg, org.assertj.core.data.Offset.offset(0.01));
    }

    @Then("there are at least {int} users with status {string}")
    public void thereAreAtLeastUsersWithStatus(int minCount, String status) throws SQLException {
        long count = getDbUtils().countRowsWhere("users", "status = ?", status);
        assertThat(count)
                .as("Should have at least %d users with status '%s'", minCount, status)
                .isGreaterThanOrEqualTo(minCount);
    }

    @Then("the user status in the database is {string}")
    public void theUserStatusInTheDatabaseIs(String expectedStatus) throws SQLException {
        Long userId = scenarioContext.get("lastUserId");
        DatabaseAssertions.assertThat(getDbUtils())
                .query("SELECT * FROM users WHERE id = ?", userId)
                .returnsOneRow()
                .columnEquals("status", expectedStatus);
    }

    @Then("a duplicate username {string} is rejected")
    public void aDuplicateUsernameIsRejected(String username) {
        // Use the actual (uniquified) username that was inserted in the Given step
        String actualUsername = scenarioContext.get("lastUsername");
        boolean duplicateFailed = false;
        try {
            getDataBuilder()
                    .forTable("users")
                    .with("username", actualUsername)
                    .with("email", uniquify("duplicate@test.com"))
                    .with("status", "active")
                    .insert(false);
        } catch (SQLException e) {
            duplicateFailed = true;
            log.info("Duplicate username '{}' rejected as expected: {}", actualUsername, e.getMessage());
        }
        assertThat(duplicateFailed)
                .as("Duplicate username '%s' should be rejected", actualUsername)
                .isTrue();
    }
}
