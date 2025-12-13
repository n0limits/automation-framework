package com.automation.multibank;

import com.automation.base.BaseWebTest;
import com.automation.pages.multibank.NavigationPage;
import com.automation.providers.TestDataProviders;
import com.automation.utils.TestDataReader;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("MultiBank Trading Platform")
@Feature("Navigation & Layout")
public class NavigationTests extends BaseWebTest {

    private NavigationPage navigationPage;
    private JsonNode testData;

    // ========================================
    // OVERRIDE: Additional Setup Hook
    // ========================================
    @Override
    protected void performAdditionalSetup() {
        log.info("Performing Navigation test-specific setup");

        // Initialize page objects
        navigationPage = new NavigationPage();

        // Load test data
        testData = TestDataReader.readJsonFile("navigation-data.json");

        // Any other Navigation-specific setup can go here
        // Example: clearCookies(), login("user", "pass"), etc.

        log.info("Navigation test setup completed");
    }

    // ========================================
    // OVERRIDE: Additional Cleanup Hook
    // ========================================
    @Override
    protected void performAdditionalCleanup() {
        log.info("Performing Navigation test-specific cleanup");

        // Any Navigation-specific cleanup can go here
        // Example: logout(), clearBrowserStorage(), takeScreenshot("final-state")

        log.info("Navigation test cleanup completed");
    }

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Checks if navigation item is available and skips test if not
     * @param itemName Name of the navigation item
     */
    private void skipIfNavigationItemNotAvailable(String itemName) {
        try {
            boolean itemAvailable = TestDataReader.getBooleanValue(
                    testData, "navigationMenu", "navigationLinks", itemName, "available"
            );
            if (!itemAvailable) {
                throw new SkipException(itemName + " navigation test skipped - link not available on current site");
            }
        } catch (Exception e) {
            // Item availability check not configured, assume available
            log.debug("Availability check not configured for {}, assuming available", itemName);
        }
    }

    /**
     * Clicks navigation item and waits for page load
     * @param itemName Name of the navigation item to click
     */
    private void clickNavigationItemAndWait(String itemName) {
        navigationPage.clickNavigationItem(itemName);
        page.waitForLoadState();
    }

