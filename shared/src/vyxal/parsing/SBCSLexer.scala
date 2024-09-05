package vyxal.parsing

import vyxal.SugarMap
import vyxal.VyxalException

class SBCSLexer extends LexerCommon:

  private var unpackDepth = 0
  var sugarUsed = false

  def headIsOpener: Boolean =
    headIn("[({ṆḌƛΩ₳µ⟨") || headLookaheadEqual("#[") ||
      headLookaheadEqual("#{") || headLookaheadEqual("#::R") ||
      headLookaheadEqual("#::+") || headLookaheadMatch("#::[EM]")

  def headIsBranch: Boolean = headEqual("|")

  def headIsCloser: Boolean = headEqual("}")

  def addToken(
      tokenType: TokenType,
      value: String,
      range: Range,
  ): Unit = tokens += Token(tokenType, value, range)

  def dropLastToken(): Unit = tokens.dropRightInPlace(1)

  def lex(program: String): Seq[Token] =
    programStack.pushAll(program.reverse.map(_.toString))

    while programStack.nonEmpty do
      if headIsDigit || headEqual(".") || headEqual("ı") then numberToken
      else if headEqual("\n") then quickToken(TokenType.Newline, "\n")
      else if headIsWhitespace then pop(1)
      else if headEqual("\"") then stringToken(false)
      else if headEqual("'") then
        pop()
        if programStack.isEmpty then
          addToken(TokenType.Command, "'", Range(index - 1, index))
        else oneCharStringToken
      else if headEqual("ᶴ") then twoCharStringToken
      else if headEqual("~") then twoCharNumberToken
      else if headIn("∆øÞk") || headLookaheadMatch("""#[^\[\]$!=#>@{:.,^]""")
      then digraphToken
      else if headLookaheadEqual("##") then
        pop(2)
        while safeCheck(c => c != "\n" && c != "\r") do pop()
      else if headLookaheadMatch("#[.,^]") then sugarTrigraph
      else if headLookaheadEqual("#[") then quickToken(TokenType.ListOpen, "#[")
      else if headLookaheadEqual("⟨") then
        pop()
        addToken(TokenType.ListOpen, "#[", Range(index - 1, index))
      else if headLookaheadEqual("#]") then
        quickToken(TokenType.ListClose, "#]")
      else if headLookaheadEqual("⟩") then
        pop()
        addToken(TokenType.ListClose, "#]", Range(index - 1, index))
      else if unpackDepth > 1 && headEqual("[") then
        addToken(TokenType.StructureOpen, "[", Range(index, index))
        unpackDepth += 1
      else if unpackDepth > 1 && headEqual("]") then
        addToken(TokenType.StructureAllClose, "]", Range(index, index))
        unpackDepth -= 1
      else if headIn("[({ṆḌƛΩ₳µ") then
        quickToken(TokenType.StructureOpen, s"${programStack.head}")
      else if headEqual("λ") then
        quickToken(TokenType.StructureOpen, "λ")
        lambdaParameters
      else if headLookaheadEqual("#{") then
        quickToken(TokenType.StructureOpen, "#{")
      else if headLookaheadEqual("#:[") then
        quickToken(TokenType.UnpackTrigraph, "#:[")
      else if headIn("ᵃᵇᶜᵈᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻ¿⸠/") then
        quickToken(TokenType.MonadicModifier, s"${programStack.head}")
      else if headIn("ϩ∥∦ᵉ") then
        quickToken(TokenType.DyadicModifier, s"${programStack.head}")
      else if headIn("эᶠ") then
        quickToken(TokenType.TriadicModifier, s"${programStack.head}")
      else if headIn("Чᴳ") then
        quickToken(TokenType.TetradicModifier, s"${programStack.head}")
      else if headIn("ᵜ") then
        quickToken(TokenType.SpecialModifier, s"${programStack.head}")
      else if headEqual("|") then quickToken(TokenType.Branch, "|")
      else if headEqual("¤") then contextIndexToken
      else if headLookaheadEqual("#$") then
        pop(2)
        getVariableToken
      else if headLookaheadEqual("#=") then
        pop(2)
        setVariableToken
      else if headLookaheadEqual("#!") then
        pop(2)
        setConstantToken
      else if headLookaheadEqual("#>") then
        pop(2)
        augmentedAssignToken
      else if headLookaheadEqual("#:[") then
        pop(3)
        addToken(TokenType.UnpackTrigraph, "#:[", Range(index - 3, index))
        unpackDepth = 1
      else if headLookaheadEqual("#:~") then
        pop(3)
        originalCommandToken
      else if headLookaheadEqual("#:@") then
        pop(3)
        commandSymbolToken
      else if headLookaheadEqual("#:=") then
        pop(3)
        modifierSymbolToken
      else if headLookaheadEqual("#::R") then
        pop(4)
        defineRecordToken
      else if headLookaheadEqual("#::+") then
        pop(4)
        defineExtensionToken
      else if headLookaheadMatch("#::[EM]") then customDefinitionToken
      else if headLookaheadEqual("#[") || headEqual("⟨") then
        quickToken(TokenType.ListOpen, "#[")
      else if headEqual("#]") || headEqual("⟩") then
        quickToken(TokenType.ListClose, "#]")
      else if headEqual("}") then quickToken(TokenType.StructureClose, "}")
      else if headEqual(")") then
        quickToken(TokenType.StructureDoubleClose, ")")
      else if headEqual("]") then quickToken(TokenType.StructureAllClose, "]")
      else
        val rangeStart = index
        val char = pop()
        tokens +=
          Token(
            TokenType.Command,
            char,
            Range(rangeStart, index),
          )
    end while

    tokens.toSeq
  end lex

  /** Number = 0 | [1-9][0-9]*(\.[0-9]*)? _? | \.[0-9]* _? */
  private def numberToken: Unit =
    val rangeStart = index
    // Check the single zero case
    if headLookaheadMatch("0[^.ı]") then
      val zeroToken = Token(TokenType.Number, "0", Range(index, index))
      pop(1)
      tokens += zeroToken
    else
      val numberVal = StringBuilder()
      if !headEqual("ı") then numberVal ++= decimalNumber()
      if headEqual("ı") then
        numberVal ++= pop()
        val number =
          if safeCheck(c => c.head.isDigit || c == ".") then decimalNumber()
          else "1"
        if headEqual("_") then numberVal ++= pop()
        numberVal ++= number
      var modified = numberVal.toString()
      // Add the implicit values
      if modified.startsWith("ı") then modified = "0" + modified

      tokens +=
        Token(
          TokenType.Number,
          modified,
          Range(rangeStart, index),
        )
    end if

  end numberToken

  private def simpleNumber(): String =
    val numberVal = StringBuilder()
    while safeCheck(c => c.head.isDigit) do numberVal ++= s"${pop()}"
    numberVal.toString()

  private def decimalNumber(): String =
    val tokenVal = StringBuilder()
    var decimalUsed = false
    // Handle headless decimal first
    if headEqual(".") then
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
    val padded = if number.startsWith(".") then s"0${number}" else number
    if headEqual("_") then
      pop()
      s"_${padded}"
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
    pop() // Pop the tilde
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

    if headEqual("#") then sugarTrigraph

    val digraphChar = pop()

    tokens +=
      Token(
        TokenType.Digraph,
        s"$digraphType$digraphChar",
        Range(rangeStart, index),
      )

  /** Convert a sugar trigraph to its normal form */
  private def sugarTrigraph: Unit =
    val trigraph = pop(3)
    val normal = SugarMap.trigraphs.getOrElse(trigraph, trigraph)
    programStack.pushAll(normal.reverse.map(_.toString))
    sugarUsed = true

  private def contextIndexToken: Unit =
    val rangeStart = index
    pop()
    val value = simpleNumber()
    tokens +=
      Token(
        TokenType.ContextIndex,
        value,
        Range(rangeStart, index),
      )

  /** Extension ::= "#::+" [a-zA-Z_][a-zA-Z0-9_] "|" (Name ">" Name)* "|" impl }
    */
  private def defineExtensionToken: Unit =
    val rangeStart = index
    eatWhitespace()
    val name = if headLookaheadMatch(". ") then pop() else simpleName()
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
    if headEqual("|") then
      quickToken(TokenType.Branch, "|")
      // Get the arguments and put them into tokens
      var arity = 0
      while !headEqual("|") do
        eatWhitespace()
        val argNameStart = index
        val argName = simpleName()
        addToken(
          TokenType.Param,
          argName,
          Range(argNameStart, index),
        )
        eatWhitespace()
        eat(">")
        eatWhitespace()
        val argTypeStart = index
        val argType = if headEqual("*") then pop() else simpleName()
        addToken(
          TokenType.Param,
          argType,
          Range(argTypeStart, index),
        )
        arity += 1
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
