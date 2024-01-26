package vyxal.parsing

import vyxal.parsing.VLitToken
import vyxal.Elements
import vyxal.Modifier
import vyxal.Modifiers

import scala.collection.mutable.ArrayBuffer

class LiterateVexer extends VexerCommon:
  private val literateKeywords = Elements.elements.values.flatMap(_.keywords)
  private val _tokens = ArrayBuffer[VLitToken]()
  def lex(program: String): Seq[VToken] =
    programStack.pushAll(program.reverse)
    while programStack.nonEmpty do
      if safeCheck(c => c.isLetter || "<>?!*+\\-=&%:@".contains(c)) then
        keywordToken
    tokens.toSeq

  private def keywordToken: Unit =
    val start = index
    val keyword = StringBuilder()
    while safeCheck(c => c.isLetterOrDigit || "_<>?!*+\\-=&%:'@".contains(c)) do
      keyword ++= pop(1)
    val value = removeDoubleNt(keyword.toString())
    if isKeyword(value) then
      _tokens +=
        VLitToken(
          VTokenType.Command,
          getSymbolFromKeyword(value),
          VRange(start, index),
        )
    else if isKeyword(value.stripSuffix("n't")) then
      _tokens +=
        VLitToken(VTokenType.NegatedCommand, value, VRange(start, index))
    else if isModifier(value) then
      val mod = getModifierFromKeyword(value)
      val name = Modifiers.modifiers.find(_._2._3.contains(keyword)).get._1
      val tokenType = mod.arity match
        case 1 => VTokenType.MonadicModifier
        case 2 => VTokenType.DyadicModifier
        case 3 => VTokenType.TriadicModifier
        case 4 => VTokenType.TetradicModifier
        case _ => VTokenType.SpecialModifier
      _tokens += VLitToken(tokenType, name, VRange(start, index))
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
end LiterateVexer
