package com.automation.factory;

import com.automation.config.TestConfig;
import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BrowserFactory {
    private static final TestConfig config = TestConfig.getInstance();

    public static void launchBrowser(String browserType) {
        Playwright playwright = PlaywrightManager.getPlaywright();
        Browser browser;

        // Launch browser with window size arguments for maximized window
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(config.isHeadless());

        // Add args to start maximized (works best with headed mode)
        if (!config.isHeadless()) {
            options.setArgs(java.util.List.of("--start-maximized"));
        }

        browser = switch (browserType.toLowerCase()) {
            case "firefox" -> {
                log.info("Launching Firefox browser");
                yield playwright.firefox().launch(options);
            }
            case "webkit" -> {
                log.info("Launching WebKit browser");
                yield playwright.webkit().launch(options);
            }
            default -> {
                log.info("Launching Chromium browser");
                yield playwright.chromium().launch(options);
            }
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

        log.info("Browser launched successfully: {} with maximized window", browserType);
    }

}
