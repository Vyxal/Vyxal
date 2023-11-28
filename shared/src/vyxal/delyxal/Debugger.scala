package vyxal.delyxal

import vyxal.parsing.Lexer
import vyxal.AST
import vyxal.BreakLoopException
import vyxal.Context
import vyxal.ContinueLoopException
import vyxal.Elements
import vyxal.ListHelpers
import vyxal.Parser

import scala.io.StdIn

class Debugger:
  def executeState(state: State): Unit =
    println(state.currentAST)
    println(state.ctx)

  def run(): Unit =
    var state = new State(null, Context())
    val program = "10(n,}"
    val asts = Parser
      .parse(Lexer.lexSBCS(program).getOrElse(List()))
      .getOrElse(AST.Group(List(), None, vyxal.parsing.Range.fake))
    given ctx: Context = state.ctx
    asts match
      case AST.Group(elems, _, _) =>
        state.currentAST = asts
        var keepGoing = true
        for ast <- elems do
          if keepGoing then
            print("Enter command: ")
            val command = StdIn.readLine()
            command match
              case "one" =>
                println("========")
                println("Executing: " + ast)
                println("Stack: " + ctx.stack)
                println("========")
              case "over" => keepGoing = false
              case "out" => keepGoing = false
              case _ => ???

          execute(ast)

      case _ => println("Error: AST is not a group")
    end match
  end run
  def execute(ast: AST)(using ctx: Context): Unit =
    ast match
      case AST.Number(value, _) => ctx.push(value)
      case AST.Group(elems, _, _) => for ast <- elems do
          println("Executing " + ast)
          execute(ast)
      case AST.Command(cmd, _) => Elements.elements.get(cmd) match
          case Some(elem) => elem.impl()
          case None => throw RuntimeException(s"No such command: '$cmd'")
      case AST.For(name, body, _) =>
        val iterable =
          ListHelpers.makeIterable(ctx.pop(), Some(true))(using ctx)
        var index = 0
        given loopCtx: Context = ctx.makeChild()
        println("=== Entering for loop ===")
        println("Iterable: " + iterable)
        println("=== Beginning loop ===")
        try
          for elem <- iterable do
            try
              println("Iteration " + index + ": " + elem)
              name.foreach(loopCtx.setVar(_, elem))
              loopCtx.ctxVarPrimary = elem
              loopCtx.ctxVarSecondary = index
              index += 1
              execute(body)(using loopCtx)
            catch case _: ContinueLoopException => ()
        catch case _: BreakLoopException => return
      case _ => println("Error: AST not yet.")

end Debugger
