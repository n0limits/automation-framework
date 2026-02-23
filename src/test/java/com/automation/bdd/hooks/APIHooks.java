package com.automation.bdd.hooks;

import com.automation.api.APIClient;
import com.automation.bdd.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Cucumber hooks for API scenarios tagged with @api.
 * Manages APIClient lifecycle and failure artifact capture.
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
        log.info("API client initialized for scenario: {}", scenario.getName());
    }

    @After(value = "@api", order = 1)
    public void teardownAPI(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                attachLastResponseOnFailure();
            }
        } catch (Exception e) {
            log.warn("Could not attach API failure artifacts", e);
        } finally {
            APIClient.cleanup();
            log.info("API client cleaned up for scenario: {}", scenario.getName());
        }
    }

    /**
     * Attach the last API response details to the Allure report on failure.
     */
    private void attachLastResponseOnFailure() {
        Response lastResponse = scenarioContext.get("lastResponse");
        if (lastResponse == null) {
            log.debug("No last response available to attach");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Status Code: ").append(lastResponse.getStatusCode()).append("\n");
        details.append("Status Line: ").append(lastResponse.getStatusLine()).append("\n");
        details.append("Response Time: ").append(lastResponse.getTime()).append(" ms\n");
        details.append("\n--- Headers ---\n");
        lastResponse.getHeaders().forEach(h ->
                details.append(h.getName()).append(": ").append(h.getValue()).append("\n"));
        details.append("\n--- Body ---\n");
        details.append(lastResponse.getBody().asPrettyString());

        Allure.addAttachment("API Response on Failure", "text/plain",
                new ByteArrayInputStream(details.toString().getBytes(StandardCharsets.UTF_8)), "txt");
        log.info("Attached API response details for failed scenario");
    }
}
