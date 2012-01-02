//
// Node - an object used in a searchable board representation
//
// This code is Copyright (C) 1997, go2net Inc. Permission is granted for
// any use so long as this header remains intact.
//
// Originally published in Deep Magic:
//     <URL:http://www.go2net.com/internet/deep/>
//
// The code herein is provided to you as is, without any warranty of any
// kind, including express or implied warranties, the warranties of
// merchantability and fitness for a particular purpose, and
// non-infringement of proprietary rights.  The risk of using this code
// remains with you.

package wordgame;

public class Node
{
    //
    // Node public data members

    char value;

    Node[] neighbors = new Node[8];

    int position; // for debugging

    //
    // Node public data members

    public String toString ()
    {
        return String.valueOf(value) + " (" + position + ")";
    }
}
