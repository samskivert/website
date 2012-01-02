//
// Client

package dclient;

import dist.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;

public class Client implements Runnable, DObjectManager
{
    //
    // Client public constructor

    public Client (String host) throws IOException
    {
        // create the control connection
        _ctlconn = new Socket(host, CCP.PORT);
        InputStream in = _ctlconn.getInputStream();
        _ctlin = new DataInputStream(new BufferedInputStream(in));
        OutputStream out = _ctlconn.getOutputStream();
        _ctlout = new DataOutputStream(new BufferedOutputStream(out));

        _cid = InetAddress.getLocalHost().toString() + ":" +
            _ctlconn.getLocalPort();
        _ctlout.writeByte(CCP.CREATE);
        _ctlout.writeUTF(_cid);
        _ctlout.flush();

        byte rsp = _ctlin.readByte();
        if (rsp != CCP.OK) {
            _ctlconn.close();
            throw new IOException("Failure to connect to server:" + rsp);
        }

        // now create the event connection
        _evtconn = new Socket(host, CCP.PORT);
        in = _evtconn.getInputStream();
        _evtin = new DataInputStream(new BufferedInputStream(in));
        out = _evtconn.getOutputStream();
        _evtout = new DataOutputStream(new BufferedOutputStream(out));

        _evtout.writeByte(CCP.REGISTER);
        _evtout.writeUTF(_cid);
        _evtout.flush();

        rsp = _evtin.readByte();
        if (rsp != CCP.OK) {
            _ctlconn.close();
            _evtconn.close();
            throw new IOException("Failure to register event socket:" + rsp);
        }

        _dispatcher = new Thread(this);
        _dispatcher.start();

        System.out.println("Registered with server as: " + _cid);
    }

    //
    // Client public member functions

    public String clientId () { return _cid; }

    public void disconnect ()
    {
        // close the control connection
        if (_ctlconn != null) {
            try { _ctlconn.close(); } catch (IOException e) {}
            _ctlconn = null;
        }

        // close the event connection
        _dispatcher = null;
        if (_evtconn != null) {
            try { _evtconn.close(); } catch (IOException e) {}
            _evtconn = null;
        }
    }

    //
    // Runnable public member functions

    public void run ()
    {
        while (_dispatcher == Thread.currentThread()) {
            try {
                // loop and loop and loop and dispatch events
                dispatchEvent(new Event(_evtin));

            } catch (IOException e) {
                System.err.println("Error reading events: " + e);
            }
        }
    }

    //
    // DObjectManager public member functions

    public void attributeChanged (DObject object, String name,
                                  Object value) throws IOException
    {
        Event evt = new Event(Event.ATTR_CHANGED, object.oid);
        evt.name = name;
        evt.value = value;
        _ctlout.writeByte(CCP.FWD_EVENT);
        evt.writeTo(_ctlout);
        _ctlout.flush();
    }

    public DObject subscribeToObject (String oid, Subscriber sub)
         throws IOException
    {
        DObject dmo = (DObject)_objects.get(oid);

        // get this object from the server if we don't already have it
        if (dmo == null) {
            // write the create request
            _ctlout.writeByte(CCP.CREATE);
            _ctlout.writeUTF(oid);
            _ctlout.flush();

            // read the result object from the stream
            dmo = new DObject(oid, this);
            dmo.readFrom(_ctlin);
            _objects.put(oid, dmo);
        }

        dmo.addSubscriber(sub);
        return dmo;
    }

    public void destroyObject (String oid) throws IOException
    {
        Event evt = new Event(Event.OBJECT_DELETED, oid);
        _ctlout.writeByte(CCP.FWD_EVENT);
        evt.writeTo(_ctlout);
        _ctlout.flush();
    }

    public void dispatchEvent (Event evt) throws IOException
    {
        DObject dmo = (evt.type == Event.OBJECT_DELETED) ?
            (DObject)_objects.remove(evt.oid) :
            (DObject)_objects.get(evt.oid);

        if (dmo == null) {
            System.err.println("No such object: " + evt.oid +
                               " for evt: " + evt.type);
            return;
        }

        dmo.notifySubscribers(evt);
    }

    //
    // Client protected data members

    protected String _cid;

    protected Socket _ctlconn;
    protected DataInputStream _ctlin;
    protected DataOutputStream _ctlout;

    protected Socket _evtconn;
    protected DataInputStream _evtin;
    protected DataOutputStream _evtout;

    protected Hashtable _objects = new Hashtable();
    protected Thread _dispatcher;
}
