import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}

object Parser extends Parsers {
  override type Elem = VyxalToken

  private def parseAll: Parser[List[AST]] = ???

  def parse(code: String): Either[VyxalCompilationError, List[AST]] = {
    Lexer(code).flatMap { tokens =>
      parseAll(VyxalTokenReader(tokens)) match {
        case NoSuccess(msg, next)  => Left(VyxalLexerError(msg))
        case Success(result, next) => Right(result)
      }
    }
  }
}

class VyxalTokenReader(tokens: Seq[VyxalToken]) extends Reader[VyxalToken] {
  override def first: VyxalToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[VyxalToken] = new VyxalTokenReader(tokens.tail)
}
