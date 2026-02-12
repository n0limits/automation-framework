package com.automation.utils;

import com.automation.config.ConfigReader;
import com.microsoft.playwright.*;
import com.microsoft.playwright.Tracing;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Playwright Manager - Thread-safe management of Playwright resources.
 *
 * Features:
 * - ThreadLocal storage for parallel test execution
 * - Optional tracing for failure debugging
 * - Automatic resource cleanup
 *
 * Tracing Configuration (via application.properties):
 * - tracing.enabled=true/false (default: true)
 * - tracing.screenshots=true/false (default: true)
 * - tracing.snapshots=true/false (default: true)
 * - tracing.sources=true/false (default: false)
 * - tracing.output.dir=path (default: target/traces)
 *
 * @author Victor Grozev
 */
@Slf4j
public class PlaywrightManager {
    private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private static final ThreadLocal<Page> page = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> tracingStarted = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<String> currentTestName = new ThreadLocal<>();

    // Tracing configuration
    private static final boolean TRACING_ENABLED = ConfigReader.getBooleanProperty("tracing.enabled", true);
    private static final boolean TRACING_SCREENSHOTS = ConfigReader.getBooleanProperty("tracing.screenshots", true);
    private static final boolean TRACING_SNAPSHOTS = ConfigReader.getBooleanProperty("tracing.snapshots", true);
    private static final boolean TRACING_SOURCES = ConfigReader.getBooleanProperty("tracing.sources", false);
    private static final String TRACING_OUTPUT_DIR = ConfigReader.getProperty("tracing.output.dir", "target/traces");

    public static void initPlaywright() {
        playwright.set(Playwright.create());
        log.info("Playwright initialized");
    }

    public static Playwright getPlaywright() {
        return playwright.get();
    }

    public static void setBrowser(Browser browserInstance) {
        browser.set(browserInstance);
    }

    public static Browser getBrowser() {
        return browser.get();
    }

    public static void setContext(BrowserContext ctx) {
        context.set(ctx);
    }

    public static BrowserContext getContext() {
        return context.get();
    }

    public static void setPage(Page pg) {
        page.set(pg);
    }

    public static Page getPage() {
        return page.get();
    }

    /**
     * Start tracing for the current test.
     * Call this at the beginning of each test to capture trace data.
     *
     * @param testName Name of the test (used for trace file naming)
     */
    public static void startTracing(String testName) {
        if (!TRACING_ENABLED) {
            log.debug("Tracing is disabled");
            return;
        }

        BrowserContext ctx = context.get();
        if (ctx == null) {
            log.warn("Cannot start tracing: BrowserContext is null");
            return;
        }

        try {
            currentTestName.set(testName);

            Tracing.StartOptions options = new Tracing.StartOptions()
                    .setScreenshots(TRACING_SCREENSHOTS)
                    .setSnapshots(TRACING_SNAPSHOTS)
                    .setSources(TRACING_SOURCES);

            ctx.tracing().start(options);
            tracingStarted.set(true);
            log.debug("Tracing started for test: {}", testName);
        } catch (Exception e) {
            log.warn("Failed to start tracing for test: {}", testName, e);
        }
    }

    /**
     * Stop tracing and save the trace file.
     * Call this at the end of each test (especially on failure).
     *
     * @param saveTrace Whether to save the trace file (typically true on failure)
     * @return Path to the saved trace file, or null if not saved
     */
    public static Path stopTracing(boolean saveTrace) {
        if (!TRACING_ENABLED || !tracingStarted.get()) {
            return null;
        }

        BrowserContext ctx = context.get();
        if (ctx == null) {
            log.warn("Cannot stop tracing: BrowserContext is null");
            tracingStarted.set(false);
            return null;
        }

        Path tracePath = null;
        try {
            if (saveTrace) {
                String testName = currentTestName.get();
                if (testName == null) {
                    testName = "unknown-test";
                }

                // Create unique trace file name
                String sanitizedName = testName.replaceAll("[^a-zA-Z0-9.-]", "_");
                String fileName = String.format("%s_%d.zip", sanitizedName, System.currentTimeMillis());
                tracePath = Paths.get(TRACING_OUTPUT_DIR, fileName);

                // Ensure directory exists
                tracePath.getParent().toFile().mkdirs();

                ctx.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                log.info("Trace saved to: {}", tracePath);
            } else {
                // Stop tracing without saving
                ctx.tracing().stop();
                log.debug("Tracing stopped (not saved)");
            }
        } catch (Exception e) {
            log.warn("Failed to stop tracing", e);
        } finally {
            tracingStarted.set(false);
            currentTestName.remove();
        }

        return tracePath;
    }

    /**
     * Stop tracing and discard the trace (use when test passes).
     */
    public static void discardTracing() {
        stopTracing(false);
    }

    /**
     * Save tracing on failure (use when test fails).
     *
     * @return Path to the saved trace file
     */
    public static Path saveTracingOnFailure() {
        return stopTracing(true);
    }

    /**
     * Check if tracing is enabled
     *
     * @return true if tracing is enabled
     */
    public static boolean isTracingEnabled() {
        return TRACING_ENABLED;
    }

    /**
     * Check if tracing is currently active for this thread
     *
     * @return true if tracing is active
     */
    public static boolean isTracingActive() {
        return tracingStarted.get();
    }

    public static void quitPlaywright() {
        // Stop any active tracing before cleanup
        if (tracingStarted.get()) {
            discardTracing();
        }

        if (page.get() != null) {
            page.get().close();
            page.remove();
        }
        if (context.get() != null) {
            context.get().close();
            context.remove();
        }
        if (browser.get() != null) {
            browser.get().close();
            browser.remove();
        }
        if (playwright.get() != null) {
            playwright.get().close();
            playwright.remove();
        }
        log.info("Playwright quit successfully");
    }
}
