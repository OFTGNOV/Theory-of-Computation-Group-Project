/**
 * Tamai Richards
 * March 30, 2026
 */
package main;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import parser.ParseError;
import parser.ParseTree;
import parser.Parser;
import util.ErrorReporter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Syntax Checker.
 *
 * Usage:
 *   java main.Main <input_file> [--trace]    # Direct mode (no menu)
 *   java main.Main                            # Interactive menu mode
 *
 * Options:
 *   --trace    Enable parsing trace output for debugging
 */
public class Main {

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
        System.out.println("       SYNTAX CHECKER - Main Menu       ");
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
                    runTestBatch("valid", false, scanner);
                    break;

                case "2":
                    runTestBatch("invalid", false, scanner);
                    break;

                case "3":
                    System.out.println();
                    System.out.println("=== Running all valid tests ===");
                    runTestBatch("valid", true, scanner);
                    System.out.println();
                    System.out.println("=== Running all invalid tests ===");
                    runTestBatch("invalid", true, scanner);
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
    private static void runTestBatch(String testType, boolean silent, Scanner scanner) {
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
            if (!silent) {
                System.out.println();
                System.out.println("Testing: " + testFile.getName());
            }

            boolean traceEnabled = false;
            if (!silent) {
                System.out.print("  Enable trace? (y/n) [n]: ");
                String traceChoice = scanner.nextLine().trim().toLowerCase();
                traceEnabled = traceChoice.equals("y") || traceChoice.equals("yes");
            }

            try {
                String code = new String(Files.readAllBytes(Paths.get(testFile.getPath())));
                
                if (!silent) {
                    System.out.println("  Input: " + code.trim().replace("\n", "\\n"));
                }

                // Lexical Analysis
                Lexer lexer = new Lexer(code);
                List<Token> tokens = lexer.getTokens();

                boolean hasLexerErrors = tokens.stream()
                        .anyMatch(t -> t.getType() == TokenType.ERROR);

                if (hasLexerErrors) {
                    if (!silent) {
                        System.out.println("  Result: LEXICAL ERROR - Cannot parse");
                    }
                    failed++;
                    continue;
                }

                // Parsing
                Parser parser = new Parser(tokens);
                parser.setTraceEnabled(traceEnabled);

                if (traceEnabled) {
                    System.out.println("  --- Parse Trace ---");
                }

                ParseTree tree = parser.parse();

                if (parser.hasErrors()) {
                    if (!silent) {
                        System.out.println("  Result: PARSING FAILED");
                        for (ParseError error : parser.getErrorReporter().getErrors()) {
                            System.out.println("    " + error.getMessage() + " (line " + 
                                    error.getLine() + ", col " + error.getColumn() + ")");
                        }
                    }
                    failed++;
                } else {
                    if (!silent) {
                        System.out.println("  Result: PARSING SUCCESSFUL");
                        if (tree != null && !tree.isEmpty()) {
                            System.out.println("  Parse Tree:");
                            tree.print();
                        }
                    }
                    passed++;
                }

            } catch (IOException e) {
                System.out.println("  Error reading file: " + e.getMessage());
                failed++;
            }

            if (!silent) {
                System.out.println();
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }
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

        System.out.print("Enable trace? (y/n) [n]: ");
        String traceChoice = scanner.nextLine().trim().toLowerCase();
        boolean traceEnabled = traceChoice.equals("y") || traceChoice.equals("yes");

        System.out.println();
        runSingleTest(selectedFile, traceEnabled, scanner);
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

        System.out.print("Enable trace? (y/n) [n]: ");
        String traceChoice = scanner.nextLine().trim().toLowerCase();
        boolean traceEnabled = traceChoice.equals("y") || traceChoice.equals("yes");

        System.out.println();
        runSingleTest(file, traceEnabled, scanner);
    }

