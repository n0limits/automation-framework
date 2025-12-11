# Core Automation Framework - Quick Start Guide

## Getting Started in 5 Minutes

### Prerequisites
- Java 21 installed
- Maven installed
- IDE (IntelliJ IDEA / Eclipse / VS Code)

---

## Quick Commands Reference

### Build & Compile
```bash
# Clean and compile everything
mvn clean compile test-compile

# Just compile without cleaning
mvn compile test-compile

# Install dependencies
mvn clean install
```

### Install Playwright Browsers
```bash
# Required for web UI testing
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Run Tests
```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=YourTestClass

# Run with specific TestNG suite
mvn clean test -DsuiteXmlFile=testng.xml

# Run with TestNG groups
mvn clean test -Dgroups=smoke

# Run in parallel
mvn clean test -DthreadCount=4

# Run with specific browser
mvn clean test -Dbrowser=firefox

# Run in headless mode
mvn clean test -Dheadless=true
```

### BDD/Cucumber Tests
```bash
# Run Cucumber tests (if you have implemented them)
mvn clean test -Dtest=YourTestRunner

# Run with tags
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@UI and @Positive"
mvn clean test -Dcucumber.filter.tags="@API and not @Negative"
```

### Generate Reports
```bash
# Generate and open Allure report
mvn allure:serve

# Generate static Allure report
mvn allure:report

# View report at: target/allure-report/index.html
```

---

## Project Structure Overview

```
core-automation-framework/
├── src/main/java/com/automation/
│   ├── api/              # API testing infrastructure
│   │   ├── APIClient.java
│   │   ├── BaseAPI.java
│   │   └── validators/ResponseValidator.java
│   ├── base/             # Base test classes
│   │   ├── BaseTest.java
│   │   ├── BaseWebTest.java
│   │   └── BaseAPITest.java
│   ├── config/           # Configuration management
│   │   ├── ConfigReader.java
│   │   └── TestConfig.java
│   ├── db/               # Database layer
│   │   ├── DatabaseConnection.java
│   │   ├── DatabaseConnectionFactory.java
│   │   ├── DatabaseType.java
│   │   ├── MongoDBConnection.java
│   │   ├── SQLConnection.java
│   │   └── QueryBuilder.java
│   ├── factory/          # Factory patterns
│   │   ├── BrowserFactory.java
│   │   └── PageFactory.java
│   ├── listeners/        # TestNG listeners
│   │   ├── TestListener.java
│   │   └── RetryAnalyzer.java
│   ├── pages/            # Page Object base class
│   │   └── BasePage.java
│   └── utils/            # Utilities
│       ├── PlaywrightManager.java
│       ├── WaitUtils.java
│       ├── FileUtils.java
│       └── TestDataGenerator.java
│
├── src/test/java/        # Your test classes go here
│   └── com/automation/   # Create your test packages here
│
├── src/test/resources/
│   ├── features/         # BDD feature files (create if using Cucumber)
│   ├── testdata/         # Test data files
│   └── testng/
│       ├── testng.xml
│       └── testng-bdd.xml
│
└── src/main/resources/
    ├── config.properties # Main configuration
    └── logback.xml       # Logging configuration
```

---

## Configuration

### config.properties

Main configuration file location: `src/main/resources/config.properties`

```properties
# Web UI Configuration
base.url=https://your-app-url.com
browser=chromium           # chromium, firefox, webkit
headless=false
timeout=30000
screenshot.on.failure=true

# API Configuration
api.base.url=https://api.your-app.com
api.timeout=30000

# Database Configuration
mongo.connection.string=mongodb://localhost:27017
mongo.database=testdb

sql.connection.string=jdbc:mysql://localhost:3306/testdb
sql.username=root
sql.password=password

postgres.connection.string=jdbc:postgresql://localhost:5432/testdb
postgres.username=postgres
postgres.password=password

# Test Data
test.data.path=src/test/resources/testdata

# Reporting
report.path=target/reports
```

---

## Creating Your First Test

### Step 1: Create a Page Object

Create file: `src/main/java/com/automation/pages/HomePage.java`

```java
package com.automation.pages;

public class HomePage extends BasePage {
    private final String searchInput = "#search";
    private final String searchButton = "button[type='submit']";
    private final String logo = ".logo";

    public HomePage() {
        super();
    }

    public HomePage searchFor(String term) {
        fill(searchInput, term);
        click(searchButton);
        return this;
    }

    public boolean isLogoDisplayed() {
        waitForSelector(logo);
        return true;
    }

    public String getPageTitle() {
        return getTitle();
    }
}
```

### Step 2: Create a Test Class

Create file: `src/test/java/com/automation/webui/HomePageTests.java`

```java
package com.automation.webui;

