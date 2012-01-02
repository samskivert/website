import java.awt.image.*;
import java.awt.*;


public class MagnifyLens extends FilterLens {

	//default magnification: 2
	private int magna = 2;

	public void initialize(Toolglass glass, int x, int y, String params) {
		super.initialize(glass, x, y, params);
		name = "Magnify";
		try {
			magna = Integer.parseInt(params);
		} catch (Exception e) {
		}
		recreateMagnaFilter();
	}

	public void resize(int width, int height) {
		super.resize(width, height);
		recreateMagnaFilter();
	}

	public void recreateMagnaFilter() {
		int w = width / magna;
		int h = height / magna;
		filter = new CropImageFilter((width - w) / 2,
											(height - h) / 2, w, h);
	}

	//magnify the event!!
	public boolean lensEvent(PaintEvent evt) {
		evt.x = evt.x / magna;
		evt.y = evt.y / magna;
		int w = width / magna;
		int h = height / magna;
		evt.translate((width - w) / 2, (height - h) / 2);
		return false;
	}

	protected boolean addBuddy(Lens l) {
        if (l instanceof MagnifyLens) {
			magna *= ((MagnifyLens) l).magna;
			recreateMagnaFilter();
            glass.removeLens(l);
			name += "+";
            return false;
        } else {
            //look for a mag in the otherguys buddylist.
            Lens l2 = l;
            while (l2.buddy != null) {
                l2 = l2.buddy;
                if (l2 instanceof MagnifyLens) {
                    MagnifyLens cl = (MagnifyLens) l2;
                    cl.magna *= magna;
					recreateMagnaFilter();
                    glass.removeLens(this);
                    return false;
                }
            }
        }
        return super.addBuddy(l);
    }
}
