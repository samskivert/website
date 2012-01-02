//
// Language -
//
// mdb - 02/11/97

public class Language
{
    // the instruction set
    public static final int MVF = 0; // move forward
    public static final int TRL = 1; // turn left
    public static final int TRR = 2; // turn right

    public static final int NUM_INSTRUCTIONS = 3;

    public static int[] randomProgram (int length)
    {
        int[] program = new int[length];
        for (int i = 0; i < length; i++) {
            program[i] = Generator.random() % NUM_INSTRUCTIONS;
        }
        return program;
    }
};
