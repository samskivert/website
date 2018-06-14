---
layout: post
title: Ramblings on programming language design
date: 2018-06-13
---

[Mario] asked me an interesting question the other day. As is often my tendency, I gave him a
multi-thousand word answer. Then he foolishly asked more interesting questions, so I hit him with
additional walls of text. Be careful what you ask for kids. In the end, we figured we'd share with
the class because perhaps some useful explanations happened along the way.

[Note: because I'm lazy I did not edit out all the profanity. Please excuse two guys talking to one
another like sailors. When forced to deal with the failings of overly complex programming languages
and tools on a daily basis, one has a tendency to cope by swearing a lot.]

### Mario

```text
Date: Tue, 12 Jun 2018 22:33:38 +0200
From: Mario Zechner
Subject: Re: Un-Reason-able
To: Michael Bayne
```

> ...blah blah...

Speaking of which. Assume I built a tiny little typed scripting language with generics. Would you
consider Kotlin's flavor of variance a good compromise compared to other langs?

https://kotlinlang.org/docs/reference/generics.html

### MDB

Yes, definitely. You want declaration site variance, rather than use site variance. And you
definitely want what they call generic functions (which the programming language people call
f-bounded polymorphism).

Kotlin's solution to variance refinement is reasonable, though it doesn't solve all the variance
problems. It allows you to do what they show in their example (write a method that reads from one
array and writes to another), but it doesn't solve the "classic" variance problem, which is
`Collection.contains`.

Say you have a Collection trait (interface) which is immutable and thus covariant in its type
parameter. That allows you to say that a collection of `B`s is a subtype of a collection of `A`s as
long as `B` is a subtype of `A`.

In Scala (where `+` is `out` and `-` is `in`), you would naturally want a `contains` method for
your Collection:

```scala
trait Coll[+A] {
  def contains (elem :A) :Boolean
}
```

but you can't have it:

```text
<console>:12: error: covariant type A occurs in contravariant position in type A of value elem
         def contains (elem :A) :Boolean
                       ^
```

because covariant interfaces "produce" things but the `contains` method is "consuming" an `A` not
producing it. If instead of `contains` we were writing `add`, you can see how this could lead to
breakage:

```scala
trait Coll[+A] {
  def add (elem :A) :Unit
  // implementation omitted, but assume it puts elem into some internal array
}

// a Dog is an Animal, so this is OK
val thing :Animal = new Dog()
// A is covariant so Coll[Cat] is a Coll[Animal], this is OK
val things :Coll[Animal] = new Coll[Cat]()
// bad! this adds a dog to cats
things.add(thing)
```

So in general it is illegal to use a covariant type parameter in "contravariant position". However,
we know that in this case we're not doing anything naughty with `elem` (like adding it to an array
internally) so we want to somehow express that what we're doing is OK. Before we look at how Scala
solves it, let's see how Kotlin solves it:

```kotlin
abstract operator fun contains(element: @UnsafeVariance E): Boolean (source)
```

Ha. So we can see that Kotlin has admitted defeat in this particular case. Which is not to say that
their solution to variance is bad, just that it has limits. Maybe those limits are fine, this
doesn't really come up that often. Even in Scala where they have a "better" solution, they still
have an `@uncheckedVariance` annotation which I have had to use on a number of occasions, so I
don't know that there is a "perfect" solution.

Back to our example, let's try it in Kotlin:

```kotlin
interface Coll<out A> {
  fun contains (elem :A) :Boolean
}
```

gives us:

```text
Test.kt:2:23: error: type parameter A is declared as 'out' but occurs in 'in' position in type A
  fun contains (elem :A) :Boolean
                      ^
```

Same as Scala.

The "right" thing to do is to use an existential type, which neither Java nor Kotlin "fully"
supports.

Java supports existential "bounds" which is part of its use site variance solution:

```java
Coll<? super A>
```

which means "a collection of _something_, and I don't know quite what, but either an `A` or some
supertype of `A`". This is basically what we want for `contains`, in that we want to accept our `A`
or any supertype of `A` (because if we're treated as a collection of our supertype then someone
could pass a supertype element into `contains`). And we still want to be prevented from using what
we're passed as if it were an `A` (like by sticking it into an internal array of `A`s), which would
be true because a `? super A` is not an `A` so we couldn't treat it as one.

