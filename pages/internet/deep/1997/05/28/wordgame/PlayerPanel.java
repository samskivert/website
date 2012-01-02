//
// PlayerPanel - displays a list of words and allows you to add to it
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

package wordgame;

import dist.DObject;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class PlayerPanel extends Panel
{
    //
    // PlayerPanel public constructor

    public PlayerPanel (WordGame wordgame, DObject game)
    {
        setLayout(new BorderLayout(0, 3));
        add("North", new Label("Players", Label.CENTER));
        add("Center", _players);
        add("South", _resign);

        _wordgame = wordgame;
        _game = game;

        // add all the players currently in the game
        Enumeration keys = _game.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (key.startsWith("player.")) {
                String pid = key.substring(7);
                addPlayer(pid, _game.getValue(key, "???"),
                          _game.getValue("score." + pid, 0));
            }
        }

        // _players.setFont(new Font("Courier", 8, Font.PLAIN));
    }

    //
    // PlayerPanel public member functions

    public int numPlayers () { return _players.countItems(); }

    public void addPlayer (String pid, String name, int score)
    {
        String scorestr = genScoreString(fixName(name), score);

        int idx = _pids.indexOf(pid);
        if (idx == -1) {
            _pids.addElement(pid);
            _players.addItem(scorestr);

        } else {
            _players.replaceItem(scorestr, idx);
        }
    }

    public void removePlayer (String pid)
    {
        int idx = _pids.indexOf(pid);
        if (idx == -1) return;

        _pids.removeElementAt(idx);
        _players.delItem(idx);
    }

    public void setScore (String pid, String name, int score)
    {
        int idx = _pids.indexOf(pid);
        if (idx == -1) return;

        // track the high scorer
        if (score > _highscore) {
            _highscore = score;
            _highscorer = name;
        }

        _players.replaceItem(genScoreString(fixName(name), score), idx);
    }

    public String winner ()
    {
        return _highscorer + " with " + _highscore + " points.";
    }

    public boolean handleEvent (Event evt)
    {
        if ((evt.id == Event.ACTION_EVENT) && (evt.target == _resign)) {
            _wordgame.quit();
        }

        return super.handleEvent(evt);
    }

    public Dimension preferredSize ()
    {
        Dimension d = super.preferredSize();
        d.height = 175;
        return d;
    }

    //
    // PlayerPanel public static member functions

    public static String trimName (String name, int width)
    {
        StringBuffer sbuf = new StringBuffer();

        if (name.length() > width) {
            sbuf.append(name.substring(0, width-4));
            sbuf.append("... ");
        } else {
            sbuf.append(name);
            while (sbuf.length() < width) sbuf.append(" ");
        }

        return sbuf.toString();
    }

    //
    // PlayerPanel protected member functions

    String genScoreString (String name, int score)
    {
        // scores up to 10,000 aught to cover it
        if (score < 10) return name + "    " + score;
        else if (score < 100) return name + "   " + score;
        else if (score < 1000) return name + "  " + score;
        else return name + " " + score;
    }

    String fixName (String name)
    {
        return trimName(name, TEXT_WIDTH);
    }

    //
    // PlayerPanel protected constants

    final static int TEXT_WIDTH = 15;

    //
    // PlayerPanel protected data members

    WordGame _wordgame;
    DObject _game;

    List _players = new List();
    Button _resign = new Button("Resign");

    Vector _pids = new Vector();
    Hashtable _words = new Hashtable();

    String _highscorer;
    int _highscore;
}
