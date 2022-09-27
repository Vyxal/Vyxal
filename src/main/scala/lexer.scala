import scala.util.parsing.combinator._

sealed trait VyxalToken
trait WorkflowCompilationError
case class WorkflowLexerError(msg: String) extends WorkflowCompilationError

case class NUMBER(value: String) extends VyxalToken
case class STRING(value: String) extends VyxalToken
case class COMMAND(value: String) extends VyxalToken

object lexer extends RegexParsers {
    override def skipWhitespace = true
    def number: Parser[NUMBER] = """[0-9]+""".r ^^ { case value => NUMBER(value) }
    def string: Parser[STRING] = """"[^"]*"""".r ^^ { case value => STRING(value.substring(1, value.length - 1)) }
    def command: Parser[COMMAND] = """[a-zA-Z+\-*/]""".r ^^ { case value => COMMAND(value) }

    def tokens: Parser[List[VyxalToken]] = phrase(rep1(number | string | command)) ^^ { case tokens => tokens }
    def apply(code: String): Either[WorkflowLexerError, List[VyxalToken]] = {
    parse(tokens, code) match {
      case NoSuccess(msg, next) => Left(WorkflowLexerError(msg))
      case Success(result, next) => Right(result)
    }
  }
}