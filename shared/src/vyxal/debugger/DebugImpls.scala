package vyxal.debugger

import vyxal.Context
import vyxal.VNum.given

object DebugImpls:
  type DebugImpl = () => Context ?=> Option[StepSeq]

  val impls: Map[String, DebugImpl] = ActualImpls.impls.toMap

  private object ActualImpls:
    val impls = collection.mutable.Map.empty[String, DebugImpl]

    def addImpl(symbol: String)(impl: DebugImpl): Unit =
      impls(symbol) = impl
