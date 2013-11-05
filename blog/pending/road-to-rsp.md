---
layout: post
title: Thinking out loud about RSP
---

Like everyone else exposed to [Bret Victor]'s excellent presentations, I have been thinking about
how programming might be substantially improved by throwing out as many of our assumptions as
possible and rebuilding our tools in light of new requirements introduced by a few decades of
hindsight (and made possible by a few decades of exponential growth in CPU speed).

I've been thinking about how to improve life for programmers for quite a while, going so far as to
spend a year in the PhD program with the [PLSE group] at UW before realizing that my preferred
approach to tackling these problems didn't fit well with that of CS academia. A combination of
circumstances have motivated me to dive into the deep end of the pool and try my hand at the
Quixotic task of developing a new programming model that solves all of our problems and makes
toast.

My ideas are still in their infancy, but they're concrete enough to put into words. I think the act
of explanation will help me to expand and refine them without succumbing to my natural tendency to
run off and start implementing things. Thus, I'm "thinking out loud" here and will probably
completely change my mind at least a few times before I arrive at anything worthwhile. Indeed, the
whole exercise may hit a wall in disastrous public failure and I'll be forced to hang my head in
shame and go do something else.

## Influences

In addition to being influenced by Victor's exhortation to "show the state", I've been barking up
the [FRP] tree for a while, and following the [Rx] diaspora with interest as it brings FRP ideas
into contact with a much broader audience (and they seem to like it). I'm also a fan of
[Jonathan Edwards]'s work and hope that it some day bears fruit.

I'm a strong believer of reducing the time/effort required to obtain feedback on a code change to
nil. [Live coding], [JRebel] and Eclipse's [edit and resume] feature are all steps in the right
direction.

Recently, I came into contact with [Reactive Demand Programming] which has the least scrutable
presentation of any programming model I've encountered, but seems full of excellent ideas, so I've
been wading through David Barbour's posts with my Haskell glasses on.

All of the ideas and insights in the programming model I've constructed so far originate in these
influences. I'm just trying to package them up in a manner that is somewhat coherent and not
completely objectionable to a working programmer. Maybe I'll have an original idea or two along the
way, but if that turns out to be unnecessary, all the better.

## State and change

A key philosophy of the programming model I'm developing is strong separation of state and
behavior. (When I say state, I mean mutable state.) As it's not possible to eliminate state (we're
usually creating stateful systems), the best one can hope for is to keep state on a short leash.
The various attempts to apply FRP to real problems that I've encountered have always resulted in
the hiding of state in the dataflow graph. While I don't have enough experience pushing FRP to its
limits to definitively decry that as bad, it sure seems like a bad idea. Now you not only have
state, but it's hiding inside something that looks declarative.

