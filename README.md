# Core Automation Framework

A production-ready test automation framework using Playwright, RestAssured, TestNG, and AssertJ for Web UI and API testing.

## Current Implementation

**MultiBank Trading Platform Test Suite** - A comprehensive test automation solution for https://trade.multibank.io/ demonstrating:
- 38 automated test cases covering Navigation, Trading, and Content validation
- Page Object Model with 4 page objects
- Data-driven testing with external JSON test data
- Cross-browser testing support (Chromium, Firefox, WebKit)
- Professional logging, wait management, and reporting

## Framework Architecture

This framework follows industry-standard design patterns and provides a solid foundation for building test automation solutions:

- **Page Object Model (POM)** - For UI test organization
- **Factory Pattern** - For browser and page object instantiation
- **Builder Pattern** - For SQL query construction
- **Singleton Pattern** - For configuration management
- **ThreadLocal Pattern** - For parallel test execution

## Project Structure

```
core-automation-framework/
│
├── src/
│   ├── main/
│   │   ├── java/com/automation/
│   │   │   ├── api/                    # API testing infrastructure
│   │   │   │   ├── APIClient.java      # RestAssured client setup
│   │   │   │   ├── BaseAPI.java        # Base API class with HTTP methods
│   │   │   │   └── validators/
│   │   │   │       └── ResponseValidator.java
│   │   │   │
│   │   │   ├── base/                   # Base test classes
│   │   │   │   ├── BaseTest.java
│   │   │   │   ├── BaseWebTest.java
│   │   │   │   └── BaseAPITest.java
│   │   │   │
│   │   │   ├── config/                 # Configuration management
│   │   │   │   ├── ConfigReader.java
│   │   │   │   └── TestConfig.java
│   │   │   │
│   │   │   ├── db/                     # Database utilities
│   │   │   │   ├── DatabaseConnection.java
│   │   │   │   ├── DatabaseConnectionFactory.java
│   │   │   │   ├── DatabaseType.java
│   │   │   │   ├── MongoDBConnection.java
│   │   │   │   ├── SQLConnection.java
│   │   │   │   └── QueryBuilder.java
│   │   │   │
│   │   │   ├── factory/                # Factory classes
│   │   │   │   ├── BrowserFactory.java
│   │   │   │   └── PageFactory.java
│   │   │   │
│   │   │   ├── listeners/              # TestNG listeners
│   │   │   │   ├── TestListener.java
│   │   │   │   └── RetryAnalyzer.java
│   │   │   │
│   │   │   ├── pages/                  # Page Object base class
│   │   │   │   └── BasePage.java       # Generic page methods
│   │   │   │
│   │   │   └── utils/                  # Utility classes
│   │   │       ├── PlaywrightManager.java
│   │   │       ├── WaitUtils.java
│   │   │       ├── FileUtils.java
│   │   │       └── TestDataGenerator.java
│   │   │
│   │   └── resources/
│   │       ├── config.properties       # Main configuration file
│   │       └── logback.xml             # Logging configuration
│   │
│   └── test/
│       ├── java/com/automation/        # Your test classes go here
│       │
│       └── resources/
│           ├── cucumber.properties     # Cucumber configuration
│           └── testng/
│               ├── testng.xml          # TestNG suite configuration
│               └── testng-bdd.xml      # BDD suite configuration
│
├── pom.xml                             # Maven dependencies
├── README.md                           # This file
├── QUICK-START-GUIDE.md                # Quick reference guide
└── .gitignore                          # Git exclusions
```

## Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **Node.js** (for Playwright browser installation)

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd core-automation-framework
```

2. **Install dependencies**
```bash
mvn clean install
```

3. **Install Playwright browsers**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Configuration

Edit `src/main/resources/config.properties`:

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

## Framework Features

### 1. Web UI Testing Infrastructure
- **Playwright** integration for modern browser automation
- **Page Object Model** base class with common methods
- **Factory Pattern** for browser and page instantiation
- **ThreadLocal** pattern for parallel execution support
- **WaitUtils** for explicit wait strategies
- Automatic screenshot capture on test failure
- Cross-browser support (Chromium, Firefox, WebKit)

### 2. API Testing Infrastructure
- **RestAssured** client configuration
- **BaseAPI** class with HTTP method wrappers (GET, POST, PUT, DELETE)
- **ResponseValidator** with common assertion methods
- Request/Response logging filters
- JSON processing support
- Allure integration for API reporting

### 3. Database Testing Support
- **Factory Pattern** for database connections
- Support for **MongoDB**, **MySQL**, and **PostgreSQL**
- **QueryBuilder** for dynamic SQL construction
- Connection pooling and thread-safe management
- AutoCloseable connections for proper resource cleanup

### 4. Test Data Management
- **TestDataGenerator** with JavaFaker integration
- Random data generation for emails, names, passwords, phone numbers
- Configurable test data paths
- External test data file support

### 5. Configuration Management
- **Singleton pattern** for configuration access
- Properties-based configuration
- Environment-specific configuration support
- Type-safe configuration getters

### 6. Reporting & Logging
- **Allure** integration for rich, interactive reports
- **SLF4J + Logback** for comprehensive logging
- **Screenshots** on test failure
- **TestNG listeners** for custom reporting
- Retry analyzer for flaky test handling

### 7. Base Test Classes
- **BaseTest** - Suite-level setup and teardown
- **BaseWebTest** - Web UI test initialization
- **BaseAPITest** - API test initialization

## MultiBank Test Suite Overview

### Test Coverage (38 Test Cases)

**Navigation & Layout Tests** (10 tests)
- Navigation menu display and structure
- Navigation items functionality
- Page transitions and URL validation
- Cross-browser navigation consistency

**Trading Functionality Tests** (15 tests)
- Spot trading section verification
- Trading pairs table structure
- Trading pair data validation
- Market indicators (Fear Index, Top Gainers/Losers)
- Investment opportunities visibility
- Quick access tools validation

**Content Validation Tests** (13 tests)
- Footer section verification
- App Store and Google Play download links
- Marketing banners validation
- About Us page components
- Content loading and rendering
- Social media links verification

### Page Objects Implemented

```
src/main/java/com/automation/pages/multibank/
├── NavigationPage.java    - Top navigation menu (10+ methods)
├── TradingPage.java        - Trading functionality (20+ methods)
├── FooterPage.java         - Footer and downloads (15+ methods)
└── AboutUsPage.java        - About Us content (10+ methods)
```

### Test Data Files

```
src/test/resources/testdata/
├── navigation-data.json    - Navigation menu items and links
├── trading-data.json       - Trading pairs and categories
└── content-data.json       - Download links, banners, content
```

## Writing Custom Tests

### Example: Creating a New Page Object

```java
package com.automation.pages.myapp;

import com.automation.pages.BasePage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomePage extends BasePage {
    private final String searchInput = "#search";
    private final String searchButton = "button[type='submit']";

    public HomePage() {
        super();
    }

    public HomePage searchFor(String term) {
        waitForSelector(searchInput);
        fill(searchInput, term);
        click(searchButton);
        log.info("Searched for: {}", term);
        return this;
    }

    public boolean isSearchResultsDisplayed() {
        waitForSelector(".results");
        return true;
    }
}
```

### Example: Creating a Test Class

```java
package com.automation.myapp;

import com.automation.base.BaseWebTest;
import com.automation.pages.myapp.HomePage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("My Application")
@Feature("Search Functionality")
public class SearchTests extends BaseWebTest {

    @Test(description = "Verify search functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Search")
    public void testSearch() {
        log.info("Starting search test");

        HomePage homePage = new HomePage();
        homePage.searchFor("test query");

        assertThat(homePage.isSearchResultsDisplayed())
                .as("Search results should be displayed")
                .isTrue();

        log.info("Search test completed successfully");
    }
}
```

### Example: Using Data Providers

```java
package com.automation.providers;

import org.testng.annotations.DataProvider;

public class MyDataProviders {

    @DataProvider(name = "browserProvider")
    public Object[][] browserProvider() {
        return new Object[][] {
            { "chromium" },
            { "firefox" },
            { "webkit" }
        };
    }

    @DataProvider(name = "searchTermsProvider")
    public Object[][] searchTermsProvider() {
        return new Object[][] {
            { "test query", "Results" },
            { "automation", "Results" },
            { "framework", "Results" }
        };
    }
}

