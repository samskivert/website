//
// WordPanel - displays a list of words and allows you to add to it
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

import java.awt.*;

import java.util.Hashtable;
import java.util.StringTokenizer;

public class WordPanel extends Panel implements Runnable
{
    //
    // WordPanel public constants

    public final static long GAME_TIME = 3 * 60 * 1000;

    //
    // WordPanel public constructor

    public WordPanel (Board board, WordGame game)
    {
        setLayout(new BorderLayout(0, 3));

        _game = game;
        _input.enable(false);

        add("North", _timer);
        add("Center", _list);
        add("South", _input);

        _board = board;
    }

    //
    // WordPanel public member functions

    public void clear ()
    {
        _input.setText("");
        _list.clear();
        _words.clear();
    }

    public boolean handleEvent (Event evt)
    {
        if ((evt.id == Event.ACTION_EVENT) && (evt.target == _input)) {
            // this is kind of clever, addWords returns a string
            // containing all the words that are not valid on the board,
            // so they stick around while the valid words get added to the
            // list. keen eh?
            _input.setText(addWords(_input.getText()));
        }

        return super.handleEvent(evt);
    }

    public void enable (boolean state)
    {
        _input.enable(state);
    }

    public void start ()
    {
        Thread t = new Thread(this);
        t.start();
    }

    public void run ()
    {
        long startTime = System.currentTimeMillis();
        _input.enable(true);

        for (long now = System.currentTimeMillis();
             now - startTime < GAME_TIME;
             now = System.currentTimeMillis()) {

            long minutes = (GAME_TIME - now + startTime) / (60*1000);
            long seconds = ((GAME_TIME - now + startTime) / (1000)) % 60;

            String left = "Time left: " + minutes + ":";
            left = left + ((seconds < 10) ? "0" : "") + seconds;
            _timer.setText(left);

            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
        _input.enable(false);

        // put the words in an array and give them back to the controller
        String[] words = new String[_list.countItems()];
        for (int i = 0; i < words.length; i++)
            words[i] = _list.getItem(i);
        _game.doneCollectingWords(words);
    }

    //
    // WordPanel protected member functions

    String addWords (String word)
    {
        StringTokenizer tok = new StringTokenizer(word);
        String failures = "";

        while (tok.hasMoreTokens()) {
            String failure = addWord(tok.nextToken());
            if (failure.length() > 0) {
                failures = failures +
                    ((failures.length() > 0) ? " " : "") + failure;
            }
        }

        return failures;
    }

    String addWord (String word)
    {
        int didx = word.indexOf("-");

        if (didx != -1) {
            String first = word.substring(0, didx);
            String f1 = addWord(first);
            String f2 = addWord(first + word.substring(didx+1));

            if ((f1.length() > 0) && (f2.length() > 0))
                return f1 + " " + f2;
            return f1 + f2;

        } else {
            if (_words.contains(word)) return "";

            if (_board.validWord(word)) {
                _list.addItem(word);
                _list.makeVisible(_list.countItems()-1);
                _words.put(word, word);
                return "";
            }

            return word;
        }
    }

    //
    // WordPanel protected data members

    WordGame _game;

    Label _timer = new Label("WordGame", Label.CENTER);
    List _list = new List();
    TextField _input = new TextField();

    Hashtable _words = new Hashtable();
    Board _board;
}
