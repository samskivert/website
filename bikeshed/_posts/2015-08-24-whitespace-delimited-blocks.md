---
layout: post
title: Whitespace-delimited blocks
---

The perfect bikeshed topic may well be tabs versus spaces in source code. In spite of my preference
for the latter, I'm not actually going to make a case for that here, nor would I preclude the use
of tabs in hypothetical language source code. But what I would do is make whitespace significant,
particularly for delimiting blocks.

This is a somewhat reckless desire on my part, because I've never written a lot of code in a
language that uses indentation-delimited blocks. Rather, I realized that my sense of style has
been subconsciously pushing me in this direction for years, and I just never noticed it.

I (and many other lisp users) have always cuddled the mass of trailing parens that tend to pile up
when you have deeply nested sexps:

```lisp
  (defun brace-list-offset (info)
    (destructuring-bind (syntax . anchor) info
      (let ((arglist-count
             (loop for (symbol . _) in c-syntactic-context
                   count (eq symbol 'arglist-cont-nonempty))))
        (if (eq syntax 'brace-list-close)
            (* -1 c-basic-offset arglist-count)
          (if (> arglist-count 0) 0 '+))
        )))
```

That's so much better than:

```lisp
  (defun brace-list-offset (info)
    (destructuring-bind (syntax . anchor) info
      (let ((arglist-count
             (loop for (symbol . _) in c-syntactic-context
                   count (eq symbol 'arglist-cont-nonempty)
             )
            )
           )
        (if (eq syntax 'brace-list-close)
            (* -1 c-basic-offset arglist-count)
          (if (> arglist-count 0) 0 '+)
        )
      )
    )
  )
```

Without even realizing the lisp influence, I started doing this to my Java code a year or so ago,
in my constant battle against the avalanche of useless tokens that is your average Java program:

```java
  private Mode movePrepped (Unit unit, Coord from, Coord to) {
    return new Mode() {
      public void onTap (Coord tapCoord, Unit tapUnit) {
        if (tapCoord == to) {
          moveUnit(unit, from, to);
          clearSelectedUnit();
        }
        else if (tapUnit != null && tapUnit.friendly()) selectUnit(tapCoord, tapUnit);
        else if (_activeMoves.contains(tapCoord)) prepMove(unit, from, tapCoord);
        else if (_activeTargets.contains(tapCoord)) prepAttack(unit, from, to, tapCoord);
        else clearSelectedUnit();
      }};}
```

Now I realize that what I really want is indentation-delimited blocks:

```python
  def movePrepped (unit :Unit, from :Coord, to :Coord) :Mode =
    new Mode:
      def onTap (tapCoord :Coord, tapUnit :Unit):
        if (tapCoord == to):
          moveUnit(unit, from, to)
          clearSelectedUnit()
        else if (tapUnit != null && tapUnit.friendly) selectUnit(tapCoord, tapUnit)
        else if (_activeMoves.contains(tapCoord)) prepMove(unit, from, tapCoord)
        else if (_activeTargets.contains(tapCoord)) prepAttack(unit, from, to, tapCoord)
        else clearSelectedUnit()
```

This would be even more pleasing to look upon with proper syntax highlighting. Regardless, it
pleases me to eliminate the tokens that merely duplicate what is already communicated (more
strongly) by indentation.

As I said at the start, without having actually written a lot of, say, Python code, I may well be
missing some huge downside to significant whitespace of this sort, but I have not been convinced by
any of the complaints I've seen on the interwebs.

The biggest complaint seems to be that you can't just copy and paste a wadge of code into your
source file without checking and potentially fixing the indentation. To that I say: if you do not
already find that you have to carefully read and reformat 99% of the code that you copy from some
external source (in any language), then your level of attention to detail is woefully lacking. It
pains me to look upon code like that, and while I'm not going to condemn you to hell for writing
it, I am certainly not going to go out of my way to make it easier to write such sloppy code in a
programming language of my own design.

The scenery is very nice from up here on my high horse.
