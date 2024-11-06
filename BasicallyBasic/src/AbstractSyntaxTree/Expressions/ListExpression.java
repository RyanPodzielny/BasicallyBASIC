///
/// List Expression Node - Represents a list of expressions, e.g. [1 + 2, "hello", (3 * 4)], meaning it is the
///     unevaluated list before transformation to a list Value. Is created from '[<expression>, <expression>, ...]'
///     in the parser.
///

package AbstractSyntaxTree.Expressions;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;

import java.util.Collections;
import java.util.List;


public class ListExpression extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Expressions.ListExpression.ListExpression()

    NAME

        ListExpression - constructor for the list expression node.

    SYNOPSIS

        ListExpression(List<Node> a_expression);
            a_expression    -> the list of expressions inside the list.

    DESCRIPTION

        Initializes the list of expressions inside the list. It serves as a way
        to store unevaluated lists, For example , '[1 + 2, "hello", (3 * 4)]'
        would be a list expression with '1 + 2', '"hello"', and '3 * 4' as the
        expressions inside the list, and would eventually be evaluated to a
        list Value of [3, "hello", 12].

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public ListExpression(List<Node> a_expression) {
        // Make sure the list can't be added to or modified later on (for encapsulation)
        m_expressions = Collections.unmodifiableList(a_expression);
    }
    /* ListExpression(List<Node> a_expression); */


    /** Accessors **/

    // Get the list of expressions inside the list
    public List<Node> GetExpressions() {
        return m_expressions;
    }


    /** Methods **/

    // Accept a visitor and evaluate all the expressions in the list
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitListExpression(this);
    }


    /* PRIVATE */

    /** Members **/

    private final List<Node> m_expressions;   // The list of expressions inside the list, e.g. [1 + 2, "hello", (3 * 4)]

}