package com.automation.pages;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Account Page Object
 * Handles account management operations:
 * - View account information
 * - Update profile details
 * - Manage security settings
 * - View transaction history
 * - Manage payment methods
 */
@Slf4j
public class AccountPage extends BasePage {
    // Selectors - Navigation
    private static final String ACCOUNT_TAB = "a:has-text('Account'), .account-tab";
    private static final String PROFILE_TAB = "button:has-text('Profile'), .profile-tab";
    private static final String SECURITY_TAB = "button:has-text('Security'), .security-tab";
    private static final String PAYMENT_TAB = "button:has-text('Payment'), .payment-tab";
    private static final String TRANSACTIONS_TAB = "button:has-text('Transactions'), .transactions-tab";

    // Selectors - Profile Information
    private static final String FULL_NAME_INPUT = "input[name='fullName'], #fullName";
    private static final String EMAIL_INPUT = "input[name='email'], #email";
    private static final String PHONE_INPUT = "input[name='phone'], #phone";
    private static final String DATE_OF_BIRTH_INPUT = "input[name='dob'], #dateOfBirth";
    private static final String ADDRESS_INPUT = "input[name='address'], #address";
    private static final String CITY_INPUT = "input[name='city'], #city";
    private static final String COUNTRY_SELECT = "select[name='country'], #country";
    private static final String POSTAL_CODE_INPUT = "input[name='postalCode'], #postalCode";
    private static final String SAVE_PROFILE_BUTTON = "button:has-text('Save'), button.save-profile";

    // Selectors - Account Summary
    private static final String ACCOUNT_ID = ".account-id, #accountId";
    private static final String ACCOUNT_TYPE = ".account-type, #accountType";
    private static final String ACCOUNT_STATUS = ".account-status, #accountStatus";
    private static final String MEMBER_SINCE = ".member-since, #memberSince";
    private static final String VERIFICATION_STATUS = ".verification-status, #verificationStatus";
    private static final String VERIFICATION_BADGE = ".verified-badge, .verification-icon";

    // Selectors - Security Settings
    private static final String CURRENT_PASSWORD_INPUT = "input[name='currentPassword'], #currentPassword";
    private static final String NEW_PASSWORD_INPUT = "input[name='newPassword'], #newPassword";
    private static final String CONFIRM_PASSWORD_INPUT = "input[name='confirmPassword'], #confirmPassword";
    private static final String CHANGE_PASSWORD_BUTTON = "button:has-text('Change Password'), .change-password-btn";
    private static final String TWO_FACTOR_TOGGLE = "input[name='twoFactor'], #twoFactorAuth";
    private static final String TWO_FACTOR_STATUS = ".two-factor-status, #twoFactorStatus";
    private static final String ENABLE_2FA_BUTTON = "button:has-text('Enable 2FA'), .enable-2fa";
    private static final String DISABLE_2FA_BUTTON = "button:has-text('Disable 2FA'), .disable-2fa";

    // Selectors - Payment Methods
    private static final String ADD_PAYMENT_METHOD_BUTTON = "button:has-text('Add Payment Method'), .add-payment";
    private static final String PAYMENT_METHOD_CARD = ".payment-method-card, .payment-card";
    private static final String CARD_NUMBER_INPUT = "input[name='cardNumber'], #cardNumber";
    private static final String CARD_HOLDER_INPUT = "input[name='cardHolder'], #cardHolder";
    private static final String EXPIRY_DATE_INPUT = "input[name='expiryDate'], #expiryDate";
    private static final String CVV_INPUT = "input[name='cvv'], #cvv";
    private static final String SAVE_CARD_BUTTON = "button:has-text('Save Card'), .save-card";
    private static final String REMOVE_PAYMENT_BUTTON = "button:has-text('Remove'), .remove-payment";

