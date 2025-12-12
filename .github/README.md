# CI/CD Pipeline Documentation

## Overview

This project includes a comprehensive CI/CD pipeline using GitHub Actions for automated cross-browser testing with Playwright.

## Workflows

### 1. Automated Test Suite (`test-automation.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches
- Manual trigger via workflow_dispatch

**Jobs:**
- **Smoke Tests**: Quick validation on Chromium only (~5-10 minutes)
- **Cross-Browser Tests**: Parallel execution on Chromium, Firefox, and WebKit (~30 minutes)
- **Full Suite**: Complete test execution on all browsers (runs on `main` branch only)

**Features:**
- Parallel execution across multiple browsers
- Automatic artifact upload (test results, screenshots, Allure reports)
- Fail-fast disabled for cross-browser tests
- Retention: 7 days for test results, 30 days for reports

### 2. Pull Request Checks (`pr-checks.yml`)

**Triggers:**
- Pull requests to `main` or `develop`

**Features:**
- Fast smoke tests on Chromium only (~5 minutes)
- Automatic PR comment with test results
- Blocks merge if smoke tests fail
- Minimal artifact retention (3 days)

### 3. Nightly Tests (`nightly-tests.yml`)

**Triggers:**
- Scheduled: Every night at 2 AM UTC
- Manual trigger via workflow_dispatch

**Features:**
- Full cross-browser test suite execution
- Creates GitHub issue on failure
- Extended artifact retention (30 days)
- Comprehensive test reporting

## Playwright Browser Setup

The pipeline uses Playwright's native browser installation:

```bash
# Install all browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"

# Install specific browser
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"
```

## TestNG Configuration

### Cross-Browser Suite (`testng.xml`)
Runs all tests on Chromium, Firefox, and WebKit in parallel:
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng.xml
```

### Smoke Tests (`testng-smoke.xml`)
Runs critical tests on Chromium only:
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml
```

### Single Browser (`testng-chromium.xml`)
Runs all tests on Chromium only:
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml
```

## Browser Parameter

Tests accept a `browser` parameter via TestNG:

```xml
<test name="Chromium Tests">
    <parameter name="browser" value="chromium"/>
    <packages>
        <package name="com.automation.multibank"/>
    </packages>
</test>
```

The `@Optional("chromium")` annotation in `BaseWebTest` ensures tests run with Chromium if no browser is specified.

## Artifacts

### Test Results
- Location: `target/surefire-reports/`
- Format: XML, HTML
- Retention: 7-30 days depending on workflow

### Screenshots
- Location: `target/screenshots/`
- Captured: On test failure only
- Retention: 7 days

### Allure Reports
- Location: `target/site/allure-maven-plugin/`
- Format: HTML with interactive UI
- Retention: 14-30 days depending on workflow

## Running Locally

### Prerequisites
```bash
# Install Java 21
# Install Maven 3.9+
# Install Playwright browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
```

### Run All Tests (Cross-Browser)
```bash
mvn clean test
```

### Run Smoke Tests
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng/testng-smoke.xml
```

### Run Specific Browser
```bash
mvn clean test -Dbrowser=firefox -DsuiteXmlFile=src/test/resources/testng/testng-chromium.xml
```

### Generate Allure Report
```bash
mvn allure:report
mvn allure:serve
```

## Troubleshooting

### Playwright Browser Installation Issues
If browsers fail to install in CI:
```yaml
- name: Install system dependencies
  run: |
    sudo apt-get update
    sudo apt-get install -y libgbm1 libglib2.0-0 libnss3 libnspr4 libatk1.0-0 libatk-bridge2.0-0 libcups2 libdrm2 libxkbcommon0 libxcomposite1 libxdamage1 libxfixes3 libxrandr2 libpango-1.0-0 libcairo2 libasound2
```

### Test Timeout Issues
Adjust timeout values in workflow:
```yaml
timeout-minutes: 45  # Increase if needed
```

### Parallel Execution Issues
Reduce thread count in `testng.xml`:
```xml
<suite name="..." parallel="tests" thread-count="2">
```

## Best Practices

1. **Smoke Tests**: Always run before cross-browser tests
2. **Parallel Execution**: Keep thread-count balanced (2-3 per browser)
3. **Test Independence**: Each test should be able to run in any order
4. **Artifact Cleanup**: CI automatically cleans up old artifacts
5. **Browser Isolation**: Each test gets a fresh browser instance

## Performance Metrics

- **Smoke Tests**: ~5-10 minutes
- **Single Browser Suite**: ~15-20 minutes
- **Cross-Browser Suite**: ~30-45 minutes
- **Full Nightly Suite**: ~60-90 minutes

## Support

For issues or questions:
1. Check GitHub Actions logs
2. Review Allure reports
3. Check test screenshots for visual debugging
4. Review `target/surefire-reports/` for detailed stack traces
