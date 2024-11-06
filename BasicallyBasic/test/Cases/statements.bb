''
Tests all statements (if, while, etc.). Once again not exhaustive, but it tests all the different types of statements.
It also tests nesting and breaking out of loops.
''

' Get our testing function from the library, we set count (variable to track tests results) to the result of the Test
'       function. Can't change global variables inside scope, only access them so we have to set count to the result of
'       of the return.
RUN("libTest.bb")

''
IF STATEMENTS
''
PRINTLN(""); PRINT("If Statements"); PRINTLN("")

' Simple if
LET result
IF TRUE THEN
    LET result = TRUE
END
LET count =Test("Simple if TRUE branch", result, TRUE)

LET result = FALSE
IF FALSE THEN
    LET result = TRUE
END
LET count = Test("Simple if FALSE branch", result, FALSE)

' If-else
LET result
IF TRUE THEN
    LET result = TRUE
ELSE
    LET result = FALSE
END
LET count = Test("If-else TRUE branch", result, TRUE)

LET result
IF FALSE THEN
    LET result = TRUE
ELSE
    LET result = FALSE
END
LET count = Test("If-else FALSE branch", result, FALSE)

' Nested if
LET result
IF (TRUE) THEN
    IF (TRUE) THEN
        LET result = TRUE
    END
END
LET count = Test("Nested if TRUE branch", result, TRUE)

LET result
IF TRUE THEN
    IF (FALSE) THEN
        LET result = TRUE
    ELSE
        LET result = FALSE
    END
END
LET count = Test("Nested if FALSE branch", result, FALSE)



' If-else-if
LET result
IF FALSE THEN
    LET result = FALSE
ELSEIF TRUE THEN
    LET result = TRUE
ELSE
    LET result = FALSE
END
LET count = Test("If-else-if TRUE branch", result, TRUE)


' Multiple else-if chain
LET result
IF FALSE THEN
    LET result = FALSE
ELSEIF FALSE THEN
    LET result = FALSE
ELSEIF TRUE THEN
    LET result = TRUE
ELSE
    LET result = FALSE
END
LET count = Test("Multiple else-if chain TRUE branch", result, TRUE)


''
WHILE LOOPS
''
PRINTLN(""); PRINT("While Loops"); PRINTLN("")

' Simple while
LET i = 0
WHILE i < 3 THEN

    LET i = i + 1
END
LET count = Test("Simple while", i, 3)


' While with break
LET i = 0
WHILE (i < 3) THEN
    IF (i == 1) THEN
        BREAK
    END
    LET i = i + 1
END
LET count = Test("While with break", i, 1)

PRINTLN("")

' While with continue
LET i = 0
WHILE (i < 3) THEN
    LET i = i + 1
    IF (i == 2) THEN
        CONTINUE
    END
END
LET count = Test("While with continue", i, 3)


''
FOR LOOPS
''
PRINTLN(""); PRINT("For Loops"); PRINTLN("")

' Simple for
FOR LET i = 0 TO 2 STEP 1 THEN
    CONTINUE
END
LET count = Test("Simple for", i, 2)

' For with break

FOR LET i = 0 TO 2 STEP 1 THEN
    IF (i == 1) THEN
        BREAK
    END
END
LET count = Test("For with break", i, 1)

' For with continue
FOR LET i = 0 TO 2 STEP 1 THEN
    IF (i == 1) THEN
        CONTINUE
    END
END
LET count = Test("For with continue", i, 2)

' Nested
LET result = 0
FOR LET i = 0 TO 2 STEP 1 THEN
    FOR LET j = 0 TO 2 STEP 1 THEN
        LET result = result + i + j
    END
END
LET count = Test("Nested for", result, 18)

' Show results
PrintResults()