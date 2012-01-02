// This code is Copyright (C) 1997, go2net Inc. Permission is granted for
// any use so long as this header remains intact.
//
// Originally published in Deep Magic:
//     <URL:http://www.go2net.com/internet/deep/>
//
// The code herein is provided to you as is, without any warranty of any
// kind, including express or implied warranties, the warranties of
// merchantability and fitness for a particular purpose, and
// non-infringement of proprietary rights.  The risk of using this code
// remains with you.

import java.applet.*;
import java.awt.*;
import java.util.Date;
/**
 * Author: Walter Korman
 *         http://www.cerfnet.com/~shaper/index.html
 *
 * This applet implements a virtual pet.  The pet is part hamster and part
 * yellowtail tuna.  Expected lifetime is up to 20+ days.  Actions
 * available are:
 *
 *   Feed, Lights, Play, Clean, Status, Attention
 *
 * Patterned after myriad other virtual pets, but coded by a group of
 * 100 monkeys locked in a room with a bunch of bananas.  
 */
public class Hamachi extends Applet implements Runnable
{
    static final long DEFAULT_MS_PER_MIN = 60000;
    
    static final int DELAY_IDLE = 200;            /* ms */
    static final int EAT_DELAY  = 600;            /* ms */
    static final int SLEEP_DELAY = 1000;          /* ms */
    static final int DEATH_DELAY = 1000;          /* ms */
    static final int GAME_INTRO_DELAY = 400;      /* ms */
    static final int GAME_DECISION_DELAY = 5000;  /* ms */
    static final int SHOW_HAM_ACTUAL_DELAY = 600; /* ms */
    static final int POOP_DELAY = 1000;           /* ms */
    static final int POOP_HAPPY_DELAY = 400;      /* ms */
    static final int SHOW_POOP_HAPPY_DELAY = 4000; /* ms */
    
    /* Action icon constants */
    static final int A_NONE   = -1;
    static final int A_FEED   = 0;
    static final int A_LIGHT  = 1;
    static final int A_PLAY   = 2;
    static final int A_CLEAN  = 3;
    static final int A_STATUS = 4;
    static final int A_ATTENTION = 5;
    
    static final int NUM_ACTIONS = 6;
    
    static final int ICON_WIDTH = 10;
    static final int ICON_HEIGHT = 10;
    static final int ICON_OFFSET_X = 26;
    
    static final int ICON_ROW1_X = 58;
    static final int ICON_ROW1_Y = 33;
    static final int ICON_ROW2_X = 58;
    static final int ICON_ROW2_Y = 95;

    static final int ANIM_ICON_WIDTH = 20;
    static final int ANIM_ICON_HEIGHT = 20;

    static final int DIGIT_ICON_WIDTH = 9;
    static final int DIGIT_ICON_HEIGHT = 20;

    static final int MAX_POOP = 4;

    /* Display State constants */
    static final int D_NONE    = -1;
    static final int D_CLOCK   = 0;
    static final int D_STATUS1 = 1;
    static final int D_STATUS2 = 2;
    static final int D_STATUS3 = 3;
    static final int D_DEATH   = 4;
    static final int D_GAME_INTRO = 5;

    /* Game constants */
    static final int GUESS_LEFT = 0;
    static final int GUESS_RIGHT = 1;
    
    static final int BOX_WIDTH = 160;
    static final int BOX_HEIGHT = 40;

    /* Light state constants */
    static final int L_ON = 0;
    static final int L_OFF = 1;
    static final int L_OFF_SLEEP = 2;
    
    /* Game-specific member data */
    boolean        attention, showing_anim;
    long           anim_started, anim_length;
    int            light_state;
    int            cur_action, display_state;
    Hamster        ham;
    Rectangle      btn_A, btn_B, btn_C, box;
    AudioClip      snd_beep, snd_attn;
    boolean        snd_on, limit_lifespan;
    Clock          clock;
    AnimScreen     anim_screen;
    Sprite         poop_sprites[];
    int            num_poop_present;
    
    static long    ms_per_min;
    
    /* Generic applet member data */
    boolean        running;
    Image          offImage, bkgndImage, blkIconImage;
    Graphics       offGraphics, iconGraphics;
    Thread         t;
    Dimension      d;

