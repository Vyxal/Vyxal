package vyxal

import scala.language.strictEquality

import vyxal.parsing.{StructureType, Token, TokenType}
import vyxal.Parser.reservedTypes

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Queue, Stack}

enum CustomElementType derives CanEqual:
  case Element
  case Modifier

enum Visibility derives CanEqual:
  case Public
  case Private
  case Restricted
case class CustomDefinition(
    name: String,
    elementType: CustomElementType,
    impl: Option[AST],
    arity: Option[Int],
    args: (List[String | Int], List[String | Int]),
)

case class CustomClass(
    fields: Map[String, (Visibility, Option[AST])]
)

case class ParserResult(
    ast: AST,
    customs: Map[String, CustomDefinition],
    classes: Map[String, CustomClass],
    typedCustoms: Map[String, (List[(List[String], CustomDefinition)], Int)],
)

object Parser:
  @throws[VyxalParsingException]
  def parse(tokens: List[Token]): ParserResult =
    val parser = Parser()
    val ast = parser.parse(tokens)
    ParserResult(
      ast,
      parser.customs.toMap,
      parser.classes.toMap,
      parser.typedCustoms.toMap,
    )

  val reservedTypes = List("num", "str", "lst", "fun", "con")

private class Parser:
  /** Custom definitions found so far */
  private val customs = mutable.Map[String, CustomDefinition]()
  private val classes = mutable.Map[String, CustomClass]()
  private val typedCustoms =
    mutable.Map[String, (List[(List[String], CustomDefinition)], Int)]()

  private def flatten(ast: AST): List[AST] =
    ast match
      case AST.Group(elems, _, _) => elems.flatMap(flatten)
      case _ => List(ast)

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
      topLevel: Boolean = false,
  ): AST =
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
        case TokenType.Number => asts.push(AST.Number(VNum(value), range))
        case TokenType.Str => asts.push(AST.Str(value, range))
        case TokenType.DictionaryString =>
          asts.push(AST.DictionaryString(value, range))
        case TokenType.CompressedString =>
          asts.push(AST.CompressedString(value, range))
        case TokenType.CompressedNumber =>
          asts.push(AST.CompressedNumber(value, range))
        case TokenType.Newline => asts.push(AST.Newline)
        case TokenType.StructureOpen =>
          asts.push(
            parseStructure(
              StructureType.values.find(_.open == value).get,
              program,
            )
          )
          if topLevel && program.nonEmpty &&
            program.front.tokenType == TokenType.StructureAllClose
          then program.dequeue()
        /*
         * List are just structures with two different opening and closing
         * token possibilities, so handle them the same way.
         */
        case TokenType.ListOpen => asts.push(
            AST.Lst((parseBranches(program, true)(_ == TokenType.ListClose)))
          )

        /*
         * Now comes command handling. When handling commands, we need to
         * perform the arity grouping when pushing the command. This is done
         * by checking the arity of the command, and then checking however many
         * top stack elements are needed to make the command equivalent to a
         * nilad. For arities -1 and 0, the command doesn't need to be grouped.
         */
        case TokenType.Command =>
          if customs.contains(value) &&
            customs(value).elementType == CustomElementType.Modifier
          then asts.push(AST.JunkModifier(value, customs(value).args(0).length))
          else asts.push(parseCommand(token, asts, program))

        case TokenType.OriginalSymbol =>
          asts.push(parseCommand(token, asts, program, false))

        case TokenType.NegatedCommand =>
          asts.push(parseCommand(token, asts, program, false))
          asts.push(AST.Command("¬"))

        // At this stage, modifiers aren't explicitly handled, so just push a
        // temporary AST to comply with the type of the AST stack
        case TokenType.ElementSymbol =>
          val name = value
          if typedCustoms.contains(name) then
            asts.push(
              parseCommand(
                Token(TokenType.Command, s"##$name", range),
                asts,
                program,
              )
            )
          else
            if !customs.contains(name) then
              throw UndefinedCustomElementException(name)
            val CustomDefinition(_, elementType, impl, arity, args) =
              customs(name)
            elementType match
              case CustomElementType.Element => asts.push(
                  parseCommand(
                    Token(TokenType.Command, s"##$name", range),
                    asts,
                    program,
                  )
                )
              case CustomElementType.Modifier =>
                throw CustomElementActuallyModifierException(name)
        case TokenType.ModifierSymbol =>
          val name = value
          if !customs.contains(name) then
            throw UndefinedCustomModifierException(name)
          val CustomDefinition(_, elementType, impl, arity, args) =
            customs(name)
          elementType match
            case CustomElementType.Element =>
              throw CustomModifierActuallyElementException(name)
            case CustomElementType.Modifier =>
              asts.push(AST.JunkModifier(name, args(0).length))
        case TokenType.MonadicModifier => asts.push(AST.JunkModifier(value, 1))
        case TokenType.DyadicModifier => asts.push(AST.JunkModifier(value, 2))
        case TokenType.TriadicModifier => asts.push(AST.JunkModifier(value, 3))
        case TokenType.TetradicModifier => asts.push(AST.JunkModifier(value, 4))
        case TokenType.SpecialModifier => asts.push(AST.SpecialModifier(value))
        case TokenType.Comment => ()
        case TokenType.DefineRecord =>
          val branches =
            parseBranches(program, true)(_ == TokenType.StructureClose)
          val className = value
          if reservedTypes.contains(className) then
            throw ReservedClassNameException(className)
          branches match
            case List() => classes(value) = CustomClass(Map())
            case _ :: fields :: _ =>
              val flat = flatten(fields)
              val variables = flat.zipWithIndex.filter(
                _(0) match
                  case _: AST.SetVar => true
                  case _: AST.GetVar => true
                  case _: AST.SetConstant => true
                  case _ => false
              )

              val valuedFields = variables.map((variableAST, index) =>
                val name = variableAST match
                  case AST.SetVar(name, _) => name
                  case AST.GetVar(name, _) => name
                  case AST.SetConstant(name, _) => name
                  case _ => throw VyxalYikesException(
                      "Somehow received non-variable AST after filtering. Ping lyxal about this please."
                    )
                val visibility = variableAST match
                  case _: AST.SetVar => Visibility.Private
                  case _: AST.GetVar => Visibility.Restricted
                  case _: AST.SetConstant => Visibility.Public
                  case _ => throw VyxalYikesException(
                      "Somehow received non-variable AST after filtering. Ping lyxal about this please."
                    )

                val value =
                  if index == 0 then None
                  else
                    val prev = flat(index - 1)
                    prev match
                      case _: AST.SetVar => None
                      case _: AST.GetVar => None
                      case _: AST.SetConstant => None
                      case _ => prev match
                          case _: AST.Lambda => Some(prev)
                          case _ =>
                            Some(AST.Lambda(Some(0), List(), List(prev)))

                name -> (visibility, value)
              )

              classes(className) = CustomClass(valuedFields.toMap)

            case _ => throw BadStructureException("class definition")
          end match
        case TokenType.DefineExtension =>
          val branches =
            parseBranches(program, false)(_ == TokenType.StructureClose)
          if branches.size < 3 then throw BadStructureException("extension")
          var symbol = branches.head.toVyxal
          if symbol.length() > 1 then symbol = toValidName(symbol)

          val arguments = branches.tail.init
          if arguments.size % 2 != 0 then
            throw BadStructureException("extension")
          val argPairs = arguments.grouped(2).toList.transpose
          val argNames = argPairs.head.map {
            case p: AST.Parameter => p.name
            case a => toValidName(a.toVyxal)
          }

          val impl = branches.last match
            case lam: AST.Lambda => lam
            case other =>
              AST.Lambda(Some(argNames.length), argNames, List(other))
          val argTypes = argPairs.last.map {
            case p: AST.Parameter => p.name
            case a =>
              val temp = a.toVyxal
              if temp == "*" then "*" else toValidName(temp)
          }
          if typedCustoms.contains(symbol) then
            val temp = typedCustoms(symbol)(0)
            typedCustoms(symbol) =
              (temp :+
                (argTypes ->
                  CustomDefinition(
                    symbol,
                    CustomElementType.Element,
                    Some(impl),
                    Some(argNames.length),
                    (List() -> argNames),
                  ))) -> Math.max(argNames.length, typedCustoms(symbol)._2)
          else
            typedCustoms(symbol) = List(
              argTypes ->
                CustomDefinition(
                  symbol,
                  CustomElementType.Element,
                  Some(impl),
                  Some(argNames.length),
                  (List() -> argNames),
                )
            ) -> argNames.length

        case TokenType.ContextIndex => asts.push(
            AST.ContextIndex(if value.nonEmpty then value.toInt else -1)
          )
        case TokenType.FunctionCall =>
          val funcName = value
          asts.push(AST.GetVar(funcName, range))
          asts.push(AST.Command("Ė"))
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
        case TokenType.Param => asts.push(AST.Parameter(value))
        case TokenType.Digraph =>
          if !value.startsWith("k") then throw NoSuchElementException(token)
          else asts.push(AST.Command(value))
      end match
    end while
    // Second stage parsing
    val finalAsts = parse(asts)
    AST.makeSingle(finalAsts.toList*)
  end parse

  private def parse(asts: Stack[AST]): Stack[AST] =
    val finalAsts = Stack[AST]()
    while asts.nonEmpty do
      val topAst = asts.pop()
      topAst match
        case AST.Newline => ()
        case AST.JunkModifier(name, arity) => if arity > 0 then
            if finalAsts.length < arity then throw BadModifierException(name)
            if customs.contains(name) then
              // First, get the ASTs for the custom modifier
              val CustomDefinition(_, _, impl, arity, args) = customs(name)
              val modifierArgs =
                List.fill(args(0).length)(finalAsts.pop()).map { ast =>
                  AST.Lambda(ast.arity, List(), List(ast))
                }

              // Then, create a lambda that wraps the implementation
              // in a context where it recieves both function arguments, and
              // stack arguments

              val wrapped = AST.Lambda(
                Some(-1),
                List(),
                modifierArgs :+
                  (
                    impl.getOrElse(throw UndefinedCustomModifierException(name))
                  ),
              )

              // Finally, push the wrapped lambda to the stack

              finalAsts.push(AST.makeSingle(wrapped, AST.Command("Ė")))
            else
              val modifier = Modifiers.modifiers.getOrElse(
                name,
                throw UndefinedCustomModifierException(name),
              )
              val modifierArgs = List.fill(arity)(finalAsts.pop())
              finalAsts.push(modifier.from(modifierArgs))
        case AST.SpecialModifier(name, _) => (name: @unchecked) match
            case "ᵜ" =>
              val lambdaAsts = Stack[AST]()
              while asts.nonEmpty && asts.top != AST.Newline do
                lambdaAsts.push(asts.pop())
              finalAsts.push(
                AST.Lambda(
                  Some(1),
                  List(),
                  List(AST.makeSingle(parse(lambdaAsts.reverse).toList*)),
                )
              )
        case AST.AuxAugmentVar(name, _) =>
          if asts.isEmpty then throw BadAugmentedAssignException()
          finalAsts.push(AST.AugmentVar(name, asts.pop()))
        case _ => finalAsts.push(topAst)
      end match
    end while
    finalAsts
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
      checkCustoms: Boolean = true,
  ): AST =
    val cmd =
      if checkCustoms && cmdTok.value.startsWith("##")
      then cmdTok.value.stripPrefix("##")
      else if !Elements.elements.contains(cmdTok.value) then
        Elements.symbolFor(cmdTok.value).getOrElse(cmdTok.value)
      else cmdTok.value

    val arity = Elements.elements.get(cmd) match
      case None =>
        if checkCustoms then
          if typedCustoms.contains(cmd) then typedCustoms(cmd)._2
          else if !customs.contains(cmd) then
            if !cmd.startsWith("k") then throw NoSuchElementException(cmdTok)
            else 0
          else
            val CustomDefinition(_, _, _, arity, _) = customs(cmd)
            arity.getOrElse(1)
        else if cmd.startsWith("k") then 0
        else throw NoSuchElementException(cmdTok)
      case Some(element) =>
        if asts.isEmpty then return AST.Command(cmd, cmdTok.range, checkCustoms)
        else element.arity.getOrElse(0)
    val nilads = ListBuffer[AST]()

    while asts.nonEmpty && nilads.sizeIs < arity &&
      asts.top.arity.fold(false)(_ == 0)
    do nilads += asts.pop()
    if nilads.isEmpty then return AST.Command(cmd, cmdTok.range, checkCustoms)
    AST.Group(
      (AST.Command(cmd, cmdTok.range, checkCustoms) :: nilads.toList).reverse,
      Some(arity - nilads.size),
    )
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
  ): List[AST] =
    if program.isEmpty then return List(AST.makeSingle())
    val branches = ListBuffer.empty[AST]

    while program.nonEmpty &&
      (!isCloser(program.front) || program.front.tokenType == TokenType.Branch)
    do
      branches += parse(program)
      if program.nonEmpty && program.front.tokenType == TokenType.Branch
      then
        // Get rid of the `|` token to move on to the next branch
        program.dequeue()
        if program.isEmpty ||
          (isCloser(program.front) &&
            program.front.tokenType != TokenType.Branch)
        then
          // Don't forget empty branches at the end
          branches += AST.makeSingle()

    if branches.isEmpty && !canBeEmpty then branches += AST.makeSingle()

    if program.nonEmpty && isEnd(program.front.tokenType) &&
      program.front.tokenType != TokenType.StructureAllClose
    then
      // Get rid of the structure/list closer
      program.dequeue()

    branches.toList
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
      program: Queue[Token],
  ): AST =
    val branches = parseBranches(program, false) {
      case TokenType.StructureAllClose | TokenType.StructureClose |
          TokenType.StructureDoubleClose => true
      case _ => false
    }
    // Now, we can create the appropriate AST for the structure
    structureType match
      case StructureType.Ternary => branches match
          case List(thenBranch) => AST.Ternary(thenBranch, None)
          case List(thenBranch, elseBranch) =>
            AST.Ternary(thenBranch, Some(elseBranch))
          case _ => throw BadStructureException("if")
      case StructureType.IfStatement =>
        if branches.sizeIs < 2 then throw BadStructureException("if")
        else
          val odd = branches.size % 2 == 1
          val grouped =
            if odd then branches.init.grouped(2).toList
            else branches.grouped(2).toList
          AST.IfStatement(
            grouped.map(_(0)),
            grouped.map(_(1)),
            Option.when(odd)(branches.last),
          )
      case StructureType.While => branches match
          case List(cond, body) => AST.While(Some(cond), body)
          case List(body) => AST.While(None, body)
          case _ => throw BadStructureException("while")
      case StructureType.For => branches match
          case List(name, body) =>
            AST.For(Some(toValidName(name.toVyxal)), body)
          case List(body) => AST.For(None, body)
          case _ => throw BadStructureException("for")
      case StructureType.DefineStructure =>
        // Name: The name of the element/modifier
        // Mode: Whether it's an element or modifier
        // Implementation: The implementation of the element/modifier
        // Arity: How many arguments the element/modifier takes
        // Args: The names of the arguments given to the implementation

        val (name, functions, args, impl) = branches match
          case List() => throw EmptyRedefine()
          case List(name, impl) => (name, (List() -> 0), (List() -> 0), impl)
          case List(name, args, impl) =>
            (name, (List() -> 0), parseParameters(args), impl)
          case List(name, functions, args, impl) =>
            (name, parseParameters(functions), parseParameters(args), impl)
          case _ => throw BadStructureException("define")

        val nameString = name.toVyxal
        val actualName =
          if nameString.length() == 2 then nameString(1).toString()
          else toValidName(nameString)
        val mode = nameString.headOption match
          case Some('@') => CustomElementType.Element
          case Some('*') => CustomElementType.Modifier
          case _ => throw BadRedefineMode(
              nameString.headOption.getOrElse("").toString()
            )

        val arity = mode match
          case CustomElementType.Element => args(1)
          case CustomElementType.Modifier =>
            if args(1) == -1 then -1
            else args(1) + functions(1)

        val actualImpl = mode match
          case CustomElementType.Element =>
            if impl.isInstanceOf[AST.Lambda] then impl
            else AST.Lambda(Some(arity), args(0), List(impl))
          case CustomElementType.Modifier => AST.makeSingle(
              if impl.isInstanceOf[AST.Lambda] then impl
              else AST.Lambda(Some(arity), functions(0) ++ args(0), List(impl)),
              AST.Command("Ė"),
            )

        customs(actualName) = CustomDefinition(
          actualName,
          mode,
          Some(actualImpl),
          Some(arity),
          (functions(0) -> args(0)),
        )

        AST.Group(List(), None)

      case lambdaType @ (StructureType.Lambda | StructureType.LambdaMap |
          StructureType.LambdaFilter | StructureType.LambdaReduce |
          StructureType.LambdaSort) =>
        val lambda =
          if lambdaType == StructureType.Lambda then
            branches match
              case List() => AST.Lambda(None, List.empty, List.empty)
              case List(body) => AST.Lambda(None, List.empty, List(body))
              case List(params, body) =>
                val (param, arity) = parseParameters(params)
                AST.Lambda(Some(arity), param, List(body))
              case _ =>
                val (param, arity) = parseParameters(branches.head)
                AST.Lambda(Some(arity), param, branches.drop(1))
          else AST.Lambda(None, List.empty, branches)

        lambdaType match
          case StructureType.Lambda => lambda
          case StructureType.LambdaMap =>
            AST.makeSingle(lambda, AST.Command("M"))
          case StructureType.LambdaFilter =>
            AST.makeSingle(lambda, AST.Command("F"))
          case StructureType.LambdaReduce =>
            AST.makeSingle(lambda, AST.Command("R"))
          case StructureType.LambdaSort =>
            AST.makeSingle(lambda, AST.Command("ṡ"))
      case StructureType.DecisionStructure => branches match
          case List(pred, container) =>
            AST.DecisionStructure(pred, Some(container))
          case List(pred) => AST.DecisionStructure(pred, None)
          case _ => throw BadStructureException("decision")
      case StructureType.GeneratorStructure =>
        if branches.sizeIs > 2 then throw BadStructureException("generator")
        else
          var rel = branches.head
          val vals = (branches: @unchecked) match
            case List(_, initial) => Some(initial)
            case List(_) => None

          val arity = rel match
            case AST.Group(elems, _, _) =>
              if elems.isEmpty then throw BadStructureException("generator")
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
                      stackItems -= elemArity +
                        1 // assume everything only returns one value
                    else
                      popped += elemArity - stackItems
                      stackItems = 1
                  popped
              end match
            case _ => rel.arity.getOrElse(2)

          AST.GeneratorStructure(rel, vals, arity)
    end match
  end parseStructure

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

  def parse(tokens: List[Token]): AST =
    val preprocessed = preprocess(tokens).to(Queue)
    val parsed = parse(preprocessed, true)
    if preprocessed.nonEmpty then
      if isCloser(preprocessed.front) then
        throw UnmatchedCloserException(preprocessed.dequeue())
      // Some tokens were left at the end, which should never happen
      throw TokensFailedParsingException(preprocessed.toList)
    else postprocess(parsed)

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
        case Token(TokenType.UnpackTrigraph, "#:[", _) =>
          val contents = mutable.StringBuilder()
          var depth = 1
          while depth != 0 do
            val top = lineup.dequeue()
            top match
              case Token(TokenType.UnpackTrigraph, "#:[", _) => depth += 1
              case Token(TokenType.UnpackVar, _, _) => depth += 1
              case Token(TokenType.StructureOpen, open, _) =>
                if open == StructureType.Ternary.open then depth += 1
              case Token(TokenType.UnpackClose, _, _) => depth -= 1
              case Token(TokenType.StructureAllClose, _, _) => depth -= 1
              case _ =>
            contents.++=(top.value)
          processed +=
            Token(TokenType.UnpackVar, contents.toString(), temp.range)
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
end Parser
