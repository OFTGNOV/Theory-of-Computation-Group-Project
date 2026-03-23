package lexer;

/**
 * Enumeration of all token types recognized by the lexer.
 */
public enum TokenType {
    // Literals
    IDENTIFIER,  // Variable names: x, result, foo_bar
    NUMBER,      // Numeric literals: 5, 123, 0
    
    // Operators
    ASSIGN,      // =
    PLUS,        // +
    MINUS,       // -
    MUL,         // *
    DIV,         // /
    
    // Delimiters
    LPAREN,      // (
    RPAREN,      // )
    SEMICOLON,   // ;
    
    // Special
    EOF,         // End of file/input
    ERROR        // Invalid/unexpected character
}
