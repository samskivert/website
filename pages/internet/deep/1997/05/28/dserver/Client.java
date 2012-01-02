//
// Client - an object to represent a client connection inside the server
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

import dist.*;

import java.io.*;
import java.net.Socket;

public class Client implements Subscriber, Runnable
{
    //
    // Client public constructor

    public Client (String cid, Socket ctlconn) throws IOException
    {
        // keep track of our client id
        _cid = cid;

        // fix up our connections
        _ctlconn = ctlconn;
        InputStream is = _ctlconn.getInputStream();
        _ctlin = new DataInputStream(new BufferedInputStream(is));
        OutputStream os = _ctlconn.getOutputStream();
        _ctlout = new DataOutputStream(new BufferedOutputStream(os));

        _handler = new Thread(this);
        _handler.start();

        System.out.println("Created new client: " + _cid);
    }

    //
    // Client public member functions

    public void setEventConnection (Socket evtconn) throws IOException
    {
        _evtconn = evtconn;
        OutputStream os = _evtconn.getOutputStream();
        _evtout = new DataOutputStream(new BufferedOutputStream(os));
    }

    public void disconnect ()
    {
        System.out.println("Disconnecting from client: " + _cid);

        // close the control connection
        if (_ctlconn != null) {
            try { _ctlconn.close(); } catch (IOException e) {}
            _ctlconn = null;
        }

        // close the event connection
        if (_evtconn != null) {
            try { _evtconn.close(); } catch (IOException e) {}
            _evtconn = null;
        }

        // remove ourselves from the clients list
        Event evt = new Event(Event.ATTR_CHANGED, "cmgr.clients");
        evt.name = _cid;
        try { Server.omgr.dispatchEvent(evt); } catch (IOException e) {}
    }

    //
    // Subscriber public member functions

    public synchronized boolean handleEvent (DObject target, Event evt)
    {
        if (_evtconn == null) return false;

        try {
            evt.writeTo(_evtout);
            _evtout.flush();

        } catch (IOException e) {
            System.err.println("Error sending event [to " + _cid + ": " + e);

            // normally, this get's handled by the closing of the control
            // connection, but I continually see cases where the event
            // connection goes away while the client connection remains
            if (e.getMessage().equals("Broken pipe")) {
                Server.cmgr.destroyClient(_cid);
                return false;
            }
        }

        return true;
    }

    //
    // Runnable public member functions

    public void run ()
    {
        while (_handler == Thread.currentThread()) {
            try {
                byte cmd = _ctlin.readByte();

                switch (cmd) {
                case CCP.CREATE: {
                    // read the oid from the stream and subscribe to that
                    // distributed object, we will subsequently receive
                    // events for that object and pass them to the client
                    DObject obj =
                        Server.omgr.subscribeToObject(_ctlin.readUTF(), this);
                    obj.writeTo(_ctlout);
                    _ctlout.flush();
                    break;
                }

                case CCP.REGISTER:
                    // shouldn't ever see this
                    System.err.println("Received repeat register request.");
                    break;

                case CCP.FWD_EVENT:
                    Server.omgr.dispatchEvent(new Event(_ctlin));
                    break;

                default:
                    System.err.println("Unknown request: " + cmd);
                    break;
                }

            } catch (IOException e) {
                System.err.println("Client error: " + e);
                Server.cmgr.destroyClient(_cid);
                break;
            }
        }
    }

    //
    // Client protected data members

    String _cid;
    Thread _handler;

    Socket _ctlconn;
    DataInputStream _ctlin;
    DataOutputStream _ctlout;

    Socket _evtconn;
    DataOutputStream _evtout;
}
