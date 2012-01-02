//
// CrossOutPanel - used to remove duplicate words from the players' word
//                 lists and generally do the whole scoring thing
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
import java.util.Vector;

public class CrossOutPanel extends Canvas implements Runnable
{
    //
    // CrossOutPanel public constructor

    public CrossOutPanel (WordGame game)
    {
        _game = game;
    }

    //
    // CrossOutPanel public member functions

    public void clear ()
    {
        _finished = false;
        _lists.removeAllElements();
        _ourlist = null;
    }

    public void addWordList (String name, String[] words, boolean ours)
    {
        Vector v = new Vector();
        for (int i = 0; i < words.length; i++) v.addElement(words[i]);
        WordList wl = new WordList(name, v, this);
        _lists.addElement(wl);
        if (ours) _ourlist = wl;
    }

    public void invalidateWord (String word)
    {
        for (int i = 0; i < _lists.size(); i++) {
            ((WordList)_lists.elementAt(i)).strikeOut(word, this);
        }
    }

    public synchronized void finished ()
    {
        _finished = true;
        notify();
    }

    // public void update (Graphics g) { paint(g); }

    public void paint (Graphics g)
    {
        Dimension d = size();
        int listwid = (d.width - 10) / _lists.size() - SPACING;
        int x = SPACING;
        int y = SPACING;

        FontMetrics fm = getFontMetrics(getFont());
        y += fm.getHeight();

        ((WordList)_lists.elementAt(0)).clearRect(g, 0, SPACING, d.width,
                                                  fm.getHeight());
        g.drawString(_message, SPACING, SPACING + fm.getAscent());

        for (int i = 0; i < _lists.size(); i++) {
            WordList list = (WordList)_lists.elementAt(i);
            list.setRemoveStrikes(_removeStrikes);
            list.paintAt(g, x, y, d.height - y - SPACING);
            x += listwid + SPACING;
        }
    }

    public void start ()
    {
        Thread t = new Thread(this);
        t.start();
    }

    public void run ()
    {
        // hold up a second for people to pay attention
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        for (int i = 0; i < _lists.size()-1; i++) {
            WordList list = (WordList)_lists.elementAt(i);

            for (int w = 0; w < list.size(); w++) {
                String word = list.wordAt(w);

                // no need to re-strike-out words
                if (list.struckOut(word)) continue;

                boolean struck = false;
                for (int t = i+1; t < _lists.size(); t++) {
                    WordList target = (WordList)_lists.elementAt(t);
                    if (target.contains(word)) {
                        struck = true;
                        target.strikeOut(word, target);
                    }
                }

                if (struck) {
                    list.strikeOut(word, list);
                    w--;

                    // draw the red lines and wait half a second
                    _removeStrikes = false;
                    repaint();
                    try { Thread.sleep(1000); }
                    catch (InterruptedException e) {}

                    // now remove the struck words and redraw
                    _removeStrikes = true;
                    repaint();
                    try { Thread.sleep(1000); }
                    catch (InterruptedException e) {}
                }
            }
        }

        _message = "Waiting for dictionary lookups.";
        repaint();

        // wait for all the dictionary invalidations to complete
        synchronized (this) {
            try { while (!_finished) wait(); }
            catch (InterruptedException e) {}
            _message = "Received all lookups, scoring.";
            repaint();
        }

        // wait a few seconds for it all to sink in
        try { Thread.sleep(5000); } catch (InterruptedException e) {}

        // look for our score and tell that to the game controller
        if (_ourlist != null) {
            System.out.println("Sending back score: " + _ourlist.getScore());
            _game.doneComputingScore(_ourlist.getScore());

        } else {
            System.err.println("Ack! Never received our word list.");
            _game.doneComputingScore(0);
        }
    }

    //
    // CrossOutPanel protected constants

    final static int SPACING = 5;

    //
    // CrossOutPanel protected data members

    String _message = "Removing duplicate words";

    WordGame _game;
    Vector _lists = new Vector();
    boolean _removeStrikes;
    boolean _finished;

    WordList _ourlist;
}
