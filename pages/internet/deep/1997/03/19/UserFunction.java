//
// UserFunction - a user defined function interpreted at runtime

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class UserFunction extends Function
{
    //
    // UserFunction public constructor

    public UserFunction (String name, Vector args, Vector sexps)
    {
        _name = name;
        _args = args;
        _sexps = sexps;
    }

    //
    // UserFunction public member functions

    public Object evaluate (Interpreter interp, Vector sexp)
        throws RunTimeException
    {
        Hashtable shadowed = new Hashtable();

        for (int i = 0; i < _args.size(); i++) {
            String name = ((Name)_args.elementAt(i)).toString();
            Object val = sexp.elementAt(i);

            // shadow this variable
            Object oval = interp.env.get(name);
            if (oval != null) shadowed.put(name, oval);
            else shadowed.put(name, new Nil());

            // evaluate the argument and bind it to the variable
            interp.env.put(name, interp.evaluateSExp(val));
        }

        // evaluate the sexps that constitute this function
        // System.out.println("Interpreting sexps.");
        Object result = interp.interpret(_sexps);

        // copy the values of the shadowed variables back into the environment
        Enumeration kiter = shadowed.keys();
        while (kiter.hasMoreElements()) {
            String key = (String)kiter.nextElement();
            Object oval = shadowed.get(key);
            if (oval instanceof Nil) interp.env.remove(key);
            else interp.env.put(key, oval);
        }

        return result;
    }

    public int numArguments ()
    {
        return _args.size();
    }

    public String name ()
    {
        return _name;
    }

    //
    // UserFunction protected data members

    String _name;
    Vector _args;
    Vector _sexps;
}
