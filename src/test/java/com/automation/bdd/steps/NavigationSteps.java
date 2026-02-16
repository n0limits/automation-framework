package com.automation.bdd.steps;

import com.automation.bdd.context.ScenarioContext;
import com.automation.config.TestConfig;
import com.automation.pages.multibank.NavigationPage;
import com.automation.utils.PlaywrightManager;
import com.automation.utils.SelfHealingLocator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class NavigationSteps {

    private final ScenarioContext scenarioContext;

    public NavigationSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Given("I am on the homepage")
    public void iAmOnTheHomepage() {
        String baseUrl = TestConfig.getInstance().getBaseUrl();
        PlaywrightManager.getPage().navigate(baseUrl);
        PlaywrightManager.getPage().waitForLoadState();
        log.info("Navigated to homepage: {}", baseUrl);
    }

    @When("I navigate to {string}")
    public void iNavigateTo(String url) {
        PlaywrightManager.getPage().navigate(url);
        PlaywrightManager.getPage().waitForLoadState();
        log.info("Navigated to: {}", url);
    }

    @Then("the page title contains {string}")
    public void thePageTitleContains(String expectedText) {
        String title = PlaywrightManager.getPage().title();
        assertThat(title)
                .as("Page title should contain '%s'", expectedText)
                .containsIgnoringCase(expectedText);
    }

    @Then("the page URL contains {string}")
    public void thePageUrlContains(String expectedUrlPart) {
        String url = PlaywrightManager.getPage().url();
        assertThat(url)
                .as("Page URL should contain '%s'", expectedUrlPart)
                .contains(expectedUrlPart);
    }

    @Then("the navigation menu is displayed")
    public void theNavigationMenuIsDisplayed() {
        NavigationPage navigationPage = new NavigationPage();
        assertThat(navigationPage.isNavigationMenuDisplayed())
                .as("Navigation menu should be displayed")
                .isTrue();
    }

    @When("I click the {string} navigation item")
    public void iClickTheNavigationItem(String itemName) {
        NavigationPage navigationPage = new NavigationPage();
        navigationPage.clickNavigationItem(itemName);
        PlaywrightManager.getPage().waitForLoadState();
    }

    @Then("the header is visible")
    public void theHeaderIsVisible() {
        Page page = PlaywrightManager.getPage();
        boolean headerVisible = SelfHealingLocator.create(page, "header")
                .withRole(AriaRole.BANNER, "")
                .withCss("[role='banner']")
                .withCss("nav")
                .find()
                .isVisible();
        assertThat(headerVisible)
                .as("Header should be visible")
                .isTrue();
    }
}
