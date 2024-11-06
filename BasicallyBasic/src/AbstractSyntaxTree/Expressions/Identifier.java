///
/// Identifier Node - Represents a variable/function. Is created from the parser when a variable is encountered, e.g.
///    'x' or 'y'. Responsible for holding the name of the variable/function and the line number where it is accessed.
///

package AbstractSyntaxTree.Expressions;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;


public class Identifier extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Expressions.Identifier.Identifier()

    NAME

        Identifier - constructor for the identifier node.

    SYNOPSIS

        Identifier(String a_variableName, int a_lineNumber);
            a_variableName  -> the name of the variable.
            a_lineNumber    -> the line number the variable was accessed on.

    DESCRIPTION

        Initializes the name of the variable and the line number where the
        variable was accessed. Specifically used for creating an identifier node
        in the  Abstract Syntax Tree. For example 'x' would be an identifier.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Identifier(String a_variableName, int a_lineNumber) {
        m_variableName = a_variableName;
        m_lineNumber = a_lineNumber;
    }
    /* Identifier(String a_variableName, int a_lineNumber); */


    /** Accessors **/

    // Get the name of the variable
    public String GetVariableName() {
        return m_variableName;
    }

    // Get the line number where the variable was used
    public int GetLineNumber() {
        return m_lineNumber;
    }

    /** Methods **/

    // Accept a visitor and visit the identifier node
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitIdentifier(this);
    }


    /* PRIVATE */

    /** Members **/

    private final String m_variableName;    // Name of the variable/identifier
    private final int m_lineNumber;         // The line number where the variable is accessed

}