---
layout: post
title: "Euler 65"
date: 2011-01-13
---

[Problem 065](http://projecteuler.net/index.php?section=problems&id=65):
(<a href="http://github.com/samskivert/euler-scala/raw/master/Euler065.scala">source</a>):

```scala
object Euler065 extends EulerApp {
  case class Frac (numer :BigInt, denom :BigInt) {
    def + (n :BigInt) = Frac(n * denom + numer, denom)
    def invert = Frac(denom, numer)
  }
  def compute (count :Int, n :Int) :Frac = {
    val term = if (n == 1) 2 else if (n % 3 == 0) 2*(n/3) else 1
    if (n == count) Frac(term, 1)
    else compute(count, n+1).invert + term
  }
  def answer = compute(100, 1).numer.toString map(_-'0') sum
}
```

The only challenge here is algorithmizing the computation of the continued
fraction, while preserving the numerator and denominator, so that we can
compute the answer from the final numerator. I think the case class makes the
calculations on the fraction much tidier.