But we can't write that type down. You can't say (we're in Java here):

```java
boolean contains (? super A elem);
```

If if you were clever, you might try to use f-bounds to solve it (which swaps the existential for a
universal, more on that later):

```java
boolean contains<B super A> (B elem);
```

but Java doesn't allow super type constraints in f-bound methods, only subtype constraints.
Thwarted again!

Kotlin uses what it calls "type projections" in the same way. At a use site, you can say
`Coll<in A>` to indicate that you have a collection of `A` or some supertype of `A`, and then you
won't be able to call any methods on the collection that return an `A`.

The way they describe this working is that you "project out" only the subset of `Coll<in A>`'s API
that is compatible with the supplied variance annotation (in this case contravariance). So in this
case, any methods that return an `A` would be omitted (that's covariant position), whereas methods
that take an `A` are OK (that's contravariant position).

But another way of thinking about this is that `A` becomes some existential type. So you can pass
values _in_ for that existential type, you have a "nameable" type that meets the criteria of the
existential, but you can't get values _out_ that have the existential type because you can't write
down (name) the type of a variable to hold them. Even if you let type inference propagate the
unnamable type to a variable, you wouldn't be able to do much with it because it's this weird "A or
any possible supertype of A" type and about the only thing you can do with that is call `Object`
methods on it since that's the only thing you can prove that it is.

Anyway, we can't write down a simple existential type in Kotlin either (and it would be weird to
write down existentials using this variance projection syntax anyway). This doesn't work:

```kotlin
fun contains (elem :in A) :Boolean
```

Like Java, Kotlin only supports subtype constraints on f-bounded methods. They don't even use a
syntax that can be "inverted". You just write `foo<A : B>(...)` which means `A` is a subtype of
`B`. I guess you could try to be sneaky and write:

```kotlin
interface Coll<out A> {
  fun <A : B> contains (elem :B) :Boolean
}
```

But kotlinc tells you to fuck right off because the thing after the `:` is supposed to be a
reference to a known type, not the place where you declare a new type:

```text
Test.kt:2:12: error: unresolved reference: B
  fun <A : B> contains (elem :B) :Boolean
           ^
```

Now we can go back to how Scala solves this, which I've hinted at above with my attempts to use
f-bounds to solve the problem in Kotlin and Java. In Scala, the method is actually defined like so:

```scala
trait Coll[+A] {
  def contains[B >: A] (elem :B) :Boolean
}
```

Which means "Hey, we take any type `B` as long as it's a supertype of `A`" (which includes `A`
itself). This using a universal instead of an existential, but we could also use an existential:

```scala
def contains (elem :_ >: A) :Boolean
```

where `_ :> A` means "some type that is a supertype of `A`", just like Java's `? super A`. But you
can't use that existential syntax outside of square brackets (yay parsers!), so we have to use the
more verbose existential syntax:

```scala
def contains (elem :B forSome { B >: A }) :Boolean
```

which the parser is happier about.

As you can see, existentials and universals are duals of one another in a deep mathy way. This is
neat but not usually super relevant when designing programming languages. However, in the case of
how we solve variance problems, this turns out to be an important distinction. Hence my rambling on
for this whole long ass email. :)

With an existential type, you're saying "hey, there's a type that fits the bill here, and I'm going
to declare it, but I can never talk about it ever again". So every time you write down an
existential variable, it's a new one. If you say:

```scala
def foo (as :Coll[_ >: A], bs :Coll[_ >: A])
```

then the things in `as` could be totally different than the things in `bs` and you cannot
interchange them. Indeed you could pass two different collections of supertypes of `A` to the two
different arguments.

But if you write:

```scala
def foo[B >: A] (as :Coll[B], bs :Coll[B])
```

then `as` and `bs` must have the same type of thing in them. You've _named_ this hypothetical type
and now you can talk about it.

This has an important influence on how you design APIs. For example, in our hypothetical read-only
collection class, we want to be able to append another collection to this one and return a new
collection that is the concatenation of both:

```scala
trait Coll[+A] {
  def append (as :Coll[A]) :Coll[A]
}
```

Well, we can't write that because `A` is used in a contravariant position, so like before we have
to use existentials or universals. If we use existentials (which were arguably the 'right' solution
to the `contains` problem):

```scala
def append (as :Coll[_ >: A]) :Coll[A]
```

then we're still in trouble because the things inside `as` are not `A`s they're some existential
type (which might be a supertype of `A`). So we can't combine them with our `A`s and claim to be
returning a new collection of `A`s. That's a lie.

But if we use universals:

```scala
def append[B >: A] (as :Coll[B]) :Coll[B]
```

we accomplish a few things. First, we declare a type `B` that we know `A` is a subtype of, so _our_
elements are also `B`s. Second, we specifically accept a collection of `B`s, so we know we can
safely put them together with our elements (which are `B`s too). Finally, we also tell the caller
what they're getting back: a collection of `B`s, _not_ a collection of `A`s.

So if you do:

```scala
trait Animal
trait Cat extends Animal
trait Dog extends Animal

val cats :Coll[Cat] = ...
val dogs :Coll[Dog] = ...
val animals = cats.append(dogs)
```

then the inferred type for animals will be a collection of the most specific supertype of `Cat` and
`Dog`, in this case `Animal`. Showing it all in action in the Scala repl:

```scala
scala> :paste
// Entering paste mode (ctrl-D to finish)

class Coll[+A] {
  def append[B >: A] (as :Coll[B]) :Coll[B] = null
}
trait Animal
trait Cat extends Animal
trait Dog extends Animal
val cats :Coll[Cat] = new Coll[Cat]()
val dogs :Coll[Dog] = new Coll[Dog]()
val animals = cats.append(dogs)

// Exiting paste mode, now interpreting.

defined class Coll
defined trait Animal
defined trait Cat
defined trait Dog
cats: Coll[Cat] = Coll@1e1232cf
dogs: Coll[Dog] = Coll@6f6efa4f
animals: Coll[Animal] = null
// see right ^^^^^^^^ here, it inferred Animal for B
```

This allows you to concatenate two collections of different types as long as they have a common
supertype, and everything just works. The existential solution is a non-starter, and the "make
everything invariant" solution (i.e. Java) means you end up wanting to add cats and dogs but have
no way to do it that doesn't involve casts and bullshit.

This may or may not be the reason that Kotlin basically doesn't have an `append()` method on their
read-only collections. They just decided "oops, can't solve this problem". Even if they tried to
fake it with the `@UnsafeVariance` annotation, there's no way in Kotlin's type system to _name_
this inferred supertype so that we can say what kind of collection you get back. You don't get back
cats when you add dogs to cats, and you don't get back (the existential) dogs. You get back their
LUB (least upper bounds).

So **the moral of this story is**: declaration site variance is good, f-bounded polymorphism is
good, but you should also consider supporting supertype f-bound constraints in addition to subtype
f-bound constraints. They allow you to solve a bunch of the problems that existentials are used to
solve in other type systems, along with even more problems that other type systems can't solve.

You can in theory get away completely without existentials. The new version of Scala (called Dotty)
has no existentials. They do have some other crazy shit though, so it's possible that some of that
crazy shit is needed to fully avoid existentials. But I don't think so, because existentials almost
*never* appear even in current Scala APIs.

That concludes your type theory lesson for the day. :)

