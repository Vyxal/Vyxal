package vyxal

import scala.collection.mutable.Queue

def toValidName(name: String): String =
  name.filter(_.isLetterOrDigit).dropWhile(!_.isLetter)

def parse(
    code: List[VyxalToken]
): Either[VyxalCompilationError, AST] = {
  var asts = Queue()[AST]
  var program = Queue() ++ code
  while (program.nonEmpty) {
    val token = program.dequeue()
    token match {
      case VyxalToken.Number(value) =>
        asts.enqueue(AST.Number(VNum.from(value)))
      case VyxalToken.Str(value) => asts.enqueue(AST.Str(value))
      case VyxalToken.Newline    => asts.enqueue(AST.Newline)
      case VyxalToken.StructureOpen(structureType) => {
        var branches = List()[List[VyxalToken]]
        var branch = List()[VyxalToken]
        while (
          program.nonEmpty && program.head != VyxalToken.StructureClose(value)
        ) {
          val structureToken = program.dequeue()
          structureToken match {
            case VyxalToken.Branch => {
              branches = branches +: branch
              branch = List()
            }
            case _ => branch = branch :+ structureToken
          }
        }
        branches = branches :+ branch
        branches.foreach(branch => {
          parse(branch) match {
            case Left(error) => return Left(error)
            case Right(ast)  => ast
          }
        })

        structureType match {
          case "[" => {
            branches match {
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
            branches match {
              case List(cond, body) =>
                asts.enqueue(AST.While(Some(cond), body))
              case List(body) => asts.enqueue(AST.While(None, body))
              case _ =>
                return Left(VyxalCompilationError("Invalid while statement"))
            }
          }

          case "(" => {
            branches match {
              case List(cond, body) =>
                asts.enqueue(AST.For(Some(toValidName(cond.toVyxal)), body))
              case List(body) => asts.enqueue(AST.For(None, body))
              case _ =>
                return Left(VyxalCompilationError("Invalid for statement"))
            }
          }

          case "λ" | "ƛ" | "Ω" | "₳" | "µ" =>
            val lambda = branches match {
              // todo actually parse arity and parameters
              case List(body) => AST.Lambda(1, List.empty, body)
              case _          => ???
            }
            // todo using the command names is a bit brittle
            //   maybe refer to the functions directly
            asts.enqueue(structureType match {
              case "λ" => lambda
              case "ƛ" => AST.makeSingle(lambda, AST.Command("M"))
              case "Ω" => AST.makeSingle(lambda, AST.Command("F"))
              case "₳" => AST.makeSingle(lambda, AST.Command("R"))
              case "µ" => AST.makeSingle(lambda, AST.Command("ṡ"))
            })

        }

      }
      case VyxalToken.ListOpen => {}
    }
  }
  Right(asts.toList)
}
