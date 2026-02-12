package com.automation.config;

import lombok.Getter;

/**
 * Thread-safe Singleton configuration holder
 * Loads all configuration from ConfigReader
 *
 * @author Victor Grozev
 */
@Getter
public class TestConfig {
    // ========== Web UI Configuration ==========
    private final String baseUrl = ConfigReader.getProperty("base.url", "https://example.com");
    private final String browser = ConfigReader.getProperty("browser", "chromium");
    private final boolean headless = ConfigReader.getBooleanProperty("headless", false);
    private final int timeout = ConfigReader.getIntProperty("timeout", 30000);

    // ========== Wait Strategy Configuration ==========
    // Element wait timeout - used when waiting for elements to appear/be ready
    private final int elementTimeout = ConfigReader.getIntProperty("wait.element.timeout", 15000);
    // Short timeout - for quick visibility checks
    private final int shortTimeout = ConfigReader.getIntProperty("wait.short.timeout", 5000);
    // Poll interval - interval between polling attempts
    private final int pollInterval = ConfigReader.getIntProperty("wait.poll.interval", 500);
    // Max poll attempts - maximum number of polling iterations
    private final int maxPollAttempts = ConfigReader.getIntProperty("wait.max.poll.attempts", 30);

    // ========== API Configuration ==========
    private final String apiBaseUrl = ConfigReader.getProperty("api.base.url", "https://api.example.com");
    private final int apiTimeout = ConfigReader.getIntProperty("api.timeout", 30000);

    // ========== AWS Configuration ==========
    private final String awsRegion = ConfigReader.getProperty("aws.region", "us-east-1");
    private final String awsStepFunctionsStateMachineArn = ConfigReader.getProperty(
            "aws.stepfunctions.state.machine.arn",
            "arn:aws:states:us-east-1:123456789012:stateMachine:DefaultWorkflow");
    private final int awsStepFunctionsExecutionTimeout = ConfigReader.getIntProperty(
            "aws.stepfunctions.execution.timeout", 300000);
    private final int awsStepFunctionsPollInterval = ConfigReader.getIntProperty(
            "aws.stepfunctions.poll.interval", 5000);
    private final String awsEndpointOverride = ConfigReader.getProperty("aws.endpoint.override");

    // ========== Database Configuration ==========
    private final String mongoConnectionString = ConfigReader.getProperty("mongo.connection.string");
    private final String mongoDatabase = ConfigReader.getProperty("mongo.database", "testdb");
    private final String sqlConnectionString = ConfigReader.getProperty("sql.connection.string");
    private final String sqlUsername = ConfigReader.getProperty("sql.username");
    private final String sqlPassword = ConfigReader.getProperty("sql.password");
    private final String postgresConnectionString = ConfigReader.getProperty("postgres.connection.string");
    private final String postgresUsername = ConfigReader.getProperty("postgres.username");
    private final String postgresPassword = ConfigReader.getProperty("postgres.password");

    // ========== Test Credentials ==========
    // Note: Test credentials should be set via environment variables:
    // TEST_USERNAME and TEST_PASSWORD
    private final String username = ConfigReader.getProperty("test.username");
    private final String password = ConfigReader.getProperty("test.password");

    // ========== Test Data ==========
    private final String testDataPath = ConfigReader.getProperty("test.data.path", "src/test/resources/testdata");

    // ========== Reporting ==========
    private final String reportPath = ConfigReader.getProperty("report.path", "target/reports");

    // ========== Mobile Testing Configuration ==========
    private final boolean mobileEnabled = ConfigReader.getBooleanProperty("mobile.enabled", false);
    private final String mobileDevice = ConfigReader.getProperty("mobile.device", "desktop");
    private final String mobileOrientation = ConfigReader.getProperty("mobile.orientation", "portrait");

    // ========== Cloud Testing Configuration ==========
    private final String cloudProvider = ConfigReader.getProperty("cloud.provider", "local");
    private final String cloudUsername = ConfigReader.getProperty("cloud.username",
            System.getenv("CLOUD_USERNAME"));
    private final String cloudAccessKey = ConfigReader.getProperty("cloud.access.key",
            System.getenv("CLOUD_ACCESS_KEY"));

    // BrowserStack Configuration
    private final boolean browserstackLocal = ConfigReader.getBooleanProperty("browserstack.local", false);
    private final boolean browserstackDebug = ConfigReader.getBooleanProperty("browserstack.debug", true);
    private final String browserstackConsole = ConfigReader.getProperty("browserstack.console", "errors");
    private final boolean browserstackNetworkLogs = ConfigReader.getBooleanProperty("browserstack.network.logs", false);

    // Sauce Labs Configuration
    private final String saucelabsRegion = ConfigReader.getProperty("saucelabs.region", "us-west-1");
    private final boolean saucelabsTunnel = ConfigReader.getBooleanProperty("saucelabs.tunnel", false);

    // ========== Singleton Instance ==========
    private static volatile TestConfig instance;

    private TestConfig() {
    }

    /**
     * Get singleton instance (thread-safe double-checked locking)
     *
     * @return TestConfig instance
     */
    public static TestConfig getInstance() {
        if (instance == null) {
            synchronized (TestConfig.class) {
                if (instance == null) {
                    instance = new TestConfig();
                }
            }
        }
        return instance;
    }

    /**
     * Get current environment name
     *
     * @return Environment name (local, staging, prod)
     */
    public String getEnvironment() {
        return ConfigReader.getCurrentEnvironment();
    }

    /**
     * Check if running in specific environment
     *
     * @param env Environment name
     * @return true if matches current environment
     */
    public boolean isEnvironment(String env) {
        return ConfigReader.isEnvironment(env);
    }
}
