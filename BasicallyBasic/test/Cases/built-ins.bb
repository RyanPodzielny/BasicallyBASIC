''
Tests all the built-in functions within the language. Also will test built-in value keywords. Obviously not exhaustive,
but it tests every built-in function with various types of arguments.
''

' Get our testing function from the library, we set count (variable to track tests results) to the result of the Test
'       function. Can't change global variables inside scope, only access them so we have to set count to the result of
'       of the return.
RUN("libTest.bb")

PRINT("Built-in Functions Tests"); PRINTLN("")


' IS_INTEGER function
LET count = Test("IS_INTEGER(5)", IS_INTEGER(5), TRUE)
LET count = Test("IS_INTEGER(5.5)", IS_INTEGER(5.5), FALSE)
LET count = Test("IS_INTEGER(TRUE)", IS_INTEGER(TRUE), FALSE)
LET count = Test("IS_INTEGER([1, 2, 3])", IS_INTEGER([1, 2, 3]), FALSE)
LET count = Test("IS_INTEGER(NULL)", IS_INTEGER(NULL), FALSE)
LET count = Test("IS_INTEGER(\"5\")", IS_INTEGER("5"), FALSE)

' IS_FLOAT function
LET count = Test("IS_FLOAT(5)", IS_FLOAT(5), FALSE)
LET count = Test("IS_FLOAT(5.5)", IS_FLOAT(5.5), TRUE)
LET count = Test("IS_FLOAT(TRUE)", IS_FLOAT(TRUE), FALSE)
LET count = Test("IS_FLOAT([1, 2, 3])", IS_FLOAT([1, 2, 3]), FALSE)
LET count = Test("IS_FLOAT(NULL)", IS_FLOAT(NULL), FALSE)
LET count = Test("IS_FLOAT(\"5.5\")", IS_FLOAT("5.5"), FALSE)

' IS_NUMBER function
LET count = Test("IS_NUMBER(5)", IS_NUMBER(5), TRUE)
LET count = Test("IS_NUMBER(5.5)", IS_NUMBER(5.5), TRUE)
LET count = Test("IS_NUMBER(TRUE)", IS_NUMBER(TRUE), FALSE)
LET count = Test("IS_NUMBER([1, 2, 3])", IS_NUMBER([1, 2, 3]), FALSE)
LET count = Test("IS_NUMBER(NULL)", IS_NUMBER(NULL), FALSE)
LET count = Test("IS_NUMBER(\"5\")", IS_NUMBER("5"), FALSE)

' IS_STRING function
LET count = Test("IS_STRING(\"Hello\")", IS_STRING("Hello"), TRUE)
LET count = Test("IS_STRING(5)", IS_STRING(5), FALSE)
LET count = Test("IS_STRING(5.5)", IS_STRING(5.5), FALSE)
LET count = Test("IS_STRING(TRUE)", IS_STRING(TRUE), FALSE)
LET count = Test("IS_STRING([1, 2, 3])", IS_STRING([1, 2, 3]), FALSE)
LET count = Test("IS_STRING(NULL)", IS_STRING(NULL), FALSE)

' IS_BOOLEAN function
LET count = Test("IS_BOOLEAN(TRUE)", IS_BOOLEAN(TRUE), TRUE)
LET count = Test("IS_BOOLEAN(FALSE)", IS_BOOLEAN(FALSE), TRUE)
LET count = Test("IS_BOOLEAN(5)", IS_BOOLEAN(5), FALSE)
LET count = Test("IS_BOOLEAN(5.5)", IS_BOOLEAN(5.5), FALSE)
LET count = Test("IS_BOOLEAN(\"Hello\")", IS_BOOLEAN("Hello"), FALSE)
LET count = Test("IS_BOOLEAN([1, 2, 3])", IS_BOOLEAN([1, 2, 3]), FALSE)
LET count = Test("IS_BOOLEAN(NULL)", IS_BOOLEAN(NULL), FALSE)

' IS_LIST function
LET count = Test("IS_LIST([1, 2, 3])", IS_LIST([1, 2, 3]), TRUE)
LET count = Test("IS_LIST([])", IS_LIST([]), TRUE)
LET count = Test("IS_LIST(5)", IS_LIST(5), FALSE)
LET count = Test("IS_LIST(5.5)", IS_LIST(5.5), FALSE)
LET count = Test("IS_LIST(TRUE)", IS_LIST(TRUE), FALSE)
LET count = Test("IS_LIST(\"Hello\")", IS_LIST("Hello"), FALSE)
LET count = Test("IS_LIST(NULL)", IS_LIST(NULL), FALSE)

