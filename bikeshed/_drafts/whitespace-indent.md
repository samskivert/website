I've started doing this:

  private Mode movePrepped (final Unit unit, final Coord from, final Coord to) {
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

which I used to do in elisp a lot:

  (defun brace-list-offset (info)
    "Calculate the correct indentation for brace-lists. When
  indenting brace-lists inside arglists, we want to avoid the
  natural arglist indention."
    (destructuring-bind (syntax . anchor) info
      (let ((arglist-count
             (loop for (symbol . _) in c-syntactic-context
                   count (eq symbol 'arglist-cont-nonempty))))
        (if (eq syntax 'brace-list-close)
            (* -1 c-basic-offset arglist-count)
          (if (> arglist-count 0) 0 '+))
        )))

But why fool around with cuddling closing block characters when we can just go whole hog to
indentation based blocking?
