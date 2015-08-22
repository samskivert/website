Make it really easy to insert debug tracing. For example:

```scala
@trace def foo (bar :Int, baz :Thing) { ... }
```

causes all calls to foo() to be logged thusly:

```
foo(<bar>, <baz>)
```

where `<bar>` and `<baz>` are either structural stringifications of the type in question or if it
implements Traceable (interface or type class or whatever) then that custom stringification.

Ditto for tracing a specific call:

```scala
def bar () {
  // ...
  @trace foo(24, Thing("quux", 42))
  // ..
}
```

The annotation could support a string for basic disambiguation:

```scala
def baz () {
  // ...
  @trace("1st") foo(24, Thing("quux", 42))
  @trace("2nd") foo(12, Thing("bippy", 1))
  @trace("3rd") foo(99, Thing("pork", -2))
  // ..
}
```

The string would be prepended to the trace in some useful way:

```
1st: foo(24, Thing(quux, 42))
2nd: foo(12, Thing(bippy, 1))
3rd: foo(99, Thing(pork, -2))
```

This could easily be done with a javac plugin today and indeed hypothetical language would use an
extensible annotation system to accomplish this, but it would still come "in the box".

And for expressions too:

```scala
  def computeDamage (apower :Int, apoints :Int, dpower :Int) =
    @trace("damage=") Math.round(apower * apoints / 100f * @trace("pratio=")(apower/dpower))
```

yields:

```
pratio=F
damage=N
```

Maybe trace return value as well for functions that generate a result:

```scala
  @trace def computeDamage (apower :Int, apoints :Int, dpower :Int) =
    Math.round(apower * apoints / 100f * apower/dpower)
```

yields:

```
computeDamage(55, 50, 55)) // emitted on method entry
 = 28                      // emitted on method exit
```