    // Selectors - Transaction History
    private static final String TRANSACTION_TABLE = ".transaction-table, #transactionHistory";
    private static final String TRANSACTION_ROW = ".transaction-row, tr.transaction";
    private static final String TRANSACTION_TYPE_FILTER = "select[name='type'], #transactionType";
    private static final String DATE_FROM_INPUT = "input[name='dateFrom'], #dateFrom";
    private static final String DATE_TO_INPUT = "input[name='dateTo'], #dateTo";
    private static final String FILTER_BUTTON = "button:has-text('Filter'), .filter-btn";
    private static final String EXPORT_BUTTON = "button:has-text('Export'), .export-btn";

    // Selectors - Notifications
    private static final String SUCCESS_MESSAGE = ".success-message, .alert-success";
    private static final String ERROR_MESSAGE = ".error-message, .alert-danger";
    private static final String INFO_MESSAGE = ".info-message, .alert-info";

    @Step("Navigate to account page")
    public AccountPage open() {
        navigateTo(config.getBaseUrl() + "/account");
        waitForPageLoad();
        waitForVisible(ACCOUNT_TAB);
        log.info("Account page loaded");
        return this;
    }

    // ========== Navigation ==========

    @Step("Navigate to profile tab")
    public AccountPage goToProfileTab() {
        log.info("Navigating to profile tab");
        click(PROFILE_TAB);
        waitForVisible(FULL_NAME_INPUT);
        return this;
    }

    @Step("Navigate to security tab")
    public AccountPage goToSecurityTab() {
        log.info("Navigating to security tab");
        click(SECURITY_TAB);
        waitForVisible(CURRENT_PASSWORD_INPUT);
        return this;
    }

    @Step("Navigate to payment tab")
    public AccountPage goToPaymentTab() {
        log.info("Navigating to payment tab");
        click(PAYMENT_TAB);
        waitForVisible(ADD_PAYMENT_METHOD_BUTTON);
        return this;
    }

    @Step("Navigate to transactions tab")
    public AccountPage goToTransactionsTab() {
        log.info("Navigating to transactions tab");
        click(TRANSACTIONS_TAB);
        waitForVisible(TRANSACTION_TABLE);
        return this;
    }

    // ========== Account Information ==========

    @Step("Get account ID")
    public String getAccountId() {
        String accountId = getText(ACCOUNT_ID);
        log.info("Account ID: {}", accountId);
        return accountId;
    }

    @Step("Get account type")
    public String getAccountType() {
        String accountType = getText(ACCOUNT_TYPE);
        log.info("Account type: {}", accountType);
        return accountType;
    }

    @Step("Get account status")
    public String getAccountStatus() {
        String status = getText(ACCOUNT_STATUS);
        log.info("Account status: {}", status);
        return status;
    }

    @Step("Get member since date")
    public String getMemberSince() {
        String memberSince = getText(MEMBER_SINCE);
        log.info("Member since: {}", memberSince);
        return memberSince;
    }

    @Step("Get verification status")
    public String getVerificationStatus() {
        String status = getText(VERIFICATION_STATUS);
        log.info("Verification status: {}", status);
        return status;
    }

    @Step("Check if account is verified")
    public boolean isAccountVerified() {
        boolean verified = isVisible(VERIFICATION_BADGE);
        log.info("Account verified: {}", verified);
        return verified;
    }

    // ========== Profile Management ==========

    @Step("Update profile with details")
    public AccountPage updateProfile(Map<String, String> profileData) {
        log.info("Updating profile with: {}", profileData);
        goToProfileTab();

        if (profileData.containsKey("fullName")) {
            fill(FULL_NAME_INPUT, profileData.get("fullName"));
        }
        if (profileData.containsKey("email")) {
            fill(EMAIL_INPUT, profileData.get("email"));
        }
        if (profileData.containsKey("phone")) {
            fill(PHONE_INPUT, profileData.get("phone"));
        }
        if (profileData.containsKey("dateOfBirth")) {
            fill(DATE_OF_BIRTH_INPUT, profileData.get("dateOfBirth"));
        }
        if (profileData.containsKey("address")) {
            fill(ADDRESS_INPUT, profileData.get("address"));
        }
        if (profileData.containsKey("city")) {
            fill(CITY_INPUT, profileData.get("city"));
        }
        if (profileData.containsKey("country")) {
            selectByText(COUNTRY_SELECT, profileData.get("country"));
        }
        if (profileData.containsKey("postalCode")) {
            fill(POSTAL_CODE_INPUT, profileData.get("postalCode"));
        }

        click(SAVE_PROFILE_BUTTON);
        waitForSuccessMessage();
        log.info("[PASS] Profile updated successfully");
        return this;
    }

