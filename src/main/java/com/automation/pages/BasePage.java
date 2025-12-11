package com.automation.pages;

import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasePage {
    protected Page page;

    public BasePage() {
        this.page = PlaywrightManager.getPage();
    }

    protected void click(String selector) {
        page.click(selector);
        log.info("Clicked on element: {}", selector);
    }

    protected void fill(String selector, String text) {
        page.fill(selector, text);
        log.info("Filled '{}' into element: {}", text, selector);
    }

    protected String getText(String selector) {
        return page.textContent(selector);
    }

    protected void waitForSelector(String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE));
    }

    protected void navigateTo(String url) {
        page.navigate(url);
        log.info("Navigated to: {}", url);
    }

    public String getTitle() {
        return page.title();
    }
}
