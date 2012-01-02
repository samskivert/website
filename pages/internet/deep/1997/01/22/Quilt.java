import java.awt.*;
import java.awt.image.*;

/**
 * Quilt
 *
 * and a bunch of ugly little classes that hang around with Quilt
 */

//my own little polygon, does tests for interection in 3 ways at once.
class Poly {
     ControlPoints[]     p = new ControlPoints[4];
     Line[]               l = new Line[4];

     //defines the controlpoints of a polygon
     public Poly(ControlPoints a, ControlPoints b, ControlPoints c,
                    ControlPoints d) {
          p[0] = a;
          p[1] = b;
          p[2] = c;
          p[3] = d;

          l[0] = new Line(a.x, a.y, b.x, b.y);
          l[1] = new Line(b.x, b.y, c.x, c.y);
          l[2] = new Line(c.x, c.y, d.x, d.y);
          l[3] = new Line(d.x, d.y, a.x, a.y);
     }

     //will test the point, and if it falls into the domain of this
     // polygon- will assign it control points.
     public boolean assignControls(PatchPoint pp) {
          int xx = (int) pp.x0;
          int yy = (int) pp.y0;

          int hits = 0;

          //step through each line in this polygon
          LOOP: for (int ii=0; ii < l.length; ii++) {

               //is the points within the x range of this line?
               if ((xx >= l[ii].xmin) && (xx <= l[ii].xmax)) {

                    //if the line is perpendicular- do some different tests
                    if (l[ii].perp) {

                         //does the points fall into the y range of thise line?
                         if ((yy >= l[ii].ymin) && (yy <= l[ii].ymax)) {
                              //yes- its on this line!!

                              //does it exactly match the first point?
                              if ((yy == l[ii].y01) && (xx == l[ii].x01)) {
                                   pp.addControl(p[ii]);
                                   return true;
                              }

                              //the second point?
                              if ((yy == l[ii].y02) && (xx == l[ii].x02)) {
                                   pp.addControl(p[(ii+1) % 4]);
                                   return true;
                              }

                              //its just somewhere on the line.
                              pp.addControl(p[ii]);
                              pp.addControl(p[(ii+1) % 4]);
                              return true;
                         }
                         //its perp but y's dont' match .. continue;
                         continue LOOP;
                    }
                    //its not perp. do hit test.
                    float tempy = (((float) xx) * l[ii].m) + l[ii].c; 
                    if (tempy == yy) {
                         //its on this line.
                         pp.addControl(p[ii]);
                         pp.addControl(p[(ii+1) % 4]);
                         return true;
                    }
                    if (tempy > yy) {
                         //ooh! hittage.
                         if ((tempy == l[ii].y01) && (l[ii].x01 == xx)) {
                              //if we hit right on the 1st vertex, then don't count
                              continue LOOP;
                         }
                         hits++;
                    }
               }
          }

          if (hits % 2 != 0) {     
               //yes it is in us!!
               pp.addControl(p[0]);
               pp.addControl(p[1]);
               pp.addControl(p[2]);
               pp.addControl(p[3]);
               return true;
          }

          return false;
     }
}

//how much each of the control points will move each step.
class ControlPoints {
     public float dx, dy;

     public int x, y;
          
     public ControlPoints(int beforex, int beforey,
                              int afterx, int aftery, int frames) {
          x = beforex;
          y = beforey;
          float steps = frames - 1;

          dx = ((float) (afterx - x)) / steps;
          dy = ((float) (aftery - y)) / steps;
     }
}

// a corner of a patch.
class PatchPoint {
     float                    totalweight = 0;

     float                    x0, y0;
     float                    dx, dy;
     float                    x, y;

     public PatchPoint(int x, int y) {
          this.x0 = x;
          this.y0 = y;
     }

     public void addControl(ControlPoints p) {
          float xx = x0 - p.x;
          float yy = y0 - p.y;
          float w = (float) Math.sqrt(xx * xx + yy * yy);
               
          if (w == 0.0) {
               w = 1;
          } else {
               w = 1 / w;
          }

          dx += p.dx * w;
          dy += p.dy * w;
          totalweight += w;
     }

     //calculate dx, dy
     public void calc() {
          dx /= totalweight;
          dy /= totalweight;
     }

     //update the pos
     public void updatePosition(int frame) {
          x = x0 + (frame * dx);
          y = y0 + (frame * dy);
     }
}

class Patch {
     //a patch is not a point! oh no- its a quadrilateral that will be
     //stretched and misshapen.
     PatchPoint     a, b, c, d;
     float          red, green, blue;
     
     public Patch(PatchPoint a, PatchPoint b, PatchPoint c, PatchPoint d,
                    Color color) {
          this.a = a;
          this.b = b;
          this.c = c;
          this.d = d;
          red = ((float) color.getRed()) / 255.0f;
          green = ((float) color.getGreen()) / 255.0f;
          blue = ((float) color.getBlue()) / 255.0f;
     }

