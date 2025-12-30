package com.automation.reporting;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test Execution Metrics Collector
 * Collects and analyzes test execution metrics for reporting and analytics
 *
 * Features:
 * - Test execution statistics
 * - Test duration tracking
 * - Failure analysis
 * - Test categorization
 * - Trend analysis support
 * - Real-time metrics
 *
 * @author Victor Grozev
 */
@Slf4j
@Getter
public class TestMetrics {
    private static final TestMetrics INSTANCE = new TestMetrics();

    private final AtomicInteger totalTests = new AtomicInteger(0);
    private final AtomicInteger passedTests = new AtomicInteger(0);
    private final AtomicInteger failedTests = new AtomicInteger(0);
    private final AtomicInteger skippedTests = new AtomicInteger(0);
    private final AtomicLong totalDuration = new AtomicLong(0);

    private final ConcurrentHashMap<String, AtomicInteger> testsByCategory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> failureReasons = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TestResult> testResults = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Long>> testDurations = new ConcurrentHashMap<>();

    private final LocalDateTime executionStart = LocalDateTime.now();

    private TestMetrics() {
        log.info("TestMetrics instance created");
    }

    public static TestMetrics getInstance() {
        return INSTANCE;
    }

    /**
     * Record test start
     *
     * @param testName Test name
     */
    public void recordTestStart(String testName) {
        totalTests.incrementAndGet();
        TestResult result = new TestResult(testName);
        testResults.put(testName, result);
        log.debug("Test started: {}", testName);
    }

    /**
     * Record test pass
     *
     * @param testName Test name
     * @param duration Duration in milliseconds
     */
    public void recordTestPass(String testName, long duration) {
        passedTests.incrementAndGet();
        totalDuration.addAndGet(duration);
        recordDuration(testName, duration);

        TestResult result = testResults.get(testName);
        if (result != null) {
            result.setPassed(true);
            result.setDuration(duration);
        }

        log.debug("Test passed: {} ({}ms)", testName, duration);
    }

    /**
     * Record test failure
     *
     * @param testName Test name
     * @param duration Duration in milliseconds
     * @param failureReason Failure reason
     */
    public void recordTestFailure(String testName, long duration, String failureReason) {
        failedTests.incrementAndGet();
        totalDuration.addAndGet(duration);
        recordDuration(testName, duration);

        TestResult result = testResults.get(testName);
        if (result != null) {
            result.setPassed(false);
            result.setDuration(duration);
            result.setFailureReason(failureReason);
        }

        // Track failure reasons
        failureReasons.computeIfAbsent(failureReason, k -> new AtomicInteger(0)).incrementAndGet();

        log.debug("Test failed: {} ({}ms) - {}", testName, duration, failureReason);
    }

    /**
     * Record test skip
     *
     * @param testName Test name
     * @param skipReason Skip reason
     */
    public void recordTestSkip(String testName, String skipReason) {
        skippedTests.incrementAndGet();

        TestResult result = testResults.get(testName);
        if (result != null) {
            result.setSkipped(true);
            result.setSkipReason(skipReason);
        }

        log.debug("Test skipped: {} - {}", testName, skipReason);
    }

