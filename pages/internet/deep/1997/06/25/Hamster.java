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

import java.awt.*;
import java.util.Date;

/**
 * Author: Walter Korman
 *         http://www.cerfnet.com/~shaper/index.html
 * 
 * In memory of Tatanka, and the many happy hours I spent watching her
 * stuff peanuts into her cheeks. May all SFRs (Small Furry Rodents) lead
 * a long and happy life!
 *
 * The Hamster is rendered on the Hamachi applet, but maintains its own
 * internal state in order to manage numerous time-related activities.
 */
class Hamster {
    static final int WIDTH = 40;
    static final int HEIGHT = 40;

    static final int EAST  = 0;
    static final int WEST  = 1;

    static final int PIX_HEIGHT = 3;
    static final int PIX_WIDTH = 3;

    static final int FOOD_WEIGHT = 4;
    static final int EXERCISE_WEIGHT = 2;

    static final int HOUR_WAKE[] = { 8, 8, 9, 10, 11 };
    static final int HOUR_SLEEP[] = { 20, 20, 21, 22, 23 };
    static final int SLEEP_DELAY = 15; /* minutes */

    /* Hamster phases last as follows:
       Phase 1 & 2      1 day
       Phase 3          3 days
       Phase 4          5 days
       Phase 5          5 days
       Delays specified here are in minutes for easier calculation. */
    static final int PHASE_DELAY[] = { 1440, 1440, 4320, 7200,
				       7200 }; /* mins */
    static final int NUM_PHASES = PHASE_DELAY.length;

    /* Update Constants used to determine when hamster's biological needs
     * must be satisfied.  Indexed by phase number. */

    static final int HAPPY_DELAY[] = { 10, 12, 20, 30, 40 }; /* minutes */
    static final float HAPPY_CHANCE[] = { .5f, .5f, .5f, .5f, .5f };
    static final int HUNGRY_DELAY[] = { 8, 10, 30, 30, 40 }; /* minutes */
    static final float HUNGRY_CHANCE[] = { .5f, .5f, .8f, .8f, .9f };
    static final int ATTN_DELAY = 180; /* minutes */
    static final int ATTN_DISPLAY_DELAY = 15; /* minutes */
    static final float ATTN_CALL_CHANCE[] = { .7f, .7f, .5f, .5f, .4f };
    static final int POOP_DELAY = 80;  /* minutes */
    static final float POOP_CHANCE[] = { .7f, .6f, .5f, .4f, .4f };
    
    static final int MAX_AGE =  24;   /* months */

    static final int MAX_FULLNESS  = 4;
    static final int MAX_HAPPINESS = 4;

    static final int LIFESPAN_DELAY = 60; /* minutes */

    static final float HUNGRY_PANG_LOSS = 0.25f;  /* months */
    static final float UNHAPPY_PANG_LOSS = 0.25f; /* months */
    
    static final int MIN_WEIGHT = 2;   /* grams */
    static final int MAX_WEIGHT = 130; /* grams */

    static final int DRIFT_DELAY = 600; /* ms */
    static final int Z_DELAY = 1000;    /* ms */
    
    Rectangle box;
    Sprite    ham_sprite, z_sprite;
    
    long    hatch_time, last_drift, last_phase_change, last_hungry_update,
	last_happy_update, last_lifespan_update, last_sleep_update,
	last_attn_call, last_attn_update, last_poop_update;
    /* ms */

    int     phase, last_phase;    /* current phase, 0..NUM_PHASES - 1 */

    float   months_per_ms;
    
    float   age;      /* months */
    float   lifespan; /* months */
    
    int     weight, happiness, fullness;
    float   old_age;
    int     old_weight, old_happiness, old_fullness;
    
    boolean dead, sleeping;
    
    Point   loc;
    Hamachi ham_app;
    
    /**
     * Construct a hamster.
     */
    Hamster(Rectangle box, Hamachi ham_app) {
	this.box = box;
	this.ham_app = ham_app;
	
	/* Initialize all time-counters to current time. */
	hatch_time = System.currentTimeMillis();
	last_hungry_update = last_happy_update = last_drift =
	    last_sleep_update = last_phase_change =
	    last_lifespan_update = last_attn_call = last_attn_update =
	    last_poop_update = hatch_time;
	
	age = 0.0f;
	weight = MIN_WEIGHT;
	happiness = fullness = 0;
	phase = last_phase = 0;
	dead = sleeping = false;
	
	loc = new Point((box.width - WIDTH) / 2,
			(box.height - HEIGHT) / 2);
	
	calcAgeRate();
	lifespan = (float) MAX_AGE;

	old_age = age;
	old_happiness = happiness;
	old_fullness = fullness;
	old_weight = weight;

	setZSprite();
    }
    
