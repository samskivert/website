---
layout: post
title: Old man yells at cloud
date: 2022-03-22
---

[Editor's note: This post vents anger at other software engineers. If they read it, they'll
probably feel bad. I hope they don't read it, as my goal is not to make them feel bad, it is merely
to express the boundless frustration I feel as we collectively fumble our way, in the dark, through
the never ending battle against complexity. Undoubtedly people who have used software that I have
created have felt equally frustrated and it is only by luck that I have not inspired sufficient
despair to motivate them to vent in a blog post.]

I'm here today to complain about Firebase, and specifically the Firebase Unity SDK. It is the most
annoying third party library I've had the misfortune of using in my nearly three decade career as a
programmer. I lack sufficient fingers to count how many times I have had to stop everything I'm
working on and redo some part of our build process, CI process, or whole goddamned game
architecture, thanks to Firebase's "we must implement everything ourselves in C++" philosophy, or
the half-assed efforts to package up this C++ monstrosity under a thin veneer of C# to make it
usable by Unity games.

I had used Firebase to good effect on a few web-based projects and was (and still am) enamored of
the concept of a reactive database (such that you can observe changes to data in your app). On top
of that, Firebase does some things that are quite useful for game developers, like auth and mobile
messaging. Neither of those are things I really want to implement myself. The former because I am
not an expert in auth best practices, and don't want to become one. The latter because I rolled my
own mobile messaging implementation back in 2008 and I still have scars from running a standalone
server to talk some custom protocol to Apple, for which I had to update the goddamned signing
certificates on a regular basis.

So there we are at the start of a new project, filled with optimism and excited to build on top of a
powerful foundation. That optimism quickly ran head on into the fact that, at the time, Firebase on
Unity was basically a technology preview. It was buggy, crashed our game all the time, and
generally made Unity's already problem-laden developer experience much worse. But I have a lot of
optimism, my rose colored glasses would not be removed so easily. So we soldiered through this pain
and did our best to give useful feedback and improve the Unity Firebase SDK as it grew into a real
product.

This tale of woe has been ongoing for over two years now, so I have suppressed most of the horrific
memories in order to continue functioning as a human being, but I will dredge up and describe a few
things to pave the road toward the latest crimes against basic decency. I can only hope that
writing about them will ease the pain.

## We have eyes on the package

The first major affront to good sense was a tag-team effort by Unity and Google. Unity lived the
first decade or two of its life without anything resembling a civilized package management system.
They had a way to package up third-party code and distribute it (via the Unity Store) which boiled
down to a slightly more automated version of "unpack this tarball into your game's source directory
and then commit everything to your VCS". (If I was telling this story in person, you would now see
me make a face that was a combination of disgust and sadness.) So naturally Google just used this
mechanism to distribute the Firebase packages.

