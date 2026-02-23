package com.automation.bdd.steps;

import com.automation.bdd.context.ScenarioContext;
import com.automation.utils.PlaywrightManager;
import com.automation.utils.SelfHealingLocator;
import com.microsoft.playwright.Page;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CommonSteps {

    private final ScenarioContext scenarioContext;

    public CommonSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Then("the page is fully loaded")
    public void thePageIsFullyLoaded() {
        String readyState = (String) PlaywrightManager.getPage().evaluate("document.readyState");
        assertThat(readyState)
                .as("Document should be in complete ready state")
                .isEqualTo("complete");
    }

    @Then("the page has links")
    public void thePageHasLinks() {
        Page page = PlaywrightManager.getPage();
        int linkCount = SelfHealingLocator.create(page, "a")
                .withCss("[href]")
                .find()
                .count();
        assertThat(linkCount)
                .as("Page should have links")
                .isGreaterThan(0);
    }

    @Then("the page body is not empty")
    public void thePageBodyIsNotEmpty() {
        Page page = PlaywrightManager.getPage();
        int bodyCount = SelfHealingLocator.create(page, "body")
                .withCss("html > body")
                .find()
                .count();
        assertThat(bodyCount)
                .as("Body element should be present")
                .isGreaterThan(0);
    }

    @When("I wait for the page to load")
    public void iWaitForThePageToLoad() {
        PlaywrightManager.getPage().waitForLoadState();
    }

    @When("I take a screenshot named {string}")
    public void iTakeAScreenshotNamed(String name) {
        String path = String.format("target/screenshots/%s.png", name);
        PlaywrightManager.getPage().screenshot(
                new com.microsoft.playwright.Page.ScreenshotOptions()
                        .setPath(java.nio.file.Paths.get(path)));
        log.info("Screenshot saved: {}", path);
    }

    @And("I wait for the network to be idle")
    public void iWaitForNetworkIdle() {
        PlaywrightManager.getPage().waitForLoadState(
                com.microsoft.playwright.options.LoadState.LOAD);
    }
}
