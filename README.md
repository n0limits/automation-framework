# Core Automation Framework

Production-grade web automation framework for testing the MultiBank Trading Platform using Playwright, TestNG, and Maven.

**Created by:** Victor Grozev

## Overview

Comprehensive test automation solution for https://trade.multibank.io/ featuring:
- 50 automated test cases covering Navigation, Trading, Content validation, Smoke, and Performance testing
- Page Object Model with 4 page objects (NavigationPage, TradingPage, FooterPage, AboutUsPage)
- Data-driven testing with external JSON test data
- Cross-browser testing with Playwright (Chromium, Firefox, WebKit)
- Type-safe browser selection using BrowserType enum
- Automatic retry mechanism for flaky tests
- Professional logging, wait management, and Allure reporting with screenshot capture
- Character frequency utility for string analysis
- CI/CD integration with GitHub Actions workflows

## Framework Architecture

This framework follows industry-standard design patterns and provides a solid foundation for building test automation solutions:

- **Page Object Model (POM)** - For UI test organization
- **Factory Pattern** - For browser and page object instantiation
- **Builder Pattern** - For SQL query construction
- **Singleton Pattern** - For configuration management
- **ThreadLocal Pattern** - For parallel test execution

## Project Structure

```
automation-framework/
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
│   │   │       ├── TestDataGenerator.java
│   │   │       └── CharacterFrequencyUtil.java
│   │   │
│   │   └── resources/
│   │       ├── config.properties       # Main configuration file
│   │       └── logback.xml             # Logging configuration
│   │
│   └── test/
│       ├── java/com/automation/
│       │   ├── multibank/              # MultiBank test classes
│       │   │   ├── NavigationTests.java
│       │   │   ├── TradingTests.java
│       │   │   ├── ContentValidationTests.java
│       │   │   ├── SmokeTests.java
│       │   │   ├── PerformanceTests.java
│       │   │   └── ScreenshotDemoTest.java
│       │   │
│       │   └── utils/                  # Utility test classes
│       │       └── CharacterFrequencyUtilTests.java
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
│               ├── testng-multi-browser-smoke.xml
│               ├── testng-bdd.xml      # BDD suite configuration
│               ├── multibank-suite.xml
│               └── run-all-tests-in-paralel-multi-browsers.xml
│
├── .github/
│   └── workflows/                      # GitHub Actions CI/CD pipelines
│       ├── test-automation.yml         # Main test automation workflow
│       ├── pr-checks.yml               # Pull request validation
│       └── nightly-tests.yml           # Scheduled nightly tests
│
├── docs/                               # Additional documentation
│   ├── character-frequency-utility.md
│   └── task2-requirements-coverage.md
│
├── reports/                            # Stakeholder test reports
│   └── latest/                         # Latest Allure HTML report
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
cd automation-framework
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
- Automatic screenshot capture on test failure with Allure integration
- Cross-browser support (Chromium, Firefox, WebKit)
- Type-safe browser selection using BrowserType enum
- ITestContext-based browser parameterization for thread-safe parallel execution

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
- Automatic screenshot capture on test failure
- Screenshots embedded in Allure reports via Allure.addAttachment API
- TestNG listeners for custom reporting
- Automatic retry analyzer for flaky tests (up to 2 retries)

**7. Base Test Classes**
- BaseTest - Suite-level setup and teardown
- BaseWebTest - Web UI test initialization with ITestContext browser parameterization
- BaseAPITest - API test initialization

**8. Type-Safe Browser Management**
- BrowserType enum for compile-time browser validation
- String-to-enum conversion with error handling
- Support for browser aliases (chrome to chromium, safari to webkit)
- Clean integration with BrowserFactory

**9. String Analysis Utility**
- CharacterFrequencyUtil for counting character occurrences
- Preserves order of first appearance using LinkedHashMap
- Case-insensitive, ignores spaces and special characters
- O(n) time complexity, O(k) space complexity
- Comprehensive test coverage with 12 test cases

**10. CI/CD Integration**
- GitHub Actions workflows for automated testing
- Pull request validation with smoke tests
- Scheduled nightly full test suite execution
- Automatic test result reporting and artifact upload

## MultiBank Test Suite Overview

### Test Coverage (50 Test Cases)

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

**Smoke Tests** (3 tests)
- Homepage load validation
- Critical page elements verification
- JavaScript error detection

**Performance Tests** (4 tests)
- Homepage load time measurement
- Page navigation performance
- DOM content loaded timing
- Network idle state verification

**Screenshot Demo Test** (1 test)
- Demonstrates automatic screenshot capture on failure
- Shows Allure report integration with failure evidence

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
- Explicit waits for dynamic content (10-15 second timeouts)
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
- Browser parameter extracted via ITestContext for thread safety

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
mvn clean test -Dtest=SmokeTests
mvn clean test -Dtest=PerformanceTests
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

### Test Reports

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
- Test execution overview with pass/fail statistics
- Visual charts and graphs
- Test duration timeline
- Detailed test results with stack traces
- Test categorization by Epic, Feature, and Story
- Historical trends (when running multiple times)
- Failed test screenshots automatically attached and embedded
- Comprehensive failure diagnostics

#### Sharing with Stakeholders
The `reports/latest/` directory is committed to the repository and can be:
- Opened directly in any browser (no installation required)
- Shared via email or file sharing
- Viewed offline (all assets bundled)
- Attached to pull requests or JIRA tickets

**Note:** The `logs/` directory is NOT committed (contains sensitive internal logs). Only the sanitized HTML report is shared.

### Available TestNG Suites

- `testng.xml` - Full cross-browser suite (Chromium + Firefox + WebKit in parallel)
- `testng-chromium.xml` - All tests on Chromium only
- `testng-smoke.xml` - Smoke tests on Chromium only
- `testng-multi-browser-smoke.xml` - Smoke tests on all 3 browsers
- `testng-bdd.xml` - BDD/Cucumber tests
- `multibank-suite.xml` - Comprehensive suite with specific test methods
- `run-all-tests-in-paralel-multi-browsers.xml` - All tests on all browsers

### Browser Initialization Architecture

**Browser Initialization Flow:**
```
TestNG XML Parameter
    ↓
