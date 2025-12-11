package com.automation.api.validators;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ResponseValidator {

    public static void validateStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.statusCode())
                .as("Status code validation")
                .isEqualTo(expectedStatusCode);
        log.info("Status code validated: {}", expectedStatusCode);
    }

    public static void validateResponseTime(Response response, long maxResponseTime) {
        assertThat(response.time())
                .as("Response time should be less than " + maxResponseTime + "ms")
                .isLessThan(maxResponseTime);
        log.info("Response time: {}ms", response.time());
    }

    public static void validateContentType(Response response, String expectedContentType) {
        assertThat(response.contentType())
                .as("Content type validation")
                .contains(expectedContentType);
        log.info("Content type validated: {}", expectedContentType);
    }

    public static void validateFieldExists(Response response, String fieldPath) {
        Object fieldValue = response.jsonPath().get(fieldPath);
        assertThat(fieldValue)
                .as("Field '" + fieldPath + "' should exist")
                .isNotNull();
        log.info("Field validated: {}", fieldPath);
    }

    public static void validateFieldValue(Response response, String fieldPath, Object expectedValue) {
        Object actualValue = response.jsonPath().get(fieldPath);
        assertThat(actualValue)
                .as("Field '" + fieldPath + "' value validation")
                .isEqualTo(expectedValue);
        log.info("Field value validated - {}: {}", fieldPath, expectedValue);
    }

    public static void validateFieldNotNull(Response response, String fieldPath) {
        Object fieldValue = response.jsonPath().get(fieldPath);
        assertThat(fieldValue)
                .as("Field '" + fieldPath + "' should not be null")
                .isNotNull();
        log.info("Field is not null: {}", fieldPath);
    }

    public static void validateFieldIsNull(Response response, String fieldPath) {
        Object fieldValue = response.jsonPath().get(fieldPath);
        assertThat(fieldValue)
                .as("Field '" + fieldPath + "' should be null")
                .isNull();
        log.info("Field is null: {}", fieldPath);
    }

    public static void validateArrayNotEmpty(Response response, String arrayPath) {
        Object arrayValue = response.jsonPath().getList(arrayPath);
        assertThat(arrayValue)
                .as("Array '" + arrayPath + "' should not be empty")
                .isNotNull();
        log.info("Array validated: {}", arrayPath);
    }
}
