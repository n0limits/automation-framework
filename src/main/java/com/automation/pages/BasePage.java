package com.automation.pages;

import com.automation.config.TestConfig;
import com.automation.utils.PlaywrightManager;
import com.automation.utils.SelfHealingLocator;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;

/**
 * Enhanced Base Page Object with comprehensive UI utilities
 * Provides foundation for all page objects with common operations
 *
 * Features:
 * - Smart waiting strategies
 * - Screenshot capture
 * - Element interaction helpers
 * - Visibility checks
 * - Navigation utilities
 * - Error handling
 *
 * @author Victor Grozev
 */
@Slf4j
@Getter
public abstract class BasePage {
    protected final Page page;
    protected final TestConfig config;
    protected final int defaultTimeout;

    public BasePage() {
        this.page = PlaywrightManager.getPage();
        this.config = TestConfig.getInstance();
        this.defaultTimeout = config.getTimeout();
    }

    /**
     * Create a self-healing locator with fallback strategies.
     * Use the builder API to add fallbacks: text, role, CSS, testId, etc.
     *
     * @param primarySelector Primary CSS or Playwright selector
     * @return SelfHealingLocator builder
     */
    protected SelfHealingLocator selfHeal(String primarySelector) {
        return SelfHealingLocator.create(page, primarySelector);
    }

