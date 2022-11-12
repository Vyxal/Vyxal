package vyxal

/** @param name
  *   The modifier's name
  * @param description
  *   A more in-depth (possibly multiline) description of the modifier
  * @param impl
  *   The implementation of the modifier that takes ASTs and turns them into
  *   functions that can be applied to the stack.
  *
  * `impl` is a `PartialFunction` so that modifiers don't have to check for
  * lists with more arguments than that modifier's arity (if you use a partial
  * function, your `case`s don't have to cover every possible case)
  */
case class Modifier(
    name: String,
    description: String,
    impl: PartialFunction[List[AST], AST]
)

/** Implementations of modifiers
  */
object Modifiers {
  val modifiers: Map[String, Modifier] = Map(
    "v" -> Modifier(
      "Vectorise",
      """|Vectorises
         |vf: f but vectorised""".stripMargin,
      { case List(a) =>
        ???
      }
    ),
    "@" -> Modifier(
      "Apply at indices",
      """|Applies at indices
         |@a b: asdf""".stripMargin,
      { case List(a, b) => ??? }
    )
  )
}
