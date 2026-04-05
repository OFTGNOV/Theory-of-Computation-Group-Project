# 📘 Project Summary: Syntax Checker for a Simple Programming Language

## **Shared Google Doc**

https://docs.google.com/document/d/1Vo84mNwzQJyHcHB37taEUTl-1Pb8Ji4hm1PTv383FNk/edit?usp=sharing

## **What We Created**

- A **syntax checker** for a small, well-defined programming language.
- Built using **Context-Free Grammars (CFGs)** and recursive descent parsing techniques.
- Designed to validate whether input programs/documents are syntactically correct.
- Mirrors real-world applications in **compiler design, interpreters, IDEs, and markup validators**.
- **Interactive menu system** for easy testing and batch processing.

## **Grammar Specification**

The parser validates assignment statements with arithmetic expressions:

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

**Operator Precedence** (highest to lowest):
1. Parentheses `()`
2. Multiplication `*` and Division `/`
3. Addition `+` and Subtraction `-`

## **Project Structure**

```
Theory-of-Computation-Group-Project/
├── README.md                       # This file - project overview
├── CODE_IMPLEMENTATION_PLAN.md     # Detailed implementation guide (1200+ lines)
├── QWEN.md                         # AI assistant context and documentation
│
└── SyntaxChecker/                  # Main source directory
    ├── lexer/                      # ✅ COMPLETE
    │   ├── Token.java              # Token representation
    │   ├── TokenType.java          # Token type enumeration
    │   └── Lexer.java              # Lexical analyzer
    │
    ├── parser/                     # ✅ COMPLETE
    │   ├── Parser.java             # Recursive descent parser
    │   ├── ParseTree.java          # Parse tree node structure
    │   └── ParseError.java         # Error representation
    │
    ├── util/                       # ✅ COMPLETE
    │   └── ErrorReporter.java      # Error collection and reporting
    │
    ├── main/                       # ✅ COMPLETE
    │   └── Main.java               # Entry point with interactive menu
    │
    └── test/
        ├── inputs/
        │   ├── valid/              # 10 valid test cases
        │   └── invalid/            # 10 invalid test cases
        ├── LexerTestRunner.java    # Lexer test runner with interactive menu
        ├── README.md               # Test suite documentation
        ├── run_tests.bat           # Windows test script
        └── run_tests.sh            # Unix/Mac test script
```

## **Building and Running**

### Prerequisites
- Java JDK (any version 8+)
- No external dependencies required

### Compilation

**Compile all source files:**
```bash
javac -d SyntaxChecker/bin SyntaxChecker/lexer/*.java SyntaxChecker/parser/*.java SyntaxChecker/util/*.java SyntaxChecker/main/*.java SyntaxChecker/test/*.java
```

### Running the Syntax Checker

#### **Interactive Menu Mode** (Recommended for exploration)

Run without arguments to access the interactive menu:

```bash
java -cp SyntaxChecker/bin main.Main
```

**Menu Options:**
1. Run all valid test cases
2. Run all invalid test cases
3. Run all test cases (valid + invalid)
4. Run a specific valid test case (select from list)
5. Run a specific invalid test case (select from list)
6. Run a custom input file
0. Exit

Each test allows you to enable **trace mode** to see the parsing steps.

#### **Direct Mode** (For scripting/automation)

```bash
java -cp SyntaxChecker/bin main.Main <input_file> [--trace]
```

**Examples:**
```bash
# Check a valid program
java -cp SyntaxChecker/bin main.Main SyntaxChecker/test/inputs/valid/test01_simple_assignment.txt

# Check with parsing trace enabled (for debugging)
java -cp SyntaxChecker/bin main.Main SyntaxChecker/test/inputs/valid/test03_precedence.txt --trace

# Check an invalid program
java -cp SyntaxChecker/bin main.Main SyntaxChecker/test/inputs/invalid/test01_missing_semicolon.txt
```

### Running Lexer Tests

#### **Interactive Menu Mode**

```bash
java -cp SyntaxChecker/bin test.LexerTestRunner
```

**Menu Options:**
1. Run all valid test cases
2. Run all invalid test cases
3. Run all test cases (valid + invalid)
4. Run a specific valid test case
5. Run a specific invalid test case
6. Run a custom input file
0. Exit

