import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.MediaTracker;

/**
 * Class:  Prison
 * Author: Walter Korman
 * Date:   2/20/97
 *
 * A modern computerized incarnation of the Iterated Prisoner's Dilemma, 
 * complete with entertaining cartoons and overwhelmingly intelligent fellow 
 * prisoner.
 */
public class Prison extends Applet implements Runnable
{
    /* Following are workarounds for font size calculations, as Netscape
     * reports unappealing font height/ascents on varying platforms. */
    static final int FIXED_FONT_HEIGHT = 10; 
    static final int FIXED_FONT_ASCENT = 3; 

    static final int DELAY_IDLE = 200;  /* ms delay on idle */

    /* Player Decision constants */
    static final int C_COOPERATE = 0;   /* Denotes cooperation */
    static final int C_DEFECT = 1;      /* Denotes defection */

    /* Computer Brain constants */
    static final int B_RANDOM    = 0;   /* Enforce "random" decisions */
    static final int B_TITFORTAT = 1;   /* Enforce "Tit-For-Tat" decisions */
    static final int B_IRON      = 2;   /* Enforce "Golden Rule" decisions */
    static final int B_GOLDEN    = 3;   /* Enforce "Iron Rule" decisions */
    
    /* Applet State constants */
    static final int S_PLAYER     = 0;  /* Player making decision */
    static final int S_SHOWCHOICE = 1;  /* Displaying player's choice */
    static final int S_RESULTS    = 3;  /* Displaying results of decisions */
    static final int S_WAITCLICK  = 4;  /* Wait for click while show results */

    /* Image ID constants */
    static final int ID_DEFCON  = 0;    /* ID # of defcon picture */
    static final int ID_CHOICE  = 1;    /* ID # of choice picture */
    static final int ID_RESULTS = 2;    /* ID # of results picture */

    static final int NUM_IMAGES  = 3;   /* Total number of images */
    static final int IMAGE_Y_POS = 10;  /* Fixed y-pos for top of most images */

    static final String STR_HELP = new String("( click one )");
    static final String STR_HELP2 = new String("( click to continue )");

    boolean        running;
    Image          offImage;
    Graphics       offGraphics;
    Thread         t;
    Dimension      d;
    boolean        clicked;
    FontMetrics    metrics;

    MediaTracker   mt;
    Image          images[] = new Image[NUM_IMAGES];
    Rectangle      i_rects[] = new Rectangle[NUM_IMAGES];
    Rectangle      rect_coop, rect_def;
    
    int state;                  /* game state */
    int brain;                  /* computer decision basis */
    int p1_choice, p2_choice;   /* Px selection */
    int p1_punish, p2_punish;   /* Px punishment in years */
    int p1_total, p2_total;     /* Px punishment total in years */
    int p1_lastchoice;          /* P1's last decision */
    
