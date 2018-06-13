---
layout: post
title: "Euler 034"
date: 2008-02-25
---

[Problem 034]\:

{% highlight scala %}
object Euler34 extends Application {
  val facts = Array(1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880);
  def digfact (sum :Int, n :Int) :Int =
    if (n == 0) sum else digfact(facts(n % 10) + sum, n/10);
  def check (sum :Int, n :Int) :Int =
    if (n == 2000000) sum;
    else if (digfact(0, n) == n) check(sum+n, n+1);
    else check(sum, n+1);
  println(check(0, 10));
}
{% endhighlight %}
Since we're only computing factorial for single digit numbers, we pre-compute the values of 0 through 9 factorial and then breeze up through the positive integers accumulating all numbers that meet our criterion. We stop searching at 2000000 because 1999999 is the last number for which the sum of the factorials of the digits is greater than or equal to the number itself.



[Problem 034]: http://projecteuler.net/index.php?section=problems&id=34
