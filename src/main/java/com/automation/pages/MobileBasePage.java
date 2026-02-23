package com.automation.pages;

import com.automation.utils.MobileUtils;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

/**
 * Base page for mobile-specific page objects.
 * Extends BasePage with touch gestures, swipe, orientation,
 * and keyboard utilities via MobileUtils.
 *
 * Desktop page objects should extend BasePage directly.
 * Mobile page objects should extend MobileBasePage.
 *
 * @author Victor Grozev
 */
@Slf4j
public abstract class MobileBasePage extends BasePage {

    /**
     * Tap on an element (mobile-friendly)
     *
     * @param selector Element selector
     */
    @Step("Tap on: {selector}")
    protected void tap(String selector) {
        log.info("Tapping on: {}", selector);
        MobileUtils.tap(page.locator(selector));
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
        MobileUtils.longPress(page.locator(selector), durationMs);
    }

    /**
     * Double tap on an element
     *
     * @param selector Element selector
     */
    @Step("Double tap on: {selector}")
    protected void doubleTap(String selector) {
        log.info("Double tapping on: {}", selector);
        MobileUtils.doubleTap(page.locator(selector));
    }

    /**
     * Swipe up (scroll down)
     */
    @Step("Swipe up")
    protected void swipeUp() {
        log.info("Swiping up");
        MobileUtils.swipeUp(page);
    }

    /**
     * Swipe down (scroll up)
     */
    @Step("Swipe down")
    protected void swipeDown() {
        log.info("Swiping down");
        MobileUtils.swipeDown(page);
    }

    /**
     * Swipe left
     */
    @Step("Swipe left")
    protected void swipeLeft() {
        log.info("Swiping left");
        MobileUtils.swipeLeft(page);
    }

    /**
     * Swipe right
     */
    @Step("Swipe right")
    protected void swipeRight() {
        log.info("Swiping right");
        MobileUtils.swipeRight(page);
    }

    /**
     * Swipe on a specific element
     *
     * @param selector Element selector
     * @param direction Swipe direction
     */
    @Step("Swipe {direction} on: {selector}")
    protected void swipeOnElement(String selector, MobileUtils.SwipeDirection direction) {
        log.info("Swiping {} on: {}", direction, selector);
        MobileUtils.swipeOnElement(page.locator(selector), direction);
    }

    /**
     * Scroll to top of page
     */
    @Step("Scroll to top")
    protected void scrollToTop() {
        log.info("Scrolling to top");
        MobileUtils.scrollToTop(page);
    }

    /**
     * Scroll to bottom of page
     */
    @Step("Scroll to bottom")
    protected void scrollToBottom() {
        log.info("Scrolling to bottom");
        MobileUtils.scrollToBottom(page);
    }

    /**
     * Hide mobile keyboard
     */
    @Step("Hide keyboard")
    protected void hideKeyboard() {
        log.info("Hiding keyboard");
        MobileUtils.hideKeyboard(page);
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
        MobileUtils.setPortraitOrientation(page, deviceWidth, deviceHeight);
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
        MobileUtils.setLandscapeOrientation(page, deviceWidth, deviceHeight);
    }
}
