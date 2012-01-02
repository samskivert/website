//
// ParseException - an exception to indicate problems with parsing

public class ParseException extends Exception
{
    //
    // ParseException public constructor

    public ParseException (String msg, int lineno, String token)
    {
        super(msg);
        _lineno = lineno;
        _token = token;
    }

    //
    // ParseException public member functions

    public int lineNumber ()
    {
        return _lineno;
    }

    public String token ()
    {
        return _token;
    }

    //
    // ParseException protected data members

    int _lineno;

    String _token;
}
