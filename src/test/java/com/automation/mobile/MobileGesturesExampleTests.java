package com.automation.mobile;

import com.automation.base.BaseWebTest;
import com.automation.utils.MobileUtils;
import io.qameta.allure.*;
import org.testng.annotations.Test;

/**
 * Example mobile test demonstrating mobile gestures and interactions
 * These tests showcase how to use mobile-specific features in the framework
 */
@Epic("Mobile Testing")
@Feature("Mobile Gestures")
public class MobileGesturesExampleTests extends BaseWebTest {

    @Test(description = "Demonstrate swipe gestures on mobile device")
    @Severity(SeverityLevel.NORMAL)
    @Story("Swipe Gestures")
    @Description("Test various swipe gestures: up, down, left, right")
    public void testSwipeGestures() {
        // Swipe up to scroll down
        MobileUtils.swipeUp(page);
        waitFor(500);

        // Swipe down to scroll back up
        MobileUtils.swipeDown(page);
        waitFor(500);

        // Swipe left (useful for carousels, image galleries)
        MobileUtils.swipeLeft(page);
        waitFor(500);

        // Swipe right
        MobileUtils.swipeRight(page);
        waitFor(500);
    }

    @Test(description = "Demonstrate tap and long press gestures")
    @Severity(SeverityLevel.NORMAL)
    @Story("Tap Gestures")
    @Description("Test tap, double tap, and long press interactions")
    public void testTapGestures() {
        // Tap on an element (mobile-friendly click)
        if (page.locator(".menu-button").isVisible()) {
            MobileUtils.tap(page.locator(".menu-button"));
            waitFor(500);
        }

        // Double tap (zoom or select)
        if (page.locator(".trading-chart").isVisible()) {
            MobileUtils.doubleTap(page.locator(".trading-chart"));
            waitFor(500);
        }

        // Long press (context menu or additional options)
        if (page.locator(".trading-pair-item").first().isVisible()) {
            MobileUtils.longPress(page.locator(".trading-pair-item").first(), 1000);
            waitFor(500);
        }
    }

    @Test(description = "Demonstrate scrolling on mobile device")
    @Severity(SeverityLevel.NORMAL)
    @Story("Scrolling")
    @Description("Test scroll to top, bottom, and element")
    public void testScrolling() {
        // Scroll to bottom of page
        MobileUtils.scrollToBottom(page);
        waitFor(500);

        // Scroll to top of page
        MobileUtils.scrollToTop(page);
        waitFor(500);

        // Scroll to specific element
        if (page.locator(".footer").isVisible()) {
            MobileUtils.scrollToElement(page.locator(".footer"));
            waitFor(500);
        }
    }

    @Test(description = "Demonstrate orientation switching")
    @Severity(SeverityLevel.NORMAL)
    @Story("Orientation")
    @Description("Test switching between portrait and landscape orientations")
    public void testOrientationSwitching() {
        // Start in portrait (390x844 for iPhone 13)
        MobileUtils.setPortraitOrientation(page, 390, 844);
        waitFor(1000);

        // Verify page adapts to portrait
        // Add your assertions here

        // Switch to landscape
        MobileUtils.setLandscapeOrientation(page, 390, 844);
        waitFor(1000);

        // Verify page adapts to landscape
        // Add your assertions here

        // Switch back to portrait
        MobileUtils.setPortraitOrientation(page, 390, 844);
    }

    @Test(description = "Demonstrate mobile keyboard interactions")
    @Severity(SeverityLevel.NORMAL)
    @Story("Keyboard")
    @Description("Test mobile keyboard show and hide")
    public void testKeyboardInteractions() {
        // Click on input field to show keyboard
        if (page.locator("input[type='text']").first().isVisible()) {
            MobileUtils.tap(page.locator("input[type='text']").first());
            waitFor(500);

            // Type some text
            page.locator("input[type='text']").first().fill("test input");
            waitFor(500);

            // Hide keyboard
            MobileUtils.hideKeyboard(page);
            waitFor(500);
        }
    }

    @Test(description = "Demonstrate swipe on specific element")
    @Severity(SeverityLevel.NORMAL)
    @Story("Element Swipe")
    @Description("Test swiping on a specific scrollable container")
    public void testSwipeOnElement() {
        // Swipe on a specific scrollable list or container
        if (page.locator(".trading-pairs-list").isVisible()) {
            MobileUtils.swipeOnElement(page.locator(".trading-pairs-list"), MobileUtils.SwipeDirection.UP);
            waitFor(500);

            MobileUtils.swipeOnElement(page.locator(".trading-pairs-list"), MobileUtils.SwipeDirection.DOWN);
            waitFor(500);
        }
    }

    @Test(description = "Demonstrate pull-to-refresh gesture")
    @Severity(SeverityLevel.NORMAL)
    @Story("Pull to Refresh")
    @Description("Test pull-to-refresh mobile pattern")
    public void testPullToRefresh() {
        // Simulate pull-to-refresh by swiping down from top
        MobileUtils.scrollToTop(page);
        waitFor(500);

        // Swipe down to trigger refresh
        MobileUtils.swipeDown(page);
        waitFor(2000); // Wait for refresh animation

        // Verify page refreshed
        // Add your assertions here
    }

    @Test(description = "Complete mobile workflow example")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Mobile Workflow")
    @Description("Complete end-to-end mobile user journey with gestures")
    public void testCompleteMobileWorkflow() {
        // 1. Wait for page to load
        waitFor(1000);

        // 2. Scroll to explore content
        MobileUtils.swipeUp(page);
        MobileUtils.swipeUp(page);
        waitFor(500);

        // 3. Tap on a trading pair
        if (page.locator(".trading-pair-item").first().isVisible()) {
            MobileUtils.tap(page.locator(".trading-pair-item").first());
            waitFor(1000);
        }

        // 4. Swipe through charts or details
        MobileUtils.swipeLeft(page);
        waitFor(500);
        MobileUtils.swipeRight(page);
        waitFor(500);

        // 5. Scroll back to top
        MobileUtils.scrollToTop(page);
        waitFor(500);

        // 6. Open menu with tap
        if (page.locator(".menu-button").isVisible()) {
            MobileUtils.tap(page.locator(".menu-button"));
            waitFor(500);
        }

        // 7. Close menu (tap outside or back)
        if (page.locator(".overlay").isVisible()) {
            MobileUtils.tap(page.locator(".overlay"));
        }
    }

    /**
     * Helper method to wait
     */
    private void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
