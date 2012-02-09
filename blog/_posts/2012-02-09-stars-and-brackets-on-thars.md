---
layout: post
title: Stars (and brackets) on thars
---

One of my colleagues recently committed a change with the comment:

    Log Message:
    -----------
    Switching from "type *varname" to "type* varname"

It gave me hope for the future, that we might overcome the legacy of K&R's
terrible mistake when they pioneered the styles `foo *pointer` and `foo
array[]`.

The world has mostly come around to the view that `[]` is part of the type, not
the variable (i.e. `foo[]` is how you declare 'array of foo'). In spite of this
enlightenment, people cling to the syntax `foo *pointer` even though obviously
`foo*` is how you declare 'pointer to foo'.

The reasoning is that you can declare:

{% highlight java %}
foo *pointerToFoo, stackAllocatedFoo;
{% endhighlight %}

which is plainly confusing and a bad idea.

Gosling was originally under the spell of K&R, but eventually came to his
senses. Unfortunately, in Java the legacy of that madness lives on:

{% highlight java %}
public class Test {
    public static void main (String[] args) {
        String[] one = {}, two = {}, three[] = {};
        // one and two are of type String[], three is of type String[][]
    }
}
{% endhighlight %}

Naturally, C clings firmly to the past. The following is perfectly legal, if
not the most maintainable, C:

{% highlight c %}
#include <stdio.h>

int main (int argc, char** argv) {
    char* foo = 0, bar = 0;
    printf("%ld %ld\n", sizeof(foo), sizeof(bar));
    return 0;
}

// prints 8 1 (or 4 1 if you're on ye olde 32-bit machine)
{% endhighlight %}

The perspective of the enlightened language designer is that special-purpose
type modifiers like `*` and `[]` are a bad idea.

Arrays can be handled simply. They should be a parameterized type. Scala and
Haskell (and probably other Haskell-influenced languages) get this right:

{% highlight java %}
val one :Array[String] = { "foo", "bar" };
{% endhighlight %}

No need for special syntax. In Scala's case they've disallowed the declaration
of multiple variables in the same clause, except for this weird construct which
is a concession made to support Scala's highly unfortunate `enum` pattern:

{% highlight java %}
val onePlusOne, twoTimesOne, fourDivTwo :Int = 2; // all vals bound to 2
{% endhighlight %}

Pointers are trickier business. For one, a civilized language doesn't have
pointers, which cuts the conversion pretty short. However, civilized languages
do have have reference types, which are basically the same thing without the
promise of naughtiness and adventure.

One can mostly get by with glossing over the difference between reference types
and value types. When you write:

{% highlight java %}
String foo = "bar";
int bar = 0xf00;
{% endhighlight %}

it's not a great mystery that `foo` is a pointer to a string and `bar` is just
an int on the stack. However, if you allow the creation of value types that are
larger than the machine's register size (e.g. `Vector3`, `Matrix4`, etc.), you
will invariably want to pass them by reference.

Java cleverly avoids this conundrum by restricting itself to only built-in
value types which fit into registers. That's the sort of approach that's likely
to get you [zero credit on your math homework].

C# takes a crack at a non-zero solution with `ref` parameters. You can declare:

{% highlight c# %}
void invert (ref Matrix4 matrix) { ... }
{% endhighlight %}

and your calls to `invert` will not involve copying 16 doubles. We can deduce
Hejlsberg's enlightened status by the fact that it's `ref Matrix4` and not
`Matrix4 ref` (the latter being the moral equivalent of `Matrix4*`).

However, I expect that if the JVM some day supports value types, Odersky's
modeling of them in Scala will be something even more enlightened, like:

{% highlight scala %}
void invert (matrix :Ref[Matrix4]) { ... }
{% endhighlight %}

Such an approach nicely mirrors `Array[Matrix4]` and avoids the need to
introduce a new keyword into the language. Scala already supports implicit
conversions, so a conversion from `A` to `Ref[A]` is as simple as:

{% highlight scala %}
implict toRef[A <: AnyVal] (value :A) :Ref[A] = null // implemented by compiler magic!
{% endhighlight %}

Under the hood, the compiler would emit the appropriate byte codes to indicate
by-reference-ness, but there's no need to sully the language with evidence of
those machinations when there are already perfectly good abstractions available
to model them.

C# also supports `out` parameters, allowing one to write:

{% highlight c# %}
bool getId (out int id) { ... }

int id;
if (getId(id)) {
  // I have an id!
}
{% endhighlight %}

`id` is considered uninitialized in the body of `getId` and must be initialized
before `getId` returns (modulo exceptions).

I'm not sure such a thing could be modeled with a standard OOP type system, so
perhaps this is a good case for special syntax. However, this whole mechanism
exists solely to support multiple return values. The combination of a `Tuple`
value type, deconstructing binding, and under-the-hood optimizations solve this
problem without special cases. The following is already legal Scala code:

{% highlight scala %}
def getId() :(Int,Boolean) = ...

val (id, gotId) = getId()
if (gotId) {
  // I have an id!
}
{% endhighlight %}

Just add value types to the JVM and said under-the-hood optimizations, and
you're good to go.

Anyhow, I seem to have rambled pretty far afield. I'll stop philosophizing and
go back to being happy that the world contains a few fewer `type *foo`
declarations. Some day we may live in a world where there are no `*`s upon
ours.

[zero credit on your math homework]: http://en.wikipedia.org/wiki/Triviality_(mathematics)
