import java.applet.*;
import java.awt.*;

//rgb2hsv
//
// a funky little applet that does hsv-rgb stuff.
public class rgb2hsv extends Applet {

	HSVCircle hsv;
	Scrollbar R, G, B, V;
	TextField Rb, Gb, Bb, Vb;
	BoxOColor box;

	Color curcolor = Color.white;

	public void init() {

		int bgcolor= 0xFFFFFF;
		try {
			bgcolor = Integer.parseInt(getParameter("bgcolor"), 16);
		} catch (Exception e) {}
		setBackground(new Color(bgcolor));


		//ok. The AWT blows. To have to do all this for my simple layout
		//needs is just ludicrous. I used to defend it, but it's just
		//got to go.
		hsv = new HSVCircle(this);
		box = new BoxOColor();
		box.setColor(Color.white);

		R = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 255);
		G = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 255);
		B = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 255);
		V = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 100);
		Rb = new TextField("255",3); 
		Rb.setEditable(false);
		Gb = new TextField("255",3); 
		Gb.setEditable(false);
		Bb = new TextField("255",3); 
		Bb.setEditable(false);
		Vb = new TextField("1", 3); 
		Vb.setEditable(false);

		Panel HSVp = new Panel();
		HSVp.setLayout(new BorderLayout());
		HSVp.add("North", hsv);
		HSVp.add("West", V);
		HSVp.add("Center", new Label("Value"));
		HSVp.add("South", Vb);

		Panel Rp = new Panel();
		Rp.setLayout(new BorderLayout());
		Rp.add("West", R);
		Rp.add("Center", new Label("Red"));
		Rp.add("South", Rb);

		Panel Gp = new Panel();
		Gp.setLayout(new BorderLayout());
		Gp.add("West", G);
		Gp.add("Center", new Label("Green"));
		Gp.add("South", Gb);

		Panel Bp = new Panel();
		Bp.setLayout(new BorderLayout());
		Bp.add("West", B);
		Bp.add("Center", new Label("Blue"));
		Bp.add("South", Bb);

		Panel RGBp = new Panel();
		RGBp.setLayout(new GridLayout(1, 3));
		RGBp.add(Rp);
		RGBp.add(Gp);
		RGBp.add(Bp);
		
		Panel RGBp2 = new Panel();
		RGBp2.setLayout(new BorderLayout());
		RGBp2.add("North", box);
		RGBp2.add("Center", RGBp);

		setLayout(new BorderLayout());
		add("West", HSVp);
		add("Center", RGBp2);
	}

	public boolean handleEvent(Event evt) {

		switch (evt.id) {
			case Event.SCROLL_ABSOLUTE:
			case Event.SCROLL_LINE_UP:
			case Event.SCROLL_LINE_DOWN:
			case Event.SCROLL_PAGE_UP:
			case Event.SCROLL_PAGE_DOWN:
				if (evt.target == V) {
					adjustHSV();
				} else {
					adjustRGB();
				}
				setBoxes();
				return true;
		}
		return false;
	}

	//called by circle.
	public void setHSV() {
		adjustHSV();
		setBoxes();
	}

	public void adjustHSV() {
		//someone changed something! in HSV land.
		float h = hsv.getH();
		float s = hsv.getS();
		float v = ((float) (100 - V.getValue())) / 100.0f;
		curcolor = Color.getHSBColor(h, s, v);

		R.setValue(255 - curcolor.getRed());
		G.setValue(255 - curcolor.getGreen());
		B.setValue(255 - curcolor.getBlue());
	}
		
	public void adjustRGB() {
		int r = 255 - R.getValue();
		int g = 255 - G.getValue();
		int b = 255 - B.getValue();

		curcolor = new Color(r, g, b);
		float[] hsb = new float[3];
		Color.RGBtoHSB(r, g, b, hsb);
		hsv.setHS(hsb[0], hsb[1]);
		V.setValue(100 - (int) Math.round(hsb[2] * 100));
	}

	public void setBoxes() {
		box.setColor(curcolor);

		Vb.setText(String.valueOf((100 - V.getValue()) / 100.0f));

		Rb.setText(String.valueOf(255 - R.getValue()));
		Gb.setText(String.valueOf(255 - G.getValue()));
		Bb.setText(String.valueOf(255 - B.getValue()));
	}
}


