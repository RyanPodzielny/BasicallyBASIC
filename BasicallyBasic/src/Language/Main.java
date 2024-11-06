///
/// Main - The entry point for the program. It initializes the global scope and interpreter for the language and
///     registers all built-in functions. Responsible for driving the program from the console (CLI) or from a file.
///

package Language;

import static Lexer.OperationCode.*;

import AbstractSyntaxTree.Node;
import Interpreter.Interpreter;
import Interpreter.Value;
import Interpreter.SymbolTable;
import Parser.Parser;
import Lexer.Lexer;
import Lexer.Token;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


public class Main {
    /* PUBLIC */

    /** Constants **/

    // Easily changeable constants for the language
    public static final String FILE_EXTENSION = ".bb";      // File extension for our language
    public static final String CONSOLE_PROMPT = ">>> ";     // Input prompt for the console
    public static final String EXIT_PROMPT = "EXIT";        // Exit prompt so user can quit out


    /** Main **/

    /**/
    /*
    Language.Main.Main.Main()

    NAME

        Main - entry point for the program.

    SYNOPSIS

        public static void main(String[] args);
            args    -> the arguments passed in from the command line.

    DESCRIPTION

        The main function is the entry point for the program. It initializes the
        global scope and interpreter for the language and registers the built-in
        functions. It then checks the arguments passed in from the command line
        and runs the program accordingly; no arguments runs the program from the
        console (CLI), one argument runs the program from a file, and any other
        number means improper usage.

    RETURNS

        Nothing - it's the main function.

    */
    /**/
    public static void main(String[] args) {
        // Initialize the global scope and interpreter
        SymbolTable globalScope = new SymbolTable();
        m_interpreter = new Interpreter(globalScope);

        // Register built-in functions (done at construction)
        BuiltInFunctions.InitializeFunctions(globalScope);

        switch (args.length) {
            // 0 arguments means run from the console
            case 0: RunFromConsole(); return;
            // 1 argument means run from a file, try to run it and catch an IO error if can't read
            case 1:
                try { RunFromFile(args[0]); }
                catch (IOException e) { System.out.println("An error occurred reading the file!"); }
                return;

            // Any other number of arguments is improper usage
            default: System.out.println("Arguments: 0 for console, 1 for file");
        }
    }
    /* public static void main(String[] args); */


    /* PRIVATE */

    /** Members **/

    private static Interpreter m_interpreter;   // Interpreter for evaluating


    /** Private Helper Functions **/

    /**/
    /*
    Language.Main.RunFromConsole()

    NAME

        RunFromConsole - runs the program from the console (CLI).

    SYNOPSIS

        public static void RunFromConsole();

    DESCRIPTION

        The function runs the program from the console, otherwise known as a
        CLI or REPL. It scans each line of input from the user and runs it
        through the interpreter. If the user enters the exit prompt, or CTRL+D
        the program will exit. It outputs the result of the expression if there
        is one, i.e. the Value is not our NULL.

    RETURNS

        Nothing - it's a void function.

    */
    /**/
    public static void RunFromConsole() {
        Scanner scanner = new Scanner(System.in);
        String input;

        // Scan each line until the user exits
        while (true) {
            System.out.print(CONSOLE_PROMPT);

            // If user enters CTRL+D, we need to break out of the loop
            if (!scanner.hasNextLine()) { break; }
            input = scanner.nextLine();

            // If user can use key bind to exit any console, but an exit prompt is also nice
            if (input == null || input.equals(EXIT_PROMPT)) { break; }

            // If we resulted in a value, i.e. from an expression, show the user it as it's a CLI
            Value result = Run(input);
            // Only print if there is a non-null (both Java and our NULL) result
            if (result != null && !result.IsNull()) { System.out.println(result.FormatValueToString()); }
        }
    }
    /* public static void RunFromConsole(); */

    /**/
    /*
    Language.Main.RunFromFile()

    NAME

        RunFromFile - runs the program from a file.

    SYNOPSIS

        public static void RunFromFile(String a_filePath);
            a_filePath    -> the string of the path to the file to run.

    DESCRIPTION

        The function runs the program from a file. It checks if the file has the
        correct extension and if it exists. If it does, it reads the entire file
        into a string and runs it through the interpreter. It does not care
        about the value returned as it's a file and not the CLI, so it outputs
        only the print statements done by the user.

    RETURNS

        Nothing - it's a void function.

    */
    /**/
    public static void RunFromFile(String a_filePath) throws IOException {
        // Check if right extension
        if (!a_filePath.endsWith(FILE_EXTENSION)) {
            System.out.println("Invalid file extension. Must be a " + FILE_EXTENSION + " file!");
            return;
        }

        // Check if file exists - can't run if it doesn't
        // Help received: https://stackoverflow.com/questions/50948361/adding-sub-directory-to-an-existing-path
        Path path = Path.of(System.getProperty("user.dir") + "/" + a_filePath);
        if (!Files.exists(path)) {
            System.out.println("File '" + a_filePath + "' not found!");
            return;
        }

        // Read entire file into a string and run it - we don't care about the value return as it's a file not the CLI
        // Help Received: https://www.geeksforgeeks.org/java-program-to-read-a-file-to-string/
        try { Run(Files.readString(path).trim()); }
        catch (IOException e) { System.out.println("An error occurred reading the file!"); }
    }
    /* public static void RunFromFile(String a_filePath); */


    /** Private Helper Functions **/

    /**/
    /*
    Language.Main.Run()

    NAME

        Run - runs the program from a string.

    SYNOPSIS

        private static Value Run(String a_input);
            a_input    -> the string of the program to run.

    DESCRIPTION

        The function runs the program from a string, only executing the program
        if there is content in the string and is not just whitespace. It lexes
        the input into tokens, parses the tokens into an abstract syntax tree,
        and evaluates the tree (or program). It will also initialize our error
        class to our input for displaying our errors.

        If at any point we generate a language error, it prints it out
        immediately. If an unexpected error occurs, it means there is a bug and
        the developer should be notified.

    RETURNS

        Value - the result of the program, or Value.NULL if there was an error.

    */
    /**/
    // Want to print out the stack trace as we don't have a proper logger
    @SuppressWarnings("CallToPrintStackTrace")
    private static Value Run(String a_input) {
        // Don't bother parsing if there is nothing there
        if (a_input.isBlank()) { return Value.NULL; }

        // Error gets a static copy of the input to display the error message, instead of having it in each class
        Error.InitializeInput(a_input);

        try {
            // Lex the input into tokens
            Lexer lexer = new Lexer(a_input);
            List<Token> tokens = lexer.GenerateAllTokens();

            // Comment or whitespace, don't bother parsing
            if (tokens.isEmpty() || tokens.get(0).GetOpcode() == EOF) { return Value.NULL; }

            // Parse tokens into an abstract syntax tree
            Parser parser = new Parser(tokens);
            List<Node> program = parser.ParseProgram();

            // Evaluate the tree (or program)
            return m_interpreter.Interpret(program);
        }
        // If at any point we generate a language error, print it out immediately
        catch (Error inBuiltError) { System.out.print(inBuiltError.GetMessage()); }
        // If unexpected error occurs (a bug), print it out for developer to see
        catch (Exception javaError) {
            System.out.println("An unexpected error occurred!");
            javaError.printStackTrace();
        }

        // In case we don't have a value to return (we errored out)
        return Value.NULL;
    }
    /* private static Value Run(String a_input); */

}