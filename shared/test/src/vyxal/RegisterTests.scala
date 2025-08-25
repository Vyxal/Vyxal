package vyxal

import vyxal.conversions.{*, given}
import vyxal.elements.Elements
import vyxal.VyxalTests.testContext
import vyxal.elements.Modifiers

import org.scalatest.funspec.AnyFunSpec

class RegisterTests extends VyxalTests:

    def vSeq(elems: VAny*): Seq[VAny] = elems

    describe("Regular Register Usage") {
      testMulti(
        "5£9::++" -> VNum(27),
        "5£9::++¥" -> VNum(5),
      )
      it("Should push zero if empty") {
        given ctx: Context = Context(testMode = true)
        Interpreter.execute(AST.Command("¥"))
        ctx.peek
        assertResult(0)(0)
      }
    }
    describe("Stack Register Stuff") {
      testMulti(
      "5£ 6£,#Q Þ¥" -> List[VAny](5),
      "5ʁ ¨£ ¥ Þ¥;" -> List[VAny](5, vSeq(0,1,2,3,4,5)),
      )
    }
end RegisterTests