import java.awt.*;
import java.awt.image.*;


// parent class for lenses that do some kind of image filtering
//

public abstract class FilterLens extends Lens {

	public FilterLens() {
		needsImage = true;
	}

	//subclass this mofo and make filter do something.
	protected ImageFilter filter;

	public void drawEffect(Graphics g, Image img) {
		g.drawImage(doImage(new FilteredImageSource(img.getSource(), filter)),
					0, 0, WIDTH, HEIGHT, null);
	}
}
