package com.automation.aws;

import com.automation.config.TestConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

import java.time.Instant;
import java.util.Map;

/**
 * High-level wrapper for AWS Step Functions operations
 * Provides simplified API for test automation
 *
 * Features:
 * - Start Step Functions executions
 * - Poll execution status with timeout
 * - Retrieve execution results
 * - Allure reporting integration
 * - Error handling and logging
 *
 * Usage:
 * <pre>
 * StepFunctionsClient sfnClient = new StepFunctionsClient();
 * String executionArn = sfnClient.startExecution("input-data");
 * ExecutionStatus status = sfnClient.waitForCompletion(executionArn);
 * String output = sfnClient.getExecutionOutput(executionArn);
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class StepFunctionsClient {
    private final SfnClient sfnClient;
    private final TestConfig config;
    private final StepFunctionsExecutionPoller poller;

    /**
     * Create Step Functions client with default configuration
     */
    public StepFunctionsClient() {
        this.sfnClient = AWSClientManager.getStepFunctionsClient();
        this.config = TestConfig.getInstance();
        this.poller = new StepFunctionsExecutionPoller(sfnClient);
    }

    /**
     * Start a Step Functions execution with JSON input
     *
     * @param input JSON input for execution
     * @return Execution ARN
     */
    @Step("Start Step Functions execution with input: {input}")
    public String startExecution(String input) {
        return startExecution(generateExecutionName(), input);
    }

    /**
     * Start a Step Functions execution with custom name and JSON input
     *
     * @param executionName Unique execution name
     * @param input         JSON input for execution
     * @return Execution ARN
     */
    @Step("Start Step Functions execution '{executionName}' with input: {input}")
    public String startExecution(String executionName, String input) {
        try {
            StartExecutionRequest request = StartExecutionRequest.builder()
                    .stateMachineArn(config.getAwsStepFunctionsStateMachineArn())
                    .name(executionName)
                    .input(input)
                    .build();

            log.info("Starting Step Functions execution: {}", executionName);
            log.debug("State Machine ARN: {}", config.getAwsStepFunctionsStateMachineArn());
            log.debug("Input: {}", input);

            StartExecutionResponse response = sfnClient.startExecution(request);
            String executionArn = response.executionArn();

            log.info("Execution started successfully. ARN: {}", executionArn);
            return executionArn;
        } catch (SfnException e) {
            log.error("Failed to start Step Functions execution: {}", executionName, e);
            throw new RuntimeException("Step Functions execution failed to start", e);
        }
    }

    /**
     * Start execution with Map input (will be converted to JSON)
     *
     * @param input Map input data
     * @return Execution ARN
     */
    @Step("Start Step Functions execution with Map input")
    public String startExecution(Map<String, Object> input) {
        String jsonInput = convertMapToJson(input);
        return startExecution(jsonInput);
    }

    /**
     * Wait for execution to complete (SUCCEEDED or FAILED)
     * Uses configured timeout and poll interval
     *
     * @param executionArn Execution ARN to monitor
     * @return Final execution status
     */
    @Step("Wait for Step Functions execution to complete: {executionArn}")
    public ExecutionStatus waitForCompletion(String executionArn) {
        return poller.pollUntilComplete(
                executionArn,
                config.getAwsStepFunctionsExecutionTimeout(),
                config.getAwsStepFunctionsPollInterval()
        );
    }

    /**
     * Wait for execution to complete with custom timeout
     *
     * @param executionArn Execution ARN to monitor
     * @param timeoutMs    Timeout in milliseconds
     * @return Final execution status
     */
    @Step("Wait for Step Functions execution (timeout: {timeoutMs}ms): {executionArn}")
    public ExecutionStatus waitForCompletion(String executionArn, long timeoutMs) {
        return poller.pollUntilComplete(
                executionArn,
                timeoutMs,
                config.getAwsStepFunctionsPollInterval()
        );
    }

    /**
     * Get current execution status
     *
     * @param executionArn Execution ARN
     * @return Current execution status
     */
    @Step("Get Step Functions execution status: {executionArn}")
    public ExecutionStatus getExecutionStatus(String executionArn) {
        try {
            DescribeExecutionRequest request = DescribeExecutionRequest.builder()
                    .executionArn(executionArn)
                    .build();

            DescribeExecutionResponse response = sfnClient.describeExecution(request);
            ExecutionStatus status = response.status();

            log.debug("Execution {} status: {}", executionArn, status);
            return status;
        } catch (SfnException e) {
            log.error("Failed to get execution status for: {}", executionArn, e);
            throw new RuntimeException("Failed to get execution status", e);
        }
    }

    /**
     * Get execution output (only available when status is SUCCEEDED)
     *
     * @param executionArn Execution ARN
     * @return Execution output as JSON string
     */
    @Step("Get Step Functions execution output: {executionArn}")
    public String getExecutionOutput(String executionArn) {
        try {
            DescribeExecutionRequest request = DescribeExecutionRequest.builder()
                    .executionArn(executionArn)
                    .build();

            DescribeExecutionResponse response = sfnClient.describeExecution(request);

            if (response.status() != ExecutionStatus.SUCCEEDED) {
                log.warn("Execution {} has not succeeded. Status: {}", executionArn, response.status());
                return null;
            }

            String output = response.output();
            log.info("Retrieved output for execution {}: {}", executionArn, output);
            return output;
        } catch (SfnException e) {
            log.error("Failed to get execution output for: {}", executionArn, e);
            throw new RuntimeException("Failed to get execution output", e);
        }
    }

    /**
     * Get execution error (only available when status is FAILED)
     *
     * @param executionArn Execution ARN
     * @return Error message
     */
    @Step("Get Step Functions execution error: {executionArn}")
    public String getExecutionError(String executionArn) {
        try {
            DescribeExecutionRequest request = DescribeExecutionRequest.builder()
                    .executionArn(executionArn)
                    .build();

            DescribeExecutionResponse response = sfnClient.describeExecution(request);

            if (response.status() != ExecutionStatus.FAILED) {
                log.warn("Execution {} has not failed. Status: {}", executionArn, response.status());
                return null;
            }

            String error = response.error();
            String cause = response.cause();

            log.error("Execution {} failed. Error: {}, Cause: {}", executionArn, error, cause);
            return String.format("Error: %s, Cause: %s", error, cause);
        } catch (SfnException e) {
            log.error("Failed to get execution error for: {}", executionArn, e);
            throw new RuntimeException("Failed to get execution error", e);
        }
    }

    /**
     * Get full execution details
     *
     * @param executionArn Execution ARN
     * @return DescribeExecutionResponse with all details
     */
    @Step("Get Step Functions execution details: {executionArn}")
    public DescribeExecutionResponse getExecutionDetails(String executionArn) {
        try {
            DescribeExecutionRequest request = DescribeExecutionRequest.builder()
                    .executionArn(executionArn)
                    .build();

            return sfnClient.describeExecution(request);
        } catch (SfnException e) {
            log.error("Failed to get execution details for: {}", executionArn, e);
            throw new RuntimeException("Failed to get execution details", e);
        }
    }

    /**
     * Stop a running execution
     *
     * @param executionArn Execution ARN to stop
     */
    @Step("Stop Step Functions execution: {executionArn}")
    public void stopExecution(String executionArn) {
        stopExecution(executionArn, "Stopped by test automation");
    }

    /**
     * Stop a running execution with reason
     *
     * @param executionArn Execution ARN to stop
     * @param reason       Reason for stopping
     */
    @Step("Stop Step Functions execution: {executionArn} (Reason: {reason})")
    public void stopExecution(String executionArn, String reason) {
        try {
            StopExecutionRequest request = StopExecutionRequest.builder()
                    .executionArn(executionArn)
                    .error("ExecutionStoppedByTest")
                    .cause(reason)
                    .build();

            sfnClient.stopExecution(request);
            log.info("Execution {} stopped. Reason: {}", executionArn, reason);
        } catch (SfnException e) {
            log.error("Failed to stop execution: {}", executionArn, e);
            throw new RuntimeException("Failed to stop execution", e);
        }
    }

    /**
     * Get State Machine details
     *
     * @return DescribeStateMachineResponse with state machine details
     */
    @Step("Get State Machine details")
    public DescribeStateMachineResponse getStateMachineDetails() {
        try {
            DescribeStateMachineRequest request = DescribeStateMachineRequest.builder()
                    .stateMachineArn(config.getAwsStepFunctionsStateMachineArn())
                    .build();

            return sfnClient.describeStateMachine(request);
        } catch (SfnException e) {
            log.error("Failed to get state machine details", e);
            throw new RuntimeException("Failed to get state machine details", e);
        }
    }

    /**
     * Generate unique execution name with timestamp
     *
     * @return Unique execution name
     */
    private String generateExecutionName() {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String threadName = Thread.currentThread().getName().replaceAll("[^a-zA-Z0-9-_]", "_");
        return String.format("test-execution-%s-%s", threadName, timestamp);
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert Map to JSON string using Jackson
     *
     * @param map Input map
     * @return JSON string
     */
    private String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert map to JSON", e);
            throw new RuntimeException("Failed to convert map to JSON", e);
        }
    }
}
