package com.automation.base;

import com.automation.factory.BrowserFactory;
import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.nio.file.Paths;

@Slf4j
public class BaseWebTest extends BaseTest {

    protected Page page;
    protected String currentBrowser;

    // ========================================
    // BEFORE METHOD - Browser Setup + Common Operations
    // ========================================
    @BeforeMethod
    @Parameters({"browser"})
    public void setupBrowser(@Optional("chromium") String browser) {
        this.currentBrowser = browser;
        log.info("Setting up {} browser for web test", browser);

        // 1. Initialize Playwright and launch browser
        PlaywrightManager.initPlaywright();
        BrowserFactory.launchBrowser(browser);
        page = PlaywrightManager.getPage();

        // 2. Navigate to base URL (home page)
        navigateToHomePage();

        // 3. Wait for page to be fully loaded
        waitForPageLoad();

        log.info("Browser {} setup completed. Navigated to: {}", browser, config.getBaseUrl());

        // 4. Call hook for additional setup (can be overridden by test classes)
        performAdditionalSetup();
    }

    // ========================================
    // AFTER METHOD - Common Cleanup + Browser Teardown
    // ========================================
    @AfterMethod
    public void tearDownBrowser() {
        log.info("Starting teardown for {} browser", currentBrowser);

        // 1. Call hook for additional cleanup (can be overridden by test classes)
        performAdditionalCleanup();

        // 2. Close browser
        PlaywrightManager.quitPlaywright();

        log.info("Teardown completed for {} browser", currentBrowser);
    }

    // ========================================
    // COMMON REUSABLE OPERATIONS
    // ========================================

    /**
     * Navigate to the application home page
     */
    protected void navigateToHomePage() {
        log.info("Navigating to home page: {}", config.getBaseUrl());
        page.navigate(config.getBaseUrl());
    }

    /**
     * Wait for page to be fully loaded
     */
    protected void waitForPageLoad() {
        page.waitForLoadState();
        log.debug("Page load state reached");
    }

    /**
     * Navigate to a specific URL
     * @param url The URL to navigate to
     */
    protected void navigateToUrl(String url) {
        log.info("Navigating to URL: {}", url);
        page.navigate(url);
        waitForPageLoad();
    }

    /**
     * Reload the current page
     */
    protected void reloadPage() {
        log.info("Reloading current page");
        page.reload();
        waitForPageLoad();
    }

    /**
     * Go back to previous page
     */
    protected void goBack() {
        log.info("Navigating back to previous page");
        page.goBack();
        waitForPageLoad();
    }

    /**
     * Clear browser cookies
     */
    protected void clearCookies() {
        log.info("Clearing browser cookies");
        page.context().clearCookies();
    }

    /**
     * Clear browser storage (localStorage and sessionStorage)
     */
    protected void clearBrowserStorage() {
        log.info("Clearing browser storage");
        page.evaluate("() => { localStorage.clear(); sessionStorage.clear(); }");
    }

    /**
     * Take a screenshot
     * @param screenshotName Name for the screenshot file
     */
    protected void takeScreenshot(String screenshotName) {
        try {
            String screenshotPath = "target/screenshots/" + screenshotName + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
            log.info("Screenshot saved: {}", screenshotPath);
        } catch (Exception e) {
            log.error("Failed to take screenshot: {}", screenshotName, e);
        }
    }

    /**
     * Get current page URL
     * @return Current URL
     */
    protected String getCurrentUrl() {
        return page.url();
    }

    /**
     * Get current page title
     * @return Page title
     */
    protected String getPageTitle() {
        return page.title();
    }

    /**
     * Reset to home page (useful for test cleanup)
     */
    protected void resetToHomePage() {
        if (!getCurrentUrl().equals(config.getBaseUrl())) {
            log.info("Resetting to home page");
            navigateToHomePage();
        }
    }

    // ========================================
    // HOOKS - Can be overridden by test classes
    // ========================================

    /**
     * Hook method called after browser setup but before test execution
     * Override this method in test classes to perform additional setup
     */
    protected void performAdditionalSetup() {
        // Empty implementation - meant to be overridden by test classes
        log.debug("No additional setup required");
    }

    /**
     * Hook method called before browser teardown
     * Override this method in test classes to perform additional cleanup
     */
    protected void performAdditionalCleanup() {
        // Empty implementation - meant to be overridden by test classes
        log.debug("No additional cleanup required");
    }

    // ========================================
    // LOGIN/LOGOUT OPERATIONS (Template for future use)
    // ========================================

    /**
     * Perform login operation
     * @param username Username
     * @param password Password
     */
    protected void login(String username, String password) {
        log.info("Performing login for user: {}", username);
        // TODO: Implement login logic when authentication is added
        // Example:
        // page.fill("input[name='username']", username);
        // page.fill("input[name='password']", password);
        // page.click("button[type='submit']");
        // waitForPageLoad();
    }

    /**
     * Perform logout operation
     */
    protected void logout() {
        log.info("Performing logout");
        // TODO: Implement logout logic when authentication is added
        // Example:
        // page.click("button.logout");
        // waitForPageLoad();
    }

    /**
     * Check if user is logged in
     * @return true if logged in, false otherwise
     */
    protected boolean isLoggedIn() {
        // TODO: Implement login status check
        // Example: return page.isVisible(".user-profile");
        return false;
    }

}
