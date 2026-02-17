package com.automation.base;

import com.automation.pages.multibank.AboutUsPage;
import com.automation.pages.multibank.FooterPage;
import com.automation.pages.multibank.NavigationPage;
import com.automation.pages.multibank.TradingPage;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for all MultiBank test classes.
 * Provides shared page object initialization, common assertions, and utility methods.
 *
 * @author Victor Grozev
 */
@Slf4j
public class BaseMultibankTest extends BaseWebTest {

    protected NavigationPage navigationPage;
    protected TradingPage tradingPage;
    protected FooterPage footerPage;
    protected AboutUsPage aboutUsPage;

    @Override
    protected void performAdditionalSetup() {
        navigationPage = new NavigationPage();
        tradingPage = new TradingPage();
        footerPage = new FooterPage();
        aboutUsPage = new AboutUsPage();
    }

    /**
     * Scrolls the page to a given percentage of the total scroll height.
     *
     * @param percentage value between 0.0 (top) and 1.0 (bottom)
     */
    protected void scrollToPercentage(double percentage) {
        page.evaluate("window.scrollTo(0, document.body.scrollHeight * " + percentage + ")");
        page.waitForTimeout(500);
        log.debug("Scrolled to {}% of page", (int) (percentage * 100));
    }

    /**
     * Scrolls to the top of the page.
     */
    protected void scrollToTop() {
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(500);
        log.debug("Scrolled to top of page");
    }

    /**
     * Asserts that the navigation menu is currently displayed.
     */
    protected void assertNavigationMenuVisible() {
        assertThat(navigationPage.isNavigationMenuDisplayed())
                .as("Navigation menu should be displayed")
                .isTrue();
    }
}
