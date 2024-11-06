'' 
Everything we do in the CLI we can do in a file!

Lets calculate the letter grade based on a score
''

' Or calcualte the letter grade based on input
DEF letter_grade(score)
    IF NOT IS_INTEGER(score) THEN
        PRINTLN("Incorrect input, score must be an integer!")
        RETURN
    END

    IF score >= 90 THEN
        RETURN "A"
    ELSEIF score >= 80 THEN
        RETURN "B"
    ELSEIF score >= 70 THEN
        RETURN "C"
    ELSEIF score >= 60 THEN
        RETURN "D"
    ELSE
        RETURN "F"
    END
END

PRINTLN("Enter your score to get your letter grade")
LET input = INPUT()
LET grade = letter_grade(TO_INTEGER(input))
PRINTLN("Based on your score of " + input + ", you got a " + grade)