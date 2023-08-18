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

  private def isExplicitMonad(ast: AST): Boolean =
    ast.arity.getOrElse(-1) == 1 && (ast match
      case f: AST.Lambda => f.params.isEmpty
      case _ => true
    )
  val modifiers: Map[String, Modifier] = Map(
    "ᵃ" -> Modifier(
      "Apply to Neighbours | Number of Truthy Elements",
      """|To each overlapping pair, reduce it by an element
       |Apply a dyadic element for all pairs of neighboring elements.
       |Count the number of truthy elements in a list under a mondaic element
       |ȧf<monad>: Count how many items in a list are truthy after applying f to each
       |ᵃf<dyad>: equivalent to pushing the function, then calling ȧ""".stripMargin,
      List("apply-to-neighbours:", "count-truthy:"),
      1
    ) { case List(ast) =>
      if isExplicitMonad(ast) then
        val lambdaAst = astToLambda(ast, 1)
        AST.makeSingle(
          lambdaAst,
          AST.Command("M"),
          AST.Lambda(1, List(), List(AST.Command("ȯ"))),
          AST.Command("#v"),
          AST.Command("∑")
        )
      else
        val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
        AST.makeSingle(lambdaAst, AST.Command("ȧ"))
    },
    "ᵇ" -> Modifier(
      "Apply Without Popping | Remove Duplicates by",
      """|Apply a 2+ arity element to the stack without popping
       |Remove duplicates from a list by an element
       |ᵇf<dyad|triad|tetrad>: apply f to the stack without popping
       |ᵇf<monad>: remove duplicates from a list by applying f to each pair of elements""".stripMargin,
      List("without-popping:", "peek:", "dedup-by:", "remove-duplicates-by:"),
      1
    ) { case List(ast) =>
      if isExplicitMonad(ast) then
        val lambdaAst = astToLambda(ast, 1)
        AST.makeSingle(
          lambdaAst,
          AST.Command("Ḋ")
        )
      else
        val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
        AST.makeSingle(lambdaAst, AST.Command("#~"))
    },
    "ᶜ" -> Modifier(
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
        "over-suffixes:"
      ),
      1
    ) { case List(ast) =>
      if isExplicitMonad(ast) then
        val lambdaAst = astToLambda(ast, 1)
        AST.makeSingle(
          lambdaAst,
          AST.Command("#|map-suffixes"),
        )
      else
        val lambdaAst = astToLambda(ast, ast.arity.getOrElse(2))
        AST.makeSingle(lambdaAst, AST.Command("#|reduce-cols"))
    },
    "ᶤ" -> Modifier(
      "First Index Where",
      """|Find the first index where an element is truthy
         |ᶤf: find the first index where f is truthy""".stripMargin,
      List("first-index-where:", "first-index-of:"),
      1
    ) { case List(ast) =>
      val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
      AST.makeSingle(lambdaAst, AST.Command("ḋ"))
    },
    "v" -> Modifier(
      "Vectorise",
      """|Vectorises
         |vf: f but vectorised""".stripMargin,
      List("vectorise:", "vec:", "v:"),
      1
    ) { case List(ast) =>
      val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
      AST.makeSingle(lambdaAst, AST.Command("#v"))
    },
    "/" -> Modifier(
      "Foldl | Reduce By | Filter by",
      """|Reduce a list by an element
         |/f: reduce by element f""".stripMargin,
      List("foldl:", "reduce:", "/:", "fold:", "reduceby:-"),
      1
    ) { case List(ast) =>
      scribe.trace(s"Modifier /, ast: $ast")
      if isExplicitMonad(ast) then
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
      List("*:"),
      1
    ) { case List(ast) => AST.makeSingle(astToLambda(ast, 1)) },
    "ϩ" -> Modifier(
      "Double Element Lambda",
      """|Turn the next two elements (whether that be a structure/modifier/element) into a lambda
         |ϩfg: Push the equivalent of λfg} to the stack""".stripMargin,
      List("**:"),
      2
    ) { case List(ast1, ast2) =>
      AST.makeSingle(astToLambda(AST.makeSingle(ast1, ast2), 1))
    },
    "э" -> Modifier(
      "Triple Element Lambda",
      """|Turn the next three elements (whether that be a structure/modifier/element) into a lambda
         |эfgh: Push the equivalent of λfgh} to the stack""".stripMargin,
      List("***:"),
      3
    ) { case List(ast1, ast2, ast3) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3), 1)
    },
    "Ч" -> Modifier(
      "Quadruple Element Lambda",
      """|Turn the next four elements (whether that be a structure/modifier/element) into a lambda
         |Чfghi: Push the equivalent of λfghi} to the stack""".stripMargin,
      List("****:"),
      4
    ) { case List(ast1, ast2, ast3, ast4) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3, ast4), 1)
    },
    "ᵈ" -> Modifier(
      "Dyadic Single Element Lambda",
      """|Turn the next element (whether that be a structure/modifier/element) into a dyadic lambda
         |ᵈf: Push the equivalent of λ2|f} to the stack""".stripMargin,
      List("*2:"),
      1
    ) { case List(ast) => AST.makeSingle(astToLambda(ast, 2)) },
    "ᵉ" -> Modifier(
      "Dyadic Double Element Lambda",
      """|Turn the next two elements (whether that be a structure/modifier/element) into a dyadic lambda
         |ᵉfg: Push the equivalent of λ2|fg} to the stack""".stripMargin,
      List("**2:"),
      2
    ) { case List(ast1, ast2) =>
      AST.makeSingle(astToLambda(AST.makeSingle(ast1, ast2), 2))
    },
    "ᶠ" -> Modifier(
      "Dyadic Triple Element Lambda",
      """|Turn the next three elements (whether that be a structure/modifier/element) into a dyadic lambda
         |ᶠfgh: Push the equivalent of λ2|fgh} to the stack""".stripMargin,
      List("***2:"),
      3
    ) { case List(ast1, ast2, ast3) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3), 2)
    },
    "ᵍ" -> Modifier(
      "Dyadic Quadruple Element Lambda",
      """|Turn the next four elements (whether that be a structure/modifier/element) into a dyadic lambda
         |ᵍfghi: Push the equivalent of λ2|fghi} to the stack""".stripMargin,
      List("****2:"),
      4
    ) { case List(ast1, ast2, ast3, ast4) =>
      astToLambda(AST.makeSingle(ast1, ast2, ast3, ast4), 2)
    },
    "ᴴ" -> Modifier(
      "Apply To Head",
      """|Apply element only to the head of list
         |ᴴf: Apply f to the head of the top of the stack""".stripMargin,
      List("apply-to-head:"),
      1
    ) { case List(ast) =>
      // TODO this only works if f takes pops exactly one value and pushes
      //   exactly one value. Is that a problem?
      AST.makeSingle(
        AST.Generated(
          () =>
            ctx ?=>
              val top = ListHelpers.makeIterable(ctx.peek)
              ctx.push(top.tail)
              ctx.push(top.head)
          ,
          arity = Some(1)
        ),
        ast,
        AST.Generated(
          () =>
            ctx ?=>
              val head = ctx.pop()
              val tail = ctx.pop().asInstanceOf[VList]
              ctx.push(VList.from(head +: tail))
          ,
          arity = Some(1)
        )
      )
    },
    "ᶨ" -> Modifier(
      "Loop and Collect While Unique",
      """|Loop and Collect While Unique
         |ᶨf: Loop and collect while unique""".stripMargin,
      List("collect-while-unique:"),
      1
    ) { case List(ast) =>
      AST.makeSingle(
        astToLambda(ast, ast.arity.getOrElse(1)),
        AST.Command("İ")
      )
    },
    "ᵐ" -> Modifier(
      "Maximum By",
      """|Maximum By Element
         |ᵐf: Maximum of top of stack based on results of f""".stripMargin,
      List("max-by", "maximum-by"),
      1
    ) { case List(ast) =>
      AST.Generated(
        () =>
          ctx ?=>
            val lst = ListHelpers.makeIterable(ctx.pop())
            val max = lst.maxByOption { elem =>
              given elemCtx: Context = ctx.makeChild()
              elemCtx.push(elem)
              Interpreter.execute(ast)(using elemCtx)
              elemCtx.pop()
            }
            ctx.push(max.getOrElse(VList.empty))
        ,
        arity = Some(1)
      )
    },
    "ⁿ" -> Modifier(
      "Minimum By",
      """|Minimum By Element
         |ᵐf: Minimum of top of stack based on results of f""".stripMargin,
      List("min-by", "minimum-by"),
      1
    ) { case List(ast) =>
      AST.Generated(
        () =>
          ctx ?=>
            val lst = ListHelpers.makeIterable(ctx.pop())
            val min = lst.minByOption { elem =>
              given elemCtx: Context = ctx.makeChild()
              elemCtx.push(elem)
              Interpreter.execute(ast)(using elemCtx)
              elemCtx.pop()
            }
            ctx.push(min.getOrElse(VList.empty))
        ,
        arity = Some(1)
      )
    },
    "ᵡ" -> Modifier(
      "Scan Fixed Point",
      """|Scan a function until it reaches a fixed point
         |ᵡf: scan f until a fixed point is reached / apply until a previous value is repeated, collecting intermediate results""".stripMargin,
      List("scan-fix:"),
      1
    ) { case List(ast) =>
      val lambdaAst = astToLambda(ast, ast.arity.getOrElse(1))
      AST.makeSingle(lambdaAst, AST.Command("Ŀ"))
    }
  )
end Modifiers
