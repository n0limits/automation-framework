package com.automation.base;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;

@Slf4j
public class BaseAPITest extends BaseTest {

    @BeforeClass
    public void setupAPI() {
        log.info("Setting up API test");
        // API Client is initialized statically
    }

}
