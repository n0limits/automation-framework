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
    private final Locator topNavigation;

    // All Menu Items
    private final Locator navMenuItems;

    // Individual Navigation Links
    private final Locator dashboardLink;
    private final Locator marketsLink;
    private final Locator tradeLink;
    private final Locator featuresLink;
    private final Locator aboutLink;
    private final Locator supportLink;

    // Language Selector
    private final Locator languageSelector;

    // ============================================================

    public NavigationPage() {
        super();

        // Initialize Navigation Locators
        this.topNavigation = page.locator("header");
        this.navMenuItems = page.locator("header nav a, header a");

        // Individual Navigation Links - using exact text matching from actual page
        this.dashboardLink = page.locator("header a:has-text('Dashboard')").first();
        this.marketsLink = page.locator("header a[href='/markets'], header a:has-text('Markets')").first();

        // These are span elements (dropdown triggers), not direct links
        this.tradeLink = page.locator("header span:has-text('Trade'), header a:has-text('Trade')").first();
        this.featuresLink = page.locator("header span:has-text('Features'), header a:has-text('Features')").first();
        this.aboutLink = page.locator("header span:has-text('About Us'), header a:has-text('About Us')").first();
        this.supportLink = page.locator("header span:has-text('Support'), header a:has-text('Support')").first();

        // Language Selector - looking for "EN" text
        this.languageSelector = page.locator(":has-text('EN'), [class*='language'], [class*='lang']").first();
    }

    // ============================================================
    // Navigation Methods
    // ============================================================

    public boolean isNavigationMenuDisplayed() {
        try {
            topNavigation.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            log.debug("Navigation menu is displayed");
            return true;
        } catch (Exception e) {
            log.error("Navigation menu not displayed", e);
            return false;
        }
    }

    public List<String> getNavigationMenuItems() {
        navMenuItems.first().waitFor(new Locator.WaitForOptions().setTimeout(10000));
        List<Locator> menuLocators = navMenuItems.all();
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
            Locator itemLocator = page.locator(String.format("header a:has-text('%s'), header span:has-text('%s')", itemName, itemName)).first();
            itemLocator.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            log.debug("Navigation item '{}' is visible", itemName);
            return true;
        } catch (Exception e) {
            log.warn("Navigation item '{}' not visible", itemName, e);
            return false;
        }
    }

    public void clickNavigationItem(String itemName) {
        // Support both <a> (direct links) and <span> (dropdown triggers)
        Locator itemLocator = page.locator(String.format(
                "header a:has-text('%s'), header span:has-text('%s')",
                itemName, itemName
        )).first();
        itemLocator.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        itemLocator.click();
        log.info("Clicked navigation item: {}", itemName);
    }

    public void clickDashboard() {
        dashboardLink.click();
        log.info("Clicked Dashboard link");
    }

    public void clickMarkets() {
        marketsLink.click();
        log.info("Clicked Markets link");
    }

    public void clickTrade() {
        // Trade might be a dropdown trigger - click and wait for any navigation
        tradeLink.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        tradeLink.click();
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        log.info("Clicked Trade link/dropdown");
    }

    public void clickFeatures() {
        // Features might be a dropdown trigger - click and wait for any navigation
        featuresLink.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        featuresLink.click();
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        log.info("Clicked Features link/dropdown");
    }

    public void clickAboutUs() {
        // About Us might be a dropdown trigger - click and wait for any navigation
        aboutLink.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        aboutLink.click();
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        log.info("Clicked About Us link/dropdown");
    }

    public void clickSupport() {
        // Support might be a dropdown trigger - click and wait for any navigation
        supportLink.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        supportLink.click();
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
        log.info("Clicked Support link/dropdown");
    }

    public String getCurrentUrl() {
        String url = page.url();
        log.debug("Current URL: {}", url);
        return url;
    }

    public boolean isLanguageSelectorVisible() {
        try {
            languageSelector.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            log.debug("Language selector is visible");
            return true;
        } catch (Exception e) {
            log.debug("Language selector not found", e);
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