    @Step("Get profile information")
    public Map<String, String> getProfileInformation() {
        log.info("Retrieving profile information");
        goToProfileTab();

        Map<String, String> profile = new HashMap<>();
        profile.put("fullName", getValue(FULL_NAME_INPUT));
        profile.put("email", getValue(EMAIL_INPUT));
        profile.put("phone", getValue(PHONE_INPUT));
        profile.put("address", getValue(ADDRESS_INPUT));
        profile.put("city", getValue(CITY_INPUT));
        profile.put("postalCode", getValue(POSTAL_CODE_INPUT));

        log.info("Retrieved profile: {}", profile);
        return profile;
    }

    @Step("Update full name to: {fullName}")
    public AccountPage updateFullName(String fullName) {
        log.info("Updating full name to: {}", fullName);
        goToProfileTab();
        fill(FULL_NAME_INPUT, fullName);
        click(SAVE_PROFILE_BUTTON);
        waitForSuccessMessage();
        return this;
    }

    @Step("Update email to: {email}")
    public AccountPage updateEmail(String email) {
        log.info("Updating email to: {}", email);
        goToProfileTab();
        fill(EMAIL_INPUT, email);
        click(SAVE_PROFILE_BUTTON);
        waitForSuccessMessage();
        return this;
    }

    @Step("Update phone to: {phone}")
    public AccountPage updatePhone(String phone) {
        log.info("Updating phone to: {}", phone);
        goToProfileTab();
        fill(PHONE_INPUT, phone);
        click(SAVE_PROFILE_BUTTON);
        waitForSuccessMessage();
        return this;
    }

    // ========== Security Management ==========

    @Step("Change password")
    public AccountPage changePassword(String currentPassword, String newPassword) {
        log.info("Changing password");
        goToSecurityTab();

        fill(CURRENT_PASSWORD_INPUT, currentPassword);
        fill(NEW_PASSWORD_INPUT, newPassword);
        fill(CONFIRM_PASSWORD_INPUT, newPassword);
        click(CHANGE_PASSWORD_BUTTON);
        waitForSuccessMessage();

        log.info("[PASS] Password changed successfully");
        return this;
    }

    @Step("Get two-factor authentication status")
    public boolean isTwoFactorEnabled() {
        goToSecurityTab();
        String status = getText(TWO_FACTOR_STATUS);
        boolean enabled = status.toLowerCase().contains("enabled");
        log.info("Two-factor authentication enabled: {}", enabled);
        return enabled;
    }

    @Step("Enable two-factor authentication")
    public AccountPage enableTwoFactor() {
        log.info("Enabling two-factor authentication");
        goToSecurityTab();

        if (!isTwoFactorEnabled()) {
            click(ENABLE_2FA_BUTTON);
            waitForSuccessMessage();
            log.info("[PASS] Two-factor authentication enabled");
        } else {
            log.info("Two-factor authentication already enabled");
        }

        return this;
    }

    @Step("Disable two-factor authentication")
    public AccountPage disableTwoFactor() {
        log.info("Disabling two-factor authentication");
        goToSecurityTab();

        if (isTwoFactorEnabled()) {
            click(DISABLE_2FA_BUTTON);
            waitForSuccessMessage();
            log.info("[PASS] Two-factor authentication disabled");
        } else {
            log.info("Two-factor authentication already disabled");
        }

        return this;
    }

    // ========== Payment Methods ==========