    /**
     * Navigate to a specific URL
     *
     * @param url URL to navigate to
     */
    @Step("Navigate to: {url}")
    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        page.navigate(url);
        waitForPageLoad();
    }

    /**
     * Wait for page to be fully loaded
     */
    @Step("Wait for page to load")
    protected void waitForPageLoad() {
        page.waitForLoadState(LoadState.NETWORKIDLE);
        log.debug("Page loaded: {}", page.url());
    }

    /**
     * Wait for element to be visible
     *
     * @param selector Element selector
     */
    @Step("Wait for element to be visible: {selector}")
    protected Locator waitForVisible(String selector) {
        log.debug("Waiting for element to be visible: {}", selector);
        Locator locator = page.locator(selector);
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(defaultTimeout));
        return locator;
    }

    /**
     * Wait for element to be hidden
     *
     * @param selector Element selector
     */
    @Step("Wait for element to be hidden: {selector}")
    protected void waitForHidden(String selector) {
        log.debug("Waiting for element to be hidden: {}", selector);
        page.locator(selector).waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(defaultTimeout));
    }

    /**
     * Check if element is visible
     *
     * @param selector Element selector
     * @return true if visible
     */
    public boolean isVisible(String selector) {
        try {
            return page.locator(selector).isVisible();
        } catch (Exception e) {
            log.debug("Element not visible: {}", selector);
            return false;
        }
    }

    /**
     * Check if element exists in DOM
     *
     * @param selector Element selector
     * @return true if exists
     */
    public boolean exists(String selector) {
        return page.locator(selector).count() > 0;
    }

    /**
     * Click element with wait
     *
     * @param selector Element selector
     */
    @Step("Click: {selector}")
    protected void click(String selector) {
        log.info("Clicking: {}", selector);
        waitForVisible(selector);
        page.locator(selector).click();
    }

    /**
     * Click element by text
     *
     * @param text Text to click
     */
    @Step("Click element with text: {text}")
    protected void clickByText(String text) {
        log.info("Clicking element with text: {}", text);
        page.getByText(text).click();
    }

    /**
     * Click element by role and name
     *
     * @param role ARIA role
     * @param name Accessible name
     */
    @Step("Click {role} with name: {name}")
    protected void clickByRole(AriaRole role, String name) {
        log.info("Clicking {} with name: {}", role, name);
        page.getByRole(role, new Page.GetByRoleOptions().setName(name)).click();
    }

    /**
     * Fill input field
     *
     * @param selector Element selector
     * @param text     Text to fill
     */
    @Step("Fill '{selector}' with: {text}")
    protected void fill(String selector, String text) {
        log.info("Filling '{}' with: {}", selector, text);
        waitForVisible(selector);
        page.locator(selector).fill(text);
    }

    /**
     * Type text with delay (simulates human typing)
     *
     * @param selector Element selector
     * @param text     Text to type
     * @param delayMs  Delay between keystrokes
     */
    @Step("Type '{text}' into '{selector}'")
    protected void type(String selector, String text, int delayMs) {
        log.info("Typing '{}' into '{}' with delay: {}ms", text, selector, delayMs);
        waitForVisible(selector);
        page.locator(selector).type(text, new Locator.TypeOptions().setDelay(delayMs));
    }

    /**
     * Get text content of element
     *
     * @param selector Element selector
     * @return Text content
     */
    public String getText(String selector) {
        waitForVisible(selector);
        String text = page.locator(selector).textContent();
        log.debug("Got text from '{}': {}", selector, text);
        return text;
    }

    /**
     * Get input value
     *
     * @param selector Element selector
     * @return Input value
     */
    public String getValue(String selector) {
        waitForVisible(selector);
        return page.locator(selector).inputValue();
    }

    /**
     * Select dropdown option by value
     *
     * @param selector Element selector
     * @param value    Option value
     */
    @Step("Select option '{value}' in '{selector}'")
    protected void selectByValue(String selector, String value) {
        log.info("Selecting option '{}' in '{}'", value, selector);
        waitForVisible(selector);
        page.locator(selector).selectOption(value);
    }

    /**
     * Select dropdown option by visible text
     *
     * @param selector Element selector
     * @param text     Option text
     */
    @Step("Select option with text '{text}' in '{selector}'")
    protected void selectByText(String selector, String text) {
        log.info("Selecting option with text '{}' in '{}'", text, selector);
        waitForVisible(selector);
        page.locator(selector).selectOption(new SelectOption().setLabel(text));
    }

    /**
     * Check checkbox
     *
     * @param selector Element selector
     */
    @Step("Check checkbox: {selector}")
    protected void check(String selector) {
        log.info("Checking checkbox: {}", selector);
        waitForVisible(selector);
        page.locator(selector).check();
    }

    /**
     * Uncheck checkbox
     *
     * @param selector Element selector
     */
    @Step("Uncheck checkbox: {selector}")
    protected void uncheck(String selector) {
        log.info("Unchecking checkbox: {}", selector);
        waitForVisible(selector);
        page.locator(selector).uncheck();
    }

    /**
     * Scroll element into view
     *
     * @param selector Element selector
     */
    @Step("Scroll to: {selector}")
    protected void scrollTo(String selector) {
        log.info("Scrolling to: {}", selector);
        page.locator(selector).scrollIntoViewIfNeeded();
    }

    /**
     * Hover over element
     *
     * @param selector Element selector
     */
    @Step("Hover over: {selector}")
    protected void hover(String selector) {
        log.info("Hovering over: {}", selector);
        waitForVisible(selector);
        page.locator(selector).hover();
    }

    /**
     * Press keyboard key
     *
     * @param key Key to press (e.g., "Enter", "Escape")
     */
    @Step("Press key: {key}")
    public void pressKey(String key) {
        log.info("Pressing key: {}", key);
        page.keyboard().press(key);
    }

    /**
     * Reload current page
     */
    @Step("Reload page")
    public void reload() {
        log.info("Reloading page");
        page.reload();
        waitForPageLoad();
    }

    /**
     * Get current page URL
     *
     * @return Current URL
     */
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * Get page title
     *
     * @return Page title
     */
    public String getTitle() {
        return page.title();
    }

    /**
     * Take full-page screenshot with timestamp
     *
     * @param screenshotName Screenshot file name
     * @return Path to screenshot
     */
    @Step("Take screenshot: {screenshotName}")
    public String takeScreenshot(String screenshotName) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = String.format("target/screenshots/%s_%s.png", screenshotName, timestamp);
        log.info("Taking screenshot: {}", path);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(path))
                .setFullPage(true));
        return path;
    }

    /**
     * Wait for navigation to complete
     *
     * @param action Action that triggers navigation
     */
    @Step("Wait for navigation")
    protected void waitForNavigation(Runnable action) {
        log.info("Waiting for navigation");
        page.waitForNavigation(action);
    }

    /**
     * Execute JavaScript
     *
     * @param script JavaScript code
     * @return Result of execution
     */
    protected Object executeScript(String script) {
        log.debug("Executing JavaScript: {}", script);
        return page.evaluate(script);
    }

    /**
     * Get element count
     *
     * @param selector Element selector
     * @return Number of matching elements
     */
    public int getElementCount(String selector) {
        return page.locator(selector).count();
    }

    /**
     * Wait for element count to be
     *
     * @param selector      Element selector
     * @param expectedCount Expected count
     */
    @Step("Wait for {expectedCount} elements matching: {selector}")
    protected void waitForElementCount(String selector, int expectedCount) {
        log.info("Waiting for {} elements matching: {}", expectedCount, selector);
        page.waitForCondition(() -> page.locator(selector).count() == expectedCount,
                new Page.WaitForConditionOptions().setTimeout(defaultTimeout));
    }

    /**
     * Check if page contains text
     *
     * @param text Text to search for
     * @return true if text is found
     */
    public boolean containsText(String text) {
        return page.content().contains(text);
    }

    /**
     * Get attribute value
     *
     * @param selector  Element selector
     * @param attribute Attribute name
     * @return Attribute value
     */
    public String getAttribute(String selector, String attribute) {
        waitForVisible(selector);
        return page.locator(selector).getAttribute(attribute);
    }

    /**
     * Check if element is enabled
     *
     * @param selector Element selector
     * @return true if enabled
     */
    public boolean isEnabled(String selector) {
        return page.locator(selector).isEnabled();
    }

    /**
     * Check if element is checked
     *
     * @param selector Element selector
     * @return true if checked
     */
    public boolean isChecked(String selector) {
        return page.locator(selector).isChecked();
    }

    // ========================================
    // MOBILE-SPECIFIC METHODS
    // ========================================

    /**
     * Tap on an element (mobile-friendly)
     *
     * @param selector Element selector
     */
    @Step("Tap on: {selector}")
    protected void tap(String selector) {
        log.info("Tapping on: {}", selector);
        com.automation.utils.MobileUtils.tap(page.locator(selector));
    }

    /**
     * Long press on an element
     *
     * @param selector Element selector
     * @param durationMs Duration to hold press
     */
    @Step("Long press on: {selector}")
    protected void longPress(String selector, int durationMs) {
        log.info("Long pressing on: {} for {} ms", selector, durationMs);
        com.automation.utils.MobileUtils.longPress(page.locator(selector), durationMs);
    }

    /**
     * Double tap on an element
     *
     * @param selector Element selector
     */
    @Step("Double tap on: {selector}")
    protected void doubleTap(String selector) {
        log.info("Double tapping on: {}", selector);
        com.automation.utils.MobileUtils.doubleTap(page.locator(selector));
    }

    /**
     * Swipe up (scroll down)
     */
    @Step("Swipe up")
    protected void swipeUp() {
        log.info("Swiping up");
        com.automation.utils.MobileUtils.swipeUp(page);
    }

    /**
     * Swipe down (scroll up)
     */
    @Step("Swipe down")
    protected void swipeDown() {
        log.info("Swiping down");
        com.automation.utils.MobileUtils.swipeDown(page);
    }

    /**
     * Swipe left
     */
    @Step("Swipe left")
    protected void swipeLeft() {
        log.info("Swiping left");
        com.automation.utils.MobileUtils.swipeLeft(page);
    }

    /**
     * Swipe right
     */
    @Step("Swipe right")
    protected void swipeRight() {
        log.info("Swiping right");
        com.automation.utils.MobileUtils.swipeRight(page);
    }

    /**
     * Swipe on a specific element
     *
     * @param selector Element selector
     * @param direction Swipe direction
     */
    @Step("Swipe {direction} on: {selector}")
    protected void swipeOnElement(String selector, com.automation.utils.MobileUtils.SwipeDirection direction) {
        log.info("Swiping {} on: {}", direction, selector);
        com.automation.utils.MobileUtils.swipeOnElement(page.locator(selector), direction);
    }

    /**
     * Scroll to top of page
     */
    @Step("Scroll to top")
    protected void scrollToTop() {
        log.info("Scrolling to top");
        com.automation.utils.MobileUtils.scrollToTop(page);
    }

    /**
     * Scroll to bottom of page
     */
    @Step("Scroll to bottom")
    protected void scrollToBottom() {
        log.info("Scrolling to bottom");
        com.automation.utils.MobileUtils.scrollToBottom(page);
    }

    /**
     * Hide mobile keyboard
     */
    @Step("Hide keyboard")
    protected void hideKeyboard() {
        log.info("Hiding keyboard");
        com.automation.utils.MobileUtils.hideKeyboard(page);
    }

    /**
     * Set device to portrait orientation
     *
     * @param deviceWidth Device width in portrait
     * @param deviceHeight Device height in portrait
     */
    @Step("Set portrait orientation")
    protected void setPortraitOrientation(int deviceWidth, int deviceHeight) {
        log.info("Setting portrait orientation");
        com.automation.utils.MobileUtils.setPortraitOrientation(page, deviceWidth, deviceHeight);
    }

    /**
     * Set device to landscape orientation
     *
     * @param deviceWidth Device width in portrait
     * @param deviceHeight Device height in portrait
     */
    @Step("Set landscape orientation")
    protected void setLandscapeOrientation(int deviceWidth, int deviceHeight) {
        log.info("Setting landscape orientation");
        com.automation.utils.MobileUtils.setLandscapeOrientation(page, deviceWidth, deviceHeight);
    }
}
