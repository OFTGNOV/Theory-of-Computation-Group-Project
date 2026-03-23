/**
 * Tamai Richards
 * March 23, 2026
 */
package lexer;

/**
 * Represents a single token from the input source code.
 * A token is the smallest meaningful unit of code (like a word in a sentence).
 */
public class Token {
    private final TokenType type;
    private final String value;
    private final int line;
    private final int column;

    /**
     * Creates a new Token with the specified type, value, and position.
     *
     * @param type   the type of the token (e.g., IDENTIFIER, NUMBER, ASSIGN)
     * @param value  the actual text of the token
     * @param line   the line number where the token starts (1-indexed)
     * @param column the column number where the token starts (1-indexed)
     */
    public Token(TokenType type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    /**
     * Returns the type of this token.
     *
     * @return the token type
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Returns the textual value of this token.
     *
     * @return the token's value as a string
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the line number where this token starts.
     *
     * @return the line number (1-indexed)
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the column number where this token starts.
     *
     * @return the column number (1-indexed)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns a string representation of this token.
     * Format: Token(TYPE, "value", line, column)
     *
     * @return a string representation of the token
     */
    @Override
    public String toString() {
        return "Token(" + type + ", \"" + value + "\", " + line + ", " + column + ")";
    }

    /**
     * Checks if this token is of the specified type.
     *
     * @param type the type to check
     * @return true if the token matches the specified type
     */
    public boolean isType(TokenType type) {
        return this.type == type;
    }

    /**
     * Checks if this token represents an end-of-file marker.
     *
     * @return true if this is an EOF token
     */
    public boolean isEOF() {
        return type == TokenType.EOF;
    }
}
