package vyxal.parsing

import scala.language.strictEquality

import vyxal.*

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Queue, Stack}

import Parser.reservedTypes
import ParsingException.*

case class ParserResult(
    ast: AST,
    customs: Map[String, CustomDefinition],
    classes: Map[String, CustomClass],
    typedCustoms: Map[String, (List[(List[String], CustomDefinition)], Int)],
)

object Parser:
  @throws[ParsingException]
  def parse(tokens: List[Token]): ParserResult =
    val LiteParser.Result(trees, extensions) = LiteParser.parse(tokens)
    val parser = Parser(extensions)
    val ast = parser.parse(trees)
    ParserResult(
      ast,
      parser.customs.toMap,
      parser.classes.toMap,
      parser.typedCustoms.toMap,
    )

  val reservedTypes = List("num", "str", "lst", "fun", "con")

/** @param extensionsRaw The extensions found by LiteParser */
private class Parser(val extensionsRaw: List[(List[LiteTree], Range)]):
  /** Custom definitions found so far */
  private val customs = mutable.Map[String, CustomDefinition]()
  private val classes = mutable.Map[String, CustomClass]()
  private val typedCustoms =
    mutable.Map[String, (List[(List[String], CustomDefinition)], Int)]()

  /** The parser takes a list of tokens and performs two sweeps of parsing:
    * structures + arity grouping and then modifiers. The first sweep deals
    * directly with the token list provided to the parser, and leaves its
    * results in a stack of ASTs (the stack data type is used because it means
    * that arity grouping is simply popping previous ASTs until a niladic state
    * is reached). The second sweep takes the stack of ASTs and applies the
    * logic of modifier grouping, placing its result in a single Group AST.
    */
  def parse(trees: List[LiteTree]): AST =
    postprocess(parseLiteTrees(trees.to(Queue)))

  private def parseLiteTrees(trees: Queue[LiteTree]): AST =
    val asts = Stack.empty[AST]

    println(s"Parsing lite trees: $trees")

    while trees.nonEmpty do
      trees.dequeue() match
        case LiteTree.Structure(opener, nonExprs, branches, range) =>
          (opener.tokenType: @unchecked) match
            case TokenType.ListOpen => asts.push(
                AST.Lst(branches.map(branch => parse(List(branch))), range)
              )
            case TokenType.StructureOpen =>
              asts.push(parseStructure(opener, nonExprs, branches, range))
            case TokenType.DefineRecord =>
              val className = opener.value
              if reservedTypes.contains(className) then
                throw ReservedClassNameException(className)
              branches match
                case List() => classes(className) = CustomClass(Map())
                case _ :: LiteTree.Group(body, _) :: _ =>
                  val fields =
                    mutable.Map.empty[String, (Visibility, Option[AST])]
                  val prev = ListBuffer.empty[LiteTree]

                  def makeValue(): Option[AST] =
                    if prev.nonEmpty then
                      val trees = prev.to(Queue)
                      prev.clear()
                      parseLiteTrees(trees) match
                        case lam: AST.Lambda => Some(lam)
                        case ast => Some(AST.Lambda(Some(0), List(), List(ast)))
                    else None

                  for tree <- body do
                    tree match
                      case LiteTree.Tok(Token(TokenType.SetVar, name, _), _) =>
                        fields.put(name, (Visibility.Private -> makeValue()))
                      case LiteTree.Tok(Token(TokenType.GetVar, name, _), _) =>
                        fields.put(name, (Visibility.Restricted -> makeValue()))
                      case LiteTree
                            .Tok(Token(TokenType.Constant, name, _), _) =>
                        fields.put(name, (Visibility.Public -> makeValue()))
                      case _ => prev += tree

                  classes(className) = CustomClass(fields.toMap)
                case _ => throw BadStructureException("record", range = range)
              end match
        case LiteTree.Group(trees, range) =>
          asts.push(parseLiteTrees(trees.to(Queue)))
        case LiteTree.LineLambda(modTok, body, range) => asts.push(
            AST.Lambda(
              Some(1),
              List(),
              List(parse(body)),
              range = range,
            )
          )
        case LiteTree.Tok(token, range) =>
          val value = token.value
          token.tokenType match
            case TokenType.Number => asts.push(AST.Number(VNum(value), range))
            case TokenType.Str => asts.push(AST.Str(value, range))
            case TokenType.DictionaryString =>
              asts.push(AST.DictionaryString(value, range))
            case TokenType.CompressedString =>
              asts.push(AST.CompressedString(value, range))
            case TokenType.CompressedNumber =>
              asts.push(AST.CompressedNumber(value, range))
            case TokenType.Command =>
              if customs.contains(value) &&
                customs(value).elementType == CustomElementType.Modifier
              then
                asts.push(
                  parseModifier(value, customs(value).args(0).length, asts)
                )
              else asts.push(parseCommand(token, asts))
            case TokenType.OriginalSymbol =>
              asts.push(parseCommand(token, asts, checkCustoms = false))
            case TokenType.NegatedCommand =>
              asts.push(parseCommand(token, asts, checkCustoms = false))
              asts.push(AST.builtin("¬"))
            case TokenType.ElementSymbol =>
              val name = value
              if typedCustoms.contains(name) then
                asts.push(
                  parseCommand(
                    Token(TokenType.Command, s"##$name", range),
                    asts,
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
                  asts.push(parseModifier(name, args(0).length, asts))
            case TokenType.MonadicModifier =>
              asts.push(parseModifier(value, 1, asts))
            case TokenType.DyadicModifier =>
              asts.push(parseModifier(value, 2, asts))
            case TokenType.TriadicModifier =>
              asts.push(parseModifier(value, 3, asts))
            case TokenType.TetradicModifier =>
              asts.push(parseModifier(value, 4, asts))
            case TokenType.SpecialModifier => // ᵜ is implemented in LiteParser so there's currently nothing to do here
            case TokenType.ContextIndex => asts.push(
                AST.ContextIndex(if value.nonEmpty then value.toInt else -1)
              )
            case TokenType.FunctionCall =>
              val funcName = value
              asts.push(AST.GetVar(funcName, range))
              asts.push(AST.builtin("Ė"))
            case TokenType.GetVar => asts.push(AST.GetVar(value, range))
            case TokenType.SetVar => asts.push(AST.SetVar(value, range))
            case TokenType.Constant => asts.push(AST.SetConstant(value, range))
            case TokenType.AugmentVar =>
              if asts.isEmpty then throw BadAugmentedAssignException()
              asts.push(AST.AugmentVar(value, asts.pop()))
            case TokenType.UnpackVar =>
              val names = ListBuffer[(String, Int)]()
              val name = StringBuilder()
              var depth = 0
              val nameQueue = Queue[String](value.split("").toList*)
              while nameQueue.nonEmpty && depth != -1 do
                val top = nameQueue.dequeue()
                (top: @unchecked) match
                  case "[" =>
                    if name.nonEmpty then names += ((name.toString, depth))
                    name.clear()
                    depth += 1
                  case "]" =>
                    if name.nonEmpty then names += ((name.toString, depth))
                    name.clear()
                    depth -= 1
                  case "|" =>
                    if name.nonEmpty then names += ((name.toString, depth))
                    name.clear()
                  case _ => name ++= top
              if depth != -1 then names += ((name.toString, depth))
              asts.push(AST.UnpackVar(names.toList))
            case TokenType.Param => asts.push(AST.Parameter(value))
            case TokenType.Digraph =>
              if !value.startsWith("k") then
                throw NoSuchElementException(token.value)
              else asts.push(AST.builtin(value))
            case TokenType.StructureOpen | TokenType.StructureClose |
                TokenType.StructureDoubleClose | TokenType.StructureAllClose |
                TokenType.ListOpen | TokenType.ListClose |
                TokenType.UnpackTrigraph | TokenType.UnpackClose |
                TokenType.DefineRecord | TokenType.DefineExtension |
                TokenType.Comment | TokenType.Branch | TokenType.Group |
                TokenType.GroupType | TokenType.MoveRight => ()
          end match
    end while

    println(s"asts: $asts")
    AST.makeSingle(asts.toSeq.reverse*)
  end parseLiteTrees

  private def parseStructure(
      opener: Token,
      nonExprs: List[Token],
      liteBranches: List[LiteTree],
      range: Range,
  ): AST =
    val branches = liteBranches.map(branch => parse(List(branch)))
    StructureType.values.find(_.open == opener.value).get match
      case StructureType.Ternary => branches match
          case List(thenBranch) => AST.Ternary(thenBranch, None)
          case List(thenBranch, elseBranch) =>
            AST.Ternary(thenBranch, Some(elseBranch))
          case _ => throw BadStructureException("ternary", range = range)
      case StructureType.IfStatement =>
        if branches.sizeIs < 2 then
          throw BadStructureException("if", range = range)
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
          case _ => throw BadStructureException("while", range = range)
      case StructureType.For => branches match
          case List(body) =>
            val name =
              if nonExprs.isEmpty then None
              else Some(toValidName(nonExprs.head.value))
            AST.For(name, body, range = range)
          case _ => throw BadStructureException("for", range = range)
      case StructureType.DefineStructure =>
        // Name: The name of the element/modifier
        // Mode: Whether it's an element or modifier
        // Implementation: The implementation of the element/modifier
        // Arity: How many arguments the element/modifier takes
        // Args: The names of the arguments given to the implementation

        val (nameTok, functions, args) = (nonExprs: @unchecked) match
          case List() => throw EmptyRedefine(range = range)
          case List(name) => (name, (Nil, 0), (Nil, 0))
          case List(name, args) => (name, (Nil, 0), parseParameters(args.value))
          case List(name, functions, args) => (
              name,
              parseParameters(functions.value),
              parseParameters(args.value),
            )

        val impl = branches match
          case List() => throw EmptyRedefine()
          case List(impl) => impl
          case _ => throw BadStructureException("define")

        val name = nameTok.value
        val actualName =
          if name.length() == 2 then name(1).toString()
          else toValidName(name)
        val mode = name.headOption match
          case Some('@') => CustomElementType.Element
          case Some('*') => CustomElementType.Modifier
          case _ => throw BadRedefineMode(name.substring(0, 1))

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
        val lambda = nonExprs match
          case List(params) =>
            val (param, arity) = parseParameters(params.value)
            AST.Lambda(Some(arity), param, branches)
          case _ => AST.Lambda(None, List.empty, branches)

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
          case _ => throw BadStructureException("decision", range = range)
      case StructureType.GeneratorStructure =>
        if branches.sizeIs > 2 then
          throw BadStructureException("generator", range = range)
        else
          var rel = branches.head
          val vals = (branches: @unchecked) match
            case List(_, initial) => Some(initial)
            case List(_) => None

          val arity = rel match
            case AST.Group(elems, _, _) =>
              if elems.isEmpty then
                throw BadStructureException("generator", range = range)
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

  private def parseModifier(name: String, arity: Int, asts: Stack[AST]): AST =
    if asts.length < arity then throw BadModifierException(name)

    if customs.contains(name) then
      // First, get the ASTs for the custom modifier
      val CustomDefinition(_, _, impl, arity, args) = customs(name)
      val modifierArgs = List.fill(args(0).length)(asts.pop()).map { ast =>
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
      AST.makeSingle(wrapped, AST.builtin("Ė"))
    else
      val modifier = Modifiers.modifiers.getOrElse(
        name,
        throw UndefinedCustomModifierException(name),
      )
      val modifierArgs = List.fill(arity)(asts.pop())
      modifier.from(modifierArgs)
  end parseModifier

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
            if !cmd.startsWith("k") then
              throw NoSuchElementException(cmdTok.value)
            else 0
          else
            val CustomDefinition(_, _, _, arity, _) = customs(cmd)
            arity.getOrElse(1)
        else if cmd.startsWith("k") then 0
        else throw NoSuchElementException(cmdTok.value)
      case Some(element) =>
        if asts.isEmpty then
          return AST.Command(cmd, None, checkCustoms, cmdTok.range)
        else element.arity.getOrElse(0)
    val nilads = ListBuffer[AST]()

    while asts.nonEmpty && nilads.sizeIs < arity &&
      asts.top.arity.fold(false)(_ == 0)
    do nilads += asts.pop()
    if nilads.isEmpty then
      return AST.Command(cmd, None, checkCustoms, cmdTok.range)
    AST.Group(
      (AST.Command(cmd, None, checkCustoms, cmdTok.range) ::
        nilads.toList).reverse,
      Some(arity - nilads.size),
    )
  end parseCommand

  private def parseParameters(params: String): (List[String | Int], Int) =
    val components = params.split(",")
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

  private def toValidName(name: String): String =
    name
      .filter(c => c.isLetterOrDigit || c == 'ı')
      .replace("ı", "i")
      .dropWhile(!_.isLetter)

  private def postprocess(asts: AST): AST =
    asts match
      case AST.Group(elems, _, range) =>
        val nilads = elems.reverse.takeWhile(isNilad).reverse
        val rest = elems.dropRight(nilads.length)
        AST.Group(nilads ++ rest, None, range = range)
      case _ => asts

  private def isNilad(ast: AST) =
    ast match
      case AST.GetVar(_, _) => false // you might want a variable at the end
      // after doing stuff like augmented assignment
      case _ => ast.arity.contains(0)
end Parser

enum ParsingException(msg: String) extends VyxalException(msg):
  case BadAugmentedAssignException(override val range: Range = Range.fake)
      extends ParsingException("Missing element for augmented assign")
  case BadModifierException(
      modifier: String,
      override val range: Range = Range.fake,
  ) extends ParsingException(
        s"Modifier '$modifier' is missing arguments"
      )
  case BadStructureException(
      structure: String,
      override val range: Range = Range.fake,
  ) extends ParsingException(s"Invalid $structure statement")
  case ModifierArityException(
      modifier: String,
      arity: Option[Int],
      override val range: Range = Range.fake,
  ) extends ParsingException(
        s"Modifier '$modifier' does not support elements of arity ${arity.getOrElse("None")}"
      )
  case NoSuchElementException(
      element: String,
      override val range: Range = Range.fake,
  ) extends ParsingException(s"No such element: $element")
  case TokensFailedParsingException(
      tokens: List[Token],
      override val range: Range = Range.fake,
  ) extends ParsingException(s"Some elements failed to parse: $tokens")
  case UnmatchedCloserException(
      closer: Token,
      override val range: Range = Range.fake,
  ) extends ParsingException(
        s"A closer/branch was found outside of a structure: ${closer.value}"
      )
  case UndefinedCustomModifierException(
      modifier: String,
      override val range: Range = Range.fake,
  ) extends ParsingException(s"Custom modifier '$modifier' not defined")

  case UndefinedCustomElementException(
      element: String,
      override val range: Range = Range.fake,
  ) extends ParsingException(s"Custom element '$element' not defined")

  case CustomModifierActuallyElementException(
      modifier: String,
      override val range: Range = Range.fake,
  ) extends ParsingException(
        s"Custom modifier '$modifier' is actually a custom element"
      )

  case CustomElementActuallyModifierException(
      element: String,
      override val range: Range = Range.fake,
  ) extends ParsingException(
        s"Custom element '$element' is actually a custom modifier"
      )

  case EmptyRedefine(override val range: Range = Range.fake)
      extends ParsingException(
        "Redefine statement is empty. Requires at least name and implementation."
      )

  case BadRedefineMode(mode: String, override val range: Range = Range.fake)
      extends ParsingException(
        s"Invalid redefine mode: '$mode'. Should either be @ for element, or * for modifier"
      )

  def range: Range
end ParsingException
