//
// Generator - a class to generate random positive integers
//
// mdb - 02/11/97

import java.util.Random;

public class Generator
{
    public static int random ()
    {
        return Math.abs(_generator.nextInt());
    }

    static Random _generator = new Random();
};
