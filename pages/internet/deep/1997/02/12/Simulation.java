//
// Simulation -
//
// mdb - 02/11/97

public class Simulation extends Thread
{
    public static View view;
    public static ControlPanel panel;
    public static ViewFrame frame;

    public void run ()
    {
        // initialize the population
        Population.initialize();

        for (int gen = 0; (gen < Population.TOTAL_GENERATIONS) && running();
             gen++) {
            runCompetition(gen);
            if (running()) Population.performSelection(gen);
        }

        // dump some of the statistics
        // for (int i = 0; i < Population.TOTAL_GENERATIONS; i++) {
        // System.out.print(i + "/" + Population.maxfit[i] + "/" +
        // Population.avgfit[i] + " ");
        // if (i % 5 == 4) System.out.print("\n");
        // }

        stopSimulation();
    }

    public boolean running ()
    {
        return Thread.currentThread() == _runner;
    }

    public void runCompetition (int generation)
    {
        Organism[] orgs = Population.organisms[generation%2];
        int living = orgs.length;
        panel.genlab.setText(Integer.toString(generation));

        // determine whether or not to run the view on this generation
        boolean watch = ((generation == 0) ||
                         (generation == Population.TOTAL_GENERATIONS-1));

        // initialize the environment
        Environment.initialize();
        if (watch) view.renderFood();

        // place all of the organisms somewhere on the grid
        for (int i = 0; i < orgs.length; i++) {
            orgs[i].init(Generator.random() % Environment.WIDTH,
                         Generator.random() % Environment.HEIGHT,
                         Environment.FOOD_ENERGY);
        }

        // now run them until they're all dead
        panel.orglab.setText(Integer.toString(living));
        while ((living > 0) && running()) {
            for (int i = 0; i < orgs.length; i++) {
                if (orgs[i].energyUnits > 0) {
                    orgs[i].iterate(watch);
                    if (orgs[i].energyUnits <= 0) {
                        panel.orglab.setText(Integer.toString(--living));
                        if (!watch) sleep(5);
                    }
                }
            }
            if (watch) sleep(5);
        }
    }

    public static void startSimulation ()
    {
        panel.setRunning(true);
        _runner = new Simulation();
        _runner.start();
    }

    public static void stopSimulation ()
    {
        panel.setRunning(false);
        _runner = null;
    }

    public static void main (String[] args)
    {
        openGUI();
    }

    public static void openGUI ()
    {
        if (frame == null) {
            view = new View();
            panel = new ControlPanel();
            frame = new ViewFrame(view, panel);
        } else {
            frame.show();
        }
    }

    public static void closeGUI ()
    {
        stopSimulation();
        frame.hide();
    }

    public static void sleep (long milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
        }
    }

    static Thread _runner;
}
