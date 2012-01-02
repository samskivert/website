//
// WordList - displays a list of words and allows crossing out and removal
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
import java.util.Vector;

public class WordList
{
    //
    // WordList public constructor

    public WordList (String title, Vector words, Component parent)
    {
        // make sure the name ain't too stinking long
        _title = PlayerPanel.trimName(title, 25);

        _words = words;
        _parent = parent;
        _metrics =
            parent.getFontMetrics(new Font("Helvetica", Font.PLAIN, 10));
        _tmetrics =
            parent.getFontMetrics(new Font("Helvetica", Font.BOLD, 10));

        for (int i = 0; i < _words.size(); i++)
            _score += scoreForWord(wordAt(i));
    }

    //
    // WordList public member function

    public String title () { return _title; }

    public boolean contains (String word) { return _words.contains(word); }
    public String wordAt (int i) { return (String)_words.elementAt(i); }
    public int size () { return _words.size(); }

    public void strikeOut (String word, Object hack)
    {
        if (!_words.contains(word)) return;
        _score -= scoreForWord(word);
        _strikes.put(word, hack);
    }

    public boolean struckOut (String word)
    {
        return _strikes.containsKey(word);
    }

    public void setRemoveStrikes (boolean removeStrikes)
    {
        _removeStrikes = removeStrikes;
    }

    public int getScore () { return _score; }

    public void paintAt (Graphics g, int x, int y, int height)
    {
        Point p = new Point(x, y);
        paintString(g, _title, p, null, height, _tmetrics, false);

        Point lp = new Point(p.x, p.y); // this is where we'll draw a line
        p.y += 5; // now move down a bit and draw the words

        for (int i = 0; i < _words.size(); i++) {
            String word = (String)_words.elementAt(i);
            Object struck = _strikes.get(word);

            if ((struck == this) && _removeStrikes) {
                _strikes.remove(word);
                _words.removeElementAt(i--);
                continue;
            }

            paintString(g, word, p, lp, height, _metrics, struck != null);
        }

        // put a blank space below the last word
        paintString(g, "", p, lp, height, _metrics, false);

        // draw the score string
        String scstr = "Score: " + _score;
        paintString(g, scstr, p, lp, height, _tmetrics, false);

        // now draw a line under the title
        g.drawLine(lp.x, lp.y, p.x+_maxwid, lp.y);
    }

    public void clearRect (Graphics g, int x, int y, int width, int height)
    {
        Color c = g.getColor();
        g.setColor(_parent.getBackground());
        g.fillRect(x, y, width, height);
        g.setColor(c);
    }

    //
    // WordList protected member functions

    void paintString (Graphics g, String str, Point p, Point lp,
                      int height, FontMetrics fm, boolean strikeOut)
    {
        int wid = fm.stringWidth(str);
        if (wid > _maxwid) _maxwid = wid;

        // clearRect(g, p.x-2, p.y, _maxwid+4, fm.getHeight());

        g.setFont(fm.getFont());
        g.drawString(str, p.x, p.y + fm.getAscent());

        // strike a line through the word if it's striked out
        if (strikeOut) {
            g.setColor(Color.red);
            int wy = p.y + fm.getAscent()/2;
            g.drawLine(p.x-2, wy, p.x+wid+2, wy);
            g.setColor(Color.black);
        }

        p.y += fm.getHeight();

        // move over a column if we hit rock bottom
        if (p.y + fm.getHeight() > height) {
            p.y = lp.y + 5;
            p.x += _maxwid + 5;
        }

        // clear out the next spot incase we recently moved up a spot
        // clearRect(g, p.x-2, p.y, _maxwid+4, fm.getHeight());
    }

    int scoreForWord (String word)
    {
        if (word.length() < 4) return 0;
        if (word.length() > 8) return 11;
        return _scoreTrans[word.length()-4];
    }

    //
    // WordList protected constants

    final static int[] _scoreTrans = { 1, 2, 3, 5, 11 };

    //
    // WordList protected data members

    boolean _removeStrikes;

    String _title;
    Vector _words;

    int _score;

    Component _parent;
    FontMetrics _metrics;
    FontMetrics _tmetrics;
    int _maxwid = 0;

    Hashtable _strikes = new Hashtable();
}
