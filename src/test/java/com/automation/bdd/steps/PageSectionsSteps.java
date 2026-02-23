package com.automation.bdd.steps;

import com.automation.bdd.context.ScenarioContext;
import com.automation.pages.multibank.FooterPage;
import com.automation.utils.PlaywrightManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PageSectionsSteps {

    private final ScenarioContext scenarioContext;

    public PageSectionsSteps(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    private FooterPage getFooterPage() {
        return scenarioContext.getOrCreate("footerPage", FooterPage::new);
    }

    @When("I scroll to the bottom of the page")
    public void iScrollToTheBottomOfThePage() {
        getFooterPage().scrollToFooter();
        PlaywrightManager.getPage().waitForLoadState();
        log.info("Scrolled to bottom of page");
    }

    @When("I scroll to the top of the page")
    public void iScrollToTheTopOfThePage() {
        PlaywrightManager.getPage().evaluate("window.scrollTo(0, 0)");
        PlaywrightManager.getPage().waitForLoadState();
        log.info("Scrolled to top of page");
    }

    @Then("the footer is displayed")
    public void theFooterIsDisplayed() {
        assertThat(getFooterPage().isFooterDisplayed())
                .as("Footer should be displayed")
                .isTrue();
    }

    @Then("the App Store link is visible")
    public void theAppStoreLinkIsVisible() {
        assertThat(getFooterPage().isAppStoreLinkVisible())
                .as("App Store link should be visible")
                .isTrue();
    }

    @Then("the Google Play link is visible")
    public void theGooglePlayLinkIsVisible() {
        assertThat(getFooterPage().isGooglePlayLinkVisible())
                .as("Google Play link should be visible")
                .isTrue();
    }

    @Then("the App Store URL contains {string}")
    public void theAppStoreUrlContains(String expectedPart) {
        String url = getFooterPage().getAppStoreUrl();
        assertThat(url)
                .as("App Store URL should contain '%s'", expectedPart)
                .contains(expectedPart);
    }

    @Then("the Google Play URL contains {string}")
    public void theGooglePlayUrlContains(String expectedPart) {
        String url = getFooterPage().getGooglePlayUrl();
        assertThat(url)
                .as("Google Play URL should contain '%s'", expectedPart)
                .contains(expectedPart);
    }
}