import com.automation.base.BaseWebTest;
import com.automation.pages.HomePage;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class HomePageTests extends BaseWebTest {

    @Test(description = "Verify home page loads successfully")
    public void testHomePageLoad() {
        HomePage homePage = new HomePage();

        assertThat(homePage.isLogoDisplayed())
                .as("Logo should be displayed")
                .isTrue();

        assertThat(homePage.getPageTitle())
                .as("Page title should contain app name")
                .containsIgnoringCase("your app");
    }

    @Test(description = "Verify search functionality")
    public void testSearch() {
        HomePage homePage = new HomePage();
        homePage.searchFor("test query");

        assertThat(homePage.getPageTitle())
                .containsIgnoringCase("search");
    }
}
```

### Step 3: Run Your Test

```bash
mvn clean test -Dtest=HomePageTests
```

---

## Creating API Tests

### Step 1: Create API Endpoint Class

Create file: `src/main/java/com/automation/api/endpoints/UserEndpoints.java`

```java
package com.automation.api.endpoints;

import com.automation.api.BaseAPI;
import io.restassured.response.Response;

public class UserEndpoints extends BaseAPI {
    private static final String USERS_PATH = "/users";

    public Response getAllUsers() {
        return get(USERS_PATH);
    }

    public Response getUserById(int userId) {
        return get(USERS_PATH + "/" + userId);
    }

    public Response createUser(Object userData) {
        return post(USERS_PATH, userData);
    }

    public Response updateUser(int userId, Object userData) {
        return put(USERS_PATH + "/" + userId, userData);
    }

    public Response deleteUser(int userId) {
        return delete(USERS_PATH + "/" + userId);
    }
}
```

### Step 2: Create API Test Class

Create file: `src/test/java/com/automation/api/UserAPITests.java`

```java
package com.automation.api;

import com.automation.base.BaseAPITest;
import com.automation.api.endpoints.UserEndpoints;
import com.automation.api.validators.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

public class UserAPITests extends BaseAPITest {

    @Test(description = "Verify get all users")
    public void testGetAllUsers() {
        UserEndpoints userAPI = new UserEndpoints();
        Response response = userAPI.getAllUsers();

        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 3000);
    }

    @Test(description = "Verify create user")
    public void testCreateUser() {
        UserEndpoints userAPI = new UserEndpoints();

        Map<String, String> userData = new HashMap<>();
        userData.put("name", "Test User");
        userData.put("email", "test@example.com");

        Response response = userAPI.createUser(userData);

        ResponseValidator.validateStatusCode(response, 201);
        ResponseValidator.validateFieldExists(response, "id");
    }
}
```

### Step 3: Run API Tests

```bash
mvn clean test -Dtest=UserAPITests
```

---

## Creating BDD Tests (Optional)

### Step 1: Create Feature File

Create file: `src/test/resources/features/UserManagement.feature`

```gherkin
Feature: User Management
  As an administrator
  I want to manage user accounts
  So that I can control system access

  Background:
    Given the API client is initialized

  @Smoke @API
  Scenario: Get all users
    When I send a GET request to "/users"
    Then the response status code should be 200
    And the response time should be less than 3000 milliseconds

  @API @Positive
  Scenario: Create a new user
    Given I have user data with name "John Doe" and email "john@example.com"
    When I send a POST request to "/users" with the user data
    Then the response status code should be 201
    And the response should have a generated "id" field
```

### Step 2: Create Step Definitions

Create file: `src/test/java/com/automation/bdd/stepdefs/UserAPISteps.java`

```java
package com.automation.bdd.stepdefs;

import com.automation.api.endpoints.UserEndpoints;
import com.automation.api.validators.ResponseValidator;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

public class UserAPISteps {
    private UserEndpoints userAPI = new UserEndpoints();
    private Response response;
    private Map<String, String> userData = new HashMap<>();

    @Given("the API client is initialized")
    public void apiClientInitialized() {
        // API client is initialized in BaseAPI constructor
    }

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {
        if (endpoint.equals("/users")) {
            response = userAPI.getAllUsers();
        }
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int expectedStatus) {
        ResponseValidator.validateStatusCode(response, expectedStatus);
    }

    @And("the response time should be less than {int} milliseconds")
    public void verifyResponseTime(int maxTime) {
        ResponseValidator.validateResponseTime(response, maxTime);
    }

    @Given("I have user data with name {string} and email {string}")
    public void prepareUserData(String name, String email) {
        userData.put("name", name);
        userData.put("email", email);
    }

    @When("I send a POST request to {string} with the user data")
    public void sendPostRequest(String endpoint) {
        response = userAPI.createUser(userData);
    }

    @And("the response should have a generated {string} field")
    public void verifyGeneratedField(String field) {
        ResponseValidator.validateFieldExists(response, field);
    }
}
```

### Step 3: Create Test Runner

Create file: `src/test/java/com/automation/bdd/runners/TestRunner.java`

```java
package com.automation.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.automation.bdd.stepdefs"},
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json",
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
```

### Step 4: Run BDD Tests

```bash
mvn clean test -Dtest=TestRunner
```

---

## Troubleshooting

### Problem: "Playwright browsers not found"
**Solution:**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Problem: "No tests found"
**Solution:**
```bash
# Verify compilation
mvn clean compile test-compile

