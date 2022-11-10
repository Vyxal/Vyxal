package vyxal

enum AST {
  /** Do nothing */
  case Empty
  case Number(value: VNum)
  case Str(value: String)
  case Lst(elems: List[AST])
  case Command(value: String)

  /** Execute one AST and then another. Meant to be used like a linked list */
  case Chain(head: AST, tail: AST)
  case MonadicModifier(modi: String, elem1: AST)
  case DyadicModifier(modi: String, elem1: AST, elem2: AST)
  case TriadicModifier(modi: String, elem1: AST, elem2: AST, elem3: AST)
  case TetradicModifier(
      modi: String,
      elem1: AST,
      elem2: AST,
      elem3: AST,
      elem4: AST
  )
  case SpecialModifier(modi: String, value: String)
  case CompressedString(value: String)
  case CompressedNumber(value: String)
  case DictionaryString(value: String)
  case If(thenBody: AST, elseBody: Option[AST])
  case For(loopVar: Option[String], body: AST)
  case While(cond: Option[AST], body: AST)
  case Lambda(body: AST)
  /** A function definition */
  case FnDef(
      name: String,
      arity: Int,
      params: Option[List[String]],
      body: AST
  )
}

object AST {

  /** Turn zero or more ASTs into one, wrapping in a [[AST.Group]] if necessary
    */
  def makeSingle(elems: AST*): AST =
    elems match {
      case Seq(elem) => elem
      case Seq(head, tail*) => AST.Chain(head, makeSingle(tail*))
      case _ => AST.Empty
    }
}
