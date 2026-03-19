package src.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int position;
    private int line;
    private int column;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.line = 1;
        this.column = 1;
    }


    public List<Token> getTokens() {
        List<Token> tokens = new ArrayList<>();
        Token token;

        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);

        return tokens;
    }

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
                    // FIX 1: Proper error reporting
                    System.out.println("Error at line " + line + ", column " + column + ": Unexpected character '" + current + "'");
                    advance();
                    continue;
            }
        }

        // EOF token
        return new Token(TokenType.EOF, "", line, column);
    }

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

    private void advance() {
        position++;
        column++;
    }
}