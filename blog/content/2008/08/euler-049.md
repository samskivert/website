---
layout: post
title: "Euler 049"
date: 2008-08-11
---

[Problem 049]\:

{% highlight scala %}
object Euler49 extends EulerApp {
  def sort (n :Int) = n.toString.toList.sort(_<_).mkString.toInt
  def isterm (n :Int, nn :Int) = isprime(nn) && sort(nn) == sort(n)
  def isseq (n :Int) = isterm(n, n+3330) && isterm(n, n+6660)
  def mkseq (n :Int) = List(n, n+3330, n+6660).mkString
  println(mkseq(1488.to(10000-6660).filter(isprime).filter(isseq).first))
}
{% endhighlight %}
This one's been somewhat deconstructed. I originally just wrote a for loop, but then I wanted to make it a bit more functional. Then I wanted to make it a bit shorter. In the end it's still fairly readable, in an HP calculator, Reverse Polish sort of way.



[Problem 049]: http://projecteuler.net/index.php?section=problems&id=49