I will admit that my distaste for committing (ostensibly) immutable third party code to my VCS
repositories is somewhat anachronistic: disk is cheap and networks are fast, why worry about it?
But recall: Firebase is a C++ monstrosity that must reinvent all wheels. "Installing" the Firebase
packages into your Unity project spits about 300MB of DLLs and related ephemera into your poor
unsuspecting VCS repository. And of course, you'll need a fresh 300MB of wheel reinvention every
time Firebase fixes a bug, and they've had
[a lot of bugs](https://firebase.google.com/support/release-notes/unity) to fix.

So, marching toward a future where checking out our game code meant also pulling down 10GB of
Firebase's sordid release history did not seem particularly appealing. Indeed, I would characterize
my opinion at the time as "fuck no." So I wrote some scripts that installed the Firebase packages
we needed and just made everyone run those after checking out the game. And again, every time we
upgraded Firebase. Not ideal, but I was sure as hell not committing all that bullshit to our git
repo.

Fortunately, around that time Unity had finally noticed that the rest of the world had been using
dependency management systems for the last ten or fifteen years and decided to make one of their
own. They did so, and Google thankfully started using it. Of course Unity made some stupid design
decisions, and Google made some questionable decisions themselves in working around Unity's design
decisions, but at least we had moved from pre-history into the stone age.

To distributed their packages via the new Unity package management system, Google had to host an
NPM registry. Unity's new system basically just copied most of NPM, pinnacle of software
engineering excellence that it is. Annoying, but surely not a terrible burden for a multi-billion
dollar company with tens of thousands of engineers on staff.

Then the winds inside Google shifted, as they do, and "making it easy for games to access our cloud
stuff" was no longer the golden road to promotions. So all the vaguely game adjacent PMs flocked to
Stadia or some other shiny new thing, and the Firebase SDK for Unity team was left with, as far as I
can tell, a couple of interns and an office dog. Aside from slowing down development, this also
apparently meant that maintaining an NPM server was too great a burden for mighty Google. They
summarily shut it down and went back to telling everyone to commit another 300MB of native code to
their game repo every time Firebase shipped a new release.

After being forcibly restrained from going down to Mountain View to do violence to the interns, I
implemented a "workaround" to this problem. You see, Google started shipping the `.tgz` files that
contained the NPM packages that they _would_ have uploaded to their own registry. So it is possible
for someone to download those `.tgz` files and manually upload them to their own private NPM
registry. Every time Firebase ships a new release. This is something I've had the pleasure of doing
for the last year or so.

"Luckily" we were _already_ running our own private NPM registry for reasons completely unrelated
to Unity or Google. Reasons that stemmed from a different clown car shit show called the JavaScript
developer ecosystem. No complaints blog post about that though. I'll have to take those complaints
to my grave, because writing about them would finally break me and leave me with no choice but to
retire from software development and ranch
[alpacas](https://www.boredpanda.com/cute-alpacas/?media_id=3258351).

## How do you like them apples?

After early enthusiasm, repeated suffering, and finally overwhelming regret, I invested the effort
to minimize our use of Firebase to the barest essentials everywhere in the project. Early on, I
rearchitected things such that the game client did not access Firestore directly. Instead that was
completely handled by the server (which still uses Firestore, and though there have been some road
bumps, their C# server libraries are quite robust, and maintained by the inimitable and illustrious
[Jon Skeet](https://twitter.com/jonskeet)). Later I removed Firebase Analytics (which was frankly
terrible for our use cases anyway) and replaced it with Amplitude (which also leaves a lot to be
desired from a software quality standpoint, but mostly does what we need).

So for a while, all was quiet on the western front. There were the usual annoyances and bumps in
the road, but nothing that inspired a desire in me to do violence to the responsible parties. Then
the winds of change started blowing once again. The new Apple Silicon Macbook Pros were coming out
and there was much excitement among our all-Macbook-Pro-using engineering team. It did not take a
rocket scientist to predict that Firebase was not going to work "out of the box" on ASi Macs.

There seem to be quite a few Unity developers using Firebase, it's not some obscure side project.
But game developers are on the whole an uncivilized lot, they just want to entertain people, they
don't really care about whether their game is built using a robust and well engineered process.
This is a completely fair, and probably sensible, set of priorities. Such people probably make
better games than we do. But I cannot give up my idiosyncratic desire for software quality. It's
that or alpaca ranching. So we were already in sparse company trying to get the Firebase Unity team
to make things not suck.

On top of that, most game developers develop on Windows. So we're oddballs for developing on
the Mac. And my insistence on not committing the Firebase libraries to our git repo made us even
more "unique." A good example was
[this bug](https://github.com/firebase/firebase-unity-sdk/issues/89) wherein Unity crashed
immediately on startup if you were using Unity 2021 (not 2020) and the "installed via the package
manager" version of the Firebase libraries (not the "commit them to your VCS repo" version). Life
on the fringe.

Well, now we were moving even further onto the fringe. We wanted Unity and Firebase to work
together on our fancy new Apple Silicon Macs. The architecture had been in the wild for about a
year, and Unity was just shipping alpha versions of editors that ran natively on Apple Silicon.
What could possibly go wrong?

I didn't expect Google to be proactive in supporting ASi for Firebase, certainly not for "Firebase
repackaged for Unity". People had been asking for this support for about six months when I showed
up, and there had been some progress in
[hacking together workarounds](https://github.com/firebase/quickstart-unity/issues/1100).
I girded my loins and cloned the Firebase C++ repo along with the Firebase Unity SDK repo and waded
into the battle. There was a lot of swearing, a lot of lamenting that things like cmake are still
"state of the art" in the year of our lord two thousand and twenty one (when I was fighting this
battle), and occasional urges to go down to Mountain View and stab a few people.

In the end, I emerged with all of my limbs still intact, a deeper knowledge of how Firebase is
built and packaged (that I desperately did not want), functioning native libraries for Apple
Silicon Macs, and a set of workarounds that allow us and others to use them. I also emerged with a
foolish optimism that, given the ultimately straightforward changes needed to get things building
for ASi, Google would patch things upstream fairly quickly. It warms my heart to know that I'm
still capable of such naive optimism at my age.

Fast forward five months or so, and I'm still manually rebuilding Firebase every time Google does a
new release, and
[making the native libraries available to the community](https://github.com/firebase/quickstart-unity/issues/1100#issuecomment-1029364297).
And, of course, I'm manually uploading the patched packages to my own private NPM registry, which
I've already been doing for a year -- ever since Google decided it couldn't be bothered to host one
of its own. Hey, two wrongs actually made a right this time! Sorta.

## A serpent in the Garden of Eden

Life on the fringe continues to be full of surprises. Most recently, Apple _finally_ stopped
shipping Python 2 with macOS (Python's migration from 2 to 3 being yet another clown car shit show
that we software developers can be proud of). I had already worked around half a dozen problems
with the Firebase SDK build because it assumed whaver `python` executable was on your path was
Python 3. On macOS, `python` is Python 2. But that is Apple's sin, not Google's. Google's sin is
much worse.

At some point in the orgy of duct taping that took place during the initial creation of the
Firebase Unity SDK, someone needed to generate an XML file. "Firebase Team JavaScript" likes their
configuration in JSON, and "Firebase Team Android" likes their configuration in XML. "Firebase Team
Unity" decided you would download the `.json` file and keep that around, and they'd convert it to
XML as part of preparing things for the Android build. Fair enough.

Unity's build process is customized by writing C# code, just like you use when you write your game,
but with a bunch of `UnityEditor` APIs. So this person was writing C# code and needed to convert a
`.json` file into an `.xml` file. They were writing code via the robust, mature, fully featured
.NET platform, which is naturally supported on every platform that Unity supports, and which has
undoubtedly been used thousands if not millions of times to perform this very same task. Did they
choose to write some C# code to convert this `.json` file to an `.xml` file? No they did not.

Instead, they chose to
[fork off a Python interpreter](https://github.com/firebase/firebase-unity-sdk/blob/main/editor/app/src/GenerateXmlFromGoogleServicesJson.cs)
and have that run a Python script that does the conversion. Yes, this Python script already
existed. Yes, someone wrote that script as part of the Firebase C++ SDK, so the bad decision had
"already been made". But that does not excuse this _egregiously bad_ system design choice. You are
making a third party library for a cross-platform game engine. Do not just cavalierly add a
dependency to a _completely different software development stack_. Are you insane? Did the office
dog write this code? Because even an intern might think twice about doing something like that.

But like everything in the software development world, tragedy begets tragedy. Not only did they
add a dependency to an entirely different, slow, complex, and fragile software development stack,
just to _convert a fucking JSON file to an XML file_. They added a dependency to a stack that blew a
giant hole in its own foot about ten years ago and is still suffering the consequences of it. And
they did more than that. They added this dependency in a way that assumed running "whatever
`python` is on the path" was naturally going to be the version they needed.

Let's also step aside and take a moment to appreciate the engineering prowess on display here, by
noting that the particular part of the Unity build process that they hooked into... to convert a
`.json` file to an `.xml` file... by forking off a Python interpreter... runs _every time any file
changes in your whole goddamned game project_. Any Unity developer who's used the platform for more
than 15 minutes knows that it's really annoying that Unity has to rethink the entire universe
whenever anything in your project changes. There are tremendous benefits that derive from this
architectural choice, but the pain is real and it accumulates into years of wasted life on the part
of millions of game developers. So how about not pointlessly exacerbating that problem with fucking
Python?

But I digress. So here we are in the year of our lord two thousand and twenty two, and Apple
_finally_ decides to stop shipping Python 2 with macOS. Did they wait too long to do this? They
most likely did. I'm sure they had their reasons. It's tragedy all the way down here in Software
Development Land. But when they did, the quality product of our (possibly canine) protagonist up
above came to a screeching halt. Now as soon as you so much as change a line of code, Unity
attempts to recompile it and rebuild the universe, the Firebase Unity SDK attempts to bring Python
to that party, and the whole house of cards
[comes tumbling down](https://github.com/firebase/quickstart-unity/issues/1232).

On the plus side, Google has actually taken action to fix this problem, unlike the "your entire SDK
does not work on Apple Silicon Macs" problem, which is still TBD five months later. Four days ago,
they
[merged a change](https://github.com/firebase/firebase-unity-sdk/commit/fcd398907f857c6af379b1ec40d2b067a30392ed)
that forks off the Python executable repeatedly, using different names than `python` until one
succeeds... every time you change a file in your game project.

But not to worry, it doesn't actually fix the problem. It just generates an error message instead
of crashing the entire Unity editor:

```
Unable to find command line tool python required for Firebase Android resource generation.
python is required to generate the Firebase Android resource file google-services.xml from Assets/GoogleService-Info.plist. Without Firebase Android resources, your app will fail to initialize.
python was distributed with each Firebase Unity SDK plugin, was it deleted?
```

I'm going to just ignore the fact that it says "python was distributed with each Firebase Unity SDK
plugin", both because I cannot find evidence that that statement is true, and because if it _was in
fact true_ I would have to either immediately drive down to Mountain View and do violence to some
responsible party, or give up software development forever and frolic with the alpacas.

## The wheel of Samsara

So here we are, dear reader, at the end of my tale of woe. I spent my morning rebuilding a patched
version of the Firebase Unity SDK, only to find that they fucked up and failed to fix the problem.
And the problem itself was so egregious, that in order to maintain my sanity, I had to write this
blog post. And now I must gird my loins and go debug, and then fix, their fucking fix so that
maybe, if the fates are kind to me, I can get back to making a video game today.

## The morning after

Brief addenda here, just to report that failure of the upstream fix to fix things was actually my
error. The process of creating a patched version of the Firebase Unity SDK that works on ASi Macs
is complex, time consuming, and easily messed up. And I messed it up. Now, in order to do it
properly, I have grapple with new "completely unrelated to my actual goals" problems.

I have learned that there are two formats for the debugging files that accompany C# DLLs: `mdb`
files (no relation) and `pdb` files. Unity uses `mdb` files and those are only generated when
building libraries on Windows. When building libraries on the Mac, `pdb` files are generated. So I
have to set up a Windows development machine with the necessary compilers and SDKs to compile the
C# parts of the Firebase Unity SDK, and then hand merge those into my patched SDK. Even if I were
crazy enough to want to set up a C++ compiler toolchain on Windows, I would not be able to build
the entire patched SDK on Windows, because I still need to build native libraries for ASi Macs,
which can only be done on a Mac.

I can only hope that this second day of my unwanted odyssey will see it full resolved. The Fates
may yet require more sacrifice.
