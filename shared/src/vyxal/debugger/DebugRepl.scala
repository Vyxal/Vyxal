package vyxal.debugger

import vyxal.{Context, Parser}
import vyxal.lexer.Lexer

import scopt.OParser

object DebugRepl:
  def start(code: String)(using Context): Unit =
    val ast = Lexer(code).flatMap(Parser.parse) match
      case Right(ast) => ast
      case Left(err) => throw new RuntimeException(err.msg)
    val debugger = Debugger(ast)
    while true do
      val line = io.StdIn.readLine("(debug)> ")
      if line.nonEmpty then
        OParser.parse(parser, line.split(" "), Config()) match
          case Some(config) =>
            config.cmd match
              case null => throw new Error("No command given to debugger")
              case Cmd.StepInto => debugger.stepInto()
              case Cmd.StepOver => debugger.stepOver()
              case Cmd.StepOut => debugger.stepOut()
              case Cmd.Exit => return
          case None => throw new Error("Could not parse command")
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
