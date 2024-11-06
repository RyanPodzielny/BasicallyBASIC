' This is a comment, everything written after it will be disregarded

''
This is a multi-line comment, everything written between the two single quotes
will be disregarded
''

' We can assign variables by using the LET keyword
' LET <identifier> can be set to any value or just initialized to NULL

' Assigns NULL to the variable
LET var
' And we can rewrite at any time with a different value type
LET var = TRUE

' This assigns an INTEGER to the variable
LET int_val = 10
' Works with STRING's and FLOAT's
LET float_val = 5.4
LET string_val = "Duck"
' As well as lists - we can nest them to make them multidimensional
LET list_val = [10, ["Hello!"]]

' We can also use expressions with variables, and use the in-built function
'   PRINTLN to print to standard output
LET result = (int_val + 1) * 3
PRINTLN("Showing the result of the expression (int_val + 1) * 3: ")
PRINTLN(result)

' We can use an if-else statement for a control structure
LET result
IF int_val < 5 THEN
    LET result = "Less than 5"
ELSEIF int_val > 11 THEN
    LET result = "Greater than 11"
ELSE
    LET result = "In between 5 and 11"
' We use keyword END to signify the control structure is over
END

PRINTLN("Showing the result of the if-else statement:")
PRINTLN(result)

' We can also nest if-else statements
LET result
IF int_val < 5 THEN
    IF int_val > 3 THEN
        LET result = "Between 3 and 5"
    END
ELSE
    LET result = "Not between 3 and 5"
END

PRINTLN("Showing the result of the nested if-else statement: ")
PRINTLN(result)