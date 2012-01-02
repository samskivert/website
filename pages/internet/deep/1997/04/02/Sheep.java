import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Class:         Sheep
 * Author:        Walter Korman
 * Date:          3/4/97
 * Last Modified: 3/31/97
 *
 * A poor, innocent, defenseless-looking sheep.  The large, dark eyes peer
 * at you as if begging your forgiveness.  The bleating cries of the sheep
 * resonate throughout the halls.
 *
 * This is one crazy Sheep, containing built-in functionality for a myriad of
 * features and options.  No sub-classing here; we've provided the Ginsu-knife
 * of Sheep!
 */
class Sheep {
    /* Sheep types */
    static final int WHITE = 0;
    static final int BLACK = 1;

    /* Sheep dimensions */
    static final int WIDTH = 10;
    static final int HEIGHT = 6;

    /* Directions */
    static final int NORTH = 0;
    static final int SOUTH = 1;
    static final int EAST  = 2;
    static final int WEST  = 3;

    /* Sheep states */
    static final int S_GRAZING  = 0;
    static final int S_LEAVING  = 1;
    static final int S_LEFT     = 2;
    static final int S_ENTERING = 3;
    static final int S_DISEASED = 4;
    static final int S_DEAD     = 5;

    /* Sheep images */
    /* Row 1 */
    static final int I_DEAD1LEFT  = 0;
    static final int I_DEAD1RIGHT = 1;
    static final int I_DEAD2LEFT  = 2;
    static final int I_DEAD2RIGHT = 3;
    static final int I_WHITELEFT  = 4;
    static final int I_WHITERIGHT = 5;
    static final int I_BLACKLEFT  = 6;
    static final int I_BLACKRIGHT = 7;
    /* Row 2 */
    static final int I_SICK1LEFT  = 8;
    static final int I_SICK1RIGHT = 9;
    static final int I_SICK2LEFT  = 10;
    static final int I_SICK2RIGHT = 11;
    static final int I_SELWHITELEFT  = 12;
    static final int I_SELWHITERIGHT = 13;
    static final int I_SELBLACKLEFT  = 14;
    static final int I_SELBLACKRIGHT = 15;

    static final int LOOK_GRASS_INT    = 50;  /* interval btwn seeking grass */
    static final int DIE_INT           = 50;
    static final double CHANCE_BOLT    = 0.3; /* % chance of sheep bolting */
    static final double CHANCE_FLIP    = 0.3; /* % chance of sheep bolting */

    Dimension d;

    int x, y;

    private int type, img_id;
    private int dx, dy, dest_x, dest_y;
    private int state, dir, stop_x, hold_dir;
    private long last_look;
    private boolean flicker_on;
    private byte grass[][];
    private int disease_stage;
    private Rectangle old_clip;

    /**
     * Sheep
     *
     * Constructor receiving all needed info.
     */
    public Sheep(Dimension d, byte grass[][], int type) {
	/* Limit sheep to on-screen coordinates */
	if(x > d.width - WIDTH) {
	    x = d.width - WIDTH;
	}
	if(y > d.height - HEIGHT) {
	    y = d.height - HEIGHT;
	}

	this.d = d;
	this.grass = grass;
	this.type = type;

	if(type == WHITE) {
	    x = 0;
	    dir = EAST;
	    img_id = I_WHITERIGHT;
	} else {
	    x = d.width - WIDTH;
	    dir = WEST;
	    img_id = I_BLACKLEFT;
	}

	y = (d.height - HEIGHT) / 2;
	stop_x = (int) (Math.random() * 1000.0) % (d.width - WIDTH);
	dx = WIDTH / 2;
	dy = HEIGHT / 2;

	last_look = hold_dir = 0;
	flicker_on = false;
	state = S_ENTERING;
	disease_stage = 0;
    }

    /**
     * getType
     *
     * Returns type of sheep.
     */
    public int getType() {
	return type;
    }

    /**
     * isLeaving
     *
     * Returns whether sheep is leaving the commons.
     */
    public boolean isLeaving() {
	return (state == S_LEAVING);
    }

    /**
     * isEntering
     *
     * Returns whether sheep is entering the commons.
     */
    public boolean isEntering() {
	return (state == S_ENTERING);
    }

    /**
     * leave
     *
     * Informs the sheep that it must leave the commons.
     */
    public void leave() {
	state = S_LEAVING;
    }

    /**
     * hasLeft
     *
     * Returns whether sheep has left the commons.
     */
    public boolean hasLeft() {
	return (state == S_LEFT);
    }

    /**
     * isDead
     *
     * Returns whether sheep is dead.
     */
    public boolean isDead() {
	return(state == S_DEAD);
    }

    /**
     * isDiseased
     *
     * Returns whether sheep is diseased (or dead, since dead infected sheep
     * are still infectious.)
     */
    public boolean isDiseased() {
	return (state == S_DISEASED || state == S_DEAD);
    }

