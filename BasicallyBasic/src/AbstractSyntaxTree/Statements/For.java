///
/// For Loop Node - Represents a for loop statement. Is created from the parser when a for loop statement is
///     found 'FOR <assignment> TO <endValue> STEP <increment> THEN <body> END'. Responsible for holding the assignment
///     variable for the start, the end value to get to, the increment/step value, and the body of the loop.
///     Responsible for executing a block of code a certain number of times.
///

package AbstractSyntaxTree.Statements;

import AbstractSyntaxTree.Expressions.Assignment;
import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
    import AbstractSyntaxTree.Statements.Body;
    import Interpreter.Value;


public class For extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.For.For()

    NAME

        For - constructor for the for loop node.

    SYNOPSIS

        For(Assignment a_assignment, Node a_endValue, Node a_increment,
            Body a_body, int a_lineNumber);
            a_assignment    -> the assignment for the loop, e.g. 'LET i = 0'.
            a_endValue      -> the end value to get to, e.g. '10'.
            a_increment     -> what to increment the variable by, e.g. '1'.
            a_body          -> loop body.
            a_lineNumber    -> the line number the for loop was found on.

    DESCRIPTION

        Initializes the assignment, end value, increment, and body of the for
        loop. Specifically used for creating a for loop node in the Abstract
        Syntax Tree. For example 'FOR LET i = 0 TO 10 STEP 1 THEN ... END' would
        be a for loop with the assignment 'LET i = 0', end value '10',
        increment '1', and the body of the loop.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public For (Assignment a_assignment, Node a_endValue, Node a_increment, Body a_body, int a_lineNumber) {
        m_assignment = a_assignment;
        m_endValue = a_endValue;
        m_increment = a_increment;
        m_body = a_body;
        m_lineNumber = a_lineNumber;
    }
    /* For(Assignment a_assignment, Node a_endValue, Node a_increment,
           Body a_body, int a_lineNumber); */


    /** Accessors **/

    // Get the assignment for the for loop (e.g. 'LET i = 0')
    public Assignment GetAssignment() {
        return m_assignment;
    }

    // Get the end value to get to (e.g. '10')
    public Node GetEndValue() {
        return m_endValue;
    }

    // Get what to increment the variable by (e.g. '1')
    public Node GetIncrement() {
        return m_increment;
    }

    // Get the body of the loop
    public Body GetBody() {
        return m_body;
    }

    // Get the line number the for loop was found on
    public int GetLineNumber() {
        return m_lineNumber;
    }


    /** Methods **/

    // Accept a visitor and evaluate the for loop
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitFor(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Assignment m_assignment;      // The assignment for the for loop, e.g. 'LET i = 0'
    private final Node m_endValue;              // The end value to get to, i.e. '10'
    private final Node m_increment;             // What to increment the variable by, e.g. '1'
    private final Body m_body;                  // Loop body to execute
    private final int m_lineNumber;             // The line number the for loop was found on

}