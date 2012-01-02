/**
 * Used to build Polygons.
 */
class PolyLine {
    int      dx, dy;
    PolyLine link;

    PolyLine(int dx, int dy, PolyLine link) {
	this.dx = dx;
	this.dy = dy;
	this.link = link;
    }
};

