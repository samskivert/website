
//
// Simple Magic Lens Demo.
// Ray Greenwell
// ray@go2net.com
//
// Magic Lenses are Trademarked Xerox corporation.
import java.awt.*;
import java.applet.*;

public class LensDemo extends Applet {

	public static final int NUMLENSES = 3;

	Lens[] lens = new Lens[NUMLENSES];
	Point[] p = new Point[NUMLENSES];
	int[] order = new int[NUMLENSES];

	Point offsets = null;
	Point selected = null;
	Point lastselected = null;
	boolean redraw = false;
	Image bg;

	public void init() {
		for (int ii=0; ii < NUMLENSES; ii++) {
			order[ii] = ii;
			p[ii] = new Point(ii * 100, 0);
		}
		lens[0] = new InverseLens();
		lens[1] = new GrayLens();
		lens[2] = new MagnifyLens();

		try {
			MediaTracker mt = new MediaTracker(this);
			bg = getImage(getDocumentBase(), getParameter("image"));
			mt.addImage(bg, 0);
			mt.waitForAll();
		} catch (Exception e) {
			System.out.println(e);
		}
		lens[0].initializeLenses(this);
	}

	public boolean handleEvent(Event evt) {
		switch (evt.id) {
			case Event.MOUSE_DOWN:
				//see if we hit a lenstop
				for (int ii=0; ii < order.length; ii++) {
					Point t = p[order[ii]];
					if ((evt.x >= t.x) && (evt.x < t.x + Lens.WIDTH) &&
						(evt.y >= t.y) && (evt.y < t.y + 10)) {
						int temp = order[ii];
						for (int jj=ii; jj > 0; jj--) {
							order[jj] = order[jj - 1];
						}
						order[0] = temp;
						offsets = new Point(evt.x - t.x, evt.y - t.y);
						selected = new Point(t.x, t.y);
						repaint();
						break;
					}
				}
				return true;

			case Event.MOUSE_DRAG:
				if (selected != null) {
					lastselected = selected;
					selected = new Point(evt.x - offsets.x, evt.y - offsets.y);
					repaint();
				}
				return true;

			case Event.MOUSE_UP:
				if (selected != null) {
					p[order[0]] = selected;
					lastselected = selected = null;
					redraw = true;
					repaint();
				}
				return true;
		}
		return false;
	}

	public void paint(Graphics g) {
		//hm.
		Image master = createImage(bg.getWidth(null), bg.getHeight(null));
		Graphics supG = master.getGraphics();
		supG.drawImage(bg, 0, 0, null);
		for (int ii = NUMLENSES -1; ii >= 0 ;ii--) {
			lens[order[ii]].drawAt(master, p[order[ii]].x, p[order[ii]].y);
		}
		g.drawImage(master, 0, 0, this);
		drawOutlines(g);
		redraw = false;
	}

	public void drawOutlines(Graphics g) {
		g.setXORMode(Color.white);
		g.setColor(Color.black);
		if (lastselected != null) {
			g.drawRect(lastselected.x, lastselected.y, 100, 100);
		}
		if (selected != null) {
			g.drawRect(selected.x, selected.y, 100, 100);
		}
	}

	public void update(Graphics g) {
		if (redraw) {
			paint(g);
		} else {
			drawOutlines(g);
		}
	}
}
