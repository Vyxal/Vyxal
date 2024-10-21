import re

with open("shared/src/vyxal/Elements.scala") as src_file:
    src = src_file.read()


START_STRING = "val elements: Map[String, Element] = Map("
END_STRING = "private def execHelper"

start_index = src.index(START_STRING) + len(START_STRING)
end_index = src.index(END_STRING)

elements = src[start_index:end_index]

# Remove the "private def execHelper" line

elements = elements[: elements.index("private def execHelper")].strip()
