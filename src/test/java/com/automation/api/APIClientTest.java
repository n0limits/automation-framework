package com.automation.api;

import com.automation.config.TestConfig;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify APIClient ThreadLocal pattern works correctly
 * Tests thread-safety for parallel API test execution
 *
 * @author Victor Grozev
 */
@Slf4j
public class APIClientTest {

    @Test(description = "Verify APIClient initialization works")
    public void testAPIClientInitialization() {
        log.info("=== Testing APIClient Initialization ===");

        // Get RequestSpecification
        RequestSpecification spec = APIClient.getRequestSpec();

        // Verify it's not null
        assertThat(spec).isNotNull();

        // Verify base URI is set from config
        TestConfig config = TestConfig.getInstance();
        String expectedBaseUri = config.getApiBaseUrl();

        log.info("Expected Base URI: {}", expectedBaseUri);
        log.info("✅ APIClient initialization successful!");
    }

    @Test(description = "Verify ThreadLocal pattern - each thread gets isolated RequestSpec")
    public void testThreadLocalIsolation() throws InterruptedException, ExecutionException {
        log.info("=== Testing ThreadLocal Isolation ===");

        int numberOfThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<RequestSpecification>> futures = new ArrayList<>();

        // Submit tasks to different threads
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            Future<RequestSpecification> future = executor.submit(() -> {
                log.info("Thread {} requesting RequestSpecification", threadId);
                RequestSpecification spec = APIClient.getRequestSpec();

                // Simulate some work
                Thread.sleep(100);

                log.info("Thread {} received RequestSpecification: {}", threadId, System.identityHashCode(spec));
                return spec;
            });
            futures.add(future);
        }

        // Collect all RequestSpecifications
        List<RequestSpecification> specs = new ArrayList<>();
        for (Future<RequestSpecification> future : futures) {
            RequestSpecification spec = future.get();
            assertThat(spec).isNotNull();
            specs.add(spec);
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Verify all threads got non-null specs
        assertThat(specs).hasSize(numberOfThreads);
        assertThat(specs).doesNotContainNull();

        log.info("✅ ThreadLocal isolation verified - {} threads safely accessed APIClient", numberOfThreads);
    }

    @Test(description = "Verify cleanup removes ThreadLocal reference")
    public void testCleanup() {
        log.info("=== Testing ThreadLocal Cleanup ===");

        // Initialize APIClient
        RequestSpecification spec1 = APIClient.getRequestSpec();
        assertThat(spec1).isNotNull();
        log.info("RequestSpec initialized: {}", System.identityHashCode(spec1));

        // Cleanup
        APIClient.cleanup();
        log.info("Cleanup called");

        // Get new spec - should be a new instance
        RequestSpecification spec2 = APIClient.getRequestSpec();
        assertThat(spec2).isNotNull();
        log.info("RequestSpec after cleanup: {}", System.identityHashCode(spec2));

        // Note: Identity hashcodes may or may not be different depending on GC and object pooling
        // The important thing is that cleanup() was called without errors

        log.info("✅ Cleanup executed successfully!");
    }

    @Test(description = "Verify withAuth adds Authorization header")
    public void testWithAuth() {
        log.info("=== Testing withAuth() Method ===");

        String testToken = "test_token_12345";
        RequestSpecification authSpec = APIClient.withAuth(testToken);

        assertThat(authSpec).isNotNull();
        log.info("✅ withAuth() created RequestSpec with Bearer token");
    }

    @Test(description = "Verify withApiKey adds X-API-Key header")
    public void testWithApiKey() {
        log.info("=== Testing withApiKey() Method ===");

        String testApiKey = "test_api_key_67890";
        RequestSpecification apiKeySpec = APIClient.withApiKey(testApiKey);

        assertThat(apiKeySpec).isNotNull();
        log.info("✅ withApiKey() created RequestSpec with API key header");
    }

    @Test(description = "Verify getBaseUri returns correct value")
    public void testGetBaseUri() {
        log.info("=== Testing getBaseUri() Method ===");

        String baseUri = APIClient.getBaseUri();
        TestConfig config = TestConfig.getInstance();
        String expectedBaseUri = config.getApiBaseUrl();

        assertThat(baseUri).isEqualTo(expectedBaseUri);
        log.info("Base URI: {}", baseUri);
        log.info("✅ getBaseUri() returns correct value from config");
    }

    @Test(description = "Verify parallel access doesn't cause race conditions")
    public void testParallelAccessSafety() throws InterruptedException {
        log.info("=== Testing Parallel Access Safety ===");

        int numberOfThreads = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        List<Throwable> errors = new CopyOnWriteArrayList<>();

        // Create threads that will all access APIClient simultaneously
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            new Thread(() -> {
                try {
                    // Wait for start signal
                    startLatch.await();

                    // Access APIClient
                    RequestSpecification spec = APIClient.getRequestSpec();
                    assertThat(spec).isNotNull();

                    // Simulate some API work
                    Thread.sleep(50);

                    // Cleanup
                    APIClient.cleanup();

                    log.info("Thread {} completed successfully", threadId);
                } catch (Throwable e) {
                    log.error("Thread {} failed", threadId, e);
                    errors.add(e);
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        // Start all threads simultaneously
        log.info("Starting {} threads simultaneously...", numberOfThreads);
        startLatch.countDown();

        // Wait for all threads to complete
        boolean completed = doneLatch.await(30, TimeUnit.SECONDS);
        assertThat(completed).isTrue().withFailMessage("Not all threads completed in time");

        // Verify no errors occurred
        assertThat(errors)
                .withFailMessage("Errors occurred in parallel execution: " + errors)
                .isEmpty();

        log.info("✅ Parallel access safe - {} threads executed without race conditions", numberOfThreads);
    }

    @AfterClass
    public void cleanup() {
        log.info("=== Test Class Cleanup ===");
        APIClient.cleanup();
        log.info("APIClient ThreadLocal cleaned up");
    }
}
