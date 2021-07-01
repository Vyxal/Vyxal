import string

from numpy.core.fromnumeric import var
NAME = "CONSTANT_TOKEN_NAME"
VALUE = "CONSTANT_TOKEN_VALUE"

IF_STMT = "STRUCTURE_IF"
FOR_STMT = "STRUCTURE_FOR"
WHILE_STMT = "STRUCTURE_WHILE"
FUNCTION_STMT = "STRUCTURE_FUNCTION"
LAMBDA_STMT = "STRUCTURE_LAMBDA"
NO_STMT = "STRUCTURE_NONE"
STRING_STMT = "STRUCTURE_STRING"
INTEGER = "STRUCTURE_INTEGER"
CHARACTER = "STRUCTURE_CHARACTER"
LAMBDA_STMT = "LAMBDA_STMT"
LAMBDA_MAP = "LAMBDA_MAP"
LAMBDA_FILTER = "LAMBDA_FILTER"
LAMBDA_SORT = "LAMBDA_SORT"
LIST_STMT = "LIST_STMT"
VARIABLE_GET = "VARIABLE_GET"
VARIABLE_SET = "VARIABLE_SET"
FUNCTION_REFERENCE = "FUNCTION_REFERENCE"
COMPRESSED_NUMBER = "COMPRESSED_NUMBER"
COMPRESSED_STRING = "COMPRESSED_STRING"

VARIABLES = [VARIABLE_GET, VARIABLE_SET]

STRING_CONTENTS = "string_contents"
INTEGER_CONTENTS = "integer_contents"
IF_ON_TRUE = "if_on_true"
IF_ON_FALSE = "if_on_false"
FOR_VARIABLE = "for_variable"
FOR_BODY = "for_body"
WHILE_CONDITION = "while_condition"
WHILE_BODY = "while_body"
FUNCTION_NAME = "function_name"
FUNCTION_BODY = "function_body"
LAMBDA_BODY = "lambda_body"
LIST_ITEM = "list_item"
LIST_ITEMS = "list_items"
VARIABLE_NAME = "variable_name"
LAMBDA_ARGUMENTS = "lambda_arguments"
COMPRESSED_NUMBER_VALUE = "compressed_number_value"
COMPRESSED_STRING_VALUE = "compressed_string_value"
TWO_CHAR_STUFF = "two_char_data_idk"

ONE = "one"
TWO = "two"
THREE = "three"
FOUR = "four"

ONE_CHARS = list("kv⁽∆ø⁺Þ¨&~ß‘")
TWO_CHARS = list("₌‡₍")

CONSTANT_CHAR = "k"
VECTORISATION_CHAR = "v"
SINGLE_SCC_CHAR = "‛"
CODEPAGE_INDEX = "⁺"
ONE_CHAR_FUNCTION_REFERENCE = "⁽"
TWO_BYTE_MATH = "∆"
TWO_BYTE_STRING = "ø"
TWO_BYTE_LIST = "Þ"
TWO_BYTE_MISC = "¨"
STRING_DELIMITER = "`"
REGISTER_MODIFIER = "&"
DONT_POP = "~"
CONDITIONAL_EXECUTION = "ß"
VAR_SET = "→"
VAR_GET = "←"

PARA_APPLY = "₌"
PARA_APPLY_COLLECT = "₍"
TWO_CHAR_LAMBDA = "‡"
THREE_CHAR_LAMBDA = "≬"

DECIMAL = "."

OPEN_CLOSE_SAME = ["`", "«", "»"]

OPENING = {
    NO_STMT: "",
    IF_STMT: "[",
    FOR_STMT: "(",
    WHILE_STMT: "{",
    FUNCTION_STMT: "@",
    LAMBDA_STMT: "λ",
    LAMBDA_MAP: "ƛ",
    LAMBDA_FILTER: "'",
    LAMBDA_SORT: "µ",
    LIST_STMT: "⟨",
    FUNCTION_REFERENCE: "°",
    COMPRESSED_NUMBER: "»",
    COMPRESSED_STRING: "«",
}

inv_OPENING = {v: k for k, v in OPENING.items()}

