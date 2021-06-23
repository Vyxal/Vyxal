import string

class Structure:
    NONE = 0; IF = 1; FOR = 2; WHILE = 3; FUNCTION = 4; LAMBDA = 5; STRING = 6; NUMBER = 7; MAP = 8
    LIST = 9; VAR_GET = 10; VAR_SET = 11; FUNC_REF = 12; COMPRESSED_NUMBER = 13; COMPRESSED_STRING = 14; CHARACTER = 15
    DICTIONARY_STRING = 16; MONAD_TRANSFORMER = 17; DYAD_TRANSFORMER = 18; TRIAD_TRANSFORMER = 19; FOUR_ARITY_TRANSFORMER = 20; VARY_TRANSFORMER = 21

class Keys:
    STRING = 1; NUMBER = 2; IF_TRUE = 3; IF_FALSE = 4; FOR_VAR = 5; FOR_BODY = 6; WHILE_COND = 7; WHILE_BODY = 8; FUNC_NAME = 9
    FUNC_BODY = 10; LAMBDA_BODY = 11; LIST_ITEM = 12; LIST_ITEMS = 13; VAR_NAME = 14; LAMBDA_ARGS = 15; COM_NUM_VALUE = 16; COM_STR_VALUE = 17

Monadic_Transformers = list("¿μςσφɓƈɗƒɠɦƇƑƘƬƲȤv/\\&")
Dyadic_Transformers = list("ξψωƁƊƓⱮƝ")
Triadic_Transformers = list("π")
Tetradic_Transformers = list("ρ")
Variadic_Transformers = list("χƤ")

class Digraphs:
    NUMERIC = "∆"
    STRING = "ø"
    LIST = "Þ"
    MISC = "¨"
    CONSTANT = "k"
    ALL_DIGRAPHS = "k∆øÞ¨"

class StringDelimiters:
    NORMAL = "`"
    DICTIONARY = "“"
    COM_NUMBER = "»"
    COM_STRING = "«"
    TWO_CHAR = "‘"
    DELIM_TUPLE = (NORMAL, DICTIONARY, COM_NUMBER, COM_STRING, TWO_CHAR)

structure_dictionary = { # (open, close, default_key, starting_active_key, secondary_key)
    Structure.IF: ("[", "]", Keys.IF_TRUE, Keys.IF_TRUE, Keys.IF_FALSE),
    Structure.FOR: ("(", ")", Keys.FOR_BODY, Keys.FOR_VAR, Keys.FOR_BODY),
    Structure.WHILE:  ("{", "}", Keys.WHILE_BODY, Keys.WHILE_COND, Keys.WHILE_BODY),
    Structure.FUNCTION: ("@", ";", Keys.FUNC_NAME, Keys.FUNC_NAME, Keys.FUNC_BODY),
    Structure.LAMBDA: ("λ", ";", Keys.LAMBDA_BODY, Keys.LAMBDA_ARGS, Keys.LAMBDA_BODY),
    Structure.MAP: ("ƛ", ";", Keys.LAMBDA_BODY, Keys.LAMBDA_ARGS, Keys.LAMBDA_BODY), # y'know what, I will let y'all have custom map arities
    Structure.LIST: ("⟨", "⟩", Keys.LIST_ITEM, Keys.LIST_ITEM, Keys.LIST_ITEM),
    Structure.FUNC_REF: ("°", ";", Keys.FUNC_NAME, Keys.FUNC_NAME, Keys.FUNC_NAME),
    Structure.NUMBER: ("", "", Keys.NUMBER, Keys.NUMBER, Keys.NUMBER),
    Structure.VAR_GET: ("", "", Keys.VAR_NAME, Keys.VAR_NAME, Keys.VAR_NAME),
    Structure.VAR_SET: ("", "", Keys.VAR_NAME, Keys.VAR_NAME, Keys.VAR_NAME)
}

OPEN = tuple(structure_dictionary[k][0] for k in structure_dictionary)
CLOSE = tuple(structure_dictionary[k][1] for k in structure_dictionary)
def group_two_byte_strings(source: str) -> list[str]:
    components = []
    temp, in_string, escaped = "", False, False

    for character in source:
        if escaped: escaped = components.append(character) or False
        elif temp: temp = components.append([temp + character, "`"]) or ""
        elif in_string: temp = character
        elif character in "'⁺": escaped = components.append(character) or True
        elif character == StringDelimiters.TWO_CHAR: in_string = True
        else: components.append(character)
    
    if temp: components.append(temp)
    return components
