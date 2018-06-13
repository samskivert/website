---
layout: post
title: Better symlink behavior for Git and Mercurial
date: 2010-09-10
---

Like most developers, you probably don't like unnecessary typing. By that, I
don't mean the static and dynamic variety, but the fingers on keyboard variety.
However, when it comes to manipulating files via version control, we have
recently taken a step backward.

Historically, when faced with paths that look like the following:

    src/main/java/com/company/project/foopkg/MyAwesomeFoo.java
    src/main/java/com/company/project/barpkg/MyAwesomeBar.java
    src/main/java/com/company/project/bazpkg/MyAwesomeBaz.java
    src/test/java/com/company/project/foopkg/MyAwesomeFooTest.java

I have created symlinks at the top-level of my project as follows:

    % ln -s src/main/java/com/company/project code
    % ln -s src/test/java/com/company/project tcode

This allows me to do things like:

    % svn diff code/foopkg/MyAwesomeFoo.java

which is reasonably concise and fairly tab-completion-friendly. Subversion
happily expands the symlink and just does the right thing. Great.

Enter Git and Mercurial. For not entirely explicable reasons (save yourself the
pain of Googling “git symlinks” or “hg symlinks” and perusing the results; it's
a wasteland of madness), they behave as follows in such circumstances:

    % git diff code/foopkg/MyAwesomeFoo.java
    (tumbleweeds)
    % hg diff code/foopkg/MyAwesomeFoo.java
    abort: path 'code/foopkg/MyAwesomeFoo.java' traverses symbolic link 'code'

Git is especially disingenuous in simply reporting that there are no diffs. At
least if you try to do something like commit a file that's on the other end of
a symlink it complains:

    % git commit code/foopkg/MyAwesomeFoo.java
    error: pathspec 'code/foopkg/MyAwesomeFoo.java' did not match any file(s) known to git.

However, what we really want these tools to do is DWIM: just figure out where
the damned file is and do what I asked.

Fortunately, both tools behave themselves when presented with fully qualified
paths. I whipped up a little Perl script to hide the scary symlinks from these
poor overburdened tools. I've reproduced it here, in case you too, dear reader,
find this to be an annoyance:

    #!/usr/bin/perl -w
    #
    # Expands symlinks before Git/Hg sees them.

    use Cwd 'abs_path';

    # Will the real DVCS please stand up?
    my ($x,$orig) = ($0 =~ m#^(.*/)?([^/]+)$#);
    my $bin;
    foreach (`which -a $orig`) {
        chomp;
        $bin = $_ unless ($_ eq $0);
    }
    die "Can't find $orig binary.\n" unless $bin;

    # Expand any symlinks in our args
    my @args = ($bin);
    foreach (@ARGV) {
        my ($dir, $name) = ($_ =~ m#^(.*)/([^/]+)$#);
        if ($dir && -d $dir) {
            my $abspath = abs_path($dir);
            if ($abspath !~ m/$dir$/) {
                push @args, "$abspath/$name";
                next;
            }
        }
        push @args, $_;
    }

    # Now pass the processed arglist on to the real deal
    exec @args;

You can stuff this into files named git and hg in your path (and `chmod a+rx`
them), or you can use more drive space and stick it in something like
`dwim-dvcs` and symlink `git` and `hg` to the single file.

If you are conversant in the line-noise that is Perl, you may notice that I go
to the trouble of handling files that don't exist. That way if you do something
like the following, it still works:

    % git rm code/fookpkg/MyAwesomeFoo.java
    % git commit code/fookpkg/MyAwesomeFoo.java (file no longer exists at this point)

It's still possible to confuse the script (in which case it just passes your
arguments through as-is to git/hg), but at least you have to go further out of
your way to cut yourself on that edge case.