CLOSING = {
    NO_STMT: "",
    IF_STMT: "]",
    FOR_STMT: ")",
    WHILE_STMT: "}",
    FUNCTION_STMT: ";",
    LAMBDA_STMT: ";",
    LAMBDA_MAP: ";",
    LAMBDA_FILTER: ";",
    LAMBDA_SORT: ";",
    LIST_STMT: "⟩",
    FUNCTION_REFERENCE: ";",
    COMPRESSED_NUMBER: "»",
    COMPRESSED_STRING: "«",
}

inv_CLOSING = {v: k for k, v in CLOSING.items()}

DEFAULT_KEYS = {
    IF_STMT: IF_ON_TRUE,
    FOR_STMT: FOR_BODY,
    WHILE_STMT: WHILE_BODY,
    INTEGER: INTEGER_CONTENTS,
    FUNCTION_STMT: FUNCTION_NAME,
    LAMBDA_STMT: LAMBDA_BODY,
    LAMBDA_MAP: LAMBDA_BODY,
    LAMBDA_FILTER: LAMBDA_BODY,
    LAMBDA_SORT: LAMBDA_BODY,
    LIST_STMT: LIST_ITEM,
    FUNCTION_REFERENCE: FUNCTION_NAME,
    COMPRESSED_NUMBER: COMPRESSED_NUMBER_VALUE,
    COMPRESSED_STRING: COMPRESSED_STRING_VALUE,
}



class Token:
    def __init__(self, name: str, value: object):
        self.name = name
        self.value = value

    def __getitem__(self, key: int):
        if key in (0, NAME):
            return self.name

        elif key in (1, VALUE):
            return self.value

        else:
            raise IndexError("Token value not in the range of 0/1")

    def __str__(self):
        return str(self.name) + "|" + str(self.value)


def group_strings(program):
    out = []
    temp = ""
    escaped = False
    STANDARD, INTEGER, ALPHA = "`", "»", "«"

    flux_string = [False, "", STANDARD]  # [in_string, temp_string, string_type]
    for char in program:
        if type(char) is list:
            if flux_string[0]:
                flux_string[1] += char
            else:
                out.append(char)
        elif flux_string[0]:
            if escaped:
                if char == STANDARD:
                    flux_string[1] = flux_string[1][:-1]
                flux_string[1] += char
                escaped = False
            elif char == flux_string[2]:
                out.append([flux_string[1], flux_string[2]])
                flux_string = [False, "", STANDARD]
            elif char in "\\":
                escaped = True
                flux_string[1] += char
            else:
                flux_string[1] += char
        elif escaped:
            escaped = False
            out.append(char)
        elif char in "\\⁺":
            escaped = True
            out.append(char)
        elif char in (STANDARD, INTEGER, ALPHA):
            flux_string[0] = True
            flux_string[1] = ""
            flux_string[2] = char

        else:
            out.append(char)

    if flux_string[0]:
        out.append([flux_string[1], flux_string[2]])

    return out
def group_two_chars(program):
    ret = []
    temp, in_string, escaped  = "", False, False
    for item in program:
        if type(item) is list:
            ret.append(item)
        elif escaped:
            escaped = False
            ret.append(item)
        elif temp:
            ret.append([temp + item,  "`"]) # Am I really that lazy? Yes.
            temp = ""
            in_string = False
        elif in_string:
            temp = item
        elif item in "\\⁺":
            escaped = True
            ret.append(item)
        elif item == SINGLE_SCC_CHAR:
            in_string = True
        else:
            ret.append(item)
    if temp:
        ret.append(temp)
    return ret
def group_digraphs(code, variables_are_digraphs=False):
    # To be called after group_strings and after group_two_char
    ret = []
    temp = ""
    escaped = False

    TWO_BYTE_DELIMS = "k∆øÞ¨"
    if variables_are_digraphs: TWO_BYTE_DELIMS += "→←"

    for item in code:
        if type(item) is list:
            ret.append(item)

        elif escaped:
            escaped = False
            ret.append(item)
        elif temp:
            ret.append(temp + item)
            temp = ""
        elif item in "\\⁺":
            escaped = True
            ret.append(item)
        elif item in TWO_BYTE_DELIMS:
            temp = item
        else:
            ret.append(item)

    return ret