I can't write all this without also mentioning that this is one of the reasons that OO needs to die
in a fire. Almost all of this variance stuff just goes away when you don't have subtyping. Poof! So
much simpler.

### Mario

That all made a lot of sense actually. Thanks for taking the time to write it all up.

I too have a feeling that OO needs to die. I need to read up on how non-OO languages do
polymorphism without subtyping (in the OO sense).

### MDB

Type classes! :)

\[We had been talking about type classes earlier in the conversation.]

### Mario

I think between subtyping, adts and type classes, type classes seem to be a good compromise. maybe i
should start with that.

\[Then shortly after because he hadn't yet seen my "Type classes!" email.]

Haha, great minds...

### MDB

ADTs are also needed in combination with type classes. Well, not strictly. You could just have
named records. But then you'd have to use type classes for *everything* which would be annoying.

There are two things people are usually doing when they say they want subtyping: unions (solved
with ADTs) and ad-hoc/subtype polymorphism (solved with type classes).

For example, if I'm reading in JSON in an OO language, I'm gonna have some sort of type-lattice:

```scala
abstract class JsonValue
class JsonNumber extends JsonValue
class JsonList extends JsonValue
class JsonObject extends JsonValue
// etc.
```

You *could* have methods on those classes, but you don't have to. In a language where you don't
have subclasses, that's not how you would solve it. You use ADTs (warning pseudo-Haskell syntax
here):

