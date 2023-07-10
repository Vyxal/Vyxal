package vyxal.lexer

import vyxal.lexer.TokenType.*

import fastparse.*

object Common:
  def eol[$: P]: P[Unit] = P("\n" | "\r\n" | "\r")

  given Whitespace with
    def apply(implicit ctx: P[?]): P[Unit] =
      P(CharsWhileIn(" \t\r\f", 0) | "##" ~ (!eol ~ AnyChar).rep)

  def int[$: P]: P[String] = P(
    ("0" | (CharIn("1-9") ~ digits)).!
  )

  def digits[$: P]: P[String] = P(CharIn("0-9").rep.!)

  def number[$: P](
      decimal: => P[String],
      complexSep: => P[String]
  ): P[Token] =
    withRange(
      ((decimal ~ (complexSep ~ decimal).?) | complexSep ~ decimal).!
    ).map { case (value, range) => Token(Number, value, range) }

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

  def singleCharString[$: P]: P[Token] = withRange(P(("'" ~ AnyChar).!)) ^^ {
    case (value, range) =>
      Token(Str, value.substring(1), range)
  }

  def twoCharString[$: P]: P[Token] =
    withRange(P(("ᶴ".! ~ AnyChar ~ AnyChar).!)) ^^ { case (value, range) =>
      Token(Str, value.substring(1), range)
    }

  def twoCharNumber[$: P]: P[Token] =
    withRange(P(("~".! ~ AnyChar ~ AnyChar).!)) ^^ { case (value, range) =>
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

  def monadicModifier[$: P]: P[Token] =
    parseToken(MonadicModifier, s"""[$MONADIC_MODIFIERS]""".r)

  def dyadicModifier[$: P]: P[Token] =
    parseToken(DyadicModifier, s"""[$DYADIC_MODIFIERS]""".r)

  def triadicModifier[$: P]: P[Token] =
    parseToken(TriadicModifier, s"""[$TRIADIC_MODIFIERS]""".r)

  def tetradicModifier[$: P]: P[Token] =
    parseToken(TetradicModifier, s"""[$TETRADIC_MODIFIERS]""".r)

  def specialModifier[$: P]: P[Token] =
    parseToken(SpecialModifier, s"""[$SPECIAL_MODIFIERS]""".r)

  def branch[$: P]: P[Token] = parseToken(Branch, "|".!)

  def withInd[T, $: P](parser: => P[T]): P[(Int, T)] =
    P(Index ~ parser)

  def withRange[T, $: P](parser: => P[T]): P[(T, Range)] =
    P(Index ~ parser ~ Index).map { case (startOffset, value, endOffset) =>
      (value, Range(startOffset, endOffset))
    }

  def parseToken[$: P](
      tokenType: TokenType,
      tokenParser: => P[String]
  ): P[Token] =
    withRange(tokenParser).map { (value, range) =>
      Token(tokenType, value, range)
    }
end Common
