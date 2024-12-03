---
layout: post
title: Advent of Rust Day 2
date: 2024-12-02
---

In Day 2 we need to do "things" to the consecutive pairs in a list of ints. The Rust standard
library was created by thorough, right-thinking people, so getting consecutive pairs from a `Vec`
is pretty easy:

```rust
nums.windows(2).map(|w| (w[0], w[1]))
```

But I'm not a barbarian. If I need to do three or four things to this list of pairs, I'm not going
to reify that transformation into a concrete collection. I'm going to fold over the transformed
collection and compute what I need along the way. So I write the following:

```rust
let pairs = nums.windows(2).map(|w| (w[0], w[1]));
(ps.iter().all(|(a, b)| a < b) || ps.iter().all(|(a, b)| a > b)) && ...etc...
```

In my baby steps with Rust so far, I've learned that you have to call `iter()` on concrete
collections like `Vec` to get an iterator and do transformations. So let's call `iter()` on the
"thing" that's returned by `map` called on `windows`. What could possibly go wrong?

Well, if I'd have thought deeply about it, the fact that I have to call `iter()` at all should have
been my first clue that things were not as I expected.

Rust's sequence transformation operations are defined for iterators (`trait Iterator`), things that
yield each element of the sequence in turn and are consumed in the process, rather than iterables,
objects that can provide iterators over the underlying elements on demand (called `trait
IntoIterator` in Rust). Indeed, if they were defined to operate on iterables, `Vec` could just be
an iterable and I wouldn't have to call `iter()` on it before calling `map()` to transform it.

So OK, that's not going to work. I'm going to have to call `windows` and `map` over and over again,
every time I need to do something to the pairs of my list. Let's at least put that code into a
function. So I wrote:

```rust
fn pairs(nums: &Vec<u32>) -> ... {
    report.windows(2).map(|w| (w[0], w[1]))
}
```

But what's the return type of this function? My IDE helpfully tells me it's
`Map<Windows<'_, u32>, impl FnMut(&[u32]) -> (u32, u32)>`, which is pretty hairy, but hey, let's
give it a whirl. I figure at this point that we're going to have to get into lifetime management,
and I see that anonymous lifetime `'_` in the `Windows` signature. So we'll try to use that to say
that our return value has the same lifetime as our argument:

```rust
fn pairs<'a>(nums: &'a Vec<u32>) -> Map<Windows<'a, u32>, impl FnMut(&[u32]) -> (u32, u32)> {
    nums.windows(2).map(|w| (w[0], w[1]))
}
```

Given that I have only a very rough understanding of how lifetime checking works, that worked about
as well as could be expected. Not at all. Compiler says `lifetime may not live long enough`. So
true compiler, so true. However, it also gave some very helpful explanations and suggestions. It
says we need to tell it that our mapping function also captures data from our argument and thus has
to have the same lifetime bound. So we try that:

```rust
fn pairs<'a>(nums: &'a Vec<u32>) -> Map<Windows<'a, u32>, impl FnMut(&[u32]) -> (u32, u32) + 'a> {
    nums.windows(2).map(|w| (w[0], w[1]))
}
```

Can you spot where I inserted a `'a`? It's _Where's Waldo_, programming edition. Anyway, compiler
still not happy. It also suggests we add `move` before our mapping function. Not sure what that
means, so I look it up: _"A moving closure takes ownership of all variables that it uses. Ordinary
closures, in contrast, just create a reference into the enclosing stack frame."_ This closure
doesn't reference anything in its enclosing stack frame, so I'm not filled with confidence, but
let's cargo cult a `move` keyword in there and hope for the best.

Nope.

Well, the compiler is out of helpful hints to give me and all I have to go on is _"returning this
value requires that `'a` must outlive `'static`"_ which sounds more like a "you are totally hosed"
than a "you forgot to do some simple thing". So, time to change tactics.

