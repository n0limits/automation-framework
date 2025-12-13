# Core Automation Framework

Production-grade web automation framework for testing the MultiBank Trading Platform using Playwright, TestNG, and Maven.

**Created by:** Victor Grozev

## Overview

Comprehensive test automation solution for https://trade.multibank.io/ featuring:
- 38 automated test cases covering Navigation, Trading, and Content validation
- Page Object Model with 4 page objects (NavigationPage, TradingPage, FooterPage, AboutUsPage)
- Data-driven testing with external JSON test data
- Cross-browser testing with Playwright (Chromium, Firefox, WebKit)
- Type-safe browser selection using BrowserType enum
- Automatic retry mechanism for flaky tests
- Professional logging, wait management, and Allure reporting

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
│   │   │   ├── enums/                  # Type-safe enumerations
│   │   │   │   └── BrowserType.java    # Browser type enum (Chromium, Firefox, WebKit)
│   │   │   │
│   │   │   ├── factory/                # Factory classes
│   │   │   │   ├── BrowserFactory.java
│   │   │   │   └── PageFactory.java
│   │   │   │
│   │   │   ├── listeners/              # TestNG listeners
│   │   │   │   ├── TestListener.java
│   │   │   │   ├── RetryAnalyzer.java
│   │   │   │   └── RetryListener.java  # Auto-attaches retry to all tests
│   │   │   │
│   │   │   ├── pages/                  # Page Object base class
│   │   │   │   ├── BasePage.java       # Generic page methods
│   │   │   │   └── multibank/          # MultiBank page objects
│   │   │   │       ├── NavigationPage.java
│   │   │   │       ├── TradingPage.java
│   │   │   │       ├── FooterPage.java
│   │   │   │       └── AboutUsPage.java
│   │   │   │
│   │   │   ├── providers/              # TestNG data providers
│   │   │   │   └── TestDataProviders.java
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
│       ├── java/com/automation/
│       │   └── multibank/              # MultiBank test classes
│       │       ├── NavigationTests.java
│       │       ├── TradingTests.java
│       │       └── ContentValidationTests.java
│       │
│       └── resources/
│           ├── cucumber.properties     # Cucumber configuration
│           ├── testdata/               # Test data JSON files
│           │   ├── navigation-data.json
│           │   ├── trading-data.json
│           │   └── content-data.json
│           │
│           └── testng/                 # TestNG suite configurations
│               ├── testng.xml          # Cross-browser suite (default)
│               ├── testng-chromium.xml # Single browser suite
│               ├── testng-smoke.xml    # Smoke test suite
│               └── testng-bdd.xml      # BDD suite configuration
│
├── pom.xml                             # Maven dependencies
├── README.md                           # This file
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
base.url=https://trade.multibank.io
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

**1. Web UI Testing Infrastructure**
- Playwright integration for modern browser automation
- Page Object Model base class with common methods
- Factory Pattern for browser and page instantiation
- ThreadLocal pattern for parallel execution support
- Explicit wait strategies with Playwright auto-waiting
- Automatic screenshot capture on test failure
- Cross-browser support (Chromium, Firefox, WebKit)
- Type-safe browser selection using BrowserType enum

**2. API Testing Infrastructure**
- RestAssured client configuration
- BaseAPI class with HTTP method wrappers (GET, POST, PUT, DELETE)
- ResponseValidator with common assertion methods
- Request/Response logging filters
- JSON processing support
- Allure integration for API reporting

**3. Database Testing Support**
- Factory Pattern for database connections
- Support for MongoDB, MySQL, and PostgreSQL
- QueryBuilder for dynamic SQL construction
- Connection pooling and thread-safe management
- AutoCloseable connections for proper resource cleanup

**4. Test Data Management**
- TestDataGenerator with JavaFaker integration
- Random data generation for emails, names, passwords, phone numbers
- External JSON test data files
- Centralized TestDataProviders for parameterized tests
- Type-safe data reading utilities

**5. Configuration Management**
- Singleton pattern for configuration access
- Properties-based configuration
- Environment-specific configuration support
- Type-safe configuration getters
- Maven property overrides for CI/CD

**6. Reporting & Logging**
- Allure integration for rich, interactive reports
- SLF4J + Logback for comprehensive logging
- Screenshots on test failure
- TestNG listeners for custom reporting
- Automatic retry analyzer for flaky tests (up to 2 retries)

