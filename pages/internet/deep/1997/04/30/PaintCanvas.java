import java.awt.*;

public class PaintCanvas extends Canvas {
	Image 	 offImg, master;
	Graphics offG, masterG;
	Toolglass glass;

//	public void addNotify() {
//		super.addNotify();

	public void doo() {
		Dimension d = size();
		offImg = createImage(d.width, d.height);
		master = createImage(d.width, d.height);
		offG = offImg.getGraphics();
		masterG = master.getGraphics();
		offG.setColor(Color.white);
		offG.fillRect(0, 0, d.width, d.height);

		glass = new Toolglass(this);
	}

	public boolean handleEvent(Event evt) {

		PaintEvent pe = new PaintEvent(evt);
		boolean repainting = false;
		repainting = repainting || glass.deliverEvent(pe);
		repainting = repainting || Painter.paint(offG, pe);
		if (repainting) {
			repaint();
		}
		return true;
	}

	public void paint(Graphics g) {
		if (masterG == null) {
			doo();
		}
		masterG.drawImage(offImg, 0, 0, null);
		glass.paint(master);
		g.drawImage(master, 0, 0, this);
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void addLens(String name, String param) {
		glass.addLens(name, param);
	}
}
