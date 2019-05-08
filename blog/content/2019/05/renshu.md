---
layout: post
title: Practice makes perfect
date: 2019-05-08
---

Hey kids, gather around the campfire. It's time for side project story time with Uncle Mike. Today
we're going to talk about a little app I made to support my taiko practice.

The motivation for the app is to answer the question "What am I practicing today?" when I go down
to my practice space and prepare to make a lot of noise banging on drums. I'm trying to stick to a
program of [deliberate practice], which means I don't want to just noodle around playing whatever
pops into my head. I want a specific, concrete list of things that I'm working on, and I want to
make sure I cover those things first while my energy level is high. After that I allow myself to
play around doing whatever seems fun, because all work and no play makes Mike a dull boy.

If I were a normal person, I'd just use a spreadsheet, or a piece of paper, or maybe some fancy
[list tracking program] to maintain this list. But that would be ignoring an opportunity to write a
whole bunch of code, and swear a lot about how much I hate HTML and CSS. So naturally, I created a
custom mobile-friendly webapp that allows one to create a database of songs, drills, techniques,
and advice, and use that database to maintain a queue of things to practice. And because I also
obsessively track all the things, naturally it keeps a detailed log of what you practice and when.

## Renshu

The app is called Renshu (yes, I know the proper romanization of 練習 is renshuu, but two u's in a
row in English is too weird, so I'm sticking with my incorrect version). A picture is worth ten
thousand words, so let's show some screenshots and save you from too much more reading.

<div style="display: inline-flex; flex-wrap: wrap">
<div style="margin: 0px 20px; width: 200px"><img width="100%" src="../renshu-songs.png" /></div>
<div style="margin: 0px 20px; width: 200px"><img width="100%" src="../renshu-song-edit.png" /></div>
<div style="margin: 0px 20px; width: 200px"><img width="100%" src="../renshu-drills.png" /></div>
</div>

Here you can see the song list, editing a song, and the drills list. Songs have specifically
enumerated parts and, just for fun, they are annotated with an emoji indicating your learning
status for the part. As you can see my repertoire is a sea of "ignorance" monkeys with a few
"learning" and "refining" smileys sprinkled around.

One can also add links to video recordings of the songs, which I find very helpful to refer to when
practicing. Taiko is a full body choreographed art.

<div style="display: inline-flex; flex-wrap: wrap">
<div style="margin: 0px 20px; width: 200px"><img width="100%" src="../renshu-advice.png" /></div>
<div style="margin: 0px 20px; width: 200px"><img width="100%" src="../renshu-pq.png" /></div>
<div style="margin: 0px 20px; width: 200px"><img width="100%" src="../renshu-plog" /></div>
</div>

Here we have the advice list, practice queue and practice log. Streamlining the process of
recording advice that I get from other members of my group, and then getting it in front of me the
next time I practice is something I really like. My previous pencil and paper and/or iPhone Notes
app solutions to this problem resulted in a lot of advice falling through the cracks.

That's about the size of it. I plan to add priorities to the practice queue, and a way to track
performances (just because I love to keep track of things). I'm sure that once I've amassed a few
juicy months of practice log data, I'll add some way to visualize that as well.

In the unlikely event that you too are a taiko player, feel free to double the size of the app's
current user base. It is available at [renshu.app] for all of your taiko practicing needs. And
because I love to share, the [source code](https://github.com/samskivert/renshu) is also available.
In the extremely unlikely event that you are also a programmer _and_ taiko player, feel free to
send pull requests.

[deliberate practice]: https://en.wikipedia.org/wiki/Practice_(learning_method)#Deliberate_practice
[list tracking program]: http://samskivert.com/blog/2018/11/pim-samsara/
[renshu.app]: https://renshu.app
