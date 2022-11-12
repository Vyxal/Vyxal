package vyxal

enum AST {
  case Number(value: VNum)
  case Str(value: String)
  case Lst(elems: List[AST])
  case Command(value: String)

  /** Multiple ASTs grouped into one list */
  case Group(elems: List[AST])
  case SpecialModifier(modi: String, value: String)

  /** The result of applying a modifier to some arguments. `res` can be applied
    * directly to the stack.
    */
  case Modified(modi: String, res: DirectFn)
  case CompressedString(value: String)
  case CompressedNumber(value: String)
  case DictionaryString(value: String)
  case If(thenBody: AST, elseBody: Option[AST])
  case For(loopVar: Option[String], body: AST)
  case While(cond: Option[AST], body: AST)
  case Lambda(arity: Int, params: List[String], body: AST)

  /** A function definition, basically sugar a lambda assigned to a variable */
  case FnDef(name: String, lam: Lambda)
  case GetVar(name: String)
  case SetVar(name: String)
  case ExecuteFn

  /** Generate the Vyxal code this AST represents */
  def toVyxal: String = this match {
    case Number(n)      => n.toString
    case Str(value)     => s"\"$value\""
    case Lst(elems)     => elems.map(_.toVyxal).mkString("#[", "|", "#]")
    case Command(value) => value
    case Group(elems)   => elems.map(_.toVyxal).mkString
    case SpecialModifier(modi, value) => s"$modi$value"
    case CompressedString(value)      => s"\"$value“"
    case CompressedNumber(value)      => s"\"$value„"
    case DictionaryString(value)      => s"\"$value”"
    case If(thenBody, elseBody)       => s"[$thenBody|$elseBody}"
    case For(loopVar, body) => s"(${loopVar.getOrElse("")}|${body.toVyxal}"
    case While(cond, body)  => s"{${cond.fold("")(_.toVyxal)}|${body.toVyxal}}"
    case Lambda(arity, params, body) => s"λ${body.toVyxal}}"
    case FnDef(name, lam)            => ???
    case GetVar(name)                => s"#<$name"
    case SetVar(name)                => s"#>$name"
  }
}

object AST {

  /** Turn zero or more ASTs into one, wrapping in a [[AST.Group]] if necessary
    */
  def makeSingle(elems: AST*): AST =
    if (elems.size == 1) elems.head
    else AST.Group(elems.toList)
}
