package vyxal.parsing

import scala.language.strictEquality

import vyxal.{Elements, Modifiers, SugarMap}
import vyxal.{LeftoverCodeException, VyxalLexingException, VyxalYikesException}
import vyxal.parsing.Common.given // For custom whitespace
import vyxal.parsing.Common.withRange
import vyxal.parsing.TokenType.*

import fastparse.*

private[parsing] object SBCSLexer:
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
      StringIn("[", "{", "(", "#{", "Ḍ", "Ṇ", "#::").! | Common.lambdaOpen,
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
        (CharIn("∆øÞk") ~~ !CharIn("|") ~~ AnyChar).! |
          ("#" ~~ !CharIn("[]$!=#>@{:") ~~ AnyChar).!
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
            case arity =>
              throw VyxalYikesException(s"Invalid modifier arity: $arity")
          Token(tokenType, digraph, range)
        else Token(Digraph, digraph, range)
    }

  def unpackTrigraph[$: P]: P[Token] = parseToken(UnpackTrigraph, "#:[".!)

  def sugarTrigraph[$: P]: P[Token] =
    withRange(("#" ~~ CharIn(".,^") ~~ AnyChar).!).map {
      case (value, range) =>
        this.sugarUsed = true
        SugarMap.trigraphs
          .get(value)
          .flatMap(char => Some(this.lex(char)).map(_.head))
          .getOrElse(Token(Command, value, range))
    }

  private val allCommands =
    (Lexer.Codepage.replaceAll(raw"[|\[\](){}\s]", "") +
      Lexer.UnicodeCommands).toSet

  def command[$: P]: P[Token] = parseToken(Command, CharPred(allCommands).!)

  def monadicModifier[$: P]: P[Token] =
    // parseToken(MonadicModifier, CharIn("ᵃᵇᶜᵈᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻ¿⸠/@").!)
    parseToken(MonadicModifier, CharIn("ᵃᵇᶜᵈᴴᶤᶨᵏᶪᵐⁿᵒᵖᴿᶳᵗᵘᵛᵂᵡᵞᶻ¿⸠/").!)

  def dyadicModifier[$: P]: P[Token] =
    parseToken(DyadicModifier, CharIn("ϩ∥∦ᵉ").!)

  def triadicModifier[$: P]: P[Token] =
    parseToken(TriadicModifier, CharIn("эᶠ").!)

  def tetradicModifier[$: P]: P[Token] =
    parseToken(TetradicModifier, CharIn("Чᴳ").!)

  def specialModifier[$: P]: P[Token] =
    parseToken(SpecialModifier, CharIn("ᵜ").!)

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

  def modifierSymbol[$: P]: P[Token] =
    parseToken(ModifierSymbol, "#:`" ~~/ Common.varName)

  def elementSymbol[$: P]: P[Token] =
    parseToken(ElementSymbol, "#:@" ~~/ Common.varName)

  def originalSymbol[$: P]: P[Token] =
    parseToken(OriginalSymbol, "#:~" ~ CharPred(allCommands).!)

  def defineObj[$: P]: P[Token] =
    parseToken(DefineObject, "#:O" ~~/ Common.varName)

  def defineExtension[$: P]: P[Token] =
    parseToken(
      DefineExtension,
      "#:>>".!,
    )

  def setConstant[$: P]: P[Token] =
    parseToken(Constant, "#!" ~~/ Common.varName)

  def augVariable[$: P]: P[Token] =
    parseToken(AugmentVar, "#>" ~~/ Common.varName)

  def newlines[$: P]: P[Token] = parseToken(Newline, Common.eol.!)

  def comment[$: P]: P[Token] =
    parseToken(Comment, "##" ~~/ CharsWhile(c => c != '\n' && c != '\r').?.!)

  def token[$: P]: P[Token] =
    P(
      comment | sugarTrigraph | unpackTrigraph | digraph | branch |
        defineExtension | modifierSymbol | defineObj | elementSymbol |
        originalSymbol | contextIndex | sbcsNumber | string | augVariable |
        getVariable | setVariable | setConstant | twoCharNumber |
        twoCharString | singleCharString | monadicModifier | dyadicModifier |
        triadicModifier | tetradicModifier | specialModifier | structureOpen |
        structureSingleClose | structureAllClose | listOpen | listClose |
        newlines | command
    )

  def parseToken[$: P](
      tokenType: TokenType,
      tokenParser: => P[String],
  ): P[Token] =
    withRange(tokenParser)
      .map { (value, range) =>
        Token(tokenType, value, range)
      }
      .opaque(tokenType.toString)
  // structureDoubleClose (")") has to be here to avoid interfering with `normalGroup` in literate lexer
  def parseAll[$: P]: P[Seq[Token]] =
    P((token | structureDoubleClose).rep ~ End)

  def lex(code: String): List[Token] =
    parse(code, this.parseAll) match
      case Parsed.Success(res, ind) =>
        if ind == code.length then res.toList
        else throw LeftoverCodeException(code.substring(ind))
      case f @ Parsed.Failure(label, index, extra) =>
        val trace = f.trace()
        throw VyxalLexingException(trace.longMsg)
end SBCSLexer
