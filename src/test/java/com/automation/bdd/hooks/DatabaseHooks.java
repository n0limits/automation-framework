package com.automation.bdd.hooks;

import com.automation.bdd.context.ScenarioContext;
import com.automation.database.DatabaseTestUtils;
import com.automation.database.TestDataBuilder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

/**
 * Cucumber hooks for database scenarios tagged with @db.
 * Manages DatabaseTestUtils and TestDataBuilder lifecycle.
 *
 * @author Victor Grozev
 */
@Slf4j
public class DatabaseHooks {

    private final ScenarioContext scenarioContext;

    public DatabaseHooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(value = "@db", order = 1)
    public void setupDatabase(Scenario scenario) {
        DatabaseTestUtils dbUtils = new DatabaseTestUtils();
        TestDataBuilder dataBuilder = new TestDataBuilder(dbUtils);

        scenarioContext.set("dbUtils", dbUtils);
        scenarioContext.set("dataBuilder", dataBuilder);

        log.info("Database utilities initialized for scenario: {}", scenario.getName());
    }

    @After(value = "@db", order = 1)
    public void teardownDatabase(Scenario scenario) {
        TestDataBuilder dataBuilder = scenarioContext.get("dataBuilder");
        DatabaseTestUtils dbUtils = scenarioContext.get("dbUtils");

        try {
            if (dataBuilder != null) {
                dataBuilder.cleanupAll();
                log.info("Test data cleaned up");
            }
        } catch (SQLException e) {
            log.error("Error during test data cleanup", e);
        } finally {
            if (dbUtils != null) {
                dbUtils.cleanup();
                log.info("Database connection closed");
            }
        }

        log.info("Database teardown complete for scenario: {}", scenario.getName());
    }
}
