///
/// Node Visitor Interface - Using the Visitor Design Pattern to communicate between the nodes and the interpreter. As
///     a node is only responsible for storing structured data for each operation type, and the interpreter is
///     responsible for evaluating the nodes, we need an interface to bind them together. Each node has an Accept
///     method that takes a visitor, and the visitor has a Visit method for each node type. This way, the interpreter
///     can call Accept on the root node, and the nodes will call the appropriate Visit method on the visitor. This
///     approach, known as double dispatch, allows the nodes to store data and the interpreter to evaluate the nodes in
///     a clean, loosely coupled, and not in a horrible polymorphic mess.
///

// The visitor design pattern was completely new to me, nad I've never heard of it before, a lot of research was needed.
// It's more of a functional programming style, which I'm not used to. Because of this I wanted to ensure I give proper
// credit to the sources (in code as well as in the docs) that helped me understand what it is and how to use them.
//
// Idea to use visitor pattern came from sources that describe implementing an interpreter:
//      https://craftinginterpreters.com/introduction.html
//      https://ruslanspivak.com/lsbasi-part1/
//      https://medium.com/codex/compound-pattern-series-visitor-and-interpreter-part-1-31dc22140bf0
// Help received for what visitor patterns are and how to implement in code:
//      https://www.youtube.com/watch?v=UQP5XqMqtqQ
//      https://www.geeksforgeeks.org/interfaces-in-java/
//      https://www.baeldung.com/java-visitor-pattern
//      https://stackoverflow.com/a/67821899
//      https://www.geeksforgeeks.org/visitor-design-pattern/
// For why I shouldn't be method overloading the inherited Node classes:
//      https://en.wikipedia.org/wiki/Separation_of_concerns
//      https://en.wikipedia.org/wiki/Interpreter_pattern
//      https://www.youtube.com/watch?v=hxGOiiR9ZKg

package AbstractSyntaxTree;

import AbstractSyntaxTree.Expressions.*;
import AbstractSyntaxTree.Expressions.Assignment;
import AbstractSyntaxTree.Statements.*;
import AbstractSyntaxTree.Statements.For;
import AbstractSyntaxTree.Statements.While;
import Interpreter.Value;


public interface NodeVisitor {
    /*
     *      Statements
     */

    Value VisitFunction(Function a_function);
    Value VisitIfElseifElse(IfElseifElse a_conditional);
    Value VisitWhile(While a_while);
    Value VisitFor(For a_for);
    Value VisitJump(Jump a_jump);
    Value VisitBody(Body a_body);


    /*
     *      Expressions
     */

    Value VisitAssignment(Assignment a_assignment);
    Value VisitBinaryOperator(BinaryOperator a_binary);
    Value VisitIndex(Index a_index);
    Value VisitUnaryOperator(UnaryOperator a_unary);
    Value VisitCall(Call a_call);
    Value VisitIdentifier(Identifier a_identifier);
    Value VisitLiteral(Literal a_literal);
    Value VisitGroup(Group a_group);
    Value VisitListExpression(ListExpression a_listExpression);

}