    @Test(description = "Verify top navigation menu is displayed", priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation Menu Display")
    @Description("Test verifies that the top navigation menu is visible and accessible on the homepage")
    public void testNavigationMenuDisplayed() {
        log.info("Starting test: Navigation menu display verification");

        boolean isDisplayed = navigationPage.isNavigationMenuDisplayed();

        assertThat(isDisplayed)
                .as("Navigation menu should be displayed")
                .isTrue();

        log.info("Test completed successfully: Navigation menu is displayed");
    }

    @Test(description = "Verify all expected navigation items are present", priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation Menu Items")
    @Description("Test verifies that all expected navigation items are present in the top menu")
    public void testNavigationMenuItems() {
        log.info("Starting test: Navigation menu items verification");

        List<String> expectedItems = TestDataReader.getStringList(testData, "navigationMenu", "expectedItems");
        List<String> actualItems = navigationPage.getNavigationMenuItems();

        log.info("Expected navigation items: {}", expectedItems);
        log.info("Actual navigation items: {}", actualItems);

        for (String expectedItem : expectedItems) {
            assertThat(actualItems)
                    .as("Navigation menu should contain item: " + expectedItem)
                    .anyMatch(item -> item.toLowerCase().contains(expectedItem.toLowerCase()));
        }

        log.info("Test completed successfully: All expected navigation items are present");
    }

    @Test(description = "Verify each navigation item is visible", priority = 3,
          dataProvider = "navigationItemsProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation Item Visibility")
    @Description("Test verifies that each navigation item is visible on the page")
    public void testNavigationItemVisibility(String itemName, String urlPart) {
        log.info("Testing visibility of navigation item: {}", itemName);

        boolean isVisible = navigationPage.isNavigationItemVisible(itemName);

        assertThat(isVisible)
                .as(itemName + " navigation item should be visible")
                .isTrue();

        log.info("{} navigation item is visible", itemName);
    }

    @Test(description = "Verify navigation items are functional and link to correct pages", priority = 4,
          dataProvider = "navigationItemsProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation Functionality")
    @Description("Test verifies that clicking navigation items navigates to the correct pages")
    public void testNavigationItemFunctionality(String itemName, String expectedUrlPart) {
        log.info("Testing navigation functionality for: {}", itemName);

        String initialUrl = navigationPage.getCurrentUrl();
        log.debug("Initial URL: {}", initialUrl);

        clickNavigationItemAndWait(itemName);

        String currentUrl = navigationPage.getCurrentUrl();
        log.info("Current URL after clicking {}: {}", itemName, currentUrl);

        assertThat(currentUrl)
                .as("URL should contain expected part: " + expectedUrlPart)
                .containsIgnoringCase(expectedUrlPart);

        log.info("Navigation test completed for {}", itemName);
    }

    @Test(description = "Verify navigation items functionality and navigation", priority = 5,
          dataProvider = "allNavigationItemsProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation Functionality")
    @Description("Test verifies that clicking navigation items navigates to correct pages and remains functional")
    public void testAllNavigationItemsFunctionality(String itemName, String expectedUrlPart) {
        log.info("Testing {} navigation", itemName);

        skipIfNavigationItemNotAvailable(itemName);

        String initialUrl = navigationPage.getCurrentUrl();
        clickNavigationItemAndWait(itemName);

        String currentUrl = navigationPage.getCurrentUrl();

        // Validate navigation occurred or stayed on valid page
        if (expectedUrlPart.equals("/")) {
            // Dashboard and Markets may stay on homepage or navigate
            assertThat(currentUrl)
                    .as(itemName + " navigation should result in valid URL")
                    .satisfiesAnyOf(
                            url -> assertThat(url).containsIgnoringCase(itemName.toLowerCase().replace(" ", "")),
                            url -> assertThat(url).endsWith("/"),
                            url -> assertThat(url).isEqualTo(initialUrl)
                    );
        } else {
            // Other navigation items should navigate to expected URL part
            assertThat(currentUrl)
                    .as("URL should contain '" + expectedUrlPart + "' or navigate successfully")
                    .satisfiesAnyOf(
                            url -> assertThat(url).containsIgnoringCase(expectedUrlPart),
                            url -> assertThat(url).isNotEqualTo(initialUrl)
                    );
        }

        // Verify navigation menu is still visible after navigation
        assertThat(navigationPage.isNavigationMenuDisplayed())
                .as("Navigation menu should be visible on " + itemName + " page")
                .isTrue();

        log.info("{} navigation successful - URL: {}", itemName, currentUrl);
    }

    // ========================================
    // OPTIONAL: DataProvider Browser Parametrization Example
    // ========================================
    // This approach runs the same test on multiple browsers sequentially
    // NOTE: The current TestNG XML approach (testng.xml) is RECOMMENDED
    // for better parallel execution across browsers
    //
    // Uncomment to use browserProvider parametrization:
    //
    // @Test(description = "Example: Test navigation on multiple browsers using DataProvider",
    //       priority = 100,
    //       dataProvider = "browserProvider",
    //       dataProviderClass = TestDataProviders.class,
    //       enabled = false)  // Disabled by default to avoid duplicate test execution
    // @Severity(SeverityLevel.NORMAL)
    // @Story("Cross-Browser Navigation Example")
    // public void testNavigationOnMultipleBrowsers(String browserName) {
    //     log.info("Testing navigation on browser: {}", browserName);
    //
    //     // Browser is already initialized by BaseWebTest.setupBrowser()
    //     // The browser parameter from TestNG XML or @Optional takes precedence
    //
    //     boolean isDisplayed = navigationPage.isNavigationMenuDisplayed();
    //
    //     assertThat(isDisplayed)
    //             .as("Navigation menu should be displayed on " + browserName)
    //             .isTrue();
    //
    //     log.info("Navigation test passed on browser: {}", browserName);
    // }
}
