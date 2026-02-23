package com.automation.multibank;

import com.automation.base.BaseMultibankTest;
import com.automation.providers.TestDataProviders;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("MultiBank Trading Platform")
@Feature("Navigation & Layout")
public class NavigationTests extends BaseMultibankTest {

    @Test(description = "Verify navigation item is clickable and nav menu remains visible",
          dataProvider = "navigationItemsProvider", dataProviderClass = TestDataProviders.class, priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Navigation Menu Clickability")
    @Description("Clicks a navigation item and verifies the nav menu remains visible after the click")
    public void testNavigationItemIsClickable(String itemName, String expectedUrlPart) {
        log.info("Testing navigation item clickability: {}", itemName);

        assertThat(navigationPage.isNavigationItemVisible(itemName))
                .as("Navigation item '%s' should be visible", itemName)
                .isTrue();

        navigationPage.clickNavigationItem(itemName);
        page.waitForLoadState();

        assertNavigationMenuVisible();

        log.info("Navigation item '{}' is clickable and nav menu persists", itemName);
    }

    @Test(description = "Verify clicking all navigation items and returning to homepage",
          dataProvider = "allNavigationItemsProvider", dataProviderClass = TestDataProviders.class, priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation Functionality")
    @Description("Clicks a navigation item, verifies URL, and navigates back to homepage")
    public void testNavigationItemFunctionality(String itemName, String expectedUrlPart) {
        log.info("Testing navigation functionality for: {}", itemName);

        String initialUrl = page.url();
        navigationPage.clickNavigationItem(itemName);
        page.waitForLoadState();

        String currentUrl = page.url();
        log.info("URL after clicking {}: {}", itemName, currentUrl);

        assertThat(currentUrl)
                .as("URL should contain '%s' after clicking '%s'", expectedUrlPart, itemName)
                .containsIgnoringCase(expectedUrlPart);

        assertNavigationMenuVisible();

        navigateToHomePage();
        waitForPageLoad();

        log.info("Navigation test completed for '{}'", itemName);
    }

    @Test(description = "Verify browser back returns to homepage after navigation", priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Story("Browser History")
    @Description("Navigates to Markets, then uses browser back to return to homepage")
    public void testBrowserBackAfterNavigation() {
        log.info("Starting test: Browser back after navigation");

        String homeUrl = page.url();
        navigationPage.clickNavigationItem("Markets");
        page.waitForLoadState();

        page.goBack();
        page.waitForLoadState();

        String urlAfterBack = page.url();
        assertThat(urlAfterBack)
                .as("URL should match homepage after browser back")
                .isEqualTo(homeUrl);

        log.info("Test completed: Browser back returns to homepage");
    }

    @Test(description = "Verify language selector is visible on homepage", priority = 4)
    @Severity(SeverityLevel.MINOR)
    @Story("Language Selector")
    @Description("Verifies the language selector and key nav items are present on homepage")
    public void testLanguageSelectorIsVisible() {
        log.info("Starting test: Language selector visibility");

        assertThat(navigationPage.isLanguageSelectorVisible())
                .as("Language selector should be visible")
                .isTrue();

        assertNavigationMenuVisible();

        assertThat(navigationPage.isNavigationItemVisible("Dashboard"))
                .as("Dashboard item should be present")
                .isTrue();

        assertThat(navigationPage.isNavigationItemVisible("Markets"))
                .as("Markets item should be present")
                .isTrue();

        log.info("Test completed: Language selector and key nav items are visible");
    }
}
