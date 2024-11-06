///
/// While Loop Node - Represents a while loop. Is created from the parser when a while loop is encountered,
///    'WHILE <condition> THEN <body> END'. Responsible for holding the condition to check for, and the body of the
///     loop. Evaluates the condition and executes the body of the loop while the condition is true within Interpreter.
///

package AbstractSyntaxTree.Statements;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;


public class While extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.While.While()

    NAME

        While - constructor for the while loop node.

    SYNOPSIS

        While(ConditionalField a_field);
            a_field    -> the field that stores the condition, body and line
                number of the while loop.

    DESCRIPTION

        Initializes the condition to check for and the body of the loop. For
        example 'WHILE x < 5 THEN BREAK END' would be a while loop with the
        condition 'x < 5' and 'BREAK' as the body.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public While(ConditionalField a_field) {
        m_condition = a_field.GetCondition();
        m_body = a_field.GetBody();
        m_lineNumber = a_field.GetLineNumber();
    }
    /* While(ConditionalField a_field); */


    /** Accessors **/

    // Get the condition to check for (e.g. x < 5)
    public Node GetCondition() {
        return m_condition;
    }

    // Get the body of the loop
    public Node GetBody() {
        return m_body;
    }

    // Get the line number where WHILE was found
    public int GetLineNumber() {
        return m_lineNumber;
    }


    /** Methods **/

    // Accept a visitor and attempt to evaluate the while loop
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitWhile(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Node m_condition;     // The condition to check for
    private final Node m_body;          // The body of the loop
    private final int m_lineNumber;     // The line number the loop started on

}