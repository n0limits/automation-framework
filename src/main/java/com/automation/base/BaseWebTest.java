package com.automation.base;

import com.automation.factory.BrowserFactory;
import com.automation.utils.PlaywrightManager;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@Slf4j
public class BaseWebTest extends BaseTest {

    protected Page page;

    @BeforeMethod
    public void setupBrowser() {
        log.info("Setting up browser for web test");
        PlaywrightManager.initPlaywright();
        BrowserFactory.launchBrowser(config.getBrowser());
        page = PlaywrightManager.getPage();
        page.navigate(config.getBaseUrl());
    }

    @AfterMethod
    public void tearDownBrowser() {
        log.info("Tearing down browser");
        PlaywrightManager.quitPlaywright();
    }

}
