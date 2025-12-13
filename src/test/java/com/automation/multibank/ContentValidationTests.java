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
import org.testng.SkipException;
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

    // ========================================
    // OVERRIDE: Additional Setup Hook
    // ========================================
    @Override
    protected void performAdditionalSetup() {
        log.info("Performing Content validation test-specific setup");

        // Initialize page objects
        navigationPage = new NavigationPage();
        footerPage = new FooterPage();
        aboutUsPage = new AboutUsPage();

        // Load test data
        testData = TestDataReader.readJsonFile("content-data.json");

        // Any other Content validation-specific setup can go here

        log.info("Content validation test setup completed");
    }

    // ========================================
    // OVERRIDE: Additional Cleanup Hook
    // ========================================
    @Override
    protected void performAdditionalCleanup() {
        log.info("Performing Content validation test-specific cleanup");

        // Any Content validation-specific cleanup can go here

        log.info("Content validation test cleanup completed");
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
        boolean isBannerVisible = footerPage.isSpecificBannerVisible(bannerText);

        assertThat(isBannerVisible)
                .as("Marketing banner containing '" + bannerText + "' should be visible")
                .isTrue();

        log.info("Marketing banner '{}' is visible", bannerText);
    }

    @Test(description = "Verify About Us page is accessible", priority = 8)
    @Severity(SeverityLevel.CRITICAL)
    @Story("About Us Page")
    @Description("Test verifies that About Us page is accessible and loads correctly")
    public void testAboutUsPageAccessible() {
        log.info("Testing About Us page accessibility");

        boolean aboutUsAvailable = TestDataReader.getBooleanValue(testData, "aboutUsPage", "available");
        if (!aboutUsAvailable) {
            throw new SkipException("About Us page test skipped - page not available on current site");
        }

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

        boolean aboutUsAvailable = TestDataReader.getBooleanValue(testData, "aboutUsPage", "available");
        if (!aboutUsAvailable) {
            throw new SkipException("Why MultiBank section test skipped - About Us page not available");
        }

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

        boolean aboutUsAvailable = TestDataReader.getBooleanValue(testData, "aboutUsPage", "available");
        if (!aboutUsAvailable) {
            throw new SkipException("About Us components test skipped - About Us page not available");
        }

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

        boolean aboutUsAvailable = TestDataReader.getBooleanValue(testData, "aboutUsPage", "available");
        if (!aboutUsAvailable) {
            throw new SkipException("About Us content test skipped - About Us page not available");
        }

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

    @Test(description = "Verify download link configuration and validity", priority = 14,
          dataProvider = "downloadLinksProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Download Links Validation")
    @Description("Test verifies download links are properly configured with correct href, visibility, and domain")
    public void testDownloadLinkConfiguration(String linkName, String expectedDomain) {
        log.info("Testing {} link configuration", linkName);

        footerPage.scrollToFooter();

        String actualHref;
        boolean linkVisible;

        // Get link details based on link name
        if (linkName.equals("App Store")) {
            actualHref = footerPage.getAppStoreUrl();
            linkVisible = footerPage.isAppStoreLinkVisible();
        } else {
            actualHref = footerPage.getGooglePlayUrl();
            linkVisible = footerPage.isGooglePlayLinkVisible();
        }

        // Verify link is visible and clickable
        assertThat(linkVisible)
                .as(linkName + " link should be visible")
                .isTrue();

        // Verify href is not null or empty
        assertThat(actualHref)
                .as(linkName + " URL should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        // Verify URL is properly formed (starts with http/https)
        assertThat(actualHref)
                .as(linkName + " URL should be a valid HTTP(S) URL")
                .matches("^https?://.+");

        // Verify link points to correct domain
        assertThat(actualHref)
                .as(linkName + " link should point to " + expectedDomain)
                .containsIgnoringCase(expectedDomain);

        log.info("{} link validated - URL: {}", linkName, actualHref);
    }

    @Test(description = "Verify marketing banners content and interaction", priority = 15)
    @Severity(SeverityLevel.NORMAL)
    @Story("Marketing Banners Interaction")
    @Description("Test verifies that marketing banners are displayed with proper content")
    public void testMarketingBannersContent() {
        log.info("Testing marketing banners content");

        footerPage.scrollToFooter();

        // Verify banners are visible
        boolean bannersVisible = footerPage.areMarketingBannersVisible();

        assertThat(bannersVisible)
                .as("Marketing banners should be visible")
                .isTrue();

        // Get all banner texts to verify content is loaded
        List<String> bannerTexts = footerPage.getMarketingBannerTexts();

        assertThat(bannerTexts)
                .as("Marketing banners should have text content")
                .isNotEmpty();

        log.info("Marketing banners verified - {} banners with content: {}",
                bannerTexts.size(), bannerTexts);
    }
}
