//
// Verifier - looks up words in a dictionary and connects to www.m-w.com
//            to look up words not found in the dictionary (uses an
//            external perl program to do most of this)
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

import dist.DObject;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

public class Verifier implements Runnable
{
    //
    // Verified public constructor

    public Verifier (DObject game)
    {
        _game = game;
    }

    //
    // Verifier public member functions

    public synchronized void addWords (String[] words)
    {
        for (int i = 0; i < words.length; i++) {
            // if we've already seen this word twice, skip it
            if (_repeats.contains(words[i])) {
                // System.out.println("skipping repeater: " + words[i]);
                continue;
            }

            if (_words.contains(words[i])) {
                // System.out.println("removing repeater: " + words[i]);
                // if it's already in there, add it to the repeats list
                _repeats.addElement(words[i]);
                // and remove it from the main list
                _words.removeElement(words[i]);
            } else {
                _words.addElement(words[i]);
            }
        }
    }

    public void start ()
    {
        Thread t = new Thread(this);
        t.start();
    }

    public void run ()
    {
        StringBuffer cmd = new StringBuffer();
        cmd.append("validate.pl");
        for (int i = 0; i < _words.size(); i++) {
            cmd.append(" ");
            cmd.append((String)_words.elementAt(i));
        }

        System.out.println("Running: " + cmd);

        Process p = null;
        DataInputStream din = null;

        try {
            p = Runtime.getRuntime().exec(cmd.toString());
            din = new DataInputStream(p.getInputStream());

        } catch (IOException e) {
            System.err.println("Error execing [" + cmd + "]: " + e);
            return;
        }

        for (String word;;) {
            // if we encounter an error reading from the stream, bail
            try { word = din.readLine(); }
            catch (IOException e) { break; }

            // if the word is null, bail
            if (word == null) break;

            // System.out.println("read invalid word: " + word);

            try { if (_game != null) _game.setValue("invalid", word); }
            catch (IOException e) {
                System.err.println("Error sending invalid word: " + e);
                break;
            }
        }

        // System.out.println("done verifying");

        try { if (_game != null) _game.setValue("verified", 1); }
        catch (IOException e) {
            System.err.println("Error setting verified flag: " + e);
        }

        _words.removeAllElements();
        _repeats.removeAllElements();
    }

    //
    // Verifier public static member functions

    public static void main (String[] args)
    {
        Verifier v = new Verifier(null);
        v.addWords(args);
        v.start();
    }

    //
    // Verifier protected data members

    DObject _game;
    Vector _words = new Vector();
    Vector _repeats = new Vector();
}
