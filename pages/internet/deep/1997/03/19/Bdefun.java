//
// Bdefun - built in to define functions

import java.util.Hashtable;
import java.util.Vector;

public class Bdefun extends Function
{
    //
    // Bdefun public member functions

    public Object evaluate (Interpreter interp, Vector sexp)
        throws RunTimeException
    {
        try {
            String name = ((Name)sexp.firstElement()).toString();
            Vector args = (Vector)sexp.elementAt(1);

            // first check the args for validity. should be a list of vars
            for (int i = 0; i < args.size(); i++) {
                if (!(args.elementAt(i) instanceof Name)) {
                    throw new RunTimeException("Argument #" + i +
                                               " of defun not valid.",
                                               args.elementAt(i));
                }
            }

            // create a new user function with this stuff
            UserFunction fn = new UserFunction(name, args,
                                               Interpreter.ccdr(sexp));
            // System.out.println("Binding function: " + name);
            interp.env.put(name, fn);

            return new Nil();

        } catch (ClassCastException cce) {
            throw new RunTimeException(cce.toString(), sexp);
        }
    }

    public void verifyArguments (Vector sexp) throws RunTimeException
    {
        switch (sexp.size()) {
        case 0:
            throw new RunTimeException("Missing function name for defun.",
                                       sexp);

        case 1:
            throw new RunTimeException("Missing argument list for defun.",
                                       sexp);

        case 2:
            throw new RunTimeException("Missing function body for defun.",
                                       sexp);
        }
    }
}
