package com.automation.bdd.hooks;

import com.automation.api.APIClient;
import com.automation.bdd.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;

/**
 * Cucumber hooks for API scenarios tagged with @api.
 * Manages APIClient lifecycle.
 *
 * @author Victor Grozev
 */
@Slf4j
public class APIHooks {

    private final ScenarioContext scenarioContext;

    public APIHooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(value = "@api", order = 1)
    public void setupAPI(Scenario scenario) {
        APIClient.getRequestSpec();
        scenarioContext.set("apiInitialized", true);
        log.info("API client initialized for scenario: {}", scenario.getName());
    }

    @After(value = "@api", order = 1)
    public void teardownAPI(Scenario scenario) {
        APIClient.cleanup();
        log.info("API client cleaned up for scenario: {}", scenario.getName());
    }
}
