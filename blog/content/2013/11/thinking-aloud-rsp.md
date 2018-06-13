---
layout: post
title: Thinking out loud about RSP
date: 2013-11-07
---

For a combination of reasons, I've finally gone completely mad and decided to try my hand at the
Quixotic task of developing a new programming model. Naturally it will solve all of our problems
and make delicious toast.

This is somewhat inspired by [Bret Victor]'s excellent presentations, but is also something that
has been gnawing at the back of my mind for some time. I even went so far as to spend a year in the
PhD program with the [PLSE group] at UW before realizing that my preferred approach to tackling
these problems doesn't fit well with that of CS academia. I've also once again allowed myself to be
subjected to the nefarious influences of [Paul Phillips](https://github.com/paulp), so he's partly
to blame.

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

As far as I can tell, all of the ideas and insights in the programming model I've constructed so
far originate in these influences. I'm just trying to package them up in a manner that is somewhat
coherent and not completely objectionable to a working programmer. Maybe I'll have an original idea
or two along the way, but if that turns out to be unnecessary, all the better.

## State and change

A key philosophy of the programming model is strong separation of state and behavior. (When I say
state, I mean mutable state.) As it's not possible to eliminate state (we're usually creating
stateful systems), the best one can hope for is to keep state on a short leash. The various
attempts I've seen that apply FRP to real problems have always resulted in the hiding of state in
the dataflow graph. While I don't have enough experience pushing FRP to its limits to definitively
decry that as bad, it sure seems like a bad idea. Now you not only have state, but it's hiding
inside something that looks declarative.

In addition to clearly identifying your state and keeping it out in the open, we also need to be
careful about how we change that state. Jonathan Edwards said that "to rationalize the insanity of
programs that can read and write globally at any time, languages should impose automatic *time*
management." I contend that what we need to manage is change, not time. If one's state is clearly
identified and changes to that state are tightly controlled, it opens the doors to substantial
benefits.

I'd explain those benefits now, but they don't make much sense unless you first know about the
programming model, so you can either wade through the rest of this and get to the benefits in due
time, or you can skip to the end and read them now and decide whether they sound interesting enough
to read the rest of the article. I'm not trying to convince anyone here, as I said, I'm just
thinking out loud. I don't expect anyone to read this (except my Mom, hi Mom!). Once things are
further along, I'll have super awesome demos that will do all the convincing for me.

## Reactive State Programming

The three main components of the programming model are:

  * hierarchical reactive state
  * pure functions
  * *systems* of *behaviors* wired up with *fittings*

The working name I have for the model is "reactive state programming". This puts the focus on the
reactive state, which is the thing that differentiates it most from plain FRP.

RSP is a programming model which can be achieved in any (reasonably expressive) programming
language, with the right amount of discipline. I'll describe things below using a hypothetical new
language designed for RSP (whose syntax is largely based on [Scala]), but that language is only
beneficial in that it *removes* capabilities rather than adds them. By removing capabilities we
guarantee that RSP's invariants are not violated accidentally or intentionally by well meaning
programmers.

There are a lot of things to unpack from the bullet points above, so I'll start with details on
each of the components and then come back to how it all fits together. Finally I'll outline some of
the benefits conferred by this particular subset of the Turing tar pit.

### Hierarchical reactive state

When I say reactive state, I mean state for which all changes may be observed and reacted to. The
simplest form of reactive state would be a single value (of any immutable type). The value can be
updated, and that can trigger other computations which result in updating other values, until the
system again achieves stability. Think spreadsheet, but don't think it too hard because it's not a
perfect analogy.

All changes to a piece of reactive state are encapsulated. You can't just say "Hey, jam this value
into this memory location". An update of a reactive value is encapsulated into a change which is
managed by the RSP-runtime. This enables reporting and visualization of all changes to mutable
state in your entire program. If you ever wondered how Victor-style "magic windows" into the inner
workings of your program are going to work in a real programming language, I think it's something
like this. If you're thinking "Oh god, the (lack of) performance" then bear with me until the next
section.

In addition to simple values, a variety of reactive collections are provided like lists, sets and
maps. This ensures that the programmer is working at a sane level of abstraction (expressing things
like "append this value to this list") while leaving the actual process of changing mutable state
to the RSP-runtime, which can enforce its invariants and make semantically rich change information
available to debugging and visualization tools.

