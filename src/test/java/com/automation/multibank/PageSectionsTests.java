package com.automation.multibank;

import com.automation.base.BaseMultibankTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("MultiBank Trading Platform")
@Feature("Page Sections")
public class PageSectionsTests extends BaseMultibankTest {

    @Test(description = "Scroll to investment cards section and verify visibility", priority = 1)
    @Severity(SeverityLevel.NORMAL)
    @Story("Investment Sections")
    @Description("Scrolls to 50% of page and verifies MBG Token or Real World Assets section is visible")
    public void testScrollToInvestmentCardsSection() {
        log.info("Starting test: Scroll to investment cards section");

        scrollToPercentage(0.5);

        boolean mbgVisible = tradingPage.isMBGTokenSectionVisible();
        boolean rwaVisible = tradingPage.isRealWorldAssetsSectionVisible();
        log.info("MBG Token visible: {}, Real World Assets visible: {}", mbgVisible, rwaVisible);

        assertThat(mbgVisible || rwaVisible)
                .as("MBG Token or Real World Assets section should be visible at 50% scroll")
                .isTrue();

        page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get("target/screenshots/investment-cards-section.png")));

        log.info("Test completed: Investment cards section verified");
    }

    @Test(description = "Scroll to quick access tools and verify visibility", priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Quick Access Tools")
    @Description("Scrolls to 60% of page and verifies at least one quick access tool is visible")
    public void testScrollToQuickAccessTools() {
        log.info("Starting test: Scroll to quick access tools");

        scrollToPercentage(0.6);

        boolean convertVisible = tradingPage.isConvertAssetsButtonVisible();
        boolean quickBuyVisible = tradingPage.isQuickBuyButtonVisible();
        boolean panicSellVisible = tradingPage.isPanicSellButtonVisible();
        log.info("Convert: {}, QuickBuy: {}, PanicSell: {}", convertVisible, quickBuyVisible, panicSellVisible);

        assertThat(convertVisible || quickBuyVisible || panicSellVisible)
                .as("At least one quick access tool should be visible at 60% scroll")
                .isTrue();

        log.info("Test completed: Quick access tools verified");
    }

    @Test(description = "Scroll to footer and verify download links", priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Footer Download Links")
    @Description("Scrolls to footer and verifies App Store, Google Play links and QR code are visible with correct URLs")
    public void testScrollToFooterAndVerifyDownloadLinks() {
        log.info("Starting test: Footer download links verification");

        footerPage.scrollToFooter();
        page.waitForTimeout(1000);

        assertThat(footerPage.isFooterDisplayed())
                .as("Footer should be displayed")
                .isTrue();

        assertThat(footerPage.isAppStoreLinkVisible())
                .as("App Store link should be visible")
                .isTrue();

        String appStoreUrl = footerPage.getAppStoreUrl();
        log.info("App Store URL: {}", appStoreUrl);
        assertThat(appStoreUrl)
                .as("App Store URL should contain apple.com")
                .contains("apple.com");

        assertThat(footerPage.isGooglePlayLinkVisible())
                .as("Google Play link should be visible")
                .isTrue();

        String googlePlayUrl = footerPage.getGooglePlayUrl();
        log.info("Google Play URL: {}", googlePlayUrl);
        assertThat(googlePlayUrl)
                .as("Google Play URL should contain play.google.com")
                .contains("play.google.com");

        assertThat(footerPage.isQRCodeVisible())
                .as("QR code should be visible")
                .isTrue();

        log.info("Test completed: Footer download links verified");
    }

    @Test(description = "Scroll through page sections sequentially", priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Scroll Sequence")
    @Description("Scrolls through page at 30%, 60%, 100%, and back to top verifying visibility at each point")
    public void testScrollSequenceThroughPage() {
        log.info("Starting test: Scroll sequence through page");

        assertNavigationMenuVisible();

        scrollToPercentage(0.3);
        assertThat(tradingPage.isTradingPairsTableDisplayed())
                .as("Trading table should be visible at 30% scroll")
                .isTrue();
        log.info("30% scroll: trading table visible");

        scrollToPercentage(0.6);
        boolean midPageContent = tradingPage.isMBGTokenSectionVisible()
                || tradingPage.isConvertAssetsButtonVisible()
                || tradingPage.isQuickBuyButtonVisible();
        assertThat(midPageContent)
                .as("MBG Token or quick tools should be visible at 60% scroll")
                .isTrue();
        log.info("60% scroll: mid-page content visible");

        scrollToPercentage(1.0);
        assertThat(footerPage.isFooterDisplayed())
                .as("Footer should be visible at 100% scroll")
                .isTrue();
        log.info("100% scroll: footer visible");

        scrollToTop();
        assertNavigationMenuVisible();
        log.info("Back to top: navigation menu visible");

        log.info("Test completed: Scroll sequence through page verified");
    }
}
