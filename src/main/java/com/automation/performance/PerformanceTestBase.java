package com.automation.performance;

import com.automation.base.BaseTest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Base class for Performance Tests
 * Provides foundation for load testing and performance measurement
 *
 * Features:
 * - Thread pool management for concurrent execution
 * - Performance metrics collection
 * - Response time tracking
 * - Throughput measurement
 * - Success/failure rate tracking
 * - SLA validation
 *
 * @author Victor Grozev
 */
@Slf4j
@Getter
public abstract class PerformanceTestBase extends BaseTest {
    protected MetricsCollector metricsCollector;
    protected ExecutorService executorService;
    protected int threadPoolSize = 10;
    protected int defaultTimeout = 60; // seconds

    // Performance thresholds (can be overridden by subclasses)
    protected long maxAverageResponseTime = 2000; // ms
    protected long maxP95ResponseTime = 5000; // ms
    protected long maxP99ResponseTime = 10000; // ms
    protected double minSuccessRate = 95.0; // percent
    protected double minThroughput = 10.0; // requests per second

    @BeforeMethod
    public void setupPerformanceTest() {
        log.info("===== Setting up Performance Test =====");

        // Initialize metrics collector
        metricsCollector = new MetricsCollector();

        // Initialize thread pool
        executorService = Executors.newFixedThreadPool(threadPoolSize);

        log.info("Thread pool initialized with {} threads", threadPoolSize);
        log.info("Performance thresholds:");
        log.info("  - Max Average Response Time: {} ms", maxAverageResponseTime);
        log.info("  - Max P95 Response Time: {} ms", maxP95ResponseTime);
        log.info("  - Max P99 Response Time: {} ms", maxP99ResponseTime);
        log.info("  - Min Success Rate: {}%", minSuccessRate);
        log.info("  - Min Throughput: {} req/s", minThroughput);
    }

    @AfterMethod
    public void tearDownPerformanceTest() {
        log.info("===== Tearing down Performance Test =====");

        // Shutdown executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(defaultTimeout, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Log final metrics
        logPerformanceMetrics();

        log.info("Performance test teardown complete");
    }

