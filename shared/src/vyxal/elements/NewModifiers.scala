package vyxal.elements

import vyxal.conversions.{*, given}
import vyxal.AST
import vyxal.VNum

extension (ast: AST) def lam(arity: Int): AST = lam(arity, false)

extension (ast: AST)
  def lam(arity: Int, originallyFunction: Boolean): AST =
    ast match
      case lam: AST.Lambda => lam.copy(lambdaArity = Some(arity))
      case _ => AST.Lambda(Some(arity), List(), List(ast), originallyFunction)

extension (ast: AST) def lam: AST = ast.lam(ast.arity.getOrElse(1))

extension (ast: AST)
  def lamLeast(arity: Int): AST =
    ast match
      case lam: AST.Lambda => lam.copy(lambdaArity = Some(arity))
      case _ =>
        AST.Lambda(Some(ast.arity.getOrElse(arity)), List(), List(ast), true)

extension (ast: AST)
  def isExplicitMonad: Boolean =
    ast.arity.getOrElse(-1) == 1 &&
      (ast match
        case f: AST.Lambda => f.params.isEmpty
        case _ => true
      )

object DyadOrMore:
  def unapply(ast: AST): Option[(AST, Int)] =
    ast match
      case AST(ast, arity) if arity >= 2 => Some((ast, arity))
      case _ => None

type MonadicModifier = AST => Seq[AST]
type DyadicModifier = (AST, AST) => Seq[AST]
type TriadicModifier = (AST, AST, AST) => Seq[AST]
type TetradicModifier = (AST, AST, AST, AST) => Seq[AST]
type PartialMonadicModifier = PartialFunction[AST, Seq[AST]]
type PartialDyadicModifier = PartialFunction[(AST, AST), Seq[AST]]
type PartialTriadicModifier = PartialFunction[(AST, AST, AST), Seq[AST]]
type PartialTetradicModifier = PartialFunction[(AST, AST, AST, AST), Seq[AST]]
type DirectFn = (Seq[AST]) => AST

sealed abstract class ModifierHelpers[P, F](val arity: Int):
  def toDirectFn(impl: F): DirectFn
  def fill(symbol: String)(impl: P): F

object Monadic
    extends ModifierHelpers[PartialMonadicModifier, MonadicModifier](1):
  override def toDirectFn(impl: MonadicModifier) =
    (arg) =>
      if arg.length != 1 then
        throw Exception("Monadic modifier somehow given more than 1 AST")
      else AST.makeSingle(impl(arg(0))*)

  override def fill(symbol: String)(fn: PartialMonadicModifier) =
    arg =>
      if fn.isDefinedAt(arg) then fn(arg)
      else throw Exception(s"Monadic modifier $symbol not defined for $arg")

object Dyadic extends ModifierHelpers[PartialDyadicModifier, DyadicModifier](2):
  override def toDirectFn(impl: DyadicModifier) =
    (arg) =>
      if arg.length != 2 then
        throw Exception("Dyadic modifier somehow given more or less than 2 AST")
      else AST.makeSingle(impl(arg(0), arg(1))*)

  override def fill(symbol: String)(fn: PartialDyadicModifier) =
    (ast1, ast2) =>
      val args = (ast1, ast2)
      if fn.isDefinedAt(args) then fn(args)
      else
        throw Exception(
          s"Dyadic modifier $symbol not defined for ${args.toList}"
        )

object Triadic
    extends ModifierHelpers[PartialTriadicModifier, TriadicModifier](3):
  override def toDirectFn(impl: TriadicModifier) =
    (arg) =>
      if arg.length != 3 then
        throw Exception(
          "Triadic modifier somehow given more or less than 3 AST"
        )
      else AST.makeSingle(impl(arg(0), arg(1), arg(2))*)

  override def fill(symbol: String)(fn: PartialTriadicModifier) =
    (ast1, ast2, ast3) =>
      val args = (ast1, ast2, ast3)
      if fn.isDefinedAt(args) then fn(args)
      else
        throw Exception(
          s"Triadic modifier $symbol not defined for ${args.toList}"
        )
