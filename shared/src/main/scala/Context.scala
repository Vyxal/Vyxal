package vyxal

import scala.collection.{mutable => mut}

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
