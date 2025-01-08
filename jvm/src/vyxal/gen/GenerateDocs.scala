/** For generating elements.txt and trigraphs.txt. See build.sc */
package vyxal.gen

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

@main def generateDocs(
    tableFile: String,
    SBCSGrammarFile: String,
    literateGrammarFile: String,
) =
  Files.write(
    Paths.get(tableFile),
    DocsUtils.genMarkdown().getBytes(StandardCharsets.UTF_8),
  )

  Files.write(
    Paths.get(SBCSGrammarFile),
    DocsUtils.genSBCSGrammar().getBytes(StandardCharsets.UTF_8),
  )

  Files.write(
    Paths.get(literateGrammarFile),
    DocsUtils.genLiterateGrammar().getBytes(StandardCharsets.UTF_8),
  )
