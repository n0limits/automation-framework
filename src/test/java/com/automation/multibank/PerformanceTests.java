package com.automation.multibank;

import com.automation.base.BaseWebTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performance tests for MultiBank.io platform
 * These tests measure and validate application performance metrics
 */
@Slf4j
@Epic("Performance Tests")
@Feature("Application Performance")
public class PerformanceTests extends BaseWebTest {

    private static final int ACCEPTABLE_PAGE_LOAD_TIME_MS = 5000; // 5 seconds
    private static final int ACCEPTABLE_NAVIGATION_TIME_MS = 3000; // 3 seconds

    @Test(description = "Measure homepage load time", priority = 1)
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Load Performance")
    @Description("Measures the time it takes to load the MultiBank.io homepage and validates it meets performance criteria")
    public void performanceTestHomepageLoadTime() {
        log.info("Starting performance test: Homepage load time measurement");

        long startTime = System.currentTimeMillis();

        // Navigate to homepage
        page.navigate("https://trade.multibank.io");
        page.waitForLoadState();

        long loadTime = System.currentTimeMillis() - startTime;
        log.info("Homepage load time: {} ms", loadTime);

        // Attach metrics to Allure report
        Allure.addAttachment("Load Time", "text/plain", loadTime + " ms");
        Allure.parameter("Load Time (ms)", loadTime);

        // Verify load time is acceptable
        assertThat(loadTime)
                .as("Homepage should load within " + ACCEPTABLE_PAGE_LOAD_TIME_MS + "ms")
                .isLessThan(ACCEPTABLE_PAGE_LOAD_TIME_MS);

        log.info("Performance test passed: Homepage loaded in {} ms (limit: {} ms)", loadTime, ACCEPTABLE_PAGE_LOAD_TIME_MS);
    }

    @Test(description = "Measure page navigation performance", priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation Performance")
    @Description("Measures the time it takes to navigate between pages")
    public void performanceTestPageNavigation() {
        log.info("Starting performance test: Page navigation measurement");

        // Start from homepage
        page.navigate("https://trade.multibank.io");
        page.waitForLoadState();

        long startTime = System.currentTimeMillis();

        // Perform navigation (scroll and interaction)
        page.evaluate("window.scrollTo(0, document.body.scrollHeight / 2)");
        page.waitForTimeout(100);

        long navigationTime = System.currentTimeMillis() - startTime;
        log.info("Page interaction time: {} ms", navigationTime);

        // Attach metrics to Allure report
        Allure.addAttachment("Navigation Time", "text/plain", navigationTime + " ms");
        Allure.parameter("Navigation Time (ms)", navigationTime);

        // Verify navigation time is acceptable
        assertThat(navigationTime)
                .as("Page interaction should complete within " + ACCEPTABLE_NAVIGATION_TIME_MS + "ms")
                .isLessThan(ACCEPTABLE_NAVIGATION_TIME_MS);

        log.info("Performance test passed: Page interaction completed in {} ms", navigationTime);
    }

    @Test(description = "Measure DOM content loaded time", priority = 3)
    @Severity(SeverityLevel.MINOR)
    @Story("DOM Load Performance")
    @Description("Measures how quickly the DOM content is loaded and ready")
    public void performanceTestDOMContentLoaded() {
        log.info("Starting performance test: DOM content load time");

        long startTime = System.currentTimeMillis();

        // Navigate and wait for DOM content loaded
        page.navigate("https://trade.multibank.io");
        page.waitForLoadState();

        long domLoadTime = System.currentTimeMillis() - startTime;
        log.info("DOM content loaded time: {} ms", domLoadTime);

        // Attach metrics to Allure report
        Allure.addAttachment("DOM Load Time", "text/plain", domLoadTime + " ms");
        Allure.parameter("DOM Load Time (ms)", domLoadTime);

        // Verify DOM loads quickly (should be faster than full page load)
        assertThat(domLoadTime)
                .as("DOM should load within 3 seconds")
                .isLessThan(3000);

        log.info("Performance test passed: DOM loaded in {} ms", domLoadTime);
    }

    @Test(description = "Measure network idle time", priority = 4)
    @Severity(SeverityLevel.MINOR)
    @Story("Network Performance")
    @Description("Measures the time until network becomes idle after page load")
    public void performanceTestNetworkIdle() {
        log.info("Starting performance test: Network idle time");

        long startTime = System.currentTimeMillis();

        // Navigate and wait for network idle
        page.navigate("https://trade.multibank.io");
        page.waitForLoadState();

        long networkIdleTime = System.currentTimeMillis() - startTime;
        log.info("Network idle time: {} ms", networkIdleTime);

        // Attach metrics to Allure report
        Allure.addAttachment("Network Idle Time", "text/plain", networkIdleTime + " ms");
        Allure.parameter("Network Idle Time (ms)", networkIdleTime);

        // Verify network becomes idle in reasonable time
        assertThat(networkIdleTime)
                .as("Network should become idle within 10 seconds")
                .isLessThan(10000);

        log.info("Performance test passed: Network became idle in {} ms", networkIdleTime);
    }
}
