# Test Execution Reports

This directory contains the latest test execution reports for stakeholder review.

##  Latest Report

The latest test execution results are available in the `latest/` directory.

### How to View the Report

1. **Open the report locally:**
   - Navigate to `reports/latest/` directory
   - Open `index.html` in your web browser
   - The report will display all test results, charts, and statistics

2. **What's included:**
   -  Test execution overview
   -  Pass/Fail statistics with charts
   -  Test duration timeline
   -  Detailed test results
   -  Failed test stack traces (if any)
   -  Test categorization by feature

##  Updating the Report

To generate and update the latest report after running tests:

```bash
# Run tests
mvn clean test

# Generate the static HTML report
mvn allure:report

# Copy to reports directory
cp -r target/allure-report/* reports/latest/

# Commit the updated report
git add reports/latest/
git commit -m "Update test execution report"
```

##  Report Information

- **Report Format:** Allure HTML Report
- **Location:** `reports/latest/index.html`
- **No Installation Required:** Just open in any modern web browser
- **Offline Accessible:** All assets are bundled, works without internet

##  Quick Access

**Direct path to report:** `reports/latest/index.html`

Simply double-click the file or open it in your browser to view the complete test execution results.
