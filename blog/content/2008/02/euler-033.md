---
layout: post
title: "Euler 033"
date: 2008-02-25
---

[Problem 033]\:

{% highlight scala %}
object Euler33 extends Application {
  def equal (ratio :Double, rnum :Int, rdenom :Int) :Boolean = {
    (rdenom > 0) && (ratio == rnum/rdenom.doubleValue());
  }
  val fracs = for {
    denom <- List.range(10,100)
    num <- List.range(10,denom)
    if ((num%10 == denom/10 && equal(num/denom.doubleValue(), num/10, denom%10)) ||
        (num/10 == denom%10 && equal(num/denom.doubleValue(), num%10, denom/10)))
  } yield Pair(num, denom);
  val prod = fracs.foldRight(Pair(1,1))((b, a) => (Pair(b._1 * a._1, b._2 * a._2)));
  println(prod._2 / prod._1);
}
{% endhighlight %}
Nothing too clever here. I did realize that cancellable fractions would either be kn/mk or nk/km, and fortunately the product reduces to 1/100 so determining the denominator is simple.



[Problem 033]: http://projecteuler.net/index.php?section=problems&id=33
