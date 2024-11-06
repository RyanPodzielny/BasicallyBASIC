///
/// Parse - Responsible for parsing the tokens from the lexer into an Abstract Syntax Tree (AST), that the Interpreter
///     can evaluate. We use recursive decent to parse the tokens and build the AST, using EBNF grammar rules to guide
///     us. This mean our grammar (found in the Grammar.txt file and within the comments of the functions) is directly
///     translated into code. We also have error checking to ensure the tokens match the grammar rules, and if not, we
///     throw an error to inform the user.
///

package Parser;

import AbstractSyntaxTree.Statements.ConditionalField;
import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.Expressions.*;
import AbstractSyntaxTree.Expressions.Assignment;
import AbstractSyntaxTree.Statements.*;
import AbstractSyntaxTree.Statements.For;
import AbstractSyntaxTree.Statements.While;
import Language.Error;
import Language.Error.ErrorType;
import Interpreter.Value;
import Lexer.OperationCode;
import Lexer.Token;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


public class Parser {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    Parser.Parser()

    NAME

        Parser - constructor for the parser.

    SYNOPSIS

        Parser(List<Token> a_tokens);
            a_tokens    -> the list of tokens to parse from the lexer.

    DESCRIPTION

        Initializes the list of tokens and sets up the state of the parser.
        Specifically, we set our nest flags to 0 and set our current token to
        the first token in the list. These tokens will be parsed to create the
        Abstract Syntax Tree (AST).

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Parser(List<Token> a_tokens) {
        m_tokens = a_tokens;
        m_currentTokenIndex = 0;
        m_currentToken = m_tokens.get(m_currentTokenIndex);
        m_savedTokenState = m_currentToken;

        // Not in any nesting structures yet
        m_ifElseNest = 0;
        m_loopNest = 0;
        m_functionNest = 0;
    }
    /* Parser(List<Token> a_tokens); */


    /** Methods **/

    // Driver function to parse the program and return the list of nodes
    public List<Node> ParseProgram() {
        return Program();
    }


    /* PRIVATE */

    /** Members **/

    private final List<Token> m_tokens;     // List of tokens to parse from Lexer
    private Token m_savedTokenState;        // Saves the state of the token before consuming
    private Token m_currentToken;           // Current token being parsed
    private int m_currentTokenIndex;        // The index of the current token within m_tokens

    // Flags to help determine positional statements, e.g. break or else, which can't be accessed in certain contexts. 
    //      They count the number of nests its currently in to determine if those statements are allowed (needs to be 
    //      able to detect nested value, hence why not just boolean).
    private int m_ifElseNest;     // See if we are in if-else statement or not
    private int m_loopNest;       // Whether we are in a loop or not
    private int m_functionNest;   // Parsing inside a function or not


    /** Constants **/

    // Each static set of operation codes are used to determine what operations are allowed in the current context when
    //      parsing. They are declared as static constants to avoid creating new sets each time they are used, so
    //      if we are deep within a nested structure, we don't have to create new sets each time we check the context.
    //      Done to reduce code duplication, e.g. binary operators all have 2 operands but need to result in a different
    //      evaluation based on the operator at a specific precedence level. Put here to easily change if needed. See
    //      grammar rules for more information, as each set corresponds to a different level of the grammar.
    private static final Set<OperationCode> BODY_TERMINATORS = EnumSet.of(
            OperationCode.END, OperationCode.EOF,
            OperationCode.ELSEIF, OperationCode.ELSE);

    private static final Set<OperationCode> LOGICAL_OPERATORS = EnumSet.of(
            OperationCode.AND, OperationCode.OR);
    private static final Set<OperationCode> EQUALITY_OPERATORS = EnumSet.of(
            OperationCode.EQUALITY, OperationCode.INEQUALITY);
    private static final Set<OperationCode> COMPARISON_OPERATORS = EnumSet.of(
            OperationCode.LESS_THAN, OperationCode.LESS_THAN_EQUAL,
            OperationCode.GREATER_THAN, OperationCode.GREATER_THAN_EQUAL);
    private static final Set<OperationCode> TERM_OPERATORS = EnumSet.of(
            OperationCode.ADD, OperationCode.SUBTRACT);
    private static final Set<OperationCode> UNARY_OPERATORS = EnumSet.of(
            OperationCode.ADD, OperationCode.SUBTRACT, OperationCode.NOT);
    private static final Set<OperationCode> POWER_OPERATORS = EnumSet.of(
            OperationCode.POWER);
    private static final Set<OperationCode> FACTOR_OPERATORS = EnumSet.of(
            OperationCode.MULTIPLY, OperationCode.DIVIDE, OperationCode.MODULO);


    /** Private Helper Functions **/

    /*
     *      Statements - Operations that have side effects
     */

    /**/
    /*
    Parser.Program()

    NAME

        Program - parses the program from the list of tokens.

    SYNOPSIS

        private List<Node> Program();

    DESCRIPTION

        Parses the program from the list of tokens. The program is a list of
        statements, which have optional newlines between. It will continue to
        parse statements until it reaches the end of the file - designated by
        the EOF operation code.

    RETURNS

        List<Node> - the list of statements that make up the program.

    */
    /**/
    private List<Node> Program() {
        /*
         * Grammar Rules
         * <program>               :   ('NEWLINE'* <statement> 'NEWLINE'*)* 'EOF'
         */

        // A program is just a bunch of statements
        List<Node> program = new ArrayList<>();

        // Make sure if user entered just a bunch of newlines we don't parse them
        IgnoreNewlines();

        // While we have not reached the end of the file, parse the statements
        while (!IsEndOfFile()) {
            program.add(Statement());

            // Ignore newlines between statements
            IgnoreNewlines();
        }

        return program;
    }
    /* private List<Node> Program(); */

    /**/
    /*
    Parse.Statement()

    NAME

        Statement - parses a statement from the list of tokens.

    SYNOPSIS

        private Node Statement();

    DESCRIPTION

        This function parses a statement from the inputted tokens. It can be a
        function definition, an if-elseif-else conditional, a while loop, a for
        loop, a jump control statement, or an expression. It will error if a
        positional statement (e.g. BREAK outside a loop or ELSEIF not in IF
        chain). It will also ensure that a newline is present after a statement
        unless it is the end of the file or the next token ends a body.

    RETURNS

        A Node object that represents the statement parsed.

    */
    /**/
    private Node Statement() {
        /*
         * Grammar Rules
         * <statement>             :   <function>
         *                             | <if-conditional>
         *                             | <while-loop>
         *                             | <for-loop>
         *                             | <jump>
         *                             | <expression>
         */

        // If we detect a statement that has a positional context, e.g. break or else with no if body, show a detailed
        //  error message. Can be caught within Atom(), but this is much more descriptive.
        ErrorOnPositionalStatements();

        // Help received: IDE suggestion for switch expression
        Node statement = switch (m_currentToken.GetOpcode()) {
            // These statements don't require a newline after them, it's pretty clear where they end
            case DEF -> Function();
            case IF -> IfConditional();
            case WHILE -> WhileLoop();
            case FOR -> ForLoop();
            case BREAK, CONTINUE, RETURN -> Jump();

            // Not a statement start, so it must be an expression
            default -> Expression();
        };

        // Newline only optional at EOF, END, ELSEIF, or ELSE as it's unambiguous where the statement ends
        if (!BODY_TERMINATORS.contains(m_currentToken.GetOpcode())) { ForceNewline(); }

        return statement;
    }
    /* private Node Statement(); */

