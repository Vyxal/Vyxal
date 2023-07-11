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

  def varName[$: P]: P[String] =
    (CharIn("A-Za-z_") ~ CharsWhileIn("0-9A-Za-z_")).!

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
    withRange(tokenParser)
      .map { (value, range) =>
        Token(tokenType, value, range)
      }
      .opaque(tokenType.toString)
end Common
