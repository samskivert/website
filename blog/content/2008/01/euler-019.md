---
layout: post
title: "Euler 019"
date: 2008-01-18
---

[Problem 019]\:

```scala
object Euler19 extends Application {
  def norm (days :Int) :Function1[Int,Int] = ((year :Int) => (days));
  def leap (days :Int) :Function1[Int,Int] = ((year :Int) => {
    if ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0)) days+1;
    else days;
  });
  val length = Array(norm(31), leap(28), norm(31), norm(30), norm(31), norm(30),
                     norm(31), norm(31), norm(30), norm(31), norm(30), norm(31));

  println((for (year <- List.range(1900, 2001); month <- List.range(0, 12))
           yield Pair(year, month)).foldLeft(Pair(0, 0))(
             (acc :Pair[Int,Int], cur :Pair[Int,Int]) => (
               Pair(if (cur._1 > 1900 && acc._2 % 7 == 6) 1+acc._1 else acc._1,
                    acc._2 + length(cur._2)(cur._1))))._1);
}
```
This is a fine example of one of the big problems with functional languages. One is tempted to write ridiculous code like the above.

My first offense was minor. I started with a function that computed the length of a given month for a specified year like so:

```scala
  val monlens = Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
  def length (year :Int, month :Int) :Int = {
    if ((month == 1) && (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0))
      return monlens(month)+1;
    else
      return monlens(month);
  }
```
Then I decided to be clever and replace that with an array of functions each of which returned the number of days in the month in question when called with a particular year. This points toward the reason I suspect Odersky uses parenthesis for array dereference:

```scala
length(cur._2)(cur._1)
```
That's actually an array dereference followed by a function call.

But then I went overboard and replaced my perfectly reasonable iterative solution:

```scala
  var days = 0;
  var firstSundays = 0;
  for (year < - List.range(1900, 2001)) {
    for (month <- List.range(0, 12)) {
      if (year > 1900 && days % 7 == 6) {
        firstSundays = firstSundays + 1;
      }
      days += length(month)(year);
    }
  }
  println(firstSundays);
```
with the single expression monstrosity you see above.

Back when I was learning functional programming with SML, we used to joke that you could implement any algorithm in one line with foldl. That may indeed be true, but readability ends up sacrificed and bleeding on the floor as a result.



[Problem 019]: http://projecteuler.net/index.php?section=problems&id=19
