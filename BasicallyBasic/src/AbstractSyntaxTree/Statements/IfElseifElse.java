///
/// If-Elseif-Else Node - Represents an if-elseif-else chain. Is created from the parser from
///     'IF <condition> THEN <body> ELSEIF <condition> THEN <body> ELSE <body> END'. Responsible for holding the
///     conditions and bodies of the if-elseif-else chain, and will evaluate first true condition's body, or the else.
///     An else is not required, nor are the else-ifs, but there must be at least one if on creation.
///

package AbstractSyntaxTree.Statements;
import AbstractSyntaxTree.Node;
import AbstractSyntaxTree.NodeVisitor;
import Interpreter.Value;

import java.util.List;


public class IfElseifElse extends Node {
    /* PUBLIC */

    /** Constructors **/

    /**/
    /*
    AbstractSyntaxTree.Statements.IfElseifElse.IfElseifElse()

    NAME

        IfElseifElse - constructor for the if-elseif-else node.

    SYNOPSIS

        IfElseifElse(List<ConditionalField> a_conditions);
            a_conditions    -> the conditions and bodies for the ifs and
                else-ifs. Must contain at least one condition.

     DESCRIPTION

         Initializes the conditions and bodies for the if-elseif-else chain.
         This constructor is used when there is no else body, setting the
         m_hasElse flag to false and the body to null. The a_conditions list
         will be 1 if there is an if, or more than one if there are else-ifs. If
         the conditions list is empty, an assertion will be thrown.

         Easier to create another constructor for the else body than to create a
         separate Node for the else body as they are not two separate entities.
         Rather a different state of the same entity.

    RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public IfElseifElse(List<ConditionalField> a_conditions) {
        ConfirmValidField(a_conditions);
        m_conditions = a_conditions;

        // No else, so set the else body to null
        m_hasElse = false;
        m_elseBody = null;
    }
    /* IfElseifElse(List<ConditionalField> a_conditions); */

    /**/
    /*
    AbstractSyntaxTree.Statements.IfElseifElse.IfElseifElse()

    NAME

        IfElseifElse - constructor for the if-elseif-else node.

    SYNOPSIS

        IfElseifElse(List<ConditionalField> a_conditions, Body a_elseBody);
            a_conditions    -> the conditions and bodies for the ifs and
                else-ifs. Must contain at least one condition.
            a_elseBody     -> the body for the else.

     DESCRIPTION

         Initializes the conditions and bodies for the if-elseif-else chain.
         This contractor is used when a else is present on the chain, setting
         the m_hasElse flag to true and the body to a_elseBody.

     RETURNS

        Nothing - it's a constructor.

    */
    /**/
    public IfElseifElse(List<ConditionalField> a_conditions, Body a_elseBody) {
        ConfirmValidField(a_conditions);
        m_conditions = a_conditions;

        // There is an else, so set the else body
        m_hasElse = true;
        m_elseBody = a_elseBody;
    }
    /* IfElseifElse(List<ConditionalField> a_conditions, Body a_elseBody) */


    /** Accessors **/

    // Returns the conditions and bodies for the ifs and else-ifs
    public List<ConditionalField> GetConditions() {
        return m_conditions;
    }

    // Returns the body for the else
    public Body GetElseBody() {
        ConfirmHasElse();
        return m_elseBody;
    }

    // Returns whether there is an else statement attached to the chain
    public boolean HasElse() {
        return m_hasElse;
    }


    /** Methods **/

    // Accept a visitor and evaluate the if-elseif-else chain
    @Override
    public Value Accept(NodeVisitor a_visitor) {
        return a_visitor.VisitIfElseifElse(this);
    }


    /* PRIVATE */

    /** Members **/

    private final List<ConditionalField> m_conditions;      // The conditions and bodies for the ifs and else-ifs
    private final boolean m_hasElse;                        // Whether there is an else
    private final Body m_elseBody;                          // The body for the else (if there is one)


    /** Private Helper Functions **/

    /*
     *      Confirm Conditional State - So developers don't do something they shouldn't
     */

    // Confirm that we have at the very least an if condition
    private void ConfirmValidField(List<ConditionalField> a_conditions) {
        assert !a_conditions.isEmpty() : "Conditional must have at least one condition";
    }

    // Confirm that there is an else body before trying to access it
    private void ConfirmHasElse() {
        assert HasElse() : "No else body exists for this conditional";
    }

}