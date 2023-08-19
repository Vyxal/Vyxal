package vyxal

import scala.language.strictEquality

import vyxal.parsing.{
  Lexer,
  StructureType,
  Token,
  TokenType,
  VyxalCompilationError
}

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Queue, Stack}

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
      program: Queue[Token],
      topLevel: Boolean = false
  ): ParserRet[AST] =
    val asts = Stack[AST]()
    // Convert the list of tokens to a queue so that ASTs like Structures can
    // freely take as many tokens as they need without needing to worry about
    // changing indexes like a for-loop would require.

    // Begin the first sweep of parsing

    while program.nonEmpty && !isCloser(program.front) do
      val token = program.dequeue()
      val value = token.value
      val range = token.range
      (token.tokenType: @unchecked) match
        // Numbers, strings and newlines are trivial, and are simply evaluated
        case TokenType.Number =>
          asts.push(AST.Number(VNum(value), range))
        case TokenType.Str => asts.push(AST.Str(value, range))
        case TokenType.DictionaryString =>
          asts.push(AST.DictionaryString(value, range))
        case TokenType.Newline => asts.push(AST.Newline)
        case TokenType.StructureOpen =>
          parseStructure(
            StructureType.values.find(_.open == value).get,
            program
          ) match
            case Right(ast) => asts.push(ast)
            case l => return l
          if topLevel && program.nonEmpty && program.front.tokenType == TokenType.StructureAllClose
          then program.dequeue()
        /*
         * List are just structures with two different opening and closing
         * token possibilities, so handle them the same way.
         */
        case TokenType.ListOpen =>
          parseBranches(program, true)(_ == TokenType.ListClose) match
            case Right(elements) => asts.push(AST.Lst(elements))
            case Left(err) => return Left(err)

        /*
         * Now comes command handling. When handling commands, we need to
         * perform the arity grouping when pushing the command. This is done
         * by checking the arity of the command, and then checking however many
         * top stack elements are needed to make the command equivalent to a
         * nilad. For arities -1 and 0, the command doesn't need to be grouped.
         */
        case TokenType.Command =>
          parseCommand(token, asts, program) match
            case Right(ast) => asts.push(ast)
            case l => return l
        // At this stage, modifiers aren't explicitly handled, so just push a
        // temporary AST to comply with the type of the AST stack
        case TokenType.MonadicModifier => asts.push(AST.JunkModifier(value, 1))
        case TokenType.DyadicModifier => asts.push(AST.JunkModifier(value, 2))
        case TokenType.TriadicModifier => asts.push(AST.JunkModifier(value, 3))
        case TokenType.TetradicModifier =>
          asts.push(AST.JunkModifier(value, 4))
        case TokenType.SpecialModifier => asts.push(AST.SpecialModifier(value))
        case TokenType.Comment => ()
        case TokenType.ContextIndex =>
          asts.push(
            AST.ContextIndex(if value.nonEmpty then value.toInt else -1)
          )
        case TokenType.GetVar => asts.push(AST.GetVar(value, range))
        case TokenType.SetVar => asts.push(AST.SetVar(value, range))
        case TokenType.Constant => asts.push(AST.SetConstant(value, range))
        case TokenType.AugmentVar => asts.push(AST.AuxAugmentVar(value, range))
        case TokenType.UnpackVar =>
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
          if depth != -1 then names += ((name, depth))
          asts.push(AST.UnpackVar(names.toList))
        case TokenType.Param =>
          asts.push(AST.Parameter(value))
      end match

    end while

    val finalAsts = Stack[AST]()
    while asts.nonEmpty do
      val topAst = asts.pop()
      topAst match
        case AST.Newline => ()
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
        case AST.SpecialModifier(name, _) =>
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
        case AST.AuxAugmentVar(name, _) =>
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
    * @param cmdTok
    *   The token from which the command is to be parsed
    */
  def parseCommand(
      cmdTok: Token,
      asts: Stack[AST],
      program: Queue[Token],
  ): ParserRet[AST] =
    val cmd =
      if !Elements.elements.contains(cmdTok.value) then
        Elements.symbolFor(cmdTok.value).get
      else cmdTok.value
    Elements.elements.get(cmd) match
      case None =>
        Right(AST.Command(cmd))
      // Left(VyxalCompilationError(s"No such element: $name"))
      case Some(element) =>
        if asts.isEmpty then Right(AST.Command(cmd, cmdTok.range))
        else
          val arity = element.arity.getOrElse(0)
          val nilads = ListBuffer[AST]()

          while asts.nonEmpty && nilads.sizeIs < arity
            && asts.top.arity.fold(false)(_ == 0)
          do nilads += asts.pop()
          if nilads.isEmpty then return Right(AST.Command(cmd, cmdTok.range))
          Right(
            AST.Group(
              (AST.Command(cmd, cmdTok.range) :: nilads.toList).reverse,
              Some(arity - nilads.size)
            )
          )
    end match
  end parseCommand

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
  def parseBranches(program: Queue[Token], canBeEmpty: Boolean)(
      isEnd: TokenType => Boolean
  ): ParserRet[List[AST]] =
    if program.isEmpty then return Right(List(AST.makeSingle()))
    val branches = ListBuffer.empty[AST]

    while program.nonEmpty
      && (!isCloser(
        program.front
      ) || program.front.tokenType == TokenType.Branch)
    do
      parse(program) match
        case Left(err) => return Left(err)
        case Right(ast) =>
          branches += ast
          if program.nonEmpty && program.front.tokenType == TokenType.Branch
          then
            // Get rid of the `|` token to move on to the next branch
            program.dequeue()
            if program.isEmpty ||
              (isCloser(
                program.front
              ) && program.front.tokenType != TokenType.Branch)
            then
              // Don't forget empty branches at the end
              branches += AST.makeSingle()
    end while

    if branches.isEmpty && !canBeEmpty then branches += AST.makeSingle()

    if program.nonEmpty
      && isEnd(program.front.tokenType)
      && program.front.tokenType != TokenType.StructureAllClose
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
      program: Queue[Token]
  ): ParserRet[AST] =
    parseBranches(program, false) {
      case TokenType.StructureAllClose | TokenType.StructureClose |
          TokenType.StructureDoubleClose =>
        true
      case _ => false
    }.flatMap { branches =>
      // Now, we can create the appropriate AST for the structure
      structureType match
        case StructureType.Ternary =>
          branches match
            case List(thenBranch) => Right(AST.Ternary(thenBranch, None))
            case List(thenBranch, elseBranch) =>
              Right(AST.Ternary(thenBranch, Some(elseBranch)))
            case _ =>
              Left(VyxalCompilationError("Invalid if statement"))
        case StructureType.IfStatement =>
          if branches.sizeIs < 2 then
            Left(VyxalCompilationError("Invalid if statement"))
          else
            val odd = branches.size % 2 == 1
            val grouped =
              if odd then branches.init.grouped(2).toList
              else branches.grouped(2).toList
            Right(
              AST.IfStatement(
                grouped.map(_(0)),
                grouped.map(_(1)),
                Option.when(odd)(branches.last)
              )
            )
        case StructureType.While =>
          branches match
            case List(cond, body) => Right(AST.While(Some(cond), body))
            case List(body) => Right(AST.While(None, body))
            case _ =>
              Left(VyxalCompilationError("Invalid while statement"))
        case StructureType.For =>
          branches match
            case List(name, body) =>
              Right(AST.For(Some(toValidName(name.toVyxal)), body))
            case List(body) => Right(AST.For(None, body))
            case _ =>
              Left(VyxalCompilationError("Invalid for statement"))
        case lambdaType @ (StructureType.Lambda | StructureType.LambdaMap |
            StructureType.LambdaFilter | StructureType.LambdaReduce |
            StructureType.LambdaSort) =>
          val lambda =
            if lambdaType == StructureType.Lambda then
              branches match
                case List() => AST.Lambda(1, List.empty, List.empty)
                case List(body) => AST.Lambda(1, List.empty, List(body))
                case List(params, body) =>
                  val (param, arity) = parseParameters(params)
                  AST.Lambda(arity, param, List(body))
                case _ =>
                  val (param, arity) = parseParameters(branches.head)
                  AST.Lambda(arity, param, branches.drop(1))
            else AST.Lambda(1, List.empty, branches)

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
        case StructureType.DecisionStructure =>
          branches match
            case List(pred, container) =>
              Right(AST.DecisionStructure(pred, Some(container)))
            case List(pred) => Right(AST.DecisionStructure(pred, None))
            case _ =>
              Left(VyxalCompilationError("Invalid decision structure"))
        case StructureType.GeneratorStructure =>
          if branches.sizeIs > 2 then
            Left(VyxalCompilationError("Invalid generator structure"))
          else
            var rel = branches.head
            val vals = (branches: @unchecked) match
              case List(_, initial) => Some(initial)
              case List(_) => None

            val arity = rel match
              case AST.Group(elems, _, _) =>
                elems.last match
                  case number: AST.Number =>
                    rel = AST.Group(elems.init, None)
                    number.value.toInt
                  case _ =>
                    var stackItems = 0
                    var popped = 0
                    for elem <- elems do
                      val elemArity = elem.arity.getOrElse(0)
                      if elemArity < stackItems then
                        stackItems -= elemArity + 1 // assume everything only returns one value
                      else
                        popped += elemArity - stackItems
                        stackItems = 1
                    popped
              case _ => rel.arity.getOrElse(2)

            Right(
              AST.GeneratorStructure(
                rel,
                vals,
                arity
              )
            )
    }

  private def parseParameters(params: AST): (List[String | Int], Int) =
    val paramString = params.toVyxal
    val components = paramString.split(",")
    // ^ may leave extra spaces, but that's okay, because
    // spaces are removed when converting to a valid name
    var arity = 0
    val paramList = ListBuffer.empty[String | Int]
    for component <- components do
      if arity != -1 && component.nonEmpty then
        if component.forall(_.isDigit) then
          // Pop n from stack onto lambda stack
          val num = component.toInt
          arity += num
          paramList += num
        else if component.startsWith("!") then
          // operate on entire stack, so set arity to -1 and remove all other parameters
          // also, process no other parameters
          arity = -1
          paramList.drop(paramList.length)
        else if component == "*" || component == "×" then
          // varargs - pop n and push n items onto lambda stack
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

  /** Whether this token is a branch or a structure/list closer */
  def isCloser(token: Token): Boolean =
    token.tokenType match
      case TokenType.Branch => true
      case TokenType.ListClose => true
      case TokenType.StructureClose => true
      case TokenType.StructureDoubleClose => true
      case TokenType.StructureAllClose => true
      case _ => false

  def parse(tokens: List[Token]): Either[VyxalCompilationError, AST] =
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

  private def preprocess(tokens: List[Token]): List[Token] =
    val doubleClose = ListBuffer[Token]()
    tokens.foreach {
      case Token(TokenType.StructureDoubleClose, _, range) =>
        doubleClose += Token(TokenType.StructureClose, "}", range)
        doubleClose += Token(TokenType.StructureClose, "}", range)
      case x => doubleClose += x
    }
    val lineup = Queue(doubleClose.toList*)
    val processed = ListBuffer[Token]()

    while lineup.nonEmpty do
      val temp = lineup.dequeue()
      temp match
        case Token(TokenType.SyntaxTrigraph, "#:[", _) =>
          val contents = mutable.StringBuilder()
          var depth = 1
          while depth != 0 do
            val top = lineup.dequeue()
            top match
              case Token(TokenType.StructureOpen, open, _)
                  if open == StructureType.Ternary.open =>
                depth += 1
              case Token(TokenType.StructureAllClose, _, _) => depth -= 1
              case _ =>
            contents.++=(top.value)
          processed += Token(
            TokenType.UnpackVar,
            contents.toString(),
            temp.range
          )
        case _ => processed += temp
      end match
    end while
    processed.toList
  end preprocess

  private def postprocess(asts: AST): AST =
    val temp = asts match
      case AST.Group(elems, _, _) =>
        val nilads = elems.reverse.takeWhile(isNilad).reverse
        val rest = elems.dropRight(nilads.length)
        AST.Group(nilads ++ rest, None)
      case _ => asts
    temp

  private def isNilad(ast: AST) =
    ast match
      case AST.GetVar(_, _) => false // you might want a variable at the end
      // after doing stuff like augmented assignment
      case _ => ast.arity.contains(0)

  def parseInput(input: String): VAny =
    Lexer
      .lexSBCS(input)
      .toOption
      .flatMap { tokens =>
        parse(tokens.to(Queue), true) match
          case Right(ast) =>
            ast match
              case AST.Number(n, _) => Some[VAny](n)
              case AST.Str(s, _) => Some(s)
              case AST.Lst(l, _) =>
                Some(VList(l.map(e => parseInput(e.toString))*))
              case _ => None
          case Left(_) => None
      }
      .getOrElse(input)
end Parser
