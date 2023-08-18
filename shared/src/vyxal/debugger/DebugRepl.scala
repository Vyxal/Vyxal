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
    val debugger = Debugger(ast)
    debugger.printState()
    while !debugger.finished do
      val line = io.StdIn.readLine("(debug)> ")
      if line.nonEmpty then
        OParser.parse(parser, line.split(" "), Config()) match
          case Some(config) =>
            config.cmd match
              case Cmd.StepInto => debugger.stepInto()
              case Cmd.StepOver => debugger.stepOver()
              case Cmd.StepOut => debugger.stepOut()
              case Cmd.Exit => return
            debugger.printState()
          case None => println("Could not parse command")
      else
        debugger.stepInto()
        debugger.printState()

  end start

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
    case Exit
end DebugRepl
