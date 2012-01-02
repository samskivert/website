//
// Plasma -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class Plasma extends Base
{
    public void init ()
    {
        setBackground(Color.white);
        super.init();
    }

    public void run ()
    {
        while (running()) {
            for (int depth = 6; depth >= 0; --depth) {
                for (int y = 0; y < SIZE; y += (1 << depth)) {
                    int ny = y + (1 << depth), hy = y + (1 << depth)/2;
                    for (int x = 0; x < SIZE; x += (1 << depth)) {
                        int nx = x + (1 << depth), hx = x + (1 << depth)/2;
                        setPixel(mutate(avg2(x, y, nx, y), depth), hx, y);
                        setPixel(mutate(avg2(x, y, x, ny), depth), x, hy);
                        setPixel(mutate(avg4(x, y, nx, ny), depth), hx, y);
                    }
                }
                repaint(150);
            }

            waitForClick();
        }
    }

    int mutate (int value, int depth)
    {
        return (value + random(_scale[depth]) -
                _scale[depth]/2 + COLORS) % COLORS;
    }

    int avg2 (int x0, int y0, int x1, int y1)
    {
        return (getPixel(x0, y0) + getPixel(x1, y1))/2;
    }

    int avg4 (int x0, int y0, int x1, int y1)
    {
        return (getPixel(x0, y0) + getPixel(x0, y1) +
                getPixel(x1, y0) + getPixel(x1, y1))/4;
    }

    void setPixel (int cidx, int x, int y)
    {
        if ((x < SIZE) && (y < SIZE)) _pixels[x][y] = cidx;
        _offGraphics.setColor(_colors[cidx]);
        _offGraphics.drawLine(33+x, 8+y, 33+x, 8+y);
    }

    int getPixel (int x, int y)
    {
        return ((x < SIZE) && (y < SIZE)) ? _pixels[x][y] : 0;
    }

    final static int[] _scale = { 1, 2, 3, 5, 8, 13, 21, 34 };

    final static int SIZE = 128;
    int[][] _pixels = new int[SIZE][SIZE];

    final static int COLORS = 64;
    Color[] _colors = rainbowPalette(COLORS);
}
