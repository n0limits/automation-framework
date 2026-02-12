package com.automation.bdd.steps;

import com.automation.bdd.context.ScenarioContext;
import com.automation.utils.PlaywrightManager;
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
        int linkCount = PlaywrightManager.getPage().locator("a").count();
        assertThat(linkCount)
                .as("Page should have links")
                .isGreaterThan(0);
    }

    @Then("the page body is not empty")
    public void thePageBodyIsNotEmpty() {
        int bodyCount = PlaywrightManager.getPage().locator("body").count();
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

    @And("I wait {int} milliseconds")
    public void iWaitMilliseconds(int ms) {
        PlaywrightManager.getPage().waitForTimeout(ms);
    }
}