    /**
     * Initialize the applet.
     */
    public void init ()
    {
	MediaTracker mt;
	int i;
	
	d = size();
        offImage = createImage(d.width, d.height);
        offGraphics = offImage.getGraphics();

	/* Load sounds */
	snd_beep = getAudioClip(getCodeBase(), "audio/beep.au");
	snd_attn = getAudioClip(getCodeBase(), "audio/attn.au");

	/* Read applet parameters */
	String str = getParameter("sndOn");
	snd_on = ((str != null) && str.equals("true"));

	str = getParameter("msPerMin");
	if(str == null) {
	    ms_per_min = DEFAULT_MS_PER_MIN;
	} else {
	    try {
		ms_per_min = Long.parseLong(str);
	    } catch(NumberFormatException e) {
		ms_per_min = DEFAULT_MS_PER_MIN;
		System.out.println("Invalid msPerMin value in applet params.");
	    }
	}
	//System.out.println("ms_per_min = " + ms_per_min);
	
	str = getParameter("limitLifespan");
	limit_lifespan = (str != null && str.equals("true"));
	
	/* Load background graphic */
	mt = new MediaTracker(this);
	bkgndImage = getImage(getCodeBase(), "images/ham_back.gif");
	mt.addImage(bkgndImage, 0);
	Sprite.spriteImage = getImage(getCodeBase(), "images/sprites.gif");
	mt.addImage(Sprite.spriteImage, 1);
	blkIconImage = getImage(getCodeBase(), "images/blk_icons.gif");
	mt.addImage(blkIconImage, 2);
	
	/* twiddle... twiddle... twiddle... */
	try {
	    mt.waitForAll();
	} catch(InterruptedException e) { }

	/* Initialize game state */
	box = new Rectangle(20, 50, BOX_WIDTH, BOX_HEIGHT);
	clock = new Clock(box);
	clock.start();
	setPoopSprites();
	
	resetGame();
	
	/* Set rectangles for buttons */
	btn_A = new Rectangle(58, 122, 20, 15);
	btn_B = new Rectangle(85, 132, 20, 15);
	btn_C = new Rectangle(125, 121, 20, 15);
    }

    /**
     * Reinitialize all game variables containing game-specific data.
     */
    void resetGame() {
	ham = new Hamster(box, this);
	anim_screen = new AnimScreen();
	showing_anim = false;
	attention = false;
	light_state = L_ON;
	cur_action = A_NONE;
	display_state = D_NONE;
	num_poop_present = 0;
    }
    
    /**
     * Start the main applet thread.
     */
    public void start () {
        running = true;
        t = new Thread(this);
        t.start();
    }

    /**
     * Stop the applet.
     */
    public void stop (){
        running = false;
    }

    /**
     * Main body of Hamster applet.
     */
    public void run () {
	repaint();
	
	while(running) {
	    try {
	        t.sleep(DELAY_IDLE);
	    } catch(InterruptedException e) { }

	    repaint();
	}
    }

    void handleDeath() {
	display_state = D_DEATH;
	setAnimDeath();
    }
    
    /**
     * Draw offscreen on main screen display.
     */
    public void update(Graphics g)
    {
	if(ham.isDead() == false) {
	    ham.update();
	} 
	    
	checkAnimation();
	
	switch(display_state) {
	case D_CLOCK:
	    drawClock(offGraphics);
	    break;

	default:
	    offGraphics.drawImage(bkgndImage, 0, 0, null);
	    anim_screen.paint(offGraphics);
	    if(display_state == D_NONE && light_state == L_ON &&
	       showing_anim == false) {
		ham.paint(offGraphics);
		drawPoop(offGraphics);
	    }
	    hilightAction(offGraphics);
	    break;
	}

	handleAttention(offGraphics);
	
        g.drawImage(offImage, 0, 0, null);
    }

    /**
     * Paint the applet to screen.
     */
    public void paint(Graphics g)
    {
	update(g);
    }

    private void drawClock(Graphics g) {
	g.drawImage(bkgndImage, 0, 0, null);
	clock.paint(g);
    }

