//
// TextAreaOutputStream - write output to a text area

import java.awt.TextArea;
import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream
{
    //
    // TextAreaOutputStream public constructor

    public TextAreaOutputStream (TextArea target)
    {
        _target = target;
    }

    //
    // TextAreaOutputStream public member functions

    public void flush () throws IOException
    {
        _target.appendText(_line.toString());
        _line = new StringBuffer();
    }

    public void write (int b) throws IOException
    {
        _line.append((char)b);

        if (b == '\n') {
            _target.appendText(_line.toString());
            _line = new StringBuffer();
        }
    }

    public void write (byte[] b) throws IOException
    {
        _target.appendText(new String(b, 0));
    }

    public void write (byte[] b, int off, int len) throws IOException
    {
        _target.appendText(new String(b, 0, off, len));
    }

    //
    // TextAreaOutputStream protected data members

    TextArea _target;
    StringBuffer _line = new StringBuffer();
}
