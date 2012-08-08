---
layout: post
title: Everthing but the kitchen async
---

I frequently suffer from Java's complete lack of assistance when it comes to
asynchronous programming, but this little multi-phase maneuver really brings
home the pain:

{% highlight java %}
protected void findFriends (final Socks.Network sock, Group contents) {
    contents.removeAll();
    final Label status = UI.wrapLabel(_msgs.findingFriends);
    contents.add(status);

    class FriendFinder {
        public void start () {
            // make sure we've authenticated with this network
            sock.authenticate(new Callback<Socks.Member>() {
                public void onSuccess (Socks.Member self) { gotSelf(self); }
                public void onFailure (Throwable err) { fail(_msgs.authErrPre, err); }
            });
        }

        protected void gotSelf (Socks.Member self) {
            // note our own name and id on this network for posterity
            Global.persist.player().socksIds.put(sock.id(), self);

            // get full list of friends for this social network...
            sock.getFriends(new Callback<List<Socks.Member>>() {
                public void onSuccess (List<Socks.Member> friends) { gotFriends(friends); }
                public void onFailure (Throwable err) { fail(_msgs.getFriendsErrPre, err); }
            });
        }

        protected void gotFriends (List<Socks.Member> friends) {
            Json.Writer writer = json().newWriter();
            writer.array();
            for (Socks.Member friend : friends) writer.value(friend.userId);
            writer.end();

            final Map<String,Socks.Member> fmap = Maps.newHashMap();
            for (Socks.Member friend : friends) fmap.put(friend.userId, friend);

            // filter our full list of friends, to obtain those that play d11s
            net().post(Global.serviceURL("filter"), writer.write(), new Callback<String>() {
                public void onSuccess (String data) { gotFiltered(fmap, data); }
                public void onFailure (Throwable err) { fail(_msgs.filterErrPre, err); }
            });
        }

        protected void gotFiltered (Map<String,Socks.Member> fmap, String data) {
            try {
                List<Socks.Member> filtered = Lists.newArrayList();
                Json.Array fids = json().parseArray(data);
                for (int ii = 0, ll = fids.length(); ii < ll; ii++) {
                    Socks.Member friend = fmap.get(fids.getString(ii));
                    if (friend != null) filtered.add(friend);
                    else log.warning("Got unknown friend from server",
                                     "id", fids.getString(ii), "fmap", fmap);
                }

                // store the filtered friends locally
                Global.persist.socks().storeFriends(sock.id(), filtered);

                // and finally update the user interface and report success
                int added = addFriends(sock.id(), filtered);
                setStatus(_msgs.foundFriends(added, SocksUI.name(sock)));

            } catch (Exception e) {
                log.warning("Failed to parse filtered friends", "data", data, e);
                fail(_msgs.filterParseErrPre, e);
            }
        }

        protected void fail (String prefix, Throwable cause) {
            setStatus(prefix + ": " + cause.getMessage());
        }

        protected void setStatus (String message) {
            status.text.update(message);
        }
    }
    new FriendFinder().start();
}
{% endhighlight %}

I've already taken great liberties with "Java style" to achieve a level of
concision that allows at least some of the control flow to remain visible, but
it still burns the eyes.

Just for fun, let's imagine what that might look like if Java supported
something like C#'s `async` mechanism (wherein the resulting code is the same
as the above, but the compiler takes care of the boilerplate behind the
scenes):

{% highlight java %}
protected void findFriends (final Socks.Network sock, Group contents) {
    contents.removeAll();
    final Label status = UI.wrapLabel(_msgs.findingFriends);
    contents.add(status);

    String errpre = _msgs.authErrPre;
    try {
        Socks.Member self = await sock.authenticate();

        // note our own name and id on this network for posterity
        Global.persist.player().socksIds.put(sock.id(), self);

        // get full list of friends for this social network...
        errpre = _msgs.getFriendsErrPre;
        List<Socks.Member> friends = await sock.getFriends();
        Map<String,Socks.Member> fmap = Maps.newHashMap();
        for (Socks.Member friend : friends) fmap.put(friend.userId, friend);

        // filter our full list of friends, to obtain those that play d11s
        Json.Writer writer = json().newWriter();
        writer.array();
        for (Socks.Member friend : friends) writer.value(friend.userId);
        writer.end();
        errpre = _msgs.filterErrPre;
        String data = await net().post(Global.serviceURL("filter"), writer.write());
        Json.Array fids = json().parseArray(data);
        List<Socks.Member> filtered = Lists.newArrayList();
        for (int ii = 0, ll = fids.length(); ii < ll; ii++) {
            Socks.Member friend = fmap.get(fids.getString(ii));
            if (friend != null) filtered.add(friend);
            else log.warning("Got unknown friend from server",
                             "id", fids.getString(ii), "fmap", fmap);
        }

        // store the filtered friends locally
        Global.persist.socks().storeFriends(sock.id(), filtered);

        // and finally update the user interface and report success
        int added = addFriends(sock.id(), filtered);
        status.text.update(_msgs.foundFriends(added, SocksUI.name(sock)));

    } catch (Exception e) {
        status.text.update(errpre + ": " + e.getMessage());
    }
}
{% endhighlight %}

While we're dreaming, let's rewrite it all in Scala (assuming the use of the
delimited continuations plugin to do the appropriate rewriting):

{% highlight scala %}
def findFriends (sock :Socks.Network, contents :Group) {
  contents.removeAll()
  val status = UI.wrapLabel(_msgs.findingFriends)
  contents.add(status)

  var errpre = _msgs.authErrPre
  try {
    val self = await sock.authenticate

    // note our own name and id on this network for posterity
    Global.persist.player.socksIds.put(sock.id, self)

    // get full list of friends for this social network...
    errpre = _msgs.getFriendsErrPre
    val friends = await sock.getFriends

    // filter our full list of friends, to obtain those that play d11s
    errpre = _msgs.filterErrPre
    val json = json.newWriter.array(friends map(_.userId)).write
    val farray = json.parseArray(await net.post(Global.serviceURL("filter"), json))
    val fids = (0 to farray.length) map(farray.getString) toSet
    val fmap = friends map(f => f.userId -> f) toMap
    val filtered = fmap filterKeys(fids) values
    if (fids.size != filtered.size) {
      log.warnning("Got unknown friends(s) from server", "friends", friends,
                   "ids", (fids -- filtered.map(_.userId)))
    }

    // store the filtered friends locally
    Global.persist.socks.storeFriends(sock.id, filtered)

    // and finally update the user interface and report success
    val added = addFriends(sock.id, filtered)
    status.text.update(_msgs.foundFriends(added, SocksUI.name(sock)))

  } catch {
    case e => status.text.update(errpre + ": " + e.getMessage)
  }
}
{% endhighlight %}

Alas, I can't use Scala on my current project. I'll shut up now and get back to
the salt mines.
