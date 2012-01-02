//
// View -
//
// mdb - 02/11/97

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;

public class View extends java.awt.Canvas
{
    public static final int SCALE = 4;

    public View ()
    {
        // adjust our size
        reshape(0, 0, Environment.WIDTH*SCALE, Environment.HEIGHT*SCALE);

        // generate a palette of reds
        for (int i = 0; i < 16; i++) _reds[i] = new Color(16*i, 0, 0);
    }

    public void addNotify ()
    {
        super.addNotify();

        Dimension d = size();
        _offImage = createImage(d.width, d.height);
        _offGraphics = _offImage.getGraphics();
        clear();
    }

    public void renderFood ()
    {
        clear();

        _offGraphics.setColor(Color.yellow);
        for (int x = 0; x < Environment.WIDTH; x++) {
            for (int y = 0; y < Environment.HEIGHT; y++) {
                if (Environment.grid[x][y] == Environment.FOOD) {
                    _offGraphics.fillRect(SCALE*x, SCALE*y, SCALE, SCALE);
                }
            }
        }

        repaint();
    }

    public void eraseOrganism (Organism o)
    {
        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(SCALE*o.x, SCALE*o.y, SCALE, SCALE);
        repaint(SCALE*o.x, SCALE*o.y, SCALE, SCALE);
    }

    public void drawOrganism (Organism o)
    {
        int br = o.energyUnits / 8;
        if (br > 15) br = 15;
        _offGraphics.setColor(_reds[br]);
        _offGraphics.fillRect(SCALE*o.x, SCALE*o.y, SCALE, SCALE);
        repaint(SCALE*o.x, SCALE*o.y, SCALE, SCALE);
    }

    public void update (Graphics g)
    {
        g.drawImage(_offImage, 0, 0, null);
    }

    public void paint (Graphics g)
    {
        g.drawImage(_offImage, 0, 0, null);
    }

    public void clear ()
    {
        Dimension d = size();
        _offGraphics.setColor(Color.white);
        _offGraphics.fillRect(0, 0, d.width, d.height);
    }

    public synchronized void waitForClick ()
    {
        try {
            this.wait();
            _clicked = false;
        } catch (InterruptedException e) {
        }
    }

    public synchronized boolean mouseDown (Event evt, int x, int y)
    {
        _clicked = true;
        this.notify();
        return true;
    }

    Image _offImage;
    Graphics _offGraphics;

    Color[] _reds = new Color[16];

    boolean _clicked;
}
