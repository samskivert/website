//
// BaseApplet - An applet with an offscreen image for drawing into, and a few
//            other kooky things that turned out to be useful.
//            Created from mdb's DBApplet class.
//
// $Id: BaseApplet.java,v 1.0 1996/12/16 09:09:38 wfk Exp $

import java.applet.Applet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

public abstract class BaseApplet extends Applet implements Runnable
{
    //
    // BaseApplet public member functions

    public void init ()
    {
        Dimension d = size();
        _offImage = createImage(d.width, d.height);
        _offGraphics = _offImage.getGraphics();

        // clear out the background
        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(0, 0, d.width, d.height);
        _offGraphics.setColor(Color.black);
        _offGraphics.setFont(new Font("Helvetica", Font.PLAIN, 10));

        // get some graphics stuff
        _metrics = _offGraphics.getFontMetrics();
        _height = FIXED_FONT_HEIGHT;
	_ascent = FIXED_FONT_ASCENT;

	_srcString = getParameter("srcString");
	_workString = new StringBuffer(_srcString);
    }

    public void start ()
    {
        _running = true;
        Thread t = new Thread(this);
        t.start();
    }

    public void stop ()
    {
        _running = false;
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

    public void title (String line1, String line2)
    {
        Dimension d = size();

        int width = _metrics.stringWidth(line1);
        _offGraphics.drawString(line1, d.width-width-5, 5 + _ascent);

        width = _metrics.stringWidth(line2);
        _offGraphics.drawString(line2, d.width-width-5, 5 + _ascent + _height);
    }

    public void message (String line1, String line2, String line3,
		  String line4)
    {
	Dimension d = size();

        _mwidth = Math.max(_metrics.stringWidth(line1),
                           _metrics.stringWidth(line2));
        _offGraphics.drawString(line1, 5, 5+_ascent);
        _offGraphics.drawString(line2, 5, 5+_ascent+_height);
        _offGraphics.drawString(line3, 5, d.height - (_ascent + _height));
        _offGraphics.drawString(line4, 5, d.height - _ascent);
    }

    public void clearMessage ()
    {
	Dimension d = size();

        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(5, 5, 5+_mwidth, 5+_ascent+_height);
        _offGraphics.fillRect(5, d.height - (_ascent+_height), 
	  5 + d.width, 5+_ascent+(2*_height));
        _offGraphics.setColor(Color.black);
    }

    //
    // BaseApplet protected data members

    boolean _running;
    Image _offImage;
    Graphics _offGraphics;

    FontMetrics _metrics;
    int _height;
    int _ascent;

    int _mwidth;

    /* Following are workarounds for font size calculations, as Netscape
     * reports unappealing font height/ascents on varying platforms. */
    static final int FIXED_FONT_HEIGHT = 12; 
    static final int FIXED_FONT_ASCENT = 6; 

    String _srcString;               // original input string
    StringBuffer _workString;        // current work string
};
