import java.awt.*;
import java.util.*;

/**
 * 
 * Toolglass
 * holds all the lenses. Is responsible for being the mack.
 */
public class Toolglass {

	SortableVector lenses = new SortableVector();
	private int gran = 5; 

	public Component daddy;

	public Toolglass(Component daddy) {
		this.daddy = daddy;
	}

	public int countLenses() {
		return lenses.size();
	}

	public Lens lensAt(int i) {
		return (Lens) lenses.elementAt(i);
	}

	public int indexOfLens(Lens l) {
		return lenses.indexOf(l);
	}

	public void removeLens(Lens l) {
		lenses.removeElement(l);
	}

	public void addLens(String name, String param) {
		try {
			Lens newlens = (Lens) Class.forName(name).newInstance();
			Dimension d = daddy.size();
			newlens.initialize(this, d.width / 2, d.height / 2, param);
			insertLens(newlens);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public void insertLens(Lens l) {

		lenses.addElement(l);
		SortableVector spanking = (SortableVector) lenses.clone();
		spanking.sort();
		lenses = spanking;
		daddy.repaint();
	}
	
	public boolean deliverEvent(PaintEvent evt) {
		if (evt.id == Event.KEY_PRESS) {
			if ((evt.evt.key >= '0') && (evt.evt.key <= '9')) {
				gran = evt.evt.key - '0';
				if (gran == 0) {
					gran = 10;
				}
				return false;
			}
			int right = 0;
			int down = 0;
			if (evt.evt.key >= 'a') {
				evt.evt.key -= 'a' - 'A';
			}
			switch (evt.evt.key) {
				case 'Q':
					down = -gran;
				case 'A':
					right = -gran;
					break;
				case 'E':
					right = gran;
				case 'W':
					down = -gran;
					break;
				case 'C':
					down = gran;
				case 'D':
					right = gran;
					break;
				case 'Z':
					right = -gran;
				case 'X':
					down = gran;
					break;
			}
			if ((right != 0) || (down != 0)) {
				for (int ii=0; ii < lenses.size(); ii++)  {
					Lens l = (Lens) lenses.elementAt(ii);
					if (l.unlocked) {
						l.x += right;
						l.y += down;
					}
				}
				return true;
			}
		}
		boolean repainting = false;
		for (int ii=0; ii < lenses.size(); ii++) {
			Lens lens = (Lens) lenses.elementAt(ii);
			repainting = repainting || lens.deliverEvent(evt);
		}
		return repainting;
	}

	public void paint(Image img) {
		for (int ii=lenses.size() - 1; ii >=0; ii--) {
			Lens lens = (Lens) lenses.elementAt(ii);
			lens.draw(img);
		}
	}
}
