---
layout: post
title: "Euler 028"
date: 2008-02-23
---

[Problem 028]\:

{% highlight scala %}
object Euler28 extends Application {
  def spiral (size :Int) :Int = {
    if (size == 1) return 1;
    val smaller = size-2;
    spiral(smaller) + 4*(smaller*smaller) + (1+2+3+4)*(smaller+1);
  }
  println(spiral(1001));
}
{% endhighlight %}
This is a slightly brevified version of my original solution which is a bit clearer:

{% highlight scala %}
  val smaller = size-2;
  val start = smaller*smaller;
  val step = smaller+1;
  return spiral(smaller) + ((start + 1*step) + (start + 2*step) +
                            (start + 3*step) + (start + 4*step));
{% endhighlight %}
Looking at the grid:

{% highlight scala %}
21 22 23 24 <b>25</b>
20  7  8  <b>9</b> 10
19  6  <b>1</b>  2 11
18  5  4  3 12
17 16 15 14 13
{% endhighlight %}
The bold numbers are the values for <b>start</b> and the step is the distance from the start position to the first corner of the next ring and from that corner to the next corner.



[Problem 028]: http://projecteuler.net/index.php?section=problems&id=28
