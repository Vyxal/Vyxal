import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}

object Parser extends Parsers {
  override type Elem = VyxalToken

  def program: Parser[List[AST]] = phrase(rep1(element))
  def element: Parser[AST] =
    number | command | structure | monadicModifier
  private def number: Parser[AST] =
    accept("number", { case VyxalToken.Number(value) => AST.Number(value) })
  private def command: Parser[AST] =
    accept("command", { case VyxalToken.Command(value) => AST.Command(value) })

  def structure: Parser[AST] = {
    val open = accept(
      "structure open",
      { case VyxalToken.StructureOpen(value) => value }
    )
    val close = accept(
      "structure close",
      { case VyxalToken.StructureClose(value) => value }
    )
    val branch = accept(
      "branch",
      { case VyxalToken.Branch => "|" }
    )
    open ~ rep(element) ~ rep(
      branch ~ rep(element)
    ) ~ close ^^ { case open ~ firstBranch ~ branches ~ _ =>
      println("branches")
      println(branches)
      AST.Structure(open, firstBranch :: branches.map(_._2))
    }
  }

  def monadicModifier: Parser[AST] = {
    val modifier = accept(
      "monadic modifier",
      { case VyxalToken.MonadicModifier(value) => value }
    )
    modifier ~ element ^^ { case mod ~ elem => AST.MonadicModifier(mod, elem) }
  }

  def apply(
      code: List[VyxalToken]
  ): Either[VyxalCompilationError, List[AST]] = {
    program(VyxalTokenReader(code)) match {
      case NoSuccess(msg, next)  => Left(VyxalLexerError(msg))
      case Success(result, next) => Right(result)
    }
  }
}

class VyxalTokenReader(tokens: Seq[VyxalToken]) extends Reader[VyxalToken] {
  override def first: VyxalToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[VyxalToken] = new VyxalTokenReader(tokens.tail)
}
