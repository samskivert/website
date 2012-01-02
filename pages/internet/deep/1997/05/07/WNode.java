import java.awt.Point;

/**
 * Represents a node (or tree) in the WalTree.
 */
public class WNode {
    String  label;
    WNode   parent, child, sibling;
    int     width, height, border;
    Point   pos, offset;
    Polygon contour;

    public WNode(String l, WNode p, WNode c, WNode s, int w, int h, int b) {
	label = l;
	parent = p;
	child = c;
	sibling = s;
	width = w;
	height = h;
	border = b;
	pos = new Point(0, 0);
	offset = new Point(0, 0);
	contour = new Polygon();
    }
};