```haskell
data JsonValue = JsonNumber Int
               | JsonList (List JsonValue)
               | JsonObject (Map String JsonValue)
               -- etc.
```

Then when you want to do something to a `JsonValue`, you pattern match on it:

```haskell
case value of
  JsonNumber num => ...
  JsonList list => ...
  JsonObject map => ...
  // etc.
```

So what would be in methods on subclasses is instead in functions that pattern match on the ADT
type.

All peachy keen and no subtypes anywhere. Technically the "cases" of an ADT are kind of like
subtypes of the ADT type, but many languages don't give them their own type at all, there's *only*
the ADT type (e.g. `JsonValue`). You can never say "Hey, I have a variable of type `JsonList`".

I think it's useful to have types for the cases, but this is still a *very* limited form of
subtyping and doesn't cause problems. Indeed you actually don't have subtypes in the type checker,
you just have a rule that says "If I ever have a case type, and I want the ADT type, I'm good."
They call it a "coercion". That's it. If you do have types for the cases, you never want to infer
them, always infer the ADT type. This is a problem that Scala has because it uses classes to model
ADTs and type inference ended up doing the undesired thing.

OK, so that's one use case for subtyping. The other is ad-hoc/subtype polymorphism. For that you
use type classes. A simple example is "How do I do `toString`?" (which in Java etc. is a method on
`Object` that you override). In Haskell and type classy languages, they call this typeclass `Show`:

```haskell
class Show a where
  show :: a -> String
```

Then I can implement that for some type by saying:

```haskell
instance Show JsonValue
  show :: JsonValue -> String -- optional type signature just to show what's going on
  show value = case value of
    JsonNumber num -> show num
    JsonList list -> "[" ++ show list ++ "]"
    JsonObject map -> -- i'll skip this one because it's complicated
    -- etc.
```

So now my `JsonValue` ADT "implements" the `Show` type class and can be turned into a string. I
might have a log function that wants to `toString` something, it could look like this:

```haskell
logShowable :: Show a => a -> IO Unit
logShowable thing = log (show thing)
```

This assumes I have some `log :: String -> IO Unit` function that logs something that's already a
string. And ignore the IO stuff, that's just Haskell's way of saying "the thing you're doing has
side effects".

The key here is the `Show a =>` type class constraint. That says, this function works for anything
as long as it has an implementation of `Show`.

This is, in certain ways, "better" than OO polymorphism because I can declare an instance of `Show`
for any type I like, whether I wrote the type or not (modulo caveats!). It's "open extension"
rather than "closed extension" (you can't make some class implement an interface after the fact,
you would have to modify the class). But it also doesn't have the "magic" of the `this` pointer
(and implementation inheritance). But I think the magic of the `this` pointer is bad magic, so good
riddance.

Anyway, that's a quick overview of how you can solve the same problems people usually do with
subtyping, using ADTs and type classes.

### MDB

One more thing and then I'll shut up¹. This is a super good paper that brought a whole lot of
haphazard ideas about OO that were rolling around in my head into clarity:

http://www.cs.utexas.edu/~wcook/Drafts/2009/essay.pdf

It also talks about how module systems play into all of this, which is also important for an
industrial strength language.

Highly recommended.