In addition to clearly identifying your state and keeping it out in the open, we also need to be
careful about how we change that state. Jonathan Edwards has said "We suggest that to rationalize
the insanity of programs that can read and write globally at any time that languages should impose
automatic *time* management," but I contend that what we need to manage is change, not time. If
one's state is clearly identified and changes to that state are tightly controlled, it opens the
doors to substantial benefits (which I'll describe below).

## Reactive State Programming

The three main components of the programming model are:

  * hierarchical reactive state
  * pure functions
  * FRP-style behaviors wired up with *fittings* and encapsulated into *systems*

The working name I have for the model is "reactive state programming". This puts the focus on the
reactive state, which is the thing that differentiates it most from plain FRP.

RSP is a programming model which can be achieved in any (reasonably expressive) programming
language, with the right amount of discipline. I'll describe things below using a hypothetical new
language designed for RSP (whose syntax is largely based on [Scala]), but that language is only
beneficial in that it *removes* capabilities rather than adds them. By removing capabilities we
guarantee that RSP's invariants are not violated accidentally or intentionally by well meaning
programmers.

There are a lot of things to unpack from the bullet points above, so I'll start with details on
each of the components and then come back to how it all fits together, and the benefits conferred
by this particular subset of the Turing tar pit.

### Hierarchical reactive state

When I say reactive state, I mean state for which all changes may be observed and reacted to. The
simplest form of reactive state would be a single value (of any "atomic" type, but we'll use an
integer). The value can be updated, and when the value is updated that can trigger other
computations, which result in updating other values. Think spreadsheet, but don't think it too hard
because it's not a perfect analogy.

All changes to a piece of reactive state are encapsulated. So you can't just say "Hey, jam this
value into this memory location". An update of a reactive value is encapsulated into a change which
is managed by the RSP-runtime. This enables reporting and visualization of all changes to mutable
state in your entire program. If you ever wondered how Victor-style "magic windows" into the inner
workings of your program are going to work in a "real" programming language, I think it's something
like this. If you're thinking "Oh god, the (lack of) performance" then bear with me a moment.

In addition to simple values, a variety of reactive "collections" are provided like lists, sets and
maps. This ensures that the programmer is working at a sane level of abstraction (expressing things
like "append this value to this list") while leaving the actual process of changing mutable state
to the RSP-runtime, which can enforce its invariants and make the change information available to
debugging and visualization tools.

Also, external resources (like `stdin` and `stdout`, the file system, the network, etc.) are also
modeled as reactive state. I'll describe how all of these usual suspects are modeled in a later
blog post.

I'll defer explaining the *hierarchical* aspect of the state until we describe *systems* below.

### Pure functions

Pure functions are how most computation is accomplished in RSP. As I'll show below in *Behaviors*,
when a piece of state changes, that new state value is routed (usually) through one or more pure
functions and the result is used to update some other piece of state. Pure functions can of course
call other pure functions in the course of their computation.

Pure functions place the most significant requirement on the "host" language: that it have no
global mutable state (or that the programmer be disciplined enough not to use it). Within a pure
function, mutable state is perfectly acceptable. One could program in RSP-style in C, and within a
function mutate stack variables to one's heart's content. Because there is no global state, and one
cannot access the reactive state except by being passed an immutable snapshot of its current value
as an argument to the function, there is no risk of accidentally introducing side effects. In a
garbage collected host language, it is even safe to heap allocate data and make use of it during
your pure computation, as it will either be garbage collected when the computation is complete, or
become part of the function's (immutable) return value.

I'll discuss in a later blog post how I think that this "pure computations on snapshots of mutable
state" approach in combination with *systems* will enable an actor-like concurrent programming
model that is easy to reason about and requires nearly no effort from the programmer.

### Behaviors and fittings

To react to changes in state and to effect new changes to state, one defines behaviors. These are
expressions of dataflow programming. If you're familiar with FRP behaviors, these are basically the
same thing. For example:

{% highlight scala %}
def fib (n :Int) = n match {
  case 0 => 0
  case 1 => 1
  case n => fib(n-1) + fib(n-2)
}

state n    :Int = 5
state fibn :Int = 0

// here comes the behavior!
n >> fib >> fibn
{% endhighlight %}

This program reacts to changes in `n` by computing the `nth` Fibonnaci number and storing it in
`fibn`. With more detail: when `n` changes, its current (immutable) value is passed to the function
`fib` which performs a side-effect free computation and returns an (immutable) value. That value is
stored into `fibn`, another piece of reactive state.

#### Fittings

The `>>` that you see in the behavior declaration is called a *fitting* as it connects pieces of
your dataflow pipe together. `>>` is the simplest fitting, it sends a value from reactive state to
a function, from a function to another function, or from a function back to reactive state.

There are a wide variety of fittings in addition to `>>`. They are used to combine data into tuples
for delivery to multi-argument functions, to split apart function results and route the parts into
different bits of mutable state, to effect container-specific changes like append to list, add
to/remove from set, etc. I'll describe some of the more interesting types of reactive state (lists,
maps, sets, etc.) along with their fittings in a future post.

#### Cycles

Cycles in behavior are not allowed. When a piece of state changes, the full consequences of that
change are computed and propagated immediately, along with any follow-on consequences due to state
changes that result from the previous changes.

This restriction is lifted when concurrency is introduced; when a change propagates from one
concurrency domain to another, the computation "stops" in the sending domain and is added to a
queue in the receiving domain to be processed once it has completed any currently executing and
already pending computations. This means that it would be possible to ping-pong a computation back
and forth between two (or more) concurrency domains. Whether or not this turns out to be a big
danger remains to be seen.

