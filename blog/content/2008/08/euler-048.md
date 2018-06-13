---
layout: post
title: "Euler 048"
date: 2008-08-11
---

[Problem 048]\:

```scala
object Euler48 extends Application {
  println(1.to(1000).map(a => BigInt(a).pow(a)).reduceRight(_+_) % BigInt(10).pow(10))
}
```
The Right Thing ™ here was to not rely on the magic of BigInt, but to do the addition modulo 10^10 as well as to compute the powers modulo 10^10, but laziness and the allure of a one line solution won out in the end.



[Problem 048]: http://projecteuler.net/index.php?section=problems&id=48
