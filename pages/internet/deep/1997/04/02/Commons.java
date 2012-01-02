import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

/**
 * Class:         Commons
 * Author:        Walter Korman
 * Date:          3/18/97
 * Last Modified: 3/31/97
 *
 * The Canvas which handles all drawing and maintenance of the actual Commons.
 */
class Commons extends Canvas {
    /* Following are workarounds for font size calculations, as Netscape
     * reports unappealing font height/ascents on varying platforms. */
    static final int FIXED_FONT_HEIGHT = 10; 
    static final int FIXED_FONT_ASCENT = 3; 

    static final int MIN_DELAY_CSHEEP = 20;
    static final int VAR_DELAY_CSHEEP = 100;
    static final int MAX_GRASS_HEIGHT = 4;

    static final int WIDTH = 200;
    static final int HEIGHT = 150;

    static final int NUM_DISEASE_SHEEP = 10;
    static final int DISEASE_DIST      = 10;

    static final String GEN_STR = new String("Generating grass...");

    Dimension      d;
    FontMetrics    metrics;

    Image          grassImage, offImage;
    Graphics       grassGraphics, offGraphics;

    Color          col_dirt = new Color(177, 126, 81);
    Color          col_grass = new Color(104, 183, 10);
    Color          col_grass2 = new Color(62, 130, 2);

    byte           grass[][];
    int            num_grass, min_grass;
    boolean        sheep_leaving, sheep_dead, drew_full;
    Vector         sheeps;
    int            last_csheep, delay_csheep, num_psheep, num_csheep;
    long           game_time;
    Rectangle      r_a, r_b;

    /**
     * Commons
     *
     * Default constructor.
     */
    public Commons() {
	super();

	r_a = new Rectangle();
	r_b = new Rectangle();
	r_a.width = Sheep.WIDTH + DISEASE_DIST;
	r_b.width = Sheep.WIDTH + DISEASE_DIST;
	r_a.height = Sheep.HEIGHT + DISEASE_DIST;
	r_b.height = Sheep.HEIGHT + DISEASE_DIST;

	resetGame();
    }

