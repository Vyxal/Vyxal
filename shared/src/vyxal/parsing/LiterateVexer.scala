package vyxal.parsing

import vyxal.parsing.VTokenType.*
import vyxal.Elements
import vyxal.Modifier
import vyxal.Modifiers

import scala.collection.mutable.ArrayBuffer

class LiterateVexer extends VexerCommon:
  private val literateKeywords = Elements.elements.values.flatMap(_.keywords)
  private val _tokens = ArrayBuffer[VLitToken]()
  private val groups = ArrayBuffer[ArrayBuffer[VLitToken]]()
  private val groupModifierToToken = Map(
    "." -> ((range) => VLitToken(MonadicModifier, "⸠", range)),
    ":" -> ((range) => VLitToken(DyadicModifier, "ϩ", range)),
    ":." -> ((range) => VLitToken(TriadicModifier, "э", range)),
    "::" -> ((range) => VLitToken(TetradicModifier, "Ч", range)),
    "," -> ((range) => VLitToken(MonadicModifier, "ᵈ", range)),
    ";" -> ((range) => VLitToken(DyadicModifier, "ᵉ", range)),
    ";," -> ((range) => VLitToken(TriadicModifier, "ᶠ", range)),
    ";;" -> ((range) => VLitToken(TetradicModifier, "ᵍ", range)),
  )
  def lex(program: String): Seq[VToken] =
    programStack.pushAll(program.reverse)
    while programStack.nonEmpty do
      if safeCheck(c => c.isLetter || "<>?!*+\\-=&%:@".contains(c)) then
        keywordToken
      else if headEqual('\'') then moveRightToken
      else if headEqual('(') then
        eat('(')
        groups += ArrayBuffer[VLitToken]()
        if headIn(":.,;") then
          appendToken(groupModifierToToken(pop(2))(VRange(index, index)))
      else if headEqual(')') then
        val group = groups.last
        groups.dropRightInPlace(1)
        appendToken(
          VLitToken(Group, group.toSeq, VRange(index, index))
        )
      else if headIsWhitespace then pop(1)
    end while
    // Flatten _tokens into tokens
    for token <- _tokens do
      if token.tokenType == Group then
        tokens ++= token.value.asInstanceOf[Seq[VLitToken]].map(_.toNormal)
      else tokens += token.toNormal
    tokens.toSeq
  end lex

  private def appendToken(token: VLitToken): Unit =
    if groups.nonEmpty then groups.last += token
    else _tokens += token

  private def keywordToken: Unit =
    val start = index
    val keyword = StringBuilder()
    while safeCheck(c => c.isLetterOrDigit || "_<>?!*+\\-=&%:'@".contains(c)) do
      keyword ++= pop(1)
    val value = removeDoubleNt(keyword.toString())
    if isKeyword(value) then
      appendToken(
        VLitToken(
          Command,
          getSymbolFromKeyword(value),
          VRange(start, index),
        )
      )
    else if isKeyword(value.stripSuffix("n't")) then
      appendToken(
        VLitToken(NegatedCommand, value, VRange(start, index))
      )
    else if isModifier(value) then
      val mod = getModifierFromKeyword(value)
      val name = Modifiers.modifiers.find(_._2._3.contains(keyword)).get._1
      val tokenType = mod.arity match
        case 1 => VTokenType.MonadicModifier
        case 2 => VTokenType.DyadicModifier
        case 3 => VTokenType.TriadicModifier
        case 4 => VTokenType.TetradicModifier
        case _ => VTokenType.SpecialModifier
      appendToken(VLitToken(tokenType, name, VRange(start, index)))
    else
      for char <- value do
        appendToken(
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
    while headEqual('\'') do quotes ++= pop(1)
    val value = quotes.toString()
    appendToken(VLitToken(MoveRight, value, VRange(start, index)))

end LiterateVexer
