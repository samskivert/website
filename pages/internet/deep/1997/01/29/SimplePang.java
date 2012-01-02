import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Class:  SimplePang 
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Displays an animation of a ball, bouncing endlessly to and fro.
 */
public class SimplePang extends Applet implements Runnable
{
    /* Following are workarounds for font size calculations, as Netscape
     * reports unappealing font height/ascents on varying platforms. */
    static final int FIXED_FONT_HEIGHT = 12; 
    static final int FIXED_FONT_ASCENT = 6; 

    static final String STRING_TO = new String("to...");
    static final String STRING_FRO = new String("fro...");

    static final int STRING_STEPS = 75;  /* # updates between string switch */

    static final int DELAY_UPDATE = 30;   /* ms delay between screen updates */

    boolean   _running;
    Image     _offImage;
    Graphics  _offGraphics;
    Thread    _t;
    SillyBall _ball;
    Dimension _d;
    String    _cur_string;
    int       _steps;

    /**
     * init 
     *
     * Initialize the applet.
     */
    public void init ()
    {
        _d = size();
        _offImage = createImage(_d.width, _d.height);
        _offGraphics = _offImage.getGraphics();

	_offGraphics.setFont(new Font("Helvetica", Font.PLAIN, 
	  FIXED_FONT_HEIGHT));

	_ball = new SillyBall(_d.width / 2, _d.height / 2, _d);

	_steps = 0;
	_cur_string = STRING_TO;
    }

    /**
     * start 
     *
     * Start the main applet thread.
     */
    public void start ()
    {
        _running = true;
        _t = new Thread(this);
        _t.start();
    }

    /**
     * stop 
     *
     * Stop the applet.
     */
    public void stop ()
    {
        _running = false;
    }

    /**
     * run 
     *
     * Main body of SimplePang applet.
     */
    public void run () {
	while(_running) {
	    _steps++;
	    if(_steps % STRING_STEPS == 0) {
		if(_cur_string == STRING_TO) {
		    _cur_string = STRING_FRO;
		} else {
		    _cur_string = STRING_TO;
		}

		_steps = 0;
	    }
	    
	    _ball.move();

	    repaint();

	    try {
		_t.sleep(DELAY_UPDATE);
	    } catch(InterruptedException e) { }
	}
    }

    /**
     * update 
     *
     * Draw current game state into offscreen, and copy offscreen to
     * main applet display.
     */
    public void update (Graphics g)
    {
	/* Wipe offscreen graphics image */
	_offGraphics.setColor(Color.darkGray);
	_offGraphics.fillRect(0, 0, _d.width, _d.height);
	_offGraphics.setColor(Color.white);

	/* Draw text in middle of screen */
	_offGraphics.drawString(_cur_string, (_d.width - 
	  (_offGraphics.getFontMetrics()).stringWidth(_cur_string)) / 2,
	  (_d.height - FIXED_FONT_HEIGHT) / 2);

	/* Paint ball */
	_ball.paint(_offGraphics);

        g.drawImage(_offImage, 0, 0, null);
    }

    /**
     * paint 
     *
     * Paint the applet, drawing offscreen to screen.
     */
    public void paint (Graphics g)
    {
        g.drawImage(_offImage, 0, 0, null);
    }
};

