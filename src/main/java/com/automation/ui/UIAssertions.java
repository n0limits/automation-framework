package com.automation.ui;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.Assertions;

/**
 * Fluent UI Assertions Helper
 * Provides readable and chainable assertions for UI testing
 *
 * Usage Example:
 * <pre>
 * UIAssertions.assertThat(page)
 *     .urlContains("/dashboard")
 *     .titleContains("Dashboard")
 *     .elementIsVisible(".buy-button")
 *     .elementIsEnabled(".buy-button");
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class UIAssertions {
    private final Page page;

    private UIAssertions(Page page) {
        this.page = page;
    }

    /**
     * Start fluent assertion chain for a page
     *
     * @param page Playwright page object
     * @return UIAssertions instance for chaining
     */
    public static UIAssertions assertThat(Page page) {
        return new UIAssertions(page);
    }

    // ========== URL Assertions ==========

    /**
     * Assert URL contains expected text
     *
     * @param expectedUrlPart Expected text in URL
     * @return this for chaining
     */
    @Step("Assert URL contains: {expectedUrlPart}")
    public UIAssertions urlContains(String expectedUrlPart) {
        String actualUrl = page.url();
        log.info("Asserting URL '{}' contains '{}'", actualUrl, expectedUrlPart);

        Assertions.assertThat(actualUrl)
                .as("URL should contain: " + expectedUrlPart)
                .contains(expectedUrlPart);

        return this;
    }

    /**
     * Assert URL equals expected URL
     *
     * @param expectedUrl Expected complete URL
     * @return this for chaining
     */
    @Step("Assert URL equals: {expectedUrl}")
    public UIAssertions urlEquals(String expectedUrl) {
        String actualUrl = page.url();
        log.info("Asserting URL '{}' equals '{}'", actualUrl, expectedUrl);

        Assertions.assertThat(actualUrl)
                .as("URL should equal: " + expectedUrl)
                .isEqualTo(expectedUrl);

        return this;
    }

    /**
     * Assert URL starts with expected prefix
     *
     * @param expectedPrefix Expected URL prefix
     * @return this for chaining
     */
    @Step("Assert URL starts with: {expectedPrefix}")
    public UIAssertions urlStartsWith(String expectedPrefix) {
        String actualUrl = page.url();
        log.info("Asserting URL '{}' starts with '{}'", actualUrl, expectedPrefix);

        Assertions.assertThat(actualUrl)
                .as("URL should start with: " + expectedPrefix)
                .startsWith(expectedPrefix);

        return this;
    }

    // ========== Title Assertions ==========

    /**
     * Assert page title contains expected text
     *
     * @param expectedTitlePart Expected text in title
     * @return this for chaining
     */
    @Step("Assert title contains: {expectedTitlePart}")
    public UIAssertions titleContains(String expectedTitlePart) {
        String actualTitle = page.title();
        log.info("Asserting title '{}' contains '{}'", actualTitle, expectedTitlePart);

        Assertions.assertThat(actualTitle)
                .as("Title should contain: " + expectedTitlePart)
                .containsIgnoringCase(expectedTitlePart);

        return this;
    }

    /**
     * Assert page title equals expected title
     *
     * @param expectedTitle Expected complete title
     * @return this for chaining
     */
    @Step("Assert title equals: {expectedTitle}")
    public UIAssertions titleEquals(String expectedTitle) {
        String actualTitle = page.title();
        log.info("Asserting title '{}' equals '{}'", actualTitle, expectedTitle);

        Assertions.assertThat(actualTitle)
                .as("Title should equal: " + expectedTitle)
                .isEqualTo(expectedTitle);

        return this;
    }

    // ========== Element Visibility Assertions ==========

    /**
     * Assert element is visible
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element is visible: {selector}")
    public UIAssertions elementIsVisible(String selector) {
        log.info("Asserting element '{}' is visible", selector);

        boolean isVisible = page.locator(selector).isVisible();
        Assertions.assertThat(isVisible)
                .as("Element should be visible: " + selector)
                .isTrue();

        return this;
    }

    /**
     * Assert element is hidden
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element is hidden: {selector}")
    public UIAssertions elementIsHidden(String selector) {
        log.info("Asserting element '{}' is hidden", selector);

        boolean isVisible = page.locator(selector).isVisible();
        Assertions.assertThat(isVisible)
                .as("Element should be hidden: " + selector)
                .isFalse();

        return this;
    }

    /**
     * Assert element exists in DOM
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element exists: {selector}")
    public UIAssertions elementExists(String selector) {
        log.info("Asserting element '{}' exists", selector);

        int count = page.locator(selector).count();
        Assertions.assertThat(count)
                .as("Element should exist: " + selector)
                .isGreaterThan(0);

        return this;
    }

    /**
     * Assert element does not exist in DOM
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element does not exist: {selector}")
    public UIAssertions elementDoesNotExist(String selector) {
        log.info("Asserting element '{}' does not exist", selector);

        int count = page.locator(selector).count();
        Assertions.assertThat(count)
                .as("Element should not exist: " + selector)
                .isEqualTo(0);

        return this;
    }

    // ========== Element State Assertions ==========

    /**
     * Assert element is enabled
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element is enabled: {selector}")
    public UIAssertions elementIsEnabled(String selector) {
        log.info("Asserting element '{}' is enabled", selector);

        boolean isEnabled = page.locator(selector).isEnabled();
        Assertions.assertThat(isEnabled)
                .as("Element should be enabled: " + selector)
                .isTrue();

        return this;
    }

    /**
     * Assert element is disabled
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element is disabled: {selector}")
    public UIAssertions elementIsDisabled(String selector) {
        log.info("Asserting element '{}' is disabled", selector);

        boolean isEnabled = page.locator(selector).isEnabled();
        Assertions.assertThat(isEnabled)
                .as("Element should be disabled: " + selector)
                .isFalse();

        return this;
    }

    /**
     * Assert checkbox/radio is checked
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element is checked: {selector}")
    public UIAssertions elementIsChecked(String selector) {
        log.info("Asserting element '{}' is checked", selector);

        boolean isChecked = page.locator(selector).isChecked();
        Assertions.assertThat(isChecked)
                .as("Element should be checked: " + selector)
                .isTrue();

        return this;
    }

    /**
     * Assert checkbox/radio is unchecked
     *
     * @param selector Element selector
     * @return this for chaining
     */
    @Step("Assert element is unchecked: {selector}")
    public UIAssertions elementIsUnchecked(String selector) {
        log.info("Asserting element '{}' is unchecked", selector);

        boolean isChecked = page.locator(selector).isChecked();
        Assertions.assertThat(isChecked)
                .as("Element should be unchecked: " + selector)
                .isFalse();

        return this;
    }

    // ========== Element Content Assertions ==========

    /**
     * Assert element text equals expected text
     *
     * @param selector Element selector
     * @param expectedText Expected text
     * @return this for chaining
     */
    @Step("Assert element '{selector}' text equals: {expectedText}")
    public UIAssertions elementTextEquals(String selector, String expectedText) {
        String actualText = page.locator(selector).textContent().trim();
        log.info("Asserting element '{}' text '{}' equals '{}'", selector, actualText, expectedText);

        Assertions.assertThat(actualText)
                .as("Element text should equal: " + expectedText)
                .isEqualTo(expectedText);

        return this;
    }

    /**
     * Assert element text contains expected text
     *
     * @param selector Element selector
     * @param expectedText Expected text fragment
     * @return this for chaining
     */
    @Step("Assert element '{selector}' text contains: {expectedText}")
    public UIAssertions elementTextContains(String selector, String expectedText) {
        String actualText = page.locator(selector).textContent().trim();
        log.info("Asserting element '{}' text '{}' contains '{}'", selector, actualText, expectedText);

        Assertions.assertThat(actualText)
                .as("Element text should contain: " + expectedText)
                .contains(expectedText);

        return this;
    }

    /**
     * Assert input value equals expected value
     *
     * @param selector Element selector
     * @param expectedValue Expected input value
     * @return this for chaining
     */
    @Step("Assert input '{selector}' value equals: {expectedValue}")
    public UIAssertions inputValueEquals(String selector, String expectedValue) {
        String actualValue = page.locator(selector).inputValue();
        log.info("Asserting input '{}' value '{}' equals '{}'", selector, actualValue, expectedValue);

        Assertions.assertThat(actualValue)
                .as("Input value should equal: " + expectedValue)
                .isEqualTo(expectedValue);

        return this;
    }

    /**
     * Assert element has CSS class
     *
     * @param selector Element selector
     * @param className Expected CSS class
     * @return this for chaining
     */
    @Step("Assert element '{selector}' has class: {className}")
    public UIAssertions elementHasClass(String selector, String className) {
        String classAttribute = page.locator(selector).getAttribute("class");
        log.info("Asserting element '{}' class '{}' contains '{}'", selector, classAttribute, className);

        Assertions.assertThat(classAttribute)
                .as("Element should have class: " + className)
                .contains(className);

        return this;
    }

    /**
     * Assert element has attribute with expected value
     *
     * @param selector Element selector
     * @param attributeName Attribute name
     * @param expectedValue Expected attribute value
     * @return this for chaining
     */
    @Step("Assert element '{selector}' attribute '{attributeName}' equals: {expectedValue}")
    public UIAssertions elementAttributeEquals(String selector, String attributeName, String expectedValue) {
        String actualValue = page.locator(selector).getAttribute(attributeName);
        log.info("Asserting element '{}' attribute '{}' value '{}' equals '{}'",
                selector, attributeName, actualValue, expectedValue);

        Assertions.assertThat(actualValue)
                .as(String.format("Element attribute '%s' should equal: %s", attributeName, expectedValue))
                .isEqualTo(expectedValue);

        return this;
    }

    // ========== Element Count Assertions ==========

    /**
     * Assert element count equals expected count
     *
     * @param selector Element selector
     * @param expectedCount Expected number of elements
     * @return this for chaining
     */
    @Step("Assert element count for '{selector}' equals: {expectedCount}")
    public UIAssertions elementCountEquals(String selector, int expectedCount) {
        int actualCount = page.locator(selector).count();
        log.info("Asserting element '{}' count {} equals {}", selector, actualCount, expectedCount);

        Assertions.assertThat(actualCount)
                .as("Element count should equal: " + expectedCount)
                .isEqualTo(expectedCount);

        return this;
    }

    /**
     * Assert element count is greater than expected
     *
     * @param selector Element selector
     * @param minCount Minimum expected count
     * @return this for chaining
     */
    @Step("Assert element count for '{selector}' is greater than: {minCount}")
    public UIAssertions elementCountGreaterThan(String selector, int minCount) {
        int actualCount = page.locator(selector).count();
        log.info("Asserting element '{}' count {} is greater than {}", selector, actualCount, minCount);

        Assertions.assertThat(actualCount)
                .as("Element count should be greater than: " + minCount)
                .isGreaterThan(minCount);

        return this;
    }

    // ========== Page Content Assertions ==========

    /**
     * Assert page contains text
     *
     * @param expectedText Expected text in page
     * @return this for chaining
     */
    @Step("Assert page contains text: {expectedText}")
    public UIAssertions pageContainsText(String expectedText) {
        String pageContent = page.content();
        log.info("Asserting page contains text: {}", expectedText);

        Assertions.assertThat(pageContent)
                .as("Page should contain text: " + expectedText)
                .contains(expectedText);

        return this;
    }

    /**
     * Assert page does not contain text
     *
     * @param unexpectedText Text that should not be in page
     * @return this for chaining
     */
    @Step("Assert page does not contain text: {unexpectedText}")
    public UIAssertions pageDoesNotContainText(String unexpectedText) {
        String pageContent = page.content();
        log.info("Asserting page does not contain text: {}", unexpectedText);

        Assertions.assertThat(pageContent)
                .as("Page should not contain text: " + unexpectedText)
                .doesNotContain(unexpectedText);

        return this;
    }

    // ========== Custom Assertions ==========

    /**
     * Execute custom assertion
     *
     * @param assertion Custom assertion runnable
     * @return this for chaining
     */
    @Step("Execute custom assertion")
    public UIAssertions custom(Runnable assertion) {
        log.info("Executing custom assertion");
        assertion.run();
        return this;
    }

    /**
     * Get the underlying page object for advanced operations
     *
     * @return Playwright Page object
     */
    public Page getPage() {
        return page;
    }
}
