package com.automation.listeners;

import com.automation.reporting.TestMetrics;
import com.automation.utils.FileUtils;
import com.automation.utils.PlaywrightManager;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Unified TestNG Listener for test lifecycle events.
 * Combines MDC logging, screenshot capture, Allure reporting, and test metrics.
 *
 * Features:
 * - MDC context for log correlation
 * - Screenshot capture on failure
 * - Allure report integration (attachments, environment info)
 * - Test metrics collection and reporting
 * - Test categorization
 *
 * @author Victor Grozev
 */
@Slf4j
public class TestListener implements ITestListener {

    private static final String MDC_TEST_ID = "testId";
    private final TestMetrics metrics = TestMetrics.getInstance();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onTestStart(ITestResult result) {
        String testId = formatTestId(result);
        MDC.put(MDC_TEST_ID, testId);

        log.info("========== Test Started: {} ==========", result.getName());

        metrics.recordTestStart(testId);

        String category = getTestCategory(result);
        if (category != null) {
            metrics.recordCategory(category);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testId = formatTestId(result);
        long duration = result.getEndMillis() - result.getStartMillis();

        log.info("========== Test Passed: {} ({}ms) ==========", result.getName(), duration);

        metrics.recordTestPass(testId, duration);
        Allure.addAttachment("Test Duration", duration + " ms");

        clearMDC();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testId = formatTestId(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        Throwable throwable = result.getThrowable();
        String failureReason = throwable != null ? throwable.getMessage() : "Unknown";

        log.error("========== Test Failed: {} ({}ms) ==========", result.getName(), duration);
        log.error("Failure reason: {}", failureReason);

        metrics.recordTestFailure(testId, duration, failureReason);

        // Allure attachments
        Allure.addAttachment("Test Duration", duration + " ms");
        Allure.addAttachment("Failure Reason", failureReason);

        if (throwable != null) {
            String stackTrace = getStackTrace(throwable);
            Allure.addAttachment("Stack Trace", "text/plain",
                    new ByteArrayInputStream(stackTrace.getBytes(StandardCharsets.UTF_8)), ".txt");
        }

        // Screenshot on failure
        try {
            if (PlaywrightManager.getPage() != null) {
                FileUtils.takeScreenshot(PlaywrightManager.getPage(), result.getName());

                byte[] screenshotBytes = PlaywrightManager.getPage().screenshot();
                Allure.addAttachment("Screenshot on Failure", "image/png",
                    new ByteArrayInputStream(screenshotBytes), "png");
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot", e);
        } finally {
            clearMDC();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testId = formatTestId(result);
        String skipReason = result.getThrowable() != null ?
                result.getThrowable().getMessage() : "Test skipped";

        log.warn("========== Test Skipped: {} ==========", result.getName());

        metrics.recordTestSkip(testId, skipReason);
        Allure.addAttachment("Skip Reason", skipReason);

        clearMDC();
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("========== Test Suite Started: {} ==========", context.getName());
        log.info("Start Time: {}", LocalDateTime.now().format(formatter));
        log.info("Total Tests: {}", context.getAllTestMethods().length);

        attachEnvironmentInfo(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("========== Test Suite Finished: {} ==========", context.getName());
        log.info("Passed tests: {}", context.getPassedTests().size());
        log.info("Failed tests: {}", context.getFailedTests().size());
        log.info("Skipped tests: {}", context.getSkippedTests().size());

        metrics.printReport();
        attachMetricsSummary();
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        String testId = formatTestId(result);
        long duration = result.getEndMillis() - result.getStartMillis();

        log.error("========== Test Timed Out: {} ({}ms) ==========", result.getName(), duration);

        metrics.recordTestFailure(testId, duration, "Timeout");
        Allure.addAttachment("Failure Reason", "Test execution timed out");

        clearMDC();
    }

    // ========== Helper Methods ==========

    private String formatTestId(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        String methodName = result.getMethod().getMethodName();
        return className + "." + methodName;
    }

    private void clearMDC() {
        MDC.remove(MDC_TEST_ID);
    }

    private String getTestCategory(ITestResult result) {
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            return groups[0];
        }

        String className = result.getTestClass().getName();
        if (className.contains("API")) return "API";
        if (className.contains("UI")) return "UI";
        if (className.contains("Database")) return "Database";
        if (className.contains("Performance")) return "Performance";
        if (className.contains("Integration")) return "Integration";

        return "Other";
    }

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
        summary.append("\nExecution Time:\n");
        summary.append("  Total: ").append(metrics.getTotalExecutionTime()).append(" ms\n");
        summary.append("  Average: ").append(String.format("%.2f ms", metrics.getAverageDuration())).append("\n");

        if (!metrics.getFailureReasons().isEmpty()) {
            summary.append("\nTop Failure Reasons:\n");
            metrics.getTopFailures(5).forEach(entry ->
                    summary.append("  ").append(entry.getKey())
                            .append(": ").append(entry.getValue()).append("\n")
            );
        }

        if (!metrics.getTestsByCategory().isEmpty()) {
            summary.append("\nTests by Category:\n");
            metrics.getTestsByCategory().forEach((category, count) ->
                    summary.append("  ").append(category)
                            .append(": ").append(count).append("\n")
            );
        }

        summary.append("\nSlowest Tests:\n");
        metrics.getSlowestTests(5).forEach(entry ->
                summary.append("  ").append(entry.getKey())
                        .append(": ").append(String.format("%.2f ms", entry.getValue())).append("\n")
        );

        Allure.addAttachment("Test Metrics Summary", "text/plain",
                new ByteArrayInputStream(summary.toString().getBytes(StandardCharsets.UTF_8)), ".txt");
    }
}
