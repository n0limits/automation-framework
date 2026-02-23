package com.automation.ui;

import com.automation.base.BaseWebTest;
import com.automation.pages.AccountPage;
import com.automation.pages.LoginPage;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UI Tests for Account Management.
 *
 * <p><b>STATUS: DISABLED</b> — All {@code @Test} annotations are commented out because
 * no login credentials are available for the target environment. Re-enable when
 * credentials or a test account are provisioned.</p>
 *
 * Tests cover:
 * - Profile viewing and updating
 * - Security settings management
 * - Password changes
 * - Two-factor authentication
 * - Payment method management
 * - Transaction history viewing
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Account Management UI")
public class AccountUITests extends BaseWebTest {
    private LoginPage loginPage;
    private AccountPage accountPage;

    @BeforeMethod
    public void setupAndLogin() {
        log.info("===== Setting up Account UI Test =====");

        // Initialize pages
        loginPage = new LoginPage();
        accountPage = new AccountPage();

        // Login before each test
        log.info("Performing login");
        loginPage.open();
        loginPage.login(config.getUsername(), config.getPassword());

        // Navigate to account page
        accountPage.open();
        log.info("Account page ready for testing");
    }

    // @Test(description = "Verify account page loads successfully")
    @Description("Test that account page displays all required sections")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Account Navigation")
    public void testAccountPageLoads() {
        log.info("=== Test: Account Page Loads ===");

        // Verify account page is loaded
        boolean isLoaded = accountPage.isAccountPageLoaded();
        assertThat(isLoaded)
                .as("Account page should be fully loaded")
                .isTrue();

        // Verify URL
        String currentUrl = accountPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("URL should contain 'account'")
                .contains("/account");

        // Take screenshot
        accountPage.takeScreenshot("account-page-loaded");

        log.info("[PASS] Account page load test passed");
    }
}