' IS_FUNCTION function
LET count = Test("IS_FUNCTION(PRINT)", IS_FUNCTION(PRINT), TRUE)
LET count = Test("IS_FUNCTION(5)", IS_FUNCTION(5), FALSE)
LET count = Test("IS_FUNCTION(5.5)", IS_FUNCTION(5.5), FALSE)
LET count = Test("IS_FUNCTION(TRUE)", IS_FUNCTION(TRUE), FALSE)
LET count = Test("IS_FUNCTION(\"Hello\")", IS_FUNCTION("Hello"), FALSE)
LET count = Test("IS_FUNCTION([1, 2, 3])", IS_FUNCTION([1, 2, 3]), FALSE)
LET count = Test("IS_FUNCTION(NULL)", IS_FUNCTION(NULL), FALSE)

' IS_NULL function
LET count = Test("IS_NULL(NULL)", IS_NULL(NULL), TRUE)
LET count = Test("IS_NULL(5)", IS_NULL(5), FALSE)
LET count = Test("IS_NULL(5.5)", IS_NULL(5.5), FALSE)
LET count = Test("IS_NULL(TRUE)", IS_NULL(TRUE), FALSE)
LET count = Test("IS_NULL(\"Hello\")", IS_NULL("Hello"), FALSE)

' TO_INTEGER function
LET count = Test("TO_INTEGER(\"5\")", TO_INTEGER("5"), 5)
LET count = Test("TO_INTEGER(5.5)", TO_INTEGER(5.5), 5)
LET count = Test("TO_INTEGER(5)", TO_INTEGER(5), 5)

' TO_FLOAT function
LET count = Test("TO_FLOAT(\"5.5\")", TO_FLOAT("5.5"), 5.5)
LET count = Test("TO_FLOAT(5)", TO_FLOAT(5), 5.0)
LET count = Test("TO_FLOAT(1.0)", TO_FLOAT(1.0), 1.0)

' TO_STRING function
LET count = Test("TO_STRING(5)", TO_STRING(5), "5")
LET count = Test("TO_STRING(5.5)", TO_STRING(5.5), "5.5")
LET count = Test("TO_STRING(TRUE)", TO_STRING(TRUE), "TRUE")
LET count = Test("TO_STRING([1, 2, 3])", TO_STRING([1, 2, 3]), "[1, 2, 3]")
LET count = Test("TO_STRING(NULL)", TO_STRING(NULL), "NULL")
LET count = Test("TO_STRING(PRINT)", TO_STRING(PRINT), "FUNCTION 'PRINT'")

' APPEND function
LET count = Test("APPEND([1, 2], 3)", APPEND([1, 2], 3), [1, 2, 3])
LET count = Test("APPEND([], 1)", APPEND([], 1), [1])

' PREPEND function
LET count = Test("PREPEND([2, 3], 1)", PREPEND([2, 3], 1), [1, 2, 3])
LET count = Test("PREPEND([], 1)", PREPEND([], 1), [1])

' INSERT function
LET count = Test("INSERT([1, 3], 1, 2)", INSERT([1, 3], 1, 2), [1, 3, 1])
LET count = Test("INSERT([1, 2], 3, 2)", INSERT([1, 2], 3, 2), [1, 2, 3])

' POP function
LET count = Test("POP([1, 2, 3])", POP([1, 2, 3]), [1, 2])
LET count = Test("POP([1])", POP([1]), [])

' REMOVE function
LET count = Test("REMOVE([1, 2, 3], 1)", REMOVE([1, 2, 3], 1), [1, 3])
LET count = Test("REMOVE([1, 2, 3], 2)", REMOVE([1, 2, 3], 2), [1, 2])

' EXTEND function
LET count = Test("EXTEND([1, 2], [3, 4])", EXTEND([1, 2], [3, 4]), [1, 2, 3, 4])
LET count = Test("EXTEND([], [1, 2])", EXTEND([], [1, 2]), [1, 2])

' LENGTH function
LET count = Test("LENGTH([1, 2, 3])", LENGTH([1, 2, 3]), 3)
LET count = Test("LENGTH([])", LENGTH([]), 0)


LET message = "Hello, World!"
LET path = "test.txt"

' WRITE function
LET count = Test("WRITE(" + path  + ", " + message + ")", WRITE(path, message), NULL)

' READ function
LET count = Test("READ(" + path + ")", READ(path), message)

' INPUT function
PRINTLN("")
PRINTLN("Testing INPUT function, please enter a value:")
LET count = Test("INPUT()", LET in = INPUT(), in)

LET path = "libRUN.bb"
' RUN function
LET count = Test("RUN(" + path + ")", RUN(path), NULL)
LET count = Test("From libRUN.bb: add(1, 2)", add(1, 2), 3)


' PRINT and PRINTLN functions have clearly been tested in libTest.bb

' Show how many we passed and failed (should be all passed)
PrintResults()