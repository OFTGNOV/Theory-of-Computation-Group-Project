/**
 * Tamai Richards
 * March 30, 2026
 */
package main;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import parser.ParseTree;
import parser.Parser;
import util.ErrorReporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main entry point for the Syntax Checker.
 * 
 * Usage:
 *   java test.Main <input_file> [--trace]
 * 
 * Options:
 *   --trace    Enable parsing trace output for debugging
 */
public class Main {

    public static void main(String[] args) {
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