The old adage in computer science used to be: "You had a problem. You decided to use regular
expressions to solve your problem. Now you have two problems." But that's quite obsolete. We have
developed _much_ better problem multipliers in this glorious gilded age. I didn't use a regular
expression to solve this problem, I used an LLM.

But I'm a careful LLM user. I explain my problems in detail and give it clear guidelines as to what
sort of solution I'm looking for. I consulted Dr. Claude and here's what it came up with:

```rust
struct ConsecutivePairs<'a> {
    slice: &'a [u32],
}
impl<'a> ConsecutivePairs<'a> {
    fn new(slice: &'a [u32]) -> Self {
        ConsecutivePairs { slice }
    }
}
impl<'a> IntoIterator for &ConsecutivePairs<'a> {
    type Item = (u32, u32);
    type IntoIter = Map<Windows<'a, u32>, fn(&[u32]) -> (u32, u32)>;
    fn into_iter(self) -> Self::IntoIter {
        self.slice.windows(2).map(|w| (w[0], w[1]))
    }
}
fn pairs<'a>(nums: &'a Vec<u32>) -> ConsecutivePairs<'a> {
    ConsecutivePairs::new(nums)
}
```

I will gloss over the fact that this giant matrix of numbers just created a rather sophisticated
piece of code that accomplishes what I want, within the highly non-trivial context of the Rust
collection framework. I've gotten used to such paltry miracles. And to Claude's credit, the code
compiled and worked correctly right out of the box.

But my goal is not mere correctness. I long for elegance. For brevity. For code that makes the
whole world sing. So I followed up immediately with my aesthetic complaints. "Really Claude? I need
all of this machinery to accomplish this simple task? Is there no clever use of the built-in
collection types in Rust that will do what I want without all this... boilerplate?"

Claude patiently explained to me how the code worked, how it fit cleanly into the abstractions
defined by the Rust standard library, how it did it all with zero allocation overhead, and how it
accomplished exactly what I asked for. Touché Claude, touché.

But I'm not going to let this big bag of numbers push me around. I know a thing or two about
programming languages. At the end of the day, the thing returned by `windows(2).map(...)` has to
conform to the `Iterator` trait. So can't I just go back to my first approach and claim that the
function returns an iterator? Let's try it!

```rust
fn pairs(nums: &Vec<u32>) -> Iterator<(u32, u32)> {
    nums.windows(2).map(|w| (w[0], w[1]))
}
```

OK, so that didn't work. I am literally just making up syntax, so what did I expect? But again, the
compiler helpfully piped in with some good error messages. `Iterator` is not parameterized, but
instead has what I think is called an "associated type". So I gotta specify that correctly. And
then the compiler said "hey, there's a secret hidden lifetime in there that you can't sweep under
the rug" but helpfully suggested that I add `+ use<'_'>` to note that it is captured. I couldn't
tell you precisely what that does, but let's cargo cult it on in:

```rust
fn pairs(nums: &Vec<u32>) -> impl Iterator<Item = (u32, u32)> + use<'_> {
    nums.windows(2).map(|w| (w[0], w[1]))
}
```

And it works. Take that Claude! There was in fact a way to accomplish my goals with a clever use of
the built-in collection types. Well, clever may be in the eye of the beholder, but at least I'm not
defining a whole separate struct with multiple trait implementations.

I even sorted out how to do this in a slightly less mysterious way:

```rust
fn pairs<'a>(nums: &'a Vec<u32>) -> impl Iterator<Item = (u32, u32)> + 'a {
    nums.windows(2).map(|w| (w[0], w[1]))
}
```

Here we name the captured lifetime up front and report that our result has the same lifetime. I'm
not sure if this is more "idiomatic" since I'm a total noob, but it seems clearer to me.

I don't love that the signature of my `pairs` function is twice as long as the code inside the
function, but I understand that you gotta break a few readability eggs if you want to make an
ownership tracking omelet. So I'm going to keep sippin' on this kool-aid.

I am also thankful that the early days of AoC are pretty easy, because learning how to declare and
call functions in Rust is going to take all my spare brain cells for a while.
