/**
 * Morph
 *
 * A canvas which does the morph!
 */

import java.awt.*;
import java.awt.image.*;

public class Morph extends Canvas implements Runnable {
     private Image          img1, img2;
     private boolean          changed;
     private Thread          runner;
     private boolean          running = false;
     private Dimension     mysize;
     private int               numframes;

     //in between frames
     private Image[]          tween;
     private int               curframe = 0;
     private long          waittime = 100;

     private Quilt          from, to;

     /**
      * make a new Morph
      */
     public Morph(Image imgA, Image imgB, int numframes) {
          int width = Math.max(imgA.getWidth(null), imgB.getWidth(null));
          int height = Math.max(imgA.getHeight(null), imgB.getHeight(null));
          mysize = new Dimension(width, height);
          img1 = imgA;
          img2 = imgB;
          setNumFrames(numframes);
     }

     public Dimension minimumSize() {
          return mysize;
     }

     public Dimension preferredSize() {
          return mysize;
     }

     public void start() {
          //start the mofo going.
          running = true;
          if (runner == null) {
               runner = new Thread(this);
               runner.start();
          }
     } 

     public void setPause(long num) {
          waittime = num;
     }

     public long getPause() {
          return waittime;
     }

     public synchronized void setNumFrames(int num) {
          numframes = num;
          tween = new Image[num];
     }

     public synchronized void newGrids(Grid g1, Grid g2) {
          tween = new Image[numframes]; //reset the frames.

          //reset the quilts.
          from = new Quilt(img1, g1, g2, numframes);
          to = new Quilt(img2, g2, g1, numframes);
     }

     //in an ideal world, you just stop the thread. But we don't want
     // to tickle anything in Netscape's java implementation.
     public void stop() {
          running = false;
     }

     public void run() {
          int frame = 0;
          int dir = 1;

          while (running) {
               synchronized (this) {
                    if (frame >= tween.length) {
                         frame = 0;
                    }
                    if (tween[frame] == null) {
                         tween[frame] = generateFrame(frame);
                    }
                    boolean flag = false;
                    for (int ii=0; ii < tween.length; ii++) {
                         if (tween[ii] == null) {
                              flag = true;
                         }
                    }
                    if (!flag) {
                         //trash the quilts.
                         //remember kids, this is java- assigning null to them
                         //frees the mem.
                         to = from = null;
                    }
                    curframe = frame;
               }
               repaint();
               try {
                    Thread.sleep(waittime);
               } catch (InterruptedException e) {
                    //foo
               }
               frame = curframe + dir;
               if ((frame < 0) || (frame == tween.length)) {
                    dir *= -1;
                    frame = curframe + dir;
               }
          }
          runner = null;
     }

     //Not currently used. 
     public synchronized Image saveMorph() {
          Image morph = createImage(mysize.width * tween.length,
                                             mysize.height);
          Graphics g = morph.getGraphics();
          for (int ii=0; ii < tween.length; ii++) {
               if (tween[ii] == null) {
                    tween[ii] = generateFrame(ii);
               }
               g.drawImage(tween[ii], ii * mysize.width, 0, null);
          }
          return morph;
     }
               

     public synchronized void update(Graphics g) {
          if (tween[curframe] == null) {
               tween[curframe] = generateFrame(curframe);
          }
          g.drawImage(tween[curframe], 0, 0, null);
     }

     public void paint(Graphics g) {
          update(g);
     }

     public Image generateFrame(int framenumber) {
          Image ret = null;
          if ((framenumber == 0) || (framenumber == tween.length -1)) {
               Graphics gg;
               ret = createImage(mysize.width, mysize.height);
               gg = ret.getGraphics();
               gg.setColor(Color.black);
               gg.fillRect(0, 0, mysize.width, mysize.height);
               gg.drawImage((framenumber == 0) ? img1 : img2, 0, 0, null);
               return ret;
          }
          //otherwise- we are rendering a frame!!
          float[][][] colorbins = new float[mysize.height][mysize.width][3];
          float[][][] colorbins2 = new float[mysize.height][mysize.width][3];
          from.fillColorBins(colorbins, framenumber);
          to.fillColorBins(colorbins2, (numframes - 1) - framenumber); 

          float perc = (float) framenumber / (float) (numframes - 1);

          //now put the color bins into an actual image!!!
          int[] pixels = new int[mysize.width * mysize.height];
          MemoryImageSource mis = new MemoryImageSource(mysize.width,
                                   mysize.height, pixels, 0 , mysize.width); 
          MediaTracker mt = new MediaTracker(this);
          for (int ii=0; ii < mysize.width; ii++) {
               for (int jj=0; jj < mysize.height; jj++) {
                    pixels[jj * mysize.width + ii] = 
                         (255 << 24) |
                         (((int) (((perc * colorbins2[jj][ii][0]) +
                              (1-perc) * colorbins[jj][ii][0]) * 255)) << 16) |
                         (((int) (((perc * colorbins2[jj][ii][1]) +
                              (1-perc) * colorbins[jj][ii][1]) * 255)) << 8) |
                         (((int) (((perc * colorbins2[jj][ii][2]) +
                              (1-perc) * colorbins[jj][ii][2]) * 255)) << 0);
               }
          }
          ret = createImage(mis);
          mt.addImage(ret, 0);
          try {
               mt.waitForAll();
          } catch (Exception e) {
               System.out.println("Ouch! : " + e);
               e.printStackTrace(System.out);
          }
          return ret;
     }
}
