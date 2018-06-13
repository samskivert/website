---
layout: post
title: "Euler 001"
date: 2008-01-09
---

I've been doing the <a href="http://www.projecteuler.net">Project Euler</a> problems in <a href="http://scala-lang.org">Scala</a> both because they're fun and as an excuse to play more with Scala and to remind myself of how much fun it is to write functional programs.

I figured it would be fun to post my solutions, inelegant as they may be. Anyone also doing the problems will have to avert their eyes if they see a solution to a problem they've not yet solved.

Now, on to [Problem 001]\:

{% highlight scala %}
object Euler1 extends Application {
  def div3or5 (from: Int, to: Int): List[Int] = {
    for (i <- List.range(from, to) if i % 3 == 0 || i % 5 == 0) yield i
  }
  println(div3or5(0, 1000).foldLeft(0)(_+_))
}
{% endhighlight %}

[Problem 001]: http://projecteuler.net/index.php?section=problems&id=1
