///
/// Node - An abstract class for the nodes in the Abstract Syntax Tree. It mainly serves as a structure for data. It
///     seems odd in OOP to have a class with only one method to override (similar to all the nodes that inherit from
///     it), but it is necessary for the visitor design pattern. Tree classes are 'owned' by any single thing, so they
///     merely need to exist just to store data. Each node should not be responsible for both storing data and
///     evaluating itself, the evaluation part is up the interpreter. This why the visitor design pattern is used, to
///     be able to store data in the nodes and put the evaluation in the interpreter with communication through double
///     dispatch. Seems odd to have roughly twenty classes with only data and no methods, but OOP doesn't really fit
///     well with trees, simply put.
///

package AbstractSyntaxTree;

import Interpreter.Value;

// Help received: https://www.geeksforgeeks.org/abstract-classes-in-java/ for abstract classes in Java
abstract public class Node {

    // This is a part of the visitor design pattern (see NodeVisitor.java)
    public abstract Value Accept(NodeVisitor a_visitor);

}