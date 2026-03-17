# Parser Implementation Plan - Simple Assignment Statements
## Beginner-Friendly Guide with Examples

---

## Table of Contents
1. [Key Terms Dictionary](#key-terms-dictionary)
2. [Big Picture Overview](#big-picture-overview)
3. [Grammar Explained Simply](#grammar-explained-simply)
4. [Project Structure](#project-structure)
5. [How the Parser Works](#how-the-parser-works)
6. [Detailed Component Guide](#detailed-component-guide)
7. [Step-by-Step Example](#step-by-step-example)
8. [Testing Your Code](#testing-your-code)

---

## Key Terms Dictionary

### Core Concepts

| Term | Simple Definition | Example |
|------|-------------------|---------|
| **Parser** | A program that reads code and checks if it follows the rules | Reads "x = 5;" and says "yes, this is valid" |
| **Token** | The smallest piece of code (like a word) | "x", "=", "5", ";" are each a token |
| **Lexer** | A program that breaks code into tokens | Turns "x=5;" into ["x", "=", "5", ";"] |
| **Grammar** | The rules that say what code is allowed | "An assignment needs: identifier = expression ;" |
| **Parse Tree** | A diagram showing how code follows the rules | Visual tree showing the structure |
| **Syntax Error** | Code that breaks the grammar rules | "x = 5" (missing semicolon) |

### Parser Components

| Term | What It Does | Think Of It As |
|------|--------------|-----------------|
| **Lexer** | Splits input into tokens | A word-breaker (tokenizer) |
| **Parser** | Checks if tokens follow grammar | A rule-checker |
| **Node** | A piece of the parse tree | A box in the tree diagram |
| **Error Reporter** | Collects and shows errors | A message collector |
| **Symbol** | A word/number in the grammar | Like a variable name |

### Grammar Terminology

| Term | Meaning | Example |
|------|---------|---------|
| **Non-terminal** | A rule name (can be broken down) | "Expression" (can become a+b) |
| **Terminal** | An actual token (can't be broken down) | "5" or "x" or ";" |
| **Production** | A rule showing what can become what | `Expression ::= Term '+' Term` |
| **Epsilon (ε)** | "nothing" or "empty" | An assignment can be empty |
| **Left-associative** | Evaluated left to right | (a + b) + c not a + (b + c) |
| **Precedence** | Which operator to do first | * before + (so 2+3*4 = 2+12) |

### Recursive Descent Specific

| Term | What It Means | Why It Matters |
|------|---------------|-----------------|
| **Recursive** | A method calls itself | Allows nested structures |
| **Descent** | Going down the grammar rules | Starting from big rules, going to small ones |
| **Call Stack** | The list of active method calls | Shows which rule we're currently parsing |
| **Backtracking** | Going back to try something else | Peek ahead without consuming token |
| **Synchronization** | Recovering from an error | Skip to next safe point (like semicolon) |

---

## Big Picture Overview

### What We're Building

Think of it like building a **spell checker**:
- **Regular spell checker**: Checks if words are spelled right
- **Our parser**: Checks if code follows grammar rules

### The Three Steps

```
INPUT CODE         LEXER           TOKENS          PARSER          PARSE TREE
┌─────────────────────────┐
│ x = 5 + 3;              │──────────────────▶ [x] [=] [5] [+] [3] [;] ──────────▶  Program
└─────────────────────────┘                                                           │
"Raw text"          "Break into pieces"    "List of tokens"    "Check rules"     "Result"
```

### Simple Example

**Input Code:**
```
x = 5;
```

**What Lexer Does:**
- Sees "x" → token: IDENTIFIER
- Sees "=" → token: ASSIGN
- Sees "5" → token: NUMBER
- Sees ";" → token: SEMICOLON

**What Parser Does:**
- Checks: "Is there an identifier?" ✓ (x)
- Checks: "Is there an equals sign?" ✓ (=)
- Checks: "Is there an expression?" ✓ (5)
- Checks: "Is there a semicolon?" ✓ (;)
- Result: "This is valid!"

**Parse Tree Created:**
```
         Program
           │
      Assignment
       /   |   \
      x    =    5
```

---

## Grammar Explained Simply

### What is a Grammar?

A grammar is like a recipe. A recipe says:
- "A cake needs: flour + eggs + sugar + heat"

Our grammar says:
- "An assignment needs: identifier + equals + expression + semicolon"

### Our Complete Grammar

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

### Breaking It Down Line By Line

**Line 1: `Program ::= Statement`**
- "A program is made of a statement"
- Think: A program is one assignment

**Line 2: `Statement ::= Assignment | ε`**
- "A statement is either an assignment OR nothing"
- The `|` means "or"
- The `ε` means "empty/nothing"

**Line 3: `Assignment ::= Identifier '=' Expression ';'`**
- "An assignment is: a name = something ;"
- Examples: `x = 5;` or `result = a + b;`

**Line 4: `Expression ::= Term (('+' | '-') Term)*`**
- "An expression is: term + term + term... (any number of terms with + or -)"
- The `*` means "zero or more times"
- Example: `2 + 3` or `5 + 6 - 7` or just `5`

**Line 5: `Term ::= Factor (('*' | '/') Factor)*`**
- "A term is: factor * factor * factor... (any number of factors with * or /)"
- Similar to expression but for multiplication/division
- Example: `3 * 4` or `8 * 2 / 4` or just `8`

**Line 6: `Factor ::= Number | Identifier | '(' Expression ')'`**
- "A factor is either a number, a variable name, or a parenthesized expression"
- Examples: `5` or `x` or `(2 + 3)`

**Lines 7-8: Identifiers and Numbers**
- "Identifier is letters/underscores followed by letters/digits/underscores"
- "Number is just digits"

### Real Examples Using This Grammar

**Example 1: Simple assignment**
```
Code: x = 5;

Breaking it down using grammar:
x        ← Identifier (valid: letter)
=        ← the = symbol
5        ← Expression (which is a Term, which is a Factor, which is a Number)
;        ← semicolon
```

**Example 2: Addition**
```
Code: y = 10 + 20;

Breaking it down:
y        ← Identifier
=        ← assignment
10 + 20  ← Expression (Term + Term)
         ├─ 10 (first Term, which is a Factor, which is a Number)
         ├─ + (operator)
         └─ 20 (second Term, which is a Factor, which is a Number)
;        ← semicolon
```

**Example 3: Complex with precedence**
```
Code: z = 2 + 3 * 4;

Breaking it down:
z        ← Identifier
=        ← assignment
2 + 3*4  ← Expression
         ├─ 2 (Term: just a Factor, just a Number)
         ├─ + (operator at Expression level)
         └─ 3*4 (Term: Factor*Factor)
            ├─ 3 (Factor: Number)
            ├─ * (operator at Term level)
            └─ 4 (Factor: Number)
;        ← semicolon

Result: Correctly parsed as 2 + (3*4) = 2 + 12, NOT (2+3)*4 = 5*4
This happens automatically because of how we nest the rules!
```

### Why This Grammar Structure?

The grammar structure **automatically encodes operator precedence**!

- **Expression level** handles `+` and `-` (lowest priority)
- **Term level** handles `*` and `/` (higher priority)
- **Factor level** handles `()` and atoms (highest priority)

So `*` and `/` are "deeper" in the grammar, meaning they execute first!

---

## Project Structure

### What Files We'll Create

```
project/
│
├── src/
│   ├── lexer/
│   │   ├── Token.java          ← Definition of a single token
│   │   └── Lexer.java          ← Breaks code into tokens
│   │
│   ├── parser/
│   │   ├── ParseTree.java      ← The tree structure we build
│   │   ├── ParseError.java     ← Error information
│   │   └── Parser.java         ← Main parser logic
│   │
│   ├── util/
│   │   └── ErrorReporter.java  ← Collects all errors
│   │
│   └── Main.java               ← Entry point (run this!)
│
├── test/
│   ├── ValidPrograms.txt       ← Test cases that should work
│   ├── InvalidPrograms.txt     ← Test cases that should fail
│   └── ParserTest.java         ← Unit tests
│
└── CODE_IMPLEMENTATION_PLAN.md  ← This file!
```

---

## How the Parser Works

### The Big Three Steps

#### Step 1: Lexing (Tokenization)
```
Raw input: "x = 5;"

Lexer reads character by character:
- 'x' → "This is a letter! Part of an identifier"
- ' ' → "Space, skip it"
- '=' → "This is the equals sign! New token!"
- ' ' → "Space, skip it"
- '5' → "This is a digit! A number!"
- ';' → "Semicolon! End of statement!"

Output: List of tokens
[Token(IDENTIFIER, "x"), Token(ASSIGN, "="), Token(NUMBER, "5"), Token(SEMICOLON, ";")]
```

#### Step 2: Parsing (Checking Rules)
```
Tokens: [IDENTIFIER, ASSIGN, NUMBER, SEMICOLON]

Parser checks grammar rules:
"Looking for an assignment..."
- ☑ Found IDENTIFIER (x)
- ☑ Found ASSIGN (=)
- ☑ Found NUMBER (5) [which is an Expression/Term/Factor]
- ☑ Found SEMICOLON (;)
"All rules satisfied! This is valid!"
```

#### Step 3: Building the Parse Tree
```
As we check rules, we build a tree:

         Assignment
        /    |    \
       x     =     5

This shows the structure of the code!
```

### Method Calls Mirror Grammar Rules

The grammar has rules. Each rule gets a method:

```
Grammar Rule              Java Method
───────────────          ──────────────
Program                  program()
Statement                statement()
Assignment               assignment()
Expression               expression()
Term                     term()
Factor                   factor()
```

When a rule says "call this other rule", the method calls that method!

Example: If `assignment()` needs an `expression()`, it calls the `expression()` method.

This is **recursive descent** - we descend through the grammar rules!

---

## Detailed Component Guide

### Component 1: Token.java

**What it does:** Represents one small piece of code (like one word)

**Real-world analogy:** Like a card with "word", spelling, line number, position

```java
// A token looks like this:
Token token = new Token(IDENTIFIER, "x", 1, 1);
//                      ↑            ↑  ↑  ↑
//                    type        value line col

// In English: "The token IDENTIFIER with value 'x' at line 1, column 1"
```

**Token Types We'll Use:**

```
IDENTIFIER  ← For variable names: x, result, foo_bar
NUMBER      ← For numbers: 5, 123, 0
ASSIGN      ← For '='
PLUS        ← For '+'
MINUS       ← For '-'
MUL         ← For '*'
DIV         ← For '/'
LPAREN      ← For '('
RPAREN      ← For ')'
SEMICOLON   ← For ';'
EOF         ← End of file marker
```

**Example: Breaking down "x = 5;"**

```
Input string: "x = 5;"

Tokens created:
1. Token(IDENTIFIER, "x")      ← Variable
2. Token(ASSIGN, "=")          ← Assignment operator
3. Token(NUMBER, "5")          ← The value
4. Token(SEMICOLON, ";")       ← End marker
5. Token(EOF, "")              ← End of input
```

### Component 2: Lexer.java

**What it does:** Reads raw text and produces tokens

**Real-world analogy:** Like a word-breaking machine

```
Input:  "x = 5;"
         ↓ (lexer breaks it apart) ↓
Output: [Token, Token, Token, Token, Token(EOF)]
```

**How it works (pseudocode):**

```java
// Main loop
while (there are characters left) {
    // Skip spaces
    if (current char is space or newline) {
        skip it
        continue
    }

    // Check what type of token this is
    if (current char is a letter) {
        read all letters/digits → create IDENTIFIER token
    }
    else if (current char is a digit) {
        read all digits → create NUMBER token
    }
    else if (current char is '+') {
        create PLUS token
    }
    else if (current char is '=') {
        create ASSIGN token
    }
    // ... etc for other symbols
}
```

**Example Step-by-Step:**

```
Reading "x = 5;"

Position 0: 'x'
  → Is it a letter? YES
  → Keep reading letters: just "x"
  → Create: Token(IDENTIFIER, "x", line 1, col 1)
  → Advance position

Position 1: ' '
  → Is it space? YES
  → Skip it

Position 2: '='
  → Is it an operator? YES
  → Create: Token(ASSIGN, "=", line 1, col 3)
  → Advance position

Position 3: ' '
  → Is it space? YES
  → Skip it

Position 4: '5'
  → Is it a digit? YES
  → Keep reading digits: just "5"
  → Create: Token(NUMBER, "5", line 1, col 5)
  → Advance position

Position 5: ';'
  → Is it ';'? YES
  → Create: Token(SEMICOLON, ";", line 1, col 6)
  → Advance position

Position 6: (end of string)
  → Create: Token(EOF, "", line 1, col 7)
```

### Component 3: ParseTree.java

**What it does:** Stores the tree structure showing how code breaks down

**Real-world analogy:** Like an organizational chart

```
Simple tree for "x = 5;":

        Assignment
         /   |   \
        x    =    5

More complex for "x = a + b;":

            Assignment
             /   |   \
            x    =   BinOp(+)
                    /       \
                   a         b
```

**Why we need it:** The tree shows the relationship between parts of the code

### Component 4: ParseError.java

**What it does:** Stores information about something that went wrong

**Example error:**

```java
ParseError error = new ParseError(
    "Unexpected token",        // what went wrong
    1,                         // line number
    6,                         // column number
    "';'",                     // what we expected
    "'='"                      // what we got instead
);

// When printed:
// ERROR at line 1, column 6
//   Expected: ';'
//   Got: '='
```

### Component 5: ErrorReporter.java

**What it does:** Collects multiple errors and displays them nicely

**Why we need it:**
- Parser can continue after finding one error
- Show the user ALL errors at once
- Not just the first error

```java
ErrorReporter reporter = new ErrorReporter();

// As we parse, we find errors:
reporter.addError(error1);  // missing semicolon
reporter.addError(error2);  // wrong operator

// At the end, show them all:
reporter.printErrors();
// Outputs:
// ERROR at line 1, column 6: expected ';'
// ERROR at line 2, column 3: expected '='
// Total errors: 2
```

### Component 6: Parser.java - The Main Logic

**What it does:** The core engine that checks if code follows rules

**The key insight:** Each grammar rule becomes a method!

```
Grammar says:           Java implements:
─────────────          ────────────────
Expression ::= ...     ParseTree.Node expression() { ... }
Term ::= ...           ParseTree.Node term() { ... }
Factor ::= ...         ParseTree.Node factor() { ... }
```

#### Method: factor()

**What it parses:** The smallest building blocks: numbers, variables, or (expression)

```java
ParseTree.Node factor() {
    // A factor is ONE of these three things:

    // Option 1: A number (like 5)
    if (current token is NUMBER) {
        record the value
        move to next token
        return NUMBER node
    }

    // Option 2: An identifier (like x)
    if (current token is IDENTIFIER) {
        record the value
        move to next token
        return IDENTIFIER node
    }

    // Option 3: A parenthesized expression (like (2+3))
    if (current token is LPAREN) {
        move past '('
        recursively parse expression inside
        expect ')'
        move past ')'
        return the expression node
    }

    // If none of the above, error!
    report error
}
```

**Examples:**
```
factor() on "5"      → returns Node(NUMBER, "5")
factor() on "x"      → returns Node(IDENTIFIER, "x")
factor() on "(2+3)"  → returns Node for the expression 2+3
```

#### Method: term()

**What it parses:** Multiplication and division (which have higher priority than addition)

```java
ParseTree.Node term() {
    // Get the first factor (base case)
    Node left = factor();

    // Now look for any * or / operators
    while (current token is * or /) {
        String operator = current token value
        move to next token

        Node right = factor()

        // Combine them: left OP right
        Node binOp = new Node("BinOp", operator)
        binOp.add(left)
        binOp.add(right)

        left = binOp  // This is now our new left side
    }

    return left
}
```

**Step-by-step example: Parsing "3 * 4"**
```
Tokens: [NUMBER(3), MUL(*), NUMBER(4)]

term():
  left = factor()
    → returns Node(NUMBER, "3")

  current token is MUL? YES, enter loop:
    operator = "*"
    right = factor()
       → returns Node(NUMBER, "4")

    binOp = Node("BinOp", "*")
    binOp.add(3)
    binOp.add(4)

    left = binOp

  current token is MUL? NO, exit loop

  return:  BinOp(*)
          /     \
         3       4
```

#### Method: expression()

**What it parses:** Addition and subtraction (lower priority than multiplication)

```java
ParseTree.Node expression() {
    // Similar to term(), but:
    // - Get first TERM (not factor!)
    // - Look for + or - operators

    Node left = term();  // START WITH A TERM (higher priority)

    while (current token is + or -) {
        String operator = current token value
        move to next token

        Node right = term()  // GET A TERM (not factor!)

        Node binOp = new Node("BinOp", operator)
        binOp.add(left)
        binOp.add(right)

        left = binOp
    }

    return left
}
```

**This structure gives us correct precedence!**

Because expression calls term, and term calls factor, multiplication gets evaluated first:

```
In "2 + 3 * 4":

expression():
  left = term()     ← gets "2" as a whole term
  sees +
  right = term()    ← gets "3*4" as a connected term!

Result: 2 + (3*4) ✓

NOT: (2+3) * 4 ✗
```

#### Method: assignment()

**What it parses:** Complete assignment statements like "x = 5;"

```java
ParseTree.Node assignment() {
    Node assignmentNode = new Node("Assignment")

    // Step 1: Expect an identifier (variable name)
    if current token is IDENTIFIER:
        name = current token value
        assignmentNode.add(Node(IDENTIFIER, name))
        move to next token
    else:
        error: expected identifier!
        return null

    // Step 2: Expect '='
    if current token is ASSIGN:
        assignmentNode.add(Node("="))
        move to next token
    else:
        error: expected '='!
        return null

    // Step 3: Expect an expression
    expr = expression()
    assignmentNode.add(expr)

    // Step 4: Expect ';'
    if current token is SEMICOLON:
        assignmentNode.add(Node(";"))
        move to next token
    else:
        error: expected ';'!
        return null

    return assignmentNode
}
```

**Example: Parsing "x = 5;"**
```
Step 1: See IDENTIFIER "x" ✓
Step 2: See ASSIGN "=" ✓
Step 3: Parse expression (calls term, calls factor, gets NUMBER "5")
Step 4: See SEMICOLON ";" ✓

Returns: Assignment
         /    |    \
        x     =     5
```

### Component 7: Main.java

**What it does:** The entry point - ties everything together

```java
public static void main(String[] args) {
    // Step 1: Read the input file
    String code = readFile(args[0]);

    // Step 2: Create a parser
    Parser parser = new Parser(code);

    // Step 3: Start parsing
    ParseTree tree = parser.parse();

    // Step 4: Check for errors
    if (parser has errors) {
        print all errors
        exit with failure
    } else {
        print "Success!"
        draw the parse tree
        exit successfully
    }
}
```

---

## Step-by-Step Example

### Parsing: "x = 2 + 3 * 4;"

#### Phase 1: Lexing

```
Input string: "x = 2 + 3 * 4;"

Lexer reads character by character:

'x'       → Letter! Identifier: "x"           → Token(IDENTIFIER, "x")
' '       → Space, skip
'='       → Equals sign!                      → Token(ASSIGN, "=")
' '       → Space, skip
'2'       → Digit! Number: "2"                → Token(NUMBER, "2")
' '       → Space, skip
'+'       → Plus sign!                        → Token(PLUS, "+")
' '       → Space, skip
'3'       → Digit! Number: "3"                → Token(NUMBER, "3")
' '       → Space, skip
'*'       → Multiply sign!                    → Token(MUL, "*")
' '       → Space, skip
'4'       → Digit! Number: "4"                → Token(NUMBER, "4")
';'       → Semicolon!                        → Token(SEMICOLON, ";")
(end)     → End of input                      → Token(EOF, "")

Final token list:
[IDENTIFIER("x"), ASSIGN("="), NUMBER("2"), PLUS("+"),
 NUMBER("3"), MUL("*"), NUMBER("4"), SEMICOLON(";"), EOF]
```

#### Phase 2: Parsing

```
Starting: parse assignment!

assignment():
  Expect IDENTIFIER?
    Current: IDENTIFIER("x") ✓
    Add to tree: x
    Move to next: ASSIGN("=")

  Expect ASSIGN?
    Current: ASSIGN("=") ✓
    Move to next: NUMBER("2")

  Parse expression:
    expression():
      term():
        factor():
          Current: NUMBER("2")
          Return: Node(NUMBER, "2")
        No * or / operator, return
      Return: Node(NUMBER, "2")

    Seen PLUS operator? YES
      term():
        factor():
          Current: NUMBER("3")
          Return: Node(NUMBER, "3")
        Seen MUL operator? YES
          factor():
            Current: NUMBER("4")
            Return: Node(NUMBER, "4")
          Create: BinOp("*") with 3 and 4
        Return: BinOp("*") [3 * 4]

      Create: BinOp("+") with 2 and (3*4)

    No more + or -, return: BinOp("+")

  Expect SEMICOLON?
    Current: SEMICOLON(";") ✓
    Done!

  Return: Assignment node
```

#### Phase 3: Parse Tree

```
Final tree built:

         Assignment
         /    |    \
        x     =   BinOp(+)
                 /        \
            NUMBER(2)   BinOp(*)
                        /       \
                   NUMBER(3)  NUMBER(4)

Which represents:
x = 2 + (3 * 4)
x = 2 + 12
x = 14
```

#### Phase 4: Output

```
INPUT:
x = 2 + 3 * 4;

OUTPUT:
PARSING SUCCESSFUL!

Parse Tree:
Assignment
  IDENTIFIER : x
  BinOp : +
    NUMBER : 2
    BinOp : *
      NUMBER : 3
      NUMBER : 4
```

---

## Testing Your Code

### Test 1: Simple Assignment

**Input:** `x = 5;`

**Expected Output:**
```
PARSING SUCCESSFUL!

Parse Tree:
Assignment
  IDENTIFIER : x
  NUMBER : 5
```

**Manual check:**
- ✓ Identifier "x" found
- ✓ Equals "=" found
- ✓ Number "5" found
- ✓ Semicolon ";" found

### Test 2: With Addition

**Input:** `result = 10 + 20;`

**Expected Output:**
```
PARSING SUCCESSFUL!

Parse Tree:
Assignment
  IDENTIFIER : result
  BinOp : +
    NUMBER : 10
    NUMBER : 20
```

### Test 3: With Precedence (the critical test!)

**Input:** `y = 2 + 3 * 4;`

**Expected Output:**
```
PARSING SUCCESSFUL!

Parse Tree:
Assignment
  IDENTIFIER : y
  BinOp : +
    NUMBER : 2
    BinOp : *
      NUMBER : 3
      NUMBER : 4
```

**Important:** The multiply must be "deeper" in the tree, showing it happens first!

### Test 4: Invalid Code (Missing Semicolon)

**Input:** `x = 5`

**Expected Output:**
```
PARSING FAILED

ERROR at line 1, column 7:
  Expected: ';'
  Got: EOF

Total errors: 1
```

### Test 5: Invalid Code (Missing Equals)

**Input:** `x 5;`

**Expected Output:**
```
PARSING FAILED

ERROR at line 1, column 3:
  Expected: '='
  Got: NUMBER

Total errors: 1
```

### Test 6: Complex with Parentheses

**Input:** `temp = (2 + 3) * 4;`

**Expected Output:**
```
PARSING SUCCESSFUL!

Parse Tree:
Assignment
  IDENTIFIER : temp
  BinOp : *
    BinOp : +
      NUMBER : 2
      NUMBER : 3
    NUMBER : 4
```

**Why it works:**
- `(2+3)` gets parsed as a factor (the parentheses make it tight)
- Then it multiplies with 4
- Result: (2+3)*4 = 5*4, NOT 2+3*4 ✓

### How to Test in Java

```bash
# 1. Compile all files
javac src/lexer/*.java src/parser/*.java src/util/*.java src/Main.java

# 2. Create a test file
echo "x = 5;" > test.txt

# 3. Run the parser
java Main test.txt

# 4. Check the output!
```

---

## Implementation Checklist

### Phase 1: Build Token & Lexer
- [ ] Create Token class with enum TokenType
- [ ] Implement getters for type, value, line, column
- [ ] Create Lexer class
- [ ] Implement nextToken() - reads and classifies tokens
- [ ] Test: Lexer correctly breaks up "x = 5"
- [ ] Test: Lexer handles multi-digit numbers "123"
- [ ] Test: Lexer handles identifiers "result"

### Phase 2: Build Support Classes
- [ ] Create ParseTree.Node class
- [ ] Implement addChild() and print() methods
- [ ] Create ParseError class
- [ ] Create ErrorReporter class

### Phase 3: Build Parser Skeleton
- [ ] Create Parser class
- [ ] Implement helper methods: advance(), match()
- [ ] Create all grammar methods as stubs:
  - [ ] program()
  - [ ] statement()
  - [ ] assignment()
  - [ ] expression()
  - [ ] term()
  - [ ] factor()

### Phase 4: Implement Parser
- [ ] Implement factor() first (simplest)
- [ ] Test factor() with: "5", "x", "(3)"
- [ ] Implement term()
- [ ] Test term() with: "3*4", "8/2"
- [ ] Implement expression()
- [ ] Test expression() with: "2+3", "5-1"
- [ ] Implement assignment()
- [ ] Implement statement() and program()

### Phase 5: Integration
- [ ] Create Main.java
- [ ] Test with valid programs
- [ ] Test with invalid programs
- [ ] Verify error messages
- [ ] Verify parse trees

---

## Common Mistakes & How to Fix Them

### Mistake 1: Forgetting to Advance Token
```java
// ❌ WRONG - Infinite loop!
if (currentToken.getType() == IDENTIFIER) {
    // you never move to next token!
    // this condition is true forever
}

// ✓ RIGHT
if (currentToken.getType() == IDENTIFIER) {
    advance();  // move to next token!
}
```

### Mistake 2: Wrong Precedence
```java
// ❌ WRONG - expression() calls expression() directly
// This causes infinite recursion
expression() {
    expression() + term()  // Wrong!
}

// ✓ RIGHT - expression() calls term()
expression() {
    term() + term()  // Correct! term is lower level
}
```

### Mistake 3: Not Handling EOF
```java
// ❌ WRONG - Crashes at end of file
while (currentToken is not something) {
    // If you reach EOF and expect something else, CRASH
}

// ✓ RIGHT - Check for EOF
while (currentToken.getType() != EOF) {
    // this is safe
}
```

### Mistake 4: Building Tree Wrong
```java
// ❌ WRONG - Tree nodes with nothing
assignmentNode.addChild(new Node("="));

// ✓ RIGHT - Tree shows the structure
assignmentNode.addChild(new Node("OPERATOR", "="));
// OR just note it's an operator by nesting
```

---

## Quick Reference

### Grammar Rules Flow

```
program()
  └─ statement()
     └─ assignment()
        ├─ expects: IDENTIFIER
        ├─ expects: ASSIGN
        ├─ calls: expression()
        │  └─ expression() {
        │     └─ calls: term()
        │        └─ term() {
        │           └─ calls: factor()
        │              └─ factor() {
        │                 ├─ NUMBER
        │                 ├─ IDENTIFIER
        │                 └─ LPAREN expression() RPAREN
        │              }
        │        }
        │     }
        └─ expects: SEMICOLON
```

### Token Checklist

```
For "x = 5 + 3 * 2;"

✓ Token(IDENTIFIER, "x")
✓ Token(ASSIGN, "=")
✓ Token(NUMBER, "5")
✓ Token(PLUS, "+")
✓ Token(NUMBER, "3")
✓ Token(MUL, "*")
✓ Token(NUMBER, "2")
✓ Token(SEMICOLON, ";")
✓ Token(EOF, "")
```

---

## Key Takeaways

1. **Parser = Rules Checker**: It reads code and checks if it follows grammar rules
2. **Tokens are Words**: Break code into tiny pieces (tokens)
3. **Grammar Rules Become Methods**: Each rule is a Java method
4. **Recursion Shows Nesting**: Nested grammar rules show hierarchy
5. **Precedence Comes Free**: Grammar structure automatically gives correct order
6. **Parse Tree is the Result**: Shows how code was structured
7. **Error Recovery Helps**: Can find multiple errors, not just the first one

---

## Summary Table

| Component | Purpose | Key Method |
|-----------|---------|-----------|
| Token.java | Represents one lexeme | getType(), getValue() |
| Lexer.java | Breaks code into tokens | nextToken() |
| ParseTree.java | Stores hierarchical structure | addChild(), print() |
| ParseError.java | Stores error information | toString() |
| ErrorReporter.java | Collects all errors | addError(), printErrors() |
| Parser.java | Checks grammar rules | parse(), assignment(), expression(), etc. |
| Main.java | Runs everything | main() |

---

## Next Steps

1. Read through this document until comfortable with the concepts
2. Create the Token and Lexer classes first
3. Test your lexer with simple inputs
4. Build the parser step-by-step
5. Test frequently with the examples provided
6. Debug by printing tokens and tree structure

Good luck! You've got this! 🚀
