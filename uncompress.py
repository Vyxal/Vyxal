import encoding
import words
import bases

def uncompress(s):
    final = ""
    current_two = ""
    escaped = False
    
    for char in s:
        if escaped:
            if char not in encoding.compression:
                final += "\\"
            final += char
            continue

        elif char == "\\":
            escaped = True
            continue

        elif char in encoding.compression:
            current_two += char
            if len(current_two) == 2:
                if bases.to_ten(current_two, encoding.compression)\
                   < len(words._words):

                    final += words.extract_word(current_two)
                else:
                    final += current_two

                current_two = ""
            continue

        else:
            final += char

    return final
        
        
