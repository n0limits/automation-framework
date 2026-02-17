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
 * UI Tests for Login functionality
 * Tests cover:
 * - Valid login scenarios
 * - Invalid credentials handling
 * - Error message validation
 * - Navigation after successful login
 * - UI element visibility
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Login UI")
public class LoginUITests extends BaseTest {
    private LoginPage loginPage;
    private TradingDashboardPage dashboardPage;

    @BeforeMethod
    public void setupPages() {
        log.info("===== Setting up Login UI Test =====");
        loginPage = new LoginPage();
        dashboardPage = new TradingDashboardPage();
    }

    // @Test(description = "Verify successful login with valid credentials") // no login credentials available
    @Description("Test successful login flow: navigate to login page, enter credentials, verify dashboard loads")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Authentication")
    public void testSuccessfulLogin() {
        log.info("=== Test: Successful Login ===" );

        // Navigate to login page
        loginPage.open();

        // Verify we're on login page
        String currentUrl = loginPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("Should be on login page")
                .contains("/login");

        // Perform login
        String username = config.getUsername();
        String password = config.getPassword();
        loginPage.login(username, password);

        // Verify successful login - should redirect to dashboard
        String dashboardUrl = loginPage.getCurrentUrl();
        assertThat(dashboardUrl)
                .as("Should redirect to dashboard after login")
                .contains("/dashboard");

        // Verify dashboard is loaded
        boolean isDashboardLoaded = dashboardPage.isDashboardLoaded();
        assertThat(isDashboardLoaded)
                .as("Dashboard should be fully loaded")
                .isTrue();

        log.info("[PASS] Login test passed - user logged in successfully");
    }
}
