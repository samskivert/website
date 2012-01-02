//
// ClientManager

package dserver;

import dist.CCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Enumeration;
import java.util.Hashtable;

public class ClientManager implements Runnable
{
    //
    // ClientManager public member functions

    public void init () throws IOException
    {
        _port = new ServerSocket(CCP.PORT);
        _listener = new Thread(this);
        _listener.start();
    }

    public synchronized void shutdown ()
    {
        // disconnect all of the active clients
        Enumeration iter = _clients.elements();
        while (iter.hasMoreElements()) {
            Client c = (Client)iter.nextElement();
            c.disconnect();
        }

        _listener = null;
        try { _port.close(); } catch (IOException e) {
            System.err.println("Error closing port: " + e);
        }
    }

    public void run ()
    {
        while (_listener == Thread.currentThread()) {
            try {
                Socket s = _port.accept();
                DataInputStream din =
                    new DataInputStream(s.getInputStream());
                DataOutputStream dout =
                    new DataOutputStream(s.getOutputStream());

                byte cmd = din.readByte();
                String cid = din.readUTF();

                switch (cmd) {
                case CCP.CREATE:
                    _clients.put(cid, new Client(cid, s));
                    dout.writeByte(CCP.OK);
                    dout.flush();
                    break;

                case CCP.REGISTER: {
                    Client c = (Client)_clients.get(cid);
                    if (c != null) {
                        c.setEventConnection(s);
                        dout.writeByte(CCP.OK);
                        dout.flush();

                    } else {
                        System.err.println("Attempt to register with " +
                                           "non-existant client: " + cid);
                        dout.writeByte(CCP.FAILURE);
                        dout.flush();
                    }
                    break;
                }

                default:
                    System.err.println("Unknown command: " + cmd);
                    break;
                }

            } catch (IOException e) {
                System.out.println("Error accepting client: " + e);
            }
        }
    }

    public synchronized void destroyClient (String cid)
    {
        Client c = (Client)_clients.remove(cid);
        if (c == null) return;
        c.disconnect();
    }

    public ServerSocket getSocket () { return _port; }

    //
    // ClientManager protected data members

    Thread _listener;
    ServerSocket _port;

    Hashtable _clients = new Hashtable();
}
