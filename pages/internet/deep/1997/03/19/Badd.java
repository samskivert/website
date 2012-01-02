//
// Badd - built in to perform addition

import java.util.Vector;

public class Badd extends Function
{
    //
    // Badd public member functions

    public Object evaluate (Interpreter interp, Vector sexp)
        throws RunTimeException
    {
        try {
            int sum = 0;

            for (int i = 0; i < sexp.size(); i++) {
                Integer ival = (Integer)interp.evaluateSExp(sexp.elementAt(i));
                sum += ival.intValue();
            }

            return new Integer(sum);

        } catch (ClassCastException cce) {
            throw new RunTimeException("Non-integer type used for " +
                                       "addition expression.", sexp);
        }
    }

    public void verifyArguments (Vector sexp) throws RunTimeException
    {
        if (sexp.size() < 2)
            throw new RunTimeException("Incorrect number of arguments " +
                                       "to add.", sexp);
    }
}
