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

        // Trading Tabs
        this.spotTab = page.locator("role=tab >> text=Spot");
        this.favoritesTab = page.locator("role=tab >> text=Favorites");
        this.allPairsTab = page.locator("role=tab >> text=All");

        // Trading Table
        this.tradingPairsTable = page.locator("role=grid");
        this.tradingPairRows = tradingPairsTable.locator("role=row >> :not(role=columnheader)");

        // Market Indicators
        this.fearIndex = page.locator("section:has-text('Fear Index')");
        this.topGainers = page.locator("section:has-text('Top Gainers')");
        this.topLosers = page.locator("section:has-text('Top Losers')");

        // Investment Opportunities
        this.mbgTokenSection = page.locator("section:has-text('MBG')");
        this.realWorldAssetsSection = page.locator("section:has-text('Real World Assets')");

        // Quick Access Buttons
        this.convertAssetsButton = page.locator("button:has-text('Convert')");
        this.quickBuyButton = page.locator("button:has-text('Quick Buy')");
        this.panicSellButton = page.locator("button:has-text('Panic Sell')");
    }

    // =========================
    // Trading Section Checks
    // =========================

    public boolean isSpotTradingSectionDisplayed() {
        try {
            tradingPairsTable.waitFor();
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
        int count = tradingPairRows.count();
        log.info("Total trading pairs count: {}", count);
        return count;
    }

    public List<String> getTradingPairs() {
        List<String> pairs = new ArrayList<>();
        try {
            List<Locator> rows = tradingPairRows.all();

            for (Locator row : rows) {
                String pair = row.locator("role=cell").nth(0).textContent().trim();
                if (!pair.isEmpty()) {
                    pairs.add(pair);
                }
            }

            log.info("Found {} trading pairs", pairs.size());
        } catch (Exception e) {
            log.error("Failed to get trading pairs", e);
        }
        return pairs;
    }

    public boolean isTradingPairVisible(String pairName) {
        try {
            List<Locator> rows = tradingPairRows.all();
            for (Locator row : rows) {
                String firstCell = row.locator("role=cell").nth(0).textContent();
                if (firstCell != null && firstCell.contains(pairName)) {
                    return true;
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
            List<Locator> rows = tradingPairRows.all();

            for (Locator row : rows) {
                String firstCell = row.locator("role=cell").nth(0).textContent();

                if (firstCell != null && firstCell.contains(pairName)) {
                    List<Locator> cells = row.locator("role=cell").all();

                    data.put("pair", cells.get(0).textContent());
                    if (cells.size() > 1) data.put("leverage", cells.get(1).textContent());
                    if (cells.size() > 5) data.put("change", cells.get(5).textContent());

                    log.info("Retrieved trading pair data for {}", pairName);
                    break;
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
            Locator headers = tradingPairsTable.locator("role=columnheader");

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
        return mbgTokenSection.isVisible();
    }

    public boolean isRealWorldAssetsSectionVisible() {
        return realWorldAssetsSection.isVisible();
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
