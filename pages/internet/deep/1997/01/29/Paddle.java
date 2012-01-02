import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.applet.AudioClip;

/**
 * Class:  Paddle 
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Super chrome-molybdenum paddle, specially designed for optimal
 * crushing-backhand rebound-power when confronted with pixelated
 * blocky ball.  Built-in intelligence perpetrates as astoundingly
 * crafty computer-player.  Brains may be utterly bypassed if
 * necessary, to allow for player-control.  Player-control must be
 * handled by main Applet class, since all mouse events are trapped
 * there.
 */
class Paddle extends Object {
    static final int FIXED_FONT_HEIGHT = 12;

    static final int PAD_WIDTH = 10;    /* paddle width */
    static final int PAD_HEIGHT = 40;   /* paddle height */

    static final int HEAD_UP = 1;       /* paddle heading up */
    static final int HEAD_DOWN = 2;     /* paddle heading down */
    static final int HEAD_STILL = 3;    /* paddle heading still */

    static final int FLAT_BOUNCE_OFFSET = 10; /* big bounce edge offset */
    static final int PAD_SPEED = 5;           /* speed paddle moves in pixels */
    static final double MIN_LOOK_FREQ = .6;  /* minimum % time paddle looks 
                                                for ball */
    static final double WAIT_LOOK_FREQ = .4;  /* minimum % time paddle only
					 	 seeks ball on its court side */

    static final int CONTROL_COMPUTER = 1;    /* paddle control by computer */
    static final int CONTROL_PLAYER = 2;      /* paddle control by player */

    static final int BRAIN_KILL = 1;          /* computer attempts kill */
    static final int BRAIN_SAFE = 2;          /* computer attempts to be safe */

    int   h, v;       /* top-left corner of the paddle */
    int   heading;    /* current heading of paddle */
    double   look_freq;  /* % frequency paddle looks for ball */
    Rectangle rb;     /* collision-detection rectangle of ball */
    Rectangle rp;     /* collision-detection rectangle of paddle */
    int   brain;      /* current computer-player tactics */
    Dimension d;      /* dimensions of screen */
    int   dest_v;     /* last v location mouse was seen for paddle control */
    int   control;    /* state of paddle control; computer or player */
    boolean wait_look;/* whether computer-control looks for ball
		         when ball is on other side of court */
    boolean disp_help;/* whether to display text showing player paddle */
    boolean snd_on;   /* whether sound is on */
    AudioClip snd_bop2; /* snd played when ball hits paddle */

    /**
     * Paddle
     *
     * Constructor, allows setting initial location.
     */
    public Paddle(int h, int v, Dimension d, AudioClip snd_bop2) {
	super();

	this.h = h;
	this.v = v;
	this.d = d;
	this.snd_bop2 = snd_bop2;

	heading = HEAD_STILL;
	control = CONTROL_COMPUTER;
	disp_help = false;
	snd_on = false;
	chooseBrain();

	/* Initialize collision-test rectangles */
	rb = new Rectangle();
	rp = new Rectangle();

	rb.width = Ball.BALL_WIDTH;
	rb.height = Ball.BALL_HEIGHT;
	rp.width = PAD_WIDTH;
	rp.height = PAD_HEIGHT;
    }

    /**
     * paint
     *
     * Draws paddle in current position and state to given Graphics object.
     */
    public void paint(Graphics g) {
	g.fillRect(h, v, PAD_WIDTH, PAD_HEIGHT);

	if(disp_help == true) {
	    g.drawString("<- You", h + PAD_WIDTH + 5, v + (PAD_HEIGHT / 2) + 3);
	}
    }

    /**
     * move
     *
     * Moves paddle in current heading, limiting to on-screen area.
     */
    public void move() {
	if(control == CONTROL_COMPUTER) {

	    switch(heading) {
	      case HEAD_UP:
	        v -= PAD_SPEED;
	        break;

	      case HEAD_DOWN:
	        v += PAD_SPEED;
	        break;

	      case HEAD_STILL:
	        break;
	    }

	} else {  /* control == CONTROL_PLAYER */

	    if(v + (PAD_HEIGHT / 2) > dest_v + PAD_SPEED) {
		heading = HEAD_UP;
	        v -= PAD_SPEED; 
	    } else if(v + (PAD_HEIGHT / 2) < dest_v - PAD_SPEED) {
		heading = HEAD_DOWN;
	        v += PAD_SPEED; 
	    } else {
		heading = HEAD_STILL;
	        v = dest_v - (PAD_HEIGHT / 2);
	    }

        }

        /* Keep paddle on-screen */
	if(v < 0) {
	    v = 0;
	    heading = HEAD_STILL;
	}

	if(v > d.height - PAD_HEIGHT) {
	    v = d.height - PAD_HEIGHT;
	    heading = HEAD_STILL;
	}
    }

