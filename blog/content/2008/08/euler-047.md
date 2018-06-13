---
layout: post
title: "Euler 047"
date: 2008-08-10
---

[Problem 047]\:

```scala
object Euler47 extends Application {
  val factors = Array.make(150000, 0)
  var idx = 2
  while (idx < factors.length) {
    for (midx <- List.range(idx+idx, factors.length, idx)) factors(midx) += 1
    do idx = idx+1
    while (idx < factors.length && factors(idx) != 0)
  }
  println(1.to(factors.length-3).find(n => factors.slice(n, n+4).mkString == "4444"))
}
```
Here we do two things: first, use a modified Sieve of Eratosthenes to count up the unique factors of all numbers up to some maximum value (which cheat and choose to be “large enough"). Start with the first prime (2), increment by one the factors cell for all multiples of that number (that's the for loop right after the while), then scan up the list for the next prime (the next number which has no factors, thus factors(idx) == 0). Lather, rinse, repeat.

The second part is easy (and less clunkily procedural): slide a window up that list of factors looking for four fours.



[Problem 047]: http://projecteuler.net/index.php?section=problems&id=47
