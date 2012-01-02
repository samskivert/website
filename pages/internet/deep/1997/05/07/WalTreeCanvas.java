import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * "There flowered a White Tree, and that was for Gondor; but Seven Stars
 *  were about it, and a high crown above it, the signs of Elendil that no
 *  lord had borne for years beyond count.  And the stars flamed in the
 *  sunlight, for they were wrought of gems by Arwen daughter of Elrond;
 *  and the crown was bright in the morning, for it was wrought of mithril
 *  and gold."                    - J. R. R. Tolkien, _The Return of the King_
 *
 * Basic canvas for creating and displaying a WalTree.
 */
public class WalTreeCanvas extends Canvas {
    int default_border;

    FontMetrics metrics;

    WTFactory wt_factory;
    WNode t;
    boolean dirty;
    int root_x;

    /**
     * Constructs new WalTreeCanvas, an associated WTFactory, and inits state.
     */
    public WalTreeCanvas() {
	super();

	setFont(new Font("Helvetica", Font.PLAIN, WTFactory.FIXED_FONT_HEIGHT));
	this.metrics = getFontMetrics(getFont());

	wt_factory = new WTFactory();
	t = null;
	default_border = 5;
	root_x = 5;
	dirty = false;
    }

    public Dimension preferredSize() {
	return new Dimension(1000, 1000);
    }

    public Dimension minimumSize() {
	return new Dimension(40, 40);
    }

    /**
     * Create a new node with the given attributes, calculating width
     * to match the displayed node name text.
     */
    public WNode makeNode(String name, WNode p, WNode c, WNode s) {
	return new WNode(name, p, c, s, metrics.stringWidth(name) + 10, 
			2 * WTFactory.FIXED_FONT_HEIGHT, default_border);
    }

    /**
     * Create a new node with the given attributes and the specified width.
     */
    public WNode makeNodeOfWidth(String name, int width, WNode p, WNode c,
		   WNode s) {
	return new WNode(name, p, c, s, width, WTFactory.FIXED_FONT_HEIGHT, 
		     default_border);
    }

    /**
     * Set the tree to be displayed by this canvas.
     */
    public void setTree(WNode t) {
	this.t = t;
	dirty = true;
    }

    /**
     * Set the default border size for nodes 
     */
    public void setDefaultBorder(int b) {
	default_border = b;
    }

    /**
     * Set the distance between parent nodes.
     */
    public void setParentDistance(int val) {
	wt_factory.setParentDistance(val);
    }

    /**
     * Set the horizontal offset between edge of canvas and root node.
     */
    public void setRootOffset(int val) {
	root_x = 5;
    }

    /**
     * Draw the tree associated with this canvas.
     */
    public void paint(Graphics g) {
	super.paint(g);

	Dimension d = size();

	/* Wipe background */
	g.setColor(Color.white);
	g.fillRect(0, 0, d.width, d.height);

	if(t == null) {
	    return;
	}

	if(dirty == true) {
	    /* Calculate node offsets */
	    wt_factory.layout(t); 

	    /* Calculate absolute node positions */
	    wt_factory.plantTree(t, root_x, (d.height - 2 * 
	      WTFactory.FIXED_FONT_HEIGHT) / 2); 

	    dirty = false;
	}

	/* Paint the beastie */
	wt_factory.paintFullTree(g, metrics, t);
    }
};

