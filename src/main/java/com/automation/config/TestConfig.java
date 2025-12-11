package com.automation.config;

import lombok.Getter;

@Getter
public class TestConfig {
    private final String baseUrl = ConfigReader.getProperty("base.url", "https://example.com");
    private final String browser = ConfigReader.getProperty("browser", "chromium");
    private final boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headless", "false"));
    private final String apiBaseUrl = ConfigReader.getProperty("api.base.url", "https://api.example.com");
    private final int apiTimeout = Integer.parseInt(ConfigReader.getProperty("api.timeout", "30000"));
    private final String mongoConnectionString = ConfigReader.getProperty("mongo.connection.string");
    private final String sqlConnectionString = ConfigReader.getProperty("sql.connection.string");

    private static volatile TestConfig instance;

    private TestConfig() {}

    public static TestConfig getInstance() {
        if (instance == null) {
            synchronized (TestConfig.class) {
                if (instance == null) {
                    instance = new TestConfig();
                }
            }
        }
        return instance;
    }
}
