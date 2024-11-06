///
/// Value - Represents and stores a value in the language. Includes data types of an integer, float, boolean, string,
///     list. Integers are stored as BigIntegers and floats as BigDecimal to avoid overflow within the language. Class
///     performs implicit conversion between floats and integers, and provides access to equality checks and string
///     formatting. All values are immutable, so only copies are returned (no references).
///

package Interpreter;

import AbstractSyntaxTree.Statements.Function;
import Lexer.Lexer;
import Lexer.OperationCode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;


public final class Value {
    /* PUBLIC */

    /** Constants **/

    // Using an enum to represent types as it is more readable and easier to understand. Making the types polymorphic
    //      to an abstract class makes it much more difficult to use and creates unnecessary complexity. If we were to
    //      add classes and a lot more types, it would be more appropriate.
    public enum DataType {
        INTEGER,    // Whole numbers
        FLOAT,      // Decimal numbers
        BOOLEAN,    // True or false, that's it
        STRING,     // Text surrounded by quotes
        LIST,       // Collection of values of any type - can't be indexed past Java's INT_MAX
        FUNCTION,   // A body of code with parameters

        NULL        // No value, way easier to handle than Java's null
    }

    // Precision of floating point numbers, set to 16 decimal places. Needed as irrational numbers can't be represented.
    //      Here to keep information together and avoid magic numbers, but also used in interpreter for calculations.
    // Help received: https://stackoverflow.com/questions/4591206/setting-the-precision-of-a-bigdecimal
    static public final MathContext PRECISION = MathContext.DECIMAL64;
    // Error message for unknown type, used in error handling
    static public final String UNKNOWN_TYPE = "Unknown value type: ";
    // Constant for our NULL to make it more clear when we are using it, instead of new Value() everywhere
    static public final Value NULL = new Value();


    /** Constructors **/

    // Initialize with a value of type integer - BigInteger as we don't want overflow
    public Value(BigInteger a_int) {
        m_type = DataType.INTEGER;
        m_value = a_int;
    }

    // Initialize with a value of type float - BigDecimal used for same reason an BigInteger (overflow)
    public Value(BigDecimal a_float) {
        m_type = DataType.FLOAT;
        // Can't represent irrational numbers, so we need to round to a set precision
        m_value = a_float;
    }

    // Initialize with a value of type boolean
    public Value(boolean a_boolean) {
        m_type = DataType.BOOLEAN;
        m_value = a_boolean;
    }

    // Initialize with a value of type string
    public Value(String a_string) {
        m_type = DataType.STRING;
        m_value = a_string;
    }

    // Initialize with a value of type list
    public Value(List<Value> a_list) {
        m_type = DataType.LIST;
        // Assume it's just empty if null
        if (a_list == null) { a_list = new ArrayList<>(); }
        m_value = a_list;
    }

    // Initialize with a function (includes its name, parameters, and body)
    public Value(Function a_function) {
        m_type = DataType.FUNCTION;
        m_value = a_function;
    }

    // No value is our NULL value
    public Value() {
        m_type = DataType.NULL;
        m_value = null;
    }


    /** Accessors **/

    // Returns the data type of the value
    public DataType GetDataType() {
        return m_type;
    }

    // Returns if the value is of type integer
    public boolean IsInteger() {
        return m_type == DataType.INTEGER;
    }

    // Returns if the value is of type float
    public boolean IsFloat() {
        return m_type == DataType.FLOAT;
    }

    // Returns if the value is of type number (integer or float)
    public boolean IsNumber() {
        return IsInteger() || IsFloat();
    }

    // Returns if the value is of type boolean
    public boolean IsBoolean() {
        return m_type == DataType.BOOLEAN;
    }

    // Returns if the value is of type string
    public boolean IsString() {
        return m_type == DataType.STRING;
    }

    // Returns if the value is of type list
    public boolean IsList() {
        return m_type == DataType.LIST;
    }

    // Returns if the value is of type function
    public boolean IsFunction() {
        return m_type == DataType.FUNCTION;
    }

