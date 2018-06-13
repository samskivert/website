---
layout: post
title: "Euler 53"
date: 2010-01-01
---

[Problem 053]\:

```scala
object Euler53 extends Application {
  def fact (n :BigInt) :BigInt = if (n < 2) 1 else n * fact(n-1)
  def choose (n :BigInt, r :BigInt) = fact(n)/(fact(r)*fact(n-r))
  println((for (n <- 1 to 100; r <- 1 to n; if (choose(n, r) > 1000000)) yield 1).sum)
}
```

Another simple brute force solution. Enumerate and count. <code>BigInt</code> lets us avoid any
serious thinking.

[Problem 053]: http://projecteuler.net/index.php?section=problems&id=53
