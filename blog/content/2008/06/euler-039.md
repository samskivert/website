---
layout: post
title: "Euler 039"
date: 2008-06-01
---

[Problem 039]\:

```scala
object Euler39 extends Application {
  def sols (perim :Int) = perim + 1000 * (for {
    a <- List.range(1, perim/2);
    b <- List.range(1, (perim-a)/2+1);
    c <- List(perim-a-b);
    if (a*a + b*b == c*c)
  } yield 1).length;
  println(List.range(3, 1000).map(sols).foldLeft(0)(Math.max) % 1000);
}
```
Nothing fancy here, just a moderately brute force search.



[Problem 039]: http://projecteuler.net/index.php?section=problems&id=39
