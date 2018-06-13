---
layout: post
title: "Why Maven Sucks: Halftime Show"
date: 2010-09-09
---

One can, and is indeed encouraged to, sign one's Maven artifacts with GPG. This
involves generating an `artifact-version.jar.asc` file containing the ASCII
representation of the cryptographic signature for the artifact in question.
This is uploaded to the Maven repository along with the artifact.

Maven then helpfully generates `artifact-version.jar.asc.md5` and
`artifact-version.jar.asc.sha1`, and uploads those to the repository as well. I
don't need one, let alone two, cryptographic hashes to tell me that my
cryptographic signature has not been tampered with. This is no doubt because
Maven generates cryptographic hashes for everything that goes into the
repository, but the result seems somewhat comical.

I tried to track down the reason for generating hashes with two different
algorithms, but the commit that introduces the change is cryptic:

    [MINSTALL-34] update dependencies and correct build.
    
I can only assume that this was a design by committee decision of some sort.

While I was perusing the commit logs, I saw that someone else shared my opinion
about the utility of hashing signatures:

    r630088 | wsmoak | 2008-02-21 20:27:13 -0800 (Thu, 21 Feb 2008) | 3 lines

    [MINSTALL-48] Don't create checksums for gpg signature files
    Submitted by: Niall Pemberton

I can only assume that my maven-install-plugin is defaulting to some version
that's more than two and a half years old because I haven't gone out of my way
to add five lines of boilerplate to my POM to ensure that I'm using the latest
version of a plugin that I had never heard of until I undertook this
investigation.
