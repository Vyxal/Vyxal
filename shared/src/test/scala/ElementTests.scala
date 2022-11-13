package vyxal.impls

import org.scalatest.funspec.AnyFunSpec

import vyxal.*
import Elements.Impls

/** Tests for specific elements
  */
class ElementTests extends AnyFunSpec {

  describe("Element +") {
    describe("when given lists") {
      it("should vectorise properly") {
        given Context = Context()
        assert(Impls.add(VList(VList(2, 5), "foo"), VList(VList(3, 4))) == VList(VList(5, 9), "foo0"))
      }
    }
  }
}
