///
/// Jump Node - Represents a jump out of a specific body. Responsible for holding the type of jump (break, continue,
///     return) and allowing another node to catch it and deal with it where it needs to. It uses exception control
///     flow to break out of the current body and go back to where the jump was used. If it is a return, it will also
///     give the value to catch block.
///

package AbstractSyntaxTree.Statements;

import AbstractSyntaxTree.Expressions.Literal;
import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;
import Lexer.OperationCode;
import Lexer.Token;


public class Jump extends Node {
    /* PUBLIC */

    // Custom exceptions for breaking, continuing, and returning. As we nest deep into the interpreter with recursive
    //      calls, we need a way to break out of the current body and go back to where the break, and exceptions do
    //      that perfectly. Having a simple try catch at a point like inside a loop, allows us to avoid having to pass
    //      a boolean up the chain to break out of the loop. Java does all the work for us.
    // Since these are simple classes that only hold one value, there is no need for them to have their own file and
    //      can be nested inside the Jump class for better organization. No method headers are needed, as they simply
    //      hold the line number it was used on or hold the value to return. Breaks are just line numbers, while returns
    //      contain the value to return.
    public static class BreakException extends RuntimeException {
        public BreakException(int a_lineNumber) { m_lineNumber = a_lineNumber; }
        public int GetLineNumber() { return m_lineNumber; }
        private final int m_lineNumber;
    }
    public static class ContinueException extends RuntimeException {
        public ContinueException(int a_lineNumber) { m_lineNumber = a_lineNumber; }
        public int GetLineNumber() { return m_lineNumber; }
        private final int m_lineNumber;
    }
    public static class ReturnException extends RuntimeException {
        public ReturnException(Value a_value) { m_value = a_value; }
        public Value GetValue() { return m_value; }
        private final Value m_value;
    }


    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.Jump.Jump()

    NAME

        Jump - constructor a jump without an expression

    SYNOPSIS

        Jump(Token a_jumpType);
            a_jumpType    -> the type of jump.

    DESCRIPTION

        Initializes the type of jump and the expression to our NULL Value.
        This constructor is used when there is no expression required for the
        jump, like an empty return, a break or continue.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Jump(Token a_jumpType) {
        m_jumpType = a_jumpType;
        // Default to a literal with no value if no expression is provided
        m_expression = new Literal(Value.NULL);
    }
    /* Jump(Token a_jumpType); */

    /**/
    /*
    AbstractSyntaxTree.Statements.Jump.Jump()

    NAME

        Jump - constructor for a jump with an expression. For return.

    SYNOPSIS

        Jump(Token a_jumpType, Node a_expression);
            a_jumpType    -> the type of jump.
            a_expression  -> the expression to return.

    DESCRIPTION

        Initializes the type of jump and the expression to return. This
        constructor is used when there is an expression required for the jump,
        like a return.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Jump(Token a_jumpType, Node a_expression) {
        m_jumpType = a_jumpType;
        m_expression = a_expression;
    }
    /* Jump(Token a_jumpType, Node a_expression); */


    /** Accessors **/

    // Get the type of jump (break, continue, return)
    public OperationCode GetJumpType() {
        return m_jumpType.GetOpcode();
    }

    // Get the line number the jump occurs on
    public int GetLineNumber() {
        return m_jumpType.GetLineNumber();
    }

    // Get the expression to return if it is return, otherwise it'll be our NULL wrapped in a Literal node
    public Node GetExpression() {
        return m_expression;
    }


    /** Methods **/

    // Accept a visitor and throw the appropriate exception
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitJump(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Token m_jumpType;     // The type of jump (break, continue, return)
    private final Node m_expression;    // The expression to return (if return) or our NULL if no expression

}