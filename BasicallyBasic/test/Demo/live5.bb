''
Lets reverse a composite value
''

' We're going to use an external library for this one with the 'RUN' command
RUN("reverse.bb")


' Works on strings
LET duck = "quack"
LET str = "Reversed string " + duck + ": " + reverse_string(duck)


' Now lets write it to a file
WRITE("reversed.txt", str + "\n We just wrote it to a file")

' And print it out what we've read
PRINTLN(READ("reversed.txt"))