\[¹ Editor's note: MDB does not in fact shut up.]

### MDB

One important note about that paper: don't confuse "abstract data type" with "algebraic data type"
(both tragically called ADTs). What I call unions up above are "algebraic data types", not
abstract. Abstract data types are about hiding the implementation, like in ML modules, and also
possible in Haskell though slightly less fancy than with ML. In the paper he unfortunately
conflates these two things because most of the languages of the day also conflated them.

An abstract type is just one that you can't look inside of. It might just be an int, or it could be
an array of ints, or some crazy algebraic data type. You don't know and it doesn't matter. You can
only pass values of the type around and pass them to functions which can "see behind the curtain"
(because they're defined in the same module as the abstract data type).

### Mario

I love these "Michael shows Mario the real programming world" email threads. I was aware of ADTs
and type classes (TS has a primitive (?) form of ADTs that I actually used in anger. I'm aware that
it's not real ADTs, but simply a more civilized way to handle function parameters that could be one
of many types, because JS is funny that way). But your explanation made a few more things click.

One thing that mildly irks me about ADTs is that they are "closed" (if I understand correctly).

Now, in practice that's probably a good thing in many respects. In OO land you end up with
something like this all the time

<a href="https://github.com/badlogic/basis-template/blob/master/src/main/java/io/marioslab/basis/template/AstInterpreter.java#L762">AstInterpreter.java#L762</a>

(ignore that these should be instanceofs, I was JMHing a bit to hard...). There's no language
construct that ensures I match all subtypes of Node, so you end up with a shitty ass big fat
if/else statement. ADTs would be great for this, and I can imagine one could even wrangle out a bit
of performance but essentially doing a computed goto style match based on the adt subtypes.

However, I think that breaks down as soon as I let that code out into the wild, to be used and
extended by others. For a user to extend an ADT, they'd have to modify my source (unless I'm
missing something). That sucks deeply. Extendable types are a must in many APIs.

OTOH, not having implementation inheritance isn't such a big deal. Most of that can be factored out
into functions anyways that can be reused when pattern matching to implement a "method" for an ADT.
The same is true for deep type hierarchies. Fuck em, I can easily live without them.

However, my OO brain is somewhat opposed to how the proximity of types and type methods (in OO
speak) is lost with ADTs. It is nice having the type lattice in one place. But it's less nice to
stuff the implementations of a function for all adt "subtypes" into a single big match statement.
When implementing a type and functions operating on that type, all that (temporary) noise is
distracting. Ideally, a sufficiently smart IDE would let me switch into OO mode and only display
what I need when implementing a specific ADT "subtype". Or I can tell my OO brain to just rotate
itself by 90°.

Now type classes I really like! Mapped to my OO brain, they are a kind of mix between Interfaces
and extension methods (and sort of similar to Go's structural typing, though I think structural
typing is a bad idea). Which is great in terms of being able to extend an API.

Maybe I should try my hands on writing a little language with records, ADTs and type classes, but
using familiar C style syntax. I'm not quite sure how parameterized types fit into this yet, but
I'll find out!

Also, thanks for the recommended reading. I didn't wanna sleep tonight anyways :)

### MDB

> One thing that mildly irks me about ADTs is that they are "closed" (if I understand correctly).

You get right to all the good problems! :)

That tension: between a closed data type which is easy to add functions to (ADTs), versus a closed
set of functions which is easy to add new "types" to (OO interfaces/classes) is known as the
"expression problem". And enough ink has been spilled describing solutions to it to fill all the
swimming pools in Southern California.

The first thing to acknowledge is that there is in fact a tradeoff. Being used to OO style, you
might take it for granted that someone can't come along and add new methods to a class hierarchy,
but that's a thing someone might want to do (there are so many methods I would love to add to
Java's standard libraries...). So in OO style, it's easy to make a new type that implements a fixed
set of methods, but a cumbersome totally external process (more on that later) to add a new method
that's implemented by all the types. Whereas in functional/ADT style, it's super easy to add a new
method that is implemented by all the types: just write a function that pattern matches on the ADT.
Done! But if you want to add a new type case, you're fucked.

You're probably familiar with some of the ways the OO people try to have their cake and eat it too:
extension methods, the visitor pattern, other crazier shit like partial classes and the mother of
all OO battleships: a research language called Familia
(https://dl.acm.org/citation.cfm?doid=3152284.3133894) which tries to solve _all the things_ and
ends up being so complex that I doubt even the authors could use it sensibly.

As it turns out, there are a bunch of ways that functional/ADT people try to have their cake and
eat it too. To name a few: GADTs (generalized algebraic data types, which are actually useful for a
bunch of different things, but solving the expression problem is one of them), multimethods, type
classes (via a variety of creative approaches).

There are even problems you don't realize you have (haha) which have vast numbers of papers
exploring not very compelling solutions to not very pressing problems. Family polymorphism is a
popular one (which is also tackled by Familia since it tries to do _all the things_). This paper
provides an interesting summary of a bunch of "extensibility problems" with the particular slant of
how they can be solved with type classes: https://dl.acm.org/citation.cfm?id=1173732 Whether or not
the type classes are the best solution, the overview of the problems is nice.

However, most of these "solutions" make the assumption that these sorts of problems are so common
that we absolutely must make our languages bend over backwards to support some concise, efficient
encoding of a solution. But I'm not so sure. I think that making it too easy to extend things (and
accidentally allowing things to be extended that were never anticipated) is actually a bad idea. I
want to tackle the problem from a radically different angle. In cases where the full matrix of
extensions really are needed, I just want to make it super easy for you to modify the library
you're depending on.

