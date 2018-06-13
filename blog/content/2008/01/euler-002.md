---
layout: post
title: "Euler 002"
date: 2008-01-09
---

[Problem 002]\:

```scala
object Euler2 extends Application {
  def fibby (a :Int, b :Int) :Int = {
    val next = a + b;
    return (if (b % 2 == 0) b else 0) + (if (next > 1000000) 0 else fibby(b, next));
  }
  println(fibby(1, 2));
}
```

[Problem 002]: http://projecteuler.net/index.php?section=problems&id=2
