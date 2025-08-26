package vyxal

import vyxal.conversions.{*, given}
import vyxal.elements.Elements
import vyxal.VyxalTests.testContext
import vyxal.elements.Modifiers

import org.scalatest.Checkpoints.Checkpoint
import org.scalatest.funspec.AnyFunSpec

class RegisterTests extends VyxalTests:

    def vSeq(elems: VAny*): Seq[VAny] = elems

    describe("Regular Register Usage") {
      testMulti(
        "5£9::++" -> VNum(27),
        "5£9::++`" -> VNum(5),
      )
      it("Should push zero if empty") {
        RegisterHelpers.clear()
        given ctx: Context = Context(testMode = true)
        ctx.push(5)
        Interpreter.execute("£")
        val checkpoint = Checkpoint()
        checkpoint { assert(ctx.isStackEmpty) }
        Interpreter.execute("``")
        checkpoint { assertResult(Seq[VAny](0,5))(Seq(ctx.pop(), ctx.pop())) }
        checkpoint.reportAll()
      }
    }
    describe("Stack Register Stuff") {
      testMulti(
        "5£ 6£ Þ¥ Þ¥;" -> vSeq(vSeq(5,6), 0),
        "Þ_ 5ʁ ¨£ ¥ Þ¥;" -> vSeq(5, vSeq(0,1,2,3,4,5)), // this fails if I don't clear the register between them
      )
    }
    describe("Register Index") {
      it("Should Index like the stack") {
        given ctx: Context = Context(testMode = true)
        Interpreter.execute("5ʁ ¨£ 1Þ⦷")
        assertResult(VNum(1))(ctx.pop())
      }
    }
end RegisterTests