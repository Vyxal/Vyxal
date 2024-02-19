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
        testLex(""" "Hello, Vyxal!""", Seq(Str("Hello, Vyxal!")))

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
  }

end VexerTests
