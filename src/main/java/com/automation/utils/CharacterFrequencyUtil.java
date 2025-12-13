package com.automation.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for counting character frequency in a string.
 *
 * Rules Applied:
 * - Case-insensitive (A == a)
 * - Ignores spaces
 * - Ignores punctuation
 * - Only counts letters and digits
 * - Preserves order of first appearance
 *
 * Complexity:
 * - Time complexity: O(n) where n = string length
 * - Space complexity: O(k) where k = unique characters
 *
 * Example:
 * Input: "Hello World"
 * Output: h:1, e:1, l:3, o:2, w:1, r:1, d:1
 */
public final class CharacterFrequencyUtil {

    // Prevent instantiation
    private CharacterFrequencyUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Counts character occurrences based on defined rules.
     *
     * @param input input string (can be null)
     * @return map of character to frequency (ordered by first appearance)
     */
    public static Map<Character, Integer> getCharFrequency(String input) {
        Map<Character, Integer> frequencyMap = new LinkedHashMap<>();

        if (input == null || input.isBlank()) {
            return frequencyMap;
        }

        String normalized = input.toLowerCase();

        for (char c : normalized.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }
        }

        return frequencyMap;
    }

    /**
     * Formats the frequency map as a readable string.
     *
     * @param frequencyMap map of character to frequency
     * @return formatted string (e.g., "h:1, e:1, l:3")
     */
    public static String formatFrequency(Map<Character, Integer> frequencyMap) {
        if (frequencyMap == null || frequencyMap.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            sb.append(entry.getKey())
              .append(":")
              .append(entry.getValue())
              .append(", ");
        }

        // Remove trailing comma and space
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }
}
