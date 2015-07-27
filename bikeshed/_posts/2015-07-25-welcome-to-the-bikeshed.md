---
layout: post
title: Welcome to the bike shed
---

Like a small number of thoroughly sanity deficient programmers, I've long yearned to create my own
programming language. A couple of years ago, I started writing down language ideas that occurred to
me, or particular annoyances with languages that I was using at the time, and potential
alternatives. This sediment has massed into a fertile soil into which the seed of a new language
could be planted. Fortunately, circumstance and good sense have kept me from burdening the world
with yet another programming language, but the burning in my heart remains.

I expect that all of my half-baked ideas would fall apart when the rubber hit the road, and I'd
find myself compromising my way toward the same barely coherent mess that every long-lived and
widely-used programming language has become. But even that healthy skepticism is not enough to
deter me. To truly instill the requisite humility and despair that are necessary to prevent this
unholy act, I feel that I need to discuss these ideas in public and have thoughtful, intelligent
people (that's you dear reader) point out all the ways in which they will fail.

So welcome to the bike shed. My goal is a weekly post on a single programming language idea,
sometimes syntactic, sometimes semantic, which I hope will be met with thoughtful, reasoned
discussion and debate. If the occasional religious war breaks out, I can only hope the casualties
are minimal.

I also feel obliged to state my own prejudices up front. If you find them particularly offensive,
it may not be worth your time to try to convince me to radically shift my world view. But if you
have the stamina or the perverse desire, I welcome all well reasoned dissidence. Now then, the
list:

* Performance is non-negligible, but is also not the highest priority. I want a language in which I
can write video games that run on power-constrained mobile devices. That doesn't mean that every
abstraction needs to be zero-cost, but it does mean that non-zero-cost abstractions should have
easily estimable cost and should not wildly deviate from programmers' expectations.

* A language should be as simple as possible but no simpler. Simplicity is good, but is trumped by
practicality. If the only syntactically convenient way to iterate over a collection is by creating
a closure, you have failed this test.

* The programmer is smart and the compiler is (relatively) dumb. It's better to give the programmer
the tools to write code that the compiler can easily translate into efficient manipulation of the
von Neumann machine than to assume the programmer is dumb and place all the burden on the compiler
to turn highly abstract code into something efficient. The sufficiently smart compiler is a lie.
Give the programmer the choice of when to use high abstraction and its associated inefficiencies or
low abstraction and its relative efficiency. Above all, be predictable. We never want to employ
heuristics that may swing one way or another (with substantial performance implications) based on
seemingly unrelated changes.

* The benefits of garbage collection outweigh its cost. This is to some degree a corollary of the
performance prejudice, as most objections to GC have to do with its unpredictable effects on
performance. That said, I greatly desire a way to allow the use of stack allocation (or its moral
equivalent) when so desired. Per the smart programmer, dumb compiler prejudice, the programmer
should be able to decide between the simplicity of garbage collection (I usually want this), or the
performance of stack allocation (I sometimes want this).

In addition to these general prejudices, I will also admit to being a big fan of Scala. I feel
that Martin Odersky has exercised a great deal of good taste in shaping it over the years. He has
also been (understandably) very experimental, and has grown the language into a shambling mess, but
there are a lot of diamonds in that rough. Thus you'll find that I tend to operate from a baseline
position of "Scala: the good parts" and explore from there.

Now that we have these preliminaries out of the way, let's get on to the [first post].

[first post]: /bikeshed/2015/07/auto-destructure-args/
