///
/// Unary Operator Node - Represents a unary operation, e.g. -1 or NOT TRUE in the Abstract Syntax Tree. Is created by
///     '<operator> <expression>' and is responsible for holding the expression and the operator in token form.
///

package AbstractSyntaxTree.Expressions;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;
import Lexer.Token;


public class UnaryOperator extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Expressions.UnaryOperator.UnaryOperator()

    NAME

        UnaryOperator - constructor for the unary operator node.

    SYNOPSIS

        UnaryOperator(Token a_token, Node a_expression);
            a_token         -> the operator token (e.g. '-', 'NOT').
            a_expression    -> the expression the operator is applied to.

    DESCRIPTION

        Initializes the operator token and the expression the operator is
        applied to. Used for creating a unary operator node in the Abstract
        Syntax Tree. For example '-1' would be a unary operation with '-' as the
        operator token and '1' as the expression the operator is applied to.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public UnaryOperator(Token a_token, Node a_expression) {
        m_token = a_token;
        m_expression = a_expression;
    }
    /* UnaryOperator(Token a_token, Node a_expression); */


    /** Accessors **/

    // Get the operator token
    public Token GetToken() {
        return m_token;
    }

    // Get the expression the operator is applied to
    public Node GetExpression() {
        return m_expression;
    }


    /** Methods **/

    // Accept a visitor and evaluate the unary operation
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitUnaryOperator(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Token m_token;        // The operator token (e.g. '-', '!')
    private final Node m_expression;    // The expression the operator is applied to

}