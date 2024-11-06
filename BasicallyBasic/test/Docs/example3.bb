LET list_val = [2, 3, 4, 5, 10]

' We can access a lists/strings length with the LENGTH() function
LET length = LENGTH(list_val)
LET result = 0
''
Printing a list can be done with a for loop. We iterate from the assignment
start to the end value (after keyword TO) and step by the value after the
keyword STEP. This is inclusive of the end value, and only only be used with
INTEGERs.
''
FOR LET i = 0 TO length - 1 STEP 1 THEN
    LET result = result + list_val:i
END

PRINTLN("Showing the result of the for loop: ")
PRINTLN(result)

' We can also use a while loop to run on conditional statements
LET int_val = 10
WHILE int_val > 5 THEN
    LET int_val = int_val - 1
END
PRINTLN("Showing the result of the while loop: ")
PRINTLN(int_val)

' For loops and whiles also have the BREAK and CONTINUE keywords
FOR LET i = 0 TO 2 STEP 1 THEN
    IF (i == 1) THEN
        BREAK
    END
END
PRINTLN("Showing the result of the for loop with break:")
PRINTLN(i)

' Now for continue
LET i = 0
WHILE i < 3 THEN
    LET i = i + 1
    IF (i == 2) THEN
        CONTINUE
    END
END
PRINTLN("Showing the result of the while loop with continue:")
PRINTLN(i)