package vyxal

import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import vyxal.impls.Elements
import spire.syntax.truncatedDivision

object Parser {

  /** The return type of a parser. If the parser fails, then it's a `Left`
    * containing a `VyxalCompilationError`. If the parser succeeds, it's a tuple
    * containing whatever it parsed
    */
  type ParserRet[T] = Either[VyxalCompilationError, T]

  private def toValidName(name: String): String =
    name.filter(_.isLetterOrDigit).dropWhile(!_.isLetter)

  /** The parser takes a list of tokens and performs two sweeps of parsing:
    * structures + arity grouping and then modifiers. The first sweep deals
    * directly with the token list provided to the parser, and leaves its
    * results in a stack of ASTs (the stack data type is used because it means
    * that arity grouping is simply popping previous ASTs until a niladic state
    * is reached). The second sweep takes the stack of ASTs and applies the
    * logic of modifier grouping, placing its result in a single Group AST.
    *
    * @param topLevel
    *   Whether this is the topmost call to parse, in which case it should
    *   remove any `StructureAllClose`s left behind by `parseStructure`
    */
  private def parse(
      program: Queue[VyxalToken],
      topLevel: Boolean = false
  ): ParserRet[AST] = {
    val asts = Stack[AST]()
    // Convert the list of tokens to a queue so that ASTs like Structures can
    // freely take as many tokens as they need without needing to worry about
    // changing indexes like a for-loop would require.

    // Begin the first sweep of parsing

    while (program.nonEmpty && !isCloser(program.front)) {
      (program.dequeue(): @unchecked) match {
        // Numbers, strings and newlines are trivial, and are simply evaluated
        case VyxalToken.Number(value) =>
          asts.push(AST.Number(VNum.from(value)))
        case VyxalToken.Str(value) => asts.push(AST.Str(value))
        case VyxalToken.Newline    => asts.push(AST.Newline)
        case VyxalToken.StructureOpen(open) =>
          parseStructure(open, program) match {
            case Right(ast) => asts.push(ast)
            case l          => return l
          }
          if (
            topLevel && program.nonEmpty && program.front == VyxalToken.StructureAllClose
          ) {
            program.dequeue()
          }
        /*
         * List are just structures with two different opening and closing
         * token possibilities, so handle them the same way.
         */
        case VyxalToken.ListOpen => {
          var listDepth: Int = 1
          val elements = ListBuffer[List[VyxalToken]]()
          var element = List[VyxalToken]()
          while (program.nonEmpty && listDepth > 0) {
            val listToken = program.dequeue()
            listToken match {
              case VyxalToken.Branch => {
                if (listDepth == 1) {
                  elements += element
                  element = List()
                } else {
                  element = element :+ listToken
                }
              }
              case VyxalToken.ListOpen => {
                listDepth += 1
                element = element :+ listToken
              }
              case VyxalToken.ListClose => {
                listDepth -= 1
                if (listDepth > 0)
                  element = element :+ listToken
              }
              case _ => element = element :+ listToken
            }
          }
          elements += element

          val parsedElements = ListBuffer[AST]()
          for (element <- elements) {
            parse(element.to(Queue)) match {
              case Right(ast)  => parsedElements += ast
              case Left(error) => return Left(error)
            }
          }
          asts.push(AST.Lst(parsedElements.toList))

        }
        /*
         * Now comes command handling. When handling commands, we need to
         * perform the arity grouping when pushing the command. This is done
         * by checking the arity of the command, and then checking however many
         * top stack elements are needed to make the command equivalent to a
         * nilad. For arities -1 and 0, the command doesn't need to be grouped.
         */
        case VyxalToken.Command(name) =>
          parseCommand(name, asts, program) match {
            case Right(ast) => asts.push(ast)
            case l          => return l
          }
        // At this stage, modifiers aren't explicitly handled, so just push a
        // temporary AST to comply with the type of the AST stack
        case VyxalToken.MonadicModifier(v) => asts.push(AST.JunkModifier(v, 1))
        case VyxalToken.DyadicModifier(v)  => asts.push(AST.JunkModifier(v, 2))
        case VyxalToken.TriadicModifier(v) => asts.push(AST.JunkModifier(v, 3))
        case VyxalToken.TetradicModifier(v) =>
          asts.push(AST.JunkModifier(v, 4))
        case VyxalToken.SpecialModifier(v) => asts.push(AST.SpecialModifier(v))
      }
    }

    val finalAsts = Stack[AST]()
    while (!asts.isEmpty) {
      val topAst = asts.pop()
      topAst match {
        case AST.Newline => ???
        case AST.JunkModifier(name, arity) =>
          if (arity > 0) {
            finalAsts.push(
              Modifiers.modifiers(name).impl(List.fill(arity)(finalAsts.pop()))
            )
          }
        case AST.SpecialModifier(name) => {
          (name: @unchecked) match {
            case "ᵜ" => {
              var lambdaAsts = ListBuffer[AST]()
              while (asts.top != AST.Newline) {
                lambdaAsts += finalAsts.pop()
              }
              finalAsts.push(
                AST.Lambda(
                  1,
                  List(),
                  AST.makeSingle(lambdaAsts.toList.reverse*)
                )
              )
            }
            case "ᵗ" => ??? // TODO: Implement tie
            case _ =>
              ??? // The hell kinda special modifier is this? Actually unreachable
            // Why? Because the lexer only recognises ᵜ and ᵗ as special modifiers
            // if you've got to this case, then someone has figured out how to
            // screw around with ACE exploits. Good job, you.
          }
        }
        case _ => finalAsts.push(topAst)
      }
    }

    Right(AST.makeSingle(finalAsts.toList*))
  }

