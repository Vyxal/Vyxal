import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}
import scala.collection.mutable.ListBuffer

class VyxalTokenReader(tokens: Seq[VyxalToken]) extends Reader[VyxalToken] {
  override def first: VyxalToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[VyxalToken] = new VyxalTokenReader(tokens.tail)
}

object VyxalParser extends Parsers {
  type Elem = VyxalToken

  // The VyxalToken.StructureAllClose.? is to get rid of leftover ']'s from parsing structures
  def parseAll: Parser[List[AST]] = phrase(
    rep(element <~ VyxalToken.StructureAllClose.?)
  )

  def nonStructElement: Parser[AST] = number | string | command | modifier

  def element: Parser[AST] = nonStructElement | structure

  def number: Parser[AST] =
    accept("number", { case VyxalToken.Number(value) => AST.Number(value) })

  def string: Parser[AST] =
    accept("string", { case VyxalToken.Str(value) => AST.Str(value) })

  def command: Parser[AST] =
    accept("command", { case VyxalToken.Command(value) => AST.Command(value) })

  def open = accept("open", { case VyxalToken.StructureOpen(open) => open })

  def close =
    accept("close", { case VyxalToken.StructureClose(close) => close })

  // not(not(VyxalToken.StructureAllClose)) is used to match that token
  // without consuming it.
  // TODO see if there's a builtin way to do it
  def structure: Parser[AST] =
    open ~ repsep(rep(element), VyxalToken.Branch) <~ (close | not(
      not(VyxalToken.StructureAllClose)
    )) ^^ { case open ~ branches =>
      AST.Structure(open, branches)
    }
  
  def modifier = monadicModifier | dyadicModifier | triadicModifier | tetradicModifier

  def monadicModifier =
    accept(
      "Monadic modifier",
      { case VyxalToken.MonadicModifier(value) => value }
    )
      ~ element ^^ { case modifier ~ elem1 =>
        AST.MonadicModifier(modifier, elem1)
      }
  
  def dyadicModifier =
    accept(
      "Dyadic modifier",
      { case VyxalToken.DyadicModifier(value) => value }
    )
      ~ element ~ element ^^ { case modifier ~ elem1 ~ elem2 =>
        AST.DyadicModifier(modifier, elem1, elem2)
      }
  
  def triadicModifier =
    accept(
      "Triadic modifier",
      { case VyxalToken.TriadicModifier(value) => value }
    )
      ~ element ~ element ~ element ^^ { case modifier ~ elem1 ~ elem2 ~ elem3 =>
        AST.TriadicModifier(modifier, elem1, elem2, elem3)
      }

  def tetradicModifier =
    accept(
      "Tetradic modifier",
      { case VyxalToken.TetradicModifier(value) => value }
    )
      ~ element ~ element ~ element ~ element ^^ { case modifier ~ elem1 ~ elem2 ~ elem3 ~ elem4 =>
        AST.TetradicModifier(modifier, elem1, elem2, elem3, elem4)
      }

  def parse(code: String): Either[VyxalCompilationError, List[AST]] = {
    Lexer(code).flatMap { tokens =>
      val reader = new VyxalTokenReader(preprocess(tokens))
      parseAll(reader) match {
        case Success(result, _) => Right(result)
        case NoSuccess(msg, _)  => Left(VyxalCompilationError(msg))
      }
    }
  }

  private def preprocess(code: Seq[VyxalToken]): List[VyxalToken] = {
    val processed = ListBuffer[VyxalToken]()

    code.foreach {
      case VyxalToken.StructureClose(")") => {
        processed += VyxalToken.StructureClose("}")
        processed += VyxalToken.StructureClose("}")
      }
      case x => processed += x
    }
    processed.toList
  }
}
