---
layout: post
title: “Fun with edge cases” or “Why your spec can never possibly be complete”
date: 2011-08-12
---

(Apologies for the duplication with Google+, still sorting out my syndication
process.)

What do you think should happen if you do the following in Java?

{% highlight java %}
Map<String,String> map = new HashMap<String,String>();
map.put("foo", "bar");
Iterator<Map.Entry<String,String>> iter = map.entrySet().iterator();
Map.Entry<String,String> entry = iter.next();
iter.remove();
entry.setValue("baz");
{% endhighlight %}

Should the entry freak out because you tried to update the mapping after it has
already been removed? Should it reintroduce a mapping from “foo” to “baz”?
Should the call succeed but have no effect on the map?

Java actually specifies the behavior as undefined. So perhaps your spec can be
complete by pushing the burden onto the developer of ensuring that they never
rely on any undefined behavior. The HashMap implementation’s interpretation of
“undefined” is “allow the call to succeed and don’t modify the underlying map.”
That makes it more difficult to ensure you’re not relying on undefined behavior
than if it, say, threw an exception.

Perhaps the question on your mind is “Gee Mike, how do you find yourself in the
position of caring about something like this?” The answer to that lies in
[this code], which chooses a different interpretation of undefined.

[this code]: https://github.com/threerings/react/blob/master/src/main/java/react/RMap.java
