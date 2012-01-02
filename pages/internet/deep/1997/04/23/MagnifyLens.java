import java.awt.image.*;
import java.awt.*;


public class MagnifyLens extends FilterLens {

	private static ImageFilter magnaFilter;
	private static final int magna = 2;
	static {
		int w = WIDTH / magna;
		int h = HEIGHT / magna;
		magnaFilter = new CropImageFilter((WIDTH - w) / 2,
											(HEIGHT - h) / 2, w, h);
	}

	public MagnifyLens() {
		name = "Magnify";
		filter = magnaFilter;
	}
}