    // Returns if the value is of type null
    public boolean IsNull() {
        return m_type == DataType.NULL;
    }


    /** Methods **/

    // Returns the integer value, will coerce into integer if it is a float
    public BigInteger AsInteger() {
        return (BigInteger) AsNumber(DataType.INTEGER);
    }

    // Returns the float value, will coerce into float if it is an integer
    public BigDecimal AsFloat() {
        return (BigDecimal) AsNumber(DataType.FLOAT);
    }

    // Returns the proper Java boolean, if it is one
    public boolean AsBoolean() {
        ConfirmType(DataType.BOOLEAN);
        return (boolean) m_value;
    }

    // Returns the proper Java string, if it is one. Casting is left up to FormatValueToString(), just want raw value.
    public String AsString() {
        ConfirmType(DataType.STRING);
        return (String) m_value;
    }

    /**/
    /*
    Interpreter.Value.AsList()

    NAME

        AsList - gets the Value as a list of Values.

    SYNOPSIS

        private List<Value> AsList();

    DESCRIPTION

        Returns the Value as a list of Values. If the Value is not a list, it
        will throw an assertion error. If the Value is a list, it will return a
        deep copy of the list. This is done to ensure that the list is immutable
        and that changes to the list do not affect the Value.

    RETURNS

        A list of Values that is a deep copy of the list stored in the Value.

    */
    /**/
    public List<Value> AsList() {
        // Don't want to implicitly cast when not a list
        ConfirmType(DataType.LIST);

        List<Value> listCopy = new ArrayList<>();

        // Type erasure means that the compiler doesn't know the type of the list, so we need to check each element
        // Help received: https://stackoverflow.com/questions/339699/java-generics-type-erasure-when-and-what-happens
        for (Object element : (List<?>) m_value) {
            // Serious issue, should never be able to happen
            assert element instanceof Value : UNKNOWN_TYPE + element.getClass().getName();

            // Can safely cast to Value
            Value value = (Value) element;

            // If we have a nested list, we need to deep copy it
            if (value.GetDataType() == DataType.LIST) { listCopy.add(new Value(value.AsList())); }
            else { listCopy.add(value); }
        }

        return listCopy;
    }
    /* private List<Value> AsList(); */

    // Returns the function value, will throw an assertion error if it is not a function
    public Function AsFunction() {
        ConfirmType(DataType.FUNCTION);
        return (Function) m_value;
    }

    /**/
    /*
    Interpreter.Value.FormatValueToString()

    NAME

        FormatValueToString - formats the Value to a 'pretty' string to output.

    SYNOPSIS

        public String FormatValueToString();

    DESCRIPTION

        Depending on the type of the Value, it will format it to a pretty string
        to output. Integers and floats will be formatted as numbers, booleans as
        all caps, strings with quotes, lists with brackets, and functions with
        the word 'FUNCTION' and the function name. If the Value is null, it will
        be formatted as 'NULL'. This is done to make the output more readable and
        easier to understand.

    RETURNS

        A string representation of the Value that is formatted to be 'pretty'.

    */
    /**/
    public String FormatValueToString() {
        // Help received: IDE suggestion for switch expression
        return switch (m_type) {
            case INTEGER -> FormatInteger(AsInteger());
            case STRING -> FormatString(AsString());
            case BOOLEAN -> FormatBoolean(AsBoolean());
            case FLOAT -> FormatFloat(AsFloat());
            case LIST -> FormatList(AsList());
            case FUNCTION -> FormatFunction(AsFunction());
            case NULL -> OperationCode.NULL.LABEL;

            // Should never happen, something very wrong
            default -> throw new IllegalStateException(UNKNOWN_TYPE + m_type);
        };
    }
    /* public String FormatValueToString(); */

