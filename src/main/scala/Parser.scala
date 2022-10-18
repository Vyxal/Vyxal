import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}

case class VyxalParserError(msg: String) extends VyxalCompilationError

object Parser extends Parsers {
  override type Elem = VyxalToken

  def program: Parser[List[AST]] = phrase(rep1(element | monadicModifier)) ^^ {
    case elements => elements
  }
  def element: Parser[AST] =
    number | command | structure
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
      AST.Structure(open, firstBranch :: branches.map(_._2))
    }
  }

  def monadicModifier: Parser[AST] = {
    val modifier = accept(
      "monadic modifier",
      { case VyxalToken.MonadicModifier(value) => println(value); value }
    )
    element ~ modifier ^^ { case elem ~ modi =>
      println(modi); println(elem);
      AST.MonadicModifier(modi, elem)
    }
  }

  def apply(
      code: List[VyxalToken]
  ): Either[VyxalCompilationError, List[AST]] = {
    program(VyxalTokenReader(code)) match {
      case NoSuccess(msg, next)  => Left(VyxalParserError(msg))
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
