---
layout: post
title: "Euler 031"
date: 2008-02-23
---

[Problem 031]\:

{% highlight scala %}
object Euler31 extends Application {
  def perms (remain :Int, coins :List[Int]) :Int = {
    if (remain == 0) return 1;
    else if (coins == Nil) return 0;
    else List.range(0, remain/coins.head+1).map(
      q => perms(remain - q*coins.head, coins.tail)).foldRight(0)(_+_);
  }
  println(perms(200, List(200, 100, 50, 20, 10, 5, 2, 1)));
}
{% endhighlight %}
Nothing too fancy here, just enumerate all legal quantities of the first coin in the list (including zero), then recurse with that coin removed from the list and its value times the number chosen removed from the sum.



[Problem 031]: http://projecteuler.net/index.php?section=problems&id=31