    /**
     * Play the clip if sound is on.
     */
    void play(AudioClip clip) {
	if(snd_on == true) {
	    clip.play();
	}
    }

    /**
     * Handle pushing of Button A.
     * Cycles through available actions, hilighting appropriate icon.
     */
    void uponPushA() {
	if(display_state == D_CLOCK) {
	    return;
	} 
	play(snd_beep);

	if(display_state == D_DEATH) {
	    displayStatus();
	    return;
	} else if(isDisplayingStatus() && ham.isDead()) {
	    handleDeath();
	    return;
	}
		
	if(display_state == D_GAME_INTRO) {
	    decideGame(GUESS_LEFT);
	    repaint();
	    return;
	}

	if(showing_anim == true) {
	    resetAnimation();
	    repaint();
	    return;
	}
	
	switch(display_state) {
	case D_STATUS1:
	case D_STATUS2:
	case D_STATUS3:
	    displayStatus();
	    break;

	default:
	    cur_action++;
	    if(cur_action > NUM_ACTIONS - 2) {
		cur_action = A_NONE;
	    }
	    break;
	}

	repaint();
    }

    /**
     * Handle pushing of Button B.
     * Performs action currently selected.
     */
    void uponPushB() {
	play(snd_beep);
	
	if(display_state == D_DEATH) {
	    displayStatus();
	    return;
	} else if(isDisplayingStatus() && ham.isDead()) {
	    handleDeath();
	    return;
	}
		

	switch(display_state) {
	case D_STATUS1:
	case D_STATUS2:
	case D_STATUS3:
	    displayStatus();
	    repaint();
	    return;
	    
	case D_GAME_INTRO:
	    decideGame(GUESS_RIGHT);
	    repaint();
	    return;
		    
	default:
	    break;
	}

	if(showing_anim == true) {
	    resetAnimation();
	    repaint();
	    return;
	}
	
	if(light_state != L_ON || ham.isSleeping() == true) {
	    /* can only turn lights on/off, view clock, or check status */
	    if(cur_action == A_LIGHT) {
		handleLights();
	    } else if(cur_action == A_NONE) {
		displayClock();
	    } else if(cur_action == A_STATUS) {
		displayStatus();
	    }
	    repaint();
	    return;
	}
	
	switch(cur_action) {
	case A_NONE:
	    displayClock();
	    break;

	case A_FEED:   /* feed hamster */
	    ham.feed();
	    setAnimEat();
	    break;

	case A_PLAY:   /* play with hamster */
	    startGame();
	    //ham.play();
	    break;

	case A_LIGHT:  /* turn lights on/off */
	    handleLights();
	    break;

	case A_CLEAN:  /* clean cage */
	    cleanPoop();
	    break;

	case A_STATUS: /* check status */
	    displayStatus();
	    break;

	default:
	    break;
	}
	
	repaint();
    }

    private void handleLights() {
	if(light_state != L_ON) {
	    setLightState(L_ON);
	} else {
	    if(ham.isSleeping()) {
		setLightState(L_OFF_SLEEP);
	    } else {
		setLightState(L_OFF);
	    }
	    
	    setAnimLightsOff();
	}
    }
    
    public void setLightState(int state) {
	light_state = state;
	if(state == L_ON) {
	    resetAnimation();
	}
    }
    
    /**
     * Handle pushing of Button C.
     * Cancels currently selected action.
     */
    void uponPushC() {
	if(display_state == D_CLOCK) {
	    return;
	}
	play(snd_beep);

	if(ham.isDead()) {
	    resetGame();
	    repaint();
	    return;
	}

	cur_action = A_NONE;
	display_state = D_NONE;
	if(light_state == L_ON) {
	    resetAnimation();
	} else {
	    setAnimLightsOff();
	}
	
	repaint();
    }

    /**
     * Display the status screen; toggle screens if already displayed.
     */
    private void displayStatus() {
	if(display_state == D_STATUS1) {
	    display_state = D_STATUS2;
	} else if(display_state == D_STATUS2) {
	    display_state = D_STATUS3;
	} else {
	    display_state = D_STATUS1;
	}
	
	setAnimStatus();
    }

    /**
     * Display the clock, toggling on/off.
     */
    private void displayClock() {
	if(display_state == D_CLOCK) {
	    display_state = D_NONE;
	} else {
	    display_state = D_CLOCK;
	}
    }
    
