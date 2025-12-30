package com.automation.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * Mobile-specific utility methods for touch interactions and gestures
 * Supports swipe, tap, long press, pinch-to-zoom, and scroll gestures
 */
@Slf4j
public class MobileUtils {

    /**
     * Swipe direction enum
     */
    public enum SwipeDirection {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * Perform a swipe gesture on the page
     * @param page Playwright page
     * @param direction Swipe direction
     * @param durationMs Duration of swipe in milliseconds
     */
    public static void swipe(Page page, SwipeDirection direction, int durationMs) {
        int width = page.viewportSize().width;
        int height = page.viewportSize().height;

        int startX, startY, endX, endY;

        switch (direction) {
            case UP:
                startX = width / 2;
                startY = (int) (height * 0.8);
                endX = width / 2;
                endY = (int) (height * 0.2);
                break;
            case DOWN:
                startX = width / 2;
                startY = (int) (height * 0.2);
                endX = width / 2;
                endY = (int) (height * 0.8);
                break;
            case LEFT:
                startX = (int) (width * 0.8);
                startY = height / 2;
                endX = (int) (width * 0.2);
                endY = height / 2;
                break;
            case RIGHT:
                startX = (int) (width * 0.2);
                startY = height / 2;
                endX = (int) (width * 0.8);
                endY = height / 2;
                break;
            default:
                throw new IllegalArgumentException("Invalid swipe direction: " + direction);
        }

        performSwipe(page, startX, startY, endX, endY, durationMs);
        log.info("Swiped {} on page", direction);
    }

    /**
     * Swipe up on the page (scroll down)
     * @param page Playwright page
     */
    public static void swipeUp(Page page) {
        swipe(page, SwipeDirection.UP, 300);
    }

    /**
     * Swipe down on the page (scroll up)
     * @param page Playwright page
     */
    public static void swipeDown(Page page) {
        swipe(page, SwipeDirection.DOWN, 300);
    }

    /**
     * Swipe left on the page
     * @param page Playwright page
     */
    public static void swipeLeft(Page page) {
        swipe(page, SwipeDirection.LEFT, 300);
    }

    /**
     * Swipe right on the page
     * @param page Playwright page
     */
    public static void swipeRight(Page page) {
        swipe(page, SwipeDirection.RIGHT, 300);
    }

    /**
     * Swipe on a specific element
     * @param element Element to swipe on
     * @param direction Swipe direction
     */
    public static void swipeOnElement(Locator element, SwipeDirection direction) {
        BoundingBox box = element.boundingBox();
        if (box == null) {
            throw new IllegalStateException("Element not visible, cannot swipe");
        }

        double startX, startY, endX, endY;
        double centerX = box.x + (box.width / 2);
        double centerY = box.y + (box.height / 2);

        switch (direction) {
            case UP:
                startX = centerX;
                startY = box.y + (box.height * 0.8);
                endX = centerX;
                endY = box.y + (box.height * 0.2);
                break;
            case DOWN:
                startX = centerX;
                startY = box.y + (box.height * 0.2);
                endX = centerX;
                endY = box.y + (box.height * 0.8);
                break;
            case LEFT:
                startX = box.x + (box.width * 0.8);
                startY = centerY;
                endX = box.x + (box.width * 0.2);
                endY = centerY;
                break;
            case RIGHT:
                startX = box.x + (box.width * 0.2);
                startY = centerY;
                endX = box.x + (box.width * 0.8);
                endY = centerY;
                break;
            default:
                throw new IllegalArgumentException("Invalid swipe direction: " + direction);
        }

        Page page = element.page();
        performSwipe(page, (int) startX, (int) startY, (int) endX, (int) endY, 300);
        log.info("Swiped {} on element", direction);
    }

