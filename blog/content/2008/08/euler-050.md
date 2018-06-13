---
layout: post
title: "Euler 050"
date: 2008-08-18
---

[Problem 050]\:

{% highlight scala %}
object Euler50 extends EulerApp {
  val pvec = genprimes(1000000)
  val primes = pvec.filter(0.!=)
  case class PSum (sum :Int, length :Int) {
    def add (prime :Int) = PSum(sum+prime, length+1)
  }
  def fsum (idx :Int, csum :PSum, lsum :PSum) :PSum = {
    if (idx >= primes.length || csum.sum >= pvec.length) lsum
    else fsum(idx+1, csum.add(primes(idx)), if (pvec(csum.sum) != 0) csum else lsum)
  }
  def longer (one :PSum, two :PSum) = if (one.length > two.length) one else two
  println(0.until(primes.length).map(fsum(_, PSum(0, 0), PSum(0, 0))).reduceLeft(longer).sum)
}
{% endhighlight %}
I had a slightly shorter solution that did not make use of a helper class, but it's just so easy to encapsulate a bit of data and functionality into a handy little helper that I couldn't resist.



[Problem 050]: http://projecteuler.net/index.php?section=problems&id=50
