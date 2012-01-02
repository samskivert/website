//
// ChatPanel - a couple of text fields and a subscriber to do chat
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

import dist.DObject;
import dist.DObjectManager;
import dist.Subscriber;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

    public ChatPanel (int maxwid)
    {
        setLayout(new BorderLayout(0, 3));
        _maxwid = maxwid;
        _area.setEditable(false);
        add("Center", _area);
        add("South", _field);
    }

    //
    // ChatPanel public member functions

    public void setClient (Client client, String oid) throws IOException
    {
        _client = client;

        _area.setText("");
        _field.setText("");

        // get a handle on the client list distributed object
        _chatdo = _client.subscribeToObject(oid, this);
    }

    public synchronized boolean handleEvent (DObject dobj, dist.Event evt)
    {
        // it's important to be safe
        if ((evt.type == dist.Event.ATTR_CHANGED) &&
            (evt.name.equals("chat.text")) &&
            (evt.value != null)) addText((String)evt.value);
        return true;
    }

    public boolean handleEvent (java.awt.Event evt)
    {
        if ((evt.id == java.awt.Event.ACTION_EVENT) &&
            (evt.target == _field)) {
            try {
                _chatdo.setValue("chat.text", _client.nickname() + ": " +
                                 _field.getText());
                _field.setText("");

            } catch (IOException e) {
                System.err.println("Network error: " + e);
            }
            return true;
        }

        return super.handleEvent(evt);
    }

    // make sure we don't claim to want to be really tall
    public Dimension preferredSize ()
    {
        Dimension d = super.preferredSize();
        d.width = 150;
        d.height = 75;
        return d;
    }

    //
    // ChatPanel protected member functions

    void addText (String text)
    {
        String prefix = "";
        for (;;) {
            int idx = Math.min(text.length(), _maxwid);
            _area.appendText(prefix + text.substring(0, idx) + "\n");
            prefix = "| ";
            if (text.length() > _maxwid) text = text.substring(idx);
            else break;
        }
    }

    //
    // ChatPanel protected data members

    int _maxwid;

    Client _client;
    DObject _chatdo;

    TextArea _area = new TextArea();
    TextField _field = new TextField();
}
