---
layout: post
title: "Euler 003"
date: 2008-01-09
---

[Problem 003]\:

{% highlight scala %}
object Euler3 extends Application {
  def lpf (divis :Long, divid :Long) :Long = {
    if (divid % divis == 0) lpf(2, divid/divis)
    else if (divis > Math.sqrt(divid)) divid
    else lpf(divis+1, divid)
  }
  println(lpf(2l, 317584931803l));
}
{% endhighlight %}

[Problem 003]: http://projecteuler.net/index.php?section=problems&id=3
