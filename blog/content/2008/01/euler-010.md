---
layout: post
title: "Euler 010"
date: 2008-01-12
---

[Problem 010]\:

```scala
object Euler10 extends Application {
  var numbers = List.range(2,1000000).toArray;
  def sumprimes (idx :Int, sum :Long) :Long = {
    var prime = numbers(idx);
    for (midx <- List.range(idx, numbers.length, prime)) {
      numbers(midx) = 0;
    }
    val nidx = numbers.slice(idx, numbers.length).findIndexOf((a) => (a != 0));
    if (nidx == -1) return sum + prime;
    else return sumprimes(idx + nidx, sum + prime);
  }
  println(sumprimes(0, 0));
}
```
This one felt like it could have been solved more concisely given more thought. But who has time for more thought these days?



[Problem 010]: http://projecteuler.net/index.php?section=problems&id=10
