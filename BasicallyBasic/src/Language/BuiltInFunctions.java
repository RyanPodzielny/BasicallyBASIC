///
/// Built-In Functions - Functions that are built into the language that provide utility that is not accessible through
///     the languages' syntax. For example PRINT() and PRINTLN() are built-in functions that print to the console, but
///     if they were build into the language it would mean each one would need it own node and syntax to call it. This
///     way it can be generalized and called like any other function. This class is a collection of built-in functions
///     and are initialized within the global scope of the program. All built-ins must return a Value and take a list of
///     arguments.
///

package Language;

import static Language.Main.RunFromFile;

import AbstractSyntaxTree.Statements.Function;
import Interpreter.SymbolTable;
import Interpreter.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;


public class BuiltInFunctions {
    /* PUBLIC */

    // This functional interface can define a Java function that takes a list of arguments and returns a Value. It,
    //      allows us to pass a Java function into a parameter so it can be resolved in the SymbolTable. Though each
    //      built-in has a specific number of arguments, and the list makes it less clear, it makes it easier to call
    //      and resolve the built-in. Done to allow for flexibility in how many arguments a function can take, so if we
    //      wanted to add a function with 20 arguments we don't need to create a new interface to handle it. It does
    //      make it harder to know how many arguments a function takes in their definition, but the flexibility is worth
    //      the tradeoff. Regardless, arity is checked beforehand they can't be called with wrong number of arguments.
    // Help received: https://www.geeksforgeeks.org/functional-interfaces-java/
    public interface BuiltIn {
        Value Execute(List<Value> arguments);
    }

    // Similar to the Jump node, we have a custom exception for built-ins that allow us to notify the place where the
    //      function was called that something went wrong. This is useful as it allows us to break out of the current
    //      recursive call to get the line to be more descriptive in the error message. This is a simple class that only
    //      holds a message, so it doesn't need its own file and can be nested inside the BuiltInFunctions.
    public static class BuiltInFunctionException extends RuntimeException {
        public BuiltInFunctionException(String a_message) { m_message = a_message; }
        public String GetMessage() { return m_message; }
        public final String m_message;
    }


    /** Public Utility Methods **/

    /**/
    /*
    Language.BuiltInFunctions.InitializeFunctions()

    NAME

        InitializeFunctions - initializes all the built-in functions for the
            language, putting them in the global scope.

    SYNOPSIS

        public static void InitializeFunctions(SymbolTable a_globalScope);
            a_globalScope -> the global scope of the program.

    DESCRIPTION

        Initializes all the built-in functions for the language, putting them in
        the global scope. This is done so that they can be accessed by the
        interpreter when a function is called. Each function must be registered
        here for the interpreter to know about it. And each function created
        must specify the parameter count and the Java function it uses to
        execute.

        For example the PRINT() function is registered here with a parameter
        count of 1 and the Java function BuiltInFunctions::PRINT() that takes a
        list of arguments (but can only use the first one) and prints it to the
        console.

    RETURNS

        Nothing - it's a void function.

    */
    /**/
    public static void InitializeFunctions(SymbolTable a_globalScope) {
        m_globalScope = a_globalScope;

        // Register all the built-in functions: Usage is RegisterFunction(name, parameter count, function)
        RegisterFunction("PRINT",       1,  BuiltInFunctions::PRINT);
        RegisterFunction("PRINTLN",     1,  BuiltInFunctions::PRINTLN);
        RegisterFunction("INPUT",       0,  BuiltInFunctions::INPUT);
        RegisterFunction("IS_INTEGER",  1,  BuiltInFunctions::IS_INTEGER);
        RegisterFunction("IS_FLOAT",    1,  BuiltInFunctions::IS_FLOAT);
        RegisterFunction("IS_NUMBER",   1,  BuiltInFunctions::IS_NUMBER);
        RegisterFunction("IS_STRING",   1,  BuiltInFunctions::IS_STRING);
        RegisterFunction("IS_BOOLEAN",  1,  BuiltInFunctions::IS_BOOLEAN);
        RegisterFunction("IS_LIST",     1,  BuiltInFunctions::IS_LIST);
        RegisterFunction("IS_FUNCTION", 1,  BuiltInFunctions::IS_FUNCTION);
        RegisterFunction("IS_NULL",     1,  BuiltInFunctions::IS_NULL);
        RegisterFunction("TO_INTEGER",  1,  BuiltInFunctions::TO_INTEGER);
        RegisterFunction("TO_FLOAT",    1,  BuiltInFunctions::TO_FLOAT);
        RegisterFunction("TO_STRING",   1,  BuiltInFunctions::TO_STRING);
        RegisterFunction("APPEND",      2,  BuiltInFunctions::APPEND);
        RegisterFunction("PREPEND",     2,  BuiltInFunctions::PREPEND);
        RegisterFunction("INSERT",      3,  BuiltInFunctions::INSERT);
        RegisterFunction("POP",         1,  BuiltInFunctions::POP);
        RegisterFunction("REMOVE",      2,  BuiltInFunctions::REMOVE);
        RegisterFunction("EXTEND",      2,  BuiltInFunctions::EXTEND);
        RegisterFunction("LENGTH",      1,  BuiltInFunctions::LENGTH);
        RegisterFunction("READ",        1,  BuiltInFunctions::READ);
        RegisterFunction("WRITE",       2,  BuiltInFunctions::WRITE);
        RegisterFunction("RUN",         1,  BuiltInFunctions::RUN);
    }
    /* public static void InitializeFunctions(SymbolTable a_globalScope); */


