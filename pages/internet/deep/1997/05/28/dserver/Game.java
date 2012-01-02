//
// dserver.Game - manages a game on the server side
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

public class Game implements Subscriber
{
    //
    // dserver.Game public data members

    public String gid;
    public int players;
    public DObject game;

    //
    // dserver.Game public member functions

    public void init (String gid, Hashtable ptog) throws IOException
    {
        System.out.println("Initializing game: " + gid);

        this.gid = gid;
        _ptog = ptog;
        game = Server.omgr.subscribeToObject(gid, this);

        // add all the players currently in the game object
        Enumeration keys = game.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (key.startsWith("player.")) addPlayer(key.substring(7));
        }
    }

    public synchronized void addPlayer (String pid)
    {
        if (_ptog.get(pid) != this) {
            System.out.println("Adding " + pid + " to " + gid);
            _ptog.put(pid, this);
            players++;

            // remove this game from the waiting list if we've reached our
            // maximum number of players
            if (players == game.getValue("maxPlayers", 4)) {
                System.out.println("Hit maxPlayers (" + players +
                                   "), switching to playing.");
                Server.gmgr.switchToPlaying(gid);

                try { game.setValue("all_present", 1); }
                catch (IOException e) {
                    System.err.println("Error indicating all_present " +
                                       "in game " + gid + ": " + e);
                }
            }
        }
    }

    public synchronized void removePlayer (String pid)
    {
        try {
            System.out.println("Removing player " + pid + " from " + gid);

            // remove this player from the game object
            if (game.containsKey("player." + pid)) {
                game.setValue("player." + pid, (String)null);
            }
            // remove this player from the player to game mapping
            _ptog.remove(pid);
            // if we have no more players, then destroy this game
            if (--players == 0) Server.gmgr.destroyGame(gid);

        } catch (IOException e) {
            System.err.println("Error removing player " + pid +
                               " from game " + gid + ": " + e);
        }
    }

    public void shutdown ()
    {
        try {
            System.out.println("Shutting down game: " + gid);
            Server.omgr.destroyObject(game.oid);

        } catch (IOException e) {
            System.err.println("Error destroying " + game.oid + ": " + e);
        }
    }

    //
    // dist.Subscriber public member functions

    public boolean handleEvent (DObject target, Event evt)
    {
        if (evt.type == Event.ATTR_CHANGED) {
            if (evt.name.startsWith("player.")) {
                if (evt.value != null) {
                    addPlayer(evt.name.substring(7));
                } else {
                    removePlayer(evt.name.substring(7));
                }
            }
        }

        return true;
    }

    //
    // dserver.Game protected data members

    Hashtable _ptog;
}
