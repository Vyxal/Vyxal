package vyxal

import vyxal.conversions.{*, given}

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.Checkpoints.Checkpoint

class RegisterTests extends VyxalTests:

  def vSeq(elems: VAny*): Seq[VAny] = elems

  describe("Regular Register Usage") {
    testMulti(
      "5£9::++" -> VNum(27),
      "1 5ʁ Þ£" -> VNum(1),
      "5£9::++`" -> VNum(5),
      """ "hello" £ " world"`$+ """ -> VStr("hello world"),
      "4w £ Þ¥ " -> vSeq(vSeq(4)),
      "4 5; £ 1 2; ` " -> vSeq(4, 5),
      "4 5; £ 1 2; " -> vSeq(1, 2),
      """  "vyxal " "cool" "is "⎇ £ J ¥ J """ -> VStr("vyxal is cool"),
    )
    it("Should pop after vectorizing") {
      given ctx: Context = Context(testMode = true)
      Interpreter.execute("5ʁ Þ£")
      assert(ctx.isStackEmpty)
    }

    it("Should push zero if empty") {
      given ctx: Context = Context(testMode = true)
      Interpreter.execute("5£")
      val cpt = Checkpoint()
      cpt { assert(ctx.isStackEmpty) }
      Interpreter.execute("``")
      cpt { assertResult(Seq[VAny](0, 5))(Seq(ctx.pop(), ctx.pop())) }
      cpt.reportAll()
    }
  }
  describe("Stack Register Stuff") {
    testMulti(
      "5£ 6£ Þ¥ Þ¥;" -> vSeq(vSeq(5, 6), 0),
      "5ʁ Þ£ ¥ Þ¥;" -> vSeq(5, vSeq(0, 1, 2, 3, 4, 5)),
      "5ʁ Þ£ Þ^ Þ¥" -> vSeq(5, 4, 3, 2, 1, 0),
      "5ʁ Þ£ Þ_ Þ¥" -> VNum(0),
    )
  }
  describe("Indexing") {
    testMulti(
      "5ʁ Þ£ 1Þ⦷" -> VNum(1),
      "5ʁ Þ£ 13Þ⦷" -> VNum(1),
      "5ʁ Þ£ 1N Þ⦷" -> VNum(5),
    )
  }
  describe("Function behavior") {
    describe("Applying to head") {
      testMulti(
        "5£ 6£ λ2+}Ͼ ` `;" -> vSeq(8, 5),
        "5 6;£ λ2+}Ͼ `" -> vSeq(7, 8),
        "nf £ λλ+}RN}Ͼ `" -> VStr("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
      )
    }
    describe("Don't Pop Functions") {
      testMulti(
        "5ʁ Þ£ λ2+}£  Þ¥" -> vSeq(0, 1, 2, 3, 4, 5),
        "λ2+}£ λ2+}£  Þ¥" -> Seq.empty,
      )
    }
    describe("Apply to specific indexes") {
      testMulti(
        "5ʁ Þ£ 2 λT} ÞϾ Þ¥" -> vSeq(0, 1, 6, 3, 4, 5),
        "5ʁ Þ£ λT} 2 ÞϾ Þ¥" -> vSeq(0, 1, 6, 3, 4, 5),
        "5ʁ Þ£ λT} 1 2; ÞϾ Þ¥" -> vSeq(0, 3, 6, 3, 4, 5),
        "5ʁ Þ£ 1 2; λT} ÞϾ Þ¥" -> vSeq(0, 3, 6, 3, 4, 5),
      )
    }
    describe("Null behavior") {
      testMulti(
        "1 5ʁ Þ£ 1 2; λT} ÞϾ" -> VNum(1)
      )
    }
    describe("Mapping") {
      testMulti(
        "5ʁ Þ£ λ2+}⎘Þ¥" -> vSeq(2, 3, 4, 5, 6, 7),
        "5ʁ Þ£ λ2}⎘Þ¥" -> vSeq(2, 2, 2, 2, 2, 2),
      )
    }
    describe("Should apply functions after popping them") {
      testMulti(
        " Þ_ λ2+}£ 6 5+:" -> VNum(11),
        " Þ_ λ2+}£ 6 5+:¥" -> VNum(13),
      )
    }

    it("Global context") {
      testCode("5ʁ Þ£ 5 11R Þ^ƛ`}", vSeq(0, 1, 2, 3, 4, 5))
    }
  }

end RegisterTests
