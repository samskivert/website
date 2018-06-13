---
layout: post
title: "Euler 040"
date: 2008-06-01
---

[Problem 040]\:

```scala
object Euler40 extends Application {
  val digits = List.range(1, 200000).flatMap(n => n.toString.toList).map(_-'0');
  println(List(0, 9, 99, 999, 9999, 99999, 999999).foldRight(1)((idx, b) => b * digits(idx)));
}
```
Another straightforward solution that's nicely accommodated by our basic tools. Generate a list of the first 200,000 integers, split those into lists of each individual integer's digits and flatmap them into one contiguous list. Then extract the digits at our desired indices and multiply them. It would have been more efficient to only do the character to integer conversion for the values we are multiplying, but then things didn't fit so nicely onto two lines.

Had I had more indices to extract, I might have written:

```scala
  println(List.range(0, 6).foldRight(1)((idx, b) => b * digits(Math.pow(10, idx).intValue-1)));
```
but the whole Math.pow and intValue business felt like too much ugly Java intrusion.



[Problem 040]: http://projecteuler.net/index.php?section=problems&id=40
