package com.automation.utils;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Test class for CharacterFrequencyUtil.
 * Tests cover edge cases, formatting, and various input scenarios.
 */
public class CharacterFrequencyUtilTests {

    @Test
    public void shouldCountCharactersIgnoringSpacesPunctuationAndCase() {
        String input = "Hello, World!!!";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);
        String formatted = CharacterFrequencyUtil.formatFrequency(result);

        System.out.println("Input: \"" + input + "\"");
        System.out.println("Output: " + formatted);

        Assert.assertEquals(result.get('h'), Integer.valueOf(1));
        Assert.assertEquals(result.get('e'), Integer.valueOf(1));
        Assert.assertEquals(result.get('l'), Integer.valueOf(3));
        Assert.assertEquals(result.get('o'), Integer.valueOf(2));
        Assert.assertEquals(result.get('w'), Integer.valueOf(1));
        Assert.assertEquals(result.get('r'), Integer.valueOf(1));
        Assert.assertEquals(result.get('d'), Integer.valueOf(1));
        Assert.assertEquals(formatted, "h:1, e:1, l:3, o:2, w:1, r:1, d:1");
    }

    @Test
    public void shouldHandleEmptyString() {
        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency("");

        Assert.assertTrue(result.isEmpty(), "Empty string should return empty map");
    }

    @Test
    public void shouldHandleNullInput() {
        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(null);

        Assert.assertTrue(result.isEmpty(), "Null input should return empty map");
    }

    @Test
    public void shouldHandleBlankString() {
        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency("   ");

        Assert.assertTrue(result.isEmpty(), "Blank string should return empty map");
    }

    @Test
    public void shouldHandleDigits() {
        String input = "abc123xyz456";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);
        String formatted = CharacterFrequencyUtil.formatFrequency(result);

        System.out.println("Input: \"" + input + "\"");
        System.out.println("Output: " + formatted);

        Assert.assertEquals(result.get('1'), Integer.valueOf(1));
        Assert.assertEquals(result.get('2'), Integer.valueOf(1));
        Assert.assertEquals(result.get('3'), Integer.valueOf(1));
        Assert.assertEquals(result.get('4'), Integer.valueOf(1));
        Assert.assertEquals(result.get('5'), Integer.valueOf(1));
        Assert.assertEquals(result.get('6'), Integer.valueOf(1));
        Assert.assertEquals(formatted, "a:1, b:1, c:1, 1:1, 2:1, 3:1, x:1, y:1, z:1, 4:1, 5:1, 6:1");
    }

    @Test
    public void shouldHandleSpecialCharactersOnly() {
        String input = "!@#$%^&*()";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);

        Assert.assertTrue(result.isEmpty(), "Special characters only should return empty map");
    }

    @Test
    public void shouldHandleSingleRepeatedCharacter() {
        String input = "aaaaaaa";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);
        String formatted = CharacterFrequencyUtil.formatFrequency(result);

        Assert.assertEquals(result.size(), 1, "Should have only one unique character");
        Assert.assertEquals(result.get('a'), Integer.valueOf(7));
        Assert.assertEquals(formatted, "a:7");
    }

    @Test
    public void shouldPreserveOrderOfFirstAppearance() {
        String input = "dcba";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);
        String formatted = CharacterFrequencyUtil.formatFrequency(result);

        Assert.assertEquals(formatted, "d:1, c:1, b:1, a:1", "Order should match first appearance");
    }

    @Test
    public void shouldBeCaseInsensitive() {
        String input = "AaBbCc";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);
        String formatted = CharacterFrequencyUtil.formatFrequency(result);

        Assert.assertEquals(result.get('a'), Integer.valueOf(2));
        Assert.assertEquals(result.get('b'), Integer.valueOf(2));
        Assert.assertEquals(result.get('c'), Integer.valueOf(2));
        Assert.assertNull(result.get('A'), "Uppercase characters should not be counted separately");
        Assert.assertEquals(formatted, "a:2, b:2, c:2");
    }

    @Test
    public void shouldFormatEmptyMapAsEmptyString() {
        Map<Character, Integer> emptyMap = CharacterFrequencyUtil.getCharFrequency("");
        String formatted = CharacterFrequencyUtil.formatFrequency(emptyMap);

        Assert.assertEquals(formatted, "", "Empty map should format as empty string");
    }

    @Test
    public void shouldFormatNullMapAsEmptyString() {
        String formatted = CharacterFrequencyUtil.formatFrequency(null);

        Assert.assertEquals(formatted, "", "Null map should format as empty string");
    }

    @Test
    public void shouldHandleMixedContent() {
        String input = "Test123!@# Case456";

        Map<Character, Integer> result = CharacterFrequencyUtil.getCharFrequency(input);
        String formatted = CharacterFrequencyUtil.formatFrequency(result);

        System.out.println("Input: \"" + input + "\"");
        System.out.println("Output: " + formatted);

        // Verify spaces and special characters are ignored
        Assert.assertNull(result.get(' '), "Spaces should be ignored");
        Assert.assertNull(result.get('!'), "Special characters should be ignored");
        Assert.assertNull(result.get('@'), "Special characters should be ignored");

        // Verify letters and digits are counted
        Assert.assertEquals(result.get('t'), Integer.valueOf(2)); // 'T' and 't' in "Test"
        Assert.assertEquals(result.get('e'), Integer.valueOf(2)); // 'e' in "Test" and "Case"
        Assert.assertEquals(result.get('s'), Integer.valueOf(2)); // 's' in "Test" and "Case"
    }
}
