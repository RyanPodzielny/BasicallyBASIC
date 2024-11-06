''
Library to test the RUN command. And for some more testing.
''

' Store count as [passed, failed]
LET count = [0, 0]

' Helper function to test built-in functions
' a_message: The message of the test
' a_result: The result of the function
' a_expectedResult: The expected result of the function
' RETURN: [passed, failed]
DEF Test(a_message, a_result, a_expectedResult)
    PRINTLN("")

    ' Print the test results
    PRINT("Testing "); PRINTLN(a_message)
    PRINT("Result: "); PRINTLN(a_result)
    PRINT("Expected Result: "); PRINTLN(a_expectedResult)

    ' Check if the result is equal to the expected result
    PRINT("PASSED: ")
    IF a_result == a_expectedResult THEN
        PRINTLN("✅")
        RETURN [count:0 + 1, count:1]
    ELSE
        PRINTLN("❌")
        RETURN [count:0, count:1 + 1]
    END

END


' Print out the results
DEF PrintResults()
    PRINTLN("")
    PRINTLN("Results: ")
    PRINT("Passed: "); PRINTLN(count:0)
    PRINT("Failed: "); PRINTLN(count:1)
END