    /**
     * contractDisease
     *
     * Informs sheep that it has contracted the disease.
     */
    public void contractDisease() {
	if(Tragedy.sound_on == true && Math.random() < 0.4) {
	    Tragedy.snd_sick_baa.play();
	}

	last_look = 0;

	state = S_DISEASED;
	if(Math.random() < 0.5) {
	    img_id = I_SICK1LEFT;
	    dir = EAST;
	} else {
	    img_id = I_SICK1RIGHT;
	    dir = WEST;
	}
    }

    /**
     * kill
     *
     * Informs sheep that it must die!  (Not really; sheep is just resting
     * peacefully...  "playing dead," shall we say?)
     */
    public void kill() {
	if(Tragedy.sound_on == true && Math.random() < 0.4) {
	    Tragedy.snd_sick_baa2.play();
	}
	state = S_DEAD;
	if(Math.random() > 0.5) {
	    img_id = I_DEAD1LEFT;
	} else {
	    img_id = I_DEAD2LEFT;
	}
    }

    /**
     * moveDir
     *
     * Moves the sheep in the specified direction.
     */
    private void moveDir(int dir) {
	switch(dir) {
	  case NORTH:
	    y -= dy;
	    break;

	  case SOUTH:
	    y += dy;
	    break;

	  case EAST:
	    x += dx;
	    break;

	  case WEST:
	    x -= dx;
	    break;

	  default:
	    break;
	}
    }

    /**
     * grassInDir
     *
     * Returns the quantity of grass in the specified direction.
     */
    private int grassInDir(int dir) {
	int a, b, c, d, i, j, count;

	count = 0;
	switch(dir) {
	  case NORTH:
	    a = x; b = x + WIDTH;
	    c = y - HEIGHT; d = y;
	    break;

	  case SOUTH:
	    a = x; b = x + WIDTH;
	    c = y + HEIGHT; d = y + (HEIGHT * 2);
	    break;

	  case EAST:
	    a = x + WIDTH; b = x + (WIDTH * 2);
	    c = y; d = y + HEIGHT;
	    break;

	  case WEST:
	    a = x - WIDTH; b = x;
	    c = y; d = y + HEIGHT;
	    break;

	  default:
	    a = b = c = d = 0;
	    break;
	}
	
	for(i = a; i < b; i++) {
	    for(j = a; j < b; j++) {
		if(i < 0 || i > (this.d).width - 1 || j < 0 || 
		   j > (this.d).height - 1) {
		    continue;
		}

		if(grass[i][j] > 0) {
		    count += grass[i][j];
		}
	    }
	}

	return count;
    }

    /**
     * doGrazing
     *
     * Handle a grazing sheep.  Sheep wanders about based on several tactics,
     * either random, bolting a bit, or seeking grass.
     */
    private void doGrazing() {
	if(Math.random() < CHANCE_FLIP) {
	    switch(img_id) {
	      case I_WHITELEFT:
		img_id = I_WHITERIGHT;
		break;

	      case I_WHITERIGHT:
		img_id = I_WHITELEFT;
		break;

	      case I_BLACKLEFT:
		img_id = I_BLACKRIGHT;
	        break;

	      case I_BLACKRIGHT:
		img_id = I_BLACKLEFT;
	        break;

	      default:
	        break;
	    }
	}

	if(last_look == LOOK_GRASS_INT) {
	    int i, count, temp;

	    if(Math.random() < CHANCE_BOLT) {
		/* Sheep bolts in random dir for up to 10 moves, seeking
		   greener pastures. */

	        dir = (int) (Math.random() * 100.0) % 4;
		hold_dir = (int) (Math.random() * 100.0) % 10;
	    } else {
	        /* Move toward largest quantity of nearby grass */

	        last_look = dir = 0;
	        count = grassInDir(0);

	        for(i = 1; i < 4; i++) {
	            temp = grassInDir(i);
		    if(temp > count) {
		        count = temp;
		        dir = i;
		    }
	        }

	        switch(dir) {
	          case WEST:
	          case EAST:
	            hold_dir = WIDTH;
		    break;

	          case NORTH:
	          case SOUTH:
	          default:
		    hold_dir = HEIGHT;
		    break;
	        }
	    }
	} else if(hold_dir != 0) {
	    
	    /* Move in random direction */

	    last_look++;
	    dir = (int) (Math.random() * 100.0) % 4;
	    moveDir(dir);
	} else {

	    /* Hold sheep's current direction if necessary */

	    hold_dir--;
	    moveDir(dir);
	}

	/* Keep sheep on-screen */
	if(x < 0) {
	    x = 0;
	} else if(x > d.width - WIDTH) {
	    x = d.width - WIDTH;
	}
	if(y < 0) {
	    y = 0;
	} else if(y > d.height - HEIGHT) {
	    y = d.height - HEIGHT;
	}
    }

    /**
     * doLeaving
     *
     * Handle a leaving sheep.  Sheep heads straight off in the last dir. it
     * travelled, until it leaves the screen.
     */
    private void doLeaving() {
	moveDir(dir);
	if(x < -WIDTH || x > d.width || y < -HEIGHT || y > d.height) {
	    state = S_LEFT;
	}
    }

