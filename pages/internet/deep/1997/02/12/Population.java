//
// Population -
//
// mdb 02/11/97

public class Population
{
    public static int SIZE = 16;
    public static int TOTAL_GENERATIONS = 50;
    public static int MUTATION_RATE = 5;

    public static int[] maxfit;
    public static int[] avgfit;

    public static Organism[][] organisms;

    public static void initialize ()
    {
        organisms = new Organism[2][SIZE];
        // create the first generation of random individuals
        for (int i = 0; i < SIZE; i++) organisms[0][i] = new Organism();
        // create arrays to hold statistics
        maxfit = new int[TOTAL_GENERATIONS];
        avgfit = new int[TOTAL_GENERATIONS];
    }

    public static void performSelection (int generation)
    {
        int tg = generation%2, ng = (generation+1)%2, sf = 0;

        // first compute the summed fitness of all organisms
        for (int i = 0; i < SIZE; i++) sf += organisms[tg][i].fitness();

        // sort the population based on fitness
        QuickSort.sort(organisms[tg]);

        // track some statistics
        maxfit[generation] = organisms[tg][0].fitness();
        avgfit[generation] = sf / SIZE;

        // copy the upper half of individuals into the new population
        for (int i = 0; i < SIZE/2; i++) {
            organisms[ng][i] = organisms[tg][i];
        }

        // now generate the other half of the new population from the old
        for (int i = SIZE/2; i < SIZE; i++) {
            organisms[ng][i] = new
                Organism(proportionalSelect(organisms[tg], sf),
                         proportionalSelect(organisms[tg], sf));
            organisms[ng][i].mutate(MUTATION_RATE);
        }
    }

    static Organism proportionalSelect (Organism[] choices, int totalFitness)
    {
        int fc = Generator.random() % totalFitness, i = 0;
        do fc -= choices[i++].fitness(); while (fc > 0);
        return choices[--i];
    }
};
