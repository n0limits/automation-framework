package com.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestDataReader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TEST_DATA_PATH = "src/test/resources/testdata/";

    public static JsonNode readJsonFile(String fileName) {
        try {
            InputStream inputStream = TestDataReader.class.getClassLoader()
                    .getResourceAsStream("testdata/" + fileName);

            if (inputStream == null) {
                File file = new File(TEST_DATA_PATH + fileName);
                log.info("Reading test data from file: {}", file.getAbsolutePath());
                return objectMapper.readTree(file);
            }

            log.info("Reading test data from classpath: testdata/{}", fileName);
            return objectMapper.readTree(inputStream);
        } catch (IOException e) {
            log.error("Failed to read test data file: {}", fileName, e);
            throw new RuntimeException("Test data file not found or invalid: " + fileName, e);
        }
    }

    public static String getStringValue(JsonNode node, String... path) {
        JsonNode current = node;
        for (String key : path) {
            current = current.get(key);
            if (current == null) {
                log.warn("Path not found in JSON: {}", String.join(" -> ", path));
                return null;
            }
        }
        return current.asText();
    }

    public static List<String> getStringList(JsonNode node, String... path) {
        JsonNode current = node;
        for (String key : path) {
            current = current.get(key);
            if (current == null) {
                log.warn("Path not found in JSON: {}", String.join(" -> ", path));
                return new ArrayList<>();
            }
        }

        List<String> result = new ArrayList<>();
        if (current.isArray()) {
            current.forEach(item -> result.add(item.asText()));
        }
        return result;
    }

    public static boolean getBooleanValue(JsonNode node, String... path) {
        JsonNode current = node;
        for (String key : path) {
            current = current.get(key);
            if (current == null) {
                log.warn("Path not found in JSON: {}", String.join(" -> ", path));
                return false;
            }
        }
        return current.asBoolean();
    }

    public static int getIntValue(JsonNode node, String... path) {
        JsonNode current = node;
        for (String key : path) {
            current = current.get(key);
            if (current == null) {
                log.warn("Path not found in JSON: {}", String.join(" -> ", path));
                return 0;
            }
        }
        return current.asInt();
    }
}
