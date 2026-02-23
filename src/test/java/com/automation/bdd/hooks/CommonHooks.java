package com.automation.bdd.hooks;

import com.automation.bdd.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * Common Cucumber hooks for all scenarios.
 * Handles MDC log correlation and Allure metadata.
 *
 * @author Victor Grozev
 */
@Slf4j
public class CommonHooks {

    private static final String MDC_TEST_ID = "testId";

    private final ScenarioContext scenarioContext;

    public CommonHooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = 0)
    public void setupMDC(Scenario scenario) {
        String scenarioId = scenario.getName().replaceAll("\\s+", "_");
        MDC.put(MDC_TEST_ID, scenarioId);
        scenarioContext.set("scenarioName", scenario.getName());
        log.info("========== Scenario Started: {} ==========", scenario.getName());
    }

    @After(order = 0)
    public void cleanupMDC(Scenario scenario) {
        log.info("========== Scenario {}: {} ==========",
                scenario.isFailed() ? "FAILED" : "PASSED", scenario.getName());

        Allure.label("scenario", scenario.getName());
        for (String tag : scenario.getSourceTagNames()) {
            Allure.getLifecycle().updateTestCase(testResult ->
                    testResult.getLabels().add(new Label().setName("tag").setValue(tag)));
        }

        scenarioContext.clear();
        MDC.remove(MDC_TEST_ID);
    }
}
