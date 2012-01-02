/**
 * A direct port from Sven Moen's "Drawing Dynamic Trees" article in 
 * IEEE Software, July 1990.  Thanks to Allan Brighton for commenting the
 * Brighton Tree Widget source code with the aforementioned reference.
 */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Encapsulates all operations performed on a WalTree.  
 */
public class WTFactory {
    static final int FIXED_FONT_HEIGHT = 10; 
    static final int FIXED_FONT_ASCENT = 3; 

    int parent_dist = 30;

    /**
     * Allows setting of the distance between levels in the tree.
     */
    public void setParentDistance(int val) {
	parent_dist = val;
    }

    /**
     * Lays out the tree node spacing in typical tidy fashion.
     */
    void layout(WNode t) {
	WNode c;

	if(t == null) {
	    return;
	}

	c = t.child;
	while(c != null) {
	    layout(c);
	    c = c.sibling;
	}

	if(t.child != null) {
	    attachParent(t, join(t));
	} else {
	    layoutLeaf(t);
	}
    }

    /**
     * Attaches the specified node to its children, setting offsets.
     */
    void attachParent(WNode t, int h) {
	int x, y1, y2;

	x = t.border + parent_dist;
	y2 = (h - t.height) / 2 - t.border;
	y1 = y2 + t.height + 2 * t.border - h;
	t.child.offset.x = x + t.width;
	t.child.offset.y = y1;
	t.contour.upper_head = new PolyLine(t.width, 0, new PolyLine(x, y1, 
	  t.contour.upper_head));
	t.contour.lower_head = new PolyLine(t.width, 0, new PolyLine(x, y2, 
	  t.contour.lower_head));
    }
    
    /**
     * Arranges contour for leaf node appropriately.
     */
    void layoutLeaf(WNode t) {
	t.contour.upper_tail = new PolyLine(t.width + 2 * t.border, 0, null);
	t.contour.upper_head = t.contour.upper_tail;
	t.contour.lower_tail = new PolyLine(0, -t.height - 2 * t.border, null);
	t.contour.lower_head = new PolyLine(t.width + 2 * t.border, 0, 
	  t.contour.lower_tail);
    }

    /**
     * Joins children/siblings together, merging contours.
     */
    int join(WNode t) {
	WNode c;
	int d, h, sum;
	
	c = t.child;
	t.contour = c.contour;
	sum = h = c.height + 2 * c.border;
	c = c.sibling;
	while(c != null) {
	    d = merge(t.contour, c.contour);
	    c.offset.y = d + h;
	    c.offset.x = 0;
	    h = c.height + 2 * c.border;
	    sum += d + h;
	    c = c.sibling;
	}

	return sum;
    }

    /**
     * Merges two polygons together.  Returns total height of final polygon.
     */
    int merge(Polygon c1, Polygon c2) {
	int x, y, total, d;
	PolyLine lower, upper, b;
	
	x = y = total = 0;
	upper = c1.lower_head;
	lower = c2.upper_head;
	
	while(lower != null && upper != null) {	/* compute offset total */
	    
	    d = offset(x, y, lower.dx, lower.dy, upper.dx, upper.dy);
	    y += d;
	    total += d;

	    if(x + lower.dx <= upper.dx) {
		y += lower.dy;
		x += lower.dx;
		lower = lower.link;
	    } else {
		y -= upper.dy;
		x -= upper.dx;
		upper = upper.link;
	    }
	}

	/* store result in c1 */
	
	if(lower != null) {
	    b = bridge(c1.upper_tail, 0, 0, lower, x, y);
	    c1.upper_tail = (b.link != null) ? c2.upper_tail : b;
	    c1.lower_tail = c2.lower_tail;
	} else {	/* (upper) */
	    b = bridge(c2.lower_tail, x, y, upper, 0, 0);
	    if(b.link == null) {
		c1.lower_tail = b;
	    }
	}

	c1.lower_head = c2.lower_head;
	
	return total;
    }

