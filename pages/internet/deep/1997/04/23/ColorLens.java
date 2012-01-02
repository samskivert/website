import java.awt.*;
public class ColorLens extends Lens {

	public ColorLens(Color color) {
		this.color = color;
		needsImage = false;
		name = "Color";
	}

	Color color;
	static Polygon polygon;
	static {
		polygon = new Polygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(0, 10);
		polygon.addPoint(10, 0);
	}

	public void drawEffect(Graphics g, Image img) {
		g.setColor(color);
		g.fillPolygon(polygon);
	}
}
