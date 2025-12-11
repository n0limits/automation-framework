package com.automation.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitUtils {

    public static void waitForPageLoad(Page page) {
        page.waitForLoadState(LoadState.NETWORKIDLE);
        log.info("Page loaded completely");
    }

    public static void waitForElementVisible(Page page, String selector, int timeoutMs) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
        log.info("Element visible: {}", selector);
    }

    public static void waitForElementClickable(Page page, String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE));
        page.waitForFunction(
                "element => !element.disabled",
                page.locator(selector)
        );
        log.info("Element clickable: {}", selector);
    }

    public static void waitForTextToAppear(Page page, String text, int timeoutMs) {
        page.waitForFunction(
                String.format("document.body.innerText.includes('%s')", text),
                new Page.WaitForFunctionOptions().setTimeout(timeoutMs)
        );
        log.info("Text appeared: {}", text);
    }

    public static void waitForAjax(Page page) {
        page.waitForFunction("typeof jQuery !== 'undefined' && jQuery.active === 0");
        log.info("AJAX requests completed");
    }

}
