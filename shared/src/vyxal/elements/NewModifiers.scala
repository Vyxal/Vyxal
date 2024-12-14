package vyxal.elements

import vyxal.AST

extension (ast: AST)
  def lam(arity: Int): AST =
    ast match
      case lam: AST.Lambda => lam.copy(lambdaArity = Some(arity))
      case _ => AST.Lambda(Some(arity), List(), List(ast), false)

extension (ast: AST) def lam: AST = ast.lam(ast.arity.getOrElse(-1))

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
    },
  )

  def addPart[P, F](name: String, arity: ModifierHelpers[P, F])(impl: P) =
    val numericArity = arity.arity

    name -> Modifier(numericArity, arity.toDirectFn(arity.fill(name)(impl)))
  def fullToImpl[F](arity: ModifierHelpers[?, F], impl: F): Modifier =
    val numericArity = arity.arity
    Modifier(numericArity, arity.toDirectFn(impl))
end NewModifiers
