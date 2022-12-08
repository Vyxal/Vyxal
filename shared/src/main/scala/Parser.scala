package vyxal

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}

import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import vyxal.impls.Elements

object VyxalParser {
  private def toValidName(name: String): String =
    name.filter(_.isLetterOrDigit).dropWhile(!_.isLetter)

  private def parse(
      code: List[VyxalToken]
  ): Either[VyxalCompilationError, AST] = {
    val asts = Stack[AST]()
    val program = code.to(Queue)
    // First sweep, doesn't do modifiers, does do arity grouping
    while (program.nonEmpty) {
      val token = program.dequeue()
      token match {
        case VyxalToken.Number(value) =>
          asts.push(AST.Number(VNum.from(value)))
        case VyxalToken.Str(value) => asts.push(AST.Str(value))
        case VyxalToken.Newline    => asts.push(AST.Newline)
        case VyxalToken.StructureOpen(structureType) => {
          var structureDepth: Int = 1
          var branches: List[List[VyxalToken]] = List()
          var branch: List[VyxalToken] = List()
          while (program.nonEmpty && structureDepth > 0) {
            val structureToken = program.dequeue()
            structureToken match {
              case VyxalToken.Branch => {
                // append branch to branches
                if (structureDepth == 1) {
                  branches = branches :+ branch
                  branch = List()
                } else {
                  branch = branch :+ structureToken
                }
              }
              case VyxalToken.StructureOpen(_) => {
                structureDepth += 1
                branch = branch :+ structureToken
              }
              case VyxalToken.StructureClose(_) => {
                structureDepth -= 1
                if (structureDepth > 0) {
                  branch = branch :+ structureToken
                }
              }
              case _ => branch = branch :+ structureToken
            }
          }
          branches = branches :+ branch
          var parsedBranches = List[AST]()

          for (branch <- branches) {
            parse(branch) match {
              case Right(ast)  => parsedBranches = parsedBranches :+ ast
              case Left(error) => return Left(error)
            }
          }

          structureType match {
            case "[" => {
              parsedBranches match {
                case List(thenBranch) =>
                  asts.push(AST.If(thenBranch, None))
                case List(thenBranch, elseBranch) =>
                  asts.push(AST.If(thenBranch, Some(elseBranch)))
                case _ =>
                  return Left(VyxalCompilationError("Invalid if statement"))
                // TODO: One day make this extended elif
              }
            }
            case "{" => {
              parsedBranches match {
                case List(cond, body) =>
                  asts.push(AST.While(Some(cond), body))
                case List(body) => asts.push(AST.While(None, body))
                case _ =>
                  return Left(VyxalCompilationError("Invalid while statement"))
              }
            }

            case "(" => {
              parsedBranches match {
                case List(cond, body) =>
                  asts.push(AST.For(Some(toValidName(cond.toVyxal)), body))
                case List(body) => asts.push(AST.For(None, body))
                case _ =>
                  return Left(VyxalCompilationError("Invalid for statement"))
              }
            }

            case "λ" | "ƛ" | "Ω" | "₳" | "µ" =>
              val lambda = parsedBranches match {
                // todo actually parse arity and parameters
                case List(body) => AST.Lambda(1, List.empty, body)
                case _          => ???
              }
              // todo using the command names is a bit brittle
              //   maybe refer to the functions directly
              asts.push((structureType: @unchecked) match {
                case "λ" => lambda
                case "ƛ" => AST.makeSingle(lambda, AST.Command("M", Some(2)))
                case "Ω" => AST.makeSingle(lambda, AST.Command("F", Some(2)))
                case "₳" => AST.makeSingle(lambda, AST.Command("R", Some(2)))
                case "µ" => AST.makeSingle(lambda, AST.Command("ṡ", Some(2)))
              })

          }

        }
        case VyxalToken.ListOpen => {
          var listDepth: Int = 1
          val elements = ListBuffer[List[VyxalToken]]()
          var element = List[VyxalToken]()
          while (program.nonEmpty && listDepth > 0) {
            print(listDepth)
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
          println(elements)
          val parsedElements = ListBuffer[AST]()
          for (element <- elements) {
            parse(element) match {
              case Right(ast)  => parsedElements += ast
              case Left(error) => return Left(error)
            }
          }

          asts.push(AST.Lst(parsedElements.toList))

        }
        case VyxalToken.Command(name) => {
          getArity(name) match {
            case -1 => asts.push(AST.Command(name, None))
            case 0  => asts.push(AST.Command(name, Some(0)))
            case 1 => {
              if (asts.isEmpty) { // Nothing to group, so just push
                asts.push(AST.Command(name, Some(1)))
              } else {
                val top = asts.top
                if (isNilad(top)) {
                  asts.pop()
                  asts.push(
                    AST.Group(List(top, AST.Command(name, Some(1))), Some(0))
                  )
                } else {
                  asts.push(AST.Command(name, Some(1)))
                }
              }
            }
            case 2 => {
              if (asts.isEmpty) {
                asts.push(AST.Command(name, Some(2)))
              } else {
                val top = asts.take(2)
                val topNilads = top.map(isNilad).toList.reverse
                topNilads match {
                  case List(true, true) => {
                    asts.pop()
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(1), top(0), AST.Command(name, Some(2))),
                        Some(0)
                      )
                    )
                  }
                  case List(false, true) => {
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(1), AST.Command(name, Some(2))),
                        Some(1)
                      )
                    )
                  }
                  case List(true) => {
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(0), AST.Command(name, Some(2))),
                        Some(1)
                      )
                    )
                  }
                  case _ => {
                    asts.push(AST.Command(name, Some(2)))
                  }
                }
              }
            }
            case 3 => {
              if (asts.isEmpty) {
                asts.push(AST.Command(name, Some(3)))
              } else {
                val top = asts.take(3)
                val topNilads = top.map(isNilad).toList.reverse
                topNilads match {
                  case List(true, true, true) => {
                    asts.pop()
                    asts.pop()
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(
                          top(2),
                          top(1),
                          top(0),
                          AST.Command(name, Some(3))
                        ),
                        Some(0)
                      )
                    )
                  }
                  case List(false, true, true) => {
                    asts.pop()
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(2), top(1), AST.Command(name, Some(3))),
                        Some(1)
                      )
                    )
                  }
                  case List(_, false, true) => {
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(0), AST.Command(name, Some(3))),
                        Some(2)
                      )
                    )
                  }
                  case List(true, true) => {
                    asts.pop()
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(1), top(0), AST.Command(name, Some(3))),
                        Some(1)
                      )
                    )
                  }
                  case List(false, true) => {
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(1), AST.Command(name, Some(3))),
                        Some(2)
                      )
                    )
                  }
                  case List(true) => {
                    asts.pop()
                    asts.push(
                      AST.Group(
                        List(top(0), AST.Command(name, Some(3))),
                        Some(2)
                      )
                    )
                  }
                  case _ => {
                    asts.push(AST.Command(name, Some(3)))
                  }
                }
              }
            }
            case _: Int => asts.push(AST.Command(name, None))
          }
        }
        case VyxalToken.MonadicModifier(v) => asts.push(AST.JunkModifier(v, 1))
        case VyxalToken.DyadicModifier(v)  => asts.push(AST.JunkModifier(v, 2))
        case VyxalToken.TriadicModifier(v) => asts.push(AST.JunkModifier(v, 3))
        case VyxalToken.TetradicModifier(v) =>
          asts.push(AST.JunkModifier(v, 4))
        case VyxalToken.SpecialModifier(v) => asts.push(AST.SpecialModifier(v))
      }
    }

    var finalAsts = Stack[AST]()
    while (!asts.isEmpty) {
      val topAst = asts.pop()
      topAst match {
        case AST.Newline => ???
        case AST.JunkModifier(name, arity) => {
          arity match {
            case 1 =>
              finalAsts.push(
                Modifiers.modifiers(name).impl(List(finalAsts.pop()))
              )
            case 2 =>
              finalAsts.push(
                Modifiers
                  .modifiers(name)
                  .impl(
                    List(finalAsts.pop(), finalAsts.pop())
                  )
              )
            case 3 =>
              finalAsts.push(
                Modifiers
                  .modifiers(name)
                  .impl(
                    List(
                      finalAsts.pop(),
                      finalAsts.pop(),
                      finalAsts.pop()
                    )
                  )
              )

            case 4 => ??? // Are there even arity 4 modifiers?

          }
        }
        case AST.SpecialModifier(name) => {
          name match {
            case "ᵜ" => {
              var lambdaAsts = ListBuffer[AST]()
              while (asts.top != AST.Newline) {
                lambdaAsts += finalAsts.pop()
              }
              finalAsts.push(
                AST.Lambda(
                  1,
                  List(),
                  AST.Group(lambdaAsts.toList.reverse, None)
                )
              )
            }
            case "ᵗ" => ??? // TODO: Implement tie
            case _ =>
              ??? // The hell kinda special modifier is this? Actually unreachable
          }
        }
        case _ => finalAsts.push(topAst)
      }
    }

    Right(AST.Group(finalAsts.toList, None))
  }

  def parse(code: String): Either[VyxalCompilationError, AST] = {
    Lexer(code).flatMap { tokens =>
      val preprocessed = preprocess(tokens)
      val parsed = parse(preprocessed) match {
        case Right(ast) => Right(ast)
        case Left(error) =>
          Left(VyxalCompilationError(s"Error parsing code: $error"))
      }
      parsed match
        case Right(ast)  => Right(postprocess(ast))
        case Left(error) => Left(error)
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

  private def getArity(cmd: String) = Elements.elements(cmd).arity.getOrElse(-1)

  private def getArity(cmd: AST.Command) = cmd.arity

  private def getArity(cmd: AST.Group) = cmd.arity

  private def isNilad(ast: AST) = ast match {
    case AST.Command(_, arity) =>
      arity match {
        case Some(0) => true
        case _       => false
      }
    case AST.Number(_) => true
    case AST.Str(_)    => true
    case AST.Lst(Nil)  => true
    case AST.Lambda(_, _, _) =>
      true // The lambda object itself is a nilad, not the actual function call
    case AST.Group(_, arity) =>
      arity match {
        case Some(0) => true
        case _       => false
      }
    case AST.Lst(_) => true
    case _          => false
  }
  def parseInput(input: String): VAny = {
    Lexer(input).toOption
      .flatMap { tokens =>
        parse(tokens) match {
          case Right(ast) =>
            ast match {
              case AST.Number(n) => Some[VAny](n)
              case AST.Str(s)    => Some(s)
              case AST.Lst(l) =>
                Some[VAny](VList(l.map(e => parseInput(e.toString))*))
              case _ => None
            }
          case Left(_) => None
        }
      }
      .getOrElse(input)
  }
}
