import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;

/**
 * Displays and allows user-creation of a tree.
 */
public class DynamicApplet extends Applet implements Runnable
{
    /* Following are workarounds for font size calculations, as Netscape
     * reports unappealing font height/ascents on varying platforms. */
    static final int FIXED_FONT_HEIGHT = 10; 
    static final int FIXED_FONT_ASCENT = 3; 

    boolean        running;
    Thread         t;
    WalTreeCanvas  tc;
    Button         btn_clear;
    DynamicField   fld_text;
    WNode          root;

    /**
     * Initialize the applet.
     */
    public void init ()
    {
	String str;
	Panel p;

	setLayout(new BorderLayout());

	add("Center", tc = new WalTreeCanvas());
	add("South", p = new Panel());
	
	tc.setParentDistance(10);

	p.add(new Label("Name:"));
	p.add(fld_text = new DynamicField(10, this));
	p.add(btn_clear = new Button("Clear"));

	root = null;
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

    /**
     * Add the string in the fld_text field to the displayed tree.
     */
    public void addString() {
	String s = fld_text.getText();

	if(s.equals("")) {
	    return;
	}

	if(root == null) {
	    root = tc.makeNode(s, null, null, null);
	} else {
	    insertString(root, s);
	}

	tc.setTree(root);

	tc.repaint();
    }

    /**
     * Insert the specified string into the tree.  In the interest of tree
     * art, we insert it into a random location.
     */
    public void insertString(WNode t, String s) {
	int loc = ((int) (Math.random() * 100.0)) % 3;
	WNode c;
	WNode n = tc.makeNode(s, null, null, null);

	switch(loc) {
	  case 0: 
	    /* make new node the parent of current node and its siblings */
	    n.parent = t.parent;
	    t.parent = n;
	    n.child = t;
	    c = t.sibling;
	    while(c != null) {
		c.parent = n;
		c = c.sibling;
	    }

	    if(t == root) {
		root = n;
	    }
	    break;

	  case 1:
	    /* make new node a child of current node */
	    n.sibling = t.child;
	    t.child = n;
	    n.parent = t;
	    break;

	  case 2:
	  default:
	    if(t != root) {
	        /* make new node a sibling of current node */
	        n.parent = t.parent;
	        n.sibling = t.sibling;
	        t.sibling = n;
	    } else {
	        /* make new node a child of current node */
	        n.sibling = t.child;
	        t.child = n;
	        n.parent = t;
	    }
	    break;
	}
    }

    public void clearTree() {
	root = null;
	tc.setTree(null);

	tc.repaint();
    }

    public boolean handleEvent(Event e) {
	if(e.id == Event.ACTION_EVENT && e.target == btn_clear) {
	    clearTree();
	} else if(e.id == Event.KEY_ACTION && e.key == '\n') {
	    addString();

	    return true;
	}

	return false;
    }
};
