---
layout: post
title: Happy New Year to Me!
---

I logged into the server that hosts samskivert.com yesterday and noticed that
someone had done a very ham fisted job of hacking into my server (in addition
to whatever nefarious business they hacked in to perpetrate, they changed the
uid of my primary account to 0 and left the passwd file thusly modified).
That's what I get for having anything other than Apache and sshd running.

Naturally, the only thing to do in such a case was to take off and nuke the
site from orbit. So I slurped down all the data from my [slice] and pressed the
big red button that wiped the slice and reinstalled a fresh Ubuntu image
(upgrading everything in the process, how nice).

Then I copied all the data back up to the machine and started setting things
back up. That's when I realized that I forgot to grab the MySQL databases.
Oops! No problem, I have scripts that back everything up to S3. I fired up the
S3 console and discovered that the directory that should contain the database
backups was empty. Double oops! I guess something broke at some point in the
five years since I set all that up.

The only thing that used MySQL was my WordPress install. There went thirteen
years of blogging. I was ready to declare the world a better place for lack of
my inane writing, but then I realized that Google Reader probably had most of
my blog archived. Not only that, but it made it pretty easy to slurp down said
archives.

One of the things I do to simplify my life, now that I'm old and forgetful, is
to give up on trying to re-memorize things that I've forgotten, and instead to
change the world to match what little I can remember. For example, if I forget
the password to some account and have to reset it, I change it to the first
thing I guessed might be my password when I was trying to log in. In this case,
since I forgot that I had MySQL databases that needed copying, I should
probably avoid creating new databases that I will undoubtedly forget about the
next time this sort of thing happens.

Instead of setting WordPress back up, I set up [Jekyll]: a bunch of Ruby
scripts that generate a blog based on flat files. The flat files can be
committed to a Git repository. I definitely did not forget to copy my Git
repositories. Indeed, I went one better and just moved
[my website Git repository] to Github so that I can not worry about backing it
up in the first place.

A few Perl and Scala scripts later and I had converted the Google Reader blog
archives into a format acceptable to Jekyll. It turns out the Google Reader
archives only went back to 2005 or so. Having come this far, I didn't feel like
settling for recovering only half of my blog, so I slurped down the Google
caches for my web site and extracted the posts from 1999 through 2005.

I took the opportunity to factor my reviews from my blog-proper (even though
both contain half-baked opinions). I set up redirects that *should* allow RSS
readers to seamlessly continue to operate with the new setup, though this post
will put the test to that hypothesis.

Another nice thing about switching away from WordPress is that I no longer need
to maintain fiddly WordPress caching plugins (and associated Apache .htaccess
machinery), nor do I need to run a Varnish reverse proxy to ensure that things
stay performant if I ever post something that the world wants to read. Simple
simple simple!

[slice]: http://www.slicehost.com/
[Jekyll]: https://github.com/mojombo/jekyll
[my website Git repository]: https://github.com/samskivert/website