// Usage in test:
@Test(dataProvider = "searchTermsProvider",
      dataProviderClass = MyDataProviders.class)
public void testSearchWithMultipleTerms(String searchTerm, String expectedText) {
    log.info("Testing search with term: {}", searchTerm);
    // Test implementation
}
```

### Example: External Test Data (JSON)

Create file: `src/test/resources/testdata/my-data.json`
```json
{
  "expectedItems": ["Item1", "Item2", "Item3"],
  "settings": {
    "timeout": 30000,
    "retryCount": 3
  }
}
```

Read in test:
```java
import com.automation.utils.TestDataReader;
import com.fasterxml.jackson.databind.JsonNode;

@BeforeMethod
public void setup() {
    JsonNode testData = TestDataReader.readJsonFile("my-data.json");
    List<String> items = TestDataReader.getStringList(testData, "expectedItems");
    int timeout = TestDataReader.getIntValue(testData, "settings", "timeout");
}
```

### Creating API Tests

1. **Create API Endpoint Class** (extend BaseAPI):

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

2. **Create Test Class** (extend BaseAPITest):

```java
package com.automation.api;

import com.automation.base.BaseAPITest;
import com.automation.api.endpoints.UserEndpoints;
import com.automation.api.validators.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

public class UserAPITests extends BaseAPITest {

    @Test(description = "Verify user creation")
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

### Creating BDD Tests (Optional)

1. **Create Feature File** in `src/test/resources/features/`:

```gherkin
Feature: User Management
  As a user
  I want to manage user accounts
  So that I can control access to the system

  Scenario: Create a new user
    Given the API client is initialized
    When I send a POST request to "/users" with user data
    Then the response status code should be 201
    And the response should contain user details
```

2. **Create Step Definitions**:

```java
package com.automation.bdd.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class UserSteps {

    @Given("the API client is initialized")
    public void apiClientInitialized() {
        // Initialize API client
    }

    @When("I send a POST request to {string} with user data")
    public void sendPostRequest(String endpoint) {
        // Send POST request
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int statusCode) {
        // Verify status code
    }
}
```

3. **Create Cucumber Test Runner**:

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
        "json:target/cucumber-reports/cucumber.json"
    }
)
public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
```

### Creating Database Tests

```java
package com.automation.db;

import com.automation.db.DatabaseConnectionFactory;
import com.automation.db.DatabaseType;
import com.automation.db.SQLConnection;
import com.automation.db.QueryBuilder;
import org.testng.annotations.Test;

public class DatabaseTests {

    @Test
    public void testDatabaseQuery() {
        SQLConnection connection = (SQLConnection)
            DatabaseConnectionFactory.getConnection(DatabaseType.MYSQL);

        try {
            String query = QueryBuilder.select("id", "name", "email")
                .from("users")
                .where("status = 'active'")
                .build();

            Object result = connection.executeQuery(query);
            // Perform assertions
        } finally {
            connection.disconnect();
        }
    }
}
```

## Running Tests

### Run MultiBank Test Suite
```bash
# Run all MultiBank tests (38 test cases)
mvn clean test -DsuiteXmlFile=testng/multibank-suite.xml

# Run specific test class
mvn clean test -Dtest=NavigationTests
mvn clean test -Dtest=TradingTests
mvn clean test -Dtest=ContentValidationTests

# Run with specific browser
mvn clean test -DsuiteXmlFile=testng/multibank-suite.xml -Dbrowser=firefox
mvn clean test -DsuiteXmlFile=testng/multibank-suite.xml -Dbrowser=webkit

# Run in headless mode
mvn clean test -DsuiteXmlFile=testng/multibank-suite.xml -Dheadless=true

# Run tests in parallel
mvn clean test -DsuiteXmlFile=testng/multibank-suite.xml -DthreadCount=4
```

### Run Custom Tests
```bash
# Run all tests in src/test/java
mvn clean test

# Run with specific TestNG suite
mvn clean test -DsuiteXmlFile=testng.xml

# Run BDD tests (if implemented)
mvn clean test -Dtest=YourTestRunner
```

## Reporting

### Generate Allure Report

```bash
# Run tests
mvn clean test

