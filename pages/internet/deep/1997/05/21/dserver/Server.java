//
// Server

package dserver;

import java.io.IOException;

public class Server
{
    //
    // Server public static data members

    public static void main (String[] args)
    {
        try {
            cmgr.init();
        } catch (IOException e) {
            System.err.println("Error initializing ClientManager: " + e);
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

    public static DObjectManager omgr = new DObjectManager();

    public static ClientManager cmgr = new ClientManager();
}