    /**/
    /*
    Interpreter.Value.Equals()

    NAME

        Equals - compares the Value object to another Value object.

    SYNOPSIS

        public boolean Equals(Value a_other);
            a_other    -> the other Value to compare to.

    DESCRIPTION

        Compares the Value object to another Value object to see if they are
        equal to one another. Java nulls can't be compared, two numbers (integer
        or float) can be compared regardless of same type. Everything else must
        be the same type to be equal. If the Value is a list, it will
        recursively compare each element to see if they are equal. If the Value
        is a function, it will compare the references to see if they are the
        same.

    RETURNS

        True if the Value objects are equal, false otherwise. Java null are not
        equal to anything, so they are false.

    */
    /**/
    // Needed to be done in value since it is important for actual object comparison (makes things much easier).
    public boolean Equals(Value a_other) {
        // Can't compare a Java null
        if (a_other == null) { return false; }

        // Can compare two numbers if they are the same type
        // Help received: https://stackoverflow.com/a/34677687 for comparing BigDecimals/BigIntegers
        if (this.IsNumber() && a_other.IsNumber()) { return this.AsFloat().compareTo(a_other.AsFloat()) == 0; }

        // But short circuit out if we are with other types and not numbers (using this for better distinction)
        if (this.m_type != a_other.m_type) { return false; }

        // Compare will depend based on the Values type (compareTo for numbers, equals for everything else)
        // Help received: IDE suggestion for switch expression
        return switch (this.m_type) {
            case INTEGER -> this.AsInteger().compareTo(a_other.AsInteger()) == 0;
            case FLOAT -> this.AsFloat().compareTo(a_other.AsFloat()) == 0;
            case BOOLEAN -> this.AsBoolean() == a_other.AsBoolean();
            case STRING -> this.AsString().equals(a_other.AsString());
            case LIST -> AreListsEqual(this.AsList(), a_other.AsList());
            // Equal if same reference
            case FUNCTION -> this.AsFunction().equals(a_other.AsFunction());
            // NULL == NULL (can't be any other piece of data)
            case NULL -> true;

            // Should never be reachable, something very wrong
            default -> throw new IllegalStateException(UNKNOWN_TYPE + m_type);
        };
    }
    /* public boolean Equals(Value a_other); */


    /* PRIVATE */

    /** Members **/

    DataType m_type;    // The type of the value (integer, float, boolean, string, list)
    Object m_value;     // The actual value, stored as object to allow for different java types


    /** Private Helper Functions **/

    /**/
    /*
    Interpreter.Value.AsNumber()

    NAME

        AsNumber - gets the Value as a number.

    SYNOPSIS

        private Object AsNumber(DataType a_targetType);
            a_targetType    -> the type to convert the Value to.

    DESCRIPTION

        Gets the Value as a number. If the Value is not a number, it will throw
        an assertion error. Depending on the target type, it will convert the
        Value to an integer or a float. If the Value is an integer, it will
        convert it to a float. If the Value is a float, it will convert it to an
        integer. This is done to allow for implicit conversion between integers
        and floats.

        Any other type will throw an assertion error as it is not a number. If
        not caught by assertion (impossible) we will throw an illegal state

    RETURNS

        The Value as a number, either a BigInteger or a BigDecimal.

    */
    /**/
    private Object AsNumber(DataType a_targetType) {
        // Ensure we got a number
        ConfirmNumberType();

        // Coerce the number to the target type
        // Help received: IDE suggestion for switch expression and ternary's
        return switch (m_type) {
            case INTEGER -> a_targetType == DataType.INTEGER ? m_value : new BigDecimal((BigInteger) m_value);
            case FLOAT -> a_targetType == DataType.INTEGER ? ((BigDecimal) m_value).toBigInteger() : m_value;

            // Should never be reachable, something very wrong
            default -> throw new IllegalStateException("Can't coerce '" + m_type + "' to '" + a_targetType + "'");
        };
    }
    /* private Object AsNumber(DataType a_targetType); */


    /*
     *      Formatting Functions - Used to convert the value to a string for good-looking output
     */

    // BigInteger.toString() works well, added as function for completion
    private String FormatInteger(BigInteger a_int) {
        return a_int.toString();
    }

