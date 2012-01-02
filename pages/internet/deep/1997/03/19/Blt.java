//
// Blt - built in to perform comparison

import java.util.Vector;

public class Blt extends Function
{
    //
    // Blt public member functions

    public Object evaluate (Interpreter interp, Vector sexp)
        throws RunTimeException
    {
        try {
            Integer l = (Integer)interp.evaluateSExp(sexp.elementAt(0));
            Integer r = (Integer)interp.evaluateSExp(sexp.elementAt(1));

            return new Integer(l.intValue() < r.intValue() ? 1 : 0);

        } catch (ClassCastException cce) {
            throw new RunTimeException("Non-integer type used for " +
                                       "comparison expression.", sexp);
        }
    }

    public int numArguments ()
    {
        return 2;
    }
}
