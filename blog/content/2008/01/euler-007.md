---
layout: post
title: "Euler 007"
date: 2008-01-09
---

[Problem 007]\:

```scala
object Euler7 extends Application {
  var numbers = List.range(2,110000);
  var primes = List(2);
  while (primes.length < 10001) {
    val prime = primes.head;
    numbers = numbers.filter((b) => (b%prime != 0));
    primes = numbers.head :: primes;
  }
  println(primes.head);
}
```
My numbering system encouraged the incorporation of a certain British Secret Service Agent into my solution, but imagination failed me.



[Problem 007]: http://projecteuler.net/index.php?section=problems&id=7
