package com.automation.debug;

import com.automation.config.TestConfig;
import com.automation.factory.BrowserFactory;
import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to debug and identify correct selectors on the MultiBank website
 * Run this to find the actual selectors present on the page
 */
@Slf4j
public class SelectorDebugger {

    public static void main(String[] args) {
        TestConfig config = TestConfig.getInstance();

        log.info("Starting Selector Debugger for: {}", config.getBaseUrl());

        // Initialize Playwright
        PlaywrightManager.initPlaywright();
        BrowserFactory.launchBrowser("chromium");
        Page page = PlaywrightManager.getPage();

        try {
            // Navigate to the page
            page.navigate(config.getBaseUrl());
            page.waitForLoadState();

            log.info("\n========================================");
            log.info("PAGE LOADED SUCCESSFULLY");
            log.info("========================================\n");

            // Debug Footer
            debugFooterSelectors(page);

            // Debug Navigation
            debugNavigationSelectors(page);

            // Debug About Us Link
            debugAboutUsSelectors(page);

            // Debug QR Code
            debugQRCodeSelectors(page);

            // Print page HTML structure
            log.info("\n========================================");
            log.info("PAGE STRUCTURE ANALYSIS");
            log.info("========================================");

            // Check for footer-like elements
            log.info("\nFooter-like elements:");
            Object footerElements = page.evaluate("() => {" +
                "const elements = [];" +
                "document.querySelectorAll('[class*=\"footer\"], [class*=\"Footer\"], [id*=\"footer\"]').forEach(el => {" +
                "  elements.push({" +
                "    tag: el.tagName," +
                "    className: el.className," +
                "    id: el.id" +
                "  });" +
                "});" +
                "return elements;" +
            "}");
            log.info("Footer elements: {}", footerElements);

            // Check for navigation elements
            log.info("\nNavigation elements:");
            Object navElements = page.evaluate("() => {" +
                "const elements = [];" +
                "document.querySelectorAll('nav, [role=\"navigation\"], [class*=\"nav\"], [class*=\"Nav\"], header').forEach(el => {" +
                "  elements.push({" +
                "    tag: el.tagName," +
                "    className: el.className," +
                "    id: el.id," +
                "    role: el.getAttribute('role')" +
                "  });" +
                "});" +
                "return elements;" +
            "}");
            log.info("Navigation elements: {}", navElements);

            // Check for About Us links
            log.info("\nAbout-related links:");
            Object aboutLinks = page.evaluate("() => {" +
                "const links = [];" +
                "document.querySelectorAll('a').forEach(link => {" +
                "  const text = link.textContent.toLowerCase();" +
                "  const href = link.href;" +
                "  if (text.includes('about') || href.includes('about')) {" +
                "    links.push({" +
                "      text: link.textContent.trim()," +
                "      href: link.href," +
                "      visible: link.offsetParent !== null" +
                "    });" +
                "  }" +
                "});" +
                "return links;" +
            "}");
            log.info("About links: {}", aboutLinks);

            // Scroll to bottom and re-check
            log.info("\n========================================");
            log.info("SCROLLING TO BOTTOM");
            log.info("========================================\n");

            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
            page.waitForTimeout(2000); // Wait for lazy-loaded content

            debugFooterSelectors(page);

            log.info("\n========================================");
            log.info("DEBUGGING COMPLETE");
            log.info("========================================\n");

        } catch (Exception e) {
            log.error("Error during debugging", e);
        } finally {
            // Cleanup
            PlaywrightManager.quitPlaywright();
        }
    }

    private static void debugFooterSelectors(Page page) {
        log.info("\n--- FOOTER SELECTORS ---");

        // Try different footer selectors
        String[] footerSelectors = {
            "footer",
            "[role='contentinfo']",
            "[class*='footer']",
            "[class*='Footer']",
            "[id*='footer']",
            "div[class*='footer']",
            "section[class*='footer']"
        };

        for (String selector : footerSelectors) {
            try {
                int count = page.locator(selector).count();
                if (count > 0) {
                    log.info("FOUND: {} - Count: {}", selector, count);
                    log.info("  First element: {}", page.locator(selector).first().evaluate("el => ({" +
                        "tag: el.tagName," +
                        "class: el.className," +
                        "id: el.id," +
                        "visible: el.offsetParent !== null" +
                    "})"));
                } else {
                    log.info("NOT FOUND: {}", selector);
                }
            } catch (Exception e) {
                log.info("ERROR: {} - {}", selector, e.getMessage());
            }
        }
    }

    private static void debugNavigationSelectors(Page page) {
        log.info("\n--- NAVIGATION SELECTORS ---");

        String[] navSelectors = {
            "nav",
            "header nav",
            "[role='navigation']",
            "header",
            "[class*='nav']",
            "[class*='Nav']",
            "[id*='nav']"
        };

        for (String selector : navSelectors) {
            try {
                int count = page.locator(selector).count();
                if (count > 0) {
                    log.info("FOUND: {} - Count: {}", selector, count);
                } else {
                    log.info("NOT FOUND: {}", selector);
                }
            } catch (Exception e) {
                log.info("ERROR: {} - {}", selector, e.getMessage());
            }
        }
    }

    private static void debugAboutUsSelectors(Page page) {
        log.info("\n--- ABOUT US SELECTORS ---");

        String[] aboutSelectors = {
            "a[href*='about']",
            "a:has-text('About')",
            "a:has-text('About Us')",
            "a:text-is('About')",
            "[href*='/about']",
            "[href*='/about-us']"
        };

        for (String selector : aboutSelectors) {
            try {
                int count = page.locator(selector).count();
                if (count > 0) {
                    log.info("FOUND: {} - Count: {}", selector, count);
                    try {
                        Object info = page.locator(selector).first().evaluate("el => ({" +
                            "text: el.textContent," +
                            "href: el.href," +
                            "visible: el.offsetParent !== null" +
                        "})");
                        log.info("  Details: {}", info);
                    } catch (Exception e) {
                        log.info("  Could not get details");
                    }
                } else {
                    log.info("NOT FOUND: {}", selector);
                }
            } catch (Exception e) {
                log.info("ERROR: {} - {}", selector, e.getMessage());
            }
        }
    }

    private static void debugQRCodeSelectors(Page page) {
        log.info("\n--- QR CODE SELECTORS ---");

        String[] qrSelectors = {
            "img[alt*='QR']",
            "img[alt*='qr']",
            "[class*='qr-code']",
            "[class*='qrcode']",
            "[class*='QR']",
            "canvas",
            "svg[class*='qr']"
        };

        for (String selector : qrSelectors) {
            try {
                int count = page.locator(selector).count();
                if (count > 0) {
                    log.info("FOUND: {} - Count: {}", selector, count);
                } else {
                    log.info("NOT FOUND: {}", selector);
                }
            } catch (Exception e) {
                log.info("ERROR: {} - {}", selector, e.getMessage());
            }
        }
    }
}
