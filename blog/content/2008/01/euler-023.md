---
layout: post
title: "Euler 023"
date: 2008-01-19
---

[Problem 023]\:

{% highlight scala %}
import scala.collection.mutable.Set;

object Euler23 extends Application {
  def sumdiv (x :Int) :Int = (1 :: List.flatten(for {
    divis <- List.range(2, Math.sqrt(x)+1)
    if x % divis == 0
  } yield List(divis, x / divis).removeDuplicates)).foldLeft(0)(_+_);

  val max = 28123;
  val abundant = List.range(1, max+1).filter(a => (a < sumdiv(a))).toArray;

  def filter (abund :Array[Int], ints :Set[Int], a :Int, b :Int) :Set[Int] = {
    ints -= (abund(a) + abund(b));
    if (b == abund.length-1)
      if (a == abund.length-1) return ints;
      else return filter(abund, ints, a+1, 0);
    else return filter(abund, ints, a, b+1);
  }
  println(filter(abundant, Set() ++ List.range(1, max+1), 0, 0).foldLeft(0)(_+_));
}
{% endhighlight %}
This was an annoying excercise in trying to get Scala not be idiotic about performance. Instead of that cumbersome recursive function, I wanted to simply write:

{% highlight scala %}
   var integers = Set() ++ List.range(1, max+1);
   for (a <- List.range(0, abundant.length);
        b <- List.range(a, abundant.length))
     integers -= (abundant(a) + abundant(b));
   println(integers.foldLeft(0)(_+_));
{% endhighlight %}
However, for some reason that took 164 seconds to execute. I wrote the same algorithm in Java which took about 450ms to execute. Clearly Scala was doing something ridiculous.

Looking at the decompiled byte codes we see where Scala gets into trouble. The for-comprehension approach generates code that looks plus or minus like so:

{% highlight scala %}
public Euler23 () {
    // ... compute abundant, etc.
    List.range(0, abundant().length).foreach(new AnonFunc() {
        public void apply (Object value) { apply(unbox(val)); }
        public void apply (int a) {
            List.range(a, abundant().length).foreach(new AnonFunc() {
                public void apply (Object value) { apply(unbox(val)); }
                public void apply (int b) {
                    integers().remove(box(abundant()[a] + abundant()[b]));
                }
            });
       }
    });
    // ... foldLeft, print sum, etc.
}

public int[] abundant () { return abundant; }
public Set integers () { return integers; }

private int[] abundant;
private Set integers;
{% endhighlight %}
So each time through the outer loop, we're creating a new anonymous function (probably not a huge deal, abundant.length is only ~6600) and each time through the inner loop we're accessing “integers" and “abundant" through non-final public method calls.

After seeing this, I suspected that if I passed “abundant" and “integers" to a recursive function call, it would pass those references directly rather than access them through a public method call. This would reduce the inner loop to a single recursive function call. As it turns out, Scala did me one better as you can see below:

{% highlight scala %}
public Set filter (int[] abund, Set ints, int a, int b) {
    do {
        ints.remove(box(abund[a] + abund[b]));
        if (b == abund.length - 1) {
            if (a == abund.length - 1)
                return ints;
            b = 0;
            a = a + 1;
        } else {
            b++;
        }
    } while(true);
}
{% endhighlight %}
Now it's exceedingly clear why this code runs in ~2400ms compared to the ~164000ms of the for-comprehension-based code. Not only does Scala indeed pass the array and set directly, but it determined that my function was tail-recursive and turned it into a while loop.

The moral of the story here is perhaps not to map in your head “val something :Int" in Scala to “final int value" in Java because that's clearly not what the compiler is doing. Perhaps using Scala in a more object oriented way by declaring classes and members would avoid this funny business. Using Application and writing my solution directly therein may result in semantics I neither need nor expect.



[Problem 023]: http://projecteuler.net/index.php?section=problems&id=23
