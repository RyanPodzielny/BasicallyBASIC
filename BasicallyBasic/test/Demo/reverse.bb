''
Reversing a a string
''

DEF reverse_string(a_composite)
    LET reversed = a_composite:0

    ' Loop through each element, and prepend to the reversed string
    FOR LET i = 1 TO LENGTH(a_composite) STEP 1 THEN
        LET reversed = a_composite:i + reversed
    END

    ' Return our result
    RETURN reversed
END

