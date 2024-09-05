package vyxal

import scala.language.strictEquality

import vyxal.parsing.{Lexer, Range, StructureType, Token}
import vyxal.parsing.TokenType.*

class LexerTests extends VyxalTests:
  def testLex(input: String, expected: List[Token]) =
    assertResult(expected)(Lexer.lexSBCS(input))

  describe("Literals") {
    it("should recognize numbers") {
      group {
        testLex("123", List(Number("123")))
        testLex("6.", List(Number("6.5")))
        testLex("3.4ı1.2", List(Number("3.4ı1.2")))
        testLex("3.4ı1.", List(Number("3.4ı1.5")))
        testLex("3.4ı.2", List(Number("3.4ı0.2")))
        testLex("3.4ı.", List(Number("3.4ı0.5")))
        testLex("3.4_ı1", List(Number("_3.4ı1")))
        testLex(".ı.", List(Number("0.5ı0.5")))
        testLex("ı", List(Number("0ı1")))
        testLex("1ı1ı1ı1", List(Number("1ı1"), Number("0ı1"), Number("0ı1")))
        testLex("3.4ı", List(Number("3.4ı1")))
        testLex(".4", List(Number("0.4")))
        testLex(".", List(Number("0.5")))
        testLex("1000000_", List(Number("_1000000")))
        testLex("5.2_", List(Number("_5.2")))
        testLex("5.2ı_", List(Number("5.2ı_1")))
      }
    }
  }

  describe("Strings") {
    it("should recognize strings") {
      group {
        testLex(""" "Hello, Vyxal!" """, List(Str("Hello, Vyxal!")))
        testLex(""" "Hello, Vyxal!" """, List(Str("Hello, Vyxal!")))

        testLex(
          """ "Vyxal is what \"you\" want!" """,
          List(Str("Vyxal is what \"you\" want!")),
        )

        testLex(
          """ k"vy """,
          List(Digraph("k\""), Command("v"), Command("y")),
        )
      }
    }

    it("should differentiate between strings and dictionary strings?") {
      group {
        testLex(""" "Hello, Vyxal!" """, List(Str("Hello, Vyxal!")))

        testLex(
          """ "Hello, Vyxal!” """,
          List(DictionaryString("Hello, Vyxal!")),
        )
      }
    }

    it("should auto-close strings") {
      testLex(""" "Unclosed""", List(Str("Unclosed")))
    }
  }

  describe("Comments") {
    it("should tokenize comments after code") {
      testLex(
        "1 1 + ##Hello, Vyxal!",
        List(
          Number("1"),
          Number("1"),
          Command("+"),
        ),
      )
    }
    it("should stop comments at the newline") {
      testLex(
        "1 1 + ##Hello, Vyxal!\n 1 +",
        List(
          Number("1"),
          Number("1"),
          Command("+"),
          Token(Newline, "\n", Range.fake),
          Number("1"),
          Command("+"),
        ),
      )
    }
    it("should not treat single #s as comments") {
      testLex("1 #a", List(Number("1"), Digraph("#a")))
    }
    it("should handle empty comments") {
      group {
        testLex(
          "1 ##\n",
          List(Number("1"), Token(Newline, "\n", Range.fake)),
        )
        testLex(
          "1 ##",
          List(Number("1")),
        )
      }
    }
  }

  describe("Modifiers") {
    it("should recognize monadic modifiers") {
      testLex(
        "1 2 3W +/",
        List(
          Number("1"),
          Number("2"),
          Number("3"),
          Command("W"),
          Command("+"),
          MonadicModifier("/"),
        ),
      )
    }
  }

  describe("Variable digraphs") {
    it("should recognize `#$`/get var") {
      testLex("3 #$my_var +", List(Number("3"), GetVar("my_var"), Command("+")))
    }
    it("should recognize `#=`/set var") {
      testLex("42 #=answer", List(Number("42"), SetVar("answer")))
    }

    it("should recognise `#>`/augmented assignment") {
      testLex(
        "45 +#>answer",
        List(Number("45"), Command("+"), AugmentVar("answer")),
      )
    }
  }

  describe("Sugar Trigraphs") {
    it("should turn them into normal form") {
      testLex(
        "5 #.[5+",
        List(
          Number("5"),
          StructureOpen(StructureType.LambdaMap.open),
          Number("5"),
          Command("+"),
        ),
      )
    }

    they("should work inside digraphs") {
      testLex(
        "#,/#,/ ø#,/ #,/ø",
        List(Digraph("øø"), Digraph("øø"), Digraph("øø")),
      )
    }
  }

  describe("Complex tests") {
    it("should understand a basic series of tokens") {
      testLex("1 1 +", List(Number("1"), Number("1"), Command("+")))
    }
  }
end LexerTests