def group_strings(source: list[str]) -> list[str]:
    components = []
    temp = ""
    escaped = False

    flux_string = [False, "", StringDelimiters.NORMAL]

    for character in source:
        if type(character) is list:
            if flux_string[0]: flux_string[1] += character
            else: components.append(character)
        
        elif flux_string[0]:
            if escaped:
                if character in (StringDelimiters.NORMAL, StringDelimiters.DICTIONARY):
                    flux_string[1] = flux_string[1][:-1]
                flux_string[1] += character
                escaped = False
            elif character == flux_string[2]:
                components.append([flux_string[1], flux_string[2]])
                flux_string = [False, "", StringDelimiters.NORMAL]
            elif character == "\\":
                escaped = True
                flux_string[1] += character
            else:
                flux_string[1] += character
        elif escaped: escaped = components.append(character) or False
        elif character in "'⁺": escaped = components.append(character) or True
        elif character in StringDelimiters.DELIM_TUPLE: flux_string = [True, "", character]
        else: components.append(character)

    if flux_string[0]: components.append([flux_string[1], flux_string[2]])
    return components
def group_digraphs(source: list[str]) -> list[str]:
    components = []
    temp = ""
    escaped = False

    for character in source:
        if type(character) is list: components.append(character)
        elif escaped: escaped = components.append(character) or False
        elif temp: temp = components.append(temp + character) or ""
        elif character in "'⁺": escaped = components.append(character) or True
        elif character in Digraphs.ALL_DIGRAPHS: temp = character
        else: components.append(character)
    
    return components

