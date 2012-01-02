import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Class:  Pang 
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Thy heart dost yearn for the days of old, the days of yore.
 * Thou dost ponder endlessly the ways of the world, yet to no
 * avail.  Beeps, boops and bops cannot help thee here.
 * 
 * A deeply magical remake of a classic video game.
 */
public class Pang extends Applet implements Runnable
{
    /* Following are workarounds for font size calculations, as Netscape
     * reports unappealing font height/ascents on varying platforms. */
    static final int FIXED_FONT_HEIGHT = 12; 
    static final int FIXED_FONT_ASCENT = 6; 

    static final String STRING_DEMO = new String("( click to begin game )"); 
    
    static final int MODE_DEMO = 1;  /* Game mode while showing demo */
    static final int MODE_GAME = 2;  /* Game mode while playing game */

    static final int ROLL_MINSTEPS = 400;/* min # screen updates btwn rolls */
    static final int ROLL_MAXSPEED = 5;  /* max speed of screen roll */
    static final int HELP_STEPS = 150;   /* # screen updates before hide help */

    static final int NUM_NOISE = 30;         /* # of noise dots on-screen */

    static final int DELAY_LOST = 50;  /* # screen upd. btwn lost and launch */
    static final int DELAY_UPDATE = 30; /* ms delay between screen upd. */

    boolean   _running;
    Image     _offImage;
    Graphics  _offGraphics;
    Thread    _t;
    Paddle    _comp_pad, _player_pad;
    Ball      _ball;
    Dimension _d;
    int       _mode;
    int       _demo_strwidth;
    VHoldLine _v_line;
    PangText  _title_str;
    boolean   _rolling;
    int       _roll_v, _roll_speed, _steps;
    int       _noise_x[], _noise_y[];    
    int       _lost_steps, _help_steps;
    AudioClip _snd_bop, _snd_bop2, _snd_lost;
    boolean   _show_vhold_line = false;
    boolean   _show_screen_roll = false;
    boolean   _show_noise = false;

