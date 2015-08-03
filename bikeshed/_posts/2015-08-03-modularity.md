---
layout: post
title: Modularity
---

[Last week's post] was pretty simple and relatively neat and tidy. This week I'm going to dig into a
subject that will range widely and include ideas that are far less baked. I'm jumping from one
extreme to another as both an exercise for myself and to make sure that I don't set the wrong
expectations by starting with a series of simple, clear-cut ideas.

Our topic this week is modularity. There are a number of aspects of hypothetical language that are
influenced by its modularity design:

  * code organization (files, compilation units, namespaces)
  * name visibility and export
  * object file format and separate compilation
  * runtime linking
  * probably other things too, I did mention that my ideas here are only half-baked

I started out trying to summarize the way other languages handle this, but every language is a
precious snowflake, so that turned into a quagmire. Instead, let me just describe the approach I
have in mind, and I'll try to point out where and why it differs from existing well-known (to me)
languages.

## In the beginning, God created the module

A module will be the preeminent organizational concept in hypothetical language. The two most
important questions you'll answer when writing code therein are: how should I split the code into
modules, and what are the APIs exposed by those modules for use by other modules?

Inside a single module, you can run around in your underpants and let the dirty laundry pile up.
But when it comes to exposing an API for use by other modules, you had better make yourself
presentable, because that's going down on your permanent record. There will be more fine grained
organizational mechanisms for use inside a module, but they will err on the side of "share by
default and don't be so uptight". Only between modules do we apply the battle-scarred conservatism
that tends to be carved into any programmer misfortunate enough to have to maintain a widely used
library over the course of many major revisions.

Though hypothetical language will support object-oriented programming, a module can export a wide
variety of linguistic constructs to other modules:

  * constant data (e.g. `PI`)
  * structs, enums and abstract data types
  * functions
  * classes
  * global variables (not recommended)

Because I don't want you to _accidentally_ expose something outside a module, the default will be
to make constructs visible only inside a module. You'll have to annotate something with `export` to
make it visible outside the module. But because I'm also not that concerned with rigid access
control inside a module, that's the extent of access control. Everything inside a module is
accessible to everything else.

Some examples:

```scala
var secret = "No one outside the module can see me"
export var global = "Hi, I'm an exported global variable! Also known as a bad idea."

// this type and all of its members are visible outside the module
export interface Person {
  def name :String
  def age  :Int
}

// neither this type, nor any of its members are visible outside the module
// even though it's a subtype of an exported type
class PersonImpl (val name :String, val age :Int) <: Person {
  def likesCheese :Boolean = ...
}

// enum and constants all exported
export enum State { AL, AK, ..., WI, WY }

// type is exported, members exported by default unless annotated with `module`
export class Address (
  val street :String,
  val city   :String,
  val state  :State) {

  def isValid :Boolean = ... // is exported
  module def isCached :Boolean = ... // is not exported
}
```

In addition to prefixing a type or member with `export` or `module`, you can also do things
C++-style and declare that everything that follows has a particular visibility:

```scala
export class Address (
  val street :String,
  val city   :String,
  val state  :State) {

  def isValid :Boolean = ...

module: // everything that follows is not exported
  def isCached :Boolean = ...
  def isResolved :Boolean = ...
  def resolve () { ... }
}
```

Following my preference for sensible convention over Father-knows-best proscription, you can mix
and match things to your heart's content (and your reader's lament):

```scala
export class Address (
  val street :String,
  val city   :String,
  val state  :State) {

  module def isResolved :Boolean = ...
  def isValid :Boolean = ...

module:
  def isCached :Boolean = ...
  def resolve () { ... }

  export def normalize :Address = ...
}
```

Naturally, I would recommend putting your exported API at the top and your module-local API down
below, but there are more things in heaven and earth than are dreamt of in my philosophy, so I'm
reluctant to enforce that.

If you've been bitten by such things in the past, you might be thinking: "But what about lexical
initialization order? That's going to gum up your precious organizational philosophies!" I'll
reserve this for a future article of its own, but I'd like to throw lexical initialization order
out with the bath water and either initialize _everything_ lazily, or do a dependency analysis and
initialize things in the "right" order. That's another place where my ideas are half-baked, but I
think it's a very important pie.

## Where are your privates, private?