    /**
     * Calculates the offset for specified points.
     */
    int offset(int p1, int p2, int a1, int a2, int b1, int b2) {
	int d, s, t;

	if(b1 <= p1 || p1 + a1 <= 0) {
	    return 0;
	}

	t = b1 * a2 - a1 * b2;
	if(t > 0) {
	    if(p1 < 0) {
		s = p1 * a2;
		d = s / a1 - p2;
	    } else if(p1 > 0) {
		s = p1 * b2;
		d = s / b1 - p2;
	    } else {
		d = -p2;
	    }
	} else if(b1 < p1 + a1) {
	    s = (b1 - p1) * a2;
	    d = b2 - (p2 + s / a1);
	} else if(b1 > p1 + a1) {
	    s = (a1 + p1) * b2;
	    d = s / b1 - (p2 + a2);
	} else {
	    d = b2 - (p2 + a2);
	}

	if(d > 0) {
	    return d;
	} else {
	    return 0;
	}
    }

    /**
     * bridge
     */
    PolyLine bridge(PolyLine line1, int x1, int y1, PolyLine line2, int x2, 
		int y2) {
	int dy, dx, s;
	PolyLine r;

	dx = x2 + line2.dx - x1;
	if(line2.dx == 0) {
	    dy = line2.dy;
	} else {
	    s = dx * line2.dy;
	    dy = s / line2.dx;
	}

	r = new PolyLine(dx, dy, line2.link);
	line1.link = new PolyLine(0, y2 + line2.dy - dy - y1, r);

	return r;
    }

    void plantTree(WNode t, int off_x, int off_y) {
	WNode c, s;
	int cur_y;

	t.pos.x = off_x + t.offset.x;
	t.pos.y = off_y + t.offset.y;

	/* Plant child node */
	c = t.child;
	if(c != null) {
	    plantTree(c, t.pos.x, t.pos.y);

	    /* Plant sibling nodes */
	    s = c.sibling;
	    cur_y = t.pos.y + c.offset.y;
	    while(s != null) {
	        plantTree(s, t.pos.x + c.offset.x, cur_y);
		cur_y += s.offset.y;
		s = s.sibling;
	    }
	}
    }

    void paintFullTree(Graphics g, FontMetrics metrics, WNode t) {
	if(t == null) {
	    System.out.println("paintFullTree::null tree.");
	    return;
	}

	g.setColor(Color.black);

	paintTree(g, metrics, t);
    }

    void paintTree(Graphics g, FontMetrics metrics, WNode t) {
	/* Draw highlights */
	g.setColor(Color.lightGray);
	g.drawLine(t.pos.x + 2, t.pos.y + t.height + 1, t.pos.x + t.width, 
	  t.pos.y + t.height + 1);
	g.drawLine(t.pos.x + t.width + 1, t.pos.y + t.height + 1, 
	  t.pos.x + t.width + 1, t.pos.y + 2);
	if(t.parent != null) {
	    g.drawLine(t.pos.x, t.pos.y + t.height / 2 + 1,
	      t.parent.pos.x + t.parent.width, 
	      t.parent.pos.y + t.parent.height / 2 + 1);
	}

	/* Draw this node */
	g.setColor(Color.black);
	g.drawRect(t.pos.x, t.pos.y, t.width, t.height);
	g.drawString(t.label, 
	  t.pos.x + (t.width - metrics.stringWidth(t.label)) / 2,
	  t.pos.y + t.height - (t.height - FIXED_FONT_HEIGHT) / 2);

	/* Draw line to parent */
	if(t.parent != null) {
	    g.drawLine(t.pos.x, t.pos.y + t.height / 2,
	      t.parent.pos.x + t.parent.width, 
	      t.parent.pos.y + t.parent.height / 2);
	}

	/* Draw siblings, using the main child's x-offset. */
	if(t.sibling != null) {
	    paintTree(g, metrics, t.sibling);
	}

	/* Draw children */
	if(t.child != null) {
	    paintTree(g, metrics, t.child);
	}
    }
};
