---
layout: post
title: "Euler 005"
date: 2008-01-09
---

[Problem 005]\:

{% highlight scala %}
object Euler5 extends Application {
  val divisors = List(20, 19, 18, 17, 16, 15, 14, 13, 12, 11);
  def check (value :Int) :Int = {
    if (divisors.exists((a) => (value % a != 0))) return check(20+value)
    else return value
  }
  println(check(20));
}
{% endhighlight %}
Interestingly the tail recursive version above runs in ~1500ms whereas this iterative version takes ~37000ms:

{% highlight scala %}
object Euler5 extends Application {
  val divisors = List(20, 19, 18, 17, 16, 15, 14, 13, 12, 11);
  def notDivisible (value :Int) :Boolean = {
    return divisors.exists((a) => (value % a != 0))
  }

  var value :Int = 20;
  while (notDivisible(value)) {
    value = value + 20;
  }
  println(value);
}
{% endhighlight %}
And this seemingly “optimized" version takes a whopping 154 seconds to run:

{% highlight scala %}
object Euler5 extends Application {
  val divisors = List(20, 19, 18, 17, 16, 15, 14, 13, 12, 11);
  var value :Int = 20;
  while (divisors.exists((a) => (value % a != 0))) {
    value = value + 20;
  }
  println(value);
}
{% endhighlight %}
Clearly something funny is going on referencing “var value" inside the anonymous function passed to divisors.exists. In the notDivisible version the “val" (immutable) function argument is referenced by the anonymous function which presumably triggers some optimization on the part of the Scala compiler.

All this is one of the many reasons that I think most people stick with procedural languages and their significantly less mysterious performance characteristics. Perhaps one can develop a sufficiently intimate knowledge of one's functional language of choice to cultivate an intuition for these sorts of performance considerations, but I expect that it would take a lot of against-wall head banging and deep digging.



[Problem 005]: http://projecteuler.net/index.php?section=problems&id=5
