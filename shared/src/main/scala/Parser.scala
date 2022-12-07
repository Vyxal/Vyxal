package vyxal

import scala.collection.mutable.Queue
import scala.collection.mutable.ListBuffer

def toValidName(name: String): String =
  name.filter(_.isLetterOrDigit).dropWhile(!_.isLetter)

def parse(
    code: List[VyxalToken]
): Either[VyxalCompilationError, AST] = {
  val asts = Queue[AST]()
  val program = code.to(Queue)
  while (program.nonEmpty) {
    val token = program.dequeue()
    token match {
      case VyxalToken.Number(value) =>
        asts.enqueue(AST.Number(VNum.from(value)))
      case VyxalToken.Str(value) => asts.enqueue(AST.Str(value))
      case VyxalToken.Newline    => asts.enqueue(AST.Newline)
      case VyxalToken.StructureOpen(structureType) => {
        var branches: List[List[VyxalToken]] = List()
        var branch: List[VyxalToken] = List()
        while (
          program.nonEmpty && program.head != VyxalToken.StructureClose
        ) {
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
                asts.enqueue(AST.If(thenBranch, None))
              case List(thenBranch, elseBranch) =>
                asts.enqueue(AST.If(thenBranch, Some(elseBranch)))
              case _ =>
                return Left(VyxalCompilationError("Invalid if statement"))
              // TODO: One day make this extended elif
            }
          }
          case "{" => {
            parsedBranches match {
              case List(cond, body) =>
                asts.enqueue(AST.While(Some(cond), body))
              case List(body) => asts.enqueue(AST.While(None, body))
              case _ =>
                return Left(VyxalCompilationError("Invalid while statement"))
            }
          }

          case "(" => {
            parsedBranches match {
              case List(cond, body) =>
                asts.enqueue(AST.For(Some(toValidName(cond.toVyxal)), body))
              case List(body) => asts.enqueue(AST.For(None, body))
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
            asts.enqueue((structureType: @unchecked) match {
              case "λ" => lambda
              case "ƛ" => AST.makeSingle(lambda, AST.Command("M"))
              case "Ω" => AST.makeSingle(lambda, AST.Command("F"))
              case "₳" => AST.makeSingle(lambda, AST.Command("R"))
              case "µ" => AST.makeSingle(lambda, AST.Command("ṡ"))
            })

        }

      }
      case VyxalToken.ListOpen => {
        val elements = ListBuffer[List[VyxalToken]]()
        var element = List[VyxalToken]()
        while (
          program.nonEmpty && program.head != VyxalToken.ListClose
        ) {
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

        asts.enqueue(AST.Lst(parsedElements.toList))

      }
    }
  }

  Right(AST.Group(asts.toList))
}

object VyxalParser {
  def parse(code: String): Either[VyxalCompilationError, AST] = {
    Right(AST.Newline)
  }

  def parseInput(input: String): VAny = {
    3
  }
}
