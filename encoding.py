from commands import codepage
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


compression  = "λƛ¬∧⟑∨⟇÷«»°•‘†€"
compression += "½∆ø↔¢⌐æʀʁɾɽÞƈ∞¨"
compression += "ß↑↓∴∵›"
compression += "‹∷¤ð→←βτȧḃċḋėḟġḣ"
compression += "ḭŀṁṅȯṗṙṡṫẇẋẏż√⟨⟩"
compression += "‛₀₁₂₃₄₅₆₇₈₉¶⁋§ε¡"
compression += "∑¦≈µȦḂĊḊĖḞĠḢİĿṀṄ"
compression += "ȮṖṘṠṪẆẊẎŻ₌₍⁰¹²∇⌈"
compression += "⌊⁾¯±₴…□↳↲⋏⋎꘍ꜝ℅≤≥"
compression += "≠⁼ƒɖ∪∩⊍£¥⇧⇩ǍǎǏǐǑ"
compression += "ǒǓǔ⁽‡≬×⁺↵⅛¼¾Π„‟"

codepage_number_compress = codepage.replace("»", "")
codepage_string_compress = codepage.replace("«", "")
