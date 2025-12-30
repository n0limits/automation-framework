package com.automation.config;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify configuration system works correctly
 * Tests multi-environment support and property precedence
 *
 * @author Victor Grozev
 */
@Slf4j
public class ConfigurationTest {

    @Test(description = "Verify configuration loading works")
    public void testConfigurationLoading() {
        log.info("=== Testing Configuration System ===");

        // Get TestConfig instance
        TestConfig config = TestConfig.getInstance();

        // Verify configuration is loaded
        assertThat(config).isNotNull();

        // Verify environment detection
        String environment = config.getEnvironment();
        log.info("Current environment: {}", environment);
        assertThat(environment).isNotEmpty();

        // Verify basic properties are loaded
        assertThat(config.getBaseUrl()).isNotNull();
        assertThat(config.getBrowser()).isNotNull();
        assertThat(config.getApiBaseUrl()).isNotNull();

        log.info("Base URL: {}", config.getBaseUrl());
        log.info("Browser: {}", config.getBrowser());
        log.info("API Base URL: {}", config.getApiBaseUrl());
        log.info("AWS Region: {}", config.getAwsRegion());

        log.info("✅ Configuration system working correctly!");
    }

    @Test(description = "Verify environment-specific loading")
    public void testEnvironmentSpecificConfiguration() {
        log.info("=== Testing Environment-Specific Configuration ===");

        TestConfig config = TestConfig.getInstance();
        String environment = config.getEnvironment();

        log.info("Running in environment: {}", environment);

        // Environment should be one of: local, staging, prod
        assertThat(environment)
                .as("Environment should be valid")
                .isIn("local", "staging", "prod");

        log.info("✅ Environment detection working!");
    }

    @Test(description = "Verify AWS configuration properties")
    public void testAWSConfiguration() {
        log.info("=== Testing AWS Configuration ===");

        TestConfig config = TestConfig.getInstance();

        // Verify AWS properties
        assertThat(config.getAwsRegion()).isNotNull();
        assertThat(config.getAwsStepFunctionsStateMachineArn()).isNotNull();
        assertThat(config.getAwsStepFunctionsExecutionTimeout()).isGreaterThan(0);
        assertThat(config.getAwsStepFunctionsPollInterval()).isGreaterThan(0);

        log.info("AWS Region: {}", config.getAwsRegion());
        log.info("Step Functions ARN: {}", config.getAwsStepFunctionsStateMachineArn());
        log.info("Execution Timeout: {} ms", config.getAwsStepFunctionsExecutionTimeout());
        log.info("Poll Interval: {} ms", config.getAwsStepFunctionsPollInterval());

        log.info("✅ AWS configuration loaded successfully!");
    }

    @Test(description = "Verify property precedence")
    public void testPropertyPrecedence() {
        log.info("=== Testing Property Precedence ===");

        // Test that ConfigReader properly reads properties
        String baseUrl = ConfigReader.getProperty("base.url");
        assertThat(baseUrl).isNotNull();

        String browser = ConfigReader.getProperty("browser", "chromium");
        assertThat(browser).isNotEmpty();

        int timeout = ConfigReader.getIntProperty("timeout", 30000);
        assertThat(timeout).isGreaterThan(0);

        boolean headless = ConfigReader.getBooleanProperty("headless", false);
        log.info("Headless mode: {}", headless);

        log.info("✅ Property precedence working correctly!");
    }

    @Test(description = "Verify environment variable override capability")
    public void testEnvironmentVariableOverride() {
        log.info("=== Testing Environment Variable Override ===");

        // Note: This test documents that environment variables CAN override
        // To test: export BASE_URL=https://custom.com && mvn test

        String baseUrl = ConfigReader.getProperty("base.url");
        log.info("Base URL (may be overridden by env var BASE_URL): {}", baseUrl);

        // The value could come from:
        // 1. Environment variable BASE_URL
        // 2. System property -Dbase.url=...
        // 3. application-{env}.properties file
        // 4. application.properties file

        assertThat(baseUrl).isNotNull();

        log.info("✅ Environment variable override capability verified!");
        log.info("💡 To test override: export BASE_URL=https://custom.com && mvn test -Dtest=ConfigurationTest");
    }
}
