/**
 * Tamai Richards
 * March 30, 2026
 */
package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import util.ErrorReporter;

import java.util.List;

/**
 * Recursive Descent Parser for the Simple Assignment Language.
 * 
 * Grammar:
 *   Program     → Statement
 *   Statement   → Assignment | ε
 *   Assignment  → Identifier '=' Expression ';'
 *   Expression  → Term (('+' | '-') Term)*
 *   Term        → Factor (('*' | '/') Factor)*
 *   Factor      → Number | Identifier | '(' Expression ')'
 */
public class Parser {
    private List<Token> tokens;
    private int current;
    private ErrorReporter errorReporter;
    private ParseTree parseTree;
    private boolean traceEnabled;

    /**
     * Creates a new Parser for the given input.
     *
     * @param input the source code to parse
     */
    public Parser(String input) {
        Lexer lexer = new Lexer(input);
        this.tokens = lexer.getTokens();
        this.current = 0;
        this.errorReporter = new ErrorReporter();
        this.parseTree = new ParseTree();
        this.traceEnabled = false;
    }

    /**
     * Creates a new Parser with pre-tokenized input.
     *
     * @param tokens the list of tokens to parse
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
        this.errorReporter = new ErrorReporter();
        this.parseTree = new ParseTree();
        this.traceEnabled = false;
    }

    /**
     * Enables or disables parsing trace output.
     *
     * @param enabled true to enable trace, false to disable
     */
    public void setTraceEnabled(boolean enabled) {
        this.traceEnabled = enabled;
    }

    /**
     * Parses the input and returns the parse tree.
     *
     * @return the parse tree, or null if parsing failed completely
     */
    public ParseTree parse() {
        trace("=== Starting Parse ===");
        
        ParseTree.Node root = program();
        parseTree.setRoot(root);

        trace("=== Parse Complete ===");

        return parseTree;
    }

    /**
     * Returns the error reporter for this parser.
     *
     * @return the error reporter
     */
    public ErrorReporter getErrorReporter() {
        return errorReporter;
    }

    /**
     * Returns true if any errors were encountered during parsing.
     *
     * @return true if there are errors
     */
    public boolean hasErrors() {
        return errorReporter.hasErrors();
    }

    // ============================================
    // Grammar Rule Methods
    // ============================================

    /**
     * Program → Statement
     */
    private ParseTree.Node program() {
        trace("program()");

        ParseTree.Node programNode = new ParseTree.Node("Program");
        ParseTree.Node statementNode = statement();

        if (statementNode != null) {
            programNode.add(statementNode);
        }

        // Check for EOF
        if (!check(TokenType.EOF)) {
            Token token = peek();
            errorReporter.reportError(
                "Unexpected token after statement",
                token.getLine(),
                token.getColumn(),
                "EOF",
                token.getValue()
            );
        }

        return programNode;
    }

    /**
     * Statement → Assignment | ε
     */
    private ParseTree.Node statement() {
        trace("statement()");

        // Check if we have an identifier (start of assignment)
        if (check(TokenType.IDENTIFIER)) {
            return assignment();
        }

        // Empty statement (epsilon) - just return null
        // This is valid if we're at EOF
        if (check(TokenType.EOF)) {
            trace("statement() -> epsilon (empty)");
            return null;
        }

        // If we're here, we have something unexpected
        Token token = peek();
        errorReporter.reportError(
            "Expected identifier or end of input",
            token.getLine(),
            token.getColumn(),
            "IDENTIFIER or EOF",
            token.getType().toString()
        );

        // Panic mode: skip to semicolon or EOF
        synchronize();

        return null;
    }

    /**
     * Assignment → Identifier '=' Expression ';'
     */
    private ParseTree.Node assignment() {
        trace("assignment()");

        ParseTree.Node assignmentNode = new ParseTree.Node("Assignment");

        // Expect Identifier
        if (check(TokenType.IDENTIFIER)) {
            Token token = advance();
            assignmentNode.add(new ParseTree.Node("IDENTIFIER", token.getValue()));
            trace("assignment: found identifier '" + token.getValue() + "'");
        } else {
            Token token = peek();
            errorReporter.reportError(
                "Expected identifier before '='",
                token.getLine(),
                token.getColumn(),
                "IDENTIFIER",
                token.getValue()
            );
            // Panic mode recovery
            synchronize();
            return null;
        }

        // Expect '='
        if (check(TokenType.ASSIGN)) {
            Token token = advance();
            assignmentNode.add(new ParseTree.Node("=", token.getValue()));
            trace("assignment: found '='");
        } else {
            Token token = peek();
            errorReporter.reportError(
                "Expected '=' after identifier",
                token.getLine(),
                token.getColumn(),
                "=",
                token.getValue()
            );
            // Panic mode recovery
            synchronize();
            return null;
        }

        // Parse Expression
        ParseTree.Node exprNode = expression();
        if (exprNode != null) {
            assignmentNode.add(exprNode);
        }

        // Expect ';'
        if (check(TokenType.SEMICOLON)) {
            Token token = advance();
            assignmentNode.add(new ParseTree.Node(";", token.getValue()));
            trace("assignment: found ';'");
        } else {
            Token token = peek();
            errorReporter.reportError(
                "Expected ';' at end of assignment",
                token.getLine(),
                token.getColumn(),
                ";",
                token.getValue()
            );
            // Panic mode: consume the semicolon if present, otherwise sync
            if (check(TokenType.SEMICOLON)) {
                advance();
            } else {
                synchronize();
            }
        }

        return assignmentNode;
    }

