---
layout: post
title: "Why Maven Sucks: Act II"
date: 2010-09-08
---

Sun, in their infinite wisdom, circa Java 1.3, extended the jar file
specification to allow dependencies to be expressed inside jar files by adding
a [Class-Path] attribute to the `MANIFEST.MF` file. This causes the JVM (and
javac, and various app servers) to try to magically add dependent jar files to
the classpath. This is half-assed and wrong in too many ways to enumerate here.

Some other enthusiastic Sun engineer then helpfully added `activation.jar` to
`mail.jar`'s Class-Path attribute, under the multiple misguided assumptions
that no one would ever possibly need to use `mail.jar` without also having
`activation.jar` in their classpath, with precisely that name, and located in
precisely the same directory. Great.

Maven then upped the ante on this little fiasco by deciding that any time the
Java compiler generates a warning that they can't parse, they should fail the
build. Clearly it's critical that your build system be conversant in every
possible warning that might be emitted by your compiler. As a result, when I
fix their boneheaded decision to suppress warnings by default, my build now
fails with this demonstration of awesomeness:

```
could not parse error message: warning: [path] bad path element
"/home/mdb/.m2/repository/javax/mail/mail/1.4.1/activation.jar": no such file or directory
```

Thank you Sun, and thank you Maven.

For those of you arriving from the Googles and looking for a solution to this
problem, it is as follows: pass `-Xlint:-path` to javac to prevent it from
issuing a warning when it cannot find `activation.jar`. If you will be passing
this compiler argument via Maven, be sure to read [Why Maven Sucks: Act I]
before doing so, to avoid the various pitfalls that await.

I should further mention the irony that `activation.jar` is listed as a
dependency of `mail.jar`, but it's being placed in
`.m2/repository/javax/activation/activation/1.1/activation-1.1.jar` which is
where Maven wants it and not where the utility deficient `Class-Path` mechanism
expects it to be.

[Class-Path]: http://download.oracle.com/javase/1.4.2/docs/guide/jar/jar.html#Main%20Attributes
[Why Maven Sucks: Act I]: /blog/2010/09/why-maven-sucks-act-i/