    /**/
    /*
    Parser.Function()

    NAME

        Function - parses a function definition if token starts with 'DEF'.

    SYNOPSIS

        private Function Function();

    DESCRIPTION

        When a statement start with the keyword 'DEF' we are in a function
        definition. 'DEF' must be followed by an identifier for the function
        name, a list of parameters which must be 'IDENTIFIER' operation codes,
        and the body of the function. The function body is ended by the 'END'
        keyword. If the tokens do not match the grammar rules, an error is
        thrown to inform the user.

    RETURNS

        A Function node object that represents the function definition.

    */
    /**/
    private Function Function() {
        /*
         * Grammar Rules
         * <function>              :   'DEF' 'IDENTIFIER' '(' ('IDENTIFIER' (',' 'IDENTIFIER')* )? ')' <body> 'END'
         */

        MarkInFunction();
        Advance(OperationCode.DEF);

        // Save where we started
        int functionLineNumber = m_savedTokenState.GetLineNumber();


        Advance(OperationCode.IDENTIFIER);
        String functionName = m_savedTokenState.GetValue().AsString();

        // Get the parameters for the function
        List<Node> parameters = ParseArguments(OperationCode.LPARENTHESIS, OperationCode.RPARENTHESIS,
                                               this::ParseIdentifier);

        // Cast them to Identifiers. ParseArguments() has same structure for calls, lists and function definitions, but
        //      the only thing that changes is what is "consumes" as the arguments. We need identifiers in the
        //      function definition, so if we can't cast there is a huge problem.
        List<Identifier> identifiers = new ArrayList<>();
        for (Node parameter : parameters) { identifiers.add((Identifier) parameter); }

        // Get the body of the function
        Body body = Body();

        // Need to ensure we end the function
        ExpectEndOfBody("function definition", functionLineNumber);
        MarkOutFunction();

        return new Function(functionName, identifiers, body);
    }
    /* private Function Function(); */

    /**/
    /*
    Parser.IfConditional()

    NAME

        IfConditional - parses an if-elseif-else conditional chain statement.

    SYNOPSIS

        private IfElseifElse IfConditional();

    DESCRIPTION

        When a statement starts with the keyword 'IF' we are in an
        if-elseif-else chain. After the 'IF' keyword, we expect an expression
        to evaluate, followed by the 'THEN' keyword. If these are not met an
        error is thrown. The body of the if statement is then parsed. If there
        are any 'ELSEIF' tokens, they are parsed in a loop until we either hit
        an 'ELSE' token or the 'END' token of the conditional. These conditions
        are stored within a list of ConditionalField objects, which will store
        the condition and body of the conditional.

        For example, this would be a valid if-elseif-else chain:
        'IF x < 10 THEN PRINT(x) ELSEIF x < 20 THEN PRINT(x) ELSE PRINT(x) END'.

        The if-elseif-else chain can also have an 'ELSE' token, which will be
        parsed if it is present. Regardless of the chain, 'END' token is always
        required to end the conditional. If the tokens do not match the grammar
        rules, an error is thrown to inform the user.

    RETURNS

        An IfElseifElse Node that represents the if-elseif-else chain is return.
        It will contain a list of ConditionalField objects that store the chain,
        if it is length 1 it is just an if statement, if it is more than 1 it is
        an else-if chain. If it had an else statement, it will be stored in the
        IfElseifElse object as well.

    */
    /**/
    private IfElseifElse IfConditional() {
        /*
         * Grammar Rules
         * <if-conditional>        :   'IF' <expression> 'THEN' <body>
         *                             <elseif-conditional>*
         *                             <else-conditional>?
         *                             'END'
         */

        // Conditions for the if-elseif-else chain. If size is one, only if, more than one is else-if chain.
        MarkInConditional();
        List<ConditionalField> a_conditions = new ArrayList<>();

        /* IF */
        Advance(OperationCode.IF);

        // Save line number for error message at runtime
        int ifLineNumber = m_savedTokenState.GetLineNumber();

        // Parse the condition and body
        Node ifCondition = Expression();
        Advance(OperationCode.THEN);
        Body ifBody = Body();

        // Add the if condition
        a_conditions.add(new ConditionalField(ifCondition, ifBody, ifLineNumber));

        /* ELSE-IF */
        // While we have an else-if chain, add their conditions and bodies
        while (m_currentToken.GetOpcode() == OperationCode.ELSEIF) {
            a_conditions.add(ElseIfConditional());
        }

        /* ELSE */
        // Check if we have an else statement. Conditional constructor will handle empty/null check of elseIfConditions,
        //      elseIfBodies, and elseBody.
        // Help received: IDE suggestion for ternary
        IfElseifElse ifElseBody = m_currentToken.GetOpcode() == OperationCode.ELSE ?
                                  new IfElseifElse(a_conditions, ElseConditional()) :
                                  new IfElseifElse(a_conditions);

        // Need to ensure we end the conditional body
        ExpectEndOfBody("conditional", ifLineNumber);
        MarkOutConditional();

        // Conditional constructor will handle empty/null check of elseIfConditions, elseIfBodies, and elseBody
        return ifElseBody;
    }
    /* private IfElseifElse IfConditional(); */

    /**/
    /*
    Parser.ElseIfConditional()

    NAME

        ElseIfConditional - parses an else-if conditional statement.

    SYNOPSIS

        private ConditionalField ElseIfConditional();

    DESCRIPTION

        When a statement starts with the keyword 'ELSEIF' we are in an
        else-if conditional. After the 'ELSEIF' keyword, we expect an expression
        that represents the condition to evaluate, followed by the 'THEN'
        keyword. An 'ELSEIF' keyword can only be parsed if it is attached to
        an already existing if-elseif-else chain. If these are not met an error
        is thrown. For example, this would be a valid else-if statement:
        'ELSEIF x < 10 THEN PRINT(x) END'.

        At any point if we hit an 'ELSE' or 'END' token, we end that elseif
        conditional and return the condition and body. If the tokens do not
        match the grammar rules, an error is thrown to inform the user.

    RETURNS

        A ConditionalField object that represents the else-if conditional. It
        will store the condition and body of the conditional.

    */
    /**/
    private ConditionalField ElseIfConditional() {
        /*
         * Grammar Rules
         * <elseif-conditional>    :   'ELSEIF' <expression> 'THEN' <body>
         */

        Advance(OperationCode.ELSEIF);

        // Save line number for error message at runtime
        int elseIfLineNumber = m_savedTokenState.GetLineNumber();

        // Parse the condition and else-if body
        Node elseIfCondition = Expression();
        Advance(OperationCode.THEN);
        Body elseIfBody = Body();

        return new ConditionalField(elseIfCondition, elseIfBody, elseIfLineNumber);
    }
    /* private ConditionalField ElseIfConditional(); */

