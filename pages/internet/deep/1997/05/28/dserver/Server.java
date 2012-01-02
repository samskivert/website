//
// Server - main distributed object server, handles everything
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

package dserver;

import java.io.IOException;

public class Server
{
    //
    // Server public static member functions

    public static void main (String[] args)
    {
        try {
            gmgr.init();
            cmgr.init();

        } catch (IOException e) {
            System.err.println("Error initializing managers: " + e);
            return;
        }

        System.out.println("Distributed object listening on port: " +
                           cmgr.getSocket().getLocalPort());

        try {
            synchronized (cmgr) {
                cmgr.wait();
            }
        } catch (InterruptedException e) {
        }
    }

    //
    // Server public static data members

    public static DObjectManager omgr = new DObjectManager();
    public static GameManager gmgr = new GameManager();
    public static ClientManager cmgr = new ClientManager();
}
