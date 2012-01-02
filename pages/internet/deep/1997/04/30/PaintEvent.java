import java.awt.*;

public class PaintEvent {

	private static final int PAINTEVENT		= 5000;
	public static final int STOP			= PAINTEVENT - 2;
	public static final int NOTHING			= PAINTEVENT - 1;
	public static final int DRAW_PIXEL		= PAINTEVENT + 1;
	public static final int DRAW_LINE		= PAINTEVENT + 2; 
	public static final int DRAW_POLYGON	= PAINTEVENT + 3;

	public int				pid;
	public int				id;
	public int				x;
	public int				y;
	public Event			evt;
	public Color			color;
	public Object			arg;
	public PaintEvent		next;


	//fuck -- i'm gonna have to make it so that a paintevent has a pointer
	// to the real event in it.
	public PaintEvent(Event e) {
		id = e.id;
		pid = NOTHING;
		x = e.x;
		y = e.y;
		evt = e;
		arg = color = null;
		next = null;
	}

	public void translate(int dx, int dy) {
		evt.translate(dx, dy);
		x += dx;
		y += dy;

		switch (pid) {
			case DRAW_LINE:
				Point p = (Point) arg;
				arg = new Point(p.x + dx, p.y + dy);
				break;

			case DRAW_POLYGON:
				Polygon pp = (Polygon) arg;
				Polygon gon = new Polygon();
				for (int ii=0; ii < pp.npoints; ii++) {
					gon.addPoint(pp.xpoints[ii] + dx, pp.ypoints[ii] + dy);
				}
				arg = gon; //Atomic weight: 39.948
				break;
		}
		if (next != null) {
			next.translate(dx, dy);
		}
	}

	public boolean willPaint() {
		return (pid > PAINTEVENT);
	}

	public Object clone() {
		PaintEvent pe = new PaintEvent(evt);
		pe.id = id;
		pe.pid = pid;
		pe.x = x;
		pe.y = y;
		pe.color = color;
		pe.arg = arg;
		if (next != null) {
			pe.next = (PaintEvent) next.clone(); 
		}
		return pe;
	}
}
