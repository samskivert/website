//
// dserver.DObjectManager - handles changes to an object by sending events
//                          to all registered clients of that object
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

import dist.Event;
import dist.DObject;
import dist.Subscriber;

import java.io.IOException;
import java.util.Hashtable;

public class DObjectManager implements dist.DObjectManager
{
    //
    // DObjectManager public member functions

    public void attributeChanged (DObject obj, String name,
                                  Object value) throws IOException
    {
        Event evt = new Event(Event.ATTR_CHANGED, obj.oid);
        evt.name = name;
        evt.value = value;
        obj.handleEvent(evt);
    }

    public DObject subscribeToObject (String oid, Subscriber sub)
         throws IOException
    {
        DObject dob = (DObject)_objects.get(oid);
        if (dob == null) _objects.put(oid, dob = new DObject(oid, this));
        dob.addSubscriber(sub);
        return dob;
    }

    public void destroyObject (String oid) throws IOException
    {
        dispatchEvent(new Event(Event.OBJECT_DELETED, oid));
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

        dmo.handleEvent(evt);
    }

    //
    // DObjectManager protected data members

    Hashtable _objects = new Hashtable();
}