    /**/
    /*
    Parser.ElseConditional()

    NAME

        ElseConditional - parses an else after an if-elseif chain.

    SYNOPSIS

        private Body ElseConditional();

    DESCRIPTION

        When a statement starts with the keyword 'ELSE' we are in an else, so
        parse its body. An 'ELSE' keyword can only be parsed if it is attached
        to an already existing if-elseif-else chain. If these are not met an
        error is thrown. For example, this would be a valid else statement:
        'ELSE PRINT("No conditions met") END'.

        If the tokens do not match the grammar rules, an error is thrown to
        inform the user.

    RETURNS

        A Body object that represents the body of the else statement.

    */
    /**/
    private Body ElseConditional() {
        /*
         * Grammar Rules
         * <else-conditional>      :   'ELSE' <body>
         */

        Advance(OperationCode.ELSE);

        // Get the body of code for the else statement
        return Body();
    }

    /**/
    /*
    Parser.WhileLoop()

    NAME

        WhileLoop - parses a while loop statement.

    SYNOPSIS

        private While WhileLoop();

    DESCRIPTION

        When a statement starts with the keyword 'WHILE' we are in a while loop.
        Mark our nested loop flag to indicate we are in a loop, so we can parse
        break and continue statements. Our 'WHILE' keyword must be followed by
        an expression which evaluates to a boolean, followed by the 'THEN'
        keyword. If these are not met an error is thrown. The body of the while
        loop is then parsed. For example, this would be a valid while loop:
        'WHILE x < 10 THEN PRINT(x); LET x = x - 1 END'.

        The loop is ended by the 'END' keyword. If the
        tokens do not match the grammar rules, an error is thrown to inform the
        user.

    RETURNS

        A While Node that represents the data from the while loop statement.

    */
    /**/
    private While WhileLoop() {
        /*
         * Grammar Rules
         * <while-loop>            :   'WHILE' <expression> 'THEN' <body> 'END'
         */

        MarkInLoop();
        Advance(OperationCode.WHILE);

        // Save line number for error message at runtime
        int whileLineNumber = m_savedTokenState.GetLineNumber();

        // Parse the condition and body with
        Node condition = Expression();
        Advance(OperationCode.THEN);
        Body body = Body();

        // Need to ensure we end the loop
        ExpectEndOfBody("while loop", whileLineNumber);
        MarkOutLoop();

        return new While(new ConditionalField(condition, body, whileLineNumber));
    }
    /* private While WhileLoop(); */

    /**/
    /*
    Parser.ForLoop()

    NAME

        ForLoop - parses a for loop statement.

    SYNOPSIS

        private For ForLoop();

    DESCRIPTION

        When a statement starts with the keyword 'FOR' we are in a for loop.
        Mark our nested loop flag to indicate we are in a loop, so we can parse
        break and continue statements. Our 'FOR' keyword must be followed by an
        assignment, e.g. 'FOR LET i = 10', the 'TO' keyword, an expression for
        the end value, the 'STEP' keyword followed by an expression for the
        increment value. It must be followed by the 'THEN' keyword, and the
        body of the loop. For example, this would be a valid for loop:
        'FOR LET i = 0 TO 10 STEP 1 THEN PRINT(i) END'.

        The loop is ended by the 'END' keyword. If the tokens do not match the
        grammar rules, an error is thrown to inform the user.

    RETURNS

        A For Node that represents the data from the for loop statement.

    */
    /**/
    private For ForLoop() {
        /*
         * Grammar Rules
         * <for-loop>              :   'FOR' <assignment> 'TO' <expression> 'STEP' <expression> 'THEN' <body> 'END'
         */

        MarkInLoop();
        Advance(OperationCode.FOR);

        // Save line number for error message at runtime
        int forLineNumber = m_savedTokenState.GetLineNumber();

        // 'FOR' must be followed by an assignment
        Assignment assignment = Assignment();

        // Get the ending value for the loop
        Advance(OperationCode.TO);
        Node endValue = Expression();

        // See how much to increment by (our step)
        Advance(OperationCode.STEP);
        Node incrementValue = Expression();

        // Get the body of code for the loop
        Advance(OperationCode.THEN);
        Body loopBody = Body();
        
        // Need to ensure we end the loop
        ExpectEndOfBody("for loop", forLineNumber);
        MarkOutLoop();

        return new For(assignment, endValue, incrementValue, loopBody, forLineNumber);
    }
    /* private For ForLoop(); */


    /**/
    /*
    Parser.Jump()

    NAME

        Jump - parses a jump control statement, e.g. break, continue, return.

    SYNOPSIS

        private Jump Jump();

    DESCRIPTION

        When a statement starts with the keyword 'BREAK', 'CONTINUE', or
        'RETURN' we have a jump control statement. Break and continue can only
        exist without any expression on the same line, while return can have an
        expression to return. If these are not met an error is thrown. If no
        expression is found next to a 'RETURN' keyword, our NULL Value is
        returned.

        It will also error if we are not inside a loop or function when the
        positional tokens are found. Meaning, 'BREAK' and 'CONTINUE' can only
        be found inside a loop, and 'RETURN' can only be found inside a
        function. If the tokens do not match the grammar rules, an error is
        thrown to inform the user.

    RETURNS

        A Jump Node object that represents the jump control statement parsed.

    */
    /**/
    private Jump Jump() {
        /*
         * Grammar Rules
         * <jump>                  :   'BREAK' | 'CONTINUE' | ('RETURN' <expression>?)
         */

        // Ensure we are inside a loop before allowing loop control statements
        ErrorOnPositionalStatements();

        OperationCode opcode = m_currentToken.GetOpcode();
        switch(opcode) {
            // Record that we found a jump or continue
            case BREAK, CONTINUE:
                Advance(opcode);
                return new Jump(m_savedTokenState);
            // Can be 'RETURN' with or without an expression
            case RETURN:
                Advance(opcode);
                // No expression to return, so return null
                if (IsNewline()) { return new Jump(m_savedTokenState); }
                return new Jump(m_savedTokenState, Expression());

            // Should not be able to reach here, but if we do, it's a syntax error
            default:
                throw new Error("Expected a jump control statement, but got '" + opcode.LABEL + "'",
                                ErrorType.SYNTAX_ERROR,
                                m_savedTokenState.GetLineNumber());
        }
    }
    /* private Jump Jump(); */

    /**/
    /*
    Parser.Body()

    NAME

        Body - parses a body of code, which is a list of statements.

    SYNOPSIS

        private Body Body();

    DESCRIPTION

        A body of code is a list of statements. It will continue to parse
        statements until it reaches the end of the file, or a token that ends a
        body (ELSEIF, ELSE, END, or EOF). The body of code can be empty, so it
        will return an empty list if no statements are found. At any point, if
        the statement that is recursively called does not match the grammar
        rules, an error is thrown to inform the user.

    RETURNS

        A Body object storing the list of statements that make up the body of
        code.

    */
    /**/
    private Body Body() {
        /*
         * Grammar Rules
         * <body>                  :   <statement>*
         */

        // Any newlines before a new body can be ignored
        IgnoreNewlines();

        List<Node> statements = new ArrayList<>();
        // While there is nothing that signals the end of the body, keep parsing statements
        while (!BODY_TERMINATORS.contains(m_currentToken.GetOpcode())) {
            statements.add(Statement());
            // Ignore newlines between statements
            IgnoreNewlines();
        }

        return new Body(statements);
    }
    /* private Body Body(); */



