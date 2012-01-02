import java.awt.*;
import java.awt.image.*;


public class Lens {
	public String name = "Generic Lens";
	public boolean needsImage = false;

	//The Incas were fascinated with powers of 60....
	private final static int MINBORDER = 1;
	private final static int MAXBORDER = 10;

	public final static int WIDTH = 100;
	public final static int HEIGHT = 90;

	

	protected static Component comp;
	private static Font font = new Font("Helvetica", Font.PLAIN, 10);

	public static void initializeLenses(Component compy) {
		comp = compy;
	}

	//you can override drawAt to add other gizmos to the border.
	public void drawAt(Image master, int x, int y) {

		Image mid = null;
		if (needsImage) {
			ImageFilter crop = new CropImageFilter(x,
										y + 10, WIDTH, HEIGHT);
			mid = doImage(new FilteredImageSource(master.getSource(), crop));
		}

		Graphics g = master.getGraphics();

		g.setColor(Color.lightGray);
		g.fillRect(x, y, WIDTH, 10);
		g.setColor(Color.black);
		g.setFont(font);
		g.drawString(name, x + 2, y + 8);
		Graphics weeG = g.create(x, y + 10, WIDTH, HEIGHT);

		drawEffect(weeG, mid);
	}

	//you can override drawEffect for each lens
	//if needsImage is set to true, an Image of the effect area is cut
	//out and passed in. 
	public void drawEffect(Graphics g, Image img) {
	}

	protected Image doImage(ImageProducer ip) {
		Image i = comp.createImage(ip);

	//	MediaTracker mt = new MediaTracker(comp);
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
}
