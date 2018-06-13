---
layout: post
title: "Euler 043"
date: 2008-07-19
---

[Problem 043]\:

```scala
object Euler43 extends Application {
  def compsum (n :Long, digits :List[Int], divs :List[Int], sum :Long) :Long = {
    if (digits.isEmpty) n + sum
    else (for { d <- digits; val nn = n*10 + d; if ((nn%1000) % divs.head == 0) }
          yield compsum(nn, digits-d, divs.tail, sum)).foldLeft(0L)(_+_)
  }
  def digits (n :Int) = n.toString.toList.map(_-'0')
  def norepeats (n :Int) = digits(n).removeDuplicates == digits(n)
  def compute (s :Int) = compsum(s, 0.to(9).toList -- digits(s), List(2, 3, 5, 7, 11, 13, 17), 0)
  println(100.to(999).filter(norepeats).map(compute).reduceLeft(_+_))
}
```
I first tackled this one by generating all permutations of the digits zero through nine and checking to see whether they met the divisibility requirements, but that took a bit too long. Then I switched to this approach which builds up the solutions one digit at a time, pruning all values that don't preserve our requirements. We start with all three digit numbers that don't have repeating digits (I cheat a bit here and ignore any less than 100 as those are tricker to compute and the solution contains none of them), then we tack on each of the remaining digits in turn and make sure the trailing three digits are divisible by two. Then we move on to the next digit, and so on. This runs substantially faster (460ms as opposed to 120000ms) but the code is a bit harder to follow.



[Problem 043]: http://projecteuler.net/index.php?section=problems&id=43
