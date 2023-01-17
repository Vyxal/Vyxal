*This file is to accompy [Interpreter.scala](../shared/src/main/scala/Interpreter.scala). For a general overview of how the whole interpretation process works, visit [INSERT MD FILE HERE](link).*

The interpreter file is the main brains of the whole Vyxal project - it's where the pipeline flows to after reading all neccesary inputs (like program files) and where vyxal programs are lexed and parsed. It also handles execution of Vyxal programs, via the `execute` function. There are two overloads of the execute function:

```scala
def execute(code: String)(using ctx: Context): Unit
```

and

```scala
def execute(ast: AST)(using ctx: Context): Unit
```

The `code: String` overload of `execute` is for programss that are still in their string form. This overload takes the string, lexes and parses it, and then hands it to the `ast: AST` overload of execute.

The `ast: AST` overload of `execute` uses the [visitor](https://en.wikipedia.org/wiki/Visitor_pattern) pattern for executing ASTs. After parsing, the result is an `AST.Group`, which is an AST object that contains a list of `AST` objects that are executed sequentially. `AST`s are evaluated according to the following rules:

| AST Object           	| How it's Executed                                                                               	| Context Variable Involved?                                                   	|
|----------------------	|-------------------------------------------------------------------------------------------------	|------------------------------------------------------------------------------	|
| `AST.Number`         	| Value simply pushed to stack                                                                    	| ❌                                                                            	|
| `AST.Str`            	| Value simply pushed to stack                                                                    	| ❌                                                                            	|
| `AST.Lst`            	| Each AST group in the list's items are evaluated. The list of results are pushed to the stack.  	| ❌                                                                            	|
| `AST.Command`        	| The element value is indexed into the element dictionary                                        	| ❌                                                                            	|
| `AST.Group`          	| Each AST in the group is executed individually.                                                 	| ❌                                                                            	|
| `AST.CompositeNilad` 	| Same as `AST.Group`. This AST type is for arity grouping purposes.                              	| ❌                                                                            	|
| `AST.If`             	| Pop the top of the stack, truthy: execute truthy branch, else: execute falsey branch if present 	| ❌                                                                            	|
| `AST.While`          	| While the condition branch evaluated on the stack is truthy, execute the loop body.             	| ✅<br><br>`N` = Number of while loop iterations<br>`M` = Last condition value 	|
| `AST.For`            	| Pop the top of the stack, cast to iterable, and execute body loop for each item in that.        	| ✅<br><br>`N` = Current loop item<br>`M` = Current loop index                 	|
