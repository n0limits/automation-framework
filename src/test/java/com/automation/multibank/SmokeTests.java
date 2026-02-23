package com.automation.multibank;

import com.automation.base.BaseMultibankTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("Smoke Tests")
@Feature("Application Health")
public class SmokeTests extends BaseMultibankTest {

    @Test(description = "Verify homepage loads and is interactive", priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Story("Homepage Accessibility")
    @Description("Validates that the homepage loads with interactive content: URL, title, nav menu, and trading table")
    public void testHomepageLoadsAndIsInteractive() {
        log.info("Starting smoke test: Homepage load and interactivity verification");

        assertThat(page.url())
                .as("Page URL should contain trade.multibank.io")
                .contains("trade.multibank.io");

        String pageTitle = page.title();
        log.info("Page title: {}", pageTitle);
        assertThat(pageTitle)
                .as("Page title should contain MultiBank")
                .containsIgnoringCase("MultiBank");

        assertNavigationMenuVisible();

        List<String> navItems = navigationPage.getNavigationMenuItems();
        log.info("Navigation items: {}", navItems);
        assertThat(navItems)
                .as("Navigation menu should have items")
                .isNotEmpty();

        assertThat(tradingPage.isTradingPairsTableDisplayed())
                .as("Trading pairs table should be displayed")
                .isTrue();

        log.info("Smoke test passed: Homepage is loaded and interactive");
    }

    @Test(description = "Verify trading table responds to tab clicks", priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Table Interactivity")
    @Description("Validates that the trading table responds to tab switching and preserves data")
    public void testTradingTableIsInteractive() {
        log.info("Starting smoke test: Trading table interactivity");

        assertThat(tradingPage.isTradingPairsTableDisplayed())
                .as("Trading table should be visible")
                .isTrue();

        tradingPage.clickAllPairsTab();
        page.waitForLoadState();
        int allPairsCount = tradingPage.getTradingPairsCount();
        log.info("All Pairs count: {}", allPairsCount);
        assertThat(allPairsCount)
                .as("All Pairs tab should show trading pairs")
                .isGreaterThan(0);

        tradingPage.clickFavoritesTab();
        page.waitForLoadState();
        assertThat(tradingPage.isTradingPairsTableDisplayed())
                .as("Trading table should remain displayed after switching to Favorites")
                .isTrue();

        tradingPage.clickAllPairsTab();
        page.waitForLoadState();
        int allPairsCountAfter = tradingPage.getTradingPairsCount();
        assertThat(allPairsCountAfter)
                .as("All Pairs count should be consistent after tab switching")
                .isEqualTo(allPairsCount);

        log.info("Smoke test passed: Trading table is interactive");
    }

    @Test(description = "Verify full page scroll and return to top", priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Scroll")
    @Description("Validates scrolling to footer and back to top preserves page structure")
    public void testFullPageScrollAndReturn() {
        log.info("Starting smoke test: Full page scroll and return");

        footerPage.scrollToFooter();
        page.waitForLoadState();

        assertThat(footerPage.isFooterDisplayed())
                .as("Footer should be visible after scrolling down")
                .isTrue();

        assertThat(footerPage.isAppStoreLinkVisible())
                .as("App Store link should be visible in footer")
                .isTrue();

        scrollToTop();

        assertNavigationMenuVisible();

        log.info("Smoke test passed: Full page scroll and return works");
    }
}
