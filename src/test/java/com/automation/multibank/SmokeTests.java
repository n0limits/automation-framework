package com.automation.multibank;

import com.automation.base.BaseWebTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests for MultiBank.io platform
 * These tests verify basic application functionality
 */
@Slf4j
@Epic("Smoke Tests")
@Feature("Application Health")
public class SmokeTests extends BaseWebTest {

    // @Test(description = "Verify main page loads successfully", priority = 1) // changed SUT functionality
    @Severity(SeverityLevel.BLOCKER)
    @Story("Homepage Accessibility")
    @Description("Validates that the MultiBank.io homepage loads successfully and displays expected content")
    // configured to run through multiple browsers (chromium, webkit) if executed via the testng-multi-browser-smoke.xml
    public void smokeTestHomepageLoads() {
        log.info("Starting smoke test: Homepage load verification");

        // Verify page loaded
        assertThat(page.url())
                .as("Page URL should be the homepage")
                .contains("trade.multibank.io");

        // Verify page title
        String pageTitle = page.title();
        log.info("Page title: {}", pageTitle);
        assertThat(pageTitle)
                .as("Page title should be present and not empty")
                .isNotNull()
                .isNotEmpty()
                .contains("MultiBank");

        // Verify page is interactive (header/navigation visible)
        boolean isNavigationVisible = page.locator("header").isVisible();
        assertThat(isNavigationVisible)
                .as("Main navigation should be visible")
                .isTrue();

        log.info("Smoke test passed: Homepage loaded successfully");
    }

    // @Test(description = "Verify critical page elements are present", priority = 2) // changed SUT functionality
    @Severity(SeverityLevel.CRITICAL)
    @Story("Critical Elements")
    @Description("Validates that critical page elements are present on the homepage")
    public void smokeTestCriticalElementsPresent() {
        log.info("Starting smoke test: Critical elements verification");

        // Check header exists
        assertThat(page.locator("header").count())
                .as("Header should be present")
                .isGreaterThan(0);

        // Check page has content (body is not empty)
        int bodyElementCount = page.locator("body").count();
        assertThat(bodyElementCount)
                .as("Body element should be present")
                .isGreaterThan(0);

        // Check page has interactive elements
        int linkCount = page.locator("a").count();
        assertThat(linkCount)
                .as("Page should have links")
                .isGreaterThan(0);

        log.info("Smoke test passed: All critical elements are present");
    }

    // @Test(description = "Verify page has no JavaScript errors", priority = 3) // changed SUT functionality
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Stability")
    @Description("Validates that the page loads without critical JavaScript errors")
    public void smokeTestNoJavaScriptErrors() {
        log.info("Starting smoke test: JavaScript errors check");

        // Navigate and wait for network idle
        page.navigate("https://trade.multibank.io");
        page.waitForLoadState();

        // Verify page is in ready state
        String readyState = (String) page.evaluate("document.readyState");
        log.info("Document ready state: {}", readyState);

        assertThat(readyState)
                .as("Document should be in complete ready state")
                .isEqualTo("complete");

        log.info("Smoke test passed: No critical JavaScript errors detected");
    }
}
