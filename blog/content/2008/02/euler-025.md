---
layout: post
title: "Euler 025"
date: 2008-02-03
---

[Problem 025]\:

```scala
object Euler25 extends Application {
  var prev2 :BigInt = 1;
  var prev1 :BigInt = 1;
  var value :BigInt = prev2 + prev1;
  var term = 3;
  while (value.toString.length < 1000) {
    prev2 = prev1;
    prev1 = value;
    value = prev1 + prev2;
    term += 1;
  }
  println(term);
}
```
Another unglamorous solution wherein all the hard work is done by BigInt.



[Problem 025]: http://projecteuler.net/index.php?section=problems&id=25
