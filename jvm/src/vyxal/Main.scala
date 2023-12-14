package vyxal

import vyxal.parsing.Lexer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object Main:
  def main(args: Array[String]): Unit =
    val glb = Globals(printFn = fuzzPrint)
    val ctx = Context(globals = glb, inputs = List("69"))
    val timeout = 1
    for (size <- 1 until args(0).toInt + 1)
      println(s"Fuzzing size $size programs:")
      for (fuzz <- fuzz(size))
        try Await.result(
            Future{
                try
                  Interpreter.execute(fuzz)(using ctx.copy)
                catch
                  case ex: VyxalUnknownException => println(s"` $fuzz `  Unknown Exception")
                  case ex: VyxalException => //println(s"` $fuzz ` VyxalException")
                  case ex: Throwable => println(s"` $fuzz ` Uncaught Exception")
              },
            Duration(timeout, "seconds")
          )
        catch
          case _ => println(s"` $fuzz ` Timeout after $timeout seconds")

  def fuzz(length: Int): IndexedSeq[String] =
    val cp = Lexer.Codepage
    if length == 1 then
      return cp.map(_.toString)
    else
      return cp.flatMap(x => fuzz(length-1).map(y => y + x))

  def fuzzPrint(str: String): Unit = {}
  // Suppresses printing. Conditions can be added for printing certain things, if needed.
