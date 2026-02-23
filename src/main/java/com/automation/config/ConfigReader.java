package com.automation.config;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Multi-environment configuration reader
 *
 * Configuration Loading Precedence (highest to lowest):
 * 1. System Properties (-Dkey=value)
 * 2. Environment Variables (KEY=value)
 * 3. Environment-specific properties (application-{env}.properties)
 * 4. Default properties (application.properties)
 * 5. Provided default value
 *
 * Environment Selection:
 * - Set via system property: -Denv=local|staging|prod
 * - Defaults to 'local' if not specified
 *
 * @author Victor Grozev
 */
@Slf4j
public class ConfigReader {
    private static Properties properties;
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    private static final String ENV_PROPERTY = "env";
    private static final String DEFAULT_ENV = "local";
    private static String currentEnvironment;

    static {
        loadProperties();
    }

    /**
     * Load properties with environment-based precedence
     */
    private static void loadProperties() {
        properties = new Properties();

        // Determine current environment
        currentEnvironment = System.getProperty(ENV_PROPERTY, DEFAULT_ENV);
        log.info("Loading configuration for environment: {}", currentEnvironment);

        try {
            // 1. Load default properties (base configuration)
            loadPropertiesFromFile(DEFAULT_CONFIG_FILE, true);

            // 2. Load environment-specific properties (overrides defaults)
            String envConfigFile = "application-" + currentEnvironment + ".properties";
            loadPropertiesFromFile(envConfigFile, false);

            log.info("Configuration loaded successfully for environment: {}", currentEnvironment);
            log.info("Total properties loaded: {}", properties.size());

        } catch (Exception e) {
            log.error("Error loading configuration", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Load properties from specific file
     *
     * @param fileName File name
     * @param required Whether file is required
     */
    private static void loadPropertiesFromFile(String fileName, boolean required) {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream(fileName)) {

            if (input == null) {
                if (required) {
                    log.error("Required configuration file not found: {}", fileName);
                    throw new RuntimeException("Required configuration file not found: " + fileName);
                } else {
                    log.warn("Optional configuration file not found: {} (skipping)", fileName);
                    return;
                }
            }

            properties.load(input);
            log.debug("Loaded configuration from: {}", fileName);

        } catch (IOException e) {
            if (required) {
                log.error("Error loading required configuration file: {}", fileName, e);
                throw new RuntimeException("Failed to load configuration: " + fileName, e);
            } else {
                log.warn("Error loading optional configuration file: {} (skipping)", fileName, e);
            }
        }
    }

    /**
     * Get property with full precedence chain
     *
     * @param key Property key
     * @return Property value or null
     */
    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Get property with full precedence chain and default value
     *
     * Precedence:
     * 1. System property
     * 2. Environment variable (key converted: "base.url" → "BASE_URL")
     * 3. Properties file
     * 4. Default value
     *
     * @param key Property key
     * @param defaultValue Default value if not found
     * @return Property value
     */
    public static String getProperty(String key, String defaultValue) {
        // 1. Check system properties (highest precedence)
        String value = System.getProperty(key);
        if (value != null) {
            log.debug("Property '{}' loaded from system properties: {}", key, maskIfSensitive(key, value));
            return value;
        }

        // 2. Check environment variables (convert key format)
        String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
        value = System.getenv(envKey);
        if (value != null) {
            log.debug("Property '{}' loaded from environment variable {}: {}", key, envKey, maskIfSensitive(key, value));
            return value;
        }

        // 3. Check properties files
        value = properties.getProperty(key);
        if (value != null) {
            log.debug("Property '{}' loaded from properties file: {}", key, maskIfSensitive(key, value));
            return value;
        }

        // 4. Return default value
        log.debug("Property '{}' not found, using default: {}", key, maskIfSensitive(key, defaultValue));
        return defaultValue;
    }

    private static String maskIfSensitive(String key, String value) {
        if (value == null) return null;
        String lowerKey = key.toLowerCase();
        if (lowerKey.contains("password") || lowerKey.contains("secret")
                || lowerKey.contains("token") || lowerKey.contains("access.key")) {
            return "****";
        }
        return value;
    }

    /**
     * Get property as integer
     *
     * @param key Property key
     * @param defaultValue Default value
     * @return Integer value
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Property '{}' value '{}' is not a valid integer, using default: {}",
                    key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Get property as boolean
     *
     * @param key Property key
     * @param defaultValue Default value
     * @return Boolean value
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * Get current environment
     *
     * @return Current environment name
     */
    public static String getCurrentEnvironment() {
        return currentEnvironment;
    }

    /**
     * Check if running in specific environment
     *
     * @param env Environment name
     * @return true if current environment matches
     */
    public static boolean isEnvironment(String env) {
        return currentEnvironment.equalsIgnoreCase(env);
    }
}
