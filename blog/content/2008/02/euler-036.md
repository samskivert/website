---
layout: post
title: "Euler 036"
date: 2008-02-25
---

[Problem 036]\:

```scala
object Euler36 extends Application {
  def ispal (n :String) = n.reverse.sameElements(n)
  def bothpal (n :Int) = ispal(n.toString) && ispal(n.toBinaryString)
  println(List.range(1, 1000000, 2).filter(bothpal).foldRight(0)(_+_));
}
```
Brief, readable and reasonably efficient, yay for Scala. The only cleverness here is to realize that binary palindromes must be odd.



[Problem 036]: http://projecteuler.net/index.php?section=problems&id=36
