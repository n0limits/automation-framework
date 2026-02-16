package com.automation.mobile;

import com.automation.base.BaseWebTest;
import com.automation.utils.MobileUtils;
import com.microsoft.playwright.options.LoadState;
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
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Swipe down to scroll back up
        MobileUtils.swipeDown(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Swipe left (useful for carousels, image galleries)
        MobileUtils.swipeLeft(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Swipe right
        MobileUtils.swipeRight(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    @Test(description = "Demonstrate tap and long press gestures")
    @Severity(SeverityLevel.NORMAL)
    @Story("Tap Gestures")
    @Description("Test tap, double tap, and long press interactions")
    public void testTapGestures() {
        // Tap on an element (mobile-friendly click)
        if (page.locator(".menu-button").isVisible()) {
            MobileUtils.tap(page.locator(".menu-button"));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        }

        // Double tap (zoom or select)
        if (page.locator(".trading-chart").isVisible()) {
            MobileUtils.doubleTap(page.locator(".trading-chart"));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        }

        // Long press (context menu or additional options)
        if (page.locator(".trading-pair-item").first().isVisible()) {
            MobileUtils.longPress(page.locator(".trading-pair-item").first(), 1000);
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        }
    }

    @Test(description = "Demonstrate scrolling on mobile device")
    @Severity(SeverityLevel.NORMAL)
    @Story("Scrolling")
    @Description("Test scroll to top, bottom, and element")
    public void testScrolling() {
        // Scroll to bottom of page
        MobileUtils.scrollToBottom(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Scroll to top of page
        MobileUtils.scrollToTop(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Scroll to specific element
        if (page.locator(".footer").isVisible()) {
            MobileUtils.scrollToElement(page.locator(".footer"));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        }
    }

    @Test(description = "Demonstrate orientation switching")
    @Severity(SeverityLevel.NORMAL)
    @Story("Orientation")
    @Description("Test switching between portrait and landscape orientations")
    public void testOrientationSwitching() {
        // Start in portrait (390x844 for iPhone 13)
        MobileUtils.setPortraitOrientation(page, 390, 844);
        page.waitForLoadState(LoadState.LOAD);

        // Verify page adapts to portrait
        // Add your assertions here

        // Switch to landscape
        MobileUtils.setLandscapeOrientation(page, 390, 844);
        page.waitForLoadState(LoadState.LOAD);

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
            page.locator("input[type='text']").first().waitFor();

            // Type some text
            page.locator("input[type='text']").first().fill("test input");

            // Hide keyboard
            MobileUtils.hideKeyboard(page);
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
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);

            MobileUtils.swipeOnElement(page.locator(".trading-pairs-list"), MobileUtils.SwipeDirection.DOWN);
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        }
    }

    @Test(description = "Demonstrate pull-to-refresh gesture")
    @Severity(SeverityLevel.NORMAL)
    @Story("Pull to Refresh")
    @Description("Test pull-to-refresh mobile pattern")
    public void testPullToRefresh() {
        // Simulate pull-to-refresh by swiping down from top
        MobileUtils.scrollToTop(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Swipe down to trigger refresh
        MobileUtils.swipeDown(page);
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Verify page refreshed
        // Add your assertions here
    }

    @Test(description = "Complete mobile workflow example")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Mobile Workflow")
    @Description("Complete end-to-end mobile user journey with gestures")
    public void testCompleteMobileWorkflow() {
        // 1. Wait for page to load
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // 2. Scroll to explore content
        MobileUtils.swipeUp(page);
        MobileUtils.swipeUp(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // 3. Tap on a trading pair
        if (page.locator(".trading-pair-item").first().isVisible()) {
            MobileUtils.tap(page.locator(".trading-pair-item").first());
            page.waitForLoadState(LoadState.LOAD);
        }

        // 4. Swipe through charts or details
        MobileUtils.swipeLeft(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        MobileUtils.swipeRight(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // 5. Scroll back to top
        MobileUtils.scrollToTop(page);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // 6. Open menu with tap
        if (page.locator(".menu-button").isVisible()) {
            MobileUtils.tap(page.locator(".menu-button"));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        }

        // 7. Close menu (tap outside or back)
        if (page.locator(".overlay").isVisible()) {
            MobileUtils.tap(page.locator(".overlay"));
        }
    }
}
