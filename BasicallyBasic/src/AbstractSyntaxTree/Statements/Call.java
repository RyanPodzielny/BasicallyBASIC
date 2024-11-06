///
/// Call Node - Represents the call of a function in the Abstract Syntax Tree. Is created by '<function>(<arguments>)'
///     and is responsible for holding the function to be called, the arguments being passed to the function, and the
///     line the call occurs on. When evaluated it will create a new scope for the function to exist in and initialize
///     the arguments passed to the function based on the Functions parameter names.
///

package AbstractSyntaxTree.Statements;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;

import java.util.List;


public class Call extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.Call.Call()

    NAME

        Call - constructor for the call node.

    SYNOPSIS

        Call(Node a_callee, List<Node> a_arguments, int a_lineNumber);
            a_callee        -> the function to be called.
            a_arguments     -> the arguments being passed to the function.
            a_lineNumber    -> the line number where the function is called.

    DESCRIPTION

        Initializes the function to be called, the arguments being passed to the
        function, and the line number where the function is called. Specifically
        used for creating a call node in the Abstract Syntax Tree. For example
        'print("hello")' would be a call with 'print' as the function to be
        called and '"hello"' as the argument being passed to the function.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Call(Node a_callee, List<Node> a_arguments, int a_lineNumber) {
        m_callee = a_callee;
        m_arguments = a_arguments;
        m_lineNumber = a_lineNumber;
    }
    /* Call(Node a_callee, List<Node> a_arguments, int a_lineNumber); */


    /** Accessors **/

    // Returns the function to be called
    public Node GetCallee() {
        return m_callee;
    }

    // Returns the arguments being passed to the function
    public List<Node> GetArguments() {
        return m_arguments;
    }

    // Returns the line number where the function is called
    public int GetLineNumber() {
        return m_lineNumber;
    }


    /** Methods **/

    // Accept a visitor and evaluate the call
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitCall(this);
    }


    /* PRIVATE */

    /** Members **/

    private final Node m_callee;                // The function to be called (must be identifier when evaluated)
    private final List<Node> m_arguments;       // The arguments being passed to the function
    private final int m_lineNumber;             // The line number where the function is called

}