# Check package names match
# Ensure test classes are in correct packages
```

### Problem: "Tests failing with timeout"
**Solution:**
- Increase timeout in `config.properties`
- Check if application is accessible
- Use proper wait strategies instead of fixed waits

### Problem: "Database connection refused"
**Solution:**
- Verify connection string in `config.properties`
- Ensure database service is running
- Check credentials are correct
- Verify network/firewall settings

### Problem: "API connection refused"
**Solution:**
```bash
# Verify API base URL in config.properties
api.base.url=https://your-actual-api.com

# Check API is accessible
curl https://your-actual-api.com/endpoint
```

---

## Common Use Cases

### Run Smoke Tests
```bash
# Create tests with @Test(groups = "smoke")
mvn clean test -Dgroups=smoke
```

### Run Tests in Different Browsers
```bash
# Chromium (default)
mvn clean test

# Firefox
mvn clean test -Dbrowser=firefox

# WebKit (Safari)
mvn clean test -Dbrowser=webkit
```

### Run Tests in Headless Mode
```bash
mvn clean test -Dheadless=true
```

### Parallel Test Execution
```bash
# Run with 4 parallel threads
mvn clean test -DthreadCount=4
```

### Debug Tests
```bash
# Run with verbose Maven output
mvn clean test -X

# Run specific test method
mvn clean test -Dtest=HomePageTests#testSearch
```

---

## Verification Checklist

Before running tests, ensure:

- [ ] Java 21 is installed: `java -version`
- [ ] Maven is installed: `mvn -version`
- [ ] Project compiles: `mvn clean compile test-compile`
- [ ] config.properties is configured with your application URLs
- [ ] Playwright browsers are installed (for UI tests)
- [ ] Test environment is accessible
- [ ] Database is running (if using DB tests)

---

## Report Locations

After test execution, find reports at:

- **Allure Results**: `target/allure-results/`
- **Allure Report**: `target/allure-report/` (after `mvn allure:report`)
- **Screenshots**: `target/screenshots/` (for failed UI tests)
- **Logs**: `logs/test-automation.log`
- **TestNG Reports**: `target/surefire-reports/`
- **Cucumber Reports**: `target/cucumber-reports/` (if using BDD)

---

## Tips & Best Practices

1. **Start Simple**: Begin with a single test and expand
2. **Use Page Objects**: Never use locators directly in tests
3. **External Configuration**: Keep test data in config files or data files
4. **Proper Waits**: Use WaitUtils methods, avoid Thread.sleep()
5. **Meaningful Names**: Use descriptive test and method names
6. **Independent Tests**: Each test should run independently
7. **Clean Up**: Close resources properly (browsers, connections)
8. **Logging**: Use SLF4J logging for debugging
9. **Assertions**: Use AssertJ for readable assertions
10. **Version Control**: Commit code regularly with meaningful messages

---

## Framework Capabilities

### What's Included

**Web UI Testing:**
- Playwright integration
- Cross-browser support (Chromium, Firefox, WebKit)
- Page Object Model base class
- Wait utilities
- Screenshot capture on failure

**API Testing:**
- RestAssured client configuration
- HTTP method wrappers
- Response validators
- Request/Response logging

**Database Testing:**
- MongoDB, MySQL, PostgreSQL support
- Connection factory pattern
- Query builder for SQL
- Connection pooling

**Test Infrastructure:**
- Base test classes for different test types
- Configuration management
- Test data generation
- TestNG listeners
- Allure reporting integration
- Retry mechanism for flaky tests

---

## Additional Resources

- **README.md**: Comprehensive framework documentation
- [Playwright Documentation](https://playwright.dev/java/)
- [RestAssured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Allure Documentation](https://docs.qameta.io/allure/)

---

## Next Steps

1. **Configure** `config.properties` with your application details
2. **Create** your first page object
3. **Write** your first test
4. **Run** the test and verify it passes
5. **Expand** with more page objects and tests
6. **Implement** BDD tests if needed
7. **Set up** CI/CD pipeline integration
8. **Review** reports and improve test coverage

---

## Getting Help

If you encounter issues:
1. Check this guide first
2. Review README.md for detailed documentation
3. Check logs in `logs/test-automation.log`
4. Review Allure reports for test execution details
5. Verify configuration in `config.properties`

**Common Debugging Commands:**
```bash
# Compile and check for errors
mvn clean compile test-compile

# Run with verbose output
mvn test -X

# Check dependencies
mvn dependency:tree
```

---

## You're Ready!

Your framework is configured with:
- Modern web automation (Playwright)
- API testing infrastructure (RestAssured)
- Database testing support
- BDD capability (Cucumber)
- Comprehensive reporting (Allure)
- Production-ready architecture

**Quick Start**: Create your first page object and test!

**Full Documentation**: See README.md for complete details

Good luck with your automation!
