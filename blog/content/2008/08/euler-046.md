---
layout: post
title: "Euler 046"
date: 2008-08-10
---

[Problem 046]\:

```scala
object Euler46 extends EulerApp {
  def isgold (n :Int) = 1.to(Math.sqrt(n/2)).exists(s => isprime(n-2*s*s))
  def check (n :Int) :Int = if (isprime(n) || isgold(n)) check(n+2) else n
  println(check(3))
}
```
Fortunately this one is easier to disprove than Goldbach's <a href="http://en.wikipedia.org/wiki/Goldbach%27s_conjecture">other conjecture</a>. If n = p + 2*s*s, we can simply try all values of s from 1 to the square root of n/2 and check whether n - 2*s*s is prime. If so, we try the next larger odd integer until we find the (surprisingly small) value that disproves the conjecture. I'm sure Euler would have disproved it more cleverly, but it also would have taken him more than 26 milliseconds.



[Problem 046]: http://projecteuler.net/index.php?section=problems&id=46
