package com.automation.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.automation.config.TestConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class APIClient {
    private static final TestConfig config = TestConfig.getInstance();
    private static RequestSpecification requestSpec;

    static {
        initializeRequestSpecification();
    }

    private static void initializeRequestSpecification() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.getApiBaseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        RestAssured.requestSpecification = requestSpec;
        log.info("API Client initialized with base URL: {}", config.getApiBaseUrl());
    }

    public static RequestSpecification getRequestSpec() {
        return RestAssured.given().spec(requestSpec);
    }

    public static RequestSpecification withAuth(String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token);
    }
}
