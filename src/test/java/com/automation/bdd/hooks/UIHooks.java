package com.automation.bdd.hooks;

import com.automation.bdd.context.ScenarioContext;
import com.automation.config.ConfigValidator;
import com.automation.config.TestConfig;
import com.automation.factory.BrowserFactory;
import com.automation.utils.FileUtils;
import com.automation.utils.PlaywrightManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

/**
 * Cucumber hooks for UI scenarios tagged with @ui.
 * Manages Playwright browser lifecycle and tracing.
 *
 * @author Victor Grozev
 */
@Slf4j
public class UIHooks {

    private final ScenarioContext scenarioContext;

    public UIHooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(value = "@ui", order = 1)
    public void validateConfig() {
        TestConfig config = TestConfig.getInstance();
        ConfigValidator.validate(config);
    }

    @Before(value = "@ui", order = 2)
    public void setupBrowser(Scenario scenario) {
        TestConfig config = TestConfig.getInstance();

        PlaywrightManager.initPlaywright();
        BrowserFactory.launchBrowser(config.getBrowser());

        String scenarioName = scenario.getName().replaceAll("\\s+", "_");
        PlaywrightManager.startTracing(scenarioName);

        PlaywrightManager.getPage().navigate(config.getBaseUrl());
        PlaywrightManager.getPage().waitForLoadState();

        scenarioContext.set("page", PlaywrightManager.getPage());
        log.info("Browser setup complete for scenario: {}", scenario.getName());
    }

    @After(value = "@ui", order = 1)
    public void teardownBrowser(Scenario scenario) {
        try {
            if (scenario.isFailed() && PlaywrightManager.getPage() != null) {
                FileUtils.takeScreenshot(PlaywrightManager.getPage(), scenario.getName());

                byte[] screenshot = PlaywrightManager.getPage().screenshot();
                Allure.addAttachment("Screenshot on Failure", "image/png",
                        new ByteArrayInputStream(screenshot), "png");

                Path tracePath = PlaywrightManager.saveTracingOnFailure();
                if (tracePath != null) {
                    log.info("Trace saved for failed scenario: {}", tracePath);
                }
            } else {
                PlaywrightManager.discardTracing();
            }
        } catch (Exception e) {
            log.error("Error during UI teardown", e);
        } finally {
            PlaywrightManager.quitPlaywright();
            log.info("Browser teardown complete for scenario: {}", scenario.getName());
        }
    }
}
