# Character Frequency Utility

## Overview
This utility counts character occurrences in a string while following strict rules commonly required in automation and interview tasks.

## Location
- **Utility Class**: `src/main/java/com/automation/utils/CharacterFrequencyUtil.java`
- **Test Class**: `src/test/java/com/automation/utils/CharacterFrequencyUtilTests.java`

## Rules Applied
- **Case-insensitive**: 'A' and 'a' are treated as the same character
- **Spaces ignored**: All whitespace characters are excluded from counting
- **Punctuation ignored**: Special characters are not counted
- **Letters and digits only**: Only alphanumeric characters are counted
- **Order preserved**: Results maintain the order of first appearance

## Implementation Details

### Data Structure
- Uses `LinkedHashMap` to preserve insertion order
- Key: Character (lowercase)
- Value: Frequency count

### Character Filtering
- Uses `Character.isLetterOrDigit()` to filter valid characters
- All input is normalized to lowercase before processing

### Complexity Analysis
- **Time Complexity**: O(n) where n is the length of the input string
- **Space Complexity**: O(k) where k is the number of unique characters

## Usage

### Basic Usage
```java
Map<Character, Integer> result =
    CharacterFrequencyUtil.getCharFrequency("Hello, World!");

String formatted =
    CharacterFrequencyUtil.formatFrequency(result);

System.out.println(formatted);
// Output: h:1, e:1, l:3, o:2, w:1, r:1, d:1
```

### Example Inputs and Outputs

| Input | Output |
|-------|--------|
| "Hello, World!!!" | h:1, e:1, l:3, o:2, w:1, r:1, d:1 |
| "abc123xyz456" | a:1, b:1, c:1, 1:1, 2:1, 3:1, x:1, y:1, z:1, 4:1, 5:1, 6:1 |
| "AaBbCc" | a:2, b:2, c:2 |
| "aaaaaaa" | a:7 |
| "!@#$%^&*()" | (empty) |
| "" | (empty) |
| null | (empty) |

## Edge Cases Handled

1. **Null input**: Returns empty map
2. **Empty string**: Returns empty map
3. **Blank string** (spaces only): Returns empty map
4. **Special characters only**: Returns empty map
5. **Single repeated character**: Correctly counts all occurrences
6. **Mixed case**: Converts to lowercase and counts together
7. **Alphanumeric mix**: Handles both letters and digits

## Test Coverage

The test suite includes:
- Basic functionality with mixed content
- Edge cases (null, empty, blank)
- Digit handling
- Special characters handling
- Single character repetition
- Order preservation verification
- Case sensitivity verification
- Format method edge cases

### Running Tests
```bash
mvn test -Dtest=CharacterFrequencyUtilTests
```

## Assumptions

1. **Case Sensitivity**: The implementation is case-insensitive by design
2. **Whitespace**: All whitespace (spaces, tabs, newlines) is ignored
3. **Special Characters**: Punctuation and symbols are not counted
4. **Digits**: Numbers are included in the character count
5. **Unicode**: Standard Java character handling applies
6. **Null Safety**: Null inputs are handled gracefully

## Design Decisions

### Why LinkedHashMap?
- Preserves insertion order (order of first appearance)
- O(1) average time for put and get operations
- Perfect for maintaining character order while counting

### Why Character.isLetterOrDigit()?
- Standard Java method for identifying alphanumeric characters
- Handles Unicode characters correctly
- Clear and readable intent

### Why Private Constructor?
- Prevents instantiation of utility class
- Follows best practices for utility classes
- Throws `UnsupportedOperationException` if attempted

## Future Enhancements (Not Currently Implemented)

These are potential improvements that could be added if needed:
- Support for case-sensitive mode via parameter
- Support for including/excluding spaces via parameter
- Support for custom character filters
- Performance optimizations for very large strings
- Streaming support for processing large files

## Related Files
- Main utility: `src/main/java/com/automation/utils/CharacterFrequencyUtil.java`
- Test suite: `src/test/java/com/automation/utils/CharacterFrequencyUtilTests.java`
