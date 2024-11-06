///
/// Literal Node - Represents a literal value (e.g. 1, true, "hello"). Is created from the parser when a literal is
///    encountered (integer, float, string, boolean or null).
///

package AbstractSyntaxTree.Expressions;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;


public class Literal extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Expressions.Literal.Literal()

    NAME

        Literal - constructor for the literal node.

    SYNOPSIS

        Literal(Value a_value);
            a_value    -> the value of the literal.

    DESCRIPTION

        Initializes the value of the literal. Specifically used for creating a
        literal node in the Abstract Syntax Tree. For example '1' would be a
        literal with the Value Object of 1.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Literal(Value a_value) {
        m_value = a_value;
    }
    /* Literal(Value a_value); */


    /** Accessors **/

    // Get the value of the literal
    public Value GetValue() {
        return m_value;
    }


    /** Methods **/

    // Accept a visitor and evaluate the literal (just returns the value)
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitLiteral(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Value m_value;    // The value of the literal (e.g. and integer, boolean, etc.)

}