    /*
     *      Expressions - Operations that will result in a value when evaluated
     */

    /**/
    /*
    Parser.Expression()

    NAME

        Expression - parses an expression from the list of tokens.

    SYNOPSIS

        private Node Expression();

    DESCRIPTION

        This function parses an expression from the inputted tokens. An
        expression can be an assignment, a logical operation, an equality
        operation, a comparison operation, a term operation, a factor operation,
        or a power operation. These rules are chain downwards (hence recursive
        decent parsing) and are evaluated if their respective tokens are found.
        It will continue to parse the expression until it reaches the 'EOF'
        token, or a token that ends an expression. If the tokens do not match
        the grammar rules, an error is thrown to inform the user.

    RETURNS

        A Node object that represents the expression parsed.

    */
    /**/
    private Node Expression() {
        /*
         * Grammar Rules
         * <expression>            :   <assignment>
         *                             | <logical>
         */

        if (m_currentToken.GetOpcode() == OperationCode.LET) { return Assignment(); }

        return Logical();
    }
    /* private Node Expression(); */

    /**/
    /*
    Parser.Assignment()

    NAME

        Assignment - parses an assignment statement.

    SYNOPSIS

        private Assignment Assignment();

    DESCRIPTION

        When a statement starts with the keyword 'LET' we are in an assignment.
        'LET' must be followed by an identifier, which is the variable we are
        assigning to. If there is an '=' token after the identifier, we are
        assigning a value to the variable. If there is no '=', we are declaring
        the variable with our NULL Value. If the tokens do not match the grammar
        rules, an error is thrown to inform the user.

    RETURNS

        An Assignment Node object that represents the assignment statement.
        Stores the variable name and the expression to assign to it (if there is
        one).

    */
    /**/
    private Assignment Assignment() {
        /*
         * Grammar Rules
         * <assignment>            :   'LET' 'IDENTIFIER' ('=' <expression>)?
         */

        Advance(OperationCode.LET);
        Advance(OperationCode.IDENTIFIER);

        // Save the identifier as the variable to assign to
        Token variable = m_savedTokenState;

        // If we are declaring here, we need to have an '=' sign to assign a value
        if (m_currentToken.GetOpcode() == OperationCode.ASSIGNMENT) {
            Advance(OperationCode.ASSIGNMENT);
            return new Assignment(variable, Expression());
        }

        // End of file or line, so no expression to assign and we have a null value
        if (IsEndOfFile() || IsNewline() || IsSeparator()) { return new Assignment(variable, new Literal(Value.NULL)); }

        // If something follows the identifier, not being EOF or newline, it means it's an expression (e.g.
        //      'LET x + 3 = 2'). Thrown here to have a more descriptive error message.
        throw new Error("Expected a complete assignment statement, but got " +
                            "'" + m_currentToken.GetOpcode().LABEL + "'",
                        ErrorType.SYNTAX_ERROR,
                        m_savedTokenState.GetLineNumber());
    }
    /* private Assignment Assignment(); */

    /**/
    /*
    Parser.Logical()

    NAME

        Logical - parses a logical operation.

    SYNOPSIS

        private Node Logical();

    DESCRIPTION

        When parsing a logical operation, we are looking for 'AND' or 'OR'
        operators. These operators are used to combine two expressions together
        and evaluate them. We call the ParseBinaryOperation function to parse
        the logical operation, if the equality operation is followed by the
        'AND' or 'OR' operators we have a logical operation. If those keywords
        are not found, then we just have an equality operation (descending until
        we hit the base case).

        For example 'x == 10 AND y == 20' or 'x == 10 OR y == 20' would be a
        valid logical operation.

    RETURNS

        A Node object that represents the logical operation parsed.

    */
    /**/
    private Node Logical() {
        /*
         * Grammar Rules
         * <logical>               :   <equality> (('AND' | 'OR') <equality>)*
         */

        return ParseBinaryOperation(Equality(), LOGICAL_OPERATORS, this::Equality);
    }
    /* private Node Logical(); */

    /**/
    /*
    Parser.Equality()

    NAME

        Equality - parses an equality operation.

    SYNOPSIS

        private Node Equality();

    DESCRIPTION

        When parsing an equality operation, we are looking for '==' or '!='
        operators. These operators are used to compare two expressions together
        and see if they are equal or not. We call the ParseBinaryOperation
        function to parse the equality operation, if the token after comparison
        is followed by the '==' or '!=' operators we have an equality operation.
        If those keywords are not found, then we just have a comparison
        operation (descending until we hit the base case).

        For example 'x == 10' or 'x != 10' would be a valid equality operation.

    RETURNS

        A Node object that represents the equality operation parsed.

    */
    /**/
    private Node Equality() {
        /*
         * Grammar Rules
         * <equality>              :   <comparison> (('==' | '!=') <comparison>)*
         */

        return ParseBinaryOperation(Comparison(), EQUALITY_OPERATORS, this::Comparison);
    }
    /* private Node Equality(); */

    /**/
    /*
    Parser.Comparison()

    NAME

        Comparison - parses a comparison operation.

    SYNOPSIS

        private Node Comparison();

    DESCRIPTION

        When parsing a comparison operation, we are looking for '<', '<=', '>',
        or '>=' operators. These operators are used to compare two expressions
        together and see if they are less than, less than or equal to, greater
        than, or greater than or equal to. We call the ParseBinaryOperation
        function to parse the comparison operation, if the token after the term
        is followed by the above symbols we have a comparison operation. If
        those keywords are not found, then we just have a term operation - still
        descending until we hit the base case.

        For example 'x < 10' or 'x >= 10' would be a valid comparison operation.

    RETURNS

        A Node object that represents the comparison operation parsed.

    */
    /**/
    private Node Comparison() {
        /*
         * Grammar Rules
         * <comparison>            :   <term> (('<' | '<=' | '>' | '>=') <term>)*
         */

        return ParseBinaryOperation(Term(), COMPARISON_OPERATORS, this::Term);
    }
    /* private Node Comparison(); */

    /**/
    /*
    Parser.Term()

    NAME

        Term - parses a term operation.

    SYNOPSIS

        private Node Term();

    DESCRIPTION

        When parsing a term operation, we are looking for '+' or '-' operators.
        These operators are used to add or subtract two expressions together,
        and can be found above the factor operation as they are of lower
        precedence. We call the ParseBinaryOperation function to parse the term
        operation, if the token after factor is followed by the '+' or '-'
        operators we have a term operation. If those keywords are not found,
        then we just have a factor operation - still descending until we hit the
        base case.

        For example 'x + 10' or 'x - 10' would be a valid term operation.

    RETURNS

        A Node object that represents the term operation parsed.

    */
    /**/
    private Node Term() {
        /*
         * Grammar Rules
         * <term>                  :   <factor> (('+' | '-') <factor>)*
         */

        return ParseBinaryOperation(Factor(), TERM_OPERATORS, this::Factor);
    }
    /* private Node Term(); */

