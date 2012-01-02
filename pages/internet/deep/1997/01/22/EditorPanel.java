import java.awt.*;

public class EditorPanel extends Panel {

     private Adjustor          fromAdjustor, toAdjustor;
     private Morph               mymorph;
     private Label               numframeslabel;
     private Scrollbar          numframesbar, pausebar, gridsizebar;
     private Button               gobutton;
     private boolean               running;

     public EditorPanel(Image img1, Image img2) {

          MediaTracker mt = new MediaTracker(this);
          mt.addImage(img1, 0);
          mt.addImage(img2, 0);
          try {
               mt.waitForAll();
          } catch (InterruptedException e) {
               //bah!
          }
          int w = Math.max(img1.getWidth(null), img2.getWidth(null));
          int h = Math.max(img1.getHeight(null), img2.getHeight(null));

          setLayout(new BorderLayout());

          //put two MorphAdjuster on the top.
          fromAdjustor = new Adjustor(img1, w, h);
          toAdjustor = new Adjustor(img2, w, h);

          Panel piclabelpanel = new Panel();
          piclabelpanel.setLayout(new GridLayout(1, 2));
          piclabelpanel.add(new Label("Before"));
          piclabelpanel.add(new Label("After"));
          Panel picpanel = new Panel();
          picpanel.setLayout(new GridLayout(1, 2, 4, 4));
          picpanel.add(fromAdjustor);
          picpanel.add(toAdjustor);

          Panel gridsizepanel = new Panel();
          gridsizepanel.setLayout(new BorderLayout());
          gridsizepanel.add("West", new Label("Grid size:"));
          gridsizebar = new Scrollbar(Scrollbar.HORIZONTAL, 10, 1, 5, 50);
          gridsizepanel.add("Center", gridsizebar);

          Panel adjustmentpanel = new Panel();
          adjustmentpanel.setLayout(new BorderLayout());
          adjustmentpanel.add("North", piclabelpanel);
          adjustmentpanel.add("Center", picpanel);
          adjustmentpanel.add("South", gridsizepanel);

          //introduce the adjustors to each other.
          fromAdjustor.setOtherAdjustor(toAdjustor);
          toAdjustor.setOtherAdjustor(fromAdjustor);

          //put in the UI widget panel
          Panel controlpanel = new Panel();
          controlpanel.setLayout(new BorderLayout());
          Panel foo = new Panel();
          foo.setLayout(new BorderLayout());
          numframeslabel = new Label("Frames: 5", Label.LEFT);
          numframesbar = new Scrollbar(Scrollbar.HORIZONTAL, 5, 1, 2, 25);
          foo.add("Center", numframeslabel);
          foo.add("South", numframesbar);
          gobutton = new Button("Go");
          controlpanel.add("Center", foo);
          controlpanel.add("South", gobutton);

          Panel lowerpanel = new Panel();
          lowerpanel.setLayout(new GridLayout(1, 2));
          lowerpanel.add(controlpanel);

          Panel morphpanel = new Panel();
          morphpanel.setLayout(new BorderLayout());
          mymorph = new Morph(img1, img2, 5);
          //and in the lower right hand corner, put the morph.
          morphpanel.add("Center", mymorph);
          pausebar = new Scrollbar(Scrollbar.HORIZONTAL, 42, 1, 2, 50);
          morphpanel.add("South", pausebar);
          lowerpanel.add(morphpanel);

          add("Center", adjustmentpanel);
          add("South", lowerpanel);
     }

     //methods called by the application.
     public Dimension getSize() {
          return mymorph.preferredSize();
     }

     public long getPause() {
          return mymorph.getPause();
     }

     public void setPause(long wait) {
          pausebar.setValue(52 - (int) (wait / 10)); 
          mymorph.setPause(wait);
     }

     public String[] getGrids() {
          String[] ret = new String[2];
          ret[0] = fromAdjustor.getGrid().toString();
          ret[1] = toAdjustor.getGrid().toString();
          return ret;
     }

     public void setGrids(String grid1, String grid2) {
          fromAdjustor.setGrid(new Grid(grid1));
          toAdjustor.setGrid(new Grid(grid2));
     }

     public int getNumFrames() {
          return numframesbar.getValue();
     }

     public void setNumFrames(int num) {
          mymorph.setNumFrames(num);
          numframesbar.setValue(num);
     }

     public void setCursor(int cursortype) {
          Component comp = this;
          do {
               comp = comp.getParent();
          } while (!(comp instanceof Frame));
          ((Frame) comp).setCursor(cursortype);
     }

     public boolean handleEvent(Event evt) {
          switch (evt.id) {
               case Event.MOUSE_ENTER:
                    setCursor((running) ? Frame.WAIT_CURSOR : Frame.DEFAULT_CURSOR);
                    return true;

               case Event.MOUSE_EXIT:
                    setCursor(Frame.DEFAULT_CURSOR);
                    return true;

               case Event.SCROLL_LINE_UP:
               case Event.SCROLL_LINE_DOWN:
               case Event.SCROLL_PAGE_UP:
               case Event.SCROLL_PAGE_DOWN:
               case Event.SCROLL_ABSOLUTE:
                    if (evt.target == numframesbar) {
                         int frames = numframesbar.getValue();
                         numframeslabel.setText("Frames: " + frames);
                         mymorph.setNumFrames(frames);
                         return true;
                    } else if (evt.target == pausebar)  {
                         mymorph.setPause((52 - pausebar.getValue()) * 10 );
                         return true;
                    } else if (evt.target == gridsizebar) {
                         int gridsize = gridsizebar.getValue();
                         fromAdjustor.setGridSize(gridsize);
                         toAdjustor.setGridSize(gridsize);
                         return true;
                    }
               case Event.ACTION_EVENT:
                    if (evt.target == gobutton) {
                         running = !running;
                         gobutton.setLabel((running) ? "Stop" : "Go");
                         gobutton.setBackground((running) ? Color.red : Color.green);
                         if (running) {
                              numframesbar.disable();
                              gridsizebar.disable();
                              toAdjustor.disable();
                              fromAdjustor.disable();
                              mymorph.newGrids(fromAdjustor.getGrid(),
                                                  toAdjustor.getGrid());
                              Thread.yield();
                              mymorph.start();
                         } else {
                              mymorph.stop();
                              numframesbar.enable();
                              gridsizebar.enable();
                              toAdjustor.enable();
                              fromAdjustor.enable();
                         }
                    }
                    return true;
          }

          return false;
     }
}
