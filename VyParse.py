NAME = "CONSTANT_TOKEN_NAME"
VALUE = "CONSTANT_TOKEN_VALUE"

IF_STMT = "STRUCTURE_IF"
FOR_STMT = "STRUCTURE_FOR"
WHILE_STMT = "STRUCTURE_WHILE"
FUNCTION_STMT = "STRUCTURE_FUNCTION"
LAMBDA_STMT = "STRUCTURE_LAMBDA"
SWITCH_STMT = "STRUCTURE_SWITCH"
NO_STMT = "STRUCTURE_NONE"
STRING_STMT = "STRUCTURE_STRING"
INTEGER = "STRUCTURE_INTEGER"
CHARACTER = "STRUCTURE_CHARACTER"

STRING_CONTENTS = "string_contents"
INTEGER_CONTENTS = "integer_contents"
IF_ON_TRUE = "if_on_true"
IF_ON_FALSE = "if_on_false"
FOR_VARIABLE = "for_variable"
FOR_BODY = "for_body"
WHILE_CONDITION = "while_condition"
WHILE_BODY = "while_body"

OPENING = {
    IF_STMT: "[",
    FOR_STMT: "(",
    WHILE_STMT: "{",
    FUNCTION_STMT: "@",
    LAMBDA_STMT: "λ",
    SWITCH_STMT: "§",
    STRING_STMT: "`"
}

CLOSING = {
    IF_STMT: "]",
    FOR_STMT: ")",
    WHILE_STMT: "}",
    FUNCTION_STMT: ";",
    LAMBDA_STMT: ";",
    SWITCH_STMT: ";",
    STRING_STMT: "`"
}

DEFAULT_KEYS = {
    IF_STMT: IF_ON_TRUE,
    FOR_STMT: FOR_BODY,
    WHILE_STMT: WHILE_BODY,
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


def Tokenise(source: str) -> [Token]:
    source += " "
    tokens = []
    structure = NO_STMT
    structure_data = {}
    escaped = False
    active_key = ""

    nest_level = 0
    


    for char in source:
        # print(char, structure, structure_data, nest_level)
        if escaped:
            if structure != NO_STMT:
                structure_data[active_key] += "\\" + char
            else:
                tokens.append(Token(CHARACTER, char))

            escaped = False
            continue

        if char == "\\":
            escaped = True
            continue

        elif structure == STRING_STMT:
            if char == CLOSING[STRING_STMT]:
                nest_level -= 1
                this_token = Token(structure, structure_data)
                tokens.append(this_token)
                structure_data = {}
                structure = NO_STMT
            else:
                structure_data[active_key] += char

            continue

        elif structure == INTEGER:
            if char in "0123456789":
                structure_data[INTEGER_CONTENTS] += char
                continue
            else:
                this_token = Token(INTEGER, structure_data[active_key])
                tokens.append(this_token)
                structure_data = {}
                structure = NO_STMT


        if char in OPENING.values():
            if nest_level > 0:
                if char != CLOSING[STRING_STMT]:
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


            elif char == OPENING[STRING_STMT]:
                structure = STRING_STMT
                active_key = STRING_CONTENTS


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
                if active_key != default_key:
                    structure_data[default_key] = structure_data[active_key]
                    del structure_data[active_key]
                    
                this_token = Token(structure, structure_data)
                tokens.append(this_token)
                structure_data = {}
                structure = NO_STMT


        elif char == "|" and nest_level == 1:
            # Oh, the magical pipe which makes Vyxal and Keg unique
            if structure == IF_STMT:
                active_key = IF_ON_FALSE

            elif structure == WHILE_STMT:
                active_key = WHILE_BODY

            elif structure == FOR_STMT:
                active_key = FOR_BODY

            structure_data[active_key] = ""


        elif structure != NO_STMT:
            structure_data[active_key] += char


        elif char in "0123456789":
            structure = INTEGER
            structure_data[INTEGER_CONTENTS] = char
            active_key = INTEGER_CONTENTS
            
        else:
            this_token = Token(NO_STMT, char)
            tokens.append(this_token)

    tokens.pop()
    return tokens


if __name__ == "__main__":
    tests = ["(x)1+"]
    for test in tests:
        print([(n[0], n[1]) for n in Tokenise(test)])
