//
// Termite - a simulation of termite wood chip gathering behavior
//
// mdb - 01/14/97

import java.awt.Color;
import java.awt.Graphics;

public class Termite extends DBApplet
{
    //
    // Termite public member functions

    public void init ()
    {
        super.init();
        randomize();
        accountForPlatformDifferences();
    }

    public void run ()
    {
        message("Termites and Wood Chips", "Click");
        repaint();
        waitForClick();
        while (running()) {
            iterate();
            renderBoard();
            title("", Integer.toString(_generation));
            sleep(5 + _delay);
        }
    }

    //
    // Termite protected member functions

    void iterate ()
    {
        boolean move = false;

        for (int i = 0; i < NUM_TERMITES; i++) {
            int tx = _termx[i];
            int ty = _termy[i];

            switch (_chips[i]) {
            case 0:
                if (_board[tx][ty] == 1) {
                    _chips[i] = 1;
                    _board[tx][ty] = 0;
                } else {
                    move = true;
                }
                break;

            case 1:
                if (_board[tx][ty] == 1) _chips[i] = 2;
                move = true;
                break;

            case 2:
                if (_board[tx][ty] == 1) {
                    move = true;
                } else {
                    _chips[i] = 0;
                    _board[tx][ty] = 1;
                }
                break;
            }

            if (move) {
                int dir = (int)(Math.random()*7.9);
                // System.out.print(_termx[i] + "/" + _termy[i] + " ==> ");
                // System.out.print(_dirsx[dir] + "/" + _dirsy[dir] + " ==> ");
                _termx[i] = (_termx[i] + _dirsx[dir] + WIDTH) % WIDTH;
                _termy[i] = (_termy[i] + _dirsy[dir] + HEIGHT) % HEIGHT;
                // System.out.println(_termx[i] + "/" + _termy[i]);
            }
        }

        _generation += 1;
    }

    void renderBoard ()
    {
        clear();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (_board[x][y] == 1) {
                    _offGraphics.setColor(Color.yellow);
                    _offGraphics.drawRect(x*7, y*7, 6, 6);
                }
            }
        }

        int tx, ty;
	
        for (int i = 0; i < NUM_TERMITES; i++) {
            tx = 7*_termx[i];
            ty = 7*_termy[i];
            switch (_chips[i]) {
            case 0:
                _offGraphics.setColor(Color.red);
                break;
            case 1:
                _offGraphics.setColor(Color.blue);
                break;
            case 2:
                _offGraphics.setColor(Color.green);
                break;
            }
            _offGraphics.drawRect(tx, ty, 6, 6);
        }

        repaint();
    }

    void randomize ()
    {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (Math.random() < CHIP_LIKELIHOOD) {
                    _board[x][y] = 1;
                } else {
                    _board[x][y] = 0;
                }
            }
        }

        for (int i = 0; i < NUM_TERMITES; i++) {
            _termx[i] = (int)(Math.random()*(WIDTH-1));
            _termy[i] = (int)(Math.random()*(HEIGHT-1));
            // System.out.println("initted to: " + _termx[i] + ", " + _termy[i]);
        }
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
    // Termite protected constants

    final static int NUM_TERMITES = 10;

    final static int WIDTH = 28;
    final static int HEIGHT = 21;

    final static double CHIP_LIKELIHOOD = 0.10;

    final static int[] _dirsx = { -1, -1, -1,  0, 0,  1, 1, 1 };
    final static int[] _dirsy = { -1,  0,  1, -1, 1, -1, 0, 1 };

    //
    // Termite protected data members

    int _generation = 0;
    int _delay = 0;

    int[] _termx = new int[NUM_TERMITES];
    int[] _termy = new int[NUM_TERMITES];
    int[] _chips = new int[NUM_TERMITES];

    int[][] _board = new int[WIDTH][HEIGHT];
};
