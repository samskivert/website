---
layout: post
title: "Euler 030"
date: 2008-02-23
---

[Problem 030]\:

```scala
object Euler30 extends Application {
  def digits (n :Int) = n.toString.toList.map(c => c - '0');
  def issum5 (n :Int) = digits(n).map(a => Math.pow(a, 5).intValue()).foldRight(0)(_+_) == n;
  println(List.range(2, 200000).filter(issum5).foldRight(0)(_+_));
}
```
A decent balance of compactness and readability, IMHO. I would like to do away with the 200,000 upper limit which I came to via some preparatory noodling.



[Problem 030]: http://projecteuler.net/index.php?section=problems&id=30
