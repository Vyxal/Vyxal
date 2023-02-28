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
    val sbcsified = if literate then LiterateLexer.litLex(code) else code
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
      case AST.Number(value)           => ctx.push(value)
      case AST.Str(value)              => ctx.push(value)
      case AST.DictionaryString(value) => ctx.push(StringHelpers.sss(value))
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
      case AST.Ternary(thenBody, elseBody) =>
        if MiscHelpers.boolify(ctx.pop()) then execute(thenBody)
        else if elseBody.nonEmpty then execute(elseBody.get)

      case AST.IfStatement(conds, bodies, elseBody) =>
        var conditions = conds
        var branches = bodies
        var truthy = false
        while !truthy && conditions.nonEmpty do
          execute(conditions.head)
          truthy = MiscHelpers.boolify(ctx.pop())
          if truthy then execute(branches.head)
          else
            conditions = conditions.tail
            branches = branches.tail
        if !truthy && elseBody.nonEmpty then execute(elseBody.get)
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

      case lam: AST.Lambda       => ctx.push(VFun.fromLambda(lam))
      case AST.FnDef(name, lam)  => ctx.setVar(name, VFun.fromLambda(lam))
      case AST.GetVar(name)      => ctx.push(ctx.getVar(name))
      case AST.SetVar(name)      => ctx.setVar(name, ctx.pop())
      case AST.SetConstant(name) => ctx.setConst(name, ctx.pop())
      case AST.AugmentVar(name, op) =>
        ctx.push(ctx.getVar(name))
        op match
          case lam: AST.Lambda => ctx.push(executeFn(VFun.fromLambda(lam)))
          case _               => execute(op)
        ctx.setVar(name, ctx.pop())
      case AST.UnpackVar(names) =>
        MiscHelpers.unpack(names)
      case AST.DecisionStructure(predicate, container) =>
        val iterable = container match
          case Some(ast) =>
            executeFn(VFun.fromLambda(AST.Lambda(0, List.empty, List(ast))))
          case None => ctx.pop()

        val list = ListHelpers.makeIterable(iterable, Some(true))
        if ListHelpers
            .filter(
              list,
              VFun.fromLambda(AST.Lambda(1, List.empty, List(predicate)))
            )
            .nonEmpty
        then ctx.push(VNum(1))
        else ctx.push(VNum(0))
      case AST.GeneratorStructure(relation, initial, arity) =>
        val initVals = initial match
          case Some(ast) =>
            executeFn(VFun.fromLambda(AST.Lambda(0, List.empty, List(ast))))
          case None =>
            ctx.pop()

        val list = ListHelpers.makeIterable(initVals)
        val relationFn =
          VFun.fromLambda(AST.Lambda(arity, List.empty, List(relation)))

        val firstN = list.length match
          case 0 => ctx.settings.defaultValue
          case 1 => list.head
          case _ => list.last

        val firstM = list.length match
          case 0 => ctx.settings.defaultValue
          case 1 => list.head
          case _ => list.init.last

        val temp =
          generator(
            relationFn,
            firstN,
            firstM,
            arity,
            list,
          )

        ctx.push(VList.from(list ++: temp))
      case AST.ContextIndex(index) =>
        val args = ctx.ctxArgs.getOrElse(Seq.empty).reverse
        if index == -1 then ctx.push(VList.from(args.reverse))
        else if args.length < index then ctx.push(ctx.settings.defaultValue)
        else ctx.push(args(index))
      case _ => throw NotImplementedError(s"$ast not implemented")
    end match
    if ctx.settings.logLevel == LogLevel.Debug then
      println(s"res was ${ctx.peek}")
  end execute

  def generator(
      relation: VFun,
      ctxVarPrimary: VAny,
      ctxVarSecondary: VAny,
      arity: Int,
      previous: Seq[VAny] = Seq.empty
  )(using ctx: Context): LazyList[VAny] =
    val next = executeFn(
      relation,
      ctxVarPrimary,
      ctxVarSecondary,
      previous.takeRight(arity),
      overrideCtxArgs = previous :+ ctxVarSecondary :+ ctxVarPrimary,
    )
    next #:: generator(
      relation,
      next,
      ctxVarPrimary,
      arity,
      previous :+ next
    )
  end generator

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
      ctxVarPrimary: VAny | Null = null,
      ctxVarSecondary: VAny | Null = null,
      args: Seq[VAny] | Null = null,
      popArgs: Boolean = true,
      overrideCtxArgs: Seq[VAny] = Seq.empty,
      vars: mut.Map[String, VAny] = mut.Map(),
  )(using ctx: Context): VAny =
    val VFun(impl, arity, params, origCtx, origAST) = fn
    val useStack = arity == -1
    val inputs =
      if args != null && params.isEmpty then args
      else if arity == -1 then List.empty // operates on entire stack
      else if params.isEmpty then // no params, so just pop the args
        if popArgs then ctx.pop(arity) else ctx.peek(arity)
      else
        var argIndex: Int = 0
        val origLength = ctx.length
        def popFunction(n: Int): Seq[VAny] =
          if args != null && args.nonEmpty then
            val res =
              (argIndex until argIndex + n).map(ind => args(ind % args.length))
            argIndex += n
            res
          else ctx.pop(n)

        def popOneFunction(): VAny =
          if args != null && args.nonEmpty then
            val res = args(argIndex % args.length)
            argIndex += 1
            res
          else ctx.pop()

        val popped = ListBuffer.empty[VAny]
        val temp = ListBuffer.empty[VAny]
        for param <- params do
          param match
            case n: Int => // number parameter, so pop from stack to lambda stack
              if n == 1 then
                val top = popOneFunction()
                temp += top
                popped += top
              else
                val top = popFunction(n)

                temp ++= top
                popped ++= top
            case name: String =>
              if name == "*" then
                val termCount = ctx.pop().asInstanceOf[VNum].toInt
                popped += termCount
                val terms = popFunction(termCount)
                popped ++= terms
                temp += VList(terms*)
              else
                val top = popOneFunction()
                vars(name) = top // set variable
                popped += top
        end for
        if !popArgs && args.isEmpty then
          ctx.push(popped.toList.take(origLength).reverse*)
        temp.toList
    given fnCtx: Context =
      Context.makeFnCtx(
        origCtx,
        ctx,
        Option(ctxVarPrimary).orElse(inputs.headOption),
        if ctxVarSecondary == null then VList(inputs*) else ctxVarSecondary,
        if overrideCtxArgs.isEmpty then inputs else overrideCtxArgs,
        vars,
        inputs,
        useStack
      )
    fn.impl()(using fnCtx)
    fnCtx.peek
  end executeFn
end Interpreter
