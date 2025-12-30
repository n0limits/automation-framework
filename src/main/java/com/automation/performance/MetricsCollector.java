package com.automation.performance;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance Metrics Collector
 * Thread-safe collector for performance testing metrics
 *
 * Features:
 * - Response time tracking
 * - Success/failure rate calculation
 * - Percentile calculations (P50, P95, P99)
 * - Min/Max/Average response times
 * - Throughput measurement
 * - Thread-safe operations
 *
 * @author Victor Grozev
 */
@Slf4j
@Getter
public class MetricsCollector {
    private final List<Long> responseTimes;
    private final AtomicInteger totalRequests;
    private final AtomicInteger successfulRequests;
    private final AtomicInteger failedRequests;
    private final AtomicLong totalResponseTime;
    private final AtomicLong minResponseTime;
    private final AtomicLong maxResponseTime;
    private final long startTime;
    private final ConcurrentHashMap<String, AtomicInteger> errorTypes;

    public MetricsCollector() {
        this.responseTimes = Collections.synchronizedList(new ArrayList<>());
        this.totalRequests = new AtomicInteger(0);
        this.successfulRequests = new AtomicInteger(0);
        this.failedRequests = new AtomicInteger(0);
        this.totalResponseTime = new AtomicLong(0);
        this.minResponseTime = new AtomicLong(Long.MAX_VALUE);
        this.maxResponseTime = new AtomicLong(Long.MIN_VALUE);
        this.startTime = System.currentTimeMillis();
        this.errorTypes = new ConcurrentHashMap<>();

        log.debug("MetricsCollector initialized");
    }

    /**
     * Record a request with response time and success status
     *
     * @param responseTime Response time in milliseconds
     * @param success Whether request was successful
     */
    public void recordRequest(long responseTime, boolean success) {
        // Increment counters
        totalRequests.incrementAndGet();
        if (success) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }

        // Track response time
        responseTimes.add(responseTime);
        totalResponseTime.addAndGet(responseTime);

        // Update min/max
        updateMin(responseTime);
        updateMax(responseTime);

