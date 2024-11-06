///
/// Binary Operator Node - Represents a binary operation in the Abstract Syntax Tree. Is created from
///     '<expression> <operator> <expression>' and is responsible for holding the left and right expressions as well as
///     the operator token.
///

package AbstractSyntaxTree.Expressions;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;
import Lexer.Token;


public class BinaryOperator extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Expressions.BinaryOperator.BinaryOperator()

    NAME

        BinaryOperator - constructor for the binary operator node.

    SYNOPSIS

        BinaryOperator(Node a_leftExpression, Token a_token, Node a_rightExpression);
            a_leftExpression    -> the left side of the binary operation.
            a_token             -> the operator token (e.g. '+', '-', '*', '/')
            a_rightExpression   -> the right side of the binary operation.

    DESCRIPTION

        Initializes the left expression, operator token, and right expression of
        the binary operation. Specifically used for creating a binary operator
        node in the Abstract Syntax Tree. For example '1 + 2' would be a binary
        operation with '1' as the left expression, '+' as the operator token,
        and '2' as the right expression.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public BinaryOperator(Node a_leftExpression, Token a_token, Node a_rightExpression) {
        m_leftExpression = a_leftExpression;
        m_token = a_token;
        m_rightExpression = a_rightExpression;
    }
    /* BinaryOperator(Node a_leftExpression, Token a_token,
                      Node a_rightExpression); */



    /** Accessors **/

    // Get the left expression of the binary operation
    public Node GetLeftExpression() {
        return m_leftExpression;
    }

    // Get the operator token of the binary operation
    public Token GetToken() {
        return m_token;
    }

    // Get the right expression of the binary operation
    public Node GetRightExpression() {
        return m_rightExpression;
    }


    /** Methods **/

    // Accept a visitor and have it visit the binary operator expression
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitBinaryOperator(this);
    }


    /* PRIVATE */

    /** Members **/
    private final Node m_leftExpression;    // The left side of the binary operation
    private final Token m_token;            // The operator token (e.g. '+', '-', '*', '/')
    private final Node m_rightExpression;   // The right side of the binary operation

}