---
layout: post
title: Gradle Difficulties
---

When I first started doing Android stuff in earnest, I kept a list of Things That Sucked&trade;
about Android, but I never cleaned it up and published it. Now (years later) I've forgotten the
details of many of my infuriated scribblings, so I'm unlikely to ever publish it as I'd likely be
complaining about things that have long since been fixed and end up misleading anyone who happened
upon my rantings.

I'm now (foolishly) converting [a large project](https://github.com/greyhavens/bang-game) to
Gradle. This is my first time using Gradle in anger, so I intend to properly document my pains as
they happen, and publish them in a timely manner, so that they have some small chance of providing
benefit to the world beyond being a vent for my frustration.

The project has an ancient Ant build, which I had converted to Maven (with which I have copious
experience) about 80% of the way before becoming so frustrated that I decided to double down on
pain and frustration by starting over from scratch with a system I had no experience with. What
could possibly go wrong?

Here are my Gradle difficulties, roughly in order of appearance.

## Documentation

I don't expect a build system to have concise, accurate documentation pertaining to my exact use
case. Once you get beyond "compile some source files" (and sometimes even sooner) each project
veers off into its own corner of an infinite dimensional space. Fair enough. However, once a system
has seen wide enough use, one of the rare beneficial laws of software development kicks in:
"someone else has already had your problem" (these days with the corollary, "and asked about it on
Stack Overflow").

Yet in the three dozen Google searches I've done in my first two hours of Gradle usership, I've
seen maybe two Stack Overflow questions come up in my search results. Instead I see a bunch of
results from Gradle's forums, with a crappy UI and all the problems with finding the solution (if
any) among the uselessly linear series of responses. Or I get a link to the official Gradle
documentation, which is uselessly vague and never talks about the details I want.

Build system recipes and edge cases are about as close to the perfect body of knowledge for Stack
Overflow, so somewhere along the line a major failure has happened.

## Multi-project builds

The documentation for multi-project builds (multi-module projects in Maven-speak) is annoyingly
discursive. It starts out with an example about water and whales and krill. I'm not a fucking
marine biologist, I'm a Java programmer trying to build a Java project. Yes, I understand that your
all-singing all-dancing build system is so general that it can handle projects related to our
planet's vast ocean resources, in addition to my Java project, but I just need to know how to
properly specify a dependency between modules.

Where do I find that, you might wonder? 4,000 words in. After sections and subsections on
cross-project task dependencies, filtering projects by name/property/sexual orientation, and a
bunch of other bullshit that appears in the wild about one thousand times less frequently than
inter-module dependencies.

Maybe there needs to be a "Gradle for Java programmers" primer that dispenses with the aquatics and
just focuses on things you're likely to encounter in your average non-trivial Java project. Of
course, if that doesn't turn up in my Google searches, then it might as well not exist, so maybe
someone has already written such a thing and it languishes in obscurity.

