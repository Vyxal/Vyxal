package vyxal

enum AST {
  case Number(value: VNum)
  case Str(value: String)
  case Lst(elems: List[AST])
  case Command(value: String)

  /** Multiple ASTs grouped into one list */
  case Group(elems: List[AST])
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
  case GetVar(name: String)
  case SetVar(name: String)

  /** Generate the Vyxal code this AST represents */
  def toVyxal: String = this match {
    case Number(n)      => n.toString
    case Str(value)     => s"\"$value\""
    case Lst(elems)     => elems.map(_.toVyxal).mkString("#[", "|", "#]")
    case Command(value) => value
    case Group(elems)   => elems.map(_.toVyxal).mkString
    case MonadicModifier(modi, elem1) => s"$modi${elem1.toVyxal}"
    case DyadicModifier(modi, elem1, elem2) =>
      s"$modi${elem1.toVyxal}${elem2.toVyxal}"
    case TriadicModifier(modi, elem1, elem2, elem3) =>
      s"$modi${elem1.toVyxal}${elem2.toVyxal}${elem3.toVyxal}"
    case TetradicModifier(modi, elem1, elem2, elem3, elem4) =>
      s"$modi${elem1.toVyxal}${elem2.toVyxal}${elem3.toVyxal}${elem4.toVyxal}"
    case SpecialModifier(modi, value) => s"$modi$value"
    case CompressedString(value) => s"\"$value“"
    case CompressedNumber(value) => s"\"$value„"
    case DictionaryString(value) => s"\"$value”"
    case If(thenBody, elseBody) => s"[$thenBody|$elseBody}"
    case For(loopVar, body) => s"(${loopVar.getOrElse("")}|${body.toVyxal}"
    case While(cond, body) => s"{${cond.fold("")(_.toVyxal)}|${body.toVyxal}}"
    case Lambda(body) => s"λ${body.toVyxal}}"
    case FnDef(name, arity, params, body) => ???
    case GetVar(name) => s"#<$name"
    case SetVar(name) => s"#>$name"
  }
}

object AST {

  /** Turn zero or more ASTs into one, wrapping in a [[AST.Group]] if necessary
    */
  def makeSingle(elems: AST*): AST =
    if (elems.size == 1) elems.head
    else AST.Group(elems.toList)
}
