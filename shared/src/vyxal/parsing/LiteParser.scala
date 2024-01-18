package vyxal.parsing

import vyxal.parsing.ParsingException.*

import scala.collection.mutable
import scala.collection.mutable.{ListBuffer, Queue}

/** A syntax tree that has yet to be processed */
enum LiteTree:
  case Tok(tok: Token, override val range: Range)
  case Group(trees: List[LiteTree], override val range: Range)
  case Structure(
      opener: Token,
      branches: List[LiteTree],
      override val range: Range,
  )

  /** For the special modifier `ᵜ`, which makes a lambda until the end of the
    * line
    */
  case LineLambda(
      modTok: Token,
      body: List[LiteTree],
      override val range: Range,
  )

  def range: Range
end LiteTree

/** LiteParser parses structures and finds custom element definitions. It
  * doesn't do arity grouping or process modifiers
  */
object LiteParser:
  def parse(tokens: List[Token]): Result =
    val parser = LiteParser()
    val trees = parser.parse(tokens)
    Result(trees, parser.extensions.toList)

  case class Result(
      trees: List[LiteTree],
      extensions: List[(List[LiteTree], Range)],
  )

private class LiteParser private ():
  val extensions = ListBuffer.empty[(List[LiteTree], Range)]

  def parse(tokens: List[Token]): List[LiteTree] =
    val preprocessed = preprocess(tokens).to(Queue)
    val parsed = parseTokens(preprocessed, true)
    if preprocessed.nonEmpty then
      if isCloser(preprocessed.front) then
        throw UnmatchedCloserException(preprocessed.dequeue())
      // Some tokens were left at the end, which should never happen
      throw TokensFailedParsingException(preprocessed.toList)
    else parsed

  private def preprocess(tokens: List[Token]): List[Token] =
    val doubleClose = ListBuffer[Token]()
    tokens.foreach {
      case Token(TokenType.StructureDoubleClose, _, range) =>
        doubleClose += Token(TokenType.StructureClose, "}", range)
        doubleClose += Token(TokenType.StructureClose, "}", range)
      case x => doubleClose += x
    }
    val lineup = Queue(doubleClose.toList*)
    val processed = ListBuffer[Token]()

    while lineup.nonEmpty do
      val temp = lineup.dequeue()
      temp match
        case Token(TokenType.UnpackTrigraph, "#:[", _) =>
          val contents = StringBuilder()
          var depth = 1
          while depth != 0 do
            val top = lineup.dequeue()
            top match
              case Token(TokenType.UnpackTrigraph, "#:[", _) => depth += 1
              case Token(TokenType.UnpackVar, _, _) => depth += 1
              case Token(TokenType.StructureOpen, open, _) =>
                if open == StructureType.Ternary.open then depth += 1
              case Token(TokenType.UnpackClose, _, _) => depth -= 1
              case Token(TokenType.StructureAllClose, _, _) => depth -= 1
              case _ =>
            contents.++=(top.value)
          processed +=
            Token(TokenType.UnpackVar, contents.toString(), temp.range)
        case _ => processed += temp
      end match
    end while
    processed.toList
  end preprocess

  /** The parser takes a list of tokens and performs two sweeps of parsing:
    * structures + arity grouping and then modifiers. The first sweep deals
    * directly with the token list provided to the parser, and leaves its
    * results in a stack of ASTs (the stack data type is used because it means
    * that arity grouping is simply popping previous ASTs until a niladic state
    * is reached). The second sweep takes the stack of ASTs and applies the
    * logic of modifier grouping, placing its result in a single Group AST.
    *
    * @param topLevel
    *   Whether this is the topmost call to parse, in which case it should
    *   remove any `StructureAllClose`s left behind by `parseStructure`
    * @param untilNewline
    *   Whether this should stop at a newline, in case we're parsing ᵜ (lambda
    *   to newline). Leaves the newline in the queue if so.
    */
  private def parseTokens(
      program: Queue[Token],
      topLevel: Boolean = false,
      untilNewline: Boolean = false,
  ): List[LiteTree] =
    val trees = ListBuffer.empty[LiteTree]
    // Convert the list of tokens to a queue so that ASTs like Structures can
    // freely take as many tokens as they need without needing to worry about
    // changing indexes like a for-loop would require.

    // Begin the first sweep of parsing

    while program.nonEmpty && !isCloser(program.front) &&
      (!untilNewline || program.front.tokenType == TokenType.Newline)
    do
      val token = program.dequeue()
      val value = token.value
      val range = token.range
      (token.tokenType: @unchecked) match
        case TokenType.Comment => ()
        case TokenType.SpecialModifier if value == "ᵜ" =>
          val body = parseTokens(program, topLevel = false, untilNewline = true)
          trees +=
            LiteTree.LineLambda(
              token,
              body,
              Range(
                range.startOffset,
                if body.nonEmpty then body.last.range.endOffset
                else range.endOffset,
              ),
            )
        // Numbers, strings and newlines are trivial, and are simply evaluated
        case TokenType.Number | TokenType.Str | TokenType.DictionaryString |
            TokenType.CompressedString | TokenType.CompressedNumber |
            TokenType.Newline | TokenType.Command | TokenType.NegatedCommand |
            TokenType.OriginalSymbol => trees +=
            LiteTree.Tok(token, token.range)
        case TokenType.StructureOpen | TokenType.DefineRecord => trees +=
            parseStructure(program, token, topLevel, TokenType.StructureClose)
        /*
         * List are just structures with two different opening and closing
         * token possibilities, so handle them the same way.
         */
        case TokenType.ListOpen => trees +=
            parseStructure(program, token, topLevel, TokenType.ListClose)
        case TokenType.DefineExtension =>
          val LiteTree.Structure(_, branches, range) =
            parseStructure(program, token, topLevel, TokenType.StructureClose)
          extensions += ((branches, range))

          if !topLevel then
            // todo this shouldn't be done by scribe since it won't be seen by web interpreter users
            // and scribe is for logging anyway
            scribe.warn(
              s"Extensions should be defined at toplevel, found at range $range"
            )
      end match
    end while

    trees.toList
  end parseTokens

  /** Parse either a structure or a list.
    *
    * @param topLevel
    *   Whether this is at the top level, in which case it should remove any
    *   `StructureAllClose`s
    * @param closer
    *   The kind of token that ends the structure/list
    * @returns
    *   The branches parsed, as well as the offset of the structure closer
    */
  def parseStructure(
      program: Queue[Token],
      opener: Token,
      topLevel: Boolean,
      closer: TokenType,
  ): LiteTree.Structure =
    if program.isEmpty then return LiteTree.Structure(opener, Nil, opener.range)

    val branches = ListBuffer.empty[LiteTree]
    // The start offset of each branch (it's a var D:)
    var branchStart = opener.range.endOffset

    var shouldBreak = false

    while !shouldBreak do
      val branchBody = parseTokens(program, topLevel = false)
      branches +=
        LiteTree.Group(
          branchBody,
          if branchBody.nonEmpty then
            Range(
              branchBody.head.range.startOffset,
              branchBody.last.range.endOffset,
            )
          else if program.nonEmpty then
            Range(branchStart, program.front.range.startOffset)
          else Range(branchStart, branchStart),
        )

      if program.isEmpty || program.front.tokenType != TokenType.Branch then
        shouldBreak = true
      if program.nonEmpty && program.front.tokenType == TokenType.Branch then
        // Get rid of the `|` token to move on to the next branch
        val branchTok = program.dequeue()
        branchStart = branchTok.range.endOffset
    end while

    val structEnd =
      if program.nonEmpty && program.front.tokenType == closer then
        val closerEnd = program.front.range.endOffset
        if topLevel || program.front.tokenType != TokenType.StructureAllClose
        then
          // Get rid of the structure/list closer (including StructureAllClose, if we're not inside another struct)
          program.dequeue()
        closerEnd
      else branchStart

    LiteTree.Structure(
      opener,
      branches.toList,
      Range(opener.range.startOffset, structEnd),
    )
  end parseStructure

  /** Whether this token is a branch or a structure/list closer */
  private def isCloser(token: Token): Boolean =
    token.tokenType match
      case TokenType.Branch => true
      case TokenType.ListClose => true
      case TokenType.StructureClose => true
      case TokenType.StructureDoubleClose => true
      case TokenType.StructureAllClose => true
      case _ => false
end LiteParser