(Two hours later...) Oh hey, [there it
is](https://gradle.org/docs/current/userguide/tutorial_java_projects.html), and it even contains a
multi-module project section with the only two things that I wanted to know: how to declare multiple
modules and how to declare cross-module dependencies. Too bad it didn't turn up in my Google
results earlier.

## No provided dependency scope or optional dependencies

Maven's dependency model is not the greatest, but when coming up with your brilliant replacement,
please don't omit things that are widely used and serve a useful purpose.

Sometimes you have a dependency that is only needed when building your project, but which should
not be exported transitively. And sometimes you have a dependency that you don't want to foist on
everyone, so you document that "if you use X, Y or Z in my API, then you need this other
dependency".

Maven provides the `provided` dependency scope for the former (which is a poor name due to the fact
that the feature was intended to solve a problem one rarely has, but just so happened to be useful
for solving a problem that people have all the time), and optional dependencies for the latter.
Gradle helpfully provides neither of these out of the box.

Fortunately, there's a [helpful blog post](http://www.sinking.in/blog/provided-scope-in-gradle/)
that explains the boilerplate that you have to put into every single one of your Gradle projects
that happens to make use of provided or optional depends. So at least figuring out how to solve the
problem wasn't too painful.

## Tab completion fail

What genius decided to name the build output directory `build` and the build file `build.gradle`? A
genius who never uses tab completion, that's who! I'm surrounded by barbarians.

## SourceDirectorySet and resource copying

One of the most common things to do in a Java build system (and likely any build system that's not
designed for use by clinically insane people, like C++ programmers) is to express rules that
enumerate a set of files in some directory tree. For example `src/**/*.java`. This should be a
fundamental building block in a build system, and it should be well documented, because people are
going to do crazy thing with it. I want all the files in directory A, except files in these
subdirectories, except these particular files in those subdirectories, which I do want. On
Wednesdays.

Ant, for all its legacy of horror, has such a building block and [decent
documentation](https://ant.apache.org/manual/Types/fileset.html) for it. Maven half-asses this (as
one would expect), but by some stroke of luck, most plugins seem to copy the same approach as the
[resource
plugin](http://maven.apache.org/plugins/maven-resources-plugin/examples/include-exclude.html), and
you can usually just use those same constructs for other plugins and they work.

Gradle also appears to avoid committing to so concrete a mechanism, preferring to preserve maximum
generality in its core (what do whales and krill know about file sets?). So it leaves things up to
the Java plugin, which defines "source sets" and a half-assed mechanism for enumerating and
filtering files.

In Ant, I had this ugly but tolerable code:

```xml
    <!-- copy global static resources -->
    <copy todir="${deploy.dir}/rsrc">
      <fileset dir="rsrc">
       <include name="**/*"/>
       <exclude name="avatars/**"/>
       <exclude name="boards/**"/>
       <exclude name="bonuses/**"/>
       <exclude name="bounties/**"/>
       <exclude name="cards/**"/>
       <exclude name="config/**/*.xml"/>
       <exclude name="effects/**"/>
       <exclude name="extras/**"/>
       <exclude name="goods/**"/>
       <exclude name="props/**"/>
       <exclude name="sounds/**"/>
       <exclude name="tutorials/**/*.xml"/>
       <exclude name="units/**"/>
       <exclude name="**/*.wav"/>
       <exclude name="**/source/**"/>
      </fileset>
    </copy>

    <!-- copy global static resources -->
    <copy todir="${deploy.dir}/rsrc/goods">
      <fileset dir="rsrc/goods">
        <include name="cards/**/*.png"/>
        <include name="gold/**/*.png"/>
        <include name="passes/**/*.png"/>
        <include name="purses/**/*.png"/>
        <include name="tickets/**/*.png"/>
        <include name="upgrades/**/*.png"/>
        <include name="*.png"/>
        <exclude name="**/source/**"/>
      </fileset>
    </copy>

    <!-- copy sounds -->
    <copy todir="${deploy.dir}/rsrc">
      <fileset dir="rsrc" includes="**/*.ogg"/>
    </copy>

    <!-- copy some per-town static resources -->
    <for list="${towns}" param="town" trim="true"><sequential>
      <mkdir dir="${deploy.dir}/rsrc/goods/@{town}"/>
      <copy todir="${deploy.dir}/rsrc/goods/@{town}">
        <fileset dir="rsrc/goods/@{town}">
          <include name="**/*.png"/>
          <exclude name="**/source/**"/>
        </fileset>
      </copy>

      <copy todir="${deploy.dir}/rsrc/cards/@{town}">
        <fileset dir="rsrc/cards/@{town}">
          <include name="**/*.png"/>
          <exclude name="**/source/**"/>
        </fileset>
      </copy>

      <copy todir="${deploy.dir}/rsrc/bounties/@{town}">
        <fileset dir="rsrc/bounties/@{town}">
          <include name="**/bounties.txt"/>
          <include name="**/*.png"/>
          <include name="**/*.game"/>
          <include name="**/*.properties"/>
          <exclude name="**/source/**"/>
        </fileset>
      </copy>
    </sequential></for>
```

In Maven, I had to dumb things down a bit, because the notion of looping over a dynamically
configured set of directories was far too advanced for the meager capabilities of the resource
plugin:

```xml
    <resources>
      <!-- global static resources -->
      <resource>
        <directory>${basedir}</directory>
        <includes>
          <include>rsrc/**</include>
        </includes>
        <excludes>
          <exclude>rsrc/avatars/**</exclude>
          <exclude>rsrc/boards/**</exclude>
          <exclude>rsrc/bonuses/**</exclude>
          <exclude>rsrc/bounties/**</exclude>
          <exclude>rsrc/cards/**</exclude>
          <exclude>rsrc/config/**/*.xml</exclude>
          <exclude>rsrc/effects/**</exclude>
          <exclude>rsrc/extras/**</exclude>
          <exclude>rsrc/props/**</exclude>
          <exclude>rsrc/sounds/**</exclude>
          <exclude>rsrc/tutorials/**/*.xml</exclude>
          <exclude>rsrc/units/**</exclude>
          <exclude>rsrc/**/*.wav</exclude>
        </excludes>
      </resource>

      <!-- sounds -->
      <resource>
        <directory>${basedir}</directory>
        <includes>
          <include>rsrc/**/*.ogg</include>
        </includes>
      </resource>

      <!-- per-town stuffs which we copy wholesale, then prune -->
      <resource>
        <directory>${basedir}</directory>
        <includes>
          <include>rsrc/units/**/unit.properties</include>
          <include>rsrc/units/**/*.png</include>
          <include>rsrc/props/**/prop.properties</include>
          <include>rsrc/props/**/*.png</include>
          <include>rsrc/bonuses/**/bonus.properties</include>
          <include>rsrc/bonuses/**/*.png</include>
          <include>rsrc/cards/**/*.png</include>
          <include>rsrc/effects/**/particles.txt</include>
          <include>rsrc/effects/**/particles.properties</include>
          <include>rsrc/effects/**/particles.jme</include>
          <include>rsrc/effects/**/icons.txt</include>
          <include>rsrc/effects/**/icon.properties</include>
          <include>rsrc/effects/**/*.png</include>
          <include>rsrc/extras/**/*.png</include>
        </includes>
      </resource>
    </resources>
```

Fortunately, I'm able to just eliminate the conditional town inclusion in this new build, so I
don't even need Gradle to be smart enough to deal with that. But even that's asking too much.

A Gradle "source set" includes one `SourceDirectorySet` for resources, which I can specify like
this:

```groovy
sourceSets {
  main {
    resources {
      srcDir 'rsrc'
      exclude 'avatars/**'
      exclude 'boards/**'
      exclude 'bonuses/**'
      exclude 'bounties/**'
      exclude 'cards/**'
      exclude 'config/**/*.xml'
      exclude 'effects/**'
      exclude 'extras/**'
      exclude 'props/**'
      exclude 'sounds/**'
      exclude 'tutorials/**/*.xml'
      exclude 'units/**'
      exclude '**/*.wav'
    }
  }
}
```

But there's no way to "go back in" and get all the `.ogg` files. I can't specify multiple
`resources` sections. Actually it's worse than that. I *can* specify multiple `resources` sections
and Gradle gleefully overwrites the first one with the second one, and blindly plows on doing only
the last thing I asked it to. No indication is given that I've screwed the pooch by specifying
`resources` twice. Yay for dynamic languages.

I can't specify multiple `srcDir`s in my one and only `resources` section. Or rather, I *can* but
the filters that follow apply to *all* the directories. So I can't use the "one crazy trick" of
specifying the same directory repeatedly, with different filter sets each time.

You might also think I could tack an `include` on after the `exclude` statements, and add things
back to the filter. Nope. Excludes win over includes, and order is irrelevant. At least I found an
explicit statement to that effect in a random forum post during my Googling, which saved me some
time wasted in trial and error.

I eventually found another forum post that proffers two solutions to the problem (which I have
since lost and can't find again, yay). The first is to add a `processResources` block which
manually copies the additional resources via a more imperative approach which allows me to do the
same thing multiple times with variations. I don't remember the second since I lost the blog post
and can't find it again. I think it involved adding a whole additional source set which then got
wired into the build via additional machinations.

Both of those options are unappealing. I want to declaratively express my resources, not use some
bastard combination of declaration and imperative code to get the right files into the right places
by hook or crook. And I don't want to have to add three levels of bullshit just to repeat myself at
the one level of abstraction that supports repetition. Having multiple resource directories each
with their own set of filters is not an esoteric requirement.

Further digging shows that I can write Groovy code to express my inclusions and exclusions
programmatically. That cluster fuck is going to look something like:

```groovy
  exclude { FileTreeElement elem -> (
    !elem.path.endsWith('.ogg') &&
    (elem.path.startsWith('avatars/') ||
     elem.path.startsWith('boards/') ||
     elem.path.startsWith('bonuses/') ||
     elem.path.startsWith('bounties/') ||
     elem.path.startsWith('cards/') ||
     (elem.path.startsWith('config/') && elem.path.endsWith('.xml')) ||
     elem.path.startsWith('effects/') ||
     elem.path.startsWith('extras/') ||
     elem.path.startsWith('props/') ||
     elem.path.startsWith('sounds/') ||
     (elem.path.startsWith('tutorials/') && elem.path.endsWith('.xml')) ||
     elem.path.startsWith('units/') ||
     elem.path.endsWith('.wav'))
  )}
```

Thanks for making me reimplement file patterns via string methods and boolean logic. Let's keep
that abstraction level high!

But of course, that doesn't work either. For some reason it's not including all of the ogg files
and instead of debugging that abomination further, I waded back into the sea of irrelevant Google
search results.

It turns out there *is* a file set abstraction called `FileCollection`, and a `FileTree` which
implements that, and `SourceDirectorySet` extends that. Looking at the Groovydoc for
`FileCollection`, it appears that I can add another file collection to it via the `plus` method.
Great.

```groovy
    resources {
      srcDir 'rsrc'
      exclude 'avatars/**'
      exclude 'boards/**'
      exclude 'bonuses/**'
      exclude 'bounties/**'
      exclude 'cards/**'
      exclude 'config/**/*.xml'
      exclude 'effects/**'
      exclude 'extras/**'
      exclude 'props/**'
      exclude 'sounds/**'
      exclude 'tutorials/**/*.xml'
      exclude 'units/**'
      exclude '**/*.wav'
      plus fileSet("rsrc") {
        include '**/*.ogg'
      }
    }
```

Now my build chokes with:

```
* What went wrong:
A problem occurred evaluating project ':assets'.
> Could not find method main() for arguments [build_9wtwj0ov76bkb3anz9lcv3s4i$_run_closure1_closure2@40d23c82] on project ':assets'.
```

This obviously means that I used `fileSet` when I should have used `fileTree`, and I naturally
immediately deduced that from the informative error message. Once that trivial little issue was out
of the way, I ran my build to discover that... no `ogg` files were copied.

Looking at the Groovydoc more closely, I realized that the `plus` method returns a new file
collection, which is going to get thrown away because `resources` presumably creates the One True
`resources` file set. No problem, there's an `add` method that adds to the current file collection.
Let's use that:

```
* What went wrong:
A problem occurred evaluating project ':assets'.
> Main resources does not allow modification.
```

This inspires further Groovydoc digging, where I see at the top of the `SourceDirectorySet`
documentation:

```
TODO - configure includes/excludes for individual source dirs, and sync up with CopySpec
TODO - allow add FileTree
```

Awesome. That comment might as well read: "TODO: any of the things that would allow MDB to make his
fucking build work."

OK, looks like we're adding some nice imperative `processResources` directives. I found an example
from some random mailing list which used `sourceSets.main.classesDir` which no longer exists. I
eventually determined that `sourceSets.main.output.resourcesDir` is what I want.

The final machinations turned out to be:

```groovy
sourceSets {
  main {
    resources {
      srcDir "."
      include "rsrc/**"
      exclude "rsrc/avatars/**"
      exclude "rsrc/boards/**"
      exclude "rsrc/bonuses/**"
      exclude "rsrc/cards/**"
      exclude "rsrc/config/**/*.xml"
      exclude "rsrc/effects/**"
      exclude "rsrc/extras/**"
      exclude "rsrc/props/**"
      exclude "rsrc/sounds/**"
      exclude "rsrc/tutorials/**/*.xml"
      exclude "rsrc/units/**"
      exclude "rsrc/**/*.wav"
    }
  }
}

task copyOggs (type: Copy) {
  from "."
  into sourceSets.main.output.resourcesDir
  include "rsrc/**/*.ogg"
}
processResources.dependsOn "copyOggs"

task copyTownBits (type: Copy) {
  from "."
  into sourceSets.main.output.resourcesDir
  include "rsrc/bonuses/**/*.png"
  include "rsrc/bonuses/**/bonus.properties"
  include "rsrc/cards/**/*.png"
  include "rsrc/effects/**/*.png"
  include "rsrc/effects/**/icon.properties"
  include "rsrc/effects/**/icons.txt"
  include "rsrc/effects/**/particles.jme"
  include "rsrc/effects/**/particles.properties"
  include "rsrc/effects/**/particles.txt"
  include "rsrc/extras/**/*.png"
  include "rsrc/props/**/*.png"
  include "rsrc/props/**/prop.properties"
  include "rsrc/units/**/*.png"
  include "rsrc/units/**/unit.properties"
}
processResources.dependsOn "copyTownBits"
```

That was something fairly trivial in Maven and nowhere naer as complex as the stuff that motivated
me to abandon Maven for Gradle. I eagerly anticipate getting to that stuff, assuming I don't have
to spend four hours on the next trivial requirement.

## Crappy parser

If I accidentally leave out a comma in a task definition, which I've done half a dozen times. I get
an annoyingly obscure and useless error message:

```
FAILURE: Build failed with an exception.

* Where:
Build file '.../bang/client/shared/build.gradle' line: 15

* What went wrong:
Could not compile build file '.../bang/client/shared/build.gradle'.
> startup failed:
  build file '.../bang/client/shared/build.gradle': 15: expecting EOF, found 'genService' @ line 15, column 6.
     task genService << {
          ^

  1 error
```

The actual error is the fact that a comma is missing after the `dir` argument in the `fileset`
call:

```groovy
task genService << {
  ant.taskdef(classpath: configurations.tools.asPath,
              resource: "com/threerings/presents/tools.properties")

  ant.genservice(header: "../../lib/SOURCE_HEADER",
                 classpathref: configurations.compile.asPath) {
    fileset(dir: sourceSets.main.java.srcDir includes: "**/*Service.java")
  }
}
```

Of course when I first wrote this, I forgot the comma after `classpath`, and `header`. So I got to
see this same error message three times in a row as I hunted for missing commas and fixed them.

The very first time I encountered this, I had no idea it was comma related, so I pored over my
.gradle file for ten minutes, commenting things out in a vain attempt to isolate the issue. I
eventually just got lucky and noticed that a comma was missing, fixed it, and the error went away.

Surely the Groovy grammar is not so loose that they can't provide a better error message in this
circumstance. Design your languages with human frailty in mind people! I'll gladly accept syntactic
strictures if it means I don't have to go on a goddamned wild goose chase every time I make a typo.

## Some Love

Now that I've done all that whining, let me mention some things that were pleasant, or even
downright easy.

**Tasks**: Defining new tasks was pretty painless. Stock tasks exist for common tasks like running a
shell command, running a JVM with an appropriately configured classpath, copying a bunch of files
somewhere. And when I needed to go beyond that, the basic task model was pretty straightforward,
unlike some other build systems I know (my evil eye is looking at you, SBT).

**Command line invocation**: Gradle does The Right Thing&trade; with regard to tasks meant to be
invoked from the command line (rather than integrated into the standard `build` process). I defined
a `client` task in my `client/desktop` submodule and could just run `gradle client` at the
top-level of the project, and it found that task in the right submodule and ran it, running the
appropriate dependencies along the way. That's a refreshing improvement on Maven, where I have to
either jam everything into its ill-conceived "build lifecycle", or sacrifice inter-module
dependencies because running Maven on a target in a particular submodule causes Maven to go into
brain-dead mode where it feigns ignorance of the full multi-module project and insists on resolving
all dependencies via the local Maven repository. I won't miss that idiocy.

**Ant integration:** Using Ant tasks was very easy. This ancient build I'm converting made heavy
use of custom Ant tasks, and invoking those from Groovy is simple and direct. Converting Groovy
paths to Ant paths was simple, and other than the above mentioned bad error message when you forget
a comma, the syntax is refreshingly terse compared to the original Ant XML, and heaven forfend, the
boilerplate extravaganza that is `maven-antrun-plugin`.

Overall, Gradle feels like Ant without the horrible XML syntax, plus a few useful parts of Maven
(particularly dependency management and sensible defaults for Java projects). I have philosophical
qualms with a build tool that is based on a Turing complete language from the ground up, regardless
of how declarative things *look* (c.f. the lack of error message when I had more than one
`resources` section).

However, Gradle also provides an apparently civilized API for obtaining project metadata in things
like IDEs. So when I get around to adding Gradle support to Scaled, I might be won over to the idea
that builds with no *actual* declarative components are fine, as long as you have programmatic
access to the metadata via the same code used by the actual build tool.

Gradle and I have only just got to know each other, but I'm tentatively optimistic about our
relationship. I'm not totally done converting the build. I may be back in a couple of days with
more complains, or perhaps more praise. Who knows?
