package vyxal

import scala.language.strictEquality

import vyxal.lexer.Range

// todo maybe record whether each AST has a breakpoint
enum AST(val arity: Option[Int]) derives CanEqual:
  case Number(value: VNum, override val range: Range = Range.fake)
      extends AST(Some(0))
  case Str(value: String, override val range: Range = Range.fake)
      extends AST(Some(0))
  case Lst(elems: List[AST], override val range: Range = Range.fake)
      extends AST(Some(0))
  case Command(value: String, override val range: Range = Range.fake)
      extends AST(Elements.elements.get(value).flatMap(_.arity))

  /** Multiple ASTs grouped into one list */
  case Group(
      elems: List[AST],
      override val arity: Option[Int],
      override val range: Range = Range.fake
  ) extends AST(arity)
  case SpecialModifier(modi: String, override val range: Range = Range.fake)
      extends AST(None)
  case CompositeNilad(elems: List[AST], override val range: Range = Range.fake)
      extends AST(Some(0))

  case CompressedString(value: String, override val range: Range = Range.fake)
      extends AST(Some(0))
  case CompressedNumber(value: String, override val range: Range = Range.fake)
      extends AST(Some(0))
  case DictionaryString(value: String, override val range: Range = Range.fake)
      extends AST(Some(0))
  case Ternary(
      thenBody: AST,
      elseBody: Option[AST],
      override val range: Range = Range.fake
  ) extends AST(Some(1))
  case IfStatement(
      conds: List[AST],
      bodies: List[AST],
      elseBody: Option[AST],
      override val range: Range = Range.fake
  ) extends AST(Some(1))
  case For(
      loopVar: Option[String],
      body: AST,
      override val range: Range = Range.fake
  ) extends AST(None)
  case While(
      cond: Option[AST],
      body: AST,
      override val range: Range = Range.fake
  ) extends AST(None)
  case Lambda(
      lambdaArity: Int,
      params: List[String | Int],
      body: List[AST],
      override val range: Range = Range.fake
  ) extends AST(Some(lambdaArity))

  case DecisionStructure(
      predicate: AST,
      container: Option[AST],
      override val range: Range = Range.fake
  ) extends AST(Some(1))
  case GeneratorStructure(
      relation: AST,
      inital: Option[AST],
      lookbackArity: Int,
      override val range: Range = Range.fake
  ) extends AST(Some(1))

  /** A function definition, basically sugar a lambda assigned to a variable */
  case FnDef(name: String, lam: Lambda, override val range: Range = Range.fake)
      extends AST(Some(0))
  case ContextIndex(index: Int, override val range: Range = Range.fake)
      extends AST(Some(0))
  case GetVar(name: String, override val range: Range = Range.fake)
      extends AST(None)
  case SetVar(name: String, override val range: Range = Range.fake)
      extends AST(Some(1))
  case SetConstant(name: String, override val range: Range = Range.fake)
      extends AST(Some(1))
  case AuxAugmentVar(name: String, override val range: Range = Range.fake)
      extends AST(None)
  case AugmentVar(
      name: String,
      what: AST,
      override val range: Range = Range.fake
  ) extends AST(None)
  case UnpackVar(
      names: List[(String, Int)],
      override val range: Range = Range.fake
  ) extends AST(None)

  /** Junk newline AST that is removed in post-processing */
  case Newline extends AST(None)

  /** Junk modifier AST that is removed during parsing after first pass */
  case JunkModifier(name: String, modArity: Int) extends AST(Some(modArity))

  def range: Range = Range.fake

  /** Generate the Vyxal code this AST represents */
  def toVyxal: String = this match
    case Number(n, _) => n.toString
    case Str(value, _) => s"\"$value\""
    case Lst(elems, _) => elems.map(_.toVyxal).mkString("#[", "|", "#]")
    case Command(value, _) => value
    case Group(elems, _, _) =>
      // replace instances of Number, Number with Number, Space, Number
      elems
        .groupBy {
          case Number(_, _) => true
          case _ => false
        }
        .map { (k, v) =>
          if k then v.map(_.toVyxal).mkString(" ")
          else v.map(_.toVyxal).mkString
        }
        .mkString
    // case SpecialModifier(modi, value) => s"$modi"
    // ^ Might not need this because it'll be converted into different ASTs
    case CompositeNilad(elems, _) => elems.map(_.toVyxal).mkString
    case CompressedString(value, _) => s"\"$value“"
    case CompressedNumber(value, _) => s"\"$value„"
    case DictionaryString(value, _) => s"\"$value”"
    case Ternary(thenBody, elseBody, _) => s"[$thenBody|$elseBody}"
    case For(loopVar, body, _) => s"(${loopVar.getOrElse("")}|${body.toVyxal}"
    case While(cond, body, _) =>
      s"{${cond.fold("")(_.toVyxal)}|${body.toVyxal}}"
    case Lambda(_, params, body, _) =>
      body.map(_.toVyxal).mkString("λ", "|", "}")
    case FnDef(name, lam, _) => ???
    case GetVar(name, _) => s"#<$name"
    case SetVar(name, _) => s"#>$name"
    case ast => ast.toString
end AST

object AST:

  /** Turn zero or more ASTs into one, wrapping in a [[AST.Group]] if necessary
    */
  def makeSingle(elems: AST*): AST =
    if elems.size == 1 then elems.head
    else AST.Group(elems.toList, None)