Effectively, no matter what the approach: you are modifying the library by extending it, whether
the library authors planned for it or not. And usually they didn't. And even if they did, they
probably did a shitty job of it. So the best solution IMO is for you to acknowledge that you are
getting deep into bed with this library, and to just see all of its dirty laundry. Go in there and
add the code you need. Change the behavior that you need. And read the damned code along the way so
that you know what you're doing. Don't just override a method and sneakily change shit and hope for
the best.

I want to provide good tool support for maintaining patches against your dependencies. For a start,
this is often the first thing you want when you're trying to use a library and you just want to put
a goddamned print statement in some library code, or make some other exploratory change to try to
better understand how this library code works. Yes if you really just want to inspect a value, you
can fire up a debugger, and I want to make improvements to debugging too so you can easily trace
values without making code edits, or graph values over time and other useful inspections, but
sometimes you need to change the code.

Sometimes you find bugs, now you have to fix those bugs. Oh god, I have to fork this library,
change my build system, deploy patched versions of artifacts to company-wide Maven repositories,
deal with dependency conflicts elsewhere, etc. etc. Someone please put a bullet in my head.

Instead, I want my build system to express both a dependency *and* any patches that I want applied
to that dependency. Now I can easily fix bugs or make little tweaks. Well hell, lets make it easy
to version control those patches along with my project. And then when I want to upgrade my
dependency, I see exactly where my patches need to change based on upstream changes. I don't
discover later, oops, they rerouted this method I was overriding, or changed this internal behavior
I was subtly depending on. I see it right there when I update the dependency. And since Compose is
all about making it easier to cope with change, all of the things that make it easier to update
code that depends on a library API, also make it easier to update code that patches the library.

Anyway, it's a radical idea and I have no idea if it will pan out, but I think it has a lot of
potential and also happens to be a great solution to the expression problem because you can go in
and add things exactly where you want them. Then everything is just as easy to use as if the
library anticipated your needs from the beginning instead of having some bolted on extra crap
that's kind of annoying to use, versus the built in stuff.

All that said, type classes are like the OO approach, so sometimes you want to use type classes
plus ADTs when you anticipate that people will be extending your code both with new types and with
new functions. As an example of a solution to your `interpretNode` problem, you can use an approach
called "object algebras" (when applied to OO languages) but which applies just as well for type
classes (where they call it 'tagless final interpreters').

The idea is that you create a thing called an "algebra": a set of operations on abstract things,
which captures the structure of the computations you're going to be performing, without pinning you
down to particular data structures. Best explained with an example.

First we define an ADT to represent the "output" of the interpreter, I'm going to make this super
simple and say we can only have Int, but you can support multiple kinds of values, you just have to
then deal with the fact that when you're interpreting you might get a runtime type error. I don't
want to deal with that here.

```haskell
data Value = IntVal Int
```

In real Haskell you'd probably use: `newtype Value = Value { value :: Int }`, but using an ADT is
what you'd do if you had more than one type of value, so I'll use this degenerate ADT instead. None
of this is particularly relevant to the technique I'm describing. Because we're using type classes
to drive teh boat, we do need _some_ sort of type to wrap `Int` so that we can say "when you ask
for this type, use this algebra" because all of this ends up being driven by the type you ask for
in the end. You could technically make `Int` mean "run the interpreter algebra" but that's a bad
idea.

Now we define our abstract "node algebra":

```haskell
class NodeAlg a where
  lit :: Int -> a
  add :: a -> a -> a
  mult :: a -> a -> a
```

This enumerates all the kinds of AST nodes we can create and in the mathematical sense "is" the
AST. It is akin to the "intensional vs. extensional" distinction in set theory. You can define a
set "extensionally" as the list of things in the set, or you can define a set "intensionally" as a
function that says yes or no when you want to know whether something is in the set. Programmers are
used to defining things "extensionally": enumerate all the cases, or enumerate all the values, but
this algebra defines things "intensionally" by saying "this is the structure of the computations
you can perform over this tree". It requires turning your brain inside out a bit, but when you
squint, you see that all the exact same "things" are being said, just in a different way.

