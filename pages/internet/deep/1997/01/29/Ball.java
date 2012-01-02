import java.awt.Graphics;
import java.awt.Dimension;
import java.applet.AudioClip;

/**
 * Class:  Ball 
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Bouncing ball, with custom move() to handle "losing" the ball off the
 * edge of the screen.  
 */
class Ball extends Bouncer {
    static final int BALL_WIDTH = 10;  /* ball width */
    static final int BALL_HEIGHT = 10; /* ball height */
    static final int MAX_VELOCITY = 8;/* max velocity of ball h/v */

    static final int STATUS_LOST_LEFT = 1;    /* ball lost left of screen */
    static final int STATUS_LOST_RIGHT = 2;   /* ball lost right of screen */
    static final int STATUS_IN_PLAY = 3;      /* ball in play */

    int       status;        /* ball status */
    AudioClip snd_bop;       /* sound played when ball bounces off wall */
    AudioClip snd_lost;      /* sound played when ball is lost */
    boolean   snd_on;        /* whether sound is on */

    /**
     * Ball
     *
     * Constructor, allows setting initial location.
     */
    public Ball(int h, int v, Dimension d, AudioClip snd_bop,
		AudioClip snd_lost) {
	super(h, v, BALL_WIDTH, BALL_HEIGHT, MAX_VELOCITY, d);

	this.snd_bop = snd_bop;
	this.snd_lost = snd_lost;

	snd_on = false;
	status = STATUS_IN_PLAY;
    }

    /**
     * paint
     *
     * Draws the ball in current position/state to given Graphics object.
     */
    public void paint(Graphics g) {
	g.fillRect(h, v, width, height);
    }

    /**
     * move
     *
     * Moves the ball within the specified dimensions.  Ball bounces off
     * top/bottom walls, and marks itself "lost" if it passes off the left/
     * right edges of the dimensions.
     */
    public void move() {
	boolean kill_ball = false;

	h += vel_h;
	v += vel_v;

	/* Bounce ball off top/bottom walls if needed */
	if(v < 0) {
	    v = 0;
	    vel_v = -vel_v;
	    if(snd_on == true && snd_bop != null) {
	        snd_bop.play();
	    }
	} else if(v > d.height - height) {
	    v = d.height - height;
	    vel_v = -vel_v;
	    if(snd_on == true && snd_bop != null) {
	        snd_bop.play();
	    }
	}

	/* Check for lost ball */
	if(h + width < 0 && status == STATUS_IN_PLAY) {
	    status = STATUS_LOST_LEFT;
	    kill_ball = true;
	} else if(h > d.width && status == STATUS_IN_PLAY) {
	    status = STATUS_LOST_RIGHT;
	    kill_ball = true;
	}

	if(kill_ball == true) {
	    if(snd_on == true && snd_lost != null) {
	        snd_lost.play();
	    }

	    h = -20;  /* move ball off-screen */
	    v = (d.height - height) / 2;
	    vel_v = vel_h = 0;
	}
    }


    /**
     * getStatus
     *
     * Returns current status of the ball.
     */
    public int getStatus() {
	return status;
    }

    /**
     * setSoundOn
     *
     * Set whether ball plays sounds.
     */
    public void setSoundOn(boolean snd_on) {
	this.snd_on = snd_on;
    }
};
