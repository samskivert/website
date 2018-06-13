---
layout: post
title: "Why Maven Sucks: Act I"
date: 2010-09-08
---

These ruminations were languishing in commit messages, which would normally
satisfy my urge to complain. But now I need to complain further, and don't have
a commit message in which to express my discontent, so I'm airing the whole
basket of dirty laundry here, and going on the record as a Maven hater. For
those of you privy to the original commit messages, my new complaints will be
aired in Act III. Now then, on with the show.

Reasons that Maven sucks:

1. Warnings and deprecations are not shown by default.

2. Documentation shows use of `<compilerArgument>` with multiple arguments in a
single element:

    `<compilerArgument>-foo -bar</compilerArgument>`

    which is a bald-faced lie. Only a single argument is allowed inside a
    `<compilerArgument>` element. If you try to use multiple arguments, you see
    that it uselessly tries to pass everything as a single argument:

        Failure executing javac, but could not parse the error:
        javac: invalid flag: -foo -bar
        Usage: javac <options> <source files>
        use -help for a list of possible options

    (Bear in mind that -foo and -bar were actual javac arguments, not the stand-ins
    I'm using here. I'm saving the real arguments for a punchline below.)

    Web search turns up “helpful” advice to use multiple elements:

        <compilerArgument>-foo</compilerArgument>
        <compilerArgument>-bar</compilerArgument>

    Fair enough, but also a bald-faced lie. After spending a bunch of time
    debugging why my compiler arguments were not working, I discovered that Maven
    was just (silently) using the last one and ignoring/overwriting all of the
    previous arguments.

    I had noticed while perusing [the documentation] that it was also possible to
    use the so-called “Map version” (whatever that means), which uses this
    completely stupid syntax:

        <compilerArguments>
          <foo/>
          <bar/>
        </compilerArguments>

    Why is that syntax completely stupid, you might ask? Well, dear reader, because
    the arguments that I'm actually passing are the following:

        <compilerArguments>
          <Xlint/>
          <Xlint:-serial/>
        </compilerArguments>

    which is a case study in how not to represent information in XML. I didn't even
    try that originally because I was sure that it would not work, given the wacky
    non-`[a-zA-z]+` nature of the argument I needed to supply. The fact that it
    does work gives me the fear. You might think the same thing [Charlie] did, when
    he replied to my original commit message:

    > Doesn't that create an element named -serial in the Xlint namespace?

    Apparently not, because it works.

    You might also wonder, like I did, whether the following form would provide
    satisfaction:

        <compilerArguments>
          <compilerArgument>-Xlint</compilerArgument>
          <compilerArgument>-Xlint:-serial</compilerArgument>
        </compilerArguments>

    By being absurdly verbose, it seems right in line with The Maven Way&trade;.
    However, that results in `-compilerArgument=-Xlint` and
    `-compilerArgument=-Xlint:-serial` being passed to the compiler. Hilarity
    naturally ensues.

[the documentation]: http://maven.apache.org/plugins/maven-compiler-plugin/examples/pass-compiler-arguments.html
[Charlie]: http://bungleton.com/
