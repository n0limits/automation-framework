package com.automation.performance;

import com.automation.api.TradingAPIClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * API Performance Tests
 * Tests cover:
 * - Response time benchmarks
 * - Load testing
 * - Spike testing
 * - Throughput validation
 * - SLA compliance
 *
 * @author Victor Grozev
 */
@Slf4j
@Feature("API Performance Testing")
public class APIPerformanceTests extends PerformanceTestBase {
    private TradingAPIClient apiClient;

    @BeforeMethod
    @Override
    public void setupPerformanceTest() {
        super.setupPerformanceTest();
        apiClient = new TradingAPIClient();
        log.info("TradingAPIClient initialized for performance testing");
    }

    @Test(description = "Verify API response time under normal load")
    @Description("Test API responds within acceptable time under normal load conditions")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Response Time")
    public void testAPIResponseTimeUnderLoad() {
        log.info("=== Test: API Response Time Under Load ===");

        // Configure performance thresholds
        setPerformanceThresholds(
                500,    // Max avg response time: 500ms
                1000,   // Max P95: 1s
                2000,   // Max P99: 2s
                99.0,   // Min success rate: 99%
                10.0    // Min throughput: 10 req/s
        );

        // Execute load test
        PerformanceResult result = executeLoadTest(
                () -> {
                    Response response = apiClient.getTradingPair("BTCUSD");
                    return response.getStatusCode() == 200;
                },
                10,  // 10 concurrent users
                10   // 10 iterations each = 100 total requests
        );

        // Verify performance metrics
        assertThat(result.getSuccessRate())
                .as("Success rate should be high")
                .isGreaterThan(95.0);

        assertThat(result.getAverageResponseTime())
                .as("Average response time should be acceptable")
                .isLessThan(1000.0);

        // Validate against SLA
        boolean meetsSLA = validatePerformanceSLA(result);
        assertThat(meetsSLA)
                .as("Performance should meet SLA requirements")
                .isTrue();

        log.info("[PASS] API response time test passed");
    }

    @Test(description = "Verify API throughput")
    @Description("Test API can handle expected throughput")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Throughput")
    public void testAPIThroughput() {
        log.info("=== Test: API Throughput ===");

        // Execute load test with higher concurrency
        PerformanceResult result = executeLoadTest(
                () -> {
                    Response response = apiClient.getTickerPrice("BTCUSD");
                    return response.getStatusCode() == 200;
                },
                20,  // 20 concurrent users
                25   // 25 iterations each = 500 total requests
        );

        log.info("Throughput: {:.2f} requests/second", result.getThroughput());
        log.info("Throughput: {:.2f} requests/minute", result.getThroughput() * 60);

        // Verify throughput meets minimum requirement
        assertThat(result.getThroughput())
                .as("Throughput should meet minimum requirement")
                .isGreaterThan(5.0); // At least 5 requests/second

        assertThat(result.getSuccessRate())
                .as("Success rate should remain high under load")
                .isGreaterThan(95.0);

        log.info("[PASS] API throughput test passed");
    }

    @Test(description = "Verify API handles spike in traffic")
    @Description("Test API performance during traffic spike")
    @Severity(SeverityLevel.NORMAL)
    @Story("Spike Testing")
    public void testAPISpike() {
        log.info("=== Test: API Spike Test ===");

        // Execute spike test
        PerformanceResult result = executeSpikeTest(
                () -> {
                    Response response = apiClient.getTradingPair("ETHUSD");
                    return response.getStatusCode() == 200;
                },
                5,   // Normal load: 5 users
                25,  // Spike load: 25 users
                5,   // Normal duration: 5 seconds
                10   // Spike duration: 10 seconds
        );

        // During spike, we may have higher response times but should still succeed
        assertThat(result.getSuccessRate())
                .as("Success rate should remain acceptable during spike")
                .isGreaterThan(90.0);

        assertThat(result.getP95ResponseTime())
                .as("P95 response time should be reasonable during spike")
                .isLessThan(5000.0);

        log.info("[PASS] API spike test passed");
    }

    @Test(description = "Verify API handles ramped load")
    @Description("Test API performance as load gradually increases")
    @Severity(SeverityLevel.NORMAL)
    @Story("Ramp Testing")
    public void testAPIRampedLoad() {
        log.info("=== Test: API Ramped Load ===");

        // Execute ramped load test
        PerformanceResult result = executeRampedLoadTest(
                () -> {
                    Response response = apiClient.getMarketDepth("BTCUSD", 100);
                    return response.getStatusCode() == 200;
                },
                5,   // Start with 5 users
                20,  // Ramp up to 20 users
                10,  // Over 10 seconds
                5    // 5 iterations per user
        );

        assertThat(result.getSuccessRate())
                .as("Success rate should remain high during ramp")
                .isGreaterThan(95.0);

        // Response time may increase slightly as load increases
        assertThat(result.getAverageResponseTime())
                .as("Average response time should still be reasonable")
                .isLessThan(2000.0);

        log.info("[PASS] API ramped load test passed");
    }

