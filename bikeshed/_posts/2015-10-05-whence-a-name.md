---
layout: post
title: Whence a name?
---

A brief explanation, aka excuse, for the belated nature of this post. I've started teaching a high
school computer science class, and what was left of my already endangered free time has been
blasted well past extinction and deep into a negative population count. So this post is coming at
you from magical negative population mirror world, also known as "some hours of the day when I
should be asleep". Let it suffice to say that the schedule henceforth will be _sporadic_.

Today I was thinking about names, specifically importing names from other code. I already mentioned
that in hypothetical language, a module will be a single namespace. Every top-level type or
function will be visible to all code in the module, without explicit import, regardless of whether
you've partitioned the module into subdirectories or some other hierarchy for internal
organizational purposes.

That doesn't mean happy commune time where everyone is sharing underpants. The language will
support nesting types (and naturally functions) inside other types, so I imagine that most
name-collision-prone types will be distinguished by the type in which they're nested (e.g.
`Map.Entry` vs. `List.Entry`) and outside the type that defines them, they'll be qualified with
their enclosing type everywhere they're mentioned. Further, a lot of types won't ever be seen
outside the confines of their enclosing type because they're implementation details.

But it does mean that a module will conceptually be "something about the size where it makes sense
to have a single namespace". The [Guava] library, for example, would probably separate all of its
top-level packages into their own modules. And since the module is one namespace with regard to all
the names it defines, I think it should also be one namespace with regard to all the names it
imports from its dependencies. One big happy ball of names that are combined in useful ways to
produce meaning for a small set of names that are then exported for use by other modules.

That last point is also important: _a small set of names exported for use by other modules._ Since
hypothetical language forces you to explicitly tag the things that should be visible outside your
module (and makes it easy to access everything else inside your module, so you won't be tempted to
make something externally visible just so it's easier to access internally), you're less likely to
export a bunch of stuff that isn't actually part of your public API (accidentally, or on purpose).
Modules can be parsimonious about what they export, and you can safely import all the names in a
module without discovering that now you have three copies of `StringUtil` classes in your namespace
which you neither want nor need.

Anyhow, back to my original train of thought, which was how annoying it is to have to import every
damned name you want to use in every damned source file you want to use it in. This is even more
annoying when the provider of that name chose "unwisely" and you decide to rename the type when you
use it in your module. (If I had a nickel for every time I typed `import
scala.collection.mutable.{Thing => MThing}`, I'd have enough money for a first class ticket to fly
to Switzerland and vent my frustration to Martin Odersky in person.)

We solve this problem by having one place where all imports happen for an entire module. Call it
the preamble, or the module file, or whatever you like, but it brings any external names into the
module's namespace once, and does any desired renaming also once. Since we're building pies in the
sky here, this module file not only brings in the names, but it specifies the precise dependencies
of the module in the process. So when you "import" some names into your namespace, you do so with a
versioned unique identifier for the module from whence those names come.

Right there! Once! Not in some separate build file. Not once in one or more build files and
sporadically across two dozen additional source files. Not in XML, or Groovy, or whatever you call
the language that comprises Makefiles. In the same programming language that you're writing your
code.

But wait, there's more! You might be thinking "Yeah, C# does something like this and it still
sucks." And you'd be right. But we're not going to stop at the expression of dependencies in the
source code of the module preamble file, and then leave the actual resolution of those dependcies
to some bastard combination of Visual Studio and the operating system. Heavens no. We're going to
build the dependency resolution system right into the compiler and the linker/runtime.

The compiler will maintain a cache of dependencies which it downloads from the interwebs when it
sees them appear in a module that it has opened for compilation. [Maven] has the right idea here,
but before you start throwing up in your mouth a little bit, remember that I'm just talking about
the magical dependency resolution part, not the terrible build system part. But it's also not going
to be centralized; that's also a proven failure.

Since I'm just making shit up, we're going to distribute our dependencies via [IPFS]. When you
publish a version of your library, it goes into the magical global FS-space and lives there
forever, so that anyone who ever depends on that version of your library for the _rest of time_
will be able to find it lurking deep on the outer platters of a rickety hard drive somewhere in a
basement in Wyoming.

We'll probably need some way for Secret Corp to maintain its own internal dependency cloud, but
they can run a private IPFS cloud, or maybe we'll support encrypted depends. Encrypted depends are
interesting as that might simplify the process of selling software libraries. Terrifying.

We'll also have meaningful version numbers that distinguish between patch releases, which only fix
bugs and make no behavioral changes, and non-patch releases, which can change whatever they please.
Two versions of a library that differ only in patch release can be coalesced in a final build,
using the highest numbered patch release. Two versions that differ in any other way might as well
not be the same library. The non-patch version is effectively part of the name of the library.

Different non-patch versions of a library are isolated from one another either at runtime (think
hierarchical class loaders in a JVM), or at link time (when turning a bunch of modules/libraries
into a single stand-alone application). There's no danger of name collision, even if we have to
squeeze a hypothetical language app into a primitive container like ELF, because names are not a
dozen ASCII characters give or take a few underscores, they're a hash of their library name,
library version, module name, symbol name and the name of my favorite Little Pony, just for good
measure.

Since libraries are distributed as serialized typed ASTs and the final build of an application is
at a much higher level than what is currently done by a linker, we have a lot of flexibility with
regard to what is automatically resolved and what might be manually resolved by a developer who
knows "yeah these libraries have different non-patch revisions, but trust me, just use this one."

I'm explicitly hand-waving over OS-level DLLs because I think they're a bad idea for application
software. OK for OS-provided libraries, maybe OK for OS-provided tools and apps, not worth the
downsides for just about anything else.

So anyway, to sum up:

 - modules import (and rename) names once, in a single preamble file
 - importing names and expressing dependencies happen in the same place
 - `libcompiler` obtains and manages dependencies automagically, via IPFS
 - all dependencies are resolved at build time and mashed into a single monolithic app

[Guava]: https://github.com/google/guava
[IPFS]: https://ipfs.io/
[Maven]: https://www.apache.org/
