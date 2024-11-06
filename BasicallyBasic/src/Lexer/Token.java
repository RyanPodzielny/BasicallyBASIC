///
/// Token - Represents a token in the input file, storing the operation code to inform parser of what to do with it, the
///     value associated with it when found, and line number it was found on for error reporting. Acts more as a struct
///     rather than a class, as it only contains data and state changing methods. Without it, we would have to pass
///     around multiple values in a list or array, which would be less readable and harder to manage.
///

package Lexer;

import Interpreter.Value;


public final class Token {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    Lexer.Token.Token()
    
    NAME
    
        Token - constructor for the token class.
        
    SYNOPSIS
    
        Token(OperationCode a_opcode, Value a_value, int a_lineNumber);
            a_opcode        -> the operation code of the token.
            a_value         -> the value associated with the token.
            a_lineNumber    -> the line number the token was found on.
            
    DESCRIPTION
            
        Initializes the operation code, value, and line number of the token.
        Specifically used for operation codes that have a specific value 
        associated with them, e.g. numbers, identifiers, etc.
        
    RETURNS
    
        Nothing - it's a constructor.
    */
    /**/
    public Token(OperationCode a_opcode, Value a_value, int a_lineNumber) {
        m_opcode = a_opcode;
        m_value = a_value;
        m_lineNumber = a_lineNumber;
    }
    /* Token(OperationCode a_opcode, Value, int a_lineNumber) */

    /**/
    /*
    Lexer.Token.Token()
    
    NAME
    
        Token - constructor for the token class.
        
    SYNOPSIS
    
        Token(OperationCode a_opcode, int a_lineNumber);
            a_opcode        -> the operation code of the token.
            a_lineNumber    -> the line number the token was found on.
            
    DESCRIPTION
    
        Initializes the operation code, value, and line number of the token.
        Specifically used for operation codes that don't have a specific value
        associated with them, e.g. ADD, SUB, MUL, DIV, etc. Will just pass the
        label of the operation code as the value.
        
    RETURNS
        
        Nothing - it's a constructor.
        
    */
    /**/
    public Token(OperationCode a_opcode, int a_lineNumber) {
        m_opcode = a_opcode;
        // No value will exist for certain operation codes, so just pass their label as value
        m_value = new Value(a_opcode.LABEL);
        m_lineNumber = a_lineNumber;
    }
    /* Token(OperationCode a_opcode, int a_lineNumber) */


    /** Accessors **/

    // Gets the operation code of the token
    public OperationCode GetOpcode() {
        return m_opcode;
    }

    // Gets the value associated with the token
    public Value GetValue() {
        return m_value;
    }

    // Gets the line number the token was found on for error reporting
    public int GetLineNumber() {
        return m_lineNumber;
    }


    /* PRIVATE */

    /** Members **/

    private final OperationCode m_opcode;   // The operation code of the token (e.g. ADD, SUB, MUL, DIV, etc.)
    private final Value m_value;            // The value of the token - will be string if token is operator
    private final int m_lineNumber;         // The line number the token was found on

}