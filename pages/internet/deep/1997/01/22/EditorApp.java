import java.awt.*;
import java.io.*;


/**
 * Morph Editor Application.
 * Ray Greenwell.
 *
 * most of this is just here to throw up the editorPanel, and load and
 * save various crap from it.
 */
public class EditorApp extends Frame implements FilenameFilter {
     EditorPanel editPanel;
     MenuItem     loadButton, saveButton, exitButton;
     String img1, img2;

     public EditorApp(String img1, String img2) {

          MenuBar mb = new MenuBar();
          Menu filemenu = new Menu("File");
          
          filemenu.add(loadButton = new MenuItem("Load"));
          filemenu.add(saveButton = new MenuItem("Save"));
          filemenu.add(exitButton = new MenuItem("Exit"));

          mb.add(filemenu);
          setMenuBar(mb);
          Toolkit tk = Toolkit.getDefaultToolkit();
          this.img1 = img1;
          this.img2 = img2;
          removeAll();
          setLayout(new BorderLayout());
          add("Center", editPanel = new EditorPanel(tk.getImage(img1),
               tk.getImage(img2)));
     }

     //Ugh!
     public void newEditor(String img1, String img2) {
          Toolkit tk = Toolkit.getDefaultToolkit();
          this.img1 = img1;
          this.img2 = img2;
          removeAll();
          add("Center", editPanel = new EditorPanel(tk.getImage(img1),
               tk.getImage(img2)));
          pack();
          show();
     }

     //FilenameFilter interface method. doesn't seem to work.
     public boolean accept(File dir, String name) {
          int dot = name.lastIndexOf('.');
          return "htm".equalsIgnoreCase(name.substring(dot + 1, dot + 4));
     }

     public void load() throws IOException {
          FileDialog ss = new FileDialog(this, "Load morph", FileDialog.LOAD);
          ss.setFilenameFilter(this);
          ss.show();
          String file = ss.getFile();
          if (file == null) {
               return;
          }
          DataInputStream in;
          file = ss.getDirectory() + file;
          in = new DataInputStream(new BufferedInputStream(
                                        new FileInputStream(file)));
          String data = "";
          String each;
          while ((each = in.readLine()) != null) {
               data += each;
          }
          int pos;
          pos = data.indexOf("MorphPlayer");
          newEditor(grab("img1", data, pos), grab("img2", data, pos));
          editPanel.setPause(Integer.parseInt(grab("wait", data, pos)));
          editPanel.setNumFrames(Integer.parseInt(grab("numframes", data, pos)));
          editPanel.setGrids(grab("grid1", data, pos), grab("grid2", data, pos));
     }
          
     public String grab(String paramname, String from, int pos) {
          int start = from.indexOf("name=\"" + paramname + "\"", pos);
          if (start == -1) {
               return null;
          }
          start = from.indexOf("value=\"", start);
          if (start == -1) {
               return null;
          }
          start += 7; //length of value="
          int end = from.indexOf("\"", start);
          if (end == -1) {
               return null;
          }
          return from.substring(start, end);
     }

     public void save() throws IOException {
          FileDialog ss = new FileDialog(this, "Save morph", FileDialog.SAVE);
          ss.setFilenameFilter(this);
          ss.show();
          String file = ss.getFile();
          if (file == null) {
               return;
          }
          PrintStream out;
          file = ss.getDirectory() + file;
          out = new PrintStream(new BufferedOutputStream(
                                   new FileOutputStream(file)));
          Dimension size = editPanel.getSize();
          long wait = editPanel.getPause();
          int frames = editPanel.getNumFrames();
          out.println("<applet code=\"MorphPlayer\" width=\"" + size.width +
                    "\" height=\"" + size.height + "\">");
          out.println("<param name=\"wait\" value=\"" + wait + "\">");
          out.println("<param name=\"img1\" value=\"" + img1 + "\">");
          out.println("<param name=\"img2\" value=\"" + img2 + "\">");
          out.println("<param name=\"numframes\" value=\"" + frames + "\">"); 
          String[] grids = editPanel.getGrids();
          out.println("<param name=\"grid1\" value=\"" + grids[0] + "\">"); 
          out.println("<param name=\"grid2\" value=\"" + grids[1] + "\">"); 
          out.println("</applet>");
          out.close();
     }

     public boolean handleEvent(Event evt) {
          switch (evt.id) {
               case Event.ACTION_EVENT:
                    if (evt.target == exitButton) {
                         System.exit(0);
                    } else if (evt.target == saveButton) {
                         try {
                              save();
                         } catch (IOException e) {
                              System.out.println("Save error.");
                              e.printStackTrace(System.out);
                         }
                         return true;
                    } else if (evt.target == loadButton) {
                         try {
                              load();
                         } catch (IOException e) {
                              System.out.println("Load error.");
                              e.printStackTrace(System.out);
                         }
                         return true;
                    }
                    System.out.println(evt.target.toString());
                    break;

               case Event.WINDOW_DESTROY:
                    System.exit(0);
          }
          
          //System.out.println("xx " + evt.id + " " + evt.target.toString());
          return false;
     }

     public static void main(String[] args) {
          if (args.length > 1) {
               EditorApp ea = new EditorApp(args[0], args[1]);
               ea.pack();
               ea.show();
          } else {
               System.out.println("please specify the paths to the two images " +
                    "you wish to morph");
               System.exit(0);
          }
     }
}
