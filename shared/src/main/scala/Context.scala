package vyxal

import scala.collection.{mutable => mut}

/** What kind of implicit output is wanted at the end
  */
enum EndPrintMode {

  /** Just print the top of the stack */
  case Default
  case JoinNewlines

  /** Join on newlines vertically */
  case JoinNewlinesVert

  /** Sum/concatenate the top of the stack */
  case Sum
}

/** Settings set by flags
  *
  * @param presetStack
  *   Whether to push a 100 onto the stack before the program starts
  */
case class Settings(
    presetStack: Boolean = false,
    endPrintMode: EndPrintMode = EndPrintMode.Default
) {

  /** Add a flag to these settings
    *
    * @return
    *   A Some with the new settings, or None if the flag is invalid
    */
  def withFlag(flag: Char): Settings = flag match {
    case 'H' => this.copy(presetStack = true)
    case 'j' => this.copy(endPrintMode = EndPrintMode.JoinNewlines)
    case 'L' => this.copy(endPrintMode = EndPrintMode.JoinNewlinesVert)
    case 's' => this.copy(endPrintMode = EndPrintMode.Sum)
    case _   => throw IllegalArgumentException(s"$flag is an invalid flag")
  }
}

/** @constructor
  *   Make a Context object for the current scope
  * @param vars
  *   The variables currently in scope, accessible by their names. Null values
  *   signify that the variable is nonlocal, i.e., it should be gotten from the
  *   parent context
  * @param inputs
  *   The inputs available in this scope
  * @param parent
  *   The context inside which this context is (to inherit variables). `None`
  *   for toplevel contexts
  */
class Context private (
    private var stack: mut.ArrayBuffer[VAny]
) {
  def pop(): VAny = if (stack.isEmpty) 0 else stack.remove(stack.size - 1)

  /** Get the top element on the stack without popping */
  def peek: VAny = if (stack.isEmpty) 0 else stack.last

  def push(item: VAny): Unit = stack += item

  /** Whether the stack is empty */
  def isEmpty: Boolean = stack.isEmpty
}

object Context {
  def apply(): Context = new Context(stack = mut.ArrayBuffer())
}
