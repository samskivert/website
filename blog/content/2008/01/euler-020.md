---
layout: post
title: "Euler 020"
date: 2008-01-18
---

[Problem 020]\:

```scala
object Euler20 extends Application {
  def fact (n: BigInt): BigInt = if (n == 0) 1 else n * fact(n - 1)
  println(fact(100).toString().foldRight(0)((a, b) => (b + (a - '0'))));
}
```
Another one where BigInt does all the heavy lifting.



[Problem 020]: http://projecteuler.net/index.php?section=problems&id=20
