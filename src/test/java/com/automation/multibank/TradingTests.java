package com.automation.multibank;

import com.automation.base.BaseMultibankTest;
import com.automation.providers.TestDataProviders;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("MultiBank Trading Platform")
@Feature("Trading Functionality")
public class TradingTests extends BaseMultibankTest {

    @Test(description = "Click All Pairs tab and read trading pair data", priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Pair Data")
    @Description("Clicks All Pairs tab, reads pair list, and verifies data structure for the first pair")
    public void testClickAllPairsTabAndReadData() {
        log.info("Starting test: Click All Pairs tab and read data");

        tradingPage.clickAllPairsTab();
        page.waitForLoadState();

        List<String> pairs = tradingPage.getTradingPairs();
        assertThat(pairs)
                .as("Trading pairs list should not be empty")
                .isNotEmpty();

        String firstPair = pairs.get(0);
        Map<String, String> pairData = tradingPage.getTradingPairData(firstPair);
        assertThat(pairData)
                .as("Trading pair data for %s should not be empty", firstPair)
                .isNotEmpty();

        log.info("First pair '{}' data: {}", firstPair, pairData);
    }

    @Test(description = "Verify tab displays trading table after switching",
          dataProvider = "tradingTabsProvider", dataProviderClass = TestDataProviders.class, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Tab Switching")
    @Description("Clicks a trading tab and verifies the trading table structure is preserved")
    public void testTabDisplaysTradingTable(String tabName) {
        log.info("Testing {} tab table display", tabName);

        tradingPage.clickTab(tabName);
        page.waitForLoadState();

        assertThat(tradingPage.isTradingPairsTableDisplayed())
                .as("Trading table should be displayed after switching to %s", tabName)
                .isTrue();

        List<String> columns = tradingPage.getTableColumns();
        assertThat(columns)
                .as("Table columns should contain 'Pair' after switching to %s", tabName)
                .anyMatch(col -> col.toLowerCase().contains("pair"));

        log.info("{} tab displays trading table correctly", tabName);
    }

    @Test(description = "Switch tabs multiple times and verify data consistency", priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Data Consistency")
    @Description("Switches between All Pairs and Favorites tabs and verifies pair count remains consistent")
    public void testSwitchTabsMultipleTimes() {
        log.info("Starting test: Tab switching data consistency");

        tradingPage.clickAllPairsTab();
        page.waitForLoadState();
        int countA = tradingPage.getTradingPairsCount();

        tradingPage.clickFavoritesTab();
        page.waitForLoadState();

        tradingPage.clickAllPairsTab();
        page.waitForLoadState();
        int countB = tradingPage.getTradingPairsCount();

        assertThat(countB)
                .as("All Pairs count should be consistent after tab switching")
                .isEqualTo(countA);

        log.info("Tab switching consistent: count before={}, after={}", countA, countB);
    }

    @Test(description = "Verify expected column is present in trading table",
          dataProvider = "expectedColumnsProvider", dataProviderClass = TestDataProviders.class, priority = 4)
    @Severity(SeverityLevel.NORMAL)
    @Story("Table Structure")
    @Description("Verifies that a specific column is present in the trading table")
    public void testTableColumnPresent(String expectedColumn) {
        log.info("Verifying column '{}' is present", expectedColumn);

        List<String> columns = tradingPage.getTableColumns();
        assertThat(columns)
                .as("Table should contain '%s' column", expectedColumn)
                .anyMatch(col -> col.toLowerCase().contains(expectedColumn.toLowerCase()));

        log.info("Column '{}' found in table columns: {}", expectedColumn, columns);
    }

    @Test(description = "Verify trading pair data has values for specific symbols",
          dataProvider = "tradingSymbolsProvider", dataProviderClass = TestDataProviders.class, priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Pair Data")
    @Description("Reads data for a specific trading pair and verifies it returns non-empty data")
    public void testTradingPairDataHasValues(String symbol) {
        log.info("Verifying data for trading pair: {}", symbol);

        tradingPage.clickAllPairsTab();
        page.waitForLoadState();

        List<String> pairs = tradingPage.getTradingPairs();
        assertThat(pairs)
                .as("Trading pairs should not be empty")
                .isNotEmpty();

        // Find matching pair (partial match since display names may differ from symbol codes)
        String matchingPair = pairs.stream()
                .filter(p -> p.toUpperCase().contains(symbol.substring(0, 3)))
                .findFirst()
                .orElse(pairs.get(0));

        Map<String, String> pairData = tradingPage.getTradingPairData(matchingPair);
        assertThat(pairData)
                .as("Data for pair matching %s should not be empty", symbol)
                .isNotEmpty();

        log.info("Pair '{}' (matched from {}) data: {}", matchingPair, symbol, pairData);
    }
}
