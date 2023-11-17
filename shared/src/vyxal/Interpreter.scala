package vyxal

import vyxal.parsing.Lexer
import vyxal.MiscHelpers.vyPrintln
import vyxal.VNum.given

import scala.collection.mutable.ListBuffer
import scala.collection.mutable as mut

object Interpreter:
  def version = "3.0.0"
  def execute(code: String)(using ctx: Context): Unit =
    val lexRes = Lexer(code)
    val tokens = lexRes match
      case Right(tokens) => tokens
      case Left(err) => throw Error(s"Lexing failed: $err")
    scribe.debug(s"Lexed tokens: $tokens")
    val sugarless = Lexer.removeSugar(
      if ctx.settings.literate then Lexer.sbcsify(tokens) else code
    )
    sugarless match
      case Some(code) => scribe.debug(s"Sugarless: $code")
      case None => ()

    val parsed = Parser.parse(tokens)

    parsed match
      case Right(ast) =>
        scribe.debug(s"Executing '$code' (ast: $ast)")
        ctx.globals.originalProgram = ast
        try execute(ast)
        catch
          case _: QuitException => scribe.debug("Program quit using Q")

          // todo implicit output according to settings
        if !ctx.isStackEmpty &&
          ctx.settings.endPrintMode == EndPrintMode.Default
        then vyPrintln(ctx.peek)
      case Left(error) => throw Error(s"Error while executing $code: $error")
  end execute

  def execute(ast: AST)(using ctx: Context): Unit =
    scribe.trace(s"Executing AST $ast, stack = ${ctx.peek(5)}")
    ast match
      case AST.Number(value, _) => ctx.push(value)
      case AST.Str(value, _) => ctx.push(value)
      case AST.DictionaryString(value, _) =>
        ctx.push(StringHelpers.decompress(value))
      case AST.Lst(elems, _) =>
        val list = collection.mutable.ListBuffer.empty[VAny]
        for elem <- elems do
          execute(elem)(using ctx.makeChild())
          list += ctx.pop()
        ctx.push(VList.from(list.toList))
      case AST.Command(cmd, _) => Elements.elements.get(cmd) match
          case Some(elem) => elem.impl()
          case None => throw RuntimeException(s"No such command: '$cmd'")
      case AST.Group(elems, _, _) => elems.foreach(Interpreter.execute)
      case AST.CompositeNilad(elems, _) => elems.foreach(Interpreter.execute)
      case AST.Ternary(thenBody, elseBody, _) =>
        if ctx.pop().toBool then execute(thenBody)
        else if elseBody.nonEmpty then execute(elseBody.get)

      case AST.IfStatement(conds, bodies, elseBody, _) =>
        var conditions = conds
        var branches = bodies
        var truthy = false
        while !truthy && conditions.nonEmpty do
          execute(conditions.head)
          truthy = ctx.pop().toBool
          if truthy then execute(branches.head)
          else
            conditions = conditions.tail
            branches = branches.tail
        if !truthy && elseBody.nonEmpty then execute(elseBody.get)
      case AST.While(None, body, _) =>
        try
          val loopCtx = ctx.makeChild()
          loopCtx.ctxVarPrimary = true
          loopCtx.ctxVarSecondary = ctx.settings.rangeStart
          while true do
            try
              execute(body)(using loopCtx)
              loopCtx.ctxVarSecondary =
                loopCtx.ctxVarSecondary.asInstanceOf[VNum] + 1
            catch case _: ContinueLoopException => ()
        catch case _: BreakLoopException => return
      case AST.While(Some(cond), body, _) =>
        try
          execute(cond)
          given loopCtx: Context = ctx.makeChild()
          loopCtx.ctxVarPrimary = ctx.peek
          loopCtx.ctxVarSecondary = ctx.settings.rangeStart
          while ctx.pop().toBool do
            try
              execute(body)
              execute(cond)
              loopCtx.ctxVarPrimary = ctx.peek
              loopCtx.ctxVarSecondary =
                loopCtx.ctxVarSecondary.asInstanceOf[VNum] + 1
            catch case _: ContinueLoopException => ()

        catch case _: BreakLoopException => return

      case AST.For(name, body, _) =>
        val iterable =
          ListHelpers.makeIterable(ctx.pop(), Some(true))(using ctx)
        var index = 0
        given loopCtx: Context = ctx.makeChild()
        try
          for elem <- iterable do
            try
              name.foreach(loopCtx.setVar(_, elem))
              loopCtx.ctxVarPrimary = elem
              loopCtx.ctxVarSecondary = index
              index += 1
              execute(body)(using loopCtx)
            catch case _: ContinueLoopException => ()
        catch case _: BreakLoopException => return

      case lam: AST.Lambda => ctx.push(VFun.fromLambda(lam))
      case AST.FnDef(name, lam, _) => ctx.setVar(name, VFun.fromLambda(lam))
      case AST.GetVar(name, _) => ctx.push(ctx.getVar(name))
      case AST.SetVar(name, _) => ctx.setVar(name, ctx.pop())
      case AST.SetConstant(name, _) => ctx.setConst(name, ctx.pop())
      case AST.AugmentVar(name, op, _) =>
        ctx.push(ctx.getVar(name))
        op match
          case lam: AST.Lambda => ctx.push(executeFn(VFun.fromLambda(lam)))
          case _ => execute(op)
        ctx.setVar(name, ctx.pop())
      case AST.UnpackVar(names, _) => MiscHelpers.unpack(names)
      case AST.DecisionStructure(predicate, container, _) =>
        val iterable = container match
          case Some(ast) =>
            executeFn(VFun.fromLambda(AST.Lambda(0, List.empty, List(ast))))
          case None => ctx.pop()

        val list = ListHelpers.makeIterable(iterable, Some(true))
        if ListHelpers
            .filter(
              list,
              VFun.fromLambda(AST.Lambda(1, List.empty, List(predicate))),
            )
            .nonEmpty
        then ctx.push(VNum(1))
        else ctx.push(VNum(0))
      case AST.GeneratorStructure(relation, initial, arity, _) =>
        val initVals = initial match
          case Some(ast) =>
            executeFn(VFun.fromLambda(AST.Lambda(0, List.empty, List(ast))))
          case None => ctx.pop()

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

        val temp = generator(relationFn, firstN, firstM, arity, list)

        ctx.push(VList.from(list ++: temp))
      case AST.ContextIndex(index, _) =>
        val args = ctx.ctxArgs.getOrElse(Seq.empty).reverse
        if index == -1 then ctx.push(VList.from(args.reverse))
        else if args.sizeIs < index then ctx.push(ctx.settings.defaultValue)
        else ctx.push(args(index))
      case AST.Generated(exec, _) => exec()
      case _ => throw NotImplementedError(s"$ast not implemented")
    end match
    scribe.trace(s"Top of stack: ${ctx.peek}")
  end execute

  def generator(
      relation: VFun,
      ctxVarPrimary: VAny,
      ctxVarSecondary: VAny,
      arity: Int,
      previous: Seq[VAny] = Seq.empty,
  )(using ctx: Context): LazyList[VAny] =
    val next = executeFn(
      relation,
      ctxVarPrimary,
      ctxVarSecondary,
      args = previous.take(arity),
      overrideCtxArgs = ctxVarPrimary +: ctxVarSecondary +: previous,
    )
    next #:: generator(relation, next, ctxVarPrimary, arity, next +: previous)

  /** Execute a function and return what was on the top of the stack, if there
    * was anything
    *
    * @param args
    *   Custom arguments (instead of popping from the stack)
    * @param popArgs
    *   Whether to pop the arguments from the stack (instead of merely peeking)
    */
  def executeFn(
      fn: VFun,
      ctxVarPrimary: VAny | Null = null,
      ctxVarSecondary: VAny | Null = null,
      args: Seq[VAny] | Null = null,
      popArgs: Boolean = true,
      overrideCtxArgs: Seq[VAny] = Seq.empty,
      vars: mut.Map[String, VAny] = mut.Map(),
  )(using ctx: Context): VAny =
    val VFun(_, arity, params, origCtx, lambda, _) = fn
    var originallyFunction = false
    if !lambda.isEmpty then
      val AST.Lambda(_, _, _, originallyFunction, _) = lambda.get
      if originallyFunction then ctx.globals.callStack.push(fn)
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
            val res = (argIndex until argIndex + n).map(ind =>
              args(ind % args.length)
            )
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
        inputs.reverse,
        useStack,
      )
    try fn.impl()(using fnCtx)
    catch case _: ReturnFromFunctionException => ()

    if originallyFunction then ctx.globals.callStack.pop()
    val res = fnCtx.peek
    scribe.trace(s"Result of executing function: $res")
    res
  end executeFn
end Interpreter
