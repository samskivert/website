//
// TestPattern -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class TestPattern extends Base
{
    public void init ()
    {
        setBackground(Color.white);
        super.init();
    }

    public void run ()
    {
        Color[] colors = rainbowPalette(COLORS);

        int width = _offImage.getWidth(null);
        int height = _offImage.getHeight(null);

        for (int x = 0; x < COLORS; x++) {
            _offGraphics.setColor(colors[x]);
            _offGraphics.drawLine(x, 0, x, height);
        }

        repaint();
    }

    final static int COLORS = 64;
}
