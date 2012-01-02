//
// CSierpinski - Generate an approximation of Sierpinski's triangle by playing
//               the chaos game.
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1996/12/11/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Point;

public class CSierpinski extends DBApplet
{
    //
    // CSierpinski public member functions

    public void init ()
    {
        super.init();

        _size = size();

        // set up the ends of the triangle
        _ends[0] = new Point(_size.width/2, 5);
        _ends[1] = new Point(5, _size.height-5);
        _ends[2] = new Point(_size.width-5, _size.height-5);
        _ends[3] = new Point(0, 0);
        _ends[4] = new Point(0, 0);

        // pick a random first point
        randomPoint();

        title("Sierpinski's", "Gasket?");
    }

    public void randomPoint ()
    {
        int height = _ends[1].y - _ends[0].y;
        int width = _ends[2].x - _ends[0].x;

        // pick a random point inside the triangle (avoid the extreme edges),
        // first the y position
        _ends[3].y = (int)(Math.random() * (double)(_size.height-10)) + 5;

        int minx = width - (_ends[3].y * width / height) + _ends[1].x;
        int maxx = _ends[3].y * width / height + _ends[0].x;

        // now pick a valid x for that y
        _ends[3].x = (int)(Math.random() * (double)(maxx - minx)) + minx;
    }

    public void newGamePoint ()
    {
        int vertex = (int)(Math.random() * 3.0);

        if (_ends[3].x > _ends[vertex].x) {
            _ends[4].x = (_ends[3].x - _ends[vertex].x)/2 + _ends[vertex].x;
        } else {
            _ends[4].x = (_ends[vertex].x - _ends[3].x)/2 + _ends[3].x;
        }

        if (_ends[3].y > _ends[vertex].y) {
            _ends[4].y = (_ends[3].y - _ends[vertex].y)/2 + _ends[vertex].y;
        } else {
            _ends[4].y = (_ends[vertex].y - _ends[3].y)/2 + _ends[3].y;
        }

        _ends[3].x = _ends[4].x;
        _ends[3].y = _ends[4].y;
    }

    public int drawSomeDots (int howmany)
    {
        for (int i = 0; i < howmany; i++) {
            newGamePoint();
            _offGraphics.drawLine(_ends[4].x, _ends[4].y,
                                  _ends[4].x, _ends[4].y);
        }
        repaint();

        return howmany;
    }

    public void run ()
    {
        int[] increments = { 100, 100, 300, 500, 1500 };
        int numPoints = 0;

        for (int i = 0; i < increments.length; i++) {
            message(numPoints + " pts", "Click");
            repaint();
            waitForClick();

            clearMessage();
            numPoints += drawSomeDots(increments[i]);
        }

        message(numPoints + " pts", "Click");
        repaint();
        waitForClick();

        while (_running) {
            clearMessage();
            numPoints += drawSomeDots(3000);

            message(numPoints + " pts", "Click");
            repaint();
            waitForClick();
        }
    }

    //
    // CSierpinski protected data members

    Point[] _ends = new Point[5];
    Dimension _size;
    boolean _clicked;
};
