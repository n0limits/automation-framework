package com.automation.base;

import com.automation.config.ConfigValidator;
import com.automation.config.TestConfig;
import com.automation.database.ConnectionPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

@Slf4j
public class BaseTest {
    protected static TestConfig config;

    @BeforeSuite
    public void setupSuite() {
        log.info("===== Test Suite Setup Started =====");
        config = TestConfig.getInstance();

        // Validate configuration at startup - fail fast if misconfigured
        ConfigValidator.validate(config);

        log.info("Environment: {}", config.getEnvironment());
        log.info("Base URL: {}", config.getBaseUrl());
        log.info("Browser: {}", config.getBrowser());
        log.info("Headless: {}", config.isHeadless());
    }

    @AfterSuite
    public void tearDownSuite() {
        // Close all database connection pools
        ConnectionPoolManager.getInstance().closeAllPools();
        log.info("===== Test Suite Teardown Completed =====");
    }

}
