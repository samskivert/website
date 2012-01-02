import java.applet.Applet;
import java.net.URL;

/**
 * Quick-n-Cute EditorApplet. All the glitz-- none of the File IO.
 *
 *
 */
public class EditorApplet extends Applet {

     public void init() {
          try {
               int bgcolor = 0xFFFFFF;
               try {
                    bgcolor = Integer.parseInt(getParameter("bgcolor"), 16);
               } catch (Exception e) {}
               setBackground(new java.awt.Color(bgcolor));
               
               setLayout(new java.awt.BorderLayout());
               add("Center", new EditorPanel(
                    getImage(new URL(getDocumentBase(), getParameter("img1"))),
                    getImage(new URL(getDocumentBase(), getParameter("img2")))));
          } catch (Exception e) {
               e.printStackTrace(System.out);
          }
     }
}