    /**
     * Handle mouseDown events in the main applet display.
     */
    public boolean mouseDown(Event e, int x, int y) {
	if(btn_A.inside(x, y)) {
	    uponPushA();
	} else if(btn_B.inside(x, y)) {
	    uponPushB();
	} else if(btn_C.inside(x, y)) {
	    uponPushC();
	}

	return true;
    }

    /**
     * Return whether the applet is currently displaying hamster's status.
     */
    public boolean isDisplayingStatus() {
	return (display_state == D_STATUS1 || display_state == D_STATUS2 ||
		display_state == D_STATUS3);
    }

    public boolean isDisplayingNothing() {
	return (display_state == D_NONE);
    }

    public int getLightState() {
	return light_state;
    }
    
    /**
     * Hilights currently-selected action icon.
     */
    void hilightAction(Graphics g) {
	int x, y, i_x, i_y;

	switch(cur_action) {
	case A_FEED:
	    x = ICON_ROW1_X;
	    y = ICON_ROW1_Y;
	    break;

	case A_LIGHT:
	    x = ICON_ROW1_X + ICON_WIDTH + ICON_OFFSET_X;
	    y = ICON_ROW1_Y;
	    break;

	case A_PLAY:
	    x = ICON_ROW1_X + (2 * ICON_WIDTH) + (2 * ICON_OFFSET_X);
	    y = ICON_ROW1_Y;
	    break;

	case A_CLEAN:
	    x = ICON_ROW2_X;
	    y = ICON_ROW2_Y;
	    break;

	case A_STATUS:
	    x = ICON_ROW2_X + ICON_WIDTH + ICON_OFFSET_X;
	    y = ICON_ROW2_Y;
	    break;

	default:
	    return; /* draw nothing */
	}

	i_x = cur_action * ICON_WIDTH;
	i_y = 0;
	
	/* Draw black icon in appropriate location */
	iconGraphics = g.create(x, y, ICON_WIDTH, ICON_HEIGHT);
	iconGraphics.drawImage(blkIconImage, -i_x, -i_y, null);
    }    

    void handleAttention(Graphics g) {
	int x, y, i_x, i_y;
	
	/* Draw attention icon if necessary */
	if(attention == true) {
	    x = ICON_ROW2_X + 2 * (ICON_WIDTH + ICON_OFFSET_X);
	    y = ICON_ROW2_Y;

	    i_x = A_ATTENTION * ICON_WIDTH;
	    i_y = 0;

	    iconGraphics = g.create(x, y, ICON_WIDTH, ICON_HEIGHT);
	    iconGraphics.drawImage(blkIconImage, -i_x, -i_y, null);
	}
    }
    
    /**
     * Draws death display into the AnimScreen.
     */
    private void setAnimDeath() {
	int off_x[], off_y[];

	resetAnimation();

	off_x = new int[2];
	off_y = new int[2];
	    
	off_x[0] = 1;
	off_y[0] = 331;
	off_x[1] = 162;
	off_y[1] = 331;
	
	anim_screen.addSprite(new Sprite(BOX_WIDTH, BOX_HEIGHT, box.x,
					 box.y, 2, off_x, off_y,
					 DEATH_DELAY));
    }

    public void startGame() {
	setAnimGameIntro();
	display_state = D_GAME_INTRO;
    }
    
    private void decideGame(int guess) {
	int actual_dir = (int) (Math.random() * 100.0) % 2;

	setAnimGameDecision(actual_dir, guess);
	if(guess == actual_dir) {
	    ham.play();
	}
	ham.loseWeight();

	display_state = D_NONE;
    }