    /* PRIVATE */

    /** Members **/

    private static SymbolTable m_globalScope;       // The global scope of the program


    /** Function Registry **/

    /**/
    /*
    Language.BuiltInFunctions.RegisterFunction()

    NAME

        RegisterFunction - registers a built-in function with the global scope.

    SYNOPSIS

        private static void RegisterFunction(String a_name, int a_parameterCount,
                                             BuiltIn a_function);
            a_name           -> the name of the function.
            a_parameterCount -> the number of parameters the function takes.
            a_function       -> the built-in Java function to call.

    DESCRIPTION

        Registers a built-in function with the global scope so a user can call
        it at a later time. This is done by creating a new Function object with
        the name, parameter count, and built-in function to call, then
        transformed into a value to put in our SymbolTable.

        This is a helper function for InitializeFunctions() that allows us to
        avoid code duplication and keep the code clean.

    RETURNS

        Nothing - it's a void function.

    */
    /**/
    private static void RegisterFunction(String a_name, int a_parameterCount, BuiltIn a_function) {
        Function function = new Function(a_name, a_parameterCount, a_function);
        m_globalScope.PutIdentifier(a_name, new Value(function));
    }
    /* private static void RegisterFunction(String a_name, int a_parameterCount,
                                            BuiltIn a_function); */


    /** Built-Ins - All must return a Value, should be NULL if no return. All built-ins should have method headers! **/

