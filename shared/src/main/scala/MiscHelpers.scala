package vyxal

import vyxal.Interpreter.executeFn

object MiscHelpers {
  // todo consider doing something like APL's forks so this doesn't have to be a partial function
  val add: VyFn[2] = forkify("add") {
    case (a: VNum, b: VNum)     => a + b
    case (a: String, b: VNum)   => s"$a$b"
    case (a: VNum, b: String)   => s"$a$b"
    case (a: String, b: String) => s"$a$b"
  }

  def boolify(x: VAny) = x match {
    case n: VNum   => n != 0
    case s: String => s.nonEmpty
    case f: VFun   => true
    case l: VList  => l.nonEmpty
  }

  def compare(a: VAny, b: VAny): Int = (a, b) match {
    case (a: VNum, b: VNum)     => a.compare(b)
    case (a: String, b: VNum)   => a.compareTo(b.toString)
    case (a: VNum, b: String)   => a.toString.compareTo(b)
    case (a: String, b: String) => a.compareTo(b)
    case (a, b) =>
      throw IllegalArgumentException(s"'$a' and '$b' can't be compared")
  }

  def firstNonNegative(f: VFun)(using ctx: Context): Int = {
    var i = 0
    while (true) {
      ctx.push(i)
      val result = executeFn(f)
      if (boolify(result)) return i
      i += 1
    }
    ???
  }

  def map(f: VFun, to: VList)(using ctx: Context): VList = {
    val result = to.toList.zipWithIndex.map { case (item, index) =>
      val mapCtx = Context.makeFnCtx(ctx, f.ctx, f.arity, f.params, true)
      mapCtx.contextVarM = index
      mapCtx.contextVarN = item
      f(item)(using mapCtx)
    }.toList
    VList(result*)
  }

  def multiply(a: VAny, b: VAny)(using Context): VAny = (a, b) match {
    case (a: VNum, b: VNum)     => a * b
    case (a: String, b: VNum)   => a * b.toInt
    case (a: VNum, b: String)   => b * a.toInt
    case (a: String, b: String) => StringHelpers.ringTranslate(a, b)
    case (a: VFun, b: VNum)     => a.withArity(b.toInt)
  }

  def reduce(iter: VAny, by: VFun, init: Option[VAny] = None)(using
      ctx: Context
  ): VAny = {
    var remaining = ListHelpers.makeIterable(iter, Some(true))(using ctx)

    // Convert niladic + monadic functions to be dyadic for reduction purposes
    val byFun = by.withArity(if (by.arity < 2) 2 else by.arity)

    // Take the first byFun.arity items as the initial set to operate on
    var operating = init match {
      case Some(elem) => elem +: remaining.take(byFun.arity - 1).toList
      case None       => remaining.take(byFun.arity).toList
    }
    remaining = remaining.drop(operating.length)

    val reduceCtx =
      Context.makeFnCtx(ctx, byFun.ctx, byFun.arity, byFun.params, true)

    if (operating.length == 0) { return 0 }
    if (operating.length == 1) { return operating.head }

    reduceCtx.contextVarN = operating(0)
    reduceCtx.contextVarM = operating(1)

    while remaining.length + operating.length != 1 do {
      val result = byFun(operating)(using reduceCtx)
      reduceCtx.contextVarM = remaining.headOption.getOrElse(result)
      reduceCtx.contextVarN = result
      println(s"m: ${reduceCtx.contextVarM}, n: ${reduceCtx.contextVarN}")
      operating = (result +: remaining.take(byFun.arity - 1)).toList
      remaining = remaining.drop(byFun.arity - 1)
    }

    reduceCtx.contextVarN
  }

  def vyPrint(x: VAny)(using ctx: Context): Unit = {
    // todo change later
    ctx.settings.printFn(x)
  }

  def vyPrintln(x: VAny)(using Context): Unit = {
    vyPrint(x)
    vyPrint("\n")
  }
}
