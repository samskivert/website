//
// PSierpinski - Draw Sierpinski's gasket with Jonathan Payne's head!
//
// Author: Michael D. Bayne
// Column: http://www.go2net.com/internet/deep/1996/12/11/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.awt.Polygon;

import java.net.URL;
import java.net.MalformedURLException;

public class PSierpinski extends DBApplet implements ImageObserver
{
    //
    // PSierpinski public member functions

    public void init ()
    {
        super.init();
        _size = size();

        try {
            URL imgurl = new URL(getDocumentBase(),
                              "/internet/deep/1996/12/11/payne.gif");
            _image = getImage(imgurl);

        } catch (MalformedURLException e) {
            System.out.println("Couldn't load payne image. :(");
        }
    }

    public synchronized void drawShape (int x, int y,
                                        int width, int height, int iter)
    {
        if (iter == 0) {
            if (_scaled) {
                _offGraphics.copyArea(_x, _y, _w, _h, x - _x, y - _y);

            } else {
                _x = x;
                _y = y;
                _w = width;
                _h = height;
                _offGraphics.drawImage(_image, _x, _y, _w, _h, this);
                _scaled = true;
            }

            repaint(x, y, _w, _h);
            Thread.yield();

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

        while (_running) {
            for (_iter = 1; _iter < MAX_ITERATIONS; _iter++) {
                blank();
                message(_iter + " iters", "Click");
                drawShape(5, 5, _size.width-10, _size.height-10, _iter);
                waitForClick();
            }

            blank();
            message(_iter + " iters", "");
            drawShape(5, 5, _size.width-10, _size.height-10, _iter);
            waitForClick();
        }
    }

    public void blank ()
    {
        Dimension d = size();
        // clear out the background
        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(0, 0, _size.width, _size.height);
        _offGraphics.setColor(Color.black);
        _scaled = false;
        title("Payne's", "Gasket?");
        repaint();
        Thread.yield();
    }

    public boolean imageUpdate (Image img, int infoflags, int x, int y,
                                int width, int height)
    {
        long currentTime = System.currentTimeMillis();
        boolean done = ((infoflags & ImageObserver.ALLBITS) != 0);

        if ((currentTime - _lastImageRefresh > MINIMUM_REFRESH_RATE) || done) {
            _imageDirty = true;
            _lastImageRefresh = currentTime;
            _offGraphics.drawImage(img, _x, _y, _w, _h, null);
            drawShape(5, 5, _size.width-10, _size.height-10, _iter);
        }

        return !done;
    }

    //
    // PSierpinski protected constants

    final static int MAX_ITERATIONS = 7;

    final static long MINIMUM_REFRESH_RATE = 500l;

    //
    // PSierpinski protected data members

    int _iter;

    Dimension _size;

    Image _image;
    boolean _imageDirty = true;
    long _lastImageRefresh = 0;

    boolean _scaled;
    int _x, _y, _w, _h;
};
