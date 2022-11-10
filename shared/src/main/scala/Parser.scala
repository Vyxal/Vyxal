package vyxal

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}
import scala.collection.mutable.ListBuffer

class VyxalTokenReader(tokens: Seq[VyxalToken]) extends Reader[VyxalToken] {
  override def first: VyxalToken = tokens.head
  override def atEnd: Boolean = tokens.isEmpty
  override def pos: Position = NoPosition
  override def rest: Reader[VyxalToken] = new VyxalTokenReader(tokens.tail)
}

object VyxalParser extends Parsers {
  type Elem = VyxalToken

  // The VyxalToken.StructureAllClose.? is to get rid of leftover ']'s from parsing structures
  def parseAll: Parser[AST] = phrase(
    rep(element <~ VyxalToken.StructureAllClose.?)
  ) ^^ { asts => AST.makeSingle(asts*) }

  def literal = number | string | listStructure

  def nonStructElement: Parser[AST] = literal | command | modifier

  def element: Parser[AST] = nonStructElement | structure

  def elements: Parser[AST] =
    rep(element) ^^ { elems => AST.makeSingle(elems*) }

  def number: Parser[AST] =
    accept(
      "number",
      { case VyxalToken.Number(value) => AST.Number(VNum.from(value)) }
    )

  def string: Parser[AST] =
    accept("string", { case VyxalToken.Str(value) => AST.Str(value) })

  def command: Parser[AST] =
    accept("command", { case VyxalToken.Command(value) => AST.Command(value) })

  def open = accept("open", { case VyxalToken.StructureOpen(open) => open })

  def close =
    accept("close", { case VyxalToken.StructureClose(close) => close })

  def listStructure =
    VyxalToken.ListOpen
      ~>! repsep(elements, VyxalToken.Branch)
      <~! VyxalToken.ListClose ^^ { elems => AST.Lst(elems) }

  // not(not(VyxalToken.StructureAllClose)) is used to match that token
  // without consuming it.
  // TODO see if there's a builtin way to do it
  def structure: Parser[AST] =
    open ~ repsep(elements, VyxalToken.Branch) <~ (close | not(
      not(VyxalToken.StructureAllClose)
    )) ^^ { case open ~ branches =>
      (open: @unchecked) match {
        case "[" =>
          branches match {
            case List(thenBody, elseBody) => AST.If(thenBody, Some(elseBody))
            case List(thenBody)           => AST.If(thenBody, None)
            case _ => ??? // todo what to do if too many branches?
          }
        case "{" =>
          branches match {
            case List(cond, body) => AST.While(Some(cond), body)
            case List(body)       => AST.While(None, body)
            case _                => ???
          }
        case "(" =>
          branches match {
            case List(cond, body) =>
              // todo come up with a better solution than simply stripping out
              // non-alphanumeric characters?
              AST.For(Some(toValidName(cond.toString)), body)
            case List(body) => AST.For(None, body)
            case _          => ???
          }
        case "λ" | "ƛ" | "Ω" | "₳" | "µ" =>
          val lambda = branches match {
            case List(body) => AST.Lambda(body)
            case _          => ???
          }
          // todo using the command names is a bit brittle
          //   maybe refer to the functions directly
          (open: @unchecked) match {
            case "λ" => lambda
            case "ƛ" => AST.makeSingle(lambda, AST.Command("M"))
            case "Ω" => AST.makeSingle(lambda, AST.Command("F"))
            case "₳" => AST.makeSingle(lambda, AST.Command("R"))
            case "µ" => AST.makeSingle(lambda, AST.Command("ṡ"))
          }
      }
    }

  def modifier =
    monadicModifier | dyadicModifier | triadicModifier | tetradicModifier

  def monadicModifier =
    accept(
      "Monadic modifier",
      { case VyxalToken.MonadicModifier(value) => value }
    )
      ~ element ^^ { case modifier ~ elem1 =>
        AST.MonadicModifier(modifier, elem1)
      }

  def dyadicModifier =
    accept(
      "Dyadic modifier",
      { case VyxalToken.DyadicModifier(value) => value }
    )
      ~ element ~ element ^^ { case modifier ~ elem1 ~ elem2 =>
        AST.DyadicModifier(modifier, elem1, elem2)
      }

  def triadicModifier =
    accept(
      "Triadic modifier",
      { case VyxalToken.TriadicModifier(value) => value }
    )
      ~ element ~ element ~ element ^^ {
        case modifier ~ elem1 ~ elem2 ~ elem3 =>
          AST.TriadicModifier(modifier, elem1, elem2, elem3)
      }

  def tetradicModifier =
    accept(
      "Tetradic modifier",
      { case VyxalToken.TetradicModifier(value) => value }
    )
      ~ element ~ element ~ element ~ element ^^ {
        case modifier ~ elem1 ~ elem2 ~ elem3 ~ elem4 =>
          AST.TetradicModifier(modifier, elem1, elem2, elem3, elem4)
      }

  def parse(code: String): Either[VyxalCompilationError, AST] = {
    Lexer(code).flatMap { tokens =>
      val reader = new VyxalTokenReader(preprocess(tokens))
      parseAll(reader) match {
        case Success(result, _) => Right(result)
        case NoSuccess(msg, _)  => Left(VyxalCompilationError(msg))
      }
    }
  }

  def parseInput(input: String): VAny = {
    Lexer(input).toOption
      .flatMap { tokens =>
        val reader = new VyxalTokenReader(preprocess(tokens))
        literal(reader) match {
          case Success(result, _) =>
            result match {
              case AST.Number(value) => Some[VAny](value)
              case AST.Str(str)      => Some(str)
              case _                 => None
            }
          case _ => None
        }
      }
      .getOrElse(input)
  }

  /** Filter out the alphanumeric characters from a given string to get a valid
    * name. Leading digits are dropped.
    */
  private def toValidName(name: String): String =
    name.filter(_.isLetterOrDigit).dropWhile(!_.isLetter)

  private def preprocess(code: Seq[VyxalToken]): List[VyxalToken] = {
    val processed = ListBuffer[VyxalToken]()

    code.foreach {
      case VyxalToken.StructureClose(")") => {
        processed += VyxalToken.StructureClose("}")
        processed += VyxalToken.StructureClose("}")
      }
      case x => processed += x
    }
    processed.toList
  }
}
