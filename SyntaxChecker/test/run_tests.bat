@echo off
REM Lexer Test Runner for Windows
REM This script tests the lexer against all test input files

setlocal enabledelayedexpansion

echo ============================================
echo Lexer Test Suite
echo ============================================
echo.

REM Get the directory where this batch file is located
set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%~dp0.."

REM Set paths (relative to script location)
set LEXER_DIR=%PROJECT_DIR%\lexer
set TEST_DIR=%PROJECT_DIR%\test\inputs
set TEST_RUNNER=%SCRIPT_DIR%LexerTestRunner.java

REM Compile the test runner and lexer
echo Compiling lexer and test runner...
mkdir %PROJECT_DIR%\bin 2>nul
javac -sourcepath %PROJECT_DIR% -d %PROJECT_DIR%\bin %LEXER_DIR%\TokenType.java %LEXER_DIR%\Token.java %LEXER_DIR%\Lexer.java %TEST_RUNNER%
if errorlevel 1 (
    echo Compilation failed!
    exit /b 1
)
echo Compilation successful.
echo.

REM Track test results
set passed=0
set failed=0

REM Test valid programs
echo ============================================
echo Testing Valid Programs
echo ============================================
for %%f in (%TEST_DIR%\valid\*.txt) do (
    echo.
    echo [TEST] %%~nxf
    java -cp %PROJECT_DIR%\bin test.LexerTestRunner "%%f" valid
    if errorlevel 1 (
        set /a failed+=1
    ) else (
        set /a passed+=1
    )
)

REM Test invalid programs (grammar errors, but lexically valid)
echo.
echo ============================================
echo Testing Invalid Programs (grammar errors - should tokenize successfully)
echo ============================================
echo NOTE: These tests check LEXICAL correctness only.
echo       PASS = Lexer successfully tokenized the input (no gibberish characters)
echo       FAIL = Lexer could not properly tokenize (found invalid characters)
echo ============================================
for %%f in (%TEST_DIR%\invalid\*.txt) do (
    echo.
    echo [TEST] %%~nxf
    java -cp %PROJECT_DIR%\bin test.LexerTestRunner "%%f" lexically_valid
    if errorlevel 1 (
        set /a failed+=1
    ) else (
        set /a passed+=1
    )
)

echo.
echo ============================================
echo Test Summary
echo ============================================
echo Passed: %passed%
echo Failed: %failed%
echo.

if %failed% == 0 (
    echo All tests completed!
    exit /b 0
) else (
    echo Some tests failed!
    exit /b 1
)
