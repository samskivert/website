//
// Base -
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1997/04/16/

import java.applet.Applet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import java.util.Random;

public abstract class Base extends Applet implements Runnable
{
    //
    // Base public data members

    // these are initialized to the width and height of the applet (and
    // thus the off screen graphics)
    int width, height;

    //
    // Base public member functions

    public void init ()
    {
        Dimension d = size();
        _offImage = createImage(width = d.width, height = d.height);
        _offGraphics = _offImage.getGraphics();

        clear();

        // get some graphics stuff
        _offGraphics.setFont(new Font("Helvetica", Font.PLAIN, 11));
        _metrics = _offGraphics.getFontMetrics();
        _height = _metrics.getHeight();
        _ascent = _metrics.getAscent();
    }

    public void start ()
    {
        _runner = new Thread(this);
        _runner.start();
    }

    public void stop ()
    {
        _runner = null;
    }

    public boolean running ()
    {
        return _runner == Thread.currentThread();
    }

    public abstract void run ();

    public void update (Graphics g)
    {
        g.drawImage(_offImage, 0, 0, null);
    }

    public void paint (Graphics g)
    {
        g.drawImage(_offImage, 0, 0, null);
    }

    public synchronized void waitForClick ()
    {
        try {
            this.wait();
        } catch (InterruptedException e) {
        }
    }

    public synchronized boolean mouseDown (Event evt, int x, int y)
    {
        this.notify();
        return true;
    }

    public void clear ()
    {
        // clear out the background
        _offGraphics.setColor(getBackground());
        _offGraphics.fillRect(0, 0, width, height);
        _offGraphics.setColor(Color.black);
    }

    // this is pretty easy since Java has HSVtoRGB built in
    Color[] rainbowPalette (int length, float saturation, float value)
    {
        Color[] colors = new Color[length];

        for (int i = 0; i < length; i++) {
            float hue = (float)i / (float)length;
            colors[i] = new Color(Color.HSBtoRGB(hue, saturation, value));
        }

        return colors;
    }

    Color[] rainbowPalette (int length)
    {
        return rainbowPalette(length, 1f, 0.75f);
    }

    void drawPixel (int x, int y)
    {
        _offGraphics.drawLine(x, y, x, y);
    }

    void drawPixel (Color c, int x, int y)
    {
        _offGraphics.setColor(c);
        _offGraphics.drawLine(x, y, x, y);
    }

    public void repaint (long delay)
    {
        super.repaint();
        try { Thread.sleep(delay); } catch (InterruptedException e) {}
    }

    int random (int maxval)
    {
        return Math.abs(_random.nextInt()) % maxval;
    }

    //
    // Base protected data members

    Thread _runner;
    Image _offImage;
    Graphics _offGraphics;

    FontMetrics _metrics;
    int _height;
    int _ascent;

    int _mwidth;

    Random _random = new Random();
};
