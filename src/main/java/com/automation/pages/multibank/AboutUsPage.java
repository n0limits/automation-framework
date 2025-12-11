package com.automation.pages.multibank;

import com.automation.config.TestConfig;
import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AboutUsPage extends BasePage {

    // About Us Page Selectors
    private final String aboutUsSection = "section:has-text('About'), [class*='about']";
    private final String whyMultiBankSection = ":has-text('Why MultiBank'), :has-text('Why MultiLink')";
    private final String companyOverview = ":has-text('Company'), :has-text('Overview')";
    private final String trustSecuritySection = ":has-text('Trust'), :has-text('Security')";
    private final String globalPresenceSection = ":has-text('Global'), :has-text('Presence')";
    private final String regulatoryComplianceSection = ":has-text('Regulatory'), :has-text('Compliance')";

    // Page Components
    private final String pageHeading = "h1, h2, [role='heading']";
    private final String contentSections = "section, article, [class*='content']";

    public AboutUsPage() {
        super();
    }

    public void navigateToAboutUs() {
        String baseUrl = TestConfig.getInstance().getBaseUrl();
        String aboutUrl = baseUrl + "/about";
        navigateTo(aboutUrl);
        log.info("Navigated to About Us page: {}", aboutUrl);
    }

    public boolean isAboutUsPageDisplayed() {
        try {
            waitForSelector(aboutUsSection);
            log.debug("About Us page is displayed");
            return true;
        } catch (Exception e) {
            log.error("About Us page not displayed", e);
            return false;
        }
    }

    public boolean isWhyMultiBankSectionVisible() {
        try {
            waitForSelector(whyMultiBankSection);
            log.debug("Why MultiBank section is visible");
            return true;
        } catch (Exception e) {
            log.warn("Why MultiBank section not found");
            return false;
        }
    }

    public boolean isCompanyOverviewVisible() {
        try {
            waitForSelector(companyOverview);
            log.debug("Company Overview section is visible");
            return true;
        } catch (Exception e) {
            log.debug("Company Overview section not found");
            return false;
        }
    }

    public boolean isTrustSecuritySectionVisible() {
        try {
            waitForSelector(trustSecuritySection);
            log.debug("Trust & Security section is visible");
            return true;
        } catch (Exception e) {
            log.debug("Trust & Security section not found");
            return false;
        }
    }

    public boolean isGlobalPresenceSectionVisible() {
        try {
            waitForSelector(globalPresenceSection);
            log.debug("Global Presence section is visible");
            return true;
        } catch (Exception e) {
            log.debug("Global Presence section not found");
            return false;
        }
    }

    public boolean isRegulatoryComplianceSectionVisible() {
        try {
            waitForSelector(regulatoryComplianceSection);
            log.debug("Regulatory Compliance section is visible");
            return true;
        } catch (Exception e) {
            log.debug("Regulatory Compliance section not found");
            return false;
        }
    }

    public String getPageHeading() {
        waitForSelector(pageHeading);
        String heading = page.locator(pageHeading).first().textContent();
        log.info("Page heading: {}", heading);
        return heading;
    }

    public List<String> getAllSectionHeadings() {
        List<String> headings = new ArrayList<>();
        try {
            waitForSelector(pageHeading);
            List<Locator> headingLocators = page.locator(pageHeading).all();

            for (Locator locator : headingLocators) {
                String text = locator.textContent();
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
        waitForSelector(contentSections);
        int count = page.locator(contentSections).count();
        log.info("Total content sections: {}", count);
        return count;
    }

    public boolean verifyAllExpectedComponentsPresent(List<String> expectedComponents) {
        boolean allPresent = true;

        for (String component : expectedComponents) {
            try {
                String selector = String.format(":has-text('%s')", component);
                waitForSelector(selector);
                log.debug("Component '{}' is present", component);
            } catch (Exception e) {
                log.warn("Component '{}' is missing", component);
                allPresent = false;
            }
        }

        return allPresent;
    }

    public String getWhyMultiBankText() {
        try {
            waitForSelector(whyMultiBankSection);
            String text = page.locator(whyMultiBankSection).first().textContent();
            log.info("Why MultiBank text: {}", text);
            return text;
        } catch (Exception e) {
            log.error("Failed to get Why MultiBank text", e);
            return "";
        }
    }

    public boolean isContentLoaded() {
        try {
            waitForSelector(contentSections);
            int sectionsCount = getContentSectionsCount();
            boolean loaded = sectionsCount > 0;
            log.info("Content loaded: {}, sections count: {}", loaded, sectionsCount);
            return loaded;
        } catch (Exception e) {
            log.error("Content not loaded", e);
            return false;
        }
    }
}
