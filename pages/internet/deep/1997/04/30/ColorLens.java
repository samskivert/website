import java.awt.*;
public class ColorLens extends Lens {

	public void initialize(Toolglass glass, int x, int y, String params) {
		super.initialize(glass, x, y, params);
		name = "Color";
		int bg = 0xff0000; //red.
		try {
			bg = Integer.parseInt(params, 16);
		} catch (Exception e) {
		}
		color = new Color(bg);
		preferredStackPosition = .75;
	}

	Color color;
	static Polygon polygon;
	static Image circle;
	static {
		polygon = new Polygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(0, 10);
		polygon.addPoint(10, 0);
	}

	public void drawEffect(Graphics g, Image img) {
		g.setColor(color);
		g.fillPolygon(polygon);
	}

	public boolean lensEvent(PaintEvent evt) {
		if (evt.willPaint()) {
			evt.color =colormix(color, evt.color);
			return false;
		}

		//this may be temporary.
		if ((evt.id == Event.MOUSE_UP) || (evt.id == Event.MOUSE_DRAG)) {
			evt.color = color;
			evt.pid = PaintEvent.DRAW_PIXEL;
			return false;
		}

		return false;
	}

	private Color colormix(Color c1, Color c2) {
		return new Color((c1.getRed() + c2.getRed()) >> 1,
				(c1.getGreen() + c2.getGreen()) >> 1,
				(c1.getBlue() + c2.getBlue()) >> 1);
	}

	protected boolean addBuddy(Lens l) {
		if (l instanceof ColorLens) {
			color = colormix(color, ((ColorLens) l).color);
			glass.removeLens(l);
			return false;
		} else {
			//look for a colorlens in the otherguys buddylist.
			Lens l2 = l;
			while (l2.buddy != null) {
				l2 = l2.buddy;
				if (l2 instanceof ColorLens) {
					ColorLens cl = (ColorLens) l2;
					cl.color = colormix(color, cl.color);
					glass.removeLens(this);
					return false;
				}
			}
		}
		return super.addBuddy(l);
	}
}
