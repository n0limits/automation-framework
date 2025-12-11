package com.automation.listeners;

import com.automation.utils.FileUtils;
import com.automation.utils.PlaywrightManager;
import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

@Slf4j
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        log.info("========== Test Started: {} ==========", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("========== Test Passed: {} ==========", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("========== Test Failed: {} ==========", result.getName());
        log.error("Failure reason: {}", result.getThrowable().getMessage());

        try {
            if (PlaywrightManager.getPage() != null) {
                FileUtils.takeScreenshot(PlaywrightManager.getPage(), result.getName());
                attachScreenshot();
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot", e);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("========== Test Skipped: {} ==========", result.getName());
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

    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        try {
            return PlaywrightManager.getPage().screenshot();
        } catch (Exception e) {
            log.error("Failed to attach screenshot to Allure", e);
            return new byte[0];
        }
    }
}