    private void setAnimPoop() {
	int off_x[], off_y[];
	int i, cur_x, cur_y;
	
	resetAnimation();

	off_x = new int[7];
	off_y = new int[7];

	cur_y = off_y[0] = 1 + (ham.getPhase()) +
	    (ham.getPhase() * Hamster.HEIGHT);

	for(i = 0; i < 7; i++) {
	    if(i % 2 == 1) {
		/* happy hamster */
		off_x[i] = 288;
	    } else {
		/* nonchalant hamster */
		off_x[i] = 1;
	    }
	    off_y[i] = cur_y;
	}
	
	cur_x = box.x + (box.width - Hamster.WIDTH) / 2;
	cur_y = box.y + (box.height - Hamster.HEIGHT) / 2;

	/* Add happy hamster */
	anim_screen.addSprite(new Sprite(Hamster.WIDTH, Hamster.HEIGHT,
					 cur_x, cur_y, 7, off_x, off_y,
					 POOP_HAPPY_DELAY));

	/* Set offsets for sunny-happy icon */
	off_x = new int[7];
	off_y = new int[7];
	
	for(i = 0; i < 7; i++) {
	    if(i % 2 == 1) {
		off_x[i] = 211;
	    } else {
		off_x[i] = 232;
	    }
	    off_y[i] = 310;
	}
	
	cur_x = box.x + (box.width - Hamster.WIDTH) / 2 + Hamster.WIDTH;
	cur_y = box.y + (box.height - Hamster.HEIGHT) / 2;

	/* Add sun icon */
	anim_screen.addSprite(new Sprite(ANIM_ICON_WIDTH, ANIM_ICON_HEIGHT,
					 cur_x, cur_y, 7, off_x, off_y,
					 POOP_HAPPY_DELAY));

	startAnimation(SHOW_POOP_HAPPY_DELAY);
    }
    
    private void setAnimGameIntro() {
	int off_x[], off_y[];
	int cur_x, cur_y;
	
	resetAnimation();

	off_x = new int[2];
	off_y = new int[2];

	/* Set offsets for ball_left and ball_right icons */
	off_x[0] = 165;
	off_y[0] = 1 + (ham.getPhase()) + (ham.getPhase() * Hamster.HEIGHT);
	off_x[1] = 206;
	off_y[1] = off_y[0];

	cur_x = box.x + (box.width - Hamster.WIDTH) / 2;
	cur_y = box.y + (box.height - Hamster.HEIGHT) / 2;
	
	anim_screen.addSprite(new Sprite(Hamster.WIDTH, Hamster.HEIGHT,
					 cur_x, cur_y, 2, off_x, off_y,
					 GAME_INTRO_DELAY));
    }

    private void setAnimGameDecision(int actual_dir, int guess) {
	int off_x[], off_y[];
	int cur_x, cur_y, i;
	
	resetAnimation();

	off_x = new int[10];
	off_y = new int[10];

	/* Set offsets for ball_left or ball_right icons */
	if(actual_dir == GUESS_LEFT) {
	    off_x[0] = 165;
	} else {
	    off_x[0] = 206;
	}
	cur_y = off_y[0] = 1 + (ham.getPhase()) +
	    (ham.getPhase() * Hamster.HEIGHT);

	off_x[1] = off_x[0];
	off_y[1] = off_y[0];
	
	/* Set offsets here for happy/sad hamster, depending on
	 * whether guess was correct. */

	for(i = 2; i < 9; i++) {
	    if(i % 2 == 1) {
		/* insert offset for happy/sad hamster */
		if(actual_dir == guess) {
		    /* happy */
		    off_x[i] = 288;
		} else {
		    /* sad */
		    off_x[i] = 247;
		}
	    } else {
		/* insert offset for nonchalant hamster */
		off_x[i] = 1;
	    }
	    off_y[i] = cur_y;
	}
	
	cur_x = box.x + (box.width - Hamster.WIDTH) / 2;
	cur_y = box.y + (box.height - Hamster.HEIGHT) / 2;

	/* Add ball and happy/sad showing direction the hamster chose */
	anim_screen.addSprite(new Sprite(Hamster.WIDTH, Hamster.HEIGHT,
					 cur_x, cur_y, 9, off_x, off_y,
					 SHOW_HAM_ACTUAL_DELAY));

	/* Set offsets for sunny-happy icon, or expletive-angry icon */
	off_x = new int[7];
	off_y = new int[7];
	
	for(i = 0; i < 7; i++) {
	    if(i % 2 == 1) {
		if(actual_dir == guess) {
		    /* happy */
		    off_x[i] = 211;
		} else {
		    /* sad */
		    off_x[i] = 169;
		}
	    } else {
		if(actual_dir == guess) {
		    /* happy */
		    off_x[i] = 232;
		} else {
		    /* sad */
		    off_x[i] = 190;
		}
	    }
	    off_y[i] = 310;
	}
	
	cur_x = box.x + (box.width - Hamster.WIDTH) / 2 + Hamster.WIDTH;
	cur_y = box.y + (box.height - Hamster.HEIGHT) / 2;

	/* Add sun/expletive icon showing whether hamster is happy/sad. */
	anim_screen.addSprite(new Sprite(ANIM_ICON_WIDTH, ANIM_ICON_HEIGHT,
					 cur_x, cur_y, 7, off_x, off_y,
					 SHOW_HAM_ACTUAL_DELAY));

	/* Set offsets for arrow showing direction the user guessed */
	/* Set location of arrow on the AnimScreen. */
	off_x = new int[1];
	off_y = new int[1];

	if(guess == GUESS_LEFT) {
	    off_x[0] = 43;
	    cur_x = box.x + 20;
	} else {
	    off_x[0] = 64;
	    cur_x = box.x + box.width - 20 - ANIM_ICON_WIDTH;
	}
	off_y[0] = 310;
	cur_y = box.y + box.height - ANIM_ICON_HEIGHT;

	/* Add fixed arrow icon showing direction the user guessed */
	anim_screen.addSprite(new Sprite(ANIM_ICON_WIDTH, ANIM_ICON_HEIGHT,
					 cur_x, cur_y, 1, off_x, off_y,
					 Sprite.NO_DELAY));

	startAnimation(GAME_DECISION_DELAY);
    }
    
