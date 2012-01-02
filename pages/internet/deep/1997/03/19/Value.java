//
// Value - an object to store values (numbers, strings, etc)

import java.util.Vector;

public class Value
{
    //
    // Value public constants

    public final static int NIL      = 0;

    public final static int INTEGER   = 1;
    public final static int STRING   = 2;
    public final static int LIST     = 3;
    public final static int VARIABLE = 4;
    public final static int CALL     = 5;

    //
    // Value public data members

    int type;

    int intval;
    String strval;
    Vector lstval;

    //
    // Value public constructors

    public Value ()
    {
        type = Value.NIL;
    }

    public Value (int value)
    {
        type = INTEGER;
        intval = value;
    }

    public Value (String name, boolean quoted)
    {
        type = (quoted ? STRING : VARIABLE);
        strval = name;
    }

    public Value (int type, Vector list)
    {
        this.type = type;
        lstval = list;
    }

    public Value (Value rhs)
    {
        type = rhs.type;
        intval = rhs.intval;
        strval = rhs.strval;
        lstval = copyList(rhs.lstval);
    }

    //
    // Value public member functions

    public void setValue (Value rhs) throws RunTimeException
    {
        if (type != rhs.type)
            throw new RunTimeException("Type mismatch error.", this);

        switch (type) {
        case INTEGER:
            intval = rhs.intval;
            break;

        case STRING:
            strval = rhs.strval;
            break;

        case LIST:
            lstval = copyList(rhs.lstval);
            break;

        default:
            throw new RunTimeException("Internal Error! RHS is an " +
                                       "unevaluated sexp.", this);
        }
    }

    public String toString ()
    {
        return toString(" ");
    }

    public String toString (String seperator)
    {
        switch (type) {
        case INTEGER:
            return String.valueOf(intval);

        case STRING:
            return "\"" + strval + "\"";

        case LIST:
            return "'(" + listToString(lstval, seperator) + ")";

        case VARIABLE:
            return strval;

        case CALL:
            return "(" + strval + seperator +
                listToString(lstval, seperator) + ")";

        default:
            return "UNKNOWN: " + Integer.toString(type);
        }
    }

    //
    // Value public static member functions

    public static Vector copyList (Vector source)
    {
        if (source == null) return null;
        return copyList(source, 0, source.size());
    }

    public static Vector copyList (Vector source, int start, int length)
    {
        if (source == null) return null;

        Vector newvec = new Vector();
        for (int i = start; i < start+length; i++) {
            newvec.addElement(new Value((Value)source.elementAt(i)));
        }

        return newvec;
    }

    //
    // Value protected member functions

    String listToString (Vector list, String seperator)
    {
        String rv = "";

        for (int i = 0; i < lstval.size(); i++) {
            Value val = (Value)lstval.elementAt(i);
            if (i != 0) rv = rv.concat(seperator);
            rv = rv.concat(val.toString(seperator));
        }

        return rv;
    }
}
