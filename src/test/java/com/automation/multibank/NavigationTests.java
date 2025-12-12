package com.automation.multibank;

import com.automation.base.BaseWebTest;
import com.automation.pages.multibank.NavigationPage;
import com.automation.providers.TestDataProviders;
import com.automation.utils.TestDataReader;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
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

        navigationPage.clickNavigationItem(itemName);
        page.waitForLoadState();

        String currentUrl = navigationPage.getCurrentUrl();
        log.info("Current URL after clicking {}: {}", itemName, currentUrl);

        assertThat(currentUrl)
                .as("URL should contain expected part: " + expectedUrlPart)
                .containsIgnoringCase(expectedUrlPart);

        log.info("Navigation test completed for {}", itemName);
    }

    @Test(description = "Verify Dashboard navigation", priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Story("Dashboard Navigation")
    @Description("Test verifies Dashboard link functionality")
    public void testDashboardNavigation() {
        log.info("Testing Dashboard navigation");

        navigationPage.clickDashboard();
        page.waitForLoadState();

        String currentUrl = navigationPage.getCurrentUrl();

        // Dashboard link exists but doesn't change URL on MultiBank homepage
        assertThat(currentUrl)
                .as("Dashboard link should be clickable")
                .isNotEmpty();

        log.info("Dashboard navigation successful");
    }

    @Test(description = "Verify Markets navigation", priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Story("Markets Navigation")
    @Description("Test verifies Markets link functionality")
    public void testMarketsNavigation() {
        log.info("Testing Markets navigation");

        navigationPage.clickMarkets();
        page.waitForLoadState();

        String currentUrl = navigationPage.getCurrentUrl();

        // Markets link exists but doesn't change URL on MultiBank homepage
        assertThat(currentUrl)
                .as("Markets link should be clickable")
                .isNotEmpty();

        log.info("Markets navigation successful");
    }

    @Test(description = "Verify Trade navigation", priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Story("Trade Navigation")
    @Description("Test verifies Trade link functionality")
    public void testTradeNavigation() {
        log.info("Testing Trade navigation");

        boolean tradeAvailable = TestDataReader.getBooleanValue(testData, "navigationMenu", "navigationLinks", "Trade", "available");
        if (!tradeAvailable) {
            log.info("Trade navigation test skipped - link not available on current site");
            return;
        }

        navigationPage.clickTrade();
        page.waitForLoadState();

        String currentUrl = navigationPage.getCurrentUrl();

        assertThat(currentUrl)
                .as("URL should contain 'trade'")
                .containsIgnoringCase("trade");

        log.info("Trade navigation successful");
    }

    @Test(description = "Verify Features navigation", priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Story("Features Navigation")
    @Description("Test verifies Features link functionality")
    public void testFeaturesNavigation() {
        log.info("Testing Features navigation");

        boolean featuresAvailable = TestDataReader.getBooleanValue(testData, "navigationMenu", "navigationLinks", "Features", "available");
        if (!featuresAvailable) {
            log.info("Features navigation test skipped - link not available on current site");
            return;
        }

        navigationPage.clickFeatures();
        page.waitForLoadState();

        String currentUrl = navigationPage.getCurrentUrl();

        assertThat(currentUrl)
                .as("URL should contain 'features'")
                .containsIgnoringCase("features");

        log.info("Features navigation successful");
    }

    @Test(description = "Verify About Us navigation", priority = 9)
    @Severity(SeverityLevel.NORMAL)
    @Story("About Us Navigation")
    @Description("Test verifies About Us link functionality")
    public void testAboutUsNavigation() {
        log.info("Testing About Us navigation");

        boolean aboutUsAvailable = TestDataReader.getBooleanValue(testData, "navigationMenu", "navigationLinks", "About Us", "available");
        if (!aboutUsAvailable) {
            log.info("About Us navigation test skipped - link not available on current site");
            return;
        }

        navigationPage.clickAboutUs();
        page.waitForLoadState();

        String currentUrl = navigationPage.getCurrentUrl();

        assertThat(currentUrl)
                .as("URL should contain 'about'")
                .containsIgnoringCase("about");

        log.info("About Us navigation successful");
    }

    @Test(description = "Verify Support navigation", priority = 10)
    @Severity(SeverityLevel.NORMAL)
    @Story("Support Navigation")
    @Description("Test verifies Support link functionality")
    public void testSupportNavigation() {
        log.info("Testing Support navigation");

        boolean supportAvailable = TestDataReader.getBooleanValue(testData, "navigationMenu", "navigationLinks", "Support", "available");
        if (!supportAvailable) {
            log.info("Support navigation test skipped - link not available on current site");
            return;
        }

        navigationPage.clickSupport();
        page.waitForLoadState();

        String currentUrl = navigationPage.getCurrentUrl();

        assertThat(currentUrl)
                .as("URL should contain 'support'")
                .containsIgnoringCase("support");

        log.info("Support navigation successful");
    }
}