        log.trace("Recorded request: {} ms, success: {}", responseTime, success);
    }

    /**
     * Record a request with error type
     *
     * @param responseTime Response time in milliseconds
     * @param success Whether request was successful
     * @param errorType Error type if failed
     */
    public void recordRequest(long responseTime, boolean success, String errorType) {
        recordRequest(responseTime, success);

        if (!success && errorType != null) {
            errorTypes.computeIfAbsent(errorType, k -> new AtomicInteger(0)).incrementAndGet();
        }
    }

    /**
     * Update minimum response time
     *
     * @param responseTime Response time
     */
    private void updateMin(long responseTime) {
        long current;
        do {
            current = minResponseTime.get();
            if (responseTime >= current) {
                return;
            }
        } while (!minResponseTime.compareAndSet(current, responseTime));
    }

    /**
     * Update maximum response time
     *
     * @param responseTime Response time
     */
    private void updateMax(long responseTime) {
        long current;
        do {
            current = maxResponseTime.get();
            if (responseTime <= current) {
                return;
            }
        } while (!maxResponseTime.compareAndSet(current, responseTime));
    }

    /**
     * Get total number of requests
     *
     * @return Total requests
     */
    public int getTotalRequests() {
        return totalRequests.get();
    }

    /**
     * Get successful request count
     *
     * @return Successful requests
     */
    public int getSuccessfulRequests() {
        return successfulRequests.get();
    }

    /**
     * Get failed request count
     *
     * @return Failed requests
     */
    public int getFailedRequests() {
        return failedRequests.get();
    }

    /**
     * Calculate success rate
     *
     * @return Success rate percentage
     */
    public double getSuccessRate() {
        int total = totalRequests.get();
        if (total == 0) {
            return 0.0;
        }
        return (successfulRequests.get() * 100.0) / total;
    }

    /**
     * Calculate failure rate
     *
     * @return Failure rate percentage
     */
    public double getFailureRate() {
        return 100.0 - getSuccessRate();
    }

    /**
     * Get average response time
     *
     * @return Average response time in milliseconds
     */
    public double getAverageResponseTime() {
        int total = totalRequests.get();
        if (total == 0) {
            return 0.0;
        }
        return totalResponseTime.get() / (double) total;
    }

    /**
     * Get minimum response time
     *
     * @return Minimum response time in milliseconds
     */
    public long getMinResponseTime() {
        long min = minResponseTime.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    /**
     * Get maximum response time
     *
     * @return Maximum response time in milliseconds
     */
    public long getMaxResponseTime() {
        long max = maxResponseTime.get();
        return max == Long.MIN_VALUE ? 0 : max;
    }

    /**
     * Calculate percentile response time
     *
     * @param percentile Percentile (e.g., 50, 95, 99)
     * @return Percentile response time in milliseconds
     */
    public double getPercentile(int percentile) {
        if (responseTimes.isEmpty()) {
            return 0.0;
        }

        List<Long> sortedTimes = new ArrayList<>(responseTimes);
        Collections.sort(sortedTimes);

        int index = (int) Math.ceil((percentile / 100.0) * sortedTimes.size()) - 1;
        index = Math.max(0, Math.min(index, sortedTimes.size() - 1));

        return sortedTimes.get(index);
    }

    /**
     * Get P50 (median) response time
     *
     * @return P50 response time
     */
    public double getP50() {
        return getPercentile(50);
    }

    /**
     * Get P95 response time
     *
     * @return P95 response time
     */
    public double getP95() {
        return getPercentile(95);
    }

    /**
     * Get P99 response time
     *
     * @return P99 response time
     */
    public double getP99() {
        return getPercentile(99);
    }

    /**
     * Calculate throughput (requests per second)
     *
     * @return Throughput in requests per second
     */
    public double getThroughput() {
        long duration = System.currentTimeMillis() - startTime;
        if (duration == 0) {
            return 0.0;
        }
        return (totalRequests.get() * 1000.0) / duration;
    }

    /**
     * Calculate requests per minute
     *
     * @return Requests per minute
     */
    public double getRequestsPerMinute() {
        return getThroughput() * 60.0;
    }

    /**
     * Get error type distribution
     *
     * @return Map of error types to counts
     */
    public ConcurrentHashMap<String, AtomicInteger> getErrorTypes() {
        return errorTypes;
    }

    /**
     * Get elapsed time since start
     *
     * @return Elapsed time in milliseconds
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Get all response times
     *
     * @return List of response times
     */
    public List<Long> getResponseTimes() {
        return new ArrayList<>(responseTimes);
    }

    /**
     * Calculate standard deviation of response times
     *
     * @return Standard deviation
     */
    public double getStandardDeviation() {
        if (responseTimes.isEmpty()) {
            return 0.0;
        }

        double mean = getAverageResponseTime();
        double sumSquaredDiff = 0.0;

        for (Long time : responseTimes) {
            double diff = time - mean;
            sumSquaredDiff += diff * diff;
        }

        return Math.sqrt(sumSquaredDiff / responseTimes.size());
    }

    /**
     * Reset all metrics
     */
    public void reset() {
        responseTimes.clear();
        totalRequests.set(0);
        successfulRequests.set(0);
        failedRequests.set(0);
        totalResponseTime.set(0);
        minResponseTime.set(Long.MAX_VALUE);
        maxResponseTime.set(Long.MIN_VALUE);
        errorTypes.clear();

        log.info("Metrics reset");
    }

    /**
     * Get metrics summary
     *
     * @return Metrics summary string
     */
    public String getSummary() {
        return String.format(
                "Metrics Summary: Total=%d, Success=%d (%.2f%%), Failed=%d (%.2f%%), " +
                        "Avg=%.2fms, Min=%dms, Max=%dms, P95=%.2fms, P99=%.2fms, Throughput=%.2f req/s",
                getTotalRequests(),
                getSuccessfulRequests(),
                getSuccessRate(),
                getFailedRequests(),
                getFailureRate(),
                getAverageResponseTime(),
                getMinResponseTime(),
                getMaxResponseTime(),
                getP95(),
                getP99(),
                getThroughput()
        );
    }

    /**
     * Print detailed metrics report
     */
    public void printReport() {
        log.info("===== Performance Metrics Report =====");
        log.info("Execution Time: {} ms", getElapsedTime());
        log.info("");
        log.info("Request Statistics:");
        log.info("  Total Requests: {}", getTotalRequests());
        log.info("  Successful: {}", getSuccessfulRequests());
        log.info("  Failed: {}", getFailedRequests());
        log.info("  Success Rate: {:.2f}%", getSuccessRate());
        log.info("  Failure Rate: {:.2f}%", getFailureRate());
        log.info("");
        log.info("Response Time Statistics:");
        log.info("  Average: {:.2f} ms", getAverageResponseTime());
        log.info("  Minimum: {} ms", getMinResponseTime());
        log.info("  Maximum: {} ms", getMaxResponseTime());
        log.info("  Std Dev: {:.2f} ms", getStandardDeviation());
        log.info("");
        log.info("Percentiles:");
        log.info("  P50 (Median): {:.2f} ms", getP50());
        log.info("  P95: {:.2f} ms", getP95());
        log.info("  P99: {:.2f} ms", getP99());
        log.info("");
        log.info("Throughput:");
        log.info("  Requests/Second: {:.2f}", getThroughput());
        log.info("  Requests/Minute: {:.2f}", getRequestsPerMinute());

        if (!errorTypes.isEmpty()) {
            log.info("");
            log.info("Error Distribution:");
            errorTypes.forEach((type, count) ->
                    log.info("  {}: {}", type, count.get())
            );
        }

        log.info("=====================================");
    }
}