    /**
     * init 
     *
     * Initialize the applet.
     */
    public void init ()
    {
	int i;
	String str;

        d = size();
        offImage = createImage(d.width, d.height);
        offGraphics = offImage.getGraphics();

	/* Set up font info */
	offGraphics.setFont(new Font("Helvetica", Font.PLAIN, 
	  FIXED_FONT_HEIGHT));
	metrics = offGraphics.getFontMetrics();

	/* Load graphics */
	mt = new MediaTracker(this);
	images[ID_DEFCON]  = getImage(getCodeBase(), "images/defcon.gif");
	images[ID_CHOICE]  = getImage(getCodeBase(), "images/choice.gif");
	images[ID_RESULTS] = getImage(getCodeBase(), "images/results.gif");
	for(i = 0; i < NUM_IMAGES; i++) {
	    mt.addImage(images[i], i);
	}

	/* Wait for images to load, in order to calculate rects */
	/* twiddle... twiddle... twiddle... */
	try {
	    mt.waitForAll();
	} catch(InterruptedException e) { }

	/* Initialize applet state */
	clicked = false;
	brain = B_RANDOM;
	state = S_PLAYER;
	p1_lastchoice = C_COOPERATE;
	p1_total = p2_total = 0;

	for(i = 0; i < NUM_IMAGES; i++) {
	    i_rects[i] = new Rectangle();
	}

	/* DefCon image is centered horiz/vert */
	i_rects[ID_DEFCON].width  = images[ID_DEFCON].getWidth(null);
	i_rects[ID_DEFCON].height = images[ID_DEFCON].getHeight(null);
	i_rects[ID_DEFCON].x      = (d.width - i_rects[ID_DEFCON].width) / 2;
	i_rects[ID_DEFCON].y      = (d.height - i_rects[ID_DEFCON].height) / 2;

	/* All other images are centered horiz, but fixed loc from top */
	for(i = ID_CHOICE; i < NUM_IMAGES; i++) {
	    i_rects[i].width  = images[i].getWidth(null);
	    i_rects[i].height = images[i].getHeight(null);
	    i_rects[i].x      = (d.width - i_rects[i].width) / 2;
	    i_rects[i].y      = IMAGE_Y_POS;
	}

	/* Set mouse-click rects for initial screen */
	rect_coop = new Rectangle(i_rects[ID_DEFCON].x, i_rects[ID_DEFCON].y, 
			i_rects[ID_DEFCON].width / 2, 
			i_rects[ID_DEFCON].height);
	rect_def = new Rectangle(rect_coop.x + i_rects[ID_DEFCON].width / 2, 
			rect_coop.y, i_rects[ID_DEFCON].width / 2,
			i_rects[ID_DEFCON].height);

	/* Read applet parameters */
	
	/* Read "brain" flag. */
	str = getParameter("brain");
	if(str != null) {
	    if(str.equals("Random")) {
	        brain = B_RANDOM;
	    } else if(str.equals("TitForTat")) {
	        brain = B_TITFORTAT;
	    } else if(str.equals("Iron")) {
		brain = B_IRON;
	    } else if(str.equals("Golden")) {
		brain = B_GOLDEN;
	    }
	}
    }

    /**
     * start 
     *
     * Start the main applet thread.
     */
    public void start ()
    {
        running = true;
        t = new Thread(this);
        t.start();
    }

    /**
     * stop 
     *
     * Stop the applet.
     */
    public void stop ()
    {
        running = false;
    }

    /**
     * run 
     *
     * Main body of Prison applet.
     */
    public void run () {
	int old_state = state;

	drawState(offGraphics);
	repaint();

	while(running) {
	    if(state == S_WAITCLICK) {
		waitForClick();
		state = S_PLAYER;
	    }

	    try {
	        t.sleep(DELAY_IDLE);
	    } catch(InterruptedException e) { }

	    if(state != old_state) {
		old_state = state;
		drawState(offGraphics);
		repaint();
	    }
	}
    }

    /**
     * update 
     *
     * Draw offscreen on main screen display.
     */
    public void update(Graphics g)
    {
        g.drawImage(offImage, 0, 0, null);
    }

    /**
     * paint 
     *
     * Paint the applet, updating state and drawing offscreen to screen.
     */
    public void paint (Graphics g)
    {
	drawState(g);
        update(g);
    }

    /**
     * hilightChoice 
     *
     * Invert Cooperate/Defect region of screen to denote player's choice.
     */
    public void hilightChoice(Graphics g) {
        g.setXORMode(Color.yellow); /* Oddly, we get no yellow on Mac Java. */

        if(p1_choice == C_DEFECT) {
	    g.fillRect(rect_def.x, rect_def.y, rect_def.width, 
	      rect_def.height);
        } else {
	    g.fillRect(rect_coop.x, rect_coop.y, rect_coop.width, 
	      rect_coop.height);
        }

        g.setPaintMode();
    }

