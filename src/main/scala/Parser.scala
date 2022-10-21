import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}

class VyxalTokenReader(tokens: Seq[VyxalToken]) extends Reader[VyxalToken] {
  override def first: VyxalToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[VyxalToken] = new VyxalTokenReader(tokens.tail)
}

def preprocess(code: List[VyxalToken]) : List[VyxalToken] = {
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