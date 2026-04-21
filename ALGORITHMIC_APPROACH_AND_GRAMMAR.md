# Algorithmic Approach and Grammar Properties

## 1. Algorithmic Approach

### 1.1 Overview

The syntax checker implements a **two-phase, top-down, recursive descent parser with panic-mode error recovery**. The algorithm is entirely hand-crafted without the use of parser generators (e.g., ANTLR, YACC, Bison), demonstrating first principles understanding of compiler construction and formal language theory.

The parsing pipeline consists of two sequential phases:

```
Source Code  ──►  Lexer  ──►  Token Stream  ──►  Parser  ──►  Parse Tree / Errors
              (Phase 1)                         (Phase 2)
```

### 1.2 Phase 1 — Lexical Analysis (Tokenization)

The lexer (`Lexer.java`) performs **lexical analysis**, which is the process of converting a raw character stream into a structured sequence of tokens. Tokens are the smallest meaningful units of the language.

#### Algorithm

The lexer operates as a **deterministic finite automaton (DFA)**-style scanner:

1. **Read** one character at a time from the input buffer.
2. **Classify** the character into one of the following categories:
   - **Whitespace** (`' '`, `'\t'`, `'\n'`, `'\r'`): skip and continue.
   - **Letter or underscore** (`[a-zA-Z_]`): enter the *identifier* recognition routine.
   - **Digit** (`[0-9]`): enter the *number* recognition routine.
   - **Operator or delimiter** (`=`, `+`, `-`, `*`, `/`, `(`, `)`, `;`): emit the corresponding single-character token.
   - **Unrecognized character**: emit an `ERROR` token and continue (allowing the parser to report multiple errors).
3. **Advance** the input position and repeat until end-of-input is reached.
4. **Terminate** by emitting an `EOF` (end-of-file) sentinel token.

#### Identifier and Number Recognition

- **Identifiers** follow the regular expression `[a-zA-Z_][a-zA-Z0-9_]*`. The lexer reads the first character (letter or underscore), then continues consuming alphanumeric characters or underscores until a non-matching character is encountered. This is a **maximal munch** strategy — it greedily consumes the longest valid sequence.
- **Numbers** follow the regular expression `[0-9]+`. The lexer reads consecutive digits until a non-digit character is found.

#### Token Metadata

Each `Token` object carries:

- **Type** (`TokenType` enumeration): classifies the token (e.g., `IDENTIFIER`, `NUMBER`, `PLUS`).
- **Lexeme** (`String`): the actual character sequence matched.
- **Position** (`line`, `column`): source location for precise error reporting.

#### Example Trace

For the input `x = 5;`:

| Position | Character | Action                   | Token Produced                 |
| -------- | --------- | ------------------------ | ------------------------------ |
| 0        | `x`       | Letter → read identifier | `Token(IDENTIFIER, "x", 1, 1)` |
| 1        | ` `       | Whitespace → skip        | —                              |
| 2        | `=`       | Operator                 | `Token(ASSIGN, "=", 1, 3)`     |
| 3        | ` `       | Whitespace → skip        | —                              |
| 4        | `5`       | Digit → read number      | `Token(NUMBER, "5", 1, 5)`     |
| 5        | `;`       | Delimiter                | `Token(SEMICOLON, ";", 1, 6)`  |
| 6        | (end)     | End of input             | `Token(EOF, "", 1, 7)`         |

### 1.3 Phase 2 — Syntax Analysis (Recursive Descent Parsing)

The parser (`Parser.java`) implements **recursive descent parsing**, a top-down parsing technique where each non-terminal in the grammar corresponds to a method in the parser. The call stack of the methods mirrors the derivation tree of the grammar.

#### Why Recursive Descent?

Recursive descent was chosen over alternative parsing strategies (e.g., LR, LALR, SLR) for the following reasons:

1. **Intuitive mapping**: Each grammar production rule maps directly to a Java method, making the implementation readable and maintainable.
2. **Top-down construction**: The parse tree is built from the root downward, which aligns naturally with how humans reason about grammar rules.
3. **No table construction**: Unlike LR-family parsers, recursive descent does not require parsing tables, making it suitable for hand-crafted implementations.
4. **Error reporting**: Top-down parsers provide intuitive error messages because the call stack reflects the expected grammar structure.

#### Parsing Algorithm

The parser operates on the pre-tokenized stream from the lexer. It maintains:

- A **token list** (`List<Token>`).
- A **current position** index (`int current`).
- An **error reporter** (`ErrorReporter`).
- A **parse tree** (`ParseTree`) that is incrementally constructed.

The core algorithm for each grammar rule method follows this pattern:

```java
ParseTree.Node ruleMethod() {
    // 1. Check if the current token matches an expected terminal
    // 2. If yes, consume (advance) and record the token
    // 3. If the rule references another non-terminal, call that method
    // 4. Build and return a parse tree node for this rule
    // 5. On mismatch, report an error and invoke error recovery
}
```

#### Lookahead Mechanism

The parser uses **single-token lookahead** through the `peek()` method, which inspects the current token without consuming it. The `check(TokenType)` method compares the current token against an expected type. This implements the **LL(1)** property — the parser makes parsing decisions by examining only the next token.

#### Grammar-to-Method Mapping

| Grammar Rule                                          | Parser Method  | Purpose                                                    |
| ----------------------------------------------------- | -------------- | ---------------------------------------------------------- |
| `Program → Statement`                                 | `program()`    | Entry point; expects exactly one statement followed by EOF |
| `Statement → Assignment \| ε`                         | `statement()`  | Dispatches to assignment or accepts empty input at EOF     |
| `Assignment → Identifier '=' Expression ';'`          | `assignment()` | Validates the structure of an assignment statement         |
| `Expression → Term (('+' \| '-') Term)*`              | `expression()` | Parses addition/subtraction with correct precedence        |
| `Term → Factor (('*' \| '/') Factor)*`                | `term()`       | Parses multiplication/division with correct precedence     |
| `Factor → Number \| Identifier \| '(' Expression ')'` | `factor()`     | Parses atomic operands and parenthesized sub-expressions   |

#### Handling Iterative Productions

Grammar rules with Kleene star (`*`) repetitions — such as `Expression → Term (('+' | '-') Term)*` — are implemented using **while loops** rather than recursion. This avoids unnecessary stack depth and naturally handles left-associativity:

```java
ParseTree.Node expression() {
    ParseTree.Node left = term();  // Parse the first Term
    while (check(PLUS) || check(MINUS)) {
        Token op = advance();       // Consume the operator
        ParseTree.Node right = term(); // Parse the next Term
        // Build a binary operator node with left and right children
        ParseTree.Node binOp = new ParseTree.Node("BinOp", op.getValue());
        binOp.add(left);
        binOp.add(right);
        left = binOp;  // The result becomes the new left operand
    }
    return left;
}
```

This iterative approach achieves two objectives simultaneously:

1. **Avoids left recursion** — the grammar is already left-factored, so no infinite recursion occurs.
2. **Enforces left associativity** — the accumulation pattern `left = binOp` ensures that `a + b + c` is parsed as `(a + b) + c`, not `a + (b + c)`.

#### Parse Tree Construction

As parsing progresses, a tree of `ParseTree.Node` objects is constructed. Each node contains:

- A **label** (String): the name of the grammar rule or token type (e.g., `"Assignment"`, `"BinOp"`, `"NUMBER"`).
- An optional **value** (String): the lexeme for terminal tokens (e.g., `"x"`, `"5"`, `"+"`).
- A **list of children**: sub-nodes representing the recursive structure.

For the input `x = 2 + 3 * 4;`, the resulting parse tree is:

```
Program
└── Assignment
    ├── IDENTIFIER : x
    ├── = : =
    └── BinOp : +
        ├── NUMBER : 2
        └── BinOp : *
            ├── NUMBER : 3
            └── NUMBER : 4
```

The tree structure reveals that `*` is nested deeper than `+`, correctly reflecting that `3 * 4` is evaluated before `2 + (3 * 4)`.

### 1.4 Error Recovery — Panic Mode

A critical feature of the parser is its ability to **recover from syntax errors** and continue parsing to report multiple errors in a single pass. The implementation uses **panic-mode recovery**, the simplest and most widely used error recovery technique in production compilers.

#### Algorithm

When a syntax error is detected:

1. **Report the error** with precise source location (line, column) via the `ErrorReporter`.
2. **Enter synchronization mode** (`synchronize()` method): discard tokens one at a time until a **synchronization token** is found.
3. **Resume parsing** from the synchronization point.

#### Synchronization Points

The parser synchronizes on tokens that represent natural boundaries in the grammar:

| Synchronization Token | Rationale                                              |
| --------------------- | ------------------------------------------------------ |
| `;` (semicolon)       | Marks the end of a statement; safe point to resume     |
| `EOF`                 | End of input; no further recovery is possible          |
| `IDENTIFIER`          | Start of a new statement; indicates the next construct |

Additionally, within `factor()`, the parser synchronizes on `)` (closing parenthesis) to recover from unclosed parenthesized expressions.

#### Example Recovery Trace

For the input `x = 5 +;`:

1. Parser parses `x`, `=`, `5` successfully.
2. Parser encounters `+` — enters `expression()` loop, expects a `Term`.
3. Parser encounters `;` — this is not a valid start of a `Factor`.
4. **Error reported**: "Expected number, identifier, or '('" at the semicolon.
5. `synchronize()` is invoked: it skips tokens until it finds `;` or `EOF`.
6. Parser returns from the error and continues, potentially finding more errors.

