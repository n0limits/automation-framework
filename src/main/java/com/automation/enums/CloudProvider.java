package com.automation.enums;

/**
 * Enum representing supported cloud testing providers
 * Enables remote browser and device testing via cloud platforms
 */
public enum CloudProvider {
    LOCAL("local", null, null),
    BROWSERSTACK("browserstack", "https://hub-cloud.browserstack.com/wd/hub", "BrowserStack"),
    SAUCELABS("saucelabs", "https://ondemand.us-west-1.saucelabs.com/wd/hub", "Sauce Labs");

    private final String providerName;
    private final String hubUrl;
    private final String displayName;

    CloudProvider(String providerName, String hubUrl, String displayName) {
        this.providerName = providerName;
        this.hubUrl = hubUrl;
        this.displayName = displayName;
    }

    /**
     * Get the provider name for configuration
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Get the hub URL for remote execution
     */
    public String getHubUrl() {
        return hubUrl;
    }

    /**
     * Get human-readable display name
     */
    public String getDisplayName() {
        return displayName != null ? displayName : "Local";
    }

    /**
     * Check if this is a cloud provider (not local)
     */
    public boolean isCloud() {
        return this != LOCAL;
    }

    /**
     * Parse cloud provider from string (case-insensitive)
     */
    public static CloudProvider fromString(String providerString) {
        if (providerString == null || providerString.trim().isEmpty()) {
            return LOCAL;
        }

        String normalized = providerString.toLowerCase().trim();
        return switch (normalized) {
            case "browserstack", "bs" -> BROWSERSTACK;
            case "saucelabs", "sauce", "sl" -> SAUCELABS;
            case "local", "none" -> LOCAL;
            default -> throw new IllegalArgumentException(
                "Unsupported cloud provider: " + providerString +
                ". Supported providers: local, browserstack, saucelabs"
            );
        };
    }

    /**
     * Build complete hub URL with credentials
     */
    public String getHubUrlWithCredentials(String username, String accessKey) {
        if (this == LOCAL) {
            return null;
        }

        return switch (this) {
            case BROWSERSTACK -> String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub", username, accessKey);
            case SAUCELABS -> String.format("https://%s:%s@ondemand.us-west-1.saucelabs.com:443/wd/hub", username, accessKey);
            default -> hubUrl;
        };
    }

    @Override
    public String toString() {
        return providerName;
    }
}