    /**
     * Draws current status display into the AnimScreen.
     */
    public void setAnimStatus() {
	int off_x[], off_y[];
		
	resetAnimation();
	
	if(display_state == D_STATUS1) {
    
	    off_x = new int[1];
	    off_y = new int[1];
	    
	    off_x[0] = 1;
	    off_y[0] = 1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT +
		1 + BOX_HEIGHT +
		1 + (BOX_HEIGHT / 2);
	    
	    /* Month bitmap */
	    anim_screen.addSprite(new Sprite(BOX_WIDTH, BOX_HEIGHT / 2, box.x,
					     box.y, 1, off_x, off_y,
					     Sprite.NO_DELAY));

	    addNumberSprites(ham.getAge(), BOX_WIDTH - ANIM_ICON_WIDTH - 28,
			     box.y - 6);
	    
	    /* Grams bitmap */
	    off_x = new int[1];
	    off_y = new int[1];
	    off_x[0] = 2 + BOX_WIDTH;
	    off_y[0] = 1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT +
		1 + BOX_HEIGHT +
		1 + (BOX_HEIGHT / 2);

	    anim_screen.addSprite(new Sprite(BOX_WIDTH, BOX_HEIGHT / 2, box.x,
					     box.y + BOX_HEIGHT / 2, 1,
					     off_x, off_y,
					     Sprite.NO_DELAY));
	    
	    addNumberSprites(ham.getWeight(), BOX_WIDTH - ANIM_ICON_WIDTH - 28,
			     box.y + BOX_HEIGHT / 2 - 6);
	    
	} else if(display_state == D_STATUS2) {
	    
	    off_x = new int[1];
	    off_y = new int[1];
	    
	    off_x[0] = 1;
	    off_y[0] = 1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT +
		1 + BOX_HEIGHT;
	    
	    /* Hungry bitmap */
	    anim_screen.addSprite(new Sprite(BOX_WIDTH, BOX_HEIGHT / 2, box.x,
					     box.y, 1, off_x, off_y,
					     Sprite.NO_DELAY));

	    addHeartSprites(ham.getFullness(), Hamster.MAX_FULLNESS);
	    
	} else {
	    
	    off_x = new int[1];
	    off_y = new int[1];

	    off_x[0] = 2 + BOX_WIDTH;
	    off_y[0] = 1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT +
		1 + BOX_HEIGHT;

	    /* Happy bitmap */
	    anim_screen.addSprite(new Sprite(BOX_WIDTH, BOX_HEIGHT / 2, box.x,
					     box.y, 1, off_x, off_y,
					     Sprite.NO_DELAY));

	    addHeartSprites(ham.getHappiness(), Hamster.MAX_HAPPINESS);
	}
    }

