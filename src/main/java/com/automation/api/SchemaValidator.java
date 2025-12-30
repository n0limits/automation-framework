package com.automation.api;

import io.qameta.allure.Step;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matcher;

import java.io.File;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * JSON Schema Validator for API responses
 * Validates API responses against JSON Schema definitions
 *
 * Features:
 * - Schema validation from classpath
 * - Schema validation from file
 * - Custom error messages
 * - Allure reporting integration
 *
 * Schema files should be placed in: src/test/resources/schemas/
 *
 * Usage:
 * <pre>
 * Response response = apiClient.getTradingPairs();
 * SchemaValidator.validateSchema(response, "trading-pairs-schema.json");
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class SchemaValidator {

    private static final String SCHEMA_BASE_PATH = "schemas/";

    /**
     * Validate response against JSON schema from classpath
     *
     * @param response   API response to validate
     * @param schemaFile Schema file name (e.g., "trading-pairs-schema.json")
     */
    @Step("Validate response against schema: {schemaFile}")
    public static void validateSchema(Response response, String schemaFile) {
        String schemaPath = SCHEMA_BASE_PATH + schemaFile;
        log.info("Validating response against schema: {}", schemaPath);

        try {
            response.then()
                    .assertThat()
                    .body(matchesJsonSchemaInClasspath(schemaPath));

            log.info("✅ Schema validation passed for: {}", schemaFile);
        } catch (AssertionError e) {
            log.error("❌ Schema validation failed for: {}", schemaFile);
            log.error("Response body: {}", response.getBody().asString());
            throw e;
        }
    }

    /**
     * Validate response against JSON schema from file
     *
     * @param response API response to validate
     * @param schemaFilePath Full path to schema file
     */
    @Step("Validate response against schema file: {schemaFilePath}")
    public static void validateSchemaFromFile(Response response, File schemaFilePath) {
        log.info("Validating response against schema file: {}", schemaFilePath.getAbsolutePath());

        try {
            response.then()
                    .assertThat()
                    .body(JsonSchemaValidator.matchesJsonSchema(schemaFilePath));

            log.info("✅ Schema validation passed");
        } catch (AssertionError e) {
            log.error("❌ Schema validation failed");
            log.error("Response body: {}", response.getBody().asString());
            throw e;
        }
    }

    /**
     * Get JSON schema matcher for custom assertions
     *
     * @param schemaFile Schema file name
     * @return Hamcrest matcher for schema validation
     */
    public static Matcher<String> schemaMatcher(String schemaFile) {
        String schemaPath = SCHEMA_BASE_PATH + schemaFile;
        return matchesJsonSchemaInClasspath(schemaPath);
    }

    /**
     * Validate that response has required fields
     * Simple field existence check without full schema validation
     *
     * @param response       API response
     * @param requiredFields Required field paths (e.g., "data.id", "data.name")
     */
    @Step("Validate required fields: {requiredFields}")
    public static void validateRequiredFields(Response response, String... requiredFields) {
        log.info("Validating required fields: {}", String.join(", ", requiredFields));

        for (String field : requiredFields) {
            Object value = response.jsonPath().get(field);
            if (value == null) {
                String error = String.format("Required field '%s' is missing in response", field);
                log.error(error);
                log.error("Response: {}", response.getBody().asString());
                throw new AssertionError(error);
            }
        }

        log.info("✅ All required fields present");
    }

    /**
     * Validate response matches expected structure (field types)
     *
     * @param response API response
     * @param fieldTypeMap Map of field paths to expected types
     */
    @Step("Validate field types")
    public static void validateFieldTypes(Response response, java.util.Map<String, Class<?>> fieldTypeMap) {
        log.info("Validating field types");

        fieldTypeMap.forEach((fieldPath, expectedType) -> {
            Object value = response.jsonPath().get(fieldPath);

            if (value == null) {
                String error = String.format("Field '%s' is null", fieldPath);
                log.error(error);
                throw new AssertionError(error);
            }

            if (!expectedType.isInstance(value)) {
                String error = String.format(
                        "Field '%s' has wrong type. Expected: %s, Actual: %s",
                        fieldPath,
                        expectedType.getSimpleName(),
                        value.getClass().getSimpleName()
                );
                log.error(error);
                throw new AssertionError(error);
            }

            log.debug("Field '{}' type validation passed: {}", fieldPath, expectedType.getSimpleName());
        });

        log.info("✅ All field types valid");
    }
}
