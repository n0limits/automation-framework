package com.automation.multibank;

import com.automation.base.BaseMultibankTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("MultiBank Trading Platform")
@Feature("Cross-Page Navigation")
public class CrossPageNavigationTests extends BaseMultibankTest {

    @Test(description = "Navigate to About Us page and back to homepage", priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("About Us Navigation")
    @Description("Navigates to About Us page and uses browser back to return to homepage")
    public void testNavigateToAboutUsAndBack() {
        log.info("Starting test: Navigate to About Us and back");

        String homeUrl = page.url();
        log.info("Home URL: {}", homeUrl);

        aboutUsPage.navigateToAboutUs();
        page.waitForLoadState();

        String aboutUrl = page.url();
        log.info("URL after About Us navigation: {}", aboutUrl);

        page.goBack();
        page.waitForLoadState();

        String urlAfterBack = page.url();
        log.info("URL after browser back: {}", urlAfterBack);

        assertThat(urlAfterBack)
                .as("URL should match homepage after browser back")
                .isEqualTo(homeUrl);

        log.info("Test completed: About Us navigation and back verified");
    }

    @Test(description = "Navigate to Markets and back to Dashboard", priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Markets-Dashboard Navigation")
    @Description("Navigates to Markets, then back to Dashboard, and verifies homepage content")
    public void testNavigateToMarketsAndBackToDashboard() {
        log.info("Starting test: Markets to Dashboard navigation");

        navigationPage.clickNavigationItem("Markets");
        page.waitForLoadState();
        log.info("URL after Markets: {}", page.url());

        navigationPage.clickNavigationItem("Dashboard");
        page.waitForLoadState();

        String currentUrl = page.url();
        log.info("URL after Dashboard: {}", currentUrl);

        assertThat(currentUrl)
                .as("URL should contain trade.multibank.io")
                .contains("trade.multibank.io");

        assertThat(tradingPage.isTradingPairsTableDisplayed())
                .as("Trading table should be visible on homepage")
                .isTrue();

        log.info("Test completed: Markets to Dashboard navigation verified");
    }

    @Test(description = "Multi-step navigation with browser history", priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Story("Browser History")
    @Description("Performs multi-step navigation and uses browser back/forward to verify history works")
    public void testMultiStepNavigationWithBrowserHistory() {
        log.info("Starting test: Multi-step navigation with browser history");

        String homeUrl = page.url();
        log.info("Step 1 - Home URL: {}", homeUrl);

        navigationPage.clickNavigationItem("Markets");
        page.waitForLoadState();
        String marketsUrl = page.url();
        log.info("Step 2 - Markets URL: {}", marketsUrl);

        navigationPage.clickNavigationItem("Dashboard");
        page.waitForLoadState();
        String dashboardUrl = page.url();
        log.info("Step 3 - Dashboard URL: {}", dashboardUrl);

        page.goBack();
        page.waitForLoadState();
        String afterFirstBack = page.url();
        log.info("Step 4 - After first back: {}", afterFirstBack);
        assertThat(afterFirstBack)
                .as("First back should return to Markets page")
                .isEqualTo(marketsUrl);

        page.goBack();
        page.waitForLoadState();
        String afterSecondBack = page.url();
        log.info("Step 5 - After second back: {}", afterSecondBack);
        assertThat(afterSecondBack)
                .as("Second back should return to homepage")
                .isEqualTo(homeUrl);

        page.goForward();
        page.waitForLoadState();
        String afterForward = page.url();
        log.info("Step 6 - After forward: {}", afterForward);
        assertThat(afterForward)
                .as("Forward should return to Markets page")
                .isEqualTo(marketsUrl);

        log.info("Test completed: Multi-step navigation with browser history verified");
    }
}
