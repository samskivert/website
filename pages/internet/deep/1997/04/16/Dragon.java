//
// Dragon -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class Dragon extends Base
{
    public void init ()
    {
        setBackground(Color.white);
        super.init();
    }

    public void run ()
    {
        double t = Math.random()*100+10, a = Math.PI-t/5000;
        double xx, x = 0, y = 0;

        for (int i = 0; i < 100000; i++) {
            drawPixel(width/2 + (int)(2.0*x), height/2 + (int)(2.0*y));
            xx = y - Math.sin(x);
            y = a - x;
            x = xx;
            if (i%500 == 0) repaint(250);
        }

        repaint();
    }
}
