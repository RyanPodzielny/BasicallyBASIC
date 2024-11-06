///
/// OperationCode Enum - Used to represent the operation codes of the language, specifically for easier lexing and
///     parsing. This enum contains all the operators and operands that are used in the language.
///

package Lexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


// Language keywords and symbols were found from the QuickBASIC syntax from:
//      https://www.qbasic.net/en/reference/qb11/overview.htm
public enum OperationCode {
    // Format: OPCODE("Label", SymbolType)

    /* Arithmetic Operators */
    ADD                 ("+",   SymbolType.SIMPLE),
    SUBTRACT            ("-",   SymbolType.SIMPLE),

    MULTIPLY            ("*",   SymbolType.SIMPLE),
    DIVIDE              ("/",   SymbolType.SIMPLE),
    MODULO              ("%",   SymbolType.SIMPLE),

    POWER               ("^",   SymbolType.SIMPLE),

    LPARENTHESIS        ("(",   SymbolType.SIMPLE),
    RPARENTHESIS        (")",   SymbolType.SIMPLE),

    ASSIGNMENT          ("=",   SymbolType.SHARED),

    /* Logical, Equality and Comparison Operators */
    EQUALITY            ("==",  SymbolType.SHARED),
    INEQUALITY          ("!=",  SymbolType.COMPLEX),        // Complex since '!' is never used anywhere else
    LESS_THAN           ("<",   SymbolType.SHARED),
    LESS_THAN_EQUAL     ("<=",  SymbolType.SHARED),
    GREATER_THAN        (">",   SymbolType.SHARED),
    GREATER_THAN_EQUAL  (">=",  SymbolType.SHARED),

    /* Punctuation */
    NEWLINE             (";",   SymbolType.SIMPLE),         // End of line - can be both '\n' and ';'
    COMMENT             ("'",   SymbolType.COMPLEX),        // Comments, doubled ('') are used for multi-lines

    QUOTE               ("\"",  SymbolType.COMPLEX),        // String creation

    LBRACKET            ("[",   SymbolType.SIMPLE),         // List start
    RBRACKET            ("]",   SymbolType.SIMPLE),         // List end
    COMMA               (",",   SymbolType.SIMPLE),         // Element separation in lists/functions
    ACCESSOR            (":",   SymbolType.SIMPLE),         // List element access
    SUBSCRIPT           ("|",   SymbolType.SIMPLE),         // Subscript in element access, e.g. [1,2,3]:0|2

    /* Keywords */
    // Conditionals
    AND                 ("AND",     SymbolType.KEYWORD),
    OR                  ("OR",      SymbolType.KEYWORD),
    NOT                 ("NOT",     SymbolType.KEYWORD),

    // Variable management
    LET                 ("LET",     SymbolType.KEYWORD),

    // Control flow
    IF                  ("IF",      SymbolType.KEYWORD),
    ELSEIF              ("ELSEIF",  SymbolType.KEYWORD),
    ELSE                ("ELSE",    SymbolType.KEYWORD),

    // Loop management
    FOR                 ("FOR",     SymbolType.KEYWORD),
    TO                  ("TO",      SymbolType.KEYWORD),
    STEP                ("STEP",    SymbolType.KEYWORD),
    WHILE               ("WHILE",   SymbolType.KEYWORD),
    CONTINUE            ("CONTINUE",SymbolType.KEYWORD),
    BREAK               ("BREAK",   SymbolType.KEYWORD),

    THEN                ("THEN",    SymbolType.KEYWORD),    // Used to separate condition from block

    // Function management
    DEF                 ("DEF",     SymbolType.KEYWORD),    // Function definition
    RETURN              ("RETURN",  SymbolType.KEYWORD),

    END                 ("END",     SymbolType.KEYWORD),    // End of block

    /* Primitive Data Types */
    TRUE                ("TRUE",    SymbolType.KEYWORD),
    FALSE               ("FALSE",   SymbolType.KEYWORD),
    NULL                ("NULL",    SymbolType.KEYWORD),    // Null value - declared but not initialized
    LITERAL             ("LITERAL", SymbolType.NO_SYMBOL),  // Literal value, e.g. 1, "Hello", TRUE