    /**
     * Update hamster's current stats.  
     */
    void update() {
	long cur_time = System.currentTimeMillis();

	updatePhase(cur_time);
	updateWeight();
	updateAge(cur_time);

	updateHappy(cur_time);
	updateHungry(cur_time);

	updateAttentionCall(cur_time);

	updatePoop(cur_time);
	updateLifespan(cur_time);
	checkDead();
	updateStatus();
	updateSleep(cur_time);
	
	drift();
    }

    private void updatePoop(long cur_time) {
	if(checkDelay(cur_time, last_poop_update, POOP_DELAY)) {
	    last_poop_update = cur_time;

	    if(sleeping == false && Math.random() < POOP_CHANCE[phase]) {
		if(ham_app.getNumPoop() < Hamachi.MAX_POOP) {
		    loseWeight();
		    ham_app.addPoop();
		}
	    }
	}
    }
    
    /**
     * Call for attention if:
     *   1)  Hamster is hungry
     *   2)  Hamster is unhappy
     *   3)  Lights are on and hamster is sleeping
     * in addition to normal requirements (delay, chance).
     */
    private void updateAttentionCall(long cur_time) {
	if((ham_app.isAttentionOn() && wantsAttention() == false) ||
	   (ham_app.isAttentionOn() &&
	    checkDelay(cur_time, last_attn_call, ATTN_DISPLAY_DELAY))) {

	    /* Remove attention call if attention has been satisfied, or
	     * if call has been visible for a while without being answered. */

	    ham_app.removeAttnCall();
	    last_attn_update = cur_time;

	} else if(ham_app.isAttentionOn() == false &&
		  ham_app.showingAnim() == false &&
		  checkDelay(cur_time, last_attn_update, ATTN_DELAY)) {
	    
	    last_attn_update = cur_time;
	    if(Math.random() < ATTN_CALL_CHANCE[phase] &&
	       wantsAttention()) {

		/* Don't raise attention call if we're sleeping, the lights
		 * are out, and we're calling because we're hungry or
		 * unhappy, since user can't play with us when lights out. */

		if(!(sleeping && (ham_app.getLightState() != Hamachi.L_ON) &&
		     (fullness < MAX_FULLNESS || happiness < MAX_HAPPINESS))) {

		    ham_app.doAttnCall();
		    last_attn_call = cur_time;

		}
		
	    }
	}
    }

    boolean wantsAttention() {
	return (fullness < MAX_FULLNESS || happiness < MAX_HAPPINESS ||
		(ham_app.getLightState() == Hamachi.L_ON &&
		 sleeping == true));
    }
    
    /**
     * Feed the hamster various nutmeats.  A nutty action, indeed!
     */
    void feed() {
	if(fullness < MAX_FULLNESS) {
	    weight += FOOD_WEIGHT;
	    if(weight > MAX_WEIGHT) {
		weight = MAX_WEIGHT;
	    }
	    
	    fullness++;
	}
    }

    /**
     * Play with hamster.  All work and no play makes Hamachi a dull boy/girl.
     */
    void play() {
	if(happiness < MAX_HAPPINESS) {
	    happiness++;
	}
    }

    void loseWeight() {
	weight -= EXERCISE_WEIGHT;
	/* Maintain minimum weight for current phase */
	if(weight < getMinWeight()) {
	    weight = getMinWeight();
	}
    }
    
    /**
     * Return minimum weight for hamster's current phase.
     */
    int getMinWeight() {
	if(phase == 0) {
	    return MIN_WEIGHT;
	} else {
	    return (phase + 1) * (MAX_WEIGHT / NUM_PHASES);
	}
    }

    /**
     * Paint hamster to Graphics object, subjectively.  Do you object?
     */
    void paint(Graphics g) {
	if(ham_sprite == null || last_phase != phase) {
	    if(sleeping == false) {
		setWakingHamsterSprite();
	    } else {
		setSleepingHamsterSprite();
	    }
	    
	    last_phase = phase;
	}

	ham_sprite.paint(g);

	/* if sleeping, draw 'z' here as well. */
	if(sleeping == true) {
	    z_sprite.paint(g);
	}
    }

