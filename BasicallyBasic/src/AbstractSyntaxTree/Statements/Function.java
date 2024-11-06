///
/// Function Node - Represents a function, both as within the Abstract Syntax Tree and as a Value. It is created from
///     'DEF <identifier>(<parameters>) <body>' in the parser. Responsible for holding the function name, parameters,
///     parameter count, whether it is a built-in function, the body of the function, and the builtin function to call.
///     It is pivotal to the language as it allows for user-defined functions and built-in functions, and allows code
///     to be reused and organized. Each function is expected to return a Value, and if it doesn't, it will return our
///     NULL Value.
///

package AbstractSyntaxTree.Statements;

import AbstractSyntaxTree.Expressions.Identifier;
import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;
import Language.BuiltInFunctions.BuiltIn;

import java.util.List;


public class Function extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.Function.Function()

    NAME

        Function - constructor for a user-defined function.

    SYNOPSIS

        Function(String a_functionName, List<Identifier> a_parameters,
                 Body a_body);
            a_functionName  -> the name of the function.
            a_parameters    -> the parameters (identifiers) of the function.
            a_body          -> the body of the function.

    DESCRIPTION

        Initializes the name of the function, the parameters of the function,
        and the body of the function.  For example 'DEF add(x, y) x + y' would
        be a function with 'add' as the name, 'x' and 'y' as the parameters, and
        'x + y' as the body of the function.

        This is for user definitions, so we need to store the names that the user
        has chosen for the parameters, as well as the body of the function the
        user created.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Function(String a_functionName, List<Identifier> a_parameters, Body a_body) {
        m_functionName = a_functionName;
        m_parameters = a_parameters;
        m_parameterCount = a_parameters.size();

        // User-defined functions don't have a built-in function to call
        m_isBuiltIn = false;
        m_body = a_body;
        m_function = null;
    }
    /* Function(String a_functionName, List<Identifier> a_parameters,
                Body a_body); */

    /**/
    /*
    AbstractSyntaxTree.Statements.Function.Function()

    NAME

        Function - constructor for a built-in function.

    SYNOPSIS

        Function(String a_functionName, int a_parameterCount,
                 BuiltIn a_function);
            a_functionName  -> the name of the function.
            a_parameterCount-> the number of parameters the function takes.
            a_function      -> the built-in function to call. Is an interface
                from the BuiltInFunctions class that uses Java code for its
                implementation.

    DESCRIPTION

        Initializes the name of the function, the number of parameters the
        function takes, and the built-in function to call. For example PRINT()
        is a function with 1 parameter, and calls BuiltInFunctions.PRINT() to
        execute.

        This is for built-in functions, so we don't need to store the names of
        the parameters or the body of the function, just the number of parameters
        and the built-in function to call so set the body and parameters to null
        and set the isBuiltIn flag to true.

    */
    /**/
    public Function(String a_functionName, int a_parameterCount, BuiltIn a_function) {
        m_functionName = a_functionName;
        m_parameterCount = a_parameterCount;
        m_function = a_function;

        // Built-in functions don't have parameter names or a body
        m_isBuiltIn = true;
        m_body = null;
        m_parameters = null;
    }
    /* Function(String a_functionName, int a_parameterCount,
                BuiltIn a_function); */


    /** Accessors **/

    // Returns the name of the function
    public String GetFunctionName() {
        return m_functionName;
    }

    // Gets the parameters of the function if it is a user-defined function
    public List<Identifier> GetParameters() {
        ConfirmUserDefined();
        return m_parameters;
    }

    // Gets the number of parameters the function takes (or arity)
    public int GetParameterCount() {
        return m_parameterCount;
    }

    // Returns whether the function is a built-in function or not
    public boolean IsBuiltIn() {
        return m_isBuiltIn;
    }

    // Gets the built-in function to call if is indeed a built-in function
    public BuiltIn GetBuiltInFunction() {
        ConfirmBuiltIn();
        return m_function;
    }

    // Gets the body of the function if it is a user-defined function
    public Body GetBody() {
        ConfirmUserDefined();
        return m_body;
    }


    /** Methods **/

    // Accept a visitor and evaluate the function, will need to check the flag to see if it is a built-in function
    //      before running
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitFunction(this);
    }


    /* PRIVATE */

    /** Members **/

    private final String m_functionName;            // The name of the function
    private final List<Identifier> m_parameters;    // The parameters of the function (all identifiers)
    private final int m_parameterCount;             // Number of parameters the function takes
    private final boolean m_isBuiltIn;              // Whether the function is a built-in function
    private final Body m_body;                      // The body of the function
    private final BuiltIn m_function;               // The built-in function (its 'body')


    /** Private Helper Functions **/

    /*
     *      Correct Function Value - So developers don't do something they shouldn't
     */

    // Used if the function is user-defined and doesn't have a built-in function to access
    public void ConfirmUserDefined() {
        assert !m_isBuiltIn : "Function '" + m_functionName + "'" +
                              "is a built-in function, no parameters names or body to access.";
    }

    // Used if the function is built-in and doesn't have parameters or a body to access
    public void ConfirmBuiltIn() {
        assert m_isBuiltIn : "Function '" + m_functionName + "'" +
                             "is a user-defined function, no built-in function to access.";
    }

}