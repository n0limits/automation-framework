package com.automation.pages.multibank;

import com.automation.config.TestConfig;
import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TradingPage extends BasePage {

    // Configurable timeouts from TestConfig
    private final int elementTimeout;
    private final int shortTimeout;

    // =========================
    // Locator Fields
    // =========================

    private final Locator spotTab;
    private final Locator favoritesTab;
    private final Locator allPairsTab;

    private final Locator tradingPairsTable;
    private final Locator tradingPairRows;

    private final Locator fearIndex;
    private final Locator topGainers;
    private final Locator topLosers;

    private final Locator mbgTokenSection;
    private final Locator realWorldAssetsSection;

    private final Locator convertAssetsButton;
    private final Locator quickBuyButton;
    private final Locator panicSellButton;

    public TradingPage() {
        super();

        // Initialize configurable timeouts
        TestConfig config = TestConfig.getInstance();
        this.elementTimeout = config.getElementTimeout();
        this.shortTimeout = config.getShortTimeout();

        // Trading Tabs - using exact text from page
        this.spotTab = page.locator(":has-text('Spot'):not(:has-text('Favorites')):not(:has-text('All'))").first();
        this.favoritesTab = page.locator(":has-text('Favorites')").first();
        this.allPairsTab = page.locator(":has-text('All'):not(:has-text('Favorites'))").first();

        // Trading Table - standard HTML table with specific columns
        this.tradingPairsTable = page.locator("table").first();
        this.tradingPairRows = tradingPairsTable.locator("tbody tr, tr:has(td)");

        // Market Indicators - using exact text from actual page
        this.fearIndex = page.locator(":has-text('Fear Index')").first();
        this.topGainers = page.locator(":has-text('Top Gainers')").first();
        this.topLosers = page.locator(":has-text('Top Losers')").first();

        // Investment Opportunities - using exact structure from page
        this.mbgTokenSection = page.locator("section[class*='investment'], div[class*='mbg']:has-text('Pay Trading Fees'), div:has-text('Backed by $29B'), section:has-text('Pay Trading Fees with MBG')").first();
        this.realWorldAssetsSection = page.locator("section[class*='investment'], div[class*='rwa']:has-text('Real World Assets'), section:has-text('Invest in tokenized Real World Assets')").first();

        // Quick Access Buttons - using exact button text from page
        this.convertAssetsButton = page.locator("button:has-text('Convert Your Assets'), button:has-text('Convert')").first();
        this.quickBuyButton = page.locator("button:has-text('Instant Buy'), button:has-text('Quick Buy Crypto'), button:has-text('Quick Buy')").first();
        this.panicSellButton = page.locator("button:has-text('Panic Sell'), button:has-text('Quick Sell Your Assets'), button:has-text('Quick Sell')").first();
    }

    // =========================
    // Trading Section Checks
    // =========================

    public boolean isSpotTradingSectionDisplayed() {
        try {
            tradingPairsTable.waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
            log.debug("Spot trading section is displayed");
            return true;
        } catch (Exception e) {
            log.error("Spot trading section not displayed", e);
            return false;
        }
    }

    public boolean isFavoritesTabVisible() {
        return favoritesTab.isVisible();
    }

    public boolean isAllPairsTabVisible() {
        return allPairsTab.isVisible();
    }

    public void clickFavoritesTab() {
        favoritesTab.click();
        log.info("Clicked Favorites tab");
    }

    public void clickAllPairsTab() {
        allPairsTab.click();
        log.info("Clicked All Pairs tab");
    }

    // =========================
    // Table
    // =========================

    public boolean isTradingPairsTableDisplayed() {
        return tradingPairsTable.isVisible();
    }

    public int getTradingPairsCount() {
        try {
            // Wait for table to be populated with data
            tradingPairRows.first().waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));
        } catch (Exception e) {
            log.warn("No trading pair rows found", e);
        }
        int count = tradingPairRows.count();
        log.info("Total trading pairs count: {}", count);
        return count;
    }

    public List<String> getTradingPairs() {
        List<String> pairs = new ArrayList<>();
        try {
            // Wait for rows to be present
            tradingPairRows.first().waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));

            // Wait for data to populate using configurable polling
            waitForDataToPopulate(tradingPairRows.first().locator("td").first());

            List<Locator> rows = tradingPairRows.all();

            for (Locator row : rows) {
                Locator firstCell = row.locator("td").first();
                if (firstCell.count() > 0) {
                    String pair = firstCell.textContent().trim();
                    if (!pair.isEmpty()) {
                        pairs.add(pair);
                    }
                }
            }

            log.info("Found {} trading pairs", pairs.size());
        } catch (Exception e) {
            log.error("Failed to get trading pairs", e);
        }
        return pairs;
    }

    /**
     * Wait for a cell to have content using Playwright's waitForCondition.
     */
    private void waitForDataToPopulate(Locator cell) {
        try {
            page.waitForCondition(() -> {
                String text = cell.textContent();
                return text != null && !text.trim().isEmpty();
            }, new Page.WaitForConditionOptions().setTimeout(elementTimeout));
            log.debug("Data populated successfully");
        } catch (Exception e) {
            log.warn("Data did not populate within timeout ({}ms)", elementTimeout);
        }
    }

    public boolean isTradingPairVisible(String pairName) {
        try {
            List<Locator> rows = tradingPairRows.all();
            for (Locator row : rows) {
                Locator firstCell = row.locator("td").first();
                if (firstCell.count() > 0) {
                    String firstCellText = firstCell.textContent();
                    if (firstCellText != null && firstCellText.contains(pairName)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("Error checking visibility of pair {}", pairName, e);
            return false;
        }
    }

    public Map<String, String> getTradingPairData(String pairName) {
        Map<String, String> data = new HashMap<>();
        try {
            // Wait for rows to be present and populated with data
            tradingPairRows.first().waitFor(new Locator.WaitForOptions().setTimeout(elementTimeout));

            // Wait for data to populate using configurable polling
            waitForDataToPopulate(tradingPairRows.first().locator("td").first());

            List<Locator> rows = tradingPairRows.all();

            for (Locator row : rows) {
                Locator firstCell = row.locator("td").first();

                if (firstCell.count() > 0) {
                    String firstCellText = firstCell.textContent();

                    if (firstCellText != null && firstCellText.contains(pairName)) {
                        List<Locator> cells = row.locator("td").all();

                        // Columns: Pair, Max Leverage, Short, Long, Charts, Change 24h
                        if (cells.size() > 0) data.put("pair", cells.get(0).textContent());
                        if (cells.size() > 1) data.put("leverage", cells.get(1).textContent());
                        if (cells.size() > 5) data.put("change", cells.get(5).textContent());

                        log.info("Retrieved trading pair data for {}", pairName);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get trading pair data for {}", pairName, e);
        }
        return data;
    }

    public List<String> getTableColumns() {
        List<String> columns = new ArrayList<>();
        try {
            // Target: Pair, Max Leverage, Short, Long, Charts, Change 24h
            Locator headers = tradingPairsTable.locator("thead th, thead td, th");

            for (Locator h : headers.all()) {
                String text = h.textContent();
                if (text != null && !text.trim().isEmpty()) {
                    columns.add(text.trim());
                }
            }

            log.info("Found table columns: {}", columns);
        } catch (Exception e) {
            log.error("Failed to read table columns", e);
        }

        return columns;
    }

    // =========================
    // Market Indicators
    // =========================

    public boolean isFearIndexVisible() {
        return fearIndex.isVisible();
    }

    public boolean areTopGainersVisible() {
        return topGainers.isVisible();
    }

    public boolean areTopLosersVisible() {
        return topLosers.isVisible();
    }

    // =========================
    // Investment Opportunities
    // =========================

    public boolean isMBGTokenSectionVisible() {
        try {
            // Scroll down to make section visible
            page.evaluate("window.scrollTo(0, document.body.scrollHeight * 0.6)");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            mbgTokenSection.waitFor(new Locator.WaitForOptions().setTimeout(shortTimeout));
            return mbgTokenSection.isVisible();
        } catch (Exception e) {
            log.warn("MBG Token section not found");
            return false;
        }
    }

    public boolean isRealWorldAssetsSectionVisible() {
        try {
            // Scroll down to make section visible
            page.evaluate("window.scrollTo(0, document.body.scrollHeight * 0.7)");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            realWorldAssetsSection.waitFor(new Locator.WaitForOptions().setTimeout(shortTimeout));
            return realWorldAssetsSection.isVisible();
        } catch (Exception e) {
            log.warn("Real World Assets section not found");
            return false;
        }
    }

    // =========================
    // Quick Tools
    // =========================

    public boolean isConvertAssetsButtonVisible() {
        return convertAssetsButton.isVisible();
    }

    public boolean isQuickBuyButtonVisible() {
        return quickBuyButton.isVisible();
    }

    public boolean isPanicSellButtonVisible() {
        return panicSellButton.isVisible();
    }
}
