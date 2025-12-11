package com.automation.base;

import com.automation.config.TestConfig;
import com.automation.db.DatabaseConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

@Slf4j
public class BaseTest {
    protected static TestConfig config;

    @BeforeSuite
    public void setupSuite() {
        log.info("===== Test Suite Setup Started =====");
        config = TestConfig.getInstance();
    }

    @AfterSuite
    public void tearDownSuite() {
        DatabaseConnectionFactory.closeAllConnections();
        log.info("===== Test Suite Teardown Completed =====");
    }

}
