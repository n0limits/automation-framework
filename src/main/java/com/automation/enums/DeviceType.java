package com.automation.enums;

/**
 * Enum representing supported mobile devices for test execution
 * Provides device emulation profiles for Playwright mobile testing
 */
public enum DeviceType {
    // iOS Devices
    IPHONE_13("iPhone 13", "iPhone", 390, 844, 3.0, true, "Mobile Safari", "iOS 15"),
    IPHONE_13_PRO_MAX("iPhone 13 Pro Max", "iPhone", 428, 926, 3.0, true, "Mobile Safari", "iOS 15"),
    IPHONE_15("iPhone 15", "iPhone", 393, 852, 3.0, true, "Mobile Safari", "iOS 17"),
    IPAD_PRO("iPad Pro 11", "iPad", 834, 1194, 2.0, true, "Mobile Safari", "iOS 15"),

    // Android Devices
    GALAXY_S21("Samsung Galaxy S21", "Android", 360, 800, 3.0, true, "Chrome Mobile", "Android 11"),
    GALAXY_S22("Samsung Galaxy S22", "Android", 360, 780, 3.0, true, "Chrome Mobile", "Android 12"),
    PIXEL_7("Google Pixel 7", "Android", 412, 915, 2.625, true, "Chrome Mobile", "Android 13"),
    PIXEL_7_PRO("Google Pixel 7 Pro", "Android", 412, 892, 3.5, true, "Chrome Mobile", "Android 13"),

    // Desktop (for comparison)
    DESKTOP("Desktop", "Desktop", 1920, 1080, 1.0, false, "Chrome", "Windows 10");

    private final String deviceName;
    private final String platform;
    private final int viewportWidth;
    private final int viewportHeight;
    private final double deviceScaleFactor;
    private final boolean isMobile;
    private final String userAgentBrowser;
    private final String osVersion;

    DeviceType(String deviceName, String platform, int viewportWidth, int viewportHeight,
               double deviceScaleFactor, boolean isMobile, String userAgentBrowser, String osVersion) {
        this.deviceName = deviceName;
        this.platform = platform;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.deviceScaleFactor = deviceScaleFactor;
        this.isMobile = isMobile;
        this.userAgentBrowser = userAgentBrowser;
        this.osVersion = osVersion;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    public double getDeviceScaleFactor() {
        return deviceScaleFactor;
    }

    public boolean isMobile() {
        return isMobile;
    }

    public String getUserAgentBrowser() {
        return userAgentBrowser;
    }

    public String getOsVersion() {
        return osVersion;
    }

    /**
     * Get user agent string for the device
     */
    public String getUserAgent() {
        if (platform.equals("iPhone")) {
            return "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1";
        } else if (platform.equals("iPad")) {
            return "Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1";
        } else if (platform.equals("Android")) {
            return "Mozilla/5.0 (Linux; Android 11; " + deviceName + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36";
        } else {
            return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36";
        }
    }

    /**
     * Parse device type from string (case-insensitive)
     */
    public static DeviceType fromString(String deviceString) {
        if (deviceString == null || deviceString.trim().isEmpty()) {
            return DESKTOP;
        }

        String normalized = deviceString.toLowerCase().trim()
                .replace(" ", "_")
                .replace("-", "_");

        return switch (normalized) {
            case "iphone_13", "iphone13" -> IPHONE_13;
            case "iphone_13_pro_max", "iphone13promax" -> IPHONE_13_PRO_MAX;
            case "iphone_15", "iphone15" -> IPHONE_15;
            case "ipad_pro", "ipadpro" -> IPAD_PRO;
            case "galaxy_s21", "galaxys21", "s21" -> GALAXY_S21;
            case "galaxy_s22", "galaxys22", "s22" -> GALAXY_S22;
            case "pixel_7", "pixel7" -> PIXEL_7;
            case "pixel_7_pro", "pixel7pro" -> PIXEL_7_PRO;
            case "desktop" -> DESKTOP;
            default -> throw new IllegalArgumentException(
                "Unsupported device: " + deviceString +
                ". Supported devices: iphone_13, iphone_13_pro_max, iphone_15, ipad_pro, galaxy_s21, galaxy_s22, pixel_7, pixel_7_pro, desktop"
            );
        };
    }

    /**
     * Check if device is iOS
     */
    public boolean isIOS() {
        return platform.equals("iPhone") || platform.equals("iPad");
    }

    /**
     * Check if device is Android
     */
    public boolean isAndroid() {
        return platform.equals("Android");
    }

    /**
     * Check if device is tablet
     */
    public boolean isTablet() {
        return platform.equals("iPad") || (isAndroid() && viewportWidth >= 600);
    }

    @Override
    public String toString() {
        return deviceName;
    }
}