    /**
     * Record test category
     *
     * @param category Category name
     */
    public void recordCategory(String category) {
        testsByCategory.computeIfAbsent(category, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * Record test duration
     *
     * @param testName Test name
     * @param duration Duration in milliseconds
     */
    private void recordDuration(String testName, long duration) {
        testDurations.computeIfAbsent(testName, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(duration);
    }

    // ========== Calculated Metrics ==========

    /**
     * Get pass rate percentage
     *
     * @return Pass rate
     */
    public double getPassRate() {
        int total = totalTests.get();
        if (total == 0) {
            return 0.0;
        }
        return (passedTests.get() * 100.0) / total;
    }

    /**
     * Get failure rate percentage
     *
     * @return Failure rate
     */
    public double getFailureRate() {
        int total = totalTests.get();
        if (total == 0) {
            return 0.0;
        }
        return (failedTests.get() * 100.0) / total;
    }

    /**
     * Get skip rate percentage
     *
     * @return Skip rate
     */
    public double getSkipRate() {
        int total = totalTests.get();
        if (total == 0) {
            return 0.0;
        }
        return (skippedTests.get() * 100.0) / total;
    }

    /**
     * Get average test duration
     *
     * @return Average duration in milliseconds
     */
    public double getAverageDuration() {
        int executed = passedTests.get() + failedTests.get();
        if (executed == 0) {
            return 0.0;
        }
        return totalDuration.get() / (double) executed;
    }

    /**
     * Get total execution time
     *
     * @return Total execution time in milliseconds
     */
    public long getTotalExecutionTime() {
        return totalDuration.get();
    }

    /**
     * Get tests by category
     *
     * @return Map of category to test count
     */
    public Map<String, Integer> getTestsByCategory() {
        Map<String, Integer> result = new HashMap<>();
        testsByCategory.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }

    /**
     * Get failure reasons distribution
     *
     * @return Map of failure reason to count
     */
    public Map<String, Integer> getFailureReasons() {
        Map<String, Integer> result = new HashMap<>();
        failureReasons.forEach((k, v) -> result.put(k, v.get()));
        return result;
    }

    /**
     * Get top failures
     *
     * @param limit Number of top failures to return
     * @return List of top failure reasons
     */
    public List<Map.Entry<String, Integer>> getTopFailures(int limit) {
        return getFailureReasons().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .toList();
    }

    /**
     * Get slowest tests
     *
     * @param limit Number of slowest tests to return
     * @return List of slowest tests with average duration
     */
    public List<Map.Entry<String, Double>> getSlowestTests(int limit) {
        Map<String, Double> avgDurations = new HashMap<>();

        testDurations.forEach((testName, durations) -> {
            double avg = durations.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            avgDurations.put(testName, avg);
        });

        return avgDurations.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .toList();
    }

    /**
     * Get execution duration (from start to now)
     *
     * @return Duration in milliseconds
     */
    public long getExecutionDuration() {
        return java.time.Duration.between(executionStart, LocalDateTime.now()).toMillis();
    }

    /**
     * Generate metrics summary
     *
     * @return Metrics summary string
     */
    public String getSummary() {
        return String.format(
                "Test Execution Summary: Total=%d, Passed=%d (%.2f%%), Failed=%d (%.2f%%), Skipped=%d (%.2f%%), " +
                        "Avg Duration=%.2fms, Total Duration=%dms",
                totalTests.get(),
                passedTests.get(), getPassRate(),
                failedTests.get(), getFailureRate(),
                skippedTests.get(), getSkipRate(),
                getAverageDuration(),
                getTotalExecutionTime()
        );
    }

    /**
     * Print detailed metrics report
     */
    public void printReport() {
        log.info("===== Test Execution Metrics Report =====");
        log.info("Execution Start: {}", executionStart);
        log.info("Execution Duration: {} ms", getExecutionDuration());
        log.info("");
        log.info("Test Statistics:");
        log.info("  Total Tests: {}", totalTests.get());
        log.info("  Passed: {} ({:.2f}%)", passedTests.get(), getPassRate());
        log.info("  Failed: {} ({:.2f}%)", failedTests.get(), getFailureRate());
        log.info("  Skipped: {} ({:.2f}%)", skippedTests.get(), getSkipRate());
        log.info("");
        log.info("Execution Time:");
        log.info("  Total: {} ms", getTotalExecutionTime());
        log.info("  Average per Test: {:.2f} ms", getAverageDuration());

        if (!testsByCategory.isEmpty()) {
            log.info("");
            log.info("Tests by Category:");
            testsByCategory.forEach((category, count) ->
                    log.info("  {}: {}", category, count.get())
            );
        }

        if (!failureReasons.isEmpty()) {
            log.info("");
            log.info("Top Failure Reasons:");
            getTopFailures(5).forEach(entry ->
                    log.info("  {}: {} occurrences", entry.getKey(), entry.getValue())
            );
        }

        if (!testDurations.isEmpty()) {
            log.info("");
            log.info("Slowest Tests:");
            getSlowestTests(5).forEach(entry ->
                    log.info("  {}: {:.2f} ms", entry.getKey(), entry.getValue())
            );
        }

        log.info("=========================================");
    }

    /**
     * Reset all metrics
     */
    public void reset() {
        totalTests.set(0);
        passedTests.set(0);
        failedTests.set(0);
        skippedTests.set(0);
        totalDuration.set(0);
        testsByCategory.clear();
        failureReasons.clear();
        testResults.clear();
        testDurations.clear();

        log.info("Test metrics reset");
    }

    /**
     * Test result data class
     */
    @Getter
    public static class TestResult {
        private final String testName;
        private final LocalDateTime startTime;
        private boolean passed;
        private boolean skipped;
        private long duration;
        private String failureReason;
        private String skipReason;

        public TestResult(String testName) {
            this.testName = testName;
            this.startTime = LocalDateTime.now();
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public void setSkipped(boolean skipped) {
            this.skipped = skipped;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void setFailureReason(String failureReason) {
            this.failureReason = failureReason;
        }

        public void setSkipReason(String skipReason) {
            this.skipReason = skipReason;
        }
    }
}
