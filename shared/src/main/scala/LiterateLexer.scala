package vyxal

import java.util.regex.Pattern
import scala.util.matching.Regex
import scala.util.parsing.combinator.*
import LiterateToken.*

enum LiterateToken(val value: String):
  case Word(override val value: String) extends LiterateToken(value)
  case AlreadyCode(override val value: String) extends LiterateToken(value)
  case LitComment(override val value: String) extends LiterateToken(value)
  case LambdaBlock(override val value: String) extends LiterateToken(value)

object LiterateLexer extends RegexParsers:
  override def skipWhitespace = true
  override val whiteSpace: Regex = "[ \t\r\f]+".r

  private def decimalRegex = raw"((0|[1-9][0-9]*)?\.[0-9]*|0|[1-9][0-9]*)"
  def number: Parser[LiterateToken] =
    raw"($decimalRegex?ı$decimalRegex?)|$decimalRegex".r ^^ { value =>
      AlreadyCode(value)
    }

  def string: Parser[LiterateToken] = raw"""("(?:[^"\\]|\\.)*["])""".r ^^ {
    value => Word(value.replaceAll("\\\\\"", "\""))
  }

  def singleCharString: Parser[LiterateToken] = """'.""".r ^^ { value =>
    Word(value)
  }

  def comment: Parser[LiterateToken] = """##[^\n]*""".r ^^ { value =>
    LitComment(value)
  }

  def lambdaBlock: Parser[LiterateToken] =
    """\{""".r ~ rep(lambdaBlock | """[^{}]+""".r) ~ """\}""".r ^^ {
      case _ ~ body ~ _ => LambdaBlock(body.mkString)
    }
  def normalGroup: Parser[LiterateToken] =
    """\(""".r ~ rep(normalGroup | """[^()]+""".r) ~ """\)""".r ^^ {
      case _ ~ body ~ _ => Word(body.mkString)
    }

  def word: Parser[LiterateToken] =
    """[a-zA-Z][a-zA-Z0-9?!*+=&%><\\-]*""".r ^^ { value =>
      Word(value)
    }

  def tokens: Parser[List[LiterateToken]] = phrase(
    rep(
      number | string | singleCharString | comment | lambdaBlock | normalGroup | word
    )
  )

  def apply(code: String): Either[VyxalCompilationError, List[LiterateToken]] =
    (parse(tokens, code): @unchecked) match
      case NoSuccess(msg, next)  => Left(VyxalCompilationError(msg))
      case Success(result, next) => Right(result)
end LiterateLexer

def main(args: Array[String]): Unit =
  println(LiterateLexer("1 2 3"))
