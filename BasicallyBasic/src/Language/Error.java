///
/// Error - Used for error handling in the language. This is used to throw exceptions when an error occurs in the users
///     code, such as a syntax error, type error, runtime error, etc. This is used to inform the user of what went wrong
///     and where it went wrong.
///

package Language;

import Lexer.OperationCode;


public class Error extends RuntimeException {
    /* PUBLIC */

    /** Constants **/

    // Using an enum instead of polymorphism to avoid having classes with nothing in them. The only thing that changes
    //      is the name/type of error.
    public enum ErrorType {
        SYNTAX_ERROR            ("Syntax"),                 // Error in the syntax of the language
        TYPE_ERROR              ("Type"),                   // Data type is not correct
        RUNTIME_ERROR           ("Runtime"),                // Something went wrong during execution
        ARITHMETIC_ERROR        ("Arithmetic"),             // Arithmetic operation failed
        OUT_OF_BOUNDS_ERROR     ("Index Out of Bounds"),    // List/String was accessed out of bounds
        UNDEFINED_IDENTIFIER    ("Undefined Identifier"),   // Identifier was not found in the symbol table
        BUILT_IN_ERROR          ("Built-In Function");      // Error in a built-in function

        // Label for the error type - used as constructor to avoid unnecessary switch statements
        ErrorType(String a_label) { LABEL = a_label + " Error"; }
        private final String LABEL;     // The label for the error type when displaying to the user
    }


    /** Constructors **/

    /**/
    /*
    Language.Error.Error()

    NAME

        Error - constructor for the error class. Should be thrown.

    SYNOPSIS

        Error(String a_message, Error.ErrorType a_errorType, int a_lineNumber);
            a_message       -> the message to display to the user.
            a_errorType     -> the type of error that occurred.
            a_lineNumber    -> the line number the error occurred on.

    DESCRIPTION

        Initializes the error message, error type, and line number of the error.
        Specifically used for throwing exceptions when an error occurs in the
        user code. Based on what error it is, line number and error message, it
        generates an  error so the user can have some idea of what went wrong
        and where.

    RETURNS

        Nothing - it's a constructor.
    */
    /**/
    public Error(String a_message, ErrorType a_errorType, int a_lineNumber) {
        m_errorMessage = a_message;
        m_errorType = a_errorType;
        m_lineNumber = a_lineNumber;
        GenerateErrorMessage();
    }
    /* Error(String a_message, Error.ErrorType a_errorType,
             int a_lineNumber); */


    /** Accessors **/

    // Gets the error message to display to the user
    public String GetMessage() {
        return m_errorMessage;
    }

    // Gets the input text for error reporting
    public static String GetInput() {
        return m_text;
    }


    /** Methods **/

    // Initializes the input text for error reporting - static as we don't need to store multiple instances of the text
    public static void InitializeInput(String a_text) {
        m_text = a_text;
    }

    public static void SaveState(Error a_error) {

    }


    /* PRIVATE */

    /** Members **/

    private static String m_text;               // The text the user entered, static as we don't need multiple copies
    private String m_errorMessage;              // The error message to display to the user
    private final ErrorType m_errorType;        // The type of error that occurred (e.g. syntax, type, runtime, etc.)
    private int m_lineNumber;                   // The line number the error occurred on


    /** Private Helper Functions **/

    /**/
    /*
    Language.Error.GenerateErrorMessage()

    NAME

        GenerateErrorMessage - generates the error message to display to the
        user.

    SYNOPSIS

        private void GenerateErrorMessage();

    DESCRIPTION

        Generates the error message to display to the user. It splits the text
        into lines and then shows the error message, the line number and the
        line itself, to point where the error occurred. Message is split up with
        the '\n' and ';' characters to make it easier to read.

    RETURNS

        Nothing - it's a void function.

    */
    /**/
    private void GenerateErrorMessage() {
        // Help received: https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
        StringBuilder message = new StringBuilder();

        // Split the current text into lines
        String[] lines = m_text.split("\n|" + OperationCode.NEWLINE.SYMBOL);

        // In case the line number is out of bounds, just use last line. Will only occur if last characters in string
        //      are the new line ones (.split() removes trailing empty strings).
        if (m_lineNumber > lines.length) { m_lineNumber = lines.length; }

        // Show the error message
        message.append("\n")
                .append(m_errorType.LABEL)
                .append(" at line ")
                .append(m_lineNumber)
                .append(": ")
                .append(m_errorMessage)
                .append("!\n");

        // Show the line where the error occurred
        message.append("\n\t")
                .append(m_lineNumber)
                .append(" |\t")
                .append(lines[m_lineNumber - 1])
                .append(" \n\n");

        m_errorMessage = message.toString();
    }
    /* private void GenerateErrorMessage(); */

}