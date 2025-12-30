package com.automation.enums;

/**
 * Enum representing network conditions for testing
 * Simulates various network speeds for mobile and slow connection testing
 */
public enum NetworkCondition {
    // Fast connections
    WIFI("WiFi", 30000, 15000, 20),
    FAST_4G("Fast 4G", 4000, 3000, 20),

    // Mobile connections
    REGULAR_4G("Regular 4G", 2000, 1750, 30),
    REGULAR_3G("Regular 3G", 750, 250, 100),
    SLOW_3G("Slow 3G", 400, 400, 400),
    SLOW_2G("Slow 2G", 250, 50, 2000),

    // Offline
    OFFLINE("Offline", 0, 0, 0),

    // No throttling (default)
    NONE("No Throttling", -1, -1, -1);

    private final String displayName;
    private final int downloadThroughput; // KB/s
    private final int uploadThroughput;   // KB/s
    private final int latency;            // ms

    NetworkCondition(String displayName, int downloadThroughput, int uploadThroughput, int latency) {
        this.displayName = displayName;
        this.downloadThroughput = downloadThroughput;
        this.uploadThroughput = uploadThroughput;
        this.latency = latency;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDownloadThroughput() {
        return downloadThroughput;
    }

    public int getUploadThroughput() {
        return uploadThroughput;
    }

    public int getLatency() {
        return latency;
    }

    /**
     * Parse network condition from string (case-insensitive)
     */
    public static NetworkCondition fromString(String conditionString) {
        if (conditionString == null || conditionString.trim().isEmpty()) {
            return NONE;
        }

        String normalized = conditionString.toLowerCase().trim().replace(" ", "_");
        return switch (normalized) {
            case "wifi" -> WIFI;
            case "fast_4g", "fast4g", "4g_fast" -> FAST_4G;
            case "regular_4g", "4g", "regular4g" -> REGULAR_4G;
            case "regular_3g", "3g", "regular3g" -> REGULAR_3G;
            case "slow_3g", "slow3g", "3g_slow" -> SLOW_3G;
            case "slow_2g", "slow2g", "2g", "2g_slow" -> SLOW_2G;
            case "offline" -> OFFLINE;
            case "none", "no_throttling" -> NONE;
            default -> throw new IllegalArgumentException(
                "Unsupported network condition: " + conditionString +
                ". Supported: wifi, fast_4g, regular_4g, regular_3g, slow_3g, slow_2g, offline, none"
            );
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
}
