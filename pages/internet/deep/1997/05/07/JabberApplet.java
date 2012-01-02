import java.applet.Applet;
import java.awt.BorderLayout;
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
 * Displays static tree containing text from "The Jabberwocky."
 */
public class JabberApplet extends Applet implements Runnable
{
    boolean        running;
    Thread         t;
    WalTreeCanvas  tc;

    /**
     * Initialize the applet.
     */
    public void init ()
    {
	setLayout(new BorderLayout());

	add("Center", tc = new JabberTreeCanvas());
	tc.setParentDistance(10);
    }

    /**
     * Start the main applet thread.
     */
    public void start ()
    {
        running = true;
        t = new Thread(this);
        t.start();
    }

    /**
     * Stop the applet.
     */
    public void stop ()
    {
        running = false;
    }

    /**
     * Main event loop.
     */
    public void run () { }
};
