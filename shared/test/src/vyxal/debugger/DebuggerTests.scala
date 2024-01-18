package vyxal.debugger

import vyxal.*
import vyxal.parsing.Range

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

  Feature("Breakpoints") {
    given ctx: Context = VyxalTests.testContext()

    Scenario("Adding/removing breakpoints") {
      val dbg = Debugger(AST.Str("foo"))

      val baz3 = Breakpoint(3, Some("baz"))
      val baz4 = Breakpoint(4, Some("baz"))

      val breakpoints = Set(
        Breakpoint(2, Some("foo")),
        Breakpoint(2, Some("bar")),
        baz3,
        baz4,
      )

      When("addBreakpoint is called")
      breakpoints.foreach(dbg.addBreakpoint)

      Then("the breakpoints should actually be added")
      assertResult(breakpoints)(dbg.getBreakpoints())

      When("a breakpoint is removed by offset")
      dbg.removeBreakpoint(2)

      Then("all breakpoints at that offset should be removed")
      assertResult(Set(baz3, baz4))(dbg.getBreakpoints())

      When("a breakpoint is removed by label")
      dbg.removeBreakpoint("baz")

      Then("all breakpoints with that label should be removed")
      assert(dbg.getBreakpoints().isEmpty)
    }

    Scenario("Continuing to and from a breakpoint") {
      val dbg = Debugger(
        AST.makeSingle(
          AST.Str("foo", Range(0, 3)),
          AST.Number(5, Range(3, 4)),
          AST.Command("+", range = Range(4, 5)),
        )
      )
      dbg.addBreakpoint(Breakpoint(3))

      When("it's continued")
      dbg.continue()

      Then("it should hit the first breakpoint")
      assertResult(Range(3, 4))(dbg.currAST.range)

      When("it's continued again")
      dbg.continue()

      Then("it should finish")
      assert(dbg.finished)
      assertResult("foo5")(ctx.pop())
    }

    Scenario("Resuming") {
      val dbg = Debugger(
        AST.makeSingle(
          AST.Str("foo", Range(0, 3)),
          AST.Number(5, Range(3, 4)),
          AST.Command("+", range = Range(4, 5)),
        )
      )
      dbg.addBreakpoint(Breakpoint(3))

      When("it's resumed")
      dbg.resume()

      Then("it should finish")
      assert(dbg.finished)
      assertResult("foo5")(ctx.pop())
    }
  }
end DebuggerTests
