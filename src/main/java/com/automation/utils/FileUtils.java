package com.automation.utils;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class FileUtils {
    private static final String SCREENSHOT_DIR = "target/screenshots/";

    static {
        createDirectory(SCREENSHOT_DIR);
    }

    public static void takeScreenshot(Page page, String testName) {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("%s_%s.png", testName, timestamp);
            Path screenshotPath = Paths.get(SCREENSHOT_DIR + fileName);

            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));

            log.info("Screenshot saved: {}", screenshotPath);
        } catch (Exception e) {
            log.error("Failed to take screenshot", e);
        }
    }

    public static void createDirectory(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Directory created: {}", directoryPath);
            }
        } catch (Exception e) {
            log.error("Failed to create directory: {}", directoryPath, e);
        }
    }

    public static String readFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (Exception e) {
            log.error("Failed to read file: {}", filePath, e);
            return null;
        }
    }
}
