//
// wordgame.Pieces - describes the letters on the wordgame pieces
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

import java.util.Random;
import java.util.Vector;

public class Pieces
{
    //
    // wordgame.Pieces public data members

    public final static char[][] pieces =
    {{ 'x', 'j', 'k', 'b', 'z', 'q' },
     { 'o', 'd', 'd', 'r', 'n', 'l' },
     { 'e', 'e', 'e', 'e', 'm', 'a' },
     { 'r', 'i', 's', 'f', 'a', 'a' },
     { 'r', 'r', 'h', 'y', 'p', 'i' },

     { 't', 'o', 'u', 't', 'o', 'o' },
     { 'l', 'e', 'i', 't', 'i', 'c' },
     { 's', 'n', 'u', 's', 'e', 's' },
     { 'e', 'a', 'e', 'e', 'a', 'e' },
     { 'h', 'd', 'h', 'l', 'o', 'r' },

     { 'r', 'g', 'w', 'v', 'o', 'r' },
     { 'a', 'u', 'e', 'm', 'e', 'g' },
     { 'p', 's', 'f', 'y', 'i', 'r' },
     { 't', 'i', 'i', 't', 'i', 'e' },
     { 's', 'a', 'a', 'r', 'a', 'f' },

     { 'l', 'i', 'p', 'c', 'e', 't' },
     { 'h', 'o', 'd', 't', 'h', 'n' },
     { 'd', 'n', 'n', 'n', 'a', 'e' },
     { 'h', 'o', 'l', 'd', 'n', 'r' },
     { 'e', 'a', 'n', 'n', 'm', 'g' },

     { 'p', 'i', 't', 's', 'e', 'c' },
     { 't', 'o', 'e', 't', 'm', 't' },
     { 't', 'n', 'o', 'w', 'o', 'u' },
     { 'w', 'n', 't', 'c', 'c', 's' },
     { 'f', 'i', 's', 'y', 'a', 'r' },
    };

    //
    // wordgame.Pieces public static member functions

    public static char[] generateBoard (long seed)
    {
        Random r = new Random(seed);
        char[] board = new char[pieces.length];

        // first pick a random face for each piece
        for (int i = 0; i < board.length; i++) {
            board[i] = pieces[i][Math.abs(r.nextInt())%6];
        }

        // now shuffle the pieces around
        for (int i = 1; i < board.length; i++) {
            // swap this piece with a random piece below it (or itself)
            int target = Math.abs(r.nextInt()) % (i+1);
            char tmp = board[i];
            board[i] = board[target];
            board[target] = tmp;
        }

        return board;
    }

    public static Node[] genNodeGraph (char[] board)
    {
        Node[] nodes = new Node[board.length];

        // create a node for each piece on tbe wordgame board
        for (int i = 0; i < board.length; i++) {
            nodes[i] = new Node();
            nodes[i].value = board[i];
            nodes[i].position = i;
        }

        // generate a connected graph of nodes representing the pieces on
        // the wordgame board
        for (int i = 0, y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++, i++) {
                if (y > 0) {
                    if (x > 0) nodes[i].neighbors[0] = nodes[i-5-1];
                    nodes[i].neighbors[1] = nodes[i-5];
                    if (x < 4) nodes[i].neighbors[2] = nodes[i-5+1];
                }
                if (x > 0) nodes[i].neighbors[3] = nodes[i-1];
                if (x < 4) nodes[i].neighbors[4] = nodes[i+1];
                if (y < 4) {
                    if (x > 0) nodes[i].neighbors[5] = nodes[i+5-1];
                    nodes[i].neighbors[6] = nodes[i+5];
                    if (x < 4) nodes[i].neighbors[7] = nodes[i+5+1];
                }
            }
        }

        return nodes;
    }

    public static boolean graphContainsWord (Node[] graph, String word)
    {
        // translate qu into just q
        word = fixWord(word);

        Vector path = new Vector();
        char target = word.charAt(0);

        for (int i = 0; i < graph.length; i++) {
            // if this node contains the first letter of the word, try it
            if (graph[i].value == target) {
                // System.out.println("Attempting match at: " + graph[i]);
                path.addElement(graph[i]);
                if (matchWord(graph[i], word, path)) return true;
                path.removeElementAt(0);
            }
        }

        return false;
    }

    //
    // wordgame.Pieces protected static member functions

    static boolean matchWord (Node node, String word, Vector path)
    {
        char target = word.charAt(path.size());

        int tried = 0;

        for (int i = 0; i < 8; i++) {
            Node potential = node.neighbors[i];
            // if it's null that means we're on the edge of the board
            if (potential == null) continue;
            // we can only traverse this node if the character matches
            if (potential.value != target) continue;
            // we can't loop back on ourselves, so skip it if it's in path
            if (path.contains(potential)) continue;

            tried++;

            // if this is the last letter of the word, we've matched
            if (path.size() == word.length()-1) {
                // path.addElement(potential);
                // System.out.println("Matched: " + path);
                return true;
            }

            // otherwise traverse this node and go from there
            path.addElement(potential);
            // if this traversal yielded a match, bail out
            if (matchWord(potential, word, path)) return true;
            // remove this traversal and try the next
            path.removeElementAt(path.size()-1);
        }

        // if (tried == 0) System.out.println("Failed: " + path);

        return false;
    }

    static String fixWord (String word)
    {
        int idx = word.indexOf("qu");
        if (idx == -1) return word;
        return word.substring(0, idx+1) + fixWord(word.substring(idx+2));
    }
}
