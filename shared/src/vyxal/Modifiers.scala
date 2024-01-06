package vyxal

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
    "ᵜ" ->
      Modifier(
        "Lambda to Newline",
        """|Scan elements to the left until a newline is found. Push a
           |lambda with all of the scanned elements""".stripMargin,
        List("<-}"),
        -1,
        Seq("<elements>ᵜ: Push a lambda"),
      ) { case _ => ??? },
    "ᵃ" ->
      Modifier(
        "Apply to Neighbours | Number of Truthy Elements",
        """|To each overlapping pair, reduce it by an element
           |Apply a dyadic element for all pairs of neighboring elements.
           |Count the number of truthy elements in a list under a mondaic element""".stripMargin,
        List(
          "apply-to-neighbours:",
          "count-truthy:",
          "apply-neighbours:",
          "apply-to-neighbors:",
          "apply-neighbors:",
          "2lvf:",
          "twolif:",
          "to-pairs:",
          "to-overlaps:",
          "count:",
        ),
        1,
        Seq(
          "ȧf<monad>: Count how many items in a list are truthy after applying f to each",
          "ᵃf<dyad>: equivalent to pushing the function, then calling ȧ",
        ),
      ) {
        case List(ast) =>
          if isExplicitMonad(ast) then
            val lambdaAst = astToLambda(ast, 1)
            AST.makeSingle(
              lambdaAst,
              AST.Command("M"),
              AST.Lambda(Some(1), List(), List(AST.Command("ȯ"))),
              AST.Command("#v"),
              AST.Command("∑"),
            )
          else
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("ȧ"))
      },
    "ᵇ" ->
      Modifier(
        "Apply Without Popping | Remove Duplicates by",
        """|Apply a 2+ arity element to the stack without popping
           |Remove duplicates from a list by an element""".stripMargin,
        List("without-popping:", "peek:", "dedup-by:", "remove-duplicates-by:"),
        1,
        Seq(
          "ᵇf<dyad|triad|tetrad>: apply f to the stack without popping",
          "ᵇf<monad>: remove duplicates from a list by applying f to each pair of elements",
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
    "ᶜ" ->
      Modifier(
        "Reduce Columns | Map Over Suffixes",
        """|Reduce columns of a 2d list by a function
           |Map an element over suffixes""".stripMargin,
        List(
          "reduce-columns:",
          "map-over-suffixes:",
          "fold-cols:",
          "foldl-cols:",
          "fold-columns-by:",
          "reduce-columns-by:",
          "over-suffixes:",
        ),
        1,
        Seq(),
      ) {
        case List(ast) =>
          if isExplicitMonad(ast) then
            val lambdaAst = astToLambda(ast, 1)
            AST.makeSingle(lambdaAst, AST.Command("#|map-suffixes"))
          else
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("#|reduce-cols"))
      },
    "ᵛ" ->
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
    "ᵈ" ->
      Modifier(
        "Dyadic Single Element Lambda",
        "Turn the next element (whether that be a structure/modifier/element) into a dyadic lambda",
        List("*2:"),
        1,
        Seq("ᵈf: Push the equivalent of λ2|f} to the stack"),
      ) { case List(ast) => astToLambda(ast, 2, true) },
    "ᵉ" ->
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
    "ᶠ" ->
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
    "ᴳ" ->
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
    "ᴴ" ->
      Modifier(
        "Apply To Head",
        "Apply element only to the head of list",
        List("apply-to-head:"),
        1,
        Seq("ᴴf: Apply f to the head of the top of the stack"),
      ) {
        case List(ast) =>
          var returnStr = false
          ast.arity match
            case Some(0) | Some(1) => AST.makeSingle(
                AST.Generated(
                  () =>
                    ctx ?=>
                      returnStr = ctx.peek.isInstanceOf[String]
                      val top = ListHelpers.makeIterable(ctx.pop())
                      ctx.push(top.tail)
                      if ast.arity == Some(1) then
                        ctx.push(
                          top.headOption.getOrElse(ctx.settings.defaultValue)
                        )
                  ,
                  arity = Some(1),
                ),
                ast,
                AST.Generated(
                  () =>
                    ctx ?=>
                      val head = ctx.pop()
                      val tail = ctx.peek match
                        case _: VList => ctx.pop().asInstanceOf[VList]
                        case _ => VList.from(ctx.pop(1))
                      val list = VList.from(head +: tail)
                      if returnStr then
                        ctx.push(ListHelpers.flatten(list).mkString)
                      else ctx.push(list)
                  ,
                  arity = Some(1),
                ),
              )
            case Some(2) => AST.makeSingle(
                AST.Generated(
                  () =>
                    ctx ?=>
                      returnStr = ctx.peek.isInstanceOf[String]
                      val top = ListHelpers.makeIterable(ctx.pop())
                      ctx.push(top.tail)
                      ctx.push(
                        top.headOption.getOrElse(ctx.settings.defaultValue)
                      )
                  ,
                  arity = Some(1),
                ),
                AST.makeSingle(astToLambda(ast, 2), AST.Command("#v")),
                AST.Generated(
                  () =>
                    ctx ?=>
                      val head = ctx.peek match
                        case _: VList => ctx.pop().asInstanceOf[VList]
                        case _ => VList.from(ctx.pop(1))
                      if returnStr then
                        ctx.push(ListHelpers.flatten(head).mkString)
                      else ctx.push(head)
                  ,
                  arity = Some(1),
                ),
              )
            case _ => throw ModifierArityException("ᴴ", ast.arity)
          end match
      },
    "ᶤ" ->
      Modifier(
        "First Index Where",
        "Find the first index where an element is truthy",
        List("first-index-where:", "first-index-of:", "ind-of:", "find-by:"),
        1,
        Seq("ᶤf: find the first index where f is truthy"),
      ) {
        case List(ast) =>
          val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
          AST.makeSingle(lambdaAst, AST.Command("ḋ"))
      },
    "ᶨ" ->
      Modifier(
        "Loop and Collect While Unique",
        "Loop and Collect While Unique",
        List("collect-while-unique:"),
        1,
        Seq("ᶨf: Loop and collect while unique"),
      ) {
        case List(ast) => AST.makeSingle(
            astToLambda(ast, ast.arity.getOrElse(1)),
            AST.Command("İ"),
          )
      },
    "ᵏ" ->
      Modifier(
        "Key",
        "Map an element over the groups formed by identical items.",
        List("key:"),
        1,
        Seq("ᵏf: Map f over the groups formed by identical items"),
      ) {
        case List(ast) => AST.makeSingle(
            AST.Generated(
              () =>
                ctx ?=>
                  val lst = ListHelpers.makeIterable(ctx.pop())
                  val bins = ListBuffer[(VAny, ListBuffer[VAny])]()
                  lst.foreach { elem =>
                    val (key, bin) = bins.find(_._1 == elem).getOrElse {
                      val bin = ListBuffer[VAny]()
                      bins += ((elem, bin))
                      (elem, bin)
                    }
                    bin += elem
                  }
                  ctx.push(VList.from(bins.map {
                    case (key, bin) =>
                      given elemCtx: Context = ctx.makeChild()
                      elemCtx.push(VList.from(bin.toSeq))
                      Interpreter.execute(ast)(using elemCtx)
                      elemCtx.pop()
                  }.toSeq))
              ,
              arity = Some(1),
            )
          )
      },
    "ᶪ" ->
      Modifier(
        "Loop While Unique",
        "Loop While Unique - similar to ᶨ, but doesn't collect",
        List("loop-while-unique:"),
        1,
        Seq("ᶪf: Loop while unique"),
      ) {
        case List(ast) => AST.makeSingle(
            astToLambda(ast, ast.arity.getOrElse(1)),
            AST.Command("İ"),
            AST.Command("t"),
          )
      },
    "ᵐ" ->
      Modifier(
        "Maximum By",
        "Maximum By Element",
        List("max-by:", "maximum-by:"),
        1,
        Seq("ᵐf: Maximum of top of stack based on results of f"),
      ) {
        case List(ast) => AST.makeSingle(
            astToLambda(ast, ast.arity.getOrElse(1)),
            AST.Command("#|maximum-by"),
          )
      },
    "ⁿ" ->
      Modifier(
        "Minimum By",
        "Minimum By Element",
        List("min-by:", "minimum-by:"),
        1,
        Seq("ᵐf: Minimum of top of stack based on results of f"),
      ) {
        case List(ast) => AST.makeSingle(
            astToLambda(ast, ast.arity.getOrElse(1)),
            AST.Command("#|minimum-by"),
          )
      },
    "ᵒ" ->
      Modifier(
        "Outer Product | Table",
        "Outer product",
        List("outer-product:", "table:"),
        1,
        Seq(
          "ᵒf: Pop two lists, then make a matrix from them by applying f to each pair of elements"
        ),
      ) {
        case List(ast) => AST.Generated(
            () =>
              ctx ?=>
                val rhs = ListHelpers.makeIterable(ctx.pop(), Some(true))
                val lhs = ListHelpers.makeIterable(ctx.pop(), Some(true))
                val matrix = VList.from(lhs.map { l =>
                  VList.from(rhs.map { r =>
                    ctx.push(l)
                    ctx.push(r)
                    Interpreter.execute(ast)
                    ctx.pop()
                  })
                })
                ctx.push(matrix)
            ,
            arity = Some(2),
          )
      },
    "ᵖ" ->
      Modifier(
        "Map Over Prefixes",
        "Map an element over the prefixes of a list",
        List("map-over-prefixes:", "over-prefixes:"),
        1,
        Seq("ᵖf: Map f over prefixes"),
      ) {
        case List(ast) => AST.makeSingle(
            astToLambda(ast, ast.arity.getOrElse(1)),
            AST.Command("#|map-prefixes"),
          )
      },
    "ᴿ" ->
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
    "ᶳ" ->
      Modifier(
        "Sort By",
        "Sort By Element / Scanl",
        List("sort-by:", "scanl:"),
        1,
        Seq(
          "ᶳf: Sort top of stack based on results of f",
          "ᶳf: Cumulatively reduce a list of items",
        ),
      ) {
        case List(ast) =>
          if isExplicitMonad(ast) then
            val lambdaAst = astToLambda(ast, 1)
            AST.makeSingle(lambdaAst, AST.Command("ṡ"))
          else
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("Ṭ"))
      },
    "ᵗ" ->
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
    "ᵘ" ->
      Modifier(
        "Collect Until No Change / Neighbours All Equal?",
        """|Run func on the prev result until the result no longer changes
           |returning all intermediate results
           |Given a dyadic function, apply the function to all overlapping pairs of elements
           |and test if all results are equal""".stripMargin,
        List(
          "collect-until-no-change:",
          "until-stable:",
          "stablise:",
          "neighbours-equals:",
        ),
        1,
        Seq("ᵘf: Collect until no change"),
      ) {
        case List(ast) =>
          if !isExplicitMonad(ast) then
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("#|all-neigh"))
          else
            AST.makeSingle(
              astToLambda(ast, ast.arity.getOrElse(1)),
              AST.Command("ċ"),
            )
      },
    "ᵂ" ->
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
    "ᵡ" ->
      Modifier(
        "Scan Fixed Point",
        "Scan a function until it reaches a fixed point",
        List("scan-fix:"),
        1,
        Seq(
          "ᵡf: scan f until a fixed point is reached / apply until a previous value is repeated, collecting intermediate results"
        ),
      ) {
        case List(ast) =>
          val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
          AST.makeSingle(lambdaAst, AST.Command("Ŀ"))
      },
    "ᵞ" ->
      Modifier(
        "Invariant Under? / Vertical Scan",
        "Check if a function is invariant under a transformation / vertical scan",
        List(
          "invariant-under:",
          "vertical-scan:",
          "vscan:",
          "v-scan:",
          "invariant?:",
          "same?:",
        ),
        1,
        Seq(
          "ᵞf: check if top of stack is invariant under a transformation",
          "ᵞf: scanl columns by f",
        ),
      ) {
        case List(ast) =>
          if isExplicitMonad(ast) then
            val lambdaAst = astToLambda(ast, 1)
            AST.makeSingle(lambdaAst, AST.Command("#|invar"))
          else
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("#|vscan"))
      },
    "ᶻ" ->
      Modifier(
        "Zip With / Reject by",
        """|Given a dyadic function, zip two lists and reduce each by f
           | and then check if all results are equal.
           |Given a monadic function, the inverse of monadic /.
           |Filters where the function is falsey""".stripMargin,
        List("zip-with:", "zipwith:"),
        1,
        Seq(),
      ) {
        case List(ast) =>
          if isExplicitMonad(ast) then
            AST.makeSingle(
              astToLambda(ast, ast.arity.getOrElse(1)),
              AST.Command("I"),
            )
          else
            val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
            AST.makeSingle(lambdaAst, AST.Command("r"))
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
  )
end Modifiers
