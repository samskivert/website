//
// wordgame.WordGameGame - code that runs on the server to handle a game of
//                     WordGame
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

import dserver.Game;
import dist.*;
import java.io.IOException;
import java.util.Hashtable;

public class WordGameGame extends Game
{
    //
    // wordgame.WordGameGame public constants

    public final static int CHECKING = 2;
    public final static int SCORING = 3;

    //
    // wordgame.WordGameGame public member functions

    public void init (String gid, Hashtable ptog) throws IOException
    {
        super.init(gid, ptog);
        _verifier = new Verifier(game);
    }

    public synchronized void removePlayer (String pid)
    {
        super.removePlayer(pid);
        // remove this player from the readies table
        _readies.remove(pid);

        // maybe this was the last player to indicate readiness and we can
        // go now, so we should check that out
        try { checkReadies(); }
        catch (IOException e) {
            System.err.println("Error sending ready event in game " +
                               gid + ": " + e);
        }
    }

    public boolean handleEvent (DObject target, Event evt)
    {
        try {
            if (evt.type == Event.ATTR_CHANGED) {
                if (evt.name.equals("all_present")) {
                    // set things in motion
                    prepareForRound();
                    game.setValue("ready", 1);

                } else if (evt.name.startsWith("ready.") ||
                           evt.name.startsWith("words.") ||
                           evt.name.startsWith("score.")) {

                    String pid = evt.name.substring(6);
                    _readies.put(pid, this);

                    // transition to the next round or end of game
                    if (evt.name.startsWith("score.")) {
                        _state = SCORING;

                        // keep track of the top scorer(s) so that we can
                        // detect ties
                        int score = ((Integer)evt.value).intValue();
                        if (score > _topscore) {
                            _topscore = score;
                            _tophavers = 1;
                        } else if (score == _topscore) {
                            _tophavers++;
                        }

                    } else if (evt.name.startsWith("words.")) {
                        _state = CHECKING;

                        String[] words = (String[])evt.value;

                        // System.out.print("received:");
                        // for (int i = 0; i < words.length; i++)
                        // System.out.print(" " + words[i]);
                        // System.out.println("");

                        // add these words to the verifier's list
                        _verifier.addWords((String[])evt.value);

                    } else {
                        _state = 0;
                    }

                    // now dispatch the ready event to the players
                    checkReadies();
                }
            }

        } catch (IOException e) {
            System.err.println("Error handling evt " + evt +
                               " for game " + gid + ": " + e);
        }

        return super.handleEvent(target, evt);
    }

    //
    // wordgame.WordGameGame protected member functions

    void prepareForRound () throws IOException
    {
        game.setValue("round", game.getValue("round", 0) + 1);
        String board =
            new String(Pieces.generateBoard(System.currentTimeMillis()));
        game.setValue("board", board);
    }

    void checkReadies () throws IOException
    {
        // send out a "ready" event went all have reported to be ready
        if (_readies.size() == players) {

            // we need to do this stuff before we send a ready event
            switch (_state) {
            case SCORING:
                // game over if someone scores over MAX_SCORE and it's not
                // tied
                _gameOver = ((_topscore >= MAX_SCORE) && (_tophavers == 1));
                prepareForRound();
                break;

            case CHECKING:
                _verifier.start();
                break;
            }

            game.setValue(_gameOver ? "game_over" : "ready", 1);
            _readies.clear();
        }
    }

    //
    // wordgame.WordGameGame protected constants

    final static int MAX_SCORE = 100;

    //
    // wordgame.WordGameGame protected data members

    boolean _gameOver = false;
    Hashtable _readies = new Hashtable();
    Verifier _verifier;

    int _topscore = 0;
    int _tophavers = 0;
    int _state = 0;
}