    /**/
    /*
    Parser.Factor()

    NAME

        Factor - parses a factor operation.

    SYNOPSIS

        private Node Factor();

    DESCRIPTION

        When parsing a factor operation, we are looking for '*', '/', or '%'
        operators. These operators are used to multiply, divide, or modulo two
        expressions together, and can be found above the power operation as they
        are of lower precedence. We call the ParseBinaryOperation function to
        parse the factor operation, if the factor operation is followed by the
        above symbols we have a factor operation.

        For example 'x * 10' or 'x / 10' would be a valid factor operation. As
        well as 'x % 10' for modulo.

    RETURNS

        A Node object that represents the factor operation parsed.

    */
    /**/
    private Node Factor() {
        /*
         * Grammar Rules
         * <factor>                :   <power> (('*' | '/' | '%') <power>)*
         */

        return ParseBinaryOperation(Power(), FACTOR_OPERATORS, this::Power);
    }
    /* private Node Factor(); */

    /**/
    /*
    Parser.Power()

    NAME

        Power - parses a power operation.

    SYNOPSIS

        private Node Power();

    DESCRIPTION

        When parsing a power operation, we are looking for the '^' operator.
        This operator is used to raise the left expression to the power of the
        right expression. It can be found above the accessor call as it's of
        lower precedence. We call the ParseBinaryOperation function to parse the
        power operation, if the accessor operation is followed by the '^'
        operator we have a power operation. If not found, we just have an
        accessor operation - still descending until we hit the base case.

        For example 'x ^ 10' or '2 ^ 1.1' would be a valid power operation.

    RETURNS

        A Node object that represents the power operation parsed.

    */
    /**/
    private Node Power() {
        /*
         * Grammar Rules
         * <power>                 :   <accessor> ('^' <accessor>)*
         */

        return ParseBinaryOperation(Accessor(), POWER_OPERATORS, this::Accessor);
    }
    /* private Node Power(); */

    /**/
    /*
    Parser.Accessor()

    NAME

        Accessor - parses an accessor operation; which accesses a string or list
        at a specific index or range.

    SYNOPSIS

        private Node Accessor();

    DESCRIPTION

        When parsing an accessor operation, we are looking for ':'. First we
        parse the unary operation, which is the value to be accessed, and if it
        is followed by a ':', we are accessing a specific index. This must be
        followed by another unary operation, which is the index to access at.

        If we find a '|', we are accessing a range of indices. The unary before
        the '|' is the starting index, and the unary after the '|' is the ending
        index. If the tokens do not match the grammar rules, an error is thrown
        to inform the user. The '|' is optional, so we can have a single index
        accessor or a ranged index accessor. And it can be chained together, so
        we can have multiple accessors in a row.

        If no ':' is found, we just have a unary operation - still descending
        until we hit the base case. For example 'x:0' or 'x:0|2' would be a
        valid accessor operation.

    RETURNS

        A Node object that represents the accessor operation parsed.

    */
    /**/
    private Node Accessor() {
        /*
         * Grammar Rules
         * <accessor>              :   <unary> (':' <unary> ('|' <unary>)? )*
         */

        // First unary is the item to be accessed (if we find a colon)
        Node rootUnary = Unary();

        // While we have a colon, continue parsing for more unary operators (the indices)
        while (m_currentToken.GetOpcode() == OperationCode.ACCESSOR) {
            Advance(OperationCode.ACCESSOR);

            // Next unary is the index (could be starting one if ranged based subscript)
            Node index = Unary();

            // If we have a ranged based subscript with '|', parse the next unary operation for ending index
            if (m_currentToken.GetOpcode() == OperationCode.SUBSCRIPT) {
                Advance(OperationCode.SUBSCRIPT);
                // Get the ending index
                Node endIndex = Unary();

                // Create the ranged subscript accessor, putting the root as the value to be accessed
                rootUnary = new Index(rootUnary, m_savedTokenState, index, endIndex);
            }
            // No ranged subscript, so only one index
            else { rootUnary = new Index(rootUnary, m_savedTokenState, index); }
        }

        return rootUnary;
    }
    /* private Node Accessor(); */

    /**/
    /*
    Parser.Unary()

    NAME

        Unary - parses a unary operation.

    SYNOPSIS

        private Node Unary();

    DESCRIPTION

        When parsing a unary operation, we are looking for '+', '-', or 'NOT'.
        If we hit any of these tokens, it means we are performing a unary
        operation on the next expression. As they can be chained together, e.g.
        '---3', we call the Unary function recursively to parse the next unary
        operation. If we do not have any of those tokens, it means we have a
        call operation (or an atom if not a function call).

        For example '-3' or 'NOT TRUE' would be a valid unary operation.

    RETURNS

        A Node object that represents the unary operation parsed.

    */
    /**/
    private Node Unary() {
        /*
         * Grammar Rules
         * <unary>                 :   ('-' | '+' | 'NOT') <unary>
         *                             | <call>
         */

        // Check if we have a unary operations, i.e. '+', '-', 'NOT'
        if (UNARY_OPERATORS.contains(m_currentToken.GetOpcode())) {
            Advance(m_currentToken.GetOpcode());

            // Fetch another unary after the unary operator
            return new UnaryOperator(m_savedTokenState, Unary());
        }

        // If we don't have a unary operator, just return the call (or atom)
        return Call();
    }
    /* private Node Unary(); */


    /**/
    /*
    Parser.Call()

    NAME

        Call - parses a call operation.

    SYNOPSIS

        private Node Call();

    DESCRIPTION

        When parsing a call operation, we are looking for a function call. A
        function call is an atom followed by a parenthesis, which contains the
        arguments for the function. If we find a parenthesis, we parse the
        arguments for the function call. It can be 0 or more arguments, and they
        are separated by a comma. If the tokens do not match the grammar rules,
        an error is thrown to inform the user.

        If no parenthesis is found, it means we just have an atom. For example
        'print("Hello")' or 'add(3, 2)' would be a valid call operation. While
        'x' or '3' would be a valid atom.

    RETURNS

        A Node object that represents the call operation parsed.

    */
    /**/
    private Node Call() {
        /*
         * Grammar Rules
         * <call>                  :   <atom> '(' ( <expression> (',' <expression>)* )? ')'
         *                             | <atom>
         */

        Node atom = Atom();

        if (m_currentToken.GetOpcode() == OperationCode.LPARENTHESIS) {
            // Line number of the call for error message at runtime (current to get the LPARENTHESIS)
            int lineNumber = m_currentToken.GetLineNumber();

            // Parse the arguments for the call and return the call node
            List<Node> arguments = ParseArguments(OperationCode.LPARENTHESIS, OperationCode.RPARENTHESIS,
                                                  this::Expression);
            return new Call(atom, arguments, lineNumber);
        }

        // If we don't have a call, just return the atom
        return atom;
    }
    /* private Node Call(); */

