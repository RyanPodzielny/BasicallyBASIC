# README

*Note: A full documented design and overview of the installation can be found within `BasicallyBASIC Docs.pdf`*


BasicallyBASIC is an dynamically typed interpreted programming language based on syntax and semantics of the [BASIC](https://en.wikipedia.org/wiki/BASIC) and [QuickBASIC](https://www.qbasic.net/en/reference/qb11/overview.htm) programming languages. It includes a command-line interface that allows you to run quick executable programs on a console, as well as a fully functional file ‘runner’ to run code from. Meaning the application is for programmers, so a background in computer science would be a good idea to have to use BasicallyBASIC.

In BasicallyBASIC, users can define and store variables, including integers, floats, booleans, strings, and lists, and perform a variety of operations on them. Logical comparisons and control flow are supported through if-else statements, while loops, and for loops, enabling the creation of complex programs. Functions are also possible and can be defined with arguments, making it easy to structure reusable code.  The language also provides several built-in functions, such as printing to the console, reading files, checking variable types, measuring string and lists lengths, and many more.

There is an error messaging system to help provide users with a better understanding of where their code went wrong. When an error occurs, the system highlights the specific location and provides a description of the issue, guiding the user toward a solution. 

Also, there is a VSCode extension available to have syntax highlighting and the ability to run BasicallyBASIC from an editor. If a user is looking for a better coding experience and environment, it is recommended that they use the extension so they can more easily debug and use the language.

More information on how to create valid code within BasicallyBASIC can be found within the User Manual section. It highlights the grammar design with Extended Backus-Naur Form, data types and variable assignment, built-in functions, etc. Everything you need to know to create a program in BasicallyBASIC can be found there. An installation guide can also be found under the Installation section.

### A Brief Implementation Breakdown
Before getting started, here is some important information on how the underlying program works. This is to give a better understanding of why things are structured the way they are, and to hopefully fill you in on the basics of creating an interpreted language. It will hopefully give you some information to start with so you aren’t completely dark. To start, the language consists of four major parts: the lexer, the parser, the abstract syntax tree and the Interpreter. 

The lexer does what is known as [lexical analysis](https://en.wikipedia.org/wiki/Lexical_analysis) which defines tokens for keywords, identifiers, literals and operators. Its function is to assign what are known as lexemes to their proper token. For example, ‘+’, ‘-’, ‘*’, and ‘/’ are examples of binary operators that need to be assigned a value in order to parse. 

The parser uses [recursive descent](https://en.wikipedia.org/wiki/Recursive_descent_parser) which takes the tokens from the lexer and creates what is known as an [abstract syntax tree (AST)](https://en.wikipedia.org/wiki/Abstract_syntax_tree) that represents the program. This tree is derived from the language grammar ensures that the inputted user code is semantically and syntactically correct (see User Manual for more details on grammar). It is one of the most important parts of the language, as the interpreter uses the AST to perform and evaluate the operations. 

Lastly, the interpreter using the generated AST, is where the code is actually evaluated. My implementation uses what is known as a tree-walking interpreter that can traverse the AST. In essence, it is the actual part of the project that is running the languages code. 

### Directory Structure

Now that you are caught up with what BasicallyBASIC is and are knowledgeable about its basic implementation, we need to ensure that you know where to find everything. Below is the directory structure of all the folders within the zip file:


├── BasicallyBasic

│   ├── prod

│   ├── src

│   │   ├── AbstractSyntaxTree

│   │   │   ├── Expressions

│   │   │   └── Statements

│   │   ├── Interpreter

│   │   ├── Language

│   │   ├── Lexer

│   │   ├── META-INF

│   │   └── Parser

│   └── test

│       ├── Cases

│       ├── Demo

│       └── Docs

├── External Libraries

├── Installation

└── VSCode Extension

    ├── out
    
    └── syntaxes

To start from the top, the `BasicallyBasic` directory holds all the code pertaining to the project, have three main directories within it. The `prod` folder contains the `.jar` file and is the actual production code that you can run. The `src` directory contains all the source code of the project, the directories inside of it are Java packages that contain related code close together for better file management. The sub-directory `test` holds all the testing code of the language, including the test cases to prove the language validity, the demo code from the video, and the examples found within the User Manual section.

Moving onto `External Libraries`, it contains only one library that was needed to implement BasicallyBASIC. The library inside of it is used for Java’s BigDecimal exponentiation, as Java’s Number classes do not natively support it – see the Design section if curious about its implementation.

Moving slightly out of order, the `VSCode Extension` folder has all the source files relating to how the VSCode extension for BasicallyBASIC works. It contains a bunch of `.json` files and a JavaScript file - basically it’s a bunch of settings to incorporate the BasicallyBASIC syntax into VSCode.

Lastly, the `Installation` directory contains all the files needed to actually run BasicallyBASIC. More notably, it contains the `.jar` file from `prod` and `.vsix` file to install the VSCode extension. Everything within this directory is for installing and running the program.

Now that you know where to find everything, the documentation below contains every specific installation instructure, language feature, and project implementation detail that you’ll ever need to know. Each major section starts on its own page and can be found within the Table of Contents, so it should be clear on where to find a specific piece of information.
