'' 
Calculating fibonacci using recursion and iteration

Formula = Fn = Fn-1 + Fn-2, where n >= 0
            if n = 0, result = 0
            if n = 2, 3, result = 1
''

' Get the users input and cast to an integer
PRINTLN("Enter you input value for Fibonacci")
LET n = TO_INTEGER(INPUT()) - 1

' Fibonacci with recursion
'   n -> input number to get to
DEF fib(n) 
    ' Formula doesn't allow for values less than 0
    IF n < 0 THEN
        PRINTLN("Incorrect input, n must be larger than 0!")
    ' Base case, 1 or 2 means result is 1
    ELSEIF n < 2 THEN
        RETURN 1
    END
    
    ' Recursive case, fib(n-1) + fib(n-2)
    RETURN fib(n - 1) + fib(n - 2)
END

PRINTLN("Result of Fibonacci with recusrion: " + TO_STRING(fib(n)))


' Now with itertion
' Our previous numbers to add to each other
LET lhs = 0
LET rhs = 1
' Next number to calculate
LET next = rhs

' Iterate till we hit n
FOR LET count = 1 TO n STEP 1 THEN
    LET lhs = rhs
    LET rhs = next

    LET next = lhs + rhs
END
PRINTLN("Result of Fibonacci with iteration: " + TO_STRING(next))