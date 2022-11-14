package vyxal

import org.scalatest.funsuite.AnyFunSuite

import VyxalToken.*

class LexerTests extends AnyFunSuite {
  test("Does the lexer recognise numbers?") {
    assert(Lexer("123") == Right(List(Number("123"))))
  }

  test("Does the lexer recognise strings?") {
    assert(
      Lexer(""" "Hello, world!" """) == Right(List(Str("Hello, world!")))
    )
  }

  test("Does the lexer differentiate between strings and dictionary strings?") {
    assert(
      Lexer(""" "Hello, world!" """) == Right(List(Str("Hello, world!")))
    )
    assert(
      Lexer(""" "Hello, world!” """) == Right(
        List(DictionaryString("Hello, world!"))
      )
    )
  }

  test("Does the lexer recognise monadic modifiers?") {
    assert(
      Lexer("1 2 3W +/") == Right(
        List(
          Number("1"),
          Number("2"),
          Number("3"),
          Command("W"),
          Command("+"),
          MonadicModifier("/")
        )
      )
    )
  }
}
