///
/// Group Node - Represents a group of expressions (e.g. (1 + 2)) (or a parenthesized expression). Is created from
///    '(<expression>)' in the parser. Responsible for holding the expression inside the group for precedence.
///

package AbstractSyntaxTree.Expressions;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;


public class Group extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Expressions.Group.Group()

    NAME

        Group - constructor for the group node.

    SYNOPSIS

        Group(Node a_expression);
            a_expression    -> the expression inside the group.

    DESCRIPTION

        Initializes the expression inside the group. Specifically used for
        creating a group node in the Abstract Syntax Tree. For example '(1 + 2)'
        would be a group with '1 + 2' as the expression inside the group.
    */
    /**/
    public Group(Node a_expression) {
        m_expression = a_expression;
    }
    /* Group(Node a_expression); */


    /** Accessors **/

    // Get the expression inside the group
    public Node GetExpression() {
        return m_expression;
    }


    /** Methods **/

    // Accept a visitor and return the value it evaluates to
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitGroup(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Node m_expression;    // The expression inside the group (e.g. (1 + 2) -> 1 + 2)

}