//
// Environment -
//
// mdb - 02/11/97

public class Environment
{
    // possible states for a cell in the grid
    public final static int EMPTY = 0;
    public final static int FOOD = 1;

    // size of the environment
    public final static int WIDTH = 64;
    public final static int HEIGHT = 64;

    public static int[][] grid = new int[WIDTH][HEIGHT];

    // how much energy an organism gains by eating a piece of food
    public static int FOOD_ENERGY = 64;

    // how much food and how close together it is in the grid
    public static int FOOD_DENSITY = 20;
    public static int FOOD_CLUSTERING = 50;

    public static void initialize ()
    {
        // stick some food in the grid cells
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (Generator.random()%100 < FOOD_DENSITY) {
                    grid[x][y] = FOOD;
                } else {
                    grid[x][y] = EMPTY;
                }
            }
        }
    }
};
