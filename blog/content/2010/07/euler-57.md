---
layout: post
title: "Euler 57"
date: 2010-07-26
---

[Problem 057]\:

```scala
object Euler57 extends EulerApp {
  case class Frac (numer :BigInt, denom :BigInt) {
    def add (n :BigInt) = Frac(n * denom + numer, denom)
    def invert = Frac(denom, numer)
    def numheavy = numer.toString.length > denom.toString.length
  }
  def expand (count :Int) :Frac = if (count == 0) Frac(1, 2)
                                  else expand(count-1).add(2).invert
  def answer = (0 to 1000) filter(i => expand(i).add(1).numheavy) length
}
```
I took the opportunity to use a case class to model just enough of a rational to get the job done. The only other thinking required was figuring out how to perform the infinite expansion iteratively: start with ½, add two, invert, repeat as desired, then finally add one.

I was lazy in two ways: I used `BigInt` instead of reducing the numerator and denominator along the way to keep things fitting in 32 (or 64) bits, and I could have probably structured the code differently to make use of incremental results, but this already runs in about two seconds, so I didn't feel the need. Oh, and I also probably shouldn't be abusing the stack like I am in `expand`, so call it three ways lazy.


[Problem 057]: http://projecteuler.net/index.php?section=problems&id=57
