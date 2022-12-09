package vyxal

import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import vyxal.impls.Elements

object Parser {
  type ParserRet = Either[VyxalCompilationError, AST]
  private def toValidName(name: String): String =
    name.filter(_.isLetterOrDigit).dropWhile(!_.isLetter)

  /** The parser takes a list of tokens and performs two sweeps of parsing:
    * structures + arity grouping and then modifiers. The first sweep deals
    * directly with the token list provided to the parser, and leaves its
    * results in a stack of ASTs (the stack data type is used because it means
    * that arity grouping is simply popping previous ASTs until a niladic state
    * is reached). The second sweep takes the stack of ASTs and applies the
    * logic of modifier grouping, placing its result in a single Group AST.
    */
  private def parse(code: List[VyxalToken]): ParserRet = {
    val asts = Stack[AST]()
    val program = code.to(Queue)
    // Convert the list of tokens to a queue so that ASTs like Structures can
    // freely take as many tokens as they need without needing to worry about
    // changing indexes like a for-loop would require.

    // Begin the first sweep of parsing

    while (program.nonEmpty) {
      program.dequeue() match {
        // Numbers, strings and newlines are trivial, and are simply evaluated
        case VyxalToken.Number(value) =>
          asts.push(AST.Number(VNum.from(value)))
        case VyxalToken.Str(value) => asts.push(AST.Str(value))
        case VyxalToken.Newline    => asts.push(AST.Newline)
        case VyxalToken.StructureOpen(open) =>
          parseStructure(open, asts, program)
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
            parse(element) match {
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
        case VyxalToken.Command(name) => parseCommand(name, asts, program)
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
  ): Unit = {
    val cmd = AST.Command(name)
    if (asts.isEmpty) {
      // Nothing to group, so just push
      asts.push(cmd)
    } else {
      Elements.elements(name).arity match {
        case None => asts.push(cmd)
        case Some(arity) =>
          @annotation.tailrec
          def group(neededArgs: Int, numToPop: Int = 0): Unit = {
            if (
              neededArgs > 0 && numToPop < asts.size && isNilad(asts(numToPop))
            ) {
              val nextArg = asts(numToPop)
              if (nextArg.arity == Some(0)) {
                group(neededArgs - 1, numToPop + 1)
              } else {
                asts.push(cmd)
              }
            } else {
              if (numToPop == 0) {
                asts.push(cmd)
              } else {
                val args = asts.take(numToPop).toList
                asts.dropInPlace(numToPop)
                asts.push(
                  AST.Group((cmd :: args).reverse, Some(neededArgs - numToPop))
                )
              }
            }
          }

          group(arity)
      }
    }
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
      asts: Stack[AST],
      program: Queue[VyxalToken]
  ): Unit = {
    var structureDepth: Int = 1
    val branches = ListBuffer.empty[List[VyxalToken]]
    val branch = ListBuffer.empty[VyxalToken]

    while (program.nonEmpty && structureDepth > 0) {
      val structureToken = program.dequeue()
      structureToken match {
        case VyxalToken.Branch => {
          if (structureDepth == 1) {
            branches += branch.toList
            branch.clear()
          } else {
            branch += structureToken
          }
        }
        case VyxalToken.StructureOpen(_) => {
          structureDepth += 1
          branch += structureToken
        }
        case VyxalToken.StructureClose(_) => {
          structureDepth -= 1
          if (structureDepth > 0) {
            branch += structureToken
          }
        }
        case _ => branch += structureToken
      }
    }
    // Make sure that the current branch is added to the list of branches,
    // because it won't be added by the branch token
    branches += branch.toList

    // Now that we have the branches, we can parse them into ASTs
    val branchList = ListBuffer[AST]()

    for (branch <- branches) {
      parse(branch) match {
        case Right(ast)  => branchList += ast
        case Left(error) => return Left(error)
      }
    }

    val parsedBranches = branchList.toList
    // Now, we can create the appropriate AST for the structure
    structureType match {
      case StructureType.If => {
        parsedBranches match {
          case List(thenBranch) => asts.push(AST.If(thenBranch, None))
          case List(thenBranch, elseBranch) =>
            asts.push(AST.If(thenBranch, Some(elseBranch)))
          case _ =>
            return Left(VyxalCompilationError("Invalid if statement"))
          // TODO: One day make this extended elif
        }
      }
      case StructureType.While => {
        parsedBranches match {
          case List(cond, body) => asts.push(AST.While(Some(cond), body))
          case List(body)       => asts.push(AST.While(None, body))
          case _ =>
            return Left(VyxalCompilationError("Invalid while statement"))
        }
      }
      case StructureType.For => {
        parsedBranches match {
          case List(cond, body) =>
            val name = Some(toValidName(cond.toVyxal))
            asts.push(AST.For(name, body))
          case List(body) => asts.push(AST.For(None, body))
          case _ =>
            return Left(VyxalCompilationError("Invalid for statement"))
        }
      }
      case lambdaType @ (
          StructureType.Lambda | StructureType.LambdaMap |
          StructureType.LambdaFilter | StructureType.LambdaReduce |
          StructureType.LambdaSort) =>
        val lambda = parsedBranches match {
          // todo actually parse arity and parameters
          case List(body) => AST.Lambda(1, List.empty, body)
          case _          => ???
        }
        // todo using the command names is a bit brittle
        //   maybe refer to the functions directly
        asts.push(lambdaType match {
          case StructureType.Lambda => lambda
          case StructureType.LambdaMap =>
            AST.makeSingle(lambda, AST.Command("M"))
          case StructureType.LambdaFilter =>
            AST.makeSingle(lambda, AST.Command("F"))
          case StructureType.LambdaReduce =>
            AST.makeSingle(lambda, AST.Command("R"))
          case StructureType.LambdaSort =>
            AST.makeSingle(lambda, AST.Command("ṡ"))
        })
    }
  }

  def parse(code: String): Either[VyxalCompilationError, AST] = {
    Lexer(code).flatMap { tokens =>
      val preprocessed = preprocess(tokens)
      val parsed = parse(preprocessed) match {
        case Right(ast) => Right(postprocess(ast))
        case Left(error) =>
          Left(VyxalCompilationError(s"Error parsing code: $error"))
      }
      parsed
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
        parse(tokens) match {
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
