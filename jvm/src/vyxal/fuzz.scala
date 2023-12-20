package vyxal

import vyxal.parsing.Lexer

import scala.concurrent.*
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

@main def fuzz(min: Int, max: Int, timeout: Int): Unit =
  val noLoop = timeout == -1
  val tm = if noLoop then 1 else timeout

  // Suppresses printing. Conditions can be added for printing certain things, if needed.
  val glb = Globals(printFn = _ => {})
  val ctx = Context(globals = glb, inputs = List("69"))
  for size <- min until max + 1 do
    println(s"Fuzzing size $size programs:")
    for fuzz <- makeFuzz(size, noLoop) do
      try
        Await.result(
          Future {
            try Interpreter.execute(fuzz)(using ctx.copy)
            catch
              case ex: VyxalUnknownException =>
                println(s"` $fuzz `  ${ex.getMessage()}: ${ex.getCause().getMessage()}")
              case ex: VyxalException => // println(s"` $fuzz ` VyxalException")
              case ex: Throwable => println(s"` $fuzz ` Uncaught Exception: ${ex.getMessage()}")
          },
          Duration(tm, "seconds"),
        )
      catch
        case _: TimeoutException => // println(s"` $fuzz ` Timeout after $tm seconds")
        case _ =>
    end for
  end for
end fuzz

def makeFuzz(length: Int, noLoop: Boolean): IndexedSeq[String] =
  val lazyLoops = raw"[xᶨᶪ\(\{?Ṇᵡ]"
  var cp = Lexer.Codepage
  if noLoop then cp = Lexer.Codepage.replaceAll(lazyLoops, "")
  if length == 1 then return cp.map(_.toString)
  else return cp.flatMap(x => makeFuzz(length - 1, noLoop).map(y => x +: y))
