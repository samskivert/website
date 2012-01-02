//
// Circles -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class Circles extends Base
{
    public void init ()
    {
        setBackground(Color.white);
        super.init();
    }

    public void run ()
    {
        Color[] colors = rainbowPalette(COLORS);

        while (running()) {

            int factor = random(10) + 1;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    drawPixel(colors[((x*x+y*y)/factor) % COLORS], x, y);
                }
            }

            repaint();
            waitForClick();
        }
    }

    final static int COLORS = 64;
}
