package vyxal

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{Reader, Position, NoPosition}
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

import vyxal.impls.Elements

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

  def nonStructElement: Parser[AST] =
    literal | compositeNilad | compositeMonad | compositeDyad | command | modifier | getvar | setvar

  def element: Parser[AST] = nonStructElement | structure

  def elements: Parser[AST] =
    rep(element) ^^ { elems => AST.makeSingle(elems*) }

  def number: Parser[AST] =
    accept(
      "number",
      { case VyxalToken.Number(value) => AST.Number(VNum.from(value)) }
    )

  def compositeNilad: Parser[AST] =
    accept(
      "number",
      { case VyxalToken.CompositeNilad(value) =>
        val temp = value.map(tok => {
          val inner = VyxalParser.parse(List(tok))
          inner match {
            case Right(ast) => ast
            case Left(error) =>
              throw RuntimeException(s"Error while parsing $tok: $error")
          }
        })
        AST.CompositeNilad(temp)
      }
    )

  def compositeMonad: Parser[AST] =
    accept(
      "number",
      { case VyxalToken.CompositeMonad(value) =>
        val temp = value.map(tok => {
          val inner = VyxalParser.parse(List(tok))
          inner match {
            case Right(ast) => ast
            case Left(error) =>
              throw RuntimeException(s"Error while parsing $tok: $error")
          }
        })
        AST.Group(temp)
      }
    )

  def compositeDyad: Parser[AST] =
    accept(
      "number",
      { case VyxalToken.CompositeDyad(value) =>
        val temp = value.map(tok => {
          val inner = VyxalParser.parse(List(tok))
          inner match {
            case Right(ast) => ast
            case Left(error) =>
              throw RuntimeException(s"Error while parsing $tok: $error")
          }
        })
        AST.Group(temp)
      }
    )

  def string: Parser[AST] =
    accept("string", { case VyxalToken.Str(value) => AST.Str(value) })

  def command: Parser[AST] =
    accept("command", { case VyxalToken.Command(value) => AST.Command(value) })

  def getvar: Parser[AST] =
    accept("getvar", { case VyxalToken.GetVar(value) => AST.GetVar(value) })

  def setvar: Parser[AST] =
    accept("setvar", { case VyxalToken.SetVar(value) => AST.SetVar(value) })

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
    open ~ repsep(elements, VyxalToken.Branch) <~ opt(
      close | not(
        not(VyxalToken.StructureAllClose)
      )
    ) ^^ { case open ~ branches =>
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
              AST.For(Some(toValidName(cond.toVyxal)), body)
            case List(body) => AST.For(None, body)
            case _          => ???
          }
        case "λ" | "ƛ" | "Ω" | "₳" | "µ" =>
          val lambda = branches match {
            // todo actually parse arity and parameters
            case List(body) => AST.Lambda(1, List.empty, body)
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
        Modifiers.modifiers(modifier).impl(List(elem1))
      }

  def dyadicModifier =
    accept(
      "Dyadic modifier",
      { case VyxalToken.DyadicModifier(value) => value }
    )
      ~ element ~ element ^^ { case modifier ~ elem1 ~ elem2 =>
        Modifiers.modifiers(modifier).impl(List(elem1, elem2))
      }

  def triadicModifier =
    accept(
      "Triadic modifier",
      { case VyxalToken.TriadicModifier(value) => value }
    )
      ~ element ~ element ~ element ^^ {
        case modifier ~ elem1 ~ elem2 ~ elem3 =>
          Modifiers.modifiers(modifier).impl(List(elem1, elem2, elem3))
      }

  def tetradicModifier =
    accept(
      "Tetradic modifier",
      { case VyxalToken.TetradicModifier(value) => value }
    )
      ~ element ~ element ~ element ~ element ^^ {
        case modifier ~ elem1 ~ elem2 ~ elem3 ~ elem4 =>
          Modifiers.modifiers(modifier).impl(List(elem1, elem2, elem3, elem4))
      }

  def parse(code: String): Either[VyxalCompilationError, AST] = {
    Lexer(code).flatMap { tokens =>
      val reader = new VyxalTokenReader(preprocess(tokens))
      (parseAll(reader): @unchecked) match {
        case Success(result, _) => Right(postprocess(result))
        case NoSuccess(msg, _)  => Left(VyxalCompilationError(msg))
      }
    }
  }

  def parse(code: List[VyxalToken]): Either[VyxalCompilationError, AST] = {
    val reader = new VyxalTokenReader(preprocess(code))
    (parseAll(reader): @unchecked) match {
      case Success(result, _) => Right(postprocess(result))
      case NoSuccess(msg, _)  => Left(VyxalCompilationError(msg))
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
    val grouped = Stack[VyxalToken]()
    for token <- processed do {
      token match {
        case VyxalToken.Command(value) => {
          getArity(value) match {
            case 0 => grouped += VyxalToken.CompositeNilad(List(token))
            case 1 => {
              grouped.lastOption match {
                case None => grouped.push(token)
                case Some(VyxalToken.CompositeNilad(tokens)) => {
                  val top = grouped.pop()
                  grouped.push(VyxalToken.CompositeNilad(List(top, token)))
                }
                case _ => {
                  val top = grouped.pop
                  isNilad(top) match {
                    case true => {
                      grouped.push(VyxalToken.CompositeNilad(List(top, token)))
                    }
                    case false => {
                      grouped.push(top)
                      grouped.push(token)
                    }
                  }
                }
              }
            }
            case 2 => {
              grouped.lastOption match {
                case None => grouped.push(token)
                case _ => {
                  val top = grouped.pop
                  grouped.lastOption match {
                    case None => {
                      if (isNilad(top)) {
                        grouped.push(
                          VyxalToken.CompositeMonad(List(top, token))
                        )
                      } else {
                        grouped.push(top)
                        grouped.push(token)
                      }
                    }
                    case _ => {
                      val top2 = grouped.pop
                      (isNilad(top), isNilad(top2)) match {
                        case (true, true) =>
                          grouped.push(
                            VyxalToken.CompositeNilad(List(top, top2, token))
                          )
                        case (true, false) =>
                          grouped.push(top2)
                          grouped.push(
                            VyxalToken.CompositeMonad(List(top, token))
                          )
                        case (_, _) => {
                          grouped.push(top2)
                          grouped.push(top)
                          grouped.push(token)
                        }
                      }
                    }
                  }

                }
              }
            }
            case 3 => {
              val top = if (grouped.nonEmpty) grouped.pop else null
              val top2 = if (grouped.nonEmpty) grouped.pop else null
              val top3 = if (grouped.nonEmpty) grouped.pop else null

              (top, top2, top3) match {
                case (_, _, null) => {
                  (isNilad(top), isNilad(top2)) match {
                    case (true, true) =>
                      grouped.push(
                        VyxalToken.CompositeMonad(List(top, top2, token))
                      )
                    case (true, false) =>
                      grouped.push(top2)
                      grouped.push(
                        VyxalToken.CompositeDyad(List(top, token))
                      )
                    case (_, _) => {
                      grouped.push(top2)
                      grouped.push(top)
                      grouped.push(token)
                    }
                  }
                }
                case (_, null, null) => {
                  if (isNilad(top)) {
                    grouped.push(
                      VyxalToken.CompositeDyad(List(top, token))
                    )
                  } else {
                    grouped.push(top)
                    grouped.push(token)
                  }
                }
                case (null, null, null) => {
                  grouped.push(token)
                }
                case _ => {
                  (isNilad(top), isNilad(top2), isNilad(top3)) match {
                    case (true, true, true) =>
                      grouped.push(
                        VyxalToken.CompositeNilad(List(top, top2, top3, token))
                      )
                    case (true, true, false) =>
                      grouped.push(top3)
                      grouped.push(
                        VyxalToken.CompositeMonad(List(top, top2, token))
                      )
                    case (true, false, false) =>
                      grouped.push(top3)
                      grouped.push(top2)
                      grouped.push(
                        VyxalToken.CompositeDyad(List(top, token))
                      )
                    case (_, _, _) => {
                      grouped.push(top3)
                      grouped.push(top2)
                      grouped.push(top)
                      grouped.push(token)
                    }
                  }
                }
              }
            }
            case _ => grouped += token
          }
        }
        case _ => grouped += token
      }
    }
    grouped.reverse.toList
  }

  private def postprocess(asts: AST): AST = {
    // Move all Numbers, Strings, Lists and Nilads to the end
    val temp = asts match {
      case AST.Group(elems) => {

        val nilads = elems.reverse.takeWhile {
          case AST.Number(value) => true
          case AST.Str(value)    => true
          case AST.Lst(value)    => true
          case AST.Command(cmd) =>
            Elements.elements.get(cmd) match {
              case Some(elem) =>
                elem.arity match {
                  case Some(value) => value == 0
                  case _           => false
                }
              case _ => false
            }
          case AST.CompositeNilad(elems) => true
          case _                         => false
        }.reverse

        // remove nilads from end of elems
        val elemsWithoutNilads = elems.dropRight(nilads.length)
        AST.Group(nilads ++ elemsWithoutNilads)
      }
      case _ => asts
    }
    // replace all composite nilads with groups
    temp match {
      case AST.Group(elems) => {
        AST.Group(
          elems.map {
            case AST.CompositeNilad(elems) => AST.Group(elems)
            case x                         => x
          }
        )
      }
      case AST.CompositeNilad(elems) => AST.Group(elems)
      case x                         => x
    }
  }
}

def getArity(cmd: String): Int = {
  val elem = Elements.elements.get(cmd)
  elem match {
    case Some(value) =>
      value.arity match {
        case Some(value) => value
        case _           => -1
      }
    case _ => -1
  }
}

def isNilad(token: VyxalToken): Boolean = {
  token match {
    case VyxalToken.Command(value) => {
      val elem = Elements.elements.get(value)
      elem match {
        case Some(value) =>
          value.arity match {
            case Some(value) => value == 0
            case _           => false
          }
        case _ => false
      }
    }
    case VyxalToken.CompositeNilad(value) => true
    case VyxalToken.Number(_)             => true
    case VyxalToken.Str(_)                => true
    case _                                => false
  }
}
