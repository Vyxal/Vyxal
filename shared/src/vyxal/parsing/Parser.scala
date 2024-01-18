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

  private def parseLiteTrees(trees: Queue[LiteTree]): AST =
    val asts = Stack.empty[AST]

    while trees.nonEmpty do
      trees.dequeue() match
        case LiteTree.Structure(opener, branches, range) => ???
        case LiteTree.Group(trees, range) => ???
        case LiteTree.LineLambda(modTok, body, range) => ???
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
            case TokenType.Newline => asts.push(AST.Newline)
            case TokenType.Command =>
              if customs.contains(value) &&
                customs(value).elementType == CustomElementType.Modifier
              then
                asts.push(
                  AST.JunkModifier(value, customs(value).args(0).length)
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

    ???
  end parseLiteTrees

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
          return AST.Command(cmd, None, None, checkCustoms, cmdTok.range)
        else element.arity.getOrElse(0)
    val nilads = ListBuffer[AST]()

    while asts.nonEmpty && nilads.sizeIs < arity &&
      asts.top.arity.fold(false)(_ == 0)
    do nilads += asts.pop()
    if nilads.isEmpty then
      return AST.Command(cmd, None, None, checkCustoms, cmdTok.range)
    AST.Group(
      (AST.Command(cmd, None, None, checkCustoms, cmdTok.range) ::
        nilads.toList).reverse,
      Some(arity - nilads.size),
    )
  end parseCommand

  def parse(tokens: List[Token]): AST =
    postprocess(parseLiteTrees(LiteParser.parse(tokens).to(Queue)))

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

enum ParsingException(msg: String) extends VyxalException(msg):
  case BadAugmentedAssignException()
      extends ParsingException("Missing element for augmented assign")
  case BadModifierException(modifier: String)
      extends ParsingException(
        s"Modifier '$modifier' is missing arguments"
      )
  case BadStructureException(structure: String)
      extends ParsingException(s"Invalid $structure statement")
  case ModifierArityException(modifier: String, arity: Option[Int])
      extends ParsingException(
        s"Modifier '$modifier' does not support elements of arity ${arity.getOrElse("None")}"
      )
  case NoSuchElementException(element: String)
      extends ParsingException(s"No such element: $element")
  case TokensFailedParsingException(tokens: List[Token])
      extends ParsingException(s"Some elements failed to parse: $tokens")
  case UnmatchedCloserException(closer: Token)
      extends ParsingException(
        s"A closer/branch was found outside of a structure: ${closer.value}"
      )
  case UndefinedCustomModifierException(modifier: String)
      extends ParsingException(s"Custom modifier '$modifier' not defined")

  case UndefinedCustomElementException(element: String)
      extends ParsingException(s"Custom element '$element' not defined")

  case CustomModifierActuallyElementException(modifier: String)
      extends ParsingException(
        s"Custom modifier '$modifier' is actually a custom element"
      )

  case CustomElementActuallyModifierException(element: String)
      extends ParsingException(
        s"Custom element '$element' is actually a custom modifier"
      )

  case EmptyRedefine()
      extends ParsingException(
        "Redefine statement is empty. Requires at least name and implementation."
      )

  case BadRedefineMode(mode: String)
      extends ParsingException(
        s"Invalid redefine mode: '$mode'. Should either be @ for element, or * for modifier"
      )
end ParsingException
