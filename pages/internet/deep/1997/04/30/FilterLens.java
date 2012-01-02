import java.awt.*;
import java.awt.image.*;


// parent class for lenses that do some kind of image filtering
//

public abstract class FilterLens extends Lens {

	public void initialize(Toolglass glass, int x, int y, String params){
		super.initialize(glass, x, y, params);
		preferredStackPosition = .9;
		needsImage = true;
	}

	//subclass this mofo and make filter do something.
	protected ImageFilter filter;

	public void drawEffect(Graphics g, Image img) {
		g.drawImage(doImage(new FilteredImageSource(img.getSource(), filter)),
					0, 0, width, height, null);
	}
}
