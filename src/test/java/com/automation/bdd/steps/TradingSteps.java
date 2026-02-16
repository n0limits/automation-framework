package com.automation.bdd.steps;

import com.automation.bdd.context.ScenarioContext;
import com.automation.pages.multibank.TradingPage;
import com.automation.utils.PlaywrightManager;
import com.automation.utils.SelfHealingLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class TradingSteps {

    private final ScenarioContext scenarioContext;

    public TradingSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    private TradingPage getTradingPage() {
        if (!scenarioContext.contains("tradingPage")) {
            scenarioContext.set("tradingPage", new TradingPage());
        }
        return scenarioContext.get("tradingPage");
    }

    // ===== Given =====

    @Given("the trading page is loaded")
    public void theTradingPageIsLoaded() {
        PlaywrightManager.getPage().waitForLoadState();
        getTradingPage();
        log.info("Trading page loaded");
    }

    // ===== When =====

    @When("I view the spot trading section")
    public void iViewTheSpotTradingSection() {
        boolean displayed = getTradingPage().isSpotTradingSectionDisplayed();
        scenarioContext.set("spotSectionDisplayed", displayed);
    }

    @When("I view the trading pairs table")
    public void iViewTheTradingPairsTable() {
        boolean displayed = getTradingPage().isTradingPairsTableDisplayed();
        scenarioContext.set("tableDisplayed", displayed);
    }

    @When("I get the table columns")
    public void iGetTheTableColumns() {
        List<String> columns = getTradingPage().getTableColumns();
        scenarioContext.set("tableColumns", columns);
    }

    @When("I get the trading pairs list")
    public void iGetTheTradingPairsList() {
        List<String> pairs = getTradingPage().getTradingPairs();
        scenarioContext.set("tradingPairs", pairs);
    }

    @When("I get the trading pairs count")
    public void iGetTheTradingPairsCount() {
        int count = getTradingPage().getTradingPairsCount();
        scenarioContext.set("pairsCount", count);
    }

    @When("I click the {string} tab")
    public void iClickTheTab(String tabName) {
        switch (tabName) {
            case "Favorites" -> getTradingPage().clickFavoritesTab();
            case "All Pairs" -> getTradingPage().clickAllPairsTab();
            default -> throw new IllegalArgumentException("Unknown tab: " + tabName);
        }
        PlaywrightManager.getPage().waitForLoadState();
        log.info("Clicked {} tab", tabName);
    }

    @When("I get the trading pair data for {string}")
    public void iGetTheTradingPairDataFor(String pairName) {
        Map<String, String> data = getTradingPage().getTradingPairData(pairName);
        scenarioContext.set("pairData", data);
    }

    // ===== Then =====

    @Then("the spot trading section is displayed")
    public void theSpotTradingSectionIsDisplayed() {
        Page page = PlaywrightManager.getPage();
        Locator spotSection = SelfHealingLocator.create(page, "table")
                .withText("Spot")
                .withCss("[class*='trading'], [class*='spot']")
                .withCss("section:has(table)")
                .find();
        assertThat(spotSection.isVisible())
                .as("Spot trading section should be displayed")
                .isTrue();
    }

    @Then("the trading pairs table is displayed")
    public void theTradingPairsTableIsDisplayed() {
        Page page = PlaywrightManager.getPage();
        Locator table = SelfHealingLocator.create(page, "table")
                .withCss("[class*='pairs'] table, [class*='trading'] table")
                .withCss("table:has(thead)")
                .find();
        assertThat(table.isVisible())
                .as("Trading pairs table should be displayed")
                .isTrue();
    }

    @Then("the table contains the following columns:")
    public void theTableContainsTheFollowingColumns(List<String> expectedColumns) {
        List<String> actualColumns = getTradingPage().getTableColumns();
        for (String expected : expectedColumns) {
            assertThat(actualColumns)
                    .as("Table should contain column: %s", expected)
                    .anyMatch(col -> col.toLowerCase().contains(expected.toLowerCase()));
        }
    }

    @Then("the trading pairs list is not empty")
    public void theTradingPairsListIsNotEmpty() {
        List<String> pairs = getTradingPage().getTradingPairs();
        assertThat(pairs)
                .as("Trading pairs should not be empty")
                .isNotEmpty();
    }

    @Then("the trading pairs count is greater than {int}")
    public void theTradingPairsCountIsGreaterThan(int minCount) {
        int count = getTradingPage().getTradingPairsCount();
        assertThat(count)
                .as("Trading pairs count should be greater than %d", minCount)
                .isGreaterThan(minCount);
    }

    @Then("the trading pair {string} is visible")
    public void theTradingPairIsVisible(String pairName) {
        assertThat(getTradingPage().isTradingPairVisible(pairName))
                .as("Trading pair %s should be visible", pairName)
                .isTrue();
    }

    @Then("the trading pair data for {string} is not empty")
    public void theTradingPairDataIsNotEmpty(String pairName) {
        Map<String, String> data = getTradingPage().getTradingPairData(pairName);
        assertThat(data)
                .as("Trading pair data for %s should not be empty", pairName)
                .isNotEmpty();
    }

    @Then("the {string} tab is visible")
    public void theTabIsVisible(String tabName) {
        boolean visible = switch (tabName) {
            case "Favorites" -> getTradingPage().isFavoritesTabVisible();
            case "All Pairs" -> getTradingPage().isAllPairsTabVisible();
            default -> throw new IllegalArgumentException("Unknown tab: " + tabName);
        };
        assertThat(visible)
                .as("%s tab should be visible", tabName)
                .isTrue();
    }

    @Then("the {string} market indicator is visible")
    public void theMarketIndicatorIsVisible(String indicatorName) {
        boolean visible = switch (indicatorName) {
            case "Fear Index" -> getTradingPage().isFearIndexVisible();
            case "Top Gainers" -> getTradingPage().areTopGainersVisible();
            case "Top Losers" -> getTradingPage().areTopLosersVisible();
            default -> throw new IllegalArgumentException("Unknown market indicator: " + indicatorName);
        };
        assertThat(visible)
                .as("%s should be visible", indicatorName)
                .isTrue();
    }

    @Then("the {string} investment section is visible")
    public void theInvestmentSectionIsVisible(String sectionName) {
        boolean visible = switch (sectionName) {
            case "MBG Token" -> getTradingPage().isMBGTokenSectionVisible();
            case "Real World Assets" -> getTradingPage().isRealWorldAssetsSectionVisible();
            default -> throw new IllegalArgumentException("Unknown investment section: " + sectionName);
        };
        assertThat(visible)
                .as("%s section should be visible", sectionName)
                .isTrue();
    }

    @Then("at least one quick access tool is visible")
    public void atLeastOneQuickAccessToolIsVisible() {
        TradingPage page = getTradingPage();
        boolean anyVisible = page.isConvertAssetsButtonVisible()
                || page.isQuickBuyButtonVisible()
                || page.isPanicSellButtonVisible();
        assertThat(anyVisible)
                .as("At least one quick access tool should be visible")
                .isTrue();
    }

    @Then("the trading pairs table is still displayed after tab switch")
    public void theTradingPairsTableIsStillDisplayedAfterTabSwitch() {
        assertThat(getTradingPage().isTradingPairsTableDisplayed())
                .as("Trading pairs table should remain displayed after tab switch")
                .isTrue();
    }
}