    /**
     * Perform the actual swipe using touch events
     */
    private static void performSwipe(Page page, int startX, int startY, int endX, int endY, int durationMs) {
        page.mouse().move(startX, startY);
        page.mouse().down();

        // Simulate smooth swipe with multiple intermediate points
        int steps = 10;
        for (int i = 1; i <= steps; i++) {
            int x = startX + ((endX - startX) * i / steps);
            int y = startY + ((endY - startY) * i / steps);
            page.mouse().move(x, y);
            try {
                Thread.sleep(durationMs / steps);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        page.mouse().up();
    }

    /**
     * Tap on an element (mobile-friendly click)
     * @param element Element to tap
     */
    public static void tap(Locator element) {
        element.tap();
        log.info("Tapped on element");
    }

    /**
     * Tap at specific coordinates
     * @param page Playwright page
     * @param x X coordinate
     * @param y Y coordinate
     */
    public static void tapAt(Page page, int x, int y) {
        page.touchscreen().tap(x, y);
        log.info("Tapped at coordinates ({}, {})", x, y);
    }

    /**
     * Long press on an element
     * @param element Element to long press
     * @param durationMs Duration to hold press (milliseconds)
     */
    public static void longPress(Locator element, int durationMs) {
        BoundingBox box = element.boundingBox();
        if (box == null) {
            throw new IllegalStateException("Element not visible, cannot long press");
        }

        int x = (int) (box.x + (box.width / 2));
        int y = (int) (box.y + (box.height / 2));

        Page page = element.page();
        page.mouse().move(x, y);
        page.mouse().down();
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        page.mouse().up();

        log.info("Long pressed on element for {} ms", durationMs);
    }

    /**
     * Double tap on an element
     * @param element Element to double tap
     */
    public static void doubleTap(Locator element) {
        element.dblclick();
        log.info("Double tapped on element");
    }

    /**
     * Scroll to element (mobile-friendly)
     * @param element Element to scroll to
     */
    public static void scrollToElement(Locator element) {
        element.scrollIntoViewIfNeeded();
        log.info("Scrolled to element");
    }

    /**
     * Scroll by pixels
     * @param page Playwright page
     * @param x Horizontal scroll amount
     * @param y Vertical scroll amount
     */
    public static void scrollBy(Page page, int x, int y) {
        page.evaluate(String.format("window.scrollBy(%d, %d)", x, y));
        log.info("Scrolled by ({}, {})", x, y);
    }

    /**
     * Scroll to top of page
     * @param page Playwright page
     */
    public static void scrollToTop(Page page) {
        page.evaluate("window.scrollTo(0, 0)");
        log.info("Scrolled to top of page");
    }

    /**
     * Scroll to bottom of page
     * @param page Playwright page
     */
    public static void scrollToBottom(Page page) {
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        log.info("Scrolled to bottom of page");
    }

    /**
     * Pinch to zoom (zoom in)
     * @param page Playwright page
     * @param scaleFactor Scale factor (e.g., 2.0 for 2x zoom)
     */
    public static void pinchZoomIn(Page page, double scaleFactor) {
        int width = page.viewportSize().width;
        int height = page.viewportSize().height;
        int centerX = width / 2;
        int centerY = height / 2;

        // Start with fingers close together
        int startOffset = 50;
        int endOffset = (int) (startOffset * scaleFactor);

        // Two-finger pinch gesture simulation
        String script = String.format(
            "const touches = [" +
            "  {identifier: 0, clientX: %d, clientY: %d}," +
            "  {identifier: 1, clientX: %d, clientY: %d}" +
            "];" +
            "document.dispatchEvent(new TouchEvent('touchstart', {touches: touches}));" +
            "setTimeout(() => {" +
            "  const touches2 = [" +
            "    {identifier: 0, clientX: %d, clientY: %d}," +
            "    {identifier: 1, clientX: %d, clientY: %d}" +
            "  ];" +
            "  document.dispatchEvent(new TouchEvent('touchmove', {touches: touches2}));" +
            "  document.dispatchEvent(new TouchEvent('touchend', {touches: []}));" +
            "}, 100);",
            centerX - startOffset, centerY,
            centerX + startOffset, centerY,
            centerX - endOffset, centerY,
            centerX + endOffset, centerY
        );

        page.evaluate(script);
        log.info("Pinch zoom in with scale factor: {}", scaleFactor);
    }

    /**
     * Pinch to zoom (zoom out)
     * @param page Playwright page
     * @param scaleFactor Scale factor (e.g., 0.5 for 50% zoom)
     */
    public static void pinchZoomOut(Page page, double scaleFactor) {
        int width = page.viewportSize().width;
        int height = page.viewportSize().height;
        int centerX = width / 2;
        int centerY = height / 2;

        // Start with fingers far apart
        int startOffset = 150;
        int endOffset = (int) (startOffset * scaleFactor);

        String script = String.format(
            "const touches = [" +
            "  {identifier: 0, clientX: %d, clientY: %d}," +
            "  {identifier: 1, clientX: %d, clientY: %d}" +
            "];" +
            "document.dispatchEvent(new TouchEvent('touchstart', {touches: touches}));" +
            "setTimeout(() => {" +
            "  const touches2 = [" +
            "    {identifier: 0, clientX: %d, clientY: %d}," +
            "    {identifier: 1, clientX: %d, clientY: %d}" +
            "  ];" +
            "  document.dispatchEvent(new TouchEvent('touchmove', {touches: touches2}));" +
            "  document.dispatchEvent(new TouchEvent('touchend', {touches: []}));" +
            "}, 100);",
            centerX - startOffset, centerY,
            centerX + startOffset, centerY,
            centerX - endOffset, centerY,
            centerX + endOffset, centerY
        );

        page.evaluate(script);
        log.info("Pinch zoom out with scale factor: {}", scaleFactor);
    }

    /**
     * Check if element is in viewport
     * @param element Element to check
     * @return true if element is in viewport
     */
    public static boolean isInViewport(Locator element) {
        return element.isVisible();
    }

    /**
     * Hide mobile keyboard (if visible)
     * @param page Playwright page
     */
    public static void hideKeyboard(Page page) {
        page.evaluate("document.activeElement.blur()");
        log.info("Hid mobile keyboard");
    }

    /**
     * Rotate device orientation
     * @param page Playwright page
     * @param width New viewport width
     * @param height New viewport height
     */
    public static void rotateDevice(Page page, int width, int height) {
        page.setViewportSize(width, height);
        log.info("Rotated device to {}x{}", width, height);
    }

    /**
     * Set device to portrait orientation
     * @param page Playwright page
     * @param deviceWidth Device width in portrait
     * @param deviceHeight Device height in portrait
     */
    public static void setPortraitOrientation(Page page, int deviceWidth, int deviceHeight) {
        rotateDevice(page, Math.min(deviceWidth, deviceHeight), Math.max(deviceWidth, deviceHeight));
        log.info("Set device to portrait orientation");
    }

    /**
     * Set device to landscape orientation
     * @param page Playwright page
     * @param deviceWidth Device width in portrait
     * @param deviceHeight Device height in portrait
     */
    public static void setLandscapeOrientation(Page page, int deviceWidth, int deviceHeight) {
        rotateDevice(page, Math.max(deviceWidth, deviceHeight), Math.min(deviceWidth, deviceHeight));
        log.info("Set device to landscape orientation");
    }
}
