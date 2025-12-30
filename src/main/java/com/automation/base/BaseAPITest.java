package com.automation.base;

import com.automation.api.APIClient;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for API tests
 * Provides common setup and teardown for API testing
 *
 * @author Victor Grozev
 */
@Slf4j
public class BaseAPITest extends BaseTest {

    protected RequestSpecification requestSpec;

    /**
     * Setup API client before each test method
     */
    @BeforeMethod
    public void setupAPI() {
        log.info("===== Setting up API Test =====");
        requestSpec = APIClient.getRequestSpec();
        log.info("API Client initialized with base URI: {}", APIClient.getBaseUri());

        // Hook for additional setup
        performAdditionalAPISetup();
    }

    /**
     * Cleanup API client after each test method
     */
    @AfterMethod
    public void tearDownAPI() {
        log.info("===== Tearing down API Test =====");

        // Hook for additional cleanup
        performAdditionalAPICleanup();

        // Cleanup ThreadLocal
        APIClient.cleanup();
        log.info("API Client cleanup completed");
    }

    /**
     * Hook method for additional API setup
     * Override this in test classes if needed
     */
    protected void performAdditionalAPISetup() {
        log.debug("No additional API setup required");
    }

    /**
     * Hook method for additional API cleanup
     * Override this in test classes if needed
     */
    protected void performAdditionalAPICleanup() {
        log.debug("No additional API cleanup required");
    }

    /**
     * Helper method to log response details
     *
     * @param response Response to log
     */
    protected void logResponse(Response response) {
        log.info("Response Status: {}", response.getStatusCode());
        log.info("Response Time: {} ms", response.getTime());
        log.debug("Response Body: {}", response.getBody().asString());
    }

    /**
     * Helper method to get authenticated request spec
     *
     * @param token Bearer token
     * @return Authenticated RequestSpecification
     */
    protected RequestSpecification withAuth(String token) {
        return APIClient.withAuth(token);
    }

    /**
     * Helper method to get request spec with API key
     *
     * @param apiKey API key
     * @return RequestSpecification with API key
     */
    protected RequestSpecification withApiKey(String apiKey) {
        return APIClient.withApiKey(apiKey);
    }
}