     //[y][x][r,g,b]
     // Look away children.
     public void addColorToBins(float[][][] z) {
     int topx =(int)Math.ceil(Math.max(Math.max(a.x, b.x), Math.max(c.x, d.x)));
     int botx =(int)Math.floor(Math.min(Math.min(a.x, b.x), Math.min(c.x, d.x)));
     int topy =(int)Math.ceil(Math.max(Math.max(a.y, b.y), Math.max(c.y, d.y)));
     int boty =(int)Math.floor(Math.min(Math.min(a.y, b.y), Math.min(c.y, d.y)));

          //HACK HACK HACK
          topx = Math.min(topx, z[0].length);
          topy = Math.min(topy, z.length);
          botx = Math.max(botx, 0);
          boty = Math.max(boty, 0);

          //this is all bad- What I really want to do is calculate the percentage
          //of intersection with each shape and put that much color into
          //each bin.


          //damn time constraints.
          for (int ii=botx; ii < topx; ii++){
               for (int jj=boty; jj < topy; jj++) {
                    z[jj][ii][0] = red;
                    z[jj][ii][1] = green;
                    z[jj][ii][2] = blue;
               }
          }
     }
}

//The big quilt! A collection of patches.
class Quilt {
     PatchPoint[]     patchpoints;
     Patch[]               patches;
     int                    numframes;

     public Quilt(Image img, Grid from, Grid to, int numframes) {
          this.numframes = numframes;

          //add 1 to width and height, since we are counting the spaces
          // between pixels.
          int width = img.getWidth(null) + 1;
          int height = img.getHeight(null) + 1;


          ControlPoints[][] controls = new ControlPoints[from.w][from.h];

          patchpoints = new PatchPoint[width * height];

          Poly[]          gonz = new Poly[(from.w - 1) * (from.h - 1)];

          //set up the control points
          for (int jj=0; jj < from.h; jj++) {
               for (int ii=0; ii < from.w; ii++) {
                    controls[ii][jj] = new ControlPoints(
                         from.points[ii][jj][0], from.points[ii][jj][1],
                         to.points[ii][jj][0], to.points[ii][jj][1], numframes);
               }
          }

          //set up polygons out of those control points
          int pindex=0;
          for (int jj=0; jj < from.h-1; jj++) {
               for (int ii=0; ii < from.w - 1; ii++) {
                    gonz[pindex++] = new Poly(controls[ii][jj], controls[ii+1][jj],
                                             controls[ii+1][jj+1], controls[ii][jj+1]);
               }
          }
                    
          
          //now go through each pixelpoint in our image, and assign control
          //points based on the pixelpoints relations to that polyogn.
          // look in Poly for what happens to each pixel here.
          int index = -1;
          Poly lastgon = gonz[0];
          for (int jj=0; jj < height; jj++) {
               BIG_LOOP: for (int ii=0; ii < width; ii++) {
                    patchpoints[++index] = new PatchPoint(ii, jj);
                    if (!lastgon.assignControls(patchpoints[index])) {
                         for (int kk=0; kk < gonz.length; kk++) {
                              if (gonz[kk].assignControls(patchpoints[index])) {
                                   lastgon = gonz[kk];
                                   //System.out.println("GOOD: " + ii+"," + jj);
                                   continue BIG_LOOP;
                              }
                         }
                         System.out.println("bad : " + ii+"," + jj);
                    }
               }
          }
                                   
          //initialize all the patchpoints.
          for (int ii=0; ii < patchpoints.length; ii++) {
               patchpoints[ii].calc();
          }


          //extract the color from the image.
          int[] colors = new int[(width-1) * (height-1)];
          PixelGrabber pg = new PixelGrabber(img, 0, 0, width-1, height-1,
                              colors, 0, width-1);
          try {
               pg.grabPixels();
          } catch (InterruptedException e) {
               System.out.println("grabPixels interrupted");
          }

          patches = new Patch[(width-1) * (height-1)];
          //set up the patches
          for (int ii=0; ii < width - 1; ii++) {
               for (int jj=0; jj < height - 1; jj++) {
                    patches[(jj * (width -1)) + ii] = new Patch(
                         patchpoints[width * jj + ii],
                         patchpoints[width * jj + ii +1],
                         patchpoints[width * (jj + 1) + ii + 1],
                         patchpoints[width * (jj + 1) + ii],
                         new Color(colors[jj * (width -1) + ii]));
               }
          }     
     }

     // c[x][y][r,g,b];
     public void fillColorBins(float[][][] c, int frame) {

          for (int ii=0; ii < patchpoints.length; ii++)  {
               patchpoints[ii].updatePosition(frame);
          }

          //now add color to all the right bins.
          for (int ii=0; ii < patches.length; ii++) {
               patches[ii].addColorToBins(c);
          }
     }
}
