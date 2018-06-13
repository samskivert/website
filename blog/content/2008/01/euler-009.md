---
layout: post
title: "Euler 009"
date: 2008-01-12
---

[Problem 009]\:

{% highlight scala %}
object Euler9 extends Application {
  for (a <- List.range(1, 1000);
       b <- List.range(a, 1000);
       c <- List.range(b, 1000);
       if (a*a + b*b == c*c && a+b+c == 1000))
    println(a*b*c);
}
{% endhighlight %}
Sequence comprehensions coming in handy here.



[Problem 009]: http://projecteuler.net/index.php?section=problems&id=9
