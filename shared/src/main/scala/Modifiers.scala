package vyxal

import vyxal.impls.Elements

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
      { case List(elem) =>
        elem match {
          case AST.Command(symbol) =>
            val element = Elements.elements(symbol)
            AST.Modified { () => (ctx: Context) ?=>
              // todo should default arity be 1 for weird elements?
              FuncHelpers.vectorise(
                VFun(
                  element.impl,
                  element.arity.getOrElse(1),
                  List.empty,
                  ctx
                )
              )
            }
          case lam: AST.Lambda =>
            AST.Modified { () => (ctx: Context) ?=>
              FuncHelpers.vectorise(VFun.fromLambda(lam))
            }
          case _ =>
            AST.Modified { () => (ctx: Context) ?=>
              FuncHelpers.vectorise(
                VFun.fromLambda(AST.Lambda(1, List.empty, elem))
              )
            }
        }
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
