package vyxal

import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import vyxal.impls.Elements

def toValidName(name: String): String =
  name.filter(_.isLetterOrDigit).dropWhile(!_.isLetter)

def parse(
    code: List[VyxalToken]
): Either[VyxalCompilationError, AST] = {
  val asts = Stack[AST]()
  val program = code.to(Queue)
  while (program.nonEmpty) {
    val token = program.dequeue()
    token match {
      case VyxalToken.Number(value) =>
        asts.push(AST.Number(VNum.from(value)))
      case VyxalToken.Str(value) => asts.push(AST.Str(value))
      case VyxalToken.Newline    => asts.push(AST.Newline)
      case VyxalToken.StructureOpen(structureType) => {
        var branches: List[List[VyxalToken]] = List()
        var branch: List[VyxalToken] = List()
        while (program.nonEmpty && program.head != VyxalToken.StructureClose) {
          val structureToken = program.dequeue()
          structureToken match {
            case VyxalToken.Branch => {
              // append branch to branches
              branches = branches :+ branch
              branch = List()
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
        val elements = ListBuffer[List[VyxalToken]]()
        var element = List[VyxalToken]()
        while (program.nonEmpty && program.head != VyxalToken.ListClose) {
          val listToken = program.dequeue()
          listToken match {
            case VyxalToken.Branch => {
              elements += element
              element = List()
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
              val top = asts.takeRight(2)
              val topNilads = top.map(isNilad).toList
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
                      List(top(1), AST.Command(name, Some(1))),
                      Some(1)
                    )
                  )
                }
                case List(true) => {
                  asts.pop()
                  asts.push(
                    AST.Group(
                      List(top(0), AST.Command(name, Some(1))),
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
              val top = asts.takeRight(3)
              val topNilads = top.map(isNilad).toList
              topNilads match {
                case List(true, true, true) => {
                  asts.pop()
                  asts.pop()
                  asts.pop()
                  asts.push(
                    AST.Group(
                      List(top(2), top(1), top(0), AST.Command(name, Some(3))),
                      Some(0)
                    )
                  )
                }
                case List(false, true, true) => {
                  asts.pop()
                  asts.pop()
                  asts.push(
                    AST.Group(
                      List(top(2), top(1), AST.Command(name, Some(2))),
                      Some(1)
                    )
                  )
                }
                case List(_, false, true) => {
                  asts.pop()
                  asts.push(
                    AST.Group(
                      List(top(0), AST.Command(name, Some(2))),
                      Some(2)
                    )
                  )
                }
                case List(true, true) => {
                  asts.pop()
                  asts.pop()
                  asts.push(
                    AST.Group(
                      List(top(1), top(0), AST.Command(name, Some(2))),
                      Some(1)
                    )
                  )
                }
                case List(false, true) => {
                  asts.pop()
                  asts.push(
                    AST.Group(
                      List(top(1), AST.Command(name, Some(1))),
                      Some(2)
                    )
                  )
                }
                case List(true) => {
                  asts.pop()
                  asts.push(
                    AST.Group(
                      List(top(0), AST.Command(name, Some(1))),
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

    }
  }

  Right(AST.Group(asts.toList.reverse, None))
}

def parse(code: String): Either[VyxalCompilationError, AST] = {
  Lexer(code).flatMap { tokens =>
    parse(tokens) match {
      case Right(ast) => Right(ast)
      case Left(error) =>
        Left(VyxalCompilationError(s"Error parsing code: $error"))
    }
  }
}

def getArity(cmd: String) = Elements.elements(cmd).arity.getOrElse(-1)

def getArity(cmd: AST.Command) = cmd.arity

def getArity(cmd: AST.Group) = cmd.arity

def isNilad(ast: AST) = ast match {
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
  case _ => false
}

object VyxalParser {
  def parse(code: String): Either[VyxalCompilationError, AST] = {
    Right(AST.Newline)
  }

  def parseInput(input: String): VAny = {
    3
  }
}
