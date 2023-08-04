package vyxal

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
    keywords: List[String],
    arity: Int
)(val from: PartialFunction[List[AST], AST])

/** Implementations of modifiers */
object Modifiers:
  private def astToLambda(ast: AST, arity: Int): AST =
    ast match
      case _: AST.Lambda => ast
      case _ => AST.Lambda(arity, List(), List(ast))
  val modifiers: Map[String, Modifier] = Map(
    "ᵃ" -> Modifier(
      "Apply to Neighbours",
      """|To each overlapping pair, reduce it by an element
       |Apply a dyadic link or a monadic chain for all pairs of neighboring elements.
       |ᵃf: equivalent to 2ov/f""".stripMargin,
      List("apply-to-neighbours"),
      1
    ) { case List(ast) =>
      val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
      // obviously incorrect right now,but it's a start
      AST.makeSingle(lambdaAst, AST.Command("2ov/"))
    },
    "v" -> Modifier(
      "Vectorise",
      """|Vectorises
         |vf: f but vectorised""".stripMargin,
      List("vectorise-", "vec-", "v-"),
      1
    ) { case List(ast) =>
      val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
      AST.makeSingle(lambdaAst, AST.Command("#v"))
    },
    "/" -> Modifier(
      "Foldl | Reduce By | Filter by",
      """|Reduce a list by an element
         |/f: reduce by element f""".stripMargin,
      List("foldl-", "reduce-", "/-", "fold-", "reduceby-"),
      1
    ) { case List(ast) =>
      scribe.trace(s"Modifier /, ast: $ast")
      println("Modifier /, ast.arity: " + ast.arity)
      if ast.arity.getOrElse(-1) == 1 && (ast match
          case f: AST.Lambda => f.params.isEmpty
          case _ => true
        )
      then
        val lambdaAst = astToLambda(ast, 1)
        AST.makeSingle(lambdaAst, AST.Command("F"))
      else
        val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
        AST.makeSingle(lambdaAst, AST.Command("R"))
    },
    "⸠" -> Modifier(
      "Single Element Lambda",
      """|Turn the next element (whether that be a structure/modifier/element) into a lambda
         |⸠f: Push the equivalent of λf} to the stack""".stripMargin,
      List("*-"),
      1
    ) { case List(ast) => AST.makeSingle(astToLambda(ast, 1)) },
    "ϩ" -> Modifier(
      "Double Element Lambda",
      """|Turn the next two elements (whether that be a structure/modifier/element) into a lambda
         |ϩfg: Push the equivalent of λfg} to the stack""".stripMargin,
      List("**-"),
      2
    ) { case List(ast1, ast2) =>
      AST.makeSingle(astToLambda(AST.makeSingle(ast1, ast2), 1))
    },
    "э" -> Modifier(
      "Triple Element Lambda",
      """|Turn the next three elements (whether that be a structure/modifier/element) into a lambda
         |эfgh: Push the equivalent of λfgh} to the stack""".stripMargin,
      List("***-"),
      3
    ) { case List(ast1, ast2, ast3) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3), 1)
    },
    "Ч" -> Modifier(
      "Quadruple Element Lambda",
      """|Turn the next four elements (whether that be a structure/modifier/element) into a lambda
         |Чfghi: Push the equivalent of λfghi} to the stack""".stripMargin,
      List("****-"),
      4
    ) { case List(ast1, ast2, ast3, ast4) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3, ast4), 1)
    },
    "ᵈ" -> Modifier(
      "Dyadic Single Element Lambda",
      """|Turn the next element (whether that be a structure/modifier/element) into a dyadic lambda
         |ᵈf: Push the equivalent of λ2|f} to the stack""".stripMargin,
      List("*2-"),
      1
    ) { case List(ast) => AST.makeSingle(astToLambda(ast, 2)) },
    "ᵉ" -> Modifier(
      "Dyadic Double Element Lambda",
      """|Turn the next two elements (whether that be a structure/modifier/element) into a dyadic lambda
         |ᵉfg: Push the equivalent of λ2|fg} to the stack""".stripMargin,
      List("**2-"),
      2
    ) { case List(ast1, ast2) =>
      AST.makeSingle(astToLambda(AST.makeSingle(ast1, ast2), 2))
    },
    "ᶠ" -> Modifier(
      "Dyadic Triple Element Lambda",
      """|Turn the next three elements (whether that be a structure/modifier/element) into a dyadic lambda
         |ᶠfgh: Push the equivalent of λ2|fgh} to the stack""".stripMargin,
      List("***2-"),
      3
    ) { case List(ast1, ast2, ast3) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3), 2)
    },
    "ᵍ" -> Modifier(
      "Dyadic Quadruple Element Lambda",
      """|Turn the next four elements (whether that be a structure/modifier/element) into a dyadic lambda
         |ᵍfghi: Push the equivalent of λ2|fghi} to the stack""".stripMargin,
      List("****2-"),
      4
    ) { case List(ast1, ast2, ast3, ast4) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3, ast4), 2)
    },
  )
end Modifiers
