package vyxal.debugger

import vyxal.*

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.{summonFrom, summonInline}

object DebugHelpers:
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

  // TODO this doesn't map using all branches
  def debugMap(ast: AST, lst: VList, fn: VFun)(using
      Debugger,
      Context
  ): Step =
    fn.originalAST match
      case Some(lam) =>
        Block(
          ast,
          StepSeq(
            lst.flatMap(elem =>
              List(Step.hidden { ctx ?=> ctx.push(elem) }, dbg.fnCall(fn))
            )
          )
        )
      case None => Step.hidden { ListHelpers.map(fn, lst) }

  // TODO this doesn't filter using all branches
  def debugFilter(iterable: VList, predicate: VFun)(using
      dbg: Debugger,
      ctx: Context
  ): Step =
    predicate.originalAST match
      case Some(lam) =>
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
        StepSeq(
          filterSteps :+
            Step.hidden { ctx ?=> ctx.push(VList.from(filtered.toList)) }
        )
      case None => Step.hidden { ListHelpers.filter(iterable, predicate) }
end DebugHelpers