    /**
     * Execute load test with specified parameters
     *
     * @param task Task to execute
     * @param numberOfUsers Number of concurrent users
     * @param iterationsPerUser Number of iterations per user
     * @return Performance results
     */
    protected PerformanceResult executeLoadTest(Callable<Boolean> task,
                                                 int numberOfUsers,
                                                 int iterationsPerUser) {
        log.info("Starting load test: {} users × {} iterations = {} total requests",
                numberOfUsers, iterationsPerUser, numberOfUsers * iterationsPerUser);

        long startTime = System.currentTimeMillis();
        List<Future<Boolean>> futures = new ArrayList<>();

        // Submit tasks
        for (int user = 0; user < numberOfUsers; user++) {
            for (int iteration = 0; iteration < iterationsPerUser; iteration++) {
                Future<Boolean> future = executorService.submit(() -> {
                    long requestStart = System.currentTimeMillis();
                    boolean success = false;

                    try {
                        success = task.call();
                        long responseTime = System.currentTimeMillis() - requestStart;
                        metricsCollector.recordRequest(responseTime, success);
                    } catch (Exception e) {
                        long responseTime = System.currentTimeMillis() - requestStart;
                        metricsCollector.recordRequest(responseTime, false);
                        log.error("Request failed", e);
                    }

                    return success;
                });
                futures.add(future);
            }
        }

        // Wait for all tasks to complete
        int successCount = 0;
        int failureCount = 0;

        for (Future<Boolean> future : futures) {
            try {
                Boolean result = future.get(defaultTimeout, TimeUnit.SECONDS);
                if (result != null && result) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (TimeoutException e) {
                failureCount++;
                log.error("Request timed out", e);
            } catch (Exception e) {
                failureCount++;
                log.error("Request execution failed", e);
            }
        }

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        // Create result
        PerformanceResult result = new PerformanceResult(
                numberOfUsers * iterationsPerUser,
                successCount,
                failureCount,
                totalDuration,
                metricsCollector
        );

        log.info("Load test completed:");
        log.info("  Total Requests: {}", result.getTotalRequests());
        log.info("  Successful: {}", result.getSuccessfulRequests());
        log.info("  Failed: {}", result.getFailedRequests());
        log.info("  Success Rate: {:.2f}%", result.getSuccessRate());
        log.info("  Duration: {} ms", result.getTotalDuration());
        log.info("  Throughput: {:.2f} req/s", result.getThroughput());
        log.info("  Avg Response Time: {:.2f} ms", result.getAverageResponseTime());
        log.info("  P95 Response Time: {:.2f} ms", result.getP95ResponseTime());
        log.info("  P99 Response Time: {:.2f} ms", result.getP99ResponseTime());

        return result;
    }

    /**
     * Execute ramped load test (gradually increase load)
     *
     * @param task Task to execute
     * @param startUsers Starting number of users
     * @param endUsers Ending number of users
     * @param rampUpTime Time to ramp up (seconds)
     * @param iterationsPerUser Iterations per user
     * @return Performance results
     */
    protected PerformanceResult executeRampedLoadTest(Callable<Boolean> task,
                                                       int startUsers,
                                                       int endUsers,
                                                       int rampUpTime,
                                                       int iterationsPerUser) {
        log.info("Starting ramped load test: {} → {} users over {} seconds",
                startUsers, endUsers, rampUpTime);

        long startTime = System.currentTimeMillis();
        List<Future<Boolean>> futures = new ArrayList<>();

        int userIncrement = (endUsers - startUsers) / rampUpTime;
        int currentUsers = startUsers;

        for (int second = 0; second < rampUpTime; second++) {
            int usersThisSecond = currentUsers;

            for (int user = 0; user < usersThisSecond; user++) {
                for (int iteration = 0; iteration < iterationsPerUser; iteration++) {
                    Future<Boolean> future = executorService.submit(() -> {
                        long requestStart = System.currentTimeMillis();
                        boolean success = false;

                        try {
                            success = task.call();
                            long responseTime = System.currentTimeMillis() - requestStart;
                            metricsCollector.recordRequest(responseTime, success);
                        } catch (Exception e) {
                            long responseTime = System.currentTimeMillis() - requestStart;
                            metricsCollector.recordRequest(responseTime, false);
                            log.error("Request failed during ramp", e);
                        }

                        return success;
                    });
                    futures.add(future);
                }
            }

            currentUsers += userIncrement;

            // Wait 1 second before next batch
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Wait for all tasks to complete
        int successCount = 0;
        int failureCount = 0;

        for (Future<Boolean> future : futures) {
            try {
                Boolean result = future.get(defaultTimeout, TimeUnit.SECONDS);
                if (result != null && result) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                failureCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        return new PerformanceResult(
                futures.size(),
                successCount,
                failureCount,
                totalDuration,
                metricsCollector
        );
    }

    /**
     * Execute spike test (sudden increase in load)
     *
     * @param task Task to execute
     * @param normalLoad Normal load (users)
     * @param spikeLoad Spike load (users)
     * @param normalDuration Duration of normal load (seconds)
     * @param spikeDuration Duration of spike (seconds)
     * @return Performance results
     */
    protected PerformanceResult executeSpikeTest(Callable<Boolean> task,
                                                  int normalLoad,
                                                  int spikeLoad,
                                                  int normalDuration,
                                                  int spikeDuration) {
        log.info("Starting spike test: {} users → {} users spike for {} seconds",
                normalLoad, spikeLoad, spikeDuration);

        List<Future<Boolean>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Phase 1: Normal load
        log.info("Phase 1: Normal load ({} users for {} seconds)", normalLoad, normalDuration);
        futures.addAll(submitLoadForDuration(task, normalLoad, normalDuration));

        // Phase 2: Spike load
        log.info("Phase 2: Spike load ({} users for {} seconds)", spikeLoad, spikeDuration);
        futures.addAll(submitLoadForDuration(task, spikeLoad, spikeDuration));

        // Phase 3: Back to normal
        log.info("Phase 3: Back to normal ({} users for {} seconds)", normalLoad, normalDuration);
        futures.addAll(submitLoadForDuration(task, normalLoad, normalDuration));

        // Collect results
        int successCount = 0;
        int failureCount = 0;

        for (Future<Boolean> future : futures) {
            try {
                Boolean result = future.get(defaultTimeout, TimeUnit.SECONDS);
                if (result != null && result) {
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception e) {
                failureCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;

        return new PerformanceResult(
                futures.size(),
                successCount,
                failureCount,
                totalDuration,
                metricsCollector
        );
    }

    /**
     * Submit load for specified duration
     *
     * @param task Task to execute
     * @param users Number of users
     * @param durationSeconds Duration in seconds
     * @return List of futures
     */
    private List<Future<Boolean>> submitLoadForDuration(Callable<Boolean> task,
                                                         int users,
                                                         int durationSeconds) {
        List<Future<Boolean>> futures = new ArrayList<>();
        long endTime = System.currentTimeMillis() + (durationSeconds * 1000L);

        while (System.currentTimeMillis() < endTime) {
            for (int i = 0; i < users; i++) {
                Future<Boolean> future = executorService.submit(() -> {
                    long requestStart = System.currentTimeMillis();
                    boolean success = false;

                    try {
                        success = task.call();
                        long responseTime = System.currentTimeMillis() - requestStart;
                        metricsCollector.recordRequest(responseTime, success);
                    } catch (Exception e) {
                        long responseTime = System.currentTimeMillis() - requestStart;
                        metricsCollector.recordRequest(responseTime, false);
                    }

                    return success;
                });
                futures.add(future);
            }

            // Small delay between iterations
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return futures;
    }

    /**
     * Validate performance results against SLA
     *
     * @param result Performance result
     * @return true if meets SLA
     */
    protected boolean validatePerformanceSLA(PerformanceResult result) {
        log.info("Validating performance against SLA...");

        boolean meetsAvgResponseTime = result.getAverageResponseTime() <= maxAverageResponseTime;
        boolean meetsP95ResponseTime = result.getP95ResponseTime() <= maxP95ResponseTime;
        boolean meetsP99ResponseTime = result.getP99ResponseTime() <= maxP99ResponseTime;
        boolean meetsSuccessRate = result.getSuccessRate() >= minSuccessRate;
        boolean meetsThroughput = result.getThroughput() >= minThroughput;

        log.info("SLA Validation Results:");
        log.info("  [OK] Avg Response Time: {} ms <= {} ms: {}",
                result.getAverageResponseTime(), maxAverageResponseTime, meetsAvgResponseTime);
        log.info("  [OK] P95 Response Time: {} ms <= {} ms: {}",
                result.getP95ResponseTime(), maxP95ResponseTime, meetsP95ResponseTime);
        log.info("  [OK] P99 Response Time: {} ms <= {} ms: {}",
                result.getP99ResponseTime(), maxP99ResponseTime, meetsP99ResponseTime);
        log.info("  [OK] Success Rate: {:.2f}% >= {:.2f}%: {}",
                result.getSuccessRate(), minSuccessRate, meetsSuccessRate);
        log.info("  [OK] Throughput: {:.2f} req/s >= {:.2f} req/s: {}",
                result.getThroughput(), minThroughput, meetsThroughput);

        boolean meetsSLA = meetsAvgResponseTime && meetsP95ResponseTime &&
                meetsP99ResponseTime && meetsSuccessRate && meetsThroughput;

        if (meetsSLA) {
            log.info("[PASS] Performance meets SLA requirements");
        } else {
            log.warn("[FAIL] Performance does NOT meet SLA requirements");
        }

        return meetsSLA;
    }

    /**
     * Log performance metrics
     */
    protected void logPerformanceMetrics() {
        log.info("===== Performance Metrics Summary =====");
        log.info("Total Requests: {}", metricsCollector.getTotalRequests());
        log.info("Successful Requests: {}", metricsCollector.getSuccessfulRequests());
        log.info("Failed Requests: {}", metricsCollector.getFailedRequests());
        log.info("Success Rate: {:.2f}%", metricsCollector.getSuccessRate());
        log.info("Average Response Time: {:.2f} ms", metricsCollector.getAverageResponseTime());
        log.info("Min Response Time: {} ms", metricsCollector.getMinResponseTime());
        log.info("Max Response Time: {} ms", metricsCollector.getMaxResponseTime());
        log.info("P50 Response Time: {:.2f} ms", metricsCollector.getPercentile(50));
        log.info("P95 Response Time: {:.2f} ms", metricsCollector.getPercentile(95));
        log.info("P99 Response Time: {:.2f} ms", metricsCollector.getPercentile(99));
        log.info("=======================================");
    }

    /**
     * Set performance thresholds
     */
    protected void setPerformanceThresholds(long maxAvgResponseTime,
                                            long maxP95,
                                            long maxP99,
                                            double minSuccessRate,
                                            double minThroughput) {
        this.maxAverageResponseTime = maxAvgResponseTime;
        this.maxP95ResponseTime = maxP95;
        this.maxP99ResponseTime = maxP99;
        this.minSuccessRate = minSuccessRate;
        this.minThroughput = minThroughput;
    }
}
