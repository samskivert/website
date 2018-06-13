---
layout: post
title: "Euler 016"
date: 2008-01-17
---

[Problem 016]\:

{% highlight scala %}
object Euler16 extends Application {
  println(BigInt(2).pow(1000).toString().foldRight(0) {(a, b) => (b + (a - '0'))});
}
{% endhighlight %}
This one almost seemed like cheating, since BigInt takes care of all the hard work for me.



[Problem 016]: http://projecteuler.net/index.php?section=problems&id=16
