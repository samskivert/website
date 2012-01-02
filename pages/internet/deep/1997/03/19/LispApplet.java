//
// LispApplet

import java.applet.Applet;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Event;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.TextArea;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringBufferInputStream;

import java.net.URL;
import java.net.MalformedURLException;

public class LispApplet extends Applet implements Runnable
{
    public void init ()
    {
        setLayout(new BorderLayout());

        add("North", _ctrlpanel = new Panel());
        _ctrlpanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        _ctrlpanel.add(new Button("Run"));
        _ctrlpanel.add(_choices = new Choice());
        _ctrlpanel.add(new Button("Load"));
        _ctrlpanel.add(new Button("Clear"));

        _choices.addItem("Hello World");
        _choices.addItem("Factorial");
        _choices.addItem("Towers of Hanoi");

        add("Center", _input = new TextArea());
        add("South", _output = new TextArea());

        _input.setFont(new Font("Courier", Font.PLAIN, 12));
        _output.setFont(new Font("Courier", Font.PLAIN, 12));
        _output.resize(10, 100);

        _out = new PrintStream(new TextAreaOutputStream(_output));
        Bprint.setOutputStream(_out);

        load("hello.lisp");
    }

    public boolean handleEvent (Event evt)
    {
        if (evt.id == Event.ACTION_EVENT) {
            if (evt.target instanceof Button) {
                Button b = (Button)evt.target;

                if (b.getLabel().equals("Run")) {
                    if (_runner == null) {
                        _out.println("\nRunning. Output shown below:");
                        _runner = new Thread(this);
                        _runner.start();
                    }

                } else if (b.getLabel().equals("Load")) {
                    String source = _sources[_choices.getSelectedIndex()];
                    _out.println("\nLoading: " + source);
                    load(source);
                } else if (b.getLabel().equals("Clear")) {
                    _output.setText("");
                }
            }
        }

        return super.handleEvent(evt);
    }

    public void run ()
    {
        StringBufferInputStream bin =
            new StringBufferInputStream(_input.getText());

        try {
            Parser p = new Parser(bin);
            _interp.interpret(p.parse());

        } catch (RunTimeException rte) {
            _out.println(rte);
            _out.println("SExp: " + rte.sexp);

        } catch (ParseException pe) {
            _out.println(pe);
            _out.println("  Line: " + pe.lineNumber() +
                               " Token: " + pe.token());
        }

        _runner = null;
    }

    public void load (String source)
    {
        try {
            URL sourceURL = new URL(getDocumentBase(), source);
            DataInputStream din = new
                DataInputStream(sourceURL.openStream());

            _input.setText("");
            String line;
            while ((line = din.readLine()) != null) {
                _input.appendText(line + "\n");
            }

        } catch (MalformedURLException mue) {
            _out.println("Error loading: " + source + ": " + mue);

        } catch (IOException ioe) {
            _out.println("Error loading: " + source + ": " + ioe);
        }
    }

    //
    // LispApplet protected constants

    final static String[] _sources = { "hello.lisp",
                                       "fact.lisp",
                                       "towers.lisp" };

    //
    // LispApplet protected data members

    Panel _ctrlpanel;
    Choice _choices;
    TextArea _input;
    TextArea _output;

    Interpreter _interp = new Interpreter();

    Thread _runner;

    PrintStream _out;
}
