//
// Bexample - built in example

import java.util.Vector;

public class Bexample extends Function
{
    //
    // Bexample public member functions

    public Object evaluate (Interpreter interp, Vector sexp)
        throws RunTimeException
    {
        return new Nil();
    }

    public int numArguments ()
    {
        return 0;
    }

    public void verifyArguments (Vector sexp) throws RunTimeException
    {
        super.verifyArguments(sexp);
    }
}