    /**
     * preferredSize
     *
     * Returns preferred size of Commons canvas.
     */
    public Dimension preferredSize() {
	return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * minimumSize
     *
     * Returns minimum acceptable size of Commons canvas.
     */
    public Dimension minimumSize() {
	return new Dimension(WIDTH / 2, HEIGHT / 2);
    }

    /**
     * sheepLeaving
     *
     * Returns whether sheep have begun leaving the Commons.
     */
    public boolean sheepLeaving() {
	return sheep_leaving;
    }

    /**
     * sheepDead
     *
     * Returns whether sheep have all died from disease.
     */
    public boolean sheepDead() {
	return sheep_dead;
    }

    /**
     * addSheep
     *
     * Adds a sheep of the specified type (Sheep.WHITE or Sheep.BLACK.)
     * White is user's sheep (enters from left), black is computer sheep
     * (enters from right.)
     */
    public void addSheep(int type) {
	if(Tragedy.sound_on && Math.random() < 0.2) {
	    Tragedy.snd_baa2.play();
	}

	sheeps.addElement(new Sheep(d, grass, type));
	if(type == Sheep.WHITE) {
	    num_psheep++;
	} else {
	    num_csheep++;
	}
    }

    /**
     * unleashDisease
     *
     * Infects a currently-well sheep with disease.  Does nothing if all
     * sheep infected.
     */
    public void unleashDisease() {
	Vector wellsheep = new Vector();
	int i, j;
	Sheep s;

	j = sheeps.size();
	for(i = 0; i < j; i++) {
	    s = (Sheep) sheeps.elementAt(i);
	    if(s.isDiseased() == false) {
		wellsheep.addElement(s);
	    } 
	}

	if(wellsheep.size() == 0) {
	    return;
	}

	i = (int) (Math.random() * 1000.0) % wellsheep.size();
	
	((Sheep) wellsheep.elementAt(i)).contractDisease();
    }

    /**
     * resetGame
     *
     * Reinitializes all data necessary to begin a new game, including
     * counters, offscreens, etc.
     */
    public void resetGame() {
	if(Tragedy.disease_app == false) {
	    /* Force recreation and reinitialization of offscreens and grass */
	    offImage = null;
	    grassImage = null;
	}

	/* Init other game variables */
	num_psheep = num_csheep = last_csheep = 0;
	delay_csheep = MIN_DELAY_CSHEEP;
	sheep_leaving = sheep_dead = false;
	sheeps = new Vector();
	game_time = 0;
    }

    /**
     * update
     *
     * Draws the Commons canvas.
     */
    public void update(Graphics g) {
	if(offImage == null || grassImage == null) {
	    createOffscreens(g);
	    drawFullScreen(offGraphics);
	} else {
	    drawGame(offGraphics);
	    g.drawImage(offImage, 0, 0, null);
	}
    }

    /**
     * paint 
     *
     * Paint the Commons canvas.
     */
    public void paint(Graphics g)
    {
        update(g);
    }

    /**
     * checkGrassCount
     *
     * Checks whether enough grass is left for sheep to graze.  If not,
     * marks all sheep as leaving.
     */
    private void checkGrassCount() {
	int i, j;

	if(sheep_leaving == false && num_grass <= min_grass) {
	    j = sheeps.size();
	    for(i = 0; i < j; i++) {
	        ((Sheep) sheeps.elementAt(i)).leave();
	    }

	    sheep_leaving = true;
	    if(Tragedy.sound_on == true) {
	        Tragedy.snd_baa.play();
	    }
	}
    }

    /**
     * removeLeftSheep
     *
     * Checks whether sheep have left the commons (moved off the screen.)
     * All sheep which have left are removed from the list of sheep.
     */
    private void removeLeftSheep() {
	int i, j, ii;
	Sheep s;

	/* Remove all sheep which have left */
	j = sheeps.size();
	ii = 0;
	for(i = 0; i < j; i++) {
	    s = (Sheep) sheeps.elementAt(i - ii);
	    if(s.hasLeft()) {
		sheeps.removeElementAt(i - ii);
		ii++;
		if(s.getType() == Sheep.WHITE) {
		    num_psheep--;
		} else {
		    num_csheep--;
		}
	    }
	}
    }

    /**
     * handleSheep
     *
     * Handles regular actions of sheep during game play.
     */
    private void handleSheep(Sheep s) {
	int ii, jj;

	for(ii = s.x; ii < s.x + Sheep.WIDTH; ii++) {
	    for(jj = s.y; jj < s.y + Sheep.HEIGHT; jj++) {

	        /* Sheep eats grass */

	        if(grass[ii][jj] > 0) {
	            grass[ii][jj]--;
		    num_grass--;
		    if(grass[ii][jj] == 0) {
		        grassGraphics.setColor(col_dirt);
		        grassGraphics.drawLine(ii, jj, ii, jj);
		    }
		} 
	    }
	}
    }

    /**
     * handleComputerSheep
     *
     * Handles regular actions of computer sheep during game play.
     */
    private void handleComputerSheep() {
	if(sheep_leaving == true) {
	    return;
	}

	last_csheep++;
	if(last_csheep == delay_csheep) {
	    addSheep(Sheep.BLACK);

	    last_csheep = 0;
	    delay_csheep = MIN_DELAY_CSHEEP + ((int) (Math.random() * 1000.0) % 
	      VAR_DELAY_CSHEEP);
	
	    /* If player has many more sheep than the computer, churn out more
	       computerized sheep. */

	    if(num_psheep > num_csheep) {
	        delay_csheep -= ((num_psheep - num_csheep) * 10);
		if(delay_csheep <= 0) {
		    delay_csheep = 10;
		}
	    }
	}
    }

    /**
     * drawFullScreen
     *
     * Draws complete offscreen grass image, and then complete offscreen
     * image.  All is then copied to the screen.
     */
    private void drawFullScreen(Graphics g) {
	int   i, j;

	/* Wipe offscreen grass graphics image */
	grassGraphics.setColor(col_dirt);
	grassGraphics.fillRect(0, 0, d.width, d.height);

	/* Draw grass */
	for(i = 0; i < d.width; i++) {
	    for(j = 0; j < d.height; j++) {
		if(grass[i][j] > 0) {
		    if(Math.random() > 0.5) {
	    		grassGraphics.setColor(col_grass);
		    } else {
	    		grassGraphics.setColor(col_grass2);
		    }
		    grassGraphics.drawLine(i, j, i, j);
		}
	    }
	}
	
	/* Copy grass image to main graphics */
	g.drawImage(grassImage, 0, 0, null);

	/* Draw sheep */
	j = sheeps.size();
	for(i = 0; i < j; i++) {
	    ((Sheep) sheeps.elementAt(i)).paint(g);
	}
    }

    /**
     * checkDiseaseTransfer
     *
     * Checks the specified sheep to see whether it is close enough to any
     * other infected sheep to contract the disease.
     */
    private void checkDiseaseTransfer(Sheep s) {
	int i, j;
	Sheep t;

	if(s.isDiseased() == true) {
	    return;
	}

	j = sheeps.size();
	for(i = 0; i < j; i++) {
	    t = (Sheep) sheeps.elementAt(i);

	    if(t == s) {
		continue;
	    }
	
	    r_a.x = s.x - DISEASE_DIST;
	    r_a.y = s.y - DISEASE_DIST;
	    r_b.x = t.x - DISEASE_DIST;
	    r_b.y = t.y - DISEASE_DIST;

	    if(r_a.intersects(r_b)) {
		if(t.isDiseased()) {
		    s.contractDisease();

		    return;
		}
	    }
	}
    }

    /**
     * allSheepDiseased
     *
     * Returns whether all sheep have contracted the disease.
     */
    public boolean allSheepDiseased() {
	int i;
	
	if(sheeps == null || sheeps.size() == 0) {
	    return false;
	}

	for(i = 0; i < sheeps.size(); i++) {
	    if(((Sheep) sheeps.elementAt(i)).isDiseased() == false) {
		return false;
	    }
	}

	return true;
    }

    /**
     * allSheepDead
     *
     * Returns whether all sheep are dead (have died from the disease.)
     */
    private boolean allSheepDead() {
	int i;
	
	for(i = 0; i < sheeps.size(); i++) {
	    if(((Sheep) sheeps.elementAt(i)).isDead() == false) {
		return false;
	    }
	}

	return true;
    }

    /**
     * drawGame 
     *
     * Draw current game state to the specified Graphics object.
     */
    private void drawGame(Graphics g)
    {
	int   i, j;
	Sheep s;

	game_time++;
	
	if(Tragedy.disease_app == true) {
	    if(sheeps.size() == 0) {
	        /* Add initial batch of sheep */

	        for(i = 0; i < NUM_DISEASE_SHEEP; i++) {
	            addSheep(Sheep.WHITE);
	        }
	    } else if(sheep_dead == false && allSheepDead()) {
		sheep_dead = true;
	    }
	}
	    
	checkGrassCount();
	removeLeftSheep();

	/* Move sheep and handle eating */
	j = sheeps.size();
	for(i = 0; i < j; i++) {
	    s = (Sheep) sheeps.elementAt(i);
	
	    s.move();

	    if(sheep_leaving == true || s.isEntering()) {
		continue;
	    }

	    if(Tragedy.disease_app == false) {
	        handleSheep(s);
	    } else {
		checkDiseaseTransfer(s);
	    }
	}

	/* Create new computer sheep as appropriate */
	if(Tragedy.disease_app == false) {
	    handleComputerSheep();
	}

	/* Copy grass image to main offscreen */
	offGraphics.drawImage(grassImage, 0, 0, null);

	/* Draw sheep onto grass */
	j = sheeps.size();
	for(i = 0; i < j; i++) {
	    ((Sheep) sheeps.elementAt(i)).paint(offGraphics);
	}

	/* Draw black frame around commons */
	g.setColor(Color.black);
	g.drawRect(0, 0, d.width - 1, d.height - 1);

        g.drawImage(offImage, 0, 0, null);
    }

    /**
     * createOffscreens
     *
     * Create offscreens used during game animation.
     */
    private void createOffscreens(Graphics g) {
	int i, j;

	d = size();

        offImage = createImage(d.width, d.height);
        offGraphics = offImage.getGraphics();

	/* Set up font info */
	g.setFont(new Font("Helvetica", Font.BOLD, FIXED_FONT_HEIGHT));
	metrics = g.getFontMetrics();

	/* Display text noting brief delay */
	i = (d.width - metrics.stringWidth(GEN_STR)) / 2;
	j = (d.height + FIXED_FONT_HEIGHT) / 2;
	g.setColor(Color.white);
	g.fillRect(i - 5, j - FIXED_FONT_HEIGHT - 5, 
	  metrics.stringWidth(GEN_STR) + 10, FIXED_FONT_HEIGHT + 10);
	g.setColor(Color.black);
	g.drawRect(i - 5, j - FIXED_FONT_HEIGHT - 5, 
	  metrics.stringWidth(GEN_STR) + 10, FIXED_FONT_HEIGHT + 10);
	g.drawString(GEN_STR, i, j);

	offGraphics.setFont(new Font("Helvetica", Font.PLAIN, 
	  FIXED_FONT_HEIGHT));
	metrics = offGraphics.getFontMetrics();

	/* Initialize grass */
        grassImage = createImage(d.width, d.height);
        grassGraphics = grassImage.getGraphics();
	
	num_grass = 0;
	grass = new byte[d.width][d.height];
	for(i = 0; i < d.width; i++) {
	    for(j = 0; j < d.height; j++) {
		grass[i][j] = (byte) (((int) (Math.random() * 100.0)) % 
		  MAX_GRASS_HEIGHT);
		if(grass[i][j] != 0) {
		    num_grass += grass[i][j];
		}
            }
        }
	/* Sheep leave when only 20% of grass is left. */
	min_grass = (int) (num_grass * 0.2);
    }
};
