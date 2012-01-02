import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

/**
 * Class:  VHoldLine 
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Thick gray line which appears periodically on game screen, moving at
 * varying speeds and in varying direction (up/down.)  Nasty thing which
 * gets in the way.  Probably irritates many game-players.  Hah!  We
 * leer evilly in your direction, for thou shalt never discover the deeper 
 * purpose of this object.
 *
 * Credits: Thanks to PSP for leaving me his television when he moved to
 * Seattle, providing the necessary inspiration for this feature.
 */
class VHoldLine extends Object {
    static final int MAX_VELOCITY = 2;      /* max speed of line */
    static final double VIS_FREQ = .5;      /* % chance line visible */
    static final int HEAD_UP = 1;           /* line heading up screen */
    static final int HEAD_DOWN = 2;         /* line heading down screen */
    static final int SPEED_STEPS = 300;     /* # line-moves between new speed */

    int   v;             /* v-loc of line */
    int   direction;     /* direction line is travelling */
    int   vel_v;         /* speed line is travelling */
    int   steps;         /* # times line has been moved since speed variance */
    boolean visible;     /* whether line is currently visible */
    Dimension d;

    /**
     * VHoldLine
     *
     * Constructor.
     */
    public VHoldLine(Dimension d) {
	super();

	this.d = d;

	v = d.height / 3;

	if(Math.random() > 0.5) {
	    vel_v = -1;
	    direction = HEAD_UP;
	} else {
	    vel_v = 1;
	    direction = HEAD_DOWN;
	}

	steps = 0;
	visible = false;
    }

    /**
     * paint
     *
     * Draws the line to the given Graphics object.
     */
    public void paint(Graphics g) {
	Color old_col;
	int i, j;

	if(visible == false) {
	    return;
	}

	old_col = g.getColor();

	g.setColor(Color.lightGray);
	g.fillRect(0, v, d.width, 5);
	g.setColor(old_col);
    }

    /**
     * move
     *
     * Moves the line within the specified dimensions.  
     */
    public void move() {
	steps++;

	if(steps % SPEED_STEPS == 0) {
	    visible = (Math.random() > VIS_FREQ);

	    /* Choose new velocity */
	    steps = 0;
	    vel_v = (int) (Math.random() * 10.0) % MAX_VELOCITY;
	    if(Math.random() > 0.5) {
		direction = HEAD_DOWN;
	    } else {
		vel_v = -vel_v;
		direction = HEAD_UP;
	    }
	}

	if(visible == false) {
	    return;
	}

	v += vel_v;

	/* Keep line on-screen */
	if(direction == HEAD_UP && v < 0) {
	    v = d.height;
	} else if(direction == HEAD_DOWN && v > d.height) {
	    v = 0;
	}
    }
};
