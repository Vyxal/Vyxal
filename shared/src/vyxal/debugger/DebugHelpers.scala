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
      case dbg: Debugger => Debugger.fnCall(fn)
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

  /** A debuggable version of [[ListHelpers.dedupBy]] */
  def dedupBy(lst: VList, fn: VFun): Step =
    val seen = mutable.ArrayBuffer.empty[VAny]
    StepSeq(lst.flatMap { item =>
      List(
        Debugger.fnCall(
          fn,
          ctxVarPrimary = item,
          ctxVarSecondary = 0,
          args = List(item)
        ),
        Step.hidden { ctx ?=>
          val res = ctx.pop()
          if !seen.contains(res) then seen += res
        }
      )
    })
  end dedupBy

  // TODO this doesn't filter using all branches
  def filter(iterable: VList, predicate: VFun)(using
      Debugger,
      Context
  ): Step =
    predicate.originalAST match
      case Some(lam) =>
        val filtered = ListBuffer.empty[VAny]
        val filterSteps = iterable.zipWithIndex.map { (item, index) =>
          Debugger
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

  // TODO this doesn't map using all branches
  def map(ast: AST, lst: VList, fn: VFun)(using
      Debugger,
      Context
  ): Step =
    fn.originalAST match
      case Some(lam) =>
        Block(
          ast,
          StepSeq(
            lst.flatMap(elem =>
              List(Step.hidden { ctx ?=> ctx.push(elem) }, Debugger.fnCall(fn))
            )
          )
        )
      case None => Step.hidden { ListHelpers.map(fn, lst) }

end DebugHelpers
