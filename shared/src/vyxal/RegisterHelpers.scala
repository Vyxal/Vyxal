package vyxal

import vyxal.conversions.{*, given}

import scala.collection.mutable.ArrayBuffer


object RegisterHelpers:

	val register = Context().globals.register

	def clear(): Unit = register.clearAndShrink(1)

	def isEmpty: Boolean = register.length == 0

	def length: VNum = VNum(register.length)

	def reverseRegister(): Unit = 
		val rev = register.reverse
		for i <- 0 to register.length - 1 do
			register.update(i, rev(i))
		
	/** Apply a monadic function to an item in the register */	
	def applyFunction(fn: VFun, idx: VNum)(using ctx: Context): Unit =
		if register.last.isInstanceOf[VPhysical]
		then
			ctx.push(register(idx.toInt))
			val c = Interpreter.executeFn(fn)
			register.update(idx.toInt, c)

	def map(fn: VFun)(using ctx: Context): Unit =
		for x <- 0 to register.length - 1 do
			if register(x).isInstanceOf[VPhysical] then applyFunction(fn, VNum(x)) 
			else register(x)



	def push(a: VAny)(using Context): Unit =
		register.append(a)

	def pop(j: VNum = 1, allVals: Boolean = false, peek: Boolean = false)(using ctx: Context): VAny =
		val n = if allVals then register.length else j.toInt
		if register.isEmpty then VNum(0)
		else
			val top = register.takeRight(n)
			if !peek then 
				register.dropRightInPlace(n)
				register.trimToSize()
			if top.length == 1 && !allVals then 
				val c = top.head
				c match
					case c: VFun => Interpreter.executeFn(c)
					case _ => c
			else top.toList.filterNot(_.isInstanceOf[VFun])
	

	def index(i: VNum)(using Context): VAny =
		val idx = (i % VNum(register.length)).toInt
		register.toIndexedSeq(idx) // scala modulo doesn't always return positive vales 

end RegisterHelpers
