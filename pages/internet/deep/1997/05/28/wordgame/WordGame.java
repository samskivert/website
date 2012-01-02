//
// wordgame.WordGame - the main thing
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

import dclient.Client;
import dclient.ChatPanel;

import dist.DObject;
import dist.DObjectManager;
import dist.Subscriber;

import java.awt.*;
import java.io.IOException;

public class WordGame extends Frame implements Subscriber, Runnable
{
    //
    // wordgame.WordGame public constants

    // state constants
    public final static int PREGAME = 0;
    public final static int PREROUND = 1;
    public final static int INROUND = 2;
    public final static int POSTROUND = 3;
    public final static int GAMEOVER = 4;

    public final static int[] transitions = { PREROUND, INROUND,
                                              POSTROUND, PREROUND };

    //
    // wordgame.WordGame public constructor

    public WordGame (Client client, String gid, String name, int maxPlayers)
        throws IOException
    {
        super("WordGame (tm)");
        if (_wordgame != null) throw new IOException("Already in a game.");

        _wordgame = this;
        _client = client;
        _game = client.subscribeToObject(gid, this);
        _game.setValue("name", name);
        _game.setValue("maxPlayers", maxPlayers);
        _game.setValue("class", "wordgame/WordGameGame");

        init();
    }

    public WordGame (Client client, String gid)
        throws IOException
    {
        super("WordGame (tm)");
        if (_wordgame != null) throw new IOException("Already in a game.");

        _wordgame = this;
        _client = client;
        _game = client.subscribeToObject(gid, this);

        init();
    }

    //
    // wordgame.WordGame public member functions

    public void init () throws IOException
    {
        setLayout(new BorderLayout(5, 5));

        _chatp.setClient(_client, _game.oid);

        Panel p = new Panel();
        p.setLayout(new BorderLayout(0, 5));
        p.add("North", _playerp = new PlayerPanel(this, _game));
        p.add("Center", _chatp);
        add("West", p);

        activateState(PREGAME);

        // have to do this after we create the player panel cuz it's going
        // to cause us to try to register ourselves int the player list
        _game.setValue("player."+_client.clientId(), _client.nickname());
    }

    public void setState (int state)
    {
        deactivateState(_state);
        activateState(_state = state);
    }

    public boolean handleEvent (Event evt)
    {
        switch (evt.id) {
        case Event.WINDOW_DESTROY:
            quit();
            break;
        }

        return super.handleEvent(evt);
    }

//     public Insets insets ()
//     {
//         Insets i = super.insets();
//         i.left += 5;
//         i.top += 5;
//         i.right += 5;
//         i.bottom += 5;
//         return i;
//     }

    public synchronized void validate ()
    {
        layout();

        for (int i = 0 ; i < countComponents() ; i++) {
            Component comp = getComponent(i);
            if (!comp.isValid()) comp.validate();
        }
    }

    public void orientAndShow ()
    {
        // center the game in the screen
        resize(640, 400);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension bd = size();
        move((d.width-bd.width)/2, (d.height-bd.height)/2);
        show();
    }

    public void quit ()
    {
        try {
            _quit = true;
            // remove ourselves from the game object to indicate that we left
            _game.setValue("player."+_client.clientId(), (String)null);

        } catch (IOException e) {
            System.err.println("Error removing ourselves from game: " + e);
        }

        dispose();
        _wordgame = null;
    }

    public void prepareForRound ()
    {
        Thread t = new Thread(this);
        t.start();
    }

    public void doneCollectingWords (String[] words)
    {
        try {
            // System.out.print("sending:");
            // for (int i = 0; i < words.length; i++)
            // System.out.print(" " + words[i]);
            // System.out.println("");

            _game.setValue("words." + _client.clientId(), words);
        } catch (IOException e) {
            System.err.println("Error sending words: " + e);
        }
    }

    public void doneComputingScore (int score)
    {
        try {
            // add this score to our total score
            String scorekey = "score." + _client.clientId();
            _game.setValue(scorekey, score + _game.getValue(scorekey, 0));

        } catch (IOException e) {
            System.err.println("Error sending score: " + e);
        }
    }

