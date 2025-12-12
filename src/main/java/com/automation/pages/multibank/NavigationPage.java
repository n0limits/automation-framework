package com.automation.pages.multibank;

import com.automation.config.TestConfig;
import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NavigationPage extends BasePage {

    // ============================================================
    // Locator Fields
    // ============================================================

    // Main Navigation Wrapper
    private final String topNavigation = "header";

    // All Menu Items
    private final String navMenuItems = "header a";

    // Individual Navigation Links
    private final String dashboardLink = "a[href*='dashboard'], a:has-text('Dashboard')";
    private final String marketsLink = "a[href*='markets'], a:has-text('Markets')";
    private final String tradeLink = "a[href*='trade'], a:has-text('Trade')";
    private final String featuresLink = "a[href*='features'], a:has-text('Features')";
    private final String aboutLink = "a[href*='about'], a:has-text('About')"; // Not always present
    private final String supportLink = "a[href*='support'], a:has-text('Support')";

    // Language Selector
    private final String languageSelector = "[class*='language'], [class*='lang'], select[name='language']";

    // ============================================================

    public NavigationPage() {
        super();
        // Page objects should not navigate in constructor
    }

    public boolean isNavigationMenuDisplayed() {
        try {
            waitForSelector(topNavigation);
            log.debug("Navigation menu is displayed");
            return true;
        } catch (Exception e) {
            log.error("Navigation menu not displayed", e);
            return false;
        }
    }

    public List<String> getNavigationMenuItems() {
        waitForSelector(navMenuItems);
        List<Locator> menuLocators = page.locator(navMenuItems).all();
        List<String> menuItems = new ArrayList<>();

        for (Locator locator : menuLocators) {
            String text = locator.textContent();
            if (text != null && !text.trim().isEmpty()) {
                menuItems.add(text.trim());
            }
        }

        log.info("Found {} navigation menu items: {}", menuItems.size(), menuItems);
        return menuItems;
    }

    public boolean isNavigationItemVisible(String itemName) {
        try {
            String selector = String.format("a:has-text('%s')", itemName);
            waitForSelector(selector);
            log.debug("Navigation item '{}' is visible", itemName);
            return true;
        } catch (Exception e) {
            log.warn("Navigation item '{}' not visible", itemName);
            return false;
        }
    }

    public void clickNavigationItem(String itemName) {
        String selector = String.format("a:has-text('%s')", itemName);
        waitForSelector(selector);
        click(selector);
        log.info("Clicked navigation item: {}", itemName);
    }

    public void clickDashboard() {
        click(dashboardLink);
        log.info("Clicked Dashboard link");
    }

    public void clickMarkets() {
        click(marketsLink);
        log.info("Clicked Markets link");
    }

    public void clickTrade() {
        click(tradeLink);
        log.info("Clicked Trade link");
    }

    public void clickFeatures() {
        click(featuresLink);
        log.info("Clicked Features link");
    }

    public void clickAboutUs() {
        click(aboutLink);
        log.info("Clicked About Us link");
    }

    public void clickSupport() {
        click(supportLink);
        log.info("Clicked Support link");
    }

    public String getCurrentUrl() {
        String url = page.url();
        log.debug("Current URL: {}", url);
        return url;
    }

    public boolean isLanguageSelectorVisible() {
        try {
            waitForSelector(languageSelector);
            log.debug("Language selector is visible");
            return true;
        } catch (Exception e) {
            log.debug("Language selector not found");
            return false;
        }
    }

    public void verifyNavigationFunctionality(String itemName, String expectedUrlPart) {
        clickNavigationItem(itemName);
        page.waitForLoadState();
        String currentUrl = getCurrentUrl();

        if (currentUrl.contains(expectedUrlPart)) {
            log.info("Navigation to '{}' successful. URL contains: {}", itemName, expectedUrlPart);
        } else {
            log.warn(
                    "Navigation to '{}' may have failed. Expected URL part '{}', but got: {}",
                    itemName, expectedUrlPart, currentUrl
            );
        }
    }
}
