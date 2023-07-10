package vyxal.lexer

import vyxal.lexer.TokenType.*

import fastparse.*
import fastparse.NoWhitespace.*

object Common:
  def ws[$: P]: P[Unit] = P(CharsWhileIn(" \t\r\f"))

  def eol[$: P]: P[Unit] = P("\n" | "\r\n" | "\r")

  def ignored[$: P]: P[Unit] = P((ws | ("##" ~ (!eol ~ AnyChar).rep)).rep)

  def int[$: P]: P[String] = P(
    ("0" | (CharIn("1-9") ~ digits)).!
  )

  def digits[$: P]: P[String] = P(CharIn("0-9").rep.!)

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
