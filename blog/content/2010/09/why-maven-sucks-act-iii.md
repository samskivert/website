---
layout: post
title: "Why Maven Sucks: Act III"
date: 2010-09-08
---

The saga ([act i], [act ii]) continues.

More reasons why Maven sucks:

1. Plugin version handling sucks.

   I learned this when I was trying to get Maven to generate javadocs. I was
   configuring the Maven Javadoc plugin with links to external javadocs when I
   discovered the very handy `detectJavaApiLink` option. It automatically adds a
   link to the standard Java API javadocs whose version matches the version
   specified in the `-source` argument to your compilation.

   Imagine my chagrin when I discovered that it was not working. I tried enabling
   it manually, even though it claims to be enabled by default. I then ran Maven
   with debugging output to see if it would report what options it was passing to
   the javadoc tool, and happened to notice that `detectJavaApiLink` didn't appear
   at all in the dump of the plugin configuration.

   This inspired me to investigated the documentation and see that this option was
   added in version 2.6 of the plugin. I inspected my debugging output and saw
   that I had version 2.5. I had already chafed against the fact that all of the
   documentation I saw for Maven on the web contained POM snippets with arbitrary
   and almost universally out of date versions for the plugins. My solution to
   this was to simply remove the `<version>` element entirely from my plugin
   declarations.

   I assumed, foolishly, that this would cause Maven to use the latest version of
   the plugins. In fact, it seems to use some arbitrary version. My POM inherits
   from a POM which actually references version 2.7 of the javadoc plugin, so I
   have no idea where it's getting version 2.5. Perhaps it is some transitive
   dependency of some other plugin whose version number I have not specified.

   Further research into this matter revealed that I can specify `RELEASE` as a
   version and get the latest release version. Peachy. However, this is considered
   bad form by the Maven community because it may cause problems with your build
   if plugins decide to change the default values of their options. Is this really
   such a big problem that it's better to have everyone expend the effort to
   periodically manually upgrade their dozens of Maven plugins in their dozens of
   builds? Furthermore, is it such a big problem that it's worth having tens of
   thousands of web pages filled with instructions on how to do X or Y with Maven,
   all filled with ancient version numbers just waiting to bite newbie/lazy/tired
   Maven users in the ass?

   It's additionally annoying that all of the plugins implicitly included in your
   build are of some arbitrary version. If you want the latest version of those,
   you have to add to your already dangerously obese POM file an additional 5
   lines of boilerplate for each plugin you wish to keep up to date.

2. Maven could have magically handled Javadoc dependencies, but didn't.

   I also noticed, while poring over the documentation for the javadoc plugin, an
   option entitled `detectLinks`, which purports to automatically add `-link`
   arguments for the projects on which your project depends. Awesome! A
   longstanding annoyance with javadoc is that you have to manually configure it
   with URLs for every project whose types you reference in your own project so
   that it correctly links those types in the generated documentation.

   Maven goes to all this trouble to collect project metadata and publish it along
   with the project's artifacts. They *must* have included the project's javadoc
   API url among that metadata, and that is what `detectLinks` is using to
   automatically wire up the documentation.

   Nope. They did nothing nearly so useful. One of Maven's dead-on-arrival ideas
   from the early days was that it could generate a cookie-cutter website for your
   project, full of a bunch of boilerplate crap. In generating such websites, it
   would put your API documentation at `${project.url}/apidocs`, and that's
   exactly where the javadoc plugin expects to find the API docs for all of your
   dependent projects.

   You can take a guess at how many of my dependent projects have their API
   documentation at that URL. I'll give you a hint, you can count the number on
   zero hands.

[act i]: /blog/2010/09/why-maven-sucks-act-i/
[act ii]: /blog/2010/09/why-maven-sucks-act-ii/
