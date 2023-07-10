package vyxal.catslexer

import scala.language.strictEquality

import vyxal.catslexer.TokenType.*
import vyxal.impls.Elements

import scala.util.matching.Regex

import cats.parse.Parser as P
import cats.parse.Rfc5234.{digit, wsp}

private[catslexer] object SBCSLexer:
  import vyxal.Lexer.decimalRegex

  override val whiteSpace: Regex = "[ \t\r\f]+".r

  /** Whether the code lexed so far has sugar trigraphs */
  var sugarUsed = false

  def lex(code: String): Either[VyxalCompilationError, List[Token]] =
    tokens.parseAll(code).left.map(err => VyxalCompilationError(err.toString()))

  def number: P[Token] =
    parseToken(Number, raw"($decimalRegex?ı($decimalRegex|_)?)|$decimalRegex".r)

  def string: P[Token] =
    withRange(raw"""("(?:[^"„”“\\]|\\.)*["„”“])""".r) ^^ {
      case (value, range) =>
        // If the last character of each token is ", then it's a normal string
        // If the last character of each token is „, then it's a compressed string
        // If the last character of each token is ”, then it's a dictionary string
        // If the last character of each token is “, then it's a compressed number

        // So replace the non-normal string tokens with the appropriate token type

        // btw thanks to @pxeger and @mousetail for the regex

        val text = value
          .substring(1, value.length - 1)
          .replace("\\\"", "\"")
          .replace(raw"\n", "\n")
          .replace(raw"\t", "\t")

        val tokenType = (value.last: @unchecked) match
          case '"' => Str
          case '„' => CompressedString
          case '”' => DictionaryString
          case '“' => CompressedNumber

        Token(tokenType, text, range)
    }

  def contextIndex: P[Token] = withRange("""\d*¤""".r) ^^ {
    case (value, range) =>
      Token(ContextIndex, value.substring(0, value.length - 1).trim, range)
  }

  def singleCharString: P[Token] = withRange("'.".r) ^^ { case (value, range) =>
    Token(Str, value.substring(1), range)
  }

  def twoCharString: P[Token] = withRange("ᶴ..".r) ^^ { case (value, range) =>
    Token(Str, value.substring(1), range)
  }

  def twoCharNumber: P[Token] = withRange("~..".r) ^^ { case (value, range) =>
    Token(
      Number,
      value
        .substring(1)
        .zipWithIndex
        .map((c, ind) => math.pow(CODEPAGE.length, ind) * CODEPAGE.indexOf(c))
        .sum
        .toString,
      range
    )
  }

  def structureOpen: P[Token] =
    parseToken(StructureOpen, Lexer.structureOpenRegex.r)

  def structureSingleClose: P[Token] = parseToken(StructureClose, "}")

  def structureDoubleClose: P[Token] =
    parseToken(StructureDoubleClose, ")")

  def structureAllClose: P[Token] =
    parseToken(StructureAllClose, "]")

  def listOpen: P[Token] = parseToken(ListOpen, """(#\[)|⟨""".r)

  def listClose: P[Token] = parseToken(ListClose, """#]|⟩""".r)

  def digraph: P[Token] = withRange("[∆øÞk].|#[^\\[\\]$!=#>@{]".r) ^^ {
    case (digraph, range) =>
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

  def syntaxTrigraph: P[Token] = parseToken(SyntaxTrigraph, "#:.".r)

  def sugarTrigraph: P[Token] =
    withRange("#[.,^].".r) ^^ { case (value, range) =>
      this.sugarUsed = true
      SugarMap.trigraphs
        .get(value)
        .flatMap(char => this.lex(char).toOption.map(_.head))
        .getOrElse(Token(Command, value, range))
    }

  private val commandRegex = CODEPAGE
    .replaceAll(raw"[|\[\](){}]", "")
    .replace("^", "\\^")
  def command: P[Token] = parseToken(Command, s"[$commandRegex🍪ඞ]".r)

  def getVariable: P[Token] = withRange("""(#\$)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(GetVar, value.substring(2), range)
  }

  def setVariable: P[Token] = withRange("""(#=)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(SetVar, value.substring(2), range)
  }

  def setConstant: P[Token] = withRange("""(#!)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(Constant, value.substring(2), range)
  }

  def augVariable: P[Token] = withRange("""(#>)[0-9A-Za-z_]*""".r) ^^ {
    case (value, range) =>
      Token(AugmentVar, value.substring(2, value.length), range)
  }

  def monadicModifier: P[Token] =
    parseToken(MonadicModifier, s"""[$MONADIC_MODIFIERS]""".r)

  def dyadicModifier: P[Token] =
    parseToken(DyadicModifier, s"""[$DYADIC_MODIFIERS]""".r)

  def triadicModifier: P[Token] =
    parseToken(TriadicModifier, s"""[$TRIADIC_MODIFIERS]""".r)

  def tetradicModifier: P[Token] =
    parseToken(TetradicModifier, s"""[$TETRADIC_MODIFIERS]""".r)

  def specialModifier: P[Token] =
    parseToken(SpecialModifier, s"""[$SPECIAL_MODIFIERS]""".r)

  def comment: P[Token] = parseToken(Comment, """##[^\n]*""".r)

  def branch: P[Token] = parseToken(Branch, "|")

  val newlines: P[Token] = parseToken(Newline, lf | crlf)

  def token: P[Token] =
    comment | sugarTrigraph | syntaxTrigraph | digraph | branch | contextIndex
      | number | string | augVariable | getVariable | setVariable
      | setConstant | twoCharNumber | twoCharString | singleCharString
      | monadicModifier | dyadicModifier | triadicModifier | tetradicModifier
      | specialModifier | structureOpen | structureSingleClose | structureAllClose
      | listOpen | listClose | newlines | command

  // structureDoubleClose (")") has to be here to avoid interfering with `normalGroup` in literate lexer
  def tokens: P[List[Token]] = rep(token | structureDoubleClose)

  def withInd[T](parser: P[T]): P[(Int, T)] = (P.index.with1 ~ parser)

  def withRange[T](parser: P[T]): P[(T, Range)] =
    (P.index.with1 ~ parser ~ P.index).map {
      case startOffset -> value -> endOffset =>
        (value, Range(startOffset, endOffset))
    }

  def parseToken(tokenType: TokenType, tokenParser: P[String]): P[Token] =
    withRange(tokenParser).map { (value, range) =>
      Token(tokenType, value, range)
    }
end SBCSLexer
