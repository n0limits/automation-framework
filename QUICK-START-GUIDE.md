# Core Automation Framework - Quick Start Guide

## Getting Started in 5 Minutes

### Prerequisites
- Java 21 installed
- Maven installed
- IDE (IntelliJ IDEA / Eclipse / VS Code)

## Current Implementation: MultiBank Trading Platform

This framework includes a complete test suite for https://trade.multibank.io/ with:
- 38 automated test cases covering Navigation, Trading, and Content validation
- 4 page objects (NavigationPage, TradingPage, FooterPage, AboutUsPage)
- External JSON test data management
- Cross-browser testing (Chromium, Firefox, WebKit)
- CI/CD pipeline integration with GitHub Actions
- Comprehensive logging and Allure reporting

---

## Quick Commands Reference

### Build & Setup

```bash
# Install dependencies
mvn clean install

# Install Playwright browsers (required for web UI testing)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

# Compile project
mvn clean compile test-compile
```

### Run Tests

**Cross-Browser Testing (Recommended):**
```bash
# Run all tests on all browsers (Chromium, Firefox, WebKit)
mvn clean test

# Run smoke tests (fast - 7 critical tests on Chromium only)
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml

# Run on single browser (all 38 tests)
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml
```

**Specific Browser Override:**
```bash
# Run on Firefox only
mvn clean test -Dbrowser=firefox -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml

# Run on WebKit only
mvn clean test -Dbrowser=webkit -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml
```

**Specific Test Classes:**
```bash
mvn clean test -Dtest=NavigationTests        # 10 navigation tests
mvn clean test -Dtest=TradingTests           # 15 trading tests
mvn clean test -Dtest=ContentValidationTests # 13 content tests
```

**Additional Options:**
```bash
# Run in headless mode
mvn clean test -Dheadless=true

# Run specific test method
mvn clean test -Dtest=NavigationTests#testNavigationMenuDisplayed
```

### Generate Reports

```bash
# Generate and open Allure report (interactive HTML)
mvn allure:serve

# Generate static Allure report
mvn allure:report

# View static report: target/site/allure-maven-plugin/index.html
```

### CI/CD Pipeline

The framework includes GitHub Actions workflows for automated testing:

**Workflows:**
- `test-automation.yml` - Main pipeline (smoke tests, cross-browser tests, full suite)
- `pr-checks.yml` - Pull request validation with smoke tests
- `nightly-tests.yml` - Scheduled nightly regression tests

**Manual Trigger:**
Push code to GitHub to automatically trigger the CI/CD pipeline:
```bash
git add .
git commit -m "Your commit message"
git push origin main
```

View results in GitHub Actions tab.

---

## Project Structure Overview

```
automation-framework/
├── .github/workflows/              # CI/CD pipelines
│   ├── test-automation.yml         # Main test pipeline
│   ├── pr-checks.yml               # PR validation
│   └── nightly-tests.yml           # Scheduled tests
│
├── src/main/java/com/automation/
│   ├── pages/multibank/            # Page Objects
│   │   ├── NavigationPage.java
│   │   ├── TradingPage.java
│   │   ├── FooterPage.java
│   │   └── AboutUsPage.java
│   ├── base/                       # Base test classes
│   ├── config/                     # Configuration
│   ├── factory/                    # Browser and page factories
│   ├── listeners/                  # TestNG listeners
│   ├── utils/                      # Utilities (waits, data readers)
│   ├── api/                        # API testing components
│   └── db/                         # Database connections
│
├── src/test/java/com/automation/
│   ├── multibank/                  # Test classes
│   │   ├── NavigationTests.java    # 10 tests
│   │   ├── TradingTests.java       # 15 tests
│   │   └── ContentValidationTests.java # 13 tests
│   └── providers/
│       └── TestDataProviders.java  # Data providers
│
├── src/test/resources/
│   ├── testdata/                   # JSON test data
│   │   ├── navigation-data.json
│   │   ├── trading-data.json
│   │   └── content-data.json
│   └── testng/                     # TestNG suites
│       ├── testng.xml              # Cross-browser suite
│       ├── testng-smoke.xml        # Smoke tests
│       └── testng-chromium.xml     # Single browser
│
└── src/main/resources/
    ├── config.properties           # Configuration
    └── logback.xml                 # Logging
```

