package com.automation.pages.multibank;

import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FooterPage extends BasePage {

    // Footer Selectors - MultiBank uses a div container instead of semantic footer
    private final String footerSection = "[class*='app-download-container'], [class*='buttons-container']";
    private final String appStoreLink = "a[href*='apps.apple.com'], a[href*='appstore'], img[alt*='App Store']";
    private final String googlePlayLink = "a[href*='play.google.com'], a[href*='googleplay'], img[alt*='Google Play']";
    private final String qrCode = "img[alt='qr-code'], img[alt*='qr']";

    // Marketing Banners
    private final String marketingBanner = "[class*='banner'], [class*='promo'], [class*='marketing']";
    private final String instantBuyBanner = ":has-text('Instant Buy')";
    private final String cardTransferBanner = ":has-text('Card'), :has-text('Wire Transfer')";
    private final String supportBanner = ":has-text('24/7'), :has-text('Support')";

    // Social Media Links
    private final String socialMediaLinks = "a[href*='facebook'], a[href*='twitter'], a[href*='linkedin'], a[href*='instagram']";

    public FooterPage() {
        super();
    }

    public boolean isFooterDisplayed() {
        try {
            waitForSelector(footerSection);
            log.debug("Footer section is displayed");
            return true;
        } catch (Exception e) {
            log.error("Footer section not displayed", e);
            return false;
        }
    }

    public boolean isAppStoreLinkVisible() {
        try {
            waitForSelector(appStoreLink);
            log.debug("App Store link is visible");
            return true;
        } catch (Exception e) {
            log.warn("App Store link not found");
            return false;
        }
    }

    public boolean isGooglePlayLinkVisible() {
        try {
            waitForSelector(googlePlayLink);
            log.debug("Google Play link is visible");
            return true;
        } catch (Exception e) {
            log.warn("Google Play link not found");
            return false;
        }
    }

    public String getAppStoreUrl() {
        waitForSelector(appStoreLink);
        String href = page.locator(appStoreLink).first().getAttribute("href");
        log.info("App Store URL: {}", href);
        return href;
    }

    public String getGooglePlayUrl() {
        waitForSelector(googlePlayLink);
        String href = page.locator(googlePlayLink).first().getAttribute("href");
        log.info("Google Play URL: {}", href);
        return href;
    }

    public boolean isQRCodeVisible() {
        try {
            waitForSelector(qrCode);
            log.debug("QR code is visible");
            return true;
        } catch (Exception e) {
            log.debug("QR code not found");
            return false;
        }
    }

    public boolean areMarketingBannersVisible() {
        try {
            waitForSelector(marketingBanner);
            int count = page.locator(marketingBanner).count();
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
            waitForSelector(marketingBanner);
            List<Locator> banners = page.locator(marketingBanner).all();

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
            waitForSelector(instantBuyBanner);
            log.debug("Instant Buy banner is visible");
            return true;
        } catch (Exception e) {
            log.debug("Instant Buy banner not found");
            return false;
        }
    }

    public boolean isCardTransferBannerVisible() {
        try {
            waitForSelector(cardTransferBanner);
            log.debug("Card/Transfer banner is visible");
            return true;
        } catch (Exception e) {
            log.debug("Card/Transfer banner not found");
            return false;
        }
    }

    public boolean isSupportBannerVisible() {
        try {
            waitForSelector(supportBanner);
            log.debug("Support banner is visible");
            return true;
        } catch (Exception e) {
            log.debug("Support banner not found");
            return false;
        }
    }

    public void clickAppStoreLink() {
        click(appStoreLink);
        log.info("Clicked App Store link");
    }

    public void clickGooglePlayLink() {
        click(googlePlayLink);
        log.info("Clicked Google Play link");
    }

    public boolean areSocialMediaLinksVisible() {
        try {
            waitForSelector(socialMediaLinks);
            int count = page.locator(socialMediaLinks).count();
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
            waitForSelector(socialMediaLinks);
            List<Locator> socialLinks = page.locator(socialMediaLinks).all();

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

    public boolean verifyDownloadLinksPointToCorrectStores() {
        boolean appStoreValid = false;
        boolean googlePlayValid = false;

        try {
            String appStoreUrl = getAppStoreUrl();
            appStoreValid = appStoreUrl != null && appStoreUrl.contains("apple.com");
            log.info("App Store link valid: {}", appStoreValid);
        } catch (Exception e) {
            log.error("Failed to verify App Store link", e);
        }

        try {
            String googlePlayUrl = getGooglePlayUrl();
            googlePlayValid = googlePlayUrl != null && googlePlayUrl.contains("play.google.com");
            log.info("Google Play link valid: {}", googlePlayValid);
        } catch (Exception e) {
            log.error("Failed to verify Google Play link", e);
        }

        return appStoreValid && googlePlayValid;
    }
}