    /**
     * Runs a single test file and displays results
     */
    private static void runSingleTest(File file, boolean traceEnabled, Scanner scanner) {
        try {
            String code = new String(Files.readAllBytes(Paths.get(file.getPath())));

            System.out.println("========================================");
            System.out.println("       SYNTAX CHECKER - Parse Result    ");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Input file: " + file.getPath());
            System.out.println("Input: " + code.trim().replace("\n", "\\n"));
            System.out.println();

            // Step 1: Lexical Analysis
            System.out.println("----------------------------------------");
            System.out.println("Step 1: Lexical Analysis (Tokenization)");
            System.out.println("----------------------------------------");

            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.getTokens();

            System.out.println("Tokens produced:");
            boolean hasLexerErrors = false;
            for (Token token : tokens) {
                System.out.println("  " + token);
                if (token.getType() == TokenType.ERROR) {
                    hasLexerErrors = true;
                }
            }
            System.out.println();

            if (hasLexerErrors) {
                System.out.println("LEXICAL ERRORS DETECTED - Cannot proceed with parsing");
                System.out.println();
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
                System.out.println();
                return;
            }

            // Step 2: Parsing
            System.out.println("----------------------------------------");
            System.out.println("Step 2: Syntactic Analysis (Parsing)");
            System.out.println("----------------------------------------");

            Parser parser = new Parser(tokens);
            parser.setTraceEnabled(traceEnabled);

            ParseTree tree = parser.parse();
            util.ErrorReporter errorReporter = parser.getErrorReporter();

            System.out.println();

            // Step 3: Results
            System.out.println("========================================");
            System.out.println("                RESULTS                 ");
            System.out.println("========================================");
            System.out.println();

            if (parser.hasErrors()) {
                errorReporter.printErrors();
            } else {
                System.out.println("PARSING SUCCESSFUL!");
                System.out.println();
                System.out.println("Parse Tree:");
                if (tree != null && !tree.isEmpty()) {
                    tree.print();
                } else {
                    System.out.println("  (empty tree - valid empty input)");
                }
                System.out.println();
                System.out.println("The input program is syntactically correct.");
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
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String inputFile = args[0];
        boolean traceEnabled = false;

        // Parse optional arguments
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--trace")) {
                traceEnabled = true;
            } else if (args[i].equals("--help") || args[i].equals("-h")) {
                printUsage();
                System.exit(0);
            }
        }

        try {
            // Read the input file
            String code = new String(Files.readAllBytes(Paths.get(inputFile)));

            System.out.println("========================================");
            System.out.println("       SYNTAX CHECKER - Parse Result    ");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Input file: " + inputFile);
            System.out.println("Input: " + code.trim().replace("\n", "\\n"));
            System.out.println();

            // Step 1: Lexical Analysis
            System.out.println("----------------------------------------");
            System.out.println("Step 1: Lexical Analysis (Tokenization)");
            System.out.println("----------------------------------------");

            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.getTokens();

            System.out.println("Tokens produced:");
            boolean hasLexerErrors = false;
            for (Token token : tokens) {
                System.out.println("  " + token);
                if (token.getType() == TokenType.ERROR) {
                    hasLexerErrors = true;
                }
            }
            System.out.println();

            if (hasLexerErrors) {
                System.out.println("LEXICAL ERRORS DETECTED - Cannot proceed with parsing");
                System.exit(1);
            }

            // Step 2: Parsing
            System.out.println("----------------------------------------");
            System.out.println("Step 2: Syntactic Analysis (Parsing)");
            System.out.println("----------------------------------------");

            Parser parser = new Parser(tokens);
            parser.setTraceEnabled(traceEnabled);

            ParseTree tree = parser.parse();
            ErrorReporter errorReporter = parser.getErrorReporter();

            System.out.println();

            // Step 3: Results
            System.out.println("========================================");
            System.out.println("                RESULTS                 ");
            System.out.println("========================================");
            System.out.println();

            if (parser.hasErrors()) {
                errorReporter.printErrors();
                System.exit(1);
            } else {
                System.out.println("PARSING SUCCESSFUL!");
                System.out.println();
                System.out.println("Parse Tree:");
                if (tree != null && !tree.isEmpty()) {
                    tree.print();
                } else {
                    System.out.println("  (empty tree - valid empty input)");
                }
                System.out.println();
                System.out.println("The input program is syntactically correct.");
                System.exit(0);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + inputFile);
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Prints usage information.
     */
    private static void printUsage() {
        System.out.println("Syntax Checker for Simple Assignment Language");
        System.out.println();
        System.out.println("Usage: java test.Main <input_file> [options]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  <input_file>    Path to the source file to parse");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --trace         Enable parsing trace output");
        System.out.println("  --help, -h      Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java test.Main program.txt");
        System.out.println("  java test.Main program.txt --trace");
    }
}
