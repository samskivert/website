---
layout: post
title: "Euler 026"
date: 2008-02-03
---

[Problem 026]\:

{% highlight scala %}
object Euler26 extends Application {
  def divcycle (numer :Int, denom :Int, rlist :List[Int]) :Int = {
    val remain = numer % denom;
    if (remain == 0) return 0;
    val ridx = rlist.indexOf(remain);
    if (ridx >= 0) ridx+1 else divcycle(remain * 10, denom, remain :: rlist);
  }
  var cycles = List.range(1, 1000).map(v => divcycle(1, v, Nil));
  println(cycles.indexOf(cycles.foldRight(0)(Math.max))+1);
}
{% endhighlight %}
This one turned out to be easy once I realized that the key was looking at the remainder after each step in the long division process. If the remainder is ever the same as a remainder you've seen previously, then you've found a loop.

Prior to that I was tracking the actual division result and looking for loops in the digits which was problematic because one doesn't know when to declare a repeated series of numbers a loop. I was in the middle of doing some hairbrained fiddling that involved making sure that the repeated series of digits was at least as long as all the digits that had come before it when I realized there must be a more elegant solution. Indeed there was.



[Problem 026]: http://projecteuler.net/index.php?section=problems&id=26
