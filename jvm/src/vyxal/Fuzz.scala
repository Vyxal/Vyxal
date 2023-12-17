package vyxal

import vyxal.parsing.Lexer

import scala.concurrent.*
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object Fuzz:
  def fuzz(args: Array[Int]): Unit =
    val min = args(0)
    val max = args(1)
    val timeout = args(2)
    val noLoop = timeout == -1
    var tm = timeout
    if noLoop then tm = 1
    val glb = Globals(printFn = fuzzPrint)
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
                  println(s"` $fuzz `  Unknown Exception")
                case ex: VyxalException => // println(s"` $fuzz ` VyxalException")
                case ex: Throwable => println(s"` $fuzz ` Uncaught Exception")
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

  def fuzzPrint(str: String): Unit = {}
  // Suppresses printing. Conditions can be added for printing certain things, if needed.
end Fuzz
