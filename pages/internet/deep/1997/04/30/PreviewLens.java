import java.awt.*;
import java.util.Vector;

public class PreviewLens extends Lens {

	private Vector queue = new Vector();
	private PaintEvent launch = null;

	public void initialize(Toolglass glass, int x, int y, String params) {
		super.initialize(glass, x, y, params);
		preferredStackPosition = .1;
		name = "Preview";
		width = DEFAULT_WIDTH * 2;
		height = DEFAULT_HEIGHT * 2;
	}

	public void drawEffect(Graphics g, Image img) {
		for (int ii=0; ii < queue.size(); ii++) {
			PaintEvent evt = (PaintEvent) queue.elementAt(ii);
			Painter.paint(g, evt);
		}
	}

	public boolean lensEvent(PaintEvent evt) {
		boolean retval = false;

		if (evt.willPaint()) {
			//throw that sucker on the queue!
			PaintEvent pe = (PaintEvent) evt.clone();
			queue.addElement(pe);
			evt.pid = PaintEvent.NOTHING;
			numAccoutrements = 3;
			retval = true;
		}

		if (launch != null) {
			while (evt.next != null) {
				evt = evt.next;
			}
			evt.next = launch;
			launch = null;
			grabEvents = false;
		}
		return retval;
	}

	public void borderAccoutrements(Graphics g, int x, int y) {

		if (numAccoutrements != 0) {
			//double up.
			g.drawLine(x + 4, y + 2, x + 4, y + 9);
			g.drawLine(x + 6, y + 2, x + 6, y + 9);
			g.drawLine(x + 3, y + 3, x + 5, y);
			g.drawLine(x + 7, y + 3, x + 5, y);

			x -= 10;
			//single up
			g.drawLine(x + 5, y + 1, x + 5, y + 9);
			g.drawLine(x + 3, y + 3, x + 5, y);
			g.drawLine(x + 7, y + 3, x + 5, y);

			x -= 10;
			//double down.
			g.drawLine(x + 4, y, x + 4, y + 7);
			g.drawLine(x + 6, y, x + 6, y + 7);
			g.drawLine(x + 3, y + 6, x + 5, y + 9);
			g.drawLine(x + 7, y + 6, x + 5, y + 9);
		}
	}

	public boolean clickedAccoutrement(int num) {
		if (num == 0) {
			queue.removeAllElements();
			numAccoutrements = 0;
			return true;
		}
		if (num == 1) {
			queue.removeElementAt(queue.size() - 1);
			if (queue.size() == 0) {
				numAccoutrements = 0;
			}
			return true;
		}
		if (num == 2) {
			if (queue.size() > 0) {
				PaintEvent newlaunch = (PaintEvent) queue.elementAt(0);
				PaintEvent next = newlaunch;
				for (int ii=1; ii < queue.size(); ii++) {
					while (next.next != null) {
						next = next.next;
					}
					next.next = (PaintEvent) queue.elementAt(ii);
					next = next.next;
				}
				if (launch != null) {
					next = launch;
					while (next.next != null) {
						next = next.next;
					}
					next.next = newlaunch;
				} else {
					launch = newlaunch;
				}
				queue.removeAllElements();
				numAccoutrements = 0;
				grabEvents = true;
				return true;
			}
		}
		return false;
	}
}