    /**
     * Draw food-eating animation into AnimScreen.
     */
    private void setAnimEat() {
	int off_x[], off_y[], cur_x, cur_y;

	resetAnimation();

	/* Set up for food icons */
	off_x = new int[3];
	off_y = new int[3];

	off_x[0] = 253;
	off_x[1] = off_x[2] = 274;
	off_y[0] = off_y[1] = off_y[2] = 310;

	cur_x = box.x + (2 * ANIM_ICON_WIDTH);
	cur_y = box.y + BOX_HEIGHT - ANIM_ICON_HEIGHT;

	/* Add food icons to screen */
	anim_screen.addSprite(new Sprite(ANIM_ICON_WIDTH,
					ANIM_ICON_HEIGHT, cur_x,
					cur_y, 3, off_x, off_y,
					3 * EAT_DELAY));

	/* Set up for eating hamster icons */
	off_x = new int[2];
	off_y = new int[2];

	off_x[0] = 83;
	off_x[1] = 124;
	off_y[0] = off_y[1] = 1 + (ham.getPhase()) +
	    (ham.getPhase() * Hamster.HEIGHT);
	
	cur_x = box.x + (box.width - Hamster.WIDTH) / 2;
	cur_y = box.y + (box.height - Hamster.HEIGHT) / 2;

	/* Add hamster icons to screen */
	anim_screen.addSprite(new Sprite(Hamster.WIDTH,
					 Hamster.HEIGHT, cur_x,
					 cur_y, 2, off_x, off_y,
					 EAT_DELAY));

	startAnimation(8 * EAT_DELAY);
    }

    /**
     * Draw current light status into AnimScreen.
     */
    public void setAnimLightsOff() {
	int off_x[], off_y[];

	resetAnimation();

	if(light_state == L_OFF) {
	    /* display solid black box, no animation */
	    
	    off_x = new int[1];
	    off_y = new int[1];
	    
	    off_x[0] = 1;
	    off_y[0] = 1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT;
	
	    anim_screen.addSprite(new Sprite(BOX_WIDTH, BOX_HEIGHT, box.x,
					     box.y, 1, off_x, off_y,
					     Sprite.NO_DELAY));
	} else {
	    /* display box with moving 'Z' */

	    off_x = new int[2];
	    off_y = new int[2];
	    
	    off_x[0] = 2 + BOX_WIDTH;
	    off_x[1] = 3 + (2 * BOX_WIDTH);
	    off_y[0] = 	1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT;
	    off_y[1] = off_y[0];

	    resetAnimation();
	    anim_screen.addSprite(new Sprite(BOX_WIDTH, BOX_HEIGHT, box.x,
					     box.y, 2, off_x, off_y,
					     SLEEP_DELAY));

	}
    }

    void drawPoop(Graphics g) {
	int i;

	for(i = 0; i < num_poop_present; i++) {
	    poop_sprites[i].paint(g);
	}
    }
    
    /**
     * Set up the four poop sprites we display on-screen as needed.
     */
    void setPoopSprites() {
	int i, off_x[], off_y[], cur_y;

        off_x = new int[2];
	off_y = new int[2];

	off_x[0] = 85;
	off_y[1] = off_y[0] = 310;
	off_x[1] = 106;
		
	poop_sprites = new Sprite[MAX_POOP];
	for(i = 0; i < MAX_POOP; i += 2) {
	    if(i == 0) {
		cur_y = box.y + box.height - ANIM_ICON_HEIGHT;
	    } else {
		cur_y = box.y + box.height - (2 * ANIM_ICON_HEIGHT) - 1;
	    }

	    poop_sprites[i] = new Sprite(ANIM_ICON_WIDTH, ANIM_ICON_HEIGHT,
					 box.x + box.width - ANIM_ICON_WIDTH,
					 cur_y, 2, off_x, off_y,
					 POOP_DELAY);
	    
	    poop_sprites[i + 1] = new Sprite(ANIM_ICON_WIDTH, ANIM_ICON_HEIGHT,
					     box.x + box.width -
					     (2 * ANIM_ICON_WIDTH),
					     cur_y, 2, off_x, off_y,
					     POOP_DELAY);
	}
	
    }
    
