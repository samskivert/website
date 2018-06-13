---
layout: post
title: "Euler 2⁶"
date: 2010-08-25
---

[Problem 064]\: (<a href="http://github.com/samskivert/euler-scala/raw/master/Euler064.scala">source</a>):

```scala
object Euler064 extends EulerApp {
  case class Root (root :Int, add :Int, div :Int) {
    def expand = {
      val term = ((math.sqrt(root) + add)/div).toInt
      val nadd = term*div - add
      (term, Root(root, nadd, (root - nadd*nadd)/div))
    }
    override def toString = "(√" + root + "+" + add + ")/" + div
  }
  def expansion (terms :List[Int], roots :List[Root]) :List[Int] = {
    val (term, root) = roots.head.expand
    if (root.div == 0 || roots.contains(root)) term :: terms
    else expansion(term :: terms, root :: roots)
  }
  def answer = 1 to 10000 map(n => expansion(Nil, Root(n, 0, 1)::Nil).length) count(_%2==0)
}
```

Here we have a nice little case class to represent a single step in the infinite expansion. The
process of generating the next expansion could be done purely with simple arithmetic, but I'm lazy,
so I just use `math.sqrt` to obtain the non-fractional part. I don't actually need to keep track of
the terms to obtain the solution, but it was handy to have when I was validating the solution, so I
kept it around. Same goes for `toString`. I just love that little root sign!

[Problem 064]: http://projecteuler.net/index.php?section=problems&id=64
