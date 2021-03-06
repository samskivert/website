---
layout: post
title: "Euler 021"
date: 2008-01-18
---

[Problem 021]\:

```scala
object Euler21 extends Application {
  def sumdiv (n :Int) = List.range(1, n/2+1).filter(div => n % div == 0).foldLeft(0)(_+_)
  println(List.range(1, 10000).filter(n => {
    val sdn = sumdiv(n); (n == sumdiv(sdn) && n != sdn && sdn < 10000)
  }).foldLeft(0)(_+_));
}
```
This one is almost readable — if you've been staring at a lot of Scala recently. It also uses a less efficient method for computing divisors because it fits on one line.

The above runs in 1176ms but the version using:

```scala
  def sumdiv (x :Int) = (1 :: List.flatten(for {
    divis < - List.range(2, Math.sqrt(x)+1)
    if x % divis == 0
  } yield List(divis, x / divis).removeDuplicates)).foldLeft(0)(_+_)
```
runs in 157ms. Oh the sacrifices we make for brevity.


[Problem 021]: http://projecteuler.net/index.php?section=problems&id=21