External resources (like `stdin` and `stdout`, the file system, the network, etc.) are also modeled
as reactive state. I'll describe how all of these usual suspects are modeled in a later blog post.

The *hierarchical* aspect of the state will be described in *Systems* below.

### Pure functions

Pure functions are how most computation is accomplished in RSP. As I'll show below in *Behaviors*,
when a piece of state changes, that new state value is routed (usually) through one or more pure
functions and the result is used to update some other piece of state. Pure functions can naturally
call other pure functions in the course of their computation.

Purity of function places the most significant restriction on the host language: that it have no
global mutable state (or the most significant demand on programmer discipline: don't use global
mutable state). Note however, that inside a pure function, mutable state is perfectly acceptable.
As long as there are no observable side effects, you can use whatever approach you like in your
pure functions. Write 'em in assembly if you like.

You don't have to worry about concurrency when writing your pure functions; that happens at a
higher level. In a garbage collected host language, it is even reasonable to heap allocate data and
make use of it during your pure computation, as it will either be garbage collected when the
computation is complete, or become part of the function's (immutable) return value.

Because you can program "close to the metal" inside your pure functions, I suspect that RSP will be
acceptably performant overall. One can choose the manner in which a program's state is modeled such
that transformations to that state happen at sufficiently coarse granularity that most of the work
is going on in pure function calls, and you're not reactively updating individual elements of a ten
million element array. That of course places some burden on the programmer to structure their state
appropriately, but every programming style comes with modeling burdens. Time will tell if RSP's are
too onerous.

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
`fibn`. Specifically: when `n` changes, its current (immutable) value is passed to the function
`fib` which performs a side-effect free computation and returns an (immutable) value; that value is
stored into `fibn`, another piece of reactive state.

#### Fittings

The `>>` that you see in the behavior declaration is called a *fitting* as it connects pieces of
your dataflow pipe together. `>>` is the simplest fitting, it sends a value from reactive state to
a function, from a function to another function, or from a function back to reactive state.

There are a wide variety of fittings in addition to `>>`. They are used to combine data into tuples
for delivery to multi-argument functions, to split apart function results and route the parts into
different bits of mutable state, to effect container-specific changes like append to list, add
to/remove from set, etc. These form a simple DSL for dataflow programming. I'll provide details on
the common types of reactive state (lists, maps, sets, etc.) along with their fittings in a future
post.

#### Cycles

Cycles in behavior are not allowed. When a piece of state changes, the full consequences of that
change are computed and propagated immediately, along with any follow-on consequences due to state
changes that result from the previous changes. Most likely this will take place in a breadth-first,
glitch-free fashion.

The cycle restriction is lifted when concurrency is introduced; when a change propagates from one
concurrency domain to another, the computation "stops" in the sending domain and is added to a
queue in the receiving domain to be processed once it has completed any currently executing and
already pending computations. This means that it would be possible (though not necessarily a good
idea) to ping-pong a computation back and forth between two (or more) concurrency domains. Whether
or not this turns out to be a big danger remains to be seen.

