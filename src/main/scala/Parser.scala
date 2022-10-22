import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}

class VyxalTokenReader(tokens: Seq[VyxalToken]) extends Reader[VyxalToken] {
  override def first: VyxalToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[VyxalToken] = new VyxalTokenReader(tokens.tail)
}

object VyxalParser extends Parsers {
  type Elem = VyxalToken
  def parseAll: Parser[List[AST]] = phrase(rep1(element))
  def element: Parser[AST] = number | string | command | structure | partialStructure
  def number: Parser[AST] = accept("number", { case VyxalToken.Number(value) => AST.Number(value) })
  def string: Parser[AST] = accept("string", { case VyxalToken.Str(value) => AST.Str(value) })
  def command: Parser[AST] = accept("command", { case VyxalToken.Command(value) => AST.Command(value) })
  def structure: Parser[AST] = {
    val open = accept("open", { case VyxalToken.StructureOpen(open) => open })
    val close = accept("close", { case VyxalToken.StructureClose(close) => close })
    open ~ rep(element) ~ (VyxalToken.Branch ~ rep(element)).? ~ close.? ^^ {
    case open ~ elements ~ Some(VyxalToken.Branch ~ elements2) ~ close => AST.Structure(open, List(elements, elements2))
    case open ~ elements ~ None ~ close => AST.Structure(open, List(elements))
  }}
  def partialStructure: Parser[AST] = {
    val open = accept("open", { case VyxalToken.StructureOpen(open) => open })
    rep1(open ~ rep(element) ~ (VyxalToken.Branch ~ rep(element)).?) ~ VyxalToken.StructureAllClose ^^ {
      case lhs ~ VyxalToken.StructureAllClose => AST.Structure(lhs.head._1, lhs.map(_._2))
    }
  }
  

  def apply(tokens: Seq[VyxalToken]): List[AST] = {
    val reader = new VyxalTokenReader(preprocess(tokens))
    parseAll(reader) match {
    case Success(result, _) => result
    case NoSuccess(msg, _) => throw new Exception(msg)
  }
}}

def preprocess(code: Seq[VyxalToken]) : List[VyxalToken] = {
  var processed = List[VyxalToken]()
  code.foreach(x => {
    x match {
      case VyxalToken.StructureClose(")") => {
        processed = processed :+ VyxalToken.StructureClose("}")
        processed = processed :+ VyxalToken.StructureClose("}")
      }
      case _ => processed = processed :+ x
    }
  })
  processed}