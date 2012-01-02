//
// ControlPanel -
//
// mdb - 02/11/97

import java.awt.Button;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import java.util.Hashtable;

public class ControlPanel extends Panel
{
    public ControlPanel ()
    {
        setLayout(new GridLayout(8, 2, 4, 0));

        add(new Label("Generation:", Label.RIGHT));
        add(genlab = new Label("0"));

        add(new Label("Organisms:", Label.RIGHT));
        add(orglab = new Label("0"));

        addRow("Population Size", 20);
        addRow("Generations", 40);
        addRow("Mutation Rate", 5);
        addRow("Food Density", 15);
        addRow("Food Energy", 64);
        // addRow("Food Clustering", 0);

        add(rb = new Button("Run"));
        add(sb = new Button("Stop"));
        sb.enable(false);
    }

    public void extractValues ()
    {
        Population.SIZE = intValue(_controls.get("Population Size"),
                                   1, Integer.MAX_VALUE);
        Population.TOTAL_GENERATIONS = intValue(_controls.get("Generations"),
                                                1, Integer.MAX_VALUE);
        Population.MUTATION_RATE = intValue(_controls.get("Mutation Rate"),
                                            0, 100);
        Environment.FOOD_DENSITY = intValue(_controls.get("Food Density"),
                                            0, 100);
        Environment.FOOD_CLUSTERING = intValue(_controls.get("Food Density"),
                                               0, 100);
        Environment.FOOD_ENERGY = intValue(_controls.get("Food Energy"),
                                           0, Integer.MAX_VALUE);
    }

    int intValue (Object obj, int min, int max)
    {
        TextField tf = (TextField)obj;
        int value;

        try {
            value = Integer.parseInt(tf.getText());
        } catch (NumberFormatException e) {
            value = min;
        }

        if (value < min) value = min;
        if (value > max) value = max;
        tf.setText(Integer.toString(value));

        return value;
    }

    public boolean handleEvent (Event evt)
    {
        if ((evt.id == Event.ACTION_EVENT) &&
            (evt.target instanceof Button)) {
            Button b = (Button)evt.target;
            if (b.getLabel().equals("Run")) {
                extractValues();
                Simulation.startSimulation();
            } else if (b.getLabel().equals("Stop")) {
                Simulation.stopSimulation();
            }
        }

        return super.handleEvent(evt);
    }

    public void setRunning (boolean running)
    {
        rb.enable(!running);
        sb.enable(running);
        Simulation.sleep(150);
    }

    void addRow (String text, int defval)
    {
        add(new Label(text, Label.RIGHT));
        TextField tf = new TextField(Integer.toString(defval), 2);
        add(tf);
        _controls.put(text, tf);
    }

    Label genlab, orglab;
    Button rb, sb;

    Hashtable _controls = new Hashtable();
}
