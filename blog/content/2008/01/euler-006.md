---
layout: post
title: "Euler 006"
date: 2008-01-09
---

[Problem 006]\:

{% highlight scala %}
object Euler6 extends Application {
  val first100sum = List.range(1,101).foldLeft(0)(_+_);
  val first100sumsq = List.range(1,101).foldLeft(0)((b, a) => (b + a*a));
  println(first100sum * first100sum - first100sumsq);
}
{% endhighlight %}

[Problem 006]: http://projecteuler.net/index.php?section=problems&id=6
