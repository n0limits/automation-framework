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

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(config.isHeadless());

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
        BrowserContext context = browser.newContext();
        PlaywrightManager.setContext(context);
        Page page = context.newPage();
        PlaywrightManager.setPage(page);

        log.info("Browser launched successfully: {}", browserType);
    }

}
