//
// GameManager - manages the game object list, informs games when players
//               disconnect
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

package dserver;

import dist.*;
import java.io.IOException;
import java.util.*;

public class GameManager implements Subscriber
{
    //
    // GameManager public member functions

    public void init () throws IOException
    {
        _clients = Server.omgr.subscribeToObject("cmgr.clients", this);
        _waiting = Server.omgr.subscribeToObject("gmgr.waiting", this);
        _playing = Server.omgr.subscribeToObject("gmgr.playing", this);
    }

    public synchronized void shutdown ()
    {
    }

    //
    // Subscriber public member functions

    public synchronized boolean handleEvent (DObject target, Event evt)
    {
        if (target == _clients) {
            // if a player went away, we need to modify the game object to
            // which they were connected (if any)
            if ((evt.type == Event.ATTR_CHANGED) && (evt.value == null)) {
                Game g = (Game)_playerToGame.get(evt.name);

                if (g != null) {
                    System.out.println("removing from game: " + evt.name);
                    g.removePlayer(evt.name);
                } else {
                    System.out.println("not in any games: " + evt.name);
                }
            }

        } else if (target == _waiting) {
            // if someone created a game, we need to create a game object
            // to go with it
            if ((evt.type == Event.ATTR_CHANGED) && (evt.value != null)) {
                try {
                    DObject gobg =
                        Server.omgr.subscribeToObject(evt.name, null);

                    String gclass = gobg.getValue("class", "dserver/Game");
                    Game g = null;

                    // load up the game handler for this game
                    try {
                        g = (Game)Class.forName(gclass).newInstance();

                    } catch (ClassCastException cce) {
                        System.err.println("Class " + gclass + " doesn't" +
                                           " derive from dserver.Game.");
                    } catch (ClassNotFoundException cnfe) {
                        System.err.println("No such class: " + gclass);
                    } catch (InstantiationException ie) {
                        System.err.println("Error instantiating " +
                                           gclass + ": " + ie);
                    } catch (IllegalAccessException iae) {
                        System.err.println("Can't access " +
                                           gclass + ": " + iae);
                    }

                    // so that the primary functionality of player removal
                    // and game cleanup will always be performed, at least
                    // create a Game object to deal with stuff
                    if (g == null) g = new Game();

                    g.init(evt.name, _playerToGame);
                    _games.put(evt.name, g);

                } catch (IOException e) {
                    System.err.println("Error creating game " +
                                       evt.name + ": " + e);
                }
            }

        } else if (target == _playing) {
        }

        return true;
    }

    public synchronized void destroyGame (String gid)
    {
        Game g = (Game)_games.remove(gid);
        if (g != null) g.shutdown();

        try {
            _waiting.setValue(gid, (String)null);
            _playing.setValue(gid, (String)null);

        } catch (IOException e) {
            System.err.println("Error destroying game " + gid + ": " + e);
        }
    }

    public synchronized void switchToPlaying (String gid)
    {
        try {
            String name = _waiting.getValue(gid, (String)null);
            if (name == null) return;
            _waiting.setValue(gid, (String)null);
            _playing.setValue(gid, name);

        } catch (IOException e) {
            System.err.println("Error switching " + gid +
                               " to playing state: " + e);
        }
    }

    //
    // GameManager protected data members

    DObject _clients;
    DObject _waiting;
    DObject _playing;

    Hashtable _games = new Hashtable();
    Hashtable _playerToGame = new Hashtable();
}
