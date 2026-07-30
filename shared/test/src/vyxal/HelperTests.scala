package vyxal

import vyxal.conversions.given

import org.scalatest.tagobjects.Slow
import org.scalatest.Checkpoints.Checkpoint

class HelperTests extends VyxalTests:
  def vSeq(elems: VAny*) = elems

  describe("Partitions") {
    it("should partition infinite lists", Slow) {
      given Context = VyxalTests.testContext()
      val parts = ListHelpers.partitions(LazyList.iterate(VNum(1))(_ + 1))

      val cp = Checkpoint()

      cp { assertResult(1)(parts(0).size) }
      cp { assertResult(vSeq(1, 2, 3))(parts(0)(0).take(3)) }

      cp { assertResult(2)(parts(1).size) }
      cp { assertResult(vSeq(1))(parts(1)(0)) }
      cp { assertResult(vSeq(2, 3, 4))(parts(1)(1).take(3)) }

      cp { assertResult(3)(parts(2).size) }
      cp { assertResult(vSeq(1))(parts(2)(0)) }
      cp { assertResult(vSeq(2))(parts(2)(1)) }
      cp { assertResult(vSeq(3, 4, 5))(parts(2)(2).take(3)) }

      cp { assertResult(2)(parts(3).size) }
      cp { assertResult(vSeq(1, 2))(parts(3)(0)) }
      cp { assertResult(vSeq(3, 4, 5))(parts(3)(1).take(3)) }

      cp { assertResult(4)(parts(4).size) }
      cp { assertResult(vSeq(1))(parts(4)(0)) }
      cp { assertResult(vSeq(2))(parts(4)(1)) }
      cp { assertResult(vSeq(3))(parts(4)(2)) }
      cp { assertResult(vSeq(4, 5, 6))(parts(4)(3).take(3)) }

      cp { assertResult(3)(parts(5).size) }
      cp { assertResult(vSeq(1, 2))(parts(5)(0)) }
      cp { assertResult(vSeq(3))(parts(5)(1)) }
      cp { assertResult(vSeq(4, 5, 6))(parts(5)(2).take(3)) }

      cp { assertResult(2)(parts(6).size) }
      cp { assertResult(vSeq(1, 2, 3))(parts(6)(0)) }
      cp { assertResult(vSeq(4, 5, 6))(parts(6)(1).take(3)) }

      cp { assertResult(3)(parts(7).size) }
      cp { assertResult(vSeq(1))(parts(7)(0)) }
      cp { assertResult(vSeq(2, 3))(parts(7)(1)) }
      cp { assertResult(vSeq(4, 5, 6))(parts(7)(2).take(3)) }

      cp.reportAll()
    }
  }

  describe("Collect By Annotations") {
    it("should collect functions correctly") {
      given Context = VyxalTests.testContext()
      val func = FuncHelpers.collectByAnnotation(
        VFun.fromElement("×"),
        VFun.fromElement("@"),
        VFun.fromElement(";"),
      )(
        VList(Seq(VType(classOf[VNum]), VType(classOf[VNum]))),
        VList(Seq(VType(classOf[VStr]), VType(classOf[VStr]))),
        VList(Seq(VType(classOf[VAny]), VType(classOf[VAny]))),
      )
      // Multiplication, Levenstein Distance, Pair
      val cp = Checkpoint()
      cp { assertResult(VNum(21))(func.execute(0, 0, Seq(VNum(3), VNum(7)))) }
      cp {
        assertResult(VNum(2))(
          func.execute(0, 0, Seq(VStr("monday"), VStr("monkey")))
        )
      }
      cp {
        assertResult(VList(Seq(VType(classOf[VNum]), VType(classOf[VNum]))))(
          func.execute(0, 0, Seq(VType(classOf[VNum]), VType(classOf[VNum])))
        )
      }

      cp.reportAll()
    }
  }
end HelperTests