    /**/
    /*
    Parser.Atom()

    NAME

        Atom - parses an atom operation; the base case of the expression
        parsing and recursive descent.

    SYNOPSIS

        private Node Atom();

    DESCRIPTION

        When parsing an atom operation, we are looking for an identifier, a
        literal, a group, or a list. If we find an identifier, it means we are
        looking at a variable, if we find a literal, it means we are looking at
        'INTEGER', 'FLOAT', 'STRING', 'TRUE', 'FALSE', or 'NULL'. If we find a
        group, it means we are looking at an expression surrounded by
        parenthesis. If we find a list, it means we are looking at a list of
        expressions surrounded by brackets. These are the items with the highest
        precedence in the expression parsing.

        For example 'x', '3', '(3 + 2)', or '[1, 2, 3]' would be a valid atom.
        Or "Hello" or 'TRUE' would be a valid literal.

        As we are at the end of the expression parsing, if we do not hit a match
        here it means we've encountered bad syntax. An error is thrown to inform
        the user of their mistake, but as we can expect an extremely long list
        of values - we just say we expected a complete expression.

    RETURNS

        A Node object that represents the atom operation parsed.

    */
    /**/
    private Node Atom() {
        /*
         * Grammar Rules
         * <atom>                  :   'IDENTIFIER'
         *                             | <literal>
         *                             | <group>
         *                             | <list>
         */

        // Help received: IDE suggestion for switch expression
        return switch (m_currentToken.GetOpcode()) {
            // Literals
            case LITERAL, TRUE, FALSE, NULL -> Literal();
            // Variables
            case IDENTIFIER -> ParseIdentifier();
            // Parenthesized expressions
            case LPARENTHESIS -> Group();
            // Lists as literal
            case LBRACKET -> List();

            // Not matching the grammar, we expected to finish an expression at this point. So error is more general
            //      than simply expecting a particular token.
            default ->
                throw new Error("Expected a complete expression, but got " +
                                    "'" + m_currentToken.GetOpcode().LABEL + "'",
                                ErrorType.SYNTAX_ERROR,
                                m_savedTokenState.GetLineNumber());
        };
    }
    /* private Node Atom(); */

    /**/
    /*
    Parser.Literal()

    NAME

        Literal - parses a literal operation; which can be an 'INTEGER',
        'FLOAT', 'STRING', 'TRUE', 'FALSE', or 'NULL'.

    SYNOPSIS

        private Literal Literal();

    DESCRIPTION

        When parsing a literal operation, we are looking for a value with a
        literal representation, i.e. an actual value.

        For example '3', '3.14', '"Hello"', 'TRUE', 'FALSE', or 'NULL' would
        be a valid literal.

    RETURNS

        A Literal Node object that represents the value parsed.

    */
    /**/
    private Literal Literal() {
        /*
         * Grammar Rules
         * <literal>               :   'INTEGER' | 'FLOAT' | 'STRING' | 'TRUE' | 'FALSE' | 'NULL'
         */

        // Advance current token and return the saved literal
        Advance(m_currentToken.GetOpcode());
        return new Literal(m_savedTokenState.GetValue());
    }
    /* private Literal Literal(); */

    /**/
    /*
    Parser.Group()

    NAME

        Group - parses a group operation; which is an expression surrounded by
        parenthesis.

    SYNOPSIS

        private Group Group();

    DESCRIPTION

        When parsing a group operation, we are looking for an expression that is
        within parenthesis. This is used to indicate the order of operations,
        e.g. '(3 + 2) * 5'. We parse the expression inside the parenthesis and
        return it. Meaning we expect a left parenthesis, an expression, and then
        a right parenthesis. If the tokens do not match the grammar rules, an
        error is thrown to inform the user.

    RETURNS

        A Group Node object that represents the expression inside the group.

    */
    /**/
    private Group Group() {
        /*
         * Grammar Rules
         * <group>                 :   '(' <expression> ')'
         */

        // Groups are surrounded by parenthesis to indicate order of operations, e.g. (3 + 2) * 5
        // Advance out inner and outer parenthesis and parse the expression inside
        Advance(OperationCode.LPARENTHESIS);
        Node expression = Expression();
        Advance(OperationCode.RPARENTHESIS);

        return new Group(expression);
    }
    /* private Group Group(); */

    /**/
    /*
    Parser.List()

    NAME

        List - parses a list operation; which is a list of expressions
        surrounded by brackets.

    SYNOPSIS

        private ListExpression List();

    DESCRIPTION

        When parsing a list operation, we are looking for a list of expressions
        that are within brackets. First we expect a left bracket, then we parse
        an expression, and if there is a comma, we parse more expressions - we
        do this until we reach the right bracket. A list can have no expressions
        within it, so it can be empty. If the tokens do not match the grammar
        rules, an error is thrown to inform the user.

        For example '[1, 2, 3]' or '["Hello", "World"]' would be a valid list,
        and '[]' would be an empty list. We are index 0 based, so the first
        element is at index 0.

    RETURNS

        A ListExpression Node object that represents the list of expressions
        inside the brackets.

    */
    /**/
    private ListExpression List() {
        /*
         * Grammar Rules
         * <list>                  :   '[' ( <expression> (',' <expression>)* )? ']'
         */

        // When parsing we can have lists with expressions in them, so we need to parse them as such. E.g. [1 + 2, "hi"]
        List<Node> expressions = ParseArguments(OperationCode.LBRACKET, OperationCode.RBRACKET, this::Expression);

        return new ListExpression(expressions);
    }
    /* private ListExpression List(); */


    /*
     *      Helping Parse - Avoiding code duplication and make parsing cleaner in grammar functions
     */

    /**/
    /*
    Parser.ParseBinaryOperation()

    NAME

        ParseBinaryOperation - parses a binary operation; e.g. addition,
        subtraction, multiplication, division, etc.

    SYNOPSIS

        private Node ParseBinaryOperation(Node a_leftExpression,
                                      Set<OperationCode> a_operators,
                                      Supplier<Node> a_rightExpressionFunction);
            a_leftExpression            -> the left expression of the binary
                operation.
            a_operators                 -> the set of operators that are allowed
                for the specific binary operation.
            a_rightExpressionFunction   -> the function to call to get the right
                expression. This is a supplier function to allow for recursive
                calls of different functions, e.g. Logical(), Comparison(),
                Term(), etc.

    DESCRIPTION

        When parsing a binary operation, we are looking for an operation that
        combines two expressions together. This can be addition, subtraction,
        multiplication, division, power, etc. This helper function is called
        by all the binary operation parsing functions (logical, equality,
        comparison, term, factor, power) to parse the binary operation. As they
        all are binary expressions and have the same rules, we can avoid code
        duplication by allowing them to pass in the right expression as a lambda
        function. This function will then parse the binary operation according
        to the rules of the specific binary operation.

        It is encouraged to look at the grammar of a specific binary operation
        to see what set of operators are allowed, and what the left and right
        expressions are.

        For example '3 + 2' and '3 - 2' would be a valid term operation, where
        the right function is the factor operation. And '3 * 2' and '3 / 2'
        would be a valid factor operation, where the right function is the power
        operation. Once again, as binary operations all share the same structure
        for their code, it makes sense to allow a function to be passed in to
        get the right expression.

    RETURNS

        A Node object that represents the binary operation parsed.

    */
    /**/
    private Node ParseBinaryOperation(Node a_leftExpression, Set<OperationCode> a_operators,
                                      Supplier<Node> a_rightExpressionFunction) {
        // Help received: https://www.baeldung.com/java-callable-vs-supplier for the right expression function

        // While we have a valid operator, continue parsing
        // This can repeat, e.g. having 3 terms "3 + 3 - 1", so we absorb the operator and term in a loop
        while (a_operators.contains(m_currentToken.GetOpcode())) {
            // Advance to move to next token
            Advance(m_currentToken.GetOpcode());

            // Left term becomes the root of the tree/subtree
            // Calling the right expression will cause a recursive call to create the Abstract Syntax Tree (AST),
            a_leftExpression = new BinaryOperator(a_leftExpression, m_savedTokenState, a_rightExpressionFunction.get());
        }

        // Return our root of the tree/subtree
        return a_leftExpression;
    }
    /* private Node ParseBinaryOperation(Node a_leftExpression,
                                 Set<OperationCode> a_operators,
                                 Supplier<Node> a_rightExpressionFunction); */

