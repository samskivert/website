---
layout: post
title: "Euler 027"
date: 2008-02-03
---

[Problem 027]\:

```scala
object Euler27 extends EulerApp {
  val MAX_N = 80;
  val primes = genprimes(MAX_N*MAX_N + MAX_N*1000 + 1000);
  def polyprimes (primes :Array[Int], a :Int, b :Int, n :Int) :Int =
    if (primes(Math.abs(n*n + a*n + b)) == 0) 0 else 1 + polyprimes(primes, a, b, n+1);

  val polys = for {
    a <- List.range(-1000, 1000)
    b <- List.range(-1000, 1000)
    p <- List(polyprimes(primes, a, b, 0))
    if (p > 0)
  } yield Pair(a * b, p);
  println(polys.foldLeft(Pair(0, 0))((a, b) => if (a._2 > b._2) a else b)._1);
}
```
Pretty straightforward. Just try all combinations from -1000 to 1000 and see how many consecutive primes are generated and pick the one that generates the most. You may notice that I'm passing the list of primes into the polyprimes function because referencing it directly from its enclosing context annoyingly results in a 40 second runtime instead of 28 seconds.



[Problem 027]: http://projecteuler.net/index.php?section=problems&id=27
