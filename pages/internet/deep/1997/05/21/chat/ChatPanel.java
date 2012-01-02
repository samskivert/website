//
// ChatPanel -

package chat;

import dist.DObject;
import dist.DObjectManager;
import dist.Subscriber;
import dclient.Client;

import java.awt.BorderLayout;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.Panel;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class ChatPanel extends Panel implements Subscriber
{
    //
    // ChatPanel public constructor

    public ChatPanel (WhoPanel whop)
    {
        // create our user interface elements
        setLayout(new BorderLayout());

        _area = new TextArea();
        add("Center", _area);
        _field = new TextField();
        add("South", _field);

        _whop = whop;
    }

    //
    // ChatPanel public member functions

    public void setClient (Client client) throws IOException
    {
        _client = client;

        _area.setText("");
        _field.setText("");

        // get a handle on the client list distributed object
        _chatdo = _client.subscribeToObject("chat.object", this);
    }

    public synchronized boolean handleEvent (DObject dobj, dist.Event evt)
    {
        // it's important to be safe
        if (evt.value != null) _area.appendText((String)evt.value + "\n");
        return true;
    }

    public boolean handleEvent (java.awt.Event evt)
    {
        if ((evt.id == java.awt.Event.ACTION_EVENT) &&
            (evt.target == _field)) {
            try {
                _chatdo.setValue("chat.text", _whop.nickname() + ": " +
                                 _field.getText());
                _field.setText("");

            } catch (IOException e) {
                System.err.println("Network error: " + e);
            }
            return true;
        }

        return super.handleEvent(evt);
    }

    //
    // ChatPanel protected data members

    Client _client;
    DObject _chatdo;
    WhoPanel _whop;

    TextArea _area;
    TextField _field;
}
