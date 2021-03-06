---
layout: post
title: "Euler 032"
date: 2008-02-23
---

[Problem 032]\:

```scala
object Euler32 extends Application {
  def digits (n :Int) = n.toString.toList.map(c => c - '0');
  def ispan (a :Int, b :Int, n :Int) :Boolean =
    (digits(a) ::: digits(n) ::: digits(b)).sort(_<_).equals(List.range(1,10));
  def haspanfact (n :Int) :Boolean =
    List.range(2, 100).find((a) => (n % a == 0 && ispan(a, n/a, n))) != None;
  println(List.range(1000, 10000).filter(haspanfact).foldRight(0)(_+_));
}
```
We want a factorization of a four digit number (which will be either two digits times three or one digit times four), so we enumerate all 4 digit numbers from 1000 to 9999, filter them by checking all possible one and two digit divisors first for divisibility and second for pandigitality, then sum the matches. Being able to decompose numbers into lists of digits, jam those lists together, sort them and compare them to a list with 1 through 9 in consecutive order in three lines of code sure is handy.



[Problem 032]: http://projecteuler.net/index.php?section=problems&id=32
