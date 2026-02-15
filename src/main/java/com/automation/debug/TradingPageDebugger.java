package com.automation.debug;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TradingPageDebugger {

    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            log.info("{}", "=".repeat(80));
            log.info("MULTIBANK TRADING PAGE STRUCTURE DEBUGGER");
            log.info("{}", "=".repeat(80));

            page.navigate("https://trade.multibank.io/");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // 1. Check for tables
            log.info("\n1. TABLE STRUCTURE:");
            log.info("{}", "-".repeat(80));

            int tableCount = page.locator("table").count();
            log.info("Number of <table> elements: {}", tableCount);

            if (tableCount > 0) {
                Locator table = page.locator("table").first();
                String tableClasses = table.getAttribute("class");
                log.info("First table classes: {}", tableClasses);

                // Check for table headers
                int headerCount = table.locator("th").count();
                log.info("Number of <th> elements: {}", headerCount);

                if (headerCount > 0) {
                    log.info("\nTable Headers:");
                    for (int i = 0; i < headerCount; i++) {
                        String headerText = table.locator("th").nth(i).textContent();
                        log.info("  - Header {}: {}", i, headerText);
                    }
                }

                // Check for table rows
                int rowCount = table.locator("tbody tr").count();
                log.info("\nNumber of data rows in tbody: {}", rowCount);

                if (rowCount > 0) {
                    log.info("\nFirst 3 rows structure:");
                    int rowsToCheck = Math.min(3, rowCount);
                    for (int i = 0; i < rowsToCheck; i++) {
                        Locator row = table.locator("tbody tr").nth(i);
                        String rowClass = row.getAttribute("class");
                        log.info("  Row {} class: {}", i, rowClass);

                        int cellCount = row.locator("td").count();
                        log.info("  Row {} has {} cells:", i, cellCount);
                        for (int j = 0; j < Math.min(6, cellCount); j++) {
                            String cellText = row.locator("td").nth(j).textContent();
                            String cellClass = row.locator("td").nth(j).getAttribute("class");
                            log.info("    Cell {}: \"{}\" (class: {})", j, cellText, cellClass);
                        }
                    }
                }
            }

            // 2. Check for trading pairs with '/' separator
            log.info("\n2. TRADING PAIRS WITH '/' SEPARATOR:");
            log.info("{}", "-".repeat(80));

            // Try different selectors
            String[] pairSelectors = {
                "tr:has-text('/')",
                "td:has-text('/')",
                "[class*='pair']:has-text('/')",
                "tbody tr",
                "table tr"
            };

            for (String selector : pairSelectors) {
                try {
                    int count = page.locator(selector).count();
                    log.info("Selector: \"{}\" -> Found: {}", selector, count);
                    if (count > 0 && count < 10) {
                        Locator first = page.locator(selector).first();
                        String text = first.textContent();
                        log.info("  First match text: {}", text.substring(0, Math.min(100, text.length())));
                    }
                } catch (Exception e) {
                    log.info("Selector: \"{}\" -> ERROR: {}", selector, e.getMessage());
                }
            }

            // 3. Check for tabs (Favorites, All Pairs, etc.)
            log.info("\n3. TABS/BUTTONS:");
            log.info("{}", "-".repeat(80));

            String[] tabSelectors = {
                "button:has-text('Favorites')",
                "button:has-text('All')",
                "[role='tab']",
                "[class*='tab']",
                "button[class*='tab']"
            };

            for (String selector : tabSelectors) {
                try {
                    int count = page.locator(selector).count();
                    log.info("Selector: \"{}\" -> Found: {}", selector, count);
                    if (count > 0 && count < 10) {
                        for (int i = 0; i < count; i++) {
                            String text = page.locator(selector).nth(i).textContent();
                            log.info("  Tab {}: \"{}\"", i, text);
                        }
                    }
                } catch (Exception e) {
                    log.info("Selector: \"{}\" -> ERROR: {}", selector, e.getMessage());
                }
            }

            // 4. Check for spot/trading sections
            log.info("\n4. SPOT/TRADING SECTIONS:");
            log.info("{}", "-".repeat(80));

            String[] sectionSelectors = {
                "[class*='spot']",
                "[class*='trading']",
                "section:has-text('Spot')",
                ":has-text('Spot Trading')",
                "section"
            };

            for (String selector : sectionSelectors) {
                try {
                    int count = page.locator(selector).count();
                    log.info("Selector: \"{}\" -> Found: {}", selector, count);
                } catch (Exception e) {
                    log.info("Selector: \"{}\" -> ERROR: {}", selector, e.getMessage());
                }
            }

            // 5. Check for trading pair rows with specific patterns
            log.info("\n5. DETECTING TRADING PAIR PATTERN:");
            log.info("{}", "-".repeat(80));

            // Execute JavaScript to find elements containing '/'
            Object result = page.evaluate("() => {" +
                "const elements = Array.from(document.querySelectorAll('td, th, div, span'));" +
                "const withSlash = elements.filter(el => el.textContent && el.textContent.includes('/') && el.textContent.includes('USDT'));" +
                "return withSlash.slice(0, 5).map(el => ({" +
                "  tagName: el.tagName," +
                "  textContent: el.textContent.substring(0, 50)," +
                "  className: el.className," +
                "  parentTag: el.parentElement ? el.parentElement.tagName : null," +
                "  parentClass: el.parentElement ? el.parentElement.className : null" +
                "}));" +
            "}");

            log.info("Elements containing '/' and 'USDT':");
            log.info("{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));

            // 6. Table container analysis
            log.info("\n6. TABLE CONTAINER ANALYSIS:");
            log.info("{}", "-".repeat(80));

            Object containerResult = page.evaluate("() => {" +
                "const table = document.querySelector('table');" +
                "if (!table) return null;" +
                "const container = table.parentElement;" +
                "return {" +
                "  tableClass: table.className," +
                "  containerTag: container.tagName," +
                "  containerClass: container.className," +
                "  rowSelector: 'tbody tr'," +
                "  firstRowClass: table.querySelector('tbody tr') ? table.querySelector('tbody tr').className : null," +
                "  hasVisibleRows: table.querySelectorAll('tbody tr').length > 0" +
                "};" +
            "}");

            log.info("Table Container Info:");
            log.info("{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(containerResult));

            log.info("\n{}", "=".repeat(80));
            log.info("DEBUGGING COMPLETED");
            log.info("{}", "=".repeat(80));

            browser.close();

        } catch (Exception e) {
            log.error("Error during trading page debugging", e);
        }
    }
}
