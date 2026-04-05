package test;

import lexer.Lexer;
import lexer.Token;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Test runner for the Lexer.
 * 
 * Usage:
 *   java test.LexerTestRunner <input_file> <expected_result>  # Direct mode
 *   java test.LexerTestRunner                                  # Interactive menu mode
 *   
 *   expected_result:
 *     - "valid" or "lexically_valid": expects no ERROR tokens (all characters are valid)
 *     - "invalid": expects ERROR tokens (contains gibberish/invalid characters)
 *
 * NOTE: This tests LEXICAL correctness only (are characters valid tokens?).
 *       It does NOT test syntactic correctness (is the grammar correct?).
 */
public class LexerTestRunner {

    private static final String TEST_INPUTS_DIR = "SyntaxChecker/test/inputs";

    public static void main(String[] args) {
        // If no arguments provided, show interactive menu
        if (args.length == 0) {
            runInteractiveMode();
            return;
        }

        // Old direct mode: run with provided arguments
        runDirectMode(args);
    }

    /**
     * Interactive menu mode - allows user to select files and options
     */
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("     LEXER TEST RUNNER - Main Menu      ");
        System.out.println("========================================");
        System.out.println();

        // Menu loop
        while (true) {
            System.out.println("Select an option:");
            System.out.println("  1. Run all valid test cases");
            System.out.println("  2. Run all invalid test cases");
            System.out.println("  3. Run all test cases (valid + invalid)");
            System.out.println("  4. Run a specific valid test case");
            System.out.println("  5. Run a specific invalid test case");
            System.out.println("  6. Run a custom input file");
            System.out.println("  0. Exit");
            System.out.println();
            System.out.print("Enter choice [0-6]: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "0":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                case "1":
                    runTestBatch("valid", scanner);
                    break;

                case "2":
                    runTestBatch("invalid", scanner);
                    break;

                case "3":
                    System.out.println();
                    System.out.println("=== Running all valid tests ===");
                    runTestBatch("valid", scanner);
                    System.out.println();
                    System.out.println("=== Running all invalid tests ===");
                    runTestBatch("invalid", scanner);
                    break;

                case "4":
                    runSpecificTest("valid", scanner);
                    break;

                case "5":
                    runSpecificTest("invalid", scanner);
                    break;

                case "6":
                    runCustomFile(scanner);
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number between 0 and 6.");
                    System.out.println();
                    break;
            }
        }
    }

    /**
     * Runs a batch of test files (all valid or all invalid)
     */
    private static void runTestBatch(String testType, Scanner scanner) {
        File testDir = new File(TEST_INPUTS_DIR + "/" + testType);
        if (!testDir.exists() || !testDir.isDirectory()) {
            System.out.println("Error: Test directory not found: " + testDir.getPath());
            return;
        }

        File[] testFiles = testDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (testFiles == null || testFiles.length == 0) {
            System.out.println("No test files found in: " + testDir.getPath());
            return;
        }

        // Sort files by name
        java.util.Arrays.sort(testFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));

        int passed = 0;
        int failed = 0;

        for (File testFile : testFiles) {
            System.out.println();
            System.out.println("Testing: " + testFile.getName());

            // Determine expected result based on test type
            String expectedResult = testType.equals("valid") ? "valid" : "invalid";

            try {
                String code = new String(Files.readAllBytes(Paths.get(testFile.getPath())));
                System.out.println("  Input: " + code.trim().replace("\n", "\\n"));
                System.out.println();

                // Create lexer and get tokens
                Lexer lexer = new Lexer(code);
                List<Token> tokens = lexer.getTokens();

                // Display tokens
                System.out.println("  Tokens produced:");
                for (Token token : tokens) {
                    System.out.println("    " + token);
                }
                System.out.println();

                // Check for basic validity
                boolean hasEOF = tokens.stream().anyMatch(t -> t.isEOF());
                boolean hasErrors = tokens.stream()
                        .anyMatch(t -> t.getType().toString().equals("ERROR"));

                boolean testPassed = false;
                if (expectedResult.equals("valid") || expectedResult.equals("lexically_valid")) {
                    if (hasEOF && !hasErrors) {
                        System.out.println("  ✓ PASS: Lexer produced valid tokens as expected");
                        testPassed = true;
                    } else {
                        System.out.println("  ✗ FAIL: Lexer should have produced valid tokens");
                        testPassed = false;
                    }
                } else if (expectedResult.equals("invalid")) {
                    if (hasErrors) {
                        System.out.println("  ✓ PASS: Lexer detected invalid input as expected");
                        testPassed = true;
                    } else {
                        System.out.println("  ✗ FAIL: Lexer should have detected invalid input");
                        testPassed = false;
                    }
                }

                if (testPassed) {
                    passed++;
                } else {
                    failed++;
                }

            } catch (IOException e) {
                System.err.println("  Error reading file: " + testFile.getPath());
                System.err.println("  " + e.getMessage());
                failed++;
            }

            System.out.println();
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
        }

        // Summary
        System.out.println();
        System.out.println("========================================");
        System.out.println("          Batch Test Summary            ");
        System.out.println("========================================");
        System.out.println("Total tests: " + testFiles.length);
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("========================================");
        System.out.println();
    }

    /**
     * Runs a specific test file from the given category
     */
    private static void runSpecificTest(String testType, Scanner scanner) {
        File testDir = new File(TEST_INPUTS_DIR + "/" + testType);
        if (!testDir.exists() || !testDir.isDirectory()) {
            System.out.println("Error: Test directory not found: " + testDir.getPath());
            return;
        }

        File[] testFiles = testDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (testFiles == null || testFiles.length == 0) {
            System.out.println("No test files found in: " + testDir.getPath());
            return;
        }

        // Sort and display test files
        java.util.Arrays.sort(testFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));

        System.out.println();
        System.out.println("Available " + testType + " test cases:");
        for (int i = 0; i < testFiles.length; i++) {
            System.out.println("  " + (i + 1) + ". " + testFiles[i].getName());
        }
        System.out.println();
        System.out.print("Select test number [1-" + testFiles.length + "] (0 to cancel): ");

        String selection = scanner.nextLine().trim();
        int testIndex;
        try {
            testIndex = Integer.parseInt(selection);
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection.");
            System.out.println();
            return;
        }

        if (testIndex == 0) {
            System.out.println();
            return;
        }

        if (testIndex < 1 || testIndex > testFiles.length) {
            System.out.println("Invalid selection.");
            System.out.println();
            return;
        }

        File selectedFile = testFiles[testIndex - 1];

        // Ask for expected result
        System.out.println();
        System.out.println("Select expected result:");
        System.out.println("  1. Valid (no ERROR tokens expected)");
        System.out.println("  2. Invalid (ERROR tokens expected)");
        System.out.print("Enter choice [1-2]: ");

        String resultChoice = scanner.nextLine().trim();
        String expectedResult;
        if (resultChoice.equals("1")) {
            expectedResult = "valid";
        } else if (resultChoice.equals("2")) {
            expectedResult = "invalid";
        } else {
            System.out.println("Invalid choice. Defaulting to 'valid'.");
            expectedResult = "valid";
        }

        System.out.println();
        runSingleTest(selectedFile, expectedResult, scanner);
    }

    /**
     * Runs a custom input file specified by the user
     */
    private static void runCustomFile(Scanner scanner) {
        System.out.println();
        System.out.print("Enter path to input file: ");
        String filePath = scanner.nextLine().trim();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Error: File not found: " + filePath);
            System.out.println();
            return;
        }

        // Ask for expected result
        System.out.println();
        System.out.println("Select expected result:");
        System.out.println("  1. Valid (no ERROR tokens expected)");
        System.out.println("  2. Invalid (ERROR tokens expected)");
        System.out.print("Enter choice [1-2]: ");

        String resultChoice = scanner.nextLine().trim();
        String expectedResult;
        if (resultChoice.equals("1")) {
            expectedResult = "valid";
        } else if (resultChoice.equals("2")) {
            expectedResult = "invalid";
        } else {
            System.out.println("Invalid choice. Defaulting to 'valid'.");
            expectedResult = "valid";
        }

        System.out.println();
        runSingleTest(file, expectedResult, scanner);
    }

    /**
     * Runs a single test file and displays results
     */
    private static void runSingleTest(File file, String expectedResult, Scanner scanner) {
        try {
            String code = new String(Files.readAllBytes(Paths.get(file.getPath())));

            System.out.println("Input: " + code.trim().replace("\n", "\\n"));
            System.out.println();

            // Create lexer and get tokens
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.getTokens();

            // Display tokens
            System.out.println("Tokens produced:");
            for (Token token : tokens) {
                System.out.println("  " + token);
            }
            System.out.println();

            // Check for basic validity
            boolean hasEOF = tokens.stream().anyMatch(t -> t.isEOF());
            boolean hasErrors = tokens.stream()
                    .anyMatch(t -> t.getType().toString().equals("ERROR"));

            if (expectedResult.equals("valid") || expectedResult.equals("lexically_valid")) {
                if (hasEOF && !hasErrors) {
                    System.out.println("✓ PASS: Lexer produced valid tokens as expected");
                } else {
                    System.out.println("✗ FAIL: Lexer should have produced valid tokens");
                }
            } else if (expectedResult.equals("invalid")) {
                if (hasErrors) {
                    System.out.println("✓ PASS: Lexer detected invalid input as expected");
                } else {
                    System.out.println("✗ FAIL: Lexer should have detected invalid input");
                }
            } else {
                System.err.println("Invalid expected_result. Use 'valid', 'lexically_valid', or 'invalid'");
            }

            System.out.println();
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            System.out.println();

        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getPath());
            System.err.println(e.getMessage());
        }
    }

    /**
     * Direct mode - runs with command-line arguments (old behavior)
     */
    private static void runDirectMode(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java test.LexerTestRunner <input_file> <expected_result>");
            System.err.println("  expected_result: 'valid' or 'invalid'");
            System.exit(1);
        }

        String inputFile = args[0];
        String expectedResult = args[1].toLowerCase();

        try {
            // Read the input file
            String code = new String(Files.readAllBytes(Paths.get(inputFile)));

            System.out.println("Input: " + code.trim().replace("\n", "\\n"));
            System.out.println();

            // Create lexer and get tokens
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.getTokens();

            // Display tokens
            System.out.println("Tokens produced:");
            for (Token token : tokens) {
                System.out.println("  " + token);
            }
            System.out.println();

            // Check for basic validity
            boolean hasEOF = tokens.stream().anyMatch(t -> t.isEOF());
            boolean hasErrors = tokens.stream()
                    .anyMatch(t -> t.getType().toString().equals("ERROR"));

            if (expectedResult.equals("valid") || expectedResult.equals("lexically_valid")) {
                if (hasEOF && !hasErrors) {
                    System.out.println("✓ PASS: Lexer produced valid tokens as expected");
                    System.exit(0);
                } else {
                    System.out.println("✗ FAIL: Lexer should have produced valid tokens");
                    System.exit(1);
                }
            } else if (expectedResult.equals("invalid")) {
                if (hasErrors) {
                    System.out.println("✓ PASS: Lexer detected invalid input (gibberish characters) as expected");
                    System.exit(0);
                } else {
                    System.out.println("✗ FAIL: Lexer should have detected invalid input (gibberish characters)");
                    System.exit(1);
                }
            } else {
                System.err.println("Invalid expected_result. Use 'valid', 'lexically_valid', or 'invalid'");
                System.exit(1);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + inputFile);
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
