package com.automation.integration;

import com.automation.api.APIAssertions;
import com.automation.api.TradingAPIClient;
import com.automation.base.BaseAWSTest;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import software.amazon.awssdk.services.sfn.model.ExecutionStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests combining API and AWS Step Functions
 * Demonstrates end-to-end workflow testing
 *
 * Test Scenarios:
 * - Trigger trading workflow via API, monitor via Step Functions
 * - Validate trading workflow execution completes successfully
 * - Verify API and AWS integration works correctly
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("Trading Workflow Integration")
public class TradingWorkflowIntegrationTests extends BaseAWSTest {
    private TradingAPIClient tradingAPI;

    @BeforeMethod
    public void setupIntegration() {
        log.info("===== Setting up Integration Test =====");

        tradingAPI = new TradingAPIClient();
        log.info("TradingAPIClient initialized");
    }

    @Test(description = "Verify API data retrieval and Step Functions workflow execution")
    @Description("Integration test: Get trading pair from API, then execute Step Functions workflow with that data")
    @Severity(SeverityLevel.CRITICAL)
    public void testAPItoStepFunctionsIntegration() {
        log.info("=== Integration Test: API → Step Functions ===");

        // Step 1: Get trading pair data from API
        log.info("Step 1: Fetching trading pair data from API");
        String symbol = "BTCUSD";
        Response apiResponse = tradingAPI.getTradingPair(symbol);

        APIAssertions.assertThat(apiResponse)
                .isOK()
                .hasJsonContentType()
                .respondsWithin(2000)
                .jsonPath("symbol").isEqualTo(symbol);

        log.info("[PASS] API call successful");

        // Step 2: Extract data and prepare Step Functions input
        log.info("Step 2: Preparing Step Functions workflow input");
        String tradingPairData = apiResponse.getBody().asString();
        log.info("Trading pair data: {}", tradingPairData);

        // Create workflow input based on API response
        String workflowInput = String.format("""
                {
                    "symbol": "%s",
                    "action": "ANALYZE",
                    "source": "api_integration_test",
                    "data": %s
                }
                """, symbol, tradingPairData);

        // Step 3: Start Step Functions execution
        log.info("Step 3: Starting Step Functions workflow");
        String executionArn = stepFunctionsClient.startExecution(workflowInput);

        assertThat(executionArn)
                .as("Execution ARN should be returned")
                .isNotNull()
                .contains("execution");

        log.info("Execution ARN: {}", executionArn);

        // Step 4: Wait for workflow completion
        log.info("Step 4: Waiting for workflow completion");
        ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArn);

        // Step 5: Verify workflow succeeded
        log.info("Step 5: Verifying workflow results");
        assertThat(status)
                .as("Workflow should complete successfully")
                .isEqualTo(ExecutionStatus.SUCCEEDED);

        String output = stepFunctionsClient.getExecutionOutput(executionArn);
        log.info("Workflow output: {}", output);

        assertThat(output)
                .as("Workflow should return output")
                .isNotNull();

