package vyxal.elements

import vyxal.conversions.{*, given}
import vyxal.AST
import vyxal.UnimplementedModifierOverloadException
import vyxal.VNum

/** Calculate the effective arity of a group. The effective arity is the number
  * of total arguments popped from the outer stack.
  *
  * @param group
  * @return
  */
def calculateArityOfAST(ast: AST): Int =
  var arity = 0
  ast match
    case AST.Lambda(arity, _, _, _, _) => arity.getOrElse(1)
    case AST.Group(elems, astArity, _) =>
      if astArity.isDefined then astArity.get
      else
        elems.foreach { elem =>
          val elemArity = calculateArityOfAST(elem)
          if elemArity > arity then arity = elemArity
          else if elemArity == arity then arity += arity - 1
        }
      arity
    case otherAST => otherAST.arity.getOrElse(1)

extension (ast: AST) def lam(arity: Int): AST = lam(arity, false)

extension (ast: AST)
  def lam(arity: Int, originallyFunction: Boolean): AST =
    ast match
      case lam: AST.Lambda => lam.copy(lambdaArity = Some(arity))
      case _ => AST.Lambda(Some(arity), List(), List(ast), originallyFunction)

extension (ast: AST)
  def lamAsFunction: AST =
    ast.lam(ast.arity.getOrElse(calculateArityOfAST(ast)), true)

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
      else throw UnimplementedModifierOverloadException(symbol, arg)

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
      else throw UnimplementedModifierOverloadException(symbol, arg)

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
      else throw UnimplementedModifierOverloadException(symbol, arg)

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
      else throw UnimplementedModifierOverloadException(symbol, arg)

object Modifiers:
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
    "⑴" -> fullToImpl(Monadic, (ast) => Seq(AST.makeSingle(ast).lamAsFunction)),
    "⑵" ->
      fullToImpl(
        Dyadic,
        (first, second) => Seq(AST.makeSingle(first, second).lamAsFunction),
      ),
    "⑶" ->
      fullToImpl(
        Triadic,
        (first, second, third) =>
          Seq(AST.makeSingle(first, second, third).lamAsFunction),
      ),
    "⑷" ->
      fullToImpl(
        Tetradic,
        (first, second, third, fourth) =>
          Seq(
            AST.makeSingle(first, second, third, fourth).lamAsFunction
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
    "¨" -> fullToImpl(Monadic, ast => Seq(ast.lam, AST.Command("#|each"))),
    addPart("Ẅ", Monadic) {
      case AST(dyad, 2) => Seq(dyad.lam, AST.Command("#|zip-with"))
    },
    "¿" -> fullToImpl(Monadic, (ast) => Seq(ast.lam, AST.Command("#|if"))),
    "#⍰" ->
      fullToImpl(
        Dyadic,
        (truthy, falsey) =>
          Seq(truthy.lam, falsey.lam, AST.Command("#|if-else")),
      ),
    "⎇" -> fullToImpl(Monadic, (ast) => Seq(ast.lam(-1), AST.Command("#|dip"))),
    addPart("~", Monadic) {
      case AST(predicate, 1) => Seq(predicate.lam, AST.Command("F"))
      case command => Seq(command.lam, AST.Command("#~"))
    },
    addPart("/", Monadic) {
      case AST(monad, 1) => Seq(monad.lam, AST.Command("#|invariant"))
      case ast => Seq(ast.lam, AST.Command("R"))
    },
    addPart("\\", Monadic) {
      case AST(monad, 1) => Seq(monad.lam, AST.Command("I"))
      case ast => Seq(ast.lam, AST.Command("Y"))
    },
    "⩔" ->
      fullToImpl(
        Monadic,
        (ast) => Seq(ast.lam, AST.Command("#|at-simple-levels")),
      ),
  )

  def addPart[P, F](name: String, arity: ModifierHelpers[P, F])(impl: P) =
    val numericArity = arity.arity

    name -> Modifier(numericArity, arity.toDirectFn(arity.fill(name)(impl)))
  def fullToImpl[F](arity: ModifierHelpers[?, F], impl: F): Modifier =
    val numericArity = arity.arity
    Modifier(numericArity, arity.toDirectFn(impl))
end Modifiers
