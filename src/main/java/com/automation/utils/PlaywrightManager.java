package com.automation.utils;

import com.microsoft.playwright.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlaywrightManager {
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private static final ThreadLocal<Page> page = new ThreadLocal<>();

    public static void initPlaywright() {
        playwright.set(Playwright.create());
        log.info("Playwright initialized");
    }

    public static Playwright getPlaywright() {
        return playwright.get();
    }

    public static void setBrowser(Browser browserInstance) {
        browser.set(browserInstance);
    }

    public static Browser getBrowser() {
        return browser.get();
    }

    public static void setContext(BrowserContext ctx) {
        context.set(ctx);
    }

    public static BrowserContext getContext() {
        return context.get();
    }

    public static void setPage(Page pg) {
        page.set(pg);
    }

    public static Page getPage() {
        return page.get();
    }

    public static void quitPlaywright() {
        if (page.get() != null) {
            page.get().close();
            page.remove();
        }
        if (context.get() != null) {
            context.get().close();
            context.remove();
        }
        if (browser.get() != null) {
            browser.get().close();
            browser.remove();
        }
        if (playwright.get() != null) {
            playwright.get().close();
            playwright.remove();
        }
        log.info("Playwright quit successfully");
    }
}
