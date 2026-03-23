package test;

import lexer.Lexer;
import lexer.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test runner for the Lexer.
 * Usage: java test.LexerTestRunner <input_file> <expected_result>
 *   expected_result: "valid" or "invalid"
 */
public class LexerTestRunner {

    public static void main(String[] args) {
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

            if (expectedResult.equals("valid")) {
                if (hasEOF && !hasErrors) {
                    System.out.println("✓ PASS: Lexer produced valid tokens as expected");
                    System.exit(0);
                } else {
                    System.out.println("✗ FAIL: Lexer should have produced valid tokens");
                    System.exit(1);
                }
            } else if (expectedResult.equals("invalid")) {
                if (hasErrors) {
                    System.out.println("✓ PASS: Lexer detected invalid input as expected");
                    System.exit(0);
                } else {
                    System.out.println("✗ FAIL: Lexer should have detected invalid input");
                    System.exit(1);
                }
            } else {
                System.err.println("Invalid expected_result. Use 'valid' or 'invalid'");
                System.exit(1);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + inputFile);
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