Now we can write an interpreter as one kind of concrete algebra:

```haskell
instance NodeAlg Value where
  lit value = IntValue value
  add (IntValue a) (IntValue b) = IntValue (a + b)
  mult (IntValue a) (IntValue b) = IntValue (a * b)
```

Now if I want to create an expression in the "AST", I can write something like:

```haskell
myexp = mult (add (lit 5) (lit 3)) (lit 3)
```

That expression is "unevaluated", it's basically a function that takes an algebra and "runs" the
algebra through the computation. If you asked Haskell, it would have the type:

```haskell
myexp :: NodeAlg a => a
```

which means "give me a `NodeAlg` for any type `a` and I'll give you back an `a`".

The way type classes work, the algebra that's going to get passed in is implicitly defined by the
type you ask for, so if I write:

```haskell
let IntValue value = mult (add (lit 5) (lit 3)) (lit 3)
```

then it's going to use the "interpreter" algebra that I defined above (`NodeAlg Value`). That will
interpret the AST and give me back 24. `IntValue value` pattern matches on the single case of the
`Value` ADT and extracts the `Int` inside so `value` will be bound to 24.

But I can also define a "pretty printer" algebra (which is one way of extending this code, this
pretty printer algebra can be written by anyone, not just the original library author):

```haskell
data Printed = Printed String

instance NodeAlg Printed where
  lit value = Printed (show value)
  add (Printed a) (Printed b) = "(" ++ a ++ "+" ++ b ++ ")"
  mult (Printed a) (Printed b) = a ++ "*" ++ b
```

And I can run this in the exact same way:

```haskell
let Printed text = mult (add (lit 5) (lit 3)) (lit 3)
```

And the typeclass machinery will say, oh send in the `Printed` node algebra, and `text` will get
bound to the string `"(5 + 3) * 3"`.

So then some guy can come along and say, dude, what kind of barbarian created a language that
doesn't have subtraction!? Christ, let's fix this shit up.

```haskell
class NodeAlg a => MyAwesomeNodeAlg a where
  sub :: a -> a -> a
```

which "extends" the `NodeAlg` type class with an additional method "sub" which represents
subtraction. In the type class world, extension is really more like logical implication. It says
"if you have a `MyAwesomeNodeAlg`" for some type, you gotta have a `NodeAlg` for that type too. So
you can "imply" multiple parents with no problem.

Under the hood, parents and children still often get consolidated to avoid having to pass around
huge numbers of typeclass dictionaries (which are roughly like vtables), but that's an
implementation detail rather than a fundamental property of the language semantics (compared to
class inheritance, say, where a single vtable _is_ part of the semantics and is necessary to make
dynamic dispatch via `this` work).

The lack of the diamond inheritance problem here is partly due to a property called "coherence"
which means that there can only ever be *one* implementation of a typeclass for a particular type.
This is a deep topic and there are people who are pro-coherence and people who are against
coherence (or rather want to sometimes break it). A good design probably allows a little of both,
but I haven't figured out the exact right thing on that front yet. TBD.

Anyway, if we try to just use our awesome node algebra right away, like so:

```haskell
let IntValue value = mult (sub (lit 5) (lit 3)) (lit 3)
```

Haskell will say, "can't find an instance of `MyAwesomeNodeAlg` for `IntValue`" because we haven't
provided one yet. So we whip that up:

```haskell
instance MyAwesomeNodeAlg Value where
  sub (IntValue a) (IntValue b) = IntValue (a - b)
```

and now it just works. Because there's already an instance of `NodeAlg` for `Value`, the
`MyAwesomeNodeAlg` automatically _implies_ that one for all the methods defined by `NodeAlg` and we
only have to define the new ones. If, in addition to adding `sub`, we were the ones to add the
pretty printer concrete algebra, we could define everything at once:

```haskell
data Printed = Printed(text :String)

instance NodeAlg Printed where
  lit value = Printed (show value)
  add (Printed a) (Printed b) = "(" ++ a ++ "+" ++ b ++ ")"
  mult (Printed a) (Printed b) = a ++ "*" ++ b

instance MyAwesomeNodeAlg Printed where
  sub (Printed a) (Printed b) = "(" ++ a ++ "-" ++ b ++ ")"
```