BaseWebTest.setupBrowser(ITestContext context)
    ↓
Extract browser: context.getCurrentXmlTest().getParameter("browser")
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
- `ITestContext` provides thread-safe parameter extraction
- Parallel execution is fully supported without parameter conflicts

### Multi-Browser Testing Best Practices

1. Use TestNG XML for full suite execution (Approach 1)
2. Use single-browser suite for local development (Approach 2)
3. Use ITestContext for browser parameterization (already implemented in BaseWebTest)
4. Enable headless mode in CI/CD pipelines (`headless=true`)
5. Let RetryListener handle flaky tests (no manual retry configuration needed)

### Related Files

- **BrowserType Enum:** `src/main/java/com/automation/enums/BrowserType.java`
- **BrowserFactory:** `src/main/java/com/automation/factory/BrowserFactory.java`
- **BaseWebTest:** `src/main/java/com/automation/base/BaseWebTest.java`
- **RetryAnalyzer:** `src/main/java/com/automation/listeners/RetryAnalyzer.java`
- **RetryListener:** `src/main/java/com/automation/listeners/RetryListener.java`
- **TestListener:** `src/main/java/com/automation/listeners/TestListener.java`
- **TestNG Suites:** `src/test/resources/testng/`

## CI/CD Integration

### GitHub Actions Workflows

The framework includes three automated CI/CD workflows:

#### 1. test-automation.yml
**Purpose:** Main test automation workflow
**Triggers:** Push to main/develop, pull requests, manual dispatch
**Jobs:**
- Smoke Tests (Chromium) - Fast feedback on critical functionality
- Cross-Browser Tests (3 browsers in parallel) - Full test coverage
- Full Suite (on main branch only) - Complete test execution

**Features:**
- Automatic Playwright browser installation
- Test result artifact upload (retained for 7-30 days)
- Allure report generation and upload
- Screenshot capture on failure
- Test result publishing

