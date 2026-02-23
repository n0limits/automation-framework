package com.automation.pages.multibank;

import com.automation.config.TestConfig;
import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FooterPage extends BasePage {

    // Configurable timeouts from TestConfig
    private final int elementTimeout;
    private final int shortTimeout;

    // ============================================================
    // Locator Fields
    // ============================================================

    // Footer Container
    private final Locator footerSection;

    // App Download Links
    private final Locator appStoreLink;
    private final Locator googlePlayLink;
    private final Locator qrCode;

    // Marketing Banners
    private final Locator marketingBanner;
    private final Locator instantBuyBanner;
    private final Locator cardTransferBanner;
    private final Locator supportBanner;

    // Social Media
    private final Locator socialMediaLinks;

    // ============================================================

    public FooterPage() {
        super();

        // Initialize configurable timeouts
        TestConfig config = TestConfig.getInstance();
        this.elementTimeout = config.getElementTimeout();
        this.shortTimeout = config.getShortTimeout();

        // Initialize Footer Locators
        this.footerSection = page.locator("[class*='app-download-container'], [class*='buttons-container']").first();

        // App Download Links - using exact href from actual page
        this.appStoreLink = page.locator("a[href*='apps.apple.com/ae/app/multibank-io'], a[href*='apps.apple.com'], img[alt='app-store']").first();
        this.googlePlayLink = page.locator("a[href*='play.google.com/store/apps/details?id=com.multibank.app'], a[href*='play.google.com'], img[alt='google-play']").first();
        this.qrCode = page.locator("img[alt='qr-code'], img[alt*='qr']").first();

        // Marketing Banners - using simpler text matching that works with page structure
        this.marketingBanner = page.locator(":has-text('Pay Trading Fees'), :has-text('Real World Assets'), :has-text('Coming Soon')");
        this.instantBuyBanner = page.locator("section:has-text('Quick Buy'), button:has-text('Quick Buy')").first();
        this.cardTransferBanner = page.locator("section:has-text('credit'), section:has-text('debit card')").first();
        this.supportBanner = page.locator("section:has-text('Coming Soon'), section:has-text('tokenized')").first();

        // Social Media
        this.socialMediaLinks = page.locator("a[href*='facebook'], a[href*='twitter'], a[href*='linkedin'], a[href*='instagram']");
    }

    // ============================================================
    // Footer Methods
    // ============================================================

    public boolean isFooterDisplayed() {
        try {
            footerSection.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("Footer section is displayed");
            return true;
        } catch (Exception e) {
            log.error("Footer section not displayed", e);
            return false;
        }
    }

    public boolean isAppStoreLinkVisible() {
        try {
            appStoreLink.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("App Store link is visible");
            return true;
        } catch (Exception e) {
            log.warn("App Store link not found", e);
            return false;
        }
    }

    public boolean isGooglePlayLinkVisible() {
        try {
            googlePlayLink.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("Google Play link is visible");
            return true;
        } catch (Exception e) {
            log.warn("Google Play link not found", e);
            return false;
        }
    }

    public String getAppStoreUrl() {
        appStoreLink.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
        String href = appStoreLink.getAttribute("href");
        log.info("App Store URL: {}", href);
        return href;
    }

    public String getGooglePlayUrl() {
        googlePlayLink.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
        String href = googlePlayLink.getAttribute("href");
        log.info("Google Play URL: {}", href);
        return href;
    }

    public boolean isQRCodeVisible() {
        try {
            qrCode.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("QR code is visible");
            return true;
        } catch (Exception e) {
            log.debug("QR code not found", e);
            return false;
        }
    }

    public boolean areMarketingBannersVisible() {
        try {
            // Scroll to make banners visible
            page.evaluate("window.scrollTo(0, document.body.scrollHeight / 2)");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);

            // Wait for at least one banner with explicit timeout
            marketingBanner.first().waitFor(new Locator.WaitForOptions().setTimeout(shortTimeout));
            int count = marketingBanner.count();
            log.info("Found {} marketing banners", count);
            return count > 0;
        } catch (Exception e) {
            log.warn("Marketing banners not found");
            return false;
        }
    }

    public List<String> getMarketingBannerTexts() {
        List<String> bannerTexts = new ArrayList<>();
        try {
            marketingBanner.first().waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            List<Locator> banners = marketingBanner.all();

            for (Locator banner : banners) {
                String text = banner.textContent();
                if (text != null && !text.trim().isEmpty()) {
                    bannerTexts.add(text.trim());
                }
            }

            log.info("Marketing banner texts: {}", bannerTexts);
        } catch (Exception e) {
            log.error("Failed to get marketing banner texts", e);
        }

        return bannerTexts;
    }

    public boolean isInstantBuyBannerVisible() {
        try {
            instantBuyBanner.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("Instant Buy banner is visible");
            return true;
        } catch (Exception e) {
            log.debug("Instant Buy banner not found", e);
            return false;
        }
    }

    public boolean isCardTransferBannerVisible() {
        try {
            cardTransferBanner.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("Card/Transfer banner is visible");
            return true;
        } catch (Exception e) {
            log.debug("Card/Transfer banner not found", e);
            return false;
        }
    }

    public boolean isSupportBannerVisible() {
        try {
            supportBanner.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("Support banner is visible");
            return true;
        } catch (Exception e) {
            log.debug("Support banner not found", e);
            return false;
        }
    }

    public boolean isSpecificBannerVisible(String bannerText) {
        try {
            // Scroll to make banners visible
            page.evaluate("window.scrollTo(0, document.body.scrollHeight / 2)");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);

            Locator specificBanner = page.locator(String.format(":has-text('%s')", bannerText)).first();
            specificBanner.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("Banner containing '{}' is visible", bannerText);
            return true;
        } catch (Exception e) {
            log.debug("Banner containing '{}' not found", bannerText, e);
            return false;
        }
    }

    public void clickAppStoreLink() {
        appStoreLink.click();
        log.info("Clicked App Store link");
    }

    public void clickGooglePlayLink() {
        googlePlayLink.click();
        log.info("Clicked Google Play link");
    }

    public boolean areSocialMediaLinksVisible() {
        try {
            // Use shorter timeout since these might not exist
            socialMediaLinks.first().waitFor(new Locator.WaitForOptions().setTimeout(shortTimeout));
            int count = socialMediaLinks.count();
            log.info("Found {} social media links", count);
            return count > 0;
        } catch (Exception e) {
            log.debug("Social media links not found");
            return false;
        }
    }

    public List<String> getSocialMediaLinks() {
        List<String> links = new ArrayList<>();
        try {
            socialMediaLinks.first().waitFor(new Locator.WaitForOptions().setTimeout(shortTimeout));
            List<Locator> socialLinks = socialMediaLinks.all();

            for (Locator link : socialLinks) {
                String href = link.getAttribute("href");
                if (href != null) {
                    links.add(href);
                }
            }

            log.info("Social media links: {}", links);
        } catch (Exception e) {
            log.error("Failed to get social media links", e);
        }

        return links;
    }

    public void scrollToFooter() {
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        log.debug("Scrolled to footer");
    }

    public void verifyDownloadLinksPointToCorrectStores() {
        String appStoreUrl = getAppStoreUrl();
        if (appStoreUrl == null || !appStoreUrl.contains("apple.com")) {
            throw new AssertionError("App Store link is invalid. URL: " + appStoreUrl);
        }
        log.info("App Store link valid: {}", appStoreUrl);

        String googlePlayUrl = getGooglePlayUrl();
        if (googlePlayUrl == null || !googlePlayUrl.contains("play.google.com")) {
            throw new AssertionError("Google Play link is invalid. URL: " + googlePlayUrl);
        }
        log.info("Google Play link valid: {}", googlePlayUrl);
    }
}
