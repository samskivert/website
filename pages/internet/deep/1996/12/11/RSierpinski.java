//
// RSierpinski - Regular old Sierpinski's gasket
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1996/12/11/

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;

public class RSierpinski extends DBApplet
{
    //
    // RSierpinski public member functions

    public void init ()
    {
        super.init();
    }

    public void drawShape (int x, int y, int width, int height, int iter)
    {
        if (++_dsCalls % 100 == 0) {
            repaint();
            Thread.yield();
        }

        if (iter == 0) {
            _xs[0] = x;
            _ys[0] = y + height;
            _xs[1] = x + width/2;
            _ys[1] = y;
            _xs[2] = x + width;
            _ys[2] = y + height;
            _offGraphics.fillPolygon(_xs, _ys, 3);
        } else {
            int nw = width/2, nh = height/2;
            // top-center quadrant
            drawShape(x+nw/2, y, nw, nh, iter-1);
            // lower-left quadrant
            drawShape(x, y+nh, nw, nh, iter-1);
            // lower-right quadrant
            drawShape(x+nw, y+nh, nw, nh, iter-1);
        }
    }

    public void run ()
    {
        Dimension d = size();
        int i;

        while (_running) {
            for (i = 1; i < 6; i++) {
                blank();
                message(i + " iters", "Click");
                drawShape(5, 5, d.width-10, d.height-10, i);
                repaint();
                waitForClick();
            }

            blank();
            message(i + " iters", "");
            drawShape(5, 5, d.width-10, d.height-10, i);
            repaint();
            waitForClick();
        }
    }

    public void blank ()
    {
        Dimension d = size();
        // clear out the background
        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(0, 0, d.width, d.height);
        _offGraphics.setColor(Color.black);
        title("Sierpinski's", "Gasket");
    }

    //
    // RSierpinski protected data members

    int _dsCalls = 0;

    int[] _xs = new int[4];
    int[] _ys = new int[4];
};
