import java.awt.*;

/**
 *
 * A grid adjustor
 */
public class Adjustor extends Canvas {
     private Image img; 
     private Grid grid;
     private Dimension mysize;
     private Adjustor other;
     private Image offImg;
     private Graphics offG;
     private Point mouseon = null;

     public Adjustor(Image img, int width, int height) {
          this.img = img;
          mysize = new Dimension(width, height);
          
          grid = new Grid(width, height);
     }

     public void setGridSize(int gridsize) {
          grid = new Grid(mysize.width, mysize.height, gridsize);
          repaint();
     }

     public Grid getGrid() {
          return grid;
     }

     public void setGrid(Grid g) {
          grid = g;
          repaint();
     }

     public void setOtherAdjustor(Adjustor other) {
          this.other = other;
     }

     public Dimension preferredSize() {
          return mysize;
     }

     public Dimension minimumSize() {
          return mysize;
     }


     public void update(Graphics g) {
          if (offG == null) {
               offImg = createImage(mysize.width, mysize.height);
               offG = offImg.getGraphics();
          }
          offG.setPaintMode();
          offG.clearRect(0, 0, mysize.width, mysize.height);
          offG.drawImage(img, 0, 0, null);
          offG.setColor(Color.white);
          offG.setXORMode(Color.black);
          for (int ii=0; ii < grid.w - 1; ii++) {
               for (int jj=0; jj < grid.h - 1; jj++) {
                    //there are 4 connections to every point.
                    //but if you just draw the ones below and to your right,
                    // you'll get them all
                    offG.drawLine(grid.points[ii][jj][0],
                                   grid.points[ii][jj][1],
                                   grid.points[ii+1][jj][0],
                                   grid.points[ii+1][jj][1]);
                    offG.drawLine(grid.points[ii][jj][0],
                                   grid.points[ii][jj][1],
                                   grid.points[ii][jj+1][0],
                                   grid.points[ii][jj+1][1]);
               }
          }
          offG.setPaintMode();

          //draw the red point.
          if (mouseon != null) {
               offG.setColor(Color.red);
               offG.drawRect(grid.points[mouseon.x][mouseon.y][0]-1,
                                   grid.points[mouseon.x][mouseon.y][1]-1,
                                   2,2);
          }
          g.drawImage(offImg, 0, 0, null);
     }

     public void paint(Graphics g) {
          update(g);
     }

     public void selectPoint(Point p) {
          mouseon = p;
          repaint();
     }
 
     public boolean mouseMove(Event evt, int x, int y) {
          Point p = grid.findClosestGridPoint(x, y);
          if (!p.equals(mouseon)) {
               mouseon = p;
               other.selectPoint(mouseon);
               repaint();
          }
          return true;
     }

     public boolean mouseDown(Event evt, int x, int y) {
          mouseMove(evt, x, y);
          return mouseDrag(evt, x, y);
     }
               
     public boolean mouseDrag(Event evt, int x, int y) {
          if (mouseon != null) {
               grid.moveGridPointTo(mouseon, x, y);
               repaint();
          }
          return true;
     }

     public boolean mouseUp(Event evt, int x, int y) {
          return mouseDrag(evt, x, y);
     }

     public boolean mouseEnter(Event evt, int x, int y) {
          setCursor(Frame.CROSSHAIR_CURSOR);
          return mouseMove(evt, x, y);
     }

     public void setCursor(int cursortype) {
          Component comp = this;
          do {
               comp = comp.getParent();
          } while (!(comp instanceof Frame));
          ((Frame) comp).setCursor(cursortype);
     }

     public boolean mouseExit(Event evt, int x, int y) {
          setCursor(Frame.DEFAULT_CURSOR);
          mouseon = null;
          other.selectPoint(mouseon);
          repaint();
          return true;
     }
}
