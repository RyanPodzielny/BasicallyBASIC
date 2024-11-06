///
/// SymbolTable - The structure that stores all the identifiers and their values. It is a tree, with each node
///     representing a scope. The root node is the global scope, and each child node is a local scope. It is traversed
///     from local till upwards to find the identifier, and if not found, it is undefined. Any time we put an
///     identifier in the table, it is put in the most local scope possible. Uses a hashmap for quick access to the
///     values.
///

package Interpreter;

import Language.Error;
import Language.Error.ErrorType;

import java.util.HashMap;


public class SymbolTable {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    Interpreter.SymbolTable.SymbolTable()

    NAME

        SymbolTable - constructor for the global scope.

    SYNOPSIS

        SymbolTable();

    DESCRIPTION

        Initializes the global scope of the symbol table. The global scope is
        the root of the tree, and has no outer scope.

    RETURNS

        Nothing - it's a constructor.
    */
    /**/
    public SymbolTable() {
        m_localScope = new HashMap<>();
        // No outer scope for global, so can't set it to anything
        m_outerScope = null;
    }
    /* SymbolTable() */


    /**/
    /*
    Interpreter.SymbolTable.SymbolTable()

    NAME

        SymbolTable - constructor for a local scope.

    SYNOPSIS

        SymbolTable(SymbolTable a_outerScope);
            a_outerScope    -> the immediate outer scope to the current scope.

    DESCRIPTION

        Initializes a local scope of the symbol table. The local scope is a
        child of the outer scope, and has no children of its own (currently).
        It is used to store identifiers and their values in the most local scope
        possible.

    RETURNS

        Nothing - it's a constructor.
    */
    /**/
    public SymbolTable(SymbolTable a_outerScope) {
        m_localScope = new HashMap<>();
        m_outerScope = a_outerScope;
    }
    /* SymbolTable(SymbolTable a_outerScope) */


    /** Accessors **/

    /**/
    /*
    Interpreter.SymbolTable.GetValue()

    NAME

        GetValue - gets the value of the identifier.

    SYNOPSIS

        Value GetValue(String a_identifier, int a_lineNumber);
            a_identifier    -> the identifier to get the value of.
            a_lineNumber    -> the line number the identifier was found on.

    DESCRIPTION

        Gets the value of the identifier in the symbol table. It first checks
        the local scope, and if it's not there, it recursively checks the outer
        scope until it hits the global scope. If it's not found anywhere, it's
        undefined, so we throw an error to inform the user.

    RETURNS

        The Value of the identifier if it is defined.

    */
    /**/
    public Value GetValue(String a_identifier, int a_lineNumber) {
        // Return closest scope first
        if (InLocalScope(a_identifier)) { return m_localScope.get(a_identifier); }

        // Recursively backup to the outer scope to see if there, until we hit global
        if (!IsOuterGlobalScope()) { return m_outerScope.GetValue(a_identifier, a_lineNumber); }

        // Couldn't find it anywhere within static tree, so it's undefined (easier to put here than in Interpreter)
        throw new Error("Undefined identifier '" + a_identifier + "'",
                        ErrorType.UNDEFINED_IDENTIFIER,
                        a_lineNumber);
    }
    /* public Value GetValue(String a_identifier, int a_lineNumber) */


    /** Mutators **/

    // Puts the identifier in the most local scope possible
    public void PutIdentifier(String a_identifier, Value a_value) {
        m_localScope.put(a_identifier, a_value);
    }


    /* PRIVATE */

    /** Members **/

    private final HashMap<String, Value> m_localScope;  // Current scope we are at (i.e. local scope)
    private final SymbolTable m_outerScope;             // The enclosing scope of the current scope (null if global)



    /** Private Helper Functions **/

    // Returns true if the identifier is in the current scope
    private boolean InLocalScope(String a_identifier) {
        return m_localScope.containsKey(a_identifier);
    }

    // Returns true if the current scope is the global scope, i.e. no more enclosing scopes
    private boolean IsOuterGlobalScope() {
        return m_outerScope == null;
    }

}