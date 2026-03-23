#!/bin/bash
# Lexer Test Runner for Unix/Linux/Mac
# This script tests the lexer against all test input files

echo "============================================"
echo "Lexer Test Suite"
echo "============================================"
echo ""

# Set paths
LEXER_DIR="SyntaxChecker/lexer"
TEST_DIR="SyntaxChecker/test/inputs"
TEST_RUNNER="SyntaxChecker/test/LexerTestRunner.java"

# Compile the test runner and lexer
echo "Compiling lexer and test runner..."
javac "$LEXER_DIR/TokenType.java" "$LEXER_DIR/Token.java" "$LEXER_DIR/Lexer.java" "$TEST_RUNNER"
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi
echo "Compilation successful."
echo ""

# Track test results
passed=0
failed=0

# Test valid programs
echo "============================================"
echo "Testing Valid Programs"
echo "============================================"
for file in "$TEST_DIR"/valid/*.txt; do
    echo ""
    echo "[TEST] $(basename "$file")"
    java -cp SyntaxChecker test.LexerTestRunner "$file" valid
    if [ $? -eq 0 ]; then
        ((passed++))
    else
        ((failed++))
    fi
done

# Test invalid programs
echo ""
echo "============================================"
echo "Testing Invalid Programs (should report errors)"
echo "============================================"
for file in "$TEST_DIR"/invalid/*.txt; do
    echo ""
    echo "[TEST] $(basename "$file")"
    java -cp SyntaxChecker test.LexerTestRunner "$file" invalid
    if [ $? -eq 0 ]; then
        ((passed++))
    else
        ((failed++))
    fi
done

echo ""
echo "============================================"
echo "Test Summary"
echo "============================================"
echo "Passed: $passed"
echo "Failed: $failed"
echo ""

if [ $failed -eq 0 ]; then
    echo "All tests completed!"
    exit 0
else
    echo "Some tests failed!"
    exit 1
fi
