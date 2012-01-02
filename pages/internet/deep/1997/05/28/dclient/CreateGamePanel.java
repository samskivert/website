//
// CreateGamePanel - a little popup that allows a user to create a game
//
// This code is Copyright (C) 1997, go2net Inc. Permission is granted for
// any use so long as this header remains intact.
//
// Originally published in Deep Magic:
//     <URL:http://www.go2net.com/internet/deep/>
//
// The code herein is provided to you as is, without any warranty of any
// kind, including express or implied warranties, the warranties of
// merchantability and fitness for a particular purpose, and
// non-infringement of proprietary rights.  The risk of using this code
// remains with you.

package dclient;

import java.awt.*;

class Labeler extends Panel
{
    public Labeler (String label, Component c, int pw)
    {
        setLayout(new BorderLayout());
        add("West", new Label(label));
        add("Center", c);
        _pw = pw;
    }

    public Dimension preferredSize ()
    {
        Dimension ps = super.preferredSize();
        ps.width = _pw;
        return ps;
    }

    int _pw;
}

public class CreateGamePanel extends Frame
{
    //
    // CreateGamePanel public constructor

    public CreateGamePanel ()
    {
        super("Create a new game");

        _players.addItem("1");
        _players.addItem("2");
        _players.addItem("3");
        _players.addItem("4");
        _players.addItem("5");
        _players.addItem("6");
        _players.select(1);

        setLayout(new FlowLayout());

        add(new Labeler("Name", _name, 240));
        add(new Labeler("Players", _players, 240));
        add(_create);
        add(_cancel);

        pack();
    }

    //
    // CreateGamePanel public member functions

    public String getName ()
    {
        return _name.getText() + " (" + getPlayers() + ")";
    }

    public int getPlayers ()
    {
        return _players.getSelectedIndex() + 1;
    }

    public synchronized boolean run ()
    {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension bd = size();
        move((d.width-bd.width)/2, (d.height-bd.height)/2);
        show();
        try { wait(); } catch (InterruptedException e) {}
        dispose();
        return _success;
    }

    public boolean handleEvent (Event evt)
    {
        switch (evt.id) {
        case Event.WINDOW_DESTROY:
            synchronized (this) { notify(); }
            break;

        case Event.ACTION_EVENT:
            if (evt.target == _create) {
                if (_name.getText().length() == 0) break;
                _success = true;
                synchronized (this) { notify(); }
            } else if (evt.target == _cancel) {
                synchronized (this) { notify(); }
            }
            break;
        }

        return super.handleEvent(evt);
    }

    public Dimension preferredSize ()
    {
        Dimension d = new Dimension();
        Insets i = insets();
        d.width = 256 + i.left + i.right;
        d.height = _name.preferredSize().height + 5 +
            _players.preferredSize().height + 5 +
            _create.preferredSize().height + 10 + i.top + i.bottom;
        return d;
    }

    //
    // CreateGamePanel protected data members

    boolean _success = false;

    TextField _name = new TextField("A plain game");
    Choice _players = new Choice();

    Button _create = new Button("Create");
    Button _cancel = new Button("Cancel");
}
