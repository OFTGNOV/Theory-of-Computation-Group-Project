# Test Suite Documentation

This directory contains test files and scripts for testing the **Lexer** and **Parser** components of the Syntax Checker.

## Directory Structure

```
test/
├── inputs/
│   ├── valid/          # Test cases that should pass (10 files)
│   │   ├── test01_simple_assignment.txt
│   │   ├── test02_addition.txt
│   │   ├── test03_precedence.txt
│   │   ├── test04_parentheses.txt
│   │   ├── test05_subtraction_division.txt
│   │   ├── test06_complex_expression.txt
│   │   ├── test07_nested_parentheses.txt
│   │   ├── test08_identifier_with_underscore.txt
│   │   ├── test09_multi_digit_number.txt
│   │   └── test10_all_operators.txt
│   └── invalid/        # Test cases that should fail (10 files)
│       ├── test01_missing_semicolon.txt
│       ├── test02_missing_equals.txt
│       ├── test03_missing_identifier.txt
│       ├── test04_missing_expression.txt
│       ├── test05_incomplete_expression.txt
│       ├── test06_unclosed_parenthesis.txt
│       ├── test07_unmatched_closing_parenthesis.txt
│       ├── test08_invalid_identifier.txt
│       ├── test09_extra_semicolon.txt
│       └── test10_invalid_character.txt
├── LexerTestRunner.java    # Lexer test runner with interactive menu
├── README.md               # This file
├── run_tests.bat           # Windows test runner script
└── run_tests.sh            # Unix/Linux/Mac test runner script
```

## Running the Tests

### **Interactive Menu Mode** (Recommended)

The test runners now feature interactive menus that let you select which tests to run without typing long commands.

#### **Lexer Test Runner**

```bash
java -cp SyntaxChecker/bin test.LexerTestRunner
```

**Menu Options:**
1. **Run all valid test cases** - Tests all 10 valid test files
2. **Run all invalid test cases** - Tests all 10 invalid test files
3. **Run all test cases** - Runs both valid and invalid tests
4. **Run a specific valid test case** - Select from numbered list
5. **Run a specific invalid test case** - Select from numbered list
6. **Run a custom input file** - Enter your own file path
0. **Exit**

#### **Full Syntax Checker (Parser)**

```bash
java -cp SyntaxChecker/bin main.Main
```

**Menu Options:**
1. **Run all valid test cases** - Parses all 10 valid test files
2. **Run all invalid test cases** - Parses all 10 invalid test files
3. **Run all test cases** - Runs both valid and invalid tests
4. **Run a specific valid test case** - Select from numbered list (with trace option)
5. **Run a specific invalid test case** - Select from numbered list (with trace option)
6. **Run a custom input file** - Enter your own file path (with trace option)
0. **Exit**

### **Direct Mode** (For scripting/automation)

If you provide command-line arguments, the test runners skip the menu and run directly.

#### **Lexer Test Runner**

```bash
java -cp SyntaxChecker/bin test.LexerTestRunner <input_file> <expected_result>
```

Where `<expected_result>` is either:
- `valid` or `lexically_valid` - expects no ERROR tokens
- `invalid` - expects ERROR tokens

**Example:**
```bash
java -cp SyntaxChecker/bin test.LexerTestRunner SyntaxChecker/test/inputs/valid/test01_simple_assignment.txt valid
```

#### **Full Syntax Checker (Parser)**

```bash
java -cp SyntaxChecker/bin main.Main <input_file> [--trace]
```

Where:
- `<input_file>` - Path to the source file to parse
- `--trace` (optional) - Enable parsing trace output for debugging

**Examples:**
```bash
# Check a valid program
java -cp SyntaxChecker/bin main.Main SyntaxChecker/test/inputs/valid/test01_simple_assignment.txt

# Check with parsing trace enabled
java -cp SyntaxChecker/bin main.Main SyntaxChecker/test/inputs/valid/test03_precedence.txt --trace

# Check an invalid program
java -cp SyntaxChecker/bin main.Main SyntaxChecker/test/inputs/invalid/test01_missing_semicolon.txt
```

### **Batch Test Scripts**

For running all tests automatically:

**Windows:**
```batch
SyntaxChecker\test\run_tests.bat
```

**Unix/Linux/Mac:**
```bash
chmod +x SyntaxChecker/test/run_tests.sh
./SyntaxChecker/test/run_tests.sh
```

## Test Cases

### Valid Programs (10 tests)

These files contain syntactically correct assignment statements that should be **accepted** by both the lexer and parser.