    /**/
    /*
    Parser.ParseArguments()

    NAME

        ParseArguments - parses a list of arguments for a function call, a list,
        or a function definition.

    SYNOPSIS

        private List<Node> ParseArguments(OperationCode a_startEncloser,
                                      OperationCode a_endEncloser,
                                      Supplier<Node> a_expressionFunction);
            a_startEncloser         -> the opening token that encloses the
                arguments, e.g. '(' for function calls, '[' for lists.
            a_endEncloser           -> the closing token that encloses the
                arguments, e.g. ')' for function calls, ']' for lists.
            a_expressionFunction    -> the function to call to get the
                expression for the argument. This is a supplier function to
                allow for different parsing functions to be called, e.g.
                ParseIdentifier() for function definitions, and Expression() for
                function calls and lists.

    DESCRIPTION

        When parsing arguments for a function call, a list, or a function
        definition, we are looking for a list of expressions that are separated
        by a comma. This function is called by the Call(), List(), and
        FunctionDefinition() functions to parse the arguments. As they all have
        the same rules for parsing arguments, we can avoid code duplication by
        allowing them to pass in the expression as a lambda function. This
        function will then parse the arguments according to the rules of the
        specific operation.

        It is encouraged to look at the grammar of a specific operation to see
        what the start and end enclosers are, and what the expression for the
        argument is.

        For example 'print("Hello", "World")' would be a valid function call,
        and '[1, 2, 3]' would be a valid list. And 'function add(x, y) { ... }'
        would be a valid function definition. As arguments all share the same
        structure for their code, it makes sense to allow a function to be
        passed in to get the expression/identifier for the argument. Similar to
        how the ParseBinaryOperation function works.

    RETURNS

        A list of Node objects that represents the arguments parsed.

    */
    /**/
    private List<Node> ParseArguments(OperationCode a_startEncloser, OperationCode a_endEncloser,
                                      Supplier<Node> a_expressionFunction) {

        // Help received: https://www.baeldung.com/java-callable-vs-supplier for the expression function

        // Arguments are surrounded by parenthesis/brackets
        Advance(a_startEncloser);

        // The arguments are a list of expressions, so we need to parse them as such (if there are any)
        List<Node> arguments = new ArrayList<>();

        // If not closed, parse the arguments
        if (m_currentToken.GetOpcode() != a_endEncloser) {
            arguments.add(a_expressionFunction.get());

            // While we have a comma, continue parsing for more arguments
            while (IsSeparator()) {
                Advance(OperationCode.COMMA);
                // Allowed to have a newline between arguments if they get long
                IgnoreNewlines();
                arguments.add(a_expressionFunction.get());
            }
        }

        // Close the end parenthesis/bracket
        Advance(a_endEncloser);

        return arguments;
    }
    /* private List<Node> ParseArguments(OperationCode a_startEncloser,
                                   OperationCode a_endEncloser,
                                   Supplier<Node> a_expressionFunction); */

    /**/
    /*
    Parser.ParseIdentifier()

    NAME

        ParseIdentifier - parses an identifier operation; which is a variable.

    SYNOPSIS

        private Identifier ParseIdentifier();

    DESCRIPTION

        When parsing an identifier operation, we are looking for a variable. In
        essence, it is just a name that represents a variable. It can be a
        function name, a variable name, or a parameter name. If we call this
        function, we are expecting an identifier token - so throw an error if we
        do not find one.

        For example 'x', 'add', or 'name' would be a valid identifier.

    RETURNS

        An Identifier object that represents the variable parsed.

    */
    /**/
    private Identifier ParseIdentifier() {
        // Just a variable, so return it
        Advance(OperationCode.IDENTIFIER);
        return new Identifier(m_savedTokenState.GetValue().AsString(), m_savedTokenState.GetLineNumber());
    }
    /* private Identifier ParseIdentifier(); */


    /*
     *      Management - Moving through tokens ensuring they are correct
     */

    /**/
    /*
    Parser.Advance()

    NAME

        Advance - moves to the next token in the list of tokens, if our current
        token is the expected token.

    SYNOPSIS

        private void Advance(OperationCode a_opcode);
            a_opcode    -> the expected token that we are on.

    DESCRIPTION

        Before advancing to the next token, we are checking if the current token
        is the expected token. If it is not, we throw an error to inform the user
        of their mistake. If it is the expected token, we move to the next token
        in the list of tokens.

        For example, if we are expecting a 'POWER' token, we call this function
        with 'POWER' as the expected token and throw and error if we do not find
        it. If we do, we are free to move to the next token.

    RETURNS

        No return value - function is void.

    */
    /**/
    private void Advance(OperationCode a_opcode) {
        // If the current token is not the expected token, throw an error
        if (m_currentToken.GetOpcode() != a_opcode) {
            throw new Error("Expected '" + a_opcode.LABEL + "', but got " +
                                "'" + m_currentToken.GetOpcode().LABEL + "'",
                            ErrorType.SYNTAX_ERROR,
                            m_savedTokenState.GetLineNumber());
        }

        // Move to the next token
        SetNextToken();
    }
    /* private void Advance(OperationCode a_opcode); */

    /**/
    /*
    Parser.SetNextToken()

    NAME

        SetNextToken - moves to the next token in the list of tokens, regardless
        of the end of file.

    SYNOPSIS

        private void SetNextToken();

    DESCRIPTION

        Before moving to the next token, we need to save the current token - so
        update the saved token state member. This is done to keep track of the
        tokens position within the line, making it easier to throw errors. This
        We also need to check if we've reached the end of the file, as we cannot
        move past the last token. If we have not reached the end of the file, we
        move to the next token in the list of tokens.

        Function keep track of the state of the parser, moving on to the next
        token when required.

    RETURNS

        No return value - function is void.

    */
    /**/
    private void SetNextToken() {
        // Save the current token and move to the next token, regardless of EOF
        m_savedTokenState = m_currentToken;

        // Check to see if we have no more tokens to consume (EOF must be the last token)
        if (!IsEndOfFile()) {
            m_currentTokenIndex++;
            m_currentToken = m_tokens.get(m_currentTokenIndex);
        }
    }
    /* private void SetNextToken(); */


    /*
     *      Error Handling - Ensuring the grammar is followed correctly
     */

