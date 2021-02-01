import words

def to_ten(n, base):
    '''
    If both arguments are strings, then this is a bijective base conversion
    If both arguments are integers, then you're doing something wrong.
    If n is a stack and base is an integer, it's as if you had a string but as codepoints.
    '''

    result = 0
    power = 0
    if type(n) is str and type(base) is str:
        from_base = len(base)
        for char in str(n)[::-1]:
            index = base.find(char)
            result += index * (from_base ** power)
            power += 1

    elif type(n) not in [int, float] and type(base) is int:
        for item in n[::-1]:
            result += item * (base ** power)
            power += 1
    return result


def from_ten(n, alphabet):
    import math
    to_base = len(alphabet)
    power = int(math.log(n if n else 1) / math.log(to_base))

    temp = n
    t = type(alphabet)
    if t in [int, float]:
        alphabet = list(range(0, int(alphabet)))
    result = "" if t is str else []

    while temp > 0:
        val = alphabet[(temp // (to_base ** power))]
        if t is str: result += val
        else: result.push(val)
        temp -= (temp // (to_base ** power)) * (to_base ** power)
        power -= 1

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


    return final


base53alphabet = "¡etaoinshrdlcumwfgypbvkjxqz ETAOINSHRDLCUMWFGYPBVKJXQZ"
base27alphabet = "¡etaoinshrdlcumwfgypbvkjxqz "


if __name__ == "__main__":
    import encoding
    while 1:
        word = input(">>> ")
        if word.isnumeric():
            print("»" + from_ten(int(word), encoding.codepage_number_compress) + "»")

        else:
            try:
                if type(eval(word)) is list:
                    charmap = dict(zip("0123456789,", "cetaoinshr "))
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
