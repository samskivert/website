//
// Grid -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;

public class Grid extends Base
{
    public void init ()
    {
        setBackground(Color.white);
        super.init();
    }

    public void run ()
    {
        for (int x = 0, y = 0; x < width;
             x += width/LINES, y += height/LINES) {
            drawLine(0, y, x, height);
            drawLine(width, height-y, width-x, 0);
        }

        repaint();
    }

    void drawLine (int x1, int y1, int x2, int y2)
    {
        _offGraphics.drawLine(x1, y1, x2, y2);
    }

    final static int LINES = 15;
}