// this is simple the Box-O-Color that displays the current selected color.
class BoxOColor extends Canvas {
	Color c;

	public void setColor(Color c) {
		this.c = c;
		repaint();
	}

	public void paint(Graphics g) {
		update(g);
	}

	public void update(Graphics g) {
		g.setColor(Color.black);
		g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
		g.setColor(c);
		g.fillRect(1, 1, WIDTH - 2, HEIGHT - 2);
	}

	public Dimension preferredSize() {
		return new Dimension(WIDTH, HEIGHT);
	}

	public Dimension minimumSize() {
		return preferredSize();
	}

	static final int WIDTH = 100;
	static final int HEIGHT = 100;
}


// a HSV color circle.
class HSVCircle extends Canvas {
	
	Image circle = null;
	int dotx, doty;
	float H = 0.0f, S = 0.0f;
	rgb2hsv parent;
	

	public HSVCircle(rgb2hsv parent) {
		dotx = doty = RADIUS;
		this.parent = parent;
	}

	public float getH() {
		return H;
	}

	public float getS() {
		return S;
	}

	public void setHS(float h, float s) {
		H = h;
		S = s;

		//do some other stuff;
		//find the x and y for that h & s.
		h *= (float) (Math.PI * 2);

		float r = RADIUS * s;

		dotx = (int) Math.round(r * Math.cos(h)) + RADIUS;
		doty = (int) Math.round(-r * Math.sin(h)) + RADIUS;
		repaint();
	}

	private void createCircle() {
		circle = createImage(RADIUS * 2, RADIUS * 2);
		Graphics g = circle.getGraphics();

		for (int ii=0; ii < RADIUS * 2; ii++) {
			for (int jj=0; jj < RADIUS * 2; jj++) {
				float sat = findSaturation(ii, jj);
				if (sat <= 1.0) {
					//draw the appropriate color for this pixel.
					g.setColor(Color.getHSBColor(findHue(ii, jj), sat, 1.0f));
				} else {
					g.setColor(getBackground());
				}
				g.drawLine(ii, jj, ii, jj);
			}
		}
	}

	public void addNotify() {
		super.addNotify();
		createCircle();
	}

	public boolean handleEvent(Event evt) {
		switch (evt.id) {
			case Event.MOUSE_DOWN:
			case Event.MOUSE_DRAG:

				//bound the saturation to 1.0, that way the thing
				//will behave intuitively on drags.
				setHS(findHue(evt.x, evt.y),
					Math.min(1.0f, findSaturation(evt.x, evt.y)));

				//but also notify daddy
				parent.setHSV();
				return true;
		}
		return false;
	}


	//return the angle for a point. (the H value)
	float findHue(int x, int y) {
		x -= RADIUS;
		y -= RADIUS;

		float h;
		if (x != 0) {
			h = (float) Math.atan(((double) Math.abs(y)) /
								((double) Math.abs(x)));
		} else {
			h = (float) Math.atan(Double.MAX_VALUE);
		}

		//there might be a faster way to do this, I should brush up
		//on my trig. OH. That's a lie, you know I'm not going to.
		if (x > 0) {
			if (y > 0) {
				h = ((float) (Math.PI * 2.0f)) - h;
			} else {
				h = h;
			}
		} else {
			if (y > 0) {
				h = (float) Math.PI + h;
			} else {
				h = (float) Math.PI - h;
			}
		}

		//scale them radians down to that 0-1 range!
		return h / ((float) (Math.PI * 2));
	}

	//return the distance from the center of the circle. (S)
	float findSaturation(int x, int y) {
		return ((float) Math.sqrt(((RADIUS - x) *
					(RADIUS -x)) + ((RADIUS - y) * (RADIUS - y))))
			 / (float) RADIUS;
	}

	public void paint(Graphics g) {
		update(g);
	}

	public void update(Graphics g) {
		g.drawImage(circle, 0, 0, null);
		g.setColor(Color.black);
		g.drawLine(dotx - 3, doty, dotx + 3, doty);
		g.drawLine(dotx, doty - 3, dotx, doty + 3);
	}

	public Dimension preferredSize() {
		return new Dimension(RADIUS * 2, RADIUS * 2);
	}

	public Dimension minimumSize() {
		return preferredSize();
	}

	final static int RADIUS = 50;
}
