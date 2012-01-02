//
// Life - an implementation of conway's game of life. this applet performs a
//        little deep magic of its own to obtain the refresh speed that it does
//
// mdb - 01/12/97

import java.awt.Graphics;

import java.util.StringTokenizer;

public class Life extends DBApplet
{
    //
    // Life public member functions

    public void init ()
    {
        String param;

        super.init();

        if (getParameter("random") != null) _random = true;
        if (getParameter("showgens") != null) _showgens = true;
        if (getParameter("showtitle") != null) _showtitle = true;

        accountForPlatformDifferences();

        if ((param = getParameter("delay")) != null) {
            try {
                _delay = Integer.parseInt(param);
            } catch (NumberFormatException e) {
            }
        }

        int i = 0;
        while ((param = getParameter("organism"+i)) != null) {
            StringTokenizer tok = new StringTokenizer(param, ",");

            try {
                int x = Integer.parseInt(tok.nextToken());
                int y = Integer.parseInt(tok.nextToken());

                while (tok.hasMoreTokens()) {
                    String cells = tok.nextToken();

                    for (int dx = 0; dx < cells.length(); dx++) {
                        if (cells.charAt(dx) == '1') {
                            createLife(x+dx, y);
                        }
                    }

                    y += 1;
                }

            } catch (NumberFormatException e) {
                System.err.println("Error parsing organism: " + param);
            }

            i += 1;
        }

        if (_random) randomize();

        copy();
        renderState();

        if (_showtitle) message("Conway's Game of Life", "Click");
    }

    public void run ()
    {
        repaint();
        if (!_random) waitForClick();
        if (_showtitle) clearMessage();

        while (running()) {
            iterate();
            renderState();
            // give Java some time to repaint
            sleep(5 + _delay);
            // pause if the user clicks the mouse
            if (clicked()) {
                if (_random) randomize();
                else waitForClick();
            }
        }
    }

    //
    // Life protected member functions

    void createLife (int x, int y)
    {
        _smasks[y] |= (1L<<x);
        _source[(y+1)*MMWIDTH+x+1] = 1;
    }

    void copy ()
    {
        _source = _cells[_generation%2];
        _target = _cells[1-_generation%2];
        System.arraycopy(_source, 0, _target, 0, MMWIDTH*MMHEIGHT);

        _smasks = _masks[_generation%2];
        _tmasks = _masks[1-_generation%2];
        System.arraycopy(_smasks, 0, _tmasks, 0, HEIGHT);
    }

    void iterate ()
    {
        copy();

        long mask;
        int neighbors;

        for (int y = 0, offset = MMWIDTH+1; y < HEIGHT; y++) {
            mask = generateMask(y);

            for (int x = 0; x < WIDTH; x++, offset++) {
                if ((mask & (1L<<x)) != 0) {
                    neighbors = livingNeighbors(offset);

                    if (_source[offset] != 0) {
                        if ((neighbors != 2) && (neighbors != 3)) {
                            _target[offset] = 0;
                            _tmasks[y] &= ~(1L<<x);
                        } else {
                            _target[offset] = 1;
                        }

                    } else if (neighbors == 3) {
                        _target[offset] = 1;
                        _tmasks[y] |= (1L<<x);
                    }
                }
            }

            offset += 2;
        }

        _generation++;
    }

    void randomize ()
    {
        for (int y = 0, offset = MMWIDTH+1; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++, offset++) {
                if (Math.random() > 0.5) {
                    _source[offset] = _target[offset] = 1;
                    _smasks[y] |= (1L<<x);
                    _tmasks[y] |= (1L<<x);
                } else {
                    _source[offset] = _target[offset] = 0;
                    _smasks[y] &= ~(1L<<x);
                    _tmasks[y] &= ~(1L<<x);
                }
            }
            offset += 2;
        }
    }

    void renderState ()
    {
        int value;

        clear();
        if (_showgens) title(Integer.toString(_generation), "");

        for (int y = YOFF, offset = MMWIDTH+1; y < DHEIGHT+YOFF; y += 3) {
            for (int x = XOFF; x < DWIDTH+XOFF; x += 3, offset++) {
                value = _target[offset];
                if (value != 0) _offGraphics.drawRect(x, y, 1, 1);
            }

            // skip two dead cells
            offset += 2;
        }

        repaint();
    }

    int livingNeighbors (int offset)
    {
        int position = offset - MMWIDTH - 1;
        int value = _source[position++] + _source[position++] +
            _source[position];

        position += WIDTH;
        value = value + _source[position++] + _source[++position];

        position += WIDTH;
        return value + _source[position++] + _source[position++] +
            _source[position];
    }

    long generateMask (int y)
    {
        long masksy = _smasks[y];
        long mask = masksy;

        // get neighbors above and below
        if (y > 0) {
            long tm = _smasks[y-1];
            mask |= tm;
            mask |= (tm<<1);
            mask |= (tm>>1);
        }

        if (y < mHEIGHT) {
            long tm = _smasks[y+1];
            mask |= tm;
            mask |= (tm<<1);
            mask |= (tm>>1);
        }

        // get neightbors to left and right
        mask |= (masksy>>1);
        return mask | (masksy<<1);
    }

    void accountForPlatformDifferences ()
    {
        String osname = System.getProperty("os.name");

        // i absolutely love the platform independance of java
        if (osname.indexOf("Windows 95") != -1) {
            if (System.getProperty("java.vendor").indexOf("Netscape") != -1) {
                _delay = 0;
            } else {
                _delay = 0;
            }
        } else if (osname.indexOf("Mac") != -1) {
            _delay = 25;
        } else if (osname.indexOf("SunOS") != -1) {
            _delay = 75;
        }
    }

    //
    // Life protected data members

    int[][] _cells = new int[2][MMWIDTH*MMHEIGHT];

    int[] _source = _cells[0];
    int[] _target = _cells[1];

    long[][] _masks = new long[2][HEIGHT];
    long[] _smasks = _masks[0];
    long[] _tmasks = _masks[1];

    int _generation;
    int _delay = 0;

    boolean _random = false;
    boolean _showgens = false;
    boolean _showtitle = false;

    //
    // Life protected constants

    // the WIDTH must be smaller than 64 because that's how many bits we have
    // in a java long

    final static int  mWIDTH = 62; // i don't trust the compiler to combine
    final static int   WIDTH = 63; // constant expressions. i'm such a freak
    final static int MMWIDTH = 65;
    final static int  DWIDTH = 189;

    final static int  mHEIGHT = 47;
    final static int   HEIGHT = 48;
    final static int MMHEIGHT = 50;
    final static int  DHEIGHT = 144;

    final static int XOFF = 3;
    final static int YOFF = 1;
};
