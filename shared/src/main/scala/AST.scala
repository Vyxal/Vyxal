package vyxal

enum AST {
  case Number(value: String)
  case Str(value: String)
  case Lst(elems: List[AST])
  case Command(value: String)

  /** Treat multiple trees as a single one (for lambdas) */
  case Group(asts: List[AST])
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
}

object AST {

  /** Turn zero or more ASTs into one, wrapping in a [[AST.Group]] if necessary
    */
  def makeSingle(elems: AST*): AST =
    if (elems.size == 1) elems.head
    else AST.Group(elems.toList)
}
