/** For generating elements.txt and trigraphs.txt. See build.sc */
package vyxal.gen

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

@main def generateDocs(
    elementsFile: String,
    trigraphsFile: String,
    tableFile: String,
) =
  Files.write(
    Paths.get(tableFile),
    DocsUtils.genMarkdown().getBytes(StandardCharsets.UTF_8),
  )
