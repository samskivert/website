//
// ViewFrame -
//
// mdb - 02/11/97

import java.awt.BorderLayout;
import java.awt.Event;

public class ViewFrame extends java.awt.Frame
{
    public ViewFrame (View view, ControlPanel panel)
    {
        super("Competing Critters");
        setLayout(new BorderLayout(4, 4));
        add("Center", view);
        add("East", panel);
        pack();
        show();
    }

    public boolean handleEvent (Event evt)
    {
        if (evt.id == Event.WINDOW_DESTROY) {
            Simulation.closeGUI();
        }

        return super.handleEvent(evt);
    }
}
