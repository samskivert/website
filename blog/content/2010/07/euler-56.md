---
layout: post
title: "Euler 56"
date: 2010-07-26
---

[Problem 056]\:

```scala
object Euler56 extends EulerApp {
  def answer = (for { a <- 90 to 100; b <- 90 to 100 }
                yield BigInt(a).pow(b).toString.map(_-'0').sum) max
}
```
I'm in Switzerland at the moment, so my mind naturally turns to <a href="http://projecteuler.net/">Project Euler</a>.

I have made some changes to my problem harness. Previously, I relied on Scala's Application class's jiggery pokery to stuff your whole program into a static initializer and execute it. This interacts poorly with a HotSpot optimization optimization which dictates that it not optimize code run in a static initializer. This led me to stick some of my solutions in a `main` method, which grated against my delicate sensibilities. Now EulerApp defines an abstract `answer` method to be implemented by the solution class. It then runs said method in a standard `main` method, yielding optimized (just-in-time) compilation and a reasonable aesthetic to boot.

This particular problem is pretty simple. We rely on `BigInt` to do the heavy lifting, and simply do a brute force scan of the likely candidates (values between 90<sup>90</sup> and 100<sup>100</sup>).


[Problem 056]: http://projecteuler.net/index.php?section=problems&id=56
