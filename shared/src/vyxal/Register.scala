package vyxal

import vyxal.conversions.{*, given}

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable as mut


object Register:

	var register: mut.ArrayBuffer[VAny] = Context().globals.register

	def isEmpty: Boolean = register.length == 0

	def length: VNum = VNum(register.length)
		
	/** Apply a monadic function to the last item in the register */	
	def applyFunction(fn: VFun)(using Context): Unit =
		register.update(register.length - 1, fn(register.last))
	
	def push(a: VPhysical)(using Context): Unit =
		register.append(a)

	def pop(c: VNum = 1, allVals: Boolean = false)(using Context): VAny =
		val n = if allVals then register.length else c.toInt
		val top = register.takeRight(n)
		register.dropRightInPlace(n)
		if register.isEmpty then register.append(0)
		if top.length == 1 then top.head
		else top.toList
	
	def peek(c: VNum = 1, allVals: Boolean = false)(using Context): VAny =
		val n = if allVals then register.length else c.toInt
		val top = register.takeRight(n)
		if top.length == 1 then top.head
		else top.toList

	def index(i: Int): VAny =
		register.reverse.toIndexedSeq(i)
	

end Register