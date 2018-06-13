---
layout: post
title: "Euler 55"
date: 2010-01-01
---

[Problem 055]\:

```scala
object Euler55 extends EulerApp {
  def ispal (n :String) = n.take(n.length/2) == n.takeRight(n.length/2).reverse
  def islychrel (n :BigInt, iter :Int = 0) :Int =
    if (iter > 0 && ispal(n.toString)) 0
    else if (iter == 50) 1
    else islychrel(n + BigInt(n.toString.reverse), iter+1)
  println((1 to 9999).map(n => islychrel(n)).sum)
}
```

Nothing much to see here, other than that I took advantage of a couple of nice features of Scala
2.8 while cleaning this up for posting.

The first is the use of a default argument to `islychrel` so that when I call it non-recursively, I
don't have to manually specify 0 for the 0th iteration. I do still have to explicitly write
`n => islychrel(n)` because I'm taking advantage of the implicit conversion to BigInt. In theory I
could do `map(BigInt.apply).map(islychrel)` but, not surprisingly, that causes Scala's implicit
argument support to do le freak out.

The other nice 2.8ism I'm taking advantage of is that `RichString.take` and `RichString.takeRight`
both return `String`, so where I used to be calling `mkString` to get the faster string comparison,
I no longer need to do so. Yay!


[Problem 055]: http://projecteuler.net/index.php?section=problems&id=55