    /**
     * init 
     *
     * Initialize the applet.
     */
    public void init ()
    {
	int i;
	String str;

        _d = size();
        _offImage = createImage(_d.width, _d.height);
        _offGraphics = _offImage.getGraphics();

	/* Set up font info */
	_offGraphics.setFont(new Font("Helvetica", Font.PLAIN, 
	  FIXED_FONT_HEIGHT));
	_demo_strwidth = (_offGraphics.getFontMetrics()).stringWidth(STRING_DEMO);

	/* Load sounds */
	_snd_bop = getAudioClip(getCodeBase(), "audio/bop.au");
	_snd_bop2 = getAudioClip(getCodeBase(), "audio/bop2.au");
	_snd_lost = getAudioClip(getCodeBase(), "audio/lost.au");

	/* Create paddles */
	_comp_pad = new Paddle(_d.width - (Paddle.PAD_WIDTH * 2), 
	  (_d.height - Paddle.PAD_HEIGHT) / 2, _d, _snd_bop2);
	_player_pad = new Paddle(Paddle.PAD_WIDTH * 2,
	  (_d.height - Paddle.PAD_HEIGHT) / 2, _d, _snd_bop2);

	/* Create V-hold line */
	_v_line = new VHoldLine(_d);

	/* Create title string */
	_title_str = new PangText(_d.width / 2, _d.height / 2, _d);

	/* Init game data */
	_mode = MODE_DEMO;
	_rolling = false;
	_help_steps = _lost_steps = _steps = _roll_v = 0;
        _noise_x = new int[NUM_NOISE];
        _noise_y = new int[NUM_NOISE];

        for(i = 0; i < NUM_NOISE; i++) {
	    _noise_x[i] = (int) (Math.random() * 1000.0) % _d.width;
	    _noise_y[i] = (int) (Math.random() * 1000.0) % _d.height;
        }

	/* Read applet parameters */

	/* Read V-Hold flag */
	str = getParameter("vHold");
	_show_vhold_line = (str != null && str.equals("true"));

	/* Read screen-roll flag */
	str = getParameter("screenRoll");
	_show_screen_roll = (str != null && str.equals("true"));

	/* Read noise flag */
	str = getParameter("noise");
	_show_noise = (str != null && str.equals("true"));
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
     * startBall 
     *
     * Launch a new ball from the middle of the playing area, with a
     * random h/v velocity.
     */
    public void startBall() {
	int h, v;

	if(Math.random() > 0.5) {
	    _ball = new Ball(_player_pad.h + Paddle.PAD_WIDTH + 
			  Ball.BALL_WIDTH + 5, ((int) (Math.random() * 
			  1000.0) % (_d.height - Ball.BALL_HEIGHT)), _d, 
			  _snd_bop, _snd_lost);
	} else {
	    _ball = new Ball(_comp_pad.h - Ball.BALL_WIDTH - 5,
		  	  ((int) (Math.random() * 1000.0) % (_d.height - 
		  	  Ball.BALL_HEIGHT)), _d, _snd_bop, _snd_lost);
	    _ball.vel_h = -_ball.vel_v;
	}

	if(Math.random() > 0.5) {
	    _ball.vel_v = -_ball.vel_v;
	}

	_lost_steps = 0;
	_ball.setSoundOn(_mode == MODE_GAME);
    }

    /**
     * checkRoll 
     *
     * Check whether to begin new screen roll.  If so, generate random params
     * for the roll.
     */
    public void checkRoll() {
	_steps++;
	if(_steps % ROLL_MINSTEPS == 0) {
	    _steps = 0;
	    _rolling = Math.random() > 0.5;
	    if(_rolling == true) {
	        _roll_speed = (int) (Math.random() * 10.0) % ROLL_MAXSPEED + 1;
	    }
	}
    }

    /**
     * updateRoll 
     *
     * Increment the screen roll and draw lines denoting edge of screen.
     */
    public void updateRoll(Graphics g) {
	/* Draw lines at bottom of screen so roll edge is visible */
	if(Math.random() > 0.5) {
	    g.drawLine(0, _d.height - 1, _d.width, _d.height - 1);
	}
	if(Math.random() > 0.3) {
	    g.drawLine(0, _d.height - 2, _d.width, _d.height - 2);
	}

	_roll_v += _roll_speed;
	if(_roll_v >= _d.height) {
	    _roll_v = 0;
	    _rolling = false;
	}
    }

    /**
     * run 
     *
     * Main body of Pang applet.
     */
    public void run () {
	startBall();

	while(_running) {
	    _ball.move();

	    if(_ball.getStatus() != Ball.STATUS_IN_PLAY) {
		_lost_steps++;
		if(_lost_steps % DELAY_LOST == 0) {
		    startBall();
		    _comp_pad.chooseBrain();
		    _player_pad.chooseBrain();
		}
	    }

	    _comp_pad.lookForBall(_ball);
	    _comp_pad.move();
	    _comp_pad.checkCollision(_ball);

	    if(_mode == MODE_DEMO) {
		_player_pad.lookForBall(_ball);
	        _title_str.move();
	    }
	    _player_pad.move();
	    _player_pad.checkCollision(_ball);

	    if(_show_vhold_line == true) {
	        _v_line.move();
	    }

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
	int i;

	/* Wipe offscreen graphics image */
	_offGraphics.setColor(Color.darkGray);
	_offGraphics.fillRect(0, 0, _d.width, _d.height);
	_offGraphics.setColor(Color.white);

	/* Paint paddles */
	_player_pad.paint(_offGraphics);
	if(_player_pad.getDispHelp() == true) {
	    _help_steps++;
	    if(_help_steps % HELP_STEPS == 0) {
	        _player_pad.setDispHelp(false);
	    }
	}

	_comp_pad.paint(_offGraphics);

	/* Paint ball */
	_ball.paint(_offGraphics);

	/* Draw demo info if appropriate */
	if(_mode == MODE_DEMO) {
	    _offGraphics.drawString(STRING_DEMO,
	      (_d.width - _demo_strwidth) / 2, 
	      (_d.height - FIXED_FONT_HEIGHT) / 2);

	    _title_str.paint(_offGraphics);
	}

	if(_show_noise == true) {
	    _offGraphics.setColor(Color.gray);
            for(i = 0; i < NUM_NOISE; i++)
	        _offGraphics.fillRect(_noise_x[i], _noise_y[i], 2, 2);

            for(i = 0; i < NUM_NOISE; i++) {
                _noise_x[i] = (_noise_x[i] + 
		  (int) (2-Math.random()*3.9))%_d.width;
                _noise_y[i] = (_noise_y[i] + 
		  (int) (2-Math.random()*3.9))%_d.height; 
            }
	}

	/* Perchance we're on a roll...? */
	if(_show_screen_roll == true) {
	    _offGraphics.setColor(Color.lightGray);
	    if(_rolling == true)
	        updateRoll(_offGraphics);
	    else
		checkRoll();
	}

        g.drawImage(_offImage, 0, -_roll_v, null);
        g.drawImage(_offImage, 0, _d.height - _roll_v, null);

	/* Paint V-Hold Line */
	if(_show_vhold_line == true)
	    _v_line.paint(g);
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

    /**
     * mouseUp 
     *
     * Handle mouseUp event.  Starts game play.
     */
    public boolean mouseUp(Event e, int x, int y) {
	if(_mode == MODE_DEMO) {
	    _player_pad.setControl(Paddle.CONTROL_PLAYER);
	    _player_pad.setDispHelp(true);

	    _help_steps = 1;
	    _mode = MODE_GAME;
	    startBall();
	} else {
	    _player_pad.setControl(Paddle.CONTROL_COMPUTER);
	    _player_pad.setDispHelp(false);
	    _mode = MODE_DEMO;
	}

	_ball.setSoundOn(_mode == MODE_GAME);
	_comp_pad.setSoundOn(_mode == MODE_GAME);
	_player_pad.setSoundOn(_mode == MODE_GAME);

	return true;
    }

    /**
     * mouseMove 
     *
     * Handle mouseMove event.  Controls player paddle if game in progress.
     */
    public boolean mouseMove(Event e, int x, int y) {
	if(_mode == MODE_DEMO) 
	    return false;

	_player_pad.setDestination(y);

	return true;
    }
};
