package vyxal

import vyxal.impls.Element
import vyxal.Interpreter.executeFn

import scala.collection.mutable as mut
import scala.reflect.TypeTest
import spire.algebra.*
import spire.implicits.*
import spire.math.{Complex, Real}

// todo check if these names or this whole way of structuring need to be changed
type VAny = VAtom | VList
type VAtom = VVal | VFun
type VVal = VNum | String

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
    originalAST: Option[AST.Lambda] = None
):

  /** Make a copy of this function with a different arity. */
  def withArity(newArity: Int): VFun = this.copy(arity = newArity)

  /** Call this function on the given arguments, using custom context variables.
    */
  def execute(
      contextVarPrimary: VAny,
      contextVarSecondary: VAny,
      args: Seq[VAny],
      vars: mut.Map[String, VAny] = mut.Map()
  )(using ctx: Context): VAny =
    Interpreter.executeFn(
      this,
      Some(contextVarPrimary),
      Some(contextVarSecondary),
      args = args,
      variables = vars
    )

  def executeGetContext(
      contextVarPrimary: VAny,
      contextVarSecondary: VAny,
      args: Seq[VAny],
      vars: mut.Map[String, VAny] = mut.Map()
  )(using ctx: Context): Context =
    Interpreter.executeFnGetContext(
      this,
      Some(contextVarPrimary),
      Some(contextVarSecondary),
      args = args,
      variables = vars
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
      Some(contextVarPrimary),
      Some(contextVarSecondary),
      args = args,
      variables = vars
    )

    res match
      case f: VFun =>
        Interpreter.executeFn(
          f,
          Some(contextVarPrimary),
          Some(contextVarSecondary),
          args = args,
          variables = vars
        )
      case _ => res
  end executeResult

  def apply(args: VAny*)(using ctx: Context): VAny =
    Interpreter.executeFn(this)
end VFun

object VFun:
  def fromLambda(lam: AST.Lambda)(using origCtx: Context): VFun =
    val AST.Lambda(arity, params, body) = lam
    VFun(
      () => ctx ?=> body.foreach(Interpreter.execute(_)(using ctx)),
      arity,
      params,
      origCtx,
      Some(lam)
    )

  def fromElement(elem: Element)(using origCtx: Context): VFun =
    val Element(symbol, name, _, arity, _, _, impl) = elem
    VFun(impl, arity.getOrElse(1), List.empty, origCtx)
end VFun

extension (self: VAny)
  def ===(that: VAny): Boolean =
    (self, that) match
      case (a: VVal, b: VVal)   => MiscHelpers.compare(a, b) == 0
      case (a: VList, b: VList) => a == b
      case _                    => false
