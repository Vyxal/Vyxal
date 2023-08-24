package vyxal.parsing

import scala.language.strictEquality

import vyxal.{Elements, Modifiers, SugarMap}
import vyxal.parsing.Common.{parseToken, withRange}
import vyxal.parsing.Common.given // For custom whitespace
import vyxal.parsing.TokenType.*

import fastparse.*

private[parsing] object SBCSLexer extends Lexer:
  /** Whether the code lexed so far has sugar trigraphs */
  var sugarUsed = false

  def string[$: P]: P[Token] =
    P(
      withRange(
        "\"" ~~/
          (("\\" ~~/ AnyChar) | (!CharIn("\"„”“") ~~ AnyChar)).repX.! ~~
          (CharIn("\"„”“").! | End)
      )
    ).map {
      case ((value, last), range) =>
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

        last match
          case quote: String =>
            val tokenType = quote match
              case "\"" => Str
              case "„" => CompressedString
              case "”" => DictionaryString
              case "“" => CompressedNumber

            Token(tokenType, text, range)
          case _ => Token(Str, text, range)
    }

  def singleCharString[$: P]: P[Token] = parseToken(Str, P("'" ~~ AnyChar.!))

  def twoCharString[$: P]: P[Token] =
    parseToken(Str, P("ᶴ" ~~/ (AnyChar ~~ AnyChar).!))

  def twoCharNumber[$: P]: P[Token] =
    withRange(P("~" ~~/ (AnyChar ~~ AnyChar).!)).map {
      case (value, range) => Token(
          Number,
          value.zipWithIndex
            .map((c, ind) =>
              math.pow(Lexer.Codepage.length, ind) * Lexer.Codepage.indexOf(c)
            )
            .sum
            .toString,
          range,
        )
    }

  def structureOpen[$: P]: P[Token] =
    parseToken(
      StructureOpen,
      StringIn("[", "{", "(", "#{", "Ḍ", "Ṇ").! | Common.lambdaOpen,
    ) // StructureType.values.map(_.open.!).reduce(_ | _))
    // TODO(user): figure out why the commented version doesn't work

  def structureSingleClose[$: P]: P[Token] = parseToken(StructureClose, "}".!)

  def structureDoubleClose[$: P]: P[Token] =
    parseToken(StructureDoubleClose, ")".!)

  def structureAllClose[$: P]: P[Token] = parseToken(StructureAllClose, "]".!)

  def listOpen[$: P]: P[Token] = parseToken(ListOpen, StringIn("#[", "⟨").!)

  def listClose[$: P]: P[Token] = parseToken(ListClose, StringIn("#]", "⟩").!)

  def digraph[$: P]: P[Token] =
    P(
      withRange(
        (CharIn("∆øÞk") ~~ AnyChar).! |
          ("#" ~~ !CharIn("[]$!=#>@{") ~~ AnyChar).!
      )
    ).map {
      case (digraph, range) =>
        if Elements.elements.contains(digraph) then
          Token(Command, digraph, range)
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
    parseToken(SyntaxTrigraph, ("#:" ~~ AnyChar).!)

  def sugarTrigraph[$: P]: P[Token] =
    withRange(("#" ~~ CharIn(".,^") ~~ AnyChar).!).map {
      case (value, range) =>
        this.sugarUsed = true
        SugarMap.trigraphs
          .get(value)
          .flatMap(char => this.lex(char).toOption.map(_.head))
          .getOrElse(Token(Command, value, range))
    }

  private val allCommands =
    (Lexer.Codepage.replaceAll(raw"[|\[\](){}\s]", "") +
      Lexer.UnicodeCommands).toSet

  def command[$: P]: P[Token] = parseToken(Command, CharPred(allCommands).!)

  def monadicModifier[$: P]: P[Token] =
    parseToken(MonadicModifier, CharIn("ᵃᵇᶜᵈᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵘᵛᵂᵡᵞᶻ¿⸠/\\\\~v@`ꜝ").!)

  def dyadicModifier[$: P]: P[Token] =
    parseToken(DyadicModifier, CharIn("ϩ∥∦ᵉ").!)

  def triadicModifier[$: P]: P[Token] =
    parseToken(TriadicModifier, CharIn("эᶠ").!)

  def tetradicModifier[$: P]: P[Token] =
    parseToken(TetradicModifier, CharIn("Чᶢ").!)

  def specialModifier[$: P]: P[Token] =
    parseToken(SpecialModifier, CharIn("ᵗᵜ").!)

  def branch[$: P]: P[Token] = parseToken(Branch, "|".!)

  def sbcsDecimal[$: P]: P[String] =
    P(
      (((Common.int ~~/ ("." ~~ Common.digits.?).?) |
        ("." ~~/ Common.digits.?)) ~~ "_".?).!
    )
  def sbcsNumber[$: P]: P[Token] =
    parseToken(
      Number,
      ((sbcsDecimal ~~ ("ı".! ~~ (sbcsDecimal | "_".!).?).?) |
        "ı".! ~~
        (sbcsDecimal ~ "_".!).?).!,
    ).opaque("<number (SBCS)>")

  def contextIndex[$: P]: P[Token] =
    parseToken(ContextIndex, Common.digits ~ "¤")

  def getVariable[$: P]: P[Token] = parseToken(GetVar, "#$" ~~/ Common.varName)

  def setVariable[$: P]: P[Token] = parseToken(SetVar, "#=" ~~/ Common.varName)

  def setConstant[$: P]: P[Token] =
    parseToken(Constant, "#!" ~~/ Common.varName)

  def augVariable[$: P]: P[Token] =
    parseToken(AugmentVar, "#>" ~~/ Common.varName)

  def newlines[$: P]: P[Token] = parseToken(Newline, Common.eol.!)

  def comment[$: P]: P[Token] =
    parseToken(Comment, "##" ~~/ CharsWhile(c => c != '\n' && c != '\r').!)

  def token[$: P]: P[Token] =
    P(
      comment | sugarTrigraph | syntaxTrigraph | digraph | branch |
        contextIndex | sbcsNumber | string | augVariable | getVariable |
        setVariable | setConstant | twoCharNumber | twoCharString |
        singleCharString | monadicModifier | dyadicModifier | triadicModifier |
        tetradicModifier | specialModifier | structureOpen |
        structureSingleClose | structureAllClose | listOpen | listClose |
        newlines | command
    )

  // structureDoubleClose (")") has to be here to avoid interfering with `normalGroup` in literate lexer
  override def parseAll[$: P]: P[Seq[Token]] =
    P((token | structureDoubleClose).rep ~ End)

end SBCSLexer
