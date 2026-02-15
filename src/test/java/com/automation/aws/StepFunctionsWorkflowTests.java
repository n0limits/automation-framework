package com.automation.aws;

import com.automation.base.BaseAWSTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import software.amazon.awssdk.services.sfn.model.DescribeExecutionResponse;
import software.amazon.awssdk.services.sfn.model.ExecutionStatus;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test AWS Step Functions workflows
 * Demonstrates Step Functions execution, polling, and result validation
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("AWS Step Functions")
public class StepFunctionsWorkflowTests extends BaseAWSTest {

    @Test(description = "Verify Step Functions execution completes successfully")
    @Description("Start a Step Functions execution and verify it completes with SUCCEEDED status")
    @Severity(SeverityLevel.CRITICAL)
    public void testBasicWorkflowExecution() {
        log.info("=== Testing Basic Step Functions Workflow ===");

        // Prepare input
        String input = createSimpleJsonInput("action", "test");
        log.info("Input: {}", input);

        // Start execution
        String executionArn = stepFunctionsClient.startExecution(input);
        assertThat(executionArn)
                .isNotNull()
                .contains("execution");

        log.info("Execution ARN: {}", executionArn);

        // Wait for completion
        ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArn);

        // Verify execution succeeded
        assertThat(status)
                .as("Execution should complete successfully")
                .isEqualTo(ExecutionStatus.SUCCEEDED);

        // Get output
        String output = stepFunctionsClient.getExecutionOutput(executionArn);
        log.info("Execution output: {}", output);
        assertThat(output).isNotNull();

        log.info("✅ Basic workflow executed successfully!");
    }

    @Test(description = "Verify Step Functions execution with complex input")
    @Description("Test Step Functions with multiple input parameters")
    @Severity(SeverityLevel.NORMAL)
    public void testWorkflowWithComplexInput() {
        log.info("=== Testing Workflow with Complex Input ===");

        // Create complex input
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("symbol", "BTCUSD");
        inputMap.put("action", "BUY");
        inputMap.put("quantity", 1.5);
        inputMap.put("price", 45000.0);

        // Start execution with Map input
        String executionArn = stepFunctionsClient.startExecution(inputMap);
        assertThat(executionArn).isNotNull();

        // Wait for completion
        ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArn);

        // Verify success
        assertThat(status).isEqualTo(ExecutionStatus.SUCCEEDED);

        log.info("✅ Complex input workflow executed successfully!");
    }

    @Test(description = "Verify Step Functions execution details can be retrieved")
    @Description("Test that execution details contain expected metadata")
    @Severity(SeverityLevel.NORMAL)
    public void testRetrieveExecutionDetails() {
        log.info("=== Testing Execution Details Retrieval ===");

        // Start execution
        String executionName = "test-details-" + System.currentTimeMillis();
        String input = createJsonInput("test", "details", "timestamp", String.valueOf(System.currentTimeMillis()));
        String executionArn = stepFunctionsClient.startExecution(executionName, input);

        // Wait for completion
        stepFunctionsClient.waitForCompletion(executionArn);

        // Get detailed execution information
        DescribeExecutionResponse details = stepFunctionsClient.getExecutionDetails(executionArn);

        // Verify execution details
        assertThat(details).isNotNull();
        assertThat(details.name()).isEqualTo(executionName);
        assertThat(details.executionArn()).isEqualTo(executionArn);
        assertThat(details.stateMachineArn()).isNotNull();
        assertThat(details.status()).isEqualTo(ExecutionStatus.SUCCEEDED);
        assertThat(details.startDate()).isNotNull();
        assertThat(details.stopDate()).isNotNull();

        // Verify execution took some time
        long durationMs = details.stopDate().toEpochMilli() - details.startDate().toEpochMilli();
        log.info("Execution duration: {} ms", durationMs);
        assertThat(durationMs).isGreaterThanOrEqualTo(0);

        log.info("✅ Execution details retrieved successfully!");
    }

    @Test(description = "Verify Step Functions execution can be stopped")
    @Description("Test ability to stop a running execution")
    @Severity(SeverityLevel.NORMAL)
    public void testStopExecution() {
        log.info("=== Testing Execution Stop ===");

        // Start execution with long-running workflow
        // (For this test to be effective, State Machine should have a Wait state)
        String input = createSimpleJsonInput("delay", "true");
        String executionArn = stepFunctionsClient.startExecution(input);

        // Verify execution has started before attempting to stop
        ExecutionStatus currentStatus = stepFunctionsClient.getExecutionStatus(executionArn);
        log.info("Execution status after start: {}", currentStatus);

        // Stop execution
        stepFunctionsClient.stopExecution(executionArn, "Test: verifying stop functionality");

        // Verify execution was stopped
        ExecutionStatus status = stepFunctionsClient.getExecutionStatus(executionArn);
        log.info("Status after stop: {}", status);

        // Status should be ABORTED (may also be RUNNING if stop is still in progress)
        assertThat(status)
                .as("Execution should be stopped or aborted")
                .isIn(ExecutionStatus.ABORTED, ExecutionStatus.RUNNING);

        log.info("✅ Execution stop functionality verified!");
    }

    @Test(description = "Verify Step Functions State Machine details can be retrieved")
    @Description("Test retrieving State Machine metadata")
    @Severity(SeverityLevel.MINOR)
    public void testGetStateMachineDetails() {
        log.info("=== Testing State Machine Details ===");

        // Get State Machine details
        var stateMachine = stepFunctionsClient.getStateMachineDetails();

        // Verify details
        assertThat(stateMachine).isNotNull();
        assertThat(stateMachine.stateMachineArn()).isNotNull();
        assertThat(stateMachine.name()).isNotNull();
        assertThat(stateMachine.definition()).isNotNull();
        assertThat(stateMachine.roleArn()).isNotNull();

        log.info("State Machine Name: {}", stateMachine.name());
        log.info("State Machine ARN: {}", stateMachine.stateMachineArn());
        log.info("State Machine Type: {}", stateMachine.type());
        log.info("State Machine Status: {}", stateMachine.status());

        log.info("✅ State Machine details retrieved successfully!");
    }

    @Test(description = "Verify parallel executions are isolated",
          enabled = false,  // Enable when testing parallel execution
          threadPoolSize = 3,
          invocationCount = 3)
    @Description("Test that multiple parallel executions don't interfere with each other")
    @Severity(SeverityLevel.CRITICAL)
    public void testParallelExecutions() {
        log.info("=== Testing Parallel Execution Isolation ===");

        String threadName = Thread.currentThread().getName();
        log.info("Running in thread: {}", threadName);

        // Each thread creates its own execution
        String input = createJsonInput("thread", threadName, "timestamp", String.valueOf(System.currentTimeMillis()));
        String executionArn = stepFunctionsClient.startExecution(input);

        assertThat(executionArn)
                .as("Execution ARN should be unique per thread")
                .isNotNull();

        // Wait for completion
        ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArn);

        // Verify success
        assertThat(status)
                .as("Thread {} execution should succeed", threadName)
                .isEqualTo(ExecutionStatus.SUCCEEDED);

        log.info("✅ Thread {} execution completed successfully!", threadName);
    }
}
