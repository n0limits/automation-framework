package com.automation.pages.multibank;

import com.automation.pages.BasePage;
import com.microsoft.playwright.Locator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TradingPage extends BasePage {

    // Trading Section Selectors
    // MultiBank doesn't have a specific spot trading section - the whole page is for trading
    private final String spotTradingSection = "table";  // Use table as proxy for trading section
    private final String favoritesTab = "button:has-text('Favorites'), [role='tab']:has-text('Favorites')";
    private final String allPairsTab = "button:has-text('All'), [role='tab']:has-text('All')";
    private final String tradingPairsTable = "table, [role='table'], [class*='pairs-table']";
    // MultiBank table rows - use tbody tr since table exists
    private final String tradingPairRow = "table tbody tr";
    private final String pairColumn = "table tbody tr td:nth-child(1)";
    private final String leverageColumn = "td:nth-child(2)";  // Max Leverage is 2nd column
    private final String changeColumn = "td:nth-child(6)";  // Change 24h is 6th column

    // Market Indicators
    private final String fearIndex = "[class*='fear'], :has-text('Fear Index')";
    private final String topGainers = ":has-text('Top Gainers'), [class*='gainers']";
    private final String topLosers = ":has-text('Top Losers'), [class*='losers']";

    // Investment Opportunities
    private final String mbgTokenSection = ":has-text('MBG'), :has-text('pay trading fees')";
    private final String realWorldAssetsSection = ":has-text('Real World Assets'), :has-text('29B')";

    // Quick Access Tools
    private final String convertAssetsButton = "button:has-text('Convert'), :has-text('Convert Your Assets')";
    private final String quickBuyButton = "button:has-text('Quick Buy'), :has-text('Buy Crypto')";
    private final String panicSellButton = "button:has-text('Panic Sell')";

    public TradingPage() {
        super();
    }

    public boolean isSpotTradingSectionDisplayed() {
        try {
            waitForSelector(spotTradingSection);
            log.debug("Spot trading section is displayed");
            return true;
        } catch (Exception e) {
            log.error("Spot trading section not displayed", e);
            return false;
        }
    }

    public boolean isFavoritesTabVisible() {
        try {
            waitForSelector(favoritesTab);
            log.debug("Favorites tab is visible");
            return true;
        } catch (Exception e) {
            log.debug("Favorites tab not found");
            return false;
        }
    }

    public boolean isAllPairsTabVisible() {
        try {
            waitForSelector(allPairsTab);
            log.debug("All Pairs tab is visible");
            return true;
        } catch (Exception e) {
            log.debug("All Pairs tab not found");
            return false;
        }
    }

    public void clickFavoritesTab() {
        click(favoritesTab);
        log.info("Clicked Favorites tab");
    }

    public void clickAllPairsTab() {
        click(allPairsTab);
        log.info("Clicked All Pairs tab");
    }

    public boolean isTradingPairsTableDisplayed() {
        try {
            waitForSelector(tradingPairsTable);
            log.debug("Trading pairs table is displayed");
            return true;
        } catch (Exception e) {
            log.error("Trading pairs table not displayed", e);
            return false;
        }
    }

    public List<String> getTradingPairs() {
        try {
            waitForSelector(tradingPairRow);
            List<Locator> pairLocators = page.locator(pairColumn).all();
            List<String> pairs = new ArrayList<>();

            for (Locator locator : pairLocators) {
                String pairText = locator.textContent();
                if (pairText != null && !pairText.trim().isEmpty()) {
                    pairs.add(pairText.trim());
                }
            }

            log.info("Found {} trading pairs", pairs.size());
            return pairs;
        } catch (Exception e) {
            log.error("Failed to get trading pairs", e);
            return new ArrayList<>();
        }
    }

    public boolean isTradingPairVisible(String pairName) {
        try {
            // Search for trading pair in the first column of the table
            waitForSelector("table tbody tr");
            List<Locator> pairCells = page.locator(pairColumn).all();

            for (Locator cell : pairCells) {
                String cellText = cell.textContent();
                if (cellText != null && cellText.contains(pairName)) {
                    log.debug("Trading pair '{}' is visible", pairName);
                    return true;
                }
            }

            log.warn("Trading pair '{}' not visible", pairName);
            return false;
        } catch (Exception e) {
            log.warn("Trading pair '{}' not visible - error: {}", pairName, e.getMessage());
            return false;
        }
    }

    public Map<String, String> getTradingPairData(String pairName) {
        Map<String, String> pairData = new HashMap<>();

        try {
            waitForSelector("table tbody tr");
            List<Locator> rows = page.locator("table tbody tr").all();

            for (Locator row : rows) {
                String firstCellText = row.locator("td").first().textContent();
                if (firstCellText != null && firstCellText.contains(pairName)) {
                    List<Locator> cells = row.locator("td").all();
                    if (!cells.isEmpty()) {
                        pairData.put("pair", cells.get(0).textContent());
                        if (cells.size() > 1) pairData.put("leverage", cells.get(1).textContent());
                        if (cells.size() > 5) pairData.put("change", cells.get(5).textContent());
                    }
                    log.info("Retrieved data for trading pair: {}", pairName);
                    break;
                }
            }

            if (pairData.isEmpty()) {
                log.warn("No data found for trading pair: {}", pairName);
            }
        } catch (Exception e) {
            log.error("Failed to get trading pair data for: {}", pairName, e);
        }

        return pairData;
    }

    public List<String> getTableColumns() {
        List<String> columns = new ArrayList<>();
        try {
            waitForSelector("th, [role='columnheader']");
            List<Locator> headerLocators = page.locator("th, [role='columnheader']").all();

            for (Locator locator : headerLocators) {
                String columnName = locator.textContent();
                if (columnName != null && !columnName.trim().isEmpty()) {
                    columns.add(columnName.trim());
                }
            }

            log.info("Found {} table columns: {}", columns.size(), columns);
        } catch (Exception e) {
            log.error("Failed to get table columns", e);
        }

        return columns;
    }

    public boolean isFearIndexVisible() {
        try {
            waitForSelector(fearIndex);
            log.debug("Fear Index is visible");
            return true;
        } catch (Exception e) {
            log.debug("Fear Index not found");
            return false;
        }
    }

    public boolean areTopGainersVisible() {
        try {
            waitForSelector(topGainers);
            log.debug("Top Gainers section is visible");
            return true;
        } catch (Exception e) {
            log.debug("Top Gainers not found");
            return false;
        }
    }

    public boolean areTopLosersVisible() {
        try {
            waitForSelector(topLosers);
            log.debug("Top Losers section is visible");
            return true;
        } catch (Exception e) {
            log.debug("Top Losers not found");
            return false;
        }
    }

    public boolean isMBGTokenSectionVisible() {
        try {
            waitForSelector(mbgTokenSection);
            log.debug("MBG Token section is visible");
            return true;
        } catch (Exception e) {
            log.debug("MBG Token section not found");
            return false;
        }
    }

    public boolean isRealWorldAssetsSectionVisible() {
        try {
            waitForSelector(realWorldAssetsSection);
            log.debug("Real World Assets section is visible");
            return true;
        } catch (Exception e) {
            log.debug("Real World Assets section not found");
            return false;
        }
    }

    public boolean isConvertAssetsButtonVisible() {
        try {
            waitForSelector(convertAssetsButton);
            log.debug("Convert Assets button is visible");
            return true;
        } catch (Exception e) {
            log.debug("Convert Assets button not found");
            return false;
        }
    }

    public boolean isQuickBuyButtonVisible() {
        try {
            waitForSelector(quickBuyButton);
            log.debug("Quick Buy button is visible");
            return true;
        } catch (Exception e) {
            log.debug("Quick Buy button not found");
            return false;
        }
    }

    public boolean isPanicSellButtonVisible() {
        try {
            waitForSelector(panicSellButton);
            log.debug("Panic Sell button is visible");
            return true;
        } catch (Exception e) {
            log.debug("Panic Sell button not found");
            return false;
        }
    }

    public int getTradingPairsCount() {
        waitForSelector(tradingPairRow);
        int count = page.locator(tradingPairRow).count();
        log.info("Total trading pairs count: {}", count);
        return count;
    }
}
