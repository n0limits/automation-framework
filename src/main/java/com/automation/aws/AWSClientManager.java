package com.automation.aws;

import com.automation.config.TestConfig;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.SfnClientBuilder;

import java.net.URI;

/**
 * Thread-safe AWS Client Manager using ThreadLocal pattern
 * Manages AWS SDK v2 clients for parallel test execution
 *
 * Features:
 * - ThreadLocal isolation for parallel tests
 * - Lazy initialization per thread
 * - Proper resource cleanup
 * - Support for LocalStack (local development)
 * - DefaultCredentialsProvider for flexible auth
 *
 * Usage:
 * <pre>
 * SfnClient client = AWSClientManager.getStepFunctionsClient();
 * // ... use client
 * AWSClientManager.cleanup(); // call in @AfterMethod
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class AWSClientManager {
    private static final TestConfig config = TestConfig.getInstance();
    private static final ThreadLocal<SfnClient> stepFunctionsClient = new ThreadLocal<>();

    /**
     * Get thread-safe Step Functions client
     * Initializes client on first access per thread
     *
     * @return SfnClient for current thread
     */
    public static SfnClient getStepFunctionsClient() {
        if (stepFunctionsClient.get() == null) {
            initializeStepFunctionsClient();
        }
        return stepFunctionsClient.get();
    }

    /**
     * Initialize Step Functions client for current thread
     * Uses environment-based configuration
     */
    private static void initializeStepFunctionsClient() {
        try {
            SfnClientBuilder builder = SfnClient.builder()
                    .region(Region.of(config.getAwsRegion()))
                    .credentialsProvider(DefaultCredentialsProvider.create());

            // Override endpoint for LocalStack (local development)
            String endpointOverride = config.getAwsEndpointOverride();
            if (endpointOverride != null && !endpointOverride.isEmpty()) {
                builder.endpointOverride(URI.create(endpointOverride));
                log.info("AWS Step Functions client initialized with LocalStack endpoint: {}", endpointOverride);
            }

            SfnClient client = builder.build();
            stepFunctionsClient.set(client);

            log.info("AWS Step Functions client initialized for thread {} in region {}",
                    Thread.currentThread().getName(), config.getAwsRegion());
        } catch (Exception e) {
            log.error("Failed to initialize AWS Step Functions client", e);
            throw new RuntimeException("AWS Step Functions client initialization failed", e);
        }
    }

    /**
     * Cleanup AWS clients for current thread
     * Should be called in test teardown (@AfterMethod)
     */
    public static void cleanup() {
        SfnClient client = stepFunctionsClient.get();
        if (client != null) {
            try {
                client.close();
                stepFunctionsClient.remove();
                log.debug("AWS clients cleaned up for thread {}", Thread.currentThread().getName());
            } catch (Exception e) {
                log.error("Error cleaning up AWS clients", e);
            }
        }
    }

    /**
     * Get AWS region from configuration
     *
     * @return AWS region
     */
    public static String getRegion() {
        return config.getAwsRegion();
    }

    /**
     * Check if using LocalStack (local development)
     *
     * @return true if endpoint override is configured
     */
    public static boolean isLocalStack() {
        String endpointOverride = config.getAwsEndpointOverride();
        return endpointOverride != null && !endpointOverride.isEmpty();
    }
}
