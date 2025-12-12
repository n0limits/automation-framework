package com.automation.debug;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TradingPageDebugger {

    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            System.out.println("=".repeat(80));
            System.out.println("MULTIBANK TRADING PAGE STRUCTURE DEBUGGER");
            System.out.println("=".repeat(80));

            page.navigate("https://trade.multibank.io/");
            Thread.sleep(8000); // Wait for dynamic content to load

            // 1. Check for tables
            System.out.println("\n1. TABLE STRUCTURE:");
            System.out.println("-".repeat(80));

            int tableCount = page.locator("table").count();
            System.out.println("Number of <table> elements: " + tableCount);

            if (tableCount > 0) {
                Locator table = page.locator("table").first();
                String tableClasses = table.getAttribute("class");
                System.out.println("First table classes: " + tableClasses);

                // Check for table headers
                int headerCount = table.locator("th").count();
                System.out.println("Number of <th> elements: " + headerCount);

                if (headerCount > 0) {
                    System.out.println("\nTable Headers:");
                    for (int i = 0; i < headerCount; i++) {
                        String headerText = table.locator("th").nth(i).textContent();
                        System.out.println("  - Header " + i + ": " + headerText);
                    }
                }

                // Check for table rows
                int rowCount = table.locator("tbody tr").count();
                System.out.println("\nNumber of data rows in tbody: " + rowCount);

                if (rowCount > 0) {
                    System.out.println("\nFirst 3 rows structure:");
                    int rowsToCheck = Math.min(3, rowCount);
                    for (int i = 0; i < rowsToCheck; i++) {
                        Locator row = table.locator("tbody tr").nth(i);
                        String rowClass = row.getAttribute("class");
                        System.out.println("  Row " + i + " class: " + rowClass);

                        int cellCount = row.locator("td").count();
                        System.out.println("  Row " + i + " has " + cellCount + " cells:");
                        for (int j = 0; j < Math.min(6, cellCount); j++) {
                            String cellText = row.locator("td").nth(j).textContent();
                            String cellClass = row.locator("td").nth(j).getAttribute("class");
                            System.out.println("    Cell " + j + ": \"" + cellText + "\" (class: " + cellClass + ")");
                        }
                    }
                }
            }

            // 2. Check for trading pairs with '/' separator
            System.out.println("\n2. TRADING PAIRS WITH '/' SEPARATOR:");
            System.out.println("-".repeat(80));

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
                    System.out.println("Selector: \"" + selector + "\" -> Found: " + count);
                    if (count > 0 && count < 10) {
                        Locator first = page.locator(selector).first();
                        String text = first.textContent();
                        System.out.println("  First match text: " + text.substring(0, Math.min(100, text.length())));
                    }
                } catch (Exception e) {
                    System.out.println("Selector: \"" + selector + "\" -> ERROR: " + e.getMessage());
                }
            }

            // 3. Check for tabs (Favorites, All Pairs, etc.)
            System.out.println("\n3. TABS/BUTTONS:");
            System.out.println("-".repeat(80));

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
                    System.out.println("Selector: \"" + selector + "\" -> Found: " + count);
                    if (count > 0 && count < 10) {
                        for (int i = 0; i < count; i++) {
                            String text = page.locator(selector).nth(i).textContent();
                            System.out.println("  Tab " + i + ": \"" + text + "\"");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Selector: \"" + selector + "\" -> ERROR: " + e.getMessage());
                }
            }

            // 4. Check for spot/trading sections
            System.out.println("\n4. SPOT/TRADING SECTIONS:");
            System.out.println("-".repeat(80));

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
                    System.out.println("Selector: \"" + selector + "\" -> Found: " + count);
                } catch (Exception e) {
                    System.out.println("Selector: \"" + selector + "\" -> ERROR: " + e.getMessage());
                }
            }

            // 5. Check for trading pair rows with specific patterns
            System.out.println("\n5. DETECTING TRADING PAIR PATTERN:");
            System.out.println("-".repeat(80));

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

            System.out.println("Elements containing '/' and 'USDT':");
            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));

            // 6. Table container analysis
            System.out.println("\n6. TABLE CONTAINER ANALYSIS:");
            System.out.println("-".repeat(80));

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

            System.out.println("Table Container Info:");
            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(containerResult));

            System.out.println("\n" + "=".repeat(80));
            System.out.println("DEBUGGING COMPLETED");
            System.out.println("=".repeat(80));

            Thread.sleep(3000);
            browser.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
