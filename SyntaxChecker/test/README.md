# Lexer Test Suite

This directory contains test files and scripts for testing the Lexer component.

## Directory Structure

```
test/
├── inputs/
│   ├── valid/          # Test cases that should tokenize successfully
│   │   ├── test01_simple_assignment.txt
│   │   ├── test02_addition.txt
│   │   └── ...
│   └── invalid/        # Test cases that should produce errors
│       ├── test01_missing_semicolon.txt
│       ├── test02_missing_equals.txt
│       └── ...
├── LexerTestRunner.java    # Test runner class
├── run_tests.bat           # Windows test runner script
└── run_tests.sh            # Unix/Linux/Mac test runner script
```

## Running the Tests

### Windows

```batch
cd D:\Dev\Projects\Theory-of-Computation-Group-Project
SyntaxChecker\test\run_tests.bat
```

### Unix/Linux/Mac

```bash
cd /path/to/Theory-of-Computation-Group-Project
chmod +x SyntaxChecker/test/run_tests.sh
./SyntaxChecker/test/run_tests.sh
```

### Manual Testing

1. Compile the lexer:
   ```bash
   javac SyntaxChecker/lexer/TokenType.java SyntaxChecker/lexer/Token.java SyntaxChecker/lexer/Lexer.java
   ```

2. Run a specific test:
   ```bash
   java -cp SyntaxChecker test.LexerTestRunner SyntaxChecker/test/inputs/valid/test01_simple_assignment.txt valid
   ```

## Test Cases

### Valid Programs

| Test File | Description | Input |
|-----------|-------------|-------|
| test01_simple_assignment.txt | Simple assignment | `x = 5;` |
| test02_addition.txt | Addition expression | `result = 10 + 20;` |
| test03_precedence.txt | Operator precedence | `y = 2 + 3 * 4;` |
| test04_parentheses.txt | Parentheses override precedence | `temp = (2 + 3) * 4;` |
| test05_subtraction_division.txt | Subtraction and division | `a = 10 - 4 / 2;` |
| test06_complex_expression.txt | Complex expression | `z = a + b * c - d / e;` |
| test07_nested_parentheses.txt | Nested parentheses | `x = ((1 + 2) * 3) + 4;` |
| test08_identifier_with_underscore.txt | Identifier with underscore | `my_var = 100;` |
| test09_multi_digit_number.txt | Multi-digit number | `value = 12345;` |
| test10_all_operators.txt | All operators | `x = 1 + 2 - 3 * 4 / 5;` |

### Invalid Programs

| Test File | Description | Input | Expected Error |
|-----------|-------------|-------|----------------|
| test01_missing_semicolon.txt | Missing semicolon | `x = 5` | Lexer succeeds, parser should fail |
| test02_missing_equals.txt | Missing equals sign | `x 5;` | Lexer succeeds, parser should fail |
| test03_missing_identifier.txt | Missing identifier | `= 5;` | Lexer succeeds, parser should fail |
| test04_missing_expression.txt | Missing expression | `x = ;` | Lexer succeeds, parser should fail |
| test05_incomplete_expression.txt | Incomplete expression | `x = 5 +;` | Lexer succeeds, parser should fail |
| test06_unclosed_parenthesis.txt | Unclosed parenthesis | `x = (2 + 3;` | Lexer succeeds, parser should fail |
| test07_unmatched_closing_parenthesis.txt | Unmatched closing paren | `x = 2 + 3);` | Lexer succeeds, parser should fail |
| test08_invalid_identifier.txt | Invalid identifier (starts with digit) | `123abc = 5;` | Lexer produces NUMBER then IDENTIFIER |
| test09_extra_semicolon.txt | Extra semicolon | `x = 5;;` | Lexer succeeds, parser may fail |
| test10_invalid_character.txt | Invalid character | `x = @5;` | Lexer produces ERROR token |

## Expected Behavior

### For Valid Tests
- Lexer should produce a sequence of valid tokens
- Final token should be EOF
- No ERROR tokens should be produced

### For Invalid Tests
- Some tests will produce ERROR tokens (e.g., invalid characters)
- Other tests will tokenize successfully but should fail at the parser level
- The test runner checks for ERROR tokens in the output

## Adding New Tests

1. Create a new `.txt` file in either `valid/` or `invalid/`
2. Add the test input code
3. Use descriptive naming: `testXX_description.txt`
4. Update this README if adding a new category of tests
