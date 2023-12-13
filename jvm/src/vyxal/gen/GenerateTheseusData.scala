package vyxal.gen

import vyxal.parsing.Lexer
import upickle.default.*
import vyxal.Modifiers

private object GenerateTheseusData:
    private def wrap(data: String) = f"""// This is a generated file, do not edit by hand
const GENERATED=${data};
export default GENERATED;
    """

    def codepage(): String = wrap(upickle.default.write(Lexer.Codepage))
    def modifiers(): String = wrap(upickle.default.write(Modifiers.modifiers.keys.toList))