  /** @param name The command's name */
  def parseCommand(
      name: String,
      asts: Stack[AST],
      program: Queue[VyxalToken]
  ): ParserRet[AST] = {
    if (!Elements.elements.contains(name)) {
      Left(VyxalCompilationError(s"No such command: '$name'"))
    } else {
      val cmd = AST.Command(name)
      val grouped = if (asts.isEmpty) {
        // Nothing to group, so just push
        cmd
      } else {
        Elements.elements(name).arity match {
          case None => cmd
          case Some(arity) =>
            @annotation.tailrec
            def group(neededArgs: Int, numToPop: Int = 0): AST = {
              if (
                neededArgs > 0 && numToPop < asts.size && isNilad(
                  asts(numToPop)
                )
              ) {
                val nextArg = asts(numToPop)
                if (nextArg.arity == Some(0)) {
                  group(neededArgs - 1, numToPop + 1)
                } else {
                  cmd
                }
              } else {
                if (numToPop == 0) {
                  cmd
                } else {
                  val args = asts.take(numToPop).toList
                  asts.dropInPlace(numToPop)
                  AST.Group(
                    (cmd :: args).reverse,
                    Some(neededArgs - numToPop)
                  )
                }
              }
            }
            group(arity)
        }
      }

      Right(grouped)
    }
  }

  /** Parse branches for an unknown structure, nothing more.
    *
    * @return
    *   The parsed branches, along with whether or not it encountered a
    *   `StructureAllClose`
    */
  def parseBranches(program: Queue[VyxalToken]): ParserRet[List[AST]] = {
    if (program.isEmpty) {
      Right((List(AST.makeSingle()), None))
    }
    val branches = ListBuffer.empty[AST]

    while (
      program.nonEmpty
      && (program.front == VyxalToken.Branch || !isCloser(program.front))
    ) {
      parse(program) match {
        case Left(err) => return Left(err)
        case Right(ast) =>
          branches += ast
          if (program.nonEmpty && program.front == VyxalToken.Branch) {
            // Get rid of the `|` token to move on to the next branch
            program.dequeue()
            if (
              program.isEmpty || (isCloser(
                program.front
              ) && program.front != VyxalToken.Branch)
            ) {
              // Don't forget empty branches at the end
              branches += AST.makeSingle()
            }
          }
      }
    }

    if (program.nonEmpty && program.front != VyxalToken.StructureAllClose) {
      // Get rid of the structure closer
      program.dequeue()
    }

    Right(branches.toList)
  }

