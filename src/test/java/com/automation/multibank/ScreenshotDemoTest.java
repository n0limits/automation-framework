package com.automation.multibank;

import com.automation.base.BaseWebTest;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demo test to verify screenshot capture on failure
 * This test intentionally fails to demonstrate Allure screenshot attachment
 */
@Slf4j
@Epic("Test Report Demo")
@Feature("Screenshot Capture on Failure")
public class ScreenshotDemoTest extends BaseWebTest {

    @Test(description = "Demo: Screenshot capture on test failure", priority = 1)
    @Severity(SeverityLevel.MINOR)
    @Story("Failure Screenshot Verification")
    @Description("This test intentionally fails to demonstrate that screenshots are automatically captured and attached to the Allure report when a test fails")
    public void demoScreenshotCaptureOnFailure() {
        log.info("Starting screenshot capture demo test");

        // Navigate to the MultiBank homepage
        page.navigate("https://trade.multibank.io");
        page.waitForLoadState();

        log.info("Page loaded: {}", page.url());
        log.info("Page title: {}", page.title());

        // This assertion will intentionally fail to trigger screenshot capture
        assertThat(page.title())
                .as(" INTENTIONAL FAILURE - This test demonstrates screenshot capture on failure")
                .isEqualTo("This Title Does Not Exist - Screenshot Demo");

        // This line won't be reached due to the failure above
        log.info("Test completed");
    }
}