    /**
     * Set hamster's current displayed image to that of an awake hamster.
     */
    private void setWakingHamsterSprite() {
	int off_x[], off_y[];
	    
	off_x = new int[2];
	off_y = new int[2];

	off_x[0] = 1;
	off_x[1] = 2 + WIDTH;
	
	off_y[0] = 1 + (phase) + (phase * HEIGHT);
	off_y[1] = off_y[0];
	ham_sprite = new Sprite(WIDTH, HEIGHT, box.x + loc.x,
				box.y + loc.y, 2,
				off_x, off_y, DRIFT_DELAY);
    }

    /**
     * Set hamster's current displayed image to that of a sleeping hamster.
     */
    private void setSleepingHamsterSprite() {
	int off_x[], off_y[];
	    
	off_x = new int[1];
	off_y = new int[1];

	off_x[0] = 329;
	off_y[0] = 1 + (phase) + (phase * HEIGHT);

	loc.x = (box.width - WIDTH) / 2;
	loc.y = (box.height - HEIGHT) / 2;
	
	ham_sprite = new Sprite(WIDTH, HEIGHT, box.x + loc.x,
				box.y + loc.y, 1,
				off_x, off_y, Sprite.NO_DELAY);
    }

    /**
     * Set up the animated Z's the hamster emits while snoozing.
     */
    private void setZSprite() {
	int off_x[], off_y[];
	
	off_x = new int[2];
	off_y = new int[2];

	off_x[0] = 127;
	off_y[0] = 310;
	off_x[1] = 148;
	off_y[1] = 310;

	z_sprite = new Sprite(Hamachi.ANIM_ICON_WIDTH,
			      Hamachi.ANIM_ICON_HEIGHT,
			      box.x + 94, box.y + 6, 2,
			      off_x, off_y, Z_DELAY);
    }
    
    /**
     * Compare age, weight, happiness, fullness to previous values.
     * Update status display in applet if changed.
     */
    private void updateStatus() {
	if(old_age != age || old_weight != weight ||
	   old_happiness != happiness || old_fullness != fullness) {
	    old_age = age;
	    old_weight = weight;
	    old_happiness = happiness;
	    old_fullness = fullness;

	    if(ham_app.isDisplayingStatus()) {
		ham_app.setAnimStatus();
	    }
	}
    }
    
    /**
     * Calculate and store the months/ms used to calculate age/lifespan/etc.
     */
    private void calcAgeRate() {
	int i;
	long max_lifetime = 0;

	/* Find maximum lifespan in minutes */
	for(i = 0; i < NUM_PHASES; i++) {
	    max_lifetime += PHASE_DELAY[i];
	}
	max_lifetime += PHASE_DELAY[NUM_PHASES - 1];
	
	months_per_ms = (float) MAX_AGE / ((float) max_lifetime *
					   Hamachi.ms_per_min);
	
	//System.out.println("\nAge rate = " + months_per_ms + " months/ms.");
    }

    /**
     * Given the current time in ms and the last time an action took place
     * in ms, return whether the specified delay in minutes has elapsed.
     */
    private boolean checkDelay(long cur_time, long last_time,
			      int mins_delay) {
	long ms_delay = mins_delay * Hamachi.ms_per_min;

	return(cur_time >= last_time + ms_delay);
    }

    /**
     * Check whether hamster has reached next phase of existence.
     */
    private void updatePhase(long cur_time) {
	if(checkDelay(cur_time, last_phase_change, PHASE_DELAY[phase])) {
	    last_phase_change = cur_time;
	    phase++;
	    if(phase > NUM_PHASES - 1) {
		phase = NUM_PHASES - 1;
	    }
	    
	    //System.out.println("New Phase Reached: " + phase);
	}
    }

    /**
     * Maintain minimum/maximum weight.
     */
    private void updateWeight() {
	if(weight < getMinWeight()) {
	    weight = getMinWeight();
	} else if(weight > 2 * MAX_WEIGHT) {
	    weight = 2 * MAX_WEIGHT;
	}
    }

    /**
     * Calculate current age.
     */
    private void updateAge(long cur_time) {
	age = months_per_ms * (cur_time - hatch_time);
    }

    /**
     * Update current happiness level.  Lose happiness periodically, by
     * either random chance, lights on and hamster sleeping, or dirty cage.
     */
    private void updateHappy(long cur_time) {
	if(checkDelay(cur_time, last_happy_update, HAPPY_DELAY[phase])) {
	    last_happy_update = cur_time;
	    
	    if(Math.random() < HAPPY_CHANCE[phase] ||
	       (ham_app.getLightState() == Hamachi.L_ON && sleeping == true) ||
	       ham_app.getNumPoop() > 0) {
		happiness--;
		if(happiness < 0) {
		    happiness = 0;
		}
	    }
	}
    }

