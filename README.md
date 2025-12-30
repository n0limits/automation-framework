# Core Automation Framework

Version 1.0

**Created by:** Victor Grozev

Enterprise-grade test automation framework built with Playwright, RestAssured, TestNG, and Maven. Provides comprehensive testing capabilities for web UI, REST APIs, databases, and performance testing with advanced reporting and analytics.

---

## Table of Contents

- [Overview](#overview)
- [Framework Architecture](#framework-architecture)
- [Key Features](#key-features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Test Development](#test-development)
- [Reporting](#reporting)
- [Design Patterns](#design-patterns)
- [Tech Stack](#tech-stack)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

---

## Overview

The Core Automation Framework is a production-ready test automation solution that supports multiple testing layers and provides a unified approach to quality assurance. The framework has been designed with scalability, maintainability, and ease of use as core principles.

### Framework Capabilities

**Multi-Layer Testing Support:**
- Web UI Testing (Playwright with Page Object Model)
- REST API Testing (RestAssured)
- Database Testing (MongoDB, MySQL, PostgreSQL)
- Performance & Load Testing
- AWS Step Functions Integration

**Advanced Features:**
- Multi-browser support (Chromium, Firefox, WebKit)
- Sequential and parallel test execution
- Fluent assertion APIs
- Realistic test data generation
- Advanced metrics collection and analytics
- Comprehensive Allure reporting
- Automatic retry mechanism for flaky tests

**Built-in Test Suites:**
- 74+ comprehensive test cases across all layers
- UI tests with cross-browser validation
- API integration tests
- Database integrity tests
- Performance benchmarks
- AWS workflow integration tests

---

## Framework Architecture

The framework follows enterprise-grade design patterns and SOLID principles to ensure maintainability and extensibility.

### Architectural Layers

```
┌─────────────────────────────────────────────────────┐
│              Test Layer                             │
│  (UI Tests, API Tests, DB Tests, Performance Tests) │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│            Page Objects & Utilities                 │
│   (Page Objects, API Clients, DB Utils, Assertions)│
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│           Core Framework Components                 │
│ (Base Classes, Managers, Listeners, Reporters)     │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│         External Libraries & Tools                  │
│  (Playwright, RestAssured, TestNG, MongoDB, etc.)  │
└─────────────────────────────────────────────────────┘
```

### Design Patterns Implemented

- **Page Object Model (POM)** - UI test organization and maintainability
- **Factory Pattern** - Browser, page object, and database connection instantiation
- **Builder Pattern** - Fluent test data construction and SQL queries
- **Singleton Pattern** - Configuration and metrics management
- **ThreadLocal Pattern** - Thread-safe parallel test execution
- **Fluent Interface Pattern** - Chainable assertions and test data builders

---

## Key Features

### 1. Web UI Testing Infrastructure

**Playwright Integration:**
- Modern browser automation with Playwright Java API
- Support for Chromium, Firefox, and WebKit browsers
- Auto-waiting mechanisms for reliable element interactions
- Screenshot capture on test failure
- Multi-browser sequential execution for stability

**Page Object Model:**
- BasePage with 30+ reusable utility methods
- Enhanced page objects with smart waiting strategies
- Full-page and viewport screenshot capabilities
- Fluent UI assertions for readable test code
- Built-in navigation and element interaction helpers

**UI Test Coverage:**
- 44 comprehensive UI tests across multiple page objects
- Login functionality validation
- Trading dashboard operations
- Account management features
- Cross-browser compatibility testing

### 2. API Testing Infrastructure

**RestAssured Integration:**
- Full REST API testing capabilities
- Comprehensive TradingAPIClient for financial API testing
- Request/response logging and validation
- JSON schema validation
- AWS Step Functions integration

**API Test Coverage:**
- Trading API endpoints testing
- Step Functions execution and monitoring
- Integration workflow validation
- Error handling and edge cases
- Performance and response time validation

**Parallel Execution:**
- API tests run in parallel (5 threads)
- Thread-safe execution
- Maximum speed for stateless tests
- Configurable via testng-api-parallel.xml

### 3. Database Testing Support

**Multi-Database Compatibility:**
- MongoDB support with native driver
- MySQL/MariaDB support
- PostgreSQL support
- Connection pooling and management
- Factory pattern for database connections

**Database Utilities:**
- DatabaseTestUtils with 30+ utility methods
- JDBC query execution and validation
- Transaction management (commit/rollback)
- Automatic cleanup tracking
- Fluent database assertions

**Test Data Management:**
- TestDataBuilder with fluent API
- Automatic test data cleanup
- Foreign key constraint handling
- Record existence validation
- Table and column verification

**Database Test Coverage:**
- 22 database tests covering CRUD operations
- Data integrity validation
- Transaction testing
- Relationship verification

### 4. Performance & Load Testing

**Performance Testing Framework:**
- PerformanceTestBase for load testing
- MetricsCollector for thread-safe metrics gathering
- Support for concurrent user simulation
- Response time percentile calculations (P50, P95, P99)
- Throughput measurement

**Test Types Supported:**
- Load testing with configurable concurrent users
- Spike testing for sudden traffic increases
- Ramp testing for gradual load increases
- Endurance testing for sustained load
- SLA validation

**Performance Metrics:**
- Average response time
- Min/max response times
- Percentile calculations (P50, P95, P99)
- Success/failure rates
- Throughput (requests per second)
- Error rate tracking

**Performance Test Coverage:**
- 8 comprehensive performance tests
- API endpoint performance validation
- Multi-endpoint concurrent testing
- Performance consistency checks

### 5. Test Data Generation

**JavaFaker Integration:**
- TestDataFactory with 50+ data generation methods
- Realistic person data (names, emails, phones)
- Address data generation
- Financial data (credit cards, amounts)
- Company and business data
- Date and time data

**Fluent Builders:**
- PersonDataBuilder for person entities
- UserDataBuilder for user accounts
- AddressDataBuilder for addresses
- OrderDataBuilder for order data
- Chainable methods for complex data structures

### 6. Advanced Reporting & Analytics

**Test Metrics Collection:**
- Real-time test execution metrics
- Pass/fail/skip rate calculation
- Test duration tracking
- Failure reason analysis
- Category distribution
- Top failures identification
- Slowest tests tracking

**Enhanced Allure Reporting:**
- Custom EnhancedAllureListener for rich reports
- Automatic environment information
- Test metrics summary in reports
- Stack trace attachments
- Custom test metadata
- Performance metrics integration

**Allure Report Features:**
- Visual test execution overview
- Step-by-step test execution details
- Screenshot attachments on failure
- Historical trend analysis
- Test categorization (Epic, Feature, Story)
- Execution timeline
- Comprehensive failure diagnostics

### 7. Multi-Browser Testing

**Browser Support:**
- Chromium (Google Chrome, Microsoft Edge)
- Firefox (Mozilla Firefox)
- WebKit (Safari engine)

**Execution Strategies:**
- Sequential multi-browser for UI tests (stability)
- Parallel execution for API tests (performance)
- Type-safe browser selection with BrowserType enum
- Thread-safe browser management with ThreadLocal

**Configuration:**
- testng-ui-multi-browser.xml for sequential UI testing
- testng-api-parallel.xml for parallel API testing
- Browser parameterization via TestNG
- Headless mode support for CI/CD

### 8. Test Reliability Features

**Automatic Retry Mechanism:**
- RetryAnalyzer for automatic test retries
- Configurable retry count (default: 2 retries)
- RetryListener auto-attaches to all tests
- Detailed retry logging

**Smart Waiting Strategies:**
- Playwright auto-waiting for elements
- Configurable default timeouts
- Element visibility waits
- Network idle waits
- Page load waits

**Screenshot Capture:**
- Automatic screenshots on test failure
- Full-page screenshot support
- Screenshots embedded in Allure reports
- Automatic Allure attachment

---

## Project Structure

```
core-automation-framework/
│
├── src/
│   ├── main/java/com/automation/
│   │   │
│   │   ├── api/                          # API Testing Layer
│   │   │   ├── APIClient.java            # RestAssured base client
│   │   │   ├── TradingAPIClient.java     # Trading API methods
│   │   │   └── validators/
│   │   │       └── ResponseValidator.java
│   │   │
│   │   ├── aws/                          # AWS Integration
│   │   │   └── StepFunctionsClient.java  # Step Functions client
│   │   │
│   │   ├── base/                         # Base Test Classes
│   │   │   ├── BaseTest.java             # Suite-level setup
│   │   │   ├── BaseWebTest.java          # Web UI base
│   │   │   └── BaseAPITest.java          # API test base
│   │   │
│   │   ├── config/                       # Configuration
│   │   │   ├── ConfigReader.java         # Properties reader
│   │   │   └── TestConfig.java           # Singleton config
│   │   │
│   │   ├── database/                     # Database Testing
│   │   │   ├── DatabaseTestUtils.java    # JDBC utilities
│   │   │   ├── TestDataBuilder.java      # Fluent data builder
│   │   │   └── DatabaseAssertions.java   # DB assertions
│   │   │
│   │   ├── db/                           # Database Connections
│   │   │   ├── DatabaseConnectionFactory.java
│   │   │   ├── MongoDBConnection.java
│   │   │   ├── SQLConnection.java
│   │   │   └── QueryBuilder.java
│   │   │
│   │   ├── enums/                        # Type-Safe Enums
│   │   │   ├── BrowserType.java          # Browser enum
│   │   │   └── DatabaseType.java         # Database enum
│   │   │
│   │   ├── factory/                      # Factory Classes
│   │   │   ├── BrowserFactory.java       # Browser creation
│   │   │   └── PageFactory.java          # Page object creation
│   │   │
│   │   ├── listeners/                    # TestNG Listeners
│   │   │   ├── AllureTestListener.java   # Allure integration
│   │   │   ├── TestRetryListener.java    # Retry configuration
│   │   │   ├── RetryAnalyzer.java        # Retry logic
│   │   │   └── RetryListener.java        # Auto-attach retries
│   │   │
│   │   ├── pages/                        # Page Objects (UI)
│   │   │   ├── BasePage.java             # Base page (30+ methods)
│   │   │   ├── LoginPage.java            # Login page
│   │   │   ├── TradingDashboardPage.java # Trading dashboard (50+ methods)
│   │   │   ├── AccountPage.java          # Account management (40+ methods)
│   │   │   └── multibank/                # MultiBank-specific pages
│   │   │       ├── NavigationPage.java
│   │   │       ├── TradingPage.java
│   │   │       ├── FooterPage.java
│   │   │       └── AboutUsPage.java
│   │   │
│   │   ├── performance/                  # Performance Testing
│   │   │   ├── PerformanceTestBase.java  # Load test base
│   │   │   ├── MetricsCollector.java     # Metrics collection
│   │   │   └── PerformanceResult.java    # Result container
│   │   │
│   │   ├── reporting/                    # Reporting & Analytics
│   │   │   ├── TestMetrics.java          # Metrics collector
│   │   │   └── EnhancedAllureListener.java # Enhanced Allure
│   │   │
│   │   ├── testdata/                     # Test Data Generation
│   │   │   ├── TestDataFactory.java      # Faker integration (50+ methods)
│   │   │   ├── PersonDataBuilder.java    # Person data builder
│   │   │   ├── UserDataBuilder.java      # User data builder
│   │   │   ├── AddressDataBuilder.java   # Address data builder
│   │   │   └── OrderDataBuilder.java     # Order data builder
│   │   │
│   │   ├── ui/                           # UI Utilities
│   │   │   └── UIAssertions.java         # Fluent UI assertions (30+ methods)
│   │   │
│   │   └── utils/                        # Utility Classes
│   │       ├── PlaywrightManager.java    # ThreadLocal browser management
│   │       ├── WaitUtils.java            # Wait utilities
│   │       ├── FileUtils.java            # File operations
│   │       └── TestDataGenerator.java    # Data generation
│   │
│   ├── test/java/com/automation/
│   │   │
│   │   ├── api/                          # API Tests
│   │   │   └── TradingAPITests.java      # Trading API tests
│   │   │
│   │   ├── aws/                          # AWS Tests
│   │   │   └── StepFunctionsTests.java   # Step Functions tests
│   │   │
│   │   ├── database/                     # Database Tests
│   │   │   ├── UserDatabaseTests.java    # User CRUD tests (13 tests)
│   │   │   └── DataIntegrityTests.java   # Integrity tests (9 tests)
│   │   │
│   │   ├── integration/                  # Integration Tests
│   │   │   └── TradingWorkflowIntegrationTests.java
│   │   │
│   │   ├── multibank/                    # MultiBank Tests
│   │   │   ├── NavigationTests.java      # Navigation tests
│   │   │   ├── TradingTests.java         # Trading tests
│   │   │   ├── ContentValidationTests.java
│   │   │   ├── SmokeTests.java
│   │   │   └── PerformanceTests.java
│   │   │
│   │   ├── performance/                  # Performance Tests
│   │   │   └── APIPerformanceTests.java  # API performance tests (8 tests)
│   │   │
│   │   └── ui/                           # UI Tests
│   │       ├── LoginUITests.java         # Login tests (9 tests)
│   │       ├── TradingDashboardUITests.java # Dashboard tests (16 tests)
│   │       └── AccountUITests.java       # Account tests (19 tests)
│   │
│   └── resources/
│       ├── application.properties         # Main configuration
│       ├── application-local.properties   # Local environment
│       ├── application-dev.properties     # Development environment
│       ├── application-qa.properties      # QA environment
│       ├── application-staging.properties # Staging environment
│       ├── application-prod.properties    # Production environment
│       ├── logback.xml                    # Logging configuration
│       ├── testng-ui.xml                  # Single browser UI suite
│       ├── testng-ui-multi-browser.xml    # Multi-browser UI suite
│       ├── testng-api-parallel.xml        # Parallel API suite
│       ├── testng-database.xml            # Database test suite
│       ├── testng-performance.xml         # Performance test suite
│       └── testdata/                      # Test data files
│           ├── navigation-data.json
│           ├── trading-data.json
│           └── content-data.json
│
├── docs/                                  # Documentation
│   ├── MULTI_BROWSER_EXECUTION_GUIDE.md
│   ├── FINAL_IMPLEMENTATION_SUMMARY.md
│   └── character-frequency-utility.md
│
├── reports/                               # Test reports
│   └── latest/                            # Latest Allure report
│
├── pom.xml                                # Maven configuration
├── README.md                              # This file
└── .gitignore

```

---

## Getting Started

### Prerequisites

**Required Software:**
- Java 21 or higher
- Maven 3.8+
- Node.js (for Playwright browser installation)

**Optional (for database testing):**
- MongoDB 5.0+
- MySQL 8.0+ or MariaDB
- PostgreSQL 14+

### Installation

**1. Clone the repository**

```bash
git clone <repository-url>
cd core-automation-framework
```

**2. Install dependencies**

```bash
mvn clean install
```

**3. Install Playwright browsers**

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

This installs Chromium, Firefox, and WebKit browsers required for UI testing.

**4. Verify installation**

```bash
mvn clean compile test-compile
```

All code should compile successfully without errors.

---

## Configuration

### Environment-Based Configuration

The framework supports multiple environments with environment-specific property files:

- `application.properties` - Default/base configuration
- `application-local.properties` - Local development (localhost)
- `application-dev.properties` - Development environment
- `application-qa.properties` - QA/Testing environment
- `application-staging.properties` - Staging/pre-production environment
- `application-prod.properties` - Production environment

**Configuration Loading Strategy:**

The framework uses a hierarchical configuration approach:
1. Loads `application.properties` (base configuration)
2. Overlays `application-{env}.properties` (environment-specific overrides)
3. System properties take precedence over file properties
4. Environment variables can override any property

**Switching environments:**

```bash
# Local environment (default)
mvn clean test

# Development environment
mvn clean test -Denv=dev

# QA environment
mvn clean test -Denv=qa

# Staging environment
mvn clean test -Denv=staging

# Production environment
mvn clean test -Denv=prod

# Via environment variable (alternative)
export ENV=qa
mvn clean test
```

**Running specific suites against different environments:**

```bash
# UI tests on development
mvn clean test -Denv=dev -DsuiteXmlFile=testng-ui.xml

# Multi-browser tests on QA
mvn clean test -Denv=qa -DsuiteXmlFile=testng-ui-multi-browser.xml

# API tests on staging
mvn clean test -Denv=staging -DsuiteXmlFile=testng-api-parallel.xml

# Performance tests on production
mvn clean test -Denv=prod -DsuiteXmlFile=testng-performance.xml
```

**Property Precedence (highest to lowest):**
1. System Properties: `-Dbase.url=http://custom-url`
2. Environment Variables: `BASE_URL=http://custom-url`
3. Environment-specific file: `application-{env}.properties`
4. Default file: `application.properties`

**Example: Override specific property:**
```bash
# Use QA environment but override base URL
mvn clean test -Denv=qa -Dbase.url=https://qa2.trade.multibank.io
```

### Main Configuration File

Edit `src/main/resources/application.properties`:

```properties
# ========== Web UI Configuration ==========
base.url=http://localhost:3000
browser=chromium                    # chromium, firefox, webkit
headless=false                      # true for CI/CD
timeout=30000                       # milliseconds
screenshot.on.failure=true

# ========== Test Credentials ==========
test.username=testuser
test.password=testpassword

# ========== API Configuration ==========
api.base.url=https://api.example.com
api.timeout=30000

# ========== AWS Configuration ==========
aws.region=us-east-1
aws.stepfunctions.state.machine.arn=arn:aws:states:us-east-1:123456789012:stateMachine:DefaultWorkflow
aws.stepfunctions.execution.timeout=300000
aws.stepfunctions.poll.interval=5000
aws.endpoint.override=                # For LocalStack

# ========== Database Configuration ==========
# MongoDB
mongo.connection.string=mongodb://localhost:27017
mongo.database=testdb

# MySQL/MariaDB
sql.connection.string=jdbc:mysql://localhost:3306/testdb
sql.username=root
sql.password=password

# PostgreSQL
postgres.connection.string=jdbc:postgresql://localhost:5432/testdb
postgres.username=postgres
postgres.password=password

# ========== Test Data ==========
test.data.path=src/test/resources/testdata

# ========== Reporting ==========
report.path=target/reports
```

### Configuration Access in Tests

```java
import com.automation.config.TestConfig;

// Get singleton instance
TestConfig config = TestConfig.getInstance();

// Access properties
String baseUrl = config.getBaseUrl();
String browser = config.getBrowser();
boolean headless = config.isHeadless();
String apiUrl = config.getApiBaseUrl();
```

---

## Running Tests

### UI Testing

**Single Browser (Default: Chromium)**

```bash
mvn clean test -DsuiteXmlFile=testng-ui.xml
```

**Multi-Browser Sequential (Chromium, Firefox, WebKit)**

```bash
mvn clean test -DsuiteXmlFile=testng-ui-multi-browser.xml
```

This runs all UI tests sequentially across all three browsers. Tests run one browser at a time to ensure stability and prevent resource conflicts.

**Specific Browser**

```bash
mvn clean test -DsuiteXmlFile=testng-ui.xml -Dbrowser=firefox
mvn clean test -DsuiteXmlFile=testng-ui.xml -Dbrowser=webkit
```

**Why Sequential for UI Tests?**
- Prevents browser resource conflicts
- Ensures stable test execution
- Avoids Playwright context issues
- Better for debugging failures
- More reliable for cross-browser validation

### API Testing

**Parallel API Tests (5 threads)**

```bash
mvn clean test -DsuiteXmlFile=testng-api-parallel.xml
```

API tests run in parallel for maximum performance since they are stateless and don't have browser dependencies.

**Why Parallel for API Tests?**
- API tests are stateless
- No browser resource conflicts
- 60-80% faster execution
- Safe for concurrent execution

### Database Testing

```bash
mvn clean test -DsuiteXmlFile=testng-database.xml
```

Requires database servers to be running and properly configured in application.properties.

### Performance Testing

```bash
mvn clean test -DsuiteXmlFile=testng-performance.xml
```

Runs load, spike, and ramp tests to validate API performance and measure response times, throughput, and success rates.

### Running Specific Test Classes

```bash
# UI Tests
mvn test -Dtest=LoginUITests
mvn test -Dtest=TradingDashboardUITests
mvn test -Dtest=AccountUITests

# API Tests
mvn test -Dtest=TradingAPITests

# Database Tests
mvn test -Dtest=UserDatabaseTests
mvn test -Dtest=DataIntegrityTests

# Performance Tests
mvn test -Dtest=APIPerformanceTests

# MultiBank Tests
mvn test -Dtest=NavigationTests
mvn test -Dtest=TradingTests
mvn test -Dtest=ContentValidationTests
```

### Running Specific Test Methods

```bash
mvn test -Dtest=LoginUITests#testSuccessfulLogin
mvn test -Dtest=TradingAPITests#testGetTradingPairs
```

### Headless Mode

For CI/CD pipelines or running tests without browser UI:

```bash
mvn clean test -Dheadless=true
```

Or edit `application.properties`:

```properties
headless=true
```

---

## Test Development

### Creating a New UI Test

**1. Create Page Object**

```java
package com.automation.pages;

import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductPage extends BasePage {

    private final Locator productTitle;
    private final Locator addToCartButton;
    private final Locator priceLabel;

    public ProductPage() {
        super();
        this.productTitle = page.locator("h1.product-title");
        this.addToCartButton = page.locator("button#add-to-cart");
        this.priceLabel = page.locator("span.price");
    }

    public String getProductTitle() {
        return productTitle.textContent();
    }

    public void addToCart() {
        addToCartButton.click();
        log.info("Added product to cart");
    }

    public String getPrice() {
        return priceLabel.textContent();
    }
}
```

**2. Create Test Class**

```java
package com.automation.ui;

import com.automation.base.BaseTest;
import com.automation.pages.ProductPage;
import com.automation.ui.UIAssertions;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("E-Commerce")
@Feature("Product Management")
public class ProductTests extends BaseTest {

    private ProductPage productPage;

    @BeforeMethod
    public void setupTest() {
        productPage = new ProductPage();
    }

    @Test(description = "Verify product details display correctly")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Display")
    public void testProductDetailsDisplay() {
        // Navigate to product page
        productPage.open("/products/123");

        // Verify title is displayed
        String title = productPage.getProductTitle();
        UIAssertions.assertThat(page)
                .elementIsVisible("h1.product-title")
                .elementTextContains("h1.product-title", "Product");

        // Take screenshot
        productPage.takeScreenshot("product-page");
    }
}
```

### Creating a New API Test

```java
package com.automation.api;

import com.automation.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Testing")
@Feature("User Management API")
public class UserAPITests extends BaseTest {

    private TradingAPIClient apiClient;

    @BeforeMethod
    public void setupTest() {
        apiClient = new TradingAPIClient();
    }

    @Test(description = "Get user by ID returns correct user data")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get User")
    public void testGetUserById() {
        // Make API request
        Response response = apiClient.get("/users/123");

        // Validate response
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("id")).isEqualTo("123");
        assertThat(response.jsonPath().getString("name")).isNotEmpty();

        // Attach response to report
        Allure.addAttachment("API Response", "application/json",
                response.getBody().asString());
    }
}
```

### Creating a Database Test

```java
package com.automation.database;

import com.automation.base.BaseTest;
import com.automation.database.DatabaseTestUtils;
import com.automation.database.TestDataBuilder;
import com.automation.database.DatabaseAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserDatabaseTests extends BaseTest {

    private DatabaseTestUtils dbUtils;
    private TestDataBuilder builder;

    @BeforeMethod
    public void setupDatabase() throws Exception {
        dbUtils = new DatabaseTestUtils();
        builder = new TestDataBuilder(dbUtils);
    }

    @Test(description = "Create and verify user in database")
    public void testCreateUser() throws Exception {
        // Create test user
        Long userId = builder.forTable("users")
                .with("username", "testuser")
                .with("email", "test@example.com")
                .with("status", "active")
                .insert();

        // Verify user exists
        DatabaseAssertions.assertThat(dbUtils)
                .table("users").hasRecord("id = ?", userId)
                .query("SELECT * FROM users WHERE id = ?", userId)
                    .returnsOneRow()
                    .columnEquals("username", "testuser")
                    .columnEquals("email", "test@example.com")
                    .columnEquals("status", "active");
    }

    @AfterMethod
    public void cleanupDatabase() throws Exception {
        builder.cleanupAll();  // Automatic cleanup
        dbUtils.close();
    }
}
```

### Creating a Performance Test

```java
package com.automation.performance;

import com.automation.performance.PerformanceTestBase;
import com.automation.performance.PerformanceResult;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class APIPerformanceTests extends PerformanceTestBase {

    @Test(description = "API endpoint should handle 10 concurrent users")
    public void testLoadCapacity() {
        // Execute load test with 10 concurrent users, 10 iterations each
        PerformanceResult result = executeLoadTest(
            () -> {
                Response response = apiClient.getTradingPairs();
                return response.getStatusCode() == 200;
            },
            10,  // concurrent users
            10   // iterations per user
        );

        // Validate performance
        assertThat(result.getAverageResponseTime())
                .as("Average response time should be under 1000ms")
                .isLessThan(1000.0);

        assertThat(result.getP95ResponseTime())
                .as("95th percentile should be under 2000ms")
                .isLessThan(2000.0);

        assertThat(result.getSuccessRate())
                .as("Success rate should be above 95%")
                .isGreaterThan(95.0);
    }
}
```

### Using Test Data Factory

```java
import com.automation.testdata.TestDataFactory;

// Create factory instance
TestDataFactory factory = new TestDataFactory();

// Generate simple data
String email = factory.email();
String fullName = factory.fullName();
String phoneNumber = factory.phoneNumber();
String address = factory.streetAddress();

// Generate complex data with builder
Map<String, Object> user = factory.user()
        .username("john_doe")
        .email("john@example.com")
        .firstName("John")
        .lastName("Doe")
        .status("active")
        .build();

Map<String, Object> order = factory.order()
        .amount(99.99)
        .currency("USD")
        .status("pending")
        .build();
```

### Using Fluent Assertions

**UI Assertions:**

```java
import com.automation.ui.UIAssertions;

UIAssertions.assertThat(page)
        .urlContains("/dashboard")
        .titleContains("Dashboard")
        .elementIsVisible(".buy-button")
        .elementIsEnabled(".buy-button")
        .elementTextEquals("h1", "Welcome")
        .elementCountEquals(".item", 5);
```

**Database Assertions:**

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

---

## Reporting

### Allure Reports

The framework generates comprehensive Allure HTML reports with detailed test execution information.

**Generate and View Report:**

```bash
# Run tests
mvn clean test

# Generate and open report in browser
mvn allure:serve
```

**Generate Static Report:**

```bash
# Run tests and generate report
mvn clean test allure:report

# Copy to reports directory
mvn site

# Report available at: reports/latest/index.html
```

**Report Features:**
- Test execution overview with pass/fail/skip statistics
- Visual charts and graphs
- Detailed test steps with screenshots
- Test categorization by Epic, Feature, Story
- Historical trends
- Failure diagnostics with stack traces
- Environment information
- Test metrics summary
- Performance data

### Test Metrics

The framework automatically collects test execution metrics:

```java
// Access metrics programmatically
TestMetrics metrics = TestMetrics.getInstance();

// Get statistics
double passRate = metrics.getPassRate();
double averageDuration = metrics.getAverageDuration();
List<Map.Entry<String, Integer>> topFailures = metrics.getTopFailures(5);
List<Map.Entry<String, Double>> slowestTests = metrics.getSlowestTests(5);

// Print report
metrics.printReport();
```

Metrics are automatically attached to Allure reports via EnhancedAllureListener.

---

## Design Patterns

### 1. Page Object Model (POM)

Separates page structure from test logic for better maintainability.

**BasePage provides:**
- Common navigation methods
- Wait utilities
- Element interaction helpers
- Screenshot capabilities

**Page Objects extend BasePage:**
- Define page-specific locators
- Implement page-specific actions
- Return `this` for method chaining

### 2. Factory Pattern

**BrowserFactory:**
- Creates browser instances with proper configuration
- Type-safe browser selection via BrowserType enum
- Handles browser-specific settings

**DatabaseConnectionFactory:**
- Creates database connections based on DatabaseType
- Connection pooling
- Resource management

### 3. Builder Pattern

**TestDataBuilder:**
- Fluent API for building test data
- Automatic cleanup tracking
- Method chaining for readable code

**QueryBuilder:**
- Dynamic SQL query construction
- Prevents SQL injection

### 4. Singleton Pattern

**TestConfig:**
- Single configuration instance
- Thread-safe access
- Environment-specific properties

**TestMetrics:**
- Single metrics collector
- Thread-safe metrics gathering
- Real-time statistics

### 5. Fluent Interface Pattern

**UIAssertions and DatabaseAssertions:**
- Chainable assertion methods
- Readable test code
- Custom error messages

---

## Tech Stack

| Technology | Purpose | Version |
|------------|---------|---------|
| **Java** | Programming language | 21 |
| **Maven** | Build and dependency management | 3.9+ |
| **Playwright** | Web browser automation | 1.48.0 |
| **RestAssured** | REST API testing | 5.5.0 |
| **TestNG** | Test framework and orchestration | 7.10.2 |
| **AssertJ** | Fluent assertions | 3.26.3 |
| **JavaFaker** | Test data generation | 1.0.2 |
| **MongoDB Driver** | NoSQL database testing | 5.2.0 |
| **MySQL Connector** | MySQL database testing | 8.4.0 |
| **PostgreSQL Driver** | PostgreSQL database testing | 42.7.4 |
| **AWS SDK** | AWS Step Functions integration | 2.29.13 |
| **Allure** | Test reporting and analytics | 2.29.0 |
| **SLF4J** | Logging facade | 2.0.16 |
| **Logback** | Logging implementation | 1.5.8 |
| **Lombok** | Boilerplate code reduction | 1.18.34 |
| **Jackson** | JSON processing | 2.18.0 |

---

## Best Practices

The framework demonstrates these industry best practices:

### Test Design
1. **Test Independence** - Each test runs standalone without dependencies on other tests
2. **AAA Pattern** - Arrange, Act, Assert structure for clarity
3. **Meaningful Names** - Descriptive test method names that explain what is being tested
4. **Single Responsibility** - Each test validates one specific behavior

### Code Organization
5. **Page Object Model** - All locators encapsulated in page objects, never in tests
6. **DRY Principle** - No code duplication, reusable utilities and base classes
7. **SOLID Principles** - Single responsibility, open/closed, dependency inversion

### Wait Strategies
8. **Explicit Waits** - Use Playwright's auto-waiting and explicit timeout configurations
9. **No Hard Sleeps** - Never use Thread.sleep(), always use smart waiting

### Test Data
10. **External Test Data** - JSON files and data generation for all test data
11. **Data-Driven Testing** - TestNG DataProviders for parameterized tests
12. **Realistic Data** - JavaFaker for realistic test data generation
13. **Automatic Cleanup** - Database test data cleaned up automatically

### Assertions
14. **Fluent Assertions** - AssertJ with custom error messages
15. **Comprehensive Validation** - Validate all aspects of expected behavior

### Reporting
16. **Allure Annotations** - @Epic, @Feature, @Story, @Severity for categorization
17. **Screenshot on Failure** - Automatic capture and report attachment
18. **Comprehensive Logging** - DEBUG/INFO/WARN/ERROR levels throughout

### Reliability
19. **Automatic Retry** - RetryListener handles flaky tests automatically
20. **Thread Safety** - ThreadLocal for parallel execution support
21. **Resource Management** - Proper cleanup in @AfterMethod/@AfterClass hooks

### Configuration
22. **Environment-Specific Config** - Separate properties files for each environment
23. **Externalized Configuration** - No hardcoded values in test code
24. **Type-Safe Access** - Configuration accessed via TestConfig singleton

---

## Troubleshooting

### Common Issues and Solutions

**Browser not launching**

Check that Playwright browsers are installed:
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

Verify browser setting in application.properties:
```properties
browser=chromium  # chromium, firefox, or webkit
```

**Tests failing with timeout**

Increase timeout in application.properties:
```properties
timeout=60000  # milliseconds
```

Or use headless mode for faster execution:
```properties
headless=true
```

**Database connection errors**

Verify database is running:
```bash
# MongoDB
systemctl status mongod

# MySQL
systemctl status mysql

# PostgreSQL
systemctl status postgresql
```

Check connection strings in application.properties match your database configuration.

**Compilation errors**

Clean and recompile:
```bash
mvn clean compile test-compile
```

Ensure Java 21 is being used:
```bash
java -version
mvn -version
```

**Tests not running in parallel**

Check TestNG suite configuration has parallel attribute:
```xml
<suite name="API Suite" parallel="tests" thread-count="5">
```

Ensure tests are in different `<test>` blocks for parallel execution.

**Screenshots not in Allure report**

Verify TestListener is registered in testng.xml:
```xml
<listeners>
    <listener class-name="com.automation.listeners.AllureTestListener"/>
    <listener class-name="com.automation.reporting.EnhancedAllureListener"/>
</listeners>
```

Check that screenshot.on.failure is enabled:
```properties
screenshot.on.failure=true
```

**Performance tests showing poor results**

Ensure adequate system resources (CPU, memory) are available.

Check that no other resource-intensive applications are running.

Verify network latency to API endpoints is acceptable.

Consider adjusting performance thresholds in PerformanceTestBase if expectations don't match environment capabilities.

---

## Framework Statistics

**Version 1.0 Deliverables:**

- **Production Classes:** 58 files
- **Test Classes:** 21 files
- **Total Test Cases:** 74+ comprehensive tests
- **Utility Methods:** 350+ reusable methods
- **Lines of Code:** 7,500+ lines of production code
- **Documentation:** 6 comprehensive guides

**Test Coverage by Layer:**

| Layer | Test Files | Test Cases |
|-------|------------|------------|
| UI Testing | 3 files | 44 tests |
| API Testing | 1 file | Multiple endpoints |
| Database Testing | 2 files | 22 tests |
| Performance Testing | 1 file | 8 tests |
| AWS Integration | 1 file | Multiple workflows |
| MultiBank Platform | 5 files | 50+ tests |

---

## License

This project is licensed under the MIT License.

---

## Author

**Victor Grozev**
Framework Creator & Lead Developer

Core Automation Framework - Version 1.0

---

## Support

For questions, issues, or contributions, please open an issue in the repository issue tracker.

---

**Core Automation Framework** - Enterprise-grade test automation built for reliability, scalability, and maintainability.
