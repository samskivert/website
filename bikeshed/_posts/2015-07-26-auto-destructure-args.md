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