    /**
     * Expression → Term (('+' | '-') Term)*
     */
    private ParseTree.Node expression() {
        trace("expression()");

        ParseTree.Node left = term();

        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            Token opToken = advance();
            trace("expression: found operator '" + opToken.getValue() + "'");

            ParseTree.Node right = term();

            ParseTree.Node binOpNode = new ParseTree.Node("BinOp", opToken.getValue());
            binOpNode.add(left);
            binOpNode.add(right);
            left = binOpNode;
        }

        return left;
    }

    /**
     * Term → Factor (('*' | '/') Factor)*
     */
    private ParseTree.Node term() {
        trace("term()");

        ParseTree.Node left = factor();

        while (check(TokenType.MUL) || check(TokenType.DIV)) {
            Token opToken = advance();
            trace("term: found operator '" + opToken.getValue() + "'");

            ParseTree.Node right = factor();

            ParseTree.Node binOpNode = new ParseTree.Node("BinOp", opToken.getValue());
            binOpNode.add(left);
            binOpNode.add(right);
            left = binOpNode;
        }

        return left;
    }

    /**
     * Factor → Number | Identifier | '(' Expression ')'
     */
    private ParseTree.Node factor() {
        trace("factor()");

        // Number
        if (check(TokenType.NUMBER)) {
            Token token = advance();
            trace("factor: found NUMBER '" + token.getValue() + "'");
            return new ParseTree.Node("NUMBER", token.getValue());
        }

        // Identifier
        if (check(TokenType.IDENTIFIER)) {
            Token token = advance();
            trace("factor: found IDENTIFIER '" + token.getValue() + "'");
            return new ParseTree.Node("IDENTIFIER", token.getValue());
        }

        // '(' Expression ')'
        if (check(TokenType.LPAREN)) {
            Token openParen = advance();
            trace("factor: found '('");

            ParseTree.Node exprNode = expression();

            if (check(TokenType.RPAREN)) {
                Token closeParen = advance();
                trace("factor: found ')'");
                return exprNode;
            } else {
                Token token = peek();
                errorReporter.reportError(
                    "Expected ')' to close expression",
                    token.getLine(),
                    token.getColumn(),
                    ")",
                    token.getValue()
                );
                // Panic mode: skip to closing paren or sync point
                while (!check(TokenType.RPAREN) && !check(TokenType.EOF) && !check(TokenType.SEMICOLON)) {
                    advance();
                }
                if (check(TokenType.RPAREN)) {
                    advance();
                }
                return exprNode;
            }
        }

        // Error: unexpected token
        Token token = peek();
        errorReporter.reportError(
            "Expected number, identifier, or '('",
            token.getLine(),
            token.getColumn(),
            "NUMBER, IDENTIFIER, or '('",
            token.getValue()
        );

        // Panic mode recovery
        synchronize();

        return null;
    }

    // ============================================
    // Helper Methods
    // ============================================

    /**
     * Advances to the next token and returns the current one.
     *
     * @return the current token before advancing
     */
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    /**
     * Checks if the current token is of the specified type.
     *
     * @param type the token type to check
     * @return true if the current token matches
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }
        return peek().getType() == type;
    }

    /**
     * Returns the current token without advancing.
     *
     * @return the current token
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Returns the previous token (the one before current).
     *
     * @return the previous token
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Returns true if we've reached the end of the token list.
     *
     * @return true if at end
     */
    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    /**
     * Panic mode error recovery: skip tokens until a synchronization point.
     * Synchronization points: semicolon, EOF, or start of next statement.
     */
    private void synchronize() {
        trace("synchronize(): entering panic mode recovery");

        while (!isAtEnd()) {
            Token token = peek();

            // Synchronize on semicolon (end of statement)
            if (token.getType() == TokenType.SEMICOLON) {
                advance();
                trace("synchronize(): found ';' - resuming");
                return;
            }

            // Synchronize on EOF
            if (token.getType() == TokenType.EOF) {
                trace("synchronize(): found EOF - stopping");
                return;
            }

            // Synchronize on identifier (start of new statement)
            if (token.getType() == TokenType.IDENTIFIER) {
                trace("synchronize(): found IDENTIFIER - resuming");
                return;
            }

            advance();
        }
    }

    /**
     * Prints a trace message if tracing is enabled.
     *
     * @param message the message to print
     */
    private void trace(String message) {
        if (traceEnabled) {
            System.out.println("[TRACE] " + message);
        }
    }
}
