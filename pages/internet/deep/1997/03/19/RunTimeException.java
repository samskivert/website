//
// RunTimeException -

public class RunTimeException extends Exception
{
    //
    // RunTimeException public data members

    Object sexp;

    //
    // RunTimeException public constructor

    RunTimeException (String msg, Object sexp)
    {
        super(msg);
        this.sexp = sexp;
    }
}
