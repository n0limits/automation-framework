package com.automation.factory;

import com.automation.config.TestConfig;
import com.automation.enums.DeviceType;
import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BrowserFactory {
    private static final TestConfig config = TestConfig.getInstance();

    /**
     * Launch browser from string parameter (for TestNG parameter compatibility)
     * @param browserTypeString Browser type as string (chromium, firefox, webkit)
     */
    public static void launchBrowser(String browserTypeString) {
        com.automation.enums.BrowserType browserType = com.automation.enums.BrowserType.fromString(browserTypeString);
        launchBrowser(browserType);
    }

    /**
     * Launch browser using type-safe enum
     * @param browserType BrowserType enum value
     */
    public static void launchBrowser(com.automation.enums.BrowserType browserType) {
        Playwright playwright = PlaywrightManager.getPlaywright();
        Browser browser;

        // Launch browser with window size arguments for maximized window
        com.microsoft.playwright.BrowserType.LaunchOptions options = new com.microsoft.playwright.BrowserType.LaunchOptions()
                .setHeadless(config.isHeadless());

        // Add args to start maximized (works best with headed mode)
        if (!config.isHeadless()) {
            options.setArgs(java.util.List.of("--start-maximized"));
        }

        log.info("Launching {} browser", browserType.getDisplayName());

        browser = switch (browserType) {
            case FIREFOX -> playwright.firefox().launch(options);
            case WEBKIT -> playwright.webkit().launch(options);
            case CHROMIUM -> playwright.chromium().launch(options);
        };

        PlaywrightManager.setBrowser(browser);

        // Configure browser context with viewport and window size
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080);  // Internal page rendering size

        // Set window size to match viewport (for headed mode)
        if (!config.isHeadless()) {
            contextOptions.setViewportSize(null);  // No viewport = use full window
        }

        BrowserContext context = browser.newContext(contextOptions);
        PlaywrightManager.setContext(context);
        Page page = context.newPage();
        PlaywrightManager.setPage(page);

        log.info("Browser launched successfully: {} with maximized window", browserType.getBrowserName());
    }

    /**
     * Launch browser with mobile device emulation
     * @param deviceType Device to emulate
     */
    public static void launchBrowserWithDevice(DeviceType deviceType) {
        Playwright playwright = PlaywrightManager.getPlaywright();
        Browser browser;

        // Launch browser
        com.microsoft.playwright.BrowserType.LaunchOptions options = new com.microsoft.playwright.BrowserType.LaunchOptions()
                .setHeadless(config.isHeadless());

        log.info("Launching browser with device emulation: {}", deviceType.getDeviceName());

        // Use appropriate browser based on device platform
        if (deviceType.isIOS()) {
            browser = playwright.webkit().launch(options);
            log.info("Using WebKit for iOS device emulation");
        } else if (deviceType.isAndroid()) {
            browser = playwright.chromium().launch(options);
            log.info("Using Chromium for Android device emulation");
        } else {
            browser = playwright.chromium().launch(options);
            log.info("Using Chromium for desktop");
        }

        PlaywrightManager.setBrowser(browser);

        // Configure browser context with device emulation
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(deviceType.getViewportWidth(), deviceType.getViewportHeight())
                .setDeviceScaleFactor(deviceType.getDeviceScaleFactor())
                .setIsMobile(deviceType.isMobile())
                .setHasTouch(deviceType.isMobile())
                .setUserAgent(deviceType.getUserAgent());

        BrowserContext context = browser.newContext(contextOptions);
        PlaywrightManager.setContext(context);
        Page page = context.newPage();
        PlaywrightManager.setPage(page);

        log.info("Browser launched successfully with device: {} ({}x{}, scale: {})",
                deviceType.getDeviceName(),
                deviceType.getViewportWidth(),
                deviceType.getViewportHeight(),
                deviceType.getDeviceScaleFactor());
    }

    /**
     * Launch browser with mobile device from string parameter
     * @param deviceString Device name as string
     */
    public static void launchBrowserWithDevice(String deviceString) {
        DeviceType deviceType = DeviceType.fromString(deviceString);
        launchBrowserWithDevice(deviceType);
    }

}