    /* Management */
    IDENTIFIER          ("IDENTIFIER",  SymbolType.COMPLEX),
    EOF                 ("End of File", SymbolType.COMPLEX);


    /** Constants **/

    // Each opcode is lexed differently, some opcodes have special rules, others can be lexed easily. The symbol type is
    //      only used for lexing, however to keep information on literal value in one place it is stored here. Can parse
    //      each symbol to determine type, but is very complicated and messy - best to assign at creation.
    public enum SymbolType {
        SIMPLE,     // Symbols that are a one character, do not share with other opcode, and no special rules
        COMPLEX,    // More than one character and may have special rules, e.g. comments
        SHARED,     // Symbols that share characters with other opcodes, e.g. assignment and equals
        NO_SYMBOL,  // Has no exact representation in language
        KEYWORD     // Reserved words that need to be lexed as a whole
    }

    public final String LABEL;              // Literal representation in the language (needed for lexing)
    public final char SYMBOL;               // Character representation of the operator, if it has one
    public final SymbolType SYMBOL_TYPE;    // The type of symbol the opcode is

    // The keywords and simple symbols are stored to easily lex (reduces if-else chains). Complex and no symbol opcodes
    //      don't need a map as their lexing is not a simple comparison. Used as static constants as to not overload
    //      enum with methods (and makes it easier to access/initialize).
    // Help received: https://www.baeldung.com/java-initialize-hashset
    public static final Map<String, OperationCode> KEYWORDS;
    public static final Map<Character, OperationCode> SIMPLE_SYMBOLS;
    // Help received: https://stackoverflow.com/questions/7996499/creating-hashtable-as-final-in-java
    static {
        // I'm aware of code duplication, but no good way to avoid in this case due to different types within the maps.
        //      Generics can't be used as char and string due to different methods.

        // Need to temporary maps as they need to be final to not be reassigned.
        Map<Character, OperationCode> tempSimpleSymbols = new HashMap<>();
        Map<String, OperationCode> tempKeywords = new HashMap<>();

        // Go through each opcode and add it to their respective maps
        for (OperationCode opcode : OperationCode.values()) {
            if (opcode.SYMBOL_TYPE == SymbolType.KEYWORD) { tempKeywords.put(opcode.LABEL, opcode); }
            else if (opcode.SYMBOL_TYPE == SymbolType.SIMPLE) { tempSimpleSymbols.put(opcode.SYMBOL, opcode); }
        }

        // Make it so they can't be changed as java passes objects by reference
        SIMPLE_SYMBOLS = Collections.unmodifiableMap(tempSimpleSymbols);
        KEYWORDS = Collections.unmodifiableMap(tempKeywords);
    }


    /** Constructors **/

    /**/
    /*
    Lexer.OperationCode.OperationCode()

    NAME

        OperationCode - constructor for each enum value.

    SYNOPSIS

        OperationCode(String a_label, OperationCode.SymbolType a_symbolType);
            a_label      -> the literal representation of the opcode in the 
                language.
            a_symbolType -> the type of symbol the opcode is (simple, complex, 
                shared, no symbol, keyword).
                
    DESCRIPTION
    
        It initializes the label, symbol, and symbol type of the opcode. The
        label is the literal representation of the opcode in the language, if
        it has no symbol however, the label acts as a name to identify the
        operation. The symbol is the character representation of the operator,
        if it's too complex to have a symbol, it is set to a null character.
        The symbol type is the type of symbol the opcode is, which is used for
        lexing and putting the opcode in the correct category.
        
    RETURNS
    
        Nothing - it's a constructor.

    */
    /**/
    OperationCode(String a_label, SymbolType a_symbolType) {
        LABEL = a_label;

        // If the label is a single character, use it as the symbol - easier when lexing
        // Help received: IDE suggestion for ternary
        SYMBOL = (a_label.length() == 1) ? a_label.charAt(0) : '\0';
        SYMBOL_TYPE = a_symbolType;
    }
    /* OperationCode(String a_label, OperationCode.SymbolType a_symbolType) */

}