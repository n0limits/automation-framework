package com.automation.base;

import com.automation.aws.AWSClientManager;
import com.automation.aws.StepFunctionsClient;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for AWS Step Functions tests
 * Provides lifecycle management and common AWS test functionality
 *
 * Features:
 * - Automatic AWS client initialization
 * - Automatic cleanup (prevents resource leaks)
 * - Template methods for custom setup/teardown
 * - Pre-configured StepFunctionsClient
 * - Thread-safe for parallel execution
 *
 * Usage:
 * <pre>
 * public class TradingWorkflowTests extends BaseAWSTest {
 *     {@literal @}Test
 *     public void testTradingWorkflow() {
 *         String executionArn = stepFunctionsClient.startExecution("{\"symbol\":\"BTC\"}");
 *         ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArn);
 *         assertThat(status).isEqualTo(ExecutionStatus.SUCCEEDED);
 *     }
 * }
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class BaseAWSTest extends BaseTest {
    protected StepFunctionsClient stepFunctionsClient;

    /**
     * Setup AWS clients before each test
     * Initializes StepFunctionsClient
     */
    @BeforeMethod
    public void setupAWS() {
        log.info("===== Setting up AWS Test =====");
        log.info("AWS Region: {}", AWSClientManager.getRegion());
        log.info("LocalStack Mode: {}", AWSClientManager.isLocalStack());

        // Initialize Step Functions client
        stepFunctionsClient = new StepFunctionsClient();
        log.info("StepFunctionsClient initialized");

        // Log State Machine details
        logStateMachineDetails();

        // Template method hook for additional setup
        performAdditionalAWSSetup();
    }

    /**
     * Cleanup AWS clients after each test
     * Ensures no resource leaks
     */
    @AfterMethod
    public void tearDownAWS() {
        log.info("===== Tearing down AWS Test =====");

        // Template method hook for additional cleanup
        performAdditionalAWSCleanup();

        // Cleanup AWS clients (closes connections, removes ThreadLocal)
        AWSClientManager.cleanup();
        log.info("AWS clients cleanup completed");
    }

    /**
     * Template method hook for additional AWS setup
     * Override in subclasses for custom initialization
     */
    protected void performAdditionalAWSSetup() {
        log.debug("No additional AWS setup required");
    }

    /**
     * Template method hook for additional AWS cleanup
     * Override in subclasses for custom teardown
     */
    protected void performAdditionalAWSCleanup() {
        log.debug("No additional AWS cleanup required");
    }

    /**
     * Log State Machine details for debugging
     */
    private void logStateMachineDetails() {
        try {
            var details = stepFunctionsClient.getStateMachineDetails();
            log.info("State Machine: {}", details.name());
            log.info("State Machine ARN: {}", details.stateMachineArn());
            log.info("State Machine Type: {}", details.type());
            log.info("State Machine Status: {}", details.status());
            log.debug("State Machine Definition: {}", details.definition());
        } catch (Exception e) {
            log.warn("Could not retrieve State Machine details (may not exist in LocalStack): {}",
                    e.getMessage());
        }
    }

    /**
     * Helper method to create simple JSON input
     *
     * @param key   Property key
     * @param value Property value
     * @return JSON string
     */
    protected String createSimpleJsonInput(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

    /**
     * Helper method to create JSON input with multiple key-value pairs
     *
     * @param keyValues Alternating key-value pairs
     * @return JSON string
     */
    protected String createJsonInput(String... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("keyValues must have even number of elements (key-value pairs)");
        }

        StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < keyValues.length; i += 2) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(keyValues[i]).append("\":\"").append(keyValues[i + 1]).append("\"");
        }
        json.append("}");
        return json.toString();
    }
}
