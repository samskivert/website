---
layout: post
title: "Euler 037"
date: 2008-02-25
---

[Problem 037]\:

{% highlight scala %}
object Euler37 extends EulerApp {
  val primes = genprimes(1000000);
  def isrtrunc (prime :Int) :Boolean =
    (prime == 0) || ((primes(prime) != 0) && isrtrunc(prime/10));
  def isltrunc (prime :String) :Boolean =
    prime.isEmpty || ((primes(prime.toInt) != 0) && isltrunc(prime.substring(1)));
  def istrunc (prime :Int) = isrtrunc(prime) && isltrunc(prime.toString)
  println(primes.drop(10).filter(0.!=).filter(istrunc).foldRight(0)(_+_));
}
{% endhighlight %}
I cheated a little and turned the integer into a string in order to easily truncate it from the left, it would perhaps have been more elegant to take the value modulo 10<sup>digits-1</sup> but the code would have been longer and I feel an irrational desire for brevity in these solutions.



[Problem 037]: http://projecteuler.net/index.php?section=problems&id=37
