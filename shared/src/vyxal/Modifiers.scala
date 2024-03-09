package vyxal

import vyxal.parsing.ParsingException

import scala.collection.mutable.ListBuffer

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
    arity: Int,
    overloads: Seq[String],
)(val from: PartialFunction[List[AST], AST])

/** Implementations of modifiers */
object Modifiers:
  private def astToLambda(
      ast: AST,
      arity: Int,
      ogOverride: Boolean = false,
  ): AST =
    ast match
      case _: AST.Lambda => ast
      case _ => AST.Lambda(Some(arity), List(), List(ast), ogOverride)

  private def isExplicitMonad(ast: AST): Boolean =
    ast.arity.getOrElse(-1) == 1 &&
      (ast match
        case f: AST.Lambda => f.params.isEmpty
        case _ => true
      )
  val modifiers: Map[String, Modifier] = Map(
    "v" ->
      Modifier(
        "Vectorise",
        "Vectorises",
        List("vectorise:", "vec:", "v:"),
        1,
        Seq("ᵛf: f but vectorised"),
      ) {
        case List(ast) =>
          val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
          AST.makeSingle(lambdaAst, AST.Command("#v"))
      },
    "~" ->
      Modifier(
        "Apply Without Popping | Filter Stack By",
        """|Apply the next element to the stack without popping it
         |Keep only items on the stack that match a predicate""".stripMargin,
        List("peek:", "filter-stack:"),
        1,
        Seq(
          "~f<monad>: Filter the stack by f",
          "~f<dyad+>: Apply f to the stack without popping",
        ),
      ) {
        case List(ast) =>
          if isExplicitMonad(ast) then
            val lambdaAst = astToLambda(ast, 1)
            AST.makeSingle(lambdaAst, AST.Command("Ḋ"))
          else
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("#~"))
      },
    "/" ->
      Modifier(
        "Foldl | Reduce By | Filter by",
        "Reduce a list by an element",
        List("foldl:", "reduce:", "/:", "fold:", "reduceby:-"),
        1,
        Seq("/f: reduce by element f"),
      ) {
        case List(ast) =>
          scribe.trace(s"Modifier /, ast: $ast")
          if isExplicitMonad(ast) then
            val lambdaAst = astToLambda(ast, 1)
            AST.makeSingle(lambdaAst, AST.Command("F"))
          else
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("R"))
      },
    "⋊" ->
      Modifier(
        "Lambda to Newline",
        """|Scan elements to the left until a newline is found. Push a
           |lambda with all of the scanned elements""".stripMargin,
        List("<-}"),
        -1,
        Seq("<elements>ᵜ: Push a lambda"),
      ) { case _ => ??? },
    "⎇" ->
      Modifier(
        "Dip",
        """|Stash the top of the stack temporarily, and then apply
           |the function. Finally, push the stashed value""".stripMargin,
        List("dip:"),
        1,
        Seq("ᵂf: pop M, apply f, push M"),
      ) {
        // See, Vyxal can do this too!
        // We don't need no fancy array model around here
        // ragged lists do just fine.
        case List(ast) => AST.makeSingle(
            astToLambda(ast, -1),
            AST.Command("#|dip"),
          )
      },
    "∥" ->
      Modifier(
        "Parallel Apply",
        "Parallel apply two elements to the top of the stack",
        List("parallel-apply:", "para-apply:", "paraply:", "!!:"),
        2,
        Seq(),
      ) {
        case List(ast1, ast2) => AST.makeSingle(
            astToLambda(ast1, ast1.arity.getOrElse(-1)),
            astToLambda(ast2, ast2.arity.getOrElse(-1)),
            AST.Command("#|para-apply"),
          )
      },
    "₾" ->
      Modifier(
        "Apply to Register",
        """|Apply a function to the register. Essentially, push
           |the register value to the stack, apply the function, and
           |then pop back into the register""".stripMargin,
        List("apply-to-register:", "to-register:", "to-reg:"),
        1,
        Seq("ᴿf: Apply f to the register"),
      ) {
        case List(ast) => AST.makeSingle(
            astToLambda(ast, ast.arity.getOrElse(1)),
            AST.Command("#|apply-to-register"),
          )
      },
    "◌" ->
      Modifier(
        "Map as Stacks",
        """|Map a function over the top of the stack, treating each iteration
           |as if it were a stack of items. Essentially, dump before mapping
           |""".stripMargin,
        List("vec-dump:", "map-dump:"),
        1,
        Seq(),
      ) {
        case List(ast) => AST.makeSingle(
            astToLambda(ast, ast.arity.getOrElse(1)),
            AST.Command("#|vec-dump"),
          )
      },
    "∦" ->
      Modifier(
        "Parallel Apply and Wrap",
        """|Parallel apply two elements to the top of the stack
           |and wrap the result in a list""".stripMargin,
        List(
          "parallel-apply-and-wrap:",
          "para-apply-and-wrap:",
          "<paraply>:",
          "<!!>:",
        ),
        2,
        Seq(),
      ) {
        case List(ast1, ast2) => AST.makeSingle(
            astToLambda(ast1, -1),
            astToLambda(ast2, -1),
            AST.Command("#|para-apply-wrap"),
          )
      },
    "¿" ->
      Modifier(
        "Conditional Execution",
        "Pop the top of the stack, and, if it's truthy, apply a function",
        List("if-top:", "if:"),
        1,
        Seq(),
      ) {
        case List(ast) => AST.makeSingle(
            AST.Ternary(
              AST.makeSingle(
                astToLambda(ast, ast.arity.getOrElse(1)),
                AST.Command("Ė"),
              ),
              None,
            )
          )
      },
    "⸠" ->
      Modifier(
        "Single Element Lambda",
        "Turn the next element (whether that be a structure/modifier/element) into a lambda",
        List("*:"),
        1,
        Seq("⸠f: Push the equivalent of λf} to the stack"),
      ) { case List(ast) => astToLambda(ast, 1, true) },
    "ϩ" ->
      Modifier(
        "Double Element Lambda",
        "Turn the next two elements (whether that be a structure/modifier/element) into a lambda",
        List("**:"),
        2,
        Seq("ϩfg: Push the equivalent of λfg} to the stack"),
      ) {
        case List(ast1, ast2) =>
          astToLambda(AST.makeSingle(ast1, ast2), 1, true)
      },
    "э" ->
      Modifier(
        "Triple Element Lambda",
        "Turn the next three elements (whether that be a structure/modifier/element) into a lambda",
        List("***:"),
        3,
        Seq("эfgh: Push the equivalent of λfgh} to the stack"),
      ) {
        case List(ast1, ast2, ast3) =>
          astToLambda(AST.makeSingle(ast1, ast2, ast3), 1, true)
      },
    "Ч" ->
      Modifier(
        "Quadruple Element Lambda",
        "Turn the next four elements (whether that be a structure/modifier/element) into a lambda",
        List("****:"),
        4,
        Seq("Чfghi: Push the equivalent of λfghi} to the stack"),
      ) {
        case List(ast1, ast2, ast3, ast4) =>
          astToLambda(AST.makeSingle(ast1, ast2, ast3, ast4), 1, true)
      },
    "♳" ->
      Modifier(
        "Dyadic Single Element Lambda",
        "Turn the next element (whether that be a structure/modifier/element) into a dyadic lambda",
        List("*2:"),
        1,
        Seq("ᵈf: Push the equivalent of λ2|f} to the stack"),
      ) { case List(ast) => astToLambda(ast, 2, true) },
    "♴" ->
      Modifier(
        "Dyadic Double Element Lambda",
        "Turn the next two elements (whether that be a structure/modifier/element) into a dyadic lambda",
        List("**2:"),
        2,
        Seq("ᵉfg: Push the equivalent of λ2|fg} to the stack"),
      ) {
        case List(ast1, ast2) =>
          astToLambda(AST.makeSingle(ast1, ast2), 2, true)
      },
    "♵" ->
      Modifier(
        "Dyadic Triple Element Lambda",
        "Turn the next three elements (whether that be a structure/modifier/element) into a dyadic lambda",
        List("***2:"),
        3,
        Seq("ᶠfgh: Push the equivalent of λ2|fgh} to the stack"),
      ) {
        case List(ast1, ast2, ast3) =>
          astToLambda(AST.makeSingle(ast1, ast2, ast3), 2, true)
      },
    "♶" ->
      Modifier(
        "Dyadic Quadruple Element Lambda",
        "Turn the next four elements (whether that be a structure/modifier/element) into a dyadic lambda",
        List("****2:"),
        4,
        Seq("ᵍfghi: Push the equivalent of λ2|fghi} to the stack"),
      ) {
        case List(ast1, ast2, ast3, ast4) =>
          astToLambda(AST.makeSingle(ast1, ast2, ast3, ast4), 2, true)
      },
  )
end Modifiers
