package vyxal

import vyxal.elements.NewElements
import vyxal.parsing.{Lexer, Parser, ParserResult}
import vyxal.MiscHelpers.vyPrintln
import vyxal.StringHelpers.prettyPrint
import vyxal.VNum.given

import scala.collection.mutable.ListBuffer
import scala.collection.mutable as mut

object Interpreter:
  def version = "3.4.6"
  def execute(code: String)(using ctx: Context): Unit =
    /** Attempt lexing */
    val tokens =
      try
        val lexRes =
          if ctx.globals.settings.literate then Lexer.lexLiterate(code)
          else Lexer.lexSBCS(code)
        scribe.debug(s"Lexed tokens: $lexRes")
        val sugarless =
          if ctx.settings.literate then None
          else
            Lexer.removeSugar(
              code
            )
        sugarless match
          case Some(code) => scribe.debug(s"Sugarless: $code")

          case None => ()
        lexRes
      catch
        case ex: VyxalException => throw ex
        case ex: Throwable => throw UnknownLexingException(ex)

    /** Attempt parsing */
    val ParserResult(ast, customDefns, classes, extensions) =
      try Parser.parse(tokens)
      catch
        case ex: VyxalException => throw ex
        case ex: Throwable => throw UnknownParsingException(ex)

    /** Attempt execution */
    try
      scribe.debug(s"Executing '$code' (ast: $ast)")
      ctx.globals.originalProgram = ast
      ctx.globals.symbols = customDefns
      ctx.globals.classes = classes
      ctx.globals.extensions = extensions
      execute(ast)
      if !ctx.globals.printed && !ctx.testMode then
        if ctx.settings.wrapStack then ctx.wrap()

        if ctx.settings.endPrintMode == EndPrintMode.Default then
          vyPrintln(ctx.pop())
        else if ctx.settings.endPrintMode == EndPrintMode.Pretty then
          vyPrintln(prettyPrint(ctx.pop()))
        else if ctx.settings.endPrintMode == EndPrintMode.JoinNewlines then
          vyPrintln(ListHelpers.makeIterable(ctx.pop()).mkString("\n"))
        else if ctx.settings.endPrintMode == EndPrintMode.Sum then
          vyPrintln(ListHelpers.sum(ListHelpers.makeIterable(ctx.pop())))
        else if ctx.settings.endPrintMode == EndPrintMode.DeepSum then
          vyPrintln(
            ListHelpers.sum(
              ListHelpers.flatten(ListHelpers.makeIterable(ctx.pop()))
            )
          )
        else if ctx.settings.endPrintMode == EndPrintMode.Length then
          vyPrintln(
            VNum(
              ListHelpers.makeIterable(ctx.pop()).length
            )
          )
        else if ctx.settings.endPrintMode == EndPrintMode.Maximum then
          vyPrintln(
            ListHelpers.makeIterable(ctx.pop()).maxOption.getOrElse(VList())
          )
        else if ctx.settings.endPrintMode == EndPrintMode.Minimum then
          vyPrintln(
            ListHelpers.makeIterable(ctx.pop()).minOption.getOrElse(VList())
          )
        else if ctx.settings.endPrintMode == EndPrintMode.JoinSpaces then
          vyPrintln(ListHelpers.makeIterable(ctx.pop()).mkString(" "))
        else if ctx.settings.endPrintMode == EndPrintMode.JoinNothing then
          vyPrintln(ListHelpers.makeIterable(ctx.pop()).mkString)
        end if
      end if
      if ctx.settings.endPrintMode == EndPrintMode.Force then
        vyPrintln(ctx.pop())
    catch
      case ex: VyxalException => throw ex
      case ex: Throwable => throw UnknownRuntimeException(ex)
  end execute

  def execute(ast: AST)(using ctx: Context): Unit =
    scribe.trace(s"Executing AST $ast, stack = ${ctx.peek(5)}")
    ast match
      case AST.Number(value, _) => ctx.push(value)
      case AST.Str(value, _) => ctx.push(value)
      case AST.DictionaryString(value, _) =>
        ctx.push(StringHelpers.decompress(value))
      case AST.CompressedNumber(value, _) =>
        ctx.push(StringHelpers.decompress252Number(value))
      case AST.CompressedString(value, _) =>
        ctx.push(StringHelpers.decompress252String(value))
      case AST.Lst(elems, _) =>
        val context = ctx.copy
        context.clear()
        for elem <- elems do execute(elem)(using context)
        ctx.push(VList.from(context.getStack))
      case AST.Command(cmd, _, overwriteable) =>
        var executed = false
        if overwriteable && ctx.globals.extensions.contains(cmd) then
          val ext = ctx.globals.extensions(cmd)
          val potentialArgs = ctx.peek(ext._2)
          val types = MiscHelpers.typesOf(potentialArgs*)
          val overload = getOverload(types, ext._1)
          overload match
            case Some((types, implementation)) =>
              ctx.privatable ++= types.filter(_ != "*")
              val lam = VFun.fromLambda(implementation.asInstanceOf[AST.Lambda])
              if implementation.arity.getOrElse(0) == -1 then executeFn(lam)
              else ctx.push(executeFn(lam))
              executed = true
              for t <- types if t != "*" do ctx.privatable.dropRightInPlace(1)
            case None => ()
        if overwriteable && !executed && ctx.globals.symbols.contains(cmd) then
          ctx.globals.symbols(cmd).impl match
            case Some(implementation) =>
              val lam = VFun.fromLambda(implementation.asInstanceOf[AST.Lambda])
              if implementation.arity.getOrElse(0) == -1 then executeFn(lam)
              else ctx.push(executeFn(lam))
              executed = true
            case None => ()
        if !executed then
          NewElements.elements.get(cmd) match
            case Some(impl) => impl()
            case None => NewElements.internalUseElements.get(cmd) match
                case Some(impl) => impl()
                case None => Elements.elements.get(cmd) match
                    case Some(elem) => elem.impl()
                    case None =>
                      throw VyxalYikesException(s"No such element: '$cmd'")
      case AST.Group(elems, _, _) => elems.foreach(Interpreter.execute)
      case AST.CompositeNilad(elems, _) => elems.foreach(Interpreter.execute)
      case AST.RedefineModifier(name, mode, args, implArity, impl, range) => ???

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
      case AST.GetVar(name, _) =>
        if ctx.globals.classes.contains(name) then ctx.push(VConstructor(name))
        else ctx.push(ctx.getVar(name))
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
          case Some(ast) => executeFn(
              VFun.fromLambda(AST.Lambda(Some(0), List.empty, List(ast)))
            )
          case None => ctx.pop()

        val list = ListHelpers.makeIterable(iterable, Some(true))
        if ListHelpers
            .filter(
              list,
              VFun.fromLambda(AST.Lambda(None, List.empty, List(predicate))),
            )
            .nonEmpty
        then ctx.push(VNum(1))
        else ctx.push(VNum(0))
      case AST.GeneratorStructure(relation, initial, arity, _) =>
        val initVals = initial match
          case Some(ast) => executeFn(
              VFun.fromLambda(AST.Lambda(Some(0), List.empty, List(ast)))
            )
          case None => ctx.pop()

        val list = ListHelpers.makeIterable(initVals)
        val relationFn =
          VFun.fromLambda(AST.Lambda(Some(arity), List.empty, List(relation)))

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
      case _ => throw VyxalYikesException(s"$ast not implemented")
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
      overrideCtxArgs = previous,
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
    val originallyFunction = false
    if !lambda.isEmpty then
      val AST.Lambda(_, _, _, originallyFunction, _) = lambda.get
      if originallyFunction then ctx.globals.callStack.push(fn)
    val useStack = arity == -1
    val inputs =
      if args != null && params.isEmpty then args
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
                temp ++= terms
              else
                val top = popOneFunction()
                vars(name) = top // set variable
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

  def createObject(con: VConstructor)(using ctx: Context): VObject =
    val fields = ctx.globals.classes(con.name).fields
    ctx.privatable += con.name
    val assignedFields = mut.Map[String, (Visibility, VAny)]()

    val originalVariables = ctx.allVars

    for (name, (visibility, predef)) <- fields do
      val fieldVal = predef match
        case Some(predef) => Interpreter.executeFn(
            VFun
              .fromLambda(predef.asInstanceOf[AST.Lambda])
              .copy(
                arity = 0
              )
          )
        case None => ctx.pop()
      assignedFields(name) = (visibility -> fieldVal)
      ctx.setVar(name, fieldVal)

    ctx.setVarsFrom(originalVariables)

    ctx.privatable.dropRightInPlace(1)
    VObject(con.name, assignedFields.toMap)
  end createObject

  private def getOverload(
      givenTypes: List[String],
      overloads: List[(List[String], CustomDefinition)],
  ): Option[(List[String], AST)] =
    overloads.find {
      case (types, _) =>
        types.zip(givenTypes).forall((a, b) => a == b || a == "*")
    } match
      case Some((types, defn)) => Some(types -> defn.impl.get)
      case None => None

end Interpreter
