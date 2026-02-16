package com.automation.config;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration Validator
 * Validates required configuration at startup and fails fast if critical values are missing.
 *
 * Usage:
 * <pre>
 * ConfigValidator.validate(config); // Throws if validation fails
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class ConfigValidator {

    /**
     * Validate all critical configuration.
     * Throws ConfigurationException if any required configuration is missing or invalid.
     *
     * @param config TestConfig instance to validate
     * @throws ConfigurationException if validation fails
     */
    public static void validate(TestConfig config) {
        log.info("Validating configuration...");

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // ========== Required Web UI Configuration ==========
        if (isNullOrEmpty(config.getBaseUrl())) {
            errors.add("base.url is required");
        } else if (!config.getBaseUrl().startsWith("http")) {
            errors.add("base.url must be a valid URL starting with http:// or https://");
        }

        if (isNullOrEmpty(config.getBrowser())) {
            errors.add("browser is required");
        } else if (!isValidBrowser(config.getBrowser())) {
            errors.add("browser must be one of: chromium, firefox, webkit");
        }

        if (config.getTimeout() <= 0) {
            errors.add("timeout must be a positive number");
        }

        // ========== Validate Wait Configuration ==========
        if (config.getElementTimeout() <= 0) {
            warnings.add("wait.element.timeout should be positive, using default");
        }

        if (config.getPollInterval() <= 0) {
            warnings.add("wait.poll.interval should be positive, using default");
        }

        // ========== Validate API Configuration ==========
        if (isNullOrEmpty(config.getApiBaseUrl())) {
            warnings.add("api.base.url is not configured - API tests may fail");
        }

        // ========== Validate AWS Configuration (warnings only) ==========
        if (isNullOrEmpty(config.getAwsStepFunctionsStateMachineArn()) ||
            config.getAwsStepFunctionsStateMachineArn().contains("123456789012")) {
            warnings.add("aws.stepfunctions.state.machine.arn appears to be a placeholder - AWS tests may fail");
        }

        // ========== Environment-specific validation ==========
        String env = config.getEnvironment();
        if ("prod".equalsIgnoreCase(env)) {
            log.warn("========================================");
            log.warn("WARNING: Running tests against PRODUCTION");
            log.warn("========================================");

            // Extra validation for production
            if (!config.isHeadless()) {
                warnings.add("Production tests should run in headless mode");
            }
        }

        // ========== Report validation results ==========
        if (!warnings.isEmpty()) {
            log.warn("Configuration warnings:");
            for (String warning : warnings) {
                log.warn("  - {}", warning);
            }
        }

        if (!errors.isEmpty()) {
            log.error("Configuration validation failed with {} error(s):", errors.size());
            for (String error : errors) {
                log.error("  - {}", error);
            }
            throw new ConfigurationException("Configuration validation failed: " + String.join("; ", errors));
        }

        log.info("Configuration validation passed for environment: {}", env);
    }

    /**
     * Validate configuration without throwing exceptions.
     *
     * @param config TestConfig instance
     * @return true if configuration is valid
     */
    public static boolean isValid(TestConfig config) {
        try {
            validate(config);
            return true;
        } catch (ConfigurationException e) {
            return false;
        }
    }

    private static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isValidBrowser(String browser) {
        return "chromium".equalsIgnoreCase(browser) ||
               "firefox".equalsIgnoreCase(browser) ||
               "webkit".equalsIgnoreCase(browser);
    }

    /**
     * Exception thrown when configuration validation fails
     */
    public static class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message) {
            super(message);
        }

        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
