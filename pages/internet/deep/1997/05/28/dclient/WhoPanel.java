//
// WhoPanel - displays a list of clients (gets info from the client DO)
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
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class WhoPanel extends Panel implements Subscriber
{
    //
    // WhoPanel public constructors

    public WhoPanel ()
    {
        // create our user interface elements
        setLayout(new BorderLayout());

        _list = new List();
        add("North", new Label("Players", Label.CENTER));
        add("Center", _list);

        _name = new TextField();
        add("South",  _name);
    }

    //
    // WhoPanel public member functions

    public void setClient (Client client) throws IOException
    {
        _client = client;

        // get a handle on the client list distributed object
        _clients = _client.subscribeToObject("cmgr.clients", this);
        _list.clear();

        // add all the currently registered clients to the list to start
        Enumeration keys = _clients.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = _clients.getValue(key, "Error");
            _list.addItem(value);
            _cnames.put(key, value);
        }
    }

    public String nickname () { return _nickname; }

    public boolean setNickname (String nickname) throws IOException
    {
        Enumeration values = _clients.elements();

        while (values.hasMoreElements()) {
            String name = (String)values.nextElement();
            if (name.equals(nickname)) return false;
        }

        _clients.setValue(_client.clientId(), nickname);
        _name.setText(_nickname = nickname);
        _client.setNickname(_nickname);
        return true;
    }

    public synchronized boolean handleEvent (DObject dobj, dist.Event evt)
    {
        if (evt.type == dist.Event.ATTR_CHANGED) {
            String name = (String)_cnames.get(evt.name);

            if (name == null) {
                if (evt.value != null) {
                    _list.addItem((String)evt.value);
                    _cnames.put(evt.name, evt.value);
                }

            } else {
                for (int i = 0; i < _list.countItems(); i++) {
                    if (_list.getItem(i).equals(name)) {
                        if (evt.value == null) {
                            _cnames.remove(evt.name);
                            _list.delItem(i);

                        } else {
                            _list.replaceItem((String)evt.value, i);
                            _cnames.put(evt.name, evt.value);
                        }
                    }
                }
            }

        } else if (evt.type == dist.Event.OBJECT_DELETED) {
            _cnames.clear();
            _list.clear();
        }

        return true;
    }

    public boolean handleEvent (java.awt.Event evt)
    {
        if ((evt.target == _name) &&
            (evt.id == java.awt.Event.ACTION_EVENT)) {
            try {
                setNickname(_name.getText());
            } catch (IOException e) {
                System.err.println("Network error: " + e);
            }
        }

        return super.handleEvent(evt);
    }

    //
    // WhoPanel protected data members

    String _nickname;
    Client _client;
    DObject _clients;
    List _list;
    TextField _name;
    Hashtable _cnames = new Hashtable();
}
