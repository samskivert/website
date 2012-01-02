/**
 * Line
 * a fairly useless little class.
 * no magic here.
 */
public class Line {
     public int xmax, xmin, ymax, ymin; 
     public float c, m;
     public boolean perp = false;
     int x01, y01, x02, y02;

     public Line(int x1, int y1, int x2, int y2) {
          x01 = x1;
          x02 = x2;
          y01 = y1;
          y02 = y2;

          float dx = x1 - x2;
          if (dx == 0) {
               perp = true;
          } else {
               m = ((float) y1-y2) / dx;
               c = y1 - (m * x1);
          }
          xmax = Math.max(x1, x2);
          xmin = Math.min(x1, x2);
          ymax = Math.max(y1, y2);
          ymin = Math.min(y1, y2);
     }
}
