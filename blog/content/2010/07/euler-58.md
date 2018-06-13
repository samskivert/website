---
layout: post
title: "Euler 58"
date: 2010-07-27
---

[Problem 058]\:

```scala
object Euler58 extends EulerApp {
  def checkring (r :Int, primes :Int, nums :Int) :Int = {
    if (primes*10/nums < 1) 2*r-1
    else {
      val skip = 2*r
      val base = (skip-1)*(skip-1)
      val rp = List(1, 2, 3).map(base+skip*_).filter(isprime).length
      checkring(r+1, primes+rp, nums+4);
    }
  }
  def answer = checkring(2, 3, 5)
}
```
This one is pretty easy after observing that you skip twice the “radius" to get from one corner to the next for a given ring of the spiral. So we just tail-recurse on out the rings, checking the corners for primality and counting them up. We precompute the first ring, because if I start with a ring of radius 1, with 0 primes and 1 number in total, our condition of fewer than 10% primes is prematurely satisfied.


[Problem 058]: http://projecteuler.net/index.php?section=problems&id=58
