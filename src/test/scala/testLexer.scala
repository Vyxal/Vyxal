import org.scalatest.funsuite.AnyFunSuite
class LexerTests extends AnyFunSuite:
    test("Does the lexer recognise numbers?") {
        assert(lexer("123") == Right(List(NUMBER("123"))))
    }
    test("Does the lexer recognise strings?") {
        assert(lexer(""" "Hello, world!" """) == Right(List(STRING("Hello, world!"))))
    }
    test("Does the lexer differentiate between strings and dictionary strings?"){
        assert(lexer(""" "Hello, world!" """) == Right(List(STRING("Hello, world!"))))
        assert(lexer(""" "Hello, world!” """) == Right(List(DICTIONARY_STRING("Hello, world!"))))
    }
end LexerTests