    /**
     * chooseComputer 
     *
     * Determine computer's Cooperate/Defect decision.
     */
    public void chooseComputer() {
	switch(brain) {
	  case B_RANDOM:
	    if(Math.random() > 0.5) {
	        p2_choice = C_COOPERATE;
	    } else {
	        p2_choice = C_DEFECT;
	    }
	    break;

	  case B_TITFORTAT:
	    p2_choice = p1_lastchoice;
	    break;

	  case B_IRON:
	    p2_choice = C_DEFECT;
	    break;

	  case B_GOLDEN:
	    p2_choice = C_COOPERATE;
	    break;

	  default:
	    break;
	}
    }

    /**
     * calcPunishment 
     *
     * Determine resulting punishment based on P1 and P2's choices.
     *
     * Payoff Matrix is as follows:
     *
     *     Action                  Payoff
     * P1          P2           P1        P2
     * ---------------------------------------
     * Defect      Defect       -2        -2
     * Defect      Cooperate     0        -3
     * Cooperate   Defect       -3         0
     * Cooperate   Cooperate    -1        -1
     */
    public void calcPunishment() {
	if(p1_choice == C_DEFECT && p2_choice == C_DEFECT) {
	    p1_punish = 2; 	/* pleasant comrades */
	    p2_punish = 2; 	/* pleasant comrades */
	} else if(p1_choice == C_COOPERATE && p2_choice == C_DEFECT) {
	    p1_punish = 3; 	/* sucker's payoff */
	    p2_punish = 0; 	/* tattle-tale! */
	} else if(p1_choice == C_DEFECT && p2_choice == C_COOPERATE) {
	    p1_punish = 0; 	/* tattle-tale! */
	    p2_punish = 3; 	/* sucker's payoff */
	} else { /* p1_choice == C_COOPERATE && p2_choice == C_COOPERATE */
	    p1_punish = 1; 	/* suspicion abounds */
	    p2_punish = 1; 	/* suspicion abounds */
	}
	p1_total += p1_punish;
	p2_total += p2_punish;
    }

    /**
     * displayImage 
     *
     * Draws image #id to the specified Graphics object, using stored
     * Image refs and rects.
     */
    public void displayImage(Graphics g, int id) {
        g.drawImage(images[id], i_rects[id].x, i_rects[id].y, null);
    }

    /**
     * displayHelp
     *
     * Draw info at bottom of opening screen.
     */
    public void displayHelp(Graphics g) {
	String strat_str = null;
	String full_str;

	switch(brain) {
	  case B_RANDOM:
	    strat_str = new String("Random");
	    break;

	  case B_TITFORTAT:
	    strat_str = new String("Tit-For-Tat");
	    break;

	  case B_IRON:
	    strat_str = new String("The Iron Rule");
	    break;

	  case B_GOLDEN:
	    strat_str = new String("The Golden Rule");
	    break;

	  default:
	    break;
	}

	full_str = new String("P2 strategy: " + strat_str);
	g.drawString(full_str, (d.width - metrics.stringWidth(full_str)) / 2,
	  d.height - (2 * FIXED_FONT_HEIGHT) - FIXED_FONT_ASCENT);

	g.drawString(STR_HELP, (d.width - metrics.stringWidth(STR_HELP)) / 2, 
	  d.height - FIXED_FONT_HEIGHT);
    }

