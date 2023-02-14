package vyxal

import vyxal.impls.Elements

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Queue
import scala.collection.mutable.Stack
import scala.compiletime.ops.double
import spire.syntax.truncatedDivision

object Parser:

  /** The return type of a parser. If the parser fails, then it's a `Left`
    * containing a `VyxalCompilationError`. If the parser succeeds, it's a tuple
    * containing whatever it parsed
    */
  type ParserRet[T] = Either[VyxalCompilationError, T]

  private def toValidName(name: String): String =
    name
      .filter(c => c.isLetterOrDigit || c == 'ı')
      .replace("ı", "i")
      .dropWhile(!_.isLetter)

  /** The parser takes a list of tokens and performs two sweeps of parsing:
    * structures + arity grouping and then modifiers. The first sweep deals
    * directly with the token list provided to the parser, and leaves its
    * results in a stack of ASTs (the stack data type is used because it means
    * that arity grouping is simply popping previous ASTs until a niladic state
    * is reached). The second sweep takes the stack of ASTs and applies the
    * logic of modifier grouping, placing its result in a single Group AST.
    *
    * @param topLevel
    *   Whether this is the topmost call to parse, in which case it should
    *   remove any `StructureAllClose`s left behind by `parseStructure`
    */
  private def parse(
      program: Queue[VyxalToken],
      topLevel: Boolean = false
  ): ParserRet[AST] =
    val asts = Stack[AST]()
    // Convert the list of tokens to a queue so that ASTs like Structures can
    // freely take as many tokens as they need without needing to worry about
    // changing indexes like a for-loop would require.

    // Begin the first sweep of parsing

    while program.nonEmpty && !isCloser(program.front) do
      (program.dequeue(): @unchecked) match
        // Numbers, strings and newlines are trivial, and are simply evaluated
        case VyxalToken.Number(value) =>
          asts.push(AST.Number(VNum(value)))
        case VyxalToken.Str(value) => asts.push(AST.Str(value))
        case VyxalToken.Newline    => asts.push(AST.Newline)
        case VyxalToken.StructureOpen(open) =>
          parseStructure(open, program) match
            case Right(ast) => asts.push(ast)
            case l          => return l
          if topLevel && program.nonEmpty && program.front == VyxalToken.StructureAllClose
          then program.dequeue()
        /*
         * List are just structures with two different opening and closing
         * token possibilities, so handle them the same way.
         */
        case VyxalToken.ListOpen =>
          parseBranches(program, true)(_ == VyxalToken.ListClose) match
            case Right(elements) => asts.push(AST.Lst(elements))
            case Left(err)       => return Left(err)

        /*
         * Now comes command handling. When handling commands, we need to
         * perform the arity grouping when pushing the command. This is done
         * by checking the arity of the command, and then checking however many
         * top stack elements are needed to make the command equivalent to a
         * nilad. For arities -1 and 0, the command doesn't need to be grouped.
         */
        case VyxalToken.Command(name) =>
          parseCommand(name, asts, program) match
            case Right(ast) => asts.push(ast)
            case l          => return l
        // At this stage, modifiers aren't explicitly handled, so just push a
        // temporary AST to comply with the type of the AST stack
        case VyxalToken.MonadicModifier(v) => asts.push(AST.JunkModifier(v, 1))
        case VyxalToken.DyadicModifier(v)  => asts.push(AST.JunkModifier(v, 2))
        case VyxalToken.TriadicModifier(v) => asts.push(AST.JunkModifier(v, 3))
        case VyxalToken.TetradicModifier(v) =>
          asts.push(AST.JunkModifier(v, 4))
        case VyxalToken.SpecialModifier(v) => asts.push(AST.SpecialModifier(v))
        case VyxalToken.GetVar(v)          => asts.push(AST.GetVar(v))
        case VyxalToken.SetVar(v)          => asts.push(AST.SetVar(v))
        case VyxalToken.AugmentVar(value) => asts.push(AST.AuxAugmentVar(value))
        case VyxalToken.UnpackVar(value) =>
          val names = ListBuffer[(String, Int)]()
          var name = ""
          var depth = 0
          val nameQueue = Queue[String](value.split("").toList*)
          while nameQueue.nonEmpty && depth != -1 do
            val top = nameQueue.dequeue()
            (top: @unchecked) match
              case "[" =>
                if name.nonEmpty then names += ((name, depth))
                name = ""
                depth += 1
              case "]" =>
                if name.nonEmpty then names += ((name, depth))
                name = ""
                depth -= 1
              case "|" =>
                if name.nonEmpty then names += ((name, depth))
                name = ""
              case _ => name += top
          end while
          if depth != -1 then names += ((name, depth))
          asts.push(AST.UnpackVar(names.toList))

    end while

    val finalAsts = Stack[AST]()
    while asts.nonEmpty do
      val topAst = asts.pop()
      topAst match
        case AST.Newline => None
        case AST.JunkModifier(name, arity) =>
          if arity > 0 then
            val modifier = Modifiers.modifiers(name)
            val modifierArgs = List.fill(arity)(finalAsts.pop())
            if modifier.from.isDefinedAt(modifierArgs) then
              finalAsts.push(modifier.from(modifierArgs))
            else
              return Left(
                VyxalCompilationError(
                  s"Modifier $name not defined for $modifierArgs"
                )
              )
        case AST.SpecialModifier(name) =>
          (name: @unchecked) match
            case "ᵜ" =>
              val lambdaAsts = ListBuffer[AST]()
              while asts.nonEmpty && asts.top != AST.Newline do
                lambdaAsts += asts.pop()
              finalAsts.push(
                AST.Lambda(
                  1,
                  List(),
                  List(AST.makeSingle(lambdaAsts.toList.reverse*))
                )
              )
            case "ᵗ" => ??? // TODO: Implement tie
        case AST.AuxAugmentVar(name) =>
          if asts.isEmpty then
            return Left(
              VyxalCompilationError("Missing element for augmented assign")
            )
          finalAsts.push(AST.AugmentVar(name, asts.pop()))
        case _ => finalAsts.push(topAst)
      end match
    end while

    Right(AST.makeSingle(finalAsts.toList*))
  end parse

  /** This is the function that performs arity grouping of elements. Here's a
    * table of all the different groupings:
    *
    * N = Nilad M = Monad D = Dyad T = Triad
    *
    * Pattern | Resulting Arity
    * --------|-----------------------------------------------------------------
    * N M ....| 0 ..............................................................
    * N N D ..| 0 ..............................................................
    * N N N T | 0 ..............................................................
    * N D ....| 1 ..............................................................
    * N N T ..| 1 ..............................................................
    * N T ....| 2
    *
    * ASTs will pop off the stack while: the top arity is 0 and the maximum
    * number of nilads is not reached.
    * @param name
    *   The command's name
    */
  def parseCommand(
      name: String,
      asts: Stack[AST],
      program: Queue[VyxalToken]
  ): ParserRet[AST] =
    Elements.elements.get(name) match
      case None =>
        Right(AST.Command(name))
      // Left(VyxalCompilationError(s"No such element: $name"))
      case Some(element) =>
        if asts.isEmpty then Right(AST.Command(name))
        else
          val arity = element.arity.getOrElse(0)
          val nilads = ListBuffer[AST]()

          while asts.nonEmpty && nilads.size < arity
            && (asts.top.arity.fold(false)(_ == 0))
          do nilads += asts.pop()
          if nilads.isEmpty then return Right(AST.Command(name))
          Right(
            AST.Group(
              (AST.Command(name) :: nilads.toList).reverse,
              Some(arity - nilads.size)
            )
          )

  /** Parse branches for an unknown structure or list, nothing more.
    *
    * @param canBeEmpty
    *   Whether the structure/list can be empty. If `true`, it will be possible
    *   to return an empty list. If `false`, an empty structure/list will result
    *   in a list containing a single empty branch.
    * @param isEnd
    *   Function to check if a token ends the structure/list
    * @return
    *   The parsed branches
    */
  def parseBranches(program: Queue[VyxalToken], canBeEmpty: Boolean)(
      isEnd: VyxalToken => Boolean
  ): ParserRet[List[AST]] =
    if program.isEmpty then Right((List(AST.makeSingle()), None))
    val branches = ListBuffer.empty[AST]

    while program.nonEmpty
      && (!isCloser(program.front) || program.front == VyxalToken.Branch)
    do
      parse(program) match
        case Left(err) => return Left(err)
        case Right(ast) =>
          branches += ast
          if program.nonEmpty && program.front == VyxalToken.Branch then
            // Get rid of the `|` token to move on to the next branch
            program.dequeue()
            if program.isEmpty ||
              (isCloser(program.front) && program.front != VyxalToken.Branch)
            then
              // Don't forget empty branches at the end
              branches += AST.makeSingle()
    end while

    if branches.isEmpty && !canBeEmpty then branches += AST.makeSingle()

    if program.nonEmpty
      && isEnd(program.front)
      && program.front != VyxalToken.StructureAllClose
    then
      // Get rid of the structure/list closer
      program.dequeue()

    Right(branches.toList)
  end parseBranches

  /** Structures are a bit more complicated. They require keeping track of a)
    * the branches of the structure and b) the number of structures that have
    * been previously opened (this is to allow for nested structures). The
    * algorithm is as follows:
    *   - While the program is not empty and structure depth > 0
    *   - Dequeue the next token
    *   - If the token is a branch (|), either add the current branch to the
    *     list of branches in this structure, or just add it to the current
    *     branch
    *   - If the token is a structure open, increment the structure depth and
    *     append the token to the current branch
    *   - If the token is a structure close, decrement the structure depth, and
    *     if we're still in a structure, append the token to the current branch
    *   - Otherwise, append the token to the current branch
    *
    * @param program
    *   The program without the opening character of the structure
    */
  private def parseStructure(
      structureType: StructureType,
      program: Queue[VyxalToken]
  ): ParserRet[AST] =
    val id =
      Option.when(structureType == StructureType.For)(parseIdentifier(program))

    parseBranches(program, false) {
      case VyxalToken.StructureAllClose | VyxalToken.StructureClose(_) => true
      case _                                                           => false
    }.flatMap { branches =>
      // Now, we can create the appropriate AST for the structure
      structureType match
        case StructureType.If =>
          branches match
            case List(thenBranch) => Right(AST.If(thenBranch, None))
            case List(thenBranch, elseBranch) =>
              Right(AST.If(thenBranch, Some(elseBranch)))
            case _ =>
              Left(VyxalCompilationError("Invalid if statement"))
        // TODO: One day make this extended elif
        case StructureType.While =>
          branches match
            case List(cond, body) => Right(AST.While(Some(cond), body))
            case List(body)       => Right(AST.While(None, body))
            case _ =>
              Left(VyxalCompilationError("Invalid while statement"))
        case StructureType.For =>
          branches match
            case List(body) => Right(AST.For(id.get, body))
            case _ =>
              Left(VyxalCompilationError("Invalid for statement"))
        case lambdaType @ (StructureType.Lambda | StructureType.LambdaMap |
            StructureType.LambdaFilter | StructureType.LambdaReduce |
            StructureType.LambdaSort) =>
          val lambda =
            if lambdaType == StructureType.Lambda then
              branches match
                case List()     => AST.Lambda(1, List.empty, List.empty)
                case List(body) => AST.Lambda(1, List.empty, List(body))
                case List(params, body) =>
                  val (param, arity) = parseParameters(params)
                  AST.Lambda(arity, param, List(body))
                case _ =>
                  val (param, arity) = parseParameters(branches.head)
                  AST.Lambda(arity, param, branches.drop(1))
            else AST.Lambda(1, List.empty, branches)
          // todo using the command names is a bit brittle
          //   maybe refer to the functions directly

          Right(lambdaType match
            case StructureType.Lambda => lambda
            case StructureType.LambdaMap =>
              AST.makeSingle(lambda, AST.Command("M"))
            case StructureType.LambdaFilter =>
              AST.makeSingle(lambda, AST.Command("F"))
            case StructureType.LambdaReduce =>
              AST.makeSingle(lambda, AST.Command("R"))
            case StructureType.LambdaSort =>
              AST.makeSingle(lambda, AST.Command("ṡ"))
          )
    }
  end parseStructure

  private def parseParameters(params: AST): (List[String | Int], Int) =
    val paramString = params.toVyxal
    val components = paramString.split(",")
    var arity = 0
    val paramList = ListBuffer.empty[String | Int]
    for component <- components do
      if arity != -1 then
        if component.forall(_.isDigit) then
          // Pop n from stack onto lambda stack
          val num = component.toInt
          arity += num
          paramList += num
        else if component.startsWith("!") then
          // operate on entire stack
          arity = -1
          paramList.drop(paramList.length)
        else if component == "*" || component == "×" then
          // varargs - pop n and pop n items onto lambda stack
          arity += 1
          paramList += "*"
        else
          // named parameter
          val name = toValidName(component)
          arity += 1
          paramList += name
    end for
    paramList.toList -> arity
  end parseParameters

  /** Parse an identifier for for loops. Consume only if there are 2 branches */
  private def parseIdentifier(program: Queue[VyxalToken]): Option[String] =
    val idEnd = program.indexWhere(isCloser)
    if idEnd == -1 || program(idEnd) != VyxalToken.Branch then None
    else
      // There are two branches, so get the name and consume the first branch
      val id = StringBuilder()
      var i = 0
      while i < idEnd do
        id ++= program.dequeue().value.filter(c => c.isLetter || c.isDigit)
        i += 1
      program.dequeue() // Get rid of the `|`
      Some(toValidName(id.toString()))

  /** Whether this token is a branch or a structure/list closer */
  def isCloser(token: VyxalToken): Boolean =
    token match
      case VyxalToken.Branch            => true
      case VyxalToken.ListClose         => true
      case VyxalToken.StructureClose(_) => true
      case VyxalToken.StructureAllClose => true
      case _                            => false

  def parse(code: String): Either[VyxalCompilationError, AST] =
    Lexer(code).flatMap { tokens =>
      val preprocessed = preprocess(tokens).to(Queue)
      val parsed = parse(preprocessed, true)
      if preprocessed.nonEmpty then
        // Some tokens were left at the end, which should never happen
        Left(
          VyxalCompilationError(
            s"Error parsing code: These tokens were not parsed ${preprocessed.toList}. Only parsed $parsed"
          )
        )
      else
        parsed match
          case Right(ast) => Right(postprocess(ast))
          case Left(error) =>
            Left(VyxalCompilationError(s"Error parsing code: $error"))
    }

  private def preprocess(tokens: List[VyxalToken]): List[VyxalToken] =
    val doubleClose = ListBuffer[VyxalToken]()
    tokens.foreach {
      case VyxalToken.StructureClose(")") =>
        doubleClose += VyxalToken.StructureClose("}")
        doubleClose += VyxalToken.StructureClose("}")
      case x => doubleClose += x
    }
    val lineup = Queue(doubleClose.toList*)
    val processed = ListBuffer[VyxalToken]()

    while (lineup.nonEmpty) do
      val temp = lineup.dequeue()
      (temp: @unchecked) match
        case VyxalToken.SyntaxTrigraph("#:[") =>
          val contents = mutable.StringBuilder()
          var depth = 1
          while depth != 0 do
            val top = lineup.dequeue()
            (top: @unchecked) match
              case VyxalToken.StructureOpen(StructureType.If) => depth += 1
              case VyxalToken.StructureAllClose               => depth -= 1
              case _                                          => None
            contents.++=(top.value)
          processed += VyxalToken.UnpackVar(contents.toString())
        case _ => processed += temp
    end while
    processed.toList
  end preprocess

  private def postprocess(asts: AST): AST =
    val temp = asts match
      case AST.Group(elems, _) =>
        val nilads = elems.reverse.takeWhile(isNilad).reverse
        val rest = elems.dropRight(nilads.length)
        AST.Group(nilads ++ rest, None)
      case _ => asts
    temp

  private def isNilad(ast: AST) =
    ast match
      case AST.GetVar(_) => false // you might want a variable at the end
      // after doing stuff like augmented assignment
      case _ => ast.arity == Some(0)

  def parseInput(input: String): VAny =
    Lexer(input).toOption
      .flatMap { tokens =>
        parse(tokens.to(Queue), true) match
          case Right(ast) =>
            ast match
              case AST.Number(n) => Some[VAny](n)
              case AST.Str(s)    => Some(s)
              case AST.Lst(l) =>
                Some(VList(l.map(e => parseInput(e.toString))*))
              case _ => None
          case Left(_) => None
      }
      .getOrElse(input)
end Parser
