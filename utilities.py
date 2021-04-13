import words

def to_ten(number, custom_base):
    # custom to ten
    # Turns something like 20 in base 5 to 10 in base 10
    # (int, int): uses an arbitrary base, and treats as a list of digits
    # (str, str): uses the provided base
    # (list, int): uses an arbitrary base, and treats as a list of digits
    # (str, int): uses an arbitrary base, and treats as a list of codepoints
    # (int, str): what the actual frick.
    # always returns Number

    result = 0
    alphabet = (lambda: custom_base, lambda: range(0, int(custom_base)))[type(custom_base) in (int, float)]()
    base_exponent = len(alphabet)
    number = list((lambda: number, lambda: map(int, str(int(number))))[type(number) in (int, float)]())
    power = 0
    for digit in reversed(number):
        if digit in alphabet:
            result += alphabet.index(digit) * (base_exponent ** power)
        else:
            result += -1 * (base_exponent ** power)
        power += 1
    
    return result

def from_ten(number, custom_base):
    # ten to custom
    # Turns something like 10 in base 10 to 20 in base 5
    # (int, int): use an arbitrary base, return a list of digits
    # (int, str): use provided base, return a string
    # (int, list): use provided base, return a list of "digits"
    # (non-int, any): what the actual frick.

    import math
    if type(number) not in (int, float):
        return number

    if type(custom_base) in (int, float):
        custom_base = range(0, int(custom_base))
    
    result = ([], "")[isinstance(custom_base, str)]
    append = (lambda x: result + [x], lambda x: result + x)[isinstance(result, str)]
    base_exponent = len(custom_base)
    temp = number
    power = int(math.log(number if number else 1) / math.log(base_exponent))

    while power >= 0:
        interesting_part, temp = divmod(temp, base_exponent ** power)
        result = append(custom_base[interesting_part])
        power -= 1
    
    if temp == 0: result
    
    return result


def uncompress(s):
    final = ""
    current_two = ""
    escaped = False

    import encoding

    for char in s:
        if escaped:
            if char not in encoding.compression:
                final += "\\"
            final += char
            escaped = False
            continue

        elif char == "\\":
            escaped = True
            continue

        elif char in encoding.compression:
            current_two += char
            if len(current_two) == 2:
                if to_ten(current_two, encoding.compression)\
                   < len(words._words):

                    final += words.extract_word(current_two)
                else:
                    final += current_two

                current_two = ""
            continue

        else:
            final += char


    return final.replace("\n", "\\n").replace("\r", "")


base53alphabet = "¡etaoinshrdlcumwfgypbvkjxqz ETAOINSHRDLCUMWFGYPBVKJXQZ"
base27alphabet = " etaoinshrdlcumwfgypbvkjxqz"


if __name__ == "__main__":
    import encoding
    while 1:
        word = input(">>> ")
        if word.isnumeric():
            print("»" + from_ten(int(word), encoding.codepage_number_compress) + "»")

        else:
            try:
                if type(eval(word)) is list:
                    charmap = dict(zip("0123456789,", "etaoinshrd "))
                    word = word.replace(" ", "").replace("[", "").replace("]", "")
                    out = ""
                    for char in word:
                        out += charmap.get(char, "")
                    c = from_ten(to_ten(out, base27alphabet), encoding.codepage_string_compress)
                    print("«" + c + "«ũ")
                    print()
                    print(repr(c))
                else:
                    print("«" + from_ten(to_ten(word, base27alphabet), encoding.codepage_string_compress) + "«")
            except:
                    print("«" + from_ten(to_ten(word, base27alphabet), encoding.codepage_string_compress) + "«")