    /**/
    /*
    Parser.ErrorOnPositionalStatements()

    NAME

        ErrorOnPositionalStatements - throws an error if a positional statement
        is found outside its allowed context.

    SYNOPSIS

        private void ErrorOnPositionalStatements();

    DESCRIPTION

        When parsing a positional statement, we are looking for 'ELSEIF', 'ELSE'
        for conditional control, 'BREAK', 'CONTINUE', 'TO', 'STEP' for loop
        control, and 'RETURN' for function control. These statements are only
        allowed in specific contexts, e.g. 'ELSEIF' and 'ELSE' are only allowed
        in a conditional body, 'BREAK' and 'CONTINUE' are only allowed in loops,
        'TO' and 'STEP' are only allowed in for loops, and 'RETURN' is only
        allowed within functions. We need to check throw and error if they exist
        outside their allowed context - which is what this function handles.

        For example, if we find a 'BREAK' when our loop nest is 0, we throw an
        error. Same goes for 'ELSEIF' and 'ELSE' when our conditional nest is 0,
        and 'RETURN' when our function nest is 0. Very important to ensure the
        grammar is followed correctly and the user knows about it.

        Also, it creates the message for the error depending on the statement
        found and the context it is found in - to make it extra clear.

    RETURNS

        No return value - function is void.

    */
    /**/
    private void ErrorOnPositionalStatements() {
        OperationCode opcode = m_currentToken.GetOpcode();
        // Error flag, if we've detected a positional statement outside its allowed context
        boolean isError = false;
        String message = "'" + opcode.LABEL + "' statement is not allowed outside of a ";

        // Check if the opcode is a positional statement, and if it is, check if it is allowed in the current context.
        //      If not, set error flag to true and append the message to the error message.
        switch (opcode) {
            // Conditional control
            case ELSEIF, ELSE:
                if (IsNotInConditional()) { message += "conditional body"; isError = true; }
                break;
            // Loop control
            case BREAK, CONTINUE, TO, STEP:
                if (IsNotInLoop()) { message += "loop"; isError = true; }
                break;
            // Function control
            case RETURN:
                if (IsNotInFunction()) { message += "function"; isError = true; }
                break;
            // Body control for loops and conditionals
            case THEN:
                if (IsNotInConditional() && IsNotInLoop()) { message += "conditional/loop body"; isError = true; }
                break;
            case END:
                if (IsNotInConditional() && IsNotInLoop() && IsNotInFunction()) { message += "body"; isError = true; }
                break;

            // Not a positional statement, so no error can be thrown
            default: return;
        }

        // Throw an error if we got one
        if (isError) {
            throw new Error(message,
                            ErrorType.SYNTAX_ERROR,
                            m_savedTokenState.GetLineNumber());
        }
    }
    /* private void ErrorOnPositionalStatements(); */

    /**/
    /*
    Parser.ExpectEndOfBody()

    NAME

        ExpectEndOfBody - throws an error if the end of a body for a specific
        body type is not found.

    SYNOPSIS

        private void ExpectEndOfBody(String a_bodyType, int startLine);
            a_bodyType      -> the type of body we are expecting to end, e.g.
                'conditional', 'loop', 'function' as a string.
            startLine       -> the line number where the body started, to give
                more context to the error message.

    DESCRIPTION

        When we are at the end of a body, we expect an 'END' token to close the
        body. This is to ensure the grammar is followed correctly and nothing is
        ambiguous. If we do not find an 'END' token when expected, we throw an
        error to inform the user of their mistake. We also give the line number
        where the body started to give more context to the error message.

    RETURNS

        No return value - function is void.

    */
    /**/
    private void ExpectEndOfBody(String a_bodyType, int startLine) {
        // If we are at the end of the body, we expect an 'END' token
        if (m_currentToken.GetOpcode() != OperationCode.END) {
            throw new Error("Expected '" + OperationCode.END.LABEL + "' after a " + a_bodyType,
                            ErrorType.SYNTAX_ERROR,
                            startLine);
        }
        Advance(OperationCode.END);
    }
    /* private void ExpectEndOfBody(String a_bodyType, int startLine); */

    /**/
    /*
    Parser.ForceNewline()

    NAME

        ForceNewline - throws an error if a newline is not found when expected.

    SYNOPSIS

        private void ForceNewline();

    DESCRIPTION

        After a statement, which isn't followed by an 'EOF' or 'END', we expect
        a newline to separate the statements. This is to ensure the grammar is
        followed correctly and nothing is ambiguous. If we do not find a newline
        when expected, we throw an error to inform the user of their mistake.

        For example, if we find a 'RETURN' statement, we expect a newline to
        separate it from the next statement. If we do not find a newline, we
        really don't know how to interpret the next statement. For example
        1 + 1 2 doesn't make sense, but 1 + 1; 2 does - they are two separate
        expressions.

    RETURNS

        No return value - function is void.

    */
    /**/
    private void ForceNewline() {
        // Newlines can be 'n' or ';' and they do not have a direct label. This ensures we are expecting a newline.
        if (!IsNewline()) {
            throw new Error("Expected a newline, but got '" + m_currentToken.GetOpcode().LABEL + "'",
                            ErrorType.SYNTAX_ERROR,
                            m_savedTokenState.GetLineNumber());
        }
        Advance(OperationCode.NEWLINE);
    }
    /* private void ForceNewline(); */

    // Ignore newlines when they are not significant in the grammar, i.e. skip all until we hit a non-newline token
    private void IgnoreNewlines() {
        while (IsNewline()) { Advance(OperationCode.NEWLINE); }
    }

    // Check if the current token is the end of the file (checked multiple times and more descriptive, so function it)
    private boolean IsEndOfFile() {
        return m_currentToken.GetOpcode() == OperationCode.EOF;
    }

    // See if the current token is a newline (made into a function as more descriptive, and we check multiple times)
    private boolean IsNewline() {
        return m_currentToken.GetOpcode() == OperationCode.NEWLINE;
    }

    // See if current token is a comma for separating arguments in function calls, lists, etc.
    private boolean IsSeparator() {
        return m_currentToken.GetOpcode() == OperationCode.COMMA;
    }


    /*
     *      Nesting - Keeping track of how deep we are in certain structures. Separated into functions to ensure
     *                readability, as having m_ifElseNest++ is not very clear, but MarkInConditional() is much more
     *                descriptive in code. Also ensures the members are being used properly. We need to use integers
     *                instead of booleans as if we are doubly nested, we will mark false when we are still in an outer
     *                structure. No method headers as they are self-explanatory functions.
     */

    // Marking when we are in a conditional (if-elseif-else)
    private void MarkInConditional(){
        m_ifElseNest++;
    }
    // Marking when we are in a loop (for, while)
    private void MarkInLoop() {
        m_loopNest++;
    }
    // Marking when we are in a function
    private void MarkInFunction() {
        m_functionNest++;
    }

    // Marking when we are out of a conditional
    private void MarkOutConditional() {
        if (m_ifElseNest > 0) { m_ifElseNest--; }
    }
    // Marking when we are out of a loop
    private void MarkOutLoop() {
        if (m_loopNest > 0) { m_loopNest--; }
    }
    // Marking when we are out of a function
    private void MarkOutFunction() {
        if (m_functionNest > 0) { m_functionNest--; }
    }

    // Checking if we are not in a conditional
    private boolean IsNotInConditional() {
        return m_ifElseNest <= 0;
    }
    // Checking if we are not in a loop
    private boolean IsNotInLoop() {
        return m_loopNest <= 0;
    }
    // Checking if we are not in a function
    private boolean IsNotInFunction() {
        return m_functionNest <= 0;
    }

}