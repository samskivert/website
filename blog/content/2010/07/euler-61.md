---
layout: post
title: "Euler 61"
date: 2010-07-27
---

[Problem 061]\:

```scala
object Euler61 extends EulerApp {
  case class Pn (card :Int, value :Int) {
    def valid = (value < 10000) && (value > 999)
    def ab = value / 100
    def cd = value % 100
  }

  def tri (n :Int) = Pn(3, n*(n+1)/2)
  def square (n :Int) = Pn(4, n*n)
  def pent (n :Int) = Pn(5, n*(3*n-1)/2)
  def hex (n :Int) = Pn(6, n*(2*n-1))
  def hept (n :Int) = Pn(7, n*(5*n-3)/2)
  def oct (n :Int) = Pn(8, n*(3*n-2))
  def gen (max :Int)(gen :(Int) => Pn) = (1 to max) map(gen) filter(_.valid)
  val nums = List(square _, pent _, hex _, hept _, oct _) flatMap(gen(100))

  def search (nums :Seq[Pn], set :List[Pn]) :Option[List[Pn]] = {
    if (!nums.isEmpty) (for (n <- nums; if (n.ab == set.last.cd);
                             s <- search(nums filter(_.card != n.card), set :+ n))
                        yield s) headOption
    else if (set.last.cd == set.head.ab) Some(set)
    else None
  }
  def sets = for (n <- gen(150)(tri); s <- search(nums, n::Nil)) yield s
  def answer = (sets head) map(_.value) sum
}
```

I model a polygonal number with a case class to track it's value and cardinality. Since I care
about four-digitality and two digit prefixes and suffixes, those fit nicely as methods on the
class. The main search proceeds by starting with a given triangle number (since all sets must have
one triangular element), and then looks for a number that matches the suffix of said number. If one
is found, it is added to the ordered set, all other numbers with the same cardinality are dropped
from the candidates list, and a new number is sought that matches the suffix of the value just
added.

If we match a number for each cardinality, then we'll finally end up in the `search` procedure with
an empty candidates list, in which case we check whether the last number in the ordered set matches
the first.

Functional programming comment: when I first wrote this, I was using `Nil` to indicate a non-match,
and had a few `find(Nil.!=)` calls sprinkled around to find matching elements. That smelled
suspiciously like using `null`, so I decided to be a good functional programmer and use `Option`.
However, this left me in situations where I'd have a `List[Option[Pn]]` and I'd need the first
non-`None` element. I know that I can take such a list and do `list flatMap(x => x) head` but that
seems insufficiently scrutable to people not familiar with the idiom.

This motivated me to switch to for-comprehensions. Instead of writing:

```scala
nums filter(_.ab == set.last.cd) map(n => search(...)) flatMap(s => s) headOption
```

I use what you see above:

```scala
(for (n <- nums; if (n.ab == set.last.cd); s <- search(...)) yield s) headOption
```

The for-comprehension turns out to be a little shorter, a lot easier to line-wrap (if needed), and
I think probably slightly more comprehensible. You still have to deduce that `Option` behaves like a
zero-or-one element list when used in a for-comprehension, but that seems more intuitive than
figuring out that `List(Some(1), None, Some(2)) flatMap(x => x)` gives you `List(1, 2)`.

[Problem 061]: http://projecteuler.net/index.php?section=problems&id=61