If one is modeling a simulation or animation that proceeds along a timeline, one must declare a
special *clock* state which ticks in real-time per the programmer's requirements. This clock state
can be specially managed by the development environment to allow the programmer to "control time"
while developing and debugging. Based on my current formulation, I don't think the ability to
"scrub" time back and forth will magically fall out of the RSP model, but if I'm able to
incorporate "undoing" changes (ala Sean McDirmid's [Glitch] programming model) this may turn out to
be possible.

For common interactive applications, computation is driven by external inputs: mouse, keyboard and
touch. In something like a video game, one would use both a clock and external user driven input
sources to create a dynamic computation that proceeds on its own as well as responds to user input.

#### Views

It is also possible to "partially apply" a behavior, by not routing a result back into reactive
state. This is called a *view*. For example:

{% highlight scala %}
struct Pos { x :Int, y :Int }
state mousePos :Pos = ... // external source

const TILE_WIDTH  :Int = 64
const TILE_HEIGHT :Int = 64
def posToTile (pos :Pos) :Pos = Pos(pos.x / TILE_WIDTH, pos.y / TILE_HEIGHT)

view tilePos :Pos = mousePos >> posToTile
{% endhighlight %}

A view can be understood as a piece of state which has been mapped by one or more functions. It is
not itself state, in that one cannot route a value to it, but it can be the source of one or more
behaviors. This enables both [DRY] and reuse of computation. If multiple behaviors use `tilePos` as
the source of their computation, they will all make use of the result of a single call to
`posToTile`. A [sufficiently smart compiler] could likely perform this common sub-expression
identification, but history has shown that sufficiently smart compilers are in short supply,
whereas programmers with a strong desire to take responsibility for the efficiency of their code
are less so. Plus DRY.

### Systems

Systems are the final piece of the RSP puzzle. They enable the dynamic assembly of otherwise
statically declared behaviors. A system is the main dynamic organizational mechanism in an RSP
program and you can think of it like an object in an OOP program. Indeed, when embedding RSP in an
OOP-language, a system will almost certainly be represented by an object.

Systems may also serve as a static organizational mechanism (i.e. namespaces for pure functions),
but I suspect that a separate module mechanism will be needed to enable one to provide a library of
nothing but pure functions and/or a combination of largely system-agnostic functions along with a
collection of reusable systems. I'd rather not use a degenerate system for that purpose in the name
of conceptual simplicity.

A system is a set of external state dependencies, external value dependencies, internal state
declared and owned by the system, and behaviors. It also provides a lexical scope for private pure
functions, for convenience. It is dynamically instantiated by a behavior in some other system, and
the top-level of a program is defined as a system. A system may subsequently be destroyed by its
enclosing system or itself.

Here's an example of a system that shows all of these elements (along with a bunch of other as yet
unexplained stuff, TODO: find a better non-contrived introductory example):

{% highlight scala %}
struct ClickE { pos :Pos }

system MouseClickDetector (
  view mousePos :Pos, view button1 :Boolean, const bounds :Rect
) {
  state armed :Boolean = false
  event clicked :ClickE

private:
  // track whether the mouse is inside our bounds
  state inside :Boolean = false
  def insidep (pos :Pos) = pos.x >= bounds.x &&
                           pos.y >= bounds.y && 
                           pos.x < bounds.x + bounds.width &&
                           pos.y < bounds.y + bounds.height
  mousePos >> insidep >> inside

  // track when the mouse is pressed down inside our bounds
  state downInside :Boolean = false
  (button1.change, inside)
    >> (bc :Change[Boolean], inside :Boolean) => { !bc.prev && bc.cur && inside }
    >> downInside

  // if we started a click inside our bounds, and we're currently inside our bounds, we're armed
  (downInside, inside) >> (_ && _) >> armed

  // if we're armed and button1 changes from down to up, emit a clicked event
  (button1.change, armed, mousePos)
    >> (bc :Change[Boolean], armed :Boolean, mpos :Pos) => {
      if (!bc.prev && bc.cur && armed) Some(ClickE(mpos)
      else None
    }
    >>? clicked
}
{% endhighlight %}

When a system is instantiated, its behaviors are "wired up" and executed based on the current
values of external and internal state. When a system is destroyed, its behaviors are all
decommissioned and any dependencies on external state are cleaned up. In this way, the creation and
destruction of systems serves as both a way to introduce dynamism into an otherwise static behavior
graph, and as a way to automatically manage resources.

For example, we can expose the file system as reactive state and relieve 


[Bret Victor]: http://worrydream.com/
[PLSE group]: http://www.cs.washington.edu/research/plse/
[FRP]: http://en.wikipedia.org/wiki/Functional_reactive_programming
[Rx]: https://rx.codeplex.com/
[Live coding]: http://en.wikipedia.org/wiki/Live_coding
[JRebel]: http://zeroturnaround.com/software/jrebel/
[edit and resume]: http://www.ibm.com/developerworks/library/os-ecbug/#N1011C
[Reactive Demand Programming]: http://awelonblue.wordpress.com/category/language-design/reactive-demand-programming/
[Jonathan Edwards]: http://en.wikipedia.org/wiki/Subtext_(programming_language)
[Scala]: http://www.scala-lang.org/
[Glitch]: http://research.microsoft.com/apps/pubs/default.aspx?id=201333
[DRY]: http://en.wikipedia.org/wiki/Don't_repeat_yourself
[sufficiently smart compiler]: http://c2.com/cgi/wiki?SufficientlySmartCompiler
