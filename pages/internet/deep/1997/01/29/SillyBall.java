import java.awt.Graphics;
import java.awt.Dimension;

/**
 * Class:  SillyBall 
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Simple bouncing ball, inherits all base functionality from Bouncer.
 */
class SillyBall extends Bouncer {
    static final int BALL_WIDTH = 10;  /* ball width */
    static final int BALL_HEIGHT = 10; /* ball height */
    static final int MAX_VELOCITY = 8;/* max velocity of ball h/v */

    /**
     * Ball
     *
     * Constructor, allows setting initial location.
     */
    public SillyBall(int h, int v, Dimension d) {
	super(h, v, BALL_WIDTH, BALL_HEIGHT, MAX_VELOCITY, d);
    }

    /**
     * paint
     *
     * Draws the ball in current position and state to given Graphics object.
     */
    public void paint(Graphics g) {
	g.fillRect(h, v, width, height);
    }
};