#### **Direct Mode**

```bash
java -cp SyntaxChecker/bin test.LexerTestRunner <input_file> <expected_result>
```

Where `<expected_result>` is either `valid` or `invalid`.

**Example:**
```bash
java -cp SyntaxChecker/bin test.LexerTestRunner SyntaxChecker/test/inputs/valid/test01_simple_assignment.txt valid
```

## **Implementation Status**

| Component       | Status      | Description                              |
|-----------------|-------------|------------------------------------------|
| **Lexer**       | ✅ Complete | Tokenizes input into identifiers, numbers, operators, delimiters |
| **Parser**      | ✅ Complete | Recursive descent parser with panic-mode error recovery |
| **ParseTree**   | ✅ Complete | Tree node structure with indented print support |
| **ErrorReporter**| ✅ Complete | Error collection and formatted reporting |
| **Main Entry**  | ✅ Complete | Dual-mode: interactive menu + direct command-line |
| **Test Suite**  | ✅ Complete | 20 test cases (10 valid, 10 invalid) |

## **Test Cases**

### Valid Programs (10 tests)
| Test | Input | Purpose |
|------|-------|---------|
| test01 | `x = 5;` | Simple assignment |
| test02 | `result = 10 + 20;` | Addition |
| test03 | `y = 2 + 3 * 4;` | Operator precedence (critical!) |
| test04 | `temp = (2 + 3) * 4;` | Parentheses override precedence |
| test05 | `a = 10 - 4 / 2;` | Subtraction and division |
| test06 | `z = a + b * c - d / e;` | Complex expression |
| test07 | `x = ((1 + 2) * 3) + 4;` | Nested parentheses |
| test08 | `my_var = 100;` | Underscore in identifier |
| test09 | `value = 12345;` | Multi-digit numbers |
| test10 | `x = 1 + 2 - 3 * 4 / 5;` | All operators |

### Invalid Programs (10 tests)
| Test | Input | Expected Failure |
|------|-------|------------------|
| test01 | `x = 5` | Missing semicolon |
| test02 | `x 5;` | Missing equals |
| test03 | `= 5;` | Missing identifier |
| test04 | `x = ;` | Missing expression |
| test05 | `x = 5 +;` | Incomplete expression |
| test06 | `x = (2 + 3;` | Unclosed parenthesis |
| test07 | `x = 2 + 3);` | Unmatched closing paren |
| test08 | `123abc = 5;` | Invalid identifier |
| test09 | `x = 5;;` | Extra semicolon |
| test10 | `x = @5;` | Invalid character (lexer ERROR) |

## **Deliverables**

- [x] **Grammar Specification** - Defined in README.md and CODE_IMPLEMENTATION_PLAN.md
- [x] **Parser Implementation** - Complete (lexer, parser, error reporting, main entry point)
- [x] **Test Suite** - 20 test cases with interactive test runner
- [ ] **Written Report** - Grammar design, ambiguity analysis, CNF conversion, parse trees
- [ ] **Interview/Demonstration** - Week 13

## **Assessment Criteria**

- Grammar accuracy and completeness (25%).
- Parser correctness and robustness (30%).
- Theoretical justification (ambiguity, CNF, parsing) (25%).
- Report quality and test cases (15%).
- Interview/presentation (5%).

## **Important Notes**

- **No automatic parser generators** (e.g., ANTLR, YACC, Bison) were used.
- External libraries allowed only for basic tasks (must be documented).
- Strict adherence to academic regulations (plagiarism rules apply).
- **Late submission penalties**:
  - 10% deduction (within 24 hours).
  - 20% deduction (24–48 hours).
  - Zero mark if more than 48 hours late.

---

✅ **Summary:** We built a **custom recursive descent parser and syntax checker** from scratch, demonstrating mastery of CFGs, parsing methods, and grammar transformations. The project includes an **interactive menu system** for easy testing, comprehensive test suites, and both direct and interactive modes. The deliverables are both **technical (code, test cases)** and **theoretical (report, interview)**, with marks spread across design, implementation, and explanation.