The above description fails to mention anything about public, protected and private visibilities,
which any red-blooded object-oriented programmer is likely to want. I'm inclined toward a somewhat
radical position on that topic.

I would not enforce public, protected and private-ness, and instead foster a convention (by way of
the standard library) of prefixing elements "internal" to a class with `_`. Thus:

```scala
class Box[A] (val init :A) {
  def current :A = _current
  def set (value :A) {
    _current = value
  }
  var _current = init // I'm internal, not for us outside this class or a subclass
}
```

This would occur in two situations: a module-local `_`-prefixed member and an exported `_`-prefixed
member.

When it occurs in an exported member, this is roughly the same as `protected` in a traditional OO
language. It means that you can subclass the type in question and access the `_`-prefixed member in
your subclass, but don't touch it in code outside a subclass. The member is still effectively part
of your public API, and just as likely as any officially "public" member to cause pain if you
change it capriciously.

When it occurs in a module-local member, it means either `protected` or `private`. I claim that the
distinction is not particularly important in that circumstance. If it is only ever seen or touched
by code inside your module, then you have all of the source code that could ever interact with that
member right in front of you when you're reading or modifying the code. If you wonder whether any
subclass uses the member, just use `grep` (or your IDE) and find out.

If you _really_ feel the need to communicate the protected/private distinction to the other
programmers working on the module, then come up with your own convention. By definition this is
code that only exists inside _your_ module, so you can prefix protected members with `@` or `__` or
whatever you like. There's no technical reason to enforce accessibility within a module, only
political. The only people that will see it are you and the other programmers that work on that
module, so go crazy.

With regard to enforcing access control, the only guarantee I care about is "inside vs. outside" of
a module. If something's not exported by a module, you can't access it from a client of that
module. That's enforced by the compiler (and ideally the runtime). Everything else is on the honor
of the programmers involved.

If a member _is_ accessible outside a module, but is denoted as `protected` by naming convention,
then it is a matter of client-code style whether it restricts itself to accessing the member only
inside subclasses or not. If a client needs to violate that style, it makes that decision, not the
module author. The client could always create a subclass just to circumvent the enforcement of
protected-ness anyway, so why add that insult to the injury that necessitated the style violation in
the first place?

