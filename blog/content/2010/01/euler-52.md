---
layout: post
title: "Euler 52"
date: 2010-01-01
---

[Problem 052]\:

```scala
object Euler52 extends Application {
  def sort (n :Int) = n.toString.toList.sortWith(_<_).mkString.toInt
  def g (n :Int, sn :Int) = (2 to 6).forall(m => sort(n*m) == sn)
  def f (n :Int) = g(n, sort(n))
  println(Stream.from(1).find(f).get)
}
```

Happy New Year! What better way to ring in the new year than doing math problems while sitting on
the lanai under the Hawaiian sun? I'm already up to Euler 90, so I've got some catching up to do
with my posts.

This one's pretty straightforward: count up from one, checking each number for the property in
question. I first did this with a recursive function, but then switched to the more concise use of
`Stream`. I also rather extravagantly used a whole second function to avoid having to declare a
local variable and write curly braces.


[Problem 052]: http://projecteuler.net/index.php?section=problems&id=52
