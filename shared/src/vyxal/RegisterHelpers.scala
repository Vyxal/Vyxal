package vyxal

import vyxal.conversions.{*, given}

import scala.annotation.unchecked.uncheckedVariance
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable as mut


object RegisterHelpers:
	var register  = Context().globals.register
	if register.length == 0 then register.append(0)

	def applyFunction(fn: VFun)(using Context): Unit =
		register.update(register.length - 1, fn(register.last))

	def registerPop()(using Context): VAny

end RegisterHelpers