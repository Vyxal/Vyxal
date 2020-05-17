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
    tokens = []
    structure = NO_STMT
    structure_data = {}

    STRING_CONTENTS = "string_contents"
    INTEGER_CONTENTS = "integer_contents"

    for char in source:
        print(structure, structure_data)

        
        if structure == STRING_STMT:
            if char == CLOSING[STRING_STMT]:
                    this_token = Token(STRING_STMT,
                                       structure_data[STRING_CONTENTS])
                    tokens.append(this_token)
                    structure_data = {}
                    structure = NO_STMT
                    continue
            else:
                structure_data[STRING_CONTENTS] += char
                continue

        elif structure == INTEGER:
            if char in "0123456789":
                structure_data[INTEGER_CONTENTS] += char
                continue
            else:
                this_token = Token(INTEGER, structure_data[INTEGER_CONTENTS])
                tokens.append(this_token)
                structure_data = {}
                structure = NO_STMT
        

        if char == CLOSING[STRING_STMT]:
            structure_data[STRING_CONTENTS] = ""
            structure = STRING_STMT

        elif char in "0123456789":
            structure = INTEGER
            structure_data[INTEGER_CONTENTS] = char

        else:
            this_token = Token(NO_STMT, char)
            tokens.append(this_token)

    return tokens
