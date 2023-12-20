package vyxal

import vyxal.parsing.Lexer

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

@main def fuzz(length: Int, timeout: Int): Unit =
  val glb = Globals(printFn = fuzzPrint)
  val ctx = Context(globals = glb, inputs = List("69"))
  for size <- 1 until length + 1 do
    println(s"Fuzzing size $size programs:")
    for fuzz <- makeFuzz(size) do
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
          Duration(timeout, "seconds"),
        )
      catch
        case _ =>
          print(".") // println(s"` $fuzz ` Timeout after $timeout seconds")
    end for
  end for
end fuzz

def makeFuzz(length: Int): IndexedSeq[String] =
  val cp = Lexer.Codepage
  if length == 1 then return cp.map(_.toString)
  else return cp.flatMap(x => makeFuzz(length - 1).map(y => y + x))

def fuzzPrint(str: String): Unit = {}
// Suppresses printing. Conditions can be added for printing certain things, if needed.
