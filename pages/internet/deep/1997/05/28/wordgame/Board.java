//
// wordgame.Board - draws the wordgame board in a nice scalable way
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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class Board extends Canvas
{
    //
    // wordgame.Board public constants

    public final static int SPACING = 4;

    //
    // wordgame.Board public member functions

    public void setPieces (char[] pieces)
    {
        _pieces = pieces;
        _board = Pieces.genNodeGraph(_pieces);
    }

    public boolean validWord (String word)
    {
        if (word.length() < 4) return false;
        return Pieces.graphContainsWord(_board, word);
    }

    public void reshape (int x, int y, int width, int height)
    {
        super.reshape(x, y, width, height);

        _boardsize = (width > height) ? height : width;
        _piecesize = (_boardsize - 4*SPACING) / 5;

        Font f = new Font("Helvetica", Font.BOLD, _piecesize*5/6);
        _pmetrics = getFontMetrics(f != null ? f : getFont());

        f = new Font("Helvetica", Font.BOLD, _piecesize*3/5);
        _qmetrics = getFontMetrics(f != null ? f : getFont());
    }

    public void update (Graphics g)
    {
        paint(g);
    }

    public void paint (Graphics g)
    {
        Dimension d = size();

        int x = (d.width-_boardsize)/2, y = (d.height-_boardsize)/2, sx = x;

        for (int i = 0; i < _pieces.length; i++) {
            paintPiece(g, x, y, _pieces[i]);
            x += _piecesize + SPACING;

            // ching (that's the noise a typewriter makes)
            if (i % 5 == 4) {
                x = sx;
                y += _piecesize + SPACING;
            }
        }
    }

    //
    // wordgame.Board protected member functions

    void paintPiece (Graphics g, int x, int y, char letter)
    {
        int half = _piecesize/2;
        int corner = Math.max(_piecesize/5, 1);

        g.setColor(_top);
        g.fillRoundRect(x, y, _piecesize, half, corner, corner);
        g.setColor(_bot);
        g.fillRoundRect(x, y+half, _piecesize, half, corner, corner);
        g.setColor(_circle);
        g.fillOval(x, y, _piecesize, _piecesize);

        String str = letterToString(letter);

        FontMetrics metrics = (letter == 'q') ? _qmetrics : _pmetrics;
        int yoff = (letter == 'q') ? _piecesize/7 : 0;

        int lwid = metrics.stringWidth(str);
        int lhei = metrics.getAscent();

        g.setColor(Color.black);
        g.setFont(metrics.getFont());
        g.drawString(str, x+(_piecesize-lwid)/2, y+metrics.getAscent()+yoff);

        g.drawRoundRect(x, y, _piecesize, _piecesize, corner, corner);
    }

    String letterToString (char letter)
    {
        if (letter == 'q') return "Qu";
        return String.valueOf((char)(letter + 'A' - 'a'));
    }

    //
    // wordgame.Board protected data members

    char[] _pieces;
    Node[] _board;

    int _boardsize;
    int _piecesize;

    Color _top = new Color(255, 255, 225);
    Color _bot = new Color(255, 215, 185);
    Color _circle = new Color(255, 235, 205);

    FontMetrics _pmetrics;
    FontMetrics _qmetrics;
}
