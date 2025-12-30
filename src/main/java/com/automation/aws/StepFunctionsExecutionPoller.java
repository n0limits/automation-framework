package com.automation.aws;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.*;

/**
 * Poller for AWS Step Functions execution status
 * Uses exponential backoff strategy for efficient polling
 *
 * Features:
 * - Exponential backoff (avoids excessive API calls)
 * - Configurable timeout
 * - Detailed logging
 * - Allure reporting integration
 * - Handles all execution states
 *
 * Polling Strategy:
 * - Initial poll: immediate
 * - Subsequent polls: exponentially increasing intervals
 * - Max interval: 30 seconds
 * - Backoff multiplier: 2.0
 *
 * Usage:
 * <pre>
 * StepFunctionsExecutionPoller poller = new StepFunctionsExecutionPoller(sfnClient);
 * ExecutionStatus status = poller.pollUntilComplete(executionArn, 300000, 2000);
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class StepFunctionsExecutionPoller {
    private final SfnClient sfnClient;

    // Exponential backoff configuration
    private static final double BACKOFF_MULTIPLIER = 2.0;
    private static final long MAX_POLL_INTERVAL_MS = 30000; // 30 seconds
    private static final long MIN_POLL_INTERVAL_MS = 1000;  // 1 second

    public StepFunctionsExecutionPoller(SfnClient sfnClient) {
        this.sfnClient = sfnClient;
    }

    /**
     * Poll execution until it reaches terminal state (SUCCEEDED, FAILED, TIMED_OUT, ABORTED)
     *
     * @param executionArn   Execution ARN to poll
     * @param timeoutMs      Maximum time to wait (milliseconds)
     * @param initialPollMs  Initial poll interval (milliseconds)
     * @return Final execution status
     * @throws RuntimeException if execution times out or fails
     */
    @Step("Poll Step Functions execution until complete")
    public ExecutionStatus pollUntilComplete(String executionArn, long timeoutMs, long initialPollMs) {
        long startTime = System.currentTimeMillis();
        long currentPollInterval = Math.max(initialPollMs, MIN_POLL_INTERVAL_MS);
        int pollCount = 0;

        log.info("Starting to poll execution: {}", executionArn);
        log.info("Timeout: {} ms, Initial poll interval: {} ms", timeoutMs, currentPollInterval);

        while (true) {
            pollCount++;
            long elapsedTime = System.currentTimeMillis() - startTime;

            // Check if timeout exceeded
            if (elapsedTime > timeoutMs) {
                String errorMsg = String.format(
                        "Execution polling timed out after %d ms (%d polls). Execution ARN: %s",
                        elapsedTime, pollCount, executionArn
                );
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }

            // Get current execution status
            ExecutionStatus status = getExecutionStatus(executionArn);
            log.debug("Poll #{}: Execution status: {} (elapsed: {} ms)",
                     pollCount, status, elapsedTime);

            // Check if execution reached terminal state
            if (isTerminalState(status)) {
                log.info("Execution completed with status: {} (elapsed: {} ms, polls: {})",
                        status, elapsedTime, pollCount);

                // Get and log execution details for terminal states
                logExecutionDetails(executionArn, status);

                return status;
            }

            // Wait before next poll (with exponential backoff)
            try {
                long waitTime = Math.min(currentPollInterval, MAX_POLL_INTERVAL_MS);
                log.debug("Waiting {} ms before next poll...", waitTime);
                Thread.sleep(waitTime);

                // Increase poll interval for next iteration (exponential backoff)
                currentPollInterval = (long) (currentPollInterval * BACKOFF_MULTIPLIER);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            }
        }
    }

    /**
     * Get current execution status
     *
     * @param executionArn Execution ARN
     * @return Current ExecutionStatus
     */
    private ExecutionStatus getExecutionStatus(String executionArn) {
        try {
            DescribeExecutionRequest request = DescribeExecutionRequest.builder()
                    .executionArn(executionArn)
                    .build();

            DescribeExecutionResponse response = sfnClient.describeExecution(request);
            return response.status();
        } catch (SfnException e) {
            log.error("Failed to get execution status for: {}", executionArn, e);
            throw new RuntimeException("Failed to poll execution status", e);
        }
    }

    /**
     * Check if execution status is terminal (won't change anymore)
     *
     * @param status Execution status
     * @return true if terminal state
     */
    private boolean isTerminalState(ExecutionStatus status) {
        return status == ExecutionStatus.SUCCEEDED
                || status == ExecutionStatus.FAILED
                || status == ExecutionStatus.TIMED_OUT
                || status == ExecutionStatus.ABORTED;
    }

    /**
     * Log detailed execution information for terminal states
     *
     * @param executionArn Execution ARN
     * @param status       Final execution status
     */
    private void logExecutionDetails(String executionArn, ExecutionStatus status) {
        try {
            DescribeExecutionRequest request = DescribeExecutionRequest.builder()
                    .executionArn(executionArn)
                    .build();

            DescribeExecutionResponse response = sfnClient.describeExecution(request);

            log.info("========== Execution Details ==========");
            log.info("Execution ARN: {}", executionArn);
            log.info("Status: {}", status);
            log.info("Start Date: {}", response.startDate());
            log.info("Stop Date: {}", response.stopDate());

            if (status == ExecutionStatus.SUCCEEDED && response.output() != null) {
                log.info("Output: {}", response.output());
            }

            if (status == ExecutionStatus.FAILED) {
                log.error("Error: {}", response.error());
                log.error("Cause: {}", response.cause());
            }

            if (status == ExecutionStatus.ABORTED) {
                log.warn("Execution was aborted");
                if (response.error() != null) {
                    log.warn("Abort Error: {}", response.error());
                }
                if (response.cause() != null) {
                    log.warn("Abort Cause: {}", response.cause());
                }
            }

            if (status == ExecutionStatus.TIMED_OUT) {
                log.warn("Execution timed out (State Machine timeout)");
            }

            log.info("======================================");
        } catch (Exception e) {
            log.warn("Failed to log execution details", e);
        }
    }

    /**
     * Calculate total wait time based on initial interval and backoff strategy
     * Useful for estimating timeout values
     *
     * @param initialPollMs Initial poll interval (ms)
     * @param maxPolls      Maximum number of polls
     * @return Estimated total wait time (ms)
     */
    public static long estimateWaitTime(long initialPollMs, int maxPolls) {
        long totalWait = 0;
        long currentInterval = initialPollMs;

        for (int i = 0; i < maxPolls; i++) {
            long waitTime = Math.min(currentInterval, MAX_POLL_INTERVAL_MS);
            totalWait += waitTime;
            currentInterval = (long) (currentInterval * BACKOFF_MULTIPLIER);
        }

        return totalWait;
    }

    /**
     * Calculate recommended number of polls for a given timeout
     *
     * @param timeoutMs     Total timeout (ms)
     * @param initialPollMs Initial poll interval (ms)
     * @return Estimated number of polls
     */
    public static int estimatePollCount(long timeoutMs, long initialPollMs) {
        long totalWait = 0;
        long currentInterval = initialPollMs;
        int polls = 0;

        while (totalWait < timeoutMs) {
            long waitTime = Math.min(currentInterval, MAX_POLL_INTERVAL_MS);
            totalWait += waitTime;
            currentInterval = (long) (currentInterval * BACKOFF_MULTIPLIER);
            polls++;
        }

        return polls;
    }
}
