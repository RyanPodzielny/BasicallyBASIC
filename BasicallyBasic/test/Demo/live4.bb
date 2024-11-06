''
Lets do a binary search on a sorted list

Algorithm:
1. Set low to 0 and high to the length of the list - 1
2. While low is less than or equal to high
    a. Calculate the mid point
    b. If the mid point is the target, return the index
    c. If the mid point is less than the target, set low to mid + 1
    d. If the mid point is greater than the target, set high to mid - 1
3. Return -1 if the target is not found
''

DEF binary_search(a_list, target)
    LET low = 0
    LET high = LENGTH(a_list) - 1
    ' While low is less than or equal to high
    WHILE low <= high THEN
        ' Calculate the mid point
        LET mid = low + (high - low) / 2

        ' If the mid point is the target, return the index
        IF a_list:mid == target THEN
            RETURN mid

        ' If the mid point is less than the target, set low to mid + 1
        ELSEIF a_list:mid < target THEN
            LET low = mid + 1

        ' If the mid point is greater than the target, set high to mid - 1
        ELSE
            LET high = mid - 1
        END
    END

    ' Return -1 if the target is not found
    RETURN -1
END

' Lets test the binary search
LET list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
LET target = 5
LET index = binary_search(list, target)
PRINTLN("The index of " + TO_STRING(target) + " is " + TO_STRING(index))

LET target = 11
LET index = binary_search(list, target)
PRINTLN("The index of " + TO_STRING(target) + " is " + TO_STRING(index))