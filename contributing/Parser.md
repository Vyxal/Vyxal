
The Vyxal parser may seem like a complex beast at first glance - with all its private def functions and `@unchecked` pattern matching, it can be hard to comprehend what's happening. Rest assured that this documentation is here to help you make sense of the Vyxal parser. It'll break down the various components and explain how they work together to achieve the desired parsing results.

## The Pipeline - A 30k Feet Overview

To start with, it's helpful to know that the parser follows a sort of three-stage pipeline:

1. Token Preprocessing
2. Token Parsing
3. AST Postprocessing

Each of the stages transforms its input into a format that is helpful for the next stage.

## Token Preprocessing

The first stage of the pipeline is token preprocessing. This stage substitutes some tokens for others, so that the next stage can fully focus on token grouping, rather than handling annoying quirks. The tokens that are changed are:

- `Token.StructureClose(")")` -> 2 x `Token.StructureClose("}")`

This is done using a temporary storage `ListBuffer` and a for loop to iterate over the unprocessed tokens. If a token is `)`, two `}`s are added, otherwise the normal token is added to the list.

- `Token.SyntaxTrigraph("#:[") {a bunch of tokens and branches} Token.StructureAllClose` -> `Token.UnpackVar("...")` (where the `...` is the entire string correlating to the unpack statement)

This is done by turning the program into a queue (a FIFO data structure) and then dequeuing tokens. When a `#:[` is reached, a depth counter is kept and token values are added to a string builder under that depth is 0. `[`s increase the depth, `]`s decrease the depth. Once the depth reaches 0, a `Token.UnpackVar()` token is added to a list of preprocessed tokens. Other tokens are also added to the list as-is. This list is what is returned and used in the next phase.

## Token Parsing

