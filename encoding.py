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


compression = "λƛ¬∧⟑∨⟇÷«»°•․⍎Ṛ½∆øÏÔÇæʀʁɾɽÞƈ∞⫙"
compression += "ß⎝⎠⎡⎣⨥⨪∺❝ð→←ÐřŠč√⳹ẊȦȮḊĖẸṙ∑Ĥ⟨⟩ı⁌"
compression += "τĴ²‿⁂ĸ¶⁋⁑Ńń‼⨊≈µʗ◁⊐∫⍋⍒∈ₛ£Œœ≕≠¥ⁱ‹›"
compression += "⍲⍱‸¡⊑≀℅≤≥↜≗⋯⧢ũ⁰¹ªₑϊ≎⇿⊛×¯±⊂⍞፣₴⍉ΐ₁⊘ᶢ₌"
compression += "↭ſƀƁ⁚⌈⌊⊓⊣Ḟḟ∪∩⊍⁜⌑Ḇ₂⁾₦¼ƒɖꝒ′₥α″βγΠ"

codepage_number_compress = codepage.replace("»", "")
codepage_string_compress = codepage.replace("«", "")
