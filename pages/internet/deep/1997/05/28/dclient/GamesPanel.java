//
// GamesPanel - displays a list of games (from the waiting game DO)
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

import wordgame.WordGame;

import dist.DObject;
import dist.DObjectManager;
import dist.Subscriber;

import java.awt.Button;
import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class GamesPanel extends Panel implements Subscriber, Runnable
{
    //
    // GamesPanel public constructors

    public GamesPanel ()
    {
        // create our user interface elements
        setLayout(new BorderLayout());

        add("North", new Label("Games", Label.CENTER));
        add("Center", _list);

        Panel p = new Panel();
        p.add(_join);
        p.add(_create);
        add("South",  p);
    }

    //
    // GamesPanel public member functions

    public void setClient (Client client) throws IOException
    {
        _client = client;

        // get a handle on the client list distributed object
        _waiting = _client.subscribeToObject("gmgr.waiting", this);
        _playing = _client.subscribeToObject("gmgr.playing", this);
        _list.clear();

        // add all the currently registered clients to the list to start
        Enumeration keys = _waiting.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = _waiting.getValue(key, "Error");
            _list.addItem(value);
            _gnames.put(key, value);
        }
    }

    public synchronized boolean handleEvent (DObject dobj, dist.Event evt)
    {
        if (dobj == _waiting) {
            if (evt.type == dist.Event.ATTR_CHANGED) {
                String name = (String)_gnames.get(evt.name);

                if (name == null) {
                    if (evt.value != null) {
                        _list.addItem((String)evt.value);
                        _gnames.put(evt.name, evt.value);
                    }

                } else {
                    for (int i = 0; i < _list.countItems(); i++) {
                        if (_list.getItem(i).equals(name)) {
                            if (evt.value == null) {
                                _gnames.remove(evt.name);
                                _list.delItem(i);

                            } else {
                                _list.replaceItem((String)evt.value, i);
                                _gnames.put(evt.name, evt.value);
                            }
                        }
                    }
                }

            } else if (evt.type == dist.Event.OBJECT_DELETED) {
                _gnames.clear();
                _list.clear();
            }
        }

        return true;
    }

    public boolean handleEvent (java.awt.Event evt)
    {
        if (evt.id == java.awt.Event.ACTION_EVENT) {
            if (evt.target == _join) doAction(JOIN_GAME);
            else if (evt.target == _create) doAction(CREATE_GAME);
        }

        return super.handleEvent(evt);
    }

    // this is kinda wacky, but we can't be doing everything on the AWT
    // thread you know...
    public void doAction (int action)
    {
        _action = action;
        Thread t = new Thread(this);
        t.start();
    }

    public void run ()
    {
        switch (_action) {
        case CREATE_GAME: createGame(); break;
        case JOIN_GAME: joinSelectedGame(); break;
        }
    }

    //
    // GamesPanel protected member functions

    void joinSelectedGame ()
    {
        String name = _list.getSelectedItem(), gid = null;
        if (name == null) return;

        // look for the game id that maps to this game name
        Enumeration keys = _gnames.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            // if this key maps to the selected name, we've found our game
            if (name.equals(_gnames.get(key))) {
                gid = key;
                break;
            }
        }

        // if we didn't find this game, bail
        if (gid == null) return;

        try {
            WordGame b = new WordGame(_client, gid);
            b.orientAndShow();

        } catch (IOException e) {
            System.err.println("Error creating game: " + e);
        }
    }

    void createGame ()
    {
        CreateGamePanel p = new CreateGamePanel();
        if (!p.run()) return;

        String gid = _client.clientId() + ".game" +
            System.currentTimeMillis();

        try {
            WordGame b = new WordGame(_client, gid, p.getName(), p.getPlayers());
            b.orientAndShow();

            // now that the wordgame board is created, the game object is
            // created and we can add ourselves to the waiting list
            _waiting.setValue(gid, p.getName());

        } catch (IOException e) {
            System.err.println("Error creating game: " + e);
        }
    }

    //
    // GamesPanel protected constants

    final static int CREATE_GAME = 1;
    final static int JOIN_GAME = 2;

    //
    // GamesPanel protected data members

    int _action;
    Client _client;

    DObject _waiting;
    DObject _playing;

    List _list = new List();
    Button _join = new Button("Join");
    Button _create = new Button("Create");

    Hashtable _gnames = new Hashtable();
}
