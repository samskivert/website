import java.awt.*;
import java.util.StringTokenizer;

/**
 * Grid
 *
 * the grid of control points that the adjustor manipulates, and
 * the morph uses to construct a quilt.
 */
public class Grid {
     public int[][][] points;
     public int w, h;

     public final static int X = 0;
     public final static int Y = 1;

     public final static int DEFAULT_GRIDSIZE = 10;
     private final static String MAGIC_NUMBER = "Gridv1.0";

     public Grid(int width, int height) {
          this(width, height, DEFAULT_GRIDSIZE);
     }
     
     public Grid(int width, int height, int gridsize) {
          //we want enough points to have the edges of the picture count too.
          w = (int) Math.ceil(((double) width) / gridsize) + 1;
          h = (int) Math.ceil(((double) height) / gridsize) + 1;

          points = new int[w][h][2];

          for (int ii=0; ii < w; ii++) {
               for (int jj=0; jj < h; jj++) {
                    points[ii][jj][X] = Math.min(ii * gridsize, width);
                    points[ii][jj][Y] = Math.min(jj * gridsize, height); 
               }
          }
     }

     public Grid(String encodedString) {
          StringTokenizer st = new StringTokenizer(encodedString, " :;,");
          if (!MAGIC_NUMBER.equals(st.nextToken())) {
               throw new IllegalArgumentException("Grid(String) constructor " +
                         "requires an encoded grid object");
          }

          w = Integer.parseInt(st.nextToken());
          h = Integer.parseInt(st.nextToken());
          points = new int[w][h][2];
          for (int jj=0; jj < h; jj++) {
               for (int ii=0; ii < w; ii++) {
                    points[ii][jj][X] = Integer.parseInt(st.nextToken());
                    points[ii][jj][Y] = Integer.parseInt(st.nextToken());
               }
          }
     }

     public Point findClosestGridPoint(int x, int y) {
          //boy is this fast!
          int mind = Integer.MAX_VALUE;
          int thisd, xp, yp;
          int xc = 0, yc = 0;

          //I shoulda' called this class 'grind'
          for (int ii=0; ii < w; ii++) {
               for (int jj=0; jj < h; jj++) {
                    xp = points[ii][jj][X] - x;
                    yp = points[ii][jj][Y] - y;
                    thisd = xp * xp + yp * yp;
                    if (thisd < mind) {
                         mind = thisd;
                         xc = ii;
                         yc = jj;
                    }
               }
          }
          return new Point(xc, yc);
     }

     public void moveGridPointTo(Point p, int x, int y) {
          if ((p.x == 0) || (p.y == 0) || (p.x == w-1)  || (p.y == h-1)) {
               return;
          }
          //optimally move the point to x,y
          //but may not if it crosses a gridline.

          //again- not super optimal. But it gets the job done. (users are slow!)
          Polygon g = new Polygon();
          g.addPoint(points[p.x - 1][p.y - 1][0], points[p.x - 1][p.y - 1][1]);
          g.addPoint(points[p.x][p.y - 1][0], points[p.x][p.y - 1][1]);
          g.addPoint(points[p.x + 1][p.y - 1][0], points[p.x + 1][p.y - 1][1]);
          g.addPoint(points[p.x + 1][p.y][0], points[p.x + 1][p.y][1]);
          g.addPoint(points[p.x + 1][p.y + 1][0], points[p.x + 1][p.y + 1][1]);
          g.addPoint(points[p.x][p.y + 1][0], points[p.x][p.y + 1][1]);
          g.addPoint(points[p.x - 1][p.y + 1][0], points[p.x - 1][p.y + 1][1]);
          g.addPoint(points[p.x - 1][p.y][0], points[p.x - 1][p.y][1]);

          if (g.inside(x, y)) {
               points[p.x][p.y][X] = x;
               points[p.x][p.y][Y] = y;
          }
     }

     public String toString() {
          //encode the grid to a string;
          String ret = MAGIC_NUMBER + " " + w + ":" + h; 
          
          for (int jj=0; jj < h; jj++) {
               for (int ii=0; ii < w; ii++) {
                    ret += " " + points[ii][jj][X] + "," + points[ii][jj][Y];
               }
          }
          return ret;
     }
}
