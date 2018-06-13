---
layout: post
title: "Euler 045"
date: 2008-07-20
---

[Problem 045]\:

```scala
object Euler45 extends Application {
  def findh (pent :Long, h :Long) :Long = {
    val hex = h*(2*h-1)
    if (hex > pent) findh(pent, h-1)
    else if (hex < pent) 0
    else hex
  }
  def findp (tri :Long, p :Long) :Long = {
    val pent = p*(3*p-1)/2
    if (pent > tri) findp(tri, p-1)
    else if (pent < tri) 0
    else findh(pent, p-1)
  }
  def find (t :Long) :Long = {
    val n = findp(t*(t+1)/2, t-1)
    if (n != 0) n
    else find(t+1)
  }
  println(find(286))
}
```
We know that for a given t, any number p for which pent(p) = tri(t) will be less than t. The same holds for hex and pent. So we search down from our starting n for a matching pentagonal number and if we find it, we search further down for a matching hexagonal number. We also know that when we get to an n that generates a pent that is less than our tri we can stop because pent(n) will only keep getting smaller and thus never be equal to our tri.

findp() and findh() are similar enough that they could be abstracted into something like:

```scala
  def findf (n :Long, x :Long, funcs :List[(Long => Long)]) :Long = {
    val fx = funcs.head(x)
    if (fx > n) findf(n, x-1, funcs)
    else if (fx < n) 0
    else if (funcs.size == 1) fx
    else findf(fx, x-1, funcs.tail)
  }
```
but that's just crazy. (It also turns out to be quite a bit slower.)



[Problem 045]: http://projecteuler.net/index.php?section=problems&id=45
