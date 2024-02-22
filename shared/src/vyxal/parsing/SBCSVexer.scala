package vyxal.parsing

import vyxal.SugarMap
import vyxal.VyxalException

class SBCSVexer extends VexerCommon:

  def headIsOpener: Boolean =
    headIn("[({ṆḌƛΩ₳µ") || headLookaheadEqual("#[") ||
      headLookaheadEqual("#{") || headLookaheadEqual("#::R") ||
      headLookaheadEqual("#::+") || headLookaheadMatch("#::[EM]")

  def headIsBranch: Boolean = headEqual("|")

  def headIsCloser: Boolean = headEqual("}")

  def addToken(
      tokenType: VTokenType,
      value: String,
      range: VRange,
  ): Unit = tokens += VToken(tokenType, value, range)

  def lex(program: String): Seq[VToken] =
    programStack.pushAll(program.reverse.map(_.toString))

    while programStack.nonEmpty do
      if headIsDigit || headEqual(".") then numberToken
      else if headIsWhitespace then pop(1)
      else if headEqual("\"") then stringToken
      else if headEqual("'") then oneCharStringToken
      else if headEqual("῟") then twoCharStringToken
      else if headEqual("⚇") then twoCharNumberToken
      else if headIn("∆øÞk") || headLookaheadMatch("""#[^\[\]$!=#>@{:.,^]""")
      then digraphToken
      else if headLookaheadEqual("##") then
        pop(2)
        while safeCheck(c => c != "\n" && c != "\r") do pop()
        addToken(VTokenType.Newline, "\n", VRange(index, index))
      else if headLookaheadMatch("#[.,^]") then sugarTrigraph
      else if headLookaheadEqual("#[") then
        quickToken(VTokenType.ListOpen, "#[")
      else if headLookaheadEqual("#]") then
        quickToken(VTokenType.ListClose, "#]")
      else if headIn("[({ṆḌƛΩ₳µ") then
        quickToken(VTokenType.StructureOpen, s"${programStack.head}")
      else if headEqual("λ") then
        quickToken(VTokenType.StructureOpen, "λ")
        lambdaParameters
      else if headLookaheadEqual("#{") then
        quickToken(VTokenType.StructureOpen, "#{")
      else if headLookaheadEqual("#:[") then
        quickToken(VTokenType.UnpackTrigraph, "#:[")
      else if headIn("⎂⇝∯⊠ß≟₾◌v⸠♳¿⎇↻⁙/\\") then
        quickToken(VTokenType.MonadicModifier, s"${programStack.head}")
      else if headIn("ϩ∥∦♴⁙") then
        quickToken(VTokenType.DyadicModifier, s"${programStack.head}")
      else if headIn("э♵") then
        quickToken(VTokenType.TriadicModifier, s"${programStack.head}")
      else if headIn("Ч♶") then
        quickToken(VTokenType.TetradicModifier, s"${programStack.head}")
      else if headIn("⋊⊙") then
        quickToken(VTokenType.SpecialModifier, s"${programStack.head}")
      else if headEqual("|") then quickToken(VTokenType.Branch, "|")
      else if headEqual("¤") then contextIndexToken
      else if headLookaheadEqual("#$") then
        pop(2)
        getVariableToken
      else if headLookaheadEqual("#=") then
        pop(2)
        setVariableToken
      else if headLookaheadEqual("#!") then setConstantToken
      else if headLookaheadEqual("#>") then
        pop(2)
        augmentedAssignToken
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
      else if headLookaheadMatch("#::[EM]") then
        pop(4)
        customDefinitionToken
      else if headEqual("}") then quickToken(VTokenType.StructureClose, "}")
      else if headEqual(")") then
        quickToken(VTokenType.StructureDoubleClose, ")")
      else if headEqual("]") then quickToken(VTokenType.StructureAllClose, "]")
      else
        val rangeStart = index
        val char = pop()
        tokens +=
          VToken(
            VTokenType.Command,
            char,
            VRange(rangeStart, index),
          )
    end while

    tokens.toSeq
  end lex

  /** Number = 0 | [1-9][0-9]*(\.[0-9]*)? _? | \.[0-9]* _? */
  private def numberToken: Unit =
    val rangeStart = index
    // Check the single zero case
    if headLookaheadMatch("0[^.ı]") then
      val zeroToken = VToken(VTokenType.Number, "0", VRange(index, index))
      pop(1)
      tokens += zeroToken
    // Then the headless decimal case
    else if headEqual(".") then
      pop(1)
      if safeCheck(c => c.head.isDigit) then
        val head = simpleNumber()
        val numberToken = VToken(
          VTokenType.Number,
          s"0.$head",
          VRange(rangeStart, index),
        )
        tokens += numberToken
      else
        val zeroToken = VToken(
          VTokenType.Number,
          "0.5",
          VRange(rangeStart, index),
        )
        tokens += zeroToken
    else
      // Not a 0, and not a headless decimal, so it's a normal number
      val head = simpleNumber()
      // Test for a decimal tail
      if headEqual(".") then
        pop(1)
        if safeCheck(c => c.head.isDigit) then
          val tail = simpleNumber()
          val isNegative = headEqual("_")
          val numberToken = VToken(
            VTokenType.Number,
            s"${if isNegative then pop() else ""}$head.$tail",
            VRange(rangeStart, index),
          )
          tokens += numberToken
        else
          val numberToken = VToken(
            VTokenType.Number,
            s"$head.5",
            VRange(rangeStart, index),
          )
          tokens += numberToken
      // No decimal tail, so normal number
      else
        val isNegative = headEqual("_")
        val numberToken = VToken(
          VTokenType.Number,
          (if isNegative then pop() else "") + head,
          VRange(rangeStart, index),
        )
        tokens += numberToken
    end if
    if headEqual("ı") then
      // Grab an imaginary part and merge with the previous number
      pop()
      val combinedTokenValue = tokens.last.value + "ı"
      tokens.dropRightInPlace(1)
      numberToken
      val finalTokenValue = combinedTokenValue + tokens.last.value
      tokens.dropRightInPlace(1)
      tokens +=
        VToken(VTokenType.Number, finalTokenValue, VRange(rangeStart, index))

  end numberToken

  private def simpleNumber(): String =
    val numberVal = StringBuilder()
    while safeCheck(c => c.head.isDigit) do numberVal ++= s"${pop()}"
    numberVal.toString()

  private def oneCharStringToken: Unit =
    val rangeStart = index
    pop() // Pop the opening quote
    val char = pop()
    tokens +=
      VToken(
        VTokenType.Str,
        char,
        VRange(rangeStart, index),
      )

  private def twoCharStringToken: Unit =
    val rangeStart = index
    pop() // Pop the opening quote
    val char = pop(2)
    tokens +=
      VToken(
        VTokenType.Str,
        char,
        VRange(rangeStart, index),
      )

  private def twoCharNumberToken: Unit =
    val rangeStart = index
    pop() // Pop the opening quote
    val value = pop(2)
    val number = value.zipWithIndex
      .map((c, ind) => math.pow(Codepage.length, ind) * Codepage.indexOf(c))
      .sum
      .toString
    tokens +=
      VToken(
        VTokenType.Number,
        number,
        VRange(rangeStart, index),
      )

  /** Digraph = [∆øÞ] . | # [^[]$!=#>@{:] */
  private def digraphToken: Unit =
    val rangeStart = index

    val digraphType = pop(1)

    if headEqual("#") then sugarTrigraph

    val digraphChar = pop()

    tokens +=
      VToken(
        VTokenType.Digraph,
        s"$digraphType$digraphChar",
        VRange(rangeStart, index),
      )

  /** Convert a sugar trigraph to its normal form */
  private def sugarTrigraph: Unit =
    val trigraph = pop(3)
    val normal = SugarMap.trigraphs.getOrElse(trigraph, trigraph)
    programStack.pushAll(normal.reverse.map(_.toString))

  private def contextIndexToken: Unit =
    val rangeStart = index
    pop()
    val value = simpleNumber()
    tokens +=
      VToken(
        VTokenType.ContextIndex,
        value,
        VRange(rangeStart, index),
      )

  /** Extension ::= "#::+" [a-zA-Z_][a-zA-Z0-9_] "|" (Name ">" Name)* "|" impl }
    */
  private def defineExtensionToken: Unit =
    val rangeStart = index
    eat("#::+")
    val name = if headLookaheadMatch(". ") then pop() else simpleName()
    tokens +=
      VToken(
        VTokenType.DefineExtension,
        name,
        VRange(rangeStart, index),
      )
    if headEqual("|") then
      pop()
      // Get the arguments and put them into tokens
      var arity = 0
      while !headEqual("|") do
        val argNameStart = index
        val argName = simpleName()
        tokens +=
          VToken(
            VTokenType.Param,
            argName,
            VRange(argNameStart, index),
          )
        eatWhitespace()
        eat(">")
        eatWhitespace()
        val argTypeStart = index
        val argType = simpleName()
        tokens +=
          VToken(
            VTokenType.Param,
            argType,
            VRange(argTypeStart, index),
          )
        arity += 1
      end while
      symbolTable += name -> Some(arity)
    end if
  end defineExtensionToken

  private def customDefinitionToken: Unit =
    val rangeStart = index
    pop(3)
    tokens +=
      VToken(
        VTokenType.StructureOpen,
        "#::",
        VRange(rangeStart, index),
      )
    val definitionType = pop()
    if !"EM".contains(definitionType) then
      throw VyxalException(
        s"Invalid definition type: $definitionType. Expected E or M"
      )
    eatWhitespace()
    val nameRangeStart = index
    val name = simpleName()

    tokens +=
      VToken(
        VTokenType.Param,
        s"$definitionType$name",
        VRange(nameRangeStart, index),
      )

    quickToken(VTokenType.Branch, "|")
    val params = lambdaParameters
    val arity = calcArity(params)

    symbolTable += s"$definitionType$name" -> arity

  end customDefinitionToken

end SBCSVexer
