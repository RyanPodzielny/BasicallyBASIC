///
/// Conditional Field - THIS IS NOT A NODE, rather a way to reuse code for the IfElseifElse and While loop. Both of
///     these nodes have a condition and a body, so this class is used to store that information. This class is not to
///     be evaluated by the interpreter, but rather act similar to a struct in C++ (Java doesn't have structs).
///

package AbstractSyntaxTree.Statements;

import AbstractSyntaxTree.Node;


public class ConditionalField {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.ConditionalField.ConditionalField()

    NAME

        ConditionalField - constructor for the conditional field.

    SYNOPSIS

        ConditionalField(Node a_condition, Body a_body, int a_lineNumber);
            a_condition     -> the condition for the statement (if or while).
            a_body          -> the body of the if statement.
            a_lineNumber    -> the line number the if statement was found on.

    DESCRIPTION

        Initializes the condition for the statement (if or while), the body of
        the if statement, and the line number the if statement was found on. It
        acts more a struct for the similarities found in condition like
        statements. For example 'IF x == 5 THEN; PRINT("HELLO")' would be a
        conditional field with 'x == 5' as the condition, 'PRINT("HELLO")' as
        the body, and the line number where the if statement was found.

        Helps reduce code duplication between the IfElseifElse and While nodes.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public ConditionalField(Node a_condition, Body a_body, int a_lineNumber) {
        m_condition = a_condition;
        m_body = a_body;
        m_lineNumber = a_lineNumber;
    }
    /* ConditionalField(Node a_condition, Body a_body, int a_lineNumber); */


    /** Accessors **/

    // Get the condition for the statement
    public Node GetCondition() {
        return m_condition;
    }

    // Gets the body of the conditional statement
    public Node GetBody() {
        return m_body;
    }

    // Gets the line number where the statement was found
    public int GetLineNumber() {
        return m_lineNumber;
    }


    /* PRIVATE */

    /** Members **/

    private final Node m_condition;               // The condition for the if statement
    private final Node m_body;                    // The body of the if statement
    private final int m_lineNumber;               // The line number the if statement was found on

}