  /** @param structureType
    *   The opening character of the structure
    *
    * Structures are a bit more complicated. They require keeping track of a)
    * the branches of the structure and b) the number of structures that have
    * been previously opened (this is to allow for nested structures). The
    * algorithm is as follows:
    *   - While the program is not empty and structure depth > 0
    *   - Dequeue the next token
    *   - If the token is a branch (|), either add the current branch to the
    *     list of branches in this structure, or just add it to the current
    *     branch
    *   - If the token is a structure open, increment the structure depth and
    *     append the token to the current branch
    *   - If the token is a structure close, decrement the structure depth, and
    *     if we're still in a structure, append the token to the current branch
    *   - Otherwise, append the token to the current branch
    */
  private def parseStructure(
      structureType: StructureType,
      program: Queue[VyxalToken]
  ): ParserRet[AST] = {
    val id =
      Option.when(structureType == StructureType.For)(parseIdentifier(program))

    parseBranches(program).map { branches =>
      // Now, we can create the appropriate AST for the structure
      structureType match {
        case StructureType.If => {
          branches match {
            case List(thenBranch) => AST.If(thenBranch, None)
            case List(thenBranch, elseBranch) =>
              AST.If(thenBranch, Some(elseBranch))
            case _ =>
              return Left(VyxalCompilationError("Invalid if statement"))
            // TODO: One day make this extended elif
          }
        }
        case StructureType.While => {
          branches match {
            case List(cond, body) => AST.While(Some(cond), body)
            case List(body)       => AST.While(None, body)
            case _ =>
              return Left(VyxalCompilationError("Invalid while statement"))
          }
        }
        case StructureType.For => {
          branches match {
            case List(cond, body) =>
              val name = Some(toValidName(id.get))
              AST.For(name, body)
            case List(body) => AST.For(None, body)
            case _ =>
              return Left(VyxalCompilationError("Invalid for statement"))
          }
        }
        case lambdaType @ (StructureType.Lambda | StructureType.LambdaMap |
            StructureType.LambdaFilter | StructureType.LambdaReduce |
            StructureType.LambdaSort) =>
          val lambda = branches match {
            // todo actually parse arity and parameters
            case List(body) => AST.Lambda(1, List.empty, body)
            case _          => ???
          }
          // todo using the command names is a bit brittle
          //   maybe refer to the functions directly
          lambdaType match {
            case StructureType.Lambda => lambda
            case StructureType.LambdaMap =>
              AST.makeSingle(lambda, AST.Command("M"))
            case StructureType.LambdaFilter =>
              AST.makeSingle(lambda, AST.Command("F"))
            case StructureType.LambdaReduce =>
              AST.makeSingle(lambda, AST.Command("R"))
            case StructureType.LambdaSort =>
              AST.makeSingle(lambda, AST.Command("ṡ"))
          }
      }
    }
  }

  /** Parse an identifier (for for loops) */
  private def parseIdentifier(program: Queue[VyxalToken]): String = {
    val id = StringBuilder()
    while (program.nonEmpty && !isCloser(program.front)) {
      id ++= program.front.value
    }
    if (program.nonEmpty && program.front == VyxalToken.Branch) {
      // Get rid of `|` so that the other branches can be parsed
      program.dequeue()
    }
    toValidName(id.toString())
  }

  /** Whether this token is a branch or a structure/list closer */
  def isCloser(token: VyxalToken): Boolean =
    token match {
      case VyxalToken.Branch            => true
      case VyxalToken.ListClose         => true
      case VyxalToken.StructureClose(_) => true
      case VyxalToken.StructureAllClose => true
      case _                            => false
    }

  def parse(code: String): Either[VyxalCompilationError, AST] = {
    Lexer(code).flatMap { tokens =>
      val preprocessed = preprocess(tokens).to(Queue)
      val parsed = parse(preprocessed, true)
      if (preprocessed.nonEmpty) {
        // Some tokens were left at the end, which should never happen
        Left(
          VyxalCompilationError(
            s"Error parsing code: These tokens were not parsed ${preprocessed.toList}"
          )
        )
      } else {
        parsed match {
          case Right(ast) => Right(postprocess(ast))
          case Left(error) =>
            Left(VyxalCompilationError(s"Error parsing code: $error"))
        }
      }
    }
  }

  private def preprocess(tokens: List[VyxalToken]): List[VyxalToken] = {
    val processed = ListBuffer[VyxalToken]()
    tokens.foreach {
      case VyxalToken.StructureClose(")") => {
        processed += VyxalToken.StructureClose("}")
        processed += VyxalToken.StructureClose("}")
      }
      case x => processed += x
    }
    processed.toList
  }

  private def postprocess(asts: AST): AST = {
    val temp = asts match {
      case AST.Group(elems, _) => {
        val nilads = elems.reverse.takeWhile(isNilad).reverse
        val rest = elems.dropRight(nilads.length)
        AST.Group(nilads ++ rest, None)
      }
      case _ => asts
    }
    temp
  }

  private def isNilad(ast: AST) = ast.arity == Some(0)

  def parseInput(input: String): VAny = {
    Lexer(input).toOption
      .flatMap { tokens =>
        parse(tokens.to(Queue), true) match {
          case Right(ast) =>
            ast match {
              case AST.Number(n) => Some[VAny](n)
              case AST.Str(s)    => Some(s)
              case AST.Lst(l) =>
                Some(VList(l.map(e => parseInput(e.toString))*))
              case _ => None
            }
          case Left(_) => None
        }
      }
      .getOrElse(input)
  }
}
