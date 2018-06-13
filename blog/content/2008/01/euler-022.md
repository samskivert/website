---
layout: post
title: "Euler 022"
date: 2008-01-18
---

[Problem 022]\:

```scala
import scala.io.Source;
object Euler22 extends Application {
  def trim (name :String) = name.slice(1, name.length-1);
  val names = Source.fromFile("names.txt").getLine(0).split(',').map(trim).toList.sort(_<_);
  def score (name :String) = name.foldLeft(0)((s, l) => (s + (l - 'A' + 1)))
  println(List.range(0, names.length).map(i => ((i+1) * score(names(i)))).foldLeft(0)(_+_));
}
```
Another reasonably legible solution. I resisted the urge to do it all in a single expression.

I can see how language designers might succumb to the urge to expose a loop counter in their iteration functions because it would be nice to do:

```scala
  println(names.map(name => ((_INDEX_+1) * score(name))).foldLeft(0)(_+_));
```

[Problem 022]: http://projecteuler.net/index.php?section=problems&id=22