| Test File                             | Description                     | Input                    | Expected Result |
| ------------------------------------- | ------------------------------- | ------------------------ | --------------- |
| test01_simple_assignment.txt          | Simple assignment               | `x = 5;`                 | ✅ PASS         |
| test02_addition.txt                   | Addition expression             | `result = 10 + 20;`      | ✅ PASS         |
| test03_precedence.txt                 | Operator precedence             | `y = 2 + 3 * 4;`         | ✅ PASS         |
| test04_parentheses.txt                | Parentheses override precedence | `temp = (2 + 3) * 4;`    | ✅ PASS         |
| test05_subtraction_division.txt       | Subtraction and division        | `a = 10 - 4 / 2;`        | ✅ PASS         |
| test06_complex_expression.txt         | Complex expression              | `z = a + b * c - d / e;` | ✅ PASS         |
| test07_nested_parentheses.txt         | Nested parentheses              | `x = ((1 + 2) * 3) + 4;` | ✅ PASS         |
| test08_identifier_with_underscore.txt | Identifier with underscore      | `my_var = 100;`          | ✅ PASS         |
| test09_multi_digit_number.txt         | Multi-digit number              | `value = 12345;`         | ✅ PASS         |
| test10_all_operators.txt              | All operators                   | `x = 1 + 2 - 3 * 4 / 5;` | ✅ PASS         |

### Invalid Programs (10 tests)

These files contain syntax errors that should be **detected and reported** by the lexer or parser.

| Test File                                | Description                            | Input         | Expected Error                        | Component |
| ---------------------------------------- | -------------------------------------- | ------------- | ------------------------------------- | --------- |
| test01_missing_semicolon.txt             | Missing semicolon                      | `x = 5`       | Expected ';' at end of assignment     | Parser    |
| test02_missing_equals.txt                | Missing equals sign                    | `x 5;`        | Expected '=' after identifier         | Parser    |
| test03_missing_identifier.txt            | Missing identifier                     | `= 5;`        | Expected identifier before '='        | Parser    |
| test04_missing_expression.txt            | Missing expression                     | `x = ;`       | Expected number, identifier, or '('   | Parser    |
| test05_incomplete_expression.txt         | Incomplete expression                  | `x = 5 +;`    | Expected number, identifier, or '('   | Parser    |
| test06_unclosed_parenthesis.txt          | Unclosed parenthesis                   | `x = (2 + 3;` | Expected ')' to close expression      | Parser    |
| test07_unmatched_closing_parenthesis.txt | Unmatched closing paren                | `x = 2 + 3);` | Expected identifier or end of input   | Parser    |
| test08_invalid_identifier.txt            | Invalid identifier (starts with digit) | `123abc = 5;` | Lexer produces NUMBER then IDENTIFIER | Lexer     |
| test09_extra_semicolon.txt               | Extra semicolon                        | `x = 5;;`     | Unexpected token after statement      | Parser    |
| test10_invalid_character.txt             | Invalid character                      | `x = @5;`     | Unexpected character '@'              | Lexer     |

## Expected Behavior

### For Valid Tests

**Lexer:**
- Produces a sequence of valid tokens
- Final token is EOF
- No ERROR tokens are produced

**Parser:**
- Successfully builds a parse tree
- No errors reported
- Parse tree can be printed showing the hierarchical structure

### For Invalid Tests

**Lexer Errors:**
- Invalid characters (e.g., `@`, `#`, `$`) produce ERROR tokens
- Tokenization continues after errors to find all issues

**Parser Errors:**
- Missing required tokens (e.g., `=`, `;`, identifiers)
- Incomplete expressions
- Mismatched parentheses
- Extra tokens after valid statements
- Parser uses **panic-mode recovery** to continue after errors and report multiple issues

## Adding New Tests

1. Create a new `.txt` file in either `valid/` or `invalid/`
2. Add the test input code
3. Use descriptive naming: `testXX_description.txt`
4. Update this README to document the new test
5. Run the interactive test runner to verify

## Implementation Details

### Lexer Test Runner

- Tests **lexical correctness** only (are characters valid tokens?)
- Does not test syntactic correctness (grammar rules)
- Checks for ERROR tokens and EOF presence
- Reports PASS/FAIL based on expected vs actual results

### Parser (Main Syntax Checker)

- Tests **syntactic correctness** (does the input follow grammar rules?)
- Builds a complete parse tree for valid inputs
- Reports detailed error messages with line and column numbers
- Supports **trace mode** to see parsing steps for debugging
- Uses **recursive descent** parsing with panic-mode error recovery

### Batch Testing

Both test runners support batch mode that:
- Runs all tests in a category automatically
- Shows pass/fail summary with counts
- Displays individual results for detailed inspection
- Allows pause between tests for review
