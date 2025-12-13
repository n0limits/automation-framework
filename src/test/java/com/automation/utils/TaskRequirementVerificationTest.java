package com.automation.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * This test verifies that the implementation meets the exact requirements
 * from Task 2: String Character Frequency
 */
public class TaskRequirementVerificationTest {

    @Test
    public void shouldMatchExactTaskExample() {
        // Exact example from the task requirements
        String input = "hello world";
        String expectedOutput = "h:1, e:1, l:3, o:2, w:1, r:1, d:1";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);
        String actualOutput = CharacterFrequencyUtil.formatFrequency(result);

        System.out.println("Task Requirement Verification");
        System.out.println("==============================");
        System.out.println("Input:    \"" + input + "\"");
        System.out.println("Expected: " + expectedOutput);
        System.out.println("Actual:   " + actualOutput);
        System.out.println("Match:    " + expectedOutput.equals(actualOutput));

        Assert.assertEquals(actualOutput, expectedOutput,
                "Output should match the exact example from task requirements");
    }
}
