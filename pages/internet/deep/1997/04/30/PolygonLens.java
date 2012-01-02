import java.awt.*;
import java.util.Vector;

public class PolygonLens extends Lens {

	static final int stop_hysteresis = 5;
	Vector points = new Vector();
	Point lastmouse;
	Color c;

	public void initialize(Toolglass glass, int x, int y, String params) {
		super.initialize(glass, x, y, params);
		name = "Polygon";
		preferredStackPosition = .25;
	}

	public void drawEffect(Graphics g, Image img) {
		if (points.size() > 0) {
			g.setColor(c);
			Point lastpoint = (Point) points.elementAt(0);
			int index = 1;
			while (index < points.size()) {
				Point topoint = (Point) points.elementAt(index);
				g.drawLine(lastpoint.x, lastpoint.y, topoint.x, topoint.y);
				lastpoint = topoint;
				index++;
			}
			g.drawLine(lastpoint.x, lastpoint.y, lastmouse.x, lastmouse.y);
		}
	}

	public boolean lensEvent(PaintEvent evt) {
		if (!grabEvents) {
			if (evt.pid == PaintEvent.DRAW_PIXEL) {
				grabEvents = true;
				c = evt.color;
				evt.pid = PaintEvent.STOP;
				points.addElement(lastmouse = new Point(evt.x, evt.y));
				return true;
			}
		} else {
			if (evt.id == Event.MOUSE_UP) {
				Point p = (Point) points.elementAt(0);
				if ((Math.abs(p.x - evt.x) <= stop_hysteresis) &&
					(Math.abs(p.y - evt.y) <= stop_hysteresis)) {
					//well then. its time to stop.
					Polygon gon = new Polygon();
					for (int ii=0; ii < points.size(); ii++) {
						p = (Point) points.elementAt(ii);
						gon.addPoint(p.x, p.y);
					}
					evt.pid = PaintEvent.DRAW_POLYGON;
					evt.color = c;
					evt.arg = gon;
					lastmouse = null;
					points.removeAllElements();
					grabEvents = false;
				} else {
					points.addElement(lastmouse = new Point(evt.x, evt.y));
					evt.pid = PaintEvent.STOP;
					return true;
				}
			} else {
				lastmouse = new Point(evt.x, evt.y);
				return true;
			}
		}
		return false;
	}
}