If one is modeling a simulation or animation that proceeds along a timeline, one must make use of a
special *clock* state which ticks in real-time per the program's requirements. This clock state can
be specially managed by the development environment to allow the programmer to "control time" while
developing and debugging. Based on my current formulation, I don't think the ability to "scrub"
time back and forth will magically fall out of the RSP model, but if I'm able to incorporate
"undoing" changes (ala Sean McDirmid's [Glitch] programming model) this may turn out to be
possible.

For common interactive applications, computation is driven by external inputs: mouse, keyboard and
touch. In something like a video game, one would use both a clock and external user driven input
sources to create a dynamic computation that proceeds on its own as well as responds to user input.

#### Views

It is also possible to "partially apply" a behavior, by not routing a result back into reactive
state. This is called a *view*. For example:

{% highlight scala %}
struct Pos { x :Int, y :Int }
state mousePos :Pos = ... // external source

val TILE_WIDTH  :Int = 64
val TILE_HEIGHT :Int = 64
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
statically declared behaviors. A system is the main organizational mechanism in an RSP program and
you can think of it like an object in an OOP program. Indeed, when embedding RSP in an
OOP-language, a system will almost certainly be represented by an object.

Systems could also serve as namespaces for pure functions and nested systems, but I suspect that a
separate module mechanism will be better. One will want to provide a library of nothing but pure
functions and/or a combination of largely system-agnostic functions along with a collection of
reusable systems. I'd rather not use a degenerate system for that purpose in pursuit of the false
idol of minimalism.

A system is a set of external state dependencies, external value dependencies, internal state
(declared and owned by the system), and behaviors. It also provides a lexical scope for private
pure functions, for convenience. The top-level of a program is a system, and that system may
statically nest other systems within it. A system may also dynamically instantiate other systems
(which can contain static and dynamic nested systems themselves). It's systems all the way down.

Here's an example of a system which has external state and value dependencies, internal public and
private state, and behaviors to connect it all together (TODO: find a simpler introductory
example):

{% highlight scala %}
system MouseClickDetector (
  view mousePos :Pos, view button1 :Boolean, val bounds :Rect
) {
  state armed :Boolean = false
  event clicked :Pos

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
  // the (a,b) fitting combines two pieces of state into a two-tuple
  (button1.change, inside) // see text below re: button1.change
    // functions can be declared inline; >> routes a two-tuple to a two-arg fn
    >> (b1 :Change[Boolean], inside :Boolean) => { !b1.previous && b1.current && inside }
    >> downInside

  // if click started inside our bounds, and we're currently inside our bounds, we're armed
  (downInside, inside)
    >> (_ && _) // this is shorthand for (a, b) => { a && b }
    >> armed

  // if we're armed and button1 changes from down to up, emit a clicked event
  (button1.change, armed, mousePos)
    >> (b1, armed, mpos) => if (!b1.previous && b1.current && armed) Some(mpos) else None
    >>? clicked // the >>? fitting routes Some(x) to its destination and ignores None
}
{% endhighlight %}

The `MouseClickDetector` system depends on two pieces of external state: `mousePos` and `button1`.
It expresses that it need only react to that state by declaring them to be `view` rather than
`state` dependencies. It also depends on some constant configuration information, the `bounds` in
which to detect clicks. Note that `bounds` would more likely be a `view` dependency in a real user
interface, as the bounds of the element for which we're detecting clicks could change. In this case
I've made it `val` just to illustrate that such dependencies are possible.

`MouseClickDetector` exposes two pieces of state to its enclosing system: `armed` and `clicked`.
`armed` might be used by an enclosing user interface element to update its visualization as the
user interacts with it. `clicked` is declared to be an `event`, which is like `state` except that
it has no current value and thus will not trigger computation when behaviors that depend on it are
initially created, only when it "fires".

Barbour's RDP chooses to dispense with the notion of events entirely, but I'm (currently) of the
opinion that they're sufficiently fundamental to a programmer's conception of a dynamic system that
they should exist in the model rather than be modeled as a change in state. The action of clicking
a button models a user's intent. Their intent was not to depress the button and release it, that is
a means to an end. I think it introduces needless complexity to require this action to be modeled
as a change in an "is clicked" value.

Because reacting to change in state is so common, we provide sugar for obtaining a state's previous
and current value when the state is changed. This can be seen above in `button1.change` which
yields a `Change[Boolean]`. A `Change` record contains the `previous` and `current` values for the
state. This could also be obtained manually by routing `button1` into a length two rolling buffer
and reacting to updates to the buffer. That's how one obtains a history of length greater than two.

#### Nested systems

Systems also give us an explanation for the *hierarchical* nature of the reactive state. A system
encloses other systems, which themselves may enclose systems, in a tree structure. Because all
reactive state is owned by a system, the totality of a program's state is also structured as a
tree. Note that though state and systems can be supplied as dependencies to other systems, making a
DAG rather than a tree, the *ownership* of the state and systems is still tree-structured.

It is possible for a containing system to access the public state of its child systems and it can
pass its own state (or state on which it depends) to its child systems:

{% highlight scala %}
system MouseClickDetector (
  view mousePos :Pos, view button1 :Boolean, view bounds :Rect
) { ... }

system Button (view mousePos :Pos, view button1 :Boolean) {
  state bounds = Rect(0, 0, 0, 0)
  // we pass down our depends (mousePos, button1) and our state (bounds)
  val clickDetect = MouseClickDetector(mousePos, button1, bounds)
  // we pull up armed and react to its changes (note: highly inefficient example)
  clickDetect.armed >> renderButton >> image
  // ... there would be a lot more to a real button ...
}
{% endhighlight %}

However, a system cannot pass state from one child to another:

{% highlight scala %}
system Foo {
  val bar = Bar()
  val baz = Baz(bar.enabled) // illegal!
}
{% endhighlight %}

If a system needs to pass data between subsystems, it must route it through state that it owns:

{% highlight scala %}
system Foo {
  val bar = Bar()
  val baz = Baz(barEnabled)
  state barEnabled = false
  bar.enabled >> barEnabled
}
{% endhighlight %}

This ensures that computations take place in the proper execution context when concurrency is taken
into account (see *Concurrency* below).

The above examples have shown a system *statically* enclosing another system. When the outer system
is instantiated, the inner system is automatically instantiated, binding their lifecycle together.
It is also possible for a system to *dynamically* enclose other systems, which is essential to most
interactive programs.

A single dynamic enclosed system:

{% highlight scala %}
system AI (...) { ... }

system Game (...) {
  state turnHolder :Int = 0
  state opp :Opt[AI] = None

  // when turn holder becomes non-zero, an AI system is instantiated, 
  // when it becomes zero, the AI system is destroyed
  turnHolder >> (thIdx) => { if (thIdx == 0) None else Some(AI(...)) } >> opp
}
{% endhighlight %}

A list of enclosed systems:

{% highlight scala %}
interface Screen { ... }

system App {
  state stack :Seq[Screen] = Seq()
  onInit // onInit is an event emitted when a system is initialized
    >> () => MainMenuScreen(stack)
    >>+ stack // >>+ appends a value to a Seq
}

system MainMenuScreen (state stack :Seq[Screen]) : Screen {
  val prefs = Button(...)
  prefs.clicked >> () => PrefsScreen(stack) >>+ stack
}

system PrefsScreen (state stack :Seq[Screen]) : Screen {
  // when something emits this event, we'll remove ourselves from the screen stack
  event closeSelf :Unit
  closeSelf
    >> () => self // self refers to the current system
    >>- stack     // >>- removes a value from a Seq
}
{% endhighlight %}

This latter example hints at some additional abstraction and code reuse mechanisms that will be
possible with systems. *Abstraction* below provides more details.

#### System lifecycle

When a system is instantiated, its behaviors are "wired up" and evaluated based on the current
values of external and internal state. All statically nested systems are also instantiated at that
time. When a system is destroyed, its behaviors are all decommissioned and any dependencies on
external state are cleaned up (ditto for statically nested systems). In this way, the creation and
destruction of systems serves as both a way to introduce dynamism into an otherwise static behavior
graph, and as a way to automatically manage resources.

When a dynamic system is no longer contained by any state (detected by reference counting or
garbage collection, not yet sure which will be best), it is destroyed. If a system is destroyed
which contains dynamically nested systems, they may or may not be destroyed depending on whether
those systems are referenced elsewhere. This makes me a little nervous, so I'll be investigating
whether it's not too limiting to disallow references to systems escaping "up the tree" to parents
of the system that created them. If that's disallowed, then every dynamic system can be destroyed
when its creator is destroyed, which would be nicely simple.

#### Concurrency

Systems provide a mechanism for concurrency. A system can serve as the root of an *execution
context*: an island of single threadedness in a sea of concurrent processes. I'll call such a
system a *process*. This is similar to the [actor model] except that message passing is implicit in
the propagation of a state change from one process (actor) to another.

By default, all statically and dynamically enclosed systems exist in the same process as their
owning system. Changes to the owning system's state as well as the contained system's state are all
processed immediately in the same (single threaded) execution context.

If a system contains a subsystem (statically or dynamically) that is marked as a process, any
changes to that subsystem's state will be posted to that subsystem's execution context for
evaluation instead of being evaluated directly by the initiating process. Similarly, any changes to
parent supplied state by the subsystem will be posted to the parent's execution context for
evaluation rather than being evaluated directly by the subsystem.

I believe that it will be possible to automatically identify these process-boundary crossings and
handle communication between concurrent processes without any additional information. The developer
will simply declare a `process` instead of a `system` and the runtime will take care of everything
else. I have not thought this concurrency stuff through in detail nor implemented it, so this may
turn out to be pie in the sky.

#### Abstraction

Systems provide a mechanism for abstraction. It would be possible to adopt an object-orientation
directly and allow systems to inherit from other systems, inheriting their state and behaviors in
the process. I suspect this is a bad idea, not least because inheritance of behavior and state is
such a source of pain in the OO world.

Naturally, this will be dictated by the host language. If a system is modeled as a class, then it's
going to inherit (pardon the pun) the capabilities of a class in that language. But in a language
designed specifically for RSP, I would lean toward a combination of interfaces and delegation. My
thoughts here are very rough, but something along these lines are likely to be where I'll start:

{% highlight scala %}
interface Screen {
  state added   :Boolean
  state showing :Boolean
}

system ScreenImpl : Screen {
  state added   :Boolean = false
  state showing :Boolean = false
}

system ScreenStack {
  state screens :Seq[Screen] = Seq()

private:
  state cycles :Seq[Lifecycle] = Seq()
  system Lifecycle (val screen :Screen) {
    screens >> _ contains screen >> screen.added
    screens >> _.tail == screen >> screen.showing
  }

  // when a screen is appended to screens, append a Lifecycle to cycles
  screens +>> Lifecycle >>+ cycles
  // when a screen is removed from screens, remove the associated Lifecycle
  screens >>- (idx, _) => idx ->> cycles
  // this is such a common pattern that I'll almost certainly provide an
  // assoc list that associates a system with each element of a list
}

system MainMenuScreen : Screen {
  val screen = ScreenImpl() provides Screen
  // can create behaviors that use added and removed
}
{% endhighlight %}

I'm already dissatisfied with this mechanism after writing this one simple example, so more thought
is here needed.

## Why bother?

In the beginning, I stated that if we can clearly identify our mutable state and carefully manage
changes to it, we will reap substantial benefits. Here I'll describe the benefits I think are
likely to fall out of this programming model. I suspect other benefits might turn up along the way,
but that could just be my optimism talking.

### Show the state

By using explicitly managed abstractions for our mutable state, which automatically track all
changes to said state, we can very easily visualize that state and changes thereto in external
tools. A given piece of state could be graphed "over time", or compared to another piece of state,
or simply logged to a console. All of this can be done in realtime as the program is executing, and
a programmer can interact with their program and see the changes that result.

### Tweak the state

Changes can be introduced externally just as easily as they can be introduced by the program. A
developer can make changes to pieces of state through a debugging tool and observe the effect that
has as the change flows through the computation graph. This allows them to develop a better
understanding of the code as well as to manually mock up state changes during the process of
development.

### Preserve the state

A development version of the runtime can store the state graph separately from the data structures
that are used by the program runtime, allowing the code for the application to be unloaded and
reloaded without abandoning its current state. This can work in conjunction with existing
on-the-fly code reloading mechanisms (like JRebel, but eliminating many of its limitations). Or one
can use a bigger hammer and simply unload and reload everything except the reactive state, when
requested by the developer. Either mechanism eliminates the tedious and time consuming process of
returning to a particular application state to test newly introduced code changes.

It should even be possible to take advantage of this separation of state and behavior on platforms
with "unsophisticated" runtimes, like when targeting and testing on a mobile device. The app can
save the state to permanent storage and reload it prior to restarting. While not as fast as simply
keeping it in memory, it will still be substantially faster than restarting the app in its default
state and manually interacting with it to recreate the state necessary to resume testing.

### Purity of essence

By expressing much of a program's computation in pure functions, you tap into their well known
benefits. They're easier to develop and test: supply the inputs, check the outputs. They're also
easier for readers of the program to understand: there are no hidden dependencies or state.

We can also leverage the "show the data" visualization tools to visualize their behavior. The
development environment can show the output of a function on randomly generated inputs, or on a set
of test inputs provided by the developer. The developer can easily play around with functions in a
REPL.

## Enough already

There are a lot of holes in the rough sketch I've provided above, which I'll endeavor to fill in in
future blog posts. I'm in the process of translating patterns that I find recurring in my own
programs into RSP style, identifying the painful parts and thinking about whether those can be
smoothed over with improvements to RSP, or whether the pain indicates that I'm doin' it wrong.

I doubt I'll be able to hold off on an implementation for much longer. My plan is to implement RSP
in Scala, which is sufficiently flexible that I think I can attain most of my "ideal" syntax and
runtime structure. Once that's roughly working, I'll start on visualization and interactive
debugging tools, as those will determine whether the extra difficulty of writing in RSP-style is
actually worth it. I'll also learn a lot along the way which will influence the design and
development of an eventual bespoke language and runtime.

Onward into the abyss.

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
[actor model]: http://en.wikipedia.org/wiki/Actor_model
