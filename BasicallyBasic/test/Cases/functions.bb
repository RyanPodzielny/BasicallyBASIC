''
Tests functions. Though they are statements, they are large enough to test on
their own. Once again not exhaustive, but it tests all the different types of
functions. It also tests recursion and early returns.
''

' Get our testing function from the library, we set count (variable to track tests results) to the result of the Test
'       function. Can't change global variables inside scope, only access them so we have to set count to the result of
'       of the return.
RUN("libTest.bb")

PRINT("Function Tests"); PRINTLN("")

' Function definition
DEF Add(a, b)
    RETURN a + b
END

' Function call
LET count = Test("Add(3, 4)", Add(3, 4), 7)

' Function call with expression
LET count = Test("Add(3 + 4, 5)", Add(3 + 4, 5), 12)

' Function call with function call
LET count = Test("Add(Add(3, 4), 5)", Add(Add(3, 4), 5), 12)

' Recursive function
DEF Factorial(n)
    IF n == 0 THEN
        RETURN 1
    ELSE
        RETURN n * Factorial(n - 1)
    END
END
LET count = Test("Factorial(5)", Factorial(5), 120)


' Function with no return

DEF NoReturn()
    PRINT("NoReturn() executed")
END
LET count = Test("NoReturn()", NoReturn(), NULL)

' Returning a function
DEF ReturnFunction()
    RETURN Add
END
LET count = Test("(ReturnFunction())(3, 4)", (ReturnFunction())(3, 4), 7)


' Nested function
DEF Outer()
    DEF Inner()
        PRINTLN("Inner() executed")
    END
    PRINTLN("Outer() executed")
    Inner()
END
LET count = Test("Outer()", Outer(), NULL)

' Calling other functions defined previously in function body
DEF Other()
    RETURN Add(3, 4)
END
LET count = Test("Other(), calling Add(3, 4) inside", Other(), 7)

' Returning early
DEF EarlyReturn()
    RETURN 5
    PRINT("This should not be printed")
END
LET count = Test("EarlyReturn()", EarlyReturn(), 5)

' Nested if in function
DEF NestedIf()
    IF TRUE THEN
        IF TRUE THEN
            RETURN TRUE
        END
    END
END
LET count = Test("NestedIf()", NestedIf(), TRUE)

PrintResults()