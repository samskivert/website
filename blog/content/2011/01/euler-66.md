---
layout: post
title: "Euler 66"
date: 2011-01-14
---

[Problem 066](http://projecteuler.net/index.php?section=problems&id=66):
(<a href="http://github.com/samskivert/euler-scala/raw/master/Euler066.scala">source</a>):

```scala
import scala.collection.mutable.{Map => MMap}
object Euler066 extends EulerApp {
  def leastx (d :Int) = {
    val amap = MMap[Int,BigInt]()
    val pmap = MMap[Int,BigInt]()
    val qmap = MMap[Int,BigInt]()
    val ppmap = MMap[Int,BigInt]()
    val qqmap = MMap[Int,BigInt]()

    def a (i :Int) :BigInt = amap.getOrElseUpdate(i, i match {
      case 0 => math.sqrt(d).toInt
      case _ => (a(0) + pp(i))/qq(i)
    })

    def p (i :Int) :BigInt = pmap.getOrElseUpdate(i, i match {
      case 0 => a(0)
      case 1 => a(0)*a(1) + 1
      case _ => a(i)*p(i-1) + p(i-2)
    })

    def q (i :Int) :BigInt = qmap.getOrElseUpdate(i, i match {
      case 0 => 1
      case 1 => a(1)
      case _ => a(i)*q(i-1) + q(i-2)
    })

    def pp (i :Int) :BigInt = ppmap.getOrElseUpdate(i, i match {
      case 0 => 0
      case 1 => a(0)
      case _ => a(i-1)*qq(i-1) - pp(i-1)
    })

    def qq (i :Int) :BigInt = qqmap.getOrElseUpdate(i, i match {
      case 0 => 1
      case 1 => BigInt(d) - a(0)*a(0)
      case _ => (BigInt(d) - pp(i)*pp(i))/qq(i-1)
    })

    val as = Stream.from(0) map(a)
    val r = (as.tail takeWhile(ai => ai != as.head*2) length)
    if (r % 2 == 1) p(r) else p(2*r+1)
  }

  def square (i :Int) = i*i
  def issquare (d :Int) = square(math.sqrt(d).toInt) == d
  def answer = (2 to 1000 filterNot(issquare) map(d => (d, leastx(d))) sortBy(_._2) last)._1
}
```

My efforts to post my Euler solutions with commentary remain woefully behind (I just finished
problem 110), but when I took action to close this gap, I discovered that I had skipped over
problem 66 so long ago. I recall being frustrated after beating my head against it for a while and
making no headway.

Filled with renewed vigor, I tackled it again here under the invigorating Mexican sunshine. Alas,
the sunshine proved to be of little help. I spent a long time wrangling with the equation in the
form <em>y² = (x+1)(x-1)/D</em>. This led me to explore the family of solutions <em>nD + 1</em> and
<em>nD – 1</em>, which are very nice, but definitely not minimal.

I then played around with factoring <em>D</em> and trying to come up with some way to efficiently
search for potential solutions using those factors, since <em>D</em> has to divide evenly into
<em>(x + 1)(x – 1)</em>. But it needn't do so in any convenient way. For example, for <em>D =
28</em>, the minimal solution is <em>(127 + 1)(127 – 1)/28</em> which factors into <em>2 · 2 · 2 ·
2 · 2 · 2 · 2 × 2 · 3 · 3 · 7 / 7 · 2 · 2</em>. Some of the factors go into the left numerator and
some in the right, it's mayhem.

Eventually I decided to do some Googling and discovered that this is closely related to the
well-known <a href="http://mathworld.wolfram.com/PellEquation.html">Pell equation</a>. For reasons
not entirely clear to me, its solutions lie in the continued fraction expansion of the square root
of <em>D</em>.

Implementing the “usual recurrence relations" was pretty straightforward, though a bit of
memoization was needed (the unsightliness of that part of the code annoys me, but I am annoyed
enough with this problem already that I don't feel like twiddling it further). <code>BigInt</code>
also turned out to be necessary, as the largest “minimal" <em>x</em> is
<em>16421658242965910275055840472270471049</em>, which doesn't quite fit into 64 bits.
