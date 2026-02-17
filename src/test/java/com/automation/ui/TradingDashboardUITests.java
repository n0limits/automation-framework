package com.automation.ui;

import com.automation.base.BaseTest;
import com.automation.pages.LoginPage;
import com.automation.pages.TradingDashboardPage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UI Tests for Trading Dashboard
 * Tests cover:
 * - Trading pair selection
 * - Order placement (Buy/Sell)
 * - Price monitoring
 * - Balance verification
 * - Order history management
 * - UI interactions and validations
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Trading Dashboard UI")
public class TradingDashboardUITests extends BaseTest {
    private LoginPage loginPage;
    private TradingDashboardPage dashboardPage;

    @BeforeMethod
    public void setupAndLogin() {
        log.info("===== Setting up Trading Dashboard UI Test =====");

        // Initialize pages
        loginPage = new LoginPage();
        dashboardPage = new TradingDashboardPage();

        // Login before each test
        log.info("Performing login");
        loginPage.open();
        loginPage.login(config.getUsername(), config.getPassword());

        // Navigate to dashboard
        dashboardPage.open();
        log.info("Dashboard ready for testing");
    }

    // @Test(description = "Verify trading dashboard loads successfully")
    @Description("Test that trading dashboard displays all required elements")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Dashboard Navigation")
    public void testDashboardLoadsSuccessfully() {
        log.info("=== Test: Dashboard Loads Successfully ===");

        // Verify dashboard is loaded
        boolean isDashboardLoaded = dashboardPage.isDashboardLoaded();
        assertThat(isDashboardLoaded)
                .as("Dashboard should be fully loaded")
                .isTrue();

        // Verify URL contains dashboard
        String currentUrl = dashboardPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("URL should contain 'dashboard'")
                .contains("/dashboard");

        // Verify page title
        String title = dashboardPage.getTitle();
        assertThat(title)
                .as("Page title should be meaningful")
                .isNotEmpty();
        assertThat(title.toLowerCase())
                .as("Page title should contain dashboard, trading, or automation")
                .satisfiesAnyOf(
                        t -> assertThat(t).contains("dashboard"),
                        t -> assertThat(t).contains("trading"),
                        t -> assertThat(t).contains("automation")
                );

        // Take screenshot
        String screenshot = dashboardPage.takeScreenshot("dashboard-loaded");
        log.info("Dashboard screenshot: {}", screenshot);

        log.info("[PASS] Dashboard load test passed");
    }
}
