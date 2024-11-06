///
/// Lexer - Breaks apart the input string into Tokens that are manageable for the parser, i.e. performs lexical
///     analysis. It is the first step in the compilation process, and is responsible for identifying the basic
///     building blocks of the language.
///

package Lexer;

import static Lexer.OperationCode.*;
import static Language.Error.*;

import Language.Error;
import Interpreter.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Lexer {
    /* PUBLIC */

    /** Constants **/

    // Escape characters that can reside in strings (defined here so we can add or remove easily)
    // Help received: https://stackoverflow.com/questions/6802483/how-to-directly-initialize-a-hashmap-in-a-literal-way
    public static final Map<Character, Character> VALID_ESCAPES = Map.of(
            'n', '\n',
            't', '\t',
            '\\', '\\',
            '\"', '\"'
    );
    // Floating point number separator (different in some parts of world, best to make it a constant)
    public static final char DECIMAL = '.';


    /** Constructors **/

    /**/
    /*/
    Lexer.Lexer.Lexer()

    NAME

        Lexer - constructor for the Lexer class.

    SYNOPSIS

        Lexer(String a_input);
            a_input     -> The input string (program) to be parsed by the 

    DESCRIPTION

        Initializes the Lexer with the input string to be parsed. It will keep
        track of the line number, the current character being parsed, and the
        index of the character in the input string.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Lexer(String a_input) {
        m_input = a_input;

        // Line numbers are not 0 indexed
        m_lineNumber = 1;

        m_textIndex = 0;
        m_currentChar = m_input.charAt(m_textIndex);
    }
    /* Lexer(String a_input); */


    /** Methods **/

    /**/
    /*
    Lexer.Lexer.GenerateAllTokens()

    NAME

        GenerateAllTokens - Generates all Tokens from the input string.

    SYNOPSIS

        public List<Token> GenerateAllTokens();

    DESCRIPTION

        Breaks apart all the input string into their respective Tokens that are 
        manageable for the parser. It will add each Token to a list until it 
        reaches the end of input string, marked by the OperationCode EOF. If at 
        any point it cannot parse a character, it will throw a syntax error for 
        the user to be informed.
        
    RETURNS
        
        Returns a list of Tokens that were generated from the input string. 
        Storing the operation code, value associated with it, and the line 
        number it was found on for error reporting.

     */
    /**/
    public List<Token> GenerateAllTokens() {
        List<Token> Tokens = new ArrayList<>();

        // Add each Token until we are done with the input
        while (true) {
            Token Token = GenerateNextToken();
            Tokens.add(Token);

            // Break out once done
            if (Token.GetOpcode() == EOF) { break; }
        }

        return Tokens;
    }
    /* public List<Token> GenerateAllTokens(); */

    /* PRIVATE */

    /** Members **/

    private final String m_input;    // String to be parsed
    private int m_lineNumber;        // Line number of the current Token
    private int m_textIndex;         // Position of the current character in input string
    private char m_currentChar;      // Current character being parsed


    /** Private Helper Functions **/

    /**/
    /*
    Lexer.Lexer.GenerateNextToken()
    
    NAME

        GenerateNextToken - Generates the next Token from input string.
        
    SYNOPSIS
    
        private Token GenerateNextToken();
        
    DESCRIPTION
    
        Generates the next Token from the input string, and assign it their
        respective operation code, value associated with it, and the line number
        based on the character it is currently on. It will skip whitespace and
        comments, and if it cannot parse a character, it will throw a syntax
        error for the user to be informed. If the end of the input is reached, 
        it will end, marking it with the OperationCode EOF. 
        
        Parsing is done by the label of the operation code, and the character
        we are currently on. For example, if a character is a letter, it will
        be an identifier or key, if it is a digit, it will be a number, etc.
        Based on the complexity of the operation code, it will call a helper
        function to generate the Token.
        
    RETURNS
    
        The Token associated with the current character in the input string.
        
    */
    /**/
    private Token GenerateNextToken() {
        // If there is whitespace (besides newlines), skip it - we don't care about it
        RemoveWhitespace();
        // Or if we have a comment, we don't want to parse it
        // Comment - skip all characters after symbol (SYMBOL: ' for line, '' for multi-line)
        RemoveComment();

        // If we are at the end of the input, then we are done
        if (IsEndOfInput()) { return new Token(EOF, m_lineNumber); }

        /* Multiple character Tokens */
        // Identifier - see if keyword, if not its variable (in regex: [a-zA-Z_][a-zA-Z0-9_]*)
        if (IsValidStartIdentifier(m_currentChar)) { return MakeIdentifier(); }
        // Number - check there are digits seperated by 1 decimal (in regex: ?[0-9]+(\.[0-9]+)?)
        if (IsPotentialDigit(m_currentChar)) { return MakeNumber(); }
        // String - anything in between quotes (SYMBOL: ", in regex: \".*\")
        if (m_currentChar == QUOTE.SYMBOL) { return MakeString(); }
        // Not equal - two character symbol (SYMBOL: !=)
        if (IsNotEqualSign(m_currentChar)) { return MakeSimpleSymbol(INEQUALITY); }
        // Newline - user inputted newline break (SYMBOL: \n and ;)
        if (IsNewline(m_currentChar)) { return MakeSimpleSymbol(NEWLINE); }

        /* Operation codes that share symbols, e.g. = and == */
        // Assignment: =    Equality: ==
        if (m_currentChar == ASSIGNMENT.SYMBOL) { return MakeSharedSymbol(ASSIGNMENT, EQUALITY); }
        // Less than: <    Less than or equal: <=
        if (m_currentChar == LESS_THAN.SYMBOL) { return MakeSharedSymbol(LESS_THAN, LESS_THAN_EQUAL); }
        // Greater than: >    Greater than or equal: >=
        if (m_currentChar == GREATER_THAN.SYMBOL) { return MakeSharedSymbol(GREATER_THAN, GREATER_THAN_EQUAL); }
        /* Single character Tokens that do not share characters, i.e. simple symbols */
        if (SIMPLE_SYMBOLS.containsKey(m_currentChar)) { return MakeSimpleSymbol(SIMPLE_SYMBOLS.get(m_currentChar)); }

        // Could not parse character - so we can't do anything with it
        throw new Error("Invalid character, could not parse: '" + m_currentChar + "'",
                        ErrorType.SYNTAX_ERROR,
                        m_lineNumber);
    }
    /* private Token GenerateNextToken(); */

    
    /*
     *      Making characters into Tokens
     */

    /**/
    /*
    Lexer.Lexer.MakeIdentifier()
    
    NAME

        MakeIdentifier - Generates a Token for an identifier or keyword.
        
    SYNOPSIS
    
        private Token MakeIdentifier();
        
    DESCRIPTION
    
        Generates a Token for an identifier or keyword based on the current
        character in the input string. It will keep track of the characters
        that are letters, underscores, or digits, and if it is a keyword 
        (which can be found in the KEYWORD map), it will assign the 
        operation code to that  keyword. If it is TRUE, FALSE,
        or NULL, it will assign the Java representable value to the Token.
        
    RETURNS
    
        The Token associated with the identifier or keyword in the input string.

    */
    /**/
    private Token MakeIdentifier() {
        // Stopping execution here as it is a problem with the call, not the lexer
        assert IsValidStartIdentifier(m_currentChar) : "Character must start with a letter or underscore";

        StringBuilder identifier = new StringBuilder();

        // The next characters are either letters, underscores or digits, but not whitespace
        do {
            identifier.append(m_currentChar);
            Advance();
        // Identifier can be letters, underscores, or digits (just can't start with digits)
        } while (!IsEndOfInput() && (IsValidStartIdentifier(m_currentChar) || Character.isDigit(m_currentChar)));

        // If the string is in the set of keywords, then it can't be an identifier
        // Help received: https://www.w3schools.com/java/ref_hashmap_getordefault.asp
        OperationCode opcode = KEYWORDS.getOrDefault(identifier.toString(), IDENTIFIER);

        // Check if TRUE, FALSE, or NULL as we need to represent as a Java value
        if (IsBooleanOpcode(opcode)) { return MakeBoolean(opcode); }
        if (opcode == OperationCode.NULL) { return MakeNull(opcode); }

        return new Token(opcode, new Value(identifier.toString()), m_lineNumber);
    }
    /* private Token MakeIdentifier(); */

    /**/
    /*
    Lexer.Lexer.MakeBoolean()
    
    NAME

        MakeBoolean - Generates a Token for TRUE or FALSE with the Java 
        representable value.
        
    SYNOPSIS
    
        private Token MakeBoolean(OperationCode a_opcode);
            a_opcode    -> A 'TRUE' or 'FALSE' operation code which we can use
                to determine the value to assign to the Token
   
    DESCRIPTION
    
        Generates a Token for TRUE or FALSE based on the operation code passed 
        in. Assigning the Java representable value to our own Value.
     
    RETURNS
        
        The Token and Value associated with the TRUE or FALSE operation code.

    */
    /**/
    private Token MakeBoolean(OperationCode a_opcode) {
        // Ensure it is TRUE or FALSE
        assert IsBooleanOpcode(a_opcode) : "Opcode must be TRUE or FALSE";

        // Return with the Java representable of a boolean
        return new Token(a_opcode, new Value(a_opcode == TRUE), m_lineNumber);
    }
    /* private Token MakeBoolean(OperationCode a_opcode); */

    /**/
    /*
    Lexer.Lexer.MakeNull()
    
    NAME

        MakeNull - Generates a Token for NULL with the Java representable 
        value.
        
    SYNOPSIS
        
        private Token MakeNull(OperationCode a_opcode);
            a_opcode    -> A 'NULL' operation code which we can use to determine 
                the value to assign to the Token
                           
    DESCRIPTION
    
        Generates a Token for NULL based on the operation code passed in. 
        Assigning the Java representable value to our own Value.
        
    RETURNS
    
        The Token with the Value.NULL associated with the NULL operation code.
        
    */
    /**/
    private Token MakeNull(OperationCode a_opcode) {
        // Ensure it is NULL
        assert (a_opcode == OperationCode.NULL) : "Opcode must be NULL";

        // Make it our NULL value
        return new Token(OperationCode.NULL, Value.NULL, m_lineNumber);
    }
    /* private Token MakeNull(OperationCode a_opcode); */

    /**/
    /*
    Lexer.Lexer.MakeNumber()

    NAME

        MakeNumber - Generates a Token for a number with the Java
        representable value (BigDecimal and BigInteger).

    SYNOPSIS

        private Token MakeNumber();

    DESCRIPTION

        Generates a Token for a number based on the current character in the
        input string. While we have a digit or decimal point, we will keep
        appending to the number. We count the decimal points to ensure it is
        when we generate the value, we know if it is a float or integer. If it
        has one decimal point, it is a float (we use Java's BigDecimal to
        represent it), otherwise it is an integer(using BigInteger to represent
        it). Using these classes as we don't want to overflow in our language.

        If the number is not a digit or decimal point, it will throw a syntax
        error for the user to be informed. If the number is a decimal point
        without any digits, it will also throw a syntax error.

    RETURNS

        The Token, with a Value that is either an INTEGER or FLOAT.

    */
    /**/
    private Token MakeNumber() {
        // Can't have a number if not a digit or decimal
        assert (IsPotentialDigit(m_currentChar)) : "Character must be a digit or decimal point";

        // Keep track if number is a float or integer
        int decimalCount = 0;

        // Create a string for our presumably multi-digit number
        StringBuilder builder = new StringBuilder();

        // While we have digits, append to result and continue with input
        while (!IsEndOfInput() && IsPotentialDigit(m_currentChar)) {
            if (m_currentChar == DECIMAL) { decimalCount++; }

            builder.append(m_currentChar);
            Advance();
        }
        String number = builder.toString();

        // Guard clauses before casting - ensures proper number syntax
        EnsureValidNumber(m_currentChar, number, decimalCount);

        // If we have a decimal point, then we have a float, otherwise it's an integer. Using BigDecimal and BigInteger
        //      as we don't overflow in our language.
        // Help received: IDE suggestion for ternary
        Value value = (decimalCount == 1) ? new Value(new BigDecimal(number)) : new Value(new BigInteger(number));
        return new Token(LITERAL, value, m_lineNumber);
    }
    /* private Token MakeNumber(); */

    /**/
    /*
    Lexer.Lexer.MakeString()

    NAME

        MakeString - Generates a Token with a value of string within quotes.

    SYNOPSIS

        private Token MakeString();

    DESCRIPTION

        If we get a character that is the quote symbol, we will generate loop
        through each character until we hit a quote symbol again. Within the
        inner contents of the two quotes, we will append to a string builder
        and keep track of escape characters (using the map VALID_ESCAPES).

        If our string does not have a closing quote, it will throw a syntax
        error for the user to be informed. It also throws a syntax error if
        there is an invalid escape character.

    RETURNS

        The Token with a Value of the string within the quotes, e.g. "Hello".

    */
    /**/
    private Token MakeString() {
        // Can't create a string Token if we don't have a quote - improper call
        assert (m_currentChar == QUOTE.SYMBOL) : "Character must be a quote";

        // Go past first quote
        Advance();

        // Parse the actual values within the string, saving the start for better error reporting
        int startLine = m_lineNumber;
        String innerContents = ProcessStringContents();

        // If last character is not a quote, then it's an error
        if (m_currentChar != QUOTE.SYMBOL) {
            throw new Error("String not closed",
                            ErrorType.SYNTAX_ERROR,
                            startLine);
        }

        // Go past last quote
        Advance();

        return new Token(LITERAL, new Value(innerContents), m_lineNumber);
    }
    /* private Token MakeString(); */

    /**/
    /*
    Lexer.Lexer.MakeSharedSymbol()

    NAME

        MakeSharedSymbol - Generates a Token for a shared symbol (a symbol
        that shares characters with another symbol).

    SYNOPSIS

        private Token MakeSharedSymbol(OperationCode a_firstOpcode,
                                       OperationCode a_secondOpcode);
            a_firstOpcode   -> The first operation code that shares the symbol,
                which should only be one character long and must be the first
                character of the second opcode.
            a_secondOpcode  -> The second operation code that shares the symbol
                with the first opcode, which should be two characters long where
                its first character should be the same as the first opcode.

    DESCRIPTION

        Generates a Token for a shared symbol based on the current character in
        the input string. If the second character is present, then we have the
        second opcode, otherwise we have the first opcode. It will advance past
        the symbol/label, however long it may be. For example, if the symbol is
        '=', then it will be either '=' or '=='. If it is '==', then it will
        advance past the second '='.

    RETURNS

        The Token associated with the symbol in the input string, e.g. = or ==.

    */
    /**/
    private Token MakeSharedSymbol(OperationCode a_firstOpcode, OperationCode a_secondOpcode) {
        /* GUARD CLAUSES */
        // We only have symbols that share first character, where one opcode is a single char and the other is two.
        //      No need in checking for longer symbol lengths in current implementation
        assert (a_firstOpcode.LABEL.length() == 1 && a_secondOpcode.LABEL.length() == 2) : "Incorrect opcode lengths";
        // The first opcode symbol, must be the first character - if not it's a problem with the call
        assert (a_firstOpcode.SYMBOL == a_secondOpcode.LABEL.charAt(0)) : "Opcode label must share first character";
        // First characters must also be the same
        assert (m_currentChar == a_firstOpcode.SYMBOL) : "Character must be the first character of the opcode";

        // Assume the first opcode is the final opcode
        OperationCode finalOpcode = a_firstOpcode;

        // If the second character is present, then we have the second opcode
        if (LookAhead() == a_secondOpcode.LABEL.charAt(1)) {
            finalOpcode = a_secondOpcode;
            Advance();
        }

        Advance();

        return new Token(finalOpcode, m_lineNumber);
    }
    /* private Token MakeSharedSymbol(OperationCode a_firstOpcode,
                                      OperationCode a_secondOpcode); */

    /**/
    /*
    Lexer.Lexer.MakeSimpleSymbol()

    NAME

        MakeSimpleSymbol - Generates a Token for a simple symbol (a symbol
        that does not share characters with another symbol).

    SYNOPSIS

        private Token MakeSimpleSymbol(OperationCode a_opcode);
            a_opcode    -> The operation code that is a simple symbol, which
                does not share characters with another symbol.

    DESCRIPTION

        Generates a Token for a simple symbol based on the current character in
        the input string. It will advance past the symbol/label, however long it
        may be. For example, if the symbol is '+', then it will advance past it,
        or '!='.

    RETURNS

        The Token associated with the symbol in the input string, e.g. '+'.

    */
    /**/
    private Token MakeSimpleSymbol(OperationCode a_opcode) {
        // Advance past the symbol/label, however long it may be
        for (int index = 0; index < a_opcode.LABEL.length(); index++) { Advance(); }

        return new Token(a_opcode, m_lineNumber);
    }
    /* private Token MakeSimpleSymbol(OperationCode a_opcode); */


    /*
     *      Helping parse the input
     */

    /**/
    /*
    Lexer.Lexer.RemoveComment()

    NAME

        RemoveComment - Removes comments from the input string.

    SYNOPSIS

        void RemoveComment();

    DESCRIPTION

        If we are on a comment symbol, we will remove the comment from the input
        string. If it is a multi-line comment, we will remove everything until
        we reach the end of the comment. If it is a single line comment, we will
        remove everything until we reach the end of the line (which is denoted
        by both a newline character and a semicolon).

        If the multi-line comment is not closed, it will throw a syntax error
        for the user to be informed.

    RETURNS

        Nothing - just avoids the comment in the input string.
    */
    /**/
    private void RemoveComment() {
        // Not a comment, move on
        if (m_currentChar != COMMENT.SYMBOL) { return; }

        // Multi-line comment (''), only need to check LookAhead() once as we know the first character is a comment
        if (LookAhead() == COMMENT.SYMBOL) {
            // Save the line number for better error reporting
            int startLine = m_lineNumber;

            // Skip the first two characters
            Advance(); Advance();

            // Advance till we reach the end of the comment or file
            while (!IsEndOfInput()) {
                if (m_currentChar == COMMENT.SYMBOL && LookAhead() == COMMENT.SYMBOL) { break; }
                Advance();
            }

            // If not closed, should inform used - would be annoying if nothing ran, and they didn't know why
            if (IsEndOfInput()) {
                throw new Error("Multi-line comment not closed",
                                ErrorType.SYNTAX_ERROR,
                                startLine);
            }

            // Skip the last two characters
            Advance(); Advance();

            // Remove any whitespace or comments after the multi-line comment (in case we have chained multi-lines
            //     comments we need to ensure we remove all of them, hence the recursive call).
            RemoveWhitespace();
            RemoveComment();
        }
        // Single line comment (')
        else { while (!IsEndOfInput() && !IsNewline(m_currentChar)) { Advance(); } }
    }
    /* private void RemoveComment(); */

    /**/
    /*
    Lexer.Lexer.RemoveWhitespace()

    NAME

        RemoveWhitespace - Removes/avoids whitespace from the input
        string.

    SYNOPSIS

        private void RemoveWhitespace();

    DESCRIPTION

        If we are on a whitespace character, that is not a newline we will skip
        it. We need to keep newlines as they are used to separate statements in
        the parser.

    RETURNS

        Nothing - just avoids the whitespace in the input string.

    */
    /**/
    private void RemoveWhitespace() {
        // Continue passed all whitespace besides newlines as they are needed for statement separation in parser
        while (!IsEndOfInput() && (Character.isWhitespace(m_currentChar) && !IsNewline(m_currentChar))) {
            Advance();
        }
    }
    /* private void RemoveWhitespace(); */

    /**/
    /*
    Lexer.Lexer.ProcessStringContents()

    NAME

        ProcessStringContents - Processes the contents withing the quotes
        of a string.

    SYNOPSIS

        private String ProcessStringContents();

    DESCRIPTION

        Builds out the raw characters within the quotes of a string. It will
        keep track of escape characters, and if it is a valid escape character
        (found in the VALID_ESCAPES map), it will append the actual character
        to the string. If it is not a valid escape character, it will throw a
        syntax error for the user to be informed.

        It will continue until it reaches the end of the input string or the
        end of the quote.

    RETURNS

        A string of raw characters within the quotes of the input text.

    */
    /**/
    private String ProcessStringContents() {
        // Seperated from MakeString() as logic was getting too complex to keep in one function
        StringBuilder contents = new StringBuilder();

        // Keep track of if we are on an escape character (meaning previous character is '\')
        boolean isEscape = false;
        // While we have characters and we haven't reached the end quote
        while (!IsEndOfInput() && (m_currentChar != QUOTE.SYMBOL || isEscape)) {
            // If we are on an escape character, and it exists in our valid characters add it to the string
            if (isEscape) {
                EnsureValidEscape(m_currentChar);
                contents.append(VALID_ESCAPES.get(m_currentChar));
                isEscape = false;
            }
            // We are at an escape character, so for next iteration our character is an escape
            else if (m_currentChar == '\\') { isEscape = true; }
            // Otherwise its normal, so add the character to the string
            else { contents.append(m_currentChar); }

            Advance();
        }

        return contents.toString();
    }
    /* private String ProcessStringContents(); */

    /**/
    /*
    Lexer.Lexer.EnsureValidEscape()

    NAME

        EnsureValidEscape - Ensures the escape character is valid.

    SYNOPSIS

        private void EnsureValidEscape(char a_char);
            a_char  -> An escape character that needs to against our valid
                escape characters in the language.

    DESCRIPTION

        Ensures the escape character is valid, and if it is not, it will throw
        a syntax error for the user to be informed.

    RETURNS

        Nothing - just ensures the escape character is valid.

    */
    /**/
    private void EnsureValidEscape(char a_char) {
        // If the escape character is not valid, then it's an error (in another function for readability)
        if (!VALID_ESCAPES.containsKey(a_char)) {
            throw new Error("Invalid escape character",
                    ErrorType.SYNTAX_ERROR,
                    m_lineNumber);
        }
    }
    /* private void EnsureValidEscape(char a_char); */

    /**/
    /*
    Lexer.Lexer.EnsureValidNumber()

    NAME

        EnsureValidNumber - Ensures the number is valid.

    SYNOPSIS

        void EnsureValidNumber(char a_char, String a_numberToBe,
                                     int a_decimalCount);
            a_char          -> The current character that is being parsed.
            a_numberToBe    -> The number as a string that is being parsed.
            a_decimalCount  -> The count of decimal points in the number.

    DESCRIPTION

        Ensures the number is valid, and if it is not, it will throw a syntax
        error for the user to be informed. It will check if the number is a
        decimal point without any digits, if the number is followed by a letter,
        or if the number has more than one decimal point. Need to ensure the
        number is valid before we cast it to a Java representable value.

    RETURNS

        Nothing - just ensures the number is valid.

    */
    /**/
    private void EnsureValidNumber(char a_char, String a_numberToBe, int a_decimalCount) {
        // Only time a decimal point can exist is if it is number (no classes in our language)
        if (a_numberToBe.equals(Character.toString(DECIMAL))) {
            throw new Error("Decimal point cannot exist without digits",
                    ErrorType.SYNTAX_ERROR,
                    m_lineNumber);
        }
        // Numbers cannot be followed by a letter - similar to most languages
        if (IsValidStartIdentifier(a_char)) {
            throw new Error("Digit cannot be followed by a letter",
                    ErrorType.SYNTAX_ERROR,
                    m_lineNumber);
        }
        // A number that is .23.2 is not an actual number
        if (a_decimalCount > 1) {
            throw new Error("Number cannot have more than one decimal point",
                    ErrorType.SYNTAX_ERROR,
                    m_lineNumber);
        }
    }
    /* private void EnsureValidNumber(char a_char, String a_numberToBe,
                                      int a_decimalCount); */


    /*
     *      Moving through the input
     */

    /**/
    /*
    Lexer.Lexer.Advance()

    NAME

        Advance - Advances to the next character in the input string.

    SYNOPSIS

        private void Advance();

    DESCRIPTION

        Advances our m_currentChar the next character in the input string. If
        the character is a newline ('\n' or ';'), it will increment the line
        number. If we are at the end of the input, it will not advance to avoid
        errors.

    RETURNS

        Nothing - just advances to the next character in the input string.

    */
    /**/
    private void Advance() {
        // Newline means new line number, otherwise just move to next character
        if (IsNewline(m_currentChar)) { m_lineNumber++; }
        m_textIndex++;

        // Ran out of input, so we are done
        if (IsEndOfInput()) { return; }

        m_currentChar = m_input.charAt(m_textIndex);
    }
    /* private void Advance(); */

    /**/
    /*
    Lexer.Lexer.LookAhead()

    NAME

        LookAhead - Peeks at the next character in the input string.

    SYNOPSIS

        char LookAhead();

    DESCRIPTION

        Peeks at the next character in the input string for multi-character
        symbols. If the next character is out of bounds, it will return the null
        character to avoid errors.

    RETURNS

        The next character in the input string, or the null character if it is
        out of bounds.
        
    */
    /**/
    private char LookAhead() {
        // Peek at the next character in the input string
        int peekPosition = m_textIndex + 1;

        // Next char is out of bounds return null character to avoid errors
        if (peekPosition > m_input.length() - 1) { return '\0'; }

        return m_input.charAt(peekPosition);
    }
    /* private char LookAhead(); */


    /*
     *      Character Checking - Seeing what type of character we are on. Ease of use functions that make the code more
     *      readable and easier to understand. Short enough to not need function headers.
     */

    // Check to see if we are at the end of the input string
    private boolean IsEndOfInput() {
        return m_textIndex == m_input.length();
    }

    // See if the character is a newline (';' or '\n') - need ';' for CLI
    private boolean IsNewline(char a_char) {
        return a_char == NEWLINE.SYMBOL || a_char == '\n';
    }

    // Check if character is alphabetic or an underscore (for snake case), and not a digit
    private boolean IsValidStartIdentifier(char a_char) {
        return Character.isAlphabetic(a_char) || a_char == '_';
    }

    // Check if character is '!=' and only supports not equal as a two character symbol, report it if longer
    private boolean IsNotEqualSign(char a_char) {
        assert INEQUALITY.LABEL.length() == 2 : "Not equal sign must be two characters";
        return (a_char == INEQUALITY.LABEL.charAt(0)) && (LookAhead() == INEQUALITY.LABEL.charAt(1));
    }

    // See if character is a digit - Character.isDigit() does a lot more than just ASCII digits, so need to make our own
    private boolean IsPotentialDigit(char a_char) {
        return (a_char >= '0' && a_char <= '9') || a_char == DECIMAL;
    }

    // Check if the opcode is TRUE or FALSE
    private boolean IsBooleanOpcode(OperationCode a_opcode) {
        return a_opcode == TRUE  || a_opcode == FALSE;
    }

}