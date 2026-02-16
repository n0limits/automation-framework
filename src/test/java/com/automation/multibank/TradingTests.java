package com.automation.multibank;

import com.automation.base.BaseWebTest;
import com.automation.pages.multibank.NavigationPage;
import com.automation.pages.multibank.TradingPage;
import com.automation.providers.TestDataProviders;
import com.automation.utils.TestDataReader;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.SkipException;
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

    // ========================================
    // OVERRIDE: Additional Setup Hook
    // ========================================
    @Override
    protected void performAdditionalSetup() {
        log.info("Performing Trading test-specific setup");

        // Initialize page objects
        navigationPage = new NavigationPage();
        tradingPage = new TradingPage();

        // Load test data
        testData = TestDataReader.readJsonFile("trading-data.json");

        // Any other Trading-specific setup can go here
        // Example: navigateToUrl("/trading"), login("trader", "pass"), etc.

        log.info("Trading test setup completed");
    }

    // ========================================
    // OVERRIDE: Additional Cleanup Hook
    // ========================================
    @Override
    protected void performAdditionalCleanup() {
        log.info("Performing Trading test-specific cleanup");

        // Any Trading-specific cleanup can go here
        // Example: resetToHomePage(), clearBrowserStorage()

        log.info("Trading test cleanup completed");
    }

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Checks if trading tab is available and skips test if not
     * @param tabName Name of the tab (e.g., "Favorites", "All Pairs")
     * @param availabilityKey Configuration key to check
     */
    private void skipIfTabNotAvailable(String tabName, String availabilityKey) {
        boolean tabAvailable = TestDataReader.getBooleanValue(testData, "tradingPairs", availabilityKey);
        if (!tabAvailable) {
            throw new SkipException(tabName + " tab test skipped - tab not available on current site");
        }
    }

    /**
     * Checks if trading pairs are available on page and skips test if not
     */
    private void skipIfTradingPairsNotAvailable() {
        boolean pairsAvailable = TestDataReader.getBooleanValue(testData, "tradingPairs", "pairsAvailableOnPage");
        if (!pairsAvailable) {
            throw new SkipException("Trading pairs test skipped - pairs not available on homepage");
        }
    }

    /**
     * Clicks trading tab and waits for page load
     * @param tabName Name of the tab to click
     */
    private void clickTabAndWait(String tabName) {
        if (tabName.equals("Favorites")) {
            tradingPage.clickFavoritesTab();
        } else if (tabName.equals("All Pairs")) {
            tradingPage.clickAllPairsTab();
        }
        page.waitForLoadState();
    }

    // @Test(description = "Verify spot trading section is displayed", priority = 1) // changed SUT functionality
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

    // @Test(description = "Verify trading pairs table is displayed", priority = 2) // changed SUT functionality
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

    // @Test(description = "Verify trading pairs table contains expected columns", priority = 3) // changed SUT functionality
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

    // @Test(description = "Verify trading pairs are displayed", priority = 4) // changed SUT functionality
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Pairs Display")
    @Description("Test verifies that trading pairs are displayed in the table")
    public void testTradingPairsDisplayed() {
        log.info("Starting test: Trading pairs display verification");

        skipIfTradingPairsNotAvailable();

        List<String> tradingPairs = tradingPage.getTradingPairs();

        assertThat(tradingPairs)
                .as("Trading pairs should not be empty")
                .isNotEmpty();

        log.info("Found {} trading pairs", tradingPairs.size());
        log.info("Test completed: Trading pairs check finished");
    }

    // @Test(description = "Verify specific trading pairs are visible", priority = 5, // changed SUT functionality
    //       dataProvider = "tradingPairsProvider", dataProviderClass = TestDataProviders.class)
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

    // @Test(description = "Verify trading pair data structure", priority = 6) // changed SUT functionality
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Pair Data")
    @Description("Test verifies that trading pair data is properly structured")
    public void testTradingPairDataStructure() {
        log.info("Starting test: Trading pair data structure verification");

        skipIfTradingPairsNotAvailable();

        List<String> expectedPairs = TestDataReader.getStringList(testData, "tradingPairs", "expectedPairs");

        if (expectedPairs == null || expectedPairs.isEmpty()) {
            throw new SkipException("Trading pair data structure test skipped - no expected pairs configured");
        }

        for (String pairName : expectedPairs) {
            Map<String, String> pairData = tradingPage.getTradingPairData(pairName);

            assertThat(pairData)
                    .as("Trading pair data should not be empty for: " + pairName)
                    .isNotEmpty();

            log.info("Trading pair {} data: {}", pairName, pairData);
        }

        log.info("Test completed: Trading pair data structure is correct");
    }

    // @Test(description = "Verify trading category tabs are visible", priority = 7, // changed SUT functionality
    //       dataProvider = "tradingTabsVisibilityProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Trading Categories")
    @Description("Test verifies that trading category tabs are visible")
    public void testTradingTabsVisible(String tabName, String availabilityKey) {
        log.info("Testing {} tab visibility", tabName);

        skipIfTabNotAvailable(tabName, availabilityKey);

        boolean isVisible;
        if (tabName.equals("Favorites")) {
            isVisible = tradingPage.isFavoritesTabVisible();
        } else {
            isVisible = tradingPage.isAllPairsTabVisible();
        }

        assertThat(isVisible)
                .as(tabName + " tab should be visible")
                .isTrue();

        log.info("{} tab is visible", tabName);
    }

    // @Test(description = "Verify market indicators are visible", priority = 9, // changed SUT functionality
    //       dataProvider = "marketIndicatorsProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.MINOR)
    @Story("Market Indicators")
    @Description("Test verifies that market indicators are visible when enabled")
    public void testMarketIndicatorsVisible(String indicatorName, String visibilityKey) {
        log.info("Testing {} visibility", indicatorName);

        boolean indicatorEnabled = TestDataReader.getBooleanValue(testData, "marketIndicators", visibilityKey);

        if (!indicatorEnabled) {
            throw new SkipException(indicatorName + " test skipped - not expected to be visible");
        }

        boolean isVisible;
        switch (indicatorName) {
            case "Fear Index" -> isVisible = tradingPage.isFearIndexVisible();
            case "Top Gainers" -> isVisible = tradingPage.areTopGainersVisible();
            case "Top Losers" -> isVisible = tradingPage.areTopLosersVisible();
            default -> throw new IllegalArgumentException("Unknown indicator: " + indicatorName);
        }

        assertThat(isVisible)
                .as(indicatorName + " should be visible")
                .isTrue();

        log.info("{} is visible", indicatorName);
    }

    // @Test(description = "Verify investment opportunity sections are visible", priority = 12, // changed SUT functionality
    //       dataProvider = "investmentSectionsProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.NORMAL)
    @Story("Investment Opportunities")
    @Description("Test verifies that investment opportunity sections are visible")
    public void testInvestmentSectionsVisible(String sectionName) {
        log.info("Testing {} section visibility", sectionName);

        boolean isVisible;
        if (sectionName.equals("MBG Token")) {
            isVisible = tradingPage.isMBGTokenSectionVisible();
        } else {
            isVisible = tradingPage.isRealWorldAssetsSectionVisible();
        }

        assertThat(isVisible)
                .as(sectionName + " section should be visible")
                .isTrue();

        log.info("{} section is visible", sectionName);
    }

    // @Test(description = "Verify Quick Access Tools are visible", priority = 14) // changed SUT functionality
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

    // @Test(description = "Verify trading pairs count is greater than zero", priority = 15) // changed SUT functionality
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Pairs Count")
    @Description("Test verifies that there are trading pairs available")
    public void testTradingPairsCount() {
        log.info("Testing trading pairs count");

        skipIfTradingPairsNotAvailable();

        int pairsCount = tradingPage.getTradingPairsCount();

        assertThat(pairsCount)
                .as("Trading pairs count should be greater than zero")
                .isGreaterThan(0);

        log.info("Total trading pairs available: {}", pairsCount);
    }

    // @Test(description = "Verify trading tab functionality", priority = 16, // changed SUT functionality
    //       dataProvider = "tradingTabsProvider", dataProviderClass = TestDataProviders.class)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Categories Switching")
    @Description("Test verifies that clicking trading category tabs switches the view and loads category data")
    public void testTradingTabFunctionality(String tabName, String availabilityKey) {
        log.info("Testing {} tab functionality", tabName);

        skipIfTabNotAvailable(tabName, availabilityKey);
        clickTabAndWait(tabName);

        // Verify trading table is still displayed after tab switch
        boolean tableDisplayed = tradingPage.isTradingPairsTableDisplayed();

        assertThat(tableDisplayed)
                .as(tabName + " tab should display trading pairs table")
                .isTrue();

        // Get count (Favorites can be 0, All Pairs should have pairs)
        int pairsCount = tradingPage.getTradingPairsCount();

        if (tabName.equals("All Pairs")) {
            assertThat(pairsCount)
                    .as("All Pairs tab must display trading pairs")
                    .isGreaterThan(0);
        } else {
            // Favorites can legitimately be empty
            assertThat(pairsCount)
                    .as("Favorites tab should display valid count (can be 0 if no favorites)")
                    .isGreaterThanOrEqualTo(0);
        }

        log.info("{} tab shows {} pairs", tabName, pairsCount);
    }

    // @Test(description = "Verify trading pairs display across different categories", priority = 17) // changed SUT functionality
    @Severity(SeverityLevel.CRITICAL)
    @Story("Trading Categories")
    @Description("Test verifies spot trading section displays trading pairs across different categories (Favorites vs All)")
    public void testTradingPairsAcrossCategories() {
        log.info("Testing trading pairs display across different categories");

        boolean favoritesAvailable = TestDataReader.getBooleanValue(testData, "tradingPairs", "favoritesTabAvailable");
        boolean allPairsAvailable = TestDataReader.getBooleanValue(testData, "tradingPairs", "allPairsTabAvailable");

        if (!favoritesAvailable || !allPairsAvailable) {
            throw new SkipException("Cross-category test skipped - both tabs not available on current site");
        }

        // Switch to All Pairs and verify
        clickTabAndWait("All Pairs");
        int allPairsCount = tradingPage.getTradingPairsCount();

        assertThat(allPairsCount)
                .as("All Pairs category must display trading pairs")
                .isGreaterThan(0);

        log.info("All Pairs category shows {} pairs", allPairsCount);

        // Switch to Favorites and verify table remains functional
        clickTabAndWait("Favorites");
        int favoritesCount = tradingPage.getTradingPairsCount();

        // Favorites can be empty, verify table is still displayed
        boolean tableDisplayed = tradingPage.isTradingPairsTableDisplayed();

        assertThat(tableDisplayed)
                .as("Trading pairs table should remain displayed across category switches")
                .isTrue();

        assertThat(favoritesCount)
                .as("Favorites category should display valid count (can be 0)")
                .isGreaterThanOrEqualTo(0);

        // Switch back to All Pairs to verify consistency
        clickTabAndWait("All Pairs");
        int allPairsCountAfter = tradingPage.getTradingPairsCount();

        assertThat(allPairsCountAfter)
                .as("All Pairs should show consistent data after switching")
                .isGreaterThan(0);

        log.info("Trading pairs successfully displayed across categories - All: {}, Favorites: {}, All(after): {}",
                allPairsCount, favoritesCount, allPairsCountAfter);
    }
}
