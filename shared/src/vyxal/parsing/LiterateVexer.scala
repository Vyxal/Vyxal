package vyxal.parsing

import vyxal.parsing.VTokenType.*
import vyxal.Elements
import vyxal.Modifier
import vyxal.Modifiers
import vyxal.UnopenedGroupException

import scala.collection.mutable.ArrayBuffer

class LiterateVexer extends VexerCommon:
  def headIsOpener: Boolean = ???

  def headIsBranch: Boolean = false // TODO: Implement
  def headIsCloser: Boolean = false // TODO: Implement
  private val literateKeywords = Elements.elements.values.flatMap(_.keywords)
  private val _tokens = ArrayBuffer[VLitToken]()
  private val groups = ArrayBuffer[ArrayBuffer[VLitToken]]()

  private val groupModifierToToken = Map(
    "." -> ((range) => VLitToken(MonadicModifier, "⸠", range)),
    ":" -> ((range) => VLitToken(DyadicModifier, "ϩ", range)),
    ":." -> ((range) => VLitToken(TriadicModifier, "э", range)),
    "::" -> ((range) => VLitToken(TetradicModifier, "Ч", range)),
    "," -> ((range) => VLitToken(MonadicModifier, "♳", range)),
    ";" -> ((range) => VLitToken(DyadicModifier, "♴", range)),
    ";," -> ((range) => VLitToken(TriadicModifier, "♵", range)),
    ";;" -> ((range) => VLitToken(TetradicModifier, "♶", range)),
  )

  private val endKeywords = List(
    "endfor",
    "end-for",
    "endwhile",
    "end-while",
    "endlambda",
    "end-lambda",
    "end",
  )

  private val branchKeywords = List(
    ":",
    "->",
    "else:",
    "else",
    "elif",
    "else-if",
    "body",
    "do",
    "branch",
    "then",
    "in",
    "using",
    "no?",
    "=>",
    "from",
    "as",
    "with",
    "given",
    ":and:",
    "has",
    "does",
    "using",
    "on",
  )

  /** Map keywords to their token types */
  private val keywords = Map(
    "close-all" -> VTokenType.StructureAllClose,
    "end-all" -> VTokenType.StructureAllClose,
    "end-end" -> VTokenType.StructureDoubleClose,
  )

  private val lambdaOpeners = Map(
    "lambda" -> VStructureType.Lambda,
    "lam" -> VStructureType.Lambda,
    "map-lambda" -> VStructureType.LambdaMap,
    "map-lam" -> VStructureType.LambdaMap,
    "map<" -> VStructureType.LambdaMap,
    "filter-lambda" -> VStructureType.LambdaFilter,
    "filter-lam" -> VStructureType.LambdaFilter,
    "filter<" -> VStructureType.LambdaFilter,
    "sort-lambda" -> VStructureType.LambdaSort,
    "sort-lam" -> VStructureType.LambdaSort,
    "sort<" -> VStructureType.LambdaSort,
    "reduce-lambda" -> VStructureType.LambdaReduce,
    "reduce-lam" -> VStructureType.LambdaReduce,
    "reduce<" -> VStructureType.LambdaReduce,
    "fold-lambda" -> VStructureType.LambdaReduce,
    "fold-lam" -> VStructureType.LambdaReduce,
    "fold<" -> VStructureType.LambdaReduce,
  )

  /** Keywords for opening structures. Has to be a separate map because while
    * all of them have the same [[TokenType]], they have different values
    * depending on the kind of structure
    */
  private val structOpeners = Map(
    // These can't go in the big map, because that's autogenerated
    "yes?" -> VStructureType.Ternary,
    "if" -> VStructureType.IfStatement,
    "for" -> VStructureType.For,
    "for<" -> VStructureType.For,
    "do-to-each" -> VStructureType.For,
    "each-as" -> VStructureType.For,
    "while" -> VStructureType.While,
    "while<" -> VStructureType.While,
    "exists<" -> VStructureType.DecisionStructure,
    "relation<" -> VStructureType.GeneratorStructure,
    "generate-from<" -> VStructureType.GeneratorStructure,
    "generate<" -> VStructureType.GeneratorStructure,
    "define" -> VStructureType.DefineStructure,
  )

  def lex(program: String): Seq[VToken] =
    programStack.pushAll(program.reverse.map(_.toString))
    while programStack.nonEmpty do
      if headIsDigit || headLookaheadMatch("-[1-9]") then numberToken
      else if safeCheck(c => c.head.isLetter || "<>?!*+\\-=&%:@".contains(c))
      then keywordToken
      else if headEqual("\"") then moveRightToken
      else if headEqual("(") then
        eat("(")
        groups += ArrayBuffer[VLitToken]()
        if headLookaheadMatch(":[.:]|;[,;]") then
          addToken(groupModifierToToken(pop(2))(VRange(index, index)))
        else if headIn(".:,;") then
          addToken(groupModifierToToken(pop(1))(VRange(index, index)))
      else if headEqual(")") then
        if groups.nonEmpty then
          val group = groups.last
          groups.dropRightInPlace(1)
          addToken(
            VLitToken(Group, group.toSeq, VRange(index, index))
          )
          eat(")")
        else throw new UnopenedGroupException(index)
      else if headEqual("{") then lambdaTokens
      else if headIsWhitespace then pop(1)
    end while
    // Flatten _tokens into tokens
    for token <- _tokens do
      if token.tokenType == Group then
        tokens ++= token.value.asInstanceOf[Seq[VLitToken]].map(_.toNormal)
      else tokens += token.toNormal
    tokens.toSeq
  end lex

  def addToken(token: VLitToken): Unit =
    if groups.nonEmpty then groups.last += token
    else _tokens += token

  def addToken(tokenType: VTokenType, value: String, range: VRange): Unit =
    addToken(VLitToken(tokenType, value, range))

  private def keywordToken: Unit =
    val start = index
    val keyword = StringBuilder()
    while safeCheck(c =>
        c.head.isLetterOrDigit || "_<>?!*+\\-=&%:'@".contains(c)
      )
    do keyword ++= pop(1)
    val value = removeDoubleNt(keyword.toString())
    if isKeyword(value) then
      addToken(
        VLitToken(
          Command,
          getSymbolFromKeyword(value),
          VRange(start, index),
        )
      )
    else if isKeyword(value.stripSuffix("n't")) then
      addToken(
        VLitToken(NegatedCommand, value, VRange(start, index))
      )
    else if isModifier(value) then
      val mod = getModifierFromKeyword(value)
      val name = Modifiers.modifiers.find(_._2._3.contains(value)).get._1
      val tokenType = mod.arity match
        case 1 => VTokenType.MonadicModifier
        case 2 => VTokenType.DyadicModifier
        case 3 => VTokenType.TriadicModifier
        case 4 => VTokenType.TetradicModifier
        case _ => VTokenType.SpecialModifier
      addToken(VLitToken(tokenType, name, VRange(start, index)))
    else if Set("lam", "lambda", "{", "λ").contains(value) then lambdaTokens
    else
      for char <- value do
        addToken(
          VLitToken(
            Command,
            char.toString,
            VRange(index, index + 1),
          )
        )
        index += 1
    end if
  end keywordToken

  private def removeDoubleNt(word: String): String =
    var temp = word
    while temp.endsWith("n'tn't") do temp = temp.stripSuffix("n'tn't")
    temp

  private def isKeyword(word: String): Boolean =
    literateKeywords.toSet.contains(word)

  private def isModifier(word: String): Boolean =
    Modifiers.modifiers.values.exists(_.keywords.contains(word))

  private def getSymbolFromKeyword(word: String): String =
    Elements.elements.values
      .find(elem => elem.keywords.contains(word))
      .get
      .symbol

  private def getModifierFromKeyword(word: String): Modifier =
    Modifiers.modifiers.values.find(mod => mod._3.contains(word)).get

  private def moveRightToken: Unit =
    val start = index
    val quotes = StringBuilder()
    while headEqual("\"") do quotes ++= pop(1)
    val value = quotes.toString()
    addToken(VLitToken(MoveRight, value, VRange(start, index)))

  /** Number = 0 | [1-9][0-9]*(\.[0-9]*)? | \.[0-9]* */
  private def numberToken: Unit =
    val rangeStart = index
    // Check the single zero case
    if headEqual("0") then
      val zeroToken = VToken(VTokenType.Number, "0", VRange(index, index))
      pop(1)
      tokens += zeroToken
      return
    val negativePrefix = if headEqual("-") then pop() else ""
    // Then the headless decimal case
    if headEqual(".") then
      pop(1)
      if safeCheck(c => c.head.isDigit) then
        val head = simpleNumber()
        val numberToken = VLitToken(
          VTokenType.Number,
          s"${negativePrefix}0.$head",
          VRange(rangeStart, index),
        )
        addToken(numberToken)
      else
        val zeroToken = VLitToken(
          VTokenType.Number,
          "0.5",
          VRange(rangeStart, index),
        )
        addToken(zeroToken)
    else
      // Not a 0, and not a headless decimal, so it's a normal number
      val head = simpleNumber()
      // Test for a decimal tail
      if headEqual(".") then
        pop(1)
        if safeCheck(c => c.head.isDigit) then
          val tail = simpleNumber()
          val numberToken = VLitToken(
            VTokenType.Number,
            s"$negativePrefix$head.$tail",
            VRange(rangeStart, index),
          )
          addToken(numberToken)
        else
          val numberToken = VLitToken(
            VTokenType.Number,
            s"$head.5",
            VRange(rangeStart, index),
          )
          addToken(numberToken)
      // No decimal tail, so normal number
      else
        val numberToken = VLitToken(
          VTokenType.Number,
          negativePrefix + head,
          VRange(rangeStart, index),
        )
        addToken(numberToken)
    end if
  end numberToken

  private def simpleNumber(): String =
    val numberVal = StringBuilder()
    while safeCheck(c => c.head.isDigit) do numberVal ++= s"${pop()}"
    numberVal.toString()

  private def lambdaTokens: Unit = ???

end LiterateVexer