    /**
     * Update current hungriness level.
     */
    private void updateHungry(long cur_time) {
	if(checkDelay(cur_time, last_hungry_update, HUNGRY_DELAY[phase])) {
	    last_hungry_update = cur_time;

	    if(Math.random() < HUNGRY_CHANCE[phase]) {
		fullness--;
		if(fullness < 0) {
		    fullness = 0;
		}
	    }
	}
    }

    /**
     * Subtract from hamster's expected lifespan if hungry/unhappy/etc.
     */
    private void updateLifespan(long cur_time) {
	if(checkDelay(cur_time, last_lifespan_update, LIFESPAN_DELAY)) {
	    last_lifespan_update = cur_time;
	    
	    if(sleeping == true || ham_app.limit_lifespan == false) {
		return;
	    }

	    if(fullness < MAX_FULLNESS) {
		lifespan -= HUNGRY_PANG_LOSS;
	    }

	    if(happiness < MAX_HAPPINESS) {
		lifespan -= UNHAPPY_PANG_LOSS;
	    }

	    //System.out.println("lifespan reduced to " + lifespan + " months.  Current age = " + age); 
	}
    }

    /**
     * Compare age to lifespan, kill hamster if life has been lived.
     */
    private void checkDead() {
	if(age > lifespan) {
	    dead = true;
	    ham_app.handleDeath();
	    //System.out.println("Dead.");
	}
    }

    /**
     * Handle waking/sleeping of hamster based on appropriate time.
     * To create narcoleptic hamster, comment out the "if/else" below,
     * and replace each with the commented versions.  This will cause
     * hamster to sleep during odd minutes and wake on even minutes.
     */
    private void updateSleep(long cur_time) {
	if(checkDelay(cur_time, last_sleep_update, SLEEP_DELAY)) {
	    last_sleep_update = cur_time;

	    Date d = new Date();

	    if(d.getHours() >= HOUR_WAKE[phase] &&
	       d.getHours() <= HOUR_SLEEP[phase] && sleeping == true) {
		//if(d.getMinutes() % 2 == 0 && sleeping == false) {

		sleeping = false;
		ham_app.setLightState(Hamachi.L_ON);

		/* Change hamster sprite to awake hamster */
		setWakingHamsterSprite();

	    } else if((d.getHours() < HOUR_WAKE[phase] ||
		       d.getHours() > HOUR_SLEEP[phase]) &&
		      sleeping == false) {
		//} else if(d.getMinutes() % 2 == 1 && sleeping == true) {

		sleeping = true;
		if(ham_app.getLightState() == Hamachi.L_OFF) {
		    ham_app.setLightState(Hamachi.L_OFF_SLEEP);
		    if(ham_app.isDisplayingNothing()) {
			ham_app.setAnimLightsOff();
		    }
		}

		/* Change hamster sprite to sleeping hamster */
		setSleepingHamsterSprite();

	    }
	}
    }
    
    /**
     * Hamster wanders in random direction, constrained within box.
     */
    private void drift() {
	int dir;
	long cur_time = System.currentTimeMillis();

	if(sleeping == true) {
	    /* Don't drift while sleeping. */
	    return;
	}
	
	if(cur_time < last_drift + DRIFT_DELAY) {
	    return;
	}

	last_drift = cur_time;
	
	dir = (int) ((Math.random() * 100) % 2);

	switch(dir) {
	case EAST:
	    loc.x += PIX_WIDTH;
	    break;
	    
	case WEST:
	    loc.x -= PIX_WIDTH;
	    break;
	    
	default:
	    break;
	}

	/* Constrain hamster to box */
	if(loc.x < 0) {
	    loc.x = 0;
	} else if(loc.x > box.width - WIDTH) {
	    loc.x = box.width - WIDTH;
	}

	if(loc.y < 0) {
	    loc.y = 0;
	} else if(loc.y > box.height - HEIGHT) {
	    loc.y = box.height - HEIGHT;
	}

	if(ham_sprite != null) {
	    ham_sprite.x = box.x + loc.x;
	    ham_sprite.y = box.y + loc.y;
	}
    }
    
    boolean isDead() {
	return dead;
    }

    boolean isSleeping() {
	return sleeping;
    }
    
    int getAge() {
	return (int) Math.floor(age);
    }

    int getWeight() {
	return weight;
    }
    
    int getHappiness() {
	return happiness;
    }

    int getFullness() {
	return fullness;
    }

    int getPhase() {
	return phase;
    }

    void setLocation(int x, int y) {
	loc.x = x;
	loc.y = y;
    }
}


