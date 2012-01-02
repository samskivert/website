//
// Organism -
//
// mdb - 02/11/97

public class Organism implements Sortable
{
    public static int MAX_IP_LENGTH = 25;
    public static int MIN_IP_LENGTH = 5;

    // coordinates in the environment
    public int x, y;

    // 0 - north, 1 - east, 2 - south, 3 - west
    public int orientation = Generator.random() % 4;

    // length of time this organism has been alive
    public int ticks = 0;

    // the amount of energy this organism has
    public int energyUnits;

    // create a new random organism
    public Organism ()
    {
        _program =
            Language.randomProgram(Generator.random() % MAX_IP_LENGTH +
                                   MIN_IP_LENGTH);
    }

    // create an organism from two parents via crossover
    public Organism (Organism father, Organism mother)
    {
        int flen = Generator.random() % father._program.length;
        int mpos = Generator.random() % mother._program.length;
        int mlen = mother._program.length - mpos;

        _program = new int[flen + mlen];
        System.arraycopy(father._program, 0, _program, 0, flen);
        System.arraycopy(mother._program, mpos, _program, flen, mlen);
    }

    public void init (int x, int y, int energy)
    {
        this.x = x;
        this.y = y;
        energyUnits = energy;
        ticks = 0;
    }

    public int fitness ()
    {
        return ticks;
    }

    // mutate this individual where rate is some value between zero and
    // 100 indicating the percent of the program that should be mutated
    public void mutate (int rate)
    {
        for (int i = 0; i < _program.length * rate / 100; i++) {
            int target = Generator.random() % _program.length;
            _program[target] =
                (_program[target] + Generator.random()) %
                Language.NUM_INSTRUCTIONS;
        }
    }

    public void iterate (boolean render)
    {
        // interpret the current instruction
        switch (_program[_pc]) {
        case Language.MVF: // move forward
            // update our position
            if (render) Simulation.view.eraseOrganism(this);
            x = (x + _dx[orientation]) % Environment.WIDTH;
            y = (y + _dy[orientation]) % Environment.HEIGHT;

            // see if we found any food at this position
            if (Environment.grid[x][y] == Environment.FOOD) {
                Environment.grid[x][y] = Environment.EMPTY;
                energyUnits += Environment.FOOD_ENERGY;
            }
            if (render) Simulation.view.drawOrganism(this);

            break;

        case Language.TRL: // turn left
            orientation = (orientation + 3) % 4;
            break;

        case Language.TRR: // turn right
            orientation = (orientation + 1) % 4;
            break;
        }

        // increment the program counter and the tick counter
        _pc = (_pc + 1) % _program.length;
        ticks++;

        // use up a unit of energy
        energyUnits--;
    }

    public int compareTo (Sortable other)
    {
        return ((Organism)other).ticks - ticks;
    }        

    public void dumpState ()
    {
        System.out.print(_program[_pc] + "/" + x + "/" + y + "/" +
                         energyUnits + " ");
    }

    int[] _program; // this organism's program
    int   _pc;      // program counter

    // used to quickly move in each of the four directions
    final static int[] _dx = {  0, 1, 0, Environment.WIDTH-1 };
    final static int[] _dy = { Environment.HEIGHT-1, 0, 1,  0 };
};
