''
Tests all expressions (binary, unary, etc.). Obviously not exhaustive, but it test every operator at least once. It also
tests grouping and precedence, so we can assume that the operators are working correctly.
''

' Get our testing function from the library, we set count (variable to track tests results) to the result of the Test
'       function. Can't change global variables inside scope, only access them so we have to set count to the result of
'       of the return.
RUN("libTest.bb")

''
ARITHMETIC OPERATORS
''
PRINT("Arithmetic Operators"); PRINTLN("")

' Addition
LET count = Test("Adding 3 + 7", 3 + 7, 10)
LET count = Test("Adding 4.2 + 5.8", 4.2 + 5.8, 10.0)
LET count = Test("Adding 6 + 3.4", 6 + 3.4, 9.4)
LET count = Test("Adding -3 + 7", -3 + 7, 4)
LET count = Test("Adding 3 + -7", 3 + -7, -4)

' Subtraction
LET count = Test("Subtracting 9 - 4", 9 - 4, 5)
LET count = Test("Subtracting 7.5 - 2.5", 7.5 - 2.5, 5.0)
LET count = Test("Subtracting 8 - 3.2", 8 - 3.2, 4.8)
LET count = Test("Subtracting -5 - 3", -5 - 3, -8)
LET count = Test("Subtracting 5 - -3", 5 - -3, 8)

' Multiplication
LET count = Test("Multiplying 2 * 5", 2 * 5, 10)
LET count = Test("Multiplying 3.5 * 2.0", 3.5 * 2.0, 7.0)
LET count = Test("Multiplying 4 * 1.5", 4 * 1.5, 6.0)
LET count = Test("Multiplying -2 * 4", -2 * 4, -8)
LET count = Test("Multiplying 2 * -4", 2 * -4, -8)

' Division
LET count = Test("Dividing 8 / 2", 8 / 2, 4.0)
LET count = Test("Dividing 9.0 / 3.0", 9.0 / 3.0, 3.0)
LET count = Test("Dividing 7 / 2.5", 7 / 2.5, 2.8)
LET count = Test("Dividing -6 / 3", -6 / 3, -2)
LET count = Test("Dividing 6 / -3", 6 / -3, -2)

' Modulus
LET count = Test("Modulus 10 % 3", 10 % 3, 1)
LET count = Test("Modulus 7.5 % 2.5", 7.5 % 2.5, 0.0)
LET count = Test("Modulus 9 % 2.5", 9 % 2.5, 1.5)
LET count = Test("Modulus -10 % 3", -10 % 3, -1)
LET count = Test("Modulus 10 % -3", 10 % -3, 1)

' Exponentiation
LET count = Test("Exponentiating 3 ^ 2", 3 ^ 2, 9)
LET count = Test("Exponentiating 2.5 ^ 3.0", 2.5 ^ 3.0, 15.625)
LET count = Test("Exponentiating 4 ^ 1.5", 4 ^ 1.5, 8.0)
LET count = Test("Exponentiating -2 ^ 3", -2 ^ 3, -8)
LET count = Test("Exponentiating 2 ^ -3", 2 ^ -3, 0)

' Grouping
LET count = Test("Grouping (3 + 4) * 2", (3 + 4) * 2, 14)
LET count = Test("Grouping 3 + (4 * 2)", 3 + (4 * 2), 11)
LET count = Test("Grouping (2 + 3) * 4 + 1", (2 + 3) * 4 + 1, 21)
LET count = Test("Grouping 2 + (3 * 4) + 1", 2 + (3 * 4) + 1, 15)
LET count = Test("Grouping 1 + 2 * (3 + 4)", 1 + 2 * (3 + 4), 15)
LET count = Test("Grouping 1 + 2 * 3 + 4", 1 + 2 * 3 + 4, 11)

PRINTLN("")

