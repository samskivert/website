import java.awt.*;



/**
 * Painter
 * 
 * The painter is really the only thing that knows how to paint an event.
 * it is seperated from the PaintCanvas so that lenses can do painting
 * with this mofo.
 */
public class Painter {

	//returns true if something was painted.
	public static boolean paint(Graphics g, PaintEvent evt) {

//		PaintEvent t = evt;
//		do {
//			System.out.println("evt " + t + ": " + evt.pid + ": " + evt.arg); 
//			t = t.next;
//		} while (t != null);



		boolean retval = false;

		do {
			if (evt.willPaint()) {
				g.setColor(evt.color);
				switch (evt.pid) {
					case PaintEvent.DRAW_LINE:
						Point p = (Point) evt.arg;
						g.drawLine(evt.x, evt.y, p.x, p.y);
						retval = true;
						break;

					case PaintEvent.DRAW_PIXEL:
						g.drawLine(evt.x, evt.y, evt.x, evt.y);
						retval = true;
						break;

					case PaintEvent.DRAW_POLYGON:
						g.fillPolygon((Polygon) evt.arg);
						retval = true;
						break;
				}
			}
			evt = evt.next;
		} while (evt != null);

		return retval;
	}
}
