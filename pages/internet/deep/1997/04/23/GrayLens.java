import java.awt.*;
import java.awt.image.*;

public class GrayLens extends FilterLens {


	static ImageFilter grayfilter = new GrayScaleFilter();

	public GrayLens() {
		name = "Grayscale";
		filter = grayfilter;
	}

}

class GrayScaleFilter extends RGBImageFilter {

	public GrayScaleFilter() {
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		int r = (rgb >>16) & 0xFF;
		int g = (rgb >>8) & 0xFF;
		int b = rgb & 0xFF;

		int avg = (r + g + b) / 3;

		return (255 <<24) | (avg <<16) | (avg <<8) | avg;
	}
}
