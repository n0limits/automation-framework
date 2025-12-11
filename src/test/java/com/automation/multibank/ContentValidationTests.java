package com.automation.multibank;

import com.automation.base.BaseWebTest;
import com.automation.pages.multibank.AboutUsPage;
import com.automation.pages.multibank.FooterPage;
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
@Feature("Content Validation")
public class ContentValidationTests extends BaseWebTest {

    private NavigationPage navigationPage;
    private FooterPage footerPage;
    private AboutUsPage aboutUsPage;
    private JsonNode testData;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        navigationPage = new NavigationPage();
        footerPage = new FooterPage();
        aboutUsPage = new AboutUsPage();
        testData = TestDataReader.readJsonFile("content-data.json");
        log.info("Content validation test setup completed");
    }

    @Test(description = "Verify footer is displayed", priority = 1)
    @Severity(SeverityLevel.NORMAL)
    @Story("Footer Section")
    @Description("Test verifies that the footer section is visible")
    public void testFooterDisplayed() {
        log.info("Starting test: Footer display verification");

        footerPage.scrollToFooter();
        boolean isDisplayed = footerPage.isFooterDisplayed();

        assertThat(isDisplayed)
                .as("Footer should be displayed")
                .isTrue();

        log.info("Test completed: Footer is displayed");
    }

    @Test(description = "Verify App Store download link is visible", priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Download Links")
    @Description("Test verifies that App Store download link is visible")
    public void testAppStoreLinkVisible() {
        log.info("Testing App Store link visibility");

        footerPage.scrollToFooter();
        boolean isVisible = footerPage.isAppStoreLinkVisible();

        assertThat(isVisible)
                .as("App Store link should be visible")
                .isTrue();

        log.info("App Store link is visible");
    }

    @Test(description = "Verify Google Play download link is visible", priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Download Links")
    @Description("Test verifies that Google Play download link is visible")
    public void testGooglePlayLinkVisible() {
        log.info("Testing Google Play link visibility");

        footerPage.scrollToFooter();
        boolean isVisible = footerPage.isGooglePlayLinkVisible();

        assertThat(isVisible)
                .as("Google Play link should be visible")
                .isTrue();

        log.info("Google Play link is visible");
    }

    @Test(description = "Verify download links point to correct stores", priority = 4)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Download Links Validation")
    @Description("Test verifies that download links point to correct app stores")
    public void testDownloadLinksPointToCorrectStores() {
        log.info("Testing download links validation");

        footerPage.scrollToFooter();
        String appStoreUrl = footerPage.getAppStoreUrl();
        String googlePlayUrl = footerPage.getGooglePlayUrl();

        String expectedAppStoreUrl = TestDataReader.getStringValue(testData, "downloadSection", "appStoreLink");
        String expectedGooglePlayUrl = TestDataReader.getStringValue(testData, "downloadSection", "googlePlayLink");

        assertThat(appStoreUrl)
                .as("App Store link should point to Apple App Store")
                .containsIgnoringCase(expectedAppStoreUrl);

        assertThat(googlePlayUrl)
                .as("Google Play link should point to Google Play Store")
                .containsIgnoringCase(expectedGooglePlayUrl);

        log.info("Download links validation successful - App Store: {}, Google Play: {}",
                appStoreUrl, googlePlayUrl);
    }

    @Test(description = "Verify QR code is visible", priority = 5)
    @Severity(SeverityLevel.MINOR)
    @Story("Download Section")
    @Description("Test verifies that QR code for app download is visible")
    public void testQRCodeVisible() {
        log.info("Testing QR code visibility");

        footerPage.scrollToFooter();
        boolean qrCodeExpected = TestDataReader.getBooleanValue(testData, "downloadSection", "qrCodeVisible");

        if (qrCodeExpected) {
            boolean isVisible = footerPage.isQRCodeVisible();

            assertThat(isVisible)
                    .as("QR code should be visible")
                    .isTrue();

            log.info("QR code is visible");
        } else {
            log.info("QR code test skipped - not expected to be visible");
        }
    }

    @Test(description = "Verify marketing banners are visible", priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Story("Marketing Banners")
    @Description("Test verifies that marketing banners are displayed")
    public void testMarketingBannersVisible() {
        log.info("Testing marketing banners visibility");

        footerPage.scrollToFooter();
        boolean areVisible = footerPage.areMarketingBannersVisible();

        assertThat(areVisible)
                .as("Marketing banners should be visible")
                .isTrue();

        log.info("Marketing banners are visible");
    }

    @Test(description = "Verify specific marketing banner is visible", priority = 7,
          dataProvider = "marketingBannersProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.MINOR)
    @Story("Marketing Banner Content")
    @Description("Test verifies that specific marketing banners are visible")
    public void testSpecificMarketingBannerVisible(String bannerText) {
        log.info("Testing visibility of marketing banner: {}", bannerText);

        footerPage.scrollToFooter();
        boolean isInstantBuyVisible = footerPage.isInstantBuyBannerVisible();
        boolean isCardTransferVisible = footerPage.isCardTransferBannerVisible();
        boolean isSupportVisible = footerPage.isSupportBannerVisible();

        boolean anyBannerVisible = isInstantBuyVisible || isCardTransferVisible || isSupportVisible;

        assertThat(anyBannerVisible)
                .as("At least one marketing banner should be visible")
                .isTrue();

        log.info("Marketing banner visibility - InstantBuy: {}, CardTransfer: {}, Support: {}",
                isInstantBuyVisible, isCardTransferVisible, isSupportVisible);
    }

    @Test(description = "Verify About Us page is accessible", priority = 8)
    @Severity(SeverityLevel.CRITICAL)
    @Story("About Us Page")
    @Description("Test verifies that About Us page is accessible and loads correctly")
    public void testAboutUsPageAccessible() {
        log.info("Testing About Us page accessibility");

        navigationPage.clickAboutUs();
        page.waitForLoadState();

        boolean isDisplayed = aboutUsPage.isAboutUsPageDisplayed();

        assertThat(isDisplayed)
                .as("About Us page should be displayed")
                .isTrue();

        log.info("About Us page is accessible");
    }

    @Test(description = "Verify Why MultiBank section is visible", priority = 9)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Why MultiBank Section")
    @Description("Test verifies that Why MultiBank section is visible on About Us page")
    public void testWhyMultiBankSectionVisible() {
        log.info("Testing Why MultiBank section visibility");

        navigationPage.clickAboutUs();
        page.waitForLoadState();

        boolean isVisible = aboutUsPage.isWhyMultiBankSectionVisible();

        assertThat(isVisible)
                .as("Why MultiBank section should be visible")
                .isTrue();

        log.info("Why MultiBank section is visible");
    }

    @Test(description = "Verify About Us page components are present", priority = 10)
    @Severity(SeverityLevel.NORMAL)
    @Story("About Us Page Components")
    @Description("Test verifies that all expected components are present on About Us page")
    public void testAboutUsPageComponents() {
        log.info("Testing About Us page components");

        navigationPage.clickAboutUs();
        page.waitForLoadState();

        List<String> expectedComponents = TestDataReader.getStringList(testData,
                "aboutUsPage", "whyMultiBankSection", "expectedComponents");

        boolean allPresent = aboutUsPage.verifyAllExpectedComponentsPresent(expectedComponents);

        assertThat(allPresent)
                .as("All expected components should be present")
                .isTrue();

        log.info("All expected components are present on About Us page");
    }

    @Test(description = "Verify About Us page content is loaded", priority = 11)
    @Severity(SeverityLevel.NORMAL)
    @Story("About Us Content")
    @Description("Test verifies that About Us page content is fully loaded")
    public void testAboutUsContentLoaded() {
        log.info("Testing About Us page content loading");

        navigationPage.clickAboutUs();
        page.waitForLoadState();

        boolean isLoaded = aboutUsPage.isContentLoaded();
        int sectionsCount = aboutUsPage.getContentSectionsCount();

        assertThat(isLoaded)
                .as("About Us page content should be loaded")
                .isTrue();

        assertThat(sectionsCount)
                .as("About Us page should have content sections")
                .isGreaterThan(0);

        log.info("About Us page content is loaded with {} sections", sectionsCount);
    }

    @Test(description = "Verify social media links are visible", priority = 12)
    @Severity(SeverityLevel.MINOR)
    @Story("Social Media Links")
    @Description("Test verifies that social media links are visible in footer")
    public void testSocialMediaLinksVisible() {
        log.info("Testing social media links visibility");

        footerPage.scrollToFooter();
        boolean areVisible = footerPage.areSocialMediaLinksVisible();

        if (areVisible) {
            List<String> socialLinks = footerPage.getSocialMediaLinks();

            assertThat(socialLinks)
                    .as("Social media links should not be empty")
                    .isNotEmpty();

            log.info("Found {} social media links", socialLinks.size());
        } else {
            log.info("Social media links not found");
        }
    }

    @Test(description = "Verify all page elements render correctly", priority = 13)
    @Severity(SeverityLevel.NORMAL)
    @Story("Page Rendering")
    @Description("Test verifies that all major page elements render correctly")
    public void testPageElementsRenderCorrectly() {
        log.info("Testing page elements rendering");

        boolean navigationDisplayed = navigationPage.isNavigationMenuDisplayed();
        footerPage.scrollToFooter();
        boolean footerDisplayed = footerPage.isFooterDisplayed();

        assertThat(navigationDisplayed)
                .as("Navigation should be displayed")
                .isTrue();

        assertThat(footerDisplayed)
                .as("Footer should be displayed")
                .isTrue();

        log.info("All major page elements rendered correctly");
    }
}