#### 2. pr-checks.yml
**Purpose:** Pull request validation
**Triggers:** Pull requests to main/develop
**Jobs:**
- Quick smoke test execution on Chromium
- Automatic PR comment with test results
- Fast feedback for code review

**Features:**
- 10-minute timeout for quick feedback
- Automatic GitHub comment with pass/fail status
- Test result artifact upload

#### 3. nightly-tests.yml
**Purpose:** Scheduled comprehensive testing
**Triggers:** Daily at 2 AM UTC, manual dispatch
**Jobs:**
- Full cross-browser test suite execution
- Extended timeout (90 minutes)
- Automatic issue creation on failure

**Features:**
- Complete test coverage across all browsers
- Extended test result retention (30 days)
- Automatic GitHub issue creation for failures
- Comprehensive artifact upload (results + screenshots)

### Running Tests in CI/CD

Tests run automatically on:
- Every push to main or develop branches
- Every pull request to main or develop
- Scheduled daily at 2 AM UTC
- Manual workflow dispatch via GitHub Actions UI

## Character Frequency Utility

### Overview
A utility class for counting character occurrences in strings while following specific rules.

### Location
- **Utility Class:** `src/main/java/com/automation/utils/CharacterFrequencyUtil.java`
- **Test Class:** `src/test/java/com/automation/utils/CharacterFrequencyUtilTests.java`
- **Documentation:** `docs/character-frequency-utility.md`

### Rules Applied
- Case-insensitive (A and a are treated as the same)
- Ignores spaces and whitespace
- Ignores punctuation and special characters
- Only counts letters and digits
- Preserves order of first appearance

### Usage Example
```java
Map<Character, Integer> result =
    CharacterFrequencyUtil.getCharFrequency("Hello, World!");

String formatted =
    CharacterFrequencyUtil.formatFrequency(result);

System.out.println(formatted);
// Output: h:1, e:1, l:3, o:2, w:1, r:1, d:1
```

### Test Coverage
- 12 comprehensive test cases covering all edge cases
- 100% pass rate
- O(n) time complexity, O(k) space complexity

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
5. **Proper Wait Strategies** - Explicit waits with Playwright auto-waiting, no fixed sleeps
6. **External Test Data** - JSON files for all test data
7. **Comprehensive Logging** - SLF4J with DEBUG/INFO/WARN/ERROR levels
8. **Fluent Assertions** - AssertJ assertions with custom messages
9. **Allure Annotations** - @Epic, @Feature, @Story, @Severity for reporting
10. **Data-Driven Testing** - TestNG DataProviders for parameterized tests
11. **Exception Handling** - Try-catch with logging, no silent failures
12. **Cross-Browser Support** - Browser parameterization via TestNG with ITestContext
13. **Fresh Test State** - Page objects initialized in @BeforeMethod hooks
14. **Configurable Timeouts** - All waits use config.properties timeout values
15. **Type-Safe Enums** - BrowserType enum for compile-time browser validation
16. **Automatic Retry** - RetryListener for handling flaky tests
17. **Screenshot on Failure** - Automatic capture and Allure attachment

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
- Verify ITestContext is properly extracting browser parameter

### RetryAnalyzer not working
- Verify `RetryListener` is in TestNG suite `<listeners>` section
- Check logs for "RetryAnalyzer attached to test" messages (DEBUG level)
- Ensure `RetryListener` is properly registered in all TestNG suite files

### Screenshots not appearing in Allure report
- Verify TestListener is registered in pom.xml and TestNG suite
- Check that tests are actually failing (screenshots only on failure)
- Ensure Allure.addAttachment is being called (check TestListener.java)
- Verify allure-results directory has screenshot attachments

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
- ITestContext-based thread-safe browser parameterization

**Parallel Execution**
- Thread-safe browser management with ThreadLocal
- Configurable thread count (suite and test level)
- Independent test execution
- Concurrent browser sessions

**Wait Strategies**
- Playwright auto-waiting for actionability
- Page load waits (NETWORKIDLE, DOMCONTENTLOADED, LOAD)
- Element visibility waits with explicit timeouts (10-15 seconds)
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
- Screenshots embedded in Allure reports
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
