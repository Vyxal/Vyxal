package vyxal

import vyxal.*

import org.scalatest.funspec.AnyFunSpec

class ModifierTests extends VyxalTests:
  describe("Modifier v") {
    it("should vectorise a function") {
      testMulti(
        "#[1|2|3#] 4 v+" -> VList(5, 6, 7),
        "#[1 10 R|1 5 R|6 8R#] ′+ vR" -> VList(45, 10, 13)
      )
    }
  }
