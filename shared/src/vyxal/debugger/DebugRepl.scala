package vyxal.debugger

import vyxal.{Context, Parser}
import vyxal.parsing.Lexer

import scopt.OParser

object DebugRepl:
  def start(code: String)(using Context): Unit =
    scribe.trace("Starting debugger REPL")
    val ast = Lexer(code).flatMap(Parser.parse) match
      case Right(ast) => ast
      case Left(err) => throw RuntimeException(err.msg)
    val dbg = Debugger(ast)
    printState(dbg)
    while !dbg.finished do
      val line = io.StdIn.readLine("(debug)> ")
      if line.nonEmpty then
        OParser.parse(parser, line.split(" "), Config()) match
          case Some(config) =>
            config.cmd match
              case Cmd.StepInto =>
                dbg.stepInto()
                printState(dbg)
              case Cmd.StepOver =>
                dbg.stepOver()
                printState(dbg)
              case Cmd.StepOut =>
                dbg.stepOut()
                printState(dbg)
              case Cmd.Frames =>
                for frame <- dbg.stackFrames do
                  println(s"<${frame.name}> ${frame.ast}")
              case Cmd.Eval(code) =>
                dbg.eval(code)
                printState(dbg)
              case Cmd.Stack(n) =>
                println(
                  dbg.stackFrames.last.ctx.peek(n).mkString("[", ",", "]")
                )
              case Cmd.Exit => return
          case None => println("Could not parse command")
      else
        dbg.stepInto()
        printState(dbg)
    end while
  end start

  def printState(dbg: Debugger): Unit =
    println(s"Top of stack is ${dbg.ctx.peek}")
    if !dbg.finished then
      println(s"Next to execute: ${dbg.currAST.toVyxal} <${dbg.currAST.range}>")

  private val builder = OParser.builder[Config]

  private val parser =
    import builder.*

    OParser.sequence(
      cmd("step-into")
        .action((_, cfg) => cfg.copy(cmd = Cmd.StepInto))
        .text("Step into"),
      cmd("step-over")
        .action((_, cfg) => cfg.copy(cmd = Cmd.StepOver))
        .text("Step over"),
      cmd("step-out")
        .action((_, cfg) => cfg.copy(cmd = Cmd.StepOut))
        .text("Step out"),
      cmd("frames")
        .action((_, cfg) => cfg.copy(cmd = Cmd.Frames))
        .text("Show all the frames"),
      cmd("eval")
        .text("Evaluate some code")
        .children(
          arg[String]("<code>")
            .action((code, cfg) => cfg.copy(cmd = Cmd.Eval(code)))
            .text("Code to evaluate")
        ),
      cmd("stack")
        .text("Show the top n items of the stack")
        .children(
          arg[Int]("<int>")
            .action((n, cfg) => cfg.copy(cmd = Cmd.Stack(n)))
            .text("Number of elements to show")
        ),
      cmd("exit")
        .action((_, cfg) => cfg.copy(cmd = Cmd.Exit))
        .text("Exit debugger")
    )
  end parser

  private case class Config(cmd: Cmd = null)

  enum Cmd:
    case StepInto
    case StepOver
    case StepOut
    case Frames
    case Eval(code: String)
    case Stack(n: Int)
    case Exit
end DebugRepl