So we can extend on both axes as needed and without too much ceremony in the extended definitions
or in the uses of those extensions.

These algebras are a little bit mind bending because *we never create an AST data structure*. So
it's hard to understand how it even works (it's functions all the way down!). Again its the
intensional extensional thing. A list is a data structure and it is also a fold function. They are
mathematical duals. We've just written a set of "fold functions" over our tree.

To be fair, there are situations where using an algebra directly is not ideal. Sometimes you really
want the data structure. But it's trivially easy to "reify" the fold by making an algebra that
creates an ADT when you need it:

```haskell
data Node = Lit Int | Add Node Node | Mult Node Node

instance NodeAlg Node where
  lit value = Lit value
  add a b = Add a b
  mult a b = Mult a b
```

So now you can say:

```haskell
node :: Node
node = mult (sub (lit 5) (lit 3)) (lit 3)
```

and you get back an AST.

Also, in reality you're not going to write these algebra functions directly, you'll have a parser
that takes a `NodeAlg` constraint and then calls that whenever it parses the appropriate AST node.
In very simplified form:

```haskell
parse :: NodeAlg a => String -> a
parse source = ...
```

The crazy thing is that if you pass the evaluation algebra to `parse`, the parser is literally
interpreting the code as it parses. But you can pass the `Node` algebra and have the parser
construct an AST as it parses, which is less crazy.

This folding stuff gets into a really interesting topic called "generalized recursion schemes"
(also called f-algebras, functional programmers love to f things :). This is a rabbit hole I've
gotten sucked into on multiple occasions. If you want to expand your brain, take this red pill:
http://blog.sumtypeofway.com/an-introduction-to-recursion-schemes/

Of course, once you've reified things into an AST data structure, then you're back to the
expression problem because the data structure can't be easily extended, but there are other
techniques for dealing with that situation (with various tradeoffs), but at least you've got
everything structured in an extensible way so then you can make smart decisions about which
technique to use (or potentially allow the library user to make those decisions so they can use
whatever works best for their use case).

But I still hold out hope for the "make it easy to hack the upstream library" approach! It's so
crazy it just might work. :)

> OTOH, not having implementation inheritance isn't such a big deal. Most of that can be factored
> out into functions anyways that can be reused when pattern matching to implement a "method" for
> an ADT.

Yeah for sure, and reusing straight functions can be made easier too. There are lots of ways to
avoid busywork once you have a nice conceptual framework where the programmer understands what
busywork is being automated for them. The danger I think is when there's too much magic and the
programmers don't really understand what the wiring is under the hood.

> However, my OO brain is somewhat opposed to how the proximity of types and type methods (in OO
> speak) is lost with ADTs. It is nice having the type lattice in one place. But it's less nice to
> stuff the implementations of a function for all adt "subtypes" into a single big match statement.
> When implementing a type and functions operating on that type, all that (temporary) noise is
> distracting. Ideally, a sufficiently smart IDE would let me switch into OO mode and only display
> what I need when implementing a specific ADT "subtype". Or I can tell my OO brain to just rotate
> itself by 90°.

As you can see above, when you use type classes, you do end up aggregating the methods rather than
the data type variants, so that's an OO style. When I first said that ADTs weren't strictly
necessary but that if you didn't have them, it would mean that you'd have to do *everything* with
type classes, I was alluding to the fact that often you can choose whether you want to structure
your operations as pattern matching on ADTs or as type classes implemented for a collection of
otherwise unrelated types. So you have some flexibility there.

I agree that it would be interesting to have support in the IDE for "focusing" on a particular ADT
case. Haskell already supports a style of writing functions that would work very nicely with such a
"focusing" mechanism. You can write:

```haskell
data Foo = Bar Int | Baz String | Bing Boolean

foozle :: Foo -> Int
foozle Bar value = value
foozle Baz text = length text
foozle Bing yes = if yes then 1 else 0
```

The pattern match is implicit in the definition of the function. It would be easy to "hide" all the
cases that were not the one you were interested in.

\[Editor's note: after this, MDB finally shuts up.]

[Mario]: https://twitter.com/badlogicgames
