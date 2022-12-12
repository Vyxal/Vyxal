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
  private def astToLambda(ast: AST, arity: Int): AST =
    ast match
      case _: AST.Lambda => ast
      case _             => AST.Lambda(arity, List(), ast)
  val modifiers: Map[String, Modifier] = Map(
    "v" -> Modifier(
      "Vectorise",
      """|Vectorises
         |vf: f but vectorised""".stripMargin,
      List("vectorise-", "vec-")
    ) { case List(ast) =>
      val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
      AST.makeSingle(lambdaAst, AST.Command("#v"))
    },
    "/" -> Modifier(
      "Foldl | Reduce By",
      """|Reduce a list by an element
         |/f: reduce by element f
      """.stripMargin,
      List("foldl-", "reduce-", "/-")
    ) { case List(ast) =>
      val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
      AST.makeSingle(lambdaAst, AST.Command("R"))
    },
    "′" -> Modifier(
      "Single Element Lambda",
      """|Turn the next element (whether that be a structure/modifier/element) into a lambda
         |′f: Push the equivalent of λf} to the stack""".stripMargin,
      List("*-")
    ) { case List(ast) => AST.makeSingle(astToLambda(ast, 1)) },
    "″" -> Modifier(
      "Double Element Lambda",
      """|Turn the next two elements (whether that be a structure/modifier/element) into a lambda
         |″fg: Push the equivalent of λfg} to the stack""".stripMargin,
      List("**-")
    ) { case List(ast1, ast2) =>
      AST.makeSingle(astToLambda(AST.makeSingle(ast1, ast2), 1))
    },
    "‴" -> Modifier(
      "Triple Element Lambda",
      """|Turn the next three elements (whether that be a structure/modifier/element) into a lambda
         |‴fgh: Push the equivalent of λfgh} to the stack""".stripMargin,
      List("***-")
    ) { case List(ast1, ast2, ast3) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3), 1)
    },
    "⁴" -> Modifier(
      "Quadruple Element Lambda",
      """|Turn the next four elements (whether that be a structure/modifier/element) into a lambda
         |⁴fghi: Push the equivalent of λfghi} to the stack""".stripMargin,
      List("****-")
    ) { case List(ast1, ast2, ast3, ast4) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3, ast4), 1)
    }
  )
end Modifiers