    @Test(description = "Verify different API endpoints performance")
    @Description("Test performance across multiple API endpoints")
    @Severity(SeverityLevel.NORMAL)
    @Story("Multi-Endpoint Testing")
    public void testMultipleEndpointsPerformance() {
        log.info("=== Test: Multiple Endpoints Performance ===");

        // Test getTradingPair endpoint
        log.info("Testing getTradingPair endpoint");
        PerformanceResult result1 = executeLoadTest(
                () -> {
                    Response response = apiClient.getTradingPair("BTCUSD");
                    return response.getStatusCode() == 200;
                },
                10, 10
        );

        log.info("getTradingPair - Avg: {:.2f}ms, P95: {:.2f}ms",
                result1.getAverageResponseTime(), result1.getP95ResponseTime());

        // Test getTickerPrice endpoint
        log.info("Testing getTickerPrice endpoint");
        PerformanceResult result2 = executeLoadTest(
                () -> {
                    Response response = apiClient.getTickerPrice("BTCUSD");
                    return response.getStatusCode() == 200;
                },
                10, 10
        );

        log.info("getTickerPrice - Avg: {:.2f}ms, P95: {:.2f}ms",
                result2.getAverageResponseTime(), result2.getP95ResponseTime());

        // Test getMarketDepth endpoint
        log.info("Testing getMarketDepth endpoint");
        PerformanceResult result3 = executeLoadTest(
                () -> {
                    Response response = apiClient.getMarketDepth("BTCUSD", 100);
                    return response.getStatusCode() == 200;
                },
                10, 10
        );

        log.info("getMarketDepth - Avg: {:.2f}ms, P95: {:.2f}ms",
                result3.getAverageResponseTime(), result3.getP95ResponseTime());

        // All endpoints should perform reasonably
        assertThat(result1.getAverageResponseTime()).isLessThan(1000.0);
        assertThat(result2.getAverageResponseTime()).isLessThan(1000.0);
        assertThat(result3.getAverageResponseTime()).isLessThan(1000.0);

        log.info("[PASS] Multiple endpoints performance test passed");
    }

    @Test(description = "Verify API performance consistency")
    @Description("Test API maintains consistent performance over time")
    @Severity(SeverityLevel.MINOR)
    @Story("Consistency Testing")
    public void testAPIPerformanceConsistency() {
        log.info("=== Test: API Performance Consistency ===");

        // Run multiple test iterations
        int iterations = 5;
        double[] averageResponseTimes = new double[iterations];

        for (int i = 0; i < iterations; i++) {
            log.info("Running iteration {}/{}", i + 1, iterations);

            PerformanceResult result = executeLoadTest(
                    () -> {
                        Response response = apiClient.getTradingPair("BTCUSD");
                        return response.getStatusCode() == 200;
                    },
                    5, 10
            );

            averageResponseTimes[i] = result.getAverageResponseTime();
            log.info("Iteration {} - Avg Response Time: {}ms",
                    i + 1, String.format("%.2f", averageResponseTimes[i]));
        }

        // Calculate variation
        double mean = 0;
        for (double time : averageResponseTimes) {
            mean += time;
        }
        mean /= iterations;

        double variance = 0;
        for (double time : averageResponseTimes) {
            variance += Math.pow(time - mean, 2);
        }
        variance /= iterations;
        double stdDev = Math.sqrt(variance);

        log.info("Consistency Metrics:");
        log.info("  Mean: {:.2f}ms", mean);
        log.info("  Std Dev: {:.2f}ms", stdDev);
        log.info("  Coefficient of Variation: {:.2f}%", (stdDev / mean) * 100);

        // Standard deviation should be relatively small (< 30% of mean)
        assertThat(stdDev / mean)
                .as("Performance should be consistent (low variation)")
                .isLessThan(0.3);

        log.info("[PASS] API performance consistency test passed");
    }

    @Test(description = "Verify API error rate under load")
    @Description("Test API error rate remains low under heavy load")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Error Rate")
    public void testAPIErrorRateUnderLoad() {
        log.info("=== Test: API Error Rate Under Load ===");

        PerformanceResult result = executeLoadTest(
                () -> {
                    try {
                        Response response = apiClient.getTradingPairs();
                        return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
                    } catch (Exception e) {
                        log.error("Request failed: {}", e.getMessage());
                        return false;
                    }
                },
                15,  // 15 concurrent users
                20   // 20 iterations = 300 requests
        );

        // Error rate should be very low
        assertThat(result.getFailureRate())
                .as("Error rate should be minimal")
                .isLessThan(5.0);

        assertThat(result.getSuccessRate())
                .as("Success rate should be high")
                .isGreaterThan(95.0);

        log.info("Error Rate: {:.2f}%", result.getFailureRate());
        log.info("Success Rate: {:.2f}%", result.getSuccessRate());

        log.info("[PASS] API error rate test passed");
    }

    @Test(description = "Verify API percentile response times")
    @Description("Test API P50, P95, P99 response times meet SLA")
    @Severity(SeverityLevel.NORMAL)
    @Story("Percentiles")
    public void testAPIPercentileResponseTimes() {
        log.info("=== Test: API Percentile Response Times ===");

        PerformanceResult result = executeLoadTest(
                () -> {
                    Response response = apiClient.getTradingPair("BTCUSD");
                    return response.getStatusCode() == 200;
                },
                10, 50  // 500 requests for good percentile data
        );

        double p50 = metricsCollector.getP50();
        double p95 = result.getP95ResponseTime();
        double p99 = result.getP99ResponseTime();

        log.info("Percentile Response Times:");
        log.info("  P50 (Median): {:.2f}ms", p50);
        log.info("  P95: {:.2f}ms", p95);
        log.info("  P99: {:.2f}ms", p99);

        // Verify percentiles meet SLA
        assertThat(p50).as("P50 should be fast").isLessThan(500.0);
        assertThat(p95).as("P95 should be acceptable").isLessThan(1500.0);
        assertThat(p99).as("P99 should be reasonable").isLessThan(3000.0);

        log.info("[PASS] API percentile response times test passed");
    }
}
