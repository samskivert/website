---
layout: post
title: "Euler 038"
date: 2008-02-25
---

[Problem 038]\:

{% highlight scala %}
object Euler38 extends Application {
  def ispan (n :String) = n.toList.sort(_<_).mkString == "123456789";
  def catprod (n :Int, r :Int) = List.range(1, r+1).flatMap(p => (n*p).toString.toList)
  def find (n :Int, r :Int, max :Int) :Int = {
    val cp = catprod(n, r);
    if (cp.length > 9)
      if (r == 2) max
      else find(n, r-1, max);
    else
      if (ispan(cp)) find(n+1, r, Math.max(cp.mkString.toInt, max))
      else find(n+1, r, max);
  }
  println(find(1, 5, 0));
}
{% endhighlight %}
I wrote the above general solution which computes the concatenated product with (1, 2, 3, 4, 5) for increasing values of n until the product exceeds 9 digits, then moves down to (1, 2, 3, 4) and so on. Once I had my answer, I read the forums and discovered that a bit of thinking would have led me to the knowledge that the solution had to be a four digit value multiplied by (1, 2). Even less thinking would have made me realize that the solution had to be larger than the example given in the problem statement. Fortunately I did none of that thinking and got to write an interesting algorithm. A more thoughtful me might have written the following:

{% highlight scala %}
object Euler38 extends Application {
  def ispan (n :String) = n.toList.sort(_<_).mkString == "123456789";
  println(List.range(9182,9876).reverse.map(n => n+""+(2*n)).filter(ispan).head);
}
{% endhighlight %}

[Problem 038]: http://projecteuler.net/index.php?section=problems&id=38
