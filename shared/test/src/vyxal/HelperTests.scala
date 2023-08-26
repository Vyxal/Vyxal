package vyxal

import org.scalatest.tagobjects.Slow
import org.scalatest.Checkpoints.Checkpoint

class HelperTests extends VyxalTests:

  describe("Partitions") {
    it("should partition infinite lists", Slow) {
      given Context = VyxalTests.testContext()
      val parts = ListHelpers
        .partitions(VList.from(LazyList.iterate(VNum(1))(_ + 1)))
        .asInstanceOf[Seq[Seq[VList]]]
      val cp = Checkpoint()

      cp { assertResult(1)(parts(0).size) }
      cp { assertResult(VList(1, 2, 3))(parts(0)(0).take(3)) }

      cp { assertResult(2)(parts(1).size) }
      cp { assertResult(VList(1))(parts(1)(0)) }
      cp { assertResult(VList(2, 3, 4))(parts(1)(1).take(3)) }

      cp { assertResult(3)(parts(2).size) }
      cp { assertResult(VList(1))(parts(2)(0)) }
      cp { assertResult(VList(2))(parts(2)(1)) }
      cp { assertResult(VList(3, 4, 5))(parts(2)(2).take(3)) }

      cp { assertResult(2)(parts(3).size) }
      cp { assertResult(VList(1, 2))(parts(3)(0)) }
      cp { assertResult(VList(3, 4, 5))(parts(3)(1).take(3)) }

      cp { assertResult(4)(parts(4).size) }
      cp { assertResult(VList(1))(parts(4)(0)) }
      cp { assertResult(VList(2))(parts(4)(1)) }
      cp { assertResult(VList(3))(parts(4)(2)) }
      cp { assertResult(VList(4, 5, 6))(parts(4)(3).take(3)) }

      cp { assertResult(3)(parts(5).size) }
      cp { assertResult(VList(1, 2))(parts(5)(0)) }
      cp { assertResult(VList(3))(parts(5)(1)) }
      cp { assertResult(VList(4, 5, 6))(parts(5)(2).take(3)) }

      cp { assertResult(2)(parts(6).size) }
      cp { assertResult(VList(1, 2, 3))(parts(6)(0)) }
      cp { assertResult(VList(4, 5, 6))(parts(6)(1).take(3)) }

      cp { assertResult(3)(parts(7).size) }
      cp { assertResult(VList(1))(parts(7)(0)) }
      cp { assertResult(VList(2, 3))(parts(7)(1)) }
      cp { assertResult(VList(4, 5, 6))(parts(7)(2).take(3)) }

      cp.reportAll()
    }
  }
end HelperTests