    /**
     * doEntering
     *
     * Handle an entering sheep.  Blink the selected sheep on and off,
     * and change to an appropriate state once the desired location has
     * been reached.
     */
    private void doEntering() {
	last_look++;

	if(last_look % 2 == 0) {
	    switch(img_id) {
	      case I_SELWHITERIGHT:
		img_id = I_WHITERIGHT;
		break;

	      case I_SELBLACKLEFT:
		img_id = I_BLACKLEFT;
		break;

	      case I_WHITERIGHT:
		img_id = I_SELWHITERIGHT;
		break;

	      case I_BLACKLEFT:
		img_id = I_SELBLACKLEFT;
		break;

	      default:
		break;
	    }
	}

	moveDir(dir);

	/* Check whether we've reached our stopping point */
	if((type == WHITE && x >= stop_x) || 
	   (type == BLACK && x <= stop_x)) {
	    state = S_GRAZING;

	    switch(img_id) {
	      case I_SELWHITERIGHT:
		img_id = I_WHITERIGHT;
		break;

	      case I_SELBLACKLEFT:
		img_id = I_BLACKLEFT;
		break;

	      default:
		break;
	    }
	}
    }

    /**
     * doDiseased
     *
     * Handle a diseased sheep.  Move in any random direction, and update
     * image to blink from one "ill-sheep" image to another.
     */
    private void doDiseased() {
	last_look++;

	dir = (int) (Math.random() * 100.0) % 4;
	moveDir(dir);

	if(last_look % 2 == 0) {
	    switch(img_id) {
	      case I_SICK1LEFT:
	      case I_SICK1RIGHT:
		if(Math.random() > 0.5) {
		    if(img_id == I_SICK1LEFT) {
			img_id = I_SICK2LEFT;
		    } else {
			img_id = I_SICK2RIGHT;
		    }
		} else {
		    if(Math.random() < CHANCE_FLIP) {
			if(img_id == I_SICK1LEFT) {
			    img_id = I_SICK1RIGHT;
			} else {
			    img_id = I_SICK1LEFT;
			}
		    }
		}
		break;

	      case I_SICK2LEFT:
	      case I_SICK2RIGHT:
		if(Math.random() > 0.5) {
		    if(img_id == I_SICK2LEFT) {
			img_id = I_SICK1LEFT;
		    } else {
			img_id = I_SICK1RIGHT;
		    }
		} else {
		    if(Math.random() < CHANCE_FLIP) {
			if(img_id == I_SICK2LEFT) {
			    img_id = I_SICK2RIGHT;
			} else {
			    img_id = I_SICK2LEFT;
			}
		    }
		}
		break;

	      default:
		break;
	    }
	}

	if(last_look % DIE_INT == 0) {
	    kill();
	}

	/* Keep sheep on-screen */
	if(x < 0) {
	    x = 0;
	} else if(x > d.width - WIDTH) {
	    x = d.width - WIDTH;
	}
	if(y < 0) {
	    y = 0;
	} else if(y > d.height - HEIGHT) {
	    y = d.height - HEIGHT;
	}
    }

    /**
     * doDead
     *
     * Handle a dead sheep.  Change image, since dead sheep is still
     * infectious and we want the user to be able to tell.
     */
    private void doDead() {
	last_look++;
	if(last_look % 2 == 0) {
	    switch(img_id) {
	      case I_DEAD1LEFT:
		img_id = I_DEAD1RIGHT;	
		break;

	      case I_DEAD1RIGHT:
		img_id = I_DEAD1LEFT;	
		break;

	      case I_DEAD2LEFT:
		img_id = I_DEAD2RIGHT;
		break;

	      case I_DEAD2RIGHT:
		img_id = I_DEAD2LEFT;
		break;

	      default:
		break;
	    }
	}
    }

    /**
     * move
     *
     * Informs the sheep that it's time to move.
     */
    public void move() {
	switch(state) {
	  case S_GRAZING:
	    doGrazing();
	    break;

	  case S_LEAVING:
	    doLeaving();
	    break;

	  case S_ENTERING:
	    doEntering();
	    break;
	
	  case S_DISEASED:
	    doDiseased();
	    break;

	  case S_DEAD: /* Sheep lies still, for god's sake! */
	    doDead();
	    break;

	  case S_LEFT: /* Sheep is removed by Commons code */
	  default:
	    break;
	}
    }

    /**
     * paint
     *
     * Paints the sheep in its current state onto specified Graphics object.
     */
    public void paint(Graphics g) {
	int img_width, img_height, i_x, i_y;

	if(img_id < 8) {
	    img_width = 13;
	    img_height = 7;
	    i_x = img_id * img_width;
	    i_y = 0;
	} else {
	    img_width = 15;
	    img_height = 9;
	    i_x = (img_id - 8) * img_width;
	    i_y = 7;
	}

	Tragedy.frameGraphics = g.create(x, y, img_width, img_height);
	Tragedy.frameGraphics.drawImage(Tragedy.sheepImage, -i_x, -i_y, null);
    }
};
