package com.automation.enums;

/**
 * Enum representing supported browser types for test execution
 * Provides type-safe browser selection and configuration
 */
public enum BrowserType {
    CHROMIUM("chromium", "Google Chromium"),
    FIREFOX("firefox", "Mozilla Firefox"),
    WEBKIT("webkit", "Apple WebKit (Safari)");

    private final String browserName;
    private final String displayName;

    BrowserType(String browserName, String displayName) {
        this.browserName = browserName;
        this.displayName = displayName;
    }

    /**
     * Get the browser name used for Playwright initialization
     * @return Browser name string
     */
    public String getBrowserName() {
        return browserName;
    }

    /**
     * Get human-readable display name
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parse browser type from string (case-insensitive)
     * @param browserString Browser name as string
     * @return Matching BrowserType enum
     * @throws IllegalArgumentException if browser type is not supported
     */
    public static BrowserType fromString(String browserString) {
        if (browserString == null || browserString.trim().isEmpty()) {
            return CHROMIUM; // Default
        }

        String normalized = browserString.toLowerCase().trim();
        return switch (normalized) {
            case "firefox" -> FIREFOX;
            case "webkit", "safari" -> WEBKIT;
            case "chromium", "chrome" -> CHROMIUM;
            default -> throw new IllegalArgumentException(
                "Unsupported browser: " + browserString + ". Supported browsers: chromium, firefox, webkit"
            );
        };
    }

    @Override
    public String toString() {
        return browserName;
    }
}
