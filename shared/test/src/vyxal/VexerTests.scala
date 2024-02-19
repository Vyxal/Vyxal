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
        testLex("3.4ı1.", Seq(Number("3.4ı1.")))
        testLex("3.4ı.2", Seq(Number("3.4ı.2")))
        testLex("3.4ı.", Seq(Number("3.4ı.5")))
        testLex(".ı.", Seq(Number(".ı.")))
        testLex("3.4ı", Seq(Number("3.4ı")))
        testLex(".4", Seq(Number(".4")))
        testLex(".", Seq(Number(".")))
        testLex("1000000_", Seq(Number("1000000_")))
        testLex("5.2_", Seq(Number("5.2_")))
        testLex("5.2ı_", Seq(Number("5.2ı_")))
      }
    }
  }

end VexerTests
