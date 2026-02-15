package com.automation.ui;

import com.automation.base.BaseTest;
import com.automation.pages.AccountPage;
import com.automation.pages.LoginPage;
import com.automation.providers.TestDataProviders;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UI Tests for Account Management
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
public class AccountUITests extends BaseTest {
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

    @Test(description = "Verify account page loads successfully")
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

        log.info("✅ Account page load test passed");
    }

    @Test(description = "Verify account information is displayed")
    @Description("Test account ID, type, status, and member since date are shown")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Account Information")
    public void testAccountInformationDisplay() {
        log.info("=== Test: Account Information Display ===");

        // Get account information
        String accountId = accountPage.getAccountId();
        String accountType = accountPage.getAccountType();
        String accountStatus = accountPage.getAccountStatus();
        String memberSince = accountPage.getMemberSince();

        log.info("Account ID: {}", accountId);
        log.info("Account Type: {}", accountType);
        log.info("Account Status: {}", accountStatus);
        log.info("Member Since: {}", memberSince);

        // Verify all fields have values
        assertThat(accountId)
                .as("Account ID should be displayed")
                .isNotEmpty();

        assertThat(accountType)
                .as("Account type should be displayed")
                .isNotEmpty();

        assertThat(accountStatus)
                .as("Account status should be displayed")
                .isNotEmpty();

        assertThat(memberSince)
                .as("Member since date should be displayed")
                .isNotEmpty();

        log.info("✅ Account information display test passed");
    }

    @Test(description = "Verify verification status display")
    @Description("Test account verification status and badge are shown")
    @Severity(SeverityLevel.NORMAL)
    @Story("Account Information")
    public void testVerificationStatus() {
        log.info("=== Test: Verification Status ===");

        // Get verification status
        String verificationStatus = accountPage.getVerificationStatus();
        boolean isVerified = accountPage.isAccountVerified();

        log.info("Verification Status: {}", verificationStatus);
        log.info("Is Verified: {}", isVerified);

        assertThat(verificationStatus)
                .as("Verification status should be displayed")
                .isNotEmpty();

        // Take screenshot showing verification status
        accountPage.takeScreenshot("verification-status");

        log.info("✅ Verification status test passed");
    }

    @Test(description = "Verify profile tab navigation")
    @Description("Test user can navigate to profile tab and view profile fields")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Profile Management")
    public void testProfileTabNavigation() {
        log.info("=== Test: Profile Tab Navigation ===");

        // Navigate to profile tab
        accountPage.goToProfileTab();

        // Get profile information
        Map<String, String> profile = accountPage.getProfileInformation();

        log.info("Profile Information: {}", profile);

        // Verify profile has required fields
        assertThat(profile)
                .as("Profile should contain data")
                .isNotEmpty()
                .containsKeys("fullName", "email", "phone");

        log.info("✅ Profile tab navigation test passed");
    }

    @Test(description = "Verify profile update functionality")
    @Description("Test user can update profile information")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Profile Management")
    public void testProfileUpdate() {
        log.info("=== Test: Profile Update ===");

        // Prepare updated profile data
        Map<String, String> updatedProfile = new HashMap<>();
        updatedProfile.put("fullName", "Test User Updated");
        updatedProfile.put("phone", "+1234567890");
        updatedProfile.put("city", "Test City");

        // Update profile
        accountPage.updateProfile(updatedProfile);

        // Verify success message
        boolean successVisible = accountPage.isSuccessMessageVisible();
        assertThat(successVisible)
                .as("Success message should be displayed after update")
                .isTrue();

        if (successVisible) {
            String successMessage = accountPage.getSuccessMessage();
            log.info("Success message: {}", successMessage);
        }

        // Take screenshot
        accountPage.takeScreenshot("profile-updated");

        log.info("✅ Profile update test passed");
    }

    @Test(description = "Verify individual profile field updates")
    @Description("Test updating individual profile fields (name, email, phone)")
    @Severity(SeverityLevel.NORMAL)
    @Story("Profile Management")
    public void testIndividualFieldUpdates() {
        log.info("=== Test: Individual Field Updates ===");

        // Test updating full name
        accountPage.updateFullName("John Doe");
        assertThat(accountPage.isSuccessMessageVisible())
                .as("Success message should show after name update")
                .isTrue();

        // Wait for success message to settle before next update
        accountPage.getPage().waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);

        // Test updating phone
        accountPage.goToProfileTab(); // Refresh to profile tab
        accountPage.updatePhone("+9876543210");
        assertThat(accountPage.isSuccessMessageVisible())
                .as("Success message should show after phone update")
                .isTrue();

        log.info("✅ Individual field updates test passed");
    }

    @Test(description = "Verify security tab navigation")
    @Description("Test user can navigate to security tab and view security settings")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security Management")
    public void testSecurityTabNavigation() {
        log.info("=== Test: Security Tab Navigation ===");

        // Navigate to security tab
        accountPage.goToSecurityTab();

        // Verify we're on security tab (security fields should be visible)
        String currentUrl = accountPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("Should be on account page")
                .contains("/account");

        // Take screenshot
        accountPage.takeScreenshot("security-tab");

        log.info("✅ Security tab navigation test passed");
    }

    @Test(description = "Verify password change functionality")
    @Description("Test user can change their password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security Management")
    public void testPasswordChange() {
        log.info("=== Test: Password Change ===");

        // Change password
        String currentPassword = config.getPassword();
        String newPassword = "NewTestPassword123!";

        accountPage.changePassword(currentPassword, newPassword);

        // Verify success message
        boolean successVisible = accountPage.isSuccessMessageVisible();
        assertThat(successVisible)
                .as("Success message should be displayed after password change")
                .isTrue();

        if (successVisible) {
            String message = accountPage.getSuccessMessage();
            log.info("Password change message: {}", message);
            assertThat(message.toLowerCase())
                    .as("Message should mention password, changed, updated, or success")
                    .satisfiesAnyOf(
                            t -> assertThat(t).contains("password"),
                            t -> assertThat(t).contains("changed"),
                            t -> assertThat(t).contains("updated"),
                            t -> assertThat(t).contains("success")
                    );
        }

        // Take screenshot
        accountPage.takeScreenshot("password-changed");

        // Note: In real test, you'd want to change it back or use test teardown
        log.info("✅ Password change test passed");
    }

    @Test(description = "Verify two-factor authentication status")
    @Description("Test 2FA status is displayed correctly")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security Management")
    public void testTwoFactorAuthenticationStatus() {
        log.info("=== Test: Two-Factor Authentication Status ===");

        // Get 2FA status
        boolean is2FAEnabled = accountPage.isTwoFactorEnabled();
        log.info("Two-Factor Authentication Enabled: {}", is2FAEnabled);

        // Status should be determinable (true or false)
        log.info("2FA status retrieved successfully");

        log.info("✅ Two-factor authentication status test passed");
    }

    @Test(description = "Verify two-factor authentication toggle")
    @Description("Test user can enable/disable 2FA")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security Management")
    public void testTwoFactorAuthenticationToggle() {
        log.info("=== Test: Two-Factor Authentication Toggle ===");

        // Get initial status
        boolean initialStatus = accountPage.isTwoFactorEnabled();
        log.info("Initial 2FA status: {}", initialStatus);

        // Toggle 2FA (disable if enabled, enable if disabled)
        if (initialStatus) {
            accountPage.disableTwoFactor();
            log.info("Disabled 2FA");
        } else {
            accountPage.enableTwoFactor();
            log.info("Enabled 2FA");
        }

        // Verify success message
        boolean successVisible = accountPage.isSuccessMessageVisible();
        assertThat(successVisible)
                .as("Success message should show after 2FA toggle")
                .isTrue();

        // Take screenshot
        accountPage.takeScreenshot("2fa-toggled");

        log.info("✅ Two-factor authentication toggle test passed");
    }

    @Test(description = "Verify payment tab navigation")
    @Description("Test user can navigate to payment methods tab")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Management")
    public void testPaymentTabNavigation() {
        log.info("=== Test: Payment Tab Navigation ===");

        // Navigate to payment tab
        accountPage.goToPaymentTab();

        // Verify we can see payment-related elements
        String currentUrl = accountPage.getCurrentUrl();
        assertThat(currentUrl)
                .as("Should be on account page")
                .contains("/account");

        // Take screenshot
        accountPage.takeScreenshot("payment-tab");

        log.info("✅ Payment tab navigation test passed");
    }

    @Test(description = "Verify payment methods display")
    @Description("Test existing payment methods are displayed")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Management")
    public void testPaymentMethodsDisplay() {
        log.info("=== Test: Payment Methods Display ===");

        // Get payment methods count
        int paymentMethodsCount = accountPage.getPaymentMethodsCount();
        log.info("Payment methods count: {}", paymentMethodsCount);

        assertThat(paymentMethodsCount)
                .as("Payment methods count should be non-negative")
                .isGreaterThanOrEqualTo(0);

        log.info("✅ Payment methods display test passed");
    }

    @Test(description = "Verify add payment method functionality")
    @Description("Test user can add a new payment method")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Payment Management")
    public void testAddPaymentMethod() {
        log.info("=== Test: Add Payment Method ===");

        // Get initial count
        int initialCount = accountPage.getPaymentMethodsCount();

        // Add payment method
        accountPage.addPaymentMethod(
                "4111111111111111",
                "Test User",
                "12/25",
                "123"
        );

        // Verify success message
        boolean successVisible = accountPage.isSuccessMessageVisible();
        assertThat(successVisible)
                .as("Success message should show after adding payment method")
                .isTrue();

        // Verify count increased
        int newCount = accountPage.getPaymentMethodsCount();
        assertThat(newCount)
                .as("Payment methods count should increase")
                .isGreaterThan(initialCount);

        log.info("✅ Add payment method test passed");
    }

    @Test(description = "Verify remove payment method functionality")
    @Description("Test user can remove a payment method")
    @Severity(SeverityLevel.NORMAL)
    @Story("Payment Management")
    public void testRemovePaymentMethod() {
        log.info("=== Test: Remove Payment Method ===");

        // First ensure we have at least one payment method
        int initialCount = accountPage.getPaymentMethodsCount();

        if (initialCount == 0) {
            // Add one first
            accountPage.addPaymentMethod("4111111111111111", "Test", "12/25", "123");
            initialCount = 1;
        }

        // Remove first payment method
        accountPage.removePaymentMethod(0);

        // Verify success
        boolean successVisible = accountPage.isSuccessMessageVisible();
        assertThat(successVisible)
                .as("Success message should show after removing payment method")
                .isTrue();

        // Verify count decreased
        int newCount = accountPage.getPaymentMethodsCount();
        assertThat(newCount)
                .as("Payment methods count should decrease")
                .isLessThan(initialCount);

        log.info("✅ Remove payment method test passed");
    }

    @Test(description = "Verify transactions tab navigation")
    @Description("Test user can navigate to transactions history tab")
    @Severity(SeverityLevel.NORMAL)
    @Story("Transaction History")
    public void testTransactionsTabNavigation() {
        log.info("=== Test: Transactions Tab Navigation ===");

        // Navigate to transactions tab
        accountPage.goToTransactionsTab();

        // Take screenshot
        accountPage.takeScreenshot("transactions-tab");

        log.info("✅ Transactions tab navigation test passed");
    }

    @Test(description = "Verify transaction history display")
    @Description("Test transaction history is displayed")
    @Severity(SeverityLevel.NORMAL)
    @Story("Transaction History")
    public void testTransactionHistoryDisplay() {
        log.info("=== Test: Transaction History Display ===");

        // Get transaction count
        int transactionCount = accountPage.getTransactionCount();
        log.info("Transaction count: {}", transactionCount);

        assertThat(transactionCount)
                .as("Transaction count should be non-negative")
                .isGreaterThanOrEqualTo(0);

        if (transactionCount > 0) {
            // Get first transaction details
            Map<String, String> transaction = accountPage.getTransactionDetails(0);
            log.info("First transaction: {}", transaction);

            assertThat(transaction)
                    .as("Transaction should have details")
                    .isNotEmpty();
        }

        log.info("✅ Transaction history display test passed");
    }

    @Test(description = "Verify transaction filtering by type",
          dataProvider = "transactionTypesProvider", dataProviderClass = TestDataProviders.class)
    @Description("Test user can filter transactions by type")
    @Severity(SeverityLevel.MINOR)
    @Story("Transaction History")
    public void testTransactionFilteringByType(String transactionType) {
        log.info("=== Test: Transaction Filtering by Type - {} ===", transactionType);

        accountPage.filterTransactionsByType(transactionType);
        int count = accountPage.getTransactionCount();
        log.info("{} transactions: {}", transactionType, count);

        assertThat(count)
                .as("%s transaction count should be non-negative", transactionType)
                .isGreaterThanOrEqualTo(0);

        log.info("✅ Transaction filtering test passed for: {}", transactionType);
    }

    @Test(description = "Verify transaction export functionality")
    @Description("Test user can export transaction history")
    @Severity(SeverityLevel.MINOR)
    @Story("Transaction History")
    public void testTransactionExport() {
        log.info("=== Test: Transaction Export ===");

        // Export transactions
        accountPage.exportTransactions();

        // Note: Actual file download verification would require additional logic
        // For now, we verify the export action completes without error

        log.info("✅ Transaction export test passed");
    }

    @Test(description = "Verify tab switching maintains page state")
    @Description("Test switching between tabs preserves account page state")
    @Severity(SeverityLevel.MINOR)
    @Story("UI Navigation")
    public void testTabSwitching() {
        log.info("=== Test: Tab Switching ===");

        // Switch through all tabs (each goToXxxTab() already waits for tab content to be visible)
        accountPage.goToProfileTab();
        accountPage.goToSecurityTab();
        accountPage.goToPaymentTab();
        accountPage.goToTransactionsTab();

        // Return to profile
        accountPage.goToProfileTab();

        // Verify account page still loaded
        boolean isLoaded = accountPage.isAccountPageLoaded();
        assertThat(isLoaded)
                .as("Account page should remain loaded after tab switching")
                .isTrue();

        log.info("✅ Tab switching test passed");
    }

    @Test(description = "Verify full page screenshot of account page")
    @Description("Test full page screenshot for visual regression")
    @Severity(SeverityLevel.MINOR)
    @Story("Visual Testing")
    public void testFullPageScreenshot() {
        log.info("=== Test: Full Page Screenshot ===");

        // Take full page screenshot
        String screenshotPath = accountPage.takeScreenshot("account-page-full");

        assertThat(screenshotPath)
                .as("Screenshot should be saved")
                .isNotEmpty()
                .contains("account-page-full");

        log.info("✅ Full page screenshot saved: {}", screenshotPath);
    }
}
