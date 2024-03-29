---
layout: post
title: "Euler 63"
date: 2010-08-21
---

[Problem 063]\: (<a href="http://github.com/samskivert/euler-scala/raw/master/Euler063.scala">source</a>):

```scala
object Euler063 extends EulerApp {
  def pows (n :Int) = Stream.from(1) prefixLength(p => BigInt(n).pow(p).toString.length == p)
  def answer = 1 to 9 map(pows) sum
}
```

The main observation here is that the number of digits of <em>a</em><sup><em>x</em></sup>, for
<em>a</em> ≥ 10, is guaranteed to exceed <em>x</em>. So we can restrict ourselves to looking only
at the numbers from 1 to 9. Furthermore, the number of digits of <em>a</em><sup><em>x</em></sup>,
for 1 ≤ <em>a</em> ≤ 9, will equal <em>x</em> up to some maximum <em>x</em>, and then be less than
<em>x</em> for all higher <em>x</em>. That enough <em>x</em>s for you?


[Problem 063]: http://projecteuler.net/index.php?section=problems&id=63
