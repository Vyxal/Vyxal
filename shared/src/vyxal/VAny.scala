package vyxal

import vyxal.Interpreter.executeFn

import scala.annotation.targetName
import scala.collection.mutable as mut

import spire.implicits.*

type VAny = VAtom | VList
type VIter = String | VList
type VAtom = VVal | VFun
type VVal = VNum | String

/** Everything but functions */
type VData = VVal | VList

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
    name: Option[String] = None
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
      vars: mut.Map[String, VAny] = mut.Map()
  )(using ctx: Context): VAny =
    val res = Interpreter.executeFn(
      this,
      contextVarPrimary,
      contextVarSecondary,
      args = args,
    )

    res match
      case f: VFun =>
        Interpreter.executeFn(
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
    val AST.Lambda(arity, params, body, _) = lam
    VFun(
      () => ctx ?=> body.foreach(Interpreter.execute(_)(using ctx)),
      arity,
      params,
      origCtx,
      Some(lam)
    )

  def fromElement(elem: Element)(using origCtx: Context): VFun =
    VFun(elem.impl, elem.arity.getOrElse(1), List.empty, origCtx)

extension (self: VAny)
  @targetName("vEquals")
  def ===(that: VAny): Boolean =
    (self, that) match
      case (a: VVal, b: VVal) => MiscHelpers.compare(a, b) == 0
      case (a: VList, b: VList) => a == b
      case _ => false

  @targetName("plus")
  def +~(that: VAny)(using Context): VAny = MiscHelpers.add(self, that)

  @targetName("times")
  def *~(that: VAny)(using Context): VAny = MiscHelpers.multiply(self, that)

extension (iterable: VIter)
  def iterLength: VNum =
    iterable match
      case s: String => s.length
      case l: VList => l.size
