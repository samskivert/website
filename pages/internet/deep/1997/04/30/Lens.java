import java.awt.*;
import java.awt.image.*;
import java.util.Vector;


public abstract class Lens implements Sortable {

	public String name = "Generic Lens";
	public boolean needsImage = false;
	public boolean unlocked = true;
	public boolean secondClass = false;

	private final static int TOPBORDER = 10;
	private final static int SIDEBORDERS = 1;

	public final static int DEFAULT_WIDTH = 100;
	public final static int DEFAULT_HEIGHT = 90;

	//5 percent baby.
	private final static double hysteresis = .05;

	private static Font font = new Font("Helvetica", Font.PLAIN, 10);

	protected Toolglass glass;
	private static Image border = null;
	private int offsetx, offsety;
	private boolean grabBorder = false;
	protected Lens buddy;

	public int width = DEFAULT_WIDTH;
	public int height = DEFAULT_HEIGHT;
	public int x, y;
	public double preferredStackPosition = .5;
	public int numAccoutrements = 0;


	//if set to true, then this lens assumes that events are important
	// to it, even when they are 'out-of-bounds'
	protected boolean grabEvents = false;

	public Lens() {
	}

	public void initialize (Toolglass glass, int x, int y, String params) {
		this.glass = glass;
		this.x = x;
		this.y = y;
		init(params);
	}

	public void init(String params) {
	}

//	private void makeBorder() {
//		int pix[] = new int[100 * 10];
//		ColorModel cm = new DirectColorModel(32, 0xff0000, 0xff00, 0xff,
//											0xff000000);
//		MemoryImageSource mis = new MemoryImageSource(100, 10, cm, pix, 0, 10);
//		int index=0;
//		for (int yy=0; yy < 10; yy++) {
//			for (int xx=0; xx < 100; xx++) {
//				pix[index++] = (127 << 24) | (127 <<16) | (127 <<8) | 127;
//			}
//		}
//		border = doImage(mis);
//	}

	//you can override drawAt to add other gizmos to the border.
	public final void draw(Image master) {
//		if (border == null) {
//			makeBorder();
//		}

		Image mid = null;
		if (needsImage) {
			ImageFilter crop = new CropImageFilter(x,
										y + TOPBORDER, width, height);
			mid = doImage(new FilteredImageSource(master.getSource(), crop));
		}

		Graphics g = master.getGraphics();

		g.setColor(Color.lightGray);
		g.fillRect(x, y, width, 10);
		//g.drawImage(border, x, y, null);
		g.setColor(Color.black);

		//draw the accoutrements
		drawX(g, x + width - 10, y); 
		drawLock(g, x + width - 20, y);
		//check to see if we wanna J.
		if (checkJoin() != null) {
			drawJ(g, x + width - 30, y);
			borderAccoutrements(g, x + width - 40, y);
		} else {
			borderAccoutrements(g, x + width - 30, y);
		}

		g.setFont(font);
		g.drawString(name, x + 2, y + 8);
		Graphics weeG = g.create(x, y + TOPBORDER, width, height);

		drawEffect(weeG, mid);
		g.drawLine(x, y + TOPBORDER, x, y + height + TOPBORDER);
		g.drawLine(x + width, y + TOPBORDER, x + width, y + height + TOPBORDER);
		g.drawLine(x, y + height + TOPBORDER,x + width, y + height + TOPBORDER);
	}

	private void drawX(Graphics g, int x, int y) {
		g.drawLine(x + 2, y + 2, x + 8, y + 8);
		g.drawLine(x + 2, y + 8, x + 8, y + 2);
	}

	private void drawLock(Graphics g, int x, int y) {
		int offs = (unlocked) ? 1 : 0;
		g.drawRect(x + 2, y + 5 , 6, 4);
		g.drawLine(x + 3, y + 5 - offs, x + 3, y + 3 -offs);
		g.drawLine(x + 4, y + 2 - offs, x + 5, y + 1 -offs);
		g.drawLine(x + 6, y + 1 - offs, x + 8, y + 3 -offs);
		if (!unlocked) {
			g.drawLine(x + 8, y + 4, x + 8, y + 5);
		}
	}

	private void drawJ(Graphics g, int x, int y) {
		g.setFont(font);
		g.drawString("J", x + 2, y + 10);
	}

	private boolean isBuddy(Lens l) {
		if (buddy == l) {
			return true;
		} else {
			if (buddy == null) {
				return false;
			} else {
				return buddy.isBuddy(l);
			}
		}
	}

	private Lens checkJoin() {
		for (int ii=glass.indexOfLens(this) + 1; ii < glass.countLenses(); ii++) {
			Lens l = glass.lensAt(ii);
			if (l.secondClass) {
				continue;
			}
			if (((((double) (Math.abs(l.x - x))) / ((double) width)) <
											hysteresis) &&
				((((double) (Math.abs(l.y - y))) / ((double) height)) <
											hysteresis) &&
				((((double) (Math.abs(l.width - width))) / ((double) width)) <
											hysteresis) &&
				((((double) (Math.abs(l.height - height))) / ((double) width)) <
											 hysteresis)) {
				return l;
			}
		}
		return null;
	}

