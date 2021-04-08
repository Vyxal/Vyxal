from commands import codepage
import string
def vyxal_to_utf8(code):
    # Taken from the old 05AB1E interpreter
    processed_code = ""
    for char in code:
        processed_code += codepage[char]

    return processed_code

def utf8_to_vyxal(code):
    # Taken from the old 05AB1E interpreter
    processed_code = ""
    for char in code:
        processed_code += chr(codepage.index(char))

    return processed_code


compression = codepage
for char in string.printable:
    compression = compression.replace(char, "")

codepage_number_compress = codepage.replace("»", "")
codepage_string_compress = codepage.replace("«", "")