    /**/
    /*
    Language.BuiltInFunctions.PRINT()

    NAME

        PRINT - prints a Value to the console. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value PRINT(List<Value> a_argument);
            a_argument -> the Value to print.

    DESCRIPTION

        Prints a Value to the console without a newline. Only uses 1 argument.

    RETURNS

        A NULL Value object.

    */
    /**/
    private static Value PRINT(List<Value> a_argument) {
        Value value = a_argument.get(0);
        System.out.print(GetPrintableValue(value));
        return Value.NULL;
    }
    /* private static Value PRINT(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.PRINTLN()

    NAME

        PRINTLN - prints a Value to the console with a newline. ONLY TAKES 1
            ARGUMENT.

    SYNOPSIS

        private static Value PRINTLN(List<Value> a_argument);
            a_argument -> the value to print.

    DESCRIPTION

        Prints a Value to the console with a newline. Only uses 1 argument.

    RETURNS

        A NULL Value object.

    */
    /**/
    private static Value PRINTLN(List<Value> a_argument) {
        Value value = a_argument.get(0);
        System.out.println(GetPrintableValue(value));
        return Value.NULL;
    }
    /* private static Value PRINTLN(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.INPUT()

    NAME

        INPUT - gets input from the user. DOESN'T TAKE ANY ARGUMENTS.

    SYNOPSIS

        private static Value INPUT(List<Value> NONE);
            NONE -> no arguments.

    DESCRIPTION

        Gets input from the user and returns it as a string. Not cast as it is
        users responsibility to convert it to the appropriate type.

    RETURNS

        A String Value object with the user's input.

    */
    /**/
    private static Value INPUT(List<Value> NONE) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        // Up to user to convert to appropriate type - more about user agency than anything
        return new Value(input);
    }
    /* private static Value INPUT(List<Value> NONE); */

    /**/
    /*
    Language.BuiltInFunctions.IS_INTEGER()

    NAME

        IS_INTEGER - checks if a Value is an integer. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_INTEGER(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a value is an integer. Only uses 1 argument. Need to check the
        type of the Value - standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is our integer.

    */
    /**/
    private static Value IS_INTEGER(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsInteger());
    }
    /* private static Value IS_INTEGER(List<Value> a_argument); */


    /**/
    /*
    Language.BuiltInFunctions.IS_FLOAT()

    NAME

        IS_FLOAT - checks if a Value is a float. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_FLOAT(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a Value is a float. Only uses 1 argument. Need to check the
        type of the Value - standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is our float.
    */
    /**/
    private static Value IS_FLOAT(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsFloat());
    }
    /* private static Value IS_FLOAT(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.IS_NUMBER()

    NAME

        IS_NUMBER - checks if a Value is a number. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_NUMBER(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a Value is a number, i.e. if the Value is our integer or
        float. Only uses 1 argument. Need to check the type of the Value -
        standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is an integer or float.

    */
    /**/
    private static Value IS_NUMBER(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsNumber());
    }

    /**/
    /*
    Language.BuiltInFunctions.IS_STRING()

    NAME

        IS_STRING - checks if a Value is a string. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_STRING(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a Value is a string. Only uses 1 argument. Need to check the
        type of the Value - standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is our string.

    */
    /**/
    private static Value IS_STRING(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsString());
    }
    /* private static Value IS_STRING(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.IS_BOOLEAN()

    NAME

        IS_BOOLEAN - checks if a Value is a boolean. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_BOOLEAN(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a Value is a boolean. Only uses 1 argument. Need to check the
        type of the Value - standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is our boolean.

    */
    /**/
    private static Value IS_BOOLEAN(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsBoolean());
    }
    /* private static Value IS_BOOLEAN(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.IS_LIST()

    NAME

        IS_LIST - checks if a Value is a list. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_LIST(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a Value is a list. Only uses 1 argument. Need to check the
        type of the Value - standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is our list.

    */
    /**/
    private static Value IS_LIST(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsList());
    }
    /* private static Value IS_LIST(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.IS_FUNCTION()

    NAME

        IS_FUNCTION - checks if a Value is a function. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_FUNCTION(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a Value is a function. Only uses 1 argument. Need to check the
        type of the Value - standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is our function.

    */
    /**/
    private static Value IS_FUNCTION(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsFunction());
    }
    /* private static Value IS_FUNCTION(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.IS_NULL()

    NAME

        IS_NULL - checks if a Value is null. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value IS_NULL(List<Value> a_argument);
            a_argument -> the Value to check.

    DESCRIPTION

        Checks if a Value is null. Only uses 1 argument. Need to check the type
        of the Value - standard across all languages.

    RETURNS

        A Boolean Value object, if the Value is null.

    */
    /**/
    private static Value IS_NULL(List<Value> a_argument) {
        Value value = a_argument.get(0);
        return new Value(value.IsNull());
    }
    /* private static Value IS_NULL(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.TO_INTEGER()

    NAME

        TO_INTEGER - converts a Value to an integer. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value TO_INTEGER(List<Value> a_argument);
            a_argument -> the Value to convert.

    DESCRIPTION

        Converts a Value to an integer if it can be converted. For example, if
        the Value is a string "5" it will convert it to the integer 5, or if it
        is a float 5.6 it will convert it to the integer 5. Lists, booleans,
        functions, and NULLs cannot be converted to integers - and an exception
        will be thrown if it is one of these values (or if a bad string). Only
        uses 1 argument.

    RETURNS

        An integer Value object, if it was able to convert.

    */
    /**/
    private static Value TO_INTEGER(List<Value> a_argument) {
        Value value = a_argument.get(0);

        // Convert to number than to integer
        BigDecimal convertedValue = ConvertToNumber(value, Interpreter.Value.DataType.INTEGER);
        return new Value(convertedValue.toBigInteger());
    }
    /* private static Value TO_INTEGER(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.TO_FLOAT()

    NAME

        TO_FLOAT - converts a Value to a float. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value TO_FLOAT(List<Value> a_argument);
            a_argument -> the Value to convert.

    DESCRIPTION

        Converts a Value to a float if it can be converted. For example, if the
        Value is a string "5.6" it will convert it to the float 5.6, or if it is
        an integer 5 it will convert it to the float 5.0. Lists, booleans,
        functions, and NULLs cannot be converted to floats - and an exception
        will be thrown if it is one of these values (or if a bad string). Only
        uses 1 argument.

    RETURNS

        A float Value object, if it was able to convert.

    */
    /**/
    private static Value TO_FLOAT(List<Value> a_argument) {
        Value value = a_argument.get(0);

        // Convert to BigDecimal, then into our FLOAT value
        BigDecimal convertedValue = ConvertToNumber(value, Interpreter.Value.DataType.FLOAT);
        return new Value(convertedValue);
    }
    /* private static Value TO_FLOAT(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.TO_STRING()

    NAME

        TO_STRING - converts a Value to a string. ONLY TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value TO_STRING(List<Value> a_argument);
            a_argument -> the Value to convert.

    DESCRIPTION

        Converts a Value to a string. For example, if the Value is an integer 5
        it will convert it to the string "5", or if it is a float 5.6 it will
        convert it to the string "5.6". Lists will be 'pretty printed' as
        something like [1, 2, "hello"]. Booleans will be shown as "TRUE" or
        "FALSE" and NULL as "NULL". Functions will be shown as "FUNCTION <name>"
        where <name> is the function name. Strings on the other hand will not
        be converted to their 'pretty print'. Only uses 1 argument.


    RETURNS

        A string Value object of the 'pretty printed' Value.

    */
    /**/
    private static Value TO_STRING(List<Value> a_argument) {
        Value value = a_argument.get(0);

        // We don't want to format strings (don't want to add quotes)
        if (value.IsString()) { return value; }

        // Everything else can be formatted
        return new Value(value.FormatValueToString());
    }
    /* private static Value TO_STRING(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.APPEND()

    NAME

        APPEND - appends a Value to the end of a list. TAKES 2 ARGUMENTS, FIRST
        ARGUMENT MUST BE A LIST.

    SYNOPSIS

        private static Value APPEND(List<Value> a_arguments);
            a_arguments -> the list and value to append (0 is list, 1 is value)

    DESCRIPTION

        Appends a Value to the end of a list. The first argument must be a list
        and the second argument is the value to append. If the first argument is
        not a list, an exception will be thrown. The original list is not
        modified, rather the new list is created with the value appended to the
        end. Only uses 2 arguments.

    RETURNS

        A new list Value object with the Value appended to the end.

    */
    /**/
    private static Value APPEND(List<Value> a_arguments) {
        int size = MakeRawList(a_arguments).size();
        return InsertValue(a_arguments, new Value(BigInteger.valueOf(size)));
    }
    /* private static Value APPEND(List<Value> a_arguments); */

    /**/
    /*
    Language.BuiltInFunctions.PREPEND()

    NAME

        PREPEND - prepends a Value to the beginning of a list. TAKES 2
        ARGUMENTS, FIRST ARGUMENT MUST BE A LIST.

    SYNOPSIS

        private static Value PREPEND(List<Value> a_arguments);
            a_arguments -> the list and value to prepend (0 is list, 1 is value)

    DESCRIPTION

        Prepends a Value to the beginning of a list. The first argument must be
        a list and the second argument is the value to prepend. If the first
        argument is not a list, an exception will be thrown. The original list
        is not modified, rather the new list is created with the value prepended
        to the beginning. Only uses 2 arguments.

    RETURNS

        A new list Value object with the Value prepended to the beginning.

    */
    /**/
    private static Value PREPEND(List<Value> a_arguments) {
        // Insert at beginning, so index is 0
        return InsertValue(a_arguments, new Value(BigInteger.ZERO));
    }
    /* private static Value PREPEND(List<Value> a_arguments); */

    /**/
    /*
    Language.BuiltInFunctions.INSERT()

    NAME

        INSERT - inserts a Value at a specified index in a list. TAKES 3
        ARGUMENTS, FIRST ARGUMENT MUST BE A LIST AND THE THIRD ARGUMENT MUST BE
        THE INDEX TO INSERT AT.

    SYNOPSIS

        private static Value INSERT(List<Value> a_argument);
            a_arguments -> the list, value to insert, and index to insert at
                (0 is list, 1 is value, 2 is index)

    DESCRIPTION

        Inserts a Value at a specified index in a list. The first argument must
        be a list, the second argument is the value to insert, and the third
        argument is the index to insert at. If the first argument is not a list,
        an exception will be thrown. If the third argument is not an integer,
        an exception will also be thrown - it also must be in bounds of the
        length of the list.

        The original list is not modified, rather the new list is created with
        the value inserted at the specified index. Only uses 3 arguments.

    RETURNS

        A new list Value object with the Value inserted at the specified index.

    */
    /**/
    private static Value INSERT(List<Value> a_arguments) {
        // Insert at specified index (third argument)
        return InsertValue(a_arguments, a_arguments.get(2));
    }
    /* private static Value INSERT(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.POP()

    NAME

        POP - removes the last element from a list. TAKES 1 ARGUMENT, WHICH MUST
        BE A LIST.

    SYNOPSIS

        private static Value POP(List<Value> a_argument);
            a_argument -> the list to remove the last element from.

    DESCRIPTION

        Removes the last element from a list. The argument must be a list. If it
        is not a list, an exception will be thrown. The original list is not
        modified, rather the new list is created without the last element. Only
        uses 1 argument.

    RETURNS

        A new list Value object with the last element removed, if it can.

    */
    /**/
    private static Value POP(List<Value> a_argument) {
        // Ensure argument is a list and remove it from the end
        Value list = a_argument.get(0);
        EnforceArgumentType(Value.DataType.LIST, list.GetDataType(), 0);
        int endIndex = list.AsList().size() - 1;

        // Return the list without the last element
        return RemoveValue(a_argument, new Value(BigInteger.valueOf(endIndex)));
    }
    /* private static Value POP(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.REMOVE()

    NAME

        REMOVE - removes an element from a list at a specified index. TAKES 2
        ARGUMENTS, FIRST ARGUMENT MUST BE A LIST AND THE SECOND ARGUMENT MUST BE
        THE INDEX TO REMOVE AT.

    SYNOPSIS

        private static Value REMOVE(List<Value> a_argument);
            a_arguments -> the list and index to remove at (0 is list, 1 is
                index)

    DESCRIPTION

        Removes an element from a list at a specified index. The first argument
        must be a list and the second argument is the index to remove at. If the
        first argument is not a list, an exception will be thrown. If the second
        argument is not an integer, an exception will also be thrown - it also
        must be in bounds of the length of the list.

        The original list is not modified, rather the new list is created
        without the element at the specified index. Only uses 2 arguments.

    RETURNS

        A new list Value object with the element removed at the specified index.

    */
    /**/
    private static Value REMOVE(List<Value> a_arguments) {
        // Remove at specified index (second argument) and return the list
        return RemoveValue(a_arguments, a_arguments.get(1));
    }
    /* private static Value REMOVE(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.EXTEND()

    NAME

        EXTEND - extends a list by adding all elements from another list. TAKES
        2 ARGUMENTS, BOTH MUST BE LISTS.

    SYNOPSIS

        private static Value EXTEND(List<Value> a_arguments);
            a_arguments -> the list to extend and the list to add (0 is list to
                extend, 1 is list to add)

    DESCRIPTION

        Extends a list by adding all elements from another list. Both arguments
        must be lists. If either argument is not a list, an exception will be
        thrown. The original list is not modified, rather the new list is
        created with all the elements from the second list added to the end of
        the first list. This operation is the '+' operator on thw two lists,
        but as a built-in function. Only uses 2 arguments.

    RETURNS

        A new list Value object with all elements from the second list added to
        the end of the first list, if it can.

    */
    /**/
    private static Value EXTEND(List<Value> a_arguments) {
        Value leftList = a_arguments.get(0);
        Value rightList = a_arguments.get(1);

        // Ensure both arguments are lists
        EnforceArgumentType(Value.DataType.LIST, leftList.GetDataType(), 0);
        EnforceArgumentType(Value.DataType.LIST, rightList.GetDataType(), 1);

        // Add all elements from right list to left list and return
        // Help received: https://stackoverflow.com/questions/1128723/how-to-join-two-lists-in-java
        List<Value> rawList = leftList.AsList();
        rawList.addAll(rightList.AsList());
        return new Value(rawList);
    }
    /* private static Value EXTEND(List<Value> a_arguments); */

    /**/
    /*
    Language.BuiltInFunctions.LENGTH()

    NAME

        LENGTH - gets the length of a string or list. TAKES 1 ARGUMENT.

    SYNOPSIS

        private static Value LENGTH(List<Value> a_argument);
            a_argument -> the string or list to get the length of.

    DESCRIPTION

        Gets the length of a string or list. The argument must be a string or
        list. If it is not a string or list, an exception will be thrown. Only
        uses 1 argument.

    RETURNS

        An integer Value object with the length of the string or list.

    */
    /**/
    private static Value LENGTH(List<Value> a_argument) {
        Value value = a_argument.get(0);

        // Help received: IDE suggestion to use switch expression
        int length = switch (value.GetDataType()) {
            case STRING -> value.AsString().length();
            case LIST -> value.AsList().size();

            // Can only get length of strings and lists (only time we check for 2 types, so not in a helper)
            default ->
                throw new BuiltInFunctionException("Argument must be a '" + Value.DataType.STRING + "'" +
                                                   " or '" + Value.DataType.LIST + "'");
        };

        return new Value(BigInteger.valueOf(length));
    }
    /* private static Value LENGTH(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.READ()

    NAME

        READ - reads the content of a file. TAKES 1 ARGUMENT, MUST BE A STRING.

    SYNOPSIS

        private static Value READ(List<Value> a_argument);
            a_argument -> the file name to read.

    DESCRIPTION

        Reads the content of a file. The argument must be a string. If it is not
        a string, an exception will be thrown. If the file is not found, or an
        error occurs while reading, an exception will be thrown. Only uses 1
        argument.

    RETURNS

        A string Value object with the content of the file.

    */
    /**/
    private static Value READ(List<Value> a_argument) {
        // Get the path based off the file name
        Path path = MakeReadPath(a_argument.get(0));

        // Try to read the file and return the content
        try { return new Value(Files.readString(path)); }
        catch (IOException ignored) { throw new BuiltInFunctionException("Could not read file '" + path + "'"); }
    }
    /* private static Value READ(List<Value> a_argument); */

    /**/
    /*
    Language.BuiltInFunctions.WRITE()

    NAME

        WRITE - writes content to a file. TAKES 2 ARGUMENTS, BOTH MUST BE
        STRINGS.

    SYNOPSIS

        private static Value WRITE(List<Value> a_arguments);
            a_arguments -> the file name to write to and the content to write.

    DESCRIPTION

        Writes content to a file. The first argument must be a string with the
        file name and the second argument must be a string with the content to
        write. If either argument is not a string, an exception will be thrown.
        If the file cannot be written to, an exception will be thrown. Only uses
        2 arguments.

    RETURNS

        A NULL Value object.

    */
    /**/
    private static Value WRITE(List<Value> a_arguments) {
        // First argument is file name, second is content - both should be strings
        Value fileName = a_arguments.get(0);
        EnforceArgumentType(Value.DataType.STRING, fileName.GetDataType(), 0);
        Value content = a_arguments.get(1);
        EnforceArgumentType(Value.DataType.STRING, content.GetDataType(), 1);

        // Get the file path and write the content to it
        Path filePath = Paths.get(fileName.AsString());

        // Write the content to the file (overwrites if the file already exists) if we can
        // Help received: https://stackoverflow.com/a/23221771
        try { Files.writeString(filePath, content.AsString()); }
        catch (NoSuchFileException error ) { throw new BuiltInFunctionException("Dir '" + filePath + "' not found"); }
        catch (IOException error) { throw new BuiltInFunctionException("Could not write to file '" + filePath + "'"); }

        return Value.NULL;
    }
    /* private static Value WRITE(List<Value> a_arguments); */

    /**/
    /*
    Language.BuiltInFunctions.RUN()

    NAME

        RUN - runs a file. TAKES 1 ARGUMENT, MUST BE A STRING.

    SYNOPSIS

        private static Value RUN(List<Value> a_argument);
            a_argument -> the file name to run.

    DESCRIPTION

        Runs a .bb program file. Any functions or variables defined in the file
        will be added to the global scope. Extremely useful for importing large
        amounts of code. The argument must be a string, an exception will be
        thrown if not. If the file is not found, does not have a '.bb'
        extension, or an error occurs while running, an exception will be
        thrown to notify the user. Only uses 1 argument.

    RETURNS

        A NULL Value object.

    */
    /**/
    private static Value RUN(List<Value> a_argument) {
        // Get the file path from first argument (filename)
        Path filePath = MakeReadPath(a_argument.get(0));
        EnforceFileExtension(filePath);

        // Save the current file we are on
        String previousText = Error.GetInput();

        // Use our Main class to run the file since it does exactly what we want (we don't care about the return value)
        try { RunFromFile(filePath.toString()); }
        catch (IOException error) { throw new BuiltInFunctionException("Could not run file '" + filePath + "'"); }

        // Reset our errors
        Error.InitializeInput(previousText);

        return Value.NULL;
    }
    /* private static Value RUN(List<Value> a_argument); */


    /** Private Helper Function for Built-Ins **/

    /**/
    /*
    Language.BuiltInFunctions.GetPrintableValue()

    NAME

        GetPrintableValue - gets a printable string from a Value.

    SYNOPSIS

        private static String GetPrintableValue(Value a_value);
            a_value -> the Value to get a printable string from.

    DESCRIPTION

        Gets a printable string from a Value. If the Value is a string, it will
        return the string to avoid quotes. If it is not a string, it will return
        the formatted string of the Value for pretty print. This is a helper
        function for the PRINT() and PRINTLN() built-in functions.

    RETURNS

        A string that is printable from the Value.

    */
    /**/
    private static String GetPrintableValue(Value a_value) {
        if (a_value.IsString()) { return a_value.AsString(); }
        else { return a_value.FormatValueToString(); }
    }

    /**/
    /*
    Language.BuiltInFunctions.ConvertToNumber()

    NAME

        ConvertToNumber - converts a Value to a specified number type.

    SYNOPSIS

        private static BigDecimal ConvertToNumber(Value a_value,
                                                  Value.DataType a_toType);
            a_value  -> the Value to convert.
            a_toType -> the type to convert to.

    DESCRIPTION

        Converts a Value to a specified number type. The Value must be an
        integer, float, or string that can be converted to a number. If it is
        not one of these types, an exception will be thrown. If the string is
        not a valid number, an exception will also be thrown. This is a helper
        function for the TO_INTEGER() and TO_FLOAT() built-in functions.

        Uses regex to check if the string is a valid number. If it is, it will
        convert it to a BigDecimal and then to the specified number type.

    RETURNS

        A BigDecimal object which was the Value converted to a Java number.

    */
    /**/
    private static BigDecimal ConvertToNumber(Value a_value, Value.DataType a_toType) {
        Value.DataType type = a_value.GetDataType();
        switch(type) {
            case INTEGER, FLOAT: return a_value.AsFloat();

            // Can only convert strings that are integers or floats, falls to default if not
            // Help received: https://regex101.com
            case STRING:
                boolean isValidNumber = a_value.AsString().matches("^-?\\d+(\\.\\d+)?$");
                if (isValidNumber) { return new BigDecimal(a_value.AsString()); }

            // Can only convert: integer, float, string everything else is invalid (can't reach ZERO, Java doesn't know)
            default: throw new BuiltInFunctionException("Cannot convert '" + type + "' to '" + a_toType + "'");
        }
    }
    /* private static BigDecimal ConvertToNumber(Value a_value, Value.DataType a_toType); */

    /**/
    /*
    Language.BuiltInFunctions.MakeReadPath()

    NAME

        MakeReadPath - creates a Path object from a file name to read.

    SYNOPSIS

        private static Path MakeReadPath(Value a_fileName);
            a_fileName -> the file name to read.

    DESCRIPTION

        Creates a Path object from a file name to read. The file name must be a
        string. If it is not a string, an exception will be thrown. If the file
        does not exist, an exception will be thrown. This is a helper function
        for the READ() and RUN() built-in functions.

    RETURNS

        A Path object to the file to read, if it can.
    */
    /**/
    private static Path MakeReadPath(Value a_fileName) {
        // Ensure argument is a string
        EnforceArgumentType(Value.DataType.STRING, a_fileName.GetDataType(), 0);

        // Get file and attach to current directory
        Path path = Paths.get(a_fileName.AsString());
        EnforceFileExists(path);

        return path;
    }
    /* private static Path MakeReadPath(Value a_fileName); */


    /*
     *      List Helper - Inserting and removing elements from a list
     */

    /**/
    /*
    Language.BuiltInFunctions.InsertValue()

    NAME

        InsertValue - inserts a Value at a specified index in a list.

    SYNOPSIS

        private static Value InsertValue(List<Value> a_arguments,
                                         Value a_index);
            a_arguments -> the list, value to insert, and index to insert at
                (0 is list, 1 is value, 2 is index)
            a_index     -> the index to insert at.

    DESCRIPTION

        Inserts a Value at a specified index in a list. The first argument must
        be a list, the second argument is the value to insert, and the third
        argument is the index to insert at. If the first argument is not a list,
        an exception will be thrown. If the third argument is not an integer, an
        exception will also be thrown - it also must be in bounds of the length
        of the list. Helper function for the INSERT() and APPEND() built-in.

        The original list is not modified, rather the new list is created with
        the value inserted at the specified index.

    RETURNS

        A new list Value object with the Value inserted at the specified index.

    */
    /**/
    private static Value InsertValue(List<Value> a_arguments, Value a_index) {
        // Get the raw list and index - ensures that the list is a list and the index is an integer and in range
        List<Value> rawList = MakeRawList(a_arguments);
        int index = MakeIndex(a_index, rawList.size());

        // Insert at that index and return
        rawList.add(index, a_arguments.get(1));
        return new Value(rawList);
    }
    /* private static Value InsertValue(List<Value> a_arguments,
                                        Value a_index); */

    /**/
    /*
    Language.BuiltInFunctions.RemoveValue()

    NAME

        RemoveValue - removes an element from a list at a specified index.

    SYNOPSIS

        private static Value RemoveValue(List<Value> a_arguments,
                                         Value a_index);
            a_arguments -> the list and index to remove at (0 is list, 1 is
                index)
            a_index     -> the index to remove at.

    DESCRIPTION

        Removes an element from a list at a specified index. The first argument
        must be a list and the second argument is the index to remove at. If the
        first argument is not a list, an exception will be thrown. If the second
        argument is not an integer, an exception will also be thrown - it also
        must be in bounds of the length of the list. Helper function for the
        REMOVE() and POP() built-in.

        The original list is not modified, rather the new list is created
        without the element at the specified index.

    RETURNS

        A new list Value object with the element removed at the specified index.

    */
    /**/
    private static Value RemoveValue(List<Value> a_arguments, Value a_index) {
        // Get the raw list and index - ensures that the list is a list and the index is an integer and in range
        List<Value> rawList = MakeRawList(a_arguments);
        int index = MakeIndex(a_index, rawList.size() - 1);

        // Remove at that index and return
        rawList.remove(index);
        return new Value(rawList);
    }
    /* private static Value RemoveValue(List<Value> a_arguments,
                                        Value a_index); */

    /**/
    /*
    Language.BuiltInFunctions.MakeRawList()

    NAME

        MakeRawList - creates a raw list (Value to List<Value>).

    SYNOPSIS

        private static List<Value> MakeRawList(List<Value> a_arguments);
            a_arguments -> the list Value to convert.

    DESCRIPTION

        Creates a raw list from a list Value. The first argument must be a list.
        If it is not a list, an exception will be thrown. Helper function for
        the InsertValue() and RemoveValue(). Ensures that the list is a list and
        avoids code duplication.

    RETURNS

        A raw list of Value objects from the list Value.

    */
    /**/
    private static List<Value> MakeRawList(List<Value> a_arguments) {
        // First argument must be a list
        Value list = a_arguments.get(0);
        EnforceArgumentType(Value.DataType.LIST, list.GetDataType(), 0);
        return list.AsList();
    }
    /* private static List<Value> MakeRawList(List<Value> a_arguments); */

    /**/
    /*
    Language.BuiltInFunctions.MakeIndex()

    NAME

        MakeIndex - creates an integer index from a Value.

    SYNOPSIS

        private static int MakeIndex(Value a_index, int a_listSize);
            a_index    -> the index Value to convert.
            a_listSize -> the size of the list.

    DESCRIPTION

        Ensures that the index is an integer and in bounds of the list size. If
        not these things, an exception will be thrown. Lists in Java are capped
        at around 2 billion, so we must convert a Java int to handle. Is the
        helper function for the InsertValue() and RemoveValue(). Ensures that
        the index is usable.

    RETURNS

        An integer index from the Value.

    */
    /**/
    private static int MakeIndex(Value a_index, int a_listSize) {
        // Ensure 3rd argument (index) is an integer
        EnforceArgumentType(Value.DataType.INTEGER, a_index.GetDataType(), 2);

        // Ensure index is in range
        int index = a_index.AsInteger().intValue();
        EnforceIndexBounds(index, a_listSize);
        return index;
    }
    /* private static int MakeIndex(Value a_index, int a_listSize); */


    /*
     *      Error Handling - Ensuring proper types, conversions, and bounds
     */


    /**/
    /*
    Language.BuiltInFunctions.EnforceArgumentType()

    NAME

        EnforceArgumentType - ensures that a Value is of a specified type.

    SYNOPSIS

        private static void EnforceArgumentType(Value.DataType a_expected,
                                                Value.DataType a_actual,
                                                int a_argumentIndex);
            a_expected      -> the expected Value type.
            a_actual        -> the actual Value type.
            a_argumentIndex -> the index of the argument when called.

    DESCRIPTION

        Ensures that a Value is of a specified type. If it is not, an exception
        will be thrown. Ensures that the Value is the expected type when called
        allowing for more descriptive error messages.

    RETURNS

        Nothing - is a void function.

    */
    /**/
    private static void EnforceArgumentType(Value.DataType a_expected, Value.DataType a_actual, int a_argumentIndex) {
        if (a_expected != a_actual) {
            throw new BuiltInFunctionException("Invalid argument (index " + a_argumentIndex  + ") for function!" +
                                               " Expected '" + a_expected + "' but got '" + a_actual + "'");
        }
    }
    /* private static void EnforceArgumentType(Value.DataType a_expected,
                                              Value.DataType a_actual,
                                              int a_argumentIndex); */

    /**/
    /*
    Language.BuiltInFunctions.EnforceIndexBounds()

    NAME

        EnforceIndexBounds - ensures that an index is in bounds of a list.

    SYNOPSIS

        private static void EnforceIndexBounds(int a_index, int a_size);
            a_index -> the index to check.
            a_size  -> the size of the list.

    DESCRIPTION

        Ensures that an index is in bounds of a list. If it is not, an exception
        will be thrown. Ensures that the index is in range of the list size when
        called allowing for more descriptive error messages.

    RETURNS

        Nothing - is a void function.

    */
    /**/
    private static void EnforceIndexBounds(int a_index, int a_size) {
        if (a_index < 0 || a_index > a_size) {
            throw new BuiltInFunctionException("Index " + a_index + " out of range of usable length " + a_size);
        }
    }
    /* private static void EnforceIndexBounds(int a_index, int a_size); */

    /**/
    /*
    Language.BuiltInFunctions.EnforceFileExists()

    NAME

        EnforceFileExists - ensures that a file exists.

    SYNOPSIS

        private static void EnforceFileExists(Path a_path);
            a_path -> the file path to check.

    DESCRIPTION

        Ensures that a file exists. If it does not, an exception will be thrown.
        Ensures that the file exists when called allowing for more descriptive
        error messages.

    RETURNS

        Nothing - is a void function.

    */
    /**/
    private static void EnforceFileExists(Path a_path) {
        if (!Files.exists(a_path)) {
            throw new BuiltInFunctionException("File '" + a_path + "' does not exist");
        }
    }
    /* private static void EnforceFileExists(Path a_path); */

    /**/
    /*
    Language.BuiltInFunctions.EnforceFileExtension()

    NAME

        EnforceFileExtension - ensures that a file has a specified extension.

    SYNOPSIS

        private static void EnforceFileExtension(Path a_path);
            a_path -> the file path to check.

    DESCRIPTION

        Ensures that a file has a specified extension. If it does not, an
        exception will be thrown as we can only guarantee that the file can run
        from a '.bb' extension. Ensures that the file has the correct extension
        when called allowing for more descriptive error messages.

    RETURNS

        Nothing - is a void function.

    */
    /**/
    private static void EnforceFileExtension(Path a_path) {
        if (!a_path.toString().endsWith(Main.FILE_EXTENSION)) {
            throw new BuiltInFunctionException("Invalid file extension. Must be a " + Main.FILE_EXTENSION + " file!");
        }
    }
    /* private static void EnforceFileExtension(Path a_path); */

}