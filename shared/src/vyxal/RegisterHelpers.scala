package vyxal

import vyxal.conversions.{*, given}

import scala.collection.mutable.ArrayBuffer


object RegisterHelpers:

	def register(using ctx: Context): ArrayBuffer[VAny] = ctx.globals.register
	def clear(using Context) = register.length == 0
	def length(using Context) = VNum(register.length)

	def isEmpty(using Context): Boolean = register.length == 0

	def reverseRegister(using ctx: Context): Unit = 
		val rev = register.reverse
		for i <- 0 to register.length - 1 do
			register.update(i, rev(i))
		
	/** Apply a monadic function to an item in the register */	
	def applyFn(fn: VFun, idx: VNum)(using ctx: Context): Unit =
		if register.last.isInstanceOf[VPhysical]
		then
			val c = Interpreter.executeFn(fn, args = Seq(register(idx.toInt)))
			register.update(idx.toInt, c)


	/** Apply a function to all elements in the register */
	def map(fn: VFun)(using ctx: Context): Unit =
		for x <- 0 until register.length do
			applyFn(fn, VNum(x)) 

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
				top.head match
					case f: VFun => Interpreter.executeFn(f)
					case _ => top.head
			else top.toList.filterNot(_.isInstanceOf[VFun])

			
	/** Indexing is cyclical and always relative to current array length*/
	def index(i: VNum)(using Context): VAny =
		val idx = (i % VNum(register.length)).toInt
		register.toIndexedSeq(idx) // scala modulo doesn't always return positive vales 

end RegisterHelpers
