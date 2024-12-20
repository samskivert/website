---
layout: post
title: "Euler 015"
date: 2008-01-17
---

[Problem 015]\:

```scala
object Euler15 extends Application {
  val size = 20;
  def fact (n: BigInt): BigInt = if (n == 0) 1 else n * fact(n - 1)
  println(fact(size * 2) / (fact(size) * fact(size)))
}
```
I first started thinking about this as a caching problem, having just finished problem 14 which was reduced to a reasonable runtime by caching. At any coordinate in the grid, there are some number of unique paths from that position to the destination, so one could start in the bottom right and traverse the graph breadth first from the end to the start, caching the number of paths at each intersection and using that to efficiently compute the number of paths as you made your way to the start. I implemented this solution in Java and indeed 841 calls to a recursive function later I had my solution.

Then I realized that every path from the start to the finish is the same length and a series of an equal number of horizontal and vertical segments (20 horizontal and 20 vertical). Thus each unique path was a unique arrangement of those horizontal and vertical segments. If we consider the 40 positions in our path to be distinct elements then the problem becomes how many ways can we choose 20 of our positions in the path to contain horizontal segments (or vertical segments if you swing that way), which any good computer scientist knows is 40 choose 20: 40! / 20! (40 - 20)!.

The funny thing is that the caching solution in Java runs in less than 1 millisecond whereas computing the factorial in Scala above requires 2 milliseconds. It isn't a tail recursive factorial function though, so that and the BigInt math probably take up all the time.



[Problem 015]: http://projecteuler.net/index.php?section=problems&id=15
