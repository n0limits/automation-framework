package com.automation.factory;

import com.automation.config.TestConfig;
import com.automation.enums.BrowserType;
import com.automation.enums.CloudProvider;
import com.automation.enums.DeviceType;
import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for launching browsers on cloud platforms (BrowserStack, Sauce Labs)
 * Supports both desktop and mobile device testing on cloud infrastructure
 */
@Slf4j
public class CloudBrowserFactory {
    private static final TestConfig config = TestConfig.getInstance();

    /**
     * Launch browser on cloud provider
     * @param provider Cloud provider (BrowserStack or Sauce Labs)
     * @param browserType Browser type for desktop testing
     */
    public static void launchCloudBrowser(CloudProvider provider, BrowserType browserType) {
        if (provider == CloudProvider.LOCAL) {
            log.warn("Cloud provider is LOCAL, falling back to local browser launch");
            BrowserFactory.launchBrowser(browserType);
            return;
        }

        validateCloudCredentials();

        log.info("Launching {} browser on {}", browserType.getDisplayName(), provider.getDisplayName());

        Playwright playwright = PlaywrightManager.getPlaywright();
        String cdpUrl = buildCdpUrl(provider, browserType, null);

        try {
            Browser browser = playwright.chromium().connectOverCDP(cdpUrl);
            PlaywrightManager.setBrowser(browser);

            BrowserContext context = browser.contexts().get(0);
            PlaywrightManager.setContext(context);

            Page page = context.pages().get(0);
            PlaywrightManager.setPage(page);

            log.info("Successfully connected to {} - {} browser", provider.getDisplayName(), browserType.getDisplayName());
        } catch (Exception e) {
            log.error("Failed to connect to {} cloud browser", provider.getDisplayName(), e);
            throw new RuntimeException("Cloud browser connection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Launch mobile device on cloud provider
     * @param provider Cloud provider (BrowserStack or Sauce Labs)
     * @param deviceType Device type for mobile testing
     */
    public static void launchCloudDevice(CloudProvider provider, DeviceType deviceType) {
        if (provider == CloudProvider.LOCAL) {
            log.warn("Cloud provider is LOCAL, falling back to local device emulation");
            BrowserFactory.launchBrowserWithDevice(deviceType);
            return;
        }

        validateCloudCredentials();

        log.info("Launching {} device on {}", deviceType.getDeviceName(), provider.getDisplayName());

        Playwright playwright = PlaywrightManager.getPlaywright();
        String cdpUrl = buildCdpUrl(provider, null, deviceType);

        try {
            Browser browser = playwright.chromium().connectOverCDP(cdpUrl);
            PlaywrightManager.setBrowser(browser);

            BrowserContext context = browser.contexts().get(0);
            PlaywrightManager.setContext(context);

            Page page = context.pages().get(0);
            PlaywrightManager.setPage(page);

            log.info("Successfully connected to {} - {} device", provider.getDisplayName(), deviceType.getDeviceName());
        } catch (Exception e) {
            log.error("Failed to connect to {} cloud device", provider.getDisplayName(), e);
            throw new RuntimeException("Cloud device connection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Build CDP (Chrome DevTools Protocol) URL for cloud provider
     */
    private static String buildCdpUrl(CloudProvider provider, BrowserType browserType, DeviceType deviceType) {
        String username = config.getCloudUsername();
        String accessKey = config.getCloudAccessKey();

        Map<String, Object> capabilities = buildCapabilities(provider, browserType, deviceType);
        String capsJson = buildCapabilitiesJson(capabilities);

        return switch (provider) {
            case BROWSERSTACK -> String.format(
                "wss://cdp.browserstack.com/playwright?caps=%s",
                urlEncode(capsJson)
            );
            case SAUCELABS -> String.format(
                "wss://ondemand.us-west-1.saucelabs.com:443/playwright?caps=%s",
                urlEncode(capsJson)
            );
            default -> throw new IllegalArgumentException("Unsupported cloud provider: " + provider);
        };
    }

    /**
     * Build capabilities for cloud provider
     */
    private static Map<String, Object> buildCapabilities(CloudProvider provider, BrowserType browserType, DeviceType deviceType) {
        Map<String, Object> caps = new HashMap<>();

        if (provider == CloudProvider.BROWSERSTACK) {
            caps.put("browserstack.username", config.getCloudUsername());
            caps.put("browserstack.accessKey", config.getCloudAccessKey());
            caps.put("project", "Core Automation Framework");
            caps.put("build", "Build " + System.currentTimeMillis());
            caps.put("name", buildTestName(browserType, deviceType));

            if (deviceType != null && deviceType.isMobile()) {
                // Mobile device testing
                if (deviceType.isIOS()) {
                    caps.put("browserName", "playwright-webkit");
                    caps.put("device", mapDeviceForBrowserStack(deviceType));
                    caps.put("realMobile", "true");
                    caps.put("os_version", "15");
                } else if (deviceType.isAndroid()) {
                    caps.put("browserName", "playwright-chromium");
                    caps.put("device", mapDeviceForBrowserStack(deviceType));
                    caps.put("realMobile", "true");
                    caps.put("os_version", "12.0");
                }
            } else {
                // Desktop browser testing
                caps.put("browser", mapBrowserForBrowserStack(browserType));
                caps.put("browser_version", "latest");
                caps.put("os", "Windows");
                caps.put("os_version", "11");
            }

        } else if (provider == CloudProvider.SAUCELABS) {
            Map<String, Object> sauceOptions = new HashMap<>();
            sauceOptions.put("username", config.getCloudUsername());
            sauceOptions.put("accessKey", config.getCloudAccessKey());
            sauceOptions.put("name", buildTestName(browserType, deviceType));
            sauceOptions.put("build", "Build " + System.currentTimeMillis());

            caps.put("sauce:options", sauceOptions);

            if (deviceType != null && deviceType.isMobile()) {
                // Mobile device testing
                if (deviceType.isIOS()) {
                    caps.put("browserName", "Safari");
                    caps.put("platformName", "iOS");
                    caps.put("appium:deviceName", mapDeviceForSauceLabs(deviceType));
                    caps.put("appium:platformVersion", "15.0");
                } else if (deviceType.isAndroid()) {
                    caps.put("browserName", "Chrome");
                    caps.put("platformName", "Android");
                    caps.put("appium:deviceName", mapDeviceForSauceLabs(deviceType));
                    caps.put("appium:platformVersion", "12.0");
                }
            } else {
                // Desktop browser testing
                caps.put("browserName", mapBrowserForSauceLabs(browserType));
                caps.put("browserVersion", "latest");
                caps.put("platformName", "Windows 11");
            }
        }

        return caps;
    }

    /**
     * Build capabilities JSON string
     */
    private static String buildCapabilitiesJson(Map<String, Object> capabilities) {
        StringBuilder json = new StringBuilder("{");
        int index = 0;
        for (Map.Entry<String, Object> entry : capabilities.entrySet()) {
            if (index > 0) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Map) {
                json.append(buildCapabilitiesJson((Map<String, Object>) value));
            } else {
                json.append(value);
            }
            index++;
        }
        json.append("}");
        return json.toString();
    }

    /**
     * URL encode string
     */
    private static String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * Build test name
     */
    private static String buildTestName(BrowserType browserType, DeviceType deviceType) {
        if (deviceType != null && deviceType.isMobile()) {
            return "Mobile Test - " + deviceType.getDeviceName();
        } else {
            return "Desktop Test - " + (browserType != null ? browserType.getDisplayName() : "Chrome");
        }
    }

    /**
     * Map browser type to BrowserStack browser name
     */
    private static String mapBrowserForBrowserStack(BrowserType browserType) {
        if (browserType == null) return "chrome";
        return switch (browserType) {
            case CHROMIUM -> "chrome";
            case FIREFOX -> "firefox";
            case WEBKIT -> "safari";
        };
    }

    /**
     * Map browser type to Sauce Labs browser name
     */
    private static String mapBrowserForSauceLabs(BrowserType browserType) {
        if (browserType == null) return "chrome";
        return switch (browserType) {
            case CHROMIUM -> "chrome";
            case FIREFOX -> "firefox";
            case WEBKIT -> "safari";
        };
    }

    /**
     * Map device type to BrowserStack device name
     */
    private static String mapDeviceForBrowserStack(DeviceType deviceType) {
        return switch (deviceType) {
            case IPHONE_13 -> "iPhone 13";
            case IPHONE_13_PRO_MAX -> "iPhone 13 Pro Max";
            case IPHONE_15 -> "iPhone 15";
            case IPAD_PRO -> "iPad Pro 11 2021";
            case GALAXY_S21 -> "Samsung Galaxy S21";
            case GALAXY_S22 -> "Samsung Galaxy S22";
            case PIXEL_7 -> "Google Pixel 7";
            case PIXEL_7_PRO -> "Google Pixel 7 Pro";
            default -> "iPhone 13";
        };
    }

    /**
     * Map device type to Sauce Labs device name
     */
    private static String mapDeviceForSauceLabs(DeviceType deviceType) {
        return switch (deviceType) {
            case IPHONE_13 -> "iPhone 13 Simulator";
            case IPHONE_13_PRO_MAX -> "iPhone 13 Pro Max Simulator";
            case IPHONE_15 -> "iPhone 15 Simulator";
            case IPAD_PRO -> "iPad Pro (11-inch) Simulator";
            case GALAXY_S21 -> "Samsung Galaxy S21 GoogleAPI Emulator";
            case GALAXY_S22 -> "Samsung Galaxy S22 GoogleAPI Emulator";
            case PIXEL_7 -> "Google Pixel 7 GoogleAPI Emulator";
            case PIXEL_7_PRO -> "Google Pixel 7 Pro GoogleAPI Emulator";
            default -> "iPhone 13 Simulator";
        };
    }

    /**
     * Validate cloud credentials are configured
     */
    private static void validateCloudCredentials() {
        String username = config.getCloudUsername();
        String accessKey = config.getCloudAccessKey();

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException(
                "Cloud username not configured. Set cloud.username property or CLOUD_USERNAME environment variable"
            );
        }

        if (accessKey == null || accessKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "Cloud access key not configured. Set cloud.access.key property or CLOUD_ACCESS_KEY environment variable"
            );
        }
    }
}
