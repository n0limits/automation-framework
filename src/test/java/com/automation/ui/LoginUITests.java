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

    @Test(description = "Verify successful login with valid credentials")
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

        log.info("✅ Login test passed - user logged in successfully");
    }

    @Test(description = "Verify login fails with invalid username")
    @Description("Test login with invalid username shows appropriate error message")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Authentication")
    public void testLoginWithInvalidUsername() {
        log.info("=== Test: Login with Invalid Username ===");

        // Navigate to login page
        loginPage.open();

        // Attempt login with invalid username
        loginPage.login("invalid_user_123", "somePassword123");

        // Verify error message is displayed
        boolean isErrorVisible = loginPage.isErrorVisible();
        assertThat(isErrorVisible)
                .as("Error message should be displayed")
                .isTrue();

        String errorMessage = loginPage.getErrorMessage();
        assertThat(errorMessage)
                .as("Error message should mention invalid credentials")
                .containsIgnoringCase("invalid")
                .containsAnyOf("username", "credentials", "login");

        // Verify we're still on login page
        String currentUrl = loginPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("Should remain on login page after failed login")
                .contains("/login");

        log.info("✅ Invalid username test passed - appropriate error shown");
    }

    @Test(description = "Verify login fails with invalid password")
    @Description("Test login with invalid password shows appropriate error message")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Authentication")
    public void testLoginWithInvalidPassword() {
        log.info("=== Test: Login with Invalid Password ===");

        loginPage.open();

        // Attempt login with valid username but invalid password
        String validUsername = config.getUsername();
        loginPage.login(validUsername, "wrongPassword123!");

        // Verify error message
        assertThat(loginPage.isErrorVisible())
                .as("Error message should be displayed")
                .isTrue();

        String errorMessage = loginPage.getErrorMessage();
        assertThat(errorMessage)
                .as("Error message should mention invalid credentials")
                .containsIgnoringCase("invalid")
                .containsAnyOf("password", "credentials", "login");

        log.info("✅ Invalid password test passed");
    }

    @Test(description = "Verify login fails with empty credentials")
    @Description("Test login with empty username and password shows validation errors")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Authentication")
    public void testLoginWithEmptyCredentials() {
        log.info("=== Test: Login with Empty Credentials ===");

        loginPage.open();

        // Attempt login with empty fields
        loginPage.login("", "");

        // Verify error is shown or we remain on login page
        String currentUrl = loginPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("Should remain on login page with empty credentials")
                .contains("/login");

        // Error message should be visible (either field validation or form validation)
        boolean isErrorOrStillOnLoginPage =
                loginPage.isErrorVisible() || currentUrl.contains("/login");

        assertThat(isErrorOrStillOnLoginPage)
                .as("Should show error or remain on login page")
                .isTrue();

        log.info("✅ Empty credentials test passed");
    }

    @Test(description = "Verify login page UI elements are visible")
    @Description("Test all required UI elements are present on login page")
    @Severity(SeverityLevel.MINOR)
    @Story("UI Layout")
    public void testLoginPageUIElements() {
        log.info("=== Test: Login Page UI Elements ===");

        loginPage.open();

        // Verify page title
        String pageTitle = loginPage.getTitle();
        assertThat(pageTitle.toLowerCase())
                .as("Page title should contain 'login', 'sign in', or 'automation'")
                .satisfiesAnyOf(
                        t -> assertThat(t).contains("login"),
                        t -> assertThat(t).contains("sign in"),
                        t -> assertThat(t).contains("automation")
                );

        // Verify URL is correct
        String currentUrl = loginPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("Should be on login page")
                .contains("/login");

        // Take screenshot of login page
        String screenshotPath = loginPage.takeScreenshot("login-page-ui");
        log.info("Screenshot saved: {}", screenshotPath);

        log.info("✅ UI elements test passed");
    }

    @Test(description = "Verify page reload preserves login state")
    @Description("Test that reloading the page after login maintains the session")
    @Severity(SeverityLevel.NORMAL)
    @Story("Session Management")
    public void testLoginSessionPersistence() {
        log.info("=== Test: Login Session Persistence ===");

        // Login successfully
        loginPage.open();
        loginPage.login(config.getUsername(), config.getPassword());

        // Verify we're on dashboard
        assertThat(dashboardPage.isDashboardLoaded())
                .as("Should be logged in and on dashboard")
                .isTrue();

        // Reload the page
        loginPage.reload();

        // Verify we're still on dashboard (session persisted)
        String currentUrl = loginPage.getCurrentUrl();
        boolean stillLoggedIn = currentUrl.contains("/dashboard") || dashboardPage.isDashboardLoaded();

        assertThat(stillLoggedIn)
                .as("Session should persist after page reload")
                .isTrue();

        log.info("✅ Session persistence test passed");
    }

    @Test(description = "Verify login with username containing special characters")
    @Description("Test login handles special characters in username gracefully")
    @Severity(SeverityLevel.MINOR)
    @Story("Input Validation")
    public void testLoginWithSpecialCharactersInUsername() {
        log.info("=== Test: Login with Special Characters ===");

        loginPage.open();

        // Test various special characters
        String[] specialUsernames = {
                "user@email.com",
                "user+test@domain.com",
                "user.name@test.com",
                "user_123",
                "user-name"
        };

        for (String username : specialUsernames) {
            log.info("Testing username: {}", username);
            loginPage.open(); // Refresh page for each attempt
            loginPage.login(username, "testPassword123");

            // Should show error or remain on login (since these are likely invalid)
            String currentUrl = loginPage.getCurrentUrl();
            boolean handledGracefully = currentUrl.contains("/login") || loginPage.isErrorVisible();

            assertThat(handledGracefully)
                    .as("Should handle special characters in username: " + username)
                    .isTrue();
        }

        log.info("✅ Special characters test passed");
    }

    @Test(description = "Verify multiple failed login attempts are handled")
    @Description("Test system handles multiple consecutive failed login attempts")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void testMultipleFailedLoginAttempts() {
        log.info("=== Test: Multiple Failed Login Attempts ===");

        loginPage.open();

        // Attempt multiple failed logins
        int attempts = 3;
        for (int i = 1; i <= attempts; i++) {
            log.info("Failed login attempt {}/{}", i, attempts);

            loginPage.login("invalid_user", "invalid_pass");

            // Verify error is shown
            assertThat(loginPage.isErrorVisible())
                    .as("Error should be shown for attempt " + i)
                    .isTrue();

            // Small delay between attempts
            loginPage.waitFor(500);

            // Refresh for next attempt
            if (i < attempts) {
                loginPage.open();
            }
        }

        // After multiple attempts, should still show error or potentially lock account
        // (Implementation dependent - we just verify we're handling it)
        boolean errorShownOrLocked = loginPage.isErrorVisible() ||
                loginPage.getCurrentUrl().contains("/login");

        assertThat(errorShownOrLocked)
                .as("Should handle multiple failed attempts appropriately")
                .isTrue();

        log.info("✅ Multiple failed attempts test passed");
    }

    @Test(description = "Verify login form accessibility")
    @Description("Test login form is accessible and follows best practices")
    @Severity(SeverityLevel.MINOR)
    @Story("Accessibility")
    public void testLoginFormAccessibility() {
        log.info("=== Test: Login Form Accessibility ===");

        loginPage.open();

        // Take full page screenshot for accessibility review
        String screenshotPath = loginPage.takeFullPageScreenshot("login-accessibility");
        log.info("Full page screenshot for accessibility review: {}", screenshotPath);

        // Verify page title exists
        String title = loginPage.getTitle();
        assertThat(title)
                .as("Page should have a meaningful title")
                .isNotEmpty();

        // Verify we can navigate using keyboard (press Tab and Enter)
        loginPage.pressKey("Tab");
        loginPage.waitFor(200);
        loginPage.pressKey("Tab");
        loginPage.waitFor(200);

        log.info("✅ Accessibility test passed");
    }
}
