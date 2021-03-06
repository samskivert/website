---
layout: post
title: "Euler 60"
date: 2010-07-27
---

[Problem 060]\:

```scala
object Euler60 extends EulerApp {
  val primes = genprimes(10000) filter(0.!=)
  val ppairs = Set() ++
    (for { ii <- 0 until primes.length-1; jj <- ii until primes.length;
          val pi = primes(ii); val pj = primes(jj);
          if (isprime((pi.toString+pj).toInt) && isprime((pj.toString+pi).toInt))
    } yield (pi, pj))
  def isset (pset :List[Int], prime :Int) =
    pset.foldLeft(true)((b, a) => b && ppairs((a, prime)))
  def find (pset :List[Int], plist :List[Int]) :Option[List[Int]] = {
    if (pset.size == 5) Some(pset)
    else if (plist.isEmpty) None
    else if (!isset(pset, plist.head)) find(pset, plist.tail)
    else find(plist.head :: pset, plist.tail).orElse(find(pset, plist.tail))
  }
  def answer = find(Nil, primes.toList).get.sum
}
```
This one's not particularly concise, but it's fairly readable (or at least I think so since I wrote the code just shy of two years ago and it wasn't too hard to regrok it in order to write this blog post).

First we generate a “reasonable" number of primes (which is cheating a little, but also really simplifies things). Then we precompute which of those primes pair with one another so that we can check a given pair of primes for pair-ness quickly later. Being able to concisely turn a sequence into a set, and having structurally-comparable tuples for free is so nice.

Now we climb up the list of primes, each time checking whether the current prime can expand our current set of concatenable primes. This check only requires that the candidate prime properly pair with each prime in the set, since we already know that all the primes in the set pair with one another (because they were each checked before they were added). If it does pair, add it to the set and either way we continue with the next prime. If we reach the end of our list of primes and we haven't found a set of five, we backtrack (making use of the call stack) and execute the `orElse` and try again without that most recently added pairable prime.

When we first start out, our set is empty, so the first prime trivially pairs with our existing empty set and is added to the set. When that turns out not to yield a set of five concatenable primes, we pop that first prime off and try the second prime, and so on down the line.


[Problem 060]: http://projecteuler.net/index.php?section=problems&id=60
