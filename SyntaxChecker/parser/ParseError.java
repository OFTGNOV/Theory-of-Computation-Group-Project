/**
 * Tamai Richards
 * March 30, 2026
 */
package parser;

/**
 * Represents a syntax error encountered during parsing.
 * Stores information about the error location and nature.
 */
public class ParseError {
    private String message;
    private int line;
    private int column;
    private String expected;
    private String found;

    /**
     * Creates a new ParseError with the specified details.
     *
     * @param message  the error message
     * @param line     the line number where the error occurred (1-indexed)
     * @param column   the column number where the error occurred (1-indexed)
     * @param expected what was expected at this position
     * @param found    what was actually found
     */
    public ParseError(String message, int line, int column, String expected, String found) {
        this.message = message;
        this.line = line;
        this.column = column;
        this.expected = expected;
        this.found = found;
    }

    /**
     * Creates a new ParseError with just a message and location.
     *
     * @param message the error message
     * @param line    the line number where the error occurred
     * @param column  the column number where the error occurred
     */
    public ParseError(String message, int line, int column) {
        this(message, line, column, null, null);
    }

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the line number where the error occurred.
     *
     * @return the line number (1-indexed)
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the column number where the error occurred.
     *
     * @return the column number (1-indexed)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns what was expected at the error position.
     *
     * @return the expected token or symbol, or null if not specified
     */
    public String getExpected() {
        return expected;
    }

    /**
     * Returns what was actually found at the error position.
     *
     * @return the found token or symbol, or null if not specified
     */
    public String getFound() {
        return found;
    }

    /**
     * Returns a formatted string representation of this error.
     *
     * @return formatted error string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ERROR at line ").append(line).append(", column ").append(column);
        sb.append(": ").append(message);
        if (expected != null) {
            sb.append("\n  Expected: ").append(expected);
        }
        if (found != null) {
            sb.append("\n  Got: ").append(found);
        }
        return sb.toString();
    }
}