---

## Configuration

### config.properties

Location: `src/main/resources/config.properties`

**Key Settings:**
```properties
# Target Application
base.url=https://trade.multibank.io

# Browser Configuration
browser=chromium              # chromium, firefox, webkit
headless=false
timeout=30000
screenshot.on.failure=true

# API Configuration
api.base.url=https://jsonplaceholder.typicode.com
api.timeout=30000

# Database Configuration (if needed)
mongo.connection.string=mongodb://localhost:27017
sql.connection.string=jdbc:mysql://localhost:3306/testdb
postgres.connection.string=jdbc:postgresql://localhost:5432/testdb

# Paths
test.data.path=src/test/resources/testdata
report.path=target/reports
```

**To test a different application:** Change the `base.url` value and update page objects accordingly.

---

## Test Suite Details

### MultiBank Test Coverage (38 Tests)

**NavigationTests.java (10 tests)**
- Navigation menu visibility and structure
- Navigation items functionality
- Page transitions and URL validation
- Cross-browser navigation consistency

**TradingTests.java (15 tests)**
- Spot trading section and table structure
- Trading pairs display and data validation (BTC, ETH, SOL, XRP)
- Market indicators (Fear Index, Top Gainers/Losers)
- Investment opportunities (MBG Token, Real World Assets)
- Quick access tools

**ContentValidationTests.java (13 tests)**
- Footer and download section validation
- App Store and Google Play links
- Marketing banners
- About Us page components
- Social media links
- Content rendering

### Test Data Management

Tests use external JSON files for maintainability:

**Example: navigation-data.json**
```json
{
  "navigationMenu": {
    "expectedItems": ["Dashboard", "Markets", "Trade", "Features", "About Us", "Support"]
  }
}
```

**Usage in Tests:**
```java
@BeforeMethod
public void setupTest() {
    testData = TestDataReader.readJsonFile("navigation-data.json");
    List<String> expectedItems = TestDataReader.getStringList(
        testData, "navigationMenu", "expectedItems"
    );
}
```

---

## Troubleshooting

**Playwright browsers not found:**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

**Tests not found or compilation errors:**
```bash
mvn clean compile test-compile
```

**Test timeouts:**
- Increase timeout in `config.properties`
- Verify application is accessible
- Check network connectivity

**Database connection issues:**
- Verify connection strings in `config.properties`
- Ensure database services are running
- Check credentials and network settings

---

## Report Locations

- **Allure Report**: `target/site/allure-maven-plugin/index.html` (after `mvn allure:report`)
- **TestNG Reports**: `target/surefire-reports/`
- **Screenshots**: `target/screenshots/`
- **Logs**: `logs/test-automation.log`

---

## Key Framework Features

**Cross-Browser Testing:**
- Playwright native support for Chromium, Firefox, WebKit
- Parallel execution across browsers
- Browser parameter configuration via TestNG

**CI/CD Integration:**
- GitHub Actions workflows included
- Automated browser installation
- Multi-stage pipeline (smoke, cross-browser, full suite)

**Test Organization:**
- Page Object Model pattern
- External JSON test data
- Data-driven testing with providers
- ThreadLocal for parallel execution

**Reporting:**
- Allure reports with screenshots
- TestNG HTML reports
- Comprehensive SLF4J logging
- Automatic screenshot capture on failure

---

## Quick Reference

**Essential Commands:**
```bash
# Setup
mvn clean install
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

# Run Tests
mvn clean test                          # All browsers
mvn clean test -DsuiteXmlFile=...      # Specific suite
mvn clean test -Dtest=NavigationTests  # Specific class
mvn clean test -Dbrowser=firefox       # Specific browser

# Reports
mvn allure:serve                       # Interactive report
mvn allure:report                      # Static report
```

**TestNG Suites:**
- `testng.xml` - Full cross-browser (Chromium + Firefox + WebKit)
- `testng-smoke.xml` - Critical tests on Chromium only (fast)
- `testng-chromium.xml` - All tests on single browser

**For More Information:**
- See `README.md` for comprehensive documentation
- See `.github/README.md` for CI/CD pipeline details
- See `PLAYWRIGHT-SELENIUM-REVIEW.md` for technical details

---

**You're ready to start testing! Run your first test:**
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml
mvn allure:serve
```
