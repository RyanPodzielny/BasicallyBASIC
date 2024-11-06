///
/// Index Node - Represents an index access of a variable, list or string. Is created from '<identifier>:<index>' which
///     is a normal index access, or '<identifier>:<start-index>|<end-index>' which is a ranged access. Responsible for
///     holding the atom being accessed, the index being accessed and whether it is a ranged access. Normal index gets
///     the element from the string/list (or accessee), while ranged access is getting a range of elements from the
///     accessee.
///

package AbstractSyntaxTree.Expressions;
import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;
import Lexer.Token;


public class Index extends Node {
    /* PUBLIC */

    /** Constructors **/

    // Not using polymorphism because the two constructors are different enough that it would be confusing to have
    //      multiple inherited classes for a simple data store. If it were to get more complex, then it would make sense
    //      to do this, but small classes like this are better off with simple constructors.

    /**/
    /*
    AbstractSyntaxTree.Expressions.Index.Index()

    NAME

        Index - constructor for the index node (normal index access).

    SYNOPSIS

        Index(Node a_accessee, Token a_token, Node a_index);
            a_accessee  -> the atom being accessed, e.g. the variable name,
                list or string.
            a_token     -> the ':' operator token (used for reporting errors).
            a_index     -> the subscript value of where to access, e.g. the
                index in a list.

    DESCRIPTION

        Initializes the accessee, token, and index of the index access. Used for
        creating index nodes with no ranged access. For example 'list:1' would
        be an index access with 'list' as the accessee, ':' as the operator
        token, and '1' as the index.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Index(Node a_accessee, Token a_token, Node a_index) {
        m_accessee = a_accessee;
        m_token = a_token;

        m_index = a_index;

        // Make start and end indices same as index, so they are not null
        m_isRanged = false;
        m_startIndex = a_index;
        m_endIndex = a_index;
    }
    /* Index(Node a_accessee, Token a_token, Node a_index); */

    /**/
    /*
    AbstractSyntaxTree.Expressions.Index.Index()

    NAME

        Index - constructor for the index node (ranged index access).

    SYNOPSIS

        Index(Node a_accessee, Token a_token, Node a_startIndex, Node a_endIndex);
            a_accessee      -> the atom being accessed, e.g. the variable name,
                list or string.
            a_token         -> the ';' operator token (used for reporting
                errors).
            a_startIndex    -> the starting index of where to access from.
            a_endIndex      -> the ending index of where to access to.

    DESCRIPTION

        Initializes the accessee, token, start index, and end index of the index
        access. Used for creating index nodes with ranged access. For example
        'list:1|3' would be an index access with 'list' as the accessee, ':' as
        the operator token, '1' as the start index, and '3' as the end index.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public Index(Node a_accessee, Token a_token, Node a_startIndex, Node a_endIndex) {
        m_accessee = a_accessee;
        m_token = a_token;

        m_isRanged = true;
        m_startIndex = a_startIndex;
        m_endIndex = a_endIndex;

        // Make index same as start index, so it is not null
        m_index = a_startIndex;
    }
    /* Index(Node a_accessee, Token a_token, Node a_startIndex,
             Node a_endIndex); */

    
    /** Accessors **/

    // Get the atom being accessed, e.g. the variable name, list or string
    public Node GetAccessee() {
        return m_accessee;
    }

    // Get the operator token of the index access
    public Token GetToken() {
        return m_token;
    }

    // Check if the index access is ranged
    public boolean IsRanged() {
        return m_isRanged;
    }

    // Get the index value of where to access, if not ranged
    public Node GetIndex() {
        ConfirmIsNotRanged();
        return m_index;
    }

    // Get the start index of where to access, if ranged
    public Node GetStartIndex() {
        ConfirmIsRanged();
        return m_startIndex;
    }

    // Get the end index of where to access, if ranged
    public Node GetEndIndex() {
        ConfirmIsRanged();
        return m_endIndex;
    }
    
    
    /** Methods **/

    // Accept a visitor and have it visit the index expression
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitIndex(this);
    }


    /* PRIVATE */

    /** Members **/
    
    private final Node m_accessee;      // The atom being accessed, e.g. the variable name, list or string
    private final Token m_token;        // The ':' or '|' operator token (used for reporting errors)

    private final Node m_index;         // The subscript value of where to access, e.g. the index in a list

    private final Node m_startIndex;    // For ranged access, the starting index of where to access from
    private final Node m_endIndex;      // For ranged access, the ending index of where to access to
    private final boolean m_isRanged;   // Whether we are accessing a range of values


    /** Private Helper Function **/

    /*
     *      Guard Clause - Check if the index is ranged
     */

    // Make sure to not improperly access the index
    private void ConfirmIsRanged() {
        assert m_isRanged : "Accessor is not ranged, use GetIndex() instead";
    }

    // Make sure you use start and end indices for ranged access
    private void ConfirmIsNotRanged() {
        assert !m_isRanged : "Accessor is ranged, use GetStartIndex() and GetEndIndex() instead";
    }

}