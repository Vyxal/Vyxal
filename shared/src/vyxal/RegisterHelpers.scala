package vyxal

import vyxal.conversions.{*, given}

import scala.collection.mutable.ArrayBuffer

object RegisterHelpers:

  def register(using ctx: Context): ArrayBuffer[VAny] = ctx.globals.register
  def clear(using Context) = register.clear()
  def length(using Context) = VNum(register.length)

  def isEmpty(using Context): Boolean = register.length == 0

  def reverseRegister(using ctx: Context): Unit =
    val rev = register.reverse
    for i <- 0 to register.length - 1 do register.update(i, rev(i))

  /** Apply a monadic function to the item in the register at the specified
    * index
    */
  def applyFn(fn: VFun, idx: VNum)(using ctx: Context): Unit =
    if register.last.isInstanceOf[VPhysical]
    then
      val c = Interpreter.executeFn(fn, args = Seq(register(idx.toInt)))
      register.update(idx.toInt, c)

  /** Apply a function to all elements in the register */
  def map(fn: VFun)(using ctx: Context): Unit =
    for x <- 0 until register.length do applyFn(fn, VNum(x))

  def push(a: VAny)(using Context): Unit = register.append(a)

  def pop(n: VNum = 1, peek: Boolean = false, all: Boolean = false)(using
      ctx: Context
  ): VAny =
    if register.isEmpty then VNum(0)
    else
      val top = register.takeRight(n.toInt)
      if !peek then register.dropRightInPlace(n.toInt)
      if top.length == 1 && !all then
        top.head match
          case f: VFun => Interpreter.executeFn(f)
          case _ => top.head
      else top.toList.filterNot(_.isInstanceOf[VFun])

  def popAll(peek: Boolean = false)(using Context): VAny =
    pop(register.length, peek = peek, all = true)

  /** Indexing is cyclical and always relative to current array length */
  def index(i: VNum)(using Context): VAny =
    val idx = (i % VNum(register.length)).toInt
    register.toIndexedSeq(
      idx
    ) // scala modulo doesn't always return positive vales

end RegisterHelpers
