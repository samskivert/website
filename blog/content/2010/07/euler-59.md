---
layout: post
title: "Euler 59"
date: 2010-07-27
---

[Problem 059]\:

{% highlight scala %}
object Euler59 extends EulerApp {
  val cipher = readline("cipher1.txt") split(',') map(_.toInt)
  def decode (cipher :Array[Int], key :Array[Char]) =
    (0 until cipher.length) map(ii => cipher(ii) ^ key(ii%key.length))
  def answer = (for {
    a <- 'a' to 'z'; b <- 'a' to 'z'; c <- 'a' to 'z'
    val decoded = decode(cipher, Array(a, b, c))
    if (decoded.map(_.toChar).mkString.contains(" chapter"))
  } yield decoded.sum).head
}
{% endhighlight %}
Nothing fancy here. I do a brute force search, trying each key in turn and looking for a particular English word to indicate that the decoding is correct. I'm cheating a little in that I first had the program print out the decoded text, and once I saw the correct decoding, I selected a word therefrom to use in the final version. I suppose I could read in a dictionary of English words and assume I'd found the key when my decoding contained a few of the words in the dictionary. That sounds like way too much work for an Euler problem.


[Problem 059]: http://projecteuler.net/index.php?section=problems&id=59
