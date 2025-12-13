package com.automation.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG listener that automatically attaches RetryAnalyzer to all test methods
 * This eliminates the need to manually add retryAnalyzer to each @Test annotation
 */
@Slf4j
public class RetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Only apply to test methods (not constructors)
        if (testMethod != null) {
            // Set RetryAnalyzer for all tests
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
            log.debug("RetryAnalyzer attached to test: {}.{}",
                testClass != null ? testClass.getSimpleName() : "Unknown",
                testMethod.getName());
        }
    }
}
