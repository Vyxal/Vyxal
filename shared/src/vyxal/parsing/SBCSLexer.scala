package vyxal.parsing

import vyxal.elements.Modifiers
import vyxal.VyxalException

class SBCSLexer extends LexerCommon:

  private val NEWLINE = "\n"
  private val DECIMAL_SEPARATOR = "."
  private val SINGLE_QUOTE = "'"
  private val DOUBLE_QUOTES = "\""
  private val TWO_CHAR_STRING = "Ꮬ"
  private val TWO_CHAR_NUMBER = "Ꮠ"
  private val DIGRAPH_CHARS = "∆øÞk"
  private val HASH_DIGRAPH_REGEX =
    """#[^\[\]$!=#>@{:]""" // Matches # followed by any character that doesn't start a trigraph
  private val STRUCTURE_OPENERS = "[({ṆḌƛΛξ⍾ʎµ⟨⎊⎄"
  private val IF_ELSE_OPENER = "#{"
  private val RECORD_OPENER = "#::R"
  private val EXTENSION_OPENER = "#::+"
  private val CUSTOM_OPENER_REGEX = "#::[EM]"
  private val BRANCH = "|"
  private val STRUCTURE_CLOSE = "}"
  private val LAMBDA = "λ"
  private val VARIABLE_UNPACK_OPENER = "#:["
  private val TERNARY_OPENER = "["
  private val VARIABLE_GET_SIGIL = "#$"
  private val VARIABLE_SET_SIGIL = "#="
  private val VARIABLE_SET_CONSTANT_SIGIL = "#!"
  private val VARIABLE_AUGMENTED_ASSIGN_SIGIL = "#>"
  private val CUSTOM_COMMAND_SYMBOL_SIGIL = "#:@"
  private val CUSTOM_MODIFIER_SYMBOL_SIGIL = "#:="
  private val ORIGINAL_COMMAND_SIGIL = "#:~"

  private val modifiersOfArity = (arity: Int) =>
    Modifiers.modifiers
      .filter((_, modifierObj) => modifierObj.arity == arity)
      .map((symbol, _) => symbol)
      .mkString

  private val MONADIC_MODIFIERS = modifiersOfArity(1)
  private val DYADIC_MODIFIERS = modifiersOfArity(2)
  private val TRIADIC_MODIFIERS = modifiersOfArity(3)
  private val TETRADIC_MODIFIERS = modifiersOfArity(4)
  private val SPECIAL_MODIFIERS = "⊐⟆"
  private val CONTEXT_INDEX = "#¤"

  private var unpackDepth = 0
  var sugarUsed = false

  def headIsOpener: Boolean =
    headIn(STRUCTURE_OPENERS) || headLookaheadEqual(LIST_OPEN) ||
      headLookaheadEqual(IF_ELSE_OPENER) || headLookaheadEqual(RECORD_OPENER) ||
      headLookaheadEqual(EXTENSION_OPENER) ||
      headLookaheadMatch(CUSTOM_OPENER_REGEX)

  def headIsBranch: Boolean = headEqual(BRANCH)

  def headIsCloser: Boolean = headEqual(STRUCTURE_CLOSE)

  def addToken(
      tokenType: TokenType,
      value: String,
      range: Range,
  ): Unit = tokens += Token(tokenType, value, range)

  def dropLastToken(): Unit = tokens.dropRightInPlace(1)

  def lex(program: String): Seq[Token] =
    programStack.pushAll(program.reverse.map(_.toString))

    while programStack.nonEmpty do
      if headIsDigit || headEqual(DECIMAL_SEPARATOR) then numberToken
      else if headEqual(NEWLINE) then quickToken(TokenType.Newline, NEWLINE)
      else if headIsWhitespace then pop(1)
      else if headEqual(DOUBLE_QUOTES) then stringToken(false)
      else if headEqual(SINGLE_QUOTE) then
        pop()
        if programStack.isEmpty then
          addToken(TokenType.Command, SINGLE_QUOTE, Range(index - 1, index))
        else oneCharStringToken
      else if headEqual(TWO_CHAR_STRING) then twoCharStringToken
      else if headEqual(TWO_CHAR_NUMBER) then twoCharNumberToken
      else if headIn(DIGRAPH_CHARS) || headLookaheadMatch(HASH_DIGRAPH_REGEX)
      then digraphToken
      else if headLookaheadEqual(COMMENT) then
        pop(2)
        while safeCheck(c => c != "\n" && c != "\r") do pop()
      else if headLookaheadEqual(LIST_OPEN) then
        quickToken(TokenType.ListOpen, LIST_OPEN)
      else if headLookaheadEqual("⟨") then
        pop()
        addToken(TokenType.ListOpen, LIST_OPEN, Range(index - 1, index))
      else if headLookaheadEqual(LIST_CLOSE) then
        quickToken(TokenType.ListClose, LIST_CLOSE)
      else if headLookaheadEqual("⟩") then
        pop()
        addToken(TokenType.ListClose, LIST_CLOSE, Range(index - 1, index))
      else if headEqual(TERNARY_OPENER) then
        quickToken(TokenType.StructureOpen, TERNARY_OPENER)
        if unpackDepth > 1 then unpackDepth += 1
      else if headEqual(STRUCTURE_CLOSE) then
        quickToken(TokenType.StructureClose, STRUCTURE_CLOSE)
      else if headEqual(STRUCTURE_DOUBLE_CLOSE) then
        quickToken(TokenType.StructureDoubleClose, STRUCTURE_DOUBLE_CLOSE)
      else if headEqual(STRUCTURE_FIRST_ITEM_CLOSE) then
        quickToken(
          TokenType.StructureCloseAndHead,
          STRUCTURE_FIRST_ITEM_CLOSE,
        )
      else if headEqual(STRUCTURE_FLATTEN_CLOSE) then
        quickToken(TokenType.StructureCloseAndFlatten, STRUCTURE_FLATTEN_CLOSE)
      else if headEqual(STRUCTURE_ALL_CLOSE) then
        pop()
        addToken(
          TokenType.StructureAllClose,
          STRUCTURE_ALL_CLOSE,
          Range(index, index),
        )
        if unpackDepth > 1 then unpackDepth -= 1
      else if headIn(STRUCTURE_OPENERS) then
        quickToken(TokenType.StructureOpen, s"${programStack.head}")
      else if headEqual(LAMBDA) then
        quickToken(TokenType.StructureOpen, LAMBDA)
        lambdaParameters
      else if headLookaheadEqual(IF_ELSE_OPENER) then
        quickToken(TokenType.StructureOpen, IF_ELSE_OPENER)
      else if headIn(MONADIC_MODIFIERS) then
        quickToken(TokenType.MonadicModifier, s"${programStack.head}")
      else if headIn(DYADIC_MODIFIERS) then
        quickToken(TokenType.DyadicModifier, s"${programStack.head}")
      else if headIn(TRIADIC_MODIFIERS) then
        quickToken(TokenType.TriadicModifier, s"${programStack.head}")
      else if headIn(TETRADIC_MODIFIERS) then
        quickToken(TokenType.TetradicModifier, s"${programStack.head}")
      else if headIn(SPECIAL_MODIFIERS) then
        quickToken(TokenType.SpecialModifier, s"${programStack.head}")
      else if headEqual(BRANCH) then quickToken(TokenType.Branch, BRANCH)
      else if headLookaheadEqual(CONTEXT_INDEX) then contextIndexToken
      else if headLookaheadEqual(VARIABLE_GET_SIGIL) then
        pop(2)
        getVariableToken
      else if headLookaheadEqual(VARIABLE_SET_SIGIL) then
        pop(2)
        setVariableToken
      else if headLookaheadEqual(VARIABLE_SET_CONSTANT_SIGIL) then
        pop(2)
        setConstantToken
      else if headLookaheadEqual(VARIABLE_AUGMENTED_ASSIGN_SIGIL) then
        pop(2)
        augmentedAssignToken
      else if headLookaheadEqual(VARIABLE_UNPACK_OPENER) then
        quickToken(TokenType.UnpackTrigraph, "#:[")
      else if headLookaheadEqual(ORIGINAL_COMMAND_SIGIL) then
        pop(3)
        originalCommandToken
      else if headLookaheadEqual(CUSTOM_COMMAND_SYMBOL_SIGIL) then
        pop(3)
        commandSymbolToken
      else if headLookaheadEqual(CUSTOM_MODIFIER_SYMBOL_SIGIL) then
        pop(3)
        modifierSymbolToken
      else if headLookaheadEqual(RECORD_OPENER) then
        pop(4)
        defineRecordToken
      else if headLookaheadEqual(EXTENSION_OPENER) then
        pop(4)
        defineExtensionToken
      else if headLookaheadMatch(CUSTOM_OPENER_REGEX) then customDefinitionToken
      else
        val rangeStart = index
        val char = pop()
        tokens +=
          Token(
            TokenType.Command,
            char,
            Range(rangeStart, index),
          )
      end if
    end while

    tokens.toSeq
  end lex

  /** Number = 0 | [1-9][0-9]*(\.[0-9]*)? _? | \.[0-9]* _? */
  private def numberToken: Unit =
    val rangeStart = index
    // Check the single zero case
    if headLookaheadMatch("0[^.]") then
      val zeroToken = Token(TokenType.Number, "0", Range(index, index))
      pop(1)
      tokens += zeroToken
    else
      val numberVal = StringBuilder()
      numberVal ++= decimalNumber()
      tokens +=
        Token(
          TokenType.Number,
          numberVal.toString(),
          Range(rangeStart, index),
        )

  end numberToken

  private def simpleNumber(): String =
    val numberVal = StringBuilder()
    while safeCheck(c => c.head.isDigit) do numberVal ++= s"${pop()}"
    numberVal.toString()

  private def decimalNumber(): String =
    val tokenVal = StringBuilder()
    var decimalUsed = false
    // Handle headless decimal first
    if headEqual(DECIMAL_SEPARATOR) then
      decimalUsed = true
      tokenVal ++= pop()
      if safeCheck(c => c.head.isDigit) then tokenVal ++= simpleNumber()
    // A normal number
    else if safeCheck(c => c.head.isDigit) then
      tokenVal ++= simpleNumber()
      // Could also a decimal number
      if headEqual(".") then
        tokenVal ++= pop()
        decimalUsed = true
        // If there's a digit after the decimal, add it
        if safeCheck(c => c.head.isDigit) then tokenVal ++= simpleNumber()

    if decimalUsed && tokenVal.last == '.' then tokenVal ++= "5"

    val number = tokenVal.toString()
    val padded =
      if number.startsWith(DECIMAL_SEPARATOR) then s"0${number}" else number
    if headEqual("_") then
      pop()
      s"${padded}_"
    else padded
  end decimalNumber

  private def oneCharStringToken: Unit =
    val rangeStart = index - 1
    val char = pop()
    tokens +=
      Token(
        TokenType.Str,
        char,
        Range(rangeStart, index),
      )

  private def twoCharStringToken: Unit =
    val rangeStart = index
    pop() // Pop the opening quote
    val char = pop(2)
    tokens +=
      Token(
        TokenType.Str,
        char,
        Range(rangeStart, index),
      )

  private def twoCharNumberToken: Unit =
    val rangeStart = index
    pop() // Pop the token
    val char = pop(2)
    tokens +=
      Token(
        TokenType.Number,
        char.zipWithIndex
          .map((c, ind) => math.pow(Codepage.length, ind) * Codepage.indexOf(c))
          .sum
          .toString,
        Range(rangeStart, index),
      )

  /** Digraph = [∆øÞ] . | # [^[]$!=#>@{:] */
  private def digraphToken: Unit =
    val rangeStart = index
    val digraphType = pop(1)
    val digraphChar = pop()

    tokens +=
      Token(
        TokenType.Digraph,
        s"$digraphType$digraphChar",
        Range(rangeStart, index),
      )

  private def contextIndexToken: Unit =
    val rangeStart = index
    pop(2)
    val value = simpleNumber()
    tokens +=
      Token(
        TokenType.ContextIndex,
        value,
        Range(rangeStart, index),
      )

  protected def defineExtensionToken: Unit =
    val rangeStart = index
    eatWhitespace()
    val name =
      if headEqual(".") then
        pop()
        s".${simpleName()}"
      else simpleName()
    addToken(
      TokenType.DefineExtension,
      "",
      Range(rangeStart, index),
    )
    addToken(
      TokenType.Param,
      name,
      Range(rangeStart, index),
    )
    eatWhitespace()
    if headIsBranch then
      quickToken(TokenType.Branch, BRANCH)
      // Get the arguments and put them into tokens
      var arity = 0
      while !headIsBranch do // Loop until the implementation
        eatWhitespace()
        // Read the name of the argument to the extension
        val argNameStart = index
        val argName = simpleName()
        addToken(
          TokenType.Param,
          argName,
          Range(argNameStart, index),
        )
        eatWhitespace()
        // Read the type of the argument to the extension
        eat(EXTENSION_TYPE_SEPARATOR)
        eatWhitespace()
        val argTypeStart = index
        val argType =
          if headEqual(EXTENSION_ANY_TYPE) then pop() else simpleName()
        addToken(
          TokenType.Param,
          argType,
          Range(argTypeStart, index),
        )
        arity += 1
        // Consume any optional comma
        if headEqual(",") then pop()
        eatWhitespace()
      end while
    end if
  end defineExtensionToken

  private def customDefinitionToken: Unit =
    val rangeStart = index
    pop(3)
    addToken(
      TokenType.StructureOpen,
      "#::",
      Range(rangeStart, index),
    )
    val definitionType = pop()
    if !"EM".contains(definitionType) then
      throw VyxalException(
        s"Invalid definition type: $definitionType. Expected E or M"
      )

    eatWhitespace()

    val nameRangeStart = index

    if programStack.isEmpty then
      throw VyxalException("No name provided for custom definition")

    val name = if headIsLetter then simpleName() else pop()

    addToken(
      TokenType.Param,
      s"$definitionType$name",
      Range(nameRangeStart, index),
    )

    if programStack.isEmpty then
      throw VyxalException("No parameters provided for custom definition")

  end customDefinitionToken
end SBCSLexer
