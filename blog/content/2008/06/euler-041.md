---
layout: post
title: "Euler 041"
date: 2008-06-01
---

[Problem 041]\:

```scala
object Euler41 extends EulerApp {
  def perms (d :List[Char], n :List[Char]) :List[Int] = d match {
    case Nil => n.mkString.toInt :: Nil;
    case _ => d.flatMap(digit => perms(d.filter(digit.!=), digit :: n))
  }
  println(perms("1234567″.toList, Nil).filter(isprime).foldLeft(0)(Math.max));
}
```
We save ourselves many CPU cycles by noticing that a pandigital number with 1 through 9 will always have digits that sum to 45, and similarly one with 1 through 8 will always have digits that sum to 36. Both of those numbers are divisible by three which means that no 8 or 9 digit pandigitals are prime.

Armed with that handy shortcut, we then generate all possible permutations of the digits 1 through 7, filter them for primeness and find the largest one. Voila!

This is sort of a gratuitous use of a case method, but it brings back fond memories of SML, so I didn't fight the feeling.



[Problem 041]: http://projecteuler.net/index.php?section=problems&id=41
