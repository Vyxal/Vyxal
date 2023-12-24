package vyxal.debugger

import vyxal.{Context, Parser}
import vyxal.parsing.Lexer

import scopt.OParser

object DebugRepl:
  def start(code: String)(using Context): Unit =
    scribe.trace("Starting debugger REPL")
    val ast = Parser().parse(Lexer(code))
    val dbg = Debugger(ast)
    printState(dbg)
    while !dbg.finished do
      val line = io.StdIn.readLine("(debug)> ")
      if line.nonEmpty then
        OParser.parse(parser, line.split(" "), Config()) match
          case Some(config) => config.cmd match
              case Cmd.StepInto =>
                dbg.stepInto()
                printState(dbg)
              case Cmd.StepOver =>
                dbg.stepOver()
                printState(dbg)
              case Cmd.StepOut =>
                dbg.stepOut()
                printState(dbg)
              case Cmd.Continue =>
                dbg.continue()
                printState(dbg)
              case Cmd.Resume =>
                dbg.resume()
                printState(dbg)
              case Cmd.AddBreakpoint(offset, label) =>
                dbg.addBreakpoint(Breakpoint(offset, label))
              case Cmd.RemoveBreakpointByOffset(offset) =>
                dbg.removeBreakpoint(offset)
              case Cmd.RemoveBreakpointByLabel(label) =>
                dbg.removeBreakpoint(label)
              case Cmd.ListBreakpoints =>
                println(dbg.getBreakpoints().mkString("\n"))
              case Cmd.Frames => for frame <- dbg.stackFrames do
                  println(s"<${frame.name}> ${frame.ast}")
              case Cmd.Eval(code) =>
                dbg.eval(code)
                printState(dbg)
              case Cmd.Stack(n) => println(
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
      cmd("continue")
        .action((_, cfg) => cfg.copy(cmd = Cmd.Continue))
        .text("Continue to the next breakpoint"),
      cmd("resume")
        .action((_, cfg) => cfg.copy(cmd = Cmd.Resume))
        .text("Resume execution to end of program, ignoring breakpoints"),
      cmd("add")
        .text("Add a breakpoint")
        .children(
          arg[Int]("<offset>")
            .action((offset, cfg) =>
              cfg.copy(cmd = Cmd.AddBreakpoint(offset, None))
            )
            .text("The offset for the breakpoint to add"),
          arg[String]("<label>")
            .action((label, cfg) =>
              val Cmd.AddBreakpoint(offset, _) = cfg.cmd: @unchecked
              cfg.copy(cmd = Cmd.AddBreakpoint(offset, Some(label)))
            )
            .optional()
            .text("The label for the breakpoint"),
        ),
      cmd("remove")
        .text("Remove a breakpoint using either its offset or label")
        .children(
          opt[Int]('o', "offset")
            .action((offset, cfg) =>
              cfg.copy(cmd = Cmd.RemoveBreakpointByOffset(offset))
            )
            .text("The offset for the breakpoint to remove")
            .optional(),
          opt[String]('l', "label")
            .action((label, cfg) =>
              cfg.copy(cmd = Cmd.RemoveBreakpointByLabel(label))
            )
            .text("The label for the breakpoint to remove")
            .optional(),
        ),
      cmd("list")
        .action((_, cfg) => cfg.copy(cmd = Cmd.ListBreakpoints))
        .text("List all breakpoints"),
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
        .text("Exit debugger"),
    )
  end parser

  private case class Config(cmd: Cmd = null)

  enum Cmd:
    case StepInto
    case StepOver
    case StepOut
    case Continue
    case Resume
    case AddBreakpoint(offset: Int, label: Option[String])
    case RemoveBreakpointByOffset(offset: Int)
    case RemoveBreakpointByLabel(label: String)
    case ListBreakpoints
    case Frames
    case Eval(code: String)
    case Stack(n: Int)
    case Exit
end DebugRepl
