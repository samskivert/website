---
layout: post
title: "Euler 035"
date: 2008-02-25
---

[Problem 035]\:

{% highlight scala %}
object Euler35 extends EulerApp {
  val primes = genprimes(1000000);
  def digits (value :Int) :Int = if (value == 0) 0 else 1 + digits(value/10);
  def rotate (value :Int, turns :Int) :Int = {
    val mod = Math.pow(10, digits(value)-turns).intValue()
    (value % mod) * Math.pow(10, turns).intValue() + (value / mod)
  }
  def circprime (n :Int) :Boolean = {
    List.range(0, digits(n)).foldRight(true)((t, b) => (b && (primes(rotate(n, t)) != 0)))
  }
  println(primes.filter(0.!=).filter(circprime).length);
}
{% endhighlight %}
Coming up with rotate() was fun and the rest was pretty straightforward. I first wrote a recursive method that rotated the number one digit at a time, but then I realized I could do it in a single expression. If there existed a built in operator for raising an integer to an integer power, rotate would look much nicer.



[Problem 035]: http://projecteuler.net/index.php?section=problems&id=35
