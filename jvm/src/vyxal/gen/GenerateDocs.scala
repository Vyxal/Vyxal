/** For generating elements.txt and trigraphs.txt. See build.sc */
package vyxal.gen

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

@main def generateDocs(
    tableFile: String,
    grammarFile: String,
) =
  Files.write(
    Paths.get(tableFile),
    DocsUtils.genMarkdown().getBytes(StandardCharsets.UTF_8),
  )

  Files.write(
    Paths.get(grammarFile),
    DocsUtils.genGrammar().getBytes(StandardCharsets.UTF_8),
  )
