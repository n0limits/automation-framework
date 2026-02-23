# Core Automation Framework

Version 2.2

**Created by:** Victor Grozev

Enterprise-grade test automation framework built with Playwright, RestAssured, TestNG, Cucumber, and Maven. Provides comprehensive testing capabilities for web UI, BDD, REST APIs, databases, AWS integrations, mobile device emulation, cloud testing, and performance testing with advanced reporting and analytics.

---

## Table of Contents

- [Overview](#overview)
- [Framework Architecture](#framework-architecture)
- [Architecture Reference](#architecture-reference)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Test Suites Reference](#test-suites-reference)
- [Self-Healing Locators](#self-healing-locators)
- [BDD / Cucumber Integration](#bdd--cucumber-integration)
- [Web UI Testing](#web-ui-testing)
- [API Testing](#api-testing)
- [Database Testing](#database-testing)
- [Performance Testing](#performance-testing)
- [Mobile Testing](#mobile-testing)
- [Cloud Testing](#cloud-testing)
- [AWS Integration](#aws-integration)
- [Test Data Generation](#test-data-generation)
- [Reporting and Metrics](#reporting-and-metrics)
- [Design Patterns](#design-patterns)
- [Playwright Lifecycle](#playwright-lifecycle)
- [Retry Mechanism](#retry-mechanism)
- [Tech Stack](#tech-stack)
- [Troubleshooting](#troubleshooting)

---

## Overview

The Core Automation Framework is a production-ready test automation solution that supports multiple testing layers and provides a unified approach to quality assurance. It targets the MultiBank trading platform (`https://trade.multibank.io`) by default, but is designed to be reused against any web application by changing the configuration.

### Capabilities at a Glance

**Multi-Layer Testing:**
- Web UI Testing (Playwright with Page Object Model)
- BDD Testing (Cucumber with Gherkin feature files)
- Mobile Web Testing (iOS and Android device emulation)
- REST API Testing (RestAssured)
- Database Testing (MongoDB, MySQL, PostgreSQL)
- Performance and Load Testing
- AWS Step Functions Integration

**Advanced Features:**
- Self-healing locators with automatic fallback strategies
- Multi-browser support (Chromium, Firefox, WebKit)
- Cross-browser parallel execution (configurable thread count)
- Mobile device emulation (8 devices: iPhone, iPad, Galaxy, Pixel)
- Cloud testing integration (BrowserStack, Sauce Labs)
- Playwright tracing for failure debugging
- Automatic retry mechanism for flaky tests (IRetryAnalyzer)
- Comprehensive Allure reporting
- Environment-based configuration (local, dev, QA, staging, prod)
- Thread-safe parallel execution via ThreadLocal pattern
- Fluent assertion APIs for UI and database validation
- Realistic test data generation with JavaFaker

---

## Framework Architecture

### Architectural Layers

```
+-------------------------------------------------------------+
|                      Test Layer                              |
|  UI Tests | BDD Steps | API Tests | DB Tests | Perf Tests   |
+-------------------------------------------------------------+
                            |
+-------------------------------------------------------------+
|               Page Objects & Utilities                       |
|  Page Objects | Self-Healing Locators | API Clients          |
|  DB Utils | Assertions | Test Data Builders                 |
+-------------------------------------------------------------+
                            |
+-------------------------------------------------------------+
|              Core Framework Components                       |
|  Base Classes | PlaywrightManager | BrowserFactory           |
|  ConfigReader | Listeners | Reporters | Cucumber Hooks       |
+-------------------------------------------------------------+
                            |
+-------------------------------------------------------------+
|            External Libraries & Tools                        |
|  Playwright | RestAssured | TestNG | Cucumber | MongoDB      |
|  MySQL | PostgreSQL | AWS SDK | Allure | JavaFaker           |
+-------------------------------------------------------------+
```

### Design Patterns Implemented

- **Page Object Model (POM)** -- UI test organization and maintainability
- **Template Method Pattern** -- Lifecycle extension hooks (`performAdditionalSetup()`, `performAdditionalAPISetup()`, `performAdditionalAWSSetup()`) let subclasses inject behaviour without overriding `@BeforeMethod` / `@AfterMethod`
- **Factory Pattern** -- Browser creation (BrowserFactory, CloudBrowserFactory)
- **Builder Pattern** -- Test data construction (TestDataBuilder, PersonDataBuilder, etc.), SQL queries (QueryBuilder), self-healing locators (SelfHealingLocator)
- **Singleton Pattern** -- Configuration (TestConfig), metrics (TestMetrics)
- **ThreadLocal Pattern** -- Thread-safe parallel execution (PlaywrightManager stores Playwright, Browser, BrowserContext, Page per thread)
- **Fluent Interface Pattern** -- Chainable assertions (UIAssertions, DatabaseAssertions), data builders

---

## Architecture Reference

For deep internals — class hierarchy with lifecycle ordering, thread safety model, extension points, DataProvider architecture, wait strategy rationale, test data isolation patterns, design patterns reference, retry mechanism, and parallelism constraints — see **[ARCHITECTURE.md](ARCHITECTURE.md)**.

---

## Project Structure

```
automation-framework/
|
+-- src/
|   +-- main/java/com/automation/
|   |   |
|   |   +-- api/                              # API Testing Layer
|   |   |   +-- APIClient.java                # RestAssured base client
|   |   |   +-- APIAssertions.java            # API response assertions
|   |   |   +-- SchemaValidator.java          # JSON schema validation
|   |   |   +-- TradingAPIClient.java         # Trading API methods
|   |   |
|   |   +-- aws/                              # AWS Integration
|   |   |   +-- AWSClientManager.java         # AWS client lifecycle
|   |   |   +-- StepFunctionsClient.java      # Step Functions client
|   |   |   +-- StepFunctionsExecutionPoller.java # Async polling
|   |   |
|   |   +-- base/                             # Base Test Classes
|   |   |   +-- BaseTest.java                 # Suite-level setup/teardown
|   |   |   +-- BaseWebTest.java              # Web UI base (Playwright lifecycle)
|   |   |   +-- BaseMultibankTest.java        # MultiBank base (shared page objects, scroll helpers, assertions)
|   |   |   +-- BaseAPITest.java              # API test base
|   |   |   +-- BaseAWSTest.java              # AWS test base
|   |   |
|   |   +-- config/                           # Configuration
|   |   |   +-- ConfigReader.java             # Multi-env properties loader
|   |   |   +-- ConfigValidator.java          # Config validation
|   |   |   +-- TestConfig.java               # Singleton config access
|   |   |
|   |   +-- database/                         # Database Testing
|   |   |   +-- ConnectionPoolManager.java    # HikariCP connection pooling
|   |   |   +-- DatabaseAssertions.java       # Fluent DB assertions
|   |   |   +-- DatabaseConnection.java       # Connection interface
|   |   |   +-- DatabaseConnectionFactory.java # (Deprecated) Legacy connection factory
|   |   |   +-- DatabaseTestUtils.java        # JDBC utility methods
|   |   |   +-- DatabaseType.java             # DB type enum
|   |   |   +-- MongoDBConnection.java        # MongoDB implementation
|   |   |   +-- QueryBuilder.java             # Fluent SQL query builder
|   |   |   +-- SQLConnection.java            # SQL implementation
|   |   |   +-- TestDataBuilder.java          # Fluent test data insertion
|   |   |
|   |   +-- enums/                            # Type-Safe Enums
|   |   |   +-- BrowserType.java              # chromium, firefox, webkit
|   |   |   +-- CloudProvider.java            # local, browserstack, saucelabs
|   |   |   +-- DeviceType.java               # Mobile device profiles
|   |   |   +-- NetworkCondition.java         # Network throttling profiles
|   |   |
|   |   +-- factory/                          # Factory Classes
|   |   |   +-- BrowserFactory.java           # Browser/device creation
|   |   |   +-- CloudBrowserFactory.java      # BrowserStack/Sauce Labs
|   |   |   +-- PageFactory.java              # Page object creation
|   |   |
|   |   +-- listeners/                        # TestNG Listeners
|   |   |   +-- RetryAnalyzer.java            # IRetryAnalyzer (max 2 retries)
|   |   |   +-- RetryListener.java            # Auto-attaches retry to all tests
|   |   |   +-- TestListener.java             # Test event listener
|   |   |
|   |   +-- pages/                            # Page Objects
|   |   |   +-- BasePage.java                 # Base page (30+ utility methods)
|   |   |   +-- LoginPage.java                # Login page
|   |   |   +-- AccountPage.java              # Account management
|   |   |   +-- TradingDashboardPage.java     # Trading dashboard
|   |   |   +-- multibank/                    # MultiBank-specific pages
|   |   |       +-- NavigationPage.java
|   |   |       +-- TradingPage.java
|   |   |       +-- FooterPage.java
|   |   |       +-- AboutUsPage.java
|   |   |
|   |   +-- performance/                      # Performance Testing
|   |   |   +-- MetricsCollector.java         # Thread-safe metrics
|   |   |   +-- PerformanceResult.java        # Result container
|   |   |   +-- PerformanceTestBase.java      # Load test base class
|   |   |
|   |   +-- reporting/                        # Reporting
|   |   |   +-- TestMetrics.java              # Singleton metrics collector
|   |   |
|   |   +-- testdata/                         # Test Data Generation
|   |   |   +-- TestDataFactory.java          # JavaFaker integration
|   |   |   +-- PersonDataBuilder.java        # Person data builder
|   |   |   +-- UserDataBuilder.java          # User data builder
|   |   |   +-- AddressDataBuilder.java       # Address data builder
|   |   |   +-- OrderDataBuilder.java         # Order data builder
|   |   |
|   |   +-- ui/                               # UI Utilities
|   |   |   +-- UIAssertions.java             # Fluent UI assertions
|   |   |
|   |   +-- utils/                            # Utility Classes
|   |       +-- PlaywrightManager.java        # ThreadLocal Playwright management
|   |       +-- SelfHealingLocator.java       # Self-healing locator with fallbacks
|   |       +-- MobileUtils.java              # Mobile gestures (20+ methods)
|   |       +-- FileUtils.java                # File/screenshot operations
|   |       +-- TestDataReader.java           # JSON test data reader
|   |
|   +-- main/resources/
|   |   +-- application.properties            # Default configuration
|   |   +-- application-local.properties      # Local environment
|   |   +-- application-dev.properties        # Development environment
|   |   +-- application-qa.properties         # QA environment
|   |   +-- application-staging.properties    # Staging environment
|   |   +-- application-prod.properties       # Production environment
|   |
|   +-- test/java/com/automation/
|   |   |
|   |   +-- api/                              # API Tests
|   |   |   +-- APIClientTest.java
|   |   |   +-- TradingAPITests.java
|   |   |
|   |   +-- aws/                              # AWS Tests
|   |   |   +-- StepFunctionsWorkflowTests.java
|   |   |
|   |   +-- bdd/                              # BDD / Cucumber
|   |   |   +-- context/
|   |   |   |   +-- ScenarioContext.java       # Shared state between steps
|   |   |   +-- hooks/
|   |   |   |   +-- CommonHooks.java           # Shared hooks
|   |   |   |   +-- UIHooks.java               # Browser lifecycle for @ui
|   |   |   |   +-- APIHooks.java              # API setup for @api
|   |   |   |   +-- DatabaseHooks.java         # DB setup for @database
|   |   |   +-- runners/
|   |   |   |   +-- CucumberTestRunner.java    # Main runner (all features)
|   |   |   |   +-- UITestRunner.java          # UI features only
|   |   |   |   +-- APITestRunner.java         # API features only
|   |   |   |   +-- SmokeTestRunner.java       # Smoke features only
|   |   |   |   +-- DatabaseTestRunner.java    # Database features only
|   |   |   +-- steps/
|   |   |       +-- CommonSteps.java           # Shared step definitions
|   |   |       +-- NavigationSteps.java       # Navigation steps
|   |   |       +-- TradingSteps.java          # Trading page steps
|   |   |       +-- PageSectionsSteps.java     # Scroll/footer interaction steps
|   |   |       +-- CrossPageSteps.java        # Browser history/URL storage steps
|   |   |       +-- TradingAPISteps.java       # API step definitions
|   |   |       +-- DatabaseSteps.java         # Database step definitions
|   |   |
|   |   +-- config/                           # Config Tests
|   |   |   +-- ConfigurationTest.java
|   |   |
|   |   +-- database/                         # Database Tests
|   |   |   +-- UserDatabaseTests.java
|   |   |   +-- DataIntegrityTests.java
|   |   |
|   |   +-- integration/                      # Integration Tests
|   |   |   +-- TradingWorkflowIntegrationTests.java
|   |   |
|   |   +-- mobile/                           # Mobile Tests
|   |   |   +-- MobileGesturesExampleTests.java
|   |   |
|   |   +-- multibank/                        # MultiBank Platform Tests
|   |   |   +-- SmokeTests.java               # 3 interactive smoke tests
|   |   |   +-- NavigationTests.java          # 4 navigation tests (DataProvider)
|   |   |   +-- TradingTests.java             # 5 trading table tests (DataProvider)
|   |   |   +-- PageSectionsTests.java        # 4 scroll-and-interact tests
|   |   |   +-- CrossPageNavigationTests.java # 3 multi-page flow tests
|   |   |
|   |   +-- performance/                      # Performance Tests
|   |   |   +-- APIPerformanceTests.java
|   |   |
|   |   +-- providers/                        # Data Providers
|   |   |   +-- TestDataProviders.java
|   |   |
|   |   +-- ui/                               # UI Tests (require login credentials)
|   |       +-- LoginUITests.java              # 1 sample login test (commented @Test)
|   |       +-- AccountUITests.java            # 1 sample account test (commented @Test)
|   |       +-- TradingDashboardUITests.java   # 1 sample dashboard test (commented @Test)
|   |
|   +-- test/resources/
|       +-- features/                         # Cucumber Feature Files
|       |   +-- ui/
|       |   |   +-- homepage.feature          # Interactive UI tests (10 scenarios)
|       |   +-- api/
|       |   |   +-- trading_api.feature
|       |   +-- database/
|       |       +-- database.feature
|       |
|       +-- testng/                           # TestNG Suite XMLs
|       |   +-- testng.xml                    # Default cross-browser suite
|       |   +-- testng-bdd.xml                # BDD/Cucumber suite
|       |   +-- testng-chromium.xml           # Chromium-only suite
|       |   +-- testng-smoke.xml              # Smoke tests
|       |   +-- testng-multi-browser-smoke.xml # Multi-browser smoke
|       |   +-- testng-api.xml                # API tests
|       |   +-- testng-aws.xml                # AWS tests
|       |   +-- testng-integration.xml        # Integration tests
|       |   +-- multibank-suite.xml           # MultiBank full suite
|       |   +-- run-all-tests-in-paralel-multi-browsers.xml # All tests, 3 browsers
|       |
|       +-- testdata/                         # Test data JSON files
|
+-- pom.xml                                  # Maven configuration
+-- README.md                                # This file
+-- ARCHITECTURE.md                          # Developer reference (class hierarchy, lifecycle, thread safety)
```

---

## Getting Started

### Prerequisites

**Required:**
- Java 21 or higher
- Maven 3.8+

**Optional (for specific test types):**
- MongoDB 5.0+ (for database tests)
- MySQL 8.0+ or MariaDB (for database tests)
- PostgreSQL 14+ (for database tests)
- AWS credentials (for Step Functions tests)
- BrowserStack or Sauce Labs account (for cloud testing)

### Installation

**1. Clone the repository:**

```bash
git clone <repository-url>
cd automation-framework
```

**2. Install dependencies and Playwright browsers:**

```bash
mvn clean install -DskipTests
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

The second command installs Chromium, Firefox, and WebKit browsers required for UI testing.

**3. Verify installation:**

```bash
mvn clean compile test-compile
```

All code should compile without errors.

---

## Configuration

### Environment-Based Configuration

The framework uses a hierarchical property loading strategy via `ConfigReader`:

1. Loads `application.properties` (base/default configuration)
2. Overlays `application-{env}.properties` (environment-specific overrides)
3. System properties (`-Dproperty=value`) take highest precedence

The environment is determined by:
- System property: `-Denv=qa`
- Environment variable: `ENV=qa`
- Default: `local`

**Available environment files:**

| File | Purpose |
|------|---------|
| `application.properties` | Base configuration (always loaded) |
| `application-local.properties` | Local development overrides |
| `application-dev.properties` | Development environment |
| `application-qa.properties` | QA/Testing environment |
| `application-staging.properties` | Staging/pre-production |
| `application-prod.properties` | Production environment |

### Key Configuration Properties

```properties
# ========== Web UI Configuration ==========
base.url=https://trade.multibank.io
browser=chromium                    # chromium, firefox, webkit
headless=false                      # true for CI/CD
timeout=30000                       # default element timeout (ms)
screenshot.on.failure=true

# ========== Wait Strategy ==========
wait.element.timeout=15000          # element visibility timeout (ms)
wait.short.timeout=5000             # quick visibility checks (ms)
wait.poll.interval=500              # polling interval (ms)
wait.max.poll.attempts=30

# ========== Playwright Tracing ==========
tracing.enabled=true                # saves trace files on test failure
tracing.screenshots=true
tracing.snapshots=true
tracing.sources=false
tracing.output.dir=target/traces

# ========== Self-Healing Locators ==========
self.healing.enabled=true           # enable fallback locator strategies

# ========== API Configuration ==========
api.base.url=https://api.multibank.io
api.timeout=30000

# ========== AWS Configuration ==========
aws.region=us-east-1
aws.stepfunctions.state.machine.arn=arn:aws:states:...
aws.stepfunctions.execution.timeout=300000
aws.stepfunctions.poll.interval=5000

# ========== Database Configuration ==========
# MongoDB
mongo.connection.string=mongodb://localhost:27017
mongo.database=testdb

# MySQL (password via env var SQL_PASSWORD)
sql.connection.string=jdbc:mysql://localhost:3306/testdb
sql.username=root

# PostgreSQL (password via env var POSTGRES_PASSWORD)
postgres.connection.string=jdbc:postgresql://localhost:5432/testdb
postgres.username=postgres

# ========== Connection Pool (HikariCP) ==========
db.pool.maximum.size=10
db.pool.minimum.idle=2
db.pool.connection.timeout=30000

# ========== Mobile Testing ==========
mobile.enabled=false
mobile.device=desktop               # iphone_13, galaxy_s21, pixel_7, etc.
mobile.orientation=portrait

# ========== Cloud Testing ==========
cloud.provider=local                 # local, browserstack, saucelabs
```

### Switching Environments

```bash
# Local (default)
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml

# QA
mvn clean test -Denv=qa -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml

# Staging with URL override
mvn clean test -Denv=staging -Dbase.url=https://staging2.trade.multibank.io

# Headless for CI/CD
mvn clean test -Dheadless=true -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml
```

### Accessing Configuration in Code

```java
import com.automation.config.TestConfig;

TestConfig config = TestConfig.getInstance();
String baseUrl = config.getBaseUrl();
String browser = config.getBrowser();
boolean headless = config.isHeadless();
int timeout = config.getTimeout();
```

---

## Running Tests

### Quick Reference

```bash
# BDD tests (Cucumber)
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml

# Smoke tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml

# Chromium-only tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml

# All tests across 3 browsers (Chromium, Firefox, WebKit)
mvn clean test -DsuiteXmlFile=src/test/resources/testng/run-all-tests-in-paralel-multi-browsers.xml

# Multi-browser smoke
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-multi-browser-smoke.xml

# Full MultiBank suite
mvn clean test -DsuiteXmlFile=src/test/resources/testng/multibank-suite.xml

# API tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-api.xml

# AWS tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-aws.xml

# Integration tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-integration.xml

# Run a specific test class
mvn test -Dtest=NavigationTests

# Run a specific test method
mvn test -Dtest=TradingAPITests#testGetTradingPairs

# Run with a specific browser
mvn clean test -Dbrowser=firefox -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml
```

### Parallel Execution

All suites are configured with `thread-count="2"` by default. The multi-browser suites run each browser as a separate `<test>` block, so Chromium, Firefox, and WebKit tests execute in parallel up to the thread count limit.

To change the thread count, edit the `thread-count` attribute in the desired TestNG XML file:

```xml
<suite name="Suite" parallel="methods" thread-count="4">
```

### Headless Mode

For CI/CD pipelines or environments without a display:

```bash
mvn clean test -Dheadless=true
```

Or set it in `application.properties`:

```properties
headless=true
```

---

## Test Suites Reference

| Suite XML | Description | Parallel |
|-----------|-------------|----------|
| `testng-bdd.xml` | Cucumber BDD tests | methods, 2 threads |
| `testng-chromium.xml` | Chromium-only UI tests | methods, 2 threads |
| `testng-smoke.xml` | Smoke tests (Chromium) | methods, 2 threads |
| `testng-multi-browser-smoke.xml` | Smoke across 3 browsers | methods, 2 threads |
| `run-all-tests-in-paralel-multi-browsers.xml` | All MultiBank tests across 3 browsers | methods, 2 threads |
| `multibank-suite.xml` | Full MultiBank suite (single browser) | methods, 2 threads |
| `testng.xml` | Default cross-browser suite | tests, 2 threads |
| `testng-api.xml` | API tests | methods |
| `testng-aws.xml` | AWS Step Functions tests | - |
| `testng-integration.xml` | Integration tests | - |

---

## Self-Healing Locators

The framework includes a custom self-healing locator system (`SelfHealingLocator`) built natively for Playwright. This is the Playwright equivalent of Healenium (which only supports Selenium).

### How It Works

When the primary CSS/Playwright selector fails to find a visible element, the self-healing locator automatically tries fallback strategies in order until one succeeds:

1. **Primary selector** -- the CSS or Playwright selector you provide
2. **Text-based** -- `page.getByText()`
3. **Role-based** -- `page.getByRole()`
4. **Additional CSS selectors** -- alternate CSS selectors
5. **Test ID** -- `page.getByTestId()`
6. **Placeholder** -- `page.getByPlaceholder()`
7. **Label** -- `page.getByLabel()`
8. **XPath** -- XPath expression

Every self-healing event is logged and reported in Allure, so you can see which fallback strategy was used.

### Usage -- Builder Pattern

```java
import com.automation.utils.SelfHealingLocator;
import com.microsoft.playwright.options.AriaRole;

// In a page object or step definition:
Locator element = SelfHealingLocator.create(page, "button.submit")
    .withText("Submit")
    .withRole(AriaRole.BUTTON, "Submit")
    .withCss("input[type='submit']")
    .withTestId("submit-btn")
    .find();

element.click();
```

### Usage -- Via BasePage

All page objects extending `BasePage` have a convenience method:

```java
public class MyPage extends BasePage {
    public Locator getSubmitButton() {
        return selfHeal("button.submit")
            .withText("Submit")
            .withRole(AriaRole.BUTTON, "Submit")
            .find();
    }
}
```

### Available Fallback Methods

| Method | Description |
|--------|-------------|
| `.withText(text)` | Match by visible text (partial) |
| `.withExactText(text)` | Match by exact visible text |
| `.withRole(role, name)` | Match by ARIA role and accessible name |
| `.withCss(selector)` | Match by alternate CSS selector |
| `.withTestId(testId)` | Match by `data-testid` attribute |
| `.withPlaceholder(text)` | Match by placeholder text |
| `.withLabel(text)` | Match by associated label text |
| `.withXPath(xpath)` | Match by XPath expression |

### Configuration

Enable or disable self-healing in `application.properties`:

```properties
self.healing.enabled=true   # set to false to always use primary selector only
```

---

## BDD / Cucumber Integration

The framework has full Cucumber BDD support integrated with TestNG, allowing you to write tests in Gherkin syntax.

### Feature Files

Feature files are located under `src/test/resources/features/` organized by test type:

```
features/
  +-- ui/
  |   +-- homepage.feature     # Interactive homepage tests (10 scenarios)
  +-- api/
  |   +-- trading_api.feature  # API tests
  +-- database/
      +-- database.feature     # Database tests
```

### Tags

Feature files use tags to control which hooks run:
- `@ui` -- triggers browser setup/teardown via `UIHooks`
- `@api` -- triggers API setup via `APIHooks`
- `@database` -- triggers DB setup via `DatabaseHooks`
- `@smoke` -- marks smoke test scenarios
- `@navigation` -- navigation interaction scenarios
- `@trading` -- trading table interaction scenarios
- `@sections` -- page section scroll/visibility scenarios
- `@crosspage` -- cross-page navigation scenarios

### Step Definitions

Step definitions are in `src/test/java/com/automation/bdd/steps/`:

- `CommonSteps.java` -- shared steps (page load checks, screenshots, network idle waits)
- `NavigationSteps.java` -- navigation and URL verification steps
- `TradingSteps.java` -- trading page interaction steps (tab switching, pair data, count storage)
- `PageSectionsSteps.java` -- scroll and footer interaction steps
- `CrossPageSteps.java` -- browser history (back/forward) and URL storage steps
- `TradingAPISteps.java` -- API-related BDD steps
- `DatabaseSteps.java` -- database-related BDD steps

### Scenario Context

`ScenarioContext` is a shared state container injected into all step classes via Cucumber's dependency injection. It allows steps to share data:

```java
// In one step:
scenarioContext.set("tradingPairs", pairs);

// In another step:
List<String> pairs = scenarioContext.get("tradingPairs");

// Lazy-init pattern: get existing or create and cache on first access
TradingPage tradingPage = scenarioContext.getOrCreate("tradingPage", TradingPage::new);
```

### Hooks

Hooks manage the test lifecycle per tag:

**UIHooks (@ui):**
1. Validates configuration
2. Initializes Playwright and launches browser
3. Starts tracing
4. Navigates to base URL
5. On failure: captures screenshot and saves trace
6. Tears down browser after each scenario

**APIHooks (@api):**
- Sets up API client

**DatabaseHooks (@database):**
- Sets up database connections

### Runners

Multiple TestNG runners are provided for different scopes:

| Runner | Scope |
|--------|-------|
| `CucumberTestRunner` | All features (parallel scenarios) |
| `UITestRunner` | UI features only |
| `APITestRunner` | API features only |
| `SmokeTestRunner` | Smoke features only |
| `DatabaseTestRunner` | Database features only |

### Running BDD Tests

```bash
# All BDD tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml

# With environment override
mvn clean test -Denv=qa -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml
```

### Writing a New BDD Test

**1. Create a feature file** (`src/test/resources/features/ui/my_feature.feature`):

```gherkin
@ui @smoke
Feature: My New Feature

  Scenario: Verify something works
    Given I am on the homepage
    When I wait for the page to load
    Then the page title contains "MultiBank"
    And the page body is not empty
```

**2. Implement step definitions** (or reuse existing ones from `CommonSteps`, `NavigationSteps`, etc.)

**3. Run:**

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml
```

---

## Web UI Testing

### BasePage Utility Methods

All page objects extend `BasePage`, which provides 30+ methods:

**Navigation:**
- `navigateTo(url)` -- navigate and wait for load
- `waitForPageLoad()` -- wait for page load (LOAD state)
- `reload()` -- reload page
- `getCurrentUrl()` / `getTitle()`

**Element Interactions:**
- `click(selector)` / `clickByText(text)` / `clickByRole(role, name)`
- `fill(selector, text)` / `type(selector, text, delayMs)`
- `check(selector)` / `uncheck(selector)`
- `selectByValue(selector, value)` / `selectByText(selector, text)`
- `hover(selector)` / `scrollTo(selector)`
- `pressKey(key)`

**Waiting:**
- `waitForVisible(selector)` -- returns Locator when visible
- `waitForHidden(selector)`
- `waitForElementCount(selector, count)`
- `waitForNavigation(action)` -- waits for navigation triggered by action

**Queries:**
- `isVisible(selector)` / `exists(selector)` / `isEnabled(selector)` / `isChecked(selector)`
- `getText(selector)` / `getValue(selector)` / `getAttribute(selector, attribute)`
- `getElementCount(selector)` / `containsText(text)`

**Screenshots:**
- `takeScreenshot(name)` -- full-page screenshot with timestamp

**Self-Healing:**
- `selfHeal(primarySelector)` -- returns `SelfHealingLocator` builder

**Mobile Gestures (via MobileUtils):**
- `tap(selector)` / `doubleTap(selector)` / `longPress(selector, durationMs)`
- `swipeUp()` / `swipeDown()` / `swipeLeft()` / `swipeRight()`
- `scrollToTop()` / `scrollToBottom()`
- `hideKeyboard()`
- `setPortraitOrientation(w, h)` / `setLandscapeOrientation(w, h)`

### Creating a New Page Object

```java
package com.automation.pages;

import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyPage extends BasePage {

    public MyPage() {
        super();
    }

    public boolean isHeaderVisible() {
        return selfHeal("h1.page-header")
            .withCss("[data-testid='header']")
            .withRole(com.microsoft.playwright.options.AriaRole.HEADING, "Page Header")
            .find()
            .isVisible();
    }

    public void clickSubmit() {
        click("button[type='submit']");
    }

    public String getResultText() {
        return getText(".result-message");
    }
}
```

### Creating a New UI Test

For MultiBank tests, extend `BaseMultibankTest` to get shared page objects (`navigationPage`, `tradingPage`, `footerPage`, `aboutUsPage`) and utility methods (`scrollToPercentage()`, `scrollToTop()`, `assertNavigationMenuVisible()`):

```java
package com.automation.multibank;

import com.automation.base.BaseMultibankTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("My Feature")
@Feature("My Page")
public class MyTests extends BaseMultibankTest {

    @Test(description = "Verify trading table is visible after scroll")
    @Severity(SeverityLevel.CRITICAL)
    public void testTradingTableAfterScroll() {
        scrollToPercentage(0.3);
        assertThat(tradingPage.isTradingPairsTableDisplayed()).isTrue();
        assertNavigationMenuVisible();
    }
}
```

For non-MultiBank tests, extend `BaseWebTest` directly and initialize page objects in `performAdditionalSetup()`.

### Fluent UI Assertions

```java
import com.automation.ui.UIAssertions;

UIAssertions.assertThat(page)
    .urlContains("/dashboard")
    .titleContains("Dashboard")
    .elementIsVisible(".header")
    .elementIsEnabled(".submit-btn")
    .elementTextEquals("h1", "Welcome")
    .elementCountEquals(".item", 5);
```

---

## API Testing

### APIClient

`APIClient` wraps RestAssured for REST API testing. `TradingAPIClient` extends it with trading-specific methods.

```java
import com.automation.api.TradingAPIClient;

TradingAPIClient client = new TradingAPIClient();
Response response = client.getTradingPairs();

assertThat(response.getStatusCode()).isEqualTo(200);
assertThat(response.jsonPath().getList("pairs")).isNotEmpty();
```

### JSON Schema Validation

```java
import com.automation.api.SchemaValidator;

SchemaValidator.validate(response, "schemas/trading-pairs-schema.json");
```

### API Assertions

```java
import com.automation.api.APIAssertions;

// Fluent API assertion chain
APIAssertions.assertThat(response)
    .statusCodeIs(200)
    .bodyContains("pairs");
```

### Creating an API Test

```java
package com.automation.api;

import com.automation.base.BaseAPITest;
import org.testng.annotations.Test;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class MyAPITests extends BaseAPITest {

    @Test
    public void testEndpoint() {
        Response response = apiClient.get("/my-endpoint");
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
```

---

## Database Testing

### Supported Databases

- **MongoDB** -- via native MongoDB Java driver
- **MySQL / MariaDB** -- via JDBC (MySQL Connector)
- **PostgreSQL** -- via JDBC (PostgreSQL Driver)

### Connection Management

Database connections are managed by `ConnectionPoolManager` using HikariCP for pooling. Access them through `DatabaseTestUtils` — this is the canonical entry point for all SQL tests:

```java
DatabaseTestUtils dbUtils = new DatabaseTestUtils();
// ConnectionPoolManager and HikariCP handle pooling transparently
```

> **Note:** `DatabaseConnectionFactory` is deprecated and no longer used by any active test code. Use `DatabaseTestUtils` directly.

### DatabaseTestUtils

Provides JDBC utility methods for SQL databases:

```java
DatabaseTestUtils dbUtils = new DatabaseTestUtils();

// Execute queries
ResultSet rs = dbUtils.executeQuery("SELECT * FROM users WHERE id = ?", userId);

// Insert data
dbUtils.executeUpdate("INSERT INTO users (name, email) VALUES (?, ?)", "John", "john@test.com");

// Transaction management
dbUtils.beginTransaction();
dbUtils.executeUpdate("UPDATE users SET status = 'active' WHERE id = ?", userId);
dbUtils.commitTransaction();
```

### TestDataBuilder (Fluent Data Insertion)

```java
TestDataBuilder builder = new TestDataBuilder(dbUtils);

Long userId = builder.forTable("users")
    .with("username", "testuser")
    .with("email", "test@example.com")
    .with("status", "active")
    .insert();

// Automatic cleanup of all inserted records
builder.cleanupAll();
```

### QueryBuilder (Fluent SQL)

```java
String sql = QueryBuilder.select("users")
    .columns("id", "username", "email")
    .where("status = ?", "active")
    .orderBy("created_at DESC")
    .limit(10)
    .build();
```

### Database Assertions

```java
import com.automation.database.DatabaseAssertions;

DatabaseAssertions.assertThat(dbUtils)
    .table("users").exists()
    .table("users").hasRowCount(10)
    .table("users").hasRecord("username = ?", "testuser")
    .query("SELECT * FROM users WHERE id = ?", userId)
        .returnsOneRow()
        .columnEquals("username", "testuser")
        .columnIsNotNull("created_at");
```

### Security Note

Database passwords must be set via environment variables, never committed to properties files:

```bash
export SQL_PASSWORD=your_mysql_password
export POSTGRES_PASSWORD=your_postgres_password
export MONGO_PASSWORD=your_mongo_password  # if auth required
```

---

## Performance Testing

### PerformanceTestBase

Extend `PerformanceTestBase` to write load, spike, and ramp tests:

```java
import com.automation.performance.PerformanceTestBase;
import com.automation.performance.PerformanceResult;

public class MyPerfTests extends PerformanceTestBase {

    @Test
    public void testLoadCapacity() {
        PerformanceResult result = executeLoadTest(
            () -> {
                Response response = apiClient.getTradingPairs();
                return response.getStatusCode() == 200;
            },
            10,   // concurrent users
            10    // iterations per user
        );

        assertThat(result.getAverageResponseTime()).isLessThan(1000.0);
        assertThat(result.getP95ResponseTime()).isLessThan(2000.0);
        assertThat(result.getSuccessRate()).isGreaterThan(95.0);
    }
}
```

### Metrics Collected

- Average, min, max response times
- Percentiles: P50, P95, P99
- Success/failure counts and rates
- Throughput (requests per second)
- Error rate

### Test Types

- **Load test** -- fixed concurrent users
- **Spike test** -- sudden traffic increases
- **Ramp test** -- gradual load increase
- **Endurance test** -- sustained load over time
- **SLA validation** -- verify response times meet thresholds

---

## Mobile Testing

### Device Emulation

The framework supports mobile web testing through Playwright device emulation. No physical devices are required.

**Supported Devices:**

| Device | Viewport | Scale | Platform |
|--------|----------|-------|----------|
| iPhone 13 | 390x844 | 3x | iOS (WebKit) |
| iPhone 13 Pro Max | 428x926 | 3x | iOS (WebKit) |
| iPhone 15 | 393x852 | 3x | iOS (WebKit) |
| iPad Pro 11 | 834x1194 | 2x | iOS (WebKit) |
| Samsung Galaxy S21 | 360x800 | 3x | Android (Chromium) |
| Samsung Galaxy S22 | 360x780 | 3x | Android (Chromium) |
| Google Pixel 7 | 412x915 | 2.625x | Android (Chromium) |
| Google Pixel 7 Pro | 412x892 | 3.5x | Android (Chromium) |

### Running Mobile Tests

```bash
# Specific device
mvn clean test -Dmobile.enabled=true -Dmobile.device=iphone_13

# Landscape orientation
mvn clean test -Dmobile.enabled=true -Dmobile.device=galaxy_s21 -Dmobile.orientation=landscape
```

### Mobile Gestures (MobileUtils)

```java
import com.automation.utils.MobileUtils;

// Swipe gestures
MobileUtils.swipeUp(page);
MobileUtils.swipeDown(page);
MobileUtils.swipeLeft(page);
MobileUtils.swipeRight(page);

// Tap gestures
MobileUtils.tap(page.locator(".button"));
MobileUtils.doubleTap(page.locator(".item"));
MobileUtils.longPress(page.locator(".item"), 1000);

// Scroll
MobileUtils.scrollToTop(page);
MobileUtils.scrollToBottom(page);

// Orientation
MobileUtils.setLandscapeOrientation(page, 390, 844);
MobileUtils.setPortraitOrientation(page, 390, 844);

// Keyboard
MobileUtils.hideKeyboard(page);

// Pinch zoom
MobileUtils.pinchZoomIn(page);
MobileUtils.pinchZoomOut(page);
```

### Network Throttling

Simulate network conditions via `NetworkCondition` enum:

| Profile | Download | Upload | Latency |
|---------|----------|--------|---------|
| WIFI | 30 Mbps | 15 Mbps | 20ms |
| FAST_4G | 4 Mbps | 3 Mbps | 20ms |
| REGULAR_4G | 2 Mbps | 1.75 Mbps | 30ms |
| REGULAR_3G | 750 Kbps | 250 Kbps | 100ms |
| SLOW_3G | 400 Kbps | 400 Kbps | 400ms |
| SLOW_2G | 250 Kbps | 50 Kbps | 2000ms |
| OFFLINE | 0 | 0 | - |
| NONE | No throttling (default) | - | - |

---

## Cloud Testing

### Supported Providers

- **BrowserStack** -- real device and browser testing
- **Sauce Labs** -- cross-browser and mobile testing
- **Local** -- standard local execution (default)

### BrowserStack

```bash
# Set credentials (use environment variables)
export CLOUD_USERNAME="your_browserstack_username"
export CLOUD_ACCESS_KEY="your_browserstack_access_key"

# Run tests on BrowserStack
mvn clean test -Dcloud.provider=browserstack -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml
```

### Sauce Labs

```bash
export CLOUD_USERNAME="your_saucelabs_username"
export CLOUD_ACCESS_KEY="your_saucelabs_access_key"

mvn clean test -Dcloud.provider=saucelabs -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml
```

### Configuration

```properties
cloud.provider=browserstack         # local, browserstack, saucelabs

# BrowserStack
browserstack.local=false
browserstack.debug=true
browserstack.console=errors
browserstack.network.logs=false

# Sauce Labs
saucelabs.region=us-west-1
saucelabs.tunnel=false
```

Cloud browsers are created by `CloudBrowserFactory`, which connects to the cloud provider's Playwright endpoint.

---

## AWS Integration

### Step Functions

The framework supports AWS Step Functions testing for workflow validation:

```java
import com.automation.aws.StepFunctionsClient;

StepFunctionsClient sfClient = new StepFunctionsClient();

// Start execution
String executionArn = sfClient.startExecution("{\"input\": \"data\"}");

// Poll for completion
String result = sfClient.waitForCompletion(executionArn);
```

`StepFunctionsExecutionPoller` handles async polling with configurable timeout and interval.

**Configuration:**

```properties
aws.region=us-east-1
aws.stepfunctions.state.machine.arn=arn:aws:states:us-east-1:123456789012:stateMachine:MyWorkflow
aws.stepfunctions.execution.timeout=300000   # 5 minutes
aws.stepfunctions.poll.interval=5000         # 5 seconds
```

AWS credentials should be set via environment variables:

```bash
export AWS_ACCESS_KEY_ID=your_key
export AWS_SECRET_ACCESS_KEY=your_secret
export AWS_SESSION_TOKEN=your_token   # optional, for assumed roles
```

---

## Test Data Generation

### TestDataFactory (JavaFaker)

Generate realistic test data:

```java
import com.automation.testdata.TestDataFactory;

TestDataFactory factory = new TestDataFactory();

String email = factory.email();
String fullName = factory.fullName();
String phone = factory.phoneNumber();
String address = factory.streetAddress();
String creditCard = factory.creditCardNumber();
```

### Fluent Data Builders

```java
// Person data
Map<String, Object> person = new PersonDataBuilder()
    .firstName("John")
    .lastName("Doe")
    .email("john@example.com")
    .build();

// User data
Map<String, Object> user = new UserDataBuilder()
    .username("john_doe")
    .email("john@example.com")
    .status("active")
    .build();

// Address data
Map<String, Object> address = new AddressDataBuilder()
    .street("123 Main St")
    .city("New York")
    .state("NY")
    .build();

// Order data
Map<String, Object> order = new OrderDataBuilder()
    .amount(99.99)
    .currency("USD")
    .status("pending")
    .build();
```

### TestNG Data Providers

`TestDataProviders` centralizes all reusable data providers for parameterized tests:

| Provider Name | Data | Used By |
|--------------|------|---------|
| `navigationItemsProvider` | Dashboard, Markets with expected URL paths | NavigationTests |
| `allNavigationItemsProvider` | All 6 nav items with expected URL paths | NavigationTests |
| `tradingSymbolsProvider` | BTCUSD, ETHUSD, XRPUSD | TradingTests, TradingAPITests |
| `tradingTabsProvider` | All Pairs, Favorites tab names | TradingTests |
| `expectedColumnsProvider` | Pair, Max Leverage, Change 24h | TradingTests |
| `authenticatedEndpointsProvider` | API endpoint paths that require authentication | TradingAPITests |
| `userDataProvider` | Randomly generated username + email pairs | UI / API tests |

`userDataProvider` is backed by `TestDataFactory` (JavaFaker) and generates unique, realistic credentials on every run:

```java
@Test(dataProvider = "userDataProvider", dataProviderClass = TestDataProviders.class)
public void testWithUser(String username, String email) {
    // username and email are freshly generated per iteration
}
```

JSON test data files are loaded via `TestDataReader`.

---

## Reporting and Metrics

### Allure Reports

The framework generates Allure HTML reports with test execution details.

**Generate and view:**

```bash
# Run tests
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-bdd.xml

# Generate and open report in browser
mvn allure:serve

# Or generate static report
mvn allure:report
# Report at: target/site/allure-maven-plugin/index.html
```

**Report features:**
- Test execution overview with pass/fail/skip statistics
- Step-by-step test execution details
- Screenshot attachments on failure
- Playwright trace file attachments on failure
- Test categorization by Epic, Feature, Story
- Historical trends
- Failure diagnostics with stack traces
- Self-healing locator events

### Allure Annotations

Use Allure annotations in test classes for better report organization:

```java
@Epic("Trading Platform")
@Feature("Trading Dashboard")
@Story("View Trading Pairs")
@Severity(SeverityLevel.CRITICAL)
@Test(description = "Verify trading pairs table is visible")
public void testTradingPairsVisible() { ... }
```

### TestMetrics

The `TestMetrics` singleton collects runtime metrics:

```java
TestMetrics metrics = TestMetrics.getInstance();
double passRate = metrics.getPassRate();
double avgDuration = metrics.getAverageDuration();
List<Map.Entry<String, Integer>> topFailures = metrics.getTopFailures(5);
```

### Playwright Tracing

When tracing is enabled, the framework saves Playwright trace files on test failure. These can be viewed in the Playwright Trace Viewer:

```bash
npx playwright show-trace target/traces/MyTest_timestamp.zip
```

Traces include screenshots, DOM snapshots, and network requests -- extremely useful for debugging failures.

---

## Playwright Lifecycle

Understanding how Playwright is managed is important for writing tests correctly.

### The Chain

```
PlaywrightManager.initPlaywright()
    -> Playwright.create()               # creates Playwright instance
    -> stored in ThreadLocal<Playwright>

BrowserFactory.launchBrowser("chromium")
    -> playwright.chromium().launch(options)   # launches browser process
    -> browser.newContext(contextOptions)       # creates BrowserContext
    -> context.newPage()                       # creates Page
    -> all stored in ThreadLocal fields

PlaywrightManager.quitPlaywright()
    -> closes Page, BrowserContext, Browser, Playwright in order
```

### For TestNG Tests (BaseWebTest / BaseMultibankTest)

`BaseWebTest.setUp()` (annotated `@BeforeMethod`) calls `PlaywrightManager.initPlaywright()` and `BrowserFactory.launchBrowser()`. `BaseWebTest.tearDown()` (`@AfterMethod`) calls `PlaywrightManager.quitPlaywright()`.

`BaseMultibankTest` extends `BaseWebTest` and adds shared page object initialization (`navigationPage`, `tradingPage`, `footerPage`, `aboutUsPage`) plus utility methods (`scrollToPercentage()`, `scrollToTop()`, `assertNavigationMenuVisible()`). All MultiBank test classes extend this base.

Each test method gets a fresh browser instance.

### For BDD / Cucumber Tests

`UIHooks.setupBrowser()` (annotated `@Before("@ui")`) handles the same lifecycle. `UIHooks.teardownBrowser()` (annotated `@After("@ui")`) handles cleanup, including trace saving on failure and screenshot capture.

### Thread Safety

All Playwright resources (Playwright, Browser, BrowserContext, Page) are stored in `ThreadLocal` fields inside `PlaywrightManager`. This means parallel test execution is safe -- each thread gets its own isolated browser instance.

### BrowserContext

`BrowserContext` is created inside `BrowserFactory.launchBrowser()`. It is configured with:
- Viewport dimensions (from device profile or defaults)
- User agent (from device profile)
- Device scale factor
- Mobile emulation settings (if enabled)
- Tracing options

Each test/scenario gets its own `BrowserContext`, so cookies, localStorage, and session state are isolated.

---

## Retry Mechanism

### IRetryAnalyzer

The framework uses TestNG's `IRetryAnalyzer` to automatically retry failed tests. This is useful for handling transient failures (network timeouts, race conditions, etc.).

**How it works:**

- `RetryAnalyzer` implements `IRetryAnalyzer` with a maximum of 2 retries
- `RetryListener` implements `IAnnotationTransformer` and automatically attaches `RetryAnalyzer` to every `@Test` method at runtime
- No manual annotation is needed -- all tests get retry capability automatically

**Configuration:**

The `RetryListener` is registered in TestNG suite XML files:

```xml
<listeners>
    <listener class-name="com.automation.listeners.RetryListener"/>
</listeners>
```

To change the maximum retry count, edit `RetryAnalyzer.java`:

```java
private static final int MAX_RETRY_COUNT = 2;  // change this value
```

---

## Tech Stack

| Technology | Purpose | Version |
|------------|---------|---------|
| Java | Programming language | 21 |
| Maven | Build and dependency management | 3.9+ |
| Playwright | Web browser automation | 1.48.0 |
| RestAssured | REST API testing | 5.5.0 |
| TestNG | Test framework and orchestration | 7.10.2 |
| Cucumber | BDD framework (Gherkin syntax) | 7.x |
| AssertJ | Fluent assertions | 3.26.3 |
| JavaFaker | Test data generation | 1.0.2 |
| MongoDB Driver | NoSQL database testing | 5.2.0 |
| MySQL Connector | MySQL database testing | 8.4.0 |
| PostgreSQL Driver | PostgreSQL database testing | 42.7.4 |
| HikariCP | Database connection pooling | - |
| AWS SDK | AWS Step Functions integration | 2.29.13 |
| Allure | Test reporting and analytics | 2.29.0 |
| SLF4J + Logback | Logging | 2.0.16 / 1.5.8 |
| Lombok | Boilerplate code reduction | 1.18.34 |
| Jackson | JSON processing | 2.18.0 |

---

## Troubleshooting

### Browser not launching

Install Playwright browsers:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

Verify browser setting:

```properties
browser=chromium   # chromium, firefox, or webkit
```

### Tests failing with timeout

Increase timeout:

```properties
timeout=60000
wait.element.timeout=30000
```

Or run headless for faster execution:

```properties
headless=true
```

### Wrong URL being used

The framework loads `application-{env}.properties` on top of `application.properties`. If tests navigate to an unexpected URL, check that the environment-specific file does not override `base.url`. The default is `local`, so `application-local.properties` is loaded by default.

```bash
# Verify which URL is being used
mvn clean test -Denv=local -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml
```

### Database connection errors

Verify database servers are running and credentials are set:

```bash
export SQL_PASSWORD=your_password
export POSTGRES_PASSWORD=your_password
```

Check connection strings in the appropriate `application-{env}.properties`.

### Compilation errors

```bash
mvn clean compile test-compile
```

Ensure Java 21:

```bash
java -version
mvn -version
```

### Allure report not generated

Ensure listeners are registered in your TestNG suite XML:

```xml
<listeners>
    <listener class-name="io.qameta.allure.testng.AllureTestNg"/>
    <listener class-name="com.automation.listeners.TestListener"/>
    <listener class-name="com.automation.listeners.RetryListener"/>
</listeners>
```

### Self-healing locator not working

Verify it is enabled:

```properties
self.healing.enabled=true
```

Check logs for `[SELF-HEALING]` entries that show which fallback strategy was attempted and which succeeded.

### Viewing Playwright traces

```bash
npx playwright show-trace target/traces/<test-name>.zip
```

Traces are only saved on test failure when `tracing.enabled=true`.

---

## Changelog

### Version 2.2 (refactoring_bdd branch)

**Test reliability, correctness, and documentation:**

- **Hollow API tests eliminated** -- Removed dual-outcome `if/else` assertions from `TradingAPITests` (`testCreateMarketOrder`, `testCreateLimitOrder`, `testCreateInvalidOrder`). Each test now asserts the single expected outcome; `testTradeHistoryPagination` was removed as a duplicate of the data-driven auth test.
- **Parallel-safe DB test data** -- All hardcoded unique constraint values in `DataIntegrityTests` (usernames, order numbers) now carry a `System.currentTimeMillis()` suffix, preventing constraint violations under concurrent or retry execution. `testGroupByQuery` uses a shared prefix threaded into both inserts and the LIKE query.
- **Integration test base class corrected** -- `TradingWorkflowIntegrationTests` now extends `BaseAWSTest` (previously `BaseTest`), inheriting the `StepFunctionsClient` lifecycle and removing duplicated setup/teardown code.
- **APIClientTest wired into framework** -- `APIClientTest` now extends `BaseAPITest`, gaining automatic `RequestSpecification` setup/teardown per test. Added `@Feature("API Client Infrastructure")` and `@Severity` annotations on all 7 tests.
- **`NETWORKIDLE` eliminated** -- All `waitForLoadState(NETWORKIDLE)` calls replaced with `waitForLoadState(LOAD)` across `BaseWebTest`, `BasePage`, `TradingDashboardPage`, `AccountPage`, `MobileGesturesExampleTests`, and `CommonSteps`. `NETWORKIDLE` caused CI timeouts on trading UIs with live WebSocket price feeds.
- **ARCHITECTURE.md added** -- New developer reference covering class hierarchy, lifecycle execution order, thread safety model, extension points, DataProvider architecture, wait strategy, test data isolation, design patterns, retry mechanism, and parallelism constraints.

### Version 2.1 (refactoring_bdd branch)

**Quality improvements and structural fixes:**

- **UI test base class corrected** -- `LoginUITests`, `AccountUITests`, and `TradingDashboardUITests` now extend `BaseWebTest` (previously `BaseTest`), ensuring Playwright is properly initialized when these tests are enabled.
- **Single DB connection system** -- Removed the legacy `DatabaseConnectionFactory` teardown from `BaseTest`. `ConnectionPoolManager` (HikariCP) is now the sole active connection mechanism. `DatabaseConnectionFactory` is marked `@Deprecated`.
- **Navigation URL assertion wired** -- `testNavigationItemFunctionality` in `NavigationTests` now asserts the URL after clicking each nav item, making the `expectedUrlPart` parameter meaningful.
- **SLF4J log format fixed** -- Corrected 13 invalid `{:.2f}` format placeholders in `APIPerformanceTests` (SLF4J only supports `{}`); values are now pre-formatted with `String.format("%.2f", ...)`.
- **TradingPage SoC restored** -- Removed hidden scroll side-effects from `isMBGTokenSectionVisible()` and `isRealWorldAssetsSectionVisible()`; visibility checks are now pure and callers control scrolling.
- **DataProvider centralization** -- `tradingTabsProvider` and `expectedColumnsProvider` moved from `TradingTests` into the shared `TestDataProviders` class; `TradingTests` now references `dataProviderClass = TestDataProviders.class`.
- **TestDataFactory wired** -- Added `userDataProvider` to `TestDataProviders`, backed by `TestDataFactory` (JavaFaker), making generated test data available to any parameterized test.
- **Hardcoded sleeps replaced** -- `page.waitForTimeout(1000)` replaced with `page.waitForLoadState()` in `SmokeTests` and `PageSectionsTests`.
- **TestDataBuilder fluent chain fixed** -- Six non-fluent `dataBuilder.forTable(); dataBuilder.with(...)` call sites in `UserDatabaseTests` corrected to proper method chaining.

---

## License

This project is licensed under the MIT License.

---

## Author

**Victor Grozev**
Framework Creator and Lead Developer

Core Automation Framework - Version 2.2

---

## Support

For questions, issues, or contributions, please open an issue in the repository issue tracker.

---

Core Automation Framework -- Enterprise-grade test automation built for reliability, scalability, and maintainability.
