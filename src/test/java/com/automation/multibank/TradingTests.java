package com.automation.multibank;

import com.automation.base.BaseWebTest;
import com.automation.pages.multibank.NavigationPage;
import com.automation.pages.multibank.TradingPage;
import com.automation.providers.TestDataProviders;
import com.automation.utils.TestDataReader;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("MultiBank Trading Platform")
@Feature("Trading Functionality")
public class TradingTests extends BaseWebTest {

    private NavigationPage navigationPage;
    private TradingPage tradingPage;
    private JsonNode testData;

    @BeforeMethod(alwaysRun = true)
    public void setupTest() {
        navigationPage = new NavigationPage();
        tradingPage = new TradingPage();
        testData = TestDataReader.readJsonFile("trading-data.json");
        log.info("Trading test setup completed");
    }

    @Test(description = "Verify spot trading section is displayed", priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Spot Trading Section")
    @Description("Test verifies that the spot trading section is visible on the homepage")
    public void testSpotTradingSectionDisplayed() {
        log.info("Starting test: Spot trading section display verification");

        boolean isDisplayed = tradingPage.isSpotTradingSectionDisplayed();

        assertThat(isDisplayed)
                .as("Spot trading section should be displayed")
                .isTrue();

        log.info("Test completed: Spot trading section is displayed");
    }

    @Test(description = "Verify trading pairs table is displayed", priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Pairs Table")
    @Description("Test verifies that the trading pairs table is visible and accessible")
    public void testTradingPairsTableDisplayed() {
        log.info("Starting test: Trading pairs table display verification");

        boolean isDisplayed = tradingPage.isTradingPairsTableDisplayed();

        assertThat(isDisplayed)
                .as("Trading pairs table should be displayed")
                .isTrue();

        log.info("Test completed: Trading pairs table is displayed");
    }

    @Test(description = "Verify trading pairs table contains expected columns", priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Table Structure")
    @Description("Test verifies that all expected table columns are present")
    public void testTradingPairsTableColumns() {
        log.info("Starting test: Trading pairs table columns verification");

        List<String> expectedColumns = TestDataReader.getStringList(testData, "tradingPairs", "tableColumns");
        List<String> actualColumns = tradingPage.getTableColumns();

        log.info("Expected columns: {}", expectedColumns);
        log.info("Actual columns: {}", actualColumns);

        for (String expectedColumn : expectedColumns) {
            assertThat(actualColumns)
                    .as("Table should contain column: " + expectedColumn)
                    .anyMatch(column -> column.toLowerCase().contains(expectedColumn.toLowerCase()));
        }

        log.info("Test completed: All expected table columns are present");
    }

    @Test(description = "Verify trading pairs are displayed", priority = 4)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Pairs Display")
    @Description("Test verifies that trading pairs are displayed in the table")
    public void testTradingPairsDisplayed() {
        log.info("Starting test: Trading pairs display verification");

        List<String> tradingPairs = tradingPage.getTradingPairs();

        assertThat(tradingPairs)
                .as("Trading pairs should not be empty")
                .isNotEmpty()
                .as("Trading pairs should contain '/' separator")
                .allMatch(pair -> pair.contains("/"));

        log.info("Found {} trading pairs", tradingPairs.size());
        log.info("Test completed: Trading pairs are displayed correctly");
    }

    @Test(description = "Verify specific trading pairs are visible", priority = 5,
          dataProvider = "tradingPairsProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Specific Trading Pairs")
    @Description("Test verifies that specific trading pairs are visible")
    public void testSpecificTradingPairVisible(String pairName) {
        log.info("Testing visibility of trading pair: {}", pairName);

        boolean isVisible = tradingPage.isTradingPairVisible(pairName);

        assertThat(isVisible)
                .as(pairName + " trading pair should be visible")
                .isTrue();

        log.info("{} trading pair is visible", pairName);
    }

    @Test(description = "Verify trading pair data structure", priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Pair Data")
    @Description("Test verifies that trading pair data is properly structured")
    public void testTradingPairDataStructure() {
        log.info("Starting test: Trading pair data structure verification");

        List<String> expectedPairs = TestDataReader.getStringList(testData, "tradingPairs", "expectedPairs");

        for (String pairName : expectedPairs) {
            Map<String, String> pairData = tradingPage.getTradingPairData(pairName);

            assertThat(pairData)
                    .as("Trading pair data should not be empty for: " + pairName)
                    .isNotEmpty();

            log.info("Trading pair {} data: {}", pairName, pairData);
        }

        log.info("Test completed: Trading pair data structure is correct");
    }

    @Test(description = "Verify Favorites tab is visible", priority = 7)
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Categories")
    @Description("Test verifies that Favorites tab is visible")
    public void testFavoritesTabVisible() {
        log.info("Testing Favorites tab visibility");

        boolean isVisible = tradingPage.isFavoritesTabVisible();

        assertThat(isVisible)
                .as("Favorites tab should be visible")
                .isTrue();

        log.info("Favorites tab is visible");
    }

    @Test(description = "Verify All Pairs tab is visible", priority = 8)
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Categories")
    @Description("Test verifies that All Pairs tab is visible")
    public void testAllPairsTabVisible() {
        log.info("Testing All Pairs tab visibility");

        boolean isVisible = tradingPage.isAllPairsTabVisible();

        assertThat(isVisible)
                .as("All Pairs tab should be visible")
                .isTrue();

        log.info("All Pairs tab is visible");
    }

    @Test(description = "Verify Fear Index market indicator is visible", priority = 9)
    @Severity(SeverityLevel.MINOR)
    @Story("Market Indicators")
    @Description("Test verifies that Fear Index indicator is visible")
    public void testFearIndexVisible() {
        log.info("Testing Fear Index visibility");

        boolean fearIndexEnabled = TestDataReader.getBooleanValue(testData, "marketIndicators", "fearIndexVisible");

        if (fearIndexEnabled) {
            boolean isVisible = tradingPage.isFearIndexVisible();

            assertThat(isVisible)
                    .as("Fear Index should be visible")
                    .isTrue();

            log.info("Fear Index is visible");
        } else {
            log.info("Fear Index test skipped - not expected to be visible");
        }
    }

    @Test(description = "Verify Top Gainers section is visible", priority = 10)
    @Severity(SeverityLevel.MINOR)
    @Story("Market Indicators")
    @Description("Test verifies that Top Gainers section is visible")
    public void testTopGainersVisible() {
        log.info("Testing Top Gainers visibility");

        boolean topGainersEnabled = TestDataReader.getBooleanValue(testData, "marketIndicators", "topGainersVisible");

        if (topGainersEnabled) {
            boolean isVisible = tradingPage.areTopGainersVisible();

            assertThat(isVisible)
                    .as("Top Gainers section should be visible")
                    .isTrue();

            log.info("Top Gainers section is visible");
        } else {
            log.info("Top Gainers test skipped - not expected to be visible");
        }
    }

    @Test(description = "Verify Top Losers section is visible", priority = 11)
    @Severity(SeverityLevel.MINOR)
    @Story("Market Indicators")
    @Description("Test verifies that Top Losers section is visible")
    public void testTopLosersVisible() {
        log.info("Testing Top Losers visibility");

        boolean topLosersEnabled = TestDataReader.getBooleanValue(testData, "marketIndicators", "topLosersVisible");

        if (topLosersEnabled) {
            boolean isVisible = tradingPage.areTopLosersVisible();

            assertThat(isVisible)
                    .as("Top Losers section should be visible")
                    .isTrue();

            log.info("Top Losers section is visible");
        } else {
            log.info("Top Losers test skipped - not expected to be visible");
        }
    }

    @Test(description = "Verify MBG Token section is visible", priority = 12)
    @Severity(SeverityLevel.NORMAL)
    @Story("Investment Opportunities")
    @Description("Test verifies that MBG Token promotion section is visible")
    public void testMBGTokenSectionVisible() {
        log.info("Testing MBG Token section visibility");

        boolean isVisible = tradingPage.isMBGTokenSectionVisible();

        assertThat(isVisible)
                .as("MBG Token section should be visible")
                .isTrue();

        log.info("MBG Token section is visible");
    }

    @Test(description = "Verify Real World Assets section is visible", priority = 13)
    @Severity(SeverityLevel.NORMAL)
    @Story("Investment Opportunities")
    @Description("Test verifies that Real World Assets section is visible")
    public void testRealWorldAssetsSectionVisible() {
        log.info("Testing Real World Assets section visibility");

        boolean isVisible = tradingPage.isRealWorldAssetsSectionVisible();

        assertThat(isVisible)
                .as("Real World Assets section should be visible")
                .isTrue();

        log.info("Real World Assets section is visible");
    }

    @Test(description = "Verify Quick Access Tools are visible", priority = 14)
    @Severity(SeverityLevel.NORMAL)
    @Story("Quick Access Tools")
    @Description("Test verifies that quick access tools (Convert, Quick Buy, Panic Sell) are visible")
    public void testQuickAccessToolsVisible() {
        log.info("Testing Quick Access Tools visibility");

        boolean convertVisible = tradingPage.isConvertAssetsButtonVisible();
        boolean quickBuyVisible = tradingPage.isQuickBuyButtonVisible();
        boolean panicSellVisible = tradingPage.isPanicSellButtonVisible();

        assertThat(convertVisible || quickBuyVisible || panicSellVisible)
                .as("At least one quick access tool should be visible")
                .isTrue();

        log.info("Quick Access Tools visibility - Convert: {}, QuickBuy: {}, PanicSell: {}",
                convertVisible, quickBuyVisible, panicSellVisible);
    }

    @Test(description = "Verify trading pairs count is greater than zero", priority = 15)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Pairs Count")
    @Description("Test verifies that there are trading pairs available")
    public void testTradingPairsCount() {
        log.info("Testing trading pairs count");

        int pairsCount = tradingPage.getTradingPairsCount();

        assertThat(pairsCount)
                .as("Trading pairs count should be greater than zero")
                .isGreaterThan(0);

        log.info("Total trading pairs available: {}", pairsCount);
    }
}
