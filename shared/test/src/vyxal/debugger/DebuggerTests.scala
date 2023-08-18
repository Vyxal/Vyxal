package vyxal.debugger

import vyxal.*

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.GivenWhenThen

class DebuggerTests extends AnyFeatureSpec with GivenWhenThen with Matchers:
  Feature("Step into") {
    Scenario("Stepping into with empty program") {
      given Context = VyxalTests.testContext()

      Given("a debugger for an empty program")
      val dbg = Debugger(AST.Group(List.empty, None))
      assert(!dbg.finished)

      When("it's stepped into")
      dbg.stepInto()

      Then("it should be finished")
      assert(dbg.finished)

      When("it's stepped into again")
      Then("it should throw an error")
      an[IllegalStateException] should be thrownBy dbg.stepInto()
    }
  }

  Feature("Step over") {
    Scenario("Stepping over a list") {
      given ctx: Context = VyxalTests.testContext()

      val dbg = Debugger(
        AST.Lst(
          List(AST.Number(123), AST.Lst(List(AST.Number(9))), AST.Str("foo"))
        )
      )

      When("it's stepped over")
      dbg.stepOver()

      Then("it should have pushed the list")
      assert(ctx.pop() == VList(123, VList(9), "foo"))

      And("it should be finished")
      assert(dbg.finished)
    }
  }
end DebuggerTests
