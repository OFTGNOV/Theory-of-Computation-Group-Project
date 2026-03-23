/**
 * Tamai Richards
 * March 23, 2026
 */
package lexer;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer (Lexical Analyzer) for the Simple Assignment Language.
 * Breaks raw source code input into meaningful tokens.
 */
public class Lexer {
    private final String input;
    private int position;
    private int line;
    private int column;

    /**
     * Creates a new Lexer for the given input string.
     *
     * @param input the source code to tokenize
     */
    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }

    /**
     * Tokenizes the entire input and returns a list of all tokens.
     *
     * @return a list of tokens representing the tokenized input
     */
    public List<Token> getTokens() {
        List<Token> tokens = new ArrayList<>();
        Token token;

        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);

        return tokens;
    }

    /**
     * Returns the next token from the input.
     *
     * @return the next token, or an EOF token if end of input is reached
     */
    private Token nextToken() {
        while (position < input.length()) {
            char current = input.charAt(position);

            // Skip whitespace
            if (Character.isWhitespace(current)) {
                skipWhitespace();
                continue;
            }

            // Identifiers
            if (Character.isLetter(current) || current == '_') {
                String identifier = readIdentifier();
                return new Token(TokenType.IDENTIFIER, identifier, line, column - identifier.length());
            }

            // Numbers
            if (Character.isDigit(current)) {
                String number = readNumber();
                return new Token(TokenType.NUMBER, number, line, column - number.length());
            }

            // Single character tokens
            switch (current) {
                case '=':
                    advance();
                    return new Token(TokenType.ASSIGN, "=", line, column - 1);
                case '+':
                    advance();
                    return new Token(TokenType.PLUS, "+", line, column - 1);
                case '-':
                    advance();
                    return new Token(TokenType.MINUS, "-", line, column - 1);
                case '*':
                    advance();
                    return new Token(TokenType.MUL, "*", line, column - 1);
                case '/':
                    advance();
                    return new Token(TokenType.DIV, "/", line, column - 1);
                case '(':
                    advance();
                    return new Token(TokenType.LPAREN, "(", line, column - 1);
                case ')':
                    advance();
                    return new Token(TokenType.RPAREN, ")", line, column - 1);
                case ';':
                    advance();
                    return new Token(TokenType.SEMICOLON, ";", line, column - 1);
                default:
                    // Create an ERROR token for unexpected characters
                    Token errorToken = new Token(TokenType.ERROR, String.valueOf(current), line, column);
                    System.out.println("Error at line " + line + ", column " + column + ": Unexpected character '" + current + "'");
                    advance();
                    return errorToken;
            }
        }

        // EOF token
        return new Token(TokenType.EOF, "", line, column);
    }

    /**
     * Reads an identifier from the current position.
     *
     * @return the identifier string that was read
     */
    private String readIdentifier() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isLetterOrDigit(c) || c == '_') {
                sb.append(c);
                advance();
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Reads a number from the current position.
     *
     * @return the number string that was read
     */
    private String readNumber() {
        StringBuilder sb = new StringBuilder();
        while (position < input.length()) {
            char c = input.charAt(position);
            if (Character.isDigit(c)) {
                sb.append(c);
                advance();
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Skips whitespace characters from the current position.
     */
    private void skipWhitespace() {
        while (position < input.length()) {
            char c = input.charAt(position);
            if (c == '\n') {
                line++;
                column = 1;
                advance();
            } else if (Character.isWhitespace(c)) {
                advance();
            } else {
                break;
            }
        }
    }

    /**
     * Advances the position by one character.
     */
    private void advance() {
        position++;
        column++;
    }
}