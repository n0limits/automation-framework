package com.automation.reporting;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enhanced Allure Test Listener
 * Extends default Allure reporting with additional metrics and analytics
 *
 * Features:
 * - Automatic test categorization
 * - Performance metrics attachment
 * - Failure analysis
 * - Test duration tracking
 * - Environment information
 * - Custom test metadata
 *
 * @author Victor Grozev
 */
@Slf4j
public class EnhancedAllureListener implements ITestListener {
    private final TestMetrics metrics = TestMetrics.getInstance();
    private final AllureLifecycle lifecycle = Allure.getLifecycle();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onStart(ITestContext context) {
        log.info("===== Test Suite Started: {} =====", context.getName());
        log.info("Start Time: {}", LocalDateTime.now().format(formatter));
        log.info("Total Tests: {}", context.getAllTestMethods().length);

        // Add environment information to Allure
        attachEnvironmentInfo(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("===== Test Suite Finished: {} =====", context.getName());
        log.info("Finish Time: {}", LocalDateTime.now().format(formatter));
        log.info("Passed: {}", context.getPassedTests().size());
        log.info("Failed: {}", context.getFailedTests().size());
        log.info("Skipped: {}", context.getSkippedTests().size());

        // Print metrics report
        metrics.printReport();

        // Attach metrics summary to Allure
        attachMetricsSummary();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);
        log.info("Test Started: {}", testName);

        metrics.recordTestStart(testName);

        // Record test category
        String category = getTestCategory(result);
        if (category != null) {
            metrics.recordCategory(category);
        }

        // Attach test start time
        Allure.addAttachment("Test Start Time",
                LocalDateTime.now().format(formatter));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();

        log.info("✅ Test Passed: {} ({}ms)", testName, duration);

        metrics.recordTestPass(testName, duration);

        // Attach test duration
        Allure.addAttachment("Test Duration", duration + " ms");
        Allure.addAttachment("Status", "PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        Throwable throwable = result.getThrowable();
        String failureReason = throwable != null ? throwable.getMessage() : "Unknown";

        log.error("❌ Test Failed: {} ({}ms)", testName, duration);
        log.error("Failure Reason: {}", failureReason);

        metrics.recordTestFailure(testName, duration, failureReason);

        // Attach failure information
        Allure.addAttachment("Test Duration", duration + " ms");
        Allure.addAttachment("Status", "FAILED");
        Allure.addAttachment("Failure Reason", failureReason);

        if (throwable != null) {
            String stackTrace = getStackTrace(throwable);
            Allure.addAttachment("Stack Trace", "text/plain",
                    new ByteArrayInputStream(stackTrace.getBytes(StandardCharsets.UTF_8)), ".txt");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);
        String skipReason = result.getThrowable() != null ?
                result.getThrowable().getMessage() : "Test skipped";

        log.warn("⏭️  Test Skipped: {}", testName);
        log.warn("Skip Reason: {}", skipReason);

        metrics.recordTestSkip(testName, skipReason);

        // Attach skip information
        Allure.addAttachment("Status", "SKIPPED");
        Allure.addAttachment("Skip Reason", skipReason);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.info("Test failed but within success percentage: {}", getTestName(result));
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();

        log.error("⏱️ Test Failed with Timeout: {} ({}ms)", testName, duration);

        metrics.recordTestFailure(testName, duration, "Timeout");

        // Attach timeout information
        Allure.addAttachment("Test Duration", duration + " ms");
        Allure.addAttachment("Status", "TIMEOUT");
        Allure.addAttachment("Failure Reason", "Test execution timed out");
    }

    /**
     * Get test name from result
     *
     * @param result Test result
     * @return Test name
     */
    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    /**
     * Get test category from annotations
     *
     * @param result Test result
     * @return Category name or null
     */
    private String getTestCategory(ITestResult result) {
        // Try to get category from test groups
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            return groups[0];
        }

        // Try to get from class name
        String className = result.getTestClass().getName();
        if (className.contains("API")) {
            return "API";
        } else if (className.contains("UI")) {
            return "UI";
        } else if (className.contains("Database")) {
            return "Database";
        } else if (className.contains("Performance")) {
            return "Performance";
        } else if (className.contains("Integration")) {
            return "Integration";
        }

        return "Other";
    }

    /**
     * Get stack trace as string
     *
     * @param throwable Throwable
     * @return Stack trace string
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");

        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }

        if (throwable.getCause() != null) {
            sb.append("Caused by: ").append(getStackTrace(throwable.getCause()));
        }

        return sb.toString();
    }

    /**
     * Attach environment information
     *
     * @param context Test context
     */
    private void attachEnvironmentInfo(ITestContext context) {
        StringBuilder envInfo = new StringBuilder();
        envInfo.append("Test Suite: ").append(context.getName()).append("\n");
        envInfo.append("Start Time: ").append(LocalDateTime.now().format(formatter)).append("\n");
        envInfo.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        envInfo.append("OS: ").append(System.getProperty("os.name")).append("\n");
        envInfo.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
        envInfo.append("User: ").append(System.getProperty("user.name")).append("\n");

        Allure.addAttachment("Environment Information",
                new ByteArrayInputStream(envInfo.toString().getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Attach metrics summary to Allure
     */
    private void attachMetricsSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("===== Test Execution Metrics =====\n\n");
        summary.append("Total Tests: ").append(metrics.getTotalTests()).append("\n");
        summary.append("Passed: ").append(metrics.getPassedTests())
                .append(String.format(" (%.2f%%)", metrics.getPassRate())).append("\n");
        summary.append("Failed: ").append(metrics.getFailedTests())
                .append(String.format(" (%.2f%%)", metrics.getFailureRate())).append("\n");
        summary.append("Skipped: ").append(metrics.getSkippedTests())
                .append(String.format(" (%.2f%%)", metrics.getSkipRate())).append("\n");
        summary.append("\n");
        summary.append("Execution Time:\n");
        summary.append("  Total: ").append(metrics.getTotalExecutionTime()).append(" ms\n");
        summary.append("  Average: ").append(String.format("%.2f ms", metrics.getAverageDuration())).append("\n");

        if (!metrics.getFailureReasons().isEmpty()) {
            summary.append("\n");
            summary.append("Top Failure Reasons:\n");
            metrics.getTopFailures(5).forEach(entry ->
                    summary.append("  ").append(entry.getKey())
                            .append(": ").append(entry.getValue()).append("\n")
            );
        }

        if (!metrics.getTestsByCategory().isEmpty()) {
            summary.append("\n");
            summary.append("Tests by Category:\n");
            metrics.getTestsByCategory().forEach((category, count) ->
                    summary.append("  ").append(category)
                            .append(": ").append(count).append("\n")
            );
        }

        summary.append("\n");
        summary.append("Slowest Tests:\n");
        metrics.getSlowestTests(5).forEach(entry ->
                summary.append("  ").append(entry.getKey())
                        .append(": ").append(String.format("%.2f ms", entry.getValue())).append("\n")
        );

        Allure.addAttachment("Test Metrics Summary", "text/plain",
                new ByteArrayInputStream(summary.toString().getBytes(StandardCharsets.UTF_8)), ".txt");
    }
}
