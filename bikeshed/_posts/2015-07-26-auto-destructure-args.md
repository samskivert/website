---
layout: post
title: Automatic destructuring of arguments
---

I can't count the number of times I've created overloaded methods to destructure a function
argument and call a version that takes arguments individually. This is most prevalent in code that
takes a `Point` or `Vector`, like so:

```scala
case class Vector (x :Float, y :Float)

class Transform {
  def translate (tx :Float, ty :Float) = ...
  def translate (t :Vector) = translate(t.x, t.y)
  // ...
}
```

but there are plenty of other situations where I end up doing the same thing. Here are some
examples from [Scaled]:

```scala
case class Region (start :Loc, end :Loc)

class BufferV {
  // ...
  def region (start :Loc, until :Loc) :Seq[Line] = ...
  def region (r :Region) :Seq[Line] = region(r.start, r.end)
  // ...
}

class Buffer extends BufferV {
  // ...
  def delete (start :Loc, until :Loc) :Seq[Line]
  def delete (r :Region) :Seq[Line] = delete(r.start, r.end)

  def replace (start :Loc, until :Loc, lines :Ordered[LineV]) :Loc
  def replace (r :Region, lines :Ordered[LineV]) :Loc = replace(r.start, r.end, lines)

  def transform (start :Loc, until :Loc, fn :Char => Char) :Loc
  def transform (r :Region, fn :Char => Char) :Loc = transform(r.start, r.end, fn)
  // ...
}
```

Some from a game I'm working on:

```java
  public Facility facility (Coord coord) { return _facs[index(coord)]; }
  public Facility facility (int x, int y) { return _facs[index(x, y)]; }

  public Stack stack (Coord coord) { return _stacks[index(coord)]; }
  public Stack stack (int x, int y) { return _stacks[index(x, y)]; }

  public boolean scouted (Coord coord) { return _scouted[index(coord)]; }
  public boolean scouted (int x, int y) { return _scouted[index(x, y)]; }
```

You get the idea.

If we have ADTs (Scala calls them `case` classes), or value classes/structs, it seems perfectly
reasonable to automatically destructure an one passed to a function which takes arguments of the
same type and in the same order.

```scala
val tx = new Transform(...)
val dt = Vector(...)
tx.translate(dt) // desugars into tx.translate(dt.x, dt.y)
```

This should work even if there are additional arguments to the function:

```scala
val buffer :Buffer = ...
val r = Region(start, end)
buffer.replace(r, Seq(Line("Yes"), Line("we"), Line("can")))
// desugars into buffer.replace(r.start, r.until, Seq(...))
```

I'm inclined to match purely on declaration order and type, because name matching seems likely to
fall prey to minor quibbles like `translate` taking `tx` and `ty` whereas `Vector` declares `x` and
`y`. However, this does restrict us to only desugaring "struct-like" types, which are unambiguously
meant to be an ordered bundle of values.

I would like to allow arbitrary types to opt-into this behavior, not least because my `Region`
example up there was cheating. The actual definition of `Region` is:

```scala
trait Region {
  def start :Loc
  def end :Loc
  def contains (loc :Loc) :Boolean = (start <= loc) && (loc < end)
  def isEmpty = start >= end
}
```

For a variety of reasons, I didn't want it to be a case class, but I sure wish I didn't have to
dozens of overloaded methods to manually destructure a `Region`.

I use another pattern in my [Java geometry library] which would fail in these circumstances as
well:

```java
interface XY {
  float x ();
  float y ();
}

class Point implements XY {
  public float x, y;
  public float x () { return x; }
  public float y () { return y; }
  // ...
  public Point set (float x, float y) { ... }
  public Point set (XY p) { return set(p.x(), p.y()); }
}

class Vector implements XY {
  public float x, y;
  public float x () { return x; }
  public float y () { return y; }
  // ...
  public Vector set (float x, float y) { ... }
  public Vector set (XY p) { return set(p.x(), p.y()); }
}
```

The `XY` interface makes it easy to express that you don't care whether your x,y pair is actually a
`Point` or a `Vector` (or any other damned thing that has an x and y), which is very useful. But
how do I know that it's OK to destructure an `XY`?

Probably some kind of annotation like `@Value` which, when placed on a class or interface means that
all public methods represent destructurable values:

```java
@Value
interface XY {
  float x ();
  float y ();
}
```

and when placed on fields/methods means that those fields/methods comprise the value part of the
class:

```scala
trait Region {
  @value def start :Loc
  @value def end :Loc
  def contains (loc :Loc) :Boolean = (start <= loc) && (loc < end)
  def isEmpty = start >= end
}
```

I can already imagine situations where this would cause trouble. Imagine I have two traits, both
annotated with `@Value` which are equivalent from the perspective of the types of their fields and
their order:

```scala
@value trait XY {
  def x :Float
  def y :Float
}

@value trait PolarVec {
  def angle :Float
  def length :Float
}

case class Impulse (x :Float, y :Float, angle :Float, length :Float)
  extends XY with PolarVec
```

If I pass an `Impulse` to `def translate (tx :Float, ty :Float)` how do I know which value to use
when destructuring (and for that matter, does a value trait override the value nature of the case
class itself)?

Maybe the answer is just to fail, report the ambiguity, and be happy that the solution helps the
programmer 90% of the time. One can always be explicit and write `translate(imp.x, imp.y)`.

[Scaled]: https://github.com/scaled/scaled
[Java geometry library]: https://github.com/samskivert/pythagoras
