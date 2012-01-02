//
// SimApplet -
//
// mdb - 02/12/97

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Event;

public class SimApplet extends Applet
{
    public void init ()
    {
        setLayout(new BorderLayout());
        add("Center", new Button("Let's go"));
    }

  public void stop ()
  {
    Simulation.closeGUI();
  }

    public boolean handleEvent (Event evt)
    {
        // open the simulation window when they click on the button
        if (evt.id == Event.ACTION_EVENT) Simulation.openGUI();
        return super.handleEvent(evt);
    }
}