	//a fun function you can override to add shit to the border. Actually
	// you can do lots of nasty things.
	public void borderAccoutrements(Graphics g, int x, int y) {
	}

	public boolean clickedAccoutrement(int num) {
		return false;
	}


	//you can override drawEffect for each lens
	//if needsImage is set to true, an Image of the effect area is cut
	//out and passed in. 
	public void drawEffect(Graphics g, Image img) {
	}

	public final boolean deliverEvent(PaintEvent evt) {

		boolean result = false;
		if (((evt.x >= x) && (evt.y >= y) && (evt.x <= x + width) &&
			(evt.y <= y + width)) || (grabEvents) || (grabBorder)) {

			//keep x and y in case some fool decides to modify those.
			int myx = x;
			int myy = y;

			if ((evt.pid != PaintEvent.STOP) &&
				((evt.y >= myy + TOPBORDER) || (grabEvents))) {
				//deliver it to the lens proper.
				evt.translate(-myx, 0 - (myy + TOPBORDER));
				result = lensEvent(evt);
				evt.translate(myx, myy + TOPBORDER);
			}
			if (((evt.y < myy + TOPBORDER) || (grabBorder)) && (!secondClass)) {
				//deliver it to the border
				evt.translate(-myx, -myy);
				result = borderEvent(evt);
				//border events DIE.
				evt.pid = PaintEvent.STOP;
				evt.id = 0;
				evt.translate(myx, myy);
			}
		}
		return result;
	}

	//return true if repainting needs to occur
	public boolean lensEvent(PaintEvent evt) {
		return false;
	}

	private void moveBuddy() {
		if (buddy != null) {
			buddy.x = x;
			buddy.y = y;
			buddy.width = width;
			buddy.height = height;
			buddy.moveBuddy();
		}
	}
			

	boolean readyToDrag = false;
	//return true if repainting needs to occur
	public boolean borderEvent(PaintEvent evt) {
		if (grabBorder) {
			if ((evt.id == Event.MOUSE_UP) ||
				(evt.id == Event.MOUSE_DRAG)) {
				x += evt.x - offsetx;
				y += evt.y - offsety;
				moveBuddy();
				if (evt.id == Event.MOUSE_UP) {
					grabBorder = false;
					readyToDrag = false;
				}
				return true;
			}
		} else {
			if (readyToDrag) {
				if (evt.id == Event.MOUSE_DRAG) {
					grabBorder = true;
					offsetx = evt.x;
					offsety = evt.y;
					evt.pid = PaintEvent.STOP;
				}
			}
			if (evt.id == Event.MOUSE_DOWN) {
				readyToDrag = true;
			}
			if (evt.id == Event.MOUSE_UP) {
				readyToDrag = false;
				if (evt.x >= width - 10) {
					//they click X. go away.
					removeSelf();
					return true;
				}
				if (evt.x >= width - 20) {
					setlock(!unlocked);
					return true;
				}
				Lens other = checkJoin();
				if (other != null) {
					if (evt.x >= width - 30) {
						addBuddy(other);
						other.setlock(unlocked);
						return true;
					}
				}

				int accoutstart = 20 + ((other != null) ? 10 : 0);
				int accnum = ((width - accoutstart) - evt.x) / 10;
				if (accnum < numAccoutrements)  {
					return clickedAccoutrement(accnum);
				}
			}
		}
		return false;
	}

	protected void removeSelf() {
		glass.removeLens(this);
		if (buddy != null) {
			buddy.removeSelf();
		}
	}

	protected void setlock(boolean unlockness) {
		unlocked = unlockness;
		if (buddy != null) {
			buddy.setlock(unlockness);
		}
	}

	protected boolean addBuddy(Lens l) {
		l.grabEvents = l.readyToDrag = false;

		if (buddy != null) {
			if (buddy.addBuddy(l)) {
				name = name + "-" + l.name;
			} else {
				return false;
			}
		} else {
			//do the join!
			name = name + "-" + l.name;
			l.secondClass = true;
			buddy = l;
			moveBuddy();
		}
		return true;
	}
		

	protected Image doImage(ImageProducer ip) {
		Image i = glass.daddy.createImage(ip);

	//	MediaTracker mt = new MediaTracker(glass.daddy);
	//	mt.addImage(i, 0);
	//	try {
	//		mt.waitForAll();
	//	} catch (InterruptedException e) {
	//		System.out.println("OUCH");
	//		e.printStackTrace(System.out);
	//	}
	//	if (mt.isErrorAny()) {
	//		System.out.println("errors!");
	//		Object[] o = mt.getErrorsAny();
	//		for (int ii=0; ii < o.length; ii++) {
	//			System.out.println(o[ii]);
	//		}
	//	}
		return i;
	}

	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}


	public boolean lessThan(Sortable comp) {
		Lens other = (Lens) comp;
		if (preferredStackPosition != other.preferredStackPosition) {
			return (preferredStackPosition > other.preferredStackPosition);
		} else {
			return (glass.indexOfLens(this) < glass.indexOfLens(other));
		}
	}
}
