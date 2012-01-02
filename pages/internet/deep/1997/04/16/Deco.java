//
// Deco -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class Deco extends Base
{
    public void init ()
    {
        setBackground(Color.black);
        super.init();
    }

    public void run ()
    {
        _colors = rainbowPalette(COLORS);

        while (running()) {
            deco(0, 0, width, height, MAXITERS);
            waitForClick();
        }
    }

    void deco (int x, int y, int wid, int hei, int depth)
    {
        if ((random(MAXITERS) > depth) || (wid < 8) || (hei < 8)) {
            drawRectangle(x, y, wid, hei, _colors[random(COLORS)]);

        } else {
            if (random(100) > 50) {
                deco(x, y, wid/2, hei, depth-1);
                deco(x+wid/2, y, wid/2, hei, depth-1);
            } else {
                deco(x, y, wid, hei/2, depth-1);
                deco(x, y+hei/2, wid, hei/2, depth-1);
            }
        }
    }

    void drawRectangle (int x, int y, int w, int h, Color c)
    {
        _offGraphics.setColor(c);
        _offGraphics.fillRect(x, y, width, height);
        _offGraphics.setColor(Color.black);
        _offGraphics.drawRect(x, y, width, height);
        repaint();
    }

    Color[] _colors;

    final static int COLORS = 64;
    final static int MAXITERS = 10;
}