# Generate and serve report
mvn allure:serve

# Generate static report
mvn allure:report
```

### Report Locations

- **Allure Results**: `target/allure-results/`
- **Allure Report**: `target/allure-report/`
- **Screenshots**: `target/screenshots/`
- **Logs**: `logs/test-automation.log`
- **TestNG Reports**: `target/surefire-reports/`
- **Cucumber Reports**: `target/cucumber-reports/`

## Design Patterns Used

### 1. Page Object Model (POM)
Separates page structure from test logic:
- `BasePage` - Common page methods (click, fill, getText, waitForSelector)
- Extend BasePage for application-specific page objects

### 2. Factory Pattern
- `BrowserFactory` - Creates browser instances with proper configuration
- `PageFactory` - Creates page objects using reflection
- `DatabaseConnectionFactory` - Creates and manages database connections

### 3. Builder Pattern
- `QueryBuilder` - Fluent API for SQL query construction

### 4. Singleton Pattern
- `TestConfig` - Single configuration instance with thread-safe access
- `PlaywrightManager` - ThreadLocal-based browser management

## Tech Stack

| Technology | Purpose | Version |
|------------|---------|---------|
| Playwright | Web browser automation | 1.48.0 |
| RestAssured | API testing | 5.5.0 |
| TestNG | Test framework | 7.10.2 |
| Cucumber | BDD framework | 7.20.1 |
| AssertJ | Fluent assertions | 3.26.3 |
| MongoDB Driver | NoSQL database testing | 5.2.0 |
| MySQL Connector | MySQL database testing | 8.4.0 |
| PostgreSQL Driver | PostgreSQL database testing | 42.7.4 |
| Allure | Test reporting | 2.29.0 |
| SLF4J/Logback | Logging | 2.0.16/1.5.8 |
| Lombok | Boilerplate reduction | 1.18.34 |
| Jackson | JSON processing | 2.18.0 |
| JavaFaker | Test data generation | 1.0.2 |

## Best Practices (Demonstrated in MultiBank Suite)

1. **Keep tests independent** - Each test runs standalone, no dependencies
2. **Use meaningful test names** - Descriptive test methods (testNavigationMenuDisplayed)
3. **Follow AAA pattern** - Arrange, Act, Assert structure
4. **Use page objects** - All locators in page objects, never in tests
5. **Handle waits properly** - waitForSelector(), no Thread.sleep()
6. **External test data** - JSON files for all test data
7. **Comprehensive logging** - SLF4J with DEBUG/INFO/WARN/ERROR levels
8. **AssertJ assertions** - Fluent, readable assertions with custom messages
9. **Allure annotations** - @Epic, @Feature, @Story, @Severity for reporting
10. **Data providers** - Parameterized tests for cross-browser and data-driven testing
11. **Proper exception handling** - Try-catch with logging, no silent failures
12. **TestNG priorities** - Ordered test execution when needed
13. **Page object constructors** - Initialize in @BeforeMethod for fresh state
14. **Configurable timeouts** - All waits use config.properties timeout values

## Troubleshooting

### Playwright browsers not found
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Database connection issues
- Verify connection strings in `config.properties`
- Ensure database services are running
- Check firewall and network settings
- Verify credentials are correct

### Tests failing in parallel
- Ensure thread safety in shared resources
- Use ThreadLocal for browser instances (already implemented)
- Avoid shared test data

### Compilation errors
```bash
mvn clean compile test-compile
```

## Framework Capabilities

### Cross-Browser Testing
- Chromium (Chrome, Edge)
- Firefox
- WebKit (Safari)

### Parallel Execution
- Thread-safe browser management
- Configurable thread count
- Independent test execution

### Wait Strategies
- Page load waits
- Element visibility waits
- Element clickability waits
- Custom timeout configuration

### Test Data
- External configuration
- Random data generation
- Database-driven tests

### Error Handling
- Automatic screenshot capture
- Detailed error logging
- Retry mechanism for flaky tests

## Additional Resources

- [Playwright Documentation](https://playwright.dev/java/)
- [RestAssured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Allure Documentation](https://docs.qameta.io/allure/)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Contact

For questions or support, please open an issue in the repository.
