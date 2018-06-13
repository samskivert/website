---
layout: post
title: "Euler 044"
date: 2008-07-20
---

[Problem 044]\:

```scala
object Euler44 extends Application {
  def pent (n :Int) = n*(3*n-1)/2
  val pents = Set() ++ List.range(1, 3000).map(pent)
  def find (a :Int, b :Int) :Int = {
    val pa = pent(a)
    val pb = pent(b)
    val d = pb-pa
    val s = pa+pb
    if (pents.contains(d) && pents.contains(s)) d
    else if (a >= b) find(1, b+1)
    else find(a+1, b)
  }
  println(find(1, 2))
}
```
This one's not especially elegant or clever (but it is fast). We just scanning increasing pairs of integers for a pair that meets our criteria and stop when we find it. We optimize by generating a “sufficiently large" list of pentagonal numbers and putting them in a set so that we can quickly test a sum or difference for pentagonality.



[Problem 044]: http://projecteuler.net/index.php?section=problems&id=44
