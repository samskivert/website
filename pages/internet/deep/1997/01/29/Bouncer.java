import java.awt.Graphics;
import java.awt.Dimension;

/**
 * Class:  Bouncer 
 * Author: Walter Korman
 * Date:   1/26/97
 *
 * Base class for bouncing objects.  Very rubbery.
 */
abstract class Bouncer extends Object {
    int   h, v;          /* top-left corner of object */
    int   vel_h, vel_v;  /* velocity of object */
    Dimension d;
    int   width;
    int   height;
    int   max_vel;

    /**
     * Bouncer
     *
     * Constructor, allows setting initial location.
     */
    public Bouncer(int h, int v, int width, int height, int max_vel, 
	     Dimension d) {
	super();

	this.h = h;
	this.v = v;
	this.d = d;
	this.width = width;
	this.height = height;
	this.max_vel = max_vel;

	/* Initialize default velocity */
	vel_h = (max_vel / 2) + 
	  ((int) (Math.random() * 10.0) % (max_vel / 2));
	vel_v = (max_vel / 2) + 
	  ((int) (Math.random() * 10.0) % (max_vel / 2));
    }

    /**
     * paint
     *
     * Draws the object in current position/state to given Graphics object.
     */
    public abstract void paint(Graphics g);

    /**
     * move
     *
     * Moves the object within the specified dimensions.  Bounces off all walls.
     */
    public void move() {
	h += vel_h;
	v += vel_v;

	/* Keep object on-screen */
	if(v < 0) {
	    v = 0;
	    vel_v = -vel_v;
	} else if(v > d.height - height) {
	    v = d.height - height;
	    vel_v = -vel_v;
	}

	if(h < 0) {
	    h = 0;
	    vel_h = -vel_h;
	} else if(h > d.width - width) {
	    h = d.width - width;
	    vel_h = -vel_h;
	}
    }

    /**
     * setVelocity
     *
     * Sets the velocity, limiting bounds to +/- max_vel.
     */
    public void setVelocity(int h, int v) {
	vel_h = h;
	vel_v = v;

	if(Math.abs(vel_h) > max_vel) {
	    if(vel_h > 0) {
	        vel_h = max_vel;
	    } else {
		vel_h = -max_vel;
	    }
	}

	if(Math.abs(vel_v) > max_vel) {
	    if(vel_v > 0) {
	        vel_v = max_vel;
	    } else { 
		vel_v = -max_vel;
	    }
	}
    }
};
