package vyxal.debugger

import vyxal.*

import scala.compiletime.{summonFrom, summonInline}

private[debugger] object DebugHelpers:
  /** These helpers give a Step if meant for the debugger and a VAny when
    * interpreting normally
    */
  type Res = Step | VAny

  extension (self: VAny)
    def map(fn: VAny => Context ?=> VAny)(using Context): VAny = fn(self)
    def flatMap(fn: VAny => Context ?=> VAny)(using Context): VAny = fn(self)

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

  transparent inline def debugMap(ast: AST, lst: VList, fn: VFun)(using
      Context
  ): Res =
    inline if isDebug then ListHelpers.map(fn, lst)
    else
      Block(
        ast,
        Step.seq(
          lst.flatMap(elem =>
            List(Hidden { () => ctx ?=> ctx.push(elem) }, dbg.fnCall(fn))
          )
        )
      )
end DebugHelpers
