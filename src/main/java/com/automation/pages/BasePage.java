package com.automation.pages;

import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasePage {
    protected Page page;

    public BasePage() {
        this.page = PlaywrightManager.getPage();
    }

    /**
     * Navigate to a specific URL
     * @param url The URL to navigate to
     */
    protected void navigateTo(String url) {
        page.navigate(url);
        log.info("Navigated to: {}", url);
    }

    /**
     * Get the current page title
     * @return Page title
     */
    public String getTitle() {
        return page.title();
    }
}
