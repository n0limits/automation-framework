package com.automation.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.automation.config.TestConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread-safe API Client for RestAssured
 * Uses ThreadLocal pattern to support parallel test execution
 *
 * @author Victor Grozev
 */
@Slf4j
public class APIClient {
    private static final TestConfig config = TestConfig.getInstance();
    private static final ThreadLocal<RequestSpecification> requestSpec = new ThreadLocal<>();

    /**
     * Get thread-safe RequestSpecification
     * Initializes spec on first access per thread
     *
     * @return RequestSpecification for current thread
     */
    public static RequestSpecification getRequestSpec() {
        if (requestSpec.get() == null) {
            initializeRequestSpecification();
        }
        return RestAssured.given().spec(requestSpec.get());
    }

    /**
     * Get RequestSpecification with authentication
     *
     * @param token Bearer token
     * @return Authenticated RequestSpecification
     */
    public static RequestSpecification withAuth(String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token);
    }

    /**
     * Get RequestSpecification with API key
     *
     * @param apiKey API key
     * @return RequestSpecification with API key header
     */
    public static RequestSpecification withApiKey(String apiKey) {
        return getRequestSpec().header("X-API-Key", apiKey);
    }

    /**
     * Initialize RequestSpecification for current thread
     */
    private static void initializeRequestSpecification() {
        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(config.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new AllureRestAssured())  // Allure reporting integration
                .build();

        requestSpec.set(spec);
        log.info("API Client initialized for thread {} with base URL: {}",
                Thread.currentThread().getName(), config.getApiBaseUrl());
    }

    /**
     * Cleanup RequestSpecification for current thread
     * Should be called in test teardown
     */
    public static void cleanup() {
        if (requestSpec.get() != null) {
            requestSpec.remove();
            log.debug("API Client cleaned up for thread {}", Thread.currentThread().getName());
        }
    }

    /**
     * Get base URI from configuration
     *
     * @return API base URI
     */
    public static String getBaseUri() {
        return config.getApiBaseUrl();
    }
}