**7. Base Test Classes**
- BaseTest - Suite-level setup and teardown
- BaseWebTest - Web UI test initialization with browser parameterization
- BaseAPITest - API test initialization

**8. Type-Safe Browser Management**
- BrowserType enum for compile-time browser validation
- String-to-enum conversion with error handling
- Support for browser aliases (chrome → chromium, safari → webkit)
- Clean integration with BrowserFactory

## MultiBank Test Suite Overview

### Test Coverage (38 Test Cases)

**Navigation & Layout Tests** (10 tests)
- Navigation menu display and structure
- Navigation items functionality
- Page transitions and URL validation
- Cross-browser navigation consistency

**Trading Functionality Tests** (17 tests)
- Spot trading section verification
- Trading pairs table structure
- Trading pair data validation
- Trading category switching (Favorites, All Pairs)
- Market indicators (Fear Index, Top Gainers/Losers)
- Investment opportunities visibility
- Quick access tools validation

**Content Validation Tests** (15 tests)
- Footer section verification
- App Store and Google Play download links
- Download link configuration validation
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

**Key Features:**
- Locator-based selectors using Playwright Locator API
- Explicit waits for dynamic content
- Scrolling support for elements below the fold
- Smart navigation with fallback strategies
- Comprehensive logging for debugging

### Test Data Files

```
src/test/resources/testdata/
├── navigation-data.json    - Navigation menu items and links
├── trading-data.json       - Trading pairs and categories
└── content-data.json       - Download links, banners, content
```

### Data Providers

```
src/main/java/com/automation/providers/TestDataProviders.java
├── browserProvider         - Browser types (chromium, firefox, webkit)
├── navigationItemsProvider - Navigation menu items
├── tradingPairsProvider    - Trading pair names
├── downloadLinksProvider   - App store download links
├── marketingBannersProvider- Marketing banner text
└── tradingTabsProvider     - Trading category tabs
```

## Running Tests

### Multi-Browser Testing

The framework supports multiple approaches for browser testing. Choose the approach that best fits your needs.

#### Supported Browsers

The framework supports three browsers via Playwright:
- **Chromium** (Google Chrome/Edge)
- **Firefox** (Mozilla Firefox)
- **WebKit** (Safari engine)

Browser selection is type-safe using the `BrowserType` enum:
```java
import com.automation.enums.BrowserType;

BrowserType.CHROMIUM
BrowserType.FIREFOX
BrowserType.WEBKIT
```

### Execution Approaches

#### Approach 1: TestNG XML Suite (RECOMMENDED)

**Best for:** Running all tests across multiple browsers in parallel

**How it works:**
- Each browser runs as a separate TestNG `<test>` block
- All browsers execute in parallel (configurable via `thread-count`)
- Each test method runs in parallel within its browser

**Default Configuration:** `testng.xml`
```xml
<suite name="Cross Browser Suite" parallel="tests" thread-count="3">
    <test name="Chromium Tests">
        <parameter name="browser" value="chromium"/>
        <packages><package name="com.automation.multibank"/></packages>
    </test>
    <test name="Firefox Tests">
        <parameter name="browser" value="firefox"/>
        <packages><package name="com.automation.multibank"/></packages>
    </test>
    <test name="WebKit Tests">
        <parameter name="browser" value="webkit"/>
        <packages><package name="com.automation.multibank"/></packages>
    </test>
</suite>
```

**Execution:**
```bash
# Default: Runs on all 3 browsers in parallel
mvn clean test

# Or explicitly specify the suite
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng.xml
```

**Advantages:**
- Fastest parallel execution across browsers
- Clear separation of browser runs in reports
- Easy to enable/disable specific browsers
- No code changes required

#### Approach 2: Single Browser Suite

**Best for:** Quick testing on one browser, debugging, CI/CD pipelines with browser-specific jobs

**Execution:**
```bash
# Chromium only
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml

# Smoke tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml
```

**Advantages:**
- Faster test feedback (1 browser instead of 3)
- Better for local development
- Ideal for browser-specific debugging

#### Approach 3: DataProvider Parametrization (OPTIONAL)

**Best for:** Running specific test methods across browsers sequentially

