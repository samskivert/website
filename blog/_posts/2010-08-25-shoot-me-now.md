---
layout: post
title: Shoot me now
---

I'm foolishly trying to write a javac annotation processor in Scala. This has
already been a road fraught with gratuitous obstacles, but the latest wrinkle
has driven me to vent in public.

When I run my annotation processor thusly:

    javac -processor FooProcessor -processorpath fooproc/classes:blah/scala-library.jar SomeFile.java

I get `java.lang.IllegalStateException: zip file closed` when my processor
tries to load Scala classes. But if I `unpack scala-library.jar` into
`fooproc/classes` then it works. Joy! Somehow the `scala-library.jar` file
causes javac to choke.

I of course tried repacking the jar file, in case perhaps something in the
stock `scala-library.jar` was weird, but using the repacked version still
failed with `zip file closed`. What's even more awesome is that javac 1.6 does
not have a problem with `scala-library.jar`, only javac 1.7.

