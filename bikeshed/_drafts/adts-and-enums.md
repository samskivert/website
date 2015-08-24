Support target type directed name resolution for enums (and opt-in for ADTs?). For example:

```scala
enum Foo { BAR, BAZ, BING }
def foo (foo :Foo) = ...
foo(BAR) // target type is Foo, so we know BAR is Foo.BAR
```

Enums desugar similarly to Java:

```scala
enum Foo { BAR, BAZ, BING }
// becomes
sealed trait Foo
object Foo {
  object BAR extends Foo
  object BAZ extends Foo
  object BING extends Foo
}
```

Also per paulp, disallow type inference of ADT constructors/enum constants. Never infer the type
`Foo.BAR`, only `Foo`. Could opt into inference for user defined sealed traits/ADTs.
