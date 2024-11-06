' We can use the keyword DEF to define a function
' It can contain any number of arguments separated by a comma
DEF add(value1, value2)
    ' Using return we can send the value back to caller
    RETURN value1 + value2
END

' We can use the in-built input function to get input from the user
PRINTLN("Enter a INTEGER to add to 3: ")
LET in = INPUT()
' And we can call it at a later place
PRINTLN("Adding 3 to the input: ")
PRINTLN(add(TO_INTEGER(in), 3))

PRINTLN("")

' We can also use recursion
DEF countdown(value)
    ' Breakout of recursion
    IF value <= 0 THEN
        RETURN
    END

    PRINTLN(value)
    countdown(value - 1)
END
' We can use the TO_STRING in-built function to convert a value to a string!
LET value = 5
PRINTLN("Counting down from: " + TO_STRING(value))
countdown(5)

PRINTLN("")

''
Using a semi-colon we can break put multi-line statements into one line. These
work with any statement within a file! They are treated as a newline
character, same as '\n'.
''
DEF sub(value1, value2); RETURN value1 - value2; END
PRINTLN("Subtracting 1 from 2: ")
PRINTLN(sub(1, 1))

PRINTLN("")

' Functions are also treated as types and can be assigned to variables
LET func = add
PRINTLN("Using the function assigned to a variable: ")
PRINTLN(func(1, 2))

PRINTLN("")

' Lastly we can also nest functions, but they are only accessible within the
'   scope they are defined in.
DEF outer()
    DEF inner()
        PRINTLN("Inner function")
    END

    inner()
END
PRINTLN("Showing the nested function: ")
outer()