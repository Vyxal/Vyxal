package vyxal.gen

import vyxal.elements.ElementInformation
import vyxal.elements.ModifierOverload
import vyxal.elements.Overload
import vyxal.parsing.{Codepage, LiterateLexer}
import vyxal.SyntaxInfo

import scala.collection.mutable.ArrayBuffer

object DocsUtils:

  def genMarkdown(): String =
    s"""%Element, Modifier, and Syntax Reference
        %
        %## Elements
        %
        %- `nsl` = Number/String/List
        %- `any` = Any type
        %- `num` = Number
        %- `str` = String
        %- `lst` = List
        %- `fun` = Function
        %- `obj` = User-defined object

        %${genElementsTable()}

        %## Modifiers

        %${genModifiersTable()}

        %## Syntax
        
        %${genSyntaxTable()}
        %""".stripMargin('%')

  def genElementsTable(): String =
    val HEADER_ROW = "| Symbol | Keywords | Arity | Vectorises | Overloads |" +
      "\n|--------|--|------|-----------|-----------|"

    val elementMap = ElementInformation.elements

    val lines = elementMap.values.toSeq
      .sortBy { elem =>
        // Have to use tuple in case of digraphs
        (
          Codepage.indexOf(elem.symbol.charAt(0)) +
            (if "#∆øÞk".contains(elem.symbol.charAt(0)) then 400 else 0),
          Codepage.indexOf(elem.symbol.substring(1)),
        )
      }
      .map { elem =>
        val symbol =
          if "`|<>\\".contains(elem.symbol) then s"\\${elem.symbol}"
          else elem.symbol
        val keywords =
          elem.keywords.map(kw => s"<code>$kw</code>").mkString("</br>")
        val arity = if elem.arity == -1 then "STACK" else elem.arity
        val vectorises = if elem.options.vectorises then "vec" else ""
        val peeks = if elem.options.peeks then "*" else ""
        val overloads = elem.overloads.map(overloadToString)
        val overloadsFlat = overloads.foldLeft(Seq.empty[String]) {
          case (acc, s: String) => acc :+ s
          case (acc, s: Seq[String]) => acc ++ s
        }

        s"| <code>$symbol</code> | $keywords | $arity$peeks | $vectorises  | ${overloadsFlat.mkString("</br>")} |"
      }

    (HEADER_ROW +: lines).mkString("\n")
  end genElementsTable

  def overloadToString(overload: Overload): Seq[String] | String =
    val description = overload.description
    if overload.typeSwitchable then
      // Extract all type switch templates
      val FIELD_REGEX = """\{((?:\\[\{\}|\\\\]|[^\{\}\\])*)\}""".r
      val fields = FIELD_REGEX.findAllMatchIn(description).map(_.group(1)).toSeq
      val fieldOptions = fields.map { field =>
        field.split(raw"(?<!\\)\|").toSeq
      } // A list of the options for each type switch field

      val descriptions = ArrayBuffer[String]()

      for (args, index) <- overload.args.permutations.zipWithIndex do
        val argsString = args.map(_.replace("|", "\\|")).mkString(",")
        val descriptionString = fields.zip(fieldOptions).foldLeft(description) {
          case (acc, (field, options)) =>
            acc.replace(s"{$field}", options(index % options.length))
        }
        descriptions +=
          s"**${overload.name}** (`$argsString`): $descriptionString"
      descriptions.toSeq
    else if overload.args.isEmpty then s"**${overload.name}**: $description"
    else
      s"**${overload.name}** (`${overload.args.map(_.replace("|", "\\|")).mkString(",")}`): $description"
    end if
  end overloadToString

  def overloadToString(overload: ModifierOverload): String =
    val description = overload.description
    val args = overload.args.map(_.replace("|", "\\|")).mkString(",")
    val example = overload.example.replace("|", "\\|").replace("`", "\\`")
    s"<tr><td>**${overload.name}**</td><td>`$args`</td><td>$description</td><td>`$example`</td></tr>"

  def genModifiersTable(): String =
    val HEADER_ROW = "| Symbol | Keywords | Number of Elements | Overloads |" +
      "\n|--------|--|------------------|-----------|"

    val modifiers = ElementInformation.modifiers

    val lines = modifiers.values.toSeq
      .sortBy { char => Codepage.indexOf(char.symbol.last) }
      .map { mod =>
        val symbol =
          if "`|<>\\".contains(mod.symbol) then s"\\${mod.symbol}"
          else mod.symbol
        val keywords = mod.keywords.map(kw => s"`$kw`").mkString("</br>")
        val numElements = mod.numberOfElements
        val overloads = mod.overloads.map(overloadToString).mkString("</br>")
        s"| `$symbol` | $keywords | $numElements | <table>$overloads</table> |"
      }

    (HEADER_ROW +: lines).mkString("\n")
  end genModifiersTable

  def genSyntaxTable(): String =
    val HEADER_ROW = "| Symbol | Name | Keywords | Description | Usage |" +
      "\n|--------|----|--|-----------|------|"

    val syntaxMap = SyntaxInfo.info

    val lines = syntaxMap.toSeq.sortBy((key, _) => Codepage.indexOf(key)).map {
      (char, syntax) =>
        val symbol =
          if "`|<>\\".contains(char) then s"\\${char}"
          else char
        val keywords =
          syntax.literateKeywords.map(kw => s"`$kw`").mkString("</br>")
        val description = syntax.description
        val usage = syntax.usage
        s"| <code>$symbol</code> | ${syntax.name} | $keywords | $description | <code>$usage</code> |"
    }

    (HEADER_ROW +: lines).mkString("\n")
  end genSyntaxTable

  def genSBCSGrammar(): String =
    val modifierCharacters = ElementInformation.modifiers.values
      .map(_.symbol)
      .filter(_.length == 1)
      .map(_.last)
      .toSet
      .mkString("")
    val structureOpeners =
      SyntaxInfo.info.filter((_, info) => info.structureOpener).keys
    val structureClosers =
      SyntaxInfo.info.filter((_, info) => info.structureCloser).keys
    val nonElementChars =
      s"$modifierCharacters${SyntaxInfo.info.keys.filter(_.length == 1).mkString("")}"
        .sortBy(Codepage.indexOf(_))
        .replace("]", "\\]")
    s"""
      |@top Program { Statement+ }
      |@skip { Space }
      |Statement { Digraph | SyntaxTrigraph | StructureOpen | StructureClose | ListStuff | ModifierChar | VariableThing | Number | AnyString | Branch | ContextIndex | Comment | Element }
      |Number { NumberDecimal | TwoCharNumber }
      |NumberDecimal {
      |    NumberPart |
      |    "."
      |} 
      |AnyString {
      |    String |
      |    SingleCharString |
      |    TwoCharString
      |}
      |@tokens {
      |  Space { @whitespace+ }
      |  ModifierChar {$$[$modifierCharacters]}
      |  Comment {"##" (![\n])*}
      |  Digraph { $$[∆øÞk] _ | "#" ![[\\]$$!=#>@{:] }
      |  NumberPart { "0" | ($$[1-9] $$[0-9]*) }
      |  SyntaxTrigraph { "#:" ![[] }
      |  Branch {"|"}
      |  ListStuff { "#[" | "#]"}
      |  StructureOpen {${structureOpeners.filterNot(_ == "#[").map(char => s"\"$char\"").mkString(" | ")}}
      |  StructureClose {${structureClosers.filterNot(_ == "#]").map(char => s"\"$char\"").mkString(" | ")}}
      |  String {'"' (!["„”“\\\\] | "\\\\" _)* $$["„”“]}
      |  SingleCharString { "'" _ }
      |  TwoCharString { "Ꮬ" _ _ }
      |  TwoCharNumber { "Ꮠ" _ _ }
      |  VariableThing { "#" ($$[=$$>]|":[") $$[A-Z] $$[a-zA-Z0-9_]* }
      |  ContextIndex { "#¤" @digit }
      |  Element { ![$nonElementChars] }
      |  @precedence { Space, Element }
      |}  
    """.stripMargin('|')
  end genSBCSGrammar

  def genLiterateGrammar(): String =
    val openers = LiterateLexer().structOpeners
      .map((keyword, _) => s"structure<\"$keyword\">")
      .mkString("\n")

    val branches = LiterateLexer().branchKeywords
      .map(keyword => s"branch<\"$keyword\">")
      .mkString("\n")

    val lambdas = LiterateLexer().lambdaOpeners
      .map((keyword, _) => s"lambda<\"$keyword\">")
      .mkString("\n")
    s"""
      |@top Program {(Word) +}
      |
      |structure<term> { @specialize[@name={term}]<WeirdKW, term> }
      |branch<term> { @specialize[@name={term}]<WeirdKW, term> }
      |lambda<term> { @specialize[@name={term}]<WeirdKW, term> }
      |
      |Word {
      |    Structure |
      |    Branch |
      |    Lambda |
      |    String |
      |    Number |
      |    NormalKW |
      |    ModifierKW |
      |    VariableThing |
      |    ListStuff |
      |    GroupStuff |
      |    Comment
      |}
      |
      |@precedence {ModifierKW, NormalKW, WeirdKW}
      |
      |Structure {
      | $openers
      |}
      |
      |Branch {
      |$branches
      |}
      |
      |Lambda {
      |$lambdas
      |}
      |
      |@tokens {
      |    WeirdKW {$$[a-zA-Z\\-?]$$[a-zA-Z0-9\\-?!*+=<>&%]*":"?}
      |    ModifierKW { $$[a-zA-Z]$$[a-zA-Z0-9\\-?!*+=<>&%]*":"}
      |    NormalKW { $$[a-zA-Z]$$[a-zA-Z0-9\\-?!*+=<>&%]*"n't"*}
      |    VariableThing { ("$$" | ":=" | ":>" | ":=[") $$[a-zA-Z]$$[a-zA-Z0-9_]* }
      |    Number { "." | "0" | ($$[1-9] $$[0-9]*) }
      |    ListStuff { "[" | "]" }
      |    String {'"' (!["„”“\\\\] | "\\\\" _)* $$["„”“]}
      |    Comment {"##" (![\\n])*}
      |    GroupStuff { "(" | ")" | "(." | "(:" | "(:." | "(::" }
      |    @precedence {ModifierKW, NormalKW, GroupStuff, Number, WeirdKW}
      |}
    """.stripMargin('|')
  end genLiterateGrammar
end DocsUtils
