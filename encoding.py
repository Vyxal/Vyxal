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
compression += "ß⎝⎠⎡⎣⨥⨪∺❝ð→←ÐřŠč√⳹ẊȦȮḊĖẸṙ∑ṠİĤ⟨⟩ı⁌"
compression += "ΤĴ²‿⁂ĸ¶⁋⁑Ńń‼⨊≈ðʗ◁⊐∫⍋⍒ℕ∈ₛ£Œœ≕≠¥ⁱ‹›"
compression += "⍲⍱‸¡⊑≀℅≤≥↜≗⋯⧢ũ⁰¹ªₑϊ≎⇿⊛×¯±⊂⍞፣₴⍉Ϊ₁⊘ᶢ₌"
compression += "↭ſƀƁ⁚⌈⌊⊓⊣Ḟḟ∪∩⊍⁜⌑Ḇ₂⁾₦¼ƒɖ𝒫′₥α″βγΠ"

codepage_number_compress = codepage.replace("»", "")
codepage_string_compress = codepage.replace("«", "")
