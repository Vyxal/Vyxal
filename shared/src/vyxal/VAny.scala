package vyxal

import vyxal.Interpreter.executeFn

import scala.annotation.targetName
import scala.collection.mutable as mut

import spire.implicits.*

type VAny = VVal | VFun | VList | VConstructor | VObject
type VVal = VNum | String
type VPhysical = VNum | String | VList
type VIter = VList | String

/** A function object (not a function definition)
  *
  * @param impl
  *   The implementation of this function
  * @param arity
  *   The arity of this function (may have been changed)
  * @param params
  *   Parameter names
  * @param ctx
  *   The context in which this function was defined
  */
case class VFun(
    impl: DirectFn,
    arity: Int,
    params: List[String | Int],
    var ctx: Context,
    originalAST: Option[AST.Lambda] = None,
    name: Option[String] = None,
):

  /** Make a copy of this function with a different arity. */
  def withArity(newArity: Int): VFun = this.copy(arity = newArity)

  /** Call this function on the given arguments, using custom context variables.
    */
  def execute(
      contextVarPrimary: VAny,
      contextVarSecondary: VAny,
      args: Seq[VAny],
  )(using ctx: Context): VAny =
    Interpreter.executeFn(
      this,
      contextVarPrimary,
      contextVarSecondary,
      args = args,
    )

  def executeResult(
      contextVarPrimary: VAny,
      contextVarSecondary: VAny,
      args: Seq[VAny],
      overwriteCtx: Boolean = false,
      vars: mut.Map[String, VAny] = mut.Map(),
  )(using ctx: Context): VAny =
    val res = Interpreter.executeFn(
      this,
      contextVarPrimary,
      contextVarSecondary,
      args = args,
    )

    res match
      case f: VFun => Interpreter.executeFn(
          f,
          contextVarPrimary,
          contextVarSecondary,
          args = args,
        )
      case _ => res
  end executeResult

  def apply(args: VAny*)(using ctx: Context): VAny =
    Interpreter.executeFn(this, args = args)
end VFun

object VFun:
  def fromLambda(lam: AST.Lambda)(using origCtx: Context): VFun =
    val AST.Lambda(arity, params, body, originallyFunction, _) = lam
    VFun(
      () => ctx ?=> body.foreach(Interpreter.execute(_)(using ctx)),
      arity.getOrElse(origCtx.settings.defaultArity),
      params,
      origCtx,
      Some(lam),
    )

  def fromElement(elem: Element)(using origCtx: Context): VFun =
    VFun(
      elem.impl,
      elem.arity.getOrElse(origCtx.settings.defaultArity),
      List.empty,
      origCtx,
    )
end VFun

extension (self: VAny)
  @targetName("vEquals")
  def ===(that: VAny)(using Context): Boolean =
    (self, that) match
      case (a: VObject, b: VObject) => a.className == b.className &&
        a.fields == b.fields
      case (a: VList, b: VList) => a == b
      case (_: VFun, _) =>
        scribe.warn(s"Tried comparing function $self to $that")
        false
      case (_, _: VFun) =>
        scribe.warn(s"Tried comparing $self to function $that")
        false
      case (a: VVal, b: VVal) => MiscHelpers.compare(a, b) == 0
      case _ => false

  @targetName("vNotEquals")
  def !==(that: VAny)(using Context): Boolean = !(self === that)

  @targetName("plus")
  def +~(that: VAny)(using Context): VAny = MiscHelpers.add(self, that)

  @targetName("times")
  def *~(that: VAny)(using Context): VAny = MiscHelpers.multiply(self, that)

  def toBool =
    self match
      case n: VNum => n != VNum(0)
      case s: String => s.nonEmpty
      case f: VFun => true
      case l: VList => l.nonEmpty
      case c: VConstructor => true
      case o: VObject => true
end extension

case class VConstructor(
    name: String
):
  override def toString = s"$name constructor"

case class VObject(
    className: String,
    fields: Map[String, (Visibility, VAny)],
):
  override def toString =
    val fs = fields.map { case (name, (vis, value)) => s"$value ${vis match
      case Visibility.Public => "#!"
      case Visibility.Restricted => "#$"
      case Visibility.Private => "#="
    }$name" }
    s"$className { ${fs.mkString(" ")} }"
given (using Context): Ordering[VAny] with
  override def compare(x: VAny, y: VAny): Int = MiscHelpers.compare(x, y)
