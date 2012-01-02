//
// Bwhile - built in to perform if comparison

import java.util.Vector;

public class Bwhile extends Function
{
    //
    // Bwhile public member functions

    public Object evaluate (Interpreter interp, Vector sexp)
        throws RunTimeException
    {
        try {
            Vector body = Interpreter.cdr(sexp);
            Integer cond = (Integer)interp.evaluateSExp(sexp.firstElement());

            while (cond.intValue() != 0) {
                interp.interpret(body);
                cond = (Integer)interp.evaluateSExp(sexp.firstElement());
            }

            return new Nil();

        } catch (ClassCastException cce) {
            throw new RunTimeException("Non-integer type used for " +
                                       "while expression.", sexp);
        }
    }

    public void verifyArguments (Vector sexp) throws RunTimeException
    {
        if (sexp.size() < 2)
            throw new RunTimeException("Incorrect number of arguments " +
                                       "to while.", sexp);
    }
}
