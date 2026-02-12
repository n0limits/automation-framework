package com.automation.listeners;

import com.automation.utils.FileUtils;
import com.automation.utils.PlaywrightManager;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * TestNG Listener for test lifecycle events.
 * Features:
 * - MDC context for log correlation
 * - Screenshot capture on failure
 * - Allure report integration
 *
 * @author Victor Grozev
 */
@Slf4j
public class TestListener implements ITestListener {

    private static final String MDC_TEST_ID = "testId";

    @Override
    public void onTestStart(ITestResult result) {
        // Set MDC context for log correlation
        String testId = formatTestId(result);
        MDC.put(MDC_TEST_ID, testId);

        log.info("========== Test Started: {} ==========", result.getName());
    }

    /**
     * Format test ID for MDC context.
     * Format: ClassName.methodName
     */
    private String formatTestId(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        String methodName = result.getMethod().getMethodName();
        return className + "." + methodName;
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("========== Test Passed: {} ==========", result.getName());
        clearMDC();
    }

    /**
     * Clear MDC context after test completion.
     */
    private void clearMDC() {
        MDC.remove(MDC_TEST_ID);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("========== Test Failed: {} ==========", result.getName());
        log.error("Failure reason: {}", result.getThrowable().getMessage());

        try {
            if (PlaywrightManager.getPage() != null) {
                // Save screenshot to file system
                FileUtils.takeScreenshot(PlaywrightManager.getPage(), result.getName());

                // Attach screenshot to Allure report
                byte[] screenshotBytes = PlaywrightManager.getPage().screenshot();
                Allure.addAttachment("Screenshot on Failure", "image/png",
                    new ByteArrayInputStream(screenshotBytes), "png");
                log.info("Screenshot attached to Allure report");
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot", e);
        } finally {
            clearMDC();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("========== Test Skipped: {} ==========", result.getName());
        clearMDC();
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("========== Test Suite Started: {} ==========", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("========== Test Suite Finished: {} ==========", context.getName());
        log.info("Passed tests: {}", context.getPassedTests().size());
        log.info("Failed tests: {}", context.getFailedTests().size());
        log.info("Skipped tests: {}", context.getSkippedTests().size());
    }
}