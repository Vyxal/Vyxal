package vyxal

import vyxal.impls.Elements
import vyxal.MiscHelpers.{vyPrint, vyPrintln}

import scala.annotation.varargs
import scala.collection.mutable.ListBuffer
import scala.collection.mutable as mut
import VNum.given

object Interpreter:
  def execute(code: String, literate: Boolean = false)(using
      ctx: Context
  ): Unit =
    val sbcsified = if literate then litLex(code) else code
    Parser.parse(sbcsified) match
      case Right(ast) =>
        if ctx.settings.logLevel == LogLevel.Debug then
          println(s"Executing '$code' (ast: $ast)")
        execute(ast)
        // todo implicit output according to settings
        if !ctx.isStackEmpty && ctx.settings.endPrintMode == EndPrintMode.Default
        then vyPrintln(ctx.peek)
      case Left(error) =>
        throw new Error(s"Error while executing $code: $error")

  def execute(ast: AST)(using ctx: Context): Unit =
    if ctx.settings.logLevel == LogLevel.Debug then
      println(s"Executing AST $ast, stack = ${ctx.peek(5)}")
    ast match
      case AST.Number(value) => ctx.push(value)
      case AST.Str(value)    => ctx.push(value)
      case AST.Lst(elems) =>
        val list = collection.mutable.ListBuffer.empty[VAny]
        for elem <- elems do
          given elemCtx: Context = ctx.makeChild()
          execute(elem)
          list += ctx.pop()
        ctx.push(VList(list.toList*))
      case AST.Command(cmd) =>
        Elements.elements.get(cmd) match
          case Some(elem) => elem.impl()
          case None       => throw RuntimeException(s"No such command: '$cmd'")
      case AST.Group(elems, _) =>
        elems.foreach(Interpreter.execute(_))
      case AST.CompositeNilad(elems) =>
        elems.foreach(Interpreter.execute(_))
      case AST.If(thenBody, elseBody) =>
        if MiscHelpers.boolify(ctx.pop()) then execute(thenBody)
        else if elseBody.nonEmpty then execute(elseBody.get)
      case AST.While(None, body) =>
        val loopCtx = ctx.makeChild()
        loopCtx.ctxVarPrimary = true
        loopCtx.ctxVarSecondary = ctx.settings.rangeStart
        while true do
          execute(body)(using loopCtx)
          loopCtx.ctxVarSecondary =
            loopCtx.ctxVarSecondary.asInstanceOf[VNum] + 1
      case AST.While(Some(cond), body) =>
        execute(cond)
        given loopCtx: Context = ctx.makeChild()
        loopCtx.ctxVarPrimary = ctx.peek
        loopCtx.ctxVarSecondary = ctx.settings.rangeStart
        while MiscHelpers.boolify(ctx.pop()) do
          execute(body)
          execute(cond)
          loopCtx.ctxVarPrimary = ctx.peek
          loopCtx.ctxVarSecondary =
            loopCtx.ctxVarSecondary.asInstanceOf[VNum] + 1

      case AST.For(None, body) =>
        val iterable =
          ListHelpers.makeIterable(ctx.pop(), Some(true))(using ctx)
        var index = 0
        given loopCtx: Context = ctx.makeChild()
        for elem <- iterable do
          loopCtx.ctxVarPrimary = elem
          loopCtx.ctxVarSecondary = index
          index += 1
          execute(body)(using loopCtx)

      case AST.For(Some(name), body) =>
        val iterable =
          ListHelpers.makeIterable(ctx.pop(), Some(true))(using ctx)
        var index = 0
        given loopCtx: Context = ctx.makeChild()
        for elem <- iterable do
          loopCtx.setVar(name, elem)
          loopCtx.ctxVarPrimary = elem
          loopCtx.ctxVarSecondary = index
          index += 1
          execute(body)(using loopCtx)

      case lam: AST.Lambda      => ctx.push(VFun.fromLambda(lam))
      case AST.FnDef(name, lam) => ctx.setVar(name, VFun.fromLambda(lam))
      case AST.GetVar(name)     => ctx.push(ctx.getVar(name))
      case AST.SetVar(name)     => ctx.setVar(name, ctx.pop())
      case AST.AugmentVar(name, op) =>
        ctx.push(ctx.getVar(name))
        op match
          case lam: AST.Lambda => ctx.push(executeFn(VFun.fromLambda(lam)))
          case _               => execute(op)
        ctx.setVar(name, ctx.pop())
      case AST.UnpackVar(names) =>
        MiscHelpers.unpack(names)
      case AST.ExecuteFn =>
        ctx.pop() match
          case fn: VFun => ctx.push(executeFn(fn))
          case _        => ???
      case _ => throw NotImplementedError(s"$ast not implemented")
    end match
    if ctx.settings.logLevel == LogLevel.Debug then
      println(s"res was ${ctx.peek}")
  end execute

  /** Execute a function and return what was on the top of the stack, if there
    * was anything
    *
    * @param args
    *   Custom arguments (instead of popping from the stack)
    * @param popArgs
    *   Whether to pop the arguments from the stack (instead of merely peeking)
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def executeFn(
      fn: VFun,
      ctxVarPrimary: Option[VAny] = None,
      ctxVarSecondary: Option[VAny] = None,
      args: Seq[VAny] | Null = null,
      popArgs: Boolean = true
  )(using ctx: Context): VAny =
    val VFun(impl, arity, params, origCtx, origAST) = fn
    var useStack: Boolean = false
    val vars: mut.Map[String, VAny] = mut.Map()
    val inputs =
      if args != null then args
      else if params.length == 0 then
        if popArgs then ctx.pop(arity) else ctx.peek(arity)
      else
        val popFn = (x: Int) => if popArgs then ctx.pop(x) else ctx.peek(x)
        val temp = ListBuffer.empty[VAny]
        for param <- params do
          param match
            case n: Int =>
              if n == 1 then temp += popFn(1)(0)
              else temp += VList(popFn(n)*)
            case name: String =>
              if name == "*" then
                val terms = popFn(1)(0)
                terms match
                  case n: VNum => temp += VList(popFn(n.toInt)*)
                  case _       => throw RuntimeException(s"Can't unpack $terms")
              else if name == "~" then useStack = true
              else vars(name) = popFn(1)(0)
        temp.toList

    given fnCtx: Context =
      Context.makeFnCtx(
        origCtx,
        ctx,
        Some(
          ctxVarPrimary.getOrElse(
            if inputs.nonEmpty then inputs(0) else ctx.settings.defaultValue
          )
        ),
        Some(ctxVarSecondary.getOrElse(VList(inputs*))),
        vars,
        inputs
      )

    fn.impl()(using fnCtx)

    fnCtx.peek
  end executeFn
end Interpreter