**How it works:**
- Use TestNG `@DataProvider` to parametrize individual test methods
- Each test method runs once per browser
- Tests execute sequentially (browser1, browser2, browser3)

**Example Implementation:**

TestDataProviders.java:
```java
@DataProvider(name = "browserProvider")
public Object[][] browserProvider() {
    return new Object[][] {
        { "chromium" },
        { "firefox" },
        { "webkit" }
    };
}
```

Test Class:
```java
@Test(description = "Verify homepage loads",
      dataProvider = "browserProvider",
      dataProviderClass = TestDataProviders.class)
public void testHomepageLoad(String browser) {
    // Test will run 3 times (once per browser)
    log.info("Testing on browser: {}", browser);

    // Browser is already set up by BaseWebTest @BeforeMethod
    // from TestNG XML parameter or @Optional default

    // Your test logic here
    assertThat(homePage.isLoaded()).isTrue();
}
```

**Important Notes:**
- DataProvider approach requires separate test execution per browser
- Use TestNG XML `<parameter name="browser" value="..."/>` to set the browser
- DataProvider parametrization is additional to the XML browser parameter
- This approach is less efficient than TestNG XML parallel execution

**When to use:**
- You need fine-grained control over which tests run on which browsers
- You want to test browser-specific behavior in a single test method
- You're running individual test methods (not full suites)

**Advantages:**
- Fine-grained browser control per test method
- Good for testing browser-specific features
- Test method explicitly receives browser parameter

**Disadvantages:**
- Slower than XML parallel execution
- Requires changes to test method signatures
- Can lead to duplicate test execution if not configured correctly

### Common Execution Commands

#### Local Development (Single Browser)
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml
```

#### CI/CD Pipeline (All Browsers)
```bash
mvn clean test
```

#### Specific Browser Override
```bash
mvn clean test -Dbrowser=firefox
```

#### Test Class Execution
```bash
# Run specific test class
mvn clean test -Dtest=NavigationTests
mvn clean test -Dtest=TradingTests
mvn clean test -Dtest=ContentValidationTests
```

#### Headless Mode
Edit `config.properties`:
```properties
headless=true
```

Or via command line:
```bash
mvn clean test -Dheadless=true
```

### 📊 Test Reports

The framework generates comprehensive Allure HTML reports for stakeholder review.

#### View Report Interactively (Local Development)
```bash
# Run tests and open interactive report in browser
mvn clean test
mvn allure:serve
```

#### Generate Stakeholder Report (for Commit/Sharing)
```bash
# Run tests
mvn clean test

# Generate static HTML report
mvn allure:report

# Copy to reports directory (for git commit)
mvn allure:report site
```

**The report will be available at:** `reports/latest/index.html`

#### What's Included in the Report
- ✅ Test execution overview with pass/fail statistics
- ✅ Visual charts and graphs
- ✅ Test duration timeline
- ✅ Detailed test results with stack traces
- ✅ Test categorization by Epic, Feature, and Story
- ✅ Historical trends (when running multiple times)
- ✅ Failed test screenshots and logs (when configured)

#### Sharing with Stakeholders
The `reports/latest/` directory is committed to the repository and can be:
- Opened directly in any browser (no installation required)
- Shared via email or file sharing
- Viewed offline (all assets bundled)
- Attached to pull requests or JIRA tickets

**Note:** The `logs/` directory is NOT committed (contains sensitive internal logs). Only the sanitized HTML report is shared.

### Available TestNG Suites

- `testng.xml` - Full cross-browser suite (Chromium + Firefox + WebKit in parallel)
- `testng-smoke.xml` - Critical tests on Chromium only
- `testng-chromium.xml` - All tests on Chromium only

### Browser Initialization Architecture

**Browser Initialization Flow:**
```
TestNG XML Parameter
    ↓
BaseWebTest.setupBrowser(@Parameters("browser"))
    ↓
BrowserFactory.launchBrowser(String)
    ↓
BrowserType.fromString(String) → Enum
    ↓
BrowserFactory.launchBrowser(BrowserType)
    ↓
Playwright.chromium|firefox|webkit().launch()
    ↓