    /**
     * Draw heart sprites into AnimScreen.
     */
    private void addHeartSprites(int num_filled, int num_hearts) {
	int ii, fullness = ham.getFullness();
	int cur_x, cur_y;
	int off_x[], off_y[];
	
	cur_x = box.x + 3 * ICON_WIDTH;
	cur_y = box.y + BOX_HEIGHT / 2;
	
	for(ii = 0; ii < num_hearts; ii++) {
	    off_x = new int[1];
	    off_y = new int[1];

	    off_y[0] = 1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT +
		1 + BOX_HEIGHT +
		3 + (3 * (BOX_HEIGHT / 2));
		
	    if(ii < num_filled) {
		/* Add solid heart */
		off_x[0] = 1;
	    } else {
		/* Add hollow heart */
		off_x[0] = 2 + ANIM_ICON_WIDTH;
	    }
		
	    anim_screen.addSprite(new Sprite(ANIM_ICON_WIDTH,
					     ANIM_ICON_HEIGHT, cur_x,
					     cur_y, 1, off_x, off_y,
					     Sprite.NO_DELAY));

	    cur_x += ANIM_ICON_WIDTH + 4;
	}
    }

    /**
     * Draw number sprites representing the specified number into AnimScreen.
     */
    private void addNumberSprites(int number, int right_align_x, int y) {
	int off_x[], off_y[];
	int cur_num, digit, cur_x, cur_y;

	cur_num = number;
	cur_x = right_align_x - DIGIT_ICON_WIDTH;
	cur_y = y;
	
	/* Add number sprites from right-to-left */

	while(true) {

	    /* Calculate current rightmost digit */
	    digit = cur_num % 10;

	    off_x = new int[1];
	    off_y = new int[1];

	    off_x[0] = 1 + (digit) + (digit * DIGIT_ICON_WIDTH);
	    off_y[0] = 1 + (Hamster.NUM_PHASES) +
		(Hamster.NUM_PHASES) * Hamster.HEIGHT +
		1 + BOX_HEIGHT +
		1 + (BOX_HEIGHT / 2) +
		ANIM_ICON_HEIGHT + 1;

	    /* Add sprite for rightmost digit */
	    anim_screen.addSprite(new Sprite(DIGIT_ICON_WIDTH,
					     DIGIT_ICON_HEIGHT, cur_x,
					     cur_y, 1, off_x, off_y,
					     Sprite.NO_DELAY));

	    if(cur_num == 0) {
		break;
	    }
	    
	    cur_x -= (DIGIT_ICON_WIDTH + 3);

	    /* Remove rightmost digit */
	    cur_num /= 10;

	    /* Don't draw the last 0 if our original number > 0 */
	    if(cur_num == 0 && number > 0) {
		break;
	    }
	}
    }

    void startAnimation(long anim_length) {
	showing_anim = true;
	anim_started = System.currentTimeMillis();
	this.anim_length = anim_length;
    }

    void checkAnimation() {
	if(showing_anim == false) {
	    return;
	}

	long cur_time = System.currentTimeMillis();

	if(cur_time >= anim_started + anim_length) {
	    resetAnimation();
	}
    }

    void resetAnimation() {
	showing_anim = false;
	anim_screen.reset();
	//ham.setLocation((box.width - Hamster.WIDTH) / 2,
	//		(box.height - Hamster.HEIGHT) / 2);
    }

    void doAttnCall() {
	attention = true;
	play(snd_attn);
    }

    boolean isAttentionOn() {
	return attention;
    }

    void removeAttnCall() {
	attention = false;
    }

    int getNumPoop() {
	return num_poop_present;
    }
    
    void addPoop() {
	num_poop_present++;

	if(num_poop_present > MAX_POOP) {
	    num_poop_present = MAX_POOP;
	}
    }

    void cleanPoop() {
	if(num_poop_present > 0) {
	    /* display animation of happy hamster */
	    setAnimPoop();
	}
	num_poop_present = 0;
    }

    boolean showingAnim() {
	return showing_anim;
    }

};