For the main token parsing stage, the list of tokens that have been preprocessed are once again turned into a queue, and a list to store all the processed ASTs is created. A queue is used because the naive for-loop approach has troubles parsing unknown-length structures (which can be fixed with continue statements, but by then you've got a glorified while loop that needs way too many additional variables.)

There are two "sweeps" of parsing: the mapping sweep and the modifying sweep.

### The Mapping Sweep

In this sweep, each token is considered in turn, and depending on the token type, a simple corresponding AST is added to the processed list, or control flow moves to a specialised function. After a mapping is made, the AST is pushed to a `Stack` of ASTs. The table below indicates how tokens are mapped.

| Token Type (`Token.`)     | Simple AST?                           | Called Function              |
|------------------------------- |-------------------------------------- |----------------------------- |
| `Number(value)`                | ✅ (`AST.Number`)                      | ❌                            |
| `Str(Value)`                   | ✅ (`AST.Str`)                         | ❌                            |
| `StructureOpen(value)`         | ❌                                     | `parseStructure`             |
| `ListOpen`                     | ❌                                     | `parseBranches`              |
| `Command(value)`               | ❌                                     | `parseCommand`               |
| `SpecialModifier(value)`       | ✅ (`AST.SpecialModifier`)             | ❌                            |
| `GetVar(value)`                | ✅ (`AST.GetVar`)                      | ❌                            |
| `SetVar(value)`                | ✅ (`AST.SetVar`)                      | ❌                            |
| `AugmentVar(value)`            | 🟨 (`AST.AuxAugmentVar`)               | Handled later in the parser  |
| `UnpackVar(value)`             | ❌                                     | Explained in depth further down     |

### The Modifying Sweep

After the mapping sweep, there is a `Stack` of ASTs. The `Stack` data structure was chosen so that mapped ASTs would end up in a LIFO order that greatly simplifies modifier grouping logic. There is also a second `Stack` of ASTs that contains ASTs that have been grouped if needed - this is what will be returned.

In this section, a while loop pops from the mapped AST stack until it is empty, doing the following:

- If the popped AST is a n-adic modifier, pop as many ASTs as needed from the mapped AST stack
- If the popped AST is a special modifier, handle it accordingly
- If the popped AST is `AST.AuxAugmentVar`, pop the top of the _grouped_ AST stack and push an `AST.AugmentVar(name, op)` to the grouped AST stack.

That last point handles the fact that Augmented Assignment is basically a postfix modifer, rather than a standard prefix modifier.

### Control Flow Redirection

This section of the parser documentation will detail the functions found in the `Called Function` column in the table from the mapping sweep section.

#### `Token.UnpackVar` handling

This token isn't handled in an external function, but is handled directly by the match case. Like the token preprocessing stage, it uses a take-from-program-queue-until-depth-is-(-1) approach to extracting all the variable names.

Whenever one of `[]|` is reached, the current name that is being read is pushed to a list of names with its depth as a tuple, and the name is reset. If the token is `[`, the depth is incremented and if the token is `]`, the depth is decremented.

A list of `(String, Int)` tuples (also caled a depth map) is used for this process instead of a ragged list of strings and lists for a few reasons:

- It reduces dependency on Vyxal objects that are designed primarily for handling program execution (i.e. the "purity" of the parser is kept as high as possible)
- There's no need to define yet another object/type - tuples come with Scala
- Easier debugging in the parser - instead of struggling to comprehend rugged list syntax, there's a flat list of 2-item tuples.

#### `parseBranches`

The signature of `parseBranches` is:

```scala
parseBranches(program: Queue[Token], canBeEmpty: Boolean)(isEnd: Token => Boolean): ParserRet[List[AST]]
```

The reason it's got such a complicated signature is because it's supposed to work for both structures and lists.
`isEnd` tells it whether to look for a `ListClose` token or a `StructureClose` token.
When parsing structures, `canBeEmpty` is `false` so that you have at least one empty branch even if the structure has nothing in it.
When parsing lists, `canBeEmpty` is `true` because we want to be able to make empty lists.

Once `parseBranches` parses the branches of the structure/list (separated by `Token.Branch` tokens), it'll return a list of those branches.
Note that the return type is `ParserRet[List[AST]]`, not `List[AST]`. `ParserRet` is a type alias, so the return type is really
`Either[VyxalCompilationError, List[AST]]`. This is because a compilation error could happen at any point, requiring us to return a
`Left` containing a `VyxalCompilationError` rather than a `Right` containing a `List[AST]`.

#### `parseStructure`

The signature of `parseStructure` is:

```scala
parseStructure(structureType: StructureType, program: Queue[Token]): ParserRet[AST]
```

Meaning it takes the structure character being parsed and the remaining program queue. It takes tokens until a matching `}` is found at a matching level. That is, there won't be an unbalanced number of structure openers and structure closes.

Once token collection has finished,  there is a list of lists of ASTs/Lists of AST. These are the branches of the structure, and are handled according to the structure type:

| Structure Type  | Branches                                     |
|---------------- |--------------------------------------------- |
| If Statement    | Truthy Branch (default), Falsey Branch       |
| While Loop      | Condition, Loop Body (default)               |
| For Loop        | Iterator Variable Name, Loop Body (default)  |
| Lambda          | Parameters, Lambda Body (default)            |

Note that structures such as variable unpacking are handled elsewhere due to their unusual structure.

#### `parseIdentifier`

The signature for `parseIdentifier` is:

```scala
parseIdentifier(program: Queue[Token]): Option[String]
```

This function collects tokens until a branch or end of structure is found. It then only keeps tokens with a value that is alphanumeric. It then concatenates the token values into a single string and returns it. This function is for getting the variable names of for-loops.

#### `isCloser`

These tokens are considered to be structure closing tokens by the function:

- `Token.ListClose`
- `Token.StructureClose`
- `Token.StructureAllClose`
`Token.Branch`es are also considered closing tokens, but aren't strictly closing tokens as such.

All other tokens are not considered closing tokens.

---

#### Some types

- `AST` - An Abstract Syntax Tree. This is what we ultimately want to get from the parser! Defined in [AST.scala](/shared/src/AST.scala).
- `Token` - Represents a token, which the lexer returns a list of. These tokens are completely unrelated to the actual structure of the program. Take a look at [Lexer.md](Lexer.md) for that.
- `ParserRet[T]` - A type alias for `Either[VyxalCompilationError, T]`. Even though Vyxal is a golflang, there is a limit to how much abuse it
  will tolerate, and compilation errors are possible. Because of this, the main `parse` method, as well as `parseStructure` and the like, return `ParserRet`s
  instead of directly returning `AST`s or `List[AST]`s or whatever. An [`Either`](https://dotty.epfl.ch/api/scala/util/Either.html) is a type used to
  represent one of two possible values. It can be either a `Left` (here, a `VyxalCompilationError` indicating failure) or a `Right` (here, a successfully
  parsed `AST`, `List[AST]`, or something else). Rustaceans may see the similarity to `Result`.
- `ListBuffer[T]` - Scala's most commonly used data structure - `List` - is immutable. When you want to build up a `List` by adding elements to it one by one,
  [`ListBuffer`](https://dotty.epfl.ch/api/scala/collection/mutable/ListBuffer.html#) is a good choice. If you just want a mutable list that you won't
  necessarily convert to a `List` later, `ArrayBuffer` works too.

## Token Post-Processing

The post-processing phase of the parser simply moves all trailing nilads in a program to the front of the program to avoid redundancies.
