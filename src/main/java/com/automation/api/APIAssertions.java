package com.automation.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Fluent assertions library for API testing
 * Provides readable, chainable assertions for API responses
 *
 * Features:
 * - Status code assertions
 * - Response time assertions
 * - Content type assertions
 * - JSON path assertions
 * - Header assertions
 * - Allure reporting integration
 *
 * Usage:
 * <pre>
 * Response response = apiClient.getTradingPairs();
 * APIAssertions.assertThat(response)
 *     .hasStatusCode(200)
 *     .hasContentType("application/json")
 *     .respondsWithin(2000)
 *     .jsonPath("$.size()").isGreaterThan(0);
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class APIAssertions {

    private final Response response;

    private APIAssertions(Response response) {
        this.response = response;
    }

    /**
     * Start fluent assertion chain for API response
     *
     * @param response API response
     * @return APIAssertions instance for chaining
     */
    public static APIAssertions forResponse(Response response) {
        return new APIAssertions(response);
    }

    /**
     * Alias for forResponse() - maintains backward compatibility with AssertJ style
     *
     * @param response API response
     * @return APIAssertions instance for chaining
     */
    public static APIAssertions assertThat(Response response) {
        return forResponse(response);
    }

    /**
     * Assert status code equals expected value
     *
     * @param expectedStatusCode Expected HTTP status code
     * @return this for chaining
     */
    @Step("Assert status code is {expectedStatusCode}")
    public APIAssertions hasStatusCode(int expectedStatusCode) {
        int actualStatusCode = response.getStatusCode();
        log.info("Asserting status code: expected={}, actual={}", expectedStatusCode, actualStatusCode);

        Assertions.assertThat(actualStatusCode)
                .as("Response status code")
                .isEqualTo(expectedStatusCode);

        return this;
    }

    /**
     * Assert status code is 200 OK
     *
     * @return this for chaining
     */
    @Step("Assert status code is 200 OK")
    public APIAssertions isOK() {
        return hasStatusCode(200);
    }

    /**
     * Assert status code is 201 Created
     *
     * @return this for chaining
     */
    @Step("Assert status code is 201 Created")
    public APIAssertions isCreated() {
        return hasStatusCode(201);
    }

    /**
     * Assert status code is 400 Bad Request
     *
     * @return this for chaining
     */
    @Step("Assert status code is 400 Bad Request")
    public APIAssertions isBadRequest() {
        return hasStatusCode(400);
    }

    /**
     * Assert status code is 401 Unauthorized
     *
     * @return this for chaining
     */
    @Step("Assert status code is 401 Unauthorized")
    public APIAssertions isUnauthorized() {
        return hasStatusCode(401);
    }

    /**
     * Assert status code is 404 Not Found
     *
     * @return this for chaining
     */
    @Step("Assert status code is 404 Not Found")
    public APIAssertions isNotFound() {
        return hasStatusCode(404);
    }

    /**
     * Assert status code is 500 Internal Server Error
     *
     * @return this for chaining
     */
    @Step("Assert status code is 500 Internal Server Error")
    public APIAssertions isServerError() {
        return hasStatusCode(500);
    }

    /**
     * Assert response time is within limit
     *
     * @param maxTimeMs Maximum allowed response time in milliseconds
     * @return this for chaining
     */
    @Step("Assert response time is within {maxTimeMs} ms")
    public APIAssertions respondsWithin(long maxTimeMs) {
        long actualTime = response.getTime();
        log.info("Asserting response time: max={} ms, actual={} ms", maxTimeMs, actualTime);

        Assertions.assertThat(actualTime)
                .as("Response time should be within %d ms", maxTimeMs)
                .isLessThanOrEqualTo(maxTimeMs);

        return this;
    }

    /**
     * Assert content type matches expected value
     *
     * @param expectedContentType Expected content type (e.g., "application/json")
     * @return this for chaining
     */
    @Step("Assert content type is {expectedContentType}")
    public APIAssertions hasContentType(String expectedContentType) {
        String actualContentType = response.getContentType();
        log.info("Asserting content type: expected={}, actual={}", expectedContentType, actualContentType);

        Assertions.assertThat(actualContentType)
                .as("Response content type")
                .contains(expectedContentType);

        return this;
    }

    /**
     * Assert content type is JSON
     *
     * @return this for chaining
     */
    @Step("Assert content type is JSON")
    public APIAssertions hasJsonContentType() {
        return hasContentType("application/json");
    }

    /**
     * Assert response body contains text
     *
     * @param expectedText Expected text in response body
     * @return this for chaining
     */
    @Step("Assert response body contains: {expectedText}")
    public APIAssertions bodyContains(String expectedText) {
        String body = response.getBody().asString();
        log.debug("Asserting body contains: '{}'", expectedText);

        Assertions.assertThat(body)
                .as("Response body should contain text")
                .contains(expectedText);

        return this;
    }

    /**
     * Assert response header exists
     *
     * @param headerName Header name
     * @return this for chaining
     */
    @Step("Assert header '{headerName}' exists")
    public APIAssertions hasHeader(String headerName) {
        String headerValue = response.getHeader(headerName);
        log.debug("Asserting header '{}' exists: {}", headerName, headerValue);

        Assertions.assertThat(headerValue)
                .as("Header '%s' should exist", headerName)
                .isNotNull();

        return this;
    }

    /**
     * Assert response header has specific value
     *
     * @param headerName  Header name
     * @param headerValue Expected header value
     * @return this for chaining
     */
    @Step("Assert header '{headerName}' equals '{headerValue}'")
    public APIAssertions hasHeader(String headerName, String headerValue) {
        String actualValue = response.getHeader(headerName);
        log.debug("Asserting header '{}': expected='{}', actual='{}'", headerName, headerValue, actualValue);

        Assertions.assertThat(actualValue)
                .as("Header '%s'", headerName)
                .isEqualTo(headerValue);

        return this;
    }

    /**
     * Assert JSON path exists in response
     *
     * @param jsonPath JSON path expression
     * @return JsonPathAssertion for further assertions
     */
    @Step("Assert JSON path exists: {jsonPath}")
    public JsonPathAssertion jsonPath(String jsonPath) {
        Object value = response.jsonPath().get(jsonPath);
        log.debug("JSON path '{}' = {}", jsonPath, value);

        Assertions.assertThat(value)
                .as("JSON path '%s' should exist", jsonPath)
                .isNotNull();

        return new JsonPathAssertion(response, jsonPath);
    }

    /**
     * Assert response body is not empty
     *
     * @return this for chaining
     */
    @Step("Assert response body is not empty")
    public APIAssertions hasNonEmptyBody() {
        String body = response.getBody().asString();
        log.debug("Asserting body is not empty");

        Assertions.assertThat(body)
                .as("Response body")
                .isNotEmpty();

        return this;
    }

    /**
     * Get the underlying Response object
     *
     * @return Response object
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Nested class for JSON path-specific assertions
     */
    public static class JsonPathAssertion {
        private final Response response;
        private final String jsonPath;

        private JsonPathAssertion(Response response, String jsonPath) {
            this.response = response;
            this.jsonPath = jsonPath;
        }

        /**
         * Assert JSON path value equals expected value
         *
         * @param expectedValue Expected value
         * @return parent APIAssertions for chaining
         */
        @Step("Assert JSON path '{jsonPath}' equals '{expectedValue}'")
        public APIAssertions isEqualTo(Object expectedValue) {
            Object actualValue = response.jsonPath().get(jsonPath);
            log.info("Asserting '{}' = '{}' (actual: '{}')", jsonPath, expectedValue, actualValue);

            Assertions.assertThat(actualValue)
                    .as("JSON path '%s'", jsonPath)
                    .isEqualTo(expectedValue);

            return new APIAssertions(response);
        }

        /**
         * Assert JSON path value is greater than expected value
         *
         * @param expectedValue Expected minimum value
         * @return parent APIAssertions for chaining
         */
        @Step("Assert JSON path '{jsonPath}' is greater than {expectedValue}")
        public APIAssertions isGreaterThan(int expectedValue) {
            Integer actualValue = response.jsonPath().getInt(jsonPath);
            log.info("Asserting '{}' > {} (actual: {})", jsonPath, expectedValue, actualValue);

            Assertions.assertThat(actualValue)
                    .as("JSON path '%s'", jsonPath)
                    .isGreaterThan(expectedValue);

            return new APIAssertions(response);
        }

        /**
         * Assert JSON path value is a list with specific size
         *
         * @param expectedSize Expected list size
         * @return parent APIAssertions for chaining
         */
        @Step("Assert JSON path '{jsonPath}' has size {expectedSize}")
        public APIAssertions hasSize(int expectedSize) {
            List<?> list = response.jsonPath().getList(jsonPath);
            log.info("Asserting '{}' size = {} (actual: {})", jsonPath, expectedSize, list.size());

            Assertions.assertThat(list)
                    .as("JSON path '%s' list size", jsonPath)
                    .hasSize(expectedSize);

            return new APIAssertions(response);
        }

        /**
         * Assert JSON path value contains expected item
         *
         * @param expectedItem Expected item in list
         * @return parent APIAssertions for chaining
         */
        @Step("Assert JSON path '{jsonPath}' contains '{expectedItem}'")
        public APIAssertions contains(Object expectedItem) {
            List<?> list = response.jsonPath().getList(jsonPath);
            log.info("Asserting '{}' contains '{}'", jsonPath, expectedItem);

            Assertions.assertThat(list)
                    .as("JSON path '%s' list", jsonPath)
                    .isNotNull()
                    .anySatisfy(item -> Assertions.assertThat(item).isEqualTo(expectedItem));

            return new APIAssertions(response);
        }

        /**
         * Assert JSON path value is not null
         *
         * @return parent APIAssertions for chaining
         */
        @Step("Assert JSON path '{jsonPath}' is not null")
        public APIAssertions isNotNull() {
            Object value = response.jsonPath().get(jsonPath);
            log.info("Asserting '{}' is not null", jsonPath);

            Assertions.assertThat(value)
                    .as("JSON path '%s'", jsonPath)
                    .isNotNull();

            return new APIAssertions(response);
        }
    }
}
