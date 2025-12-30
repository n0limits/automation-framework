package com.automation.performance;

import lombok.Getter;

/**
 * Performance Test Result
 * Holds the results of a performance test execution
 *
 * @author Victor Grozev
 */
@Getter
public class PerformanceResult {
    private final int totalRequests;
    private final int successfulRequests;
    private final int failedRequests;
    private final long totalDuration;
    private final MetricsCollector metricsCollector;

    public PerformanceResult(int totalRequests,
                             int successfulRequests,
                             int failedRequests,
                             long totalDuration,
                             MetricsCollector metricsCollector) {
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.totalDuration = totalDuration;
        this.metricsCollector = metricsCollector;
    }

    /**
     * Get success rate percentage
     *
     * @return Success rate
     */
    public double getSuccessRate() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (successfulRequests * 100.0) / totalRequests;
    }

    /**
     * Get failure rate percentage
     *
     * @return Failure rate
     */
    public double getFailureRate() {
        return 100.0 - getSuccessRate();
    }

    /**
     * Get throughput (requests per second)
     *
     * @return Throughput
     */
    public double getThroughput() {
        if (totalDuration == 0) {
            return 0.0;
        }
        return (totalRequests * 1000.0) / totalDuration;
    }

    /**
     * Get average response time
     *
     * @return Average response time in milliseconds
     */
    public double getAverageResponseTime() {
        return metricsCollector.getAverageResponseTime();
    }

    /**
     * Get minimum response time
     *
     * @return Minimum response time in milliseconds
     */
    public long getMinResponseTime() {
        return metricsCollector.getMinResponseTime();
    }

    /**
     * Get maximum response time
     *
     * @return Maximum response time in milliseconds
     */
    public long getMaxResponseTime() {
        return metricsCollector.getMaxResponseTime();
    }

    /**
     * Get P95 response time
     *
     * @return P95 response time in milliseconds
     */
    public double getP95ResponseTime() {
        return metricsCollector.getP95();
    }

    /**
     * Get P99 response time
     *
     * @return P99 response time in milliseconds
     */
    public double getP99ResponseTime() {
        return metricsCollector.getP99();
    }

    /**
     * Get standard deviation
     *
     * @return Standard deviation of response times
     */
    public double getStandardDeviation() {
        return metricsCollector.getStandardDeviation();
    }

    @Override
    public String toString() {
        return String.format(
                "PerformanceResult{total=%d, success=%d, failed=%d, successRate=%.2f%%, " +
                        "duration=%dms, throughput=%.2f req/s, avgResponseTime=%.2fms, " +
                        "p95=%.2fms, p99=%.2fms}",
                totalRequests,
                successfulRequests,
                failedRequests,
                getSuccessRate(),
                totalDuration,
                getThroughput(),
                getAverageResponseTime(),
                getP95ResponseTime(),
                getP99ResponseTime()
        );
    }
}
