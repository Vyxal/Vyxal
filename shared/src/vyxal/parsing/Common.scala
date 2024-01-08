package vyxal.parsing

import vyxal.parsing.TokenType.*

import fastparse.*

private[parsing] object Common:
  def eol[$: P]: P[Unit] = P("\n" | "\r\n" | "\r")

  given Whitespace with
    def apply(implicit ctx: P[?]): P[Unit] = P(CharsWhileIn(" \t", 0))

  def int[$: P]: P[String] =
    P(("0" | (CharIn("1-9") ~~ CharsWhileIn("0-9", 0))).!)

  def digits[$: P]: P[String] = P(CharsWhileIn("0-9").!)

  def varName[$: P]: P[String] =
    P(CharIn("A-Za-z_") ~~ CharsWhileIn("0-9A-Za-z_", 0)).?.!

  def lambdaOpen[$: P]: P[String] = P(StringIn("λ", "ƛ", "Ω", "₳", "µ").!)

  def withRange[T, $: P](parser: => P[T]): P[(T, Range)] =
    P(Index ~ parser ~ Index).map {
      case (startOffset, value, endOffset) =>
        (value, Range(startOffset, endOffset))
    }

end Common
