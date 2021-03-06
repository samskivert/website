---
layout: post
title: "Euler 62"
date: 2010-08-21
---

[Problem 062]\: (<a href="http://github.com/samskivert/euler-scala/raw/master/Euler062.scala">source</a>):

```scala
object Euler062 extends EulerApp {
  def search (n :Long, cubes :Map[Long,List[Long]]) :Long = {
    val cube = n*n*n
    val key = cube.toString.sortWith(_>_).toLong
    val perms = cube :: cubes.getOrElse(key, Nil)
    if (perms.length == 5) perms.last
    else search(n+1, cubes + (key -> perms))
  }
  def answer = search(1, Map())
}
```

I was originally using the dreaded mutation here, and then realized that I could easily rewrite the
code to use an immutable map, making it about 10% shorter and about 10% slower. Of course, the
garbage collector gets a great big free ride when the program terminates and it most certainly
hasn't collected any of the intermediate map bits.

The algorithm is pretty straightforward, iterate up the cubes, collecting them into lists of cubes
which permute to one another (I bet there's some fancy math term for lists that are equivalent up
to permutation). When we find one with five permutations, we're done. We're sort of cheating here
because it would be possible to find a cube that permutes to four other seen cubes, but also
permutes to as yet unseen cubes, and the problem asks for the smallest cube for which
<em>exactly</em> five permutations of its digits are a cube. Fortunately, we find the fifth
permutation of the correct cube before we find the fifth permutation of any cube that has more
permutations, so we're in the clear.

[Problem 062]: http://projecteuler.net/index.php?section=problems&id=62