def Tokenise(source: str, variables_are_digraphs=False):
    tokens = []
    structure = NO_STMT
    structure_data = {}
    default_key = ""
    escaped = comment = False
    active_key = ""
    scc_mode, scc = False, ""
    nest_level = 0
    vectorisation = False
    bracket_stack = []
    # print(source)

    source = group_two_chars(source)
    source = group_strings(source)
    source = group_digraphs(source, variables_are_digraphs)

    for char in source:
        # print(char, structure, structure_data, escaped, nest_level, scc_mode)

        if comment:
            if char == "\n":
                comment = False
            continue
        if escaped:
            if structure != NO_STMT:
                structure_data[active_key] += "\\" + char
            else:
                tokens.append(Token(CHARACTER, char))

            escaped = False
            continue

        elif type(char) is list:
            if structure not in [NO_STMT, INTEGER, VARIABLE_GET, VARIABLE_SET]:
                structure_data[active_key] += char[1] + char[0] + char[1]
            else:
                if structure == INTEGER:
                    value = structure_data[active_key]
                    end = value.find(".", value.find(".") + 1)

                    if end > -1:
                        value = value[:end]

                    if value.isnumeric():
                        this_token = Token(INTEGER, int(value))

                    else:
                        try:
                            this_token = Token(INTEGER, float(value))
                        except:
                            this_token = Token(INTEGER, 0.5)
                    tokens.append(this_token)
                    structure_data = {}
                    structure = NO_STMT
                elif structure in VARIABLES:
                    this_token = Token(structure, structure_data)
                    tokens.append(this_token)
                    structure_data = {}
                    structure = NO_STMT
                    active_key = ""
                    default_key = ""
                yes = (
                    {"`": STRING_STMT, "«": COMPRESSED_STRING, "»": COMPRESSED_NUMBER}[
                        char[1]
                    ],
                    {
                        "`": STRING_CONTENTS,
                        "«": COMPRESSED_STRING_VALUE,
                        "»": COMPRESSED_NUMBER_VALUE,
                    }[char[1]],
                )
                tokens.append(Token(yes[0], {yes[1]: char[0]}))
            continue

        elif structure == INTEGER:
            if char in "0123456789.":
                structure_data[INTEGER_CONTENTS] += char
                continue
            else:
                value = structure_data[active_key]
                end = value.find(".", value.find(".") + 1)

                if end > -1:
                    value = value[:end]

                if value.isnumeric():
                    this_token = Token(INTEGER, int(value))

                else:
                    try:
                        this_token = Token(INTEGER, float(value))
                    except:
                        this_token = Token(INTEGER, 0.5)
                tokens.append(this_token)
                structure_data = {}
                structure = NO_STMT

        elif structure in VARIABLES:
            if char in string.ascii_letters + "_":
                structure_data[active_key] += char
                continue
            else:
                this_token = Token(structure, structure_data)
                tokens.append(this_token)
                structure_data = {}
                structure = NO_STMT
                active_key = ""
                default_key = ""

        elif scc_mode:
            scc += char
            if len(scc) == 2:
                scc_mode = False
                this_token = Token(SINGLE_SCC_CHAR, scc)
                tokens.append(this_token)
                scc = ""
                structure = NO_STMT
            continue

        elif structure in ONE_CHARS:
            this_token = Token(structure, char)
            tokens.append(this_token)
            structure = NO_STMT
            continue

        elif structure == TWO_CHAR_LAMBDA:
            if len(structure_data[active_key]) == 1:
                tokens.append(
                    Token(
                        LAMBDA_STMT,
                        {LAMBDA_BODY: "".join(structure_data[active_key] + [char])},
                    )
                )
                structure = NO_STMT
                structure_data = {}
            else:
                structure_data[active_key].append(char)
            continue

        elif structure in TWO_CHARS:
            if len(structure_data[active_key]) == 1:
                tokens.append(Token(structure, structure_data[active_key][0] + char))
                structure = NO_STMT
                structure_data = {}
            else:
                structure_data[active_key] = [char]
            continue

        elif structure == THREE_CHAR_LAMBDA:
            if len(structure_data[active_key]) == 2:
                tokens.append(
                    Token(
                        LAMBDA_STMT,
                        {LAMBDA_BODY: "".join(structure_data[active_key] + [char])},
                    )
                )
                structure = NO_STMT
                structure_data = {}
            else:
                structure_data[active_key].append(char)
            continue

        if char == "\\":
            escaped = True
            continue

        if char in OPENING.values():
            if nest_level:
                if char not in OPEN_CLOSE_SAME:
                    nest_level += 1
                structure_data[active_key] += char
                continue

            elif char == OPENING[IF_STMT]:
                structure = IF_STMT
                active_key = IF_ON_TRUE

            elif char == OPENING[WHILE_STMT]:
                structure = WHILE_STMT
                active_key = WHILE_CONDITION

            elif char == OPENING[FOR_STMT]:
                structure = FOR_STMT
                active_key = FOR_VARIABLE

            elif char == OPENING[FUNCTION_STMT]:
                structure = FUNCTION_STMT
                active_key = FUNCTION_NAME

            elif char == OPENING[LAMBDA_STMT]:
                structure = LAMBDA_STMT
                active_key = LAMBDA_BODY

            elif char == OPENING[LAMBDA_MAP]:
                structure = LAMBDA_MAP
                active_key = LAMBDA_BODY

            elif char == OPENING[LAMBDA_FILTER]:
                structure = LAMBDA_FILTER
                active_key = LAMBDA_BODY

            elif char == OPENING[LAMBDA_SORT]:
                structure = LAMBDA_SORT
                active_key = LAMBDA_BODY

            elif char == OPENING[LIST_STMT]:
                structure = LIST_STMT
                active_key = LIST_ITEM
                structure_data[LIST_ITEMS] = []

            elif char == OPENING[FUNCTION_REFERENCE]:
                structure = FUNCTION_REFERENCE
                active_key = FUNCTION_NAME

            elif char == OPENING[COMPRESSED_NUMBER]:
                structure = COMPRESSED_NUMBER
                active_key = COMPRESSED_NUMBER_VALUE

            elif char == OPENING[COMPRESSED_STRING]:
                structure = COMPRESSED_STRING
                active_key = COMPRESSED_STRING_VALUE

            else:
                raise NotImplementedError("That structure isn't implemented yet")

            structure_data[active_key] = ""
            nest_level += 1
            default_key = DEFAULT_KEYS[structure]

        elif char in CLOSING.values():
            nest_level -= 1
            if nest_level > 0:
                structure_data[active_key] += char
            else:
                additional_token = None

                if structure == LAMBDA_MAP:
                    additional_token = Token(NO_STMT, "M")
                    structure = LAMBDA_STMT

                elif structure == LAMBDA_FILTER:
                    additional_token = Token(NO_STMT, "F")
                    structure = LAMBDA_STMT

                elif structure == LAMBDA_SORT:
                    additional_token = Token(NO_STMT, "ṡ")
                    structure = LAMBDA_STMT

                elif structure == LIST_STMT:
                    structure_data[LIST_ITEMS].append(structure_data[LIST_ITEM])
                    del structure_data[LIST_ITEM]
                else:
                    if default_key not in structure_data and structure != NO_STMT:
                        structure_data[default_key] = structure_data[active_key]
                        del structure_data[active_key]

                this_token = Token(structure, structure_data)
                tokens.append(this_token)
                structure_data = {}
                structure = NO_STMT

                if additional_token:
                    tokens.append(additional_token)

        elif char == "|" and nest_level == 1:
            # Oh, the magical pipe which makes Vyxal and Keg unique
            # and a major causer of pain.
            # the above comment is mostly to do with strings.
            if structure == IF_STMT:
                active_key = IF_ON_FALSE

            elif structure == WHILE_STMT:
                active_key = WHILE_BODY

            elif structure == FOR_STMT:
                active_key = FOR_BODY

            elif structure == FUNCTION_STMT:
                active_key = FUNCTION_BODY

            elif structure == LAMBDA_STMT:
                structure_data[LAMBDA_ARGUMENTS] = structure_data[LAMBDA_BODY]
                active_key = LAMBDA_BODY

            elif structure == LIST_STMT:
                structure_data[LIST_ITEMS].append(structure_data[active_key])

            structure_data[active_key] = ""

        elif structure != NO_STMT:
            structure_data[active_key] += char

        elif char in "0123456789.":
            structure = INTEGER
            structure_data[INTEGER_CONTENTS] = char
            active_key = INTEGER_CONTENTS
            default_key = DEFAULT_KEYS[INTEGER]

        elif char == "→":
            structure = VARIABLE_SET
            structure_data[VARIABLE_NAME] = ""
            active_key = VARIABLE_NAME
            default_key = VARIABLE_NAME

        elif char == "←":
            structure = VARIABLE_GET
            structure_data[VARIABLE_NAME] = ""
            active_key = VARIABLE_NAME
            default_key = VARIABLE_NAME

        elif char == VECTORISATION_CHAR:
            vectorisation = True
            continue

        elif char in TWO_CHARS:
            char_mode = TWO
            structure = char
            active_key = TWO_CHAR_STUFF
            structure_data[active_key] = []

        elif char == THREE_CHAR_LAMBDA:
            char_mode = THREE
            structure = THREE_CHAR_LAMBDA
            active_key = LAMBDA_BODY
            structure_data[active_key] = []

        elif char in ONE_CHARS:
            char_mode = ONE
            structure = char

        elif char == SINGLE_SCC_CHAR:
            scc_mode = True

        elif char == "#":
            comment = True
            continue

        else:
            if vectorisation:
                tokens.append(Token(VECTORISATION_CHAR, char))
                vectorisation = False
            else:
                if len(char) == 2:
                    tokens.append(Token(char[0], char[1]))
                else:
                    this_token = Token(NO_STMT, char)
                    tokens.append(this_token)

    if structure != NO_STMT:
        additional_token = None

        if structure == LAMBDA_MAP:
            additional_token = Token(NO_STMT, "M")
            structure = LAMBDA_STMT

        elif structure == LAMBDA_FILTER:
            additional_token = Token(NO_STMT, "F")
            structure = LAMBDA_STMT

        elif structure == LAMBDA_SORT:
            additional_token = Token(NO_STMT, "ṡ")
            structure = LAMBDA_STMT

        elif structure == LIST_STMT:
            structure_data[LIST_ITEMS].append(structure_data[LIST_ITEM])
            del structure_data[LIST_ITEM]

        elif structure == INTEGER:
            value = structure_data[default_key]
            end = value.find(".", value.find(".") + 1)

            if end > -1:
                value = value[:end]

            if value.isnumeric():
                structure_data = int(value)

            else:
                try:
                    structure_data = float(value)
                except:
                    structure_data = 0.5

        else:
            if default_key not in structure_data:
                structure_data[default_key] = structure_data[active_key]
                del structure_data[active_key]

        this_token = Token(structure, structure_data)
        tokens.append(this_token)
        structure_data = {}
        structure = NO_STMT

        if additional_token:
            tokens.append(additional_token)
    # print([(n[0], n[1]) for n in tokens])
    return tokens