PlaywrightManager.setPage(page)
```

**Thread Safety:**
- Each test thread has its own isolated browser instance
- `PlaywrightManager` uses `ThreadLocal<Browser>`, `ThreadLocal<Page>`, etc.
- Parallel execution is fully supported

### Multi-Browser Testing Best Practices

1. Use TestNG XML for full suite execution (Approach 1)
2. Use single-browser suite for local development (Approach 2)
3. Use DataProvider only for browser-specific test logic (Approach 3)
4. Enable headless mode in CI/CD pipelines (`headless=true`)
5. Let RetryListener handle flaky tests (no manual retry configuration needed)

### Related Files

- **BrowserType Enum:** `src/main/java/com/automation/enums/BrowserType.java`
- **BrowserFactory:** `src/main/java/com/automation/factory/BrowserFactory.java`
- **BaseWebTest:** `src/main/java/com/automation/base/BaseWebTest.java`
- **RetryAnalyzer:** `src/main/java/com/automation/listeners/RetryAnalyzer.java`
- **RetryListener:** `src/main/java/com/automation/listeners/RetryListener.java`
- **TestNG Suites:** `src/test/resources/testng/`

## Writing Custom Tests

### Example: Creating a New Page Object

```java
package com.automation.pages.myapp;

import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomePage extends BasePage {
    private final Locator searchInput;
    private final Locator searchButton;
    private final Locator searchResults;

    public HomePage() {
        super();
        this.searchInput = page.locator("#search");
        this.searchButton = page.locator("button[type='submit']");
        this.searchResults = page.locator(".results");
    }

    public HomePage searchFor(String term) {
        searchInput.fill(term);
        searchButton.click();
        log.info("Searched for: {}", term);
        return this;
    }

    public boolean isSearchResultsDisplayed() {
        return searchResults.isVisible();
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

    private HomePage homePage;

    @Override
    protected void performAdditionalSetup() {
        homePage = new HomePage();
    }

    @Test(description = "Verify search functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Search")
    public void testSearch() {
        log.info("Starting search test");

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
@Test(description = "Test search with multiple terms",
      dataProvider = "searchTermsProvider",
      dataProviderClass = MyDataProviders.class)
public void testSearchWithMultipleTerms(String searchTerm, String expectedText) {
    log.info("Testing search with term: {}", searchTerm);
    homePage.searchFor(searchTerm);
    assertThat(homePage.isSearchResultsDisplayed()).isTrue();
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

@Override
protected void performAdditionalSetup() {
    JsonNode testData = TestDataReader.readJsonFile("my-data.json");
    List<String> items = TestDataReader.getStringList(testData, "expectedItems");
    int timeout = TestDataReader.getIntValue(testData, "settings", "timeout");
}
```

### Example: Type-Safe Browser Selection

```java
import com.automation.enums.BrowserType;
import com.automation.factory.BrowserFactory;

// Using enum directly (compile-time safety)
BrowserFactory.launchBrowser(BrowserType.FIREFOX);

// Converting from string (runtime validation)
BrowserType browser = BrowserType.fromString("chromium");
BrowserFactory.launchBrowser(browser);

// Get display name
String displayName = BrowserType.WEBKIT.getDisplayName(); // "Apple WebKit (Safari)"
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
import java.util.HashMap;
import java.util.Map;

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

## Design Patterns Used

### 1. Page Object Model (POM)
Separates page structure from test logic:
- `BasePage` - Common page methods (navigateTo, getTitle)
- Extend BasePage for application-specific page objects
- Locator-based selectors using Playwright Locator API

### 2. Factory Pattern
- `BrowserFactory` - Creates browser instances with proper configuration
- `PageFactory` - Creates page objects using reflection
- `DatabaseConnectionFactory` - Creates and manages database connections

### 3. Builder Pattern
- `QueryBuilder` - Fluent API for SQL query construction

### 4. Singleton Pattern
- `TestConfig` - Single configuration instance with thread-safe access
- `PlaywrightManager` - ThreadLocal-based browser management

### 5. Enum Pattern
- `BrowserType` - Type-safe browser selection
- `DatabaseType` - Type-safe database selection

## Retry Mechanism

All tests automatically retry up to 2 times on failure via the `RetryListener`.

**Configuration:**
- `RetryAnalyzer` - Implements retry logic (MAX_RETRY_COUNT = 2)
- `RetryListener` - Automatically attaches RetryAnalyzer to all test methods
- No manual `@Test(retryAnalyzer = ...)` annotation required

**How it works:**
```
Test fails → RetryAnalyzer.retry() called
  ↓
  If retryCount < 2 → Retry test
  ↓
  If retryCount >= 2 → Mark as failed
```

**Logs Example:**
```
[WARN] Retrying test 'testHomepageLoad' - Attempt 1 of 2
[WARN] Retrying test 'testHomepageLoad' - Attempt 2 of 2
```

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

## Best Practices

The framework demonstrates these best practices:

1. **Test Independence** - Each test runs standalone, no dependencies
2. **Meaningful Test Names** - Descriptive test methods (testNavigationMenuDisplayed)
3. **AAA Pattern** - Arrange, Act, Assert structure
4. **Page Object Model** - All locators in page objects, never in tests
5. **Proper Wait Strategies** - Explicit waits with Playwright auto-waiting
6. **External Test Data** - JSON files for all test data
7. **Comprehensive Logging** - SLF4J with DEBUG/INFO/WARN/ERROR levels
8. **Fluent Assertions** - AssertJ assertions with custom messages
9. **Allure Annotations** - @Epic, @Feature, @Story, @Severity for reporting
10. **Data-Driven Testing** - TestNG DataProviders for parameterized tests
11. **Exception Handling** - Try-catch with logging, no silent failures
12. **Cross-Browser Support** - Browser parameterization via TestNG
13. **Fresh Test State** - Page objects initialized in @BeforeMethod hooks
14. **Configurable Timeouts** - All waits use config.properties timeout values
15. **Type-Safe Enums** - BrowserType enum for compile-time browser validation
16. **Automatic Retry** - RetryListener for handling flaky tests

## Troubleshooting

### Browser not launching
- Check `config.properties` has correct browser value
- Verify Playwright browsers are installed:
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### Tests not running on multiple browsers
- Verify you're using `testng.xml` (not `testng-chromium.xml`)
- Check `pom.xml` has `<suiteXmlFile>src/test/resources/testng/testng.xml</suiteXmlFile>`
- Ensure browser parameters are correctly defined in TestNG XML suite

### RetryAnalyzer not working
- Verify `RetryListener` is in TestNG suite `<listeners>` section
- Check logs for "RetryAnalyzer attached to test" messages (DEBUG level)
- Ensure `RetryListener` is properly registered in all TestNG suite files

### Database connection issues
- Verify connection strings in `config.properties`
- Ensure database services are running
- Check firewall and network settings
- Verify credentials are correct

### Tests failing in parallel
- Ensure thread safety in shared resources
- Use ThreadLocal for browser instances (already implemented)
- Avoid shared test data
- Check thread-count configuration in TestNG suite files

### Compilation errors
```bash
mvn clean compile test-compile
```

### Browser-specific test failures
- Some tests may behave differently across browsers (expected)
- Use browser-specific logic when necessary
- Check Playwright documentation for browser-specific limitations
- Consider using conditional test execution for browser-specific features

## Framework Capabilities

**Cross-Browser Testing**
- Chromium (Chrome, Edge, Chromium browsers)
- Firefox (Mozilla Firefox)
- WebKit (Apple Safari engine)
- Parallel execution across browsers
- Type-safe browser selection with BrowserType enum
- Browser parameter support via TestNG

**Parallel Execution**
- Thread-safe browser management with ThreadLocal
- Configurable thread count (suite and test level)
- Independent test execution
- Concurrent browser sessions

**Wait Strategies**
- Playwright auto-waiting for actionability
- Page load waits (NETWORKIDLE, DOMCONTENTLOADED, LOAD)
- Element visibility waits with explicit timeouts
- Element clickability waits
- Custom timeout configuration

**Test Data Management**
- External JSON configuration files
- Random data generation with JavaFaker
- Database-driven tests
- Centralized data providers for parameterization
- Type-safe data reading utilities

**Error Handling & Reporting**
- Automatic screenshot capture on failure
- Detailed error logging with SLF4J
- Automatic retry mechanism for flaky tests (up to 2 retries)
- Allure reporting with step-by-step execution
- TestNG HTML reports

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

## Author

**Victor Grozev**
- Role: Creator & Lead Developer
- Framework: Core Automation Framework

## Contact

For questions or support, please open an issue in the repository.