    /**
     * checkCollision
     *
     * Checks whether ball collided with paddle; rebounds ball if so.
     */
    public void checkCollision(Ball b) {
	int orig_vel_h;
	double rnd;

	/* Set rectangles to current ball/paddle locations */
	rb.x = b.h; 
	rb.y = b.v;
	rp.x = h; 
	rp.y = v;

	if(rb.intersects(rp)) {

	    /* Bounce ball off paddle */

	    orig_vel_h = b.vel_h;

	    if(b.v + Ball.BALL_HEIGHT <= v + FLAT_BOUNCE_OFFSET) {

		/* Ball bounces at extreme angle upward */
	        b.setVelocity((h < (d.width / 2)) ? Math.abs(b.vel_h) : 
		  -Math.abs(b.vel_h), -(Math.abs(b.vel_v * 2)));

	    } else if(b.v >= v + PAD_HEIGHT - FLAT_BOUNCE_OFFSET) {

		/* Ball bounces at extreme angle downward */
	        b.setVelocity((h < (d.width / 2)) ? Math.abs(b.vel_h) : 
		  -Math.abs(b.vel_h), (Math.abs(b.vel_v * 2)));

	    } else {

		/* Ball bounces flat */
	        b.setVelocity((h < (d.width / 2)) ? Math.abs(b.vel_h) : 
		  -Math.abs(b.vel_h), b.vel_v);
		
//		if(control == CONTROL_COMPUTER) {
//		    chooseBrain();
//		}
	    }

	    /* Add slight randomness to bounce */
	    rnd = Math.random();
	    if(rnd > 0.6) {
		if(Math.random() > 0.5) {
		    b.setVelocity(b.vel_h - 1, b.vel_v);
		} else {
		    b.setVelocity(b.vel_h, b.vel_v - 1);
		}
	    } else if(rnd < 0.4) {
		if(Math.random() > 0.5) {
		    b.setVelocity(b.vel_h, b.vel_v - 1);
		} else {
		    b.setVelocity(b.vel_h - 1, b.vel_v);
		}
	    } else {
	        b.setVelocity(b.vel_h - 1, b.vel_v - 1);
	    }
	
	    /* Keep ball at minimum velocity of 1 in h/v */
	    if(b.vel_h == 0) {
		b.setVelocity((Math.random() > 0.5) ? 1 : -1, b.vel_v);
	    }
	    if(b.vel_v == 0) {
	        b.setVelocity(b.vel_h, (Math.random() > 0.5) ? 1 : -1);
	    }

	    if(snd_on == true && snd_bop2 != null) {
	        snd_bop2.play();
	    }
	}
    }

    /**
     * lookForBall
     *
     * Perform computerized paddle-control.  Paddle-player has look_freq 
     * percent chance of reassessing its current heading based on where
     * the ball is.
     */
    public void lookForBall(Ball b) {
	int pc_h, pc_v;  /* center of paddle */
	int bc_h, bc_v;  /* center of ball */

	pc_v = v + (PAD_HEIGHT / 2);
	pc_h = h + (PAD_WIDTH / 2);
	bc_v = b.v + (Ball.BALL_HEIGHT / 2);
	bc_h = b.h + (Ball.BALL_WIDTH / 2);

	if(Math.random() <= look_freq) {
	    
	    if(wait_look == true) {
		/* only look for ball if it's on our side of the court */

	        if(pc_h < d.width / 2) { /* left paddle */
	            if(!(bc_h < (2 * d.width / 3) && b.vel_h < 0))
		        return;
	        } else { /* right paddle */
	            if(!(bc_h > d.width / 3 && b.vel_h > 0))
		        return;
	        }
	    }

	    if(bc_v < pc_v) { /* ball is above us */

		switch(brain) {
		  case BRAIN_KILL:
		    if(b.v + Ball.BALL_HEIGHT >= v) {
		        /* Ball is over our top edge, so stop paddle */
		        heading = HEAD_STILL;
		    } else {
	                heading = HEAD_UP;
		    }
		    break;

		  case BRAIN_SAFE:
		  default:
	            heading = HEAD_UP;
		    break;
		}

	    } else if(bc_v > pc_v) { /* ball is below us */

		switch(brain) {
		  case BRAIN_KILL:
		    if(b.v <= v + PAD_HEIGHT) {
		        /* Ball is over our bottom edge, so stop paddle */
		        heading = HEAD_STILL;
		    } else {
	                heading = HEAD_DOWN;
		    }
		    break;

		  case BRAIN_SAFE:
		  default:
	            heading = HEAD_DOWN;
		    break;
		}

	    } else { /* Ball is centered over paddle, so stop paddle */
		heading = HEAD_STILL;
	    }
	}
    }

    /**
     * setControl
     *
     * Sets paddle controller.
     */
    public void setControl(int control) {
	this.control = control;
    }

    /**
     * chooseBrain
     *
     * Choose a random brain to control the computer-play of paddle,
     * change frequency paddle looks for the ball, and change whether
     * paddle only moves if ball is on its side of the court.
     */
    public void chooseBrain() {
	if(Math.random() > 0.5) {
	    brain = BRAIN_KILL;
	} else {
	    brain = BRAIN_SAFE;
	}

	wait_look = (Math.random() < WAIT_LOOK_FREQ);
	    
	look_freq = Math.random() + MIN_LOOK_FREQ;
	if(look_freq > 1.0)
	    look_freq = 1.0;
    }

    /**
     * setDestination
     *
     * Sets destination v location of paddle.  Only used for paddle movement
     * when controlled by player.
     */
    public void setDestination(int v) {
	dest_v = v;
    }

    /**
     * setDispHelp
     *
     * Sets whether help note showing which paddle is player is displayed.
     */
    public void setDispHelp(boolean disp_help) {
	this.disp_help = disp_help;
    }

    /**
     * getDispHelp
     *
     * Returns whether help note is displayed.
     */
    public boolean getDispHelp() {
	return disp_help;
    }

    /**
     * setSoundOn
     *
     * Set whether paddle plays sounds.
     */
    public void setSoundOn(boolean snd_on) {
	this.snd_on = snd_on;
    }
};
