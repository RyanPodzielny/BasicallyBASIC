///
/// Assignment Node - Represents and holds data for an assignment operation in the Abstract Syntax Tree. Is created by
///     'LET <identifier> = <expression>' or 'LET <identifier>' in the parser. Responsible for holding the variable name
///      and the expression being assigned to the variable.
///

package AbstractSyntaxTree.Expressions;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;
import Lexer.Token;


public class Assignment extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Expressions.Assignment.Assignment()

    NAME

        Assignment - constructor for the assignment node.

    SYNOPSIS

        Assignment(Token a_variable, Node a_expression);
            a_variable      -> the name of the variable being assigned to.
            a_expression    -> the expression being assigned to the variable.

    DESCRIPTION

        Initializes the variable name and expression being assigned to the
        variable. Specifically used for creating an assignment node in the
        Abstract Syntax Tree. For example 'LET x = 1' would be an assignment
        with 'x' as the variable name and '1' as the expression being assigned.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Assignment(Token a_variable, Node a_expression) {
        m_variable = a_variable;
        m_expression = a_expression;
    }
    /* Assignment(Token a_variable, Node a_expression); */


    /** Accessors **/

    // Get the name of the variable being assigned to
    public String GetVariableName() {
        return m_variable.GetValue().AsString();
    }

    // Get the expression being assigned to the variable
    public Node GetExpression() {
        return m_expression;
    }


    /** Methods **/

    // Accept a visitor and have it visit the assignment expression
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitAssignment(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Token m_variable;     // The name of the variable being assigned to
    private final Node m_expression;    // The expression (soon to be value) being assigned to the variable

}