def parse(source: str) -> list[tuple]:
    tokens = []
    
    source = group_two_byte_strings(source)
    source = group_strings(source)
    source = group_digraphs(source)

    escaped = False
    comment = False

    structure = Structure.NONE
    structure_data = {}

    active_key = "" # The key which is currently being dealt with
    default_key = "" # The key that is used if there is only one component in an element that can have branches (|)
    nest_level = 0 # How far deep we are, as in, are we at the uppermost level of the program?

    for character in source:
        # print(character, nest_level, escaped, structure, tokens, active_key, default_key)

        # First, we do all the conditions that require immediant moving onto the next character (hence the continue)
        if escaped:
            tokens.append((Structure.CHARACTER, character))
            escaped = False
            continue
        if comment:
            comment = character == "\n"
            continue
        if structure == Structure.NUMBER:
            if type(character) is str and character in (string.digits + "."): # If the character is a digit, we keep adding to the flux number
                structure_data[active_key] += character
                continue
            else:
                tokens.append((Structure.NUMBER, structure_data[active_key]))
                structure, structure_data, active_key, default_key = Structure.NONE, {}, "", ""
        if structure == Structure.VAR_GET or structure == Structure.VAR_SET:
            if type(character) is str and character in string.ascii_letters + "_": # If the character is a valid variable name letter, we keep adding to the name
                structure_data[active_key] += character
                continue
            else:
                tokens.append((structure, structure_data[active_key]))
                structure, structure_data, active_key, default_key = Structure.NONE, {}, "", ""
        if character == "'":
            escaped = True
            continue
        if type(character) is list:
            tokens.append((Structure.STRING, character))
            continue

        # Now we move onto the possibility that we are starting a new kind of token        
        if character in OPEN:
            # Opening character. Note that this won't be variable stuff because I just handeled it. kekw very cool kanye. very cool.
            # no I'm not stalling with comments because I don't want to write the logic here. what gives you that impression?
            if nest_level:
                nest_level += 1
                structure_data[active_key] += character
                continue # That is, we're already in a structure, so we go deeper m'dude.

            structure = tuple(k for k in structure_dictionary if character == structure_dictionary[k][0])[0] # there's guaranteed to only be 1, because we have determined that it is in the dictionary
            default_key = structure_dictionary[structure][2]
            active_key = default_key 
            structure_data[active_key] = ""
            nest_level += 1

            # We have to special case lists though
            if structure == Structure.LIST:
                structure_data[Keys.LIST_ITEMS] = []
        
        elif character in CLOSE:
            nest_level -= 1
            if nest_level:
                structure_data[active_key] += character
                continue # We still have things to close m'dude.

            if structure == Structure.MAP:
                tokens.append((Structure.LAMBDA, structure_data))
                tokens.append((Structure.NONE, "M")) # Yes, lambda maps really are just lambda followed by M
            
            elif structure == Structure.LIST:
                structure_data[Keys.LIST_ITEMS].append(structure_data[Keys.LIST_ITEM])
                del structure_data[Keys.LIST_ITEM]
            else:
                if default_key not in structure_data and structure != Structure.NONE:
                        structure_data[default_key] = structure_data[active_key]
                        del structure_data[active_key]
            tokens.append((structure, structure_data))
            structure, structure_data, active_key, default_key = Structure.NONE, {}, "", ""
        
        elif character == "|" and nest_level == 1:
            active_key = structure_dictionary[structure][-1]
            if structure == Structure.LAMBDA:
                structure_data[Keys.LAMBDA_ARGS] = structure_data[Keys.LAMBDA_BODY]
            if structure == Structure.LIST:
                structure_data[Keys.LIST_ITEMS].append(structure_data[Keys.LIST_ITEM])
            
            structure_data[active_key] = ""
        
        elif character == "#":
            comment = True
        
        elif structure != Structure.NONE:
            structure_data[active_key] += character
        elif character in Monadic_Transformers:
            tokens.append((Structure.MONAD_TRANSFORMER, (character, (tokens.pop(),))))
        
        elif character in (string.digits + "."):
            structure = Structure.NUMBER
            active_key = structure_dictionary[Structure.NUMBER][2]
            structure_data[active_key] = character
        
        elif character == "←":
            structure = Structure.VAR_GET
            active_key = structure_dictionary[Structure.VAR_GET][2]
            structure_data[active_key] = ""
        
        elif character == "→":
            structure = Structure.VAR_SET
            active_key = structure_dictionary[Structure.VAR_SET][2]
            structure_data[active_key] = ""
        
        elif character in Dyadic_Transformers:
            elements = (tokens.pop(), tokens.pop())[::-1]
            tokens.append((Structure.DYAD_TRANSFORMER, (character, elements)))
        
        elif character in Triadic_Transformers:
            elements = (tokens.pop(), tokens.pop(), tokens.pop())[::-1]
            tokens.append((Structure.TRIAD_TRANSFORMER, (character, elements)))
        
        elif character in Tetradic_Transformers:
            elements = (tokens.pop(), tokens.pop(), tokens.pop(), tokens.pop())[::-1]
            tokens.append((Structure.FOUR_ARITY_TRANSFORMER, (character, elements)))

        elif character in Variadic_Transformers:
            raise NotImplementedError("Lyxal hasn't done those yet. Don't do that.")
        
        else:
            tokens.append((Structure.NONE, character))
        
    
    if structure != Structure.NONE:
        if structure == Structure.MAP:
            tokens.append((Structure.LAMBDA, structure_data))
            tokens.append((Structure.NONE, "M")) # Yes, lambda maps really are just lambda followed by M
        if structure == Structure.NUMBER:
            tokens.append((Structure.NUMBER, structure_data[active_key]))
        elif structure == Structure.LIST:
            structure_data[Keys.LIST_ITEMS].append(structure_data[Keys.LIST_ITEM])
            del structure_data[Keys.LIST_ITEM]
            tokens.append((structure, structure_data))
        elif structure == Structure.VAR_GET or structure == Structure.VAR_SET:
            tokens.append((structure, structure_data[Keys.VAR_NAME]))
        else:
            if default_key not in structure_data and structure != Structure.NONE:
                    structure_data[default_key] = structure_data[active_key]
                    del structure_data[active_key]
            tokens.append((structure, structure_data))
    
    return tokens


    
if __name__ == "__main__":
    tests = [
        "123.456`hello`789 42→x`world` ←x ",
        "'h'e'c'k",
        "1 2 3W 4 5\" 6J +v",
        "ABƝ",
        "1[23|45]"
    ]

    for test in tests:
        print(test, parse(test))
