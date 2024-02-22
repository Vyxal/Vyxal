package vyxal

import scala.language.strictEquality

import vyxal.parsing.{VRange, VStructureType, VToken, Vexer}
import vyxal.parsing.VTokenType.*

class VexerTests extends VyxalTests:
  def testLex(input: String, expected: Seq[VToken]) =
    assertResult(expected)(Vexer.lexSBCS(input))

  describe("Literals") {
    it("should recognize numbers") {
      group {
        testLex("123", Seq(Number("123")))
        testLex("6.", Seq(Number("6.5")))
        testLex("3.4ı1.2", Seq(Number("3.4ı1.2")))
        testLex("3.4ı1.", Seq(Number("3.4ı1.5")))
        testLex("3.4ı.2", Seq(Number("3.4ı0.2")))
        testLex("3.4ı.", Seq(Number("3.4ı0.5")))
        testLex(".ı.", Seq(Number("0.5ı0.5")))
        testLex("3.4ı", Seq(Number("3.4ı")))
        testLex(".4", Seq(Number("0.4")))
        testLex(".", Seq(Number("0.5")))
        testLex("1000000_", Seq(Number("_1000000")))
        testLex("5.2_", Seq(Number("_5.2")))
        testLex("5.2ı_", Seq(Number("5.2ı_")))
      }
    }
    it("should recognize strings") {
      group {
        testLex(""" "Hello, Vyxal!" """, Seq(Str("Hello, Vyxal!")))
        testLex(
          """ "Vyxal is what \"you\" want!" """,
          Seq(Str("Vyxal is what \"you\" want!")),
        )

        testLex(
          """ k"vy """,
          Seq(Digraph("k\""), MonadicModifier("v"), Command("y")),
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
          VToken(Newline, "\n", VRange.fake),
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
          VToken(Newline, "\n", VRange.fake),
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
          List(Number("1"), VToken(Newline, "\n", VRange.fake)),
        )
        testLex(
          "1 ##",
          List(Number("1"), VToken(Newline, "\n", VRange.fake)),
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
      testLex("3 #$my_var +", Seq(Number("3"), GetVar("my_var"), Command("+")))
    }
    it("should recognize `#=`/set var") {
      testLex("42 #=answer", Seq(Number("42"), SetVar("answer")))
    }

    it("should recognise `#>`/augmented assignment") {
      testLex(
        "45 +#>answer",
        Seq(Number("45"), Command("+"), AugmentVar("answer")),
      )
    }
  }

  describe("Sugar Trigraphs") {
    it("should turn them into normal form") {
      testLex(
        "5 #.[5+",
        Seq(
          Number("5"),
          StructureOpen(VStructureType.LambdaMap.open),
          Number("5"),
          Command("+"),
        ),
      )
    }

    they("should work inside digraphs") {
      testLex(
        "#,/#,/ ø#,/ #,/ø",
        Seq(Digraph("øø"), Digraph("øø"), Digraph("øø")),
      )
    }
  }

  describe("Complex tests") {
    it("should understand a basic series of tokens") {
      testLex("1 1 +", List(Number("1"), Number("1"), Command("+")))
    }
  }

end VexerTests