' Unary minus
LET count = Test("Unary minus -3", -3, -3)
LET count = Test("Unary minus -4.5", -4.5, -4.5)
LET count = Test("Unary minus -2 + 5", -2 + 5, 3)
LET count = Test("Unary minus -3.5 + 2.5", -3.5 + 2.5, -1.0)
LET count = Test("Unary minus -4 - 1", -4 - 1, -5)

' Unary plus
LET count = Test("Unary plus +3", +3, 3)
LET count = Test("Unary plus +4.5", +4.5, 4.5)
LET count = Test("Unary plus +2 + 5", +2 + 5, 7)
LET count = Test("Unary plus +3.5 + 2.5", +3.5 + 2.5, 6.0)
LET count = Test("Unary plus +4 + 1", +4 + 1, 5)

' Unary logical NOT
LET count = Test("Unary logical NOT TRUE", NOT TRUE, FALSE)
LET count = Test("Unary logical NOT FALSE", NOT FALSE, TRUE)


''
COMPARISON OPERATORS
''

PRINTLN(""); PRINTLN("Comparison Operators"); PRINTLN("")

' Equal
LET count = Test("3 == 3", 3 == 3, TRUE)
LET count = Test("4.5 == 4.5", 4.5 == 4.5, TRUE)
LET count = Test("3 == 4.5", 3 == 4.5, FALSE)
LET count = Test("Hello == Hello", "Hello" == "Hello", TRUE)
LET count = Test("[1, 2, 3] == [1, 2, 3]", [1, 2, 3] == [1, 2, 3], TRUE)

' Not equal
LET count = Test("3 != 4", 3 != 4, TRUE)
LET count = Test("4.5 != 5.5", 4.5 != 5.5, TRUE)
LET count = Test("3 != 4.5", 3 != 4.5, TRUE)
LET count = Test("Hello != World", "Hello" != "World", TRUE)
LET count = Test("[1, 2, 3] != [1, 2, 4]", [1, 2, 3] != [1, 2, 4], TRUE)

' Less than
LET count = Test("3 < 4", 3 < 4, TRUE)
LET count = Test("4.5 < 5.5", 4.5 < 5.5, TRUE)
LET count = Test("3 < 4.5", 3 < 4.5, TRUE)

' Less than or equal
LET count = Test("3 <= 4", 3 <= 4, TRUE)
LET count = Test("4.5 <= 5.5", 4.5 <= 5.5, TRUE)
LET count = Test("3 <= 4.5", 3 <= 4.5, TRUE)

' Greater than
LET count = Test("3 > 4", 3 > 4, FALSE)
LET count = Test("4.5 > 5.5", 4.5 > 5.5, FALSE)
LET count = Test("3 > 4.5", 3 > 4.5, FALSE)

' Greater than or equal
LET count = Test("3 >= 4", 3 >= 4, FALSE)
LET count = Test("4.5 >= 5.5", 4.5 >= 5.5, FALSE)
LET count = Test("3 >= 4.5", 3 >= 4.5, FALSE)

''
LOGICAL OPERATORS
''

PRINTLN(""); PRINTLN("Logical Operators"); PRINTLN("")

' Logical AND
LET count = Test("TRUE AND TRUE", TRUE AND TRUE, TRUE)
LET count = Test("TRUE AND FALSE", TRUE AND FALSE, FALSE)
LET count = Test("FALSE AND TRUE", FALSE AND TRUE, FALSE)
LET count = Test("FALSE AND FALSE", FALSE AND FALSE, FALSE)

' Logical OR
LET count = Test("TRUE OR TRUE", TRUE OR TRUE, TRUE)
LET count = Test("TRUE OR FALSE", TRUE OR FALSE, TRUE)
LET count = Test("FALSE OR TRUE", FALSE OR TRUE, TRUE)
LET count = Test("FALSE OR FALSE", FALSE OR FALSE, FALSE)

' Logical NOT
LET count = Test("NOT TRUE", NOT TRUE, FALSE)
LET count = Test("NOT FALSE", NOT FALSE, TRUE)

PrintResults()
