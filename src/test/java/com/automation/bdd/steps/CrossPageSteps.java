package com.automation.bdd.steps;

import com.automation.bdd.context.ScenarioContext;
import com.automation.pages.multibank.AboutUsPage;
import com.automation.utils.PlaywrightManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CrossPageSteps {

    private final ScenarioContext scenarioContext;

    public CrossPageSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("I store the current URL")
    public void iStoreTheCurrentUrl() {
        String url = PlaywrightManager.getPage().url();
        scenarioContext.set("storedUrl", url);
        log.info("Stored current URL: {}", url);
    }

    @Then("the current URL matches the stored URL")
    public void theCurrentUrlMatchesTheStoredUrl() {
        String storedUrl = scenarioContext.get("storedUrl");
        String currentUrl = PlaywrightManager.getPage().url();
        assertThat(currentUrl)
                .as("Current URL should match stored URL")
                .isEqualTo(storedUrl);
    }

    @When("I go back in the browser")
    public void iGoBackInTheBrowser() {
        PlaywrightManager.getPage().goBack();
        PlaywrightManager.getPage().waitForLoadState();
        log.info("Navigated back in browser");
    }

    @When("I navigate to the About Us page")
    public void iNavigateToTheAboutUsPage() {
        AboutUsPage aboutUsPage = new AboutUsPage();
        aboutUsPage.navigateToAboutUs();
        log.info("Navigated to About Us page");
    }
}
