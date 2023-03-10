package vyxal

import vyxal.impls.Elements

enum AST(val arity: Option[Int]):
  case Number(value: VNum) extends AST(Some(0))
  case Str(value: String) extends AST(Some(0))
  case Lst(elems: List[AST]) extends AST(Some(0))
  case Command(value: String)
      extends AST(Elements.elements.get(value).flatMap(_.arity))

  /** Multiple ASTs grouped into one list */
  case Group(elems: List[AST], override val arity: Option[Int])
      extends AST(arity)
  case SpecialModifier(modi: String) extends AST(None)
  case CompositeNilad(elems: List[AST]) extends AST(Some(0))

  case CompressedString(value: String) extends AST(Some(0))
  case CompressedNumber(value: String) extends AST(Some(0))
  case DictionaryString(value: String) extends AST(Some(0))
  case Ternary(thenBody: AST, elseBody: Option[AST]) extends AST(Some(1))
  case IfStatement(conds: List[AST], bodies: List[AST], elseBody: Option[AST])
      extends AST(Some(1))
  case For(loopVar: Option[String], body: AST) extends AST(None)
  case While(cond: Option[AST], body: AST) extends AST(None)
  case Lambda(lambdaArity: Int, params: List[String | Int], body: List[AST])
      extends AST(Some(lambdaArity))

  case DecisionStructure(predicate: AST, container: Option[AST])
      extends AST(Some(1))
  case GeneratorStructure(
      relation: AST,
      inital: Option[AST],
      lookbackArity: Int
  ) extends AST(Some(1))

  /** A function definition, basically sugar a lambda assigned to a variable */
  case FnDef(name: String, lam: Lambda) extends AST(Some(0))
  case ContextIndex(index: Int) extends AST(Some(0))
  case GetVar(name: String) extends AST(None)
  case SetVar(name: String) extends AST(Some(1))
  case SetConstant(name: String) extends AST(Some(1))
  case AuxAugmentVar(name: String) extends AST(None)
  case AugmentVar(name: String, what: AST) extends AST(None)
  case UnpackVar(names: List[(String, Int)]) extends AST(None)

  /** Junk newline AST that is removed in post-processing */
  case Newline extends AST(None)

  /** Junk modifier AST that is removed during parsing after first pass */
  case JunkModifier(name: String, modArity: Int) extends AST(Some(modArity))

  /** Generate the Vyxal code this AST represents */
  def toVyxal: String = this match
    case Number(n)       => n.toString
    case Str(value)      => s"\"$value\""
    case Lst(elems)      => elems.map(_.toVyxal).mkString("#[", "|", "#]")
    case Command(value)  => value
    case Group(elems, _) =>
      // replace instances of Number, Number with Number, Space, Number
      elems
        .groupBy(_ match
          case Number(_) => true
          case _         => false
        )
        .map((k, v) =>
          if k then v.map(_.toVyxal).mkString(" ")
          else v.map(_.toVyxal).mkString
        )
        .mkString
    // case SpecialModifier(modi, value) => s"$modi"
    // ^ Might not need this because it'll be converted into different ASTs
    case CompositeNilad(elems)       => elems.map(_.toVyxal).mkString
    case CompressedString(value)     => s"\"$value“"
    case CompressedNumber(value)     => s"\"$value„"
    case DictionaryString(value)     => s"\"$value”"
    case Ternary(thenBody, elseBody) => s"[$thenBody|$elseBody}"
    case For(loopVar, body) => s"(${loopVar.getOrElse("")}|${body.toVyxal}"
    case While(cond, body)  => s"{${cond.fold("")(_.toVyxal)}|${body.toVyxal}}"
    case Lambda(arity, params, body) =>
      body.map(_.toVyxal).mkString("λ", "|", "}")
    case FnDef(name, lam) => ???
    case GetVar(name)     => s"#<$name"
    case SetVar(name)     => s"#>$name"
    case ast              => ast.toString
end AST

object AST:

  /** Turn zero or more ASTs into one, wrapping in a [[AST.Group]] if necessary
    */
  def makeSingle(elems: AST*): AST =
    if elems.size == 1 then elems.head
    else AST.Group(elems.toList, None)
