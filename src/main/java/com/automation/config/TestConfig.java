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
    private final String username = ConfigReader.getProperty("test.username", "testuser");
    private final String password = ConfigReader.getProperty("test.password", "testpassword");

    // ========== Test Data ==========
    private final String testDataPath = ConfigReader.getProperty("test.data.path", "src/test/resources/testdata");

    // ========== Reporting ==========
    private final String reportPath = ConfigReader.getProperty("report.path", "target/reports");

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
