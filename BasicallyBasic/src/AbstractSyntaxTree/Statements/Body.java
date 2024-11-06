///
/// Body Node - Represents a block of statements attached to a statement, e.g. the statements inside an if-else chain.
///     No exact representation for parser, but is created after a loop, if-elseif-else chain or a function.
///     Responsible for holding a list of statements.
///

package AbstractSyntaxTree.Statements;

import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;

import java.util.List;


public class Body extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.Body.Body()

    NAME

        Body - constructor for the body node.

    SYNOPSIS

        Body(List<Node> a_statements);
            a_statements    -> the list of statements in the body.

    DESCRIPTION

        Holds a list of statements to get executed. Specifically used for
        if-elseif-else chains, loops, and functions in the Abstract Syntax Tree.
        Important for grouping multiple statements together.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Body(List<Node> a_statements) {
        m_statements = a_statements;
    }
    /* Body(List<Node> a_statements); */


    /** Accessors **/

    // Get all the statements in the body
    public List<Node> GetStatements() {
        return m_statements;
    }


    /** Methods **/

    // Accept a visitor and visit/execute all the statements in the body
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitBody(this);
    }


    /* PRIVATE */

    /** Members **/

    private final List<Node> m_statements;    // The statements in the body

}