//
// Bprintln - calls print and then outputs a newline

import java.io.PrintStream;

import java.util.Vector;

public class Bprintln extends Bprint
{
    //
    // Bprintln public member functions

    public Object evaluate (Interpreter interp, Vector sexp)
        throws RunTimeException
    {
        Object v = super.evaluate(interp, sexp);
        _out.println("");
        return v;
    }
}
