package vyxal

import org.scalatest.funsuite.AnyFunSuite

class InterpreterTests extends AnyFunSuite {
  test("Can the interpreter make lists?") {
    given ctx: Context = Context()
    Interpreter.execute("#[1 | 2 3 + | 4#]")
    assert(ctx.pop() == VList(1, 5, 4))
  }

  test("Can the interpreter execute if statements?") {
    given ctx: Context = Context()
    Interpreter.execute("0 [ 2 | 5 } : [ 6 | 4} +")
    assert(ctx.pop() == VNum(11))
  }

  test("Can the interpreter execute while loops?") {
    given ctx: Context = Context()
    Interpreter.execute("6 { : 2 - | 1 -}")
    assert(ctx.pop() == VNum(2))
  }

  test("Can the interpreter vectorise simple monads?") {
    given ctx: Context = Context()
    Interpreter.execute("#[100 | #[101 | 0#] #] vB")
    assert(ctx.pop() == VList(4, VList(5, 0)))
  }

  test("Can the interpreter vectorise simple dyads?") {
    given ctx: Context = Context()
    Interpreter.execute("#[4 | #[5 | 6#] #] 3 v;")
    assert(ctx.pop() == VList(VList(4, 3), VList(VList(5, 3), VList(6, 3))))
    Interpreter.execute("#[4 | #[5 | 6#] #] #[4#] v;")
    assert(ctx.pop() == VList(VList(4, 4), VList(VList(5, 0), VList(6, 0))))
  }

  test("Can the interpreter execute monadic lambdas?") {
    given ctx: Context = Context()
    Interpreter.execute(
      AST.makeSingle(
        AST.Number(3),
        AST.Lambda(1, List.empty, AST.Command("!")),
        AST.ExecuteFn
      )
    )
    assertResult(VNum(6))(ctx.pop())
  }

  test("Can the interpreter vectorise monadic lambdas?") {
    given ctx: Context = Context()
    ctx.push(VList(0, 3, VList(2, 1)))
    Interpreter.execute(
      Modifiers
        .modifiers("v")
        .from(List(AST.Lambda(1, List.empty, AST.Command("!"))))
    )
    assertResult(VList(1, 6, VList(2, 1)))(ctx.pop())
  }

  test("Can the interpreter execute dyadic lambdas?") {
    given ctx: Context = Context()
    Interpreter.execute(
      AST.makeSingle(
        AST.Number(3),
        AST.Number(1),
        AST.Lambda(2, List.empty, AST.Command("-")),
        AST.ExecuteFn
      )
    )
    assertResult(VNum(2))(ctx.pop())
  }

  test("Can the interpreter vectorise dyadic lambdas?") {
    given ctx: Context = Context()
    ctx.push(VList(0, 3, VList(2, 1)))
    ctx.push(VList(4, 2, 6))
    Interpreter.execute(
      Modifiers
        .modifiers("v")
        .from(List(AST.Lambda(2, List.empty, AST.Command("-"))))
    )
    assertResult(VList(-4, 1, VList(-4, -5)))(ctx.pop())
  }
}