end Triadic

object Tetradic
    extends ModifierHelpers[PartialTetradicModifier, TetradicModifier](4):
  override def toDirectFn(impl: TetradicModifier) =
    (arg) =>
      if arg.length != 4 then
        throw Exception(
          "Tetradic modifier somehow given more or less than 4 AST"
        )
      else AST.makeSingle(impl(arg(0), arg(1), arg(2), arg(3))*)

  override def fill(symbol: String)(fn: PartialTetradicModifier) =
    (ast1, ast2, ast3, ast4) =>
      val args = (ast1, ast2, ast3, ast4)
      if fn.isDefinedAt(args) then fn(args)
      else
        throw Exception(
          s"Tetradic modifier $symbol not defined for ${args.toList}"
        )
end Tetradic

object NewModifiers:
  case class Modifier(arity: Int, from: DirectFn)

  val modifiers: Map[String, Modifier] = Map(
    "∺" ->
      fullToImpl(
        Dyadic,
        (first, second) =>
          Seq(
            first.lamLeast(2),
            second.lamLeast(2),
            AST.Command("#|correspond"),
          ),
      ),
    addPart("⁜", Monadic) {
      case AST(ast, 1) => Seq(ast.lam, AST.Command("※"))
      case otherAST =>
        val arity = otherAST.arity.getOrElse(-1)
        if arity == -1 then
          throw Exception(
            "Monadic modifier ⁜ not defined for AST with unknown arity"
          )
        else
          Seq(
            otherAST.lam,
            AST.Number(VNum(arity)),
            AST.Command("o"),
          )
    },
    "∥" ->
      fullToImpl(
        Dyadic,
        (first, second) =>
          Seq(
            first.lam(-1),
            second.lam(-1),
            AST.Command("#|parallel-apply"),
          ),
      ),
    "∦" ->
      fullToImpl(
        Dyadic,
        (first, second) =>
          Seq(
            first.lam(-1),
            second.lam(-1),
            AST.Command("#|parallel-apply"),
            AST.Command(";"),
          ),
      ),
    "⑴" -> fullToImpl(Monadic, (ast) => Seq(AST.makeSingle(ast).lam(1, true))),
    "⑵" ->
      fullToImpl(
        Dyadic,
        (first, second) => Seq(AST.makeSingle(first, second).lam(1, true)),
      ),
    "⑶" ->
      fullToImpl(
        Triadic,
        (first, second, third) =>
          Seq(AST.makeSingle(first, second, third).lam(1, true)),
      ),
    "⑷" ->
      fullToImpl(
        Tetradic,
        (first, second, third, fourth) =>
          Seq(
            AST.makeSingle(first, second, third, fourth).lam(1, true)
          ),
      ),
    "⎂" ->
      fullToImpl(
        Monadic,
        (first) => Seq(first.lam, AST.Command("#|both")),
      ),
    addPart("⟒", Dyadic) {
      case (DyadOrMore(ast1, _), DyadOrMore(ast2, _)) =>
        Seq(ast1.lam, ast2.lam, AST.Command("#|fork"))
    },
    addPart("ᛞ", Dyadic) {
      case (AST(left, 2), AST(right, 2)) =>
        Seq(left.lam, right.lam, AST.Command("#|inner-product"))
    },
    addPart("▦", Monadic) {
      case AST(ast, 2) => Seq(ast.lam, AST.Command("#|outer-product"))
    },
  )

  def addPart[P, F](name: String, arity: ModifierHelpers[P, F])(impl: P) =
    val numericArity = arity.arity

    name -> Modifier(numericArity, arity.toDirectFn(arity.fill(name)(impl)))
  def fullToImpl[F](arity: ModifierHelpers[?, F], impl: F): Modifier =
    val numericArity = arity.arity
    Modifier(numericArity, arity.toDirectFn(impl))
end NewModifiers
