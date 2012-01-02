// ScrollPanel
//
// Ray Greenwell
// ray@go2net.com
//
// v1.0
// Mimics the complete functionality of java.awt.Panel, but adds scrollbars
// if needed! Very useful.

import java.awt.*;
import java.io.PrintStream;

public class ScrollPanel extends Panel implements LayoutManager {
	Panel		child = new Panel();
	Canvas		corner = new Canvas();
	Scrollbar	vert, horz;
	boolean		setup = false;

	public ScrollPanel() {
		super.setLayout(this);

		vert = new Scrollbar(Scrollbar.VERTICAL);
		horz = new Scrollbar(Scrollbar.HORIZONTAL);

		super.add(child, -1);
		super.add(vert, -1);
		super.add(horz, -1);
		super.add(corner, -1);
		vert.hide();
		horz.hide();
		corner.hide();
		setup = true;
	}

	public Dimension preferredSize() {
		//oh this is super stealthy.
		Dimension d = child.preferredSize();
		if (vert.isVisible()) {
			d.width += vert.minimumSize().width;
		}
		if (horz.isVisible()) {
			d.height += horz.minimumSize().height;
		}
		return d;
	}
	public Dimension minimumSize() {
		Dimension d = child.minimumSize();
		if (vert.isVisible()) {
			d.width += vert.minimumSize().width;
		}
		if (horz.isVisible()) {
			d.height += horz.minimumSize().height;
		}
		return d;
	}

	//Panel methods that we wish to override so that we appear
	//to the user of this class to be a regular panel.

	public Component add(Component comp) {
		return child.add(comp);
	}
	public synchronized Component add(Component comp, int pos) {
		return child.add(comp, pos);
	}
	public synchronized Component add(String name, Component comp) {
		return child.add(name, comp);
	}
	public int countComponents() {
		return child.countComponents();
	}
	public void deliverEvent(Event e) {
		//might need to do some stuff here.
		child.deliverEvent(e);
	}
	public synchronized Component getComponent(int n) {
		return child.getComponent(n);
	}
	public synchronized Component[] getComponents() {
		return child.getComponents();
	}
	public LayoutManager getLayout() {
		return child.getLayout();
	}
	public Insets insets() {
		return child.insets();
	}
	public void list(PrintStream out, int indent) {
		child.list(out, indent);
	}
	public Component locate(int x, int y) {
		return child.locate(x, y);
	}
	public synchronized void remove(Component comp) {
		child.remove(comp);
	}
	public synchronized void removeAll() {
		child.removeAll();
	}
	public synchronized void removeNotify() {
		child.removeNotify();
	}
	public void setLayout(LayoutManager mgr) { 
		//annoying. the default constructor for Panel sets up a layoutmgr.
		// we want to skip it.
		if (!setup) {
			return;
		}
		child.setLayout(mgr);
	}


	public boolean handleEvent(Event evt) {
		switch (evt.id) {
			case Event.SCROLL_ABSOLUTE:
			case Event.SCROLL_LINE_UP:
			case Event.SCROLL_LINE_DOWN:
			case Event.SCROLL_PAGE_UP:
			case Event.SCROLL_PAGE_DOWN:
				if (evt.target == horz) {
					child.move(- horz.getValue(), child.location().y);
				}
				if (evt.target == vert) {
					child.move(child.location().x, - vert.getValue());
				}
				return true;
		}
		return false;
	}

	public void addLayoutComponent(String name, Component comp) { }
	public void removeLayoutComponent(Component comp) { }
	public Dimension preferredLayoutSize(Container parent) {	
		return parent.preferredSize();
	}
	public Dimension minimumLayoutSize(Container parent) {
		return parent.minimumSize();
	}

	public void layoutContainer(Container parent) {
		boolean horzon = false;
		boolean verton = false;

		int xoff = vert.minimumSize().width;
		int yoff = horz.minimumSize().height;

		Dimension childsize = child.preferredSize();
		Dimension mysize = parent.size();
		
		if (childsize.width > mysize.width) {
			//we for sure need to turn on the horz scrollbar.
			//do we also need the vertical bar??
			horzon = true;
			if (childsize.height > mysize.height - yoff) {
				verton = true;
			}
		} else if (childsize.height > mysize.height) {
			//ok so we need vert bar.
			verton = true;
			if (childsize.width > mysize.width - xoff) {
				horzon = true;
			}
		}
		vert.show(verton);
		horz.show(horzon);
		corner.show(verton && horzon);

		if (horzon) {
			horz.setValues(0, mysize.width - xoff, 0,
				childsize.width - (mysize.width - ((verton) ? xoff : 0)));
			horz.reshape(0, mysize.height - yoff, mysize.width -
				((verton) ? xoff : 0), yoff);
		}
		if (verton) {
			vert.setValues(0, mysize.height - yoff, 0,
				childsize.height - (mysize.height - ((horzon) ? yoff : 0)));
			vert.reshape(mysize.width - xoff, 0, xoff, mysize.height -
				((horzon) ? yoff : 0));
		}

		if ((!horzon) && (!verton)) {
			child.reshape(0, 0, mysize.width, mysize.height);
			return;
		}
		if ((horzon) && (!verton)) {
			child.reshape(0, 0, childsize.width, mysize.height - yoff);
			return;
		}
		if ((!horzon) && (verton)) {
			child.reshape(0, 0, mysize.width - xoff, childsize.height);
			return;
		}

		//all thats left is vert and horz both on.
		corner.reshape(mysize.width - xoff, mysize.height - yoff, xoff, yoff);
		child.reshape(0, 0, childsize.width, childsize.height);
	}
}