    @Step("Add payment method")
    public AccountPage addPaymentMethod(String cardNumber, String cardHolder, String expiryDate, String cvv) {
        log.info("Adding payment method");
        goToPaymentTab();

        click(ADD_PAYMENT_METHOD_BUTTON);
        waitForVisible(CARD_NUMBER_INPUT);

        fill(CARD_NUMBER_INPUT, cardNumber);
        fill(CARD_HOLDER_INPUT, cardHolder);
        fill(EXPIRY_DATE_INPUT, expiryDate);
        fill(CVV_INPUT, cvv);

        click(SAVE_CARD_BUTTON);
        waitForSuccessMessage();

        log.info("[PASS] Payment method added successfully");
        return this;
    }

    @Step("Get payment methods count")
    public int getPaymentMethodsCount() {
        goToPaymentTab();
        int count = getElementCount(PAYMENT_METHOD_CARD);
        log.info("Payment methods count: {}", count);
        return count;
    }

    @Step("Remove payment method at index: {index}")
    public AccountPage removePaymentMethod(int index) {
        log.info("Removing payment method at index: {}", index);
        goToPaymentTab();

        page.locator(PAYMENT_METHOD_CARD).nth(index)
                .locator(REMOVE_PAYMENT_BUTTON)
                .click();

        waitForSuccessMessage();
        log.info("[PASS] Payment method removed");
        return this;
    }

    // ========== Transaction History ==========

    @Step("Get transaction count")
    public int getTransactionCount() {
        goToTransactionsTab();
        int count = getElementCount(TRANSACTION_ROW);
        log.info("Transaction count: {}", count);
        return count;
    }

    @Step("Filter transactions by type: {type}")
    public AccountPage filterTransactionsByType(String type) {
        log.info("Filtering transactions by type: {}", type);
        goToTransactionsTab();
        selectByText(TRANSACTION_TYPE_FILTER, type);
        click(FILTER_BUTTON);
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        return this;
    }

    @Step("Filter transactions by date range")
    public AccountPage filterTransactionsByDateRange(String fromDate, String toDate) {
        log.info("Filtering transactions from {} to {}", fromDate, toDate);
        goToTransactionsTab();
        fill(DATE_FROM_INPUT, fromDate);
        fill(DATE_TO_INPUT, toDate);
        click(FILTER_BUTTON);
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        return this;
    }

    @Step("Export transactions")
    public AccountPage exportTransactions() {
        log.info("Exporting transactions");
        goToTransactionsTab();
        click(EXPORT_BUTTON);
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        log.info("[PASS] Transactions exported");
        return this;
    }

    @Step("Get transaction details at index: {index}")
    public Map<String, String> getTransactionDetails(int index) {
        log.info("Getting transaction details at index: {}", index);
        goToTransactionsTab();

        Map<String, String> transaction = new HashMap<>();
        var row = page.locator(TRANSACTION_ROW).nth(index);

        transaction.put("type", row.locator("td:nth-child(1)").textContent());
        transaction.put("amount", row.locator("td:nth-child(2)").textContent());
        transaction.put("date", row.locator("td:nth-child(3)").textContent());
        transaction.put("status", row.locator("td:nth-child(4)").textContent());

        log.info("Transaction {}: {}", index, transaction);
        return transaction;
    }

    // ========== Validation & Helpers ==========

    @Step("Wait for success message")
    private void waitForSuccessMessage() {
        log.debug("Waiting for success message");
        waitForVisible(SUCCESS_MESSAGE);
    }

    @Step("Check if success message is visible")
    public boolean isSuccessMessageVisible() {
        return isVisible(SUCCESS_MESSAGE);
    }

    @Step("Check if error message is visible")
    public boolean isErrorMessageVisible() {
        return isVisible(ERROR_MESSAGE);
    }

    @Step("Get success message")
    public String getSuccessMessage() {
        String message = getText(SUCCESS_MESSAGE);
        log.info("Success message: {}", message);
        return message;
    }

    @Step("Get error message")
    public String getErrorMessage() {
        String message = getText(ERROR_MESSAGE);
        log.error("Error message: {}", message);
        return message;
    }

    @Step("Verify account page is loaded")
    public boolean isAccountPageLoaded() {
        return isVisible(ACCOUNT_TAB)
                && (isVisible(PROFILE_TAB) || isVisible(ACCOUNT_ID));
    }
}
