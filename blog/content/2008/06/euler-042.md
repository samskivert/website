---
layout: post
title: "Euler 042"
date: 2008-06-01
---

[Problem 042]\:

```scala
object Euler42 extends EulerApp {
  def wvalue (word :Seq[Char]) = word.foldLeft(0)((s, l) => (s + (l - 'A' + 1)))
  def tri (n :Int) = (n*(n+1))/2
  println(readwords("words.txt").map(wvalue).filter(v => (v == tri(Math.sqrt(2*v)))).length);
}
```

This one is kind of cheating. I discovered (using the terminology `tri = (n *
(n+1))/2)` that `floor(sqrt(2*tri)) == n` for at least the first 10,000 values
of n. This isn't hugely surprising because `2*tri = (n*n + n)`, so the square
root of that is always going to be a bit bigger than n, and floor of that could
well take us right to the integer we need. Who knew?

It may be true for larger values of n but BigInt doesn't have sqrt() so I
didn't feel like going through the pain of checking. Certainly the biggest n we
encounter in words.txt is way smaller than 10,000 (it is in fact 19).

[Problem 042]: http://projecteuler.net/index.php?section=problems&id=42
