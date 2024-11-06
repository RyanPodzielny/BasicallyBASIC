''
Lets do some operations on the 2D list
''
LET list = [[10, 10, 10, 10, 10], 
            [10, 10, 10, 10], 
            [10, 10, 9],
            [],
            [10, 10]]

DEF sum_no_nines(a_2d_list)
    LET sum = 0
    ' Go through each outer list
    FOR LET i = 0 TO LENGTH(a_2d_list) - 1 STEP 1 THEN

        ' Perhaps a break? Lets break after we encounter an empty list
        IF LENGTH(a_2d_list:i) == 0 THEN
            BREAK
        END

	    ' Go through each element in the inner and add
        FOR LET j = 0 TO LENGTH(a_2d_list:i) - 1 STEP 1 THEN
            ' Lets see some continue action        
            IF a_2d_list:i:j == 9 THEN
                CONTINUE
            END
            LET sum = sum + a_2d_list:i:j
            
        END

    END

    RETURN sum
END


PRINTLN("The sum of the 2D list is: " + TO_STRING(sum_no_nines(list)))

