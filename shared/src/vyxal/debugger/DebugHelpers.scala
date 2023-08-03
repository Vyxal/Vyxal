package vyxal.debugger

import vyxal.*

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.{summonFrom, summonInline}

private[debugger] object DebugHelpers:
  /** These helpers give a Step if meant for the debugger and a VAny when
    * interpreting normally
    */
  type Res = Step | VAny

  extension (self: VAny)
    def map(fn: VAny => Context ?=> VAny)(using Context): VAny = fn(self)
    def flatMap(fn: VAny => Context ?=> VAny)(using Context): VAny = fn(self)
    def foreach(fn: VAny => Context ?=> Unit)(using Context): Unit = fn(self)

  transparent inline def debugOrInterpret(fn: VFun)(using
      Context
  ): Res =
    summonFrom {
      case dbg: Debugger => dbg.fnCall(fn)
      case _ => Interpreter.executeFn(fn)
    }

  /** Whether this is to be a debuggable implementation or a normal
    * implementation
    */
  inline def isDebug: Boolean =
    summonFrom {
      case _: Debugger => true
      case _ => false
    }

  inline def dbg: Debugger = summonInline[Debugger]

  def debugMap(ast: AST, lst: VList, fn: VFun)(using
      Debugger,
      Context
  ): Step =
    Block(
      ast,
      Step.seq(
        lst.flatMap(elem =>
          List(Hidden { () => ctx ?=> ctx.push(elem) }, dbg.fnCall(fn))
        )
      )
    )

  def debugFilter(iterable: VList, predicate: VFun)(using
      dbg: Debugger,
      ctx: Context
  ): Step =
    predicate.originalAST match
      case Some(lam) =>
        val branches = lam.body
        ???
      case None =>
        val filtered = ListBuffer.empty[VAny]
        val filterSteps = iterable.zipWithIndex.map { (item, index) =>
          dbg
            .fnCall(
              predicate,
              ctxVarPrimary = item,
              ctxVarSecondary = index,
              args = List(item)
            )
            .foreach { res =>
              if MiscHelpers.boolify(res) then filtered += res
            }
        }
        Step
          .seq(
            filterSteps :+
              Hidden { () => ctx ?=> ctx.push(VList.from(filtered.toList)) }
          )
          .get
end DebugHelpers
