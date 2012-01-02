//
// Scanner -

import java.io.InputStream;
import java.io.IOException;
import java.io.StreamTokenizer;

public class Scanner extends StreamTokenizer
{
    //
    // Scanner public constructor

    public Scanner (InputStream is)
    {
        super(is);

        // initialize the stream tokenizer for lisp
        commentChar(';');
        parseNumbers();
        quoteChar('"');
        ordinaryChar('\'');
        wordChars('_', '_');
    }
}
