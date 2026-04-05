/**
 * Tamai Richards
 * March 30, 2026
 */
package util;

import parser.ParseError;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for collecting and reporting parsing errors.
 * Supports panic-mode error recovery by continuing to collect errors
 * after the first one is found.
 */
public class ErrorReporter {
    private List<ParseError> errors;
    private boolean hasError;

    /**
     * Creates a new ErrorReporter.
     */
    public ErrorReporter() {
        this.errors = new ArrayList<>();
        this.hasError = false;
    }

    /**
     * Reports a new error with full details.
     *
     * @param message  the error message
     * @param line     the line number where the error occurred
     * @param column   the column number where the error occurred
     * @param expected what was expected
     * @param found    what was found instead
     */
    public void reportError(String message, int line, int column, String expected, String found) {
        errors.add(new ParseError(message, line, column, expected, found));
        hasError = true;
    }

    /**
     * Reports a new error with just message and location.
     *
     * @param message the error message
     * @param line    the line number where the error occurred
     * @param column  the column number where the error occurred
     */
    public void reportError(String message, int line, int column) {
        errors.add(new ParseError(message, line, column));
        hasError = true;
    }

    /**
     * Adds an existing ParseError to the reporter.
     *
     * @param error the error to add
     */
    public void addError(ParseError error) {
        errors.add(error);
        hasError = true;
    }

    /**
     * Returns true if any errors have been reported.
     *
     * @return true if there are errors
     */
    public boolean hasErrors() {
        return hasError;
    }

    /**
     * Returns the number of errors reported.
     *
     * @return the error count
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Returns the list of all errors.
     *
     * @return list of ParseError objects
     */
    public List<ParseError> getErrors() {
        return errors;
    }

    /**
     * Clears all reported errors.
     */
    public void clear() {
        errors.clear();
        hasError = false;
    }

    /**
     * Prints all errors to standard output.
     */
    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("No errors found.");
            return;
        }

        System.out.println("PARSING FAILED");
        System.out.println();

        for (ParseError error : errors) {
            System.out.println(error.toString());
            System.out.println();
        }

        System.out.println("Total errors: " + errors.size());
    }

    /**
     * Prints all errors to standard error.
     */
    public void printErrorsToStderr() {
        if (errors.isEmpty()) {
            return;
        }

        System.err.println("PARSING FAILED");
        System.err.println();

        for (ParseError error : errors) {
            System.err.println(error.toString());
            System.err.println();
        }

        System.err.println("Total errors: " + errors.size());
    }
}
