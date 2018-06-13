---
layout: post
title: "Euler 029"
date: 2008-02-23
---

[Problem 029]\:

```scala
object Euler29 extends Application {
  println((for {
    a <- List.range(2, 101)
    b <- List.range(2, 101)
  } yield BigInt(a).pow(b)).removeDuplicates.length);
}
```
This one's pretty easy thanks to BigInt.



[Problem 029]: http://projecteuler.net/index.php?section=problems&id=29
