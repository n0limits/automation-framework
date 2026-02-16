package com.automation.utils;

import com.automation.config.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Self-Healing Locator for Playwright - tries fallback locator strategies
 * when the primary locator fails to find a visible element.
 *
 * Similar to Healenium's approach but built natively for Playwright.
 *
 * Usage:
 * <pre>
 *   Locator element = SelfHealingLocator.create(page, "button.submit")
 *       .withText("Submit")
 *       .withRole(AriaRole.BUTTON, "Submit")
 *       .withCss("input[type='submit']")
 *       .find();
 * </pre>
 *
 * @author Victor Grozev
 */
@Slf4j
public class SelfHealingLocator {

    private static final boolean ENABLED = ConfigReader.getBooleanProperty("self.healing.enabled", true);

    private final Page page;
    private final String primarySelector;
    private final List<LocatorStrategy> fallbackStrategies = new ArrayList<>();

    private SelfHealingLocator(Page page, String primarySelector) {
        this.page = page;
        this.primarySelector = primarySelector;
    }

    /**
     * Create a self-healing locator with a primary CSS/text selector
     *
     * @param page             Playwright page instance
     * @param primarySelector  Primary CSS or Playwright selector
     * @return SelfHealingLocator builder
     */
    public static SelfHealingLocator create(Page page, String primarySelector) {
        return new SelfHealingLocator(page, primarySelector);
    }

    /**
     * Add a text-based fallback strategy
     *
     * @param text Visible text to search for
     * @return this builder
     */
    public SelfHealingLocator withText(String text) {
        fallbackStrategies.add(new LocatorStrategy(
                "text['" + text + "']",
                () -> page.getByText(text).first()
        ));
        return this;
    }

    /**
     * Add a text-based fallback with exact match
     *
     * @param text Exact visible text to search for
     * @return this builder
     */
    public SelfHealingLocator withExactText(String text) {
        fallbackStrategies.add(new LocatorStrategy(
                "exactText['" + text + "']",
                () -> page.getByText(text, new Page.GetByTextOptions().setExact(true)).first()
        ));
        return this;
    }

    /**
     * Add a role-based fallback strategy
     *
     * @param role ARIA role
     * @param name Accessible name
     * @return this builder
     */
    public SelfHealingLocator withRole(AriaRole role, String name) {
        fallbackStrategies.add(new LocatorStrategy(
                "role[" + role + ", '" + name + "']",
                () -> page.getByRole(role, new Page.GetByRoleOptions().setName(name)).first()
        ));
        return this;
    }

    /**
     * Add a CSS selector fallback
     *
     * @param cssSelector CSS selector
     * @return this builder
     */
    public SelfHealingLocator withCss(String cssSelector) {
        fallbackStrategies.add(new LocatorStrategy(
                "css['" + cssSelector + "']",
                () -> page.locator(cssSelector).first()
        ));
        return this;
    }

    /**
     * Add a test-id based fallback
     *
     * @param testId data-testid attribute value
     * @return this builder
     */
    public SelfHealingLocator withTestId(String testId) {
        fallbackStrategies.add(new LocatorStrategy(
                "testId['" + testId + "']",
                () -> page.getByTestId(testId).first()
        ));
        return this;
    }

    /**
     * Add a placeholder-based fallback (for inputs)
     *
     * @param placeholder Placeholder text
     * @return this builder
     */
    public SelfHealingLocator withPlaceholder(String placeholder) {
        fallbackStrategies.add(new LocatorStrategy(
                "placeholder['" + placeholder + "']",
                () -> page.getByPlaceholder(placeholder).first()
        ));
        return this;
    }

    /**
     * Add a label-based fallback (for form fields)
     *
     * @param label Label text
     * @return this builder
     */
    public SelfHealingLocator withLabel(String label) {
        fallbackStrategies.add(new LocatorStrategy(
                "label['" + label + "']",
                () -> page.getByLabel(label).first()
        ));
        return this;
    }

    /**
     * Add an XPath fallback
     *
     * @param xpath XPath expression
     * @return this builder
     */
    public SelfHealingLocator withXPath(String xpath) {
        fallbackStrategies.add(new LocatorStrategy(
                "xpath['" + xpath + "']",
                () -> page.locator("xpath=" + xpath).first()
        ));
        return this;
    }

    /**
     * Find the element using primary selector, falling back to alternative strategies
     * if self-healing is enabled and the primary selector fails.
     *
     * @return Locator for the found element
     * @throws com.microsoft.playwright.PlaywrightException if no strategy finds the element
     */
    @Step("Self-healing locator: {primarySelector}")
    public Locator find() {
        // Try primary selector first
        Locator primary = page.locator(primarySelector).first();
        if (isLocatorUsable(primary)) {
            log.debug("Primary selector found element: {}", primarySelector);
            return primary;
        }

        // If self-healing is disabled, return primary locator as-is (Playwright will throw on interaction)
        if (!ENABLED || fallbackStrategies.isEmpty()) {
            log.debug("Self-healing disabled or no fallbacks configured, returning primary: {}", primarySelector);
            return primary;
        }

        // Try fallback strategies
        log.warn("Primary selector failed: '{}' - attempting self-healing", primarySelector);

        for (LocatorStrategy strategy : fallbackStrategies) {
            try {
                Locator fallback = strategy.resolve();
                if (isLocatorUsable(fallback)) {
                    String healMessage = String.format(
                            "SELF-HEALED: '%s' -> %s", primarySelector, strategy.name);
                    log.warn(healMessage);
                    Allure.addAttachment("Self-Healing Event", "text/plain", healMessage);
                    return fallback;
                }
            } catch (Exception e) {
                log.debug("Fallback strategy {} failed: {}", strategy.name, e.getMessage());
            }
        }

        // No fallback worked - return primary and let Playwright throw naturally
        log.error("Self-healing exhausted all {} fallback strategies for: '{}'",
                fallbackStrategies.size(), primarySelector);
        return primary;
    }

    /**
     * Check if a locator points to a usable (existing and visible) element
     */
    private boolean isLocatorUsable(Locator locator) {
        try {
            return locator.count() > 0 && locator.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Internal strategy holder
     */
    private static class LocatorStrategy {
        final String name;
        final LocatorSupplier supplier;

        LocatorStrategy(String name, LocatorSupplier supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        Locator resolve() {
            return supplier.get();
        }
    }

    @FunctionalInterface
    private interface LocatorSupplier {
        Locator get();
    }
}
