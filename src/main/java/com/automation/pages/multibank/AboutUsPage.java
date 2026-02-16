package com.automation.pages.multibank;

import com.automation.config.TestConfig;
import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AboutUsPage extends BasePage {

    // ===============================
    // Locator Fields
    // ===============================

    private final Locator aboutUsSection;
    private final Locator whyMultiBankSection;
    private final Locator companyOverviewSection;
    private final Locator trustSecuritySection;
    private final Locator globalPresenceSection;
    private final Locator regulatoryComplianceSection;

    private final Locator pageHeadings;
    private final Locator contentSections;

    public AboutUsPage() {
        super();

        // About Us Sections
        this.aboutUsSection = page.locator("section:has-text('About')");
        this.whyMultiBankSection = page.locator("section:has-text('Why MultiBank')");
        this.companyOverviewSection = page.locator("section:has-text('Company'), section:has-text('Overview')");
        this.trustSecuritySection = page.locator("section:has-text('Trust'), section:has-text('Security')");
        this.globalPresenceSection = page.locator("section:has-text('Global'), section:has-text('Presence')");
        this.regulatoryComplianceSection = page.locator("section:has-text('Regulatory'), section:has-text('Compliance')");

        // Generic Page Components
        this.pageHeadings = page.locator("role=heading");
        this.contentSections = page.locator("section, article");
    }

    // ===============================
    // Navigation
    // ===============================

    public void navigateToAboutUs() {
        // Try clicking the About Us navigation item first
        try {
            Locator aboutUsNav = page.locator("header span:has-text('About Us'), header a:has-text('About Us')").first();
            aboutUsNav.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            aboutUsNav.click();
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            log.info("Clicked About Us navigation item");

            // Check if we navigated to about page
            if (page.url().contains("/about")) {
                log.info("Successfully navigated to About Us page via click");
                return;
            }
        } catch (Exception e) {
            log.warn("Could not click About Us nav, trying direct URL navigation");
        }

        // Fallback to direct URL navigation
        String baseUrl = TestConfig.getInstance().getBaseUrl();
        String aboutUrl = baseUrl + "/about";
        navigateTo(aboutUrl);
        log.info("Navigated to About Us page via URL: {}", aboutUrl);
    }

    // ===============================
    // Section Visibility Checks
    // ===============================

    public boolean isAboutUsPageDisplayed() {
        return aboutUsSection.isVisible();
    }

    public boolean isWhyMultiBankSectionVisible() {
        return whyMultiBankSection.isVisible();
    }

    public boolean isCompanyOverviewVisible() {
        return companyOverviewSection.isVisible();
    }

    public boolean isTrustSecuritySectionVisible() {
        return trustSecuritySection.isVisible();
    }

    public boolean isGlobalPresenceSectionVisible() {
        return globalPresenceSection.isVisible();
    }

    public boolean isRegulatoryComplianceSectionVisible() {
        return regulatoryComplianceSection.isVisible();
    }

    // ===============================
    // Headings & Content Sections
    // ===============================

    public String getPageHeading() {
        String heading = pageHeadings.first().textContent().trim();
        log.info("Page heading: {}", heading);
        return heading;
    }

    public List<String> getAllSectionHeadings() {
        List<String> headings = new ArrayList<>();

        try {
            for (Locator heading : pageHeadings.all()) {
                String text = heading.textContent();
                if (text != null && !text.trim().isEmpty()) {
                    headings.add(text.trim());
                }
            }

            log.info("Found {} section headings: {}", headings.size(), headings);
        } catch (Exception e) {
            log.error("Failed to get section headings", e);
        }

        return headings;
    }

    public int getContentSectionsCount() {
        int count = contentSections.count();
        log.info("Total content sections: {}", count);
        return count;
    }

    public boolean isContentLoaded() {
        try {
            int sections = getContentSectionsCount();
            boolean loaded = sections > 0;
            log.info("Content loaded: {}, sections: {}", loaded, sections);
            return loaded;
        } catch (Exception e) {
            log.error("Content not loaded", e);
            return false;
        }
    }

    // ===============================
    // Dynamic Component Checks
    // ===============================

    public boolean verifyAllExpectedComponentsPresent(List<String> expectedComponents) {
        boolean allPresent = true;

        for (String component : expectedComponents) {
            Locator componentLocator = page.locator(String.format("section:has-text('%s')", component));

            if (!componentLocator.isVisible()) {
                log.warn("Component '{}' is missing", component);
                allPresent = false;
            } else {
                log.debug("Component '{}' is present", component);
            }
        }

        return allPresent;
    }

    // ===============================
    // Section Text Retrieval
    // ===============================

    public String getWhyMultiBankText() {
        try {
            return whyMultiBankSection.first().textContent();
        } catch (Exception e) {
            log.error("Failed to get Why MultiBank text", e);
            return "";
        }
    }
}
