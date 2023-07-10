package vyxal.lexer

import scala.language.strictEquality

import vyxal.{Modifiers, SugarMap}
import vyxal.impls.Elements
import vyxal.lexer.Common.{parseToken, withInd, withRange}
import vyxal.lexer.Common.given // For custom whitespace
import vyxal.lexer.TokenType.*

import scala.util.matching.Regex

import fastparse.*

private[lexer] object SBCSLexer extends Lexer:
  /** Whether the code lexed so far has sugar trigraphs */
  var sugarUsed = false

  def string[$: P]: P[Token] =
    withRange(
      "\"" ~/ ("\\" ~/ AnyChar | !CharIn("\"„”“") ~ AnyChar).rep.! ~ CharIn(
        "\"„”“"
      ).!
    )
      .map { case ((value, last), range) =>
        // If the last character of each token is ", then it's a normal string
        // If the last character of each token is „, then it's a compressed string
        // If the last character of each token is ”, then it's a dictionary string
        // If the last character of each token is “, then it's a compressed number

        // So replace the non-normal string tokens with the appropriate token type

        // btw thanks to @pxeger and @mousetail for the regex

        val text = value
          .replace("\\\"", "\"")
          .replace(raw"\n", "\n")
          .replace(raw"\t", "\t")

        val tokenType = (last.charAt(0): @unchecked) match
          case '"' => Str
          case '„' => CompressedString
          case '”' => DictionaryString
          case '“' => CompressedNumber

        Token(tokenType, text, range)
      }

  def singleCharString[$: P]: P[Token] =
    parseToken(Str, P("'" ~ AnyChar.!))

  def twoCharString[$: P]: P[Token] =
    parseToken(Str, P("ᶴ" ~/ (AnyChar ~ AnyChar).!))

  def twoCharNumber[$: P]: P[Token] =
    withRange(P("~" ~/ (AnyChar ~ AnyChar).!)).map { case (value, range) =>
      Token(
        Number,
        value.zipWithIndex
          .map((c, ind) =>
            math.pow(Lexer.Codepage.length, ind) * Lexer.Codepage.indexOf(c)
          )
          .sum
          .toString,
        range
      )
    }

  def structureOpen[$: P]: P[Token] =
    parseToken(StructureOpen, StructureType.values.map(_.open.!).reduce(_ | _))

  def structureSingleClose[$: P]: P[Token] = parseToken(StructureClose, "}".!)

  def structureDoubleClose[$: P]: P[Token] =
    parseToken(StructureDoubleClose, ")".!)

  def structureAllClose[$: P]: P[Token] = parseToken(StructureAllClose, "]".!)

  def listOpen[$: P]: P[Token] = parseToken(ListOpen, StringIn("#[", "⟨").!)

  def listClose[$: P]: P[Token] = parseToken(ListClose, StringIn("#]", "⟩").!)

  def digraph[$: P]: P[Token] = P(
    withRange(
      (CharIn("∆øÞk") ~ AnyChar).! | ("#" ~ !CharIn("[]$!=#>@{") ~ AnyChar).!
    )
  ).map { case (digraph, range) =>
    if Elements.elements.contains(digraph) then Token(Command, digraph, range)
    else if Modifiers.modifiers.contains(digraph) then
      val modifier = Modifiers.modifiers(digraph)
      val tokenType = modifier.arity match
        case 1 => MonadicModifier
        case 2 => DyadicModifier
        case 3 => TriadicModifier
        case 4 => TetradicModifier
        case -1 => SpecialModifier
        case arity => throw Exception(s"Invalid modifier arity: $arity")
      Token(tokenType, digraph, range)
    else Token(Digraph, digraph, range)
  }

  def syntaxTrigraph[$: P]: P[Token] =
    parseToken(SyntaxTrigraph, ("#:" ~ AnyChar).!)

  def sugarTrigraph[$: P]: P[Token] =
    withRange(("#" ~ CharIn(".,^") ~ AnyChar).!).map { case (value, range) =>
      this.sugarUsed = true
      SugarMap.trigraphs
        .get(value)
        .flatMap(char => this.lex(char).toOption.map(_.head))
        .getOrElse(Token(Command, value, range))
    }

  /** Match any one of the characters in a given string */
  def charsFromString[$: P](str: String): P[String] =
    P(str.toSeq.map(_.toString.!).reduce(_ | _))

  def command[$: P]: P[Token] = parseToken(
    Command,
    charsFromString(
      Lexer.Codepage.replaceAll(raw"[|\[\](){}]", "") + Lexer.UnicodeCommands
    )
  )

  def monadicModifier[$: P]: P[Token] =
    parseToken(MonadicModifier, charsFromString(Lexer.MonadicModifiers))

  def dyadicModifier[$: P]: P[Token] =
    parseToken(DyadicModifier, charsFromString(Lexer.DyadicModifiers))

  def triadicModifier[$: P]: P[Token] =
    parseToken(TriadicModifier, charsFromString(Lexer.TriadicModifiers))

  def tetradicModifier[$: P]: P[Token] =
    parseToken(TetradicModifier, charsFromString(Lexer.TetradicModifiers))

  def specialModifier[$: P]: P[Token] =
    parseToken(SpecialModifier, charsFromString(Lexer.SpecialModifiers))

  def branch[$: P]: P[Token] = parseToken(Branch, "|".!)

  def sbcsDecimal[$: P]: P[String] =
    P(((Common.int ~ ("." ~ Common.digits).? | "." ~ Common.digits) ~ "_".?).!)
  def sbcsNumber[$: P]: P[Token] =
    Common.number(sbcsDecimal, P("ı".!))

  def contextIndex[$: P]: P[Token] =
    parseToken(ContextIndex, Common.digits ~ "¤")

  def getVariable[$: P]: P[Token] = parseToken(GetVar, "#$" ~/ Common.varName)

  def setVariable[$: P]: P[Token] = parseToken(SetVar, "#=" ~/ Common.varName)

  def setConstant[$: P]: P[Token] = parseToken(Constant, "#!" ~/ Common.varName)

  def augVariable[$: P]: P[Token] =
    parseToken(AugmentVar, "#>" ~/ Common.varName)

  def newlines[$: P]: P[Token] = parseToken(Newline, Common.eol.!)

  def token[$: P]: P[Token] =
    P(
      sugarTrigraph | syntaxTrigraph | digraph | branch | contextIndex
        | sbcsNumber | string | augVariable | getVariable | setVariable
        | setConstant | twoCharNumber | twoCharString | singleCharString
        | monadicModifier | dyadicModifier | triadicModifier | tetradicModifier
        | specialModifier | structureOpen | structureSingleClose | structureAllClose
        | listOpen | listClose | newlines | command
    )

  // structureDoubleClose (")") has to be here to avoid interfering with `normalGroup` in literate lexer
  def tokens[$: P]: P[Seq[Token]] = P((token | structureDoubleClose).rep)

end SBCSLexer
