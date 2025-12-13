package com.automation.factory;

import com.automation.config.TestConfig;
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

}