    // Floats can be shown as integers in BigDecimal class, want to ensure user knows it's a float
    private String FormatFloat(BigDecimal a_float) {
        String result = a_float.stripTrailingZeros().toPlainString();
        // If it is being represented as an int (can happen with division) make it look like a float
        if (!result.contains(String.valueOf(Lexer.DECIMAL))) { return result + Lexer.DECIMAL + "0"; }
        return result;
    }

    // We want all caps to mimic syntax design, ensuring how we represent it in our opcodes
    private String FormatBoolean(boolean a_boolean) {
        // Help received: IDE suggestion for ternary
        return a_boolean ? OperationCode.TRUE.LABEL : OperationCode.FALSE.LABEL;
    }

    // Add quotes to end of string according to how we represent it within syntax
    private String FormatString(String a_string) {
        return OperationCode.QUOTE.LABEL + a_string + OperationCode.QUOTE.LABEL;
    }

    /**/
    /*
    Interpreter.Value.FormatList()

    NAME

        FormatList - formats a list of Values to a string.

    SYNOPSIS

        private String FormatList(List<Value> a_list);
            a_list    -> the list of Values to format.

    DESCRIPTION

        Formats a list of Values to a string. It will add brackets to the start
        and end of the list, and commas and spaces between each element. If the
        list is empty, it will return empty brackets. If the list is not empty,
        it will recursively call FormatValueToString() on each element to format
        it to a string. This is done to make the output more readable and easier
        to understand.

    RETURNS

        A string representation of the list of Values that is formatted to be
        'pretty', e.g. ["hello", 1.2, [1, TRUE], ...].

    */
    /**/
    private String FormatList(List<Value> a_list) {
        StringBuilder builder = new StringBuilder();

        // Add our list starter according to how we represent it
        builder.append(OperationCode.LBRACKET.LABEL);

        // Deep 'stringify' every element - list will recursively call
        // Will look like this: [element1, element2, ...]
        for (Value element : a_list) {
            builder.append(element.FormatValueToString());
            builder.append(OperationCode.COMMA.LABEL);
            builder.append(" ");
        }

        // Remove the last comma and space if there is one
        if (!a_list.isEmpty()) { builder.delete(builder.length() - 2, builder.length()); }

        // Same thing for ender
        builder.append(OperationCode.RBRACKET.LABEL);

        return builder.toString();
    }
    /* private String FormatList(List<Value> a_list); */

    // Just want to show that it is a function, and the name of the function
    private String FormatFunction(Function a_function) {
        return "FUNCTION '" + a_function.GetFunctionName() + "'";
    }

    /**/
    /*
    Interpreter.Value.AreListsEqual()

    NAME

        AreListsEqual - compares two lists of Values to see if they are equal.

    SYNOPSIS

        private boolean AreListsEqual(List<Value> a_left, List<Value> a_right);
            a_left     -> the first list of Values to compare.
            a_right    -> the second list of Values to compare.

    DESCRIPTION

        Compares two lists of Values to see if they are equal. When the lists
        are the same size it will recursively compare each element to see if
        they are equal. If at least one of the values is not equal, it will
        return false.

    RETURN

        True if the deep compare on the lists are equal, false otherwise.

    */
    /**/
    private boolean AreListsEqual(List<Value> a_left, List<Value> a_right) {
        // Short circuit out if not same size
        if (a_left.size() != a_right.size()) { return false; }

        // Deep compare each value
        for (int index = 0; index < a_left.size(); index++) {
            // At any point if they are not equal, it means the list aren't
            if (!a_left.get(index).Equals(a_right.get(index))) { return false; }
        }
        return true;
    }
    /* private boolean AreListsEqual(List<Value> a_left, List<Value> a_right); */


    /*
     *      Correct Type Conversions - So developers don't do something they shouldn't
     */

    // Used to tell developer not to use a type that is not the one they are expecting
    private void ConfirmType(DataType a_type) {
        assert m_type == a_type : "Value is not a " + a_type;
    }

    // Confirm that it is an integer or a float, used for developers to know what type they are working with
    private void ConfirmNumberType() {
        assert IsNumber() : "Value is not a " + DataType.INTEGER + " or a " + DataType.FLOAT;
    }

}