if __name__ == "__main__":
    # tests = ["«S⊍ǐ/µȦġk*∪±c*ɖøW₌≤₀e+₇ /)ðaðc~²⊍λġOṙŻZ⁽ɽẇ¼∴ðḂ>⁰IŻ↳Y%⁼ǐ∩\\ǔḞo⁋$∪@ø₇↑^V×Qc□„&<$↲AFðM‟[Ẏ`∵∪SĊ⟩%IHṠλ!q⟩»ꜝ∩=ẏ¼≥ȧ(ε∑²Z₁Ẇġ@Ḃ9d@3ġf₇Ṗꜝµ∞†≥¨ǐ $*∆⇩nTǎ√7Ḃ«"]
    tests = [
        "123.456`hello`789 42→x`world` ←x",
        "‡∆p-Ẋ1=",
        "‛`a",
        "‡∆p-Ẋ1=",
        "‡ab",
        "`\\``",
        "‡kAkA",
        "vøD",
        ".",
        "‛| mm",
        "‛`0`\`0`",
        "k\\",
        "«S⊍ǐ/µȦġk*∪±c*ɖøW₌≤₀e+₇ /)ðaðc~²⊍λġOṙŻZ⁽ɽẇ¼∴ðḂ>⁰IŻ↳Y%⁼ǐ∩\\ǔḞo⁋$∪@ø₇↑^V×Qc□„&<$↲AFðM‟[Ẏ`∵∪SĊ⟩%IHṠλ!q⟩»ꜝ∩=ẏ¼≥ȧ(ε∑²Z₁Ẇġ@Ḃ9d@3ġf₇Ṗꜝµ∞†≥¨ǐ $*∆⇩nTǎ√7Ḃ«",
        "kv"
    ]
    for test in tests:
        print(test, group_strings(group_two_chars(test)))
        print([(n[0], n[1]) for n in Tokenise(test)])
    input()