    //
    // dist.Subscriber public member functions

    public synchronized boolean handleEvent (DObject dobj, dist.Event evt)
    {
        if (_quit) return false;

        switch (evt.type) {
        case dist.Event.ATTR_CHANGED:
            if (evt.name.startsWith("player.")) {
                String pid = evt.name.substring(7);
                if (evt.value != null) {
                    _playerp.addPlayer(pid, (String)evt.value,
                                       _game.getValue("score." + pid, 0));
                } else {
                    _playerp.removePlayer(pid);
                }
                if (_state == PREGAME) _message.setText(needMessage());

            } else if (evt.name.startsWith("words.")) {
                String pid = evt.name.substring(6);
                _crossp.addWordList(_game.getValue("player." + pid, "Someone"),
                                    (String[])evt.value,
                                    pid.equals(_client.clientId()));

            } else if (evt.name.startsWith("score.")) {
                String pid = evt.name.substring(6);
                _playerp.setScore(pid,
                                  _game.getValue("player." + pid, "Someone"),
                                  ((Integer)evt.value).intValue());

            } else if (evt.name.equals("board")) {
                _board.setPieces(((String)evt.value).toCharArray());

            } else if (evt.name.equals("ready")) {
                // transition states if everyone is ready
                setState(transitions[_state]);

            } else if (evt.name.equals("game_over")) {
                setState(GAMEOVER);

            } else if (evt.name.equals("invalid")) {
                _crossp.invalidateWord((String)evt.value);

            } else if (evt.name.equals("verified")) {
                _crossp.finished();
            }
            break;

        case dist.Event.OBJECT_DELETED:
            // oh crap
            break;
        }

        return true;
    }

    //
    // Runnable public member functions

    public void run ()
    {
        // sleep for 5 seconds and then indicate that we're ready
        try { Thread.sleep(5000); } catch (InterruptedException e) {}

        try { _game.setValue("ready." + _client.clientId(), 1); }
        catch (IOException e) {
            System.err.println("Error indicating readiness: " + e);
        }
    }

    //
    // wordgame.WordGame protected data members

    String needMessage ()
    {
        int need = _game.getValue("maxPlayers", 4);
        need -= _playerp.numPlayers();
        return "Waiting for " + need + " more players.";
    }

    void relayout ()
    {
        for (int i = 0 ; i < countComponents() ; i++) {
            getComponent(i).invalidate();
        }
        validate();
    }

    void activateState (int state)
    {
        switch (state) {
        case PREGAME:
            _message.setText(needMessage());
            add("Center", _message);
            break;

        case PREROUND:
            _message.setText("Round " + _game.getValue("round", 0));
            add("Center", _message);
            relayout();
            prepareForRound();
            break;

        case INROUND:
            add("Center", _board);
            add("East", _wordp);
            relayout();
            _wordp.start();
            break;

        case POSTROUND:
            add("Center", _crossp);
            relayout();
            _crossp.start();
            break;

        case GAMEOVER:
            _message.setText("And the winner is: " + _playerp.winner());
            add("Center", _message);
            relayout();
            break;
        }
    }

    void deactivateState (int state)
    {
        switch (state) {
        case PREGAME:
            remove(_message);
            break;

        case PREROUND:
            remove(_message);
            break;

        case INROUND:
            remove(_board);
            remove(_wordp);
            _wordp.clear();
            break;

        case POSTROUND:
            remove(_crossp);
            _crossp.clear();
            break;
        }
    }

    //
    // wordgame.WordGame protected data members

    boolean _quit;

    Client _client;
    DObject _game;
    int _state = PREGAME;

    Label _message = new Label("", Label.CENTER);
    Board _board = new Board();
    CrossOutPanel _crossp = new CrossOutPanel(this);
    WordPanel _wordp = new WordPanel(_board, this);
    PlayerPanel _playerp;
    ChatPanel _chatp = new ChatPanel(20);

    //
    // wordgame.WordGame protected static data members

    static WordGame _wordgame;
}
