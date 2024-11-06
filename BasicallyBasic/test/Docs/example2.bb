' Like in example 1 we can assign a list to a variable
LET list_val = [1, 2, 3, 4, 5]

PRINTLN("Showing the list: ")
PRINTLN(list_val)

' We can access an element in the list by using a colon :, using 0-based index
PRINTLN("Element at index 0: ")
PRINTLN(list_val:0)
' We can also use the APPEND built-in method to add items to a list
LET list_val = APPEND(list_val, 10)
' And remove and element from a list with the built-in method REMOVE
LET list_val = REMOVE(list_val, 0)

''
There are built-in methods for lists, and I encourage you to look at the docs
for more information on them
''

PRINTLN("Showing the list after appending/removing an element: ")
PRINTLN(list_val)

' And we can also access a nested list
LET list_val2 = [1, [2, 3]]
PRINTLN("Showing the nested list access: ")
PRINT(list_val2:1:0)

PRINTLN("")
''
Strings operate on the same principle as lists for access, however they don't
have the same built-in functions
''
LET string_val = "Hello, World!"
PRINTLN("Showing the string: ")
PRINTLN(string_val)

PRINTLN("String at index 0: ")
PRINTLN(string_val:0)

''
We also have a ranged access operator for lists and strings, using '|'. This
is an exclusive range, so list:0|2 will return the elements at index 0 and 1
''
PRINTLN("Showing the ranged access of the list: ")
PRINTLN(list_val:0|2)
PRINTLN("Showing the ranged access of the string: ")
PRINTLN(string_val:0|5)
