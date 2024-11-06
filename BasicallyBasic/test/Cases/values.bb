''
Tests all values (integers, floats, strings, booleans, lists, and null) and assignment.
''

' Get our testing function from the library, we set count (variable to track tests results) to the result of the Test
'       function. Can't change global variables inside scope, only access them so we have to set count to the result of
'       of the return.
RUN("libTest.bb")



PRINTLN(""); PRINT("Value Tests"); PRINTLN("")

' Integer
LET count = Test("INTEGER 5", 5, 5)
LET count = Test("INTEGER -5", -5, -5)

' Float
LET count = Test("FLOAT 5.5", 5.5, 5.5)
LET count = Test("FLOAT -5.5", -5.5, -5.5)

' Boolean
LET count = Test("BOOLEAN TRUE", TRUE, TRUE)
LET count = Test("BOOLEAN FALSE", FALSE, FALSE)

' String
LET count = Test("STRING \"Hello\"", "Hello", "Hello")
LET count = Test("STRING \"Hello, World\"", "Hello, World", "Hello, World")

' List
LET count = Test("LIST [1, 2, 3]", [1, 2, 3], [1, 2, 3])
LET count = Test("LIST []", [], [])

' Simple access
LET list = [1, 2, 3]
LET string = "Hello, World"
LET count = Test("Access list:0", list:0, 1)
LET count = Test("Access list:1", list:1, 2)
LET count = Test("Access list:2", list:2, 3)
LET count = Test("Access string:0", string:0, "H")
LET count = Test("Access string:7", string:7, "W")
LET count = Test("Access string:11", string:11, "d")

' Ranged Access
LET count = Test("Ranged Access list:0|2", list:0|2, [1, 2])
LET count = Test("Ranged Access list:1|2", list:1|2, [2])
LET count = Test("Ranged Access string:0|5", string:0|5, "Hello")
LET count = Test("Ranged Access string:7|12", string:7|12, "World")

' NULL
LET count = Test("NULL", NULL, NULL)

' Assignment
LET count = Test("Assignment LET a = 5", LET a = 5, a)
LET count = Test("Assignment LET b = 5.5", LET b = 5.5, b)
LET count = Test("Assignment LET c = TRUE", LET c = TRUE, c)
LET count = Test("Assignment LET d = \"Hello\"", LET d = "Hello", d)
LET count = Test("Assignment LET e = [1, 2, 3]", LET e = [1, 2, 3], e)
LET count = Test("Assignment LET f = NULL", LET f = NULL, f)
LET count = Test("Assignment LET g", LET g, NULL)

' Assigning functions
LET count = Test("Function PRINT", LET print = PRINT, print)

PrintResults()