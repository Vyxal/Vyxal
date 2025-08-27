package vyxal

import vyxal.conversions.{*, given}

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable as mut


object RegisterHelpers:

	val register = Context().globals.register

	def clear(): Unit = register.clearAndShrink(1)

	def isEmpty: Boolean = register.length == 0

	def length: VNum = VNum(register.length)

	def reverseRegister(): Unit = 
		val rev = register.reverse
		for i <- 0 to register.length do
			register.update(i, rev(i))
		
	/** Apply a monadic function to the last item in the register */	
	def applyFunction(fn: VFun)(using Context): Unit =
		if !register.last.isInstanceOf[VFun] then register.update(register.length - 1, fn(register.last))
	
	def push(a: VPhysical)(using Context): Unit =
		register.append(a)

	def pop(c: VNum = 1, allVals: Boolean = false)(using ctx: Context): VAny =
		val n = if allVals then register.length else c.toInt
		if register.isEmpty then VNum(0)
		else
			val top = register.takeRight(n)
			register.dropRightInPlace(n)
			register.trimToSize()
			if top.length == 1 then top.head
			else top.toList.filterNot(_.isInstanceOf[VFun])
	
	def peek(c: VNum = 1, allVals: Boolean = false)(using Context): VAny =
		val n = if allVals then register.length else c.toInt
		val top = register.takeRight(n)
		if top.length == 1 then top.head
		else top.toList.filterNot(_.isInstanceOf[VFun])

	def index(i: Int)(using Context): VAny =
		register.toIndexedSeq(i)

end RegisterHelpers
