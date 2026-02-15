package com.automation.debug;

import com.automation.config.TestConfig;
import com.automation.factory.BrowserFactory;
import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

/**
 * Enhanced debugger to understand the actual page structure
 */
@Slf4j
public class EnhancedPageStructureDebugger {

    public static void main(String[] args) {
        TestConfig config = TestConfig.getInstance();
        log.info("Starting Enhanced Page Structure Debugger for: {}", config.getBaseUrl());

        PlaywrightManager.initPlaywright();
        BrowserFactory.launchBrowser("chromium");
        Page page = PlaywrightManager.getPage();

        try {
            page.navigate(config.getBaseUrl());
            page.waitForLoadState();
            log.info("Page loaded successfully");

            // Scroll to bottom to ensure all content loads
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);

            // Find download section
            log.info("\n========================================");
            log.info("DOWNLOAD SECTION ANALYSIS");
            log.info("========================================");

            // Check for app store links
            Object appStoreInfo = page.evaluate("() => {" +
                "const links = [];" +
                "document.querySelectorAll('a').forEach(link => {" +
                "  if (link.href.includes('apple.com') || link.href.includes('appstore')) {" +
                "    links.push({" +
                "      href: link.href," +
                "      text: link.textContent.trim()," +
                "      visible: link.offsetParent !== null," +
                "      parentClass: link.parentElement ? link.parentElement.className : ''," +
                "      hasImg: link.querySelector('img') !== null" +
                "    });" +
                "  }" +
                "});" +
                "return links;" +
            "}");
            log.info("App Store links found: {}", appStoreInfo);

            // Check for Google Play links
            Object googlePlayInfo = page.evaluate("() => {" +
                "const links = [];" +
                "document.querySelectorAll('a').forEach(link => {" +
                "  if (link.href.includes('play.google.com') || link.href.includes('googleplay')) {" +
                "    links.push({" +
                "      href: link.href," +
                "      text: link.textContent.trim()," +
                "      visible: link.offsetParent !== null," +
                "      parentClass: link.parentElement ? link.parentElement.className : ''," +
                "      hasImg: link.querySelector('img') !== null" +
                "    });" +
                "  }" +
                "});" +
                "return links;" +
            "}");
            log.info("Google Play links found: {}", googlePlayInfo);

            // Check for QR code images
            Object qrCodeInfo = page.evaluate("() => {" +
                "const images = [];" +
                "document.querySelectorAll('img').forEach(img => {" +
                "  const alt = img.alt || '';" +
                "  const src = img.src || '';" +
                "  if (alt.toLowerCase().includes('qr') || src.toLowerCase().includes('qr')) {" +
                "    images.push({" +
                "      alt: img.alt," +
                "      src: img.src," +
                "      visible: img.offsetParent !== null," +
                "      width: img.width," +
                "      height: img.height" +
                "    });" +
                "  }" +
                "});" +
                "return images;" +
            "}");
            log.info("QR code images found: {}", qrCodeInfo);

            // Find parent container of download links
            log.info("\n========================================");
            log.info("DOWNLOAD SECTION CONTAINER");
            log.info("========================================");

            Object downloadSectionInfo = page.evaluate("() => {" +
                "const appStoreLink = document.querySelector('a[href*=\"apps.apple.com\"]');" +
                "if (appStoreLink) {" +
                "  let parent = appStoreLink.parentElement;" +
                "  let depth = 0;" +
                "  const parents = [];" +
                "  while (parent && depth < 5) {" +
                "    parents.push({" +
                "      tag: parent.tagName," +
                "      class: parent.className," +
                "      id: parent.id," +
                "      depth: depth" +
                "    });" +
                "    parent = parent.parentElement;" +
                "    depth++;" +
                "  }" +
                "  return parents;" +
                "}" +
                "return null;" +
            "}");
            log.info("Download section container hierarchy: {}", downloadSectionInfo);

            // Check main page sections
            log.info("\n========================================");
            log.info("MAIN PAGE SECTIONS");
            log.info("========================================");

            Object mainSections = page.evaluate("() => {" +
                "const sections = [];" +
                "document.querySelectorAll('section, div[class*=\"section\"], div[class*=\"container\"]').forEach(el => {" +
                "  if (el.className && el.offsetParent !== null) {" +
                "    sections.push({" +
                "      tag: el.tagName," +
                "      class: el.className," +
                "      id: el.id," +
                "      hasText: el.textContent.length > 0," +
                "      textSample: el.textContent.substring(0, 100).trim()" +
                "    });" +
                "  }" +
                "});" +
                "return sections.slice(0, 20);" + // Limit to first 20
            "}");
            log.info("Main sections found: {}", mainSections);

            // Check for actual footer-like content at bottom
            log.info("\n========================================");
            log.info("BOTTOM PAGE CONTENT");
            log.info("========================================");

            Object bottomContent = page.evaluate("() => {" +
                "const bodyHeight = document.body.scrollHeight;" +
                "const elements = [];" +
                "document.querySelectorAll('div, section').forEach(el => {" +
                "  const rect = el.getBoundingClientRect();" +
                "  const scrollY = window.pageYOffset || document.documentElement.scrollTop;" +
                "  const absoluteTop = rect.top + scrollY;" +
                "  // Check if element is in bottom 20% of page" +
                "  if (absoluteTop > bodyHeight * 0.8) {" +
                "    elements.push({" +
                "      tag: el.tagName," +
                "      class: el.className," +
                "      id: el.id," +
                "      position: Math.round((absoluteTop / bodyHeight) * 100) + '%'" +
                "    });" +
                "  }" +
                "});" +
                "return elements.slice(0, 15);" +
            "}");
            log.info("Bottom page content: {}", bottomContent);

            // Check navigation structure
            log.info("\n========================================");
            log.info("NAVIGATION STRUCTURE");
            log.info("========================================");

            Object headerStructure = page.evaluate("() => {" +
                "const header = document.querySelector('header');" +
                "if (header) {" +
                "  const allLinks = [];" +
                "  header.querySelectorAll('a').forEach(link => {" +
                "    allLinks.push({" +
                "      text: link.textContent.trim()," +
                "      href: link.href," +
                "      visible: link.offsetParent !== null" +
                "    });" +
                "  });" +
                "  return {" +
                "    headerClass: header.className," +
                "    links: allLinks" +
                "  };" +
                "}" +
                "return null;" +
            "}");
            log.info("Header structure: {}", headerStructure);

            log.info("\n========================================");
            log.info("DEBUGGING COMPLETE");
            log.info("========================================");

        } catch (Exception e) {
            log.error("Error during debugging", e);
        } finally {
            PlaywrightManager.quitPlaywright();
        }
    }
}
