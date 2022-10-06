import org.scalatest.funsuite.AnyFunSuite
class LexerTests extends AnyFunSuite:
  test("Does the lexer recognise numbers?") {
    assert(Lexer("123") == Right(List(NUMBER("123"))))
  }
  test("Does the lexer recognise strings?") {
    assert(
      Lexer(""" "Hello, world!" """) == Right(List(STRING("Hello, world!")))
    )
  }
  test("Does the lexer differentiate between strings and dictionary strings?") {
    assert(
      Lexer(""" "Hello, world!" """) == Right(List(STRING("Hello, world!")))
    )
    assert(
      Lexer(""" "Hello, world!” """) == Right(
        List(DICTIONARY_STRING("Hello, world!"))
      )
    )
  }
  test("Does the lexer recognise monadic modifiers?") {
    assert(
      Lexer("1 2 3W +/") == Right(
        List(
          NUMBER("1"),
          NUMBER("2"),
          NUMBER("3"),
          COMMAND("W"),
          COMMAND("+"),
          MONADIC_MODIFIER("/")
        )
      )
    )
  }
end LexerTests