        log.info("[PASS] API → Step Functions integration test passed!");
    }

    @Test(description = "Verify parallel API calls trigger parallel Step Functions workflows")
    @Description("Integration test: Multiple API calls trigger multiple workflows that execute in parallel")
    @Severity(SeverityLevel.NORMAL)
    public void testParallelAPIAndWorkflowExecution() {
        log.info("=== Integration Test: Parallel API & Workflow Execution ===");

        String[] symbols = {"BTCUSD", "ETHUSD", "XRPUSD"};
        String[] executionArns = new String[symbols.length];

        // Step 1: Fetch data from API and start workflows for all symbols
        log.info("Step 1: Starting workflows for {} symbols", symbols.length);
        for (int i = 0; i < symbols.length; i++) {
            String symbol = symbols[i];

            // Get data from API
            Response apiResponse = tradingAPI.getTradingPair(symbol);
            APIAssertions.assertThat(apiResponse).isOK();

            // Start workflow
            String input = String.format("""
                    {
                        "symbol": "%s",
                        "action": "PARALLEL_TEST",
                        "index": %d
                    }
                    """, symbol, i);

            executionArns[i] = stepFunctionsClient.startExecution(input);
            log.info("Started workflow {} for {}", i + 1, symbol);
        }

        // Step 2: Wait for all workflows to complete
        log.info("Step 2: Waiting for all {} workflows to complete", symbols.length);
        for (int i = 0; i < executionArns.length; i++) {
            ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArns[i]);

            assertThat(status)
                    .as("Workflow %d (%s) should succeed", i + 1, symbols[i])
                    .isEqualTo(ExecutionStatus.SUCCEEDED);

            log.info("[PASS] Workflow {} ({}) completed successfully", i + 1, symbols[i]);
        }

        log.info("[PASS] Parallel execution integration test passed!");
    }

    @Test(description = "Verify API error triggers appropriate Step Functions error handling")
    @Description("Integration test: API error scenario should be handled gracefully in workflow")
    @Severity(SeverityLevel.NORMAL)
    public void testAPIErrorHandlingInWorkflow() {
        log.info("=== Integration Test: API Error → Workflow Error Handling ===");

        // Step 1: Make API call that will fail (invalid symbol)
        log.info("Step 1: Making API call with invalid symbol");
        String invalidSymbol = "INVALID123";
        Response apiResponse = tradingAPI.getTradingPair(invalidSymbol);

        APIAssertions.assertThat(apiResponse).isNotFound();
        log.info("[PASS] API returned expected error (404)");

        // Step 2: Start workflow with error handling scenario
        log.info("Step 2: Starting workflow with error handling");
        String errorInput = String.format("""
                {
                    "symbol": "%s",
                    "action": "ERROR_TEST",
                    "expectedError": true
                }
                """, invalidSymbol);

        String executionArn = stepFunctionsClient.startExecution(errorInput);

        // Step 3: Wait for workflow completion
        // (Workflow should handle error gracefully - may succeed or fail depending on implementation)
        log.info("Step 3: Waiting for error handling workflow");
        ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArn);

        log.info("Workflow completed with status: {}", status);

        // Verify workflow either:
        // a) Succeeded (error was handled gracefully), OR
        // b) Failed (error was properly reported)
        assertThat(status)
                .as("Workflow should reach terminal state")
                .isIn(ExecutionStatus.SUCCEEDED, ExecutionStatus.FAILED);

        if (status == ExecutionStatus.FAILED) {
            String error = stepFunctionsClient.getExecutionError(executionArn);
            log.info("Workflow failed as expected. Error: {}", error);
        }

        log.info("[PASS] Error handling integration test passed!");
    }

    @Test(description = "Verify complete trading workflow: API → Validation → Step Functions → Result")
    @Description("End-to-end test of complete trading workflow with all components")
    @Severity(SeverityLevel.CRITICAL)
    public void testCompleteTradingWorkflow() {
        log.info("=== Integration Test: Complete Trading Workflow ===");

        String symbol = "BTCUSD";

        // Phase 1: Pre-workflow API validation
        log.info("Phase 1: Validating trading pair via API");
        Response pairResponse = tradingAPI.getTradingPair(symbol);
        APIAssertions.assertThat(pairResponse)
                .isOK()
                .jsonPath("symbol").isEqualTo(symbol);

        Response tickerResponse = tradingAPI.getTickerPrice(symbol);
        APIAssertions.assertThat(tickerResponse)
                .isOK()
                .respondsWithin(1000);

        double currentPrice = tickerResponse.jsonPath().getDouble("price");
        log.info("Current price for {}: {}", symbol, currentPrice);

        // Phase 2: Execute trading workflow via Step Functions
        log.info("Phase 2: Executing trading workflow");
        String workflowInput = String.format("""
                {
                    "symbol": "%s",
                    "action": "COMPLETE_WORKFLOW_TEST",
                    "price": %s,
                    "timestamp": %d
                }
                """, symbol, currentPrice, System.currentTimeMillis());

        String executionArn = stepFunctionsClient.startExecution(workflowInput);
        log.info("Workflow started: {}", executionArn);

        // Phase 3: Monitor workflow execution
        log.info("Phase 3: Monitoring workflow execution");
        ExecutionStatus status = stepFunctionsClient.waitForCompletion(executionArn);

        assertThat(status)
                .as("Complete workflow should succeed")
                .isEqualTo(ExecutionStatus.SUCCEEDED);

        String output = stepFunctionsClient.getExecutionOutput(executionArn);
        log.info("Workflow output: {}", output);

        // Phase 4: Post-workflow validation (if applicable)
        log.info("Phase 4: Post-workflow validation");
        // Could verify state changes via API here if workflow modified data

        log.info("[PASS] Complete trading workflow integration test passed!");
    }

    @Test(description = "Verify performance of API + Step Functions integration")
    @Description("Performance test: Measure end-to-end latency of integrated workflow")
    @Severity(SeverityLevel.MINOR)
    public void testIntegrationPerformance() {
        log.info("=== Integration Test: Performance Measurement ===");

        long startTime = System.currentTimeMillis();

        // API call
        String symbol = "BTCUSD";
        Response apiResponse = tradingAPI.getTradingPair(symbol);
        APIAssertions.assertThat(apiResponse).isOK();

        long apiTime = System.currentTimeMillis() - startTime;
        log.info("API call time: {} ms", apiTime);

        // Step Functions execution
        long workflowStartTime = System.currentTimeMillis();
        String input = String.format("{\"symbol\":\"%s\",\"action\":\"PERFORMANCE_TEST\"}", symbol);
        String executionArn = stepFunctionsClient.startExecution(input);
        stepFunctionsClient.waitForCompletion(executionArn);

        long workflowTime = System.currentTimeMillis() - workflowStartTime;
        log.info("Workflow execution time: {} ms", workflowTime);

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Total integration time: {} ms", totalTime);

        // Performance assertions (adjust thresholds based on requirements)
        assertThat(apiTime)
                .as("API call should be fast")
                .isLessThan(3000);

        assertThat(totalTime)
                .as("Total integration time should be reasonable")
                .isLessThan(60000);  // 1 minute max

        log.info("[PASS] Performance integration test passed!");
    }
}
