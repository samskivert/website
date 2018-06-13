---
layout: post
title: An emacs on every desktop
date: 2010-08-12
---

I am a heavy user of virtual desktops. I generally have a couple of shell
windows, an emacs window, possibly a browser, and whatever target program I'm
working on, on each of a few virtual desktops. When I'm interrupted from one
project, I flip to the next virtual desktop, open a shell window, open an emacs
window and start hacking away on said interruption. Often the stack will get
three or four virtual desktops deep, and I'll find myself flipping back and
forth between the various in-progress projects.

I generally try to open new files from within emacs, rather than from a
terminal, but occasionally the terminal is more expedient, except that it
results in an entirely new emacs process and window being created on that
virtual desktop. The additional virtual memory consumption isn't a big deal,
but the fact that it screws up my ability to alt-tab between windows is a major
annoyance. Plus my brain naturally assumes there's one emacs process on each
virtual desktop and that process contains all the buffers that have been opened
thereon. So when I go to switch to a buffer that I know is open only to
discover that it's in a different emacs process whose window is hidden behind
the one I'm currently using, it harshes my mellow.

I have played with emacsclient a bit in the past, but it assumes that you use
one emacs to rule them all. So if I have an emacs open on virtual desktop one,
and run emacsclient on virtual desktop three, it will happily open the
requested file on the completely invisible and completely inappropriate emacs
instance on virtual desktop one. Not useful. Further, it assumes that you're
the sort of developer that started emacs back in the late seventies and have
kept that instance running ever since. So if you run emacsclient when there's
no emacs instance running, it happily spits out seven or eight lines of error
message and does nothing especially useful.

For whatever reason (probably me subconsciously avoiding the tedious work that
I have to get done before the end of this weekend), the camel's back broke
today (even though this has been bothering me since I started using FVWM in
like 1995). I scoured the interwebs for a way to find out what virtual desktop
was active (`wmctrl` FTW) and wrote the necessary scripts and elisp to cause
`emacs filename` executed on the command line to always do the Right Thing. If
there's no emacs instance on the active virtual desktop, one is started with
the requested file(s). If an instance is running on that virtual desktop, it is
instructed to open the requested file(s). And as an added bonus, since I use
`emacs -nw` for `$EDITOR` jobs, if the script sees `-nw` on the command line,
it just forks off a whole new emacs with the supplied args, knowing that it
will remain safely ensconced in the terminal.

If you too are an emacs user whose workflow resembles mine, feel free to reap
the fruits of my labor. Add the following to your `.emacs` file:

{% highlight scheme %}
(defun cur-desk ()
  "Returns the numeric identifer of the current desktop."
  (replace-regexp-in-string "\\(^[[:space:]\n]*\\|[[:space:]\n]*$\\)" ""
    (shell-command-to-string "wmctrl -d | grep '\*' | awk '{ print $1 }'"))
  )
(if (string= "x" window-system)
    (progn (setq server-name (format "server%s" (cur-desk)))
           (server-start)))
{% endhighlight %}

and create a shell script in your favorite location with the following contents:

{% highlight sh %}
#!/bin/sh

EMACS=/usr/bin/emacs

# if we have -nw on the command line, invoke a separate emacs instance
for ARG in $*; do
    if [ $ARG = "-nw" ]; then
        exec $EMACS $*
    fi
done

# otherwise send the file(s) to the emacs instance on this virtual desktop
CURDESK=`wmctrl -d | grep '\*' | awk '{ print $1 }'`
emacsclient -s "server$CURDESK" --no-wait $* > /dev/null 2>&1
if [ $? != 0 ]; then
    # no instance running on this virtual desktop, so start one
    $EMACS $* &
fi
{% endhighlight %}

There are probably better ways to accomplish both of the above bits of code. My
bash and elisp skills have both perpetually lingered in the “good enough to get
the job done” realm.
