---
layout: post
title: More fun than a box of docs
---

A short and belated post due to being distracted by [PAX] this week. [Editor's note: so distracted
that I didn't realize that I was actually early for my biweekly post, not late for my weekly post.]

This time, I want to put forth some opinions on documentation, specifically inline documentation
which is written directly in the code. This documentation is commonly extracted into a separate
HTML bundle (or PDF, etc.) for posting on the Internets. It is also nice when it is grokked by
IDEs.

### Markup

Javadoc made the terrible mistake of using a weird combination of HTML and custom tags, which kind
of works when your documentation is simple and then fails miserably when you try to do anything
interesting. Scala vastly improved upon this situation by using a wiki-like syntax with some
vestiges of Javadoc in the form of the `@` tags.

This is a big win, because two decades of experience with Javadoc has taught me that one spends at
least as much time reading docs directly in the code as one does via their HTML formatted
incarnation. Thus it's important that the docs be readable in your editor. [Github-flavored
Markdown] is a great, widely known foundation. Add a few rules for linking to types and members,
and you have a reasonable way to express yourself in ASCII:

```scala
/** Defines a key binding. Trigger sequences are defined thusly: A single key consists of
  * the key identifier (e.g. 'g', 'F1', '-',) prefixed by zero or more modifier keys.
  * Key sequences consist of single keys separated by spaces. Examples:
  *
  *  - `e`: lowercase e
  *  - `S-s`: upper case S
  *  - `C-c`: control-c
  *  - `C-c C-i`: control-c followed by control-i
  *
  * Modifier prefixes are as follows:
  *
  * Shift | Ctrl | Alt  | Meta
  * ------|------|------|------
  * `S-`  | `C-` | `A-` | `M-`
  *
  * The fn bindings are defined by the mode, by using the [[Fn]] annotation on methods. The
  * name in the keymap corresponds to the de-camel-cased method name (see [[Mode]] docs). When
  * a mode refers to its own fns, it may provide just the name, but if a mode (or a mode hook)
  * refers to another mode's fns, it must prefix the name by the name of the mode and a colon
  * (e.g. "scala:goto-term").
  */
case class Binding (trigger :String, fn :String)
```

An improvement over the status quo that I'd like to make includes allowing ADT class members and
method parameters to be documented directly, like so:

```scala
/** A single fn-binding. */
case class FnBinding (
  /** The mode instance from whence this binding came. */
  mode :Mode,
  /** The method to which the name is bound. */
  meth :Method,
  /** Whether the fn binding wants to be passed the typed character. */
  wantsTyped :Boolean) { ... }
```

instead of:

```scala
/** A single fn-binding.
  * @param mode the mode instance from whence this binding came.
  * @param name the de-camel-cased name of this binding.
  * @param meth the method to which the name is bound.
  * @param wantsTyped whether the fn binding wants to be passed the typed character.
  */
case class FnBinding (mode :Mode, meth :Method, wantsTyped :Boolean) { ... }
```

The astute reader will notice a bogus parameter in the above docs, which was actually there when I
pulled this example out of the code. A perfect example of why keeping the documentation as close to
the thing being documented is a goodness.

This approach might seem more radical when applied to function parameters, so I would probably
retain the ability to use `@param` documentation. This:

```scala
  /** Appends compiler status to our modeline status string and tooltip. */
  def addStatus (
    /** The builder for the status line. */
    sb :StringBuilder,
    /** The builder for the tooltip. */
    tb :StringBuilder) { ... }
```

is perhaps a bit clunkier than:

```scala
  /** Appends compiler status to our modeline status string and tooltip.
    * @param sb the builder for the status line
    * @param tb the builder for the tooltip */
  def addStatus (sb :StringBuilder, tb :StringBuilder) { ... }
```

But maybe there are more compact styles which would obtain in such cases:

```scala
  /** Appends compiler status to our modeline status string and tooltip. */
  def addStatus (
    /** The builder for the status line. */ sb :StringBuilder,
    /** The builder for the tooltip.     */ tb :StringBuilder) { ... }
```

Now we're getting crazy.

### Docs are code

Another essential characteristic of documentation in hypothetical language is that it is checked
every time you compile, along with all of the other code in the file. Doc comments are structured
text, just like everything else in a source file, and that structure should be checked.

If a doc comment references a type or member name, you should get a warning or error if that name
is invalid or ambiguous. If doc comments support special tags like `@param` and an invalid tag is
encountered, that should yield a warning or error. If we're able to detect syntax errors in the
markup format, those should be reported as well.

Maybe these are warnings and don't prevent you from compiling the code, but you should see them
every time you run the compiler (or in your IDE) so that you can fix them right after you make the
mistake, not weeks or months later when someone is building the docs in preparation for a release.

Furthermore, doc comments will be reflected in the AST and the documentation generation tools will
be just another client of libcompiler. The compiler will handle name resolution, and will use the
same standard reporting mechanism to report warnings and errors in the docs as it does in the code.
The doc generator will just walk the AST like anything else and regurgitate the data in the desired
format. No heavy lifting (or half-assed duplication of complex compiler logic) required.

As I mentioned in the post on [modularity], a serialized form of the AST will likely be what's
shipped as a library artifact, and this means that the documentation comes along for the ride. It
will not be a separate build artifact that needs to be managed and pushed through the development
pipeline, or not at all, due to laziness or forgetfulness.

[PAX]: http://prime.paxsite.com/
[Github-flavored markdown]: https://help.github.com/articles/github-flavored-markdown/
[modularity]: /bikeshed/2015/08/modularity/