### 1.5 Time and Space Complexity

| Metric           | Analysis                                                                                                                                                                                                                                              |
| ---------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Lexing time**  | O(n), where n is the number of input characters. Each character is read and processed exactly once.                                                                                                                                                   |
| **Parsing time** | O(t), where t is the number of tokens. Each token is examined a constant number of times (once by `peek()`, once by `advance()`). The while-loop structure of `expression()` and `term()` ensures each operator token triggers exactly one iteration. |
| **Space**        | O(t) for the token list + O(d) for the call stack, where d is the maximum nesting depth of the parse tree (bounded by the number of parenthesized expressions).                                                                                       |

The overall algorithm runs in **linear time** with respect to input size, which is optimal for this class of languages.

---

## 2. Grammar Properties

### 2.1 Grammar Specification

The syntax checker validates the following **Context-Free Grammar (CFG)**:

```
Program     ::= Statement
Statement   ::= Assignment | ε
Assignment  ::= Identifier '=' Expression ';'
Expression  ::= Term (('+' | '-') Term)*
Term        ::= Factor (('*' | '/') Factor)*
Factor      ::= Number | Identifier | '(' Expression ')'
Identifier  ::= [a-zA-Z_][a-zA-Z0-9_]*
Number      ::= [0-9]+
```

This is an **LL(1) grammar** — parsable top-down with single-token lookahead and no backtracking. Every production is deterministic: the first token of any right-hand side uniquely identifies which rule to apply.

### 2.2 Grammar Structure and Design

#### Hierarchical Nesting for Precedence

Operator precedence is encoded directly into the grammar through a four-level hierarchy where each level calls down to the next:

```
Expression  →  Term        (handles + and -, lowest precedence)
  └─ Term   →  Factor      (handles * and /, higher precedence)
    └─ Factor → atoms / ()  (parentheses, highest precedence)
```

Because `Expression` consumes a complete `Term` before checking for `+` or `-`, any `*` or `/` within that `Term` is fully resolved first. This ensures `2 + 3 * 4` parses as `2 + (3 * 4)`, never `(2 + 3) * 4`.

#### Elimination of Left Recursion

A naive arithmetic grammar such as `Expression → Expression + Term` is **left-recursive**, causing a recursive descent parser to loop infinitely. We eliminate left recursion by restructuring into iterative form:

```
Expression → Term (('+' | '-') Term)*
```

The `Term` is parsed first, then a `while` loop handles subsequent operators. This accepts the same language but is safe for top-down parsing.

#### Left Factoring

The grammar is **left-factored**: no two alternatives of any rule share a common prefix. The `Factor` alternatives (`Number`, `Identifier`, `'(' Expression ')'`) each begin with a distinct token, so the parser can always choose the correct production with one-token lookahead.

### 2.3 Recursion

The grammar contains a **recursive cycle** through `Factor → '(' Expression ')'`:

```
Expression → Term → Factor → Expression  (recursive cycle)
```

Each pair of parentheses triggers a fresh recursive invocation, enabling arbitrarily deep nesting (e.g., `((1 + 2) * 3) + 4`). The cycle is bounded by the base cases in `Factor` (`NUMBER` and `IDENTIFIER`), which are terminals that terminate the recursion. The full derivation chain is:

```
Program → Statement → Assignment → Expression → Term → Factor → Expression → ...
```

### 2.4 Operator Precedence

The grammar encodes three precedence levels:

| Level       | Operators | Grammar Rule | Example     |
| ----------- | --------- | ------------ | ----------- |
| 1 (lowest)  | `+`, `-`  | `Expression` | `a + b - c` |
| 2           | `*`, `/`  | `Term`       | `a * b / c` |
| 3 (highest) | `()`      | `Factor`     | `(a + b)`   |

**Proof of correctness** for `2 + 3 * 4`:

1. `expression()` calls `term()` → `factor()` returns `NUMBER(2)`.
2. `expression()` sees `+`, enters loop, calls `term()` for the right operand.
3. That `term()` calls `factor()` → `NUMBER(3)`, then sees `*` and enters its own loop.
4. `term()` calls `factor()` → `NUMBER(4)`, builds `BinOp(*)` with children `3`, `4`.
5. `expression()` builds `BinOp(+)` with children `2` and the `BinOp(*)` node.

Result:

```
     BinOp(+)
    /        \
  2        BinOp(*)
          /        \
         3          4
```

This represents `2 + (3 * 4)` — the mathematically correct interpretation.

### 2.5 Associativity

All binary operators are **left-associative**. For `a - b - c`, the iterative accumulation in `expression()` (`left = binOp`) produces `(a - b) - c`:

```
         BinOp(-)
        /        \
    BinOp(-)      c
   /        \
  a          b
```

The same pattern applies to `*` and `/` in `term()`.

### 2.6 Ambiguity Analysis

The grammar is **unambiguous** — every valid string has exactly one parse tree. This follows from three properties:

1. **Deterministic decisions**: At every point, the current token uniquely determines the production to apply (LL(1) property).
2. **Distinct alternatives**: `Factor`'s three alternatives (`Number`, `Identifier`, `'(' Expression ')'`) start with distinct terminals, so no conflict exists.
3. **No dangling constructs**: Unlike the classic "dangling else" problem, our grammar has no nested optional constructs open to multiple interpretations.

### 2.7 Chomsky Normal Form (CNF) Conversion

The grammar is **not in CNF**. CNF requires every production to be `A → BC` (two non-terminals) or `A → a` (one terminal). Converting our grammar would require:

| Step | Transformation                  | Example                                                   |
| ---- | ------------------------------- | --------------------------------------------------------- |
| 1    | Introduce new start symbol      | `Program' → Program`                                      |
| 2    | Eliminate ε-productions         | Inline `Statement → ε`                                    |
| 3    | Eliminate unit productions      | Replace `Program → Statement` with `Program → Assignment` |
| 4    | Decompose long RHS              | Split `Assignment` into binary chains                     |
| 5    | Wrap terminals in non-terminals | `Plus → '+'`, `Assign → '='`                              |

The CNF form would be significantly larger and less readable, which is why the original grammar is preferred for implementation. However, the ability to perform this conversion demonstrates understanding of grammar normalization and its role in algorithms like CYK.

### 2.8 Limitations and Design Trade-offs

| #   | Limitation / Trade-off         | Description                                                                                                                             | Mitigation                                                                            |
| --- | ------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| 1   | **Single-statement language**  | `Program → Statement` accepts exactly one assignment.                                                                                   | Change to `Program → Statement Program \| ε`.                                         |
| 2   | **Syntactic validation only**  | No variable declarations, type tracking, or semantic checks (e.g., undefined variables, division by zero).                              | Add a semantic analysis phase after parsing.                                          |
| 3   | **Integer-only numbers**       | `Number` rule `[0-9]+` rejects floats (`3.14`), negative literals (`-5`), and scientific notation (`1e-5`).                             | Extend lexer for float literals; add unary operators to `Factor`.                     |
| 4   | **No unary operators**         | `x = -5;` fails because `-` is only defined as binary in `Expression`.                                                                  | Add `Factor → '-' Factor \| '+' Factor`.                                              |
| 5   | **Panic-mode recovery**        | Simple but may skip valid tokens and produce cascading errors from a single root cause.                                                 | Phrase-level recovery (minimal-edit fix) would be more accurate but far more complex. |
| 6   | **LL(1) expressiveness limit** | Cannot directly handle common-prefix or left-associive constructs without restructuring. Extending with `if-then-else` may break LL(1). | Use a more powerful parser (e.g., LR) or manual disambiguation.                       |
| 7   | **Batch parsing**              | No incremental re-parsing of changed regions — unacceptable for production IDEs.                                                        | Implement incremental parsing with a region-based token cache.                        |
| 8   | **Concrete parse tree**        | Full parse tree includes syntactic sugar (semicolons, parens) not needed for semantic analysis.                                         | Generate an Abstract Syntax Tree (AST) that omits unnecessary nodes.                  |

### 2.9 Grammar Properties Summary

| Property                  | Status | Justification                                       |
| ------------------------- | ------ | --------------------------------------------------- |
| **Context-Free**          | ✅ Yes  | Single non-terminal on every left-hand side.        |
| **LL(1)**                 | ✅ Yes  | Deterministic decisions with one-token lookahead.   |
| **Unambiguous**           | ✅ Yes  | Every valid string yields exactly one parse tree.   |
| **Left-recursive**        | ❌ No   | Eliminated via iterative restructuring.             |
| **Left-factored**         | ✅ Yes  | No shared prefixes between alternatives.            |
| **CNF**                   | ❌ No   | Contains ε-productions, unit productions, long RHS. |
| **Precedence-encoded**    | ✅ Yes  | Operator priority embedded in grammar hierarchy.    |
| **Associativity-encoded** | ✅ Yes  | Left associativity via iterative accumulation.      |

---

## References

1. Aho, A. V., Lam, M. S., Sethi, R., & Ullman, J. D. (2006). *Compilers: Principles, Techniques, and Tools* (2nd ed.). Pearson.
2. Sipser, M. (2012). *Introduction to the Theory of Computation* (3rd ed.). Cengage Learning.
3. Appel, A. W. (1998). *Modern Compiler Implementation in Java*. Cambridge University Press.
