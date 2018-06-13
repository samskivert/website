---
layout: post
title: "Why Maven Sucks: Act I, Reprise"
date: 2010-09-10
---

The XML insanity of passing compiler arguments to Maven like so:

    <compilerArguments>
      <Xlint/>
      <Xlint:-serial/>
      <Xlint:-path/>
    </compilerArguments>

has not surprisingly, come back to bite me in the ass. Most normal Maven
actions seem to have no problem with it, but when I try to actually publish my
project (via `mvn release:prepare`), I get the error I expected to see all
along:

    [INFO] ------------------------------------------------------------------------
    [ERROR] BUILD ERROR
    [INFO] ------------------------------------------------------------------------
    [INFO] Error reading POM: Error on line 104: The prefix "Xlint" for element "Xlint:-serial" is not bound.

This inspired further investigation into this problem. *Surely* among the
thousands of projects built with Maven, someone else is trying to pass one of
javac's myriad options that include a colon (e.g. `-proc:only, -Xlint:-foo,
-g:lines, -Xprefer:source, -Xbootclasspath/p:...,` etc.).

I eventually found [this improvement request], which has remained unresolved
for three years! Is passing arguments to the compiler not one of the most
fundamental things a build system has to do? Even make gets that right.

Someone even submitted a patch in November '09 to make `<compilerArgument>`
work as advertised. It has not been applied. The issue has not been assigned to
anyone. As far as I can tell the Maven developers are not even aware that this
is a problem. I'm not even sure these “Maven developers” actually exist. More
likely, Skynet sent Maven down to slowly sap the will of the programmers that
created it, ensuring that they never render it obsolete by releasing a newer
version.

**Update:**
I've since discovered that this seems to be the practiced workaround:

    <fork>true</fork>
    <compilerArgument>-Xlint" "-Xlint:-serial" "-Xlint:-path</compilerArgument>

Yes. Really.

[this improvement request]: http://web.archive.org/web/20100923154949/http://jira.codehaus.org/browse/MCOMPILER-62
