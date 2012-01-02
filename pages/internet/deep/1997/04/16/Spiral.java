//
// Spiral -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class Spiral extends Base
{
    public void init ()
    {
        setBackground(Color.white);
        super.init();
    }

    public void run ()
    {
        Color[] colors = rainbowPalette(COLORS);

        double side = (double)Math.min(width, height), angle = 0.0;
        for (double radius = 0.05; radius < 0.5; radius += 0.001) {
            drawCircle((int)(radius * Math.sin(angle) * side) + width/2,
                       (int)(radius * Math.cos(angle) * side) + height/2,
                       Math.max(side / 25.0 * Math.sqrt(radius), 1.0),
                       colors[(int)(radius * 2 * (double)COLORS)]);
            angle = angle + GOLDEN_ANGLE;
        }

        repaint();
    }

    void drawCircle (int x, int y, double size, Color c)
    {
        if (size > 0.5) {
            _offGraphics.setColor(c);
            _offGraphics.drawOval(x, y, (int)size, (int)size);
        } else {
            drawPixel(c, x, y);
        }
    }

    final static int COLORS = 64;

    final static double GOLDEN_NUMBER = (Math.sqrt(5.0) - 1.0) / 2.0;
    final static double GOLDEN_ANGLE =  2 * Math.PI * (1.0 - GOLDEN_NUMBER);
}
