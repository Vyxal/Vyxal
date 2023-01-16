*This file is to accompy [Interpreter.scala](../shared/src/main/scala/Interpreter.scala). For a general overview of how the whole interpretation process works, visit [INSERT MD FILE HERE](link).*

The interpreter file is where parsed programs are sent to be executed. After a program is parsed, it will be in a single `AST.Group` object, which contains a list of AST objects to be executed sequentially.
