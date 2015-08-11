Whether and why to require parens for zero arg methods:

- methods that are "pure" can/should require no arguments
- basically lazy vals though maybe they're always computed
- policy or enforced? (we want @pure but is it infeasible)

Also what about trailing closures, like Scala's second arg lists or Swift's trailing closure
support:

  foo.map { e =>
    // blah blah blah
  }

or:

  sort(foo, { e =>
    // blah blah
  })

vs.

  sort(foo) { e =>
  }

Clearly the latter is nicer. Scala would require second arglist, which can work nicely with
currying, Swift requires special "trailing closure" annotation (feels kind of hacky).
