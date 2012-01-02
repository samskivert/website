import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Rectangle;
import java.util.Vector;

/**
 * Class:         Tragedy
 * Author:        Walter Korman
 * Date:          3/4/97
 * Last Modified: 3/31/97
 *
 * The Tragedy of the Commons, as you've never seen it before!  It slices,
 * it dices, and it _never_ gets dull!
 */

public class Tragedy extends Applet implements Runnable
{
    static final int DELAY_IDLE = 100;  /* ms delay on idle */

    boolean        running;

    Thread         t;
   
    Button         btn_clone;
    Button         btn_unleash;
    Button         btn_restart;
    Panel          panel_btn;
    Commons        canvas_commons;

    static boolean   disease_app, sound_on;
    static AudioClip snd_baa, snd_baa2, snd_sick_baa, snd_sick_baa2;
    static Image     sheepImage, frameImage;
    static Graphics  frameGraphics;

    /**
     * init 
     *
     * Initialize the applet.
     */
    public void init() {
	int i, j;
	String str;
	MediaTracker mt;

	/* Read applet parameters */
	str = getParameter("disease");
	disease_app = (str != null && str.equals("true"));
	str = getParameter("soundOn");
	sound_on = (str != null && str.equals("true"));

	if(sound_on == true) {
	    snd_baa = getAudioClip(getCodeBase(), "audio/baa.au");
	    snd_baa2 = getAudioClip(getCodeBase(), "audio/baa2.au");
	    snd_sick_baa = getAudioClip(getCodeBase(), "audio/sick_baa.au");
	    snd_sick_baa2 = getAudioClip(getCodeBase(), "audio/sick_baa2.au");
	} 

	/* Grab images */
	mt = new MediaTracker(this);
	sheepImage = getImage(getCodeBase(), "images/sheep.gif");
	mt.addImage(sheepImage, 0);
	
	/* Wait for images to load */
	try {
	    mt.waitForAll();
	} catch(InterruptedException e) { }

	frameImage = createImage(20, 20);
	frameGraphics = frameImage.getGraphics();

	/* Build UI */
	setLayout(new BorderLayout());

	add("North", panel_btn = new Panel());
	panel_btn.setLayout(new FlowLayout(FlowLayout.CENTER));
	if(disease_app == false) {
	    panel_btn.add(btn_clone = new Button("Add Sheep"));
	} else {
	    panel_btn.add(btn_unleash = new Button("Unleash Disease"));
	}
	panel_btn.add(btn_restart = new Button("Restart"));

	add("Center", canvas_commons = new Commons());
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
     * Main body of Tragedy applet.
     */
    public void run() {
	while(running) {
	    try {
	        t.sleep(DELAY_IDLE);
	    } catch(InterruptedException e) { }
	
	    canvas_commons.repaint();

	    if(btn_clone != null && canvas_commons.sheepLeaving() && 
	      btn_clone.isEnabled()) {
		btn_clone.disable();
	    }
	    if(btn_unleash != null && canvas_commons.allSheepDiseased() &&
	      btn_unleash.isEnabled()) {
		btn_unleash.disable();
	    }
	}
    }

    /**
     * handleEvent
     *
     * Handle UI events in main applet.
     */
    public boolean handleEvent(Event e) {
	if(e.id == Event.ACTION_EVENT) {
	    if(e.target instanceof Button) {
		Button b = (Button) e.target;

		if(b.getLabel().equals("Add Sheep")) {

		    canvas_commons.addSheep(Sheep.WHITE);

		} else if(b.getLabel().equals("Restart")) {

		    canvas_commons.resetGame();
		    if(btn_clone != null) {
		        btn_clone.enable();
		    } 
		    if(btn_unleash != null) {
		        btn_unleash.enable();
		    }

		} else if(b.getLabel().equals("Unleash Disease")) {
	
		    canvas_commons.unleashDisease();

		}

	        return true;
	    }
	}

	return super.handleEvent(e);
    }
};
