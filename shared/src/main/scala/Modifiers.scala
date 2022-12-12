package vyxal

import vyxal.impls.Elements

/** @param name
  *   The modifier's name
  * @param description
  *   A more in-depth (possibly multiline) description of the modifier
  * @param keywords
  *   Keywords to help search for the modifier
  * @param from
  *   The implementation of the modifier that takes ASTs and turns them into
  *   functions that can be applied to the stack.
  *
  * `from` is a `PartialFunction` so that modifiers don't have to check for
  * lists with more arguments than that modifier's arity.
  */
case class Modifier(
    name: String,
    description: String,
    keywords: List[String]
)(val from: PartialFunction[List[AST], AST])

/** Implementations of modifiers */
object Modifiers:
  val modifiers: Map[String, Modifier] = Map(
    "v" -> Modifier(
      "Vectorise",
      """|Vectorises
         |vf: f but vectorised""".stripMargin,
      List("vectorise-", "vec-")
    ) { case List(ast) =>
      val lambdaAst = ast match
        case _: AST.Lambda => ast
        case _             => AST.Lambda(ast.arity.getOrElse(1), List(), ast)
      AST.makeSingle(lambdaAst, AST.Command("#v"))
    },
    "/" -> Modifier(
      "Foldl | Reduce By",
      """|Reduce a list by an element
         |/f: reduce by element f
      """.stripMargin,
      List("foldl-", "reduce-", "/-")
    ) { case List(ast) =>
      val lambdaAst = ast match
        case _: AST.Lambda => ast
        case _             => AST.Lambda(2, List(), ast)
      AST.makeSingle(lambdaAst, AST.Command("R"))
    }
  )
end Modifiers