On the other hand, if a member is only accessible inside a module, then I expect you to enforce
your house style with the threat of violence (or the withholding of treats, if you're a pacifist),
not with the compiler. The programmer could just go _change_ the original definition to whatever
they want, so trust them to do that when it's appropriate, and trust them to violate style only
when they have good cause.

The nice thing about the use of `_`-prefixed members as a naming convention (aside from its
historical precedent) is that when someone does sin in the eyes of the Lord and Alan Kay, it stands
out in the code:

```scala
val box = new Box("Hello!")
box._current = "I'm naughty!"
```

*"What's that weird `._` doing there? Bob, have you been missing your AA meetings?"*

## What's in a name... space?

Modules define a bunch of related code and the API said code exposes to the world, but it is still
useful to divide up the code inside a module into namespaces. In hypothetical language, namespaces
are only for organizational and name-hiding purposes, they don't influence accessibility at all. If
you're inside a module, you have full access to all other code inside that module, regardless of
its namespace.

Some languages (like Java) require code to be organized into packages and those packages to be
reflected in the directory hierarchy that contains the code:

```java
// this must be in the file: foo/bar/Baz.java
package foo.bar;
public class Baz {}
```

Scala, C#, ActionScript, C++ and others relax the directory restriction and allow you to put
multiple packages into a single file and put those files anywhere you damned well please.

```scala
// can be in any file you want, like: elvis/presley/WasCool.scala
package foo {
  package bar {
    class Baz
  }
  package dingle {
    class Berry
  }
}
```

My experience with the latter approach (and "best practice" in languages that support it) tends to
be that your directory names better match up with your namespaces, otherwise your code is confusing
and hard to maintain.

In addition to the wisdom of ensuring that namespace and directory names match, I would also like
to adhere to the wisdom of not repeating yourself. Thus, my approach to namespaces is that the
directory name _is_ the namespace. It's not declared in the source file at all. In addition to
that, all of the code in a module must be rooted in a single top-level directory which is both the
name of the module and it's root namespace.

So a library might be organized thusly:

```
guava/base/Enums.ext
guava/base/Joiner.ext
guava/cache/AbstractCache.ext
guava/cache/CacheBuilder.ext
guava/net/HostAndPort.ext
guava/net/HttpHeaders.ext
```

The module is named `guava` by virtue of that being the top-level directory which contains source.
The `Enums` class, for example, is in the `guava.base` namespace, whereas the `CacheBuilder` class
is in the `guava.cache` namespace.

A multi-module library would have multiple top-level directories:

```
shared/data/Foo.ext
shared/util/Strings.ext
client/FooService.ext
server/FooProvider.ext
```

The above would generate three modules: `shared`, `client` and `server`.

If you decide to move something from one namespace to another (or one module to another in a
multi-module project), you just move the source file. No need to change a namespace declaration and
no need to have some kind of warning or compiler error to indicate that a namespace declaration
does not match up with the directory in which the source file lives.

With regard to whether file names and type names match up (i.e. `class Foo` is in `Foo.ext`), I'm
inclined to be more relaxed. I would at least experiment with allowing any code in a given
namespace to appear in any file in that directory. If that turned out to be too confusing, I would
likely require that exported types (classes, interfaces, enums, etc.) be declared in a file of the
same name and (by implication) that only one exported type be declared per file. Any number of
additional unexported types could coexist in that file with the exported type, but no additional
exported types.

### The importance of being imported

As usual, all of the names in a namespace are implicitly visible to all code in that namespace, and
one _imports_ names from other namespaces to make them visible. This would be done in the usual way
and support the usual conveniences:

```scala
import math.geom.* // imports all names in the math.geom namespace
import collect.Map // import a single type
import util.check.assertTrue // import a single function
import collect.{ArrayList, HashMap, HashSet} // import multiple types
import collect.{LinkedList => LList} // import and rename
import util.check.{Checker, assertFalse} // import type and function
```

Like Scala, `import` can be used at any scope:

```scala
import collect.Map // visible to everything in file
class Foo {
  import collect.Set // visible only inside this class
  def bar () {
    import util.check.assertNotNull // visible only inside this method
  }
}
```

Also like Scala, `import` can reach into types and pull their members into the namespace:

```scala
data Name (title :String, first :String, last :String)

class Util {
  def format (name :Name) = {
    import name.*
    s"$title $first $last"
  }
}
```

Unlike Scala, we will avoid the abomination of `__root__` and instead require that relative imports
be preceded by dot. In a class `foo/Bar.ext` you could import `foo/bar/Baz.ext` with `import
.bar.Baz`. Any import that does not start with a `.` is fully qualified.

Qualified names can also appear directly in source code. So you can opt not to import something and
instead just write `val s = new collect.HashSet[String]()`.

## What is a compilation unit anyway?

Here's where we try to take a baby step into the future. The compiler for hypothetical language is
not a batch compiler. We haven't used punch cards in a long time. The compiler is interactive by
design and is optimized for an interactive edit, run cycle. It's designed primarily to be embedded
into an IDE, and secondarily to be run from the command line as part of an automated build.

The way in which modularity fits into this picture is that the module is the compilation unit. You
point the compiler at the directories for one or more modules and it watches every file and
directory thereunder and automatically recompiles anything when it changes. It handles the
incremental recompilation of dependent code and it does it with a level of efficiency and accuracy
that is only possible by the compiler itself. Every IDE and build system does not need to
reimplement (to whatever half-assed degree) this very complex, very language-dependent process.

Furthermore, because the module is the compilation unit, the notion of "separate compilation" is
vastly simplified. Within a single module _there is no separate compilation_. That idea was
possibly useful fifty years ago when recompiling two or three C files out of five hundred was good
economics. Now, if I can't recompile an entire module from scratch in one to two seconds and
incrementally recompile it in tens of milliseconds, then I'm a failure as a compiler writer and a
language designer.

This brings other benefits as well. Within a module any kind of optimization is fair game, because
you know that the entire module will be recompiled when anything changes. You don't have to worry
that you inlined code from another class which is then changed and separately recompiled without
the inlining class also being recompiled.

The manual step of doing a clean recompile because certain object files somehow became out of date
with others also goes away. Indeed, individual object files go away. A module is a collection of
source files which is turned into a single object file. There's no directory full of `.o` or
`.class` files. You go straight to `.dll` or `.jar` or whatever you want to call it. It's the
compiler's job to make sure the generated file is internally consistent, not the build system's or
the IDE's.

## Runtime is funtime

In these antic-filled post-modern times, a language can't afford to tie itself to a particular
virtual machine or to a virtual machine at all (cf. iOS). So I'm reluctant to say too much about
what's inside a module's object file and when and how that is turned into machine code and linked
with other modules to create a fully operational application. However, there are a couple of things
that bear talking about even if this is all very hand-wavy and vague.

First, with regard to loading (or reloading) code at runtime, I would aim to support two
modalities:

* For a shipped application, one can load (and unload) an entire module at runtime. And there is a
mechanism (runtime API) that allows one to discover the (versioned) dependencies of a module and
whether those are already loaded.

* For development, the compiler will integrate closely with a VM that supports fine-grained
redefinition of classes, types, etc. so that (as much as possible) a programmer can simply edit
code and continue testing their already running application. Because the compiler knows what
changed and how, it is in a much better position to tell the VM what to do than to have the VM try
to figure out what changed by reloading object files and "diffing" things (or just trusting that
the new code will work with old memory layouts).

Second, there is the question of static initialization. It would be great if this could be
completely lazy. Not like the JVM or CLR where static initialization for a class takes place when
it is loaded, but lazy down to the individual static member. So when a given static member is first
referenced, its initializer is run and all code paths that access it are all (eventually)
recompiled with a direct reference to the now initialized memory (which will possibly be inlined if
it was just a lazily defined constant).

To do this efficiently requires support from a JIT-compiling VM, so perhaps it's a bad idea to use
these semantics in the language and necessitate an inefficient translation for platforms that
cannot JIT-compile code (again cf. iOS). However, I'm never going to implement this language, so
what's the harm in dreaming of a world where we can do things "properly"?

There are also cases where lazy initialization is inappropriate, so I would introduce some
linguistic mechanism to express that a particular initializer or block of code should be run
immediately after a module is loaded and linked. That's the only granularity at which this kind of
code could be run, there would be no per-class or per-type static initialization phase.

This up-front initialization would probably be part of a dependency "injection" mechanism (which is
also closely related to modularity and should be discussed here, but I'm running out of steam, so
I'll have to cover that in a future post). An example of where something like this is useful is a
plugin system. If you load a module at runtime, it may want to immediately register with the app
that it provides some plugin, and it can be more convenient for that to be initiated by the module
rather than requiring the app to go poking into the module to see whether there's anything inside
that should be wired up.

## Etcetera

Herein are a few other bits that relate to modularity which didn't fit into the above sections:

* Hypothetical language will support type inference, but members exported outside the module cannot
have inferred type. You have to write it down so that you know what you're exporting.

* The compiler will check and require that all types "visible" from an exported type are also
themselves exported. If a type appears in an exported member of an exported type it has to be
exported as well. It will not implicitly be exported, you have to go mark it as such. Types must
not "leak" out of a module.

* The object file generated by the compiler will contain a high-level (serialized AST)
representation of the code rather than (or in addition to) a byte-code representation, and there
will be a final "baking" step which combines modules into an actual executable binary or DLL. This
baking step can perform whole program optimizations, prune unused code, and ensure that the
dependencies of all modules are met. I sympathize with Go's philosophy here: executables should be
mostly statically linked to minimize surprises at runtime. Certainly I don't want to perpetuate the
clusterfuck that is Java's classpath, or even the twisty maze of passages that is your average
operating system's DLL search path (though that latter may be somewhat unavoidable).

* The object file will contain the _documentation_ for the code in the module, and optionally the
source code as well. So you ship a single artifact to users of your module and it contains
everything they need to use your module during development (source and docs would not be propagated
into the final exe or DLL by the baking process). If you are a sensible person and realize that
your code is not a precious jewel, you will ship it to your clients and make their lives easier.
But even if you believe that you must protect your proprietary codes and their purity of essence,
at least the documentation will always be available. (I have other strong feelings about
documentation which will be the subject of a future article.)

As I said at the start, modularity is a big subject, and this is not comprehensive coverage. I'll
probably have more to say in the future, but this is a nice meaty chunk that we can argue about.

[Last week's post]: /bikeshed/2015/07/auto-destructure-args/
