import java.awt.*;
import java.awt.image.*;

public class InverseLens extends FilterLens {

	static ImageFilter inversefilter = new inverseFilter();

	public void init(String s) {
		name = "negative";
		filter = inversefilter;
	}
}

class inverseFilter extends RGBImageFilter {

	public inverseFilter() {
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		int r = (rgb >>16) & 0xFF;
		int g = (rgb >>8) & 0xFF;
		int b = rgb & 0xFF;

		// this gives a fun photonegative look.
		r = 255 -r;
		b = 255 -b;
		g = 255 -g;
		return (255<<24) | (r <<16) | (g << 8) |b;
	}
}