    /**
     * displayResults 
     *
     * Draw resulting decisions and punishments text.
     */
    public void displayResults(Graphics g) {
	int x1, x2, x3;
	int y = i_rects[ID_RESULTS].y + i_rects[ID_RESULTS].height + 
		  FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT + 5;
	String win_str;
	Color old_col;

	x1 = 5;
	x2 = x1 + (d.width / 3);
	x3 = x2 + (d.width / 3);

	old_col = g.getColor();
	g.setColor(Color.lightGray);

	/* Draw horizontal table lines */
	g.drawLine(5, y + 2, d.width - 5, y + 2);
	g.drawLine(5, y + 2 + FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT, 
	  d.width - 5, y + 2 + FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT);
	g.drawLine(5, y + 5 + (2 * FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT), 
	  d.width - 5, y + 5 + (2 * FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT));

	/* Draw vertical table lines */
	g.drawLine(x2 - 3, y - FIXED_FONT_HEIGHT, x2 - 3, 
	  y + (4 * FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT));
	g.drawLine(x3 - 3, y - FIXED_FONT_HEIGHT, x3 - 3, 
	  y + (4 * FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT));

	g.setColor(old_col);

	g.drawString("P1 (you)", x2, y);
	g.drawString("P2", x3, y);
	y += FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT;

	g.drawString("Choice", x1, y);
	g.drawString(choiceStr(p1_choice), x2, y);
	g.drawString(choiceStr(p2_choice), x3, y);
	y += FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT;

	g.drawString("Punishment", x1, y);
        g.drawString("" + p1_punish, x2, y);
        g.drawString("" + p2_punish, x3, y);
	y += FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT;

	g.drawString("Total Years", x1, y);
        g.drawString("" + p1_total, x2, y);
        g.drawString("" + p2_total, x3, y);
	y += FIXED_FONT_HEIGHT + FIXED_FONT_ASCENT + 10;

	if(p1_punish < p2_punish) {
	    win_str = new String("P1");
	} else if (p1_punish > p2_punish) {
	    win_str = new String("P2");
	} else 
	    win_str = new String("<tie>");

        g.drawString("Round Winner: " + win_str, x1, y);

	g.drawString(STR_HELP2, (d.width - metrics.stringWidth(STR_HELP2)) / 2, 
	  d.height - FIXED_FONT_HEIGHT);
    }

    /**
     * choiceStr 
     *
     * Return string representing a player's decision.
     */
    public String choiceStr(int choice) {
	if(choice == C_COOPERATE) {
	    return new String("cooperated");
	} else {
	    return new String("defected");
	}
    }

    /**
     * drawState 
     *
     * Draw the game in its current state.  A state machine!  Ooo boy!
     * I reckon I've solved the Halting problem!
     */
    public void drawState(Graphics g) {
	Color old_col;

	old_col = g.getColor();

	/* Wipe offscreen graphics image */
	g.setColor(Color.black);
	g.fillRect(0, 0, d.width, d.height);
	g.setColor(Color.white);

	switch(state) {
	  case S_PLAYER:
	    displayImage(g, ID_CHOICE);
	    displayImage(g, ID_DEFCON);
	    displayHelp(g);
	    break;

	  case S_SHOWCHOICE:
	    displayImage(g, ID_CHOICE);
	    displayImage(g, ID_DEFCON);
	    hilightChoice(g);
	    
	    state = S_RESULTS;
	    break;

	  case S_RESULTS:
	    displayImage(g, ID_RESULTS);
	    chooseComputer();
	    calcPunishment();
	    displayResults(g);

	    state = S_WAITCLICK;
	    break;

	  default:
	    break;
	}

	g.setColor(old_col);
    }

    /**
     * waitForClick 
     *
     * Pause applet until mouseUp detected.
     */
    public synchronized void waitForClick() {
        try {
            this.wait();
            clicked = false;
        } catch (InterruptedException e) { }
    }

    /**
     * mouseUp 
     *
     * Handle mouseUp event.  If on opening screen, determine which rect
     * user clicked in.  Also, notify applet in case waiting for mouse-click.
     */
    public synchronized boolean mouseUp(Event e, int x, int y) {
	switch(state) {
	  case S_PLAYER:
	    if(rect_def.inside(x, y)) {
		p1_lastchoice = p1_choice;
		p1_choice = C_DEFECT;
	        state = S_SHOWCHOICE;
	    } else if(rect_coop.inside(x, y)) {
		p1_lastchoice = p1_choice;
		p1_choice = C_COOPERATE;
	        state = S_SHOWCHOICE;
	    }
	    break;	

	  default:
	    break;
	}
	
	clicked = true;
	this.notify();